/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.esigate;

import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.esigate.cookie.CookieManager;
import org.esigate.events.EventManager;
import org.esigate.events.impl.ProxyEvent;
import org.esigate.events.impl.RenderEvent;
import org.esigate.extension.ExtensionFactory;
import org.esigate.http.GenericHttpRequest;
import org.esigate.http.HttpClientHelper;
import org.esigate.http.HttpResponseUtils;
import org.esigate.http.ResourceUtils;
import org.esigate.util.HttpRequestHelper;
import org.esigate.vars.VariablesResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class used to retrieve data from a provider application using HTTP
 * requests. Data can be retrieved as binary streams or as String for text data.
 * To improve performance, the Driver uses a cache that can be configured
 * depending on the needs.
 * 
 * @author Francois-Xavier Bonnet
 * @author Nicolas Richeton
 * @author Sylvain Sicard
 */
public class Driver {
	private static final Logger LOG = LoggerFactory.getLogger(Driver.class);
	private final DriverConfiguration config;
	private HttpClientHelper httpClientHelper;
	private final Collection<String> parsableContentTypes;
	private final EventManager eventManager;

	private Driver(Properties properties, String name, EventManager eventManagerParam) {
		this.eventManager = eventManagerParam;
		config = new DriverConfiguration(name, properties);
		// Load extensions.
		ExtensionFactory.getExtensions(properties, Parameters.EXTENSIONS, this);

		parsableContentTypes = Parameters.PARSABLE_CONTENT_TYPES.getValueList(properties);
	}

	public Driver(String name, Properties properties) {
		this(properties, name, new EventManager(name));
		CookieManager cookieManager = ExtensionFactory.getExtension(properties, Parameters.COOKIE_MANAGER, this);
		httpClientHelper = new HttpClientHelper(eventManager, cookieManager, properties);
	}

	public Driver(String name, Properties properties, HttpClientHelper httpClientHelper) {
		this(properties, name, httpClientHelper.getEventManager());
		this.httpClientHelper = httpClientHelper;
	}

	public EventManager getEventManager() {
		return this.eventManager;
	}

	/**
	 * Perform rendering on a single url content, and append result to "writer".
	 * 
	 * @param pageUrl
	 *            Address of the page containing the template
	 * @param parameters
	 *            parameters to be added to the request
	 * @param writer
	 *            Writer where to write the result
	 * @param originalRequest
	 *            originating request object
	 * @param renderers
	 *            the renderers to use in order to transform the output
	 * @throws IOException
	 *             If an IOException occurs while writing to the writer
	 * @throws HttpErrorPage
	 *             If an Exception occurs while retrieving the template
	 */
	public final void render(String pageUrl, Map<String, String> parameters, Appendable writer,
			HttpEntityEnclosingRequest originalRequest, Renderer... renderers) throws IOException, HttpErrorPage {

		initHttpRequestParams(originalRequest, parameters);

		// Replace ESI variables in URL
		// TODO: should be performed in the ESI extension
		String resultingPageUrl = VariablesResolver.replaceAllVariables(pageUrl, originalRequest);

		// Retrieve URL
		String currentValue = getResourceAsString(resultingPageUrl, originalRequest);

		// Apply renderers
		currentValue = performRendering(pageUrl, originalRequest, currentValue, renderers);

		// Output result
		writer.append(currentValue);
	}

	/**
	 * Log current provider, page and renderers that will be applied.
	 * <p>
	 * This methods log at the INFO level.
	 * <p>
	 * You should only call this method if INFO level is enabled.
	 * 
	 * <pre>
	 * if (LOG.isInfoEnabled()) {
	 * 	logRendering(pageUrl, renderers);
	 * }
	 * </pre>
	 * 
	 * @see Driver#logRendering(String, Renderer[]) logRendering for renderer
	 *      <b>list</b>.
	 * @param action
	 *            Action name (eg. "proxy" or "render")
	 * @param onUrl
	 *            current page url.
	 * @param renderers
	 *            array of renderers
	 * 
	 */
	private void logAction(String action, String onUrl, Renderer[] renderers) {
		List<String> rendererNames = new ArrayList<String>(renderers.length);
		for (Renderer renderer : renderers) {
			rendererNames.add(renderer.getClass().getName());
		}
		LOG.info("render provider={} page= {} renderers={}", new Object[] { this.config.getInstanceName(), onUrl,
				rendererNames });
	}

	public void initHttpRequestParams(HttpRequest request, Map<String, String> parameters) throws HttpErrorPage {
		HttpRequestHelper.setDriver(request, this);
		HttpRequestHelper.setParameters(request, parameters);
		UserContext userContext = new UserContext(request, config.getInstanceName());
		HttpRequestHelper.setUserContext(request, userContext);
		try {
			URL baseUrl = new URL(config.getBaseUrlRetrieveStrategy().getBaseURL(request));
			HttpRequestHelper.setBaseUrl(request, baseUrl);
		} catch (MalformedURLException e) {
			throw new HttpErrorPage(500, "Internal server error", e);
		}
	}

	/**
	 * Retrieves a resource from the provider application and transforms it
	 * using the Renderer passed as a parameter.
	 * 
	 * @param relUrl
	 *            the relative URL to the resource
	 * @param request
	 *            the request
	 * @param renderers
	 *            the renderers to use to transform the output
	 * @throws IOException
	 *             If an IOException occurs while writing to the response
	 * @throws HttpErrorPage
	 *             If the page contains incorrect tags
	 */
	public void proxy(String relUrl, HttpEntityEnclosingRequest request, Renderer... renderers) throws IOException,
			HttpErrorPage {
		initHttpRequestParams(request, null);

		if (LOG.isInfoEnabled()) {
			logAction("render", relUrl, renderers);
		}

		HttpRequestHelper.setCharacterEncoding(request, config.getUriEncoding());

		// Create Proxy event
		ProxyEvent e = new ProxyEvent();
		e.originalRequest = request;

		// Event pre-proxy
		eventManager.fire(EventManager.EVENT_PROXY_PRE, e);

		// Return immediately if exit is requested by extension
		if (e.exit) {
			return;
		}

		String url = ResourceUtils.getHttpUrlWithQueryString(relUrl, e.originalRequest, true);
		GenericHttpRequest httpRequest = httpClientHelper.createHttpRequest(request, url, true);

		try {
			// Execute request
			HttpResponse httpResponse = execute(httpRequest);

			// Perform rendering
			httpResponse = performRendering(relUrl, request, httpResponse, renderers);

			// Send request to the client.
			HttpRequestHelper.getMediator(request).sendResponse(httpResponse);

		} catch (HttpErrorPage errorPage) {
			// On error returned by the proxy request, perform rendering on the
			// error page.
			HttpResponse errorResponse = errorPage.getHttpResponse();
			errorResponse = performRendering(relUrl, request, errorResponse, renderers);
			throw new HttpErrorPage(errorResponse);
		}

		// Event post-proxy
		eventManager.fire(EventManager.EVENT_PROXY_POST, e);
	}

	/**
	 * Performs rendering on an HttpResponse.
	 * <p>
	 * Rendering is only performed if page can be parsed.
	 * 
	 * @param pageUrl
	 *            The remove url from which the body was retrieved.
	 * @param originalRequest
	 *            The request received by esigate.
	 * @param response
	 *            The response which will be rendered.
	 * @param renderers
	 *            list of renderers to apply.
	 * @return The rendered response, or the original response if if was not
	 *         parsed.
	 * @throws HttpErrorPage
	 * @throws IOException
	 */
	private HttpResponse performRendering(String pageUrl, HttpEntityEnclosingRequest originalRequest,
			HttpResponse response, Renderer[] renderers) throws HttpErrorPage, IOException {

		if (!isTextContentType(response)) {
			LOG.debug("'{}' is binary on no transformation to apply: was forwarded without modification.", pageUrl);
			return response;
		}

		LOG.debug("'{}' is text : will apply renderers.", pageUrl);

		// Get response body
		String currentValue = HttpResponseUtils.toString(response, this.eventManager);

		// Perform rendering
		currentValue = performRendering(pageUrl, originalRequest, currentValue, renderers);

		// Generate the new response.
		HttpEntity transformedHttpEntity = new StringEntity(currentValue, ContentType.get(response.getEntity()));
		HttpResponse transformedResponse = new BasicHttpResponse(response.getStatusLine());
		transformedResponse.setHeaders(response.getAllHeaders());
		transformedResponse.setEntity(transformedHttpEntity);
		return transformedResponse;

	}

	/**
	 * Performs rendering (apply a render list) on an http response body (as a
	 * String).
	 * 
	 * @param pageUrl
	 *            The remove url from which the body was retrieved.
	 * @param originalRequest
	 *            The request received by esigate.
	 * @param body
	 *            The response body which will be rendered.
	 * @param renderers
	 *            list of renderers to apply.
	 * @return The rendered response body.
	 * @throws HttpErrorPage
	 * @throws IOException
	 */
	private String performRendering(String pageUrl, HttpEntityEnclosingRequest originalRequest, String body,
			Renderer[] renderers) throws IOException, HttpErrorPage {

		String currentBody = body;

		if (LOG.isInfoEnabled()) {
			logAction("render", pageUrl, renderers);
		}

		// Start rendering
		RenderEvent renderEvent = new RenderEvent();
		renderEvent.originalRequest = originalRequest;
		renderEvent.remoteUrl = pageUrl;
		// Create renderer list from parameters. Ensure at least 10
		// additional
		// renderers can be added at no cost.
		renderEvent.renderers = new ArrayList<Renderer>(renderers.length + 10);
		renderEvent.renderers.addAll(Arrays.asList(renderers));

		this.eventManager.fire(EventManager.EVENT_RENDER_PRE, renderEvent);
		for (Renderer renderer : renderEvent.renderers) {
			StringWriter stringWriter = new StringWriter();
			renderer.render(originalRequest, currentBody, stringWriter);
			currentBody = stringWriter.toString();
		}
		this.eventManager.fire(EventManager.EVENT_RENDER_POST, renderEvent);

		return currentBody;
	}

	/**
	 * This method returns the content of an url as a String. The result
	 * is cached into the request scope in order not to send several requests if
	 * you need several blocks in the same page to build the final page.
	 * 
	 * @param url
	 * @param originalRequest
	 *            the target resource
	 * @return the content of the url
	 * @throws HttpErrorPage
	 */
	protected String getResourceAsString(String url, HttpEntityEnclosingRequest originalRequest) throws HttpErrorPage {
		String pageBody;

		String targetUrl = ResourceUtils.getHttpUrlWithQueryString(url, originalRequest, false);

		// Get from cache
		boolean cacheable = "GET".equalsIgnoreCase(originalRequest.getRequestLine().getMethod());
		if (cacheable) {
			pageBody = (String) originalRequest.getParams().getParameter(targetUrl);
			if (pageBody != null)
				return pageBody;
		}

		GenericHttpRequest httpRequest = this.httpClientHelper.createHttpRequest(originalRequest, targetUrl, false);
		HttpResponse httpResponse = execute(httpRequest);

		// Unzip
		pageBody = HttpResponseUtils.toString(httpResponse, this.eventManager);

		// Cache
		if (cacheable) {
			originalRequest.getParams().setParameter(targetUrl, pageBody);
		}
		return pageBody;
	}

	/**
	 * Get current driver configuration.
	 * <p>
	 * This method is not intended to get a WRITE access to the configuration.
	 * <p>
	 * This may be supported in future versions (testing is needed). For the
	 * time being, changing configuration settings after getting access through
	 * this method is <b>UNSUPPORTED</b> and <b>SHOULD NOT</b> be used.
	 * 
	 * @return current configuration
	 */
	public DriverConfiguration getConfiguration() {
		return this.config;
	}

	/**
	 * Get current HTTP Client.
	 * 
	 * <p>
	 * This method is not intended to get a WRITE access to the HTTP Client
	 * configuration.
	 * <p>
	 * For the time being, changing HTTP Client configuration after getting
	 * access through this method is <b>UNSUPPORTED</b> and <b>SHOULD NOT</b> be
	 * used.
	 * 
	 * @return current HttpClient
	 */
	public HttpClientHelper getHttpClientHelper() {
		return this.httpClientHelper;
	}

	/**
	 * Check whether the given request's content-type corresponds to "parsable"
	 * text.
	 * 
	 * @param httpResponse
	 *            the response to analyze depending on its content-type
	 * @return true if this represents text or false if not
	 */
	private boolean isTextContentType(HttpResponse httpResponse) {
		String contentType = HttpResponseUtils.getFirstHeader(HttpHeaders.CONTENT_TYPE, httpResponse);
		return isTextContentType(contentType);
	}

	/**
	 * Check whether the given content-type corresponds to "parsable" text.
	 * 
	 * @param contentType
	 *            the input content-type
	 * @return true if this represents text or false if not
	 */
	protected boolean isTextContentType(String contentType) {
		if (contentType != null) {
			String lowerContentType = contentType.toLowerCase();
			for (String textContentType : this.parsableContentTypes) {
				if (lowerContentType.startsWith(textContentType)) {
					return true;
				}
			}
		}
		return false;
	}

	public HttpResponse executeSingleRequest(GenericHttpRequest httpRequest) {
		return httpClientHelper.execute(httpRequest);
	}

	private HttpResponse execute(GenericHttpRequest httpRequest) throws HttpErrorPage {
		HttpResponse httpResponse = executeSingleRequest(httpRequest);
		// Handle errors.
		if (httpResponse == null) {
			throw new HttpErrorPage(500, "Request was cancelled by server", "Request was cancelled by server");
		}
		if (HttpResponseUtils.isError(httpResponse)) {
			throw new HttpErrorPage(httpResponse);
		}
		return httpResponse;
	}

}

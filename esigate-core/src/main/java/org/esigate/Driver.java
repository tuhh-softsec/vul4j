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
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

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
	private final List<String> parsableContentTypes;
	private final EventManager eventManager ;

	private Driver(Properties properties, String name, EventManager eventManagerParam) {
		this.eventManager = eventManagerParam;
		config = new DriverConfiguration(name, properties);
		// Load extensions.
		ExtensionFactory.getExtensions(properties, Parameters.EXTENSIONS, this);

		parsableContentTypes = new ArrayList<String>();
		String strContentTypes = Parameters.PARSABLE_CONTENT_TYPES.getValueString(properties);
		StringTokenizer tokenizer = new StringTokenizer(strContentTypes, ",");
		String contentType;
		while (tokenizer.hasMoreElements()) {
			contentType = tokenizer.nextToken();
			contentType = contentType.trim();
			parsableContentTypes.add(contentType);
		}
	}

	public Driver(String name, Properties properties) {
		this(properties, name,  new EventManager());
		CookieManager cookieManager = ExtensionFactory.getExtension(properties, Parameters.COOKIE_MANAGER, this);
		httpClientHelper = new HttpClientHelper(eventManager, cookieManager, properties);
	}

	public Driver(String name, Properties properties, HttpClientHelper httpClientHelper) {
		this(properties, name, httpClientHelper.getEventManager());
		this.httpClientHelper = httpClientHelper;
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	/**
	 * @param page
	 *            Address of the page containing the template
	 * @param parameters
	 *            parameters to be added to the request
	 * @param writer
	 *            Writer where to write the result
	 * @param request
	 *            originating request object
	 * @param renderers
	 *            the renderers to use to transform the output
	 * @throws IOException
	 *             If an IOException occurs while writing to the writer
	 * @throws HttpErrorPage
	 *             If an Exception occurs while retrieving the template
	 */
	public final void render(String page, Map<String, String> parameters, Appendable writer, HttpEntityEnclosingRequest request, Renderer... renderers) throws IOException, HttpErrorPage {
		initHttpRequestParams(request, parameters);
		if (LOG.isInfoEnabled()) {
			List<String> rendererNames = new ArrayList<String>(renderers.length);
			for (Renderer renderer : renderers) {
				rendererNames.add(renderer.getClass().getName());
			}
			LOG.info("render provider={} page= {} renderers={}", new Object[] { config.getInstanceName(), page, rendererNames });
		}
		String resultingpage = VariablesResolver.replaceAllVariables(page, request);
		String currentValue = getResourceAsString(resultingpage, request);

		// Start rendering.
		RenderEvent renderEvent = new RenderEvent();
		renderEvent.originalRequest = request;
		renderEvent.remoteUrl = page;
		// Create renderer list from parameters. Ensure at least an additional
		// renderer can be added at no cost.
		renderEvent.renderers = new ArrayList<Renderer>(renderers.length + 1);
		renderEvent.renderers.addAll(Arrays.asList(renderers));

		eventManager.fire(EventManager.EVENT_RENDER_PRE, renderEvent);

		// Process all renderers
		for (Renderer renderer : renderEvent.renderers) {
			StringWriter stringWriter = new StringWriter();
			renderer.render(request, currentValue, stringWriter);
			currentValue = stringWriter.toString();
		}
		eventManager.fire(EventManager.EVENT_RENDER_POST, renderEvent);

		writer.append(currentValue);
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
	public void proxy(String relUrl, HttpEntityEnclosingRequest request, Renderer... renderers) throws IOException, HttpErrorPage {
		initHttpRequestParams(request, null);

		if (LOG.isInfoEnabled()) {
			LOG.info("proxy provider={} relUrl={}", config.getInstanceName(), relUrl);
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
		HttpResponse httpResponse = execute(httpRequest);
		if (!isTextContentType(httpResponse)) {
			LOG.debug("'{}' is binary on no transformation to apply: was forwarded without modification.", relUrl);
			HttpRequestHelper.getMediator(request).sendResponse(httpResponse);
		} else {
			LOG.debug("'{}' is text : will apply renderers.", relUrl);
			String currentValue = HttpResponseUtils.toString(httpResponse);

			// Start rendering
			RenderEvent renderEvent = new RenderEvent();
			renderEvent.originalRequest = request;
			renderEvent.remoteUrl = relUrl;
			// Create renderer list from parameters. Ensure at least an
			// additional
			// renderer can be added at no cost.
			renderEvent.renderers = new ArrayList<Renderer>(renderers.length + 1);
			renderEvent.renderers.addAll(Arrays.asList(renderers));

			eventManager.fire(EventManager.EVENT_RENDER_PRE, renderEvent);
			for (Renderer renderer : renderEvent.renderers) {
				StringWriter stringWriter = new StringWriter();
				renderer.render(request, currentValue, stringWriter);
				currentValue = stringWriter.toString();
			}
			eventManager.fire(EventManager.EVENT_RENDER_POST, renderEvent);

			// Write the result to the OutpuStream using default charset
			// ISO-8859-1 if not defined
			String charsetName = HttpResponseUtils.getContentCharset(httpResponse);
			if (charsetName == null) {
				charsetName = "ISO-8859-1";
			}
			HttpEntity transformedHttpEntity = new StringEntity(currentValue, ContentType.get(httpResponse.getEntity()));
			HttpResponse transformedResponse = new BasicHttpResponse(httpResponse.getStatusLine());
			transformedResponse.setHeaders(httpResponse.getAllHeaders());
			transformedResponse.setEntity(transformedHttpEntity);
			HttpRequestHelper.getMediator(request).sendResponse(transformedResponse);
		}

		// Event post-proxy
		eventManager.fire(EventManager.EVENT_PROXY_POST, e);
	}

	/**
	 * This method returns the content of an url as a StringOutput. The result
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
		String result;
		url = VariablesResolver.replaceAllVariables(url, originalRequest);
		url = ResourceUtils.getHttpUrlWithQueryString(url, originalRequest, false);

		// Pre get ressource

		// pre : get from cache
		boolean cacheable = "GET".equalsIgnoreCase(originalRequest.getRequestLine().getMethod());
		if (cacheable) {
			result = (String) originalRequest.getParams().getParameter(url);
			if (result != null)
				return result;
		}

		GenericHttpRequest httpRequest = httpClientHelper.createHttpRequest(originalRequest, url, false);
		HttpResponse httpResponse = execute(httpRequest);

		// post getResource

		// post : unzip
		result = HttpResponseUtils.toString(httpResponse);

		// post : cache
		if (cacheable) {
			originalRequest.getParams().setParameter(url, result);
		}
		return result;
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
		return config;
	}

	/**
	 * Check whether the given content-type value corresponds to "parsable"
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

	protected boolean isTextContentType(String contentType) {
		if (contentType != null) {
			String lowerContentType = contentType.toLowerCase();
			for (String textContentType : parsableContentTypes) {
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

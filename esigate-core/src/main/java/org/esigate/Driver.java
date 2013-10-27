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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.esigate.RequestExecutor.RequestExecutorBuilder;
import org.esigate.events.EventManager;
import org.esigate.events.impl.ProxyEvent;
import org.esigate.events.impl.RenderEvent;
import org.esigate.extension.ExtensionFactory;
import org.esigate.http.ContentTypeHelper;
import org.esigate.http.HttpClientRequestExecutor;
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
	private static final String CACHE_RESPONSE_PREFIX = "response_";
	private static final Logger LOG = LoggerFactory.getLogger(Driver.class);
	private DriverConfiguration config;
	private EventManager eventManager;
	private RequestExecutor requestExecutor;
	private ContentTypeHelper contentTypeHelper;

	public static class DriverBuilder {
		private Driver driver = new Driver();
		private String name;
		private Properties properties;
		private RequestExecutorBuilder requestExecutorBuilder;

		public Driver build() {
			if (name == null)
				throw new ConfigurationException("name is mandatory");
			if (properties == null)
				throw new ConfigurationException("properties is mandatory");
			if (requestExecutorBuilder == null)
				requestExecutorBuilder = HttpClientRequestExecutor.builder();
			if (driver.eventManager == null)
				driver.eventManager = new EventManager(name);
			driver.config = new DriverConfiguration(name, properties);
			driver.contentTypeHelper = new ContentTypeHelper(properties);
			// Load extensions.
			ExtensionFactory.getExtensions(properties, Parameters.EXTENSIONS, driver);
			driver.requestExecutor = requestExecutorBuilder.setDriver(driver).setEventManager(driver.getEventManager()).setProperties(properties).build();
			return driver;
		}

		public DriverBuilder setName(String name) {
			this.name = name;
			return this;
		}

		public DriverBuilder setProperties(Properties properties) {
			this.properties = properties;
			return this;
		}

		public DriverBuilder setEventManager(EventManager eventManager) {
			driver.eventManager = eventManager;
			return this;
		}

		public DriverBuilder setRequestExecutorBuilder(RequestExecutorBuilder requestExecutorBuilder) {
			this.requestExecutorBuilder = requestExecutorBuilder;
			return this;
		}

	}

	protected Driver() {
	}

	public static DriverBuilder builder() {
		return new DriverBuilder();
	}

	/**
	 * Get current event manager for this driver instance.
	 * 
	 * @return event manager.
	 */
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
	public final void render(String pageUrl, Map<String, String> parameters, Appendable writer, HttpEntityEnclosingRequest originalRequest, Renderer... renderers) throws IOException, HttpErrorPage {

		initHttpRequestParams(originalRequest, parameters);

		// Replace ESI variables in URL
		// TODO: should be performed in the ESI extension
		String resultingPageUrl = VariablesResolver.replaceAllVariables(pageUrl, originalRequest);

		// Retrieve URL
		HttpResponse response = null;
		String currentValue = null;

		// Get from cache to prevent multiple request to the same url if
		// multiple fragments are used.
		boolean cacheable = "GET".equalsIgnoreCase(originalRequest.getRequestLine().getMethod());
		if (cacheable) {
			currentValue = (String) originalRequest.getParams().getParameter(resultingPageUrl);
			response = (HttpResponse) originalRequest.getParams().getParameter(CACHE_RESPONSE_PREFIX + resultingPageUrl);
		}

		// content and reponse were not in cache
		if (currentValue == null) {
			response = getResource(resultingPageUrl, originalRequest);
			// Unzip
			currentValue = HttpResponseUtils.toString(response, this.eventManager);
			// Cache
			if (cacheable) {
				originalRequest.getParams().setParameter(resultingPageUrl, currentValue);
				originalRequest.getParams().setParameter(CACHE_RESPONSE_PREFIX + resultingPageUrl, response);
			}
		}

		// Apply renderers
		currentValue = performRendering(pageUrl, originalRequest, response, currentValue, renderers);

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
		LOG.info("{} provider={} page= {} renderers={}", action, this.config.getInstanceName(), onUrl, rendererNames);
	}

	/**
	 * Set request context informations as request parameters.
	 * <p>
	 * 
	 * @see HttpRequest#setParams(org.apache.http.params.HttpParams)
	 * @param request
	 *            the HTTP request
	 * @param parameters
	 *            Additional parameters (other than the ones included in the
	 *            request itself).
	 * @throws HttpErrorPage
	 *             if url base for this request is invalid (remoteBaseUrl or
	 *             baseUrlRetriveStrategy).
	 */
	public void initHttpRequestParams(HttpRequest request, Map<String, String> parameters) throws HttpErrorPage {
		HttpRequestHelper.setDriver(request, this);
		HttpRequestHelper.setParameters(request, parameters);
		UserContext userContext = new UserContext(request, this.config.getInstanceName());
		HttpRequestHelper.setUserContext(request, userContext);
		try {
			URL baseUrl = new URL(this.config.getBaseUrlRetrieveStrategy().getBaseURL(request));
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
		// This is used to ensure EVENT_PROXY_POST is called once and only once.
		// there are 3 different cases
		// - Success -> the main code
		// - Error page -> the HttpErrorPage exception
		// - Unexpected error -> Other Exceptions
		boolean postProxyPerformed = false;

		// Create Proxy event
		ProxyEvent e = new ProxyEvent();
		e.originalRequest = request;

		try {
			// Event pre-proxy
			this.eventManager.fire(EventManager.EVENT_PROXY_PRE, e);
			// Return immediately if exit is requested by extension
			if (e.exit) {
				return;
			}

			if (LOG.isInfoEnabled()) {
				logAction("proxy", relUrl, renderers);
			}

			initHttpRequestParams(request, null);
			HttpRequestHelper.setCharacterEncoding(request, this.config.getUriEncoding());
			String url = ResourceUtils.getHttpUrlWithQueryString(relUrl, e.originalRequest, true);
			e.response = requestExecutor.createAndExecuteRequest(request, url, true);

			// Perform rendering
			e.response = performRendering(relUrl, request, e.response, renderers);

			// Event post-proxy
			// This must be done before calling sendResponse to ensure response
			// can still be changed.
			postProxyPerformed = true;
			this.eventManager.fire(EventManager.EVENT_PROXY_POST, e);

			// Send request to the client.
			HttpRequestHelper.getMediator(request).sendResponse(e.response);

		} catch (HttpErrorPage errorPage) {
			e.errorPage = errorPage;

			// On error returned by the proxy request, perform rendering on the
			// error page.
			e.errorPage = new HttpErrorPage(performRendering(relUrl, request, e.errorPage.getHttpResponse(), renderers));

			// Event post-proxy
			// This must be done before throwing exception to ensure response
			// can still be changed.
			postProxyPerformed = true;
			this.eventManager.fire(EventManager.EVENT_PROXY_POST, e);

			throw e.errorPage;
		} finally {
			if (!postProxyPerformed) {
				this.eventManager.fire(EventManager.EVENT_PROXY_POST, e);
			}
		}
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
	private HttpResponse performRendering(String pageUrl, HttpEntityEnclosingRequest originalRequest, HttpResponse response, Renderer[] renderers) throws HttpErrorPage, IOException {

		if (!contentTypeHelper.isTextContentType(response)) {
			LOG.debug("'{}' is binary on no transformation to apply: was forwarded without modification.", pageUrl);
			return response;
		}

		LOG.debug("'{}' is text : will apply renderers.", pageUrl);

		// Get response body
		String currentValue = HttpResponseUtils.toString(response, this.eventManager);

		// Perform rendering
		currentValue = performRendering(pageUrl, originalRequest, response, currentValue, renderers);

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
	 * @param response
	 *            The Http Reponse.
	 * @param body
	 *            The body of the Http Response which will be rendered.
	 * @param renderers
	 *            list of renderers to apply.
	 * @return The rendered response body.
	 * @throws HttpErrorPage
	 * @throws IOException
	 */
	private String performRendering(String pageUrl, HttpEntityEnclosingRequest originalRequest, HttpResponse response, String body, Renderer[] renderers) throws IOException, HttpErrorPage {
		if (LOG.isInfoEnabled()) {
			logAction("render", pageUrl, renderers);
		}
		// Start rendering
		RenderEvent renderEvent = new RenderEvent();
		renderEvent.originalRequest = originalRequest;
		renderEvent.remoteUrl = pageUrl;
		renderEvent.httpResponse = response;
		// Create renderer list from parameters. Ensure at least 10
		// additional
		// renderers can be added at no cost.
		renderEvent.renderers = new ArrayList<Renderer>(renderers.length + 10);
		renderEvent.renderers.addAll(Arrays.asList(renderers));

		String currentBody = body;

		this.eventManager.fire(EventManager.EVENT_RENDER_PRE, renderEvent);
		for (Renderer renderer : renderEvent.renderers) {
			StringBuilderWriter stringWriter = new StringBuilderWriter(1024);
			renderer.render(originalRequest, currentBody, stringWriter);
			stringWriter.close();
			currentBody = stringWriter.toString();
		}
		this.eventManager.fire(EventManager.EVENT_RENDER_POST, renderEvent);

		return currentBody;
	}

	/**
	 * This method returns the content of an url as an HttpResponse.
	 * 
	 * @param url
	 * @param originalRequest
	 *            the target resource
	 * @return the Http reponse
	 * @throws HttpErrorPage
	 */
	private HttpResponse getResource(String url, HttpEntityEnclosingRequest originalRequest) throws HttpErrorPage {
		String targetUrl = ResourceUtils.getHttpUrlWithQueryString(url, originalRequest, false);
		return requestExecutor.createAndExecuteRequest(originalRequest, targetUrl, false);
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

	public RequestExecutor getRequestExecutor() {
		return requestExecutor;
	}

}

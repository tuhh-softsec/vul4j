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

import org.apache.http.HttpHeaders;
import org.apache.http.client.CookieStore;
import org.apache.http.protocol.HttpContext;
import org.esigate.api.HttpRequest;
import org.esigate.api.HttpResponse;
import org.esigate.cookie.CookieManager;
import org.esigate.events.EventManager;
import org.esigate.events.impl.FragmentEvent;
import org.esigate.events.impl.ProxyEvent;
import org.esigate.extension.ExtensionFactory;
import org.esigate.http.GenericHttpRequest;
import org.esigate.http.HttpClientHelper;
import org.esigate.http.HttpResponseUtils;
import org.esigate.http.RequestCookieStore;
import org.esigate.http.ResourceUtils;
import org.esigate.renderers.ResourceFixupRenderer;
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
	private final HttpClientHelper httpClientHelper;
	private final List<String> parsableContentTypes;
	private final CookieManager cookieManager;
	private final EventManager eventManager = new EventManager();

	public EventManager getEventManager() {
		return eventManager;
	}

	public Driver(String name, Properties props) {
		this(name, props, new HttpClientHelper());
		httpClientHelper.init(eventManager, props);
	}

	public Driver(String name, Properties props,
			HttpClientHelper httpClientHelper) {
		config = new DriverConfiguration(name, props);
		this.httpClientHelper = httpClientHelper;
		// Load extensions.
		ExtensionFactory.getExtensions(props, Parameters.EXTENSIONS,
				this);

		parsableContentTypes = new ArrayList<String>();
		String strContentTypes = Parameters.PARSABLE_CONTENT_TYPES
				.getValueString(props);
		StringTokenizer tokenizer = new StringTokenizer(strContentTypes, ",");
		String contentType;
		while (tokenizer.hasMoreElements()) {
			contentType = tokenizer.nextToken();
			contentType = contentType.trim();
			parsableContentTypes.add(contentType);
		}
		cookieManager = ExtensionFactory.getExtension(props,
				Parameters.COOKIE_MANAGER, this);
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
	 * @param response
	 * @param renderers
	 *            the renderers to use to transform the output
	 * @throws IOException
	 *             If an IOException occurs while writing to the writer
	 * @throws HttpErrorPage
	 *             If an Exception occurs while retrieving the template
	 */
	public final void render(String page, Map<String, String> parameters,
			Appendable writer, HttpRequest request, HttpResponse response,
			Renderer... renderers) throws IOException, HttpErrorPage {
		initHttpRequestParams(request, response, parameters);
		if (LOG.isInfoEnabled()) {
			List<String> rendererNames = new ArrayList<String>(renderers.length);
			for (Renderer renderer : renderers) {
				rendererNames.add(renderer.getClass().getName());
			}
			LOG.info("render provider={} page= {} renderers={}", new Object[] {
					config.getInstanceName(), page, rendererNames });
		}
		String resultingpage = VariablesResolver.replaceAllVariables(page,
				request);
		String currentValue = getResourceAsString(resultingpage, request);

		// Fix resources
		if (config.isFixResources()) {
			String baseUrl = HttpRequestHelper.getBaseUrl(request).toString();
			ResourceFixupRenderer fixup = new ResourceFixupRenderer(baseUrl,
					config.getVisibleBaseURL(baseUrl), page,
					config.getFixMode());
			StringWriter stringWriter = new StringWriter();
			fixup.render(request, currentValue, stringWriter);
			currentValue = stringWriter.toString();
		}

		// Process all renderers
		for (Renderer renderer : renderers) {
			StringWriter stringWriter = new StringWriter();
			renderer.render(request, currentValue, stringWriter);
			currentValue = stringWriter.toString();
		}
		writer.append(currentValue);
	}

	public void initHttpRequestParams(HttpRequest request,
			HttpResponse response, Map<String, String> parameters)
			throws HttpErrorPage {
		HttpRequestHelper.setResponse(request, response);
		HttpRequestHelper.setDriver(request, this);
		HttpRequestHelper.setParameters(request, parameters);
		UserContext userContext = new UserContext(request,
				config.getInstanceName());
		HttpRequestHelper.setUserContext(request, userContext);
		try {
			URL baseUrl = new URL(config.getBaseUrlRetrieveStrategy()
					.getBaseURL(request, response));
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
	 * @param response
	 *            the response
	 * @param renderers
	 *            the renderers to use to transform the output
	 * @throws IOException
	 *             If an IOException occurs while writing to the response
	 * @throws HttpErrorPage
	 *             If the page contains incorrect tags
	 */
	public void proxy(String relUrl, HttpRequest request,
			HttpResponse response, Renderer... renderers) throws IOException,
			HttpErrorPage {
		initHttpRequestParams(request, response, null);
		
		if (LOG.isInfoEnabled()) {
			LOG.info("proxy provider={} relUrl={}", config.getInstanceName(),
					relUrl);
		}

		request.setCharacterEncoding(config.getUriEncoding());

		// Create Proxy event
		ProxyEvent e = new ProxyEvent();
		e.originalRequest = request;

		// Event pre-proxy
		eventManager.fire(EventManager.EVENT_PROXY_PRE, e);

		// Return immediately if exit is requested by extension
		if (e.exit) {
			return;
		}

		String url = ResourceUtils.getHttpUrlWithQueryString(relUrl,
				e.originalRequest, true);
		GenericHttpRequest httpRequest = httpClientHelper.createHttpRequest(
				request, url, true);
		org.apache.http.HttpResponse httpResponse = execute(httpRequest,
				request);
		if (!isTextContentType(httpResponse)) {
			LOG.debug("'{}' is binary on no transformation to apply: was forwarded without modification.", relUrl);
			httpClientHelper.render(httpResponse, response);
		} else {
			LOG.debug("'{}' is text : will apply renderers.", relUrl);
			String currentValue = HttpResponseUtils.toString(httpResponse);

			List<Renderer> listOfRenderers = new ArrayList<Renderer>(
					renderers.length + 1);
			if (config.isFixResources()) {
				String baseUrl = HttpRequestHelper.getBaseUrl(request)
						.toString();
				ResourceFixupRenderer fixup = new ResourceFixupRenderer(
						baseUrl, config.getVisibleBaseURL(baseUrl), relUrl,
						config.getFixMode());
				listOfRenderers.add(fixup);
			}
			listOfRenderers.addAll(Arrays.asList(renderers));

			for (Renderer renderer : listOfRenderers) {
				StringWriter stringWriter = new StringWriter();
				renderer.render(request, currentValue, stringWriter);
				currentValue = stringWriter.toString();
			}
			// Write the result to the OutpuStream using default charset
			// ISO-8859-1 if not defined
			String charsetName = HttpResponseUtils
					.getContentCharset(httpResponse);
			if (charsetName == null) {
				charsetName = "ISO-8859-1";
			}
			httpClientHelper.render(currentValue, httpResponse, response);
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
	 * @throws IOException
	 */
	public String getResourceAsString(String url, HttpRequest originalRequest)
			throws HttpErrorPage, IOException {
		String result;
		url = VariablesResolver.replaceAllVariables(url, originalRequest);
		url = ResourceUtils.getHttpUrlWithQueryString(url, originalRequest,
				false);

		// Pre get ressource

		// pre : get from cache
		boolean cacheable = "GET".equalsIgnoreCase(originalRequest
				.getRequestLine().getMethod());
		if (cacheable) {
			result = (String) originalRequest.getParams().getParameter(url);
			if (result != null)
				return result;
		}

		GenericHttpRequest httpRequest = httpClientHelper.createHttpRequest(
				originalRequest, url, false);
		org.apache.http.HttpResponse httpResponse = execute(httpRequest,
				originalRequest);

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
	private boolean isTextContentType(org.apache.http.HttpResponse httpResponse) {
		String contentType = HttpResponseUtils.getFirstHeader(
				HttpHeaders.CONTENT_TYPE, httpResponse);
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

	public org.apache.http.HttpResponse executeSingleRequest(
			GenericHttpRequest httpRequest, HttpContext httpContext,
			HttpRequest originalRequest) {


		// Create request event
		FragmentEvent event = new FragmentEvent();
		event.httpContext = httpContext;
		event.httpRequest = httpRequest;
		event.originalRequest = originalRequest;
		event.httpResponse = null;

		// EVENT pret
		eventManager.fire(EventManager.EVENT_FRAGMENT_PRE, event);

		// If exit : stop immediately.
		if (!event.exit) {
			// Proceed to request only if extensions did not inject a response.
			if (event.httpResponse == null) {
				event.httpResponse = httpClientHelper.execute(
						event.httpRequest, event.httpContext);
			}

			// EVENT post 
			eventManager.fire(EventManager.EVENT_FRAGMENT_POST, event);
		}
		
	
		
		return event.httpResponse;
	}

	private org.apache.http.HttpResponse execute(
			GenericHttpRequest httpRequest, HttpRequest originalRequest)
			throws HttpErrorPage, IOException {
		CookieStore cookieStore = new RequestCookieStore(cookieManager,
				originalRequest);

		org.apache.http.HttpResponse httpResponse = executeSingleRequest(
				httpRequest, httpClientHelper.createHttpContext(cookieStore),
				originalRequest);

		// Handle errors.
		if (httpResponse == null) {
			throw new HttpErrorPage(500, "Request was cancelled by server",
					"Request was cancelled by server");
		}
		if (HttpResponseUtils.isError(httpResponse)) {
			throw new HttpErrorPage(httpResponse, httpClientHelper);
		}
		return httpResponse;
	}

}

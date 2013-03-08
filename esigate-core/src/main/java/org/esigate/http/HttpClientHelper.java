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

package org.esigate.http;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.DefaultedHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.esigate.HttpErrorPage;
import org.esigate.Parameters;
import org.esigate.cache.CacheConfigHelper;
import org.esigate.cookie.CookieManager;
import org.esigate.events.EventManager;
import org.esigate.events.impl.FragmentEvent;
import org.esigate.util.HttpRequestHelper;
import org.esigate.util.UriUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HttpClientHelper is responsible for creating Apache HttpClient requests from
 * incoming requests. It can copy a request with its method and entity or simply
 * create a new GET request to the same URI. Some parameters enable to control
 * which http headers have to be copied and whether or not to preserve the
 * original host header.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class HttpClientHelper {
	private final static Logger LOG = LoggerFactory.getLogger(HttpClientHelper.class);
	private static final Set<String> SIMPLE_METHODS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("GET", "HEAD", "OPTIONS", "TRACE", "DELETE")));
	private static final Set<String> ENTITY_METHODS = Collections
			.unmodifiableSet(new HashSet<String>(Arrays.asList("POST", "PUT", "PROPFIND", "PROPPATCH", "MKCOL", "COPY", "MOVE", "LOCK", "UNLOCK")));
	private static final String ORIGINAL_REQUEST_KEY = "ORIGINAL_REQUEST";
	private static final String TARGET_HOST = "TARGET_HOST";
	private final boolean preserveHost;
	private CookieManager cookieManager;
	private HttpClient httpClient;
	private EventManager eventManager = null;
	private HeaderManager headerManager;
	private final int connectTimeout;
	private final int socketTimeout;

	private final static HttpClient buildHttpClient(Properties properties, EventManager eventManager, HttpClientConnectionManager connectionManager) {
		HttpHost proxyHost = null;
		Credentials proxyCredentials = null;
		// Proxy settings
		String proxyHostParameter = Parameters.PROXY_HOST.getValueString(properties);
		if (proxyHostParameter != null) {
			int proxyPort = Parameters.PROXY_PORT.getValueInt(properties);
			proxyHost = new HttpHost(proxyHostParameter, proxyPort);
			String proxyUser = Parameters.PROXY_USER.getValueString(properties);
			if (proxyUser != null) {
				String proxyPassword = Parameters.PROXY_PASSWORD.getValueString(properties);
				proxyCredentials = new UsernamePasswordCredentials(proxyUser, proxyPassword);
			}
		}

		ProxyingHttpClientBuilder httpClientBuilder = new ProxyingHttpClientBuilder();

		httpClientBuilder.setProperties(properties);

		httpClientBuilder.setMaxConnPerRoute(Parameters.MAX_CONNECTIONS_PER_HOST.getValueInt(properties));
		httpClientBuilder.setMaxConnTotal(Parameters.MAX_CONNECTIONS_PER_HOST.getValueInt(properties));
		httpClientBuilder.setRedirectStrategy(new RedirectStrategy());
		// Proxy settings
		if (proxyHost != null) {
			httpClientBuilder.setProxy(proxyHost);
			if (proxyCredentials != null) {
				CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
				credentialsProvider.setCredentials(new AuthScope(proxyHost), proxyCredentials);
				httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
			}
		}

		// Cache settings
		boolean useCache = Parameters.USE_CACHE.getValueBoolean(properties);
		httpClientBuilder.setUseCache(Parameters.USE_CACHE.getValueBoolean(properties));
		if (useCache) {
			httpClientBuilder.setHttpCacheStorage(CacheConfigHelper.createCacheStorage(properties));
			httpClientBuilder.setCacheConfig(CacheConfigHelper.createCacheConfig(properties));
		}

		// Event manager
		httpClientBuilder.setEventManager(eventManager);

		// Used for tests to skip connection manager and return hard coded
		// responses
		if (connectionManager != null)
			httpClientBuilder.setConnectionManager(connectionManager);

		return httpClientBuilder.build();
	}

	private HttpClientHelper(Properties properties, CookieManager cookieManager) {
		preserveHost = Parameters.PRESERVE_HOST.getValueBoolean(properties);
		headerManager = new HeaderManager(properties);
		this.cookieManager = cookieManager;
		connectTimeout = Parameters.CONNECT_TIMEOUT.getValueInt(properties);
		socketTimeout = Parameters.SOCKET_TIMEOUT.getValueInt(properties);

	}

	public HttpClientHelper(EventManager eventManager, CookieManager cookieManager, Properties properties) {
		this(properties, cookieManager);
		this.eventManager = eventManager;
		httpClient = buildHttpClient(properties, eventManager, null);
	}

	public HttpClientHelper(EventManager eventManager, CookieManager cookieManager, Properties properties, HttpClientConnectionManager connectionManager) {
		this(properties, cookieManager);
		this.eventManager = eventManager;
		httpClient = buildHttpClient(properties, eventManager, connectionManager);
	}

	public GenericHttpRequest createHttpRequest(HttpEntityEnclosingRequest originalRequest, String uri, boolean proxy) throws HttpErrorPage {
		// Extract the host in the URI. This is the host we have to send the
		// request to physically. We will use this value to force the route to
		// the server
		HttpHost targetHost = UriUtils.extractHost(uri);

		// Preserve host if required
		HttpHost virtualHost;
		if (preserveHost)
			virtualHost = HttpRequestHelper.getHost(originalRequest);
		else
			virtualHost = targetHost;

		// FIXME if we don't preserveHost and we are clustering, we will have
		// one different cache entry for each node. The unit test for that is
		// commented out
		// Rewrite the uri with the virtualHost, this is the key used by the
		// cache
		uri = UriUtils.rewriteURI(uri, virtualHost).toString();

		String method = (proxy) ? originalRequest.getRequestLine().getMethod().toUpperCase() : "GET";
		GenericHttpRequest httpRequest;
		if (SIMPLE_METHODS.contains(method)) {
			httpRequest = new GenericHttpRequest(method, uri, originalRequest.getProtocolVersion());
		} else if (ENTITY_METHODS.contains(method)) {
			httpRequest = new GenericHttpRequest(method, uri, originalRequest.getProtocolVersion());
			httpRequest.setEntity(originalRequest.getEntity());
		} else {
			throw new UnsupportedHttpMethodException(method + " " + uri);
		}

		RequestConfig.Builder builder = RequestConfig.custom();
		builder.setConnectTimeout(connectTimeout);
		builder.setSocketTimeout(socketTimeout);
		builder.setCircularRedirectsAllowed(true);
		// Use browser compatibility cookie policy. This policy is the closest
		// to the behavior of a real browser.
		builder.setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY);

		builder.setRedirectsEnabled(!proxy);

		httpRequest.setConfig(builder.build());

		httpRequest.setParams(new DefaultedHttpParams(new BasicHttpParams(), originalRequest.getParams()));

		// We use the same user-agent and accept headers that the one sent by
		// the browser as some web sites generate different pages and scripts
		// depending on the browser
		headerManager.copyHeaders(originalRequest, httpRequest);

		httpRequest.getParams().setParameter(TARGET_HOST, targetHost);
		httpRequest.getParams().setParameter(ORIGINAL_REQUEST_KEY, originalRequest);
		// When the cache is used the request from ExecutionContext is not set
		// so we set it just in case
		httpRequest.getParams().setParameter(ExecutionContext.HTTP_REQUEST, httpRequest);

		httpRequest.setHeader(HttpHeaders.HOST, virtualHost.toHostString());

		return httpRequest;
	}

	public HttpResponse execute(GenericHttpRequest httpRequest) {
		HttpEntityEnclosingRequest originalRequest = (HttpEntityEnclosingRequest) httpRequest.getParams().getParameter(ORIGINAL_REQUEST_KEY);
		HttpContext httpContext = new BasicHttpContext();

		if (cookieManager != null) {
			CookieStore cookieStore = new RequestCookieStore(cookieManager, originalRequest);
			httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		}
		HttpResponse result;
		// Create request event
		FragmentEvent event = new FragmentEvent();
		event.httpRequest = httpRequest;
		event.originalRequest = originalRequest;
		event.httpResponse = null;
		event.httpContext = httpContext;
		// EVENT pre
		eventManager.fire(EventManager.EVENT_FRAGMENT_PRE, event);
		// If exit : stop immediately.
		if (!event.exit) {
			// Proceed to request only if extensions did not inject a response.
			if (event.httpResponse == null) {
				try {
					HttpHost host = (HttpHost) httpRequest.getParams().getParameter(TARGET_HOST);
					HttpResponse response = httpClient.execute(host, httpRequest, httpContext);
					result = new BasicHttpResponse(response.getStatusLine());
					headerManager.copyHeaders(httpRequest, originalRequest, response, result);
					result.setEntity(response.getEntity());
				} catch (IOException e) {
					int statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
					String statusText = "Error retrieving URL";
					LOG.warn(httpRequest.getRequestLine() + " -> " + statusCode + " " + statusText);
					result = IOExceptionHandler.toHttpResponse(e);
				}
				// FIXME workaround for a bug in http client cache that does not
				// keep params in response
				result.setParams(httpRequest.getParams());
				event.httpResponse = result;
			}
			// EVENT post
			eventManager.fire(EventManager.EVENT_FRAGMENT_POST, event);
		}
		return event.httpResponse;
	}

	/**
	 * Get the Event manager used to create the HttpClientHelper.
	 * 
	 * @return current EventManager or null if no cacheDecorator was used.
	 */
	public EventManager getEventManager() {
		return eventManager;
	}

}

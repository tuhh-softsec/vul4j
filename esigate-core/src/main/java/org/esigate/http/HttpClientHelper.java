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
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.DefaultedHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.esigate.ConfigurationException;
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
	private EventManager eventManager;
	private HeaderManager headerManager;

	private final static HttpClient buildDefaultHttpClient(Properties properties) {
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
		// Create and initialize scheme registry
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		try {
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, null, null);
			SSLSocketFactory sslSocketFactory = new SSLSocketFactory(sslContext, SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
			Scheme https = new Scheme("https", 443, sslSocketFactory);
			schemeRegistry.register(https);
		} catch (NoSuchAlgorithmException e) {
			throw new ConfigurationException(e);
		} catch (KeyManagementException e) {
			throw new ConfigurationException(e);
		}
		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		// Create an HttpClient with the ThreadSafeClientConnManager.
		// This connection manager must be used if more than one thread will
		// be using the HttpClient.
		PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager(schemeRegistry);
		connectionManager.setMaxTotal(Parameters.MAX_CONNECTIONS_PER_HOST.getValueInt(properties));
		connectionManager.setDefaultMaxPerRoute(Parameters.MAX_CONNECTIONS_PER_HOST.getValueInt(properties));
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, Parameters.CONNECT_TIMEOUT.getValueInt(properties));
		HttpConnectionParams.setSoTimeout(httpParams, Parameters.SOCKET_TIMEOUT.getValueInt(properties));
		httpParams.setBooleanParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
		DefaultHttpClient defaultHttpClient = new DefaultHttpClient(connectionManager, httpParams);
		defaultHttpClient.setRedirectStrategy(new RedirectStrategy());
		// Proxy settings
		if (proxyHost != null) {
			if (proxyCredentials != null) {
				defaultHttpClient.getCredentialsProvider().setCredentials(new AuthScope(proxyHost.getHostName(), proxyHost.getPort()), proxyCredentials);
			}
			defaultHttpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyHost);
		}
		return defaultHttpClient;
	}

	private final static HttpClient decorateWithCache(EventManager d, HttpClient HttpClient, Properties properties) {
		boolean useCache = Parameters.USE_CACHE.getValueBoolean(properties);
		if (useCache) {
			return CacheConfigHelper.addCache(d, properties, HttpClient);
		} else {
			return HttpClient;
		}
	}

	private HttpClientHelper(Properties properties, CookieManager cookieManager) {
		preserveHost = Parameters.PRESERVE_HOST.getValueBoolean(properties);
		headerManager = new HeaderManager(properties);
		this.cookieManager = cookieManager;
	}

	public HttpClientHelper(EventManager eventManager, CookieManager cookieManager, HttpClient defaultHttpClient, Properties properties) {
		this(properties, cookieManager);
		this.eventManager = eventManager;
		httpClient = decorateWithCache(eventManager, defaultHttpClient, properties);
	}

	public HttpClientHelper(EventManager eventManager, CookieManager cookieManager, Properties properties) {
		this(properties, cookieManager);
		this.eventManager = eventManager;
		httpClient = decorateWithCache(eventManager, buildDefaultHttpClient(properties), properties);
	}

	public GenericHttpRequest createHttpRequest(HttpEntityEnclosingRequest originalRequest, String uri, boolean proxy) throws HttpErrorPage {
		// Extract the host in the URI. This is the host we have to send the
		// request to physically. We will use this value to force the route to
		// the server
		HttpHost uriHost = UriUtils.extractHost(UriUtils.createUri(uri));

		// Rewrite requested URI with the original request host. This is the key
		// for the cache
		HttpHost targetHost = UriUtils.extractHost(originalRequest.getRequestLine().getUri());
		uri = UriUtils.rewriteURI(uri, targetHost).toString();
		String method = (proxy) ? originalRequest.getRequestLine().getMethod().toUpperCase() : "GET";
		GenericHttpRequest httpRequest;
		if (SIMPLE_METHODS.contains(method)) {
			httpRequest = new GenericHttpRequest(method, uri);
		} else if (ENTITY_METHODS.contains(method)) {
			GenericHttpRequest result = new GenericHttpRequest(method, uri);
			result.setEntity(originalRequest.getEntity());
			httpRequest = result;
		} else {
			throw new UnsupportedHttpMethodException(method + " " + uri);
		}
		httpRequest.setParams(new DefaultedHttpParams(new BasicHttpParams(), originalRequest.getParams()));
		httpRequest.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, !proxy);
		// Use browser compatibility cookie policy. This policy is the closest
		// to the behavior of a real browser.
		httpRequest.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);

		// Preserve host if required
		if (preserveHost)
			httpRequest.addHeader(HttpHeaders.HOST, HttpRequestHelper.getHost(originalRequest));

		// We use the same user-agent and accept headers that the one sent by
		// the browser as some web sites generate different pages and scripts
		// depending on the browser
		headerManager.copyHeaders(originalRequest, httpRequest);

		httpRequest.getParams().setParameter(TARGET_HOST, uriHost);
		httpRequest.getParams().setParameter(ORIGINAL_REQUEST_KEY, originalRequest);
		// When the cache is used the request from ExecutionContext is not set
		// so we set it just in case
		httpRequest.getParams().setParameter(ExecutionContext.HTTP_REQUEST, httpRequest);

		return httpRequest;
	}

	public HttpResponse execute(GenericHttpRequest httpRequest) {
		HttpEntityEnclosingRequest originalRequest = (HttpEntityEnclosingRequest) httpRequest.getParams().getParameter(ORIGINAL_REQUEST_KEY);
		HttpContext httpContext = new BasicHttpContext();
		if (cookieManager != null) {
			CookieStore cookieStore = new RequestCookieStore(cookieManager, httpRequest);
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
				} catch (HttpHostConnectException e) {
					int statusCode = HttpStatus.SC_BAD_GATEWAY;
					String statusText = "Connection refused";
					LOG.warn(httpRequest.getRequestLine() + " -> " + statusCode + " " + statusText);
					result = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, statusCode, statusText));
				} catch (ConnectionPoolTimeoutException e) {
					int statusCode = HttpStatus.SC_GATEWAY_TIMEOUT;
					String statusText = "Connection pool timeout";
					LOG.warn(httpRequest.getRequestLine() + " -> " + statusCode + " " + statusText);
					result = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, statusCode, statusText));
				} catch (ConnectTimeoutException e) {
					int statusCode = HttpStatus.SC_GATEWAY_TIMEOUT;
					String statusText = "Connect timeout";
					LOG.warn(httpRequest.getRequestLine() + " -> " + statusCode + " " + statusText);
					result = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, statusCode, statusText));
				} catch (SocketTimeoutException e) {
					int statusCode = HttpStatus.SC_GATEWAY_TIMEOUT;
					String statusText = "Socket timeout";
					LOG.warn(httpRequest.getRequestLine() + " -> " + statusCode + " " + statusText);
					result = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, statusCode, statusText));
				} catch (IOException e) {
					int statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
					String statusText = "Error retrieving URL";
					LOG.warn(httpRequest.getRequestLine() + " -> " + statusCode + " " + statusText, e);
					result = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, statusCode, statusText));
				}
				// FIXME workaround for a bug in http client cache that does not
				// keep
				// params in response
				result.setParams(httpRequest.getParams());
				event.httpResponse = result;
			}
			// EVENT post
			eventManager.fire(EventManager.EVENT_FRAGMENT_POST, event);
		}
		return event.httpResponse;
	}

}

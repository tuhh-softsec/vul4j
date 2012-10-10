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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.net.ssl.SSLContext;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
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
import org.apache.http.client.params.ClientParamBean;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.esigate.ConfigurationException;
import org.esigate.HttpErrorPage;
import org.esigate.Parameters;
import org.esigate.api.HttpRequest;
import org.esigate.cache.CacheConfigHelper;
import org.esigate.extension.Extension;
import org.esigate.util.FilterList;
import org.esigate.util.PropertiesUtil;
import org.esigate.util.UriUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HttpClientHelper is responsible for creating Apache HttpClient requests from
 * incoming requests. It can copy a request with its method and entity or simply
 * create a new GET request to the same URI. Some parameters enable to control
 * which http headers have to be copied and wether or not to preserve the
 * original host header.
 * 
 * @author frbon
 * 
 */
public class HttpClientHelper implements Extension {
	private final static Logger LOG = LoggerFactory.getLogger(HttpClientHelper.class);
	private static final Set<String> SIMPLE_METHODS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("GET", "HEAD", "OPTIONS", "TRACE", "DELETE")));
	private static final Set<String> ENTITY_METHODS = Collections
			.unmodifiableSet(new HashSet<String>(Arrays.asList("POST", "PUT", "PROPFIND", "PROPPATCH", "MKCOL", "COPY", "MOVE", "LOCK", "UNLOCK")));
	private static final String ORIGINAL_REQUEST_KEY = "ORIGINAL_REQUEST";
	private static final String SENT_REQUEST_KEY = "SENT_REQUEST";
	private boolean preserveHost;
	private FilterList requestHeadersFilterList;
	private FilterList responseHeadersFilterList;
	private HttpClient httpClient;
	private HttpHost proxyHost;
	private Credentials proxyCredentials;

	public void init(HttpClient defaultHttpClient, Properties properties) {
		boolean useCache = Parameters.USE_CACHE.getValueBoolean(properties);
		if (useCache) {
			httpClient = CacheConfigHelper.addCache(properties, defaultHttpClient);
		} else {
			httpClient = defaultHttpClient;
		}
		preserveHost = Parameters.PRESERVE_HOST.getValueBoolean(properties);
		// Populate headers filter lists
		requestHeadersFilterList = new FilterList();
		responseHeadersFilterList = new FilterList();
		// By default all headers are forwarded
		requestHeadersFilterList.add(Collections.singletonList("*"));
		responseHeadersFilterList.add(Collections.singletonList("*"));
		PropertiesUtil.populate(requestHeadersFilterList, properties, Parameters.FORWARD_REQUEST_HEADERS.name, Parameters.DISCARD_REQUEST_HEADERS.name, "",
				Parameters.DISCARD_REQUEST_HEADERS.defaultValue);
		PropertiesUtil.populate(responseHeadersFilterList, properties, Parameters.FORWARD_RESPONSE_HEADERS.name, Parameters.DISCARD_RESPONSE_HEADERS.name, "",
				Parameters.DISCARD_RESPONSE_HEADERS.defaultValue);
	}

	public void init(Properties properties) {
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
		init(defaultHttpClient, properties);
	}

	protected boolean isForwardedRequestHeader(String headerName) {
		return requestHeadersFilterList.contains(headerName);
	}

	protected boolean isForwardedResponseHeader(String headerName) {
		return responseHeadersFilterList.contains(headerName);
	}

	public GenericHttpRequest createHttpRequest(HttpRequest originalRequest, String uri, boolean proxy) throws HttpErrorPage {
		// Extract the host in the URI
		HttpHost uriHost;
		uriHost = UriUtils.extractHost(UriUtils.createUri(uri));
		HttpHost targetHost = uriHost;
		HttpHost virtualHost = null;
		HttpRoute route = null;
		// Preserve host if required
		if (preserveHost) {
			virtualHost = UriUtils.extractHost(originalRequest.getUri());
			targetHost = virtualHost;
			// force the route to the server in case of load balancing because.
			// The request and the host header (virtualHost) will be the same as
			// in original request but the request will be routed to
			// the host in the original url. We need to do this if we don't want
			// a separate cache entry for each node.
			if (proxyHost == null)
				route = new HttpRoute(uriHost);
			else
				route = new HttpRoute(uriHost, null, proxyHost, false);
			uri = UriUtils.rewriteURI(uri, targetHost).toString();
		}
		String method = (proxy) ? originalRequest.getMethod().toUpperCase() : "GET";
		GenericHttpRequest httpRequest;
		if (SIMPLE_METHODS.contains(method)) {
			httpRequest = new GenericHttpRequest(method, uri);
		} else if (ENTITY_METHODS.contains(method)) {
			GenericHttpRequest result = new GenericHttpRequest(method, uri);
			long contentLength = (originalRequest.getHeader(HttpHeaders.CONTENT_LENGTH) != null) ? Long.parseLong(originalRequest.getHeader(HttpHeaders.CONTENT_LENGTH)) : -1;
			InputStreamEntity inputStreamEntity;
			try {
				inputStreamEntity = new InputStreamEntity(originalRequest.getInputStream(), contentLength);
			} catch (IOException e) {
				throw new HttpErrorPage(HttpStatus.SC_BAD_REQUEST, "Bad request", e);
			}
			if (originalRequest.getContentType() != null) {
				inputStreamEntity.setContentType(originalRequest.getContentType());
			}
			if (originalRequest.getHeader(HttpHeaders.CONTENT_ENCODING) != null) {
				inputStreamEntity.setContentEncoding(originalRequest.getHeader(HttpHeaders.CONTENT_ENCODING));
			}
			result.setEntity(inputStreamEntity);
			httpRequest = result;
		} else {
			throw new UnsupportedHttpMethodException(method + " " + uri);
		}
		httpRequest.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, !proxy);
		// Use browser compatibility cookie policy. This policy is the closest
		// to the behavior of a real browser.
		httpRequest.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
		// We use the same user-agent and accept headers that the one sent by
		// the browser as some web sites generate different pages and scripts
		// depending on the browser

		copyHeaders(originalRequest, httpRequest);

		if (virtualHost != null) {
			ClientParamBean clientParamBean = new ClientParamBean(httpRequest.getParams());
			clientParamBean.setVirtualHost(virtualHost);
			httpRequest.getParams().setParameter(ConnRoutePNames.FORCED_ROUTE, route);
		}
		httpRequest.getParams().setParameter(ORIGINAL_REQUEST_KEY, originalRequest);
		httpRequest.getParams().setParameter(SENT_REQUEST_KEY, httpRequest);

		return httpRequest;
	}

	private void copyHeaders(HttpRequest originalRequest, org.apache.http.HttpRequest httpRequest) throws HttpErrorPage {
		String originalUri = originalRequest.getUri().toString();
		String uri = httpRequest.getRequestLine().getUri();
		for (String name : originalRequest.getHeaderNames()) {
			// Special headers
			// User-agent must be set in a specific way
			if (HttpHeaders.USER_AGENT.equalsIgnoreCase(name) && isForwardedRequestHeader(HttpHeaders.USER_AGENT))
				httpRequest.getParams().setParameter(CoreProtocolPNames.USER_AGENT, originalRequest.getHeader(name));
			// Referer must be rewritten
			else if (HttpHeaders.REFERER.equalsIgnoreCase(name) && isForwardedRequestHeader(HttpHeaders.REFERER)) {
				String value = originalRequest.getHeader(name);
				try {
					value = UriUtils.translateUrl(value, originalUri, uri);
				} catch (MalformedURLException e) {
					throw new HttpErrorPage(HttpStatus.SC_BAD_REQUEST, "Bad request", e);
				}
				httpRequest.addHeader(name, value);
				// All other headers are copied if allowed
			} else if (isForwardedRequestHeader(name)) {
				String value = originalRequest.getHeader(name);
				if (value != null) {
					httpRequest.addHeader(name, value);
				}
			}
		}
		// process X-Forwarded-For header (is missing in request and not
		// blacklisted) -> use remote address instead
		if (originalRequest.getHeader("X-Forwarded-For") == null && isForwardedRequestHeader("X-Forwarded-For") && originalRequest.getRemoteAddr() != null) {
			httpRequest.addHeader("X-Forwarded-For", originalRequest.getRemoteAddr());
		}
	}

	/**
	 * Copies end-to-end headers from a resource to an output.
	 * 
	 * @param httpClientResponse
	 * @param output
	 * @throws MalformedURLException
	 */
	private void copyHeaders(HttpResponse httpClientResponse, org.esigate.api.HttpResponse output) throws MalformedURLException {
		org.apache.http.HttpRequest httpRequest = (org.apache.http.HttpRequest) httpClientResponse.getParams().getParameter(SENT_REQUEST_KEY);
		HttpRequest originalRequest = (HttpRequest) httpClientResponse.getParams().getParameter(ORIGINAL_REQUEST_KEY);
		String originalUri = originalRequest.getUri().toString();
		String uri = httpRequest.getRequestLine().getUri();
		for (Header header : httpClientResponse.getAllHeaders()) {
			String name = header.getName();
			String value = header.getValue();
			if (isForwardedResponseHeader(name)) {
				// Some headers containing an URI have to be rewritten
				if (HttpHeaders.LOCATION.equalsIgnoreCase(name) || HttpHeaders.CONTENT_LOCATION.equalsIgnoreCase(name) || "Link".equalsIgnoreCase(name) || "P3p".equalsIgnoreCase(name)) {
					value = UriUtils.translateUrl(value, uri, originalUri);
					value = HttpResponseUtils.removeSessionId(value, httpClientResponse);
					output.addHeader(name, value);
				} else if (HttpHeaders.CONTENT_ENCODING.equalsIgnoreCase(name)) {
					// Ignore it, it will be copied only when the entity is
					// unchanged
				} else {
					output.addHeader(header.getName(), header.getValue());
				}
			}
		}
	}

	public HttpResponse execute(org.apache.http.HttpRequest httpRequest, HttpContext httpContext) {
		HttpResponse result;
		try {
			HttpHost httpHost = UriUtils.extractHost(httpRequest.getRequestLine().getUri());
			HttpResponse httpResponse = httpClient.execute(httpHost, httpRequest, httpContext);
			org.apache.http.HttpRequest lastRequest = RedirectStrategy.getLastRequest(httpRequest, httpContext);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_NOT_MODIFIED) {
				if (LOG.isInfoEnabled()) {
					LOG.info(lastRequest.getRequestLine() + " -> " + httpResponse.getStatusLine());
				}

			} else if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY || statusCode == HttpStatus.SC_MOVED_PERMANENTLY) {
				if (LOG.isInfoEnabled()) {
					Header locationHeader = httpResponse.getFirstHeader(HttpHeaders.LOCATION);
					String location = "";
					if (locationHeader != null)
						location = locationHeader.getValue();
					LOG.info(lastRequest.getRequestLine() + " -> " + httpResponse.getStatusLine() + " Location: " + location);
				}
			} else {
				if (LOG.isWarnEnabled()) {
					LOG.warn(lastRequest.getRequestLine() + " -> " + httpResponse.getStatusLine());
				}
			}
			result = httpResponse;
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
		// FIXME workaround for a bug in http client cache that does not keep params in response
		result.setParams(httpRequest.getParams());
		return result;

	}

	public HttpContext createHttpContext(CookieStore cookieStore) {
		HttpContext httpContext = new BasicHttpContext();
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		return httpContext;
	}

	public void render(HttpResponse httpResponse, org.esigate.api.HttpResponse output) throws IOException {
		// As the entity is sent unchanged it has not been decompressed so we
		// can copy Accept-encoding header
		String contentEncoding = HttpResponseUtils.getFirstHeader(HttpHeaders.CONTENT_ENCODING, httpResponse);
		if (contentEncoding != null)
			output.addHeader(HttpHeaders.CONTENT_ENCODING, contentEncoding);
		render(httpResponse.getEntity(), httpResponse, output);
	}

	public void render(String transformedEntity, HttpResponse httpResponse, org.esigate.api.HttpResponse output) throws IOException {
		HttpEntity httpEntity = new StringEntity(transformedEntity, ContentType.get(httpResponse.getEntity()));
		render(httpEntity, httpResponse, output);
	}

	private void render(HttpEntity transformedEntity, HttpResponse httpResponse, org.esigate.api.HttpResponse output) throws IOException {
		output.setStatus(httpResponse.getStatusLine().getStatusCode());
		copyHeaders(httpResponse, output);
		if (transformedEntity == null)
			return;
		InputStream content = transformedEntity.getContent();
		try {
			IOUtils.copy(content, output.getOutputStream());
		} finally {
			content.close();
		}
	}
}

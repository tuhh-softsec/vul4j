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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.ClientParamBean;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.esigate.DriverConfiguration;
import org.esigate.HttpErrorPage;
import org.esigate.api.HttpRequest;
import org.esigate.util.UriUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Request to a remote Http server.
 * 
 * @author François-Xavier Bonnet
 * @author Alexis Thaveau
 * @author Nicolas Richeton
 * 
 */
public class HttpClientRequest {
	private final static Logger LOG = LoggerFactory.getLogger(HttpClientRequest.class);
	private String uri;
	private final HttpRequest originalRequest;
	private final boolean proxy;
	private org.apache.http.HttpRequest httpRequest;
	private HashMap<String, String> headers;
	private boolean preserveHost = false;
	private CookieStore cookieStore;
	private DriverConfiguration configuration;

	public HttpClientRequest(String uri, HttpRequest originalRequest, boolean proxy, boolean preserveHost) {
		this.uri = uri;
		this.originalRequest = originalRequest;
		this.proxy = proxy;
		this.preserveHost = preserveHost;
	}

	public HttpClientResponse execute(HttpClient httpClient) throws IOException, HttpErrorPage {
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
			HttpHost proxyHost = (HttpHost) httpClient.getParams().getParameter(ConnRoutePNames.DEFAULT_PROXY);
			// force the route to the server in case of load balancing because. The request and the host header (virtualHost) will be the same as in original request but the request will be routed to
			// the host in the original url. We need to do this if we don't want a separate cache entry for each node.
			if (proxyHost == null)
				route = new HttpRoute(uriHost);
			else
				route = new HttpRoute(uriHost, null, proxyHost, false);
			uri = UriUtils.rewriteURI(uri, targetHost).toString();
		}
		buildHttpMethod();
		if (virtualHost != null) {
			ClientParamBean clientParamBean = new ClientParamBean(httpRequest.getParams());
			clientParamBean.setVirtualHost(virtualHost);
			httpRequest.getParams().setParameter(ConnRoutePNames.FORCED_ROUTE, route);
		}

		long start = System.currentTimeMillis();
		// Do the request
		HttpClientResponse result = new HttpClientResponse(targetHost, httpRequest, httpClient, cookieStore);
		long end = System.currentTimeMillis();
		if (LOG.isDebugEnabled()) {
			LOG.debug(toString() + " -> " + result.toString() + " (" + (end - start) + " ms)");
		}

		return result;
	}

	/** This method copies the body of the request without modification. */
	private static void copyEntity(HttpRequest src, HttpEntityEnclosingRequest dest) throws IOException {
		long contentLength = (src.getHeader(HttpHeaders.CONTENT_LENGTH) != null) ? Long.parseLong(src.getHeader(HttpHeaders.CONTENT_LENGTH)) : -1;
		InputStreamEntity inputStreamEntity = new InputStreamEntity(src.getInputStream(), contentLength);
		if (src.getContentType() != null) {
			inputStreamEntity.setContentType(src.getContentType());
		}
		if (src.getHeader(HttpHeaders.CONTENT_ENCODING) != null) {
			inputStreamEntity.setContentEncoding(src.getHeader(HttpHeaders.CONTENT_ENCODING));
		}
		dest.setEntity(inputStreamEntity);
	}

	private static final Set<String> SIMPLE_METHODS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("GET", "HEAD", "OPTIONS", "TRACE", "DELETE")));
	private static final Set<String> ENTITY_METHODS = Collections
			.unmodifiableSet(new HashSet<String>(Arrays.asList("POST", "PUT", "PROPFIND", "PROPPATCH", "MKCOL", "COPY", "MOVE", "LOCK", "UNLOCK")));

	/** Method creates appropriate apache http request object for request method provided and fills request body if it should exist. */
	private static org.apache.http.HttpRequest createRequestObject(String uri, String method, HttpRequest originalRequest) throws IOException {
		if (SIMPLE_METHODS.contains(method)) {
			return new BasicHttpRequest(method, uri);
		} else if (ENTITY_METHODS.contains(method)) {
			HttpEntityEnclosingRequest result = new BasicHttpEntityEnclosingRequest(method, uri);
			copyEntity(originalRequest, result);
			return result;
		} else {
			throw new UnsupportedHttpMethodException(method + " " + uri);
		}
	}

	private void buildHttpMethod() throws IOException, HttpErrorPage {
		String method = (proxy) ? originalRequest.getMethod().toUpperCase() : "GET";
		httpRequest = createRequestObject(uri, method, originalRequest);

		httpRequest.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, !proxy);
		// Use browser compatibility cookie policy. This policy is the closest
		// to the behavior of a real browser.
		httpRequest.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
		// We use the same user-agent and accept headers that the one sent by
		// the browser as some web sites generate different pages and scripts
		// depending on the browser

		Integer maxWait = originalRequest.getFetchMaxWait();
		if (maxWait != null) {
			HttpConnectionParams.setSoTimeout(httpRequest.getParams(), maxWait);
		}

		// process request headers
		for (String name : originalRequest.getHeaderNames()) {
			// Special headers
			if (HttpHeaders.USER_AGENT.equalsIgnoreCase(name) && configuration.isForwardedRequestHeader(HttpHeaders.USER_AGENT))
				httpRequest.getParams().setParameter(CoreProtocolPNames.USER_AGENT, originalRequest.getHeader(name));
			else if (HttpHeaders.EXPECT.equalsIgnoreCase(name))
				throw new HttpErrorPage(417, "Expection failed", "This server does not support 'Expect' HTTP header.");
			else if (HttpHeaders.REFERER.equalsIgnoreCase(name)) {
				String value = originalRequest.getHeader(name);
				value = RewriteUtils.translateUrl(value, originalRequest.getUri().toString(), uri);
				httpRequest.addHeader(name, value);
			} else if (configuration.isForwardedRequestHeader(name)) {
				String value = originalRequest.getHeader(name);
				if (value != null) {
					httpRequest.addHeader(name, value);
				}
			}
		}
		// process X-Forwarded-For header (is missing in request and not blacklisted) -> use remote address instead
		if (originalRequest.getHeader("X-Forwarded-For") == null && (configuration == null || configuration.isForwardedRequestHeader("X-Forwarded-For")) && originalRequest.getRemoteAddr() != null) {
			httpRequest.addHeader("X-Forwarded-For", originalRequest.getRemoteAddr());
		}
		// process added headers
		if (headers != null) {
			for (Entry<String, String> entry : headers.entrySet()) {
				httpRequest.addHeader(entry.getKey(), entry.getValue());
			}
		}
	}

	public void addHeader(String name, String value) {
		if (headers == null) {
			headers = new HashMap<String, String>();
		}
		headers.put(name, value);
	}

	/**
	 * Get current headers.
	 * 
	 * @return The map of current headers. This is NOT an internal object : changes are ignored.
	 */
	public Map<String, String> getHeaders() {
		return new HashMap<String, String>(headers);
	}

	@Override
	public String toString() {
		return httpRequest.getRequestLine().toString();
	}

	public boolean isPreserveHost() {
		return preserveHost;
	}

	public void setPreserveHost(boolean preserveHost) {
		this.preserveHost = preserveHost;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public void setCookieStore(CookieStore cookieStore) {
		this.cookieStore = cookieStore;
	}

	public void setConfiguration(DriverConfiguration configuration) {
		this.configuration = configuration;
	}
}

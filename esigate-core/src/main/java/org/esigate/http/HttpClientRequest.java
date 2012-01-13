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
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.ClientParamBean;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.esigate.DriverConfiguration;
import org.esigate.api.HttpRequest;
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
		// FIXME HTTPClient 4 uses URI class that is too much restrictive about
		// allowed characters. We have to escape some characters like |{}[]
		// but this may have some side effects.
		// https://issues.apache.org/jira/browse/HTTPCLIENT-900
		this.uri = escapeUnsafeCharacters(uri);
		this.originalRequest = originalRequest;
		this.proxy = proxy;
		this.preserveHost = preserveHost;
	}

	private static final String[] UNSAFE = { "{", "}", "|", "\\", "^", "~", "[", "]", "`" };
	private static final String[] ESCAPED = { "%7B", "%7D", "%7C", "%5C", "%5E", "%7E", "%5B", "%5D", "%60" };

	private static String escapeUnsafeCharacters(String url) {
		String result = url;
		for (int i = 0; i < UNSAFE.length; i++) {
			result = result.replaceAll(Pattern.quote(UNSAFE[i]), ESCAPED[i]);
		}
		return result;
	}

	public HttpClientResponse execute(HttpClient httpClient) throws IOException {
		buildHttpMethod();
		URL url = new URL(uri);
		HttpHost httpHost = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
		// Preserve host if required
		if (preserveHost) {
			// original port is -1 for default ports(80, 443),
			// the real port otherwise
			int originalport = -1;
			if (originalRequest.getServerPort() != 80 && originalRequest.getServerPort() != 443) {
				originalport = originalRequest.getServerPort();
			}
			HttpHost virtualHost = new HttpHost(originalRequest.getServerName(), originalport, originalRequest.getScheme());
			ClientParamBean clientParamBean = new ClientParamBean(httpRequest.getParams());
			clientParamBean.setVirtualHost(virtualHost);
		}

		long start = System.currentTimeMillis();
		// Do the request
		HttpClientResponse result = new HttpClientResponse(httpHost, httpRequest, httpClient, cookieStore);
		long end = System.currentTimeMillis();
		if (LOG.isDebugEnabled()) {
			LOG.debug(toString() + " -> " + result.toString() + " (" + (end - start) + " ms)");
		}

		return result;
	}

	/** This method copies the body of the request without modification. */
	private static void copyEntity(HttpRequest src, HttpEntityEnclosingRequest dest) throws IOException {
		long contentLength = (src.getHeader(HttpHeaders.CONTENT_LENGTH) != null)
				? Long.parseLong(src.getHeader(HttpHeaders.CONTENT_LENGTH))
				: -1;
		InputStreamEntity inputStreamEntity = new InputStreamEntity(src.getInputStream(), contentLength);
		if (src.getContentType() != null) {
			inputStreamEntity.setContentType(src.getContentType());
		}
		if (src.getHeader(HttpHeaders.CONTENT_ENCODING) != null) {
			inputStreamEntity.setContentEncoding(src.getHeader(HttpHeaders.CONTENT_ENCODING));
		}
		dest.setEntity(inputStreamEntity);
	}

	private static final Set<String> SIMPLE_METHODS = Collections.unmodifiableSet(new HashSet<String>(
			Arrays.asList("GET", "HEAD", "OPTIONS", "TRACE", "DELETE")));
	private static final Set<String> ENTITY_METHODS = Collections.unmodifiableSet(new HashSet<String>(
			Arrays.asList("POST", "PUT", "PROPFIND", "PROPPATCH", "MKCOL", "COPY", "MOVE", "LOCK", "UNLOCK")));

	/** Method creates appropriate apache http request object for request method provided and fills request body if it should exist. */
	private static org.apache.http.HttpRequest createRequestObject(String uri, String method, HttpRequest originalRequest)
			throws IOException {
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

	private void buildHttpMethod() throws IOException {
		String method = (proxy) ? originalRequest.getMethod().toUpperCase() : "GET";
		httpRequest = createRequestObject(uri, method, originalRequest);

		httpRequest.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, !proxy);
		// Use browser compatibility cookie policy. This policy is the closest
		// to the behavior of a real browser.
		httpRequest.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
		// We use the same user-agent and accept headers that the one sent by
		// the browser as some web sites generate different pages and scripts
		// depending on the browser
		String userAgent = originalRequest.getHeader(HttpHeaders.USER_AGENT);
		if (userAgent != null) {
			httpRequest.getParams().setParameter(CoreProtocolPNames.USER_AGENT, userAgent);
		}
		
		Integer maxWait = originalRequest.getFetchMaxWait();
		if (maxWait != null) {
			HttpConnectionParams.setSoTimeout(httpRequest.getParams(), maxWait);
		}
		
		// process request headers
		for (String name : originalRequest.getHeaderNames()) {
			if (configuration == null || !configuration.isBlackListed(name)) {
				String value = originalRequest.getHeader(name);
				if (value != null) {
					httpRequest.addHeader(name, value);
				}
			}
		}
		// process X-Forwarded-For header (is missing in request and not blacklisted) -> use remote address instead
		if (originalRequest.getHeader("X-Forwarded-For") == null 
				&& (configuration == null || !configuration.isBlackListed("X-Forwarded-For"))
				&& originalRequest.getRemoteAddr() != null) {
			httpRequest.addHeader("X-Forwarded-For", originalRequest.getRemoteAddr());
		}
		// process other headers
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

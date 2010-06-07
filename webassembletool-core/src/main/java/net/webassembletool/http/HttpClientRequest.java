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
package net.webassembletool.http;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.ClientParamBean;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HttpContext;

/**
 * Request to a remote Http server.
 * 
 * @author François-Xavier Bonnet
 * @author Alexis Thaveau
 * @author Nicolas Richeton
 * 
 */
public class HttpClientRequest {
	private final static Log LOG = LogFactory.getLog(HttpClientRequest.class);
	private String uri;
	private final HttpServletRequest originalRequest;
	private boolean proxy;
	private BasicHttpRequest httpRequest;
	private HashMap<String, String> headers;
	private boolean preserveHost = false;

	public HttpClientRequest(String uri, HttpServletRequest originalRequest,
			boolean proxy, boolean preserveHost) {
		// FIXME HTTPClient 4 uses URI class that is too much restrictive about
		// allowed characters. We have to escape some characters like |{}[]
		// but this may have some side effects.
		// https://issues.apache.org/jira/browse/HTTPCLIENT-900
		this.uri = escapeUnsafeCharacters(uri);
		this.originalRequest = originalRequest;
		this.proxy = proxy;
		this.preserveHost = preserveHost;
	}

	private String escapeUnsafeCharacters(String url) {
		final String[] UNSAFE = { "{", "}", "|", "\\", "^", "~", "[", "]", "`" };
		final String[] ESCAPED = { "%7B", "%7D", "%7C", "%5C", "%5E", "%7E",
				"%5B", "%5D", "%60" };
		String result = url;
		for (int i = 0; i < UNSAFE.length; i++) {
			result = result.replaceAll(Pattern.quote(UNSAFE[i]), ESCAPED[i]);
		}
		return result;
	}

	public HttpClientResponse execute(HttpClient httpClient,
			HttpContext httpContext) throws IOException {
		buildHttpMethod();
		HttpClientResponse result;
		URL url = new URL(uri);
		HttpHost httpHost = new HttpHost(url.getHost(), url.getPort(), url
				.getProtocol());
		// Preserve host if required
		if (preserveHost) {
			// original port is -1 for default ports(80, 443),
			// the real port otherwise
			int originalport = -1;
			if (originalRequest.getServerPort() != 80
					&& originalRequest.getServerPort() != 443) {
				originalport = originalRequest.getServerPort();
			}
			HttpHost virtualHost = new HttpHost(
					originalRequest.getServerName(), originalport,
					originalRequest.getScheme());
			ClientParamBean clientParamBean = new ClientParamBean(httpRequest
					.getParams());
			clientParamBean.setVirtualHost(virtualHost);
		}

		long start = System.currentTimeMillis();
		// Do the request
		result = new HttpClientResponse(httpHost, httpRequest, httpClient,
				httpContext);
		long end = System.currentTimeMillis();
		if (LOG.isDebugEnabled()) {
			LOG.debug(toString() + " -> " + result.toString() + " ("
					+ (end - start) + " ms)");
		}

		return result;
	}

	/**
	 * This method copies the body of the request without modification.
	 * 
	 * @throws IOException
	 *             if problem getting the request
	 */
	private void copyEntity(HttpServletRequest req,
			HttpEntityEnclosingRequest httpEntityEnclosingRequest)
			throws IOException {
		long contentLengthLong = -1;
		String contentLength = req.getHeader("Content-length");
		if (contentLength != null) {
			contentLengthLong = Long.parseLong(contentLength);
		}
		InputStreamEntity inputStreamEntity = new InputStreamEntity(req
				.getInputStream(), contentLengthLong);
		String contentType = req.getContentType();
		if (contentType != null) {
			inputStreamEntity.setContentType(contentType);
		}
		String contentEncoding = req.getHeader("Content-Encoding");
		if (contentEncoding != null) {
			inputStreamEntity.setContentEncoding(contentEncoding);
		}
		httpEntityEnclosingRequest.setEntity(inputStreamEntity);
	}

	private void buildHttpMethod() throws IOException {
		String method;
		if (proxy) {
			method = originalRequest.getMethod();
		} else {
			method = "GET";
		}
		if ("GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method)
				|| "OPTIONS".equalsIgnoreCase(method)
				|| "TRACE".equalsIgnoreCase(method)
				|| "DELETE".equalsIgnoreCase(method)) {
			httpRequest = new BasicHttpRequest(method, uri);
		} else if ("POST".equalsIgnoreCase(method)
				|| "PUT".equalsIgnoreCase(method)
				|| "PROPFIND".equalsIgnoreCase(method)
				|| "PROPPATCH".equalsIgnoreCase(method)
				|| "MKCOL".equalsIgnoreCase(method)
				|| "COPY".equalsIgnoreCase(method)
				|| "MOVE".equalsIgnoreCase(method)
				|| "LOCK".equalsIgnoreCase(method)
				|| "UNLOCK".equalsIgnoreCase(method)) {
			BasicHttpEntityEnclosingRequest genericHttpEntityEnclosingRequest = new BasicHttpEntityEnclosingRequest(
					method, uri);
			copyEntity(originalRequest, genericHttpEntityEnclosingRequest);
			httpRequest = genericHttpEntityEnclosingRequest;
		} else {
			throw new UnsupportedHttpMethodException(method + " " + uri);
		}
		if (proxy) {
			httpRequest.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS,
					false);
		} else {
			httpRequest.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS,
					true);
		}
		// Use browser compatibility cookie policy. This policy is the closest
		// to the behavior of a real browser.
		httpRequest.getParams().setParameter(ClientPNames.COOKIE_POLICY,
				CookiePolicy.BROWSER_COMPATIBILITY);
		// We use the same user-agent and accept headers that the one sent by
		// the browser as some web sites generate different pages and scripts
		// depending on the browser
		String userAgent = originalRequest.getHeader("User-Agent");
		if (userAgent != null) {
			httpRequest.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
					userAgent);
		}
		copyRequestHeader("Accept");
		copyRequestHeader("Accept-Encoding");
		copyRequestHeader("Accept-Language");
		copyRequestHeader("Accept-Charset");
		copyRequestHeader("Cache-control");
		copyRequestHeader("Pragma");
		if (headers != null) {
			for (Entry<String, String> entry : headers.entrySet()) {
				httpRequest.addHeader(entry.getKey(), entry.getValue());
			}
		}
	}

	private void copyRequestHeader(String name) {
		if (originalRequest != null) {
			String value = originalRequest.getHeader(name);
			if (value != null) {
				httpRequest.setHeader(name, value);
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
	 * @return The map of current headers. This is NOT an internal object :
	 *         changes are ignored.
	 */
	public Map<String, String> getHeaders() {
		return new HashMap<String, String>(headers);
	}

	@Override
	public String toString() {
		return httpRequest.getRequestLine().toString();
	}

	public boolean isProxy() {
		return proxy;
	}

	public void setProxy(boolean proxy) {
		this.proxy = proxy;
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
}

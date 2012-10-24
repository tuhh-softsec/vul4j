/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.esigate.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.http.cookie.Cookie;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.esigate.api.HttpRequest;
import org.esigate.api.HttpSession;
import org.esigate.util.UriUtils;

public class MockHttpRequest implements HttpRequest {
	private String characterEncoding;
	private URI uri;
	private String method = "GET";
	private HttpSession session;
	private final ArrayList<Cookie> cookies = new ArrayList<Cookie>();
	private final HashMap<String, String> headers = new HashMap<String, String>();
	private byte[] content;
	private boolean consumed = false;
	private String remoteUser;
	private HttpParams params = new BasicHttpParams();

	public MockHttpRequest(String uri) {
		setUri(uri);
	}

	public MockHttpRequest() {
		this("http://localhost:8080");
	}

	public void setUri(String uri) {
		this.uri = UriUtils.createUri(uri);
		setHeader("Host", getUri().getHost());
	}

	public String getCharacterEncoding() {
		return characterEncoding;
	}

	public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
		this.characterEncoding = env;
	}

	public String getContentType() {
		return getHeader("Content-type");
	}

	public InputStream getInputStream() throws IOException {
		if (consumed) {
			throw new IllegalStateException("InputStream already obtained");
		}
		consumed = true;
		final InputStream is = new ByteArrayInputStream(content);
		return new InputStream() {
			@Override
			public int read() throws IOException {
				return is.read();
			}
		};
	}

	public String getParameter(String name) {
		String queryString = uri.getRawQuery();
		String[] params = queryString.split("&");
		for (String param : params) {
			String paramName = param.split("=")[0];
			String paramValue = param.split("=")[1];
			if (name.equals(paramName)) {
				return paramValue;
			}
		}
		return null;
	}

	public String getRemoteAddr() {
		return null;
	}

	public boolean isSecure() {
		throw new RuntimeException("Method not implemented");
	}

	public Cookie[] getCookies() {
		Cookie[] cookiesArray = new Cookie[cookies.size()];
		return cookies.toArray(cookiesArray);
	}

	public void addCookie(Cookie cookie) {
		cookies.add(cookie);
	}

	public String getHeader(String name) {
		return headers.get(name.toLowerCase());
	}

	public void setHeader(String name, String value) {
		headers.put(name.toLowerCase(), value);
	}

	public Collection<String> getHeaderNames() {
		return headers.keySet();
	}

	public String getMethod() {
		return method;
	}

	public String getRemoteUser() {
		return remoteUser;
	}

	public Principal getUserPrincipal() {
		throw new RuntimeException("Method not implemented");
	}

	public HttpSession getSession(boolean create) {
		if (create && session == null) {
			session = new MockHttpSession();
		}
		return session;
	}

	public URI getUri() {
		return uri;
	}

	@Override
	public String toString() {
		return method + " " + uri.toString();
	}

	public InputStream getResourceAsStream(String path) {
		throw new RuntimeException("Method not implemented");
	}

	public HttpParams getParams() {
		return params;
	}

}

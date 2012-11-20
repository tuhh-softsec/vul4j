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

import java.io.InputStream;
import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpVersion;
import org.apache.http.RequestLine;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicRequestLine;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.esigate.api.HttpRequest;
import org.esigate.api.HttpSession;
import org.esigate.util.UriUtils;

public class MockHttpRequest implements HttpRequest {
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
		setHeader("Host", this.uri.getHost());
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

	public void setHeader(String name, String value) {
		headers.put(name.toLowerCase(), value);
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

	public RequestLine getRequestLine() {
		return new BasicRequestLine(method, uri.toString(), HttpVersion.HTTP_1_1);
	}

	public Header[] getHeaders(String name) {
		String value = headers.get(name.toLowerCase());
		if (value == null)
			return new Header[] {};
		return new Header[] { new BasicHeader(name, value) };
	}

	public Header[] getAllHeaders() {
		Header[] result = new Header[headers.size()];
		Iterator<Entry<String, String>> iterator = headers.entrySet().iterator();
		int i = 0;
		while (iterator.hasNext()) {
			Entry<String, String> entry = iterator.next();
			result[i] = new BasicHeader(entry.getKey(), entry.getValue());
			i++;
		}
		return result;
	}

	public HttpEntity getEntity() {
		if (consumed) {
			throw new IllegalStateException("InputStream already obtained");
		}
		consumed = true;
		Header[] contentTypeHeaders = getHeaders(HttpHeaders.CONTENT_TYPE);
		if (contentTypeHeaders.length > 0)
			return new ByteArrayEntity(content, ContentType.parse(contentTypeHeaders[0].getValue()));
		return new ByteArrayEntity(content);
	}

}

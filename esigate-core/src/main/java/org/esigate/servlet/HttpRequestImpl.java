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

package org.esigate.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.Header;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.BasicRequestLine;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.esigate.api.HttpRequest;
import org.esigate.api.HttpSession;
import org.esigate.util.UriUtils;

public class HttpRequestImpl implements HttpRequest {

	private final HttpServletRequest parent;
	private final ServletContext servletContext;
	private final HttpParams params = new BasicHttpParams();
	private final RequestLine requestLine;

	private HttpRequestImpl(HttpServletRequest parent, ServletContext servletContext) {
		this.parent = parent;
		this.servletContext = servletContext;
		String uri = UriUtils.createURI(parent.getScheme(), parent.getServerName(), parent.getServerPort(), parent.getRequestURI(), parent.getQueryString(), null).toString();
		ProtocolVersion protocolVersion = BasicLineParser.parseProtocolVersion(parent.getProtocol(), null);
		this.requestLine = new BasicRequestLine(parent.getMethod(), uri, protocolVersion);
	}

	public static HttpRequest wrap(HttpServletRequest parent, ServletContext servletContext) {
		return new HttpRequestImpl(parent, servletContext);

	}

	public String getParameter(String name) {
		return parent.getParameter(name);
	}

	public Cookie[] getCookies() {
		javax.servlet.http.Cookie[] src = parent.getCookies();
		Cookie result[] = null;
		if (src != null) {
			result = new Cookie[src.length];
			for (int i = 0; i < src.length; i++) {
				javax.servlet.http.Cookie c = src[i];
				BasicClientCookie dest = new BasicClientCookie(c.getName(), c.getValue());
				dest.setSecure(c.getSecure());
				dest.setDomain(c.getDomain());
				dest.setPath(c.getPath());
				dest.setComment(c.getComment());
				dest.setVersion(c.getVersion());
				// if (cookie.getMaxAge() < 0) {

				result[i] = dest;
			}
		}
		return result;
	}

	public String getRemoteAddr() {
		return parent.getRemoteAddr();
	}

	public InputStream getInputStream() throws IOException {
		return parent.getInputStream();
	}

	public String getContentType() {
		return parent.getContentType();
	}

	public boolean isSecure() {
		return parent.isSecure();
	}

	public String getCharacterEncoding() {
		return parent.getCharacterEncoding();
	}

	public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
		this.parent.setCharacterEncoding(env);
	}

	public String getRemoteUser() {
		return parent.getRemoteUser();
	}

	public Principal getUserPrincipal() {
		return parent.getUserPrincipal();
	}

	public HttpSession getSession(boolean create) {
		javax.servlet.http.HttpSession session = parent.getSession(create);
		return session != null ? HttpSessionImpl.wrap(session) : null;
	}

	public InputStream getResourceAsStream(String path) {
		return servletContext.getResourceAsStream(path);
	}

	public HttpParams getParams() {
		return params;
	}

	public RequestLine getRequestLine() {
		return requestLine;
	}

	public Header[] getHeaders(String name) {
		@SuppressWarnings("rawtypes")
		Enumeration values = parent.getHeaders(name);
		ArrayList<Header> headers = new ArrayList<Header>();
		while (values.hasMoreElements()) {
			String value = (String) values.nextElement();
			headers.add(new BasicHeader(name, value));
		}
		Header[] t = new Header[] {};
		return headers.toArray(t);
	}

	public Header[] getAllHeaders() {
		@SuppressWarnings("rawtypes")
		Enumeration names = parent.getHeaderNames();
		ArrayList<Header> headers = new ArrayList<Header>();
		String name;
		while (names.hasMoreElements()) {
			name = (String) names.nextElement();
			@SuppressWarnings("rawtypes")
			Enumeration values = parent.getHeaders(name);
			while (values.hasMoreElements()) {
				String value = (String) values.nextElement();
				headers.add(new BasicHeader(name, value));
			}
		}
		Header[] t = new Header[] {};
		return headers.toArray(t);
	}

}

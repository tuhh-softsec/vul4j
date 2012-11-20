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
import org.apache.http.HttpEntity;
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

	private final HttpServletRequest request;
	private final ServletContext servletContext;
	private final HttpParams params = new BasicHttpParams();
	private final RequestLine requestLine;
	private final HttpEntity entity;

	private HttpRequestImpl(HttpServletRequest httpServletRequest, ServletContext servletContext) {
		this.request = httpServletRequest;
		this.servletContext = servletContext;
		String uri = UriUtils.createURI(httpServletRequest.getScheme(), httpServletRequest.getServerName(), httpServletRequest.getServerPort(), httpServletRequest.getRequestURI(), httpServletRequest.getQueryString(), null).toString();
		ProtocolVersion protocolVersion = BasicLineParser.parseProtocolVersion(httpServletRequest.getProtocol(), null);
		this.requestLine = new BasicRequestLine(httpServletRequest.getMethod(), uri, protocolVersion);
		entity = new HttpServletRequestEntity(request);
	}

	public static HttpRequest wrap(HttpServletRequest parent, ServletContext servletContext) {
		return new HttpRequestImpl(parent, servletContext);

	}

	public String getParameter(String name) {
		return request.getParameter(name);
	}

	public Cookie[] getCookies() {
		javax.servlet.http.Cookie[] src = request.getCookies();
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
				result[i] = dest;
			}
		}
		return result;
	}

	public String getRemoteAddr() {
		return request.getRemoteAddr();
	}

	public InputStream getInputStream() throws IOException {
		return request.getInputStream();
	}

	public String getContentType() {
		return request.getContentType();
	}

	public boolean isSecure() {
		return request.isSecure();
	}

	public String getCharacterEncoding() {
		return request.getCharacterEncoding();
	}

	public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
		this.request.setCharacterEncoding(env);
	}

	public String getRemoteUser() {
		return request.getRemoteUser();
	}

	public Principal getUserPrincipal() {
		return request.getUserPrincipal();
	}

	public HttpSession getSession(boolean create) {
		javax.servlet.http.HttpSession session = request.getSession(create);
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
		Enumeration values = request.getHeaders(name);
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
		Enumeration names = request.getHeaderNames();
		ArrayList<Header> headers = new ArrayList<Header>();
		String name;
		while (names.hasMoreElements()) {
			name = (String) names.nextElement();
			@SuppressWarnings("rawtypes")
			Enumeration values = request.getHeaders(name);
			while (values.hasMoreElements()) {
				String value = (String) values.nextElement();
				headers.add(new BasicHeader(name, value));
			}
		}
		Header[] t = new Header[] {};
		return headers.toArray(t);
	}

	public HttpEntity getEntity() {
		return entity;
	}

}

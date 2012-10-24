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
import java.net.URI;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.esigate.api.HttpRequest;
import org.esigate.api.HttpSession;
import org.esigate.util.UriUtils;

public class HttpRequestImpl implements HttpRequest {

	private final HttpServletRequest parent;
	private final ServletContext servletContext;
	private final HttpParams params = new BasicHttpParams();

	private HttpRequestImpl(HttpServletRequest parent, ServletContext servletContext) {
		this.parent = parent;
		this.servletContext = servletContext;
	}

	public static HttpRequest wrap(HttpServletRequest parent, ServletContext servletContext) {
		return new HttpRequestImpl(parent, servletContext);

	}

	public String getParameter(String name) {
		return parent.getParameter(name);
	}

	public String getHeader(String name) {
		return parent.getHeader(name);
	}

	@SuppressWarnings("unchecked")
	public Collection<String> getHeaderNames() {
		return Collections.<String> list(parent.getHeaderNames());
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

	public String getMethod() {
		return parent.getMethod();
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

	public URI getUri() {
		return UriUtils.createURI(parent.getScheme(), parent.getServerName(), parent.getServerPort(), parent.getRequestURI(), parent.getQueryString(), null);
	}

	public InputStream getResourceAsStream(String path) {
		return servletContext.getResourceAsStream(path);
	}

	public HttpParams getParams() {
		return params;
	}

}

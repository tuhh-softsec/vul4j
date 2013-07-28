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

package org.esigate.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;
import org.esigate.api.ContainerRequestMediator;
import org.esigate.servlet.HttpServletMediator;
import org.esigate.util.HttpRequestHelper;
import org.esigate.util.UriUtils;

/**
 * MockMediator can be used in unit test, instead of {@link HttpServletMediator}.
 * @see  HttpServletMediator
 * 
 * @author Francois-Xavier Bonnet
 * @author Nicolas Richeton
 */
public class MockMediator implements ContainerRequestMediator {
	private final Map<String, Object> sessionAttributes = new HashMap<String, Object>();
	private final ArrayList<Cookie> cookies = new ArrayList<Cookie>();
	private String remoteUser;
	private HttpResponse httpResponse;
	private HttpEntityEnclosingRequest httpRequest;
	private String remoteAddr;

	public MockMediator(String uriString) {
		URI uri = UriUtils.createUri(uriString);
		String scheme = uri.getScheme();
		String host = uri.getHost();
		int port = uri.getPort();
		this.httpRequest = new BasicHttpEntityEnclosingRequest("GET", uriString);

		// Remove default ports
		if (port == -1 || (port == 80 && "http".equals(scheme)) || (port == 443 && "https".equals(scheme)))
			this.httpRequest.setHeader("Host", host);
		else
			this.httpRequest.setHeader("Host", host + ":" + port);

		HttpRequestHelper.setMediator(this.httpRequest, this);
	}

	public MockMediator() {
		this("http://localhost:8080");
	}

	@Override
	public Cookie[] getCookies() {
		Cookie[] cookiesArray = new Cookie[this.cookies.size()];
		return this.cookies.toArray(cookiesArray);
	}

	@Override
	public void addCookie(Cookie cookie) {
		this.cookies.add(cookie);
	}

	@Override
	public String getRemoteAddr() {
		return this.remoteAddr;
	}

	@Override
	public String getRemoteUser() {
		return this.remoteUser;
	}

	@Override
	public Principal getUserPrincipal() {
		throw new RuntimeException("Method not implemented");
	}

	@Override
	public void sendResponse(HttpResponse response) throws IOException {
		this.httpResponse = new BasicHttpResponse(response.getStatusLine());
		this.httpResponse.setHeaders(response.getAllHeaders());
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			ByteArrayEntity copiedEntity = new ByteArrayEntity(EntityUtils.toByteArray(entity), ContentType.get(entity));
			if (entity.getContentEncoding() != null)
				copiedEntity.setContentEncoding(entity.getContentEncoding());
			this.httpResponse.setEntity(copiedEntity);
		}
	}

	@Override
	public void setSessionAttribute(String key, Serializable value) {
		this.sessionAttributes.put(key, value);
	}

	@Override
	public Serializable getSessionAttribute(String key) {
		return (Serializable) this.sessionAttributes.get(key);
	}

	@Override
	public InputStream getResourceAsStream(String path) {
		throw new RuntimeException("Method not implemented");
	}

	@Override
	public HttpEntityEnclosingRequest getHttpRequest() {
		return this.httpRequest;
	}

	@Override
	public String getSessionId() {
		return null;
	}

	public HttpResponse getHttpResponse() {
		return this.httpResponse;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

}

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
import org.esigate.util.HttpRequestHelper;
import org.esigate.util.UriUtils;

/**
 * @author Francois-Xavier Bonnet
 * 
 */
public class MockMediator implements ContainerRequestMediator {
	private final HashMap<String, Object> attributes = new HashMap<String, Object>();
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
		httpRequest = new BasicHttpEntityEnclosingRequest("GET", uriString);
		if (port == -1 || (port == 80 && "http".equals(scheme)) || (port == 443 && "https".equals(scheme)))
			httpRequest.setHeader("Host", host);
		else
			httpRequest.setHeader("Host", host + ":" + port);
		HttpRequestHelper.setMediator(httpRequest, this);
	}

	public MockMediator() {
		this("http://localhost:8080");
	}

	@Override
	public Cookie[] getCookies() {
		Cookie[] cookiesArray = new Cookie[cookies.size()];
		return cookies.toArray(cookiesArray);
	}

	@Override
	public void addCookie(Cookie cookie) {
		cookies.add(cookie);
	}

	@Override
	public String getRemoteAddr() {
		return remoteAddr;
	}

	@Override
	public String getRemoteUser() {
		return remoteUser;
	}

	@Override
	public Principal getUserPrincipal() {
		throw new RuntimeException("Method not implemented");
	}

	@Override
	public void sendResponse(HttpResponse response) throws IOException {
		httpResponse = new BasicHttpResponse(response.getStatusLine());
		httpResponse.setHeaders(response.getAllHeaders());
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			ByteArrayEntity copiedEntity = new ByteArrayEntity(EntityUtils.toByteArray(entity), ContentType.get(entity));
			if (entity.getContentEncoding() != null)
				copiedEntity.setContentEncoding(entity.getContentEncoding());
			httpResponse.setEntity(copiedEntity);
		}
	}

	@Override
	public void setSessionAttribute(String key, Serializable value) {
		attributes.put(key, value);
	}

	@Override
	public Serializable getSessionAttribute(String key) {
		return (Serializable) attributes.get(key);
	}

	@Override
	public InputStream getResourceAsStream(String path) {
		throw new RuntimeException("Method not implemented");
	}

	@Override
	public HttpEntityEnclosingRequest getHttpRequest() {
		return httpRequest;
	}

	@Override
	public String getSessionId() {
		throw new RuntimeException("Method not implemented");
	}

	public HttpResponse getHttpResponse() {
		return httpResponse;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

}

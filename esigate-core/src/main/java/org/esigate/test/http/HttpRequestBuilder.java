package org.esigate.test.http;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.ProtocolVersion;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.esigate.api.ContainerRequestMediator;
import org.esigate.test.MockMediator;
import org.esigate.util.HttpRequestHelper;
import org.esigate.util.UriUtils;

/**
 * Fluent-style builder for HttpRequest.
 * 
 * <p>
 * Default request is
 * 
 * <pre>
 * GET http://localhost/ HTTP/1.1
 * </pre>
 * 
 * @author Nicolas Richeton
 * 
 */
public class HttpRequestBuilder {
	ProtocolVersion protocolVersion = new ProtocolVersion("HTTP", 1, 1);
	String uriString = "http://localhost/";
	List<Header> headers = new ArrayList<Header>();
	List<Cookie> cookies = new ArrayList<Cookie>();
	HttpEntity entity = null;
	private String method = "GET";

	boolean mockMediator = false;
	ContainerRequestMediator mediator = null;

	public HttpRequestBuilder uri(String uri) {
		this.uriString = uri;
		return this;
	}

	public HttpRequestBuilder header(String name, String value) {
		this.headers.add(new BasicHeader(name, value));
		return this;
	}

	public HttpRequestBuilder method(String paramMethod) {
		this.method = paramMethod;
		return this;
	}

	public HttpRequestBuilder protocolVersion(
			ProtocolVersion paramProtocolVersion) {
		this.protocolVersion = paramProtocolVersion;
		return this;
	}

	public HttpRequestBuilder cookie(String name, String value) {
		this.cookies.add(new BasicClientCookie(name, value));
		return this;
	}

	public HttpRequestBuilder entity(HttpEntity paramEntity) {
		this.entity = paramEntity;
		return this;
	}

	public HttpRequestBuilder mockMediator() {

		if (this.mediator != null)
			throw new IllegalArgumentException(
					"Cannot use both mockMediator and mediator when building HttpRequest");

		this.mockMediator = true;
		return this;
	}

	public HttpRequestBuilder mediator(ContainerRequestMediator paramMediator) {
		if (this.mockMediator)
			throw new IllegalArgumentException(
					"Cannot use both mockMediator and mediator when building HttpRequest");

		this.mediator = paramMediator;
		return this;
	}

	/**
	 * Build the request as defined in the current builder.
	 * 
	 * @return the request
	 */
	public HttpEntityEnclosingRequest build() {
		HttpEntityEnclosingRequest request = null;
		URI uri = UriUtils.createUri(this.uriString);
		String scheme = uri.getScheme();
		String host = uri.getHost();
		int port = uri.getPort();
		request = new BasicHttpEntityEnclosingRequest(this.method,
				this.uriString, this.protocolVersion);
		if (port == -1 || (port == 80 && "http".equals(scheme))
				|| (port == 443 && "https".equals(scheme)))
			request.setHeader("Host", host);
		else
			request.setHeader("Host", host + ":" + port);

		for (Header h : this.headers) {
			request.addHeader(h.getName(), h.getValue());
		}

		if (this.entity != null) {
			request.setEntity(this.entity);
		}

		ContainerRequestMediator requestMediator = null;
		if (this.mockMediator)
			requestMediator = new MockMediator(this.uriString);

		if (this.mediator != null)
			requestMediator = this.mediator;

		if (requestMediator != null) {
			for (Cookie c : this.cookies) {
				requestMediator.addCookie(c);
			}
			HttpRequestHelper.setMediator(request, requestMediator);
		}

		return request;
	}
}

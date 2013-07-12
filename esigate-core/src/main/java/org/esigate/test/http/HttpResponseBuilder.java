package org.esigate.test.http;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;

/**
 * Fluent-style builder for HttpResponse.
 * 
 * <p>
 * Default response is
 * 
 * <pre>
 * 200 OK  HTTP/1.1
 * </pre>
 * 
 * @author Nicolas Richeton
 * 
 */
public class HttpResponseBuilder {

	ProtocolVersion protocolVersion = new ProtocolVersion("HTTP", 1, 1);
	int status = HttpStatus.SC_OK;
	String reason = "Ok";
	List<Header> headers = new ArrayList<Header>();
	HttpEntity entity = null;

	public HttpResponseBuilder protocolVersion(
			ProtocolVersion paramProtocolVersion) {
		this.protocolVersion = paramProtocolVersion;
		return this;
	}

	public HttpResponseBuilder status(int paramStatus) {
		this.status = paramStatus;
		return this;
	}

	public HttpResponseBuilder reason(String paramReason) {
		this.reason = paramReason;
		return this;
	}

	public HttpResponseBuilder header(String name, String value) {
		this.headers.add(new BasicHeader(name, value));
		return this;
	}

	public HttpResponseBuilder entity(HttpEntity paramEntity) {
		this.entity = paramEntity;
		this.headers.add( this.entity.getContentType());
		return this;
	}

	public HttpResponseBuilder entity(String entityBody)
			throws UnsupportedEncodingException {
		this.entity = new StringEntity(entityBody);
		return this;
	}

	public HttpResponse build() {
		BasicHttpResponse response = new BasicHttpResponse(
				this.protocolVersion, this.status, this.reason);

		for (Header h : this.headers) {
			response.addHeader(h.getName(), h.getValue());
		}

		if (this.entity != null) {
			response.setEntity(this.entity);
		}
		return response;
	}
}

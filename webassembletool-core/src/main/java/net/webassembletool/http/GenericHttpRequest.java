package net.webassembletool.http;

import java.net.URI;

import org.apache.http.client.methods.HttpRequestBase;

/**
 * Generic class for all http requests without body (like GET or HEAD)
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class GenericHttpRequest extends HttpRequestBase {
	private final String method;

	public GenericHttpRequest(String method, String uri) {
		this.method = method;
		setURI(URI.create(uri));
	}

	@Override
	public String getMethod() {
		return method;
	}

}

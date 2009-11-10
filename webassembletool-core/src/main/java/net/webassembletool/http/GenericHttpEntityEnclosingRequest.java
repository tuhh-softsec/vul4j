package net.webassembletool.http;

import java.net.URI;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

/**
 * Generic class for all http requests containing a body (like POST)
 * 
 * @author Francois-Xavier Bonnet
 *
 */
public class GenericHttpEntityEnclosingRequest extends
		HttpEntityEnclosingRequestBase {
	private final String method;

	public GenericHttpEntityEnclosingRequest(String method, String uri) {
		this.method = method;
		setURI(URI.create(uri));
	}

	@Override
	public String getMethod() {
		return method;
	}

}

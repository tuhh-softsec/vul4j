package net.webassembletool.authentication;

import java.util.Properties;

import net.webassembletool.RequestContext;
import net.webassembletool.http.HttpClientRequest;
import net.webassembletool.http.HttpClientResponse;

/**
 * An AuthenticationHandler is called before any request and can modify it to
 * add authentication information such as request parameters or HTTP headers.
 * After the request has been executed, it can ask for a new request as many
 * times as needed. This can be used to implement challenge authentication
 * schemes.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public interface AuthenticationHandler {

	public void init(Properties properties);

	public void preRequest(HttpClientRequest request,
			RequestContext requestContext);

	public boolean needsNewRequest(HttpClientResponse response,
			RequestContext requestContext);

}

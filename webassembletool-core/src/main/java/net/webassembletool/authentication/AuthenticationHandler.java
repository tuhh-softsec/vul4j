package net.webassembletool.authentication;

import java.io.IOException;
import java.util.Properties;

import net.webassembletool.Renderer;
import net.webassembletool.ResourceContext;
import net.webassembletool.http.HttpClientRequest;
import net.webassembletool.http.HttpClientResponse;

/**
 * An AuthenticationHandler is called before any request and can modify it to
 * add authentication information such as request parameters or HTTP headers.
 * After the request has been executed, it can ask for a new request as many
 * times as needed. This can be used to implement challenge authentication
 * schemes.
 * 
 * There is only one instance of this class for a driver instance so it must be
 * thread-safe.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public interface AuthenticationHandler extends Renderer {

	public void init(Properties properties);

	/**
	 * Method called before proxying a request
	 * 
	 * This method can ask the users credentials by sending an authentication
	 * page or a 401 code or redirect to a login page. If so the method must
	 * return false in order to stop further processing.
	 * 
	 * @param requestContext
	 * @return true if the processing must continue, false if the response has
	 *         already been sent to the client.
	 */
	public boolean beforeProxy(ResourceContext requestContext)
			throws IOException;

	/**
	 * Method called before sending a request to the destination server.
	 * 
	 * This method can be used to add user credentials to the request
	 * 
	 * @param request
	 * @param requestContext
	 */
	public void preRequest(HttpClientRequest request,
			ResourceContext requestContext);

	/**
	 * Method called after the response has been obtained from the destination
	 * server.
	 * 
	 * This method can be used to ask for a new request if the destination
	 * server uses a challenge-based authentication mechanism with an arbitrary
	 * number of steps.
	 * 
	 * @param response
	 * @param requestContext
	 * @return true if a new request is needed
	 */
	public boolean needsNewRequest(HttpClientResponse response,
			ResourceContext requestContext);

}

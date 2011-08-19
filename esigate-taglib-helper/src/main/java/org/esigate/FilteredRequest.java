package org.esigate;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Wrapper to the response used inside the servlet filter to override some of
 * the methods of the request.
 * 
 * @author Francois-Xavier Bonnet
 */
public class FilteredRequest extends HttpServletRequestWrapper {
	public FilteredRequest(HttpServletRequest request) {
		super(request);
	}

	/**
	 * Returns the user defined as parameter "user" if present
	 */
	@Override
	public String getRemoteUser() {
		String user = getHeader("X_REMOTE_USER");
		if (user != null)
			return user;
		else
			return super.getRemoteUser();
	}

	@Override
	public Principal getUserPrincipal() {
		String user = getRemoteUser();
		if (user != null)
			return new Principal() {
				public String getName() {
					return getRemoteUser();
				}
			};
		else
			return null;
	}
}

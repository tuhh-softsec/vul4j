package net.webassembletool.authentication;

import net.webassembletool.RequestContext;
import net.webassembletool.UserContext;
import net.webassembletool.http.HttpClientRequest;
import net.webassembletool.http.HttpClientResponse;

/**
 * AuthenticationHandler implementation that retrieves the user passed by the
 * servlet container or set manually into the RequestContext and transmits it as
 * a HTTP header X_REMOTE_USER in all requests
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class RemoteUserAuthenticationHandler implements AuthenticationHandler {

	public boolean needsNewRequest(HttpClientResponse response,
			RequestContext requestContext) {
		return false;
	}

	public void preRequest(HttpClientRequest request,
			RequestContext requestContext) {
		UserContext userContext = requestContext.getUserContext();
		String remoteUser = null;
		if (userContext != null && userContext.getUser() != null)
			remoteUser = userContext.getUser();
		else if (requestContext.getOriginalRequest().getRemoteUser() != null)
			remoteUser = requestContext.getOriginalRequest().getRemoteUser();
		if (remoteUser != null)
			request.addHeader("X_REMOTE_USER", remoteUser);
	}
}

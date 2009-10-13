package net.webassembletool.authentication;

import net.webassembletool.RequestContext;
import net.webassembletool.UserContext;
import net.webassembletool.http.HttpClientRequest;
import net.webassembletool.http.HttpClientResponse;

public class RemoteUserAuthenticationHandler implements AuthenticationHandler {

	public boolean needsNewRequest(HttpClientResponse response,
			RequestContext requestContext) {
		return false;
	}

	public void preRequest(HttpClientRequest request,
			RequestContext requestContext) {
		UserContext userContext = requestContext.getUserContext();
		if (userContext != null && userContext.getUser() != null)
			request.addHeader("X_REMOTE_USER", userContext.getUser());
		else if (requestContext.getOriginalRequest().getRemoteUser() != null)
			request.addHeader("X_REMOTE_USER", requestContext
					.getOriginalRequest().getRemoteUser());
	}
}

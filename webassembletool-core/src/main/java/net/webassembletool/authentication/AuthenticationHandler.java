package net.webassembletool.authentication;

import net.webassembletool.RequestContext;
import net.webassembletool.http.HttpClientRequest;
import net.webassembletool.http.HttpClientResponse;

public interface AuthenticationHandler {
	public void preRequest(HttpClientRequest request,
			RequestContext requestContext);

	public boolean needsNewRequest(HttpClientResponse response,
			RequestContext requestContext);

}

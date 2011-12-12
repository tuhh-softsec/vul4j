package org.esigate.cas;

import java.security.Principal;
import java.util.Properties;

import org.esigate.ResourceContext;
import org.esigate.api.HttpRequest;
import org.esigate.authentication.AuthenticationHandler;
import org.esigate.http.HttpClientRequest;
import org.esigate.http.HttpClientResponse;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CasAuthenticationHandler implements AuthenticationHandler {
	private final static Logger LOG = LoggerFactory.getLogger(AuthenticationHandler.class);
	private final static String SECOND_REQUEST = "SECOND_REQUEST";
	private String loginUrl = "/login";

	private String addCasAuthentication(String location, ResourceContext requestContext) {
		Principal principal = requestContext.getOriginalRequest().getUserPrincipal();
		if (principal != null && principal instanceof AttributePrincipal) {
			AttributePrincipal casPrincipal = (AttributePrincipal) principal;
			LOG.debug("User logged in CAS as: " + casPrincipal.getName());
			String casProxyTicket = casPrincipal.getProxyTicketFor(location);
			LOG.debug("Proxy ticket retrieved: " + casPrincipal.getName() + " for service: " + location + " : " + casProxyTicket);
			if (casProxyTicket != null) {
				if (location.indexOf("?") > 0) {
					return location + "&ticket=" + casProxyTicket;
				} else {
					return location + "?ticket=" + casProxyTicket;
				}
			}
		}
		return location;
	}

	public boolean beforeProxy(ResourceContext requestContext) {
		return true;
	}

	public void init(Properties properties) {
		String casLoginUrl = properties.getProperty("casLoginUrl");
		if (casLoginUrl != null) {
			this.loginUrl = casLoginUrl;
		}
	}

	public boolean needsNewRequest(HttpClientResponse httpClientResponse, ResourceContext requestContext) {
		HttpRequest httpServletRequest = requestContext.getOriginalRequest();
		if (httpServletRequest.getAttribute(SECOND_REQUEST) != null) {
			// Calculating the URL we may have been redirected to, as
			// automatic redirect following is activated
			String currentLocation = httpClientResponse.getHeader("Location");
			if (currentLocation != null && currentLocation.contains(loginUrl)) {
				// If the user is authenticated we need a second request with
				// the proxy ticket
				Principal principal = requestContext.getOriginalRequest().getUserPrincipal();
				if (principal != null && principal instanceof AttributePrincipal) {
					return true;
				}
			}
		}
		return false;
	}

	public void preRequest(HttpClientRequest request, ResourceContext requestContext) {
		HttpRequest httpServletRequest = requestContext.getOriginalRequest();
		if (httpServletRequest.getAttribute(SECOND_REQUEST) != null) {
			request.setUri(addCasAuthentication(request.getUri(), requestContext));
		}
		httpServletRequest.setAttribute(SECOND_REQUEST, true);
	}

}

package net.webassembletool.cas;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import net.webassembletool.RequestContext;
import net.webassembletool.authentication.AuthenticationHandler;
import net.webassembletool.http.HttpClientRequest;
import net.webassembletool.http.HttpClientResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.cas.client.authentication.AttributePrincipal;

public class CasAuthenticationHandler implements AuthenticationHandler {
	private final static Log LOG = LogFactory
			.getLog(AuthenticationHandler.class);
	private final static String SECOND_REQUEST = "SECOND_REQUEST";
	private String loginUrl;

	public CasAuthenticationHandler() {
		this.loginUrl = "/login";
	}

	public CasAuthenticationHandler(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public boolean needsNewRequest(HttpClientResponse httpClientResponse,
			RequestContext requestContext) {
		HttpServletRequest httpServletRequest = requestContext
				.getOriginalRequest();
		if (httpServletRequest.getAttribute(SECOND_REQUEST) == null) {
			// Calculating the URL we may have been redirected to, as
			// automatic redirect following is activated
			String currentLocation = httpClientResponse.getCurrentLocation();
			if (currentLocation != null && currentLocation.contains(loginUrl)) {
				// If the user is authenticated we need a second request with
				// the proxy ticket
				Principal principal = requestContext.getOriginalRequest()
						.getUserPrincipal();
				if (principal != null
						&& principal instanceof AttributePrincipal)
					return true;
			}
		}
		return false;
	}

	public void preRequest(HttpClientRequest request,
			RequestContext requestContext) {
		HttpServletRequest httpServletRequest = requestContext
				.getOriginalRequest();
		if (httpServletRequest.getAttribute(SECOND_REQUEST) != null)
			request.setUri(addCasAuthentication(request.getUri(),
					requestContext));
		httpServletRequest.setAttribute(SECOND_REQUEST, true);
	}

	private String addCasAuthentication(String location,
			RequestContext requestContext) {
		Principal principal = requestContext.getOriginalRequest()
				.getUserPrincipal();
		if (principal != null && principal instanceof AttributePrincipal) {
			AttributePrincipal casPrincipal = (AttributePrincipal) principal;
			LOG.debug("User logged in CAS as: " + casPrincipal.getName());
			String service = location;
			service = service.substring(service.indexOf("service=")
					+ "service=".length());
			int ampersandPosition = service.indexOf('&');
			if (ampersandPosition > 0)
				service = service.substring(0, ampersandPosition);
			try {
				service = URLDecoder.decode(service, "ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				// Should not happen
			}
			String casProxyTicket = casPrincipal.getProxyTicketFor(service);
			LOG.debug("Proxy ticket retrieved: " + casPrincipal.getName()
					+ " for service: " + service + " : " + casProxyTicket);
			if (casProxyTicket != null)
				return service += "&ticket=" + casProxyTicket;
			return service;
		}
		return location;
	}
}

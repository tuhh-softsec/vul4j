package net.webassembletool.cas;

import java.security.Principal;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import net.webassembletool.ResourceContext;
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
	private String loginUrl = "/login";

	public boolean needsNewRequest(HttpClientResponse httpClientResponse,
			ResourceContext requestContext) {
		HttpServletRequest httpServletRequest = requestContext
				.getOriginalRequest();
		if (httpServletRequest.getAttribute(SECOND_REQUEST) != null) {
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
			ResourceContext requestContext) {
		HttpServletRequest httpServletRequest = requestContext
				.getOriginalRequest();
		if (httpServletRequest.getAttribute(SECOND_REQUEST) != null)
			request.setUri(addCasAuthentication(request.getUri(),
					requestContext));
		httpServletRequest.setAttribute(SECOND_REQUEST, true);
	}

	private String addCasAuthentication(String location,
			ResourceContext requestContext) {
		Principal principal = requestContext.getOriginalRequest()
				.getUserPrincipal();
		if (principal != null && principal instanceof AttributePrincipal) {
			AttributePrincipal casPrincipal = (AttributePrincipal) principal;
			LOG.debug("User logged in CAS as: " + casPrincipal.getName());
			String casProxyTicket = casPrincipal.getProxyTicketFor(location);
			LOG.debug("Proxy ticket retrieved: " + casPrincipal.getName()
					+ " for service: " + location + " : " + casProxyTicket);
			if (casProxyTicket != null) {
				if (location.indexOf("?") > 0)
					return location + "&ticket=" + casProxyTicket;
				else
					return location + "?ticket=" + casProxyTicket;
			}
		}
		return location;
	}

	public void init(Properties properties) {
		String casLoginUrl = properties.getProperty("casLoginUrl");
		if (casLoginUrl != null)
			this.loginUrl = casLoginUrl;
	}
}

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
	public static final String DEFAULT_LOGIN_URL = "/login";

	private final static Logger LOG = LoggerFactory.getLogger(AuthenticationHandler.class);

	// Configuration properties names
	private final static String LOGIN_URL_PROPERTY = "casLoginUrl";
	private final static String SECOND_REQUEST = "SECOND_REQUEST";
	private final static String SPRING_SECURITY_PROPERTY = "isSpringSecurity";

	private final static String SPRING_SECURITY_URL_PATTERN_PROPERTY = "springSecurityUrl";

	private String loginUrl;
	private boolean springSecurity;
	private String springSecurityUrl;

	private String addCasAuthentication(String location, ResourceContext requestContext) {
		String resultLocation = location;
		Principal principal = requestContext.getOriginalRequest().getUserPrincipal();
		if (principal != null && principal instanceof AttributePrincipal) {
			AttributePrincipal casPrincipal = (AttributePrincipal) principal;
			LOG.debug("User logged in CAS as: " + casPrincipal.getName());

			if (springSecurity) {
				String params = null;
				if (resultLocation.indexOf("?") != -1) {
					params = resultLocation.substring(resultLocation.indexOf("?"));
					LOG.debug("params: " + params.substring(1));
				}
				if (springSecurityUrl != null && !"".equals(springSecurityUrl)) {
					resultLocation = requestContext.getBaseURL() + springSecurityUrl + ((params != null) ? params : "");
					LOG.debug("getIsSpringSecurity=true => updated location: " + resultLocation);
				}
			}
			String casProxyTicket = casPrincipal.getProxyTicketFor(resultLocation);
			LOG.debug("Proxy ticket retrieved: " + casPrincipal.getName() + " for service: " + location + " : " + casProxyTicket);
			if (casProxyTicket != null) {
				if (resultLocation.indexOf("?") > 0) {
					return resultLocation + "&ticket=" + casProxyTicket;
				} else {
					return resultLocation + "?ticket=" + casProxyTicket;
				}
			}
		}

		return resultLocation;
	}

	public boolean beforeProxy(ResourceContext requestContext) {
		return true;
	}

	public void init(Properties properties) {
		loginUrl = properties.getProperty(LOGIN_URL_PROPERTY);
		if (loginUrl == null) {
			loginUrl = DEFAULT_LOGIN_URL;
		}
		String springSecurityString = properties.getProperty(SPRING_SECURITY_PROPERTY);
		if (springSecurityString != null) {
			springSecurity = Boolean.parseBoolean(springSecurityString);
		} else {
			springSecurity = false;
		}
		springSecurityUrl = properties.getProperty(SPRING_SECURITY_URL_PATTERN_PROPERTY);
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
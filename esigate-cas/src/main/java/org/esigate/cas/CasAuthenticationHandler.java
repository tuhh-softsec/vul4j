/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.esigate.cas;

import java.security.Principal;
import java.util.Properties;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.esigate.api.HttpRequest;
import org.esigate.authentication.AuthenticationHandler;
import org.esigate.http.GenericHttpRequest;
import org.esigate.util.HttpRequestHelper;
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

	private String addCasAuthentication(String location, HttpRequest request) {
		String resultLocation = location;
		Principal principal = request.getUserPrincipal();
		if (principal != null && principal instanceof AttributePrincipal) {
			AttributePrincipal casPrincipal = (AttributePrincipal) principal;
			LOG.debug("User logged in CAS as: " + casPrincipal.getName());
			String springRedirectParam = "";

			if (springSecurity) {
				String params = null;
				if (resultLocation.indexOf("?") != -1) {
					params = resultLocation.substring(resultLocation.indexOf("?"));
					LOG.debug("params: " + params.substring(1));
				}
				if (springSecurityUrl != null && !"".equals(springSecurityUrl)) {
					resultLocation = HttpRequestHelper.getBaseUrl(request) + springSecurityUrl + ((params != null) ? params : "");
					springRedirectParam = "&spring-security-redirect=" + location;
					LOG.debug("getIsSpringSecurity=true => updated location: " + resultLocation);
				}
			}
			String casProxyTicket = casPrincipal.getProxyTicketFor(resultLocation);
			LOG.debug("Proxy ticket retrieved: " + casPrincipal.getName() + " for service: " + location + " : " + casProxyTicket);
			if (casProxyTicket != null) {
				if (resultLocation.indexOf("?") > 0) {
					return resultLocation + "&ticket=" + casProxyTicket + springRedirectParam;
				} else {
					return resultLocation + "?ticket=" + casProxyTicket + springRedirectParam;
				}
			}
		}

		return resultLocation;
	}

	public boolean beforeProxy(HttpRequest request) {
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

	public boolean needsNewRequest(HttpResponse httpResponse, HttpRequest request) {
		if (request.getParams().getBooleanParameter(SECOND_REQUEST, false)) {
			// Calculating the URL we may have been redirected to, as
			// automatic redirect following is activated
			Header LocationHeader = httpResponse.getFirstHeader("Location");
			String currentLocation = null;
			if (LocationHeader != null) {
				currentLocation = LocationHeader.getValue();
			}
			if (currentLocation != null && currentLocation.contains(loginUrl)) {
				// If the user is authenticated we need a second request with
				// the proxy ticket
				Principal principal = request.getUserPrincipal();
				if (principal != null && principal instanceof AttributePrincipal) {
					return true;
				}
			}
		}
		return false;
	}

	public void preRequest(GenericHttpRequest request, HttpRequest httpRequest) {
		if (httpRequest.getParams().getBooleanParameter(SECOND_REQUEST, false)) {
			request.setUri(addCasAuthentication(request.getRequestLine().getUri(), httpRequest));
		}
		httpRequest.getParams().setBooleanParameter(SECOND_REQUEST, true);
	}
}
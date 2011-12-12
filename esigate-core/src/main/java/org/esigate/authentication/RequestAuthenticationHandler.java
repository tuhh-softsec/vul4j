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
package org.esigate.authentication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.esigate.ResourceContext;
import org.esigate.api.HttpSession;
import org.esigate.http.HttpClientRequest;
import org.esigate.http.HttpClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Forward session and/or request attributes as request headers.
 * 
 * @author Nicolas Richeton
 */
public class RequestAuthenticationHandler implements AuthenticationHandler {
	private final static Logger logger = LoggerFactory.getLogger(RequestAuthenticationHandler.class);
	private final List<String> sessionAttributes = new ArrayList<String>();
	private final List<String> requestAttributes = new ArrayList<String>();
	private String headerPrefix = "X-ATTR-";

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.esigate.authentication.AuthenticationHandler#beforeProxy(org.esigate.ResourceContext)
	 */
	public boolean beforeProxy(ResourceContext requestContext) throws IOException {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.esigate.authentication.AuthenticationHandler#init(java.util.Properties)
	 */
	public void init(Properties properties) {
		// Attributes for session
		String sessionAttributesProperty = properties.getProperty("forwardSessionAttributes");
		if (sessionAttributesProperty != null) {
			String attributes[] = sessionAttributesProperty.split(",");
			for (String attribute : attributes) {
				sessionAttributes.add(attribute.trim());
				if (logger.isInfoEnabled()) {
					logger.info("Forwading session attribute: " + attribute);
				}
			}
		}

		// Attributes for request
		String requestAttributesProperty = (String) properties
				.get("forwardRequestAttributes");
		if (requestAttributesProperty != null) {
			String attributes[] = requestAttributesProperty.split(",");
			for (String attribute : attributes) {
				requestAttributes.add(attribute.trim());
				if (logger.isInfoEnabled()) {
					logger.info("Forwading request attribute: " + attribute);
				}
			}
		}

		// Prefix name
		String headerPrefixProperty = (String) properties.get("headerPrefix");
		if (headerPrefixProperty != null) {
			headerPrefix = headerPrefixProperty;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.esigate.authentication.AuthenticationHandler#needsNewRequest(org.esigate.http.HttpClientResponse,
	 *      org.esigate.ResourceContext)
	 */

	public boolean needsNewRequest(HttpClientResponse response,
			ResourceContext requestContext) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.esigate.authentication.AuthenticationHandler#preRequest(org.esigate.http.HttpClientRequest,
	 *      org.esigate.ResourceContext)
	 */

	public void preRequest(HttpClientRequest request,
			ResourceContext requestContext) {
		if (logger.isDebugEnabled()) {
			logger.debug("preRequest");
		}
		// Process session
		HttpSession session = requestContext.getOriginalRequest().getSession(false);

		if (session != null) {
			for (String attribute : sessionAttributes) {
				String value = (String) session.getAttribute(attribute);
				if (value != null) {
					if (logger.isDebugEnabled()) {
						logger.debug("Adding session attribute " + attribute
								+ " (" + value + ") as header (" + headerPrefix
								+ attribute + ")");
					}
					request.addHeader(headerPrefix + attribute, value);
				}
			}
		}

		// Process request
		for (String attribute : requestAttributes) {
			String value = (String) requestContext.getOriginalRequest()
					.getAttribute(attribute);
			if (value != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("Adding request attribute " + attribute + " ("
							+ value + ") as header (" + headerPrefix
							+ attribute + ")");
				}
				request.addHeader(headerPrefix + attribute, value);
			}
		}
	}

}

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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.esigate.api.ContainerRequestMediator;
import org.esigate.http.GenericHttpRequest;
import org.esigate.util.HttpRequestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Forward session and/or request attributes as HTTP request headers. Best when
 * ESIGate is used in embedded mode.
 * <p>
 * Example : Forward the "username" session attribute as "X-ATTR-username" in
 * the request fetching remote content.
 * <p>
 * Configuration uses the following attributes from driver.properties :
 * <p>
 * <ul>
 * <li>
 * <b>forwardSessionAttributes</b>: comma separated list of session attributes
 * which will be forwarded.</li>
 * <li>
 * <b>forwardRequestAttributes</b>: comma separated list of request attributes
 * which will be forwarded.</li>
 * <li><b>headerPrefix</b> : header prefix. Default is "X-ATTR-"</li>
 * </ul>
 * <p>
 * 
 * 
 * 
 * @author Nicolas Richeton
 */
public class RequestAuthenticationHandler extends GenericAuthentificationHandler {
	private final static Logger logger = LoggerFactory.getLogger(RequestAuthenticationHandler.class);
	private final List<String> sessionAttributes = new ArrayList<String>();
	private final List<String> requestAttributes = new ArrayList<String>();
	private String headerPrefix = "X-ATTR-";

	@Override
	public boolean beforeProxy(HttpRequest httpRequest) {
		return true;
	}

	@Override
	public void init(Properties properties) {
		// Attributes for session
		String sessionAttributesProperty = properties.getProperty("forwardSessionAttributes");
		if (sessionAttributesProperty != null) {
			String attributes[] = sessionAttributesProperty.split(",");
			for (String attribute : attributes) {
				this.sessionAttributes.add(attribute.trim());
				if (logger.isInfoEnabled()) {
					logger.info("Forwading session attribute: " + attribute);
				}
			}
		}

		// Attributes for request
		String requestAttributesProperty = (String) properties.get("forwardRequestAttributes");
		if (requestAttributesProperty != null) {
			String attributes[] = requestAttributesProperty.split(",");
			for (String attribute : attributes) {
				this.requestAttributes.add(attribute.trim());
				if (logger.isInfoEnabled()) {
					logger.info("Forwading request attribute: " + attribute);
				}
			}
		}

		// Prefix name
		String headerPrefixProperty = (String) properties.get("headerPrefix");
		if (headerPrefixProperty != null) {
			this.headerPrefix = headerPrefixProperty;
		}
	}

	@Override
	public boolean needsNewRequest(HttpResponse response, HttpRequest httpRequest) {
		return false;
	}

	@Override
	public void preRequest(GenericHttpRequest request, HttpRequest httpRequest) {
		logger.debug("preRequest");

		// Process session
		ContainerRequestMediator mediator = HttpRequestHelper.getMediator(httpRequest);
		for (String attribute : this.sessionAttributes) {
			String value = (String) mediator.getSessionAttribute(attribute);
			if (value != null) {
				logger.debug("Adding session attribute {} ({}) as header ({}{})", attribute, value, this.headerPrefix,
						attribute);
				request.addHeader(this.headerPrefix + attribute, value);
			}
		}

		// Process request
		for (String attribute : this.requestAttributes) {
			String value = (String) httpRequest.getParams().getParameter(attribute);
			if (value != null) {
				logger.debug("Adding request attribute {} ({}) as header ({}{})", attribute, value, this.headerPrefix,
						attribute);
				request.addHeader(this.headerPrefix + attribute, value);
			}
		}
	}

}

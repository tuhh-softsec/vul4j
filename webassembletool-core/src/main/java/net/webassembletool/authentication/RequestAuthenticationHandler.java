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
package net.webassembletool.authentication;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Properties;

import javax.servlet.http.HttpSession;

import net.webassembletool.HttpErrorPage;
import net.webassembletool.ResourceContext;
import net.webassembletool.http.HttpClientRequest;
import net.webassembletool.http.HttpClientResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Forward session and/or request attributes as request headers.
 * 
 * @author Nicolas Richeton
 */
public class RequestAuthenticationHandler implements AuthenticationHandler {
	private String headerPrefix = "X-ATTR-";
	private static Log logger = LogFactory
			.getLog(RequestAuthenticationHandler.class);
	private final ArrayList<String> sessionAttributes = new ArrayList<String>();
	private final ArrayList<String> requestAttributes = new ArrayList<String>();

	/**
	 * {@inheritDoc}
	 * 
	 * @see net.webassembletool.authentication.AuthenticationHandler#beforeProxy(net.webassembletool.ResourceContext)
	 */
	public boolean beforeProxy(ResourceContext requestContext)
			throws IOException {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see net.webassembletool.authentication.AuthenticationHandler#init(java.util.Properties)
	 */
	public void init(Properties properties) {
		// Attributes for session
		String sessionAttributesProperty = (String) properties
				.get("forwardSessionAttributes");
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
	 * @see net.webassembletool.authentication.AuthenticationHandler#needsNewRequest(net.webassembletool.http.HttpClientResponse,
	 *      net.webassembletool.ResourceContext)
	 */

	public boolean needsNewRequest(HttpClientResponse response,
			ResourceContext requestContext) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see net.webassembletool.authentication.AuthenticationHandler#preRequest(net.webassembletool.http.HttpClientRequest,
	 *      net.webassembletool.ResourceContext)
	 */

	public void preRequest(HttpClientRequest request,
			ResourceContext requestContext) {
		if (logger.isDebugEnabled()) {
			logger.debug("preRequest");
		}
		// Process session
		HttpSession session = requestContext.getOriginalRequest().getSession(
				false);

		if (session != null && sessionAttributes != null) {
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

	/**
	 * {@inheritDoc}
	 * 
	 * @see net.webassembletool.Renderer#render(net.webassembletool.ResourceContext,
	 *      java.lang.String, java.io.Writer)
	 */
	public void render(ResourceContext requestContext, String src, Writer out)
			throws IOException, HttpErrorPage {
		out.write(src);
	}

}

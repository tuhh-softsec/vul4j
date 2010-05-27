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
 * Forward session attributes as request headers.
 * 
 * @author Nicolas Richeton
 */
public class SessionAuthenticationHandler implements AuthenticationHandler {
	private String headerPrefix = "X-ATTR-";
	private static Log logger = LogFactory
			.getLog(SessionAuthenticationHandler.class);
	private final ArrayList<String> sessionAttributes = new ArrayList<String>();

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
		// Attributes
		String attributesProperty = (String) properties
				.get("forwardSessionAttributes");
		if (attributesProperty != null) {
			String attributes[] = attributesProperty.split(",");
			for (String attribute : attributes) {
				sessionAttributes.add(attribute.trim());
				if (logger.isInfoEnabled()) {
					logger.info("Forwading session attribute: " + attribute);
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
		HttpSession session = requestContext.getOriginalRequest().getSession(
				false);

		if (session != null) {

			for (String attribute : sessionAttributes) {
				String value = (String) session.getAttribute(attribute);
				if (value != null) {
					if (logger.isDebugEnabled()) {
						logger.debug("Adding attribute " + attribute + " ("
								+ value + ") as header (" + headerPrefix
								+ attribute + ")");
					}
					request.addHeader(headerPrefix + attribute, value);
				}
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

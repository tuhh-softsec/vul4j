/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package net.webassembletool.filter;

import java.util.ArrayList;
import java.util.Properties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import net.webassembletool.ConfigurationException;
import net.webassembletool.ResourceContext;
import net.webassembletool.extension.ExtensionFactory;
import net.webassembletool.http.HttpClientRequest;
import net.webassembletool.http.HttpClientResponse;
import net.webassembletool.output.Output;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Cookie forwarding Filter.
 * 
 * @author Nicolas Richeton
 * 
 */
public class CookieForwardingFilter implements Filter {
	private static final String HEADER_COOKIE = "Cookie";
	private static final String HEADER_SET_COOKIE = "Set-cookie";
	private static Log logger = LogFactory.getLog(ExtensionFactory.class);
	public static final String PROP_ATTRIBUTE = "forwardCookies";
	private final ArrayList<String> forwardCookies = new ArrayList<String>();

	/**
	 * Get configured cookies.
	 * 
	 * @return list of names.
	 */
	public ArrayList<String> getForwardCookies() {
		return forwardCookies;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see net.webassembletool.extension.Extension#init(java.util.Properties)
	 */
	public void init(Properties properties) {
		// Cookies to forward
		String cookiesProperty = (String) properties.get(PROP_ATTRIBUTE);
		if (cookiesProperty != null) {
			String attributes[] = cookiesProperty.split(",");
			for (String cookieName : attributes) {
				forwardCookies.add(cookieName.trim());
				if (logger.isInfoEnabled()) {
					logger.info("Forwading cookie: " + cookieName);
				}
			}
		} else {
			throw new ConfigurationException("drivername." + PROP_ATTRIBUTE
					+ " is empty : no cookie to forward.");
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see net.webassembletool.filter.Filter#postRequest(net.webassembletool.http.HttpClientResponse,
	 *      net.webassembletool.output.Output)
	 */
	public void postRequest(HttpClientResponse httpClientResponse,
			Output output, ResourceContext resourceContext) {

		HttpServletResponse response = resourceContext.getOriginalResponse();
		if (response == null) {
			return;
		}

		String[] cookies = httpClientResponse.getHeaders(HEADER_SET_COOKIE);
		if (cookies != null) {
			for (String v : cookies) {
				for (String forwardCookie : forwardCookies) {
					if (v.startsWith(forwardCookie + "=")) {

						resourceContext.getOriginalResponse().addHeader(
								HEADER_SET_COOKIE, v);
					}
				}
			}
		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see net.webassembletool.filter.Filter#preRequest(net.webassembletool
	 *      .http.HttpClientRequest, net.webassembletool.ResourceContext)
	 */
	public void preRequest(HttpClientRequest httpClientRequest,
			ResourceContext resourceContext) {
		if (logger.isDebugEnabled()) {
			logger.debug("preRequest");
		}

		ArrayList<Cookie> toForward = null;

		// Select the cookie to forward
		Cookie[] cookies = resourceContext.getOriginalRequest().getCookies();
		if (cookies != null) {
			for (Cookie c : cookies) {
				for (String forwardCookie : forwardCookies) {
					if (forwardCookie.equals(c.getName())) {
						if (toForward == null) {
							toForward = new ArrayList<Cookie>();
						}
						toForward.add(c);
					}
				}
			}
		}

		// Create and add the headers.
		if (toForward != null) {
			StringBuilder headerValue = new StringBuilder();
			boolean first = true;
			for (Cookie c : toForward) {
				// Handle separator.
				if (first) {
					first = false;
				} else {
					headerValue.append("; ");
				}

				headerValue.append(c.getName());
				headerValue.append("=");
				headerValue.append(c.getValue());
			}

			httpClientRequest.addHeader(HEADER_COOKIE, headerValue.toString());
		}

	}
}

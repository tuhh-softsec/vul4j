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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import net.webassembletool.ConfigurationException;
import net.webassembletool.ResourceContext;
import net.webassembletool.extension.ExtensionFactory;
import net.webassembletool.http.HttpClientRequest;
import net.webassembletool.http.HttpClientResponse;
import net.webassembletool.http.HttpHeaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cookie forwarding Filter.
 * 
 * @author Nicolas Richeton
 * 
 */
public class CookieForwardingFilter implements Filter {
	private static final Logger logger = LoggerFactory.getLogger(ExtensionFactory.class);
	static final String PROP_ATTRIBUTE = "forwardCookies";
	private final Collection<String> forwardCookies = new HashSet<String>();

	/**
	 * Get configured cookies.
	 * 
	 * @return list of names.
	 */
	Collection<String> getForwardCookies() {
		return forwardCookies;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see net.webassembletool.extension.Extension#init(java.util.Properties)
	 */
	public void init(Properties properties) {
		// Cookies to forward
		String cookiesProperty = properties.getProperty(PROP_ATTRIBUTE);
		if (cookiesProperty != null) {
			String attributes[] = cookiesProperty.split(",");
			for (String cookieName : attributes) {
				forwardCookies.add(cookieName.trim());
				if (logger.isInfoEnabled()) {
					logger.info("Forwading cookie: " + cookieName);
				}
			}
		} else {
			throw new ConfigurationException("drivername." + PROP_ATTRIBUTE + " is empty : no cookie to forward.");
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see net.webassembletool.filter.Filter#postRequest(net.webassembletool.http.HttpClientResponse,
	 *      net.webassembletool.output.Output)
	 */
	public void postRequest(HttpClientResponse response, ResourceContext context) {
		HttpServletResponse dest = context.getOriginalResponse();
		if (dest == null) {
			return;
		}

		String[] cookies = response.getHeaders(HttpHeaders.SET_COOKIE);
		if (cookies != null && cookies.length != 0) {
			for (String cookie : cookies) {
				int idx = cookie.indexOf('=');
				if (idx != -1 && forwardCookies.contains(cookie.substring(0, idx))) {
					dest.addHeader(HttpHeaders.SET_COOKIE, cookie);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see net.webassembletool.filter.Filter#preRequest(net.webassembletool.http.HttpClientRequest,
	 *      net.webassembletool.ResourceContext)
	 */
	public void preRequest(HttpClientRequest request, ResourceContext context) {
		if (logger.isDebugEnabled()) {
			logger.debug("preRequest");
		}

		List<Cookie> toForward = new ArrayList<Cookie>();

		// Select the cookie to forward
		Cookie[] cookies = context.getOriginalRequest().getCookies();
		if (cookies != null && cookies.length != 0) {
			for (Cookie cookie : cookies) {
				if (forwardCookies.contains(cookie.getName())) {
					toForward.add(cookie);
				}
			}
		}

		// Create and add the headers.
		if (!toForward.isEmpty()) {
			StringBuilder headerValue = new StringBuilder();
			for (Iterator<Cookie> iter = toForward.iterator(); iter.hasNext();) {
				Cookie cookie = iter.next();
				headerValue.append(cookie.getName());
				headerValue.append('=');
				headerValue.append(cookie.getValue());
				if (iter.hasNext()) {
					headerValue.append("; ");
				}
			}
			request.addHeader(HttpHeaders.COOKIE, headerValue.toString());
		}

	}
}

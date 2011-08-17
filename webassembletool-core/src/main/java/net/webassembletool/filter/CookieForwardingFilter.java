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

import java.util.Collection;
import java.util.Properties;

import net.webassembletool.ConfigurationException;
import net.webassembletool.ResourceContext;
import net.webassembletool.cookie.RequestCookieStore;
import net.webassembletool.extension.ExtensionFactory;
import net.webassembletool.http.HttpClientRequest;
import net.webassembletool.http.HttpClientResponse;
import net.webassembletool.util.PropertiesUtil;

import org.apache.http.client.CookieStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cookie forwarding Filter.
 * 
 * @author Nicolas Richeton
 * 
 */
public class CookieForwardingFilter implements Filter {
	private static final Logger logger = LoggerFactory
			.getLogger(ExtensionFactory.class);
	private Collection<String> discardCookies;
	private Collection<String> forwardCookies;

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
		forwardCookies = PropertiesUtil.parseListedProperty(properties,
				"forwardCookies");
		// Cookies to discard
		discardCookies = PropertiesUtil.parseListedProperty(properties,
				"discardCookies");
		if (forwardCookies.contains("*") && forwardCookies.size() > 1) {
			throw new ConfigurationException(
					"forwardCookies must be a liste of cookie names OR *");
		}
		if (discardCookies.contains("*") && discardCookies.size() > 1) {
			throw new ConfigurationException(
					"discardCookies must be a liste of cookie names OR *");
		}
		if (forwardCookies.contains("*") && discardCookies.contains("*")) {
			throw new ConfigurationException(
					"cannot use * for forwardCookies AND discardCookies at the same time");
		}

	}

	/**
	 * Retrieves the wrapper to the CookieStore and forwards th
	 * 
	 * @see net.webassembletool.filter.Filter#postRequest(HttpClientResponse,
	 *      ResourceContext)
	 */
	public void postRequest(HttpClientResponse response, ResourceContext context) {
		// Nothing to do
	}

	/**
	 * Replace actual cookieStore by a wrapper to filter some cookies and
	 * forward others
	 * 
	 * @see net.webassembletool.filter.Filter#preRequest(net.webassembletool.http.HttpClientRequest,
	 *      net.webassembletool.ResourceContext)
	 */
	public void preRequest(HttpClientRequest request, ResourceContext context) {
		if (logger.isDebugEnabled()) {
			logger.debug("preRequest");
		}
		CookieStore cookieStore = context.getUserContext().getCookieStore();
		CookieStore wrappedCookieStore = new RequestCookieStore(discardCookies,
				forwardCookies, cookieStore, request, context);
		request.setCookieStore(wrappedCookieStore);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see net.webassembletool.filter.Filter#needUserContext()
	 */
	public boolean needUserContext() {
		return true;
	}
}

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

package org.esigate.http;

import java.util.Collection;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.protocol.HttpContext;
import org.esigate.ConfigurationException;
import org.esigate.Parameters;
import org.esigate.ResourceContext;
import org.esigate.extension.ExtensionFactory;
import org.esigate.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cookie forwarding Filter.
 * 
 * @author Nicolas Richeton
 * 
 */
public class CookieForwardingFilter implements Filter {
	private static final Logger LOG = LoggerFactory.getLogger(ExtensionFactory.class);
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
	 * @see org.esigate.extension.Extension#init(java.util.Properties)
	 */
	public void init(Properties properties) {
		// Cookies to forward
		forwardCookies = Parameters.FORWARD_COOKIES.getValueList(properties);
		// Cookies to discard
		discardCookies = Parameters.DISCARD_COOKIES.getValueList(properties);
		if (forwardCookies.contains("*") && forwardCookies.size() > 1) {
			throw new ConfigurationException("forwardCookies must be a list of cookie names OR *");
		}
		if (discardCookies.contains("*") && discardCookies.size() > 1) {
			throw new ConfigurationException("discardCookies must be a list of cookie names OR *");
		}
		if (forwardCookies.contains("*") && discardCookies.contains("*")) {
			throw new ConfigurationException("cannot use * for forwardCookies AND discardCookies at the same time");
		}
	}

	/**
	 * Retrieves the wrapper to the CookieStore and forwards th
	 * 
	 */
	public void postRequest(GenericHttpRequest request, HttpResponse response, HttpContext context, ResourceContext resourceContext) {
		// Nothing to do
	}

	/**
	 * Replace actual cookieStore by a wrapper to filter some cookies and forward others
	 * 
	 */
	public void preRequest(GenericHttpRequest request, HttpContext httpContext, ResourceContext context) {
		LOG.debug("preRequest");
		CookieStore cookieStore = CookieAdapter.convertCookieStore(context.getUserContext().getCookieStore());
		CookieStore wrappedCookieStore = new RequestCookieStore(discardCookies, forwardCookies, cookieStore, context);
		httpContext.setAttribute(ClientContext.COOKIE_STORE, wrappedCookieStore);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.esigate.filter.Filter#needUserContext()
	 */
	public boolean needUserContext() {
		return true;
	}
}

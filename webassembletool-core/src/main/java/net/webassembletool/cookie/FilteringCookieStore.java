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
package net.webassembletool.cookie;

import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;

import org.apache.http.cookie.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Prevent specified cookies from being stored in the cookie store.
 * 
 * @author Nicolas Richeton
 * 
 */
public class FilteringCookieStore extends SerializableBasicCookieStore {
	/** Serialization ID. */
	private static final long serialVersionUID = -2112501012354521287L;
	private static final Logger logger = LoggerFactory.getLogger(FilteringCookieStore.class);

	private final Collection<String> forwardCookies = new HashSet<String>();

	/**
	 * {@inheritDoc}
	 * 
	 * @see net.webassembletool.cookie.SerializableBasicCookieStore#addCookie(org.apache.http.cookie.Cookie)
	 */
	@Override
	public void addCookie(Cookie cookie) {
		if (!forwardCookies.contains(cookie.getName())) {
			super.addCookie(cookie);
		} else {
			if (logger.isInfoEnabled()) {
				logger.info("Not storing " + cookie.getName());
			}
		}
	}

	/**
	 * Get names of the cookies which are forwarded.
	 * 
	 * @return cookie name list.
	 */
	Collection<String> getForwardCookies() {
		return forwardCookies;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see net.webassembletool.cookie.SerializableBasicCookieStore#init(java.util.Properties)
	 */
	@Override
	public void init(Properties properties) {
		// Parent init.
		super.init(properties);

		// Cookies to discard
		String cookiesProperty = properties.getProperty("discardCookies");
		if (cookiesProperty != null) {
			String attributes[] = cookiesProperty.split(",");
			for (String cookieName : attributes) {
				forwardCookies.add(cookieName.trim());
				if (logger.isInfoEnabled()) {
					logger.info("Discarding cookie: " + cookieName);
				}
			}
		}
	}
}

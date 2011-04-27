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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
	private static final Logger logger = LoggerFactory
			.getLogger(FilteringCookieStore.class);

	private final Collection<String> discardCookies = new HashSet<String>();

	/**
	 * Get names of the cookies which are forwarded.
	 * 
	 * @return cookie name list.
	 */
	Collection<String> getForwardCookies() {
		return discardCookies;
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
				discardCookies.add(cookieName.trim());
				if (logger.isInfoEnabled()) {
					logger.info("Discarding cookie: " + cookieName);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see net.webassembletool.cookie.SerializableBasicCookieStore#cleanUpCookies()
	 */
	@Override
	public void cleanUpCookies() {
		List<Cookie> cookies = getCookies();
		for (Cookie cookie : cookies) {
			if (discardCookies.contains(cookie.getName())) {
				if (logger.isInfoEnabled()) {
					logger.info("Removing cookie: " + cookie.getName());
				}
				SerializableBasicClientCookie2 newCookie = new SerializableBasicClientCookie2(
						cookie);
				newCookie.setExpiryDate(new Date(0));
				super.addCookie(newCookie);
			}
		}
	}
}

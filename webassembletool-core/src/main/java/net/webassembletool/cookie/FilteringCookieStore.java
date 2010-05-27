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

import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.cookie.Cookie;

/**
 * Prevent specified cookies from being stored in the cookie store.
 * 
 * @author Nicolas Richeton
 * 
 */
public class FilteringCookieStore extends SerializableBasicCookieStore {
	private static Log logger = LogFactory.getLog(FilteringCookieStore.class);
	/**
	 * Serialization ID.
	 */
	private static final long serialVersionUID = -2112501012354521287L;

	private final ArrayList<String> forwardCookies = new ArrayList<String>();

	/**
	 * {@inheritDoc}
	 * 
	 * @see net.webassembletool.cookie.SerializableBasicCookieStore#addCookie(org.apache.http.cookie.Cookie)
	 */
	@Override
	public void addCookie(Cookie cookie) {

		boolean store = true;
		for (String cookieName : forwardCookies) {
			if (cookieName.equals(cookie.getName())) {
				if (logger.isInfoEnabled()) {
					logger.info("Not storing " + cookie.getName());
				}
				store = false;
				break;
			}
		}

		if (store) {
			super.addCookie(cookie);
		}
	}

	/**
	 * Get names of the cookies which are forwarded.
	 * 
	 * @return cookie name list.
	 */
	public ArrayList<String> getForwardCookies() {
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
		String cookiesProperty = (String) properties.get("discardCookies");
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

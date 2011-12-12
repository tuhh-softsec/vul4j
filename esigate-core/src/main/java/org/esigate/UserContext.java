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
package org.esigate;

import java.io.Serializable;

import org.esigate.api.Cookie;
import org.esigate.cookie.CustomCookieStore;

/**
 * User context that can be used in the master application to define the user
 * id. This context will be transmitted to the provider applications.<br />
 * There is one instance of user context associated with each session.
 * 
 * @author Francois-Xavier Bonnet
 * @contributor Nicolas Richeton
 * 
 */
public class UserContext implements Serializable {
	private static final long serialVersionUID = 1L;
	private String user;
	private final CustomCookieStore cookieStore;

	public UserContext(CustomCookieStore cookieStore) {
		if (cookieStore == null) {
			throw new IllegalArgumentException(
					"cookieStore implementation not set");
		}

		this.cookieStore = cookieStore;
	}

	/** Looks up for <code>jsessionid</code> cookie and returns its value. Returns <code>null</code> otherwise. */
	public String getSessionId() {
		String result = null;
		for (Cookie cookie : cookieStore.getCookies()) {
			if ("jsessionid".equalsIgnoreCase(cookie.getName())) {
				result = cookie.getValue();
				break;
			}
		}
		return result;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder()
			.append("User=")
			.append(user)
			.append(" Cookies={\n");
		for (Cookie cookie : cookieStore.getCookies()) {
			result.append('\t');
			if (cookie.isSecure()) {
				result.append("https");
			} else {
				result.append("http");
			}
			result.append("://")
				.append(cookie.getDomain())
				.append(cookie.getPath())
				.append('#')
				.append(cookie.getName())
				.append('=')
				.append(cookie.getValue())
				.append('\n');
		}
		return result.append('}').toString();
	}

	public CustomCookieStore getCookieStore() {
		return cookieStore;
	}

	public boolean isEmpty() {
		return user == null && cookieStore.getCookies().isEmpty();
	}
}

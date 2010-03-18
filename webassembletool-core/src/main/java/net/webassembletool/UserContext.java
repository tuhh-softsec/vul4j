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
package net.webassembletool;

import java.io.Serializable;

import net.webassembletool.http.SerializableBasicCookieStore;
import net.webassembletool.http.SerializableBasicHttpContext;

import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.protocol.HttpContext;

/**
 * User context that can be used in the master application to define the user
 * id. This context will be transmitted to the provider applications.<br />
 * There is one instance of user context associated with each session.
 * 
 * @author Francois-Xavier Bonnet
 * @contributor Nicolas Richeton
 * 
 */
public class UserContext implements Serializable{
	private String user;
	private HttpContext httpContext;
	public HttpContext getHttpContext() {
		return httpContext;
	}

	private CookieStore cookieStore;

	public UserContext() {
		// Create a local instance of cookie store
		cookieStore = new SerializableBasicCookieStore();
		// Create local HTTP context
		httpContext = new SerializableBasicHttpContext();
		// Bind custom cookie store to the local context
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	}

	public CookieStore getCookieStore() {
		return cookieStore;
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
		StringBuilder result = new StringBuilder();
		result.append("User=");
		result.append(user);
		result.append(" Cookies={\n");
		for (Cookie cookie : cookieStore.getCookies()) {
			result.append("\t");
			if (cookie.isSecure())
				result.append("https");
			else
				result.append("http");
			result.append("://");
			result.append(cookie.getDomain());
			result.append(cookie.getPath());
			result.append("#");
			result.append(cookie.getName());
			result.append("=");
			result.append(cookie.getValue());
			result.append("\n");
		}
		result.append("}");
		return result.toString();
	}
}

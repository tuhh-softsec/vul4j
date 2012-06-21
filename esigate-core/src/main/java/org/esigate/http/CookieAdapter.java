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

package org.esigate.http;

import org.apache.http.impl.cookie.BasicClientCookie2;
import org.esigate.api.Cookie;
import org.esigate.cookie.BasicClientCookie;

public class CookieAdapter {

	public static org.apache.http.cookie.Cookie toApacheCookie(Cookie cookie) {
		BasicClientCookie2 newCookie = new BasicClientCookie2(cookie.getName(), cookie.getValue());
		newCookie.setComment(cookie.getComment());
		newCookie.setCommentURL(cookie.getCommentURL());
		newCookie.setDomain(cookie.getDomain());
		newCookie.setExpiryDate(cookie.getExpiryDate());
		newCookie.setPath(cookie.getPath());
		newCookie.setPorts(cookie.getPorts());
		newCookie.setSecure(cookie.isSecure());
		newCookie.setVersion(cookie.getVersion());

		return newCookie;
	}

	public static Cookie toCustomCookie(org.apache.http.cookie.Cookie cookie) {
		BasicClientCookie newCookie = new BasicClientCookie(cookie.getName(), cookie.getValue());
		newCookie.setComment(cookie.getComment());
		newCookie.setCommentURL(cookie.getCommentURL());
		newCookie.setDomain(cookie.getDomain());
		newCookie.setExpiryDate(cookie.getExpiryDate());
		newCookie.setPath(cookie.getPath());
		newCookie.setPorts(cookie.getPorts());
		newCookie.setSecure(cookie.isSecure());
		newCookie.setVersion(cookie.getVersion());

		return newCookie;
	}
}

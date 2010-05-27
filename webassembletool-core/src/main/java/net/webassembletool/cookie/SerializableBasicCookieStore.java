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

import java.util.Properties;


import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;

/**
 * Serializable implementation of {@link CookieStore}
 * 
 * @author Nicolas Richeton
 */
public class SerializableBasicCookieStore extends BasicCookieStore implements
		CustomCookieStore {

	/**
	 * Serial Id
	 */
	private static final long serialVersionUID = 5884817839252416275L;

	/**
	 * @see org.apache.http.impl.client.BasicCookieStore#addCookie(org.apache.http.cookie.Cookie)
	 */
	@Override
	public synchronized void addCookie(Cookie cookie) {
		// Copy cookie using a serializable implementation.
		super.addCookie(new SerializableBasicClientCookie2(cookie));
	}

	public void init(Properties properties) {
	}

}

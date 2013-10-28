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

package org.esigate.url;

import org.apache.http.HttpRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.esigate.api.BaseUrlRetrieveStrategy;
import org.esigate.api.ContainerRequestMediator;
import org.esigate.util.HttpRequestHelper;

public class StickySessionBaseUrlRetrieveStrategy implements BaseUrlRetrieveStrategy {
	public static final String ESI_SESSION_COOKIE_NAME = "_esigate_session_cookie";
	private final String[] urls;

	public StickySessionBaseUrlRetrieveStrategy(String[] urls) {
		super();
		this.urls = urls;
	}

	private int generateIndex() {
		return (int) (Math.random() * this.urls.length);
	}

	@Override
	public String getBaseURL(HttpRequest originalRequest) {
		ContainerRequestMediator mediator = HttpRequestHelper.getMediator(originalRequest);
		Cookie sessionCookie = getEsiSessionCookie(mediator.getCookies());
		int index = 0;
		boolean toGenerate = true;

		if (null != sessionCookie) {
			toGenerate = false;
			String indexStr = sessionCookie.getValue();

			if (null != indexStr) {
				try {
					Integer indexInt = Integer.parseInt(indexStr);
					index = indexInt.intValue();
				} catch (Exception e) {
					index = -1;
				}
				if (index < 0 || index >= this.urls.length) {
					toGenerate = true;
				}
			} else {
				toGenerate = true;
			}
		}
		if (toGenerate) {
			index = generateIndex();
			Cookie cookie = new BasicClientCookie(ESI_SESSION_COOKIE_NAME, Integer.toString(index));
			mediator.addCookie(cookie);
		}

		return this.urls[index];
	}

	private Cookie getEsiSessionCookie(Cookie[] cookies) {
		Cookie ret = null;
		if (null != cookies && cookies.length > 0) {
			for (Cookie cookie : cookies) {
				if (ESI_SESSION_COOKIE_NAME.equals(cookie.getName())) {
					ret = cookie;
					break;
				}
			}
		}
		return ret;
	}

}

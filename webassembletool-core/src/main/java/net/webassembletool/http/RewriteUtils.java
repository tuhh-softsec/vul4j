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
package net.webassembletool.http;

import java.util.regex.Pattern;

import net.webassembletool.ResourceContext;
import net.webassembletool.UserContext;

import org.apache.http.cookie.Cookie;

public class RewriteUtils {

	private RewriteUtils() {
	}

	public final static String removeSessionId(String sessionId, String page) {
		String regexp = ";?jsessionid=" + Pattern.quote(sessionId);
		return page.replaceAll(regexp, "");
	}

	public final static String getSessionId(ResourceContext resourceContext) {
		String jsessionid = null;
		UserContext userContext = resourceContext.getUserContext();
		if (userContext != null) {
			for (Cookie cookie : userContext.getCookies()) {
				if ("jsessionid".equalsIgnoreCase(cookie.getName())) {
					jsessionid = cookie.getValue();
					break;
				}
			}
		}
		return jsessionid;
	}

}

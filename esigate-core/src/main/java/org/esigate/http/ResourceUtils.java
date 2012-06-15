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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import org.esigate.ResourceContext;
import org.esigate.api.HttpSession;

/**
 * Utility class to generate URL and path for Resources
 * 
 * @author Francois-Xavier Bonnet
 */
public class ResourceUtils {

	/**
	 * Private constructor
	 */
	private ResourceUtils() {

	}

	private final static String buildQueryString(ResourceContext target) {
		try {
			StringBuilder queryString = new StringBuilder();
			String charset = target.getOriginalRequest().getCharacterEncoding();
			if (charset == null) {
				charset = "ISO-8859-1";
			}
			String originalQuerystring = target.getOriginalRequest().getUri().getRawQuery();
			if (target.isProxy() && originalQuerystring != null) {
				// Remove jsessionid from request if it is present
				// As we are in a java application, the container might add
				// jsessionid to the querystring. We must not forward it to
				// included applications.
				String jsessionid = null;
				HttpSession session = target.getOriginalRequest().getSession(false);
				if (session != null) {
					jsessionid = session.getId();
				}
				if (jsessionid != null) {
					originalQuerystring = RewriteUtils.removeSessionId(jsessionid, originalQuerystring);
				}
				queryString.append(originalQuerystring);
			}
			if (target.getParameters() != null) {
				ResourceUtils.appendParameters(queryString, charset, target.getParameters());
			}
			return queryString.toString();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private static void appendParameters(StringBuilder buf, String charset, Map<String, String> params) throws UnsupportedEncodingException {
		for (Entry<String, String> param : params.entrySet()) {
			if (buf.length() > 0) {
				buf.append("&");
			}
			buf.append(URLEncoder.encode(param.getKey(), charset));
			buf.append("=");
			buf.append(URLEncoder.encode(param.getValue(), charset));
		}
	}

	private final static String concatUrl(String baseUrl, String relUrl) {
		StringBuilder url = new StringBuilder();
		if (baseUrl != null && relUrl != null && (baseUrl.endsWith("/") || baseUrl.endsWith("\\")) && relUrl.startsWith("/")) {
			url.append(baseUrl.substring(0, baseUrl.length() - 1)).append(relUrl);
		} else {
			url.append(baseUrl).append(relUrl);
		}
		return url.toString();
	}

	public final static String getHttpUrlWithQueryString(ResourceContext target) {
		String url = target.getRelUrl();
		if (!url.startsWith("http://") && !url.startsWith("https://")) {
			// Relative URL, we need to add the driver base url
			if (target.getBaseURL() != null) {
				url = concatUrl(target.getBaseURL(), url);
			}
		}
		String queryString = ResourceUtils.buildQueryString(target);
		if (queryString.length() == 0) {
			return url;
		} else {
			return url + "?" + queryString;
		}
	}

}

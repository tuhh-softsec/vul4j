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
package org.esigate.servlet.impl;

import javax.servlet.http.HttpServletRequest;

/**
 * Utility class to work with Servlet request urls.
 * 
 * @author Nicolas Richeton
 * 
 */
public class RequestUrl {

	/**
	 * Get the relative url to the current servlet.
	 * <p>
	 * Uses the reuquest URI and removes : the context path and the servlet
	 * path.
	 * 
	 * @param request
	 *            The current HTTP request
	 * @return the url, relative to the servlet mapping.
	 */
	public static String getRelativeUrl(HttpServletRequest request) {
		String relUrl = request.getRequestURI();
		relUrl = relUrl.substring(request.getContextPath().length());
		if (request.getServletPath() != null) {
			relUrl = relUrl.substring(request.getServletPath().length());
		}

		return relUrl;
	}
}

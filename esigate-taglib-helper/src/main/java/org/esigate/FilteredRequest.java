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

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Wrapper to the response used inside the servlet filter to override some of
 * the methods of the request.
 * 
 * @author Francois-Xavier Bonnet
 */
public class FilteredRequest extends HttpServletRequestWrapper {
	public FilteredRequest(HttpServletRequest request) {
		super(request);
	}

	/**
	 * Returns the user defined as parameter "user" if present
	 */
	@Override
	public String getRemoteUser() {
		String user = getHeader("X_REMOTE_USER");
		if (user != null) {
			return user;
		} else {
			return super.getRemoteUser();
		}
	}

	@Override
	public Principal getUserPrincipal() {
		String user = getRemoteUser();
		if (user != null) {
			return new Principal() {
				@Override
				public String getName() {
					return getRemoteUser();
				}
			};
		} else {
			return null;
		}
	}
}

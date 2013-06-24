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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * This handles the user name and locale passed as parameters by the driver and
 * overrides getRemoteUser and getLocale methods from the request in order for
 * the application to retrieve them.
 * 
 * @author Francois-Xavier Bonnet
 */
public class ConnectorFilter implements Filter {
	@Override
	public void destroy() {
		// Nothing to do
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		FilteredRequest filteredRequest = new FilteredRequest((HttpServletRequest) request);
		filterChain.doFilter(filteredRequest, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// Nothing to do
	}
}

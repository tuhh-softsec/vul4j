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

package org.esigate.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.tuple.Pair;
import org.esigate.Driver;
import org.esigate.DriverFactory;
import org.esigate.HttpErrorPage;
import org.esigate.impl.UriMapping;
import org.esigate.servlet.impl.DriverSelector;
import org.esigate.servlet.impl.RequestUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyFilter implements Filter {
	private static final Logger LOG = LoggerFactory.getLogger(ProxyFilter.class);
	private DriverSelector driverSelector;
	private FilterConfig config;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.config = filterConfig;
		// Force esigate configuration parsing to trigger errors right away (if
		// any) and prevent delay on first call.
		DriverFactory.ensureConfigured();
		driverSelector = new DriverSelector();
		driverSelector.setUseMappings(true);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		HttpServletMediator mediator = new HttpServletMediator(httpServletRequest, httpServletResponse, config.getServletContext(), chain);
		Pair<Driver, UriMapping> dm = null;
		try {
			dm = this.driverSelector.selectProvider(httpServletRequest, false);
			String relUrl = RequestUrl.getRelativeUrl(httpServletRequest, dm.getRight(), false);
			LOG.debug("Proxying {}", relUrl);
			dm.getLeft().proxy(relUrl, mediator.getHttpRequest());
		} catch (HttpErrorPage e) {
			if (!httpServletResponse.isCommitted())
				mediator.sendResponse(e.getHttpResponse());
		}
	}

	@Override
	public void destroy() {
		// Nothing to do
	}

}

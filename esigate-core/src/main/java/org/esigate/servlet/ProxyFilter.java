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
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.esigate.Driver;
import org.esigate.DriverFactory;
import org.esigate.HttpErrorPage;
import org.esigate.esi.EsiRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyFilter implements Filter {
	private static final Logger LOG = LoggerFactory.getLogger(ProxyFilter.class);
	private Pattern[] mappings;
	private Driver[] providers;
	private FilterConfig config;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.config = filterConfig;
		Properties properties = new Properties();
		InputStream inputStream = this.getClass().getResourceAsStream("/esigate-mapping.properties");
		if (inputStream != null) {
			try {
				properties.load(inputStream);
			} catch (Exception e) {
				throw new ServletException(e);
			}
		}
		int size = properties.size();
		Set<Entry<Object, Object>> set = properties.entrySet();
		mappings = new Pattern[size];
		providers = new Driver[size];
		Iterator<Entry<Object, Object>> it = set.iterator();
		int i = 0;
		while (it.hasNext()) {
			Entry<Object, Object> entry = it.next();
			mappings[i] = Pattern.compile((String) entry.getKey());
			providers[i] = DriverFactory.getInstance((String) entry.getValue());
			i++;
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		String relUrl = httpServletRequest.getRequestURI();
		HttpServletMediator mediator = new HttpServletMediator(httpServletRequest, httpServletResponse, config.getServletContext());
		for (int i = 0; i < mappings.length; i++) {
			if (mappings[i].matcher(relUrl).matches()) {
				LOG.debug("Proxying " + relUrl);
				try {
					providers[i].proxy(relUrl, mediator.getHttpRequest(), new EsiRenderer());
				} catch (HttpErrorPage e) {
					mediator.sendResponse(e.getHttpResponse());
				}
				return;
			}
		}
		LOG.debug("Calling local resource " + relUrl);
		ResponseCapturingWrapper wrappedResponse = new ResponseCapturingWrapper(httpServletResponse);
		chain.doFilter(httpServletRequest, wrappedResponse);
		String result = wrappedResponse.getResult();
		StringWriter stringWriter = new StringWriter();
		if (result != null) {
			try {
				new EsiRenderer().render(mediator.getHttpRequest(), result, stringWriter);
				response.getWriter().write(stringWriter.toString());
			} catch (HttpErrorPage e) {
				mediator.sendResponse(e.getHttpResponse());
			}
		}
	}

	@Override
	public void destroy() {
		// Nothing to do
	}

}

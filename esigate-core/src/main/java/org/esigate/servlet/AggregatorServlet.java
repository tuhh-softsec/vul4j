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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.esigate.Driver;
import org.esigate.DriverFactory;
import org.esigate.HttpErrorPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet used to proxy requests from a remote application.
 * <p>
 * Parameters are :
 * <ul>
 * <li>provider (optional): single provider name</li>
 * <li>providers (optional): comma-separated list of provider mappings based on
 * host requested. Format is: host1=provider,host2=provider2</li>
 * </ul>
 * 
 * @author Francois-Xavier Bonnet
 * @author Nicols Richeton
 */
public class AggregatorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(AggregatorServlet.class);
	private String provider = null;
	private Map<String, String> providerMappings = null ;
	
	/**
	 * Return current provider mappings, parsed from the "providers" init param.
	 * 
	 * @return map or null if mappings are not used.
	 */
	public Map<String, String> getProviderMappings() {
		return providerMappings;
	}

	/**
	 * Get the provider to use based on "name"
	 * <p>
	 * 
	 * This methods allows overriding in unit test
	 * 
	 * @param name
	 * @return the Driver instance associated with this servlet
	 */
	public Driver getDriver(String name) {
		return DriverFactory.getInstance(name);
	}
	
	/**
	 * Select the provider for this request.
	 * <p>
	 * Perform selection based on the Host header.
	 * 
	 * @param request
	 * @return provider name or null.
	 */
	public String selectProvider(HttpServletRequest request) {
		// Select provider. null is valid (default)
		String targetProvider = provider;
		if (providerMappings != null) {
			String host = request.getHeader("Host");
			if (host != null) {
				host = host.toLowerCase(Locale.ENGLISH);
				String mapping = providerMappings.get(host);
				if (mapping != null)
					targetProvider = mapping;
			}
		}

		return targetProvider;
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String relUrl = request.getRequestURI();
		relUrl = relUrl.substring(request.getContextPath().length());
		if (request.getServletPath() != null) {
			relUrl = relUrl.substring(request.getServletPath().length());
		}
		LOG.debug("Aggregating {}", relUrl);
		
		// Select provider. null is valid (default)
		String targetProvider = selectProvider( request );
		
		// Process ressource
		HttpServletMediator mediator = new HttpServletMediator(request, response, getServletContext());
		try {
			getDriver(targetProvider).proxy(relUrl, mediator.getHttpRequest());
		} catch (HttpErrorPage e) {
			mediator.sendResponse(e.getHttpResponse());
		}
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		provider = config.getInitParameter("provider");
		
		// Load mappings
		String providersString = config.getInitParameter("providers");
		if (providersString != null) {
			providerMappings = new HashMap<String, String>();
			String[] providersArray = StringUtils.split(providersString, ",");
			for (String p : providersArray) {
				String[] mapping = StringUtils.split(p, "=");
				providerMappings.put(StringUtils.trim(mapping[0]
						.toLowerCase(Locale.ENGLISH)), StringUtils
						.trim(mapping[1]));
			}
		}
	
	}
}

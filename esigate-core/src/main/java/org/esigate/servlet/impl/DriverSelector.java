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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.esigate.Driver;
import org.esigate.DriverFactory;
import org.esigate.HttpErrorPage;
import org.esigate.impl.UriMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles support of legacy options for Driver selection.
 * <p>
 * Configuration is evaluated in the following order :
 * <ol>
 * <li>providers mapping (host-based) in web.xml</li>
 * <li>provider mapping (servlet based) in web.xml</li>
 * <li>provider mapping in configuration file (eg. instance.mappings=/* in
 * esigate.properties)</li>
 * </ol>
 * 
 * @author Nicolas Richeton
 */
public class DriverSelector {
	private static final Logger LOG = LoggerFactory.getLogger(DriverSelector.class);
	private String webXmlProvider = null;
	private Map<String, String> webXmlProviderMappings = null;
	private boolean useMappings = false;

	public void setUseMappings(boolean useConfiguration) {
		this.useMappings = useConfiguration;
	}

	public boolean isUseMappings() {
		return this.useMappings;
	}

	public Map<String, String> getWebXmlProviderMappings() {
		return this.webXmlProviderMappings;
	}

	public String getWebXmlProvider() {
		return this.webXmlProvider;
	}

	public DriverSelector() {

	}

	public void setWebXmlProvider(String provider) {
		this.webXmlProvider = provider;
	}

	public void setWebXmlProviders(String providers) {
		if (providers != null) {
			this.webXmlProviderMappings = new HashMap<String, String>();
			String[] providersArray = StringUtils.split(providers, ",");
			for (String p : providersArray) {
				String[] mapping = StringUtils.split(p, "=");
				this.webXmlProviderMappings.put(StringUtils.trim(mapping[0].toLowerCase(Locale.ENGLISH)), StringUtils.trim(mapping[1]));
			}
		}
	}

	/**
	 * Select the provider for this request.
	 * <p>
	 * Perform selection based on the Host header.
	 * 
	 * @param request
	 * @return provider name or null.
	 * @throws HttpErrorPage
	 */
	public Pair<Driver, UriMapping> selectProvider(HttpServletRequest request, boolean servlet) throws HttpErrorPage {

		String host = request.getHeader("Host");
		String scheme = request.getScheme();

		String relUrl = RequestUrl.getRelativeUrl(request, null, servlet);

		Pair<Driver, UriMapping> result;

		if (this.useMappings) {
			result = DriverFactory.getInstanceFor(scheme, host, relUrl);
		} else {

			// Select provider. null is valid (default)
			String targetProvider = this.webXmlProvider;

			if (this.webXmlProviderMappings != null) {
				if (host != null) {
					host = host.toLowerCase(Locale.ENGLISH);
					String mapping = this.webXmlProviderMappings.get(host);
					if (mapping != null)
						targetProvider = mapping;
				}
			}

			result = new ImmutablePair<Driver, UriMapping>(DriverFactory.getInstance(targetProvider), null);
		}
		LOG.debug("Selected {} for scheme:{} host:{} relUrl:{}", result, scheme, host, relUrl);

		return result;
	}

}

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.esigate.api.BaseUrlRetrieveStrategy;
import org.esigate.impl.UriMapping;
import org.esigate.renderers.ResourceFixupRenderer;
import org.esigate.url.IpHashBaseUrlRetrieveStrategy;
import org.esigate.url.RoundRobinBaseUrlRetrieveStrategy;
import org.esigate.url.SingleBaseUrlRetrieveStrategy;
import org.esigate.url.StickySessionBaseUrlRetrieveStrategy;

/**
 * Driver configuration parameters
 * 
 * @author Francois-Xavier Bonnet
 * @author Nicolas Richeton
 */
public class DriverConfiguration {

	private final String instanceName;
	private final String uriEncoding;
	private final boolean fixResources;
	private final String visibleBaseURL;
	private final int fixMode;
	private final Properties properties;
	private final boolean preserveHost;
	private final BaseUrlRetrieveStrategy baseUrlRetrieveStrategy;
	private final boolean isVisibleBaseURLEmpty;
	private final List<UriMapping> uriMappings;

	public DriverConfiguration(String instanceName, Properties props) {
		this.instanceName = instanceName;
		baseUrlRetrieveStrategy = getBaseUrlRetrieveSession(props);
		uriEncoding = Parameters.URI_ENCODING.getValueString(props);
		preserveHost = Parameters.PRESERVE_HOST.getValueBoolean(props);
		fixResources = Parameters.FIX_RESOURCES.getValueBoolean(props);
		visibleBaseURL = Parameters.VISIBLE_URL_BASE.getValueString(props);
		isVisibleBaseURLEmpty = StringUtils.isEmpty(visibleBaseURL);
		if ("absolute".equalsIgnoreCase(Parameters.FIX_MODE.getValueString(props))) {
			this.fixMode = ResourceFixupRenderer.ABSOLUTE;
		} else {
			this.fixMode = ResourceFixupRenderer.RELATIVE;
		}

		this.uriMappings = parseMappings(props);
		properties = props;
	}

	/**
	 * Read the "Mappings" parameter and create the corresponding UriMapping
	 * rules.
	 * 
	 * @param props
	 * @return The mapping rules for this driver instance.
	 */
	private static List<UriMapping> parseMappings(Properties props) {
		List<UriMapping> mappings = new ArrayList<UriMapping>();

		Collection<String> mappingsParam = Parameters.MAPPINGS.getValueList(props);
		for (String mappingParam : mappingsParam) {
			mappings.add(UriMapping.create(mappingParam));
		}

		return mappings;
	}

	private BaseUrlRetrieveStrategy getBaseUrlRetrieveSession(Properties props) {
		BaseUrlRetrieveStrategy urlStrategy = null;
		String baseURLs = Parameters.REMOTE_URL_BASE.getValueString(props);
		if (StringUtils.isEmpty(baseURLs))
			throw new ConfigurationException(Parameters.REMOTE_URL_BASE.name
					+ " property cannot be empty for instance '" + instanceName + "'");
		String[] urls = StringUtils.split(baseURLs, ",");
		if (1 == urls.length) {
			String baseURL = StringUtils.trimToEmpty(urls[0]);
			urlStrategy = new SingleBaseUrlRetrieveStrategy(baseURL);
		} else if (urls.length > 0) {
			String[] urlArr = new String[urls.length];
			for (int i = 0; i < urls.length; i++) {
				String baseURL = StringUtils.trimToEmpty(urls[i]);
				urlArr[i] = baseURL;
			}
			String strategy = Parameters.REMOTE_URL_BASE_STRATEGY.getValueString(props);
			if (Parameters.ROUNDROBIN.equalsIgnoreCase(strategy)) {
				urlStrategy = new RoundRobinBaseUrlRetrieveStrategy(urlArr);
			} else if (Parameters.IPHASH.equalsIgnoreCase(strategy)) {
				urlStrategy = new IpHashBaseUrlRetrieveStrategy(urlArr);
			} else if (Parameters.STICKYSESSION.equalsIgnoreCase(strategy)) {
				urlStrategy = new StickySessionBaseUrlRetrieveStrategy(urlArr);
			} else {
				throw new ConfigurationException("No such BaseUrlRetrieveStrategy '" + strategy + "'");
			}
		}
		return urlStrategy;
	}

	public int getFixMode() {
		return fixMode;
	}

	public boolean isFixResources() {
		return fixResources;
	}

	public String getVisibleBaseURL(String currentBaseUrl) {
		return isVisibleBaseURLEmpty ? currentBaseUrl : visibleBaseURL;
	}

	public boolean isPreserveHost() {
		return preserveHost;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public String getUriEncoding() {
		return uriEncoding;
	}

	public Properties getProperties() {
		return properties;
	}

	public BaseUrlRetrieveStrategy getBaseUrlRetrieveStrategy() {
		return baseUrlRetrieveStrategy;
	}

	/**
	 * Get the URI mappings for this driver instance
	 * 
	 * @return The URI mappings for this driver instance.
	 */
	public List<UriMapping> getUriMappings() {
		return this.uriMappings;
	}

}

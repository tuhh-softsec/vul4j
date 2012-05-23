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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.esigate.api.BaseUrlRetrieveStrategy;
import org.esigate.renderers.ResourceFixupRenderer;
import org.esigate.url.IpHashBaseUrlRetrieveStrategy;
import org.esigate.url.RoundRobinBaseUrlRetrieveStrategy;
import org.esigate.url.SingleBaseUrlRetrieveStrategy;
import org.esigate.url.StickySessionBaseUrlRetrieveStrategy;
import org.esigate.util.FilterList;
import org.esigate.util.PropertiesUtil;

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
	private final String authenticationHandler;
	private final Properties properties;
	private final boolean preserveHost;
	private final String cookieStore;
	private final String filter;
	private final List<String> parsableContentTypes;
	private final FilterList requestHeadersFilterList;
	private final FilterList responseHeadersFilterList;
	private final BaseUrlRetrieveStrategy baseUrlRetrieveStrategy;
	private final boolean isVisibleBaseURLEmpty;

	public DriverConfiguration(String instanceName, Properties props) {
		this.instanceName = instanceName;
		baseUrlRetrieveStrategy = getBaseUrlRetrieveSession(props);
		uriEncoding = Parameters.URI_ENCODING.getValueString(props);
		authenticationHandler = Parameters.AUTHENTICATION_HANDLER.getValueString(props);
		cookieStore = Parameters.COOKIE_STORE.getValueString(props);
		filter = Parameters.FILTER.getValueString(props);
		preserveHost = Parameters.PRESERVE_HOST.getValueBoolean(props);
		fixResources = Parameters.FIX_RESOURCES.getValueBoolean(props);
		visibleBaseURL = Parameters.VISIBLE_URL_BASE.getValueString(props);
		isVisibleBaseURLEmpty = StringUtils.isEmpty(visibleBaseURL);
		if ("absolute".equalsIgnoreCase(Parameters.FIX_MODE.getValueString(props))) {
			this.fixMode = ResourceFixupRenderer.ABSOLUTE;
		} else {
			this.fixMode = ResourceFixupRenderer.RELATIVE;
		}
		String strContentTypes = Parameters.PARSABLE_CONTENT_TYPES.getValueString(props);
		StringTokenizer tokenizer = new StringTokenizer(strContentTypes, ",");
		String contentType;
		parsableContentTypes = new ArrayList<String>();
		while (tokenizer.hasMoreElements()) {
			contentType = tokenizer.nextToken();
			contentType = contentType.trim();
			parsableContentTypes.add(contentType);
		}
		// Populate headers filter lists
		requestHeadersFilterList = new FilterList();
		responseHeadersFilterList = new FilterList();
		// By default all headers are forwarded
		requestHeadersFilterList.add(Collections.singletonList("*"));
		responseHeadersFilterList.add(Collections.singletonList("*"));
		PropertiesUtil.populate(requestHeadersFilterList, props, Parameters.FORWARD_REQUEST_HEADERS.name, Parameters.DISCARD_REQUEST_HEADERS.name, "",
				Parameters.DISCARD_REQUEST_HEADERS.defaultValue);
		PropertiesUtil.populate(responseHeadersFilterList, props, Parameters.FORWARD_RESPONSE_HEADERS.name, Parameters.DISCARD_RESPONSE_HEADERS.name, "",
				Parameters.DISCARD_RESPONSE_HEADERS.defaultValue);
		properties = props;
	}

	private BaseUrlRetrieveStrategy getBaseUrlRetrieveSession(Properties props) {
		BaseUrlRetrieveStrategy urlStrategy = null;
		String baseURLs = Parameters.REMOTE_URL_BASE.getValueString(props);
		try {
			if (!StringUtils.isEmpty(baseURLs)) {
				String[] urls = StringUtils.split(baseURLs, ",");
				if (1 == urls.length) {
					String baseURL = StringUtils.trimToEmpty(urls[0]);
					new URL(baseURL);
					urlStrategy = new SingleBaseUrlRetrieveStrategy(baseURL);
				} else if (urls.length > 0) {
					String[] urlArr = new String[urls.length];
					for (int i = 0; i < urls.length; i++) {
						String baseURL = StringUtils.trimToEmpty(urls[i]);
						new URL(baseURL);
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
			}
		} catch (MalformedURLException e) {
			throw new ConfigurationException(e);
		}
		return urlStrategy;
	}

	public String getFilter() {
		return filter;
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

	public String getAuthenticationHandler() {
		return authenticationHandler;
	}

	public Properties getProperties() {
		return properties;
	}

	public String getCookieStore() {
		return cookieStore;
	}

	public Collection<String> getParsableContentTypes() {
		return parsableContentTypes;
	}

	public BaseUrlRetrieveStrategy getBaseUrlRetrieveStrategy() {
		return baseUrlRetrieveStrategy;
	}

	public boolean isForwardedRequestHeader(String headerName) {
		return requestHeadersFilterList.contains(headerName);
	}

	public boolean isForwardedResponseHeader(String headerName) {
		return responseHeadersFilterList.contains(headerName);
	}

}

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
import org.esigate.authentication.RemoteUserAuthenticationHandler;
import org.esigate.cookie.SerializableBasicCookieStore;
import org.esigate.renderers.ResourceFixupRenderer;
import org.esigate.url.IpHashBaseUrlRetrieveStrategy;
import org.esigate.url.RoundRobinBaseUrlRetrieveStrategy;
import org.esigate.url.SingleBaseUrlRetrieveStrategy;
import org.esigate.url.StickySessionBaseUrlRetrieveStrategy;
import org.esigate.util.FilterList;
import org.esigate.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Driver configuration parameters
 * 
 * @author Francois-Xavier Bonnet
 * @author Nicolas Richeton
 */
public class DriverConfiguration {
	private static final Logger LOG = LoggerFactory.getLogger(DriverConfiguration.class);

	private static final String STICKYSESSION = "stickysession";
	private static final String IPHASH = "iphash";
	private static final String ROUNDROBIN = "roundrobin";

	private final String instanceName;
	private String uriEncoding = "ISO-8859-1";
	private boolean fixResources = false;
	private String visibleBaseURL = null;
	private int fixMode = ResourceFixupRenderer.RELATIVE;
	private int maxConnectionsPerHost = 20;
	private int connectTimeout = 1000;
	private int socketTimeout = 10000;
	private boolean useCache = true;
	private int cacheRefreshDelay = 0;
	private int cacheMaxFileSize = 0;
	private final String localBase;
	private boolean putInCache = false;
	private String proxyHost = null;
	private int proxyPort = 0;
	private String proxyUser = null;
	private String proxyPassword = null;
	private boolean filterJsessionid = true;
	private String authenticationHandler = RemoteUserAuthenticationHandler.class.getName();
	private final Properties properties;
	private boolean preserveHost = false;
	private String cookieStore = SerializableBasicCookieStore.class.getName();
	private String filter = null;
	private final List<String> parsableContentTypes;
	private final FilterList requestHeadersFilterList;
	private final FilterList responseHeadersFilterList;
	private final BaseUrlRetrieveStrategy baseUrlRetrieveStrategy;
	private boolean isVisibleBaseURLEmpty = true;

	private static final String DEFAULT_PARSABLE_CONTENT_TYPES = "text/html, application/xhtml+xml";
	private static final String DEFAULT_BLACK_LISTED_REQUEST_HEADERS = "Authorization,Connection,"
			+ "Content-Length,Cookie,Expect,Host,Max-Forwards,Proxy-Authorization,Range,TE,Trailer,"
			+ "Transfer-Encoding,Upgrade";
	private static final String DEFAULT_BLACK_LISTED_RESPONSE_HEADERS = "Age,Connection,Content-Encoding,"
			+ "Content-Length,Content-Location,Content-MD5,Keep-Alive,Location,Proxy-Authenticate,Set-Cookie,"
			+ "Trailer,Transfer-Encoding,WWW-Authenticate";

	public DriverConfiguration(String instanceName, Properties props) {
		this.instanceName = instanceName;
		// Remote application settings
		baseUrlRetrieveStrategy = getBaseUrlRetrieveSession(props);

		uriEncoding = PropertiesUtil.getPropertyValue(props, "uriEncoding", uriEncoding);
		maxConnectionsPerHost = PropertiesUtil.getPropertyValue(props, "maxConnectionsPerHost", maxConnectionsPerHost);
		int timeout = PropertiesUtil.getPropertyValue(props, "timeout", 1000);
		connectTimeout = PropertiesUtil.getPropertyValue(props, "connectTimeout", timeout);
		socketTimeout = PropertiesUtil.getPropertyValue(props, "socketTimeout", timeout * 10);

		// Cache settings
		cacheRefreshDelay = PropertiesUtil.getPropertyValue(props, "cacheRefreshDelay", cacheRefreshDelay);
		cacheMaxFileSize = PropertiesUtil.getPropertyValue(props, "cacheMaxFileSize", cacheMaxFileSize);

		// Local file system settings
		localBase = PropertiesUtil.getPropertyValue(props, "localBase", null);
		putInCache = PropertiesUtil.getPropertyValue(props, "putInCache", putInCache);

		// proxy settings
		if (props.getProperty("proxyHost") != null && props.getProperty("proxyPort") != null) {
			proxyHost = PropertiesUtil.getPropertyValue(props, "proxyHost", null);
			proxyPort = PropertiesUtil.getPropertyValue(props, "proxyPort", 0);
			if (props.getProperty("proxyUser") != null && props.getProperty("proxyPassword") != null) {
				proxyUser = PropertiesUtil.getPropertyValue(props, "proxyUser", null);
				proxyPassword = PropertiesUtil.getPropertyValue(props, "proxyPassword", null);
			}
		}
		useCache = PropertiesUtil.getPropertyValue(props, "useCache", useCache);
		filterJsessionid = PropertiesUtil.getPropertyValue(props, "filterJsessionid", filterJsessionid);

		// Authentification handler
		authenticationHandler = PropertiesUtil.getPropertyValue(props, "authenticationHandler", authenticationHandler);

		// Cookie Store
		cookieStore = PropertiesUtil.getPropertyValue(props, "cookieStore", cookieStore);

		// Wat Filter
		filter = PropertiesUtil.getPropertyValue(props, "filter", filter);

		preserveHost = PropertiesUtil.getPropertyValue(props, "preserveHost", preserveHost);

		// Fix resources
		if (props.getProperty("fixResources") != null) {
			fixResources = PropertiesUtil.getPropertyValue(props, "fixResources", fixResources);
			// Fix resources mode
			if (props.getProperty("fixMode") != null) {
				if ("absolute".equalsIgnoreCase(props.getProperty("fixMode"))) {
					this.fixMode = ResourceFixupRenderer.ABSOLUTE;
				}
			}
			// Visible base url
			visibleBaseURL = PropertiesUtil.getPropertyValue(props, "visibleUrlBase", null);
			isVisibleBaseURLEmpty = StringUtils.isEmpty(visibleBaseURL);
		}

		// Parsable content types
		String strContentTypes = PropertiesUtil.getPropertyValue(props, "parsableContentTypes", DEFAULT_PARSABLE_CONTENT_TYPES);
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
		if (props.get("blackListedHeaders") != null) {
			LOG.warn("Property 'blackListedHeaders' is deprecated");
			Collection<String> blackListedHeadersList = PropertiesUtil.getPropertyValueAsList(props, "blackListedHeaders");
			requestHeadersFilterList.remove(blackListedHeadersList);
			responseHeadersFilterList.remove(blackListedHeadersList);
		}
		PropertiesUtil.populate(requestHeadersFilterList, props, "forwardRequestHeaders", "discardRequestHeaders", "", DEFAULT_BLACK_LISTED_REQUEST_HEADERS);
		PropertiesUtil.populate(responseHeadersFilterList, props, "forwardResponseHeaders", "discardResponseHeaders", "", DEFAULT_BLACK_LISTED_RESPONSE_HEADERS);

		properties = props;
	}

	private BaseUrlRetrieveStrategy getBaseUrlRetrieveSession(Properties props) {
		BaseUrlRetrieveStrategy urlStrategy = null;
		String baseURLs = PropertiesUtil.getPropertyValue(props, "remoteUrlBase", null);
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
					String strategy = StringUtils.trimToEmpty(PropertiesUtil.getPropertyValue(props, "remoteUrlBaseStrategy", ROUNDROBIN));
					if (ROUNDROBIN.equalsIgnoreCase(strategy)) {
						urlStrategy = new RoundRobinBaseUrlRetrieveStrategy(urlArr);
					} else if (IPHASH.equalsIgnoreCase(strategy)) {
						urlStrategy = new IpHashBaseUrlRetrieveStrategy(urlArr);
					} else if (STICKYSESSION.equalsIgnoreCase(strategy)) {
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

	public int getMaxConnectionsPerHost() {
		return maxConnectionsPerHost;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public int getSocketTimeout() {
		return socketTimeout;
	}

	public boolean isUseCache() {
		return useCache;
	}

	public int getCacheRefreshDelay() {
		return cacheRefreshDelay;
	}

	public int getCacheMaxFileSize() {
		return cacheMaxFileSize;
	}

	public String getLocalBase() {
		return localBase;
	}

	public boolean isPutInCache() {
		return putInCache;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public String getProxyUser() {
		return proxyUser;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}

	public String getUriEncoding() {
		return uriEncoding;
	}

	public boolean isFilterJsessionid() {
		return filterJsessionid;
	}

	public String getAuthenticationHandler() {
		return authenticationHandler;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setCookieStore(String cookieStore) {
		this.cookieStore = cookieStore;
	}

	public String getCookieStore() {
		return cookieStore;
	}

	/**
	 * List of parsable content types. Default is text/html, application/xhtml+xml
	 * 
	 * @return List of parsable content types.
	 */
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

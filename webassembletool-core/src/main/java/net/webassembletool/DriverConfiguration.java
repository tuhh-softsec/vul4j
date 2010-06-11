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
package net.webassembletool;

import java.util.Properties;

import net.webassembletool.authentication.RemoteUserAuthenticationHandler;
import net.webassembletool.cookie.SerializableBasicCookieStore;
import net.webassembletool.renderers.ResourceFixupRenderer;

/**
 * Driver configuration parameters
 * 
 * @author Francois-Xavier Bonnet
 * @contributor Nicolas Richeton
 */
public class DriverConfiguration {
	private final String instanceName;
	private final String baseURL;
	private String uriEncoding = "ISO-8859-1";
	private boolean fixResources = false;
	private String visibleBaseURL = null;
	private int fixMode = ResourceFixupRenderer.RELATIVE;
	private int maxConnectionsPerHost = 20;
	private int timeout = 1000;
	private boolean useCache = true;
	private int cacheRefreshDelay = 0;
	private int cacheMaxFileSize = 0;
	private final String localBase;
	private boolean putInCache = false;
	private String proxyHost;
	private int proxyPort = 0;
	private boolean filterJsessionid = true;
	private String authenticationHandler = RemoteUserAuthenticationHandler.class.getName();
	private final Properties properties;
	private boolean preserveHost = false;
	private String cookieStore = SerializableBasicCookieStore.class.getName();
	private String filter = null;

	public String getFilter() {
		return filter;
	}

	public DriverConfiguration(String instanceName, Properties props) {
		this.instanceName = instanceName;
		// Remote application settings
		baseURL = props.getProperty("remoteUrlBase");
		if (props.getProperty("uriEncoding") != null) {
			uriEncoding = props.getProperty("uriEncoding");
		}
		if (props.getProperty("maxConnectionsPerHost") != null) {
			maxConnectionsPerHost = Integer.parseInt(props
					.getProperty("maxConnectionsPerHost"));
		}
		if (props.getProperty("timeout") != null) {
			timeout = Integer.parseInt(props.getProperty("timeout"));
		}
		// Cache settings
		if (props.getProperty("cacheRefreshDelay") != null) {
			cacheRefreshDelay = Integer.parseInt(props
					.getProperty("cacheRefreshDelay"));
		}
		if (props.getProperty("cacheMaxFileSize") != null) {
			cacheMaxFileSize = Integer.parseInt(props
					.getProperty("cacheMaxFileSize"));
		}
		// Local file system settings
		localBase = props.getProperty("localBase");
		if (props.getProperty("putInCache") != null) {
			putInCache = Boolean.parseBoolean(props.getProperty("putInCache"));
		}
		// proxy settings
		if (props.getProperty("proxyHost") != null
				&& props.getProperty("proxyPort") != null) {
			proxyHost = props.getProperty("proxyHost");
			proxyPort = Integer.parseInt(props.getProperty("proxyPort"));
		}
		if (props.getProperty("useCache") != null) {
			useCache = Boolean.parseBoolean(props.getProperty("useCache"));
		}
		if (props.getProperty("filterJsessionid") != null) {
			filterJsessionid = Boolean.parseBoolean(props
					.getProperty("filterJsessionid"));
		}

		// Authentification handler
		if (props.getProperty("authenticationHandler") != null) {
			authenticationHandler = props.getProperty("authenticationHandler");
		}

		// Cookie Store
		if (props.getProperty("cookieStore") != null) {
			cookieStore = props.getProperty("cookieStore");
		}

		// Wat Filter
		if (props.getProperty("filter") != null) {
			filter = props.getProperty("filter");
		}

		if (props.getProperty("preserveHost") != null) {
			preserveHost = Boolean.parseBoolean(props
					.getProperty("preserveHost"));
		}

		// Fix resources
		if (props.getProperty("fixResources") != null) {
			fixResources = Boolean.parseBoolean(props
					.getProperty("fixResources"));
			// Fix resources mode
			if (props.getProperty("fixMode") != null) {
				if ("absolute".equalsIgnoreCase(props.getProperty("fixMode"))) {
					this.fixMode = ResourceFixupRenderer.ABSOLUTE;
				}
			}
			// Visible base url
			if (props.getProperty("visibleUrlBase") != null) {
				visibleBaseURL = props.getProperty("visibleUrlBase");
			} else {
				visibleBaseURL = baseURL;
			}
		}

		properties = props;
	}

	public int getFixMode() {
		return fixMode;
	}

	public boolean isFixResources() {
		return fixResources;
	}

	public String getVisibleBaseURL() {
		return visibleBaseURL;
	}

	public boolean isPreserveHost() {
		return preserveHost;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public String getBaseURL() {
		return baseURL;
	}

	public int getMaxConnectionsPerHost() {
		return maxConnectionsPerHost;
	}

	public int getTimeout() {
		return timeout;
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

}

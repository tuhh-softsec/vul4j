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
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.esigate.authentication.RemoteUserAuthenticationHandler;
import org.esigate.cache.CacheStorage;
import org.esigate.cache.DefaultCacheStorage;
import org.esigate.cookie.SerializableBasicCookieStore;
import org.esigate.renderers.ResourceFixupRenderer;


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
	private Class<? extends CacheStorage> cacheStorageClass;
	private final String localBase;
	private boolean putInCache = false;
	private String proxyHost;
	private int proxyPort = 0;
	private boolean filterJsessionid = true;
	private String authenticationHandler = RemoteUserAuthenticationHandler.class
			.getName();
	private final Properties properties;
	private boolean preserveHost = false;
	private String cookieStore = SerializableBasicCookieStore.class.getName();
	private String filter = null;
	private final List<String> parsableContentTypes;
	private final Set<String> blackListedHeaders;

	private static final String DEFAULT_PARSABLE_CONTENT_TYPES = "text/html, application/xhtml+xml";
	private static final String DEFAULT_BLACK_LISTED_HEADERS = "Content-Length,Content-Encoding,Transfer-Encoding,"
			+ "Set-Cookie,Cookie,Connection,Keep-Alive,Proxy-Authenticate,Proxy-Authorization,TE,Trailers,Upgrade";

	private URL baseURLasURL = null;

	public DriverConfiguration(String instanceName, Properties props) {
		this.instanceName = instanceName;
		// Remote application settings
		baseURL = props.getProperty("remoteUrlBase");
		try {
			if (baseURL != null) {
				baseURLasURL = new URL(baseURL);
			}
		} catch (MalformedURLException e1) {
			throw new ConfigurationException(e1);
		}
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
		if (props.getProperty("cacheStorageClassName") != null) {
			String cacheStorageClassName = props
					.getProperty("cacheStorageClassName");
			try {
				@SuppressWarnings("unchecked")
				Class<? extends CacheStorage> cacheStorageClass = (Class<? extends CacheStorage>) this
						.getClass().getClassLoader()
						.loadClass(cacheStorageClassName);
				if (cacheStorageClass != null) {
					this.cacheStorageClass = cacheStorageClass;
				}
			} catch (Exception e) {
				throw new RuntimeException(
						"Cashestorage insatnce can not be loaded", e);
			}
		}
		if (null == this.cacheStorageClass) {
			this.cacheStorageClass = DefaultCacheStorage.class;
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
		// Parsable content types
		String strContentTypes = props.getProperty("parsableContentTypes",
				DEFAULT_PARSABLE_CONTENT_TYPES);
		StringTokenizer tokenizer = new StringTokenizer(strContentTypes, ",");
		String contentType;
		parsableContentTypes = new ArrayList<String>();
		while (tokenizer.hasMoreElements()) {
			contentType = tokenizer.nextToken();
			contentType = contentType.trim();
			parsableContentTypes.add(contentType);
		}

		// populate headers black list
		blackListedHeaders = new HashSet<String>();
		String headers = props.getProperty("blackListedHeaders", DEFAULT_BLACK_LISTED_HEADERS);
		String[] split = headers.split(",");
		for (String header : split) {
			blackListedHeaders.add(header.toLowerCase());
		}

		properties = props;
	}

	public boolean isBlackListed(String headerName) {
		if (headerName == null || headerName.length() == 0) {
			return true;
		}
		return blackListedHeaders.contains(headerName.toLowerCase());
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

	public Class<? extends CacheStorage> getCacheStorageClass() {
		return cacheStorageClass;
	}

	/**
	 * List of parsable content types. Default is text/html,
	 * application/xhtml+xml
	 * 
	 * @return List of parsable content types.
	 */
	public List<String> getParsableContentTypes() {
		return parsableContentTypes;
	}

	public URL getBaseURLasURL() {
		return baseURLasURL;
	}

}

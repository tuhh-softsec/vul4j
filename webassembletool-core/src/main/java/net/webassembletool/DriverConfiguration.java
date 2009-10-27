package net.webassembletool;

import java.util.Properties;

import net.webassembletool.authentication.RemoteUserAuthenticationHandler;

/**
 * Driver configuration parameters
 * 
 * @author Francois-Xavier Bonnet
 */
class DriverConfiguration {
	private final String instanceName;
	private final String baseURL;
	private String uriEncoding = "ISO-8859-1";
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
	private String authenticationHandler = RemoteUserAuthenticationHandler.class
			.getName();
	private Properties properties;

	public DriverConfiguration(String instanceName, Properties props) {
		this.instanceName = instanceName;
		// Remote application settings
		baseURL = props.getProperty("remoteUrlBase");
		if (props.getProperty("uriEncoding") != null)
			uriEncoding = props.getProperty("uriEncoding");
		if (props.getProperty("maxConnectionsPerHost") != null)
			maxConnectionsPerHost = Integer.parseInt(props
					.getProperty("maxConnectionsPerHost"));
		if (props.getProperty("timeout") != null)
			timeout = Integer.parseInt(props.getProperty("timeout"));
		// Cache settings
		if (props.getProperty("cacheRefreshDelay") != null)
			cacheRefreshDelay = Integer.parseInt(props
					.getProperty("cacheRefreshDelay"));
		if (props.getProperty("cacheMaxFileSize") != null)
			cacheMaxFileSize = Integer.parseInt(props
					.getProperty("cacheMaxFileSize"));
		// Local file system settings
		localBase = props.getProperty("localBase");
		if (props.getProperty("putInCache") != null)
			putInCache = Boolean.parseBoolean(props.getProperty("putInCache"));
		// proxy settings
		if (props.getProperty("proxyHost") != null
				&& props.getProperty("proxyPort") != null) {
			proxyHost = props.getProperty("proxyHost");
			proxyPort = Integer.parseInt(props.getProperty("proxyPort"));
		}
		if (props.getProperty("useCache") != null)
			useCache = Boolean.parseBoolean(props.getProperty("useCache"));
		if (props.getProperty("filterJsessionid") != null)
			filterJsessionid = Boolean.parseBoolean(props
					.getProperty("filterJsessionid"));
		if (props.getProperty("authenticationHandler") != null)
			authenticationHandler = props.getProperty("authenticationHandler");
		properties = props;
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

}

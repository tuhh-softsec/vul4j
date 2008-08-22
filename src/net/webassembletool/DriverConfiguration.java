/**
 * 
 */
package net.webassembletool;

import java.util.Properties;

/**
 * TODO
 * 
 * @author FRBON, 22 août 2008
 */
class DriverConfiguration {
    private String instanceName;
    private String baseURL;
    private int maxConnectionsPerHost = 20;
    private int timeout = 1000;
    private boolean useCache = true;
    private int cacheRefreshDelay = 0;
    private int cacheMaxFileSize = 0;
    private String localBase;
    private boolean putInCache = false;
    private String proxyHost;
    private int proxyPort = 0;

    public DriverConfiguration(String instanceName, Properties props) {
	// Remote application settings
	baseURL = props.getProperty("remoteUrlBase");
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

}

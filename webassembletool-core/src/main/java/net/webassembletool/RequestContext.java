/**
 * 
 */
package net.webassembletool;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Represents the location of a requested resource with all the necessary
 * parameters. When proxyMode is set to true, the resource should not be cached
 * and any cookie or parameter coming from the original request should be
 * forwarded to the target server.
 * 
 * @author Francois-Xavier Bonnet
 */
public class RequestContext {
	private final Driver driver;

	/**
	 * @return driver.
	 */
	public Driver getDriver() {
		return driver;
	}

	private final String relUrl;
	private final HttpServletRequest originalRequest;
	private final Map<String, String> parameters;
	private boolean proxyMode = false;

	public void setProxyMode(boolean proxyMode) {
		this.proxyMode = proxyMode;
	}

	public RequestContext(Driver driver, String relUrl,
			Map<String, String> parameters, HttpServletRequest originalRequest) {
		this.driver = driver;
		this.relUrl = relUrl;
		if (parameters != null)
			this.parameters = parameters;
		else
			this.parameters = new HashMap<String, String>();
		this.originalRequest = originalRequest;
	}

	public String getRelUrl() {
		return relUrl;
	}

	public HttpServletRequest getOriginalRequest() {
		return originalRequest;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public boolean isProxyMode() {
		return proxyMode;
	}

	public boolean isCacheable() {
		return "GET".equalsIgnoreCase(originalRequest.getMethod());
	}

	public UserContext getUserContext() {
		return driver.getContext(originalRequest);
	}

}

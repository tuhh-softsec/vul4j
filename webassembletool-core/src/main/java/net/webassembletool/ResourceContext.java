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
public class ResourceContext {
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
	private boolean proxy = false;
	private boolean preserveHost = false;
	private boolean neededForTransformation = true;

	public boolean isPreserveHost() {
		return preserveHost;
	}

	public void setPreserveHost(boolean preserveHost) {
		this.preserveHost = preserveHost;
	}

	public void setProxy(boolean proxyMode) {
		this.proxy = proxyMode;
	}

	public ResourceContext(Driver driver, String relUrl,
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

	public boolean isProxy() {
		return proxy;
	}

	public UserContext getUserContext() {
		return driver.getContext(originalRequest);
	}

	public boolean isNeededForTransformation() {
		return neededForTransformation;
	}

	public void setNeededForTransformation(boolean neededForTransformation) {
		this.neededForTransformation = neededForTransformation;
	}

}

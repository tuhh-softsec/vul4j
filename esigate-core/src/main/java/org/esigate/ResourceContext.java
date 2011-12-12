/**
 * 
 */
package org.esigate;

import java.util.HashMap;
import java.util.Map;

import org.esigate.api.HttpRequest;
import org.esigate.api.HttpResponse;

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
	private final HttpRequest originalRequest;
	private final HttpResponse originalResponse;
	private final Map<String, String> parameters;
	private boolean proxy = false;
	private boolean preserveHost = false;
	private boolean neededForTransformation = true;
	private Map<String, String> validators = null;

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
			Map<String, String> parameters, HttpRequest originalRequest,
			HttpResponse originalResponse) {
		this.driver = driver;
		this.relUrl = relUrl;
		if (parameters != null) {
			this.parameters = parameters;
		} else {
			this.parameters = new HashMap<String, String>();
		}
		this.originalRequest = originalRequest;
		this.originalResponse = originalResponse;
	}

	public String getRelUrl() {
		return relUrl;
	}

	public HttpRequest getOriginalRequest() {
		return originalRequest;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public boolean isProxy() {
		return proxy;
	}

	public UserContext getUserContext() {
		return driver.getUserContext(originalRequest);
	}

	public boolean isNeededForTransformation() {
		return neededForTransformation;
	}

	public void setNeededForTransformation(boolean neededForTransformation) {
		this.neededForTransformation = neededForTransformation;
	}

	public HttpResponse getOriginalResponse() {
		return originalResponse;
	}

	public Map<String, String> getValidators() {
		return validators;
	}

	public void setValidators(Map<String, String> validators) {
		this.validators = validators;
	}
}

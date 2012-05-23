/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.esigate;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.esigate.api.HttpRequest;
import org.esigate.api.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the location of a requested resource with all the necessary parameters. When proxyMode is set to true, the resource should not be cached and any cookie or parameter coming from the
 * original request should be forwarded to the target server.
 * 
 * @author Francois-Xavier Bonnet
 */
public class ResourceContext {
	private static final Logger LOG = LoggerFactory.getLogger(ResourceContext.class);
	private final Driver driver;

	/**
	 * @return driver.
	 */
	public Driver getDriver() {
		return driver;
	}

	private final String baseURL;
	private final String relUrl;
	private final HttpRequest originalRequest;
	private final HttpResponse originalResponse;
	private final Map<String, String> parameters;
	private boolean proxy = false;
	private boolean preserveHost = false;
	private URL baseURLasURL = null;

	public boolean isPreserveHost() {
		return preserveHost;
	}

	public void setPreserveHost(boolean preserveHost) {
		this.preserveHost = preserveHost;
	}

	public void setProxy(boolean proxyMode) {
		this.proxy = proxyMode;
	}

	public ResourceContext(Driver driver, String relUrl, Map<String, String> parameters, HttpRequest originalRequest, HttpResponse originalResponse) {
		this.driver = driver;
		String baseURLLocal = null;
		if (null != driver && null != driver.getConfiguration() && null != driver.getConfiguration().getBaseUrlRetrieveStrategy()) {
			baseURLLocal = driver.getConfiguration().getBaseUrlRetrieveStrategy().getBaseURL(originalRequest, originalResponse);
		}
		this.baseURL = baseURLLocal;
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

	public HttpResponse getOriginalResponse() {
		return originalResponse;
	}

	public String getBaseURL() {
		return baseURL;
	}

	public URL getBaseURLasURL() {
		if (null == baseURLasURL) {
			if (null != baseURL) {
				try {
					baseURLasURL = new URL(baseURL);
				} catch (MalformedURLException e) {
					LOG.error("Base URL is not valid", e);
				}
			}
		}
		return baseURLasURL;
	}
}

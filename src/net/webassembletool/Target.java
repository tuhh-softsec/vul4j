/**
 * 
 */
package net.webassembletool;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Represents the location of a requested resource with all the necessary
 * parameters. When proxyMode is set to true, the resource should not be cached
 * and any cookie or parameter coming from the original request should be
 * forwarded to the target server.
 * 
 * @author François-Xavier Bonnet
 */
public class Target {
    private String relUrl;
    private HttpServletRequest originalRequest;
    private Map<String, String> parameters;
    private Context context;
    private boolean proxyMode = false;
    private String baseUrl;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setProxyMode(boolean proxyMode) {
	this.proxyMode = proxyMode;
    }

    public Target(String relUrl, Context context,
	    Map<String, String> parameters, HttpServletRequest originalRequest) {
	this.relUrl = relUrl;
	this.context = context;
	this.parameters = parameters;
	this.originalRequest = originalRequest;
    }

    public Target(String relUrl, Context context, Map<String, String> parameters) {
	this.relUrl = relUrl;
	this.context = context;
	this.parameters = parameters;
    }

    public String getMethod() {
	if (originalRequest != null)
	    return originalRequest.getMethod();
	return "GET";
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

    public Context getContext() {
	return context;
    }

    public boolean isCacheable() {
	return !proxyMode && "GET".equalsIgnoreCase(getMethod());
    }
}

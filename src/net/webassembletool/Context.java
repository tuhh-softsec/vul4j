package net.webassembletool;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.httpclient.HttpState;

/**
 * User context that can be used in the master application to define the user id
 * and the Locale. This context will be transmitted to the provider
 * applications.<br /> There is one instance of user context associated with
 * each Thread.
 * 
 * @author François-Xavier Bonnet
 * 
 */
public class Context {
    private final static String USER_PARAM_KEY = "user";
    private final static String LOCALE_PARAM_KEY = "locale";
    private Map<String, String> parameterMap = new TreeMap<String, String>();
    private HttpState httpState;

    public Context() {
	// Nothing to do
    }

    public Map<String, String> getParameterMap() {
	return parameterMap;
    }

    public Set<String> getParameterNames() {
	return parameterMap.keySet();
    }

    public String getLocale() {
	return parameterMap.get(LOCALE_PARAM_KEY);
    }

    public void setLocale(String locale) {
	parameterMap.put(LOCALE_PARAM_KEY, locale);
    }

    public String getUser() {
	return parameterMap.get(LOCALE_PARAM_KEY);
    }

    public void setUser(String user) {
	parameterMap.put(USER_PARAM_KEY, user);
    }

    public HttpState getHttpState() {
	return httpState;
    }

    public void setHttpState(HttpState httpState) {
	this.httpState = httpState;
    }

    public Object getParameter(String key) {
	return parameterMap.get(key);
    }

    public void setParameter(String key, String value) {
	parameterMap.put(key, value);
    }
}

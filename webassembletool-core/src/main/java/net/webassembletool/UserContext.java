package net.webassembletool;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpState;

/**
 * User context that can be used in the master application to define the user id
 * and the Locale. This context will be transmitted to the provider
 * applications.<br />
 * There is one instance of user context associated with each Thread.
 * 
 * @author François-Xavier Bonnet
 * 
 */
public class UserContext {
    private final static String USER_PARAM_KEY = "user";
    private final static String LOCALE_PARAM_KEY = "locale";
    private final Map<String, String> parameterMap = new TreeMap<String, String>();
    private HttpState httpState = new HttpState();

    public UserContext() {
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

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	StringBuilder result = new StringBuilder();
	result.append("Parameters={\n");
	Iterator<Map.Entry<String, String>> it = parameterMap.entrySet()
		.iterator();
	while (it.hasNext()) {
	    Map.Entry<String, String> entry = it.next();
	    result.append("\t");
	    result.append(entry.getKey());
	    result.append("=");
	    result.append(entry.getValue());
	    result.append("\n");
	}
	result.append("}");
	result.append(" Cookies={\n");
	if (httpState != null) {
	    for (Cookie cookie : httpState.getCookies()) {
		result.append("\t");
		if (cookie.getSecure())
		    result.append("https");
		else
		    result.append("http");
		result.append("://");
		result.append(cookie.getDomain());
		result.append(cookie.getPath());
		result.append("#");
		result.append(cookie.getName());
		result.append("=");
		result.append(cookie.getValue());
		result.append("\n");
	    }
	}
	result.append("}");
	return result.toString();
    }
}

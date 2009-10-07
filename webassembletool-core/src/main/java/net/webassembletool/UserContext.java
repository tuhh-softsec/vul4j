package net.webassembletool;

import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

/**
 * User context that can be used in the master application to define the user
 * id. This context will be transmitted to the provider applications.<br />
 * There is one instance of user context associated with each session.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class UserContext {
	private String user;
	private HttpContext httpContext;
	public HttpContext getHttpContext() {
		return httpContext;
	}

	private CookieStore cookieStore;

	public UserContext() {
		// Create a local instance of cookie store
		cookieStore = new BasicCookieStore();
		// Create local HTTP context
		httpContext = new BasicHttpContext();
		// Bind custom cookie store to the local context
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	}

	public CookieStore getCookieStore() {
		return cookieStore;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("User=");
		result.append(user);
		result.append(" Cookies={\n");
		for (Cookie cookie : cookieStore.getCookies()) {
			result.append("\t");
			if (cookie.isSecure())
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
		result.append("}");
		return result.toString();
	}
}

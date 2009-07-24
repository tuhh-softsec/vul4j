package net.webassembletool;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpState;

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
	private HttpState httpState = new HttpState();

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public HttpState getHttpState() {
		return httpState;
	}

	public void setHttpState(HttpState httpState) {
		this.httpState = httpState;
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

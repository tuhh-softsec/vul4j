package net.webassembletool.http;

import java.util.regex.Pattern;

import net.webassembletool.ResourceContext;
import net.webassembletool.UserContext;

import org.apache.http.cookie.Cookie;

public class RewriteUtils {

	private RewriteUtils() {
	}

	public final static String removeSessionId(String sessionId, String page) {
		String regexp = ";?jsessionid=" + Pattern.quote(sessionId);
		return page.replaceAll(regexp, "");
	}

	public final static String getSessionId(ResourceContext resourceContext) {
		String jsessionid = null;
		UserContext userContext = resourceContext.getUserContext(false);
		if (userContext != null) {
			for (Cookie cookie : userContext.getCookies()) {
				if ("jsessionid".equalsIgnoreCase(cookie.getName())) {
					jsessionid = cookie.getValue();
					break;
				}
			}
		}
		return jsessionid;
	}

}

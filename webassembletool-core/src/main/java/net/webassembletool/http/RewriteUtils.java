package net.webassembletool.http;

import java.util.List;
import java.util.regex.Pattern;

import net.webassembletool.ResourceContext;

import org.apache.http.cookie.Cookie;

public class RewriteUtils {
	public final static String removeSessionId(String sessionId, String page) {
		String regexp = ";?jsessionid=" + Pattern.quote(sessionId);
		return page.replaceAll(regexp, "");
	}

	public final static String getSessionId(ResourceContext resourceContext) {
		String jsessionid = null;
		if (resourceContext.getUserContext() != null) {
			List<Cookie> cookies = resourceContext.getUserContext()
					.getCookieStore().getCookies();
			for (Cookie cookie : cookies)
				if ("jsessionid".equalsIgnoreCase(cookie.getName())) {
					jsessionid = cookie.getValue();
					break;
				}
		}
		return jsessionid;
	}

}

package net.webassembletool.http;

public class RewriteUtils {
	public final static String removeSessionId(String sessionId, String page) {
		return page.replaceAll("[;]{0,1}jsessionid=([^?#&'\"]+)", "");
	}

}

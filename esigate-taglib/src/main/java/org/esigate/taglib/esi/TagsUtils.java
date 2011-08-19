package org.esigate.taglib.esi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

public class TagsUtils {

	public static String escape(String str) {
		return str.replaceAll("<%", "").replaceAll("%>", "");
	}

	public static String loadContent(String url) throws Exception {

		if (url == null || url.trim().equalsIgnoreCase("")) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		URL u = new URL(url);
		URLConnection yc = u.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(yc
				.getInputStream()));
		String inputLine;

		while ((inputLine = in.readLine()) != null) {
			sb.append(inputLine);
		}

		in.close();

		return sb.toString();
	}

	private static String processVar(String var, String arg,
			PageContext pageContext) {
		String res = "";

		if (var.indexOf("QUERY_STRING") != -1) {
			if (arg == null) {
				return res;
			}
			res = pageContext.getRequest().getParameter(arg);
		}

		else if (var.indexOf("HTTP_ACCEPT_LANGUAGE") != -1) {
			if (arg == null) {
				return res;
			}

			String langs = ((HttpServletRequest) pageContext.getRequest())
					.getHeader("Accept-Language");

			if (langs.indexOf(arg) == -1) {
				res = "false";
			} else {
				res = "true";
			}
		}

		else if (var.indexOf("HTTP_HOST") != -1) {
			res = ((HttpServletRequest) pageContext.getRequest())
					.getHeader("Host");
		}

		else if (var.indexOf("HTTP_REFERER") != -1) {
			res = ((HttpServletRequest) pageContext.getRequest())
					.getHeader("Referer");
		}

		else if (var.indexOf("HTTP_COOKIE") != -1) {
			if (arg == null) {
				return res;
			}
			Cookie[] cookies = ((HttpServletRequest) pageContext.getRequest())
					.getCookies();
			for (Cookie c : cookies) {
				if (c.getName().equals(arg)) {
					res = c.getValue();
					break;
				}
			}
		}

		else if (var.indexOf("HTTP_USER_AGENT") != -1) {
			if (arg == null) {
				return res;
			}
			String userAgent = ((HttpServletRequest) pageContext.getRequest())
					.getHeader("User-Agent").toLowerCase();
			if (arg.equals("os")) {
				if (userAgent.indexOf("unix") != -1) {
					res = "UNIX";
				} else if (userAgent.indexOf("mac") != -1) {
					res = "MAC";
				} else if (userAgent.indexOf("windows") != -1) {
					res = "WIN";
				} else {
					res = "OTHER";
				}
			} else if (arg.equals("browser")) {
				if (userAgent.indexOf("msie") != -1) {
					res = "MSIE";
				} else {
					res = "MOZILLA";
				}
			}

		}

		if (res == null) {
			return "";
		} else {
			return res;
		}
	}

	public static String processVars(String str, PageContext pageContext) {

		Pattern pattern = Pattern.compile("\\$\\([A-Z_]+(\\{.+\\})?\\)");
		Matcher matcher = pattern.matcher(str);
		String result = str;
		String var, arg, replacement;

		while (matcher.find()) {
			arg = null;
			var = matcher.group();

			// try to find argument
			try {
				arg = var.substring(var.indexOf('{') + 1, var.indexOf('}'));
			} catch (Exception e) {
			}

			replacement = processVar(var, arg, pageContext);
			result = matcher.replaceFirst(replacement);
			matcher = pattern.matcher(result);
		}

		return result;
	}
}

package org.esigate.resource;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.esigate.ResourceContext;
import org.esigate.api.HttpSession;
import org.esigate.http.RewriteUtils;

/**
 * Utility class to generate URL and path for Resources
 * 
 * @author Francois-Xavier Bonnet
 */
public class ResourceUtils {

	/**
	 * Private constructor
	 */
	private ResourceUtils() {

	}

	private final static String buildQueryString(ResourceContext target) {
		try {
			StringBuilder queryString = new StringBuilder();
			String charset = target.getOriginalRequest().getCharacterEncoding();
			if (charset == null) {
				charset = "ISO-8859-1";
			}
			String originalQuerystring = target.getOriginalRequest()
					.getQueryString();
			if (target.isProxy() && originalQuerystring != null) {
				// Remove jsessionid from request if it is present
				// As we are in a java application, the container might add
				// jsessionid to the querystring. We must not forward it to
				// included applications.
				String jsessionid = null;
				HttpSession session = target.getOriginalRequest().getSession(false);
				if (session != null) {
					jsessionid = session.getId();
				}
				if (jsessionid != null) {
					originalQuerystring = RewriteUtils.removeSessionId(
							jsessionid, originalQuerystring);
				}
				queryString.append(originalQuerystring);
			}
			if (target.getParameters() != null) {
				ResourceUtils.appendParameters(queryString, charset,
						target.getParameters());
			}
			return queryString.toString();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Check whether the given content-type value corresponds to "parsable"
	 * text.
	 * 
	 * @param contentType
	 *            the value of http header Content-Type
	 * @return true if this represents text or false if not
	 */
	public static boolean isTextContentType(String contentType, Collection<String> textContentTypes) {
		boolean isText = false;

		if (contentType != null) {
			String lowerContentType = contentType.toLowerCase();
			for (String textContentType : textContentTypes) {
				if (lowerContentType.startsWith(textContentType)) {
					isText = true;
					break;
				}
			}
		}
		return isText;
	}

	private static void appendParameters(StringBuilder buf, String charset,
			Map<String, String> params) throws UnsupportedEncodingException {
		for (Entry<String, String> param : params.entrySet()) {
			if (buf.length() > 0) {
				buf.append("&");
			}
			buf.append(URLEncoder.encode(param.getKey(), charset));
			buf.append("=");
			buf.append(URLEncoder.encode(param.getValue(), charset));
		}
	}

	private final static String concatUrl(String baseUrl, String relUrl) {
		StringBuilder url = new StringBuilder();
		if (baseUrl != null && relUrl != null
				&& (baseUrl.endsWith("/") || baseUrl.endsWith("\\"))
				&& relUrl.startsWith("/")) {
			url.append(baseUrl.substring(0, baseUrl.length() - 1)).append(
					relUrl);
		} else {
			url.append(baseUrl).append(relUrl);
		}
		return url.toString();
	}

	public final static String getHttpUrlWithQueryString(ResourceContext target) {
		String url = target.getRelUrl();
		if (!url.startsWith("http://") && !url.startsWith("https://")) {
			// Relative URL, we need to add the driver base url
			if (target.getBaseURL() != null) {
				url = concatUrl(target.getBaseURL(), url);
			}
		}
		String queryString = ResourceUtils.buildQueryString(target);
		if (queryString.length() == 0) {
			return url;
		} else {
			return url + "?" + queryString;
		}
	}

}

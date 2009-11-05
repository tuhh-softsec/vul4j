package net.webassembletool.resource;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import net.webassembletool.ResourceContext;

/**
 * Utility class to generate URL and path for Resources
 * 
 * @author Francois-Xavier Bonnet
 */
public class ResourceUtils {
	private final static String buildQueryString(ResourceContext target) {
		try {
			StringBuilder queryString = new StringBuilder();
			String charset = target.getOriginalRequest().getCharacterEncoding();
			if (charset == null)
				charset = "ISO-8859-1";
			String qs = target.getOriginalRequest().getQueryString();
			if (target.isProxy())
				if (qs != null && !qs.equals(""))
					// Should encode parameters here !
					queryString.append(qs).append("&");
			if (qs != null && qs.length() > 0)
				// queryString.append(qs).append("&");
				// remove jsessionid from request if it is present
				ResourceUtils.removeJsessionId(queryString);
			if (target.getParameters() != null)
				ResourceUtils.appendParameters(queryString, charset, target
						.getParameters());
			if (queryString.length() == 0)
				return "";
			return queryString.substring(0, queryString.length() - 1);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Check whether the given content-type value corresponds to "parsable"
	 * text. "Parsable" text is actually html/xhtml
	 * 
	 * @param contentType
	 *            the value of http header Content-Type
	 * @return true if this represents text or false if not
	 */
	public static boolean isTextContentType(String contentType) {
		if (contentType == null)
			return false;
		String lowerContentType = contentType.toLowerCase();
		if (lowerContentType.startsWith("text/html")
				|| lowerContentType.startsWith("application/xhtml+xml"))
			return true;
		else
			return false;
	}

	/**
	 * Removes <code>;jsessionid=value</code> sequence from the provided string
	 * source
	 */
	static void removeJsessionId(StringBuilder queryString) {
		int startIdx = queryString.indexOf("jsessionid=");
		if (startIdx == -1)
			return;
		else if (startIdx > 0 && queryString.charAt(startIdx - 1) == ';')
			startIdx--;
		int idx1 = queryString.indexOf("?", startIdx);
		int idx2 = queryString.indexOf("&", startIdx);
		int endIdx;
		if (idx1 == -1 && idx2 == -1)
			endIdx = queryString.length();
		else if (idx1 == -1)
			endIdx = idx2;
		else if (idx2 == -1)
			endIdx = idx1;
		else
			endIdx = Math.min(idx1, idx2);
		queryString.replace(startIdx, endIdx, "");
	}

	private static void appendParameters(StringBuilder buf, String charset,
			Map<String, String> params) throws UnsupportedEncodingException {
		for (Entry<String, String> temp : params.entrySet())
			buf.append(URLEncoder.encode(temp.getKey(), charset)).append("=")
					.append(URLEncoder.encode(temp.getValue(), charset))
					.append("&");
	}

	private final static String concatUrl(String baseUrl, String relUrl) {
		StringBuilder url = new StringBuilder();
		if (baseUrl != null && relUrl != null
				&& (baseUrl.endsWith("/") || baseUrl.endsWith("\\"))
				&& relUrl.startsWith("/"))
			url.append(baseUrl.substring(0, baseUrl.length() - 1)).append(
					relUrl);
		else
			url.append(baseUrl).append(relUrl);
		return url.toString();
	}

	private final static String encodeSpecialChars(String input) {
		// One might think that we should URL-encode everything, this could have
		// been done in getHttpUrlWithQueryString; Actually, we have encountred
		// cases of tools that do not like url encodings for all characters (in
		// some jahia servlets !)! Thus, only special chars not accepted by
		// javax.net.URI ar urlencoded for HTTPClient.
		return input.replaceAll(" ", "%20").replaceAll("\\|", "%7C");
	}

	public final static String getHttpUrlWithQueryString(ResourceContext target) {

		String url = concatUrl(target.getDriver().getBaseURL(), target
				.getRelUrl());
		String queryString = ResourceUtils.buildQueryString(target);
		if (queryString.length() == 0)
			return encodeSpecialChars(url);
		else
			return encodeSpecialChars(url + "?" + queryString);
	}

	public final static String getHttpUrl(ResourceContext target) {
		String url = ResourceUtils.concatUrl(target.getDriver().getBaseURL(),
				target.getRelUrl());
		return encodeSpecialChars(url);
	}

	public final static String getFileUrl(String localBase,
			ResourceContext target) {
		String url = ResourceUtils.concatUrl(localBase, target.getRelUrl());
		// Append queryString hashcode to supply different cache
		// filenames
		String queryString = ResourceUtils.buildQueryString(target);
		if ("".equals(queryString))
			return url;
		else
			return url + "_" + queryString.hashCode();
	}
}

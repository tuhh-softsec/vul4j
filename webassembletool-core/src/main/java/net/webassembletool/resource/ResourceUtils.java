package net.webassembletool.resource;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpSession;

import net.webassembletool.ResourceContext;
import net.webassembletool.http.RewriteUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class to generate URL and path for Resources
 * 
 * @author Francois-Xavier Bonnet
 */
public class ResourceUtils {

	private static final Log LOG = LogFactory.getLog(ResourceUtils.class);

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
				HttpSession session = target.getOriginalRequest().getSession(
						false);
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
				ResourceUtils.appendParameters(queryString, charset, target
						.getParameters());
			}
			return queryString.toString();
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
		if (contentType == null) {
			return false;
		}
		String lowerContentType = contentType.toLowerCase();
		return (lowerContentType.startsWith("text/html") || lowerContentType
				.startsWith("application/xhtml+xml"));
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

	private final static String concatUrlForFile(String baseUrl, String relUrl) {
		StringBuilder url = new StringBuilder();
		if (baseUrl != null) {
			if (StringUtils.endsWith(baseUrl, File.separator)) {
				url.append(baseUrl);
			} else {
				url.append(baseUrl).append(File.separator);
			}
		}
		if (relUrl != null) {
			String tmpRelUrl = StringUtils.replace(relUrl, "/", File.separator);
			String[] relUrlTokens = StringUtils
					.split(tmpRelUrl, File.separator);
			if (tmpRelUrl.startsWith(File.separator)) {
				url.append(File.separator);
			}
			for (int i = 0; i < relUrlTokens.length; i++) {
				String relUrlToken = relUrlTokens[i];
				url.append(cleanDirectoryFile(relUrlToken));
				if (i < relUrlTokens.length - 1) {
					url.append(File.separator);
				}
			}
			if (tmpRelUrl.endsWith(File.separator)) {
				url.append(File.separator);
			}
		}
		return url.toString();
	}

	private static String cleanDirectoryFile(String urlToken) {
		if (urlToken != null) {
			String answer = urlToken.replaceAll("[^A-Za-z0-9\\-\\.]", "");
			answer = StringUtils.abbreviate(answer, 20);
			return answer + "_" + urlToken.hashCode();
		}
		return "";
	}

	public final static String getHttpUrlWithQueryString(ResourceContext target) {
		String url = target.getRelUrl();
		if (!url.startsWith("http://") && !url.startsWith("https://")) {
			// Relative URL, we need to add the driver base url
			if (target.getDriver().getBaseURL() != null) {
				url = concatUrl(target.getDriver().getBaseURL(), url);
			}
		}
		String queryString = ResourceUtils.buildQueryString(target);
		if (queryString.length() == 0) {
			return url;
		} else {
			return url + "?" + queryString;
		}
	}

	public final static String getFileUrl(String localBase,
			ResourceContext target) {
		String answer = "";
		String url = target.getRelUrl();
		// Remove ":" and "//" for absolute urls
		url = url.replaceAll(":", "_");
		url = url.replaceAll("//", "__");
		url = ResourceUtils.concatUrlForFile(localBase, target.getRelUrl());
		// Append queryString hashcode to supply different cache
		// filenames
		String queryString = ResourceUtils.buildQueryString(target);
		if ("".equals(queryString)) {
			answer = url;
		} else {
			answer = url + "_" + queryString.hashCode();
		}
		LOG.debug("GetFileURL : >" + answer + "<");
		return answer;
	}
}

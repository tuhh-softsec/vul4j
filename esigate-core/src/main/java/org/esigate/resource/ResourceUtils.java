package org.esigate.resource;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.esigate.ResourceContext;
import org.esigate.api.HttpSession;
import org.esigate.file.FileOutput;
import org.esigate.file.FileResource;
import org.esigate.http.RewriteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to generate URL and path for Resources
 * 
 * @author Francois-Xavier Bonnet
 */
public class ResourceUtils {
	private static final Logger LOG = LoggerFactory.getLogger(ResourceUtils.class);

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

	private static boolean endsWith(String str, String suffix) {
		if (str == null || suffix == null) {
			return (str == null && suffix == null);
		}
		if (suffix.length() > str.length()) {
			return false;
		}
		int strOffset = str.length() - suffix.length();
		return str.regionMatches(false, strOffset, suffix, 0, suffix.length());
	}

	private final static String concatUrlForFile(String baseUrl, String relUrl) {
		StringBuilder url = new StringBuilder();
		if (baseUrl != null) {
			if (endsWith(baseUrl, File.separator)) {
				url.append(baseUrl);
			} else {
				url.append(baseUrl).append(File.separator);
			}
		}
		if (relUrl != null) {
			if (relUrl.endsWith("/")) {
				relUrl = relUrl.subSequence(0, relUrl.length() - 1) + "_";
			}
			if ("".equals(relUrl)) {
				relUrl = "__";
			}
			String tmpRelUrl = StringUtils.replace(relUrl, "/", File.separator);
			String[] relUrlTokens = StringUtils
					.split(tmpRelUrl, File.separator);
			if (tmpRelUrl.startsWith(File.separator)) {
				url.append(File.separator);
			}
			for (int i = 0; i < relUrlTokens.length; i++) {
				String relUrlToken = relUrlTokens[i];
				if (i < relUrlTokens.length - 1) {
					url.append("dir_");
				}
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
			String answer = urlToken.replaceAll("[^A-Za-z0-9\\-\\._]", "");
			answer = abbreviate(answer, 20);
			return answer + "_" + urlToken.hashCode();
		}
		return "";
	}

	private static String abbreviate(String str, int maxWidth) {
		int offset = 0;
		if (str == null) {
			return null;
		}
		if (maxWidth < 4) {
			throw new IllegalArgumentException(
					"Minimum abbreviation width is 4");
		}
		if (str.length() <= maxWidth) {
			return str;
		}

		if ((str.length()) < (maxWidth - 3)) {
			offset = str.length() - (maxWidth - 3);
		}
		if (offset <= 4) {
			return str.substring(0, maxWidth - 3) + "...";
		}
		if (maxWidth < 7) {
			throw new IllegalArgumentException(
					"Minimum abbreviation width with offset is 7");
		}
		if ((offset + (maxWidth - 3)) < str.length()) {
			return "..." + abbreviate(str.substring(offset), maxWidth - 3);
		}
		return "..." + str.substring(str.length() - (maxWidth - 3));
	}

	public final static String getHttpUrlWithQueryString(ResourceContext target) {
		String url = target.getRelUrl();
		if (!url.startsWith("http://") && !url.startsWith("https://")) {
			// Relative URL, we need to add the driver base url
			if (target.getDriver().getConfiguration().getBaseURL() != null) {
				url = concatUrl(target.getDriver().getConfiguration()
						.getBaseURL(), url);
			}
		}
		String queryString = ResourceUtils.buildQueryString(target);
		if (queryString.length() == 0) {
			return url;
		} else {
			return url + "?" + queryString;
		}
	}

	public static FileOutput createFileOutput(String localBase,
			ResourceContext target) {
		String baseFileName = getFileUrl(localBase, target);
		String headersFileName = baseFileName + ".headers";
		return new FileOutput(new File(baseFileName), new File(headersFileName));
	}

	public static FileResource createFileResource(String localBase,
			ResourceContext target) throws IOException {
		String baseFileName = getFileUrl(localBase, target);
		String headersFileName = baseFileName + ".headers";
		return new FileResource(new File(baseFileName), new File(
				headersFileName));
	}

	private final static String getFileUrl(String localBase,
			ResourceContext target) {
		String answer = "";
		String url = target.getRelUrl();
		// Remove ":" and "//" for absolute urls
		url = url.replaceAll(":", "_");
		url = url.replaceAll("//", "__");
		url = ResourceUtils.concatUrlForFile(localBase, url);
		// Append queryString hashcode to supply different cache
		// filenames
		String queryString = ResourceUtils.buildQueryString(target);
		if (queryString.length() == 0) {
			answer = url;
		} else {
			answer = url + "_" + queryString.hashCode();
		}
		LOG.debug("GetFileURL : >" + answer + "<");
		return answer;
	}
}

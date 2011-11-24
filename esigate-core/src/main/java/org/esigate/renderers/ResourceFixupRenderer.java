package org.esigate.renderers;

import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.esigate.Renderer;
import org.esigate.ResourceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This renderer fixes links to resources, images and pages in pages retrieved
 * by the WAT. This enables use of WAT without any special modifications of the
 * generated urls on the provider side.
 * 
 * All href and src attributes are processed, except javascript links.
 * 
 * @author Nicolas Richeton
 * 
 */
public class ResourceFixupRenderer implements Renderer {
	private static final Logger LOG = LoggerFactory
			.getLogger(ResourceFixupRenderer.class);

	public static final int ABSOLUTE = 0;
	public static final int RELATIVE = 1;
	public static final char SLASH = '/';
	private static final Pattern URL_PATTERN = Pattern.compile(
			"<([^>]+)(src|href|action|background)\\s*=\\s*('[^<']*'|\"[^<\"]*\")([^>]*)>",
			Pattern.CASE_INSENSITIVE);
	private String contextAdd = null;
	private String contextRemove = null;
	private String pagePath = null;
	private String server = null;
	private String baseUrl;
	private String replacementUrl;
	private final boolean fixRelativeUrls;
	private final int mode;

	/**
	 * Creates a renderer which fixes urls. The domain name and the url path are
	 * computed from the full url made of baseUrl + pageFullPath.
	 * 
	 * If mode is ABSOLUTE, all relative urls will be replaced by the full urls
	 * :
	 * <ul>
	 * <li>images/image.png is replaced by
	 * http://server/context/images/image.png</li>
	 * <li>/context/images/image.png is replaced by
	 * http://server/context/images/image.png</li>
	 * </ul>
	 * 
	 * If mode is RELATIVE, context will be added to relative urls :
	 * <ul>
	 * <li>images/image.png is replaced by /context/images/image.png</li>
	 * </ul>
	 * 
	 * @param visibleBaseUrl
	 *            Base url (same as configured in provider).
	 * @param pageFullPath
	 *            Page as used in tag lib or using API
	 * @param mode
	 *            ResourceFixupRenderer.ABSOLUTE or
	 *            ResourceFixupRenderer.RELATIVE
	 * @throws MalformedURLException
	 */
	public ResourceFixupRenderer(String baseUrl, String visibleBaseUrl,
			String pageFullPath, int mode) throws MalformedURLException {
		this(baseUrl, visibleBaseUrl, pageFullPath, mode, true);
	}

	/**
	 * Creates a renderer which fixes urls. The domain name and the url path are
	 * computed from the full url made of baseUrl + pageFullPath.
	 * 
	 * If mode is ABSOLUTE, all relative urls will be replaced by the full urls
	 * :
	 * <ul>
	 * <li>images/image.png is replaced by
	 * http://server/context/images/image.png</li>
	 * <li>/context/images/image.png is replaced by
	 * http://server/context/images/image.png</li>
	 * </ul>
	 * 
	 * If mode is RELATIVE, context will be added to relative urls :
	 * <ul>
	 * <li>images/image.png is replaced by /context/images/image.png</li>
	 * </ul>
	 * 
	 * @param visibleBaseUrl
	 *            Base url (same as configured in provider).
	 * @param pageFullPath
	 *            Page as used in tag lib or using API
	 * @param mode
	 *            ResourceFixupRenderer.ABSOLUTE or
	 *            ResourceFixupRenderer.RELATIVE
	 * @param fixRelativeUrls
	 *            defines whether relative URLs should be fixed
	 * @throws MalformedURLException
	 */
	public ResourceFixupRenderer(String baseUrl, String visibleBaseUrl,
			String pageFullPath, int mode, boolean fixRelativeUrls)
			throws MalformedURLException {
		this.mode = mode;
		this.fixRelativeUrls = fixRelativeUrls;

		if (visibleBaseUrl != null && visibleBaseUrl.length() != 0) {
			this.baseUrl = removeLeadingSlash(baseUrl);
			this.replacementUrl = removeLeadingSlash(visibleBaseUrl);
		} else {
			this.baseUrl = null;
			this.replacementUrl = null;
		}

		// Clean up input
		String cleanBaseUrl = baseUrl;
		if (visibleBaseUrl != null) {
			cleanBaseUrl = visibleBaseUrl;
		}
		cleanBaseUrl = removeLeadingSlash(cleanBaseUrl);

		String cleanPageFullPath = pageFullPath;
		if (cleanPageFullPath.charAt(0) == SLASH) {
			cleanPageFullPath = cleanPageFullPath.substring(1);
		}
		URL url = new URL(cleanBaseUrl + SLASH + cleanPageFullPath);

		// Split url
		server = url.getProtocol() + "://" + url.getHost();
		if (url.getPort() > -1) {
			server += ":" + url.getPort();
		}
		this.pagePath = url.getPath();
		if (pagePath != null) {
			int indexSlash = pagePath.lastIndexOf(SLASH);
			if (indexSlash >= 0) {
				pagePath = pagePath.substring(0, indexSlash);
			}
		}

		// Check if we are going to replace context
		if (baseUrl != null && !baseUrl.equals(visibleBaseUrl)) {
			contextRemove = new URL(baseUrl).getPath();
			contextAdd = new URL(visibleBaseUrl).getPath();
		}
	}

	private String removeLeadingSlash(String src) {
		int lastCharPosition = src.length() - 1;
		if (src.charAt(lastCharPosition) != SLASH) {
			return src;
		} else {
			return src.substring(0, lastCharPosition);
		}
	}

	/**
	 * Fix an url according to the chosen mode.
	 * 
	 * @param urlParam
	 *            the url to fix.
	 * @return the fixed url.
	 */
	private String fixUrl(String urlParam) {
		String url = urlParam;

		// Do not process 0-length urls
		if (url.length() == 0) {
			return url;
		}

		if (replacementUrl != null && url.startsWith(baseUrl)) {
			url = new StringBuffer(replacementUrl).append(
					url.substring(baseUrl.length())).toString();
			LOG.debug("fix absolute url: " + urlParam + " -> " + url);
			return url;
		}
		// Keep absolute and javascript urls untouched.
		if (url.startsWith("http://") || url.startsWith("https://")
				|| url.startsWith("#") || url.startsWith("javascript:")) {
			LOG.debug("keeping absolute url: " + url);
			return url;
		}

		// Add domain to context absolute urls
		if (url.charAt(0) == SLASH) {

			if (contextRemove != null && url.startsWith(contextRemove)) {
				url = url.substring(contextRemove.length());
				url = contextAdd + url;
			}

			if (mode == ABSOLUTE) {
				url = server + url;
			}
		} else if (fixRelativeUrls) {
			// Process relative urls
			if (mode == ABSOLUTE) {
				url = server + pagePath + SLASH + url;
			} else {
				url = pagePath + SLASH + url;
			}
		}

		LOG.debug("url fixed: " + urlParam + " -> " + url);
		return url;
	}

	/** {@inheritDoc} */
	public void render(ResourceContext requestContext, String src, Writer out)
			throws IOException {
		out.write(replace(src).toString());
	}

	/**
	 * Fix all resources urls and return the result.
	 * 
	 * @param input
	 *            The original charSequence to be processed.
	 * 
	 * 
	 * @return the result of this renderer.
	 */
	CharSequence replace(CharSequence input) {
		StringBuffer result = new StringBuffer(input.length());
		Matcher m = URL_PATTERN.matcher(input);
		while (m.find()) {
			LOG.trace("found match: " + m);
			// m.group(3) matches to the attribute value including surrounded quotes
			//String url = m.group(3);
			String url = input.subSequence(m.start(3) + 1, m.end(3) - 1).toString();
			url = fixUrl(url);
			url = url.replaceAll("\\$", "\\\\\\$"); // replace '$' -> '\$' as it denotes group
			StringBuffer tagReplacement = new StringBuffer("<$1$2=\"").append(url).append("\"");
			if (m.groupCount() > 3) {
				tagReplacement.append("$4");
			}
			tagReplacement.append('>');
			LOG.trace("replacement: " + tagReplacement);
			m.appendReplacement(result, tagReplacement.toString());
		}
		m.appendTail(result);

		return result;
	}

}

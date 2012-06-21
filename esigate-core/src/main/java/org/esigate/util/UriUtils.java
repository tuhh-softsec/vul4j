package org.esigate.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.utils.URIUtils;

public class UriUtils {
	private UriUtils() {
		// Do not instantiate
	}

	public static class InvalidUriException extends RuntimeException {
		private static final long serialVersionUID = 7013885420191182730L;

		private InvalidUriException(URISyntaxException cause) {
			super(cause);
		}

	}

	public static URI createURI(final String scheme, final String host, int port, final String path, final String query, final String fragment) {
		try {
			return URIUtils.createURI(scheme, host, port, path, query, fragment);
		} catch (URISyntaxException e) {
			throw new InvalidUriException(e);
		}
	}

	public static URI rewriteURI(final URI uri, final HttpHost target) {
		try {
			return URIUtils.rewriteURI(uri, target);
		} catch (URISyntaxException e) {
			throw new InvalidUriException(e);
		}
	}

	public static URI resolve(final URI baseURI, final String reference) {
		return URIUtils.resolve(baseURI, reference);
	}

	public static HttpHost extractHost(final URI uri) {
		return URIUtils.extractHost(uri);
	}

	public static HttpHost extractHost(final String uri) {
		return URIUtils.extractHost(URI.create(uri));
	}

	public static URI createUri(String uri) {
		return URI.create(uri);
	}

	public static URI resolve(String baseURI, String reference) {
		return resolve(URI.create(baseURI), reference);
	}

	public static Object rewriteURI(String uri, HttpHost targetHost) {
		return rewriteURI(URI.create(uri), targetHost);
	}

	public final static String removeSessionId(String sessionId, String page) {
		String regexp = ";?jsessionid=" + Pattern.quote(sessionId);
		return page.replaceAll(regexp, "");
	}

	/**
	 * Translates an URL by replacing the beginning like in the example passed as parameters
	 * 
	 * @param sourceUrl
	 * @param sourceContext
	 * @param targetContext
	 * @return The translated URL
	 * @throws MalformedURLException
	 */
	public final static String translateUrl(String sourceUrl, String sourceContext, String targetContext) throws MalformedURLException {
		// Find what has been replaced at the beginning of sourceContext to transform it to targetContext
		String commonSuffix = StringUtils.reverse(StringUtils.getCommonPrefix(StringUtils.reverse(sourceContext), StringUtils.reverse(targetContext)));
		String sourcePrefix = StringUtils.removeEnd(sourceContext, commonSuffix);
		String targetPrefix = StringUtils.removeEnd(targetContext, commonSuffix);
		// Make the source url absolute
		String absoluteSourceUrl;
		absoluteSourceUrl = resolve(sourceContext, sourceUrl).toString();
		if (absoluteSourceUrl.startsWith(sourcePrefix))
			return targetPrefix + StringUtils.removeStart(absoluteSourceUrl, sourcePrefix);
		else
			return absoluteSourceUrl;
	}

}

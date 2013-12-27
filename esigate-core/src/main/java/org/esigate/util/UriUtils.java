package org.esigate.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;

public final class UriUtils {
    private UriUtils() {
        // Do not instantiate
    }

    private static final class InvalidUriException extends RuntimeException {
        private static final long serialVersionUID = 7013885420191182730L;

        private InvalidUriException(URISyntaxException cause) {
            super(cause);
        }

    }

    public static String createURI(final String scheme, final String host, int port, final String path,
            final String query, final String fragment) {
        StringBuilder buffer = new StringBuilder();
        if (host != null) {
            if (scheme != null) {
                buffer.append(scheme);
                buffer.append("://");
            }
            buffer.append(host);
            if (port > 0) {
                buffer.append(':');
                buffer.append(port);
            }
        }
        if (path == null || !path.startsWith("/")) {
            buffer.append('/');
        }
        if (path != null) {
            buffer.append(path);
        }
        if (query != null) {
            buffer.append('?');
            buffer.append(query);
        }
        if (fragment != null) {
            buffer.append('#');
            buffer.append(fragment);
        }
        return buffer.toString();
    }

    public static String extractHostName(final String uri) {
        return extractHost(uri).getHostName();
    }

    public static HttpHost extractHost(final String uri) {
        return URIUtils.extractHost(createUri(uri));
    }

    private static URI createUri(String uri) {
        return URI.create(uri);
    }

    public static String rewriteURI(String uri, HttpHost targetHost) {
        try {
            return URIUtils.rewriteURI(createUri(uri), targetHost).toString();
        } catch (URISyntaxException e) {
            throw new InvalidUriException(e);
        }
    }

    public static String removeSessionId(String sessionId, String page) {
        String regexp = ";?jsessionid=" + Pattern.quote(sessionId);
        return page.replaceAll(regexp, "");
    }

    public static String extractScheme(String uri) {
        return extractHost(uri).getSchemeName();
    }

    /**
     * Translates an URL by replacing the beginning like in the example passed as parameters.
     * 
     * @param sourceUrl
     *            The url to translate
     * @param sourceContext
     *            The request which was sent to backend
     * @param targetContext
     *            The request which was received by esigate
     * @return The translated URL
     */
    public static String translateUrl(String sourceUrl, String sourceContext, String targetContext) {
        // Find what has been replaced at the beginning of sourceContext to
        // transform it to targetContext
        String commonSuffix = StringUtils.reverse(StringUtils.getCommonPrefix(StringUtils.reverse(sourceContext),
                StringUtils.reverse(targetContext)));
        String sourcePrefix = StringUtils.removeEnd(sourceContext, commonSuffix);
        HttpHost sourceHost = extractHost(sourcePrefix);
        String targetPrefix = StringUtils.removeEnd(targetContext, commonSuffix);
        // Make the source url absolute
        String absoluteSourceUrl;
        if (isAbsolute(sourceUrl)) {
            absoluteSourceUrl = sourceUrl;
        } else {
            absoluteSourceUrl = URIUtils.resolve(createUri(sourceContext), sourceUrl).toString();
        }

        // If url is on the same host than the request, do translation
        if (extractHost(absoluteSourceUrl).equals(sourceHost) && absoluteSourceUrl.startsWith(sourcePrefix)) {
            return targetPrefix + StringUtils.removeStart(absoluteSourceUrl, sourcePrefix);
        }

        // follow redirect url.
        return absoluteSourceUrl;
    }

    /**
     * Returns the raw query component of this URI. The query component of a URI, if defined, only contains legal URI
     * characters.
     * 
     * @param uri
     * @return The raw query component of this URI, or null if the query is undefined
     */
    public static String getRawQuery(String uri) {
        return createUri(uri).getRawQuery();
    }

    /**
     * Returns the decoded path component of this URI. The string returned by this method is equal to that returned by
     * the getRawPath method except that all sequences of escaped octets are decoded.
     * 
     * @param uri
     * @return The decoded path component of this URI, or null if the path is undefined
     */

    public static String getPath(String uri) {
        return createUri(uri).getPath();
    }

    /**
     * Returns a list of {@link NameValuePair NameValuePairs} as built from the URI's query portion. For example, a URI
     * of http://example.org/path/to/file?a=1&b=2&c=3 would return a list of three NameValuePairs, one for a=1, one for
     * b=2, and one for c=3. By convention, {@code '&'} and {@code ';'} are accepted as parameter separators.
     * <p>
     * This is typically useful while parsing an HTTP PUT.
     * 
     * This API is currently only used for testing.
     * 
     * @param uri
     *            URI to parse
     * @param charset
     *            Charset name to use while parsing the query
     * @return a list of {@link NameValuePair} as built from the URI's query portion.
     */
    public static List<NameValuePair> parse(final String uri, final String charset) {
        return URLEncodedUtils.parse(createUri(uri), charset);
    }

    public static boolean isAbsolute(String uri) {
        return (uri.startsWith("http://") || uri.startsWith("https://"));
    }

}

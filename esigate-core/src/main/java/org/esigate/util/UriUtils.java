package org.esigate.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.utils.URIUtils;

public final class UriUtils {
    private UriUtils() {
        // Do not instantiate
    }

    public static final class InvalidUriException extends RuntimeException {
        private static final long serialVersionUID = 7013885420191182730L;

        private InvalidUriException(URISyntaxException cause) {
            super(cause);
        }

    }

    public static URI createURI(final String scheme, final String host, int port, final String path,
            final String query, final String fragment) {
        try {
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
            return new URI(buffer.toString());
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

    public static String extractHostName(final URI uri) {
        return uri.getHost();
    }

    public static String extractHostName(final String uri) {
        return extractHostName(createUri(uri));
    }

    public static HttpHost extractHost(final String uri) {
        return URIUtils.extractHost(createUri(uri));
    }

    public static URI createUri(String uri) {
        return URI.create(uri);
    }

    public static URI resolve(String baseURI, String reference) {
        return resolve(createUri(baseURI), reference);
    }

    public static Object rewriteURI(String uri, HttpHost targetHost) {
        return rewriteURI(createUri(uri), targetHost);
    }

    public static String removeSessionId(String sessionId, String page) {
        String regexp = ";?jsessionid=" + Pattern.quote(sessionId);
        return page.replaceAll(regexp, "");
    }

    public static String extractScheme(String uri) {
        return createUri(uri).getScheme();
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
     * @throws MalformedURLException
     */
    public static String translateUrl(String sourceUrl, String sourceContext, String targetContext)
            throws MalformedURLException {
        // Find what has been replaced at the beginning of sourceContext to
        // transform it to targetContext
        String commonSuffix = StringUtils.reverse(StringUtils.getCommonPrefix(StringUtils.reverse(sourceContext),
                StringUtils.reverse(targetContext)));
        String sourcePrefix = StringUtils.removeEnd(sourceContext, commonSuffix);
        HttpHost sourceHost = extractHost(sourcePrefix);
        String targetPrefix = StringUtils.removeEnd(targetContext, commonSuffix);
        // Make the source url absolute
        String absoluteSourceUrl;
        absoluteSourceUrl = resolve(sourceContext, sourceUrl).toString();

        // If url is on the same host than the request, do translation
        if (extractHost(absoluteSourceUrl).equals(sourceHost) && absoluteSourceUrl.startsWith(sourcePrefix)) {
            return targetPrefix + StringUtils.removeStart(absoluteSourceUrl, sourcePrefix);
        }

        // follow redirect url.
        return absoluteSourceUrl;
    }

}

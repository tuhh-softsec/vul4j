/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
import org.esigate.Parameters;

public final class UriUtils {

    private static final String RESERVED_CHARACTERS = ":/?&=#%";
    private static final String[] CONVERSION_TABLE = new String[128];
    static {
        for (int i = 0; i < 128; i++) {
            char character = (char) i;
            String charString = Character.toString(character);
            if (RESERVED_CHARACTERS.indexOf(i) == -1) {
                charString = encode(charString);
            }
            CONVERSION_TABLE[i] = charString;
        }
    }

    private UriUtils() {
        // Do not instantiate
    }

    private static String encode(char character) {
        return Character.toString(character);
    }

    private static String encode(String charString) {
        try {
            return new URI(null, null, null, -1, charString, null, null).toASCIIString();
        } catch (URISyntaxException e) {
            throw new InvalidUriException(e);
        }
    }

    public static String encodeIllegalCharacters(String uri) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < uri.length(); i++) {
            char character = uri.charAt(i);
            int j = (int) character;
            if (j >= 128 || j < 0) {
                result.append(encode(character));
            } else {
                result.append(CONVERSION_TABLE[j]);
            }
        }
        return result.toString();
    }

    private static final class InvalidUriException extends RuntimeException {
        private static final long serialVersionUID = 7013885420191182730L;

        private InvalidUriException(URISyntaxException cause) {
            super(cause);
        }

    }

    public static String createURI(final String scheme, final String host, int port, final String path,
            final String query, final String fragment) {
        StringBuilder buffer = new StringBuilder(Parameters.SMALL_BUFFER_SIZE);
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
        return URIUtils.extractHost(createURI(uri));
    }

    /**
     * Creates an {@link URI} after escaping some special characters in order to tolerate some incorrect URI types
     * 
     * @param uriString
     * @return
     */
    public static URI createURI(String uriString) {
        uriString = encodeIllegalCharacters(uriString);
        return URI.create(uriString);
    }

    public static String rewriteURI(String uri, HttpHost targetHost) {
        try {
            return URIUtils.rewriteURI(createURI(uri), targetHost).toString();
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
        String commonSuffix =
                StringUtils.reverse(StringUtils.getCommonPrefix(StringUtils.reverse(sourceContext),
                        StringUtils.reverse(targetContext)));
        String sourcePrefix = StringUtils.removeEnd(sourceContext, commonSuffix);
        HttpHost sourceHost = extractHost(sourcePrefix);
        String targetPrefix = StringUtils.removeEnd(targetContext, commonSuffix);
        // Make the source url absolute
        String absoluteSourceUrl;
        if (isAbsolute(sourceUrl)) {
            absoluteSourceUrl = sourceUrl;
        } else {
            absoluteSourceUrl = URIUtils.resolve(createURI(sourceContext), sourceUrl).toString();
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
        return createURI(uri).getRawQuery();
    }

    /**
     * Returns the decoded path component of this URI. The string returned by this method is equal to that returned by
     * the getRawPath method except that all sequences of escaped octets are decoded.
     * 
     * @param uri
     *            the uri to retrieve the path from
     * @return The decoded path component of this URI, or null if the path is undefined
     */

    public static String getPath(String uri) {
        return createURI(uri).getPath();
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
        return URLEncodedUtils.parse(createURI(uri), charset);
    }

    public static boolean isAbsolute(String uri) {
        return (uri.startsWith("http://") || uri.startsWith("https://"));
    }

    /**
     * Concatenates 2 {@link URI} by taking the beginning of the first (up to the path) and the end of the other
     * (starting from the path). While concatenating, checks that there is no doubled "/" character between the path
     * fragments.
     * 
     * @param base
     *            the base uri
     * @param relPath
     *            the path to concatenate with the base uri
     * @return the concatenated uri
     */
    public static URI concatPath(URI base, String relPath) {
        String resultPath = base.getPath() + StringUtils.stripStart(relPath, "/");
        try {
            return new URI(base.getScheme(), base.getUserInfo(), base.getHost(), base.getPort(), resultPath, null, null);
        } catch (URISyntaxException e) {
            throw new InvalidUriException(e);
        }
    }

    public static URI removeServer(URI uri) {
        try {
            return new URI(null, null, null, -1, uri.getPath(), uri.getQuery(), uri.getFragment());
        } catch (URISyntaxException e) {
            throw new InvalidUriException(e);
        }

    }

    /**
     * Interpret the url relatively to the request url (may be relative). Due to a bug in {@link URI} class when using a
     * relUri containing only a query string, we cannot use directly the method provided by {@link URI} class.
     * 
     * @param relUri
     * @param base
     * @return the resolved {@link URI}
     */
    public static URI resolve(String relUri, URI base) {
        URI uri = createURI(relUri);
        if (uri.getScheme() == null && uri.getUserInfo() == null && uri.getHost() == null && uri.getPort() == -1
                && StringUtils.isEmpty(uri.getPath()) && uri.getQuery() != null) {
            try {
                return new URI(base.getScheme(), base.getUserInfo(), base.getHost(), base.getPort(), base.getPath(),
                        uri.getQuery(), uri.getFragment());
            } catch (URISyntaxException e) {
                throw new InvalidUriException(e);
            }
        } else {
            return base.resolve(uri);
        }
    }

    /**
     * Removes the query and fragment at the end of a URI.
     * 
     * @param uriString
     *            the original URI as a String
     * 
     * @return the URI without querystring nor fragment
     */
    public static String removeQuerystring(String uriString) {
        URI uri = createURI(uriString);
        try {
            return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), null, null)
                    .toASCIIString();
        } catch (URISyntaxException e) {
            throw new InvalidUriException(e);
        }
    }

}

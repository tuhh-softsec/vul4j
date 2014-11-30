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

/**
 * Utility class to manipulate {@link URI} represented as a {@link String} or as a {@link URI}.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public final class UriUtils {

    private static final int CONVERSION_TABLE_SIZE = 128;
    private static final String RESERVED_CHARACTERS = ":/?&=#%";
    private static final String[] CONVERSION_TABLE = new String[CONVERSION_TABLE_SIZE];
    static {
        for (int i = 0; i < CONVERSION_TABLE_SIZE; i++) {
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

    /**
     * Fixes common mistakes in URI by replacing all illegal characters by their encoded value.
     * 
     * @param uri
     *            the URI to fix
     * @return the fixed URI
     */
    public static String encodeIllegalCharacters(String uri) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < uri.length(); i++) {
            char character = uri.charAt(i);
            int j = (int) character;
            if (j >= CONVERSION_TABLE_SIZE || j < 0) {
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

    /**
     * Creates an URI as a String.
     * 
     * @param scheme
     *            the scheme
     * @param host
     *            the host
     * @param port
     *            the port
     * @param path
     *            the path
     * @param query
     *            the query
     * @param fragment
     *            the fragment
     * @return the uri
     */
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

    /**
     * Extracts the host name from a URI.
     * 
     * @param uri
     *            the uri
     * @return the host name
     */
    public static String extractHostName(final String uri) {
        return extractHost(uri).getHostName();
    }

    /**
     * Extracts the {@link HttpHost} from a URI.
     * 
     * @param uri
     *            the uri
     * @return the {@link HttpHost}
     */
    public static HttpHost extractHost(final String uri) {
        return URIUtils.extractHost(createURI(uri));
    }

    /**
     * Creates an {@link URI} after escaping some special characters in order to tolerate some incorrect URI types.
     * 
     * @param uri
     *            the URI as a {@link String}
     * @return the URI as a {@link URI} object
     */
    public static URI createURI(String uri) {
        uri = encodeIllegalCharacters(uri);
        return URI.create(uri);
    }

    /**
     * Replaces the scheme, host and port in a URI.
     * 
     * @param uri
     *            the URI
     * @param targetHost
     *            the target host
     * @return the rewritten URI
     */
    public static String rewriteURI(String uri, HttpHost targetHost) {
        try {
            return URIUtils.rewriteURI(createURI(uri), targetHost).toString();
        } catch (URISyntaxException e) {
            throw new InvalidUriException(e);
        }
    }

    /**
     * Removes the jsessionid that may have been added to a URI on a java application server.
     * 
     * @param sessionId
     *            the value of the sessionId that can also be found in a JSESSIONID cookie
     * @param page
     *            the html code of the page
     * @return the fixed html
     */
    public static String removeSessionId(String sessionId, String page) {
        String regexp = ";?jsessionid=" + Pattern.quote(sessionId);
        return page.replaceAll(regexp, "");
    }

    /**
     * Extracts the scheme of a URI.
     * 
     * @param uri
     *            the URI
     * @return the scheme
     */
    public static String extractScheme(String uri) {
        return extractHost(uri).getSchemeName();
    }

    /**
     * Returns the raw query component of this URI. The query component of a URI, if defined, only contains legal URI
     * characters.
     * 
     * @param uri
     *            the URI
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

    /**
     * Checks if a URI starts with a protocol.
     * 
     * @param uri
     *            the URI
     * @return true if the URI starts with "http://" or "https://"
     */
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
            URI result =
                    new URI(base.getScheme(), base.getUserInfo(), base.getHost(), base.getPort(), resultPath, null,
                            null);
            return result;
        } catch (URISyntaxException e) {
            throw new InvalidUriException(e);
        }
    }

    /**
     * Removes the server information frome a {@link URI}.
     * 
     * @param uri
     *            the {@link URI}
     * @return a new {@link URI} with no scheme, host and port
     */
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
     *            the relative URI
     * @param base
     *            the reference {@link URI}
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

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

package org.esigate.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.DeflateDecompressingEntity;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.cookie.DefaultCookieSpec;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;
import org.esigate.HttpErrorPage;
import org.esigate.events.EventManager;
import org.esigate.events.impl.ReadEntityEvent;
import org.esigate.util.UriUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for HttpClient's Request and Response objects.
 * 
 * @author Francois-Xavier Bonnet
 * @author Nicolas Richeton
 * 
 */
public final class HttpResponseUtils {
    private static final Logger LOG = LoggerFactory.getLogger(HttpResponseUtils.class);
    private static final int OUTPUT_BUFFER_SIZE = 4096;

    private HttpResponseUtils() {

    }

    /**
     * Check if httpResponse has an error status.
     * 
     * @param httpResponse
     *            tge {@link HttpResponse}
     * @return true if status code >= 400
     */
    public static boolean isError(HttpResponse httpResponse) {
        return httpResponse.getStatusLine().getStatusCode() >= HttpStatus.SC_BAD_REQUEST;
    }

    /**
     * Get the value of the first header matching "headerName".
     * 
     * @param headerName
     * @param httpResponse
     * @return value of the first header or null if it doesn't exist.
     */
    public static String getFirstHeader(String headerName, HttpResponse httpResponse) {
        Header header = httpResponse.getFirstHeader(headerName);
        if (header != null) {
            return header.getValue();
        }
        return null;
    }

    /**
     * Removes ";jsessionid=&lt;id&gt;" from the url, if the session id is also set in "httpResponse".
     * <p>
     * This methods first looks for the following header :
     * 
     * <pre>
     * Set-Cookie: JSESSIONID=
     * </pre>
     * 
     * If found and perfectly matches the jsessionid value in url, the complete jsessionid definition is removed from
     * the url.
     * 
     * @param uri
     *            original uri, may contains a jsessionid.
     * @param httpResponse
     *            the response which set the jsessionId
     * @return uri, without jsession
     */
    public static String removeSessionId(String uri, HttpResponse httpResponse) {
        CookieSpec cookieSpec = new DefaultCookieSpec();
        // Dummy origin, used only by CookieSpec for setting the domain for the
        // cookie but we don't need it
        CookieOrigin cookieOrigin = new CookieOrigin("dummy", Http.DEFAULT_HTTP_PORT, "/", false);
        Header[] responseHeaders = httpResponse.getHeaders("Set-cookie");
        String jsessionid = null;
        for (Header header : responseHeaders) {
            try {
                List<Cookie> cookies = cookieSpec.parse(header, cookieOrigin);
                for (Cookie cookie : cookies) {
                    if ("JSESSIONID".equalsIgnoreCase(cookie.getName())) {
                        jsessionid = cookie.getValue();
                    }
                    break;
                }
            } catch (MalformedCookieException ex) {
                LOG.warn("Malformed header: " + header.getName() + ": " + header.getValue());
            }
            if (jsessionid != null) {
                break;
            }
        }
        if (jsessionid == null) {
            return uri;
        }

        return UriUtils.removeSessionId(jsessionid, uri);

    }

    /**
     * Returns the response body as a string or the reason phrase if body is empty.
     * <p>
     * This methods is similar to EntityUtils#toString() internally, but uncompress the entity first if necessary.
     * <p>
     * This methods also holds an extension point, which can be used to guess the real encoding of the entity, if the
     * HTTP headers set a wrong encoding declaration.
     * 
     * @since 3.0
     * @since 4.1 - Event EventManager.EVENT_READ_ENTITY is fired when calling this method.
     * 
     * @param httpResponse
     * @param eventManager
     * @return The body as string or the reason phrase if body was empty.
     * @throws HttpErrorPage
     */
    public static String toString(HttpResponse httpResponse, EventManager eventManager) throws HttpErrorPage {
        HttpEntity httpEntity = httpResponse.getEntity();
        String result;
        if (httpEntity == null) {
            result = httpResponse.getStatusLine().getReasonPhrase();
        } else {
            // Unzip the stream if necessary
            Header contentEncoding = httpEntity.getContentEncoding();
            if (contentEncoding != null) {
                String contentEncodingValue = contentEncoding.getValue();
                if ("gzip".equalsIgnoreCase(contentEncodingValue) || "x-gzip".equalsIgnoreCase(contentEncodingValue)) {
                    httpEntity = new GzipDecompressingEntity(httpEntity);
                } else if ("deflate".equalsIgnoreCase(contentEncodingValue)) {
                    httpEntity = new DeflateDecompressingEntity(httpEntity);
                } else {
                    throw new UnsupportedContentEncodingException("Content-encoding \"" + contentEncoding
                            + "\" is not supported");
                }
            }

            try {
                byte[] rawEntityContent = EntityUtils.toByteArray(httpEntity);
                ContentType contentType;
                Charset charset;
                String mimeType;
                try {
                    contentType = ContentType.getOrDefault(httpEntity);
                    mimeType = contentType.getMimeType();
                    charset = contentType.getCharset();
                } catch (UnsupportedCharsetException ex) {
                    throw new UnsupportedEncodingException(ex.getMessage());
                }

                // Use default charset is no valid information found from HTTP
                // headers
                if (charset == null) {
                    charset = HTTP.DEF_CONTENT_CHARSET;
                }

                ReadEntityEvent event = new ReadEntityEvent(mimeType, charset, rawEntityContent);

                // Read using charset based on HTTP headers
                event.setEntityContent(new String(rawEntityContent, charset));

                // Allow extensions to detect document encoding
                if (eventManager != null) {
                    eventManager.fire(EventManager.EVENT_READ_ENTITY, event);
                }

                return event.getEntityContent();

            } catch (IOException e) {
                throw new HttpErrorPage(HttpErrorPage.generateHttpResponse(e));
            }
        }

        return removeSessionId(result, httpResponse);
    }

    public static ContentType getContentType(CloseableHttpResponse response) {
        HttpEntity entity = response.getEntity();
        if (entity == null) {
            return null;
        }
        return ContentType.get(entity);
    }

    public static String toString(CloseableHttpResponse response) throws HttpErrorPage {
        return toString(response, null);
    }

    /**
     * Copied from org.apache.http.entity.InputStreamEntity.writeTo(OutputStream) method but flushes the buffer after
     * each read in order to allow streaming and web sockets.
     * 
     * @param httpEntity
     *            The entity to copy to the OutputStream
     * @param outstream
     *            The OutputStream
     * @throws IOException
     *             If a problem occurs
     */
    public static void writeTo(final HttpEntity httpEntity, final OutputStream outstream) throws IOException {
        Args.notNull(outstream, "Output stream");
        try (InputStream instream = httpEntity.getContent()) {
            final byte[] buffer = new byte[OUTPUT_BUFFER_SIZE];
            int l;
            if (httpEntity.getContentLength() < 0) {
                // consume until EOF
                while ((l = instream.read(buffer)) != -1) {
                    outstream.write(buffer, 0, l);
                    outstream.flush();
                    LOG.debug("Flushed {} bytes of data");
                }
            } else {
                // consume no more than length
                long remaining = httpEntity.getContentLength();
                while (remaining > 0) {
                    l = instream.read(buffer, 0, (int) Math.min(OUTPUT_BUFFER_SIZE, remaining));
                    if (l == -1) {
                        break;
                    }
                    outstream.write(buffer, 0, l);
                    outstream.flush();
                    LOG.debug("Flushed {} bytes of data");
                    remaining -= l;
                }
            }
        }
    }

}

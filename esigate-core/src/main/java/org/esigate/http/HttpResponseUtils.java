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
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.protocol.HTTP;
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
public class HttpResponseUtils {
	private static final Logger LOG = LoggerFactory.getLogger(HttpResponseUtils.class);

	/**
	 * Check if httpResponse has an error status.
	 * 
	 * @param httpResponse
	 * @return true if status code >= 400
	 */
	public static boolean isError(HttpResponse httpResponse) {
		return httpResponse.getStatusLine().getStatusCode() >= 400;
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
		if (header != null)
			return header.getValue();
		return null;
	}

	/**
	 * Returns the charset of the entity of "httpResponse".
	 * 
	 * @param httpResponse
	 * @return charset as string or null if no charset defined.
	 */
	public static String getContentCharset(HttpResponse httpResponse) {
		ContentType contentType = ContentType.get(httpResponse.getEntity());
		if (contentType != null) {
			Charset charset = contentType.getCharset();
			if (charset != null)
				return charset.name();
		}
		return null;
	}

	/**
	 * Removes ";jsessionid=&lt;id&gt;" from the url, if the session id is also
	 * set in "httpResponse".
	 * <p>
	 * This methods first looks for the following header :
	 * 
	 * <pre>
	 * Set-Cookie: JSESSIONID=
	 * </pre>
	 * 
	 * If found and perfectly matches the jsessionid value in url, the complete
	 * jsessionid definition is removed from the url.
	 * 
	 * @param uri
	 *            original uri, may contains a jsessionid.
	 * @param httpResponse
	 *            the response which set the jsessionId
	 * @return uri, without jsession
	 */
	public static String removeSessionId(String uri, HttpResponse httpResponse) {
		CookieSpec cookieSpec = new BrowserCompatSpec();
		// Dummy origin, used only by CookieSpec for setting the domain for the
		// cookie but we don't need it
		CookieOrigin cookieOrigin = new CookieOrigin("dummy", 80, "/", false);
		Header[] responseHeaders = httpResponse.getHeaders("Set-cookie");
		String jsessionid = null;
		for (int i = 0; i < responseHeaders.length; i++) {
			Header header = responseHeaders[i];
			try {
				List<Cookie> cookies = cookieSpec.parse(header, cookieOrigin);
				for (Cookie cookie : cookies) {
					if ("JSESSIONID".equalsIgnoreCase(cookie.getName()))
						jsessionid = cookie.getValue();
					break;
				}
			} catch (MalformedCookieException ex) {
				LOG.warn("Malformed header: " + header.getName() + ": " + header.getValue());
			}
			if (jsessionid != null)
				break;
		}
		if (jsessionid == null) {
			return uri;
		}

		return UriUtils.removeSessionId(jsessionid, uri);

	}

	/**
	 * Returns the response body as a string or the reason phrase if body is
	 * empty.
	 * <p>
	 * This methods is similar to EntityUtils#toString() internally, but
	 * uncompress the entity first if necessary.
	 * <p>
	 * This methods also holds an extension point, which can be used to guess
	 * the real encoding of the entity, if the HTTP headers set a wrong encoding
	 * declaration.
	 * 
	 * @since 3.0
	 * @since 4.1 - Event EventManager.EVENT_READ_ENTITY is fired when calling
	 *        this method.
	 * 
	 * @param httpResponse
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
				ReadEntityEvent event = new ReadEntityEvent();
				event.rawEntityContent = EntityUtils.toByteArray(httpEntity);

				try {
					ContentType contentType = ContentType.getOrDefault(httpEntity);
					event.mimeType = contentType.getMimeType();
					event.charset = contentType.getCharset();
				} catch (UnsupportedCharsetException ex) {
					throw new UnsupportedEncodingException(ex.getMessage());
				}

				// Use default charset is no valid information found from HTTP
				// headers
				if (event.charset == null) {
					event.charset = HTTP.DEF_CONTENT_CHARSET;
				}

				// Read using charset based on HTTP headers
				event.entityContent = new String(event.rawEntityContent, event.charset);

				// Allow extensions to detect document encoding
				if (eventManager != null) {
					eventManager.fire(EventManager.EVENT_READ_ENTITY, event);
				}

				// Return entityContent
				result = event.entityContent;

			} catch (IOException e) {
				throw new HttpErrorPage(IOExceptionHandler.toHttpResponse(e));
			}
		}

		return removeSessionId(result, httpResponse);
	}

	/**
	 * This method is work in progress to externalize the provider.ttl option in
	 * an extension. May be moved and/or removed in 5.x Please do not use.
	 * 
	 * @deprecated since this is work in progress.
	 * @param httpResponse
	 * @return
	 */
	@Deprecated
	public static boolean isCacheableError(HttpResponse httpResponse) {
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		return statusCode == HttpStatus.SC_NOT_FOUND || statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR
				|| statusCode == HttpStatus.SC_SERVICE_UNAVAILABLE;
	}

	/**
	 * This method is work in progress to externalize the provider.ttl option in
	 * an extension. May be moved and/or removed in 5.x Please do not use.
	 * 
	 * @deprecated since this is work in progress.
	 * @param httpResponse
	 * @return
	 */
	@Deprecated
	public static boolean isCacheableSuccess(HttpResponse httpResponse) {
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		return statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_MOVED_PERMANENTLY
				|| statusCode == HttpStatus.SC_MOVED_TEMPORARILY || statusCode == HttpStatus.SC_NOT_MODIFIED;
	}

}

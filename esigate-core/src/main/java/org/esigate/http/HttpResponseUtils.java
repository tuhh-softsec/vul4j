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
import org.esigate.events.impl.EncodingEvent;
import org.esigate.util.UriUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponseUtils {
	private static final Logger LOG = LoggerFactory.getLogger(HttpResponseUtils.class);
 

 	
 	
	public static boolean isError(HttpResponse httpResponse) {
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		return statusCode != HttpStatus.SC_OK && statusCode != HttpStatus.SC_MOVED_TEMPORARILY && statusCode != HttpStatus.SC_MOVED_PERMANENTLY && statusCode != HttpStatus.SC_NOT_MODIFIED;
	}

	public static String getFirstHeader(String headerName, HttpResponse httpResponse) {
		Header header = httpResponse.getFirstHeader(headerName);
		if (header != null)
			return header.getValue();
		return null;
	}

	public static String getContentCharset(HttpResponse httpResponse) {
		ContentType contentType = ContentType.get(httpResponse.getEntity());
		if (contentType != null) {
			Charset charset = contentType.getCharset();
			if (charset != null)
				return charset.name();
		}
		return null;
	}

	public static void release(HttpResponse httpResponse) {
		HttpEntity httpEntity = httpResponse.getEntity();
		if (httpEntity != null)
			try {
				EntityUtils.consume(httpEntity);
			} catch (IOException e) {
				LOG.debug("Could not release request. Usualy this is due to a client abort.");
			}
	}

	public static String removeSessionId(String src, HttpResponse httpResponse) {
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
			return src;
		} else {
			return UriUtils.removeSessionId(jsessionid, src);
		}
	}

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
					throw new UnsupportedContentEncodingException("Content-encoding \"" + contentEncoding + "\" is not supported");
				}
			}
			
			try {
				EncodingEvent event = new EncodingEvent();
				event.rawEntityContent = EntityUtils.toByteArray(httpEntity);

				try {
					ContentType contentType = ContentType
							.getOrDefault(httpEntity);
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
					eventManager.fire(EventManager.EVENT_ENCODING, event);
				}

				// Return entityContent
				result = event.entityContent;

			} catch (IOException e) {
				throw new HttpErrorPage(IOExceptionHandler.toHttpResponse(e));
			}
		}
		
		return removeSessionId(result, httpResponse);
	}
}

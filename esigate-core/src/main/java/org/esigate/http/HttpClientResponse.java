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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.esigate.output.UnsupportedContentEncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author François-Xavier Bonnet
 * @author Nicolas Richeton
 * 
 */
public class HttpClientResponse {
	private final static Logger LOG = LoggerFactory
			.getLogger(HttpClientResponse.class);
	private HttpResponse httpResponse;
	private HttpEntity httpEntity;
	private int statusCode;
	private String statusText;
	private InputStream content;

	public HttpClientResponse(HttpHost httpHost, HttpRequest httpRequest,
			HttpClient httpClient, CookieStore cookieStore) {
		HttpContext httpContext = new BasicHttpContext();
		try {
			httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
			httpResponse = httpClient.execute(httpHost, httpRequest,
					httpContext);
			HttpRequest lastRequest = getLastRequest(httpRequest, httpContext);
			statusCode = httpResponse.getStatusLine().getStatusCode();
			statusText = httpResponse.getStatusLine().getReasonPhrase();
			httpEntity = httpResponse.getEntity();
			if (statusCode == HttpStatus.SC_OK
					|| statusCode == HttpStatus.SC_NOT_MODIFIED) {
				if (LOG.isInfoEnabled()) {
					LOG.info(lastRequest.getRequestLine() + " -> "
							+ httpResponse.getStatusLine());
				}

			} else if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY
					|| statusCode == HttpStatus.SC_MOVED_PERMANENTLY) {
				if (LOG.isInfoEnabled()) {
					LOG.info(lastRequest.getRequestLine() + " -> "
							+ httpResponse.getStatusLine() + " Location: "
							+ getHeader("Location"));
				}
			} else {
				if (LOG.isWarnEnabled()) {
					LOG.warn(lastRequest.getRequestLine() + " -> "
							+ httpResponse.getStatusLine());
				}
			}
		} catch (HttpHostConnectException e) {
			statusCode = HttpStatus.SC_BAD_GATEWAY;
			statusText = "Connection refused";
			logError(httpRequest, e);
		} catch (ConnectionPoolTimeoutException e) {
			statusCode = HttpStatus.SC_GATEWAY_TIMEOUT;
			statusText = "Connection pool timeout";
			logError(httpRequest, e);
		} catch (ConnectTimeoutException e) {
			statusCode = HttpStatus.SC_GATEWAY_TIMEOUT;
			statusText = "Connect timeout";
			logError(httpRequest, e);
		} catch (SocketTimeoutException e) {
			statusCode = HttpStatus.SC_GATEWAY_TIMEOUT;
			statusText = "Socket timeout";
			logError(httpRequest, e);
		} catch (IOException e) {
			statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
			statusText = "Error retrieving URL";
			logError(httpRequest, e);
		}
	}

	private static HttpRequest getLastRequest(HttpRequest httpRequest,
			HttpContext httpContext) {
		HttpRequest result = httpRequest;
		HttpRequest lastRedirectionRequest = RedirectStrategy
				.getLastRequest(httpContext);
		if (lastRedirectionRequest != null) {
			result = lastRedirectionRequest;
		}
		return result;
	}

	private void logError(HttpRequest request, Exception e) {
		LOG.error(request.getRequestLine() + " -> " + statusCode + " "
				+ statusText + " " + e.getClass().getName() + " "
				+ e.getMessage());
	}

	HttpClientResponse(HttpResponse httpResponse) {
		this.httpResponse = httpResponse;
		this.statusCode = httpResponse.getStatusLine().getStatusCode();
		this.statusText = httpResponse.getStatusLine().getReasonPhrase();
		this.httpEntity = httpResponse.getEntity();
	}

	public void finish() {
		if (content != null) {
			try {
				content.close();
			} catch (IOException e) {
				LOG.warn("Could not close response stream properly", e);
			}
		}
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getStatusText() {
		return statusText;
	}

	public String getContentCharset() {
		if (httpEntity == null) {
			return null;
		} else {
			return EntityUtils.getContentCharSet(httpEntity);
		}
	}

	public InputStream openStream() throws IllegalStateException, IOException {
		if (httpEntity == null) {
			content = new ByteArrayInputStream(
					statusText.getBytes("ISO-8859-1"));
		} else {
			// Unzip the stream if necessary
			String contentEncoding = getHeader(HttpHeaders.CONTENT_ENCODING);
			if (contentEncoding != null) {
				if (!"gzip".equalsIgnoreCase(contentEncoding)
						&& !"x-gzip".equalsIgnoreCase(contentEncoding)) {
					throw new UnsupportedContentEncodingException(
							"Content-encoding \"" + contentEncoding
									+ "\" is not supported");
				}
				GzipDecompressingEntity compressed = new GzipDecompressingEntity(
						httpEntity);
				content = compressed.getContent();
			} else {
				content = httpEntity.getContent();
			}
		}
		return content;
	}

	public String getHeader(String name) {
		if (httpResponse == null) {
			return null;
		}
		Header header = httpResponse.getFirstHeader(name);
		if (header != null) {
			return header.getValue();
		} else {
			return null;
		}
	}

	public Collection<String> getHeaderNames() {
		Set<String> result = new HashSet<String>();
		if (httpResponse != null) {
			Header[] headers = httpResponse.getAllHeaders();
			if (headers != null) {
				for (Header header : headers) {
					result.add(header.getName());
				}
			}
		}
		return result;
	}

	public String[] getHeaders(String name) {
		String[] result = null;
		if (httpResponse != null) {
			Header[] headers = httpResponse.getHeaders(name);
			if (headers != null) {
				result = new String[headers.length];
				for (int i = 0; i < headers.length; i++) {
					Header h = headers[i];
					result[i] = h.getValue();
				}
			}
		}
		return result;
	}

	@Override
	public String toString() {
		String result = statusCode + " " + statusText;
		if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY
				|| statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
			result += " -> " + getHeader("Location");
		}
		return result;
	}
}

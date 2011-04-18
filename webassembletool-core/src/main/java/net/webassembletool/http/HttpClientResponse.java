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
package net.webassembletool.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
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
	private final HttpResponse httpResponse;
	private final HttpEntity httpEntity;
	private final int statusCode;
	private final String statusText;
	private final String currentLocation;
	private final boolean error;

	public static HttpClientResponse create(HttpHost httpHost,
			HttpRequest basicHttpRequest, HttpClient httpClient,
			HttpContext httpContext) {
		try {
			HttpResponse httpResponse = httpClient.execute(httpHost,
					basicHttpRequest, httpContext);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			String currentLocation;
			if (statusCode == HttpServletResponse.SC_MOVED_PERMANENTLY
					|| statusCode == HttpServletResponse.SC_MOVED_TEMPORARILY) {
				currentLocation = httpResponse.getFirstHeader(
						HttpHeaders.LOCATION).getValue();
			} else {
				// Calculating the URL we may have been redirected to, as
				// automatic redirect following is activated
				currentLocation = buildLocation(httpContext);
			}
			LOG.debug(" -> create: " + statusCode + ";" + currentLocation);
			return new HttpClientResponse(httpResponse, currentLocation);
		} catch (HttpHostConnectException e) {
			return new HttpClientResponse(HttpServletResponse.SC_BAD_GATEWAY,
					"Connection refused");
		} catch (ConnectionPoolTimeoutException e) {
			return new HttpClientResponse(
					HttpServletResponse.SC_GATEWAY_TIMEOUT,
					"Connection pool timeout");
		} catch (ConnectTimeoutException e) {
			return new HttpClientResponse(
					HttpServletResponse.SC_GATEWAY_TIMEOUT, "Connect timeout");
		} catch (SocketTimeoutException e) {
			return new HttpClientResponse(
					HttpServletResponse.SC_GATEWAY_TIMEOUT, "Socket timeout");
		} catch (IOException e) {
			return new HttpClientResponse(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Error retrieving URL");
		}
	}

	static String buildLocation(HttpContext context) {
		StringBuffer buf = new StringBuffer();
		HttpHost host = (HttpHost) context
				.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
		buf.append(host.getSchemeName()).append("://")
				.append(host.getHostName());
		if (host.getPort() != -1) {
			buf.append(':').append(host.getPort());
		}
		HttpRequest finalRequest = (HttpRequest) context
				.getAttribute(ExecutionContext.HTTP_REQUEST);
		buf.append(finalRequest.getRequestLine().getUri());
		return buf.toString();
	}

	protected HttpClientResponse(int statusCode, String statusText) {
		this.httpResponse = null;
		this.httpEntity = null;
		this.currentLocation = null;
		this.error = true;
		this.statusCode = statusCode;
		this.statusText = statusText;
	}

	HttpClientResponse(HttpResponse httpResponse, String currentLocation) {
		this.httpResponse = httpResponse;
		this.statusCode = httpResponse.getStatusLine().getStatusCode();
		this.statusText = httpResponse.getStatusLine().getReasonPhrase();
		this.httpEntity = httpResponse.getEntity();
		this.currentLocation = currentLocation;
		this.error = false;
	}

	public void finish() {
		if (httpEntity != null) {
			try {
				httpEntity.consumeContent();
			} catch (IOException e) {
				LOG.warn("Could not close response stream properly", e);
			}
		}
	}

	public String getCurrentLocation() {
		return currentLocation;
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
		return httpEntity.getContent();
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

	public InputStream decompressStream() throws IllegalStateException,
			IOException {
		GzipDecompressingEntity compressed = new GzipDecompressingEntity(
				httpEntity);
		return compressed.getContent();
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
		if (statusCode == HttpServletResponse.SC_MOVED_PERMANENTLY
				|| statusCode == HttpServletResponse.SC_MOVED_TEMPORARILY) {
			result += " -> " + currentLocation;
		}
		return result;
	}

	public boolean isError() {
		return error;
	}
}

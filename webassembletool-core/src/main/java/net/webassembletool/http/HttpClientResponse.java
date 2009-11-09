package net.webassembletool.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class HttpClientResponse {
	private final static Log LOG = LogFactory.getLog(HttpClientResponse.class);
	private HttpResponse httpResponse;
	private HttpEntity httpEntity;
	private int statusCode;
	private String statusText;
	private String currentLocation;
	private Exception exception;
	private InputStream inputStream;

	public HttpClientResponse(HttpUriRequest httpUriRequest,
			HttpClient httpClient, HttpContext httpContext) {
		try {
			httpResponse = httpClient.execute(httpUriRequest, httpContext);
			statusCode = httpResponse.getStatusLine().getStatusCode();
			statusText = httpResponse.getStatusLine().getReasonPhrase();
			httpEntity = httpResponse.getEntity();
			if (statusCode == HttpServletResponse.SC_MOVED_PERMANENTLY
					|| statusCode == HttpServletResponse.SC_MOVED_TEMPORARILY) {
				currentLocation = httpResponse.getFirstHeader("location")
						.getValue();
			} else {
				// Calculating the URL we may have been redirected to, as
				// automatic redirect following is activated
				HttpUriRequest finalRequest = (HttpUriRequest) httpContext
						.getAttribute(ExecutionContext.HTTP_REQUEST);
				HttpHost host = (HttpHost) httpContext
						.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
				currentLocation = host.getSchemeName() + "://";
				currentLocation += host.getHostName();
				if (host.getPort() != -1)
					currentLocation += ":" + host.getPort();
				currentLocation += finalRequest.getURI().normalize().toString();
			}
		} catch (ConnectionPoolTimeoutException e) {
			exception = e;
			statusCode = HttpServletResponse.SC_GATEWAY_TIMEOUT;
			statusText = "Connect timeout retrieving URL: "
					+ httpUriRequest.getURI();
			LOG.warn(
					"Connect timeout retrieving URL, connection pool exhausted: "
							+ httpUriRequest.getURI(), e);
		} catch (ConnectTimeoutException e) {
			exception = e;
			statusCode = HttpServletResponse.SC_GATEWAY_TIMEOUT;
			statusText = "Connect timeout retrieving URL: "
					+ httpUriRequest.getURI();
			LOG.warn("Connect timeout retrieving URL: "
					+ httpUriRequest.getURI());
		} catch (SocketTimeoutException e) {
			exception = e;
			statusCode = HttpServletResponse.SC_GATEWAY_TIMEOUT;
			statusText = "Socket timeout retrieving URL: "
					+ httpUriRequest.getURI();
			LOG.warn("Socket timeout retrieving URL: "
					+ httpUriRequest.getURI());
		} catch (IOException e) {
			exception = e;
			statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
			statusText = "Error retrieving URL: " + httpUriRequest.getURI();
			LOG.error("Error retrieving URL: " + httpUriRequest.getURI(), e);
		}
	}

	public void finish() {
		if (httpEntity != null)
			try {
				httpEntity.consumeContent();
			} catch (IOException e) {
				LOG.warn("Could not close response stream properly", e);
			}
	}

	public Exception getException() {
		return exception;
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
		if (httpEntity == null)
			return null;
		else
			return EntityUtils.getContentCharSet(httpEntity);
	}

	public InputStream openStream() throws IllegalStateException, IOException {
		inputStream = httpEntity.getContent();
		return inputStream;
	}

	public String getHeader(String name) {
		if (httpResponse == null)
			return null;
		Header header = httpResponse.getFirstHeader(name);
		if (header != null)
			return header.getValue();
		else
			return null;
	}

	@Override
	public String toString() {
		String result = statusCode + " " + statusText;
		if (statusCode == HttpServletResponse.SC_MOVED_PERMANENTLY
				|| statusCode == HttpServletResponse.SC_MOVED_TEMPORARILY)
			result += " -> " + currentLocation;
		return result;
	}
}

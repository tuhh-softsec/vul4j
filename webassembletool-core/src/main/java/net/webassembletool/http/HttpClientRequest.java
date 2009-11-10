package net.webassembletool.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HttpContext;

public class HttpClientRequest {
	private final static Log LOG = LogFactory.getLog(HttpClientRequest.class);
	private String uri;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	private HttpServletRequest originalRequest;
	private boolean proxy;
	private HttpUriRequest httpUriRequest;
	private HashMap<String, String> headers;
	private boolean preserveHost = false;

	public HttpClientRequest(String uri, HttpServletRequest originalRequest,
			boolean proxy, boolean preserveHost) {
		this.uri = uri;
		this.originalRequest = originalRequest;
		this.proxy = proxy;
		this.preserveHost = preserveHost;
	}

	public HttpClientResponse execute(HttpClient httpClient,
			HttpContext httpContext) throws IOException {
		buildHttpMethod();
		HttpClientResponse result;
		HttpHost httpHost = null;
		if (preserveHost) {
			httpHost = new HttpHost(originalRequest.getServerName(),
					originalRequest.getServerPort(), originalRequest
							.getScheme());
			httpUriRequest.getParams().setParameter(ClientPNames.VIRTUAL_HOST,
					httpHost);
		}
		result = new HttpClientResponse(httpUriRequest, httpClient, httpContext);
		LOG.debug(toString() + " -> " + result.toString());
		return result;
	}

	/**
	 * This method copies the body of the request without modification.
	 * 
	 * @throws IOException
	 *             if problem getting the request
	 */
	private void copyEntity(HttpServletRequest req,
			HttpEntityEnclosingRequest httpEntityEnclosingRequest)
			throws IOException {
		long contentLengthLong = -1;
		String contentLength = req.getHeader("Content-length");
		if (contentLength != null)
			contentLengthLong = Long.parseLong(contentLength);
		InputStreamEntity inputStreamEntity = new InputStreamEntity(req
				.getInputStream(), contentLengthLong);
		String contentType = req.getContentType();
		if (contentType != null)
			inputStreamEntity.setContentType(contentType);
		String contentEncoding = req.getHeader("Content-Encoding");
		if (contentEncoding != null)
			inputStreamEntity.setContentEncoding(contentEncoding);
		httpEntityEnclosingRequest.setEntity(inputStreamEntity);
	}

	private void buildHttpMethod() throws IOException {
		String method;
		if (proxy)
			method = originalRequest.getMethod();
		else
			method = "GET";
		if ("GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method)
				|| "OPTIONS".equalsIgnoreCase(method)
				|| "TRACE".equalsIgnoreCase(method)
				|| "DELETE".equalsIgnoreCase(method)) {
			httpUriRequest = new GenericHttpRequest(method, uri);
		} else if ("POST".equalsIgnoreCase(method)
				|| "PUT".equalsIgnoreCase(method)
				|| "PROPFIND".equalsIgnoreCase(method)
				|| "PROPPATCH".equalsIgnoreCase(method)
				|| "MKCOL".equalsIgnoreCase(method)
				|| "COPY".equalsIgnoreCase(method)
				|| "MOVE".equalsIgnoreCase(method)
				|| "LOCK".equalsIgnoreCase(method)
				|| "UNLOCK".equalsIgnoreCase(method)) {
			GenericHttpEntityEnclosingRequest genericHttpEntityEnclosingRequest = new GenericHttpEntityEnclosingRequest(
					method, uri);
			copyEntity(originalRequest, genericHttpEntityEnclosingRequest);
			httpUriRequest = genericHttpEntityEnclosingRequest;
		} else
			throw new UnsupportedHttpMethodException(method + " " + uri);
		if (proxy)
			httpUriRequest.getParams().setParameter(
					ClientPNames.HANDLE_REDIRECTS, false);
		else
			httpUriRequest.getParams().setParameter(
					ClientPNames.HANDLE_REDIRECTS, true);
		// We use the same user-agent and accept headers that the one sent by
		// the browser as some web sites generate different pages and scripts
		// depending on the browser
		String userAgent = originalRequest.getHeader("User-Agent");
		if (userAgent != null)
			httpUriRequest.getParams().setParameter(
					CoreProtocolPNames.USER_AGENT, userAgent);
		copyRequestHeader("Accept");
		copyRequestHeader("Accept-Encoding");
		copyRequestHeader("Accept-Language");
		copyRequestHeader("Accept-Charset");
		copyRequestHeader("Cache-control");
		copyRequestHeader("Pragma");
		if (headers != null) {
			for (Iterator<Entry<String, String>> iterator = headers.entrySet()
					.iterator(); iterator.hasNext();) {
				Entry<String, String> entry = iterator.next();
				httpUriRequest.addHeader(entry.getKey(), entry.getValue());
			}
		}
	}

	private void copyRequestHeader(String name) {
		if (originalRequest != null) {
			String value = originalRequest.getHeader(name);
			if (value != null)
				httpUriRequest.addHeader(name, value);
		}
	}

	public void addHeader(String name, String value) {
		if (headers == null)
			headers = new HashMap<String, String>();
		headers.put(name, value);
	}

	@Override
	public String toString() {
		return httpUriRequest.getMethod() + " " + uri;
	}

	public boolean isProxy() {
		return proxy;
	}

	public void setProxy(boolean proxy) {
		this.proxy = proxy;
	}

	public boolean isPreserveHost() {
		return preserveHost;
	}

	public void setPreserveHost(boolean preserveHost) {
		this.preserveHost = preserveHost;
	}

}

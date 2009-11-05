package net.webassembletool.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.webassembletool.ResourceContext;
import net.webassembletool.authentication.AuthenticationHandler;
import net.webassembletool.output.Output;
import net.webassembletool.resource.Resource;
import net.webassembletool.resource.ResourceUtils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.cookie.Cookie;
import org.apache.http.protocol.HttpContext;

/**
 * Resource implementation pointing to a resource on an external application.
 * 
 * @author Francois-Xavier Bonnet
 */
public class HttpResource extends Resource {
	private final static Log LOG = LogFactory.getLog(HttpResource.class);
	private HttpClientResponse httpClientResponse;
	private final ResourceContext target;
	private String url;

	public HttpResource(HttpClient httpClient, ResourceContext resourceContext)
			throws IOException {
		this.target = resourceContext;
		this.url = ResourceUtils.getHttpUrlWithQueryString(resourceContext);
		// Retrieve session and other cookies
		HttpContext httpContext = null;
		if (resourceContext.getUserContext() != null)
			httpContext = resourceContext.getUserContext().getHttpContext();
		HttpServletRequest originalRequest = resourceContext
				.getOriginalRequest();
		AuthenticationHandler authenticationHandler = resourceContext
				.getDriver().getAuthenticationHandler();
		boolean proxy = resourceContext.isProxy();
		boolean preserveHost = resourceContext.isPreserveHost();
		HttpClientRequest httpClientRequest = new HttpClientRequest(url,
				originalRequest, proxy, preserveHost);
		authenticationHandler.preRequest(httpClientRequest, resourceContext);
		httpClientResponse = httpClientRequest.execute(httpClient, httpContext);
		// Authentication challenge
		while (authenticationHandler.needsNewRequest(httpClientResponse,
				resourceContext)) {
			// We must first ensure that the connection is always released, if
			// not the connection manager's pool may be exhausted soon !
			httpClientResponse.finish();
			httpClientRequest = new HttpClientRequest(url, originalRequest,
					proxy, preserveHost);
			authenticationHandler
					.preRequest(httpClientRequest, resourceContext);
			httpClientResponse = httpClientRequest.execute(httpClient,
					httpContext);
		}
		if (isError())
			HttpResource.LOG.warn("Problem retrieving URL: " + url + ": "
					+ httpClientResponse.getStatusCode() + " "
					+ httpClientResponse.getStatusText());
	}

	private void copyHeader(Output dest, String name) {
		String value = httpClientResponse.getHeader(name);
		if (value != null)
			dest.addHeader(name, value);
	}

	@Override
	public void render(Output output) throws IOException {
		output.setStatus(httpClientResponse.getStatusCode(), httpClientResponse
				.getStatusText());
		copyHeader(output, "Date");
		copyHeader(output, "Content-Type");
		copyHeader(output, "Content-Length");
		copyHeader(output, "Last-Modified");
		copyHeader(output, "ETag");
		copyHeader(output, "Expires");
		copyHeader(output, "Cache-control");
		String location = httpClientResponse.getHeader("Location");
		if (location != null) {
			// In case of a redirect, we need to rewrite the location header to
			// match
			// provider application and remove any jsessionid in the URL
			location = rewriteLocation(location);
			location = removeSessionId(location);
			output.addHeader("Location", location);
		}
		String charset = httpClientResponse.getContentCharset();
		if (charset != null)
			output.setCharsetName(charset);
		try {
			output.open();
			if (httpClientResponse.getException() != null)
				output.write(httpClientResponse.getStatusText());
			else {
				removeSessionId(httpClientResponse.openStream(), output);
			}
		} finally {
			output.close();
		}
	}

	private String rewriteLocation(String location) {
		// Location header rewriting
		HttpServletRequest request = target.getOriginalRequest();
		String originalBase = request.getScheme() + "://"
				+ request.getServerName() + ":" + request.getServerPort()
				+ request.getContextPath() + request.getServletPath();
		if (request.getPathInfo() != null)
			originalBase += request.getPathInfo();
		int pos = originalBase.indexOf(target.getRelUrl());
		originalBase = originalBase.substring(0, pos + 1);
		return location.replaceFirst(target.getDriver().getBaseURL(),
				originalBase);

	}

	private void removeSessionId(InputStream inputStream, Output output)
			throws IOException {
		String jsessionid = null;
		if (target.getUserContext() != null) {
			List<Cookie> cookies = target.getUserContext().getCookieStore()
					.getCookies();
			for (Cookie cookie : cookies)
				if ("jsessionid".equalsIgnoreCase(cookie.getName())) {
					jsessionid = cookie.getValue();
					break;
				}
		}
		boolean textContentType = ResourceUtils
				.isTextContentType(httpClientResponse.getHeader("Content-Type"));
		if (jsessionid == null || !textContentType) {
			IOUtils.copy(inputStream, output.getOutputStream());
		} else {
			String charset = httpClientResponse.getContentCharset();
			String content = IOUtils.toString(inputStream, charset);
			content = removeSessionId(content);
			if (output.getHeader("Content-length") != null)
				output.setHeader("Content-length", Integer.toString(content
						.length()));
			OutputStream outputStream = output.getOutputStream();
			IOUtils.write(content, outputStream, charset);
		}
		inputStream.close();
	}

	private String removeSessionId(String src) {
		return src.replaceAll("[;]{0,1}jsessionid=([^?#&'\"]+)", "");
	}

	@Override
	public void release() {
		httpClientResponse.finish();
	}

	@Override
	public int getStatusCode() {
		return httpClientResponse.getStatusCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(target.getOriginalRequest().getMethod());
		result.append(" ");
		result.append(ResourceUtils.getHttpUrlWithQueryString(target));
		result.append("\n");
		if (target.getUserContext() != null)
			result.append(target.getUserContext().toString());
		return result.toString();
	}

}

package net.webassembletool.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.webassembletool.RequestContext;
import net.webassembletool.UserContext;
import net.webassembletool.output.Output;
import net.webassembletool.output.StringOutput;
import net.webassembletool.resource.Resource;
import net.webassembletool.resource.ResourceUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jasig.cas.client.authentication.AttributePrincipal;

/**
 * Resource implementation pointing to a resource on an external application.
 * 
 * @author Francois-Xavier Bonnet
 */
public class HttpResource extends Resource {
	private final static Log LOG = LogFactory.getLog(HttpResource.class);
	private HttpUriRequest httpUriRequest;
	private HttpResponse httpResponse;
	private HttpEntity httpEntity;
	private int statusCode;
	private String statusText;
	private final RequestContext target;
	private String url;
	private Exception exception;

	/**
	 * This builds a postMethod forwarding content and content type without
	 * modification.
	 * 
	 * @throws IOException
	 *             if problem getting the request
	 */
	private void buildPostMethod() throws IOException {
		url = ResourceUtils.getHttpUrlWithQueryString(target);
		HttpPost postMethod = new HttpPost(url);
		HttpServletRequest req = target.getOriginalRequest();
		long contentLengthLong = -1;
		String contentLength = req.getHeader("Content-length");
		if (contentLength!=null)
			contentLengthLong = Long.parseLong(contentLength);
		InputStreamEntity inputStreamEntity = new InputStreamEntity(req
				.getInputStream(), contentLengthLong);
		String contentType = req.getContentType();
		if (contentType != null)
			inputStreamEntity.setContentType(contentType);
		String contentEncoding = req.getHeader("Content-Encoding");
		if (contentEncoding != null)
			inputStreamEntity.setContentEncoding(contentEncoding);
		postMethod.setEntity(inputStreamEntity);
		httpUriRequest = postMethod;
	}

	private void buildGetMethod() {
		url = ResourceUtils.getHttpUrlWithQueryString(target);
		httpUriRequest = new HttpGet(url);
	}

	private void addCasAuthentication(String location) {
		Principal principal = target.getOriginalRequest().getUserPrincipal();
		if (principal != null && principal instanceof AttributePrincipal) {
			AttributePrincipal casPrincipal = (AttributePrincipal) principal;
			HttpResource.LOG.debug("User logged in CAS as: "
					+ casPrincipal.getName());
			String service = location;
			service = service.substring(service.indexOf("service=")
					+ "service=".length());
			int ampersandPosition = service.indexOf('&');
			if (ampersandPosition > 0)
				service = service.substring(0, ampersandPosition);
			try {
				service = URLDecoder.decode(service, "ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				// Should not happen
			}
			String casProxyTicket = casPrincipal.getProxyTicketFor(service);
			HttpResource.LOG.debug("Proxy ticket retrieved: "
					+ casPrincipal.getName() + " for service: " + service
					+ " : " + casProxyTicket);
			if (casProxyTicket != null)
				target.getParameters().put("ticket", casProxyTicket);
		}
	}

	private void buildHttpMethod() throws IOException {
		if ("GET".equalsIgnoreCase(target.getMethod()) || !target.isProxyMode())
			buildGetMethod();
		else if ("POST".equalsIgnoreCase(target.getMethod())) {
			buildPostMethod();
		} else
			throw new UnsupportedHttpMethodException(target.getMethod() + " "
					+ ResourceUtils.getHttpUrl(target));
		if (target.isProxyMode())
			httpUriRequest.getParams().setParameter(
					ClientPNames.HANDLE_REDIRECTS, false);
		else
			httpUriRequest.getParams().setParameter(
					ClientPNames.HANDLE_REDIRECTS, true);
		// We use the same user-agent and accept headers that the one sent by
		// the browser as some web sites generate different pages and scripts
		// depending on the browser
		String userAgent = target.getOriginalRequest().getHeader("User-Agent");
		if (userAgent != null)
			httpUriRequest.getParams().setParameter(
					CoreProtocolPNames.USER_AGENT, userAgent);
		copyRequestHeaders("Accept", "Accept-Encoding", "Accept-Language",
				"Accept-Charset", "Cache-control", "Pragma");
		UserContext userContext = target.getUserContext();
		if (userContext != null && userContext.getUser() != null)
			httpUriRequest.addHeader("X_REMOTE_USER", userContext.getUser());
	}

	public HttpResource(HttpClient httpClient, RequestContext target) {
		this.target = target;
		// Retrieve session and other cookies
		HttpContext httpContext = null;
		if (target.getUserContext() != null)
			httpContext = target.getUserContext().getHttpContext();
		try {
			buildHttpMethod();
			if (HttpResource.LOG.isDebugEnabled())
				HttpResource.LOG.debug(toString());
			httpResponse = httpClient.execute(httpUriRequest, httpContext);
			statusCode = httpResponse.getStatusLine().getStatusCode();
			statusText = httpResponse.getStatusLine().getReasonPhrase();
			httpEntity = httpResponse.getEntity();
			// CAS support
			String currentLocation = null;
			if (!target.isProxyMode()) {
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

			} else if (statusCode == HttpServletResponse.SC_MOVED_PERMANENTLY
					|| statusCode == HttpServletResponse.SC_MOVED_TEMPORARILY)
				currentLocation = httpResponse.getFirstHeader("location")
						.getValue();
			if (currentLocation != null && currentLocation.contains("/login")) {
				addCasAuthentication(currentLocation);
				// We must ensure that the connection is always released, if
				// not the connection manager's pool may be exhausted soon !
				if (httpEntity != null)
					httpEntity.consumeContent();
				buildHttpMethod();
				HttpResource.LOG.debug(toString());
				httpResponse = httpClient.execute(httpUriRequest, httpContext);
				statusCode = httpResponse.getStatusLine().getStatusCode();
				statusText = httpResponse.getStatusLine().getReasonPhrase();
				httpEntity = httpResponse.getEntity();
			}
			if (isError())
				HttpResource.LOG.warn("Problem retrieving URL: " + url + ": "
						+ statusCode + " " + statusText);
		} catch (ConnectionPoolTimeoutException e) {
			exception = e;
			statusCode = HttpServletResponse.SC_GATEWAY_TIMEOUT;
			statusText = "Connect timeout retrieving URL: " + url;
			HttpResource.LOG.warn(
					"Connect timeout retrieving URL, connection pool exhausted: "
							+ url, e);
		} catch (ConnectTimeoutException e) {
			exception = e;
			statusCode = HttpServletResponse.SC_GATEWAY_TIMEOUT;
			statusText = "Connect timeout retrieving URL: " + url;
			HttpResource.LOG.warn("Connect timeout retrieving URL: " + url);
		} catch (SocketTimeoutException e) {
			exception = e;
			statusCode = HttpServletResponse.SC_GATEWAY_TIMEOUT;
			statusText = "Socket timeout retrieving URL: " + url;
			HttpResource.LOG.warn("Socket timeout retrieving URL: " + url);
		} catch (IOException e) {
			exception = e;
			statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
			statusText = "Error retrieving URL: " + url;
			HttpResource.LOG.error("Error retrieving URL: " + url, e);
		}
	}

	private void copyHeaders(Output dest, String... headers) {
		if (httpResponse != null) {
			for (String name : headers) {
				Header header = httpResponse.getFirstHeader(name);
				if (header != null)
					dest.addHeader(header.getName(), header.getValue());
			}
		}
	}

	private void copyRequestHeaders(String... headers) {
		HttpServletRequest request = target.getOriginalRequest();
		if (request != null) {
			for (String name : headers) {
				String value = request.getHeader(name);
				if (value != null)
					httpUriRequest.addHeader(name, value);
			}
		}
	}

	@Override
	public void render(Output output) throws IOException {
		output.setStatus(statusCode, statusText);
		copyHeaders(output, "Date", "Content-Type", "Content-Length",
				"Last-Modified", "ETag", "Expires", "Cache-control");
		// TODO: refactor this
		Header header = null;
		if (httpResponse != null)
			header = httpResponse.getFirstHeader("Location");
		if (header != null) {
			// Location header rewriting
			String location = header.getValue();
			String originalBase = target.getOriginalRequest().getScheme()
					+ "://" + target.getOriginalRequest().getServerName() + ":"
					+ target.getOriginalRequest().getServerPort()
					+ target.getOriginalRequest().getContextPath()
					+ target.getOriginalRequest().getServletPath();
			if (target.getOriginalRequest().getPathInfo() != null)
				originalBase += target.getOriginalRequest().getPathInfo();
			int pos = originalBase.indexOf(target.getRelUrl());
			originalBase = originalBase.substring(0, pos + 1);
			location = location.replaceFirst(target.getDriver().getBaseURL(),
					originalBase);
			output.addHeader(header.getName(), location);
		}
		String charset = null;
		if (httpEntity != null)
			charset = EntityUtils.getContentCharSet(httpEntity);
		if (charset != null)
			output.setCharsetName(charset);
		try {
			output.open();
			if (exception != null)
				output.write(statusText);
			else {
				Handler handler = createHandler();
				handler.handle(httpResponse.getEntity().getContent(), output);
			}
		} finally {
			output.close();
		}
	}

	private Handler createHandler() {
		String jsessionid = null;
		if (target.getUserContext() != null && target.isFilterJsessionid()) {
			List<Cookie> cookies = target.getUserContext().getCookieStore()
					.getCookies();
			for (Cookie cookie : cookies)
				if ("jsessionid".equalsIgnoreCase(cookie.getName())) {
					jsessionid = cookie.getValue();
					break;
				}
		}
		boolean textContentType = false;
		Header contentTypeHeader = httpResponse.getFirstHeader("Content-Type");
		if (contentTypeHeader != null)
			textContentType = ResourceUtils.isTextContentType(contentTypeHeader
					.getValue());
		if (jsessionid == null || !textContentType)
			return new OldHandler();
		else {
			String charset = null;
			if (httpEntity != null)
				charset = EntityUtils.getContentCharSet(httpEntity);
			if (target.isPropagateJsessionId())
				return new ReplaceCookieHandler(charset, jsessionid);
			else
				return new RemoveCookieHandler(charset, jsessionid);
		}
	}

	@Override
	public void release() {
		if (httpEntity != null) {
			try {
				httpEntity.consumeContent();
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
	}

	@Override
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(target.getMethod());
		result.append(" ");
		result.append(ResourceUtils.getHttpUrlWithQueryString(target));
		result.append("\n");
		if (target.getUserContext() != null)
			result.append(target.getUserContext().toString());
		return result.toString();
	}

	interface Handler {
		void handle(InputStream src, Output dest) throws IOException;
	}

	static final class ReplaceCookieHandler extends CookieHandler {
		public ReplaceCookieHandler(String charset, String jsessionid) {
			super(charset, jsessionid);
		}

		/** Replaces <code>jsessionid</code>'s some value with new one */
		@Override
		protected String parseContent(String content) {
			String replacement = "jsessionid=" + jsessionid;
			return content.replaceAll("jsessionid=([^?#&'\"]+)", replacement);
		}
	}

	static final class RemoveCookieHandler extends CookieHandler {
		public RemoveCookieHandler(String charset, String jsessionid) {
			super(charset, jsessionid);
		}

		/** Replaces <code>jsessionid</code>'s some value with new one */
		@Override
		protected String parseContent(String content) {
			return content.replaceAll("[;]{0,1}jsessionid=([^?#&'\"]+)", "");
		}
	}

	static abstract class CookieHandler implements Handler {
		private final Handler defaultHandler = new OldHandler();
		private final String charset;
		protected final String jsessionid;

		protected CookieHandler(String charset, String jsessionid) {
			this.charset = charset;
			this.jsessionid = jsessionid;
		}

		public void handle(InputStream src, Output dest) throws IOException {
			StringOutput out = new StringOutput();
			out.setCharsetName(charset);
			defaultHandler.handle(src, out);
			// parse content;
			String parsed = parseContent(out.toString());
			// update content-length header
			if (dest.getHeader("Content-length") != null)
				dest.setHeader("Content-length", Integer.toString(parsed
						.length()));
			// put parsed content to destination
			defaultHandler.handle(new ByteArrayInputStream(parsed
					.getBytes(charset)), dest);
		}

		protected abstract String parseContent(String content);
	}

	static final class OldHandler implements Handler {
		public OldHandler() {
			// no op constructor
		}

		public void handle(InputStream src, Output dest) throws IOException {
			if (src != null)
				try {
					OutputStream out = dest.getOutputStream();
					if (out != null) {
						// out may be null when NOT_MODIFIED return code
						byte[] buffer = new byte[1024];
						for (int len = -1; (len = src.read(buffer)) != -1;)
							out.write(buffer, 0, len);
					}
				} finally {
					src.close();
				}
		}
	}
}

package org.esigate.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.esigate.api.Cookie;
import org.esigate.api.HttpRequest;
import org.esigate.api.HttpSession;

public class MockHttpRequest implements HttpRequest {
	private final HashMap<String, Object> attributes = new HashMap<String, Object>();
	private String characterEncoding;
	private URL url;
	private String method = "GET";
	private HttpSession session;
	private final ArrayList<Cookie> cookies = new ArrayList<Cookie>();
	private final HashMap<String, String> headers = new HashMap<String, String>();
	private byte[] content;
	private boolean consumed = false;
	private static final SimpleDateFormat httpDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
	private Long ttl;
	private Integer maxWait;
	private boolean noStore;
	
	public void setMethod(String method) {
		this.method = method;
	}

	public MockHttpRequest(String url) {
		setUrl(url);
	}

	public MockHttpRequest() {
		this("http://localhost:8080");
	}

	public void setUrl(String url) {
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		setHeader("Host", getServerName());
	}

	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	@SuppressWarnings("rawtypes")
	public Enumeration getAttributeNames() {
		return Collections.enumeration(attributes.keySet());
	}

	public String getCharacterEncoding() {
		return characterEncoding;
	}

	public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
		this.characterEncoding = env;
	}

	public int getContentLength() {
		String headerValue = getHeader("Content-length");
		if (headerValue != null) {
			return Integer.parseInt(headerValue);
		}
		return 0;
	}

	public String getContentType() {
		return getHeader("Content-type");
	}

	public InputStream getInputStream() throws IOException {
		if (consumed) {
			throw new IllegalStateException("InputStream already obtained");
		}
		consumed = true;
		final InputStream is = new ByteArrayInputStream(content);
		return new InputStream() {
			@Override
			public int read() throws IOException {
				return is.read();
			}
		};
	}

	public String getParameter(String name) {
		String queryString = url.getQuery();
		String[] params = queryString.split("&");
		for (String param : params) {
			String paramName = param.split("=")[0];
			String paramValue = param.split("=")[1];
			if (name.equals(paramName)) {
				return paramValue;
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Enumeration getParameterNames() {
		throw new RuntimeException("Method not implemented");
	}

	public String[] getParameterValues(String name) {
		throw new RuntimeException("Method not implemented");
	}

	@SuppressWarnings("rawtypes")
	public Map getParameterMap() {
		throw new RuntimeException("Method not implemented");
	}

	public String getProtocol() {
		throw new RuntimeException("Method not implemented");
	}

	public String getScheme() {
		return url.getProtocol();
	}

	public String getServerName() {
		return url.getHost();
	}

	public int getServerPort() {
		return url.getPort();
	}

	public BufferedReader getReader() throws IOException {
		throw new RuntimeException("Method not implemented");
	}

	public String getRemoteAddr() {
		return null;
	}

	public String getRemoteHost() {
		throw new RuntimeException("Method not implemented");
	}

	public void setAttribute(String name, Object o) {
		attributes.put(name, o);
	}

	public void removeAttribute(String name) {
		attributes.remove(name);
	}

	public Locale getLocale() {
		throw new RuntimeException("Method not implemented");
	}

	@SuppressWarnings("rawtypes")
	public Enumeration getLocales() {
		throw new RuntimeException("Method not implemented");
	}

	public boolean isSecure() {
		throw new RuntimeException("Method not implemented");
	}

//	public RequestDispatcher getRequestDispatcher(String path) {
//		throw new RuntimeException("Method not implemented");
//	}

	public String getRealPath(String path) {
		throw new RuntimeException("Method not implemented");
	}

	public int getRemotePort() {
		throw new RuntimeException("Method not implemented");
	}

	public String getLocalName() {
		throw new RuntimeException("Method not implemented");
	}

	public String getLocalAddr() {
		throw new RuntimeException("Method not implemented");
	}

	public int getLocalPort() {
		throw new RuntimeException("Method not implemented");
	}

	public String getAuthType() {
		throw new RuntimeException("Method not implemented");
	}

	public Cookie[] getCookies() {
		Cookie[] cookiesArray = new Cookie[cookies.size()];
		return cookies.toArray(cookiesArray);
	}

	public void addCookie(Cookie cookie) {
		cookies.add(cookie);
	}

	public long getDateHeader(String name) {
		String dateString = getHeader(name);
		if (dateString != null) {
			try {
				return httpDateFormat.parse(dateString).getTime();
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}
		return 0;
	}

	public void setDateHeader(String name, Date date) {
		setHeader(name, httpDateFormat.format(date));
	}

	public String getHeader(String name) {
		return headers.get(name.toLowerCase());
	}

	public void setHeader(String name, String value) {
		headers.put(name.toLowerCase(), value);
	}

	@SuppressWarnings("rawtypes")
	public Enumeration getHeaders(String name) {
		throw new RuntimeException("Method not implemented");
	}

	public Collection<String> getHeaderNames() {
		return headers.keySet();
	}

	public int getIntHeader(String name) {
		throw new RuntimeException("Method not implemented");
	}

	public String getMethod() {
		return method;
	}

	public String getPathInfo() {
		throw new RuntimeException("Method not implemented");
	}

	public String getPathTranslated() {
		throw new RuntimeException("Method not implemented");
	}

	public String getContextPath() {
		throw new RuntimeException("Method not implemented");
	}

	public String getQueryString() {
		return url.getQuery();
	}

	public String getRemoteUser() {
		throw new RuntimeException("Method not implemented");
	}

	public boolean isUserInRole(String role) {
		throw new RuntimeException("Method not implemented");
	}

	public Principal getUserPrincipal() {
		throw new RuntimeException("Method not implemented");
	}

	public String getRequestedSessionId() {
		throw new RuntimeException("Method not implemented");
	}

	public String getRequestURI() {
		return url.getPath();
	}

	public String getRequestURL() {
		StringBuffer sb = new StringBuffer();
		sb.append(url.getProtocol());
		sb.append("://");
		sb.append(url.getHost());
		if (url.getPort() > 0) {
			sb.append(":");
			sb.append(url.getPort());
		}
		sb.append(url.getPath());
		if (url.getQuery() != null && !"".equals(url.getQuery())) {
			sb.append("?");
			sb.append(url.getQuery());
		}
		return sb.toString();
	}

	public String getServletPath() {
		throw new RuntimeException("Method not implemented");
	}

	public HttpSession getSession(boolean create) {
		if (create && session == null) {
			session = new MockHttpSession();
		}
		return session;
	}

	public HttpSession getSession() {
		return getSession(true);
	}

	public boolean isRequestedSessionIdValid() {
		throw new RuntimeException("Method not implemented");
	}

	public boolean isRequestedSessionIdFromCookie() {
		throw new RuntimeException("Method not implemented");
	}

	public boolean isRequestedSessionIdFromURL() {
		throw new RuntimeException("Method not implemented");
	}

	public boolean isRequestedSessionIdFromUrl() {
		throw new RuntimeException("Method not implemented");
	}

	public Long getResourceTtl() {
		return ttl;
	}

	public Boolean isNoStoreResource() {
		return noStore;
	}

	public Integer getFetchMaxWait() {
		return maxWait;
	}

	public void setResourceTtl(Long ttl) {
		this.ttl = ttl;
	}

	public void setNoStoreResource(boolean noStore) {
		this.noStore = noStore;
	}

	public void setFetchMaxWait(Integer maxWait) {
		this.maxWait = maxWait;
	}

}

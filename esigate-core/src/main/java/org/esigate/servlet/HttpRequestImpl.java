package org.esigate.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.esigate.api.Cookie;
import org.esigate.api.HttpRequest;
import org.esigate.api.HttpSession;
import org.esigate.cookie.BasicClientCookie;

class HttpRequestImpl implements HttpRequest {

	private final HttpServletRequest parent;
	private Long ttl;
	private Integer maxWait;
	private boolean noStore;
	
	
	private HttpRequestImpl(HttpServletRequest parent) {
		this.parent = parent;
	}
	
	public static HttpRequest wrap(HttpServletRequest parent) {
		return new HttpRequestImpl(parent);
	}

	public String getQueryString() {
		return parent.getQueryString();
	}

	public String getParameter(String name) {
		return parent.getParameter(name);
	}

	public String getHeader(String name) {
		return parent.getHeader(name);
	}

	@SuppressWarnings("unchecked")
	public Collection<String> getHeaderNames() {
		return Collections.<String>list(parent.getHeaderNames());
	}

	public Cookie[] getCookies() {
		javax.servlet.http.Cookie[] src = parent.getCookies();
		Cookie result[] = null;
		if(src != null){
			result = new Cookie[src.length];
			for (int i = 0; i < src.length; i++) {
				javax.servlet.http.Cookie c = src[i];
				Cookie dest = new BasicClientCookie(c.getName(), c.getValue());
				dest.setSecure(c.getSecure());
				dest.setDomain(c.getDomain());
				dest.setPath(c.getPath());
				dest.setComment(c.getComment());
				dest.setVersion(c.getVersion());
				//if (cookie.getMaxAge() < 0) {
	
				result[i] = dest;
			}
		}
		return result;
	}

	public String getMethod() {
		return parent.getMethod();
	}

	public int getServerPort() {
		return parent.getServerPort();
	}

	public String getServerName() {
		return parent.getServerName();
	}

	public String getScheme() {
		return parent.getScheme();
	}

	public String getRemoteAddr() {
		return parent.getRemoteAddr();
	}

	public InputStream getInputStream() throws IOException {
		return parent.getInputStream();
	}

	public String getContentType() {
		return parent.getContentType();
	}

	public String getRequestURI() {
		return parent.getRequestURI();
	}

	public String getRequestURL() {
		return parent.getRequestURL().toString();
	}

	public boolean isSecure() {
		return parent.isSecure();
	}

	public String getCharacterEncoding() {
		return parent.getCharacterEncoding();
	}
	
	public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
		 this.parent.setCharacterEncoding(env);
	}

	public String getRemoteUser() {
		return parent.getRemoteUser();
	}

	public Object getAttribute(String name) {
		return parent.getAttribute(name);
	}

	public void setAttribute(String name, Object o) {
		parent.setAttribute(name, o);
	}

	public Principal getUserPrincipal() {
		return parent.getUserPrincipal();
	}

	public HttpSession getSession(boolean create) {
		javax.servlet.http.HttpSession session = parent.getSession(create);
		return session != null ? HttpSessionImpl.wrap(session) : null;
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

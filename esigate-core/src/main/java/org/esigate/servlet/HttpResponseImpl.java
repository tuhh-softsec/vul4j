package org.esigate.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import org.esigate.api.Cookie;
import org.esigate.api.HttpResponse;

class HttpResponseImpl implements HttpResponse{
	private final HttpServletResponse parent;

	private HttpResponseImpl(HttpServletResponse parent) {
		this.parent = parent;
	}
	
	public static HttpResponse wrap(HttpServletResponse parent) {
		return new HttpResponseImpl(parent);
	}

	public void addCookie(Cookie src) {
		javax.servlet.http.Cookie servletCookie = new javax.servlet.http.Cookie(src.getName(), src.getValue());
		
		if(src.getDomain() != null)
			servletCookie.setDomain(src.getDomain());
		servletCookie.setPath(src.getPath());
		servletCookie.setSecure(src.isSecure());
		servletCookie.setComment(src.getComment());
		servletCookie.setVersion(src.getVersion());
		if (src.getExpiryDate() != null) {
			int maxAge = (int) (src.getExpiryDate().getTime() - System.currentTimeMillis()) / 1000;
			// According to Cookie class specification, a negative value
			// would be considered as no value. That is not what we want!
			if (maxAge < 0) {
				maxAge = 0;
			}
			servletCookie.setMaxAge(maxAge);
		}

		parent.addCookie(servletCookie);
	}

	public void setStatus(int sc) {
		parent.setStatus(sc);
	}

	public OutputStream getOutputStream() throws IOException {
		return parent.getOutputStream();
	}

	public Writer getWriter() throws IOException {
		return parent.getWriter();
	}

	public void addHeader(String name, String value) {
		parent.addHeader(name, value);
	}

	public void setCharacterEncoding(String charset) {
		parent.setCharacterEncoding(charset);
	}

	public void setContentLength(int len) {
		parent.setContentLength(len);
	}

	public void setContentType(String type) {
		parent.setContentType(type);
	}
}

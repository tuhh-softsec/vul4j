package org.esigate.servlet;

import java.io.InputStream;

import org.esigate.api.HttpSession;

public class HttpSessionImpl implements HttpSession {

	private final javax.servlet.http.HttpSession parent;

	private HttpSessionImpl(javax.servlet.http.HttpSession parent) {
		this.parent = parent;
	}

	public static HttpSession wrap(javax.servlet.http.HttpSession parent) {
		return new HttpSessionImpl(parent);
	}

	public String getId() {
		return parent.getId();
	}

	public Object getAttribute(String name) {
		return parent.getAttribute(name);
	}

	public void setAttribute(String name, Object value) {
		parent.setAttribute(name, value);
	}
	
	public InputStream getResourceTemplate(String template){
		return parent.getServletContext().getResourceAsStream(template);
	}
	
}

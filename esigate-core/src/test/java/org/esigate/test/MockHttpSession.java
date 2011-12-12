package org.esigate.test;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;

import org.esigate.api.HttpSession;

public class MockHttpSession implements HttpSession {

	private final HashMap<String, Object> attributes = new HashMap<String, Object>();

	public long getCreationTime() {
		throw new RuntimeException("Method not implemented");
	}

	public String getId() {
		throw new RuntimeException("Method not implemented");
	}

	public long getLastAccessedTime() {
		throw new RuntimeException("Method not implemented");
	}

//	public ServletContext getServletContext() {
//		throw new RuntimeException("Method not implemented");
//	}

	public void setMaxInactiveInterval(int interval) {
		throw new RuntimeException("Method not implemented");
	}

	public int getMaxInactiveInterval() {
		throw new RuntimeException("Method not implemented");
	}

//	public HttpSessionContext getSessionContext() {
//		throw new RuntimeException("Method not implemented");
//	}

	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	public Object getValue(String name) {
		throw new RuntimeException("Method not implemented");
	}

	@SuppressWarnings("rawtypes")
	public Enumeration getAttributeNames() {
		throw new RuntimeException("Method not implemented");
	}

	public String[] getValueNames() {
		throw new RuntimeException("Method not implemented");
	}

	public void setAttribute(String name, Object value) {
		attributes.put(name, value);
	}

	public void putValue(String name, Object value) {
		throw new RuntimeException("Method not implemented");
	}

	public void removeAttribute(String name) {
		throw new RuntimeException("Method not implemented");
	}

	public void removeValue(String name) {
		throw new RuntimeException("Method not implemented");
	}

	public void invalidate() {
		throw new RuntimeException("Method not implemented");
	}

	public boolean isNew() {
		throw new RuntimeException("Method not implemented");
	}

	public InputStream getResourceTemplate(String template) {
		throw new RuntimeException("Method not implemented");
	}

}

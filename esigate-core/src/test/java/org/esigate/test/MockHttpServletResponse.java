package org.esigate.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class MockHttpServletResponse implements HttpServletResponse {
	private int statusCode = 200;
	private String statusMessage;

	public String getCharacterEncoding() {
		throw new RuntimeException("Method not implemented");
	}

	public String getContentType() {
		throw new RuntimeException("Method not implemented");
	}

	public ServletOutputStream getOutputStream() throws IOException {
		throw new RuntimeException("Method not implemented");
	}

	public PrintWriter getWriter() throws IOException {
		throw new RuntimeException("Method not implemented");
	}

	public void setCharacterEncoding(String charset) {
		throw new RuntimeException("Method not implemented");
	}

	public void setContentLength(int len) {
		throw new RuntimeException("Method not implemented");
	}

	public void setContentType(String type) {
		throw new RuntimeException("Method not implemented");
	}

	public void setBufferSize(int size) {
		throw new RuntimeException("Method not implemented");
	}

	public int getBufferSize() {
		throw new RuntimeException("Method not implemented");
	}

	public void flushBuffer() throws IOException {
		throw new RuntimeException("Method not implemented");
	}

	public void resetBuffer() {
		throw new RuntimeException("Method not implemented");
	}

	public boolean isCommitted() {
		throw new RuntimeException("Method not implemented");
	}

	public void reset() {
		throw new RuntimeException("Method not implemented");
	}

	public void setLocale(Locale loc) {
		throw new RuntimeException("Method not implemented");
	}

	public Locale getLocale() {
		throw new RuntimeException("Method not implemented");
	}

	public void addCookie(Cookie cookie) {
		throw new RuntimeException("Method not implemented");
	}

	public boolean containsHeader(String name) {
		throw new RuntimeException("Method not implemented");
	}

	public String encodeURL(String url) {
		throw new RuntimeException("Method not implemented");
	}

	public String encodeRedirectURL(String url) {
		throw new RuntimeException("Method not implemented");
	}

	public String encodeUrl(String url) {
		throw new RuntimeException("Method not implemented");
	}

	public String encodeRedirectUrl(String url) {
		throw new RuntimeException("Method not implemented");
	}

	public void sendError(int sc, String msg) throws IOException {
		setStatus(sc, msg);
	}

	public void sendError(int sc) throws IOException {
		setStatus(sc);
	}

	public void sendRedirect(String location) throws IOException {
		setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY, "Moved temporarily");
		setHeader("Location", location);
	}

	public void setDateHeader(String name, long date) {
		throw new RuntimeException("Method not implemented");
	}

	public void addDateHeader(String name, long date) {
		throw new RuntimeException("Method not implemented");
	}

	public void setHeader(String name, String value) {
		throw new RuntimeException("Method not implemented");
	}

	public void addHeader(String name, String value) {
		throw new RuntimeException("Method not implemented");
	}

	public void setIntHeader(String name, int value) {
		throw new RuntimeException("Method not implemented");
	}

	public void addIntHeader(String name, int value) {
		throw new RuntimeException("Method not implemented");
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatus(int sc) {
		this.statusCode = sc;
	}

	public void setStatus(int sc, String sm) {
		this.statusCode = sc;
		this.statusMessage = sm;
	}

}

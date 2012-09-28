package org.esigate.test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.esigate.api.Cookie;
import org.esigate.api.HttpResponse;

public class MockHttpResponse implements HttpResponse {
	private int statusCode = 200;
	private String statusMessage;
	private final HashMap<String, String> headers = new HashMap<String, String>();
	private ByteArrayOutputStream outputStream;
	private StringWriter writer;
	/**
	 * Status code (302) indicating that the resource has temporarily moved to
	 * another location, but that future references should still use the
	 * original URI to access the resource.
	 * 
	 * This definition is being retained for backwards compatibility. SC_FOUND
	 * is now the preferred definition.
	 */
	public static final int SC_MOVED_TEMPORARILY = 302;

	public String getCharacterEncoding() {
		throw new RuntimeException("Method not implemented");
	}

	public String getContentType() {
		throw new RuntimeException("Method not implemented");
	}

	public OutputStream getOutputStream() throws IOException {
		if (writer != null)
			throw new IllegalStateException("Writer already obtained");
		outputStream = new ByteArrayOutputStream();
		return outputStream;
	}

	public PrintWriter getWriter() throws IOException {
		if (outputStream != null)
			throw new IllegalStateException("Outputstream already obtained");
		writer = new StringWriter();
		return new PrintWriter(writer);
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

	public Locale getLocale() {
		throw new RuntimeException("Method not implemented");
	}

	public void addCookie(Cookie cookie) {
		throw new RuntimeException("Method not implemented");
	}

	public void sendError(int sc, String msg) throws IOException {
		setStatus(sc, msg);
	}

	public void sendError(int sc) throws IOException {
		setStatus(sc);
	}

	public void addHeader(String name, String value) {
		headers.put(name.toLowerCase(), value);
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

	public boolean containsHeader(String string) {
		return headers.containsKey(string.toLowerCase());
	}
	
	public String getBodyAsString() throws UnsupportedEncodingException {
		if (writer!=null)
			return writer.toString();
		if(outputStream!=null){
			String charset="ISO-8859-1";
			String contentType =headers.get("content-type");
			if (contentType!=null && contentType.toLowerCase().contains("utf-8"))
				charset="UTF-8";
				return new String(outputStream.toByteArray(), charset);
	
		}
		return null;
	}

}

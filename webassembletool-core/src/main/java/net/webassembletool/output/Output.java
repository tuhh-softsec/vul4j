package net.webassembletool.output;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * An Output is designed to collect the response to a successfull HTTP request,
 * typically an HTML page or any other file type with all the headers sent by
 * the server.<br />
 * 
 * Output implementations may handle the data as needed : write it to an
 * HttpServletResponse, save it to a File or a database for example.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public abstract class Output {

	// Default charset on the web is ISO-8859-1
	// For more details see
	// http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.7.1
	private String charsetName;
	private int statusCode;
	private String statusMessage;
	private final Properties headers = new Properties();

	/**
	 * Sets the HTTP status code of the response
	 * 
	 * @param code
	 *            The code
	 * @param message
	 *            The message
	 */
	public final void setStatus(int code, String message) {
		statusCode = code;
		statusMessage = message;
	}

	public final int getStatusCode() {
		return statusCode;
	}

	public final void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public final String getStatusMessage() {
		return statusMessage;
	}

	public final void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	protected final Properties getHeaders() {
		return headers;
	}

	public final String getHeader(String key) {
		for (Iterator<Map.Entry<Object, Object>> headersIterator = getHeaders()
				.entrySet().iterator(); headersIterator.hasNext();) {
			Map.Entry<Object, Object> entry = headersIterator.next();
			if (key.equalsIgnoreCase(entry.getKey().toString()))
				return entry.getValue().toString();
		}
		return null;
	}

	public final void setHeader(String key, String value) {
		for (Iterator<Map.Entry<Object, Object>> headersIterator = getHeaders()
				.entrySet().iterator(); headersIterator.hasNext();) {
			Map.Entry<Object, Object> entry = headersIterator.next();
			if (key.equalsIgnoreCase(entry.getKey().toString()))
				headers.remove(entry.getKey());
		}
		headers.put(key, value);
	}

	/**
	 * Copy all headers from this output to the <code>dest</code> Output
	 * 
	 * @param dest
	 *            destination output
	 */
	public final void copyHeaders(Output dest) {
		dest.headers.putAll(headers);
	}

	/**
	 * Adds an HTTP Header
	 * 
	 * @param name
	 *            The name of the HTTP header
	 * @param value
	 *            The value of the header
	 */
	public final void addHeader(String name, String value) {
		headers.put(name, value);
	}

	public final String getCharsetName() {
		return charsetName;
	}

	/**
	 * Defines the charset of the Output. <br />
	 * Needed for text outputs (for example HTML pages).
	 * 
	 * @param charsetName
	 *            name of the charset
	 */
	public final void setCharsetName(String charsetName) {
		this.charsetName = charsetName;
	}

	/**
	 * Opens the OutputStreams that may be needed by the OutPut.<br />
	 * The headers and charset may be ignored if not defined before calling this
	 * method.<br />
	 * Any opened Output should be closed in order to release the resources.
	 */
	public abstract void open();

	/**
	 * Returns underlying output stream
	 * 
	 * @return output stream
	 */
	public abstract OutputStream getOutputStream();

	/**
	 * Closes underlying output stream
	 * 
	 * @throws OutputException
	 *             in case of error
	 */
	public abstract void close();

	public final void write(String string) {
		try {
			if (charsetName != null)
				getOutputStream().write(string.getBytes(charsetName));
			else
				getOutputStream().write(string.getBytes("ISO-8859-1"));
		} catch (UnsupportedEncodingException e) {
			throw new OutputException(e);
		} catch (IOException e) {
			throw new OutputException(e);
		}
	}
}

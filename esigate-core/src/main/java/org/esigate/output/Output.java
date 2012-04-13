package org.esigate.output;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

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
	
	// This map has to be synchronized, because headers are updated by multiple threads
	// see https://sourceforge.net/apps/mantisbt/webassembletool/view.php?id=84
	private final Map<String, Set<String>> headers = Collections
		    .synchronizedMap(new HashMap<String, Set<String>>());
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

	protected final Map<String, Set<String>> getHeaders() {
		return headers;
	}

	public final String getHeader(String key) {
		String result = null;
		for (Entry<String, Set<String>> entry : headers.entrySet()) {
			if (key.equalsIgnoreCase(entry.getKey())
					&& !entry.getValue().isEmpty()) {
				result = entry.getValue().iterator().next();
				break;
			}
		}
		return result;
	}

	public final void setHeader(String key, String value) {
		String keyToRemove = null;
		for (String header : headers.keySet()) {
			if (key.equalsIgnoreCase(header)) {
				keyToRemove = header;
				break;
			}
		}
		if (keyToRemove != null) {
			headers.remove(keyToRemove);
		}
		addHeader(key, value);
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
		Set<String> values = null;
		for (Entry<String, Set<String>> entry : headers.entrySet()) {
			if (name.equalsIgnoreCase(entry.getKey())) {
				values = entry.getValue();
				break;
			}
		}
		if (values == null) {
			// This set has to be synchronized, because headers are updated by multiple threads
			// see https://sourceforge.net/apps/mantisbt/webassembletool/view.php?id=84
			values = Collections.synchronizedSortedSet(new TreeSet<String>());

			headers.put(name, values);
		}
		values.add(value);
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
			if (charsetName != null) {
				getOutputStream().write(string.getBytes(charsetName));
			} else {
				getOutputStream().write(string.getBytes("ISO-8859-1"));
			}
		} catch (UnsupportedEncodingException e) {
			throw new OutputException(e);
		} catch (IOException e) {
			throw new OutputException(e);
		}
	}
}

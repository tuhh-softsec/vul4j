package org.esigate.output;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Output implementation that writes to a String.<br />
 * StringOutput should be used only for text responses such as HTML and not for
 * binary data such as images.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class StringOutput extends Output {
	private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

	public StringOutput() {
		super();
	}

	public String getLocation() {
		return getHeader("location");
	}

	/** {@inheritDoc} */
	@Override
	public OutputStream getOutputStream() {
		return byteArrayOutputStream;
	}

	/** {@inheritDoc} 
	 * @throws IOException */
	@Override
	public void close() throws IOException {
			byteArrayOutputStream.close();
	}

	@Override
	public String toString() {
		String charsetName = getCharsetName();
		if (charsetName == null) {
			charsetName = "ISO-8859-1";
		}
		try {
			return byteArrayOutputStream.toString(charsetName);
		} catch (UnsupportedEncodingException e) {
			throw new OutputException(e);
		}
	}

	@Override
	public void open() {
		// Nothing to do
	}

}

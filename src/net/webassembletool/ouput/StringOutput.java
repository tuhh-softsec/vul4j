package net.webassembletool.ouput;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Output implementation that writes to a String.<br />
 * StringOutput should be used only for text responses such as HTML and not for
 * binary data such as images.
 * 
 * @author François-Xavier Bonnet
 * 
 */
public class StringOutput implements Output {
    private String charset = "ISO-8859-1";
    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    public void addHeader(String name, String value) {
	// Nothing to do
    }

    public void close() {
	// Nothing to do
    }

    public void open() {
	// Nothing to do
    }

    public void setCharset(String charset) {
	this.charset = charset;
    }

    public void write(byte[] bytes, int off, int len) throws IOException {
	byteArrayOutputStream.write(bytes, off, len);
    }

    public String toString() {
	try {
	    return byteArrayOutputStream.toString(charset);
	} catch (UnsupportedEncodingException e) {
	    throw new OutputException("Encoding not supported: " + charset, e);
	}
    }
}

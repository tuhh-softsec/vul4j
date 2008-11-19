package net.webassembletool.ouput;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

/**
 * Output implementation that writes to a String.<br />
 * StringOutput should be used only for text responses such as HTML and not for
 * binary data such as images.
 * 
 * @author François-Xavier Bonnet
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
    public void open() {
	// Nothing to do
    }

    /** {@inheritDoc} */
    @Override
    public OutputStream getOutputStream() {
	return byteArrayOutputStream;
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
	try {
	    byteArrayOutputStream.close();
	} catch (IOException e) {
	    // should not happen
	    throw new OutputException(e);
	}
    }

    public void copyHeaders(HttpServletResponse response) {
	for (Iterator<Map.Entry<Object, Object>> headersIterator = getHeaders()
		.entrySet().iterator(); headersIterator.hasNext();) {
	    Map.Entry<Object, Object> entry = headersIterator.next();
	    if (!"content-length".equalsIgnoreCase(entry.getKey().toString()))
		response.addHeader(entry.getKey().toString(), entry.getValue()
			.toString());
	}
    }

    @Override
    public String toString() {
	try {
	    return byteArrayOutputStream.toString(getCharsetName());
	} catch (UnsupportedEncodingException e) {
	    throw new OutputException(e);
	}
    }

}

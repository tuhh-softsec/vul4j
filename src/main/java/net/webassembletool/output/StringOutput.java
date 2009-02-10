package net.webassembletool.output;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map.Entry;

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
        for (Entry<?, ?> entry : getHeaders().entrySet()) {
            String key = entry.getKey().toString();
            String value = entry.getValue().toString();
            if (!"content-length".equalsIgnoreCase(key)) {
                response.addHeader(key, value);
            }
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

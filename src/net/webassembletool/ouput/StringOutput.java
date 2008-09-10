package net.webassembletool.ouput;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.HttpServletResponse;

import net.webassembletool.Driver;

import org.apache.commons.httpclient.Header;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Output implementation that writes to a String.<br /> StringOutput should be
 * used only for text responses such as HTML and not for binary data such as
 * images.
 * 
 * @author François-Xavier Bonnet
 * 
 */
public class StringOutput implements Output {
    private final static Log log = LogFactory.getLog(Driver.class);
    private ArrayList<Header> headers = new ArrayList<Header>();
    private String charset = "ISO-8859-1";
    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    private int statusCode = 200;
    private String location;

    public String getCharset() {
	return charset;
    }

    public int getStatusCode() {
	return statusCode;
    }

    public void setStatusCode(int statusCode) {
	this.statusCode = statusCode;
    }

    public String getLocation() {
	return location;
    }

    public void setLocation(String location) {
	this.location = location;
    }

    public void addHeader(String name, String value) {
	if ("location".equalsIgnoreCase(name))
	    location = value;
	else
	    headers.add(new Header(name, value));
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

    public void write(byte[] bytes, int off, int len) {
	byteArrayOutputStream.write(bytes, off, len);
    }

    @Override
    public String toString() {
	if (statusCode != 200)
	    return null;
	try {
	    return byteArrayOutputStream.toString(charset);
	} catch (UnsupportedEncodingException e) {
	    throw new OutputException("Encoding not supported: " + charset, e);
	}
    }

    public void setStatus(int code, String message) {
	statusCode = code;
	log.debug(code + "" + message);
    }

    public void copyHeaders(HttpServletResponse response) {
	Iterator<Header> iterator = headers.iterator();
	while (iterator.hasNext()) {
	    Header header = iterator.next();
	    response.addHeader(header.getName(), header.getValue());
	}
    }

}

package net.webassembletool.http;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.webassembletool.ouput.Output;
import net.webassembletool.ouput.OutputException;

/**
 * Output implementation that simply writes to an HttpServletResponse.
 * 
 * @author François-Xavier Bonnet
 * 
 */
public class ResponseOutput implements Output {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private boolean notModified = false;
    private String etag;
    private String lastModified;

    public ResponseOutput(HttpServletRequest request,
	    HttpServletResponse response) {
	this.request = request;
	this.response = response;
    }

    public void addHeader(String name, String value) {
	if ("Last-Modified".equalsIgnoreCase(name))
	    lastModified = value;
	if ("ETag".equalsIgnoreCase(name))
	    etag = value;
	response.addHeader(name, value);
    }

    public void close() {
	// Nothing to do
    }

    public void open() {
	String ifModifiedSince = request.getHeader("If-Modified-Since");
	String ifNoneMatch = request.getHeader("If-None-Match");
	if ((ifModifiedSince != null && ifModifiedSince.equals(lastModified))
		|| (ifNoneMatch != null && ifNoneMatch.equals(etag))) {
	    response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
	    notModified = true;
	}
    }

    public void setCharset(String charset) {
	response.setCharacterEncoding(charset);
    }

    public void write(byte[] bytes, int offset, int length) {
	if (!notModified)
	    try {
		response.getOutputStream().write(bytes, offset, length);
	    } catch (IOException e) {
		throw new OutputException("Could not write to the response", e);
	    }
    }

    public void setStatus(int code, String message) {
	response.setStatus(code);
    }
}

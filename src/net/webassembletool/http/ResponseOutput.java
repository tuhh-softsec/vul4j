package net.webassembletool.http;

import java.io.IOException;
import java.io.OutputStream;

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
public class ResponseOutput extends Output {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private boolean notModified = false;
    private OutputStream outputStream;

    public ResponseOutput(HttpServletRequest request,
	    HttpServletResponse response) {
	this.request = request;
	this.response = response;
    }

    @Override
    public void open() {
        String ifModifiedSince = request.getHeader("If-Modified-Since");
        String ifNoneMatch = request.getHeader("If-None-Match");
        if ((ifModifiedSince != null && ifModifiedSince
        	.equals(getHeader("Last-Modified")))
        	|| (ifNoneMatch != null && ifNoneMatch
        		.equals(getHeader("ETag")))) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            notModified = true;
        }
        response.setStatus(getStatusCode());
        response.setCharacterEncoding(getCharsetName());
        if (!notModified)
            if (!notModified)
        	try {
        	    outputStream = response.getOutputStream();
        	} catch (IOException e) {
        	    throw new OutputException(e);
        	}
    }

    /**
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(int i) throws IOException {
        outputStream.write(i);
    }

    @Override
    public void close() {
	try {
	    outputStream.close();
	} catch (IOException e) {
	    throw new OutputException(e);
	}
    }
}

package net.webassembletool.resource;

import java.io.IOException;

import net.webassembletool.ouput.Output;

import org.apache.commons.httpclient.Header;

/**
 * Resource implementation that keeps the contents inside a byte array. A
 * MemoryResource can be reused and kept in cache.
 * 
 * @author François-Xavier Bonnet
 * 
 */
public class MemoryResource implements Resource {
    private byte[] byteArray;
    private Header[] headers;
    private String charset;
    private int statusCode;
    private String statusMessage;

    public MemoryResource(byte[] byteArray, String charset, Header[] headers,
	    int statusCode, String statusMessage) {
	this.byteArray = byteArray;
	this.headers = headers;
	this.charset = charset;
	this.statusCode = statusCode;
	this.statusMessage = statusMessage;
    }

    public void release() {
	// Nothing to do
    }

    public void render(Output output) throws IOException {
	output.setStatus(statusCode, statusMessage);
	output.open();
	try {
	    output.setCharset(charset);
	    for (int i = 0; i < headers.length; i++) {
		output.addHeader(headers[i].getName(), headers[i].getValue());
	    }
	    output.write(byteArray, 0, byteArray.length);
	} finally {
	    output.close();
	}
    }

    public String getStatusMessage() {
	return statusMessage;
    }

    public int getStatusCode() {
	return statusCode;
    }
}

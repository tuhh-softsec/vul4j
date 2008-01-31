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

    public MemoryResource(byte[] byteArray, String charset, Header[] headers) {
	this.byteArray = byteArray;
	this.headers = headers;
	this.charset = charset;
    }

    public boolean exists() {
	return true;
    }

    public void release() {
	// Nothing to do
    }

    public void render(Output output) throws IOException {
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
}

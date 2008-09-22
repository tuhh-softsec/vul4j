package net.webassembletool.cache;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import net.webassembletool.ouput.Output;
import net.webassembletool.resource.Resource;

/**
 * Resource implementation that keeps the contents inside a byte array. A
 * MemoryResource can be reused and kept in cache.
 * 
 * @author François-Xavier Bonnet
 * 
 */
public class MemoryResource extends Resource {
    private final byte[] byteArray;
    private final Properties headers;
    private final String charset;
    private final int statusCode;
    private final String statusMessage;
    private boolean stale = false;
    private final boolean empty;

    public MemoryResource(byte[] byteArray, String charset, Properties headers,
	    int statusCode, String statusMessage) {
	this.byteArray = byteArray;
	this.headers = headers;
	this.charset = charset;
	this.statusCode = statusCode;
	this.statusMessage = statusMessage;
	this.empty = false;
    }

    public boolean isEmpty() {
	return empty;
    }

    public MemoryResource() {
	// Used to crate an empty resource
	this.byteArray = null;
	this.headers = null;
	this.charset = null;
	this.statusCode = 0;
	this.statusMessage = null;
	this.empty = true;
    }

    @Override
    public void release() {
	// Nothing to do
    }

    @Override
    public void render(Output output) throws IOException {
	output.setStatus(statusCode, statusMessage);
	try {
	    output.setCharsetName(charset);
	    for (Iterator<Map.Entry<Object, Object>> headersIterator = headers
		    .entrySet().iterator(); headersIterator.hasNext();) {
		Map.Entry<Object, Object> entry = headersIterator.next();
		output.addHeader(entry.getKey().toString(), entry.getValue()
			.toString());
	    }
	    output.open();
	    output.write(byteArray, 0, byteArray.length);
	} finally {
	    output.close();
	}
    }

    public String getStatusMessage() {
	return statusMessage;
    }

    @Override
    public int getStatusCode() {
	return statusCode;
    }

    public boolean isStale() {
	return stale;
    }

    public void setStale(boolean stale) {
	this.stale = stale;
    }

}

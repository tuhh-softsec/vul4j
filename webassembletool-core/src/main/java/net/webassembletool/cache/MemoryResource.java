package net.webassembletool.cache;

import java.io.IOException;
import java.util.Properties;
import java.util.Map.Entry;

import net.webassembletool.output.Output;
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
	    for (Entry<Object, Object> entry : headers.entrySet()) {
		String key = entry.getKey().toString();
		String value = entry.getValue().toString();
		output.addHeader(key, value);
	    }
	    output.open();
	    output.getOutputStream().write(byteArray, 0, byteArray.length);
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

    public void setStale() {
	this.stale = true;
    }

}

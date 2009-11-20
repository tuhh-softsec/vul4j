package net.webassembletool.cache;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import net.webassembletool.output.Output;
import net.webassembletool.resource.Resource;

/**
 * Resource implementation that keeps the contents inside a byte array. A
 * MemoryResource can be reused and kept in cache.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class CachedResponse extends Resource {
	private final byte[] byteArray;
	private final Properties headers;
	private final String charset;
	private final int statusCode;
	private final String statusMessage;
	private boolean stale = false;
	private Map<String, String> vary;

	public CachedResponse(byte[] byteArray, String charset, Properties headers,
			int statusCode, String statusMessage) {
		this.byteArray = byteArray;
		this.headers = headers;
		this.charset = charset;
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
	}

	public boolean isEmpty() {
		return byteArray == null;
	}

	public CachedResponse() {
		// Used to crate an empty resource
		this.byteArray = null;
		this.headers = null;
		this.charset = null;
		this.statusCode = 0;
		this.statusMessage = null;
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
		// TODO compute with headers
		return stale;
	}

	public void setStale() {
		this.stale = true;
	}

	@Override
	public final String getHeader(String key) {
		for (Iterator<Map.Entry<Object, Object>> headersIterator = headers
				.entrySet().iterator(); headersIterator.hasNext();) {
			Map.Entry<Object, Object> entry = headersIterator.next();
			if (key.equalsIgnoreCase(entry.getKey().toString()))
				return entry.getValue().toString();
		}
		return null;
	}

	public Map<String, String> getVary() {
		return vary;
	}

}

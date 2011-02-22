package net.webassembletool.cache;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import net.webassembletool.output.Output;
import net.webassembletool.resource.Resource;

/**
 * A summary of a response, which does not include body content. Can be used to
 * check the validity of a resource before actually getting it.
 * 
 * @author Nicolas Richeton
 * 
 */
public class CachedResponseSummary extends Resource {
	private Map<String, Object> headers;
	private int statusCode;
	private Map<String, String> requestHeaders;
	private boolean responseBody;
	private String cacheKey;
	private Date localDate;

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CachedResponseSummary && cacheKey != null) {
			CachedResponseSummary crs = (CachedResponseSummary) obj;
			return cacheKey.equals(crs.getCacheKey());
		}
		return false;
	}

	@Override
	public Date getLocalDate() {
		return localDate;
	}

	public void setLocalDate(Date localDate) {
		this.localDate = localDate;
	}

	public String getCacheKey() {
		return cacheKey;
	}

	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}

	public CachedResponseSummary() {
	}

	@Override
	public boolean hasResponseBody() {
		return responseBody;
	}

	public void setHeaders(Map<String, Object> headers) {
		this.headers = headers;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public void setRequestHeaders(Map<String, String> requestHeaders) {
		this.requestHeaders = requestHeaders;
	}

	public void setResponseBody(boolean responseBody) {
		this.responseBody = responseBody;
	}

	@Override
	public void release() {
		// Nothing to do
	}

	@Override
	public void render(Output output) throws IOException {
		throw new IOException("CachedResponseSummary cannot be rendered");
	}

	@Override
	public int getStatusCode() {
		return statusCode;
	}

	@Override
	public Collection<String> getHeaderNames() {
		return headers.keySet();
	}

	@Override
	public final String getHeader(String key) {
		for (Entry<String, Object> entry : headers.entrySet()) {
			if (key.equalsIgnoreCase(entry.getKey().toString())) {
				return entry.getValue().toString();
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see net.webassembletool.resource.Resource#getRequestHeaders()
	 */
	@Override
	public String getRequestHeader(String key) {
		for (Entry<String, String> entry : requestHeaders.entrySet()) {
			if (key.equalsIgnoreCase(entry.getKey().toString())) {
				return entry.getValue().toString();
			}
		}

		return null;
	}

	@Override
	public String toString() {
		return cacheKey;
	}
}

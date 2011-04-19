package net.webassembletool.cache;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.webassembletool.output.Output;

/**
 * A summary of a response, which does not include body content. Can be used to check the validity of a resource before
 * actually getting it.
 * 
 * @author Nicolas Richeton
 * 
 */
public class CachedResponseSummary extends BaseCachedResource {
	private static final long serialVersionUID = 5229420665779140066L;
	private Map<String, String> requestHeaders;
	private boolean responseBody;
	private String cacheKey;
	private Date localDate;

	public CachedResponseSummary(Map<String, List<String>> headers, int statusCode, String statusMessage) {
		super(headers, statusCode, statusMessage);
	}

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

	@Override
	public boolean hasResponseBody() {
		return responseBody;
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

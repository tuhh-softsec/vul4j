package org.esigate.cache;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.esigate.output.Output;


/**
 * A summary of a response, which does not include body content. Can be used to
 * check the validity of a resource before actually getting it.
 * 
 * @author Nicolas Richeton
 * 
 */
public class CachedResponseSummary extends BaseCachedResource {
	private static final long serialVersionUID = 5229420665779140066L;

	private final String cacheKey;
	private Map<String, String> requestHeaders;
	private boolean responseBody;
	private Date localDate;

	public CachedResponseSummary(String cacheKey, Map<String, Set<String>> headers,
			int statusCode, String statusMessage) {
		super(headers, statusCode, statusMessage);
		this.cacheKey = cacheKey;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(cacheKey).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CachedResponseSummary)) {
			return false;
		}

		CachedResponseSummary other = (CachedResponseSummary) obj;
		return new EqualsBuilder()
			.append(cacheKey, other.cacheKey)
			.isEquals();
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
	 * @see org.esigate.resource.Resource#getRequestHeader(String)
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

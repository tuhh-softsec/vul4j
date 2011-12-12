package org.esigate.cache;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.esigate.api.HttpRequest;
import org.esigate.output.Output;
import org.esigate.util.Rfc2616;


/**
 * Resource implementation that keeps the contents inside a byte array. A
 * MemoryResource can be reused and kept in cache.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class CachedResponse extends BaseCachedResource {
	private static final long serialVersionUID = 8125407854908774093L;
	private final byte[] byteArray;
	private final String charset;
	private final Date localDate = new Date();
	private Map<String, String> requestHeaders;

	public CachedResponse(byte[] byteArray, String charset,
			Map<String, Set<String>> headers, int statusCode,
			String statusMessage) {
		super(headers, statusCode, statusMessage);
		this.byteArray = byteArray;
		this.charset = charset;
	}

	@Override
	public boolean hasResponseBody() {
		return !(byteArray == null);
	}

	public byte[] getByteArray() {
		return byteArray;
	}

	public CachedResponse() {
		// Used to create an empty resource
		super(null, 0, null);
		this.byteArray = null;
		this.charset = null;
		this.requestHeaders = null;
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
			for (Entry<String, Set<String>> entry : headers.entrySet()) {
				for (String value : entry.getValue()) {
					output.addHeader(entry.getKey(), value);
				}
			}
			output.open();
			output.getOutputStream().write(byteArray, 0, byteArray.length);
		} finally {
			output.close();
		}
	}

	@Override
	public Date getLocalDate() {
		return localDate;
	}

	@Override
	public String toString() {
		return statusCode + " " + statusMessage + " age="
				+ Rfc2616.getAge(this) + " stale=" + Rfc2616.isStale(this)
				+ " hasBody=" + hasResponseBody();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(byteArray);
		result = prime * result + ((charset == null) ? 0 : charset.hashCode());
		result = prime * result + ((headers == null) ? 0 : headers.hashCode());
		result = prime * result
				+ ((localDate == null) ? 0 : localDate.hashCode());
		result = prime * result
				+ ((requestHeaders == null) ? 0 : requestHeaders.hashCode());
		result = prime * result + statusCode;
		result = prime * result
				+ ((statusMessage == null) ? 0 : statusMessage.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CachedResponse other = (CachedResponse) obj;
		if (!Arrays.equals(byteArray, other.byteArray)) {
			return false;
		}
		if (charset == null) {
			if (other.charset != null) {
				return false;
			}
		} else if (!charset.equals(other.charset)) {
			return false;
		}
		if (headers == null) {
			if (other.headers != null) {
				return false;
			}
		} else if (!headers.equals(other.headers)) {
			return false;
		}
		if (localDate == null) {
			if (other.localDate != null) {
				return false;
			}
		} else if (!localDate.equals(other.localDate)) {
			return false;
		}
		if (requestHeaders == null) {
			if (other.requestHeaders != null) {
				return false;
			}
		} else if (!requestHeaders.equals(other.requestHeaders)) {
			return false;
		}
		if (statusCode != other.statusCode) {
			return false;
		}
		if (statusMessage == null) {
			if (other.statusMessage != null) {
				return false;
			}
		} else if (!statusMessage.equals(other.statusMessage)) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.esigate.resource.Resource#getRequestHeader(String key)
	 */
	@Override
	public String getRequestHeader(String key) {
		if (requestHeaders != null) {
			for (String name : requestHeaders.keySet()) {
				if (key.equalsIgnoreCase(name)) {
					return requestHeaders.get(name);
				}
			}
		}

		return null;
	}

	/**
	 * Set headers which were used to get this resource.
	 */
	public void setRequestHeader(String name, String value) {
		if (requestHeaders == null) {
			requestHeaders = new HashMap<String, String>();
		}
		this.requestHeaders.put(name, value);
	}

	/**
	 * Set headers which were used to get this resource from the original
	 * HttpServletRequest.
	 * <p>
	 * Headers can then be retrieved from HttpServletRequest#getRequestHeaders()
	 * 
	 * Headers will be
	 * 
	 * @param request
	 */
	public void setRequestHeadersFromRequest(HttpRequest request) {
		Collection<String> e = request.getHeaderNames();
		if (e != null && !e.isEmpty()) {
			HashMap<String, String> headers = new HashMap<String, String>();
			for (String name : e) {
				headers.put(name, request.getHeader(name));
			}
			this.requestHeaders = headers;
		} else {
			this.requestHeaders = null;
		}
	}

	public CachedResponseSummary getSummary(String key) {
		CachedResponseSummary s = new CachedResponseSummary(key, new HashMap<String, Set<String>>(headers),
				statusCode, statusMessage);
		s.setResponseBody(this.hasResponseBody());
		s.setLocalDate(localDate);
		// Copy Request headers
		Map<String, String> requestHeaders2 = new HashMap<String, String>();
		if (requestHeaders != null) {
			requestHeaders2.putAll(requestHeaders);
		}
		s.setRequestHeaders(requestHeaders2);

		return s;
	}
}

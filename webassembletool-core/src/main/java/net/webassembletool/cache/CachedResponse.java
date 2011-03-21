package net.webassembletool.cache;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import net.webassembletool.output.Output;
import net.webassembletool.resource.Resource;
import net.webassembletool.util.Rfc2616;

/**
 * Resource implementation that keeps the contents inside a byte array. A
 * MemoryResource can be reused and kept in cache.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class CachedResponse extends Resource implements Serializable{
	private static final long serialVersionUID = 8125407854908774093L;
	private final byte[] byteArray;
	private final Map<String, Object> headers;
	private final String charset;
	private final int statusCode;
	private final String statusMessage;
	private final Date localDate = new Date();
	private Map<String, String> requestHeaders;

	public CachedResponse(byte[] byteArray, String charset, Map<String, Object> headers,
			int statusCode, String statusMessage) {
		this.byteArray = byteArray;
		this.headers = headers;
		this.charset = charset;
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
	}

	@Override
	public boolean hasResponseBody() {
		return !(byteArray == null);
	}

	public byte[] getByteArray() {
		return byteArray;
	}

	public CachedResponse() {
		// Used to crate an empty resource
		this.byteArray = null;
		this.headers = null;
		this.charset = null;
		this.statusCode = 0;
		this.statusMessage = null;
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
			for (Entry<String, Object> entry : headers.entrySet()) {
				String key = entry.getKey();
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

	@Override
	public Collection<String> getHeaderNames() {
		return headers.keySet();
	}

	@Override
	public final String getHeader(String key) {
		for (Entry<String, Object> entry : headers.entrySet()) {
			if (key.equalsIgnoreCase(entry.getKey())) {
				return entry.getValue().toString();
			}
		}
		return null;
	}

	public void setHeader(String name, String value) {
		headers.put(name, value);
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CachedResponse other = (CachedResponse) obj;
		if (!Arrays.equals(byteArray, other.byteArray))
			return false;
		if (charset == null) {
			if (other.charset != null)
				return false;
		} else if (!charset.equals(other.charset))
			return false;
		if (headers == null) {
			if (other.headers != null)
				return false;
		} else if (!headers.equals(other.headers))
			return false;
		if (localDate == null) {
			if (other.localDate != null)
				return false;
		} else if (!localDate.equals(other.localDate))
			return false;
		if (requestHeaders == null) {
			if (other.requestHeaders != null)
				return false;
		} else if (!requestHeaders.equals(other.requestHeaders))
			return false;
		if (statusCode != other.statusCode)
			return false;
		if (statusMessage == null) {
			if (other.statusMessage != null)
				return false;
		} else if (!statusMessage.equals(other.statusMessage))
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see net.webassembletool.resource.Resource#getRequestHeaders()
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
	 * 
	 * @param requestHeaders
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
	public void setRequestHeadersFromRequest(HttpServletRequest request) {
		@SuppressWarnings("unchecked")
		Enumeration<String> e = request.getHeaderNames();
		if (e != null && e.hasMoreElements()) {
			HashMap<String, String> headers = new HashMap<String, String>();
			while (e.hasMoreElements()) {
				String name = e.nextElement();
				headers.put(name, request.getHeader(name));
			}

			this.requestHeaders = headers;
		} else {
			this.requestHeaders = null;
		}
	}

	public CachedResponseSummary getSummary() {
		CachedResponseSummary s = new CachedResponseSummary();

		s.setResponseBody(this.hasResponseBody());
		s.setStatusCode(statusCode);
		s.setLocalDate(localDate);

		// Copy Response headers
		Map<String, Object> headers2 = new HashMap<String, Object>(headers);
		s.setHeaders(headers2);

		// Copy Request headers
		Map<String, String> requestHeaders2 = new HashMap<String, String>();
		if (requestHeaders != null) {
			requestHeaders2.putAll(requestHeaders);
		}
		s.setRequestHeaders(requestHeaders2);

		return s;
	}
}

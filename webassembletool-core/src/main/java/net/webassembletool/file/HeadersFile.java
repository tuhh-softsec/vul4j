package net.webassembletool.file;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

class HeadersFile {
	private final Map<String, Set<String>> headers;
	private int statusCode;
	private String statusMessage;

	public HeadersFile() {
		this(0, null);
	}

	public HeadersFile(int statusCode, String statusMessage) {
		this(new HashMap<String, Set<String>>(), statusCode, statusMessage);
	}

	public HeadersFile(Map<String, Set<String>> headers, int statusCode,
			String statusMessage) {
		this.headers = headers;
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
	}

	public void addHeader(String name, String value) {
		Set<String> values = null;
		for (Entry<String, Set<String>> entry : headers.entrySet()) {
			if (name.equalsIgnoreCase(entry.getKey())) {
				values = entry.getValue();
				break;
			}
		}
		if (values == null) {
			values = new TreeSet<String>();
			headers.put(name, values);
		}
		values.add(value);
	}

	public String getHeader(String key) {
		String result = null;
		for (Entry<String, Set<String>> entry : headers.entrySet()) {
			if (key.equalsIgnoreCase(entry.getKey())
					&& !entry.getValue().isEmpty()) {
				result = entry.getValue().iterator().next();
				break;
			}
		}
		return result;
	}

	public Collection<String> getHeaders(String key) {
		Collection<String> result = new TreeSet<String>();
		for (Entry<String, Set<String>> entry : headers.entrySet()) {
			if (key.equalsIgnoreCase(entry.getKey())) {
				result.addAll(entry.getValue());
				break;
			}
		}
		return result;
	}

	public Map<String, Set<String>> getHeadersMap() {
		return headers;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

}

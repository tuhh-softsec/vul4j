package net.webassembletool.file;

import java.util.HashMap;
import java.util.Map;

class HeadersFile {
	private final Map<String, Object> headersMap;
	private int statusCode;
	private String statusMessage;

	public HeadersFile() {
		this(0, null);
	}
	public HeadersFile(int statusCode, String statusMessage) {
		this(new HashMap<String, Object>(), statusCode, statusMessage);
	}

	public HeadersFile(Map<String, Object> headersMap, int statusCode, String statusMessage) {
		this.headersMap = headersMap;
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
	}

	public void addHeader(String key, Object value) {
		headersMap.put(key.toLowerCase(), value);
	}

	public Object getHeader(String key) {
		return headersMap.get(key.toLowerCase());
	}

	public Map<String, Object> getHeadersMap() {
		return headersMap;
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

package net.webassembletool.cache;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import net.webassembletool.resource.Resource;

/**
 * Base class for cached resources implementations.
 * 
 * @author Stanislav Bernatsky
 */
abstract class BaseCachedResource extends Resource implements Serializable {
	private static final long serialVersionUID = 1771883783632694287L;
	protected final Map<String, Set<String>> headers;
	protected final int statusCode;
	protected final String statusMessage;

	protected BaseCachedResource(Map<String, Set<String>> headers,
			int statusCode, String statusMessage) {
		this.headers = headers;
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
	}

	@Override
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

	@Override
	public Collection<String> getHeaders(String name) {
		Collection<String> result = new TreeSet<String>();
		for (Entry<String, Set<String>> entry : headers.entrySet()) {
			if (name.equalsIgnoreCase(entry.getKey())) {
				result.addAll(entry.getValue());
				break;
			}
		}
		return result;
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

}

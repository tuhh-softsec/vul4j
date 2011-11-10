package org.esigate.esi;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class InlineCache {

	private static final Map<String, InlineCache> cache = new HashMap<String, InlineCache>();

	private final Date outdate;
	private final boolean fetchable;
	private final String originalUrl;
	private final String fragment;

	public static void storeFragment(String uri, Date outdate,
			boolean fetchable, String originalUrl, String fragment) {
		InlineCache ic = new InlineCache(outdate, fetchable, originalUrl,
				fragment);
		cache.put(uri, ic);
	}

	public static InlineCache getFragment(String uri) {
		return cache.get(uri);
	}

	private InlineCache(Date outdate, boolean fetchable, String originalUrl,
			String fragment) {
		this.outdate = outdate;
		this.fetchable = fetchable;
		this.originalUrl = originalUrl;
		this.fragment = fragment;
	}

	public Date getOutdate() {
		return outdate;
	}

	public boolean isFetchable() {
		return fetchable;
	}

	public String getOriginalUrl() {
		return originalUrl;
	}

	public String getFragment() {
		return fragment;
	}

}

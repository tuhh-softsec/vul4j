package org.esigate.cache;

import java.io.IOException;

import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheStorage;
import org.apache.http.client.cache.HttpCacheUpdateCallback;
import org.apache.http.client.cache.HttpCacheUpdateException;
import org.esigate.extension.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CacheStorage implements HttpCacheStorage, Extension {
	private static final Logger LOG = LoggerFactory.getLogger(CacheStorage.class);

	protected HttpCacheStorage impl;

	public void putEntry(String key, HttpCacheEntry entry) throws IOException {
		LOG.info("putEntry(" + key + ")");
		impl.putEntry(key, entry);
	}

	public HttpCacheEntry getEntry(String key) throws IOException {
		LOG.info("getEntry(" + key + ")");
		return impl.getEntry(key);
	}

	public void removeEntry(String key) throws IOException {
		LOG.info("removeEntry(" + key + ")");
		impl.removeEntry(key);

	}

	public void updateEntry(String key, HttpCacheUpdateCallback callback) throws IOException, HttpCacheUpdateException {
		LOG.info("updateEntry(" + key + ")");
		impl.updateEntry(key, callback);
	}

}

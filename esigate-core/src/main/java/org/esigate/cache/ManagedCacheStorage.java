package org.esigate.cache;

import java.util.Properties;

import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.ManagedHttpCacheStorage;

public class ManagedCacheStorage extends CacheStorage {

	public void init(Properties properties) {
		CacheConfig cacheConfig = CacheConfigHelper.createCacheConfig(properties);
		impl = new ManagedHttpCacheStorage(cacheConfig);
	}

}

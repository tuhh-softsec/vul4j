package org.esigate.cache;

import java.util.Properties;

import org.apache.http.impl.client.cache.BasicHttpCacheStorage;
import org.apache.http.impl.client.cache.CacheConfig;

public class BasicCacheStorage extends CacheStorage {

	public void init(Properties properties) {
		CacheConfig cacheConfig = CacheConfigHelper.createCacheConfig(properties);
		impl = new BasicHttpCacheStorage(cacheConfig);
	}

}

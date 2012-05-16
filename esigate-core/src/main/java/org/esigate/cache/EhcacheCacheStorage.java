package org.esigate.cache;

import java.util.Properties;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.ehcache.EhcacheHttpCacheStorage;

public class EhcacheCacheStorage extends CacheStorage {
	public final static String CACHE_NAME_PROPERTY = "ehcache.cacheName";
	public final static String CONFIGURATION_FILE_PROPERTY = "ehcache.configurationFile";
	public final static String DEFAULT_CACHE_NAME = "EsiGate";

	public void init(Properties properties) {
		String cacheName = properties.getProperty(CACHE_NAME_PROPERTY, DEFAULT_CACHE_NAME);
		String configurationFileName = properties.getProperty(CACHE_NAME_PROPERTY);
		// Loaded from the Classpath, default will use /ehcache.xml or if not found /ehcache-failsafe.xml
		CacheManager cacheManager = CacheManager.create(configurationFileName);
		Ehcache ehcache = cacheManager.getEhcache(cacheName);
		if (ehcache == null) {
			cacheManager.addCache(cacheName);
			ehcache = cacheManager.getEhcache(cacheName);
		}
		CacheConfig cacheConfig = CacheConfigHelper.createCacheConfig(properties);
		impl = new EhcacheHttpCacheStorage(ehcache, cacheConfig);
	}

}

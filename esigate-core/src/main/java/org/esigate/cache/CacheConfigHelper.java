package org.esigate.cache;

import java.util.Properties;

import org.apache.http.impl.client.cache.CacheConfig;
import org.esigate.util.PropertiesUtil;

public class CacheConfigHelper {
	public final static String HEURISTIC_CACHING_ENABLED_PROPERTY = "cache.heuristicCachingEnabled";
	public final static String HEURISTIC_COEFFICIENT_PROPERTY = "cache.heuristicCoefficient";
	public final static String HEURISTIC_DEFAULT_LIFETIME_SECS_PROPERTY = "cache.heuristicDefaultLifetimeSecs";
	public final static String MAX_CACHE_ENTRIES_PROPERTY = "cache.maxCacheEntries";
	public final static String MAX_OBJECT_SIZE_PROPERTY = "cache.maxObjectSize";
	public final static String MIN_ASYNCHRONOUS_WORKERS_PROPERTY = "cache.minAsynchronousWorkers";
	public final static String MAX_ASYNCHRONOUS_WORKERS_PROPERTY = "cache.maxAsynchronousWorkers";
	public final static String ASYNCHRONOUS_WORKER_IDLE_LIFETIME_SECS_PROPERTY = "cache.asynchronousWorkerIdleLifetimeSecs";
	public final static String MAX_UPDATE_RETRIES_PROPERTY = "cache.maxUpdateRetries";
	public final static String REVALIDATION_QUEUE_SIZE_PROPERTY = "cache.revalidationQueueSize";

	public final static CacheConfig createCacheConfig(Properties properties) {
		// Heuristic caching
		boolean heuristicCachingEnabled = PropertiesUtil.getPropertyValue(properties, HEURISTIC_CACHING_ENABLED_PROPERTY, true);
		// default value defined in http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.2.4
		float heuristicCoefficient = PropertiesUtil.getPropertyValue(properties, HEURISTIC_COEFFICIENT_PROPERTY, 0.1f);
		// when no cache directive at all, nothing is cached by default
		long heuristicDefaultLifetimeSecs = PropertiesUtil.getPropertyValue(properties, HEURISTIC_DEFAULT_LIFETIME_SECS_PROPERTY, 0l); // Cache size
		int maxCacheEntries = PropertiesUtil.getPropertyValue(properties, MAX_CACHE_ENTRIES_PROPERTY, 1000);
		int maxObjectSize = PropertiesUtil.getPropertyValue(properties, MAX_OBJECT_SIZE_PROPERTY, 0);
		// Asynchronous revalidation
		int minAsynchronousWorkers = PropertiesUtil.getPropertyValue(properties, MAX_OBJECT_SIZE_PROPERTY, 1);
		int maxAsynchronousWorkers = PropertiesUtil.getPropertyValue(properties, MAX_OBJECT_SIZE_PROPERTY, 20);
		int asynchronousWorkerIdleLifetimeSecs = PropertiesUtil.getPropertyValue(properties, MAX_OBJECT_SIZE_PROPERTY, 60);
		int maxUpdateRetries = PropertiesUtil.getPropertyValue(properties, MAX_OBJECT_SIZE_PROPERTY, 1);
		int revalidationQueueSize = PropertiesUtil.getPropertyValue(properties, MAX_OBJECT_SIZE_PROPERTY, 100);

		CacheConfig cacheConfig = new CacheConfig();
		cacheConfig.setHeuristicCachingEnabled(heuristicCachingEnabled);
		cacheConfig.setHeuristicCoefficient(heuristicCoefficient);
		cacheConfig.setHeuristicDefaultLifetime(heuristicDefaultLifetimeSecs);
		cacheConfig.setMaxCacheEntries(maxCacheEntries);
		if (maxObjectSize > 0)
			cacheConfig.setMaxObjectSizeBytes(maxObjectSize);
		else
			cacheConfig.setMaxObjectSizeBytes(Integer.MAX_VALUE);
		cacheConfig.setAsynchronousWorkersCore(minAsynchronousWorkers);
		cacheConfig.setAsynchronousWorkersMax(maxAsynchronousWorkers);
		cacheConfig.setAsynchronousWorkerIdleLifetimeSecs(asynchronousWorkerIdleLifetimeSecs);
		cacheConfig.setMaxUpdateRetries(maxUpdateRetries);
		cacheConfig.setRevalidationQueueSize(revalidationQueueSize);
		return cacheConfig;
	}

}

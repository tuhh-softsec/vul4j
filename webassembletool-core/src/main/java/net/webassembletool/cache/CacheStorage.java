package net.webassembletool.cache;

import java.util.Properties;

public interface CacheStorage {
	/**
	 * Put value in the cache
	 * 
	 * @param key
	 * @param value
	 */
	void put(String key, Object value);

	/**
	 * Put value in the cache for defined time in milliseconds
	 * 
	 * @param key
	 * @param value
	 * @param ttl
	 */
	void put(String key, Object value, long ttl);

	/**
	 * Return value by key
	 * 
	 * @param key
	 */
	Object get(String key);

	/**
	 * Return value by key
	 * 
	 * @param key
	 */
	<T> T get(String key, Class<T> clazz);

	/**
	 * Touch value
	 * 
	 * @param key
	 */
	void touch(String key);

	/**
	 * Initialize the cacheStorage using the given properties.
	 * 
	 * @param properties
	 */
	public void init(Properties properties);

}

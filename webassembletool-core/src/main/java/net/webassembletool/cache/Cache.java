package net.webassembletool.cache;

import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;

/**
 * Represents a local interface to caching system.
 * <p>
 * Currently only opensimphony's OScache is supported.
 * 
 * @author Francois-Xavier Bonnet
 */
/*
 * TODO: Rewrite a caching system that will not depend on Oscache, non blocking
 * and thread-safe
 */
public class Cache {
	private GeneralCacheAdministrator cache;
	private int expirationDelay;

	public Cache(int expirationDelay) {
		cache = new GeneralCacheAdministrator();
		this.expirationDelay = expirationDelay;
	}

	public void put(String key, CachedResponse resource) {
		cache.putInCache(key, resource);
	}

	/**
	 * Returns a cache entry.
	 * 
	 * @param key
	 *            key of the cache entry
	 * @return the cache entry if any
	 */
	public CachedResponse get(String key) {
		CachedResponse memoryResource = null;
		try {
			memoryResource = (CachedResponse) cache.getFromCache(key);
			memoryResource = (CachedResponse) cache.getFromCache(key,
					expirationDelay);
		} catch (NeedsRefreshException e1) {
			// Not in cache
			if (memoryResource != null)
				memoryResource.setStale();
		}
		return memoryResource;
	}

	public void cancelUpdate(String key) {
		cache.cancelUpdate(key);
	}

}

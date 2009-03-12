package net.webassembletool.cache;

import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;

/**
 * Represents a local interface to caching sistem.
 * <p>
 * Currently only opensimphony's OScache is supported.
 * 
 * @author François-Xavier Bonnet
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

    public void put(String key, MemoryResource resource) {
        cache.putInCache(key, resource);
    }

    /**
     * Returns a cache entry.
     */
    public MemoryResource get(String key) {
        MemoryResource memoryResource = null;
        try {
            memoryResource = (MemoryResource) cache.getFromCache(key);
            memoryResource = (MemoryResource) cache.getFromCache(key,
                    expirationDelay);
        } catch (NeedsRefreshException e1) {
            // Not in cache
        	if (memoryResource!=null)
        		memoryResource.setStale();
        }
        return memoryResource;
    }

    public void cancelUpdate(String key) {
        cache.cancelUpdate(key);
    }

}

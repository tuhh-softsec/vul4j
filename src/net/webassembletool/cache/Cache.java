package net.webassembletool.cache;

import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;

/**
 * TODO Type javadoc
 * 
 * @author François-Xavier Bonnet
 */
public class Cache {
    private GeneralCacheAdministrator cache;
    private int expirationDelay;

    @SuppressWarnings("unused")
    private Cache() {
	// Not to be used
    }

    public Cache(int expirationDelay) {
	cache = new GeneralCacheAdministrator();
	this.expirationDelay = expirationDelay;
    }

    public void put(String key, MemoryResource resource) {
	cache.putInCache(key, resource);
    }

    public MemoryResource get(String key) {
	MemoryResource memoryResource = null;
	try {
	    memoryResource = (MemoryResource) cache.getFromCache(key);
	    memoryResource.setStale(true);
	    memoryResource = (MemoryResource) cache.getFromCache(key,
		    expirationDelay);
	    memoryResource.setStale(false);
	} catch (NeedsRefreshException e1) {
	    // Not in cache
	}
	return memoryResource;
    }

    public void cancelUpdate(String key) {
	cache.cancelUpdate(key);
    }

}

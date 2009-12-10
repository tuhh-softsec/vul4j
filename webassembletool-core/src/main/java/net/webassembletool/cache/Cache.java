package net.webassembletool.cache;

import java.util.Map;

import net.webassembletool.HttpErrorPage;
import net.webassembletool.ResourceContext;
import net.webassembletool.resource.Resource;

/**
 * Represents a local interface to caching system.
 * <p>
 * Curently only opensimphony's OScache is supported.
 * 
 * @author Francois-Xavier Bonnet
 */
public class Cache {
	private final Storage storage;

	public Cache() {
		storage = new Storage();
	}

	private CacheEntry getCacheEntry(ResourceContext resourceContext) {
		String url = resourceContext.getRelUrl();
		CacheEntry cacheEntry = (CacheEntry) storage.get(url);
		if (cacheEntry == null) {
			cacheEntry = new CacheEntry(url, storage);
			storage.put(url, cacheEntry);
		}
		return cacheEntry;
	}

	public CachedResponse get(ResourceContext resourceContext) {
		return getCacheEntry(resourceContext).get(resourceContext);
	}

	public Map<String, String> getValidators(ResourceContext resourceContext,
			CachedResponse cachedResponse) {
		return getCacheEntry(resourceContext).getValidators(resourceContext,
				cachedResponse);
	}

	public Resource select(ResourceContext resourceContext,
			CachedResponse oldResponse, Resource newResponse)
			throws HttpErrorPage {
		return getCacheEntry(resourceContext).select(resourceContext,
				oldResponse, newResponse);
	}

	public void put(ResourceContext resourceContext, CachedResponse resource) {
		getCacheEntry(resourceContext).put(resourceContext, resource);
	}
}

package org.esigate.cache;

import java.util.Map;

import org.esigate.HttpErrorPage;
import org.esigate.ResourceContext;
import org.esigate.http.HttpHeaders;
import org.esigate.resource.Resource;
import org.esigate.resource.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a local interface to caching system.
 * <p>
 * Currently only opensimphony's OScache is supported.
 * 
 * @author Francois-Xavier Bonnet
 */
public class Cache {
	private static Logger LOG = LoggerFactory.getLogger(Cache.class);
	private CacheStorage storage;


	private CacheEntry getCacheEntry(ResourceContext resourceContext) {
		String key = ResourceUtils.getHttpUrlWithQueryString(resourceContext);

		if (resourceContext.getDriver().getConfiguration().isPreserveHost()) {
			// Prefix with host in order to ensure content will not be mixed
			// when using preserveHost = true and name virtual host on the
			// provider.
			String host = resourceContext.getOriginalRequest().getHeader(HttpHeaders.HOST);
			if (host != null) {
				key = host + "=>" + key;
			}
		}

		CacheEntry cacheEntry = storage.get(key, CacheEntry.class);
		if (cacheEntry == null) {
			cacheEntry = new CacheEntry(key, storage);
			putToStorage(cacheEntry, resourceContext);
		} else {
			// Storage is not persisted into cache. It need to be set each time
			// the cache entry is read from cache.
			cacheEntry.setStorage(storage);
		}
		return cacheEntry;
	}

	public CachedResponse get(ResourceContext resourceContext) {
		CacheEntry entry = getCacheEntry(resourceContext);
		CachedResponse reponse = entry.get(resourceContext);

		// Getting content may update the list of response (remove expired
		// items)
		// So we have to update the cache.
		if (entry.isDirty()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("CacheEntry was updated during get. Updating cache");
			}
			entry.setDirty(false);
			putToStorage(entry, resourceContext);
		}

		return reponse;
	}

	public Map<String, String> getValidators(ResourceContext resourceContext,
			CachedResponse cachedResponse) {
		return getCacheEntry(resourceContext).getValidators(resourceContext,
				cachedResponse);
	}

	public Resource select(ResourceContext resourceContext,
			CachedResponse oldResponse, Resource newResponse)
			throws HttpErrorPage {
		CacheEntry entry = getCacheEntry(resourceContext);
		Resource resource = entry.select(resourceContext, oldResponse,
				newResponse);

		// Reading entry may update the list of response (remove expired
		// items)
		// So we have to update the cache.
		if (entry.isDirty()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("CacheEntry was updated during get. Updating cache");
			}
			entry.setDirty(false);
			putToStorage(entry, resourceContext);
		}

		return resource;
	}

	public void put(ResourceContext resourceContext, CachedResponse resource) {
		CacheEntry entry = getCacheEntry(resourceContext);
		entry.put(resourceContext, resource);
		// Entry will be set to dirty during put.
		
		entry.setDirty(false);
		putToStorage(entry, resourceContext);
	}

	public void setStorage(CacheStorage storage) {
		this.storage = storage;
	}
	
	private void putToStorage(CacheEntry entry, ResourceContext resourceContext) {
		boolean noStore = resourceContext.getOriginalRequest().isNoStoreResource();
		// if no-store is "on", assume -1 no store in cache
		if (!noStore) {
			Long ttl = resourceContext.getOriginalRequest().getResourceTtl();
			if (ttl == null) {
				storage.put(entry.getUrl(), entry);
			} else {
				storage.put(entry.getUrl(), entry, ttl.longValue());
			}
		}

	}
}

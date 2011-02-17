package net.webassembletool.cache;

import java.util.Map;

import net.webassembletool.HttpErrorPage;
import net.webassembletool.ResourceContext;
import net.webassembletool.http.HttpHeaders;
import net.webassembletool.resource.Resource;
import net.webassembletool.resource.ResourceUtils;

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
	private final Storage storage;

	public Cache() {
		storage = new Storage();
	}

	private CacheEntry getCacheEntry(ResourceContext resourceContext) {
		String key = ResourceUtils.getHttpUrlWithQueryString(resourceContext);
		String host = resourceContext.getOriginalRequest().getHeader(
				HttpHeaders.HOST);

		if (resourceContext.getDriver().getConfiguration().isPreserveHost()) {
			// Prefix with host in order to ensure content will not be mixed
			// when using preserveHost = true and name virtual host on the
			// provider.
			if (host != null) {
				key = host + "=>" + key;
			}
		}

		CacheEntry cacheEntry = (CacheEntry) storage.get(key);
		if (cacheEntry == null) {
			cacheEntry = new CacheEntry(key, storage);
			storage.put(key, cacheEntry);
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
			storage.put(entry.getUrl(), entry);
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
			storage.put(entry.getUrl(), entry);
		}

		return resource;
	}

	public void put(ResourceContext resourceContext, CachedResponse resource) {
		CacheEntry entry = getCacheEntry(resourceContext);
		entry.put(resourceContext, resource);
		// Entry will be set to dirty during put.
		entry.setDirty(false);
		storage.put(entry.getUrl(), entry);
	}
}

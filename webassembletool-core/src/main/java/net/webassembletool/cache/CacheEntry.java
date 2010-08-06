package net.webassembletool.cache;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import net.webassembletool.HttpErrorPage;
import net.webassembletool.ResourceContext;
import net.webassembletool.resource.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Francois-Xavier Bonnet
 * @author Nicolas Richeton
 */
class CacheEntry {
	private static Log LOG = LogFactory.getLog(CacheEntry.class);
	private final String url;
	private transient Storage storage;
	private transient boolean dirty;
	private long lastClean = -1;
	private static long CLEAN_DELAY = 15 * 60 * 1000; // 15 minutes;

	/**
	 * A list a all responses for this ResourceContext. Only includes
	 * informations which can be used to decide if a cached response matches to
	 * a new resource context. On matching, the complete cache value need to be
	 * retrieved from cached.
	 * 
	 * <p>
	 * Responses described in responseSummaries may have been removed from
	 * cache. In that case, the retrieve operation fails and the summary is
	 * removed from this entry.
	 * 
	 */
	private final List<CachedResponseSummary> responseSummaries = new CopyOnWriteArrayList<CachedResponseSummary>();

	public String getUrl() {
		return url;
	}

	private static Pattern ETAG_PATTERN = Pattern
			.compile(",?\\s*((W/)?\"[^\"]*\")");

	public CacheEntry(String url, Storage storage) {
		this.url = url;
		this.storage = storage;
		this.dirty = false;
	}

	/**
	 * Returns the first matching cache entry for this request, this entry may
	 * be valid or stale but Etag must match If-None-Match header from the
	 * request and vary headers must be the same in the request and in the
	 * original request that caused this entry to get cached.
	 * <p>
	 * Be sure to check CacheEntry#isDirty() on return. Entry content may have
	 * been updated thus need to be persisted.
	 * 
	 * @param resourceContext
	 * @return the first matching cache entry for this request
	 */
	public CachedResponse get(ResourceContext resourceContext) {
		CachedResponse result = null;

		if (LOG.isDebugEnabled()) {
			LOG.debug("get(" + resourceContext.getRelUrl() + ")");
		}

		for (CachedResponseSummary summary : responseSummaries) {

			if (Rfc2616.matches(resourceContext, summary)) {
				CachedResponse cachedResponse = getCacheResponseAndClean(summary);

				if (cachedResponse != null) {
					result = cachedResponse;
					storage.touch(summary.getCacheKey());
					if (LOG.isDebugEnabled()) {
						LOG.debug("get(" + summary + ")=" + result);
					}
				}
			}
		}

		if (LOG.isDebugEnabled() && result == null) {
			LOG.debug("get(" + resourceContext.getRelUrl() + ") : Not found.");
		}

		return result;
	}

	/**
	 * 
	 * Returns a cached response from cache. If the response is no longer in
	 * cache, any reference in CacheEntry is cleaned and entry is marked dirty.
	 * 
	 * @param summary
	 *            Summary of cached response
	 * @return CachedResponse or null if no longer in cache.
	 */
	private CachedResponse getCacheResponseAndClean(
			CachedResponseSummary summary) {
		CachedResponse cachedResponse = (CachedResponse) storage.get(summary
				.getCacheKey());

		// Handle case when resource is no longer in cache.
		if (cachedResponse == null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Resource " + summary.getCacheKey()
						+ "is no longer in cache. Removing");
			}
			if (responseSummaries.contains(summary)) {
				responseSummaries.remove(summary);
				dirty = true;
			}
		}

		return cachedResponse;
	}

	/**
	 * Return the first existing CacheResponse. If responses have been removed
	 * from cache, entry is updated and marked dirty.
	 * 
	 * 
	 * @return CachedResponse or null
	 */
	private CachedResponse getFirstCacheResponse() {
		CachedResponse cachedResponse = null;
		for (CachedResponseSummary summary : responseSummaries) {
			cachedResponse = getCacheResponseAndClean(summary);
			if (cachedResponse != null) {
				break;
			}
		}
		return cachedResponse;
	}

	/**
	 * Computes http entity tags in the incoming request with the ones in all
	 * the cached response in order to make a conditional request to the server
	 * with headers "If-Modified-Since" and "If-None-Match"
	 * 
	 * @param resourceContext
	 *            The resource we want to get
	 * @param cachedResponse
	 *            The current stale version of the resource
	 * @return The headers "If-Modified-Since" and "If-None-Match" to add to the
	 *         request
	 */
	public Map<String, String> getValidators(ResourceContext resourceContext,
			CachedResponse cachedResponse) {
		HashMap<String, String> result = new HashMap<String, String>();
		String ifNoneMatch = getIfNoneMatch(resourceContext);
		if (ifNoneMatch != null) {
			result.put("If-None-Match", ifNoneMatch);
		}
		String ifModifiedSince = getIfModifiedSince(resourceContext,
				cachedResponse);
		if (ifModifiedSince != null) {
			result.put("If-Modified-Since", ifModifiedSince);
		}
		return result;
	}

	private String getIfNoneMatch(ResourceContext resourceContext) {
		Set<String> etags = new HashSet<String>();
		if (resourceContext.isProxy()
				&& !resourceContext.isNeededForTransformation()) {
			String ifNoneMatch = resourceContext.getOriginalRequest()
					.getHeader("If-None-Match");
			if (ifNoneMatch != null) {
				Matcher matcher = ETAG_PATTERN.matcher(ifNoneMatch);
				while (!matcher.hitEnd()) {
					if (matcher.find()) {
						etags.add(matcher.group(1));
					}
				}
			}
		}
		for (CachedResponseSummary key : responseSummaries) {
			CachedResponse cachedResponse = getCacheResponseAndClean(key);

			if (cachedResponse != null) {
				String etag = cachedResponse.getHeader("Etag");
				if (etag != null && cachedResponse.hasResponseBody()) {
					etags.add(etag);
				}
			}
		}
		if (!etags.isEmpty()) {
			Iterator<String> iterator = etags.iterator();
			StringBuilder etagsString = new StringBuilder(iterator.next());
			while (iterator.hasNext()) {
				etagsString.append(", ").append(iterator.next());
			}
			return etagsString.toString();
		}
		return null;
	}

	private String getIfModifiedSince(ResourceContext resourceContext,
			CachedResponse cachedResponse) {
		String requestedIfModifiedSinceString = resourceContext
				.getOriginalRequest().getHeader("If-Modified-Since");
		Date requestedIfModifiedSinceDate = Rfc2616.getDateHeader(
				resourceContext, "If-Modified-Since");
		String cacheLastModifiedString = null;
		Date cacheLastModifiedDate = null;
		if (cachedResponse != null && cachedResponse.hasResponseBody()) {
			cacheLastModifiedString = cachedResponse.getHeader("Last-modified");
			cacheLastModifiedDate = Rfc2616.getDateHeader(cachedResponse,
					"Last-modified");
		}
		if (resourceContext.isNeededForTransformation()
				|| requestedIfModifiedSinceDate == null
				|| (cacheLastModifiedDate != null && cacheLastModifiedDate
						.after(requestedIfModifiedSinceDate))) {
			return cacheLastModifiedString;
		}
		return requestedIfModifiedSinceString;
	}

	/**
	 * Selects the response to the request between a cache entry (if return code
	 * is 304) and the resource sent by the server (if return code is 200).
	 * Updates the cache.
	 * 
	 * <p>
	 * Be sure to check CacheEntry#isDirty() on return. Entry content may have
	 * been updated thus need to be persisted.
	 * 
	 * @param resourceContext
	 * @param cachedResponse
	 * @param newResource
	 * @return The response to send to the client
	 * @throws HttpErrorPage
	 */
	public Resource select(ResourceContext resourceContext,
			CachedResponse cachedResponse, Resource newResource)
			throws HttpErrorPage {
		if (LOG.isDebugEnabled()) {
			LOG.debug("select(" + resourceContext.getRelUrl() + ")");
		}

		Resource result = null;
		if (newResource.getStatusCode() == HttpServletResponse.SC_NOT_MODIFIED) {
			String etag = Rfc2616.getEtag(newResource);
			if (etag == null) {
				// No e-tag specified by the server
				// The not modified response is for the if-modified-since we
				// sent
				String sentIfModifiedSince = getIfModifiedSince(
						resourceContext, cachedResponse);
				if (sentIfModifiedSince != null) {
					if (!resourceContext.isNeededForTransformation()
							&& sentIfModifiedSince.equals(resourceContext
									.getOriginalRequest().getHeader(
											"If-Modified-Since"))) {
						result = newResource;
					} else {
						result = cachedResponse;
					}
				} else {
					// Buggy behaviour from the server, it should not send a 304
					// for a if-none-match request without the etag to select.
					// Let's take the first.
					if (cachedResponse != null) {
						result = cachedResponse;
					} else if (getFirstCacheResponse() != null) {
						result = getFirstCacheResponse();
					}
				}
			} else {
				if (!resourceContext.isNeededForTransformation()
						&& Rfc2616.etagMatches(resourceContext, newResource)) {
					result = newResource;
				} else {
					result = findByEtag(etag);
				}
			}
			if (cachedResponse != null) {
				updateHeaders(cachedResponse, newResource);
			}
			if (result == null) {
				LOG.warn("Invalid 304 response, neededForTransformation: "
						+ resourceContext.isNeededForTransformation()
						+ " etag: " + etag);
				throw new HttpErrorPage(
						HttpServletResponse.SC_PRECONDITION_FAILED,
						"Invalid 304 response", "Invalid 304 response");
			}
		} else {
			result = newResource;
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("select(" + resourceContext.getRelUrl() + ")=" + result);
		}

		return result;
	}

	private CachedResponse findByEtag(String etag) {
		LOG.debug("findByEtag(" + etag + ")");
		for (CachedResponseSummary summary : responseSummaries) {

			if (etag.equals(summary.getHeader("Etag"))) {
				// seems the right resource. Try to get it from cache
				CachedResponse cachedResponse = getCacheResponseAndClean(summary);
				if (cachedResponse != null) {
					return cachedResponse;
				}
				// Response was no longer in cache : continue.
			}

		}
		return null;
	}

	private void copyHeader(Resource source, CachedResponse dest, String name) {
		String value = source.getHeader(name);
		if (value != null) {
			dest.setHeader(name, value);
		}
	}

	private void updateHeaders(CachedResponse cachedResponse,
			Resource newResource) {
		copyHeader(newResource, cachedResponse, "Date");
		copyHeader(newResource, cachedResponse, "Content-Type");
		copyHeader(newResource, cachedResponse, "Content-Length");
		copyHeader(newResource, cachedResponse, "Last-Modified");
		copyHeader(newResource, cachedResponse, "ETag");
		copyHeader(newResource, cachedResponse, "Expires");
		copyHeader(newResource, cachedResponse, "Cache-control");
		copyHeader(newResource, cachedResponse, "Content-encoding");
	}

	/**
	 * Add a response to the cache.
	 * <p>
	 * Be sure to check CacheEntry#isDirty() on return. Entry content will
	 * probably be updated thus need to be persisted.
	 * 
	 * @param resourceContext
	 *            must be the resourceContext used to get resource. Not the one
	 *            of this cache entry.
	 * @param resource
	 *            the new response.
	 */
	public void put(ResourceContext resourceContext, CachedResponse resource) {
		// Don't put in cache null or not modified responses
		if (resource != null
				&& resource.getStatusCode() != HttpServletResponse.SC_NOT_MODIFIED) {

			// Inject headers from the original request.
			resource.setRequestHeadersFromRequest(resourceContext
					.getOriginalRequest());

			String key = getCacheKey(resourceContext, resource);

			CachedResponseSummary summary = resource.getSummary();
			summary.setCacheKey(key);

			if (responseSummaries.contains(summary)) {
				responseSummaries.remove(summary);
			}
			responseSummaries.add(summary);
			storage.put(key, resource);
			dirty = true;
			if (LOG.isDebugEnabled()) {
				LOG.debug("put(" + key + ")");
			}
		}

		// Cleanup
		if (System.currentTimeMillis() - lastClean > CLEAN_DELAY) {
			// Ensure only a single thread starts cleaning.
			synchronized (this) {
				if (System.currentTimeMillis() - lastClean > CLEAN_DELAY) {
					for (CachedResponseSummary summary : responseSummaries) {
						// Delete no longer existing responses.
						getCacheResponseAndClean(summary);
					}
					lastClean = System.currentTimeMillis();
				}
			}
		}
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	/**
	 * Create a cache key depending on url, etag and Vary headers.
	 * 
	 * @param resourceContext
	 * @param resource
	 * @return
	 */
	private String getCacheKey(ResourceContext resourceContext,
			CachedResponse resource) {
		StringBuilder cacheKey = new StringBuilder();
		cacheKey.append(url).append(" ");
		String etag = Rfc2616.getEtag(resource);

		if (etag != null) {
			cacheKey.append(" etag=").append(etag);
		}

		Map<String, String> vary = Rfc2616.getVary(resourceContext, resource);
		if (vary != null) {
			cacheKey.append(" vary={");
			for (Iterator<Entry<String, String>> iterator = vary.entrySet()
					.iterator(); iterator.hasNext();) {
				Entry<String, String> header = iterator.next();
				cacheKey.append(header.getKey()).append("=")
						.append(header.getValue()).append(";");
			}
			cacheKey.append("}");
		}
		return cacheKey.toString();
	}

	public Storage getStorage() {
		return storage;
	}

	public void setStorage(Storage storage) {
		this.storage = storage;
	}
}

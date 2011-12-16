package org.esigate.cache;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.esigate.HttpErrorPage;
import org.esigate.ResourceContext;
import org.esigate.api.HttpStatusConstants;
import org.esigate.http.HttpHeaders;
import org.esigate.resource.Resource;
import org.esigate.util.Rfc2616;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Francois-Xavier Bonnet
 * @author Nicolas Richeton
 */
class CacheEntry implements Serializable {
	private static final long serialVersionUID = 7110248280110189961L;
	private static final Logger LOG = LoggerFactory.getLogger(CacheEntry.class);
	private static final long CLEAN_DELAY = 15 * 60 * 1000; // 15 minutes;
	private static final Pattern ETAG_PATTERN = Pattern.compile(",?\\s*((W/)?\"[^\"]*\")");

	private final String url;
	private transient CacheStorage storage;
	private transient boolean dirty;
	private long lastClean = -1;

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
	private final Set<CachedResponseSummary> responseSummaries = new CopyOnWriteArraySet<CachedResponseSummary>();

	public String getUrl() {
		return url;
	}

	public CacheEntry(String url, CacheStorage storage) {
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
	private CachedResponse getCacheResponseAndClean(CachedResponseSummary summary) {
		CachedResponse cachedResponse = storage.get(summary.getCacheKey(), CachedResponse.class);

		// Handle case when resource is no longer in cache.
		if (cachedResponse == null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Resource " + summary.getCacheKey()
						+ "is no longer in cache. Removing");
			}
			dirty = responseSummaries.remove(summary);
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
			Resource cachedResponse) {
		HashMap<String, String> result = new HashMap<String, String>();
		String ifNoneMatch = getIfNoneMatch(resourceContext);
		if (ifNoneMatch != null) {
			result.put(HttpHeaders.IF_NONE_MATCH, ifNoneMatch);
		}
		String ifModifiedSince = getIfModifiedSince(resourceContext,
				cachedResponse);
		if (ifModifiedSince != null) {
			result.put(HttpHeaders.IF_MODIFIED_SINCE, ifModifiedSince);
		}
		return result;
	}

	private String getIfNoneMatch(ResourceContext resourceContext) {
		Set<String> etags = new HashSet<String>();
		if (resourceContext.isProxy()
				&& !resourceContext.isNeededForTransformation()) {
			String ifNoneMatch = resourceContext.getOriginalRequest()
					.getHeader(HttpHeaders.IF_NONE_MATCH);
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
				String etag = cachedResponse.getHeader(HttpHeaders.ETAG);
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
			Resource cachedResponse) {
		String requestedIfModifiedSinceString = resourceContext
				.getOriginalRequest().getHeader(HttpHeaders.IF_MODIFIED_SINCE);
		Date requestedIfModifiedSinceDate = Rfc2616.getDateHeader(
				resourceContext, HttpHeaders.IF_MODIFIED_SINCE);
		String cacheLastModifiedString = null;
		Date cacheLastModifiedDate = null;
		if (cachedResponse != null && cachedResponse.hasResponseBody()) {
			cacheLastModifiedString = cachedResponse
					.getHeader(HttpHeaders.LAST_MODIFIED);
			cacheLastModifiedDate = Rfc2616.getDateHeader(cachedResponse,
					HttpHeaders.LAST_MODIFIED);
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
		if (newResource.getStatusCode() == HttpStatusConstants.SC_NOT_MODIFIED) {
			String etag = Rfc2616.getEtag(newResource);
			if (etag == null) {
				// No e-tag specified by the server
				// The not-modified response is for the if-modified-since we
				// sent
				String sentIfModifiedSince = getIfModifiedSince(
						resourceContext, cachedResponse);
				if (sentIfModifiedSince != null) {
					if (!resourceContext.isNeededForTransformation()
							&& sentIfModifiedSince.equals(resourceContext
									.getOriginalRequest().getHeader(
											HttpHeaders.IF_MODIFIED_SINCE))) {
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
						&& ((resourceContext.getOriginalRequest().getHeader(
								HttpHeaders.IF_NONE_MATCH) != null && Rfc2616
								.etagMatches(resourceContext, newResource)) || resourceContext
								.getOriginalRequest().getHeader(
										HttpHeaders.IF_MODIFIED_SINCE) != null)) {

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
						HttpStatusConstants.SC_PRECONDITION_FAILED,
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

			if (etag.equals(Rfc2616.getEtag(summary))) {
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
		Collection<String> values = source.getHeaders(name);
		for (String value : values) {
			dest.addHeader(name, value);
		}
	}

	private void updateHeaders(CachedResponse cachedResponse,
			Resource newResource) {
		copyHeader(newResource, cachedResponse, HttpHeaders.DATE);
		copyHeader(newResource, cachedResponse, HttpHeaders.CONTENT_TYPE);
		copyHeader(newResource, cachedResponse, HttpHeaders.CONTENT_LENGTH);
		copyHeader(newResource, cachedResponse, HttpHeaders.LAST_MODIFIED);
		copyHeader(newResource, cachedResponse, HttpHeaders.ETAG);
		copyHeader(newResource, cachedResponse, HttpHeaders.EXPIRES);
		copyHeader(newResource, cachedResponse, HttpHeaders.CACHE_CONTROL);
		copyHeader(newResource, cachedResponse, HttpHeaders.CONTENT_ENCODING);
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
				&& resource.getStatusCode() != HttpStatusConstants.SC_NOT_MODIFIED) {

			// Inject headers from the original request.
			resource.setRequestHeadersFromRequest(resourceContext.getOriginalRequest());

			String key = getCacheKey(resourceContext, resource);

			CachedResponseSummary summary = resource.getSummary(key);

			responseSummaries.remove(summary);
			responseSummaries.add(summary);
			storage.put(key, resource);
			dirty = true;
			if (LOG.isDebugEnabled()) {
				LOG.debug("put(" + key + ")");
			}
		}

		// Cleanup
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

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(url).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CacheEntry)) {
			return false;
		}

		CacheEntry other = (CacheEntry) obj;
		return new EqualsBuilder()
			.append(url, other.url)
			.isEquals();
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	/**
	 * Create a cache key for {@linkplain CachedResponse} depending on url, etag and Vary headers.
	 * 
	 * @param resourceContext
	 * @param resource
	 * @return
	 */
	private String getCacheKey(ResourceContext resourceContext, Resource resource) {
		StringBuilder cacheKey = new StringBuilder()
			.append(CachedResponse.class.getName()).append(" ")
			.append(url).append(" ");

		String etag = Rfc2616.getEtag(resource);
		if (etag != null) {
			cacheKey.append(" etag=").append(etag);
		}

		Map<String, String> vary = Rfc2616.getVary(resourceContext, resource);
		if (vary != null) {
			cacheKey.append(" vary={");
			for (Entry<String, String> header : vary.entrySet()) {
				cacheKey.append(header.getKey())
					.append("=")
					.append(header.getValue())
					.append(";");
			}
			cacheKey.append("}");
		}
		return cacheKey.toString();
	}

	public void setStorage(CacheStorage storage) {
		this.storage = storage;
	}
}

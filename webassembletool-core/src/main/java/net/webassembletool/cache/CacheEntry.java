package net.webassembletool.cache;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import net.webassembletool.HttpErrorPage;
import net.webassembletool.ResourceContext;
import net.webassembletool.resource.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class CacheEntry {
	private final static Log LOG = LogFactory.getLog(CacheEntry.class);
	private final String url;
	private final Storage storage;
	private Map<String, CachedResponse> cachedResponses = Collections
			.synchronizedMap(new WeakHashMap<String, CachedResponse>());
	private final static Pattern ETAG_PATTERN = Pattern
			.compile(",?\\s*((W/)?\"[^\"]*\")");

	public CacheEntry(String url, Storage storage) {
		this.url = url;
		this.storage = storage;
	}

	/**
	 * Returns the first matching cache entry for this request, this entry may
	 * be valid or stale but Etag must match If-None-Match header from the
	 * request and vary headers must be the same in the request and in the
	 * original request that caused this entry to get cached.
	 * 
	 * @param resourceContext
	 * @return the first matching cache entry for this request
	 */
	public CachedResponse get(ResourceContext resourceContext) {
		CachedResponse result = null;
		for (Iterator<CachedResponse> iterator = cachedResponses.values()
				.iterator(); iterator.hasNext();) {
			CachedResponse cachedResponse = iterator.next();
			if (Rfc2616.matches(resourceContext, cachedResponse)) {
				result = cachedResponse;
				String key = getCacheKey(resourceContext, result);
				storage.touch(key);
				if (LOG.isDebugEnabled()) {
					LOG.debug("get(" + key + ")=" + result);
				}
			}
		}
		return result;
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
		if (ifNoneMatch != null)
			result.put("If-None-Match", ifNoneMatch);
		String ifModifiedSince = getIfModifiedSince(resourceContext,
				cachedResponse);
		if (ifModifiedSince != null)
			result.put("If-Modified-Since", ifModifiedSince);
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
		for (Iterator<CachedResponse> iterator = cachedResponses.values()
				.iterator(); iterator.hasNext();) {
			CachedResponse cachedResponse = iterator.next();
			String etag = cachedResponse.getHeader("Etag");
			if (etag != null && cachedResponse.hasResponseBody()) {
				etags.add(etag);
			}
		}
		if (!etags.isEmpty()) {
			Iterator<String> iterator = etags.iterator();
			String etagsString = iterator.next();
			while (iterator.hasNext())
				etagsString += ", " + iterator.next();
			return etagsString;
		}
		return null;
	}

	private String getIfModifiedSince(ResourceContext resourceContext,
			CachedResponse cachedResponse) {
		Date requestedIfModifiedSinceDate = Rfc2616.getDateHeader(
				resourceContext, "If-Modified-Since");
		Date cacheLastModifiedDate = null;
		if (cachedResponse != null && cachedResponse.hasResponseBody()) {
			cacheLastModifiedDate = Rfc2616.getDateHeader(cachedResponse,
					"Last-modified");
			if (resourceContext.isNeededForTransformation()
					|| requestedIfModifiedSinceDate == null
					|| (cacheLastModifiedDate != null && cacheLastModifiedDate
							.after(requestedIfModifiedSinceDate)))
				return cachedResponse.getHeader("Last-modified");
		}
		return resourceContext.getOriginalRequest().getHeader(
				"If-Modified-Since");
	}

	/**
	 * Selects the response to the request between a cache entry (if return code
	 * is 304) and the resource sent by the server (if return code is 200).
	 * Updates the cache.
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
											"If-Modified-Since")))
						result = newResource;
					else
						result = cachedResponse;
				}
			} else {
				if (!resourceContext.isNeededForTransformation()
						&& Rfc2616.etagMatches(resourceContext, newResource))
					result = newResource;
				else
					result = findByEtag(etag);
			}
			if (cachedResponse != null)
				updateHeaders(cachedResponse, newResource);
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
		return result;
	}

	private CachedResponse findByEtag(String etag) {
		LOG.debug("findByEtag(" + etag + ")");
		for (Iterator<CachedResponse> iterator = cachedResponses.values()
				.iterator(); iterator.hasNext();) {
			CachedResponse cachedResponse = iterator.next();
			if (etag.equals(cachedResponse.getHeader("Etag")))
				return cachedResponse;
		}
		return null;
	}

	private void copyHeader(Resource source, CachedResponse dest, String name) {
		String value = source.getHeader(name);
		if (value != null)
			dest.setHeader(name, value);
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

	public void put(ResourceContext resourceContext, CachedResponse resource) {
		// Don't put in cache null or not modified responses
		if (resource != null
				&& resource.getStatusCode() != HttpServletResponse.SC_NOT_MODIFIED) {
			String key = getCacheKey(resourceContext, resource);
			cachedResponses.put(key, resource);
			storage.put(key, resource);
			LOG.debug("put(" + key + ")");
		}
	}

	private String getCacheKey(ResourceContext resourceContext,
			CachedResponse resource) {
		String cacheKey = url + " ";
		String etag = Rfc2616.getEtag(resource);
		if (etag != null)
			cacheKey += " etag=" + etag;
		Map<String, String> vary = Rfc2616.getVary(resourceContext, resource);
		if (vary != null) {
			cacheKey += " vary={";
			for (Iterator<Entry<String, String>> iterator = vary.entrySet()
					.iterator(); iterator.hasNext();) {
				Entry<String, String> header = iterator.next();
				cacheKey += header.getKey() + "=" + header.getValue() + ";";
			}
			cacheKey += "}";
		}
		return cacheKey;
	}
}

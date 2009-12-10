package net.webassembletool.cache;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import net.webassembletool.ResourceContext;
import net.webassembletool.resource.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;

public class Rfc2616 {
	private static final Log LOG = LogFactory.getLog(Rfc2616.class);

	private final static class CacheControlResponseHeader {
		private boolean _public = false;
		private boolean _private = false;
		private boolean noCache = false;
		private boolean noStore = false;
		private boolean mustRevalidate = false;
		private boolean proxyRevalidate = false;
		private Long maxAge = null;
		private Long sMaxAge = null;

		private CacheControlResponseHeader() {
			// Nothing to do
		}

		public final static CacheControlResponseHeader parse(Resource resource) {
			String cacheControlString = resource.getHeader("Cache-control");
			if (cacheControlString == null)
				return null;
			CacheControlResponseHeader result = new CacheControlResponseHeader();
			String[] split = cacheControlString.split(",");
			for (int i = 0; i < split.length; i++) {
				String[] token = split[i].trim().split("=");
				if (token[0].equalsIgnoreCase("public"))
					result._public = true;
				else if (token[0].equalsIgnoreCase("private"))
					result._private = true;
				else if (token[0].equalsIgnoreCase("no-cache"))
					result.noCache = true;
				else if (token[0].equalsIgnoreCase("no-store"))
					result.noStore = true;
				else if (token[0].equalsIgnoreCase("must-revalidate"))
					result.mustRevalidate = true;
				else if (token[0].equalsIgnoreCase("proxy-revalidate"))
					result.proxyRevalidate = true;
				else if (token[0].equalsIgnoreCase("max-age")) {
					result.maxAge = new Long(-1);
					if (token.length == 2)
						try {
							result.maxAge = Long.parseLong(token[1]);
						} catch (NumberFormatException e) {
							// Invalid max-age, assume -1 no cache
						}
				} else if (token[0].equalsIgnoreCase("s-max-age")) {
					result.sMaxAge = new Long(-1);
					if (token.length == 2)
						try {
							result.maxAge = Long.parseLong(token[1]);
						} catch (NumberFormatException e) {
							// Invalid max-age, assume -1 no cache
						}
				}
			}
			return result;
		}

	}

	public final static Map<String, String> getVary(
			ResourceContext resourceContext, Resource resource) {
		String varyString = resource.getHeader("Vary");
		if (varyString != null) {
			Map<String, String> result = new HashMap<String, String>();
			HttpServletRequest request = resourceContext.getOriginalRequest();
			String[] varyStringSplit = varyString.split(",");
			for (int i = 0; i < varyStringSplit.length; i++) {
				String key = varyStringSplit[i];
				String value = request.getHeader(key);
				if (value != null)
					result.put(key, value);
			}
			return result;
		}
		return null;
	}

	public final static boolean varyMatches(ResourceContext resourceContext,
			Resource resource) {
		Map<String, String> vary = getVary(resourceContext, resource);
		if (vary == null)
			return true;
		else {
			HttpServletRequest request = resourceContext.getOriginalRequest();
			for (Iterator<Entry<String, String>> iterator = vary.entrySet()
					.iterator(); iterator.hasNext();) {
				Entry<String, String> header = iterator.next();
				String key = header.getKey();
				String value = header.getValue();
				if (!value.equals(request.getHeader(key)))
					return false;
			}
			return true;
		}
	}

	public final static boolean needsValidation(
			ResourceContext resourceContext, CachedResponse resource) {
		if (resource == null)
			return true;
		return requiresRefresh(resourceContext) || isStale(resource);
	}

	public final static boolean isStale(CachedResponse resource) {
		Date date = getDate(resource);
		Date expiration = getHeuristicExpiration(resource);
		if (date == null || expiration == null)
			return true;
		Date nowOnOriginServer = new Date(getAge(resource) + date.getTime());
		return (nowOnOriginServer.after(expiration));
	}

	public final static String getEtag(Resource resource) {
		return resource.getHeader("E-tag");
	}

	public final static boolean etagMatches(ResourceContext resourceContext,
			Resource resource) {
		String etag = getEtag(resource);
		if (etag == null)
			return true;
		HttpServletRequest request = resourceContext.getOriginalRequest();
		String ifNoneMatch = request.getHeader("If-none-match");
		if (ifNoneMatch == null)
			return true;
		String[] ifNoneMatchSplit = ifNoneMatch.split(",");
		for (int i = 0; i < ifNoneMatchSplit.length; i++) {
			if (ifNoneMatchSplit[i].equals(etag))
				return true;
		}
		return false;
	}

	public final static Date getExpiration(Resource resource) {
		Date date = getDate(resource);
		if (date == null) {
			LOG.warn("No Date header, cannot calculate expiration.");
			return null;
		}
		Long maxAge = null;
		CacheControlResponseHeader cacheControl = CacheControlResponseHeader
				.parse(resource);
		if (cacheControl.maxAge != null)
			maxAge = cacheControl.maxAge;
		if (cacheControl.sMaxAge != null)
			maxAge = cacheControl.sMaxAge;
		if (maxAge != null) {
			// maxAge directive found according to HTTP/1.1
			return new Date(date.getTime() + maxAge.longValue());

		}
		Date expires = getDateHeader(resource, "Expires");
		if (expires != null)
			return expires;
		return null;
	}

	public final static Date getDate(Resource resource) {
		return getDateHeader(resource, "Date");
	}

	public final static Date getHeuristicExpiration(Resource resource) {
		Date expiration = getExpiration(resource);
		if (expiration != null)
			return expiration;
		Date date = getDate(resource);
		if (date == null) {
			return null;
		}
		Date lastModified = getDateHeader(resource, "Last-modified");
		// If the response does have a Last-Modified time, the heuristic
		// expiration value SHOULD be no more than some fraction of the interval
		// since that time. A typical setting of this fraction might be 10%.
		if (lastModified != null) {
			return new Date(date.getTime()
					+ (date.getTime() - lastModified.getTime()) / 10);
		}
		return null;
	}

	public final static long getAge(CachedResponse resource) {
		return new Date().getTime() - resource.getLocalDate().getTime();
	}

	public final static boolean isCacheable(Resource resource) {
		CacheControlResponseHeader cacheControl = CacheControlResponseHeader
				.parse(resource);
		if (cacheControl == null) {
			// Check expire header, if not a valid date, assume no cache
			if (resource.getHeader("Expires") != null
					&& getDateHeader(resource, "Expires") == null)
				return false;
		}
		if (cacheControl._public)
			return true;
		if (cacheControl._private || cacheControl.noCache
				|| cacheControl.noStore || cacheControl.mustRevalidate
				|| cacheControl.proxyRevalidate || cacheControl.maxAge <= 0)
			return false;
		return true;
	}

	public final static boolean isCacheable(ResourceContext context) {
		String method = context.getOriginalRequest().getMethod();
		return "GET".equalsIgnoreCase(method)
				|| "HEAD".equalsIgnoreCase(method);
	}

	public final static boolean matches(ResourceContext resourceContext,
			CachedResponse cachedResponse) {
		String method = resourceContext.getOriginalRequest().getMethod();
		if (!"HEAD".equalsIgnoreCase(method)
				&& !cachedResponse.hasResponseBody())
			return false;
		if (!etagMatches(resourceContext, cachedResponse))
			return false;
		if (!varyMatches(resourceContext, cachedResponse))
			return false;
		return true;
	}

	public final static Date getDateHeader(Resource resource, String name) {
		String dateString = resource.getHeader(name);
		if (dateString != null) {
			try {
				return DateUtils.parseDate(dateString);
			} catch (DateParseException e) {
				// Ignore invalid date
				LOG.warn("Invalid date format: " + dateString);
			}
		}
		return null;
	}

	public final static boolean requiresRefresh(ResourceContext context) {
		HttpServletRequest originalRequest = context.getOriginalRequest();
		String pragma = originalRequest.getHeader("Pragma");
		if ("no-cache".equalsIgnoreCase(pragma))
			return true;
		String cacheControl = originalRequest.getHeader("Cache-control");
		if (cacheControl != null) {
			cacheControl = cacheControl.toLowerCase();
			if (cacheControl.contains("no-cache")
					|| cacheControl.contains("no-store")
					|| cacheControl.contains("must-revalidate")
					|| cacheControl.contains("max-age=0"))
				return true;
		}
		return false;
	}

}

package org.esigate.util;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.esigate.DriverConfiguration;
import org.esigate.HttpErrorPage;
import org.esigate.ResourceContext;
import org.esigate.api.HttpRequest;
import org.esigate.api.HttpStatusConstants;
import org.esigate.http.DateUtils;
import org.esigate.output.Output;
import org.esigate.output.StringOutput;
import org.esigate.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Rfc2616 caching implementation.
 * 
 * @author Francois-Xavier Bonnet
 * @author Nicolas Richeton
 * 
 */
public class Rfc2616 {
	private Rfc2616() {

	}

	private static final int SECONDS = 1000;
	private static final Logger LOG = LoggerFactory.getLogger(Rfc2616.class);

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
			if (cacheControlString == null) {
				return null;
			}
			CacheControlResponseHeader result = new CacheControlResponseHeader();
			String[] split = cacheControlString.split(",");
			for (int i = 0; i < split.length; i++) {
				String[] token = split[i].trim().split("=");
				if (token[0].equalsIgnoreCase("public")) {
					result._public = true;
				} else if (token[0].equalsIgnoreCase("private")) {
					result._private = true;
				} else if (token[0].equalsIgnoreCase("no-cache")) {
					result.noCache = true;
				} else if (token[0].equalsIgnoreCase("no-store")) {
					result.noStore = true;
				} else if (token[0].equalsIgnoreCase("must-revalidate")) {
					result.mustRevalidate = true;
				} else if (token[0].equalsIgnoreCase("proxy-revalidate")) {
					result.proxyRevalidate = true;
				} else if (token[0].equalsIgnoreCase("max-age")) {
					result.maxAge = Long.valueOf(-1);
					if (token.length == 2) {
						try {
							result.maxAge = Long.parseLong(token[1]);
						} catch (NumberFormatException e) {
							// Invalid max-age, assume -1 no cache
						}
					}
				} else if (token[0].equalsIgnoreCase("s-max-age")) {
					result.sMaxAge = Long.valueOf(-1);
					if (token.length == 2) {
						try {
							result.sMaxAge = Long.parseLong(token[1]);
						} catch (NumberFormatException e) {
							// Invalid s-max-age, assume -1 no cache
						}
					}
				}
			}
			return result;
		}

	}

	/**
	 * Create a map of all headers and their values which are mentioned in the
	 * Vary header of "resource".
	 * 
	 * <p>
	 * This method is used to get the headers from an existing cached resource.
	 * (Cache side)
	 * 
	 * <p>
	 * When doing a new request on this resource, the cached resource can be
	 * used if all headers values contained in the returned map are exactly the
	 * same than the ones in the new request.
	 * 
	 * @param resource
	 */
	public final static Map<String, String> getVary(Resource resource) {
		String varyString = resource.getHeader("Vary");
		if (varyString != null) {
			Map<String, String> result = new HashMap<String, String>();
			String[] varyStringSplit = varyString.split(",");
			for (String key : varyStringSplit) {
				String value = resource.getRequestHeader(key);

				result.put(key, value);

			}
			return result;
		}
		return null;
	}

	/**
	 * Create a map of all headers and their values in
	 * resourceContext#getOriginalRequest() which are mentioned in the Vary
	 * header of "resource".
	 * 
	 * <p>
	 * This method is used to get the headers from a new, upcoming request.
	 * (Client side)
	 * 
	 * @param resource
	 */
	public final static Map<String, String> getVary(
			ResourceContext resourceContext, Resource resource) {
		HttpRequest request = resourceContext.getOriginalRequest();
		String varyString = resource.getHeader("Vary");
		if (varyString != null) {
			Map<String, String> result = new HashMap<String, String>();
			String[] varyStringSplit = varyString.split(",");
			for (int i = 0; i < varyStringSplit.length; i++) {
				String key = varyStringSplit[i];
				String value = request.getHeader(key);
				if (value != null) {
					result.put(key, value);
				}
			}
			return result;
		}
		return null;
	}

	/**
	 * Ensure that "resourceContext" matches "resource" according to the Vary
	 * header of "resource". "resource" is usually a cached resource.
	 * 
	 * @param resourceContext
	 * @param resource
	 * @return true if resource matches according to "Vary"
	 */
	public final static boolean varyMatches(ResourceContext resourceContext,
			Resource resource) {
		Map<String, String> vary = getVary(resource);
		if (vary == null) {
			return true;
		} else {
			HttpRequest request = resourceContext.getOriginalRequest();
			for (Iterator<Entry<String, String>> iterator = vary.entrySet()
					.iterator(); iterator.hasNext();) {
				Entry<String, String> header = iterator.next();
				String key = header.getKey();
				String value = header.getValue();

				if (!StringUtils.equals(value, request.getHeader(key))) {
					return false;
				}
			}
			return true;
		}
	}

	public final static boolean needsValidation(
			ResourceContext resourceContext, Resource resource) {
		if (resource == null) {
			return true;
		}
		return requiresRefresh(resourceContext) || isStale(resource);
	}

	public final static boolean isStale(Resource resource) {
		Date date = getDate(resource);
		Date expiration = getHeuristicExpiration(resource);
		if (date == null || expiration == null) {
			return true;
		}
		Date nowOnOriginServer = new Date(getAge(resource) + date.getTime());
		return (nowOnOriginServer.after(expiration));
	}

	/**
	 * Get ETag from a resource.
	 * 
	 * @param resource
	 *            Resource (not null)
	 * @return etag or null
	 */
	public final static String getEtag(Resource resource) {
		return resource.getHeader("ETag");
	}

	public final static boolean etagMatches(ResourceContext resourceContext,
			Resource resource) {
		String etag = getEtag(resource);
		if (etag == null) {
			return true;
		}
		HttpRequest request = resourceContext.getOriginalRequest();
		String ifNoneMatch = request.getHeader("If-none-match");
		if (ifNoneMatch == null) {
			return true;
		}
		String[] ifNoneMatchSplit = ifNoneMatch.split(",");
		for (int i = 0; i < ifNoneMatchSplit.length; i++) {
			if (ifNoneMatchSplit[i].equals(etag)) {
				return true;
			}
		}
		return false;
	}

	public final static Date getExpiration(Resource resource) {
		Date date = getDate(resource);
		Long maxAge = null;
		CacheControlResponseHeader cacheControl = CacheControlResponseHeader
				.parse(resource);
		if (cacheControl != null) {
			if (cacheControl.maxAge != null) {
				maxAge = cacheControl.maxAge;
			}
			if (cacheControl.sMaxAge != null) {
				maxAge = cacheControl.sMaxAge;
			}
		}
		if (maxAge != null) {
			// maxAge directive found according to HTTP/1.1
			// Age is in seconds. See
			// https://sourceforge.net/apps/mantisbt/webassembletool/view.php?id=17
			return new Date(date.getTime() + maxAge.longValue() * SECONDS);

		}
		Date expires = getDateHeader(resource, "Expires");
		if (expires != null) {
			return expires;
		}
		return null;
	}

	public final static Date getDate(Resource resource) {
		// Fixed according to
		// http://sourceforge.net/apps/mantisbt/webassembletool/view.php?id=21
		Date date = getDateHeader(resource, "Date");
		if (date == null) {
			date = Calendar.getInstance().getTime();
		}
		return date;
	}

	public final static Date getHeuristicExpiration(Resource resource) {
		Date expiration = getExpiration(resource);
		if (expiration != null) {
			return expiration;
		}
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

	public final static long getAge(Resource resource) {
		return System.currentTimeMillis() - resource.getLocalDate().getTime();
	}

	public final static boolean isCacheable(Resource resource) {
		CacheControlResponseHeader cacheControl = CacheControlResponseHeader
				.parse(resource);
		if (cacheControl == null) {
			// Check expire header, if not a valid date, assume no cache
			if (resource.getHeader("Expires") != null
					&& getDateHeader(resource, "Expires") == null) {
				return false;
			}
		}
		if (cacheControl._public) {
			return true;
		}
		if (cacheControl._private || cacheControl.noCache
				|| cacheControl.noStore || cacheControl.mustRevalidate
				|| cacheControl.proxyRevalidate || cacheControl.maxAge <= 0) {
			return false;
		}
		return true;
	}

	/**
	 * A Resource is cacheable if the HTTP method is GET or HEAD according to
	 * HTTP specification. Additionnaly, if we are not in proxy mode, the driver
	 * will send a GET method as we are not proxying the original request but
	 * only including an element. In that case, the response is cacheable.
	 * 
	 * @param context
	 * @return true if the resource is cacheable
	 */
	public final static boolean isCacheable(ResourceContext context) {
		String method = context.getOriginalRequest().getMethod();
		return !context.isProxy() || "GET".equalsIgnoreCase(method)
				|| "HEAD".equalsIgnoreCase(method);
	}

	public final static boolean matches(ResourceContext resourceContext,
			Resource cachedResponse) {
		String method = resourceContext.getOriginalRequest().getMethod();

		if (!"HEAD".equalsIgnoreCase(method)
				&& !cachedResponse.hasResponseBody()) {
			return false;
		}
		if (!etagMatches(resourceContext, cachedResponse)) {
			return false;
		}
		if (!varyMatches(resourceContext, cachedResponse)) {
			return false;
		}
		return true;
	}

	public final static Date getDateHeader(Resource resource, String name) {
		String dateString = resource.getHeader(name);
		return convertDate(dateString);
	}

	public final static Date getDateHeader(ResourceContext resourceContext,
			String name) {
		String dateString = resourceContext.getOriginalRequest()
				.getHeader(name);
		return convertDate(dateString);
	}

	private final static Date convertDate(String dateString) {
		if (dateString != null) {
			try {
				return DateUtils.parseDate(dateString);
			} catch (ParseException e) {
				// Ignore invalid date
				LOG.warn("Invalid date format: " + dateString);
			}
		}
		return null;
	}

	public final static boolean requiresRefresh(ResourceContext context) {
		HttpRequest originalRequest = context.getOriginalRequest();
		String pragma = originalRequest.getHeader("Pragma");
		if ("no-cache".equalsIgnoreCase(pragma)) {
			return true;
		}
		String cacheControl = originalRequest.getHeader("Cache-control");
		if (cacheControl != null) {
			cacheControl = cacheControl.toLowerCase();
			if (cacheControl.contains("no-cache")
					|| cacheControl.contains("no-store")
					|| cacheControl.contains("must-revalidate")
					|| cacheControl.contains("max-age=0")) {
				return true;
			}
		}
		return false;
	}

	public final static void renderResource(DriverConfiguration config,
			Resource resource, Output output) throws IOException, HttpErrorPage {
		if (resource.isError()) {
			String errorPageContent;
			StringOutput stringOutput = new StringOutput();
			resource.render(stringOutput);
			errorPageContent = stringOutput.toString();
			output.setStatusCode(resource.getStatusCode());
			output.setStatusMessage(resource.getStatusMessage());
			copyHeaders(config, resource, output);
			output.open();
			output.write(errorPageContent);
			output.close();
			throw new HttpErrorPage(resource.getStatusCode(),
					resource.getStatusMessage(), errorPageContent);
		} else if (HttpStatusConstants.SC_NOT_MODIFIED == resource
				.getStatusCode()) {
			output.setStatusCode(resource.getStatusCode());
			output.setStatusMessage(resource.getStatusMessage());
			copyHeaders(config, resource, output);
			output.open();
			output.getOutputStream();
			// No response body
			output.close();
		} else {
			resource.render(output);
		}
	}

	/** Copies end-to-end headers from a resource to an output. */
	public final static void copyHeaders(DriverConfiguration config,
			Resource resource, Output output) {
		for (String headerName : resource.getHeaderNames()) {
			if (!config.isBlackListed(headerName)) {
				Collection<String> values = resource.getHeaders(headerName);
				for (String value : values) {
					output.addHeader(headerName, value);
				}
			}
		}
	}
	
}

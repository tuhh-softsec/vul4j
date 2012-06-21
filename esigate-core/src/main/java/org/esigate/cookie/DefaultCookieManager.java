package org.esigate.cookie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.http.cookie.Cookie;
import org.esigate.ConfigurationException;
import org.esigate.Parameters;
import org.esigate.ResourceContext;
import org.esigate.UserContext;
import org.esigate.http.SerializableBasicClientCookie2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultCookieManager implements CookieManager {
	private static final Logger LOG = LoggerFactory.getLogger(CookieManager.class);
	private static final String COOKIES_LIST_SESSION_KEY = CookieManager.class.getName() + "#cookies";
	protected Collection<String> discardCookies;
	protected Collection<String> forwardCookies;

	public void init(Properties properties) {
		// Cookies to forward
		forwardCookies = Parameters.FORWARD_COOKIES.getValueList(properties);
		// Cookies to discard
		discardCookies = Parameters.DISCARD_COOKIES.getValueList(properties);
		if (forwardCookies.contains("*") && forwardCookies.size() > 1) {
			throw new ConfigurationException("forwardCookies must be a list of cookie names OR *");
		}
		if (discardCookies.contains("*") && discardCookies.size() > 1) {
			throw new ConfigurationException("discardCookies must be a list of cookie names OR *");
		}
		if (forwardCookies.contains("*") && discardCookies.contains("*")) {
			throw new ConfigurationException("cannot use * for forwardCookies AND discardCookies at the same time");
		}
	}

	public void addCookie(Cookie cookie, ResourceContext resourceContext) {
		String name = cookie.getName();
		if (discardCookies.contains(name) || (discardCookies.contains("*") && !forwardCookies.contains(name))) {
			LOG.info("Cookie " + toString(cookie) + " -> discarding");
		} else if (forwardCookies.contains(name) || forwardCookies.contains("*")) {
			LOG.info("Cookie " + toString(cookie) + " -> forwarding");
			resourceContext.getOriginalResponse().addCookie(rewrite(cookie, resourceContext));
		} else {
			LOG.info("Cookie " + toString(cookie) + " -> storing to context");
			UserContext userContext = resourceContext.getUserContext();
			@SuppressWarnings("unchecked")
			List<Cookie> cookies = (List<Cookie>) userContext.getAttribute(COOKIES_LIST_SESSION_KEY);
			if (cookies == null)
				cookies = new ArrayList<Cookie>();
			cookies.add(cookie);
			userContext.setAttribute(COOKIES_LIST_SESSION_KEY, cookies);
		}
	}

	public List<Cookie> getCookies(ResourceContext resourceContext) {
		List<Cookie> cookies = new ArrayList<Cookie>();
		UserContext userContext = resourceContext.getUserContext();
		@SuppressWarnings("unchecked")
		List<Cookie> sessionCookies = (List<Cookie>) userContext.getAttribute(COOKIES_LIST_SESSION_KEY);
		if (sessionCookies != null)
			cookies.addAll(sessionCookies);
		org.esigate.api.Cookie[] requestCookies = resourceContext.getOriginalRequest().getCookies();
		if (requestCookies != null) {
			for (org.esigate.api.Cookie cookie : requestCookies) {
				String name = cookie.getName();
				if (forwardCookies.contains(name) || (forwardCookies.contains("*") && !discardCookies.contains(name))) {
					cookies.add(toApacheCookie(cookie, resourceContext));
				}
			}
		}
		return cookies;
	}

	private static Cookie toApacheCookie(org.esigate.api.Cookie cookie, ResourceContext resourceContext) {
		String name = cookie.getName();
		if ("_JSESSIONID".equalsIgnoreCase(name)) {
			name = name.substring(1);
		}
		SerializableBasicClientCookie2 httpClientCookie = new SerializableBasicClientCookie2(name, cookie.getValue());
		httpClientCookie.setSecure(false);
		String domain;
		if (resourceContext.getDriver().getConfiguration().isPreserveHost()) {
			domain = resourceContext.getOriginalRequest().getUri().getHost();
		} else {
			domain = resourceContext.getBaseURLasURL().getHost();
		}
		httpClientCookie.setDomain(domain);
		httpClientCookie.setPath("/");
		httpClientCookie.setComment(cookie.getComment());
		httpClientCookie.setVersion(cookie.getVersion());
		return httpClientCookie;
	}

	protected static String rewriteDomain(String originalDomain, String providerHostName, String requestHostName) {
		String domain = null;
		if (!providerHostName.equals(originalDomain)) {
			// if original domain starts with ".", remove it.
			if (originalDomain.startsWith(".")) {
				originalDomain = originalDomain.substring(1);
			}
			String[] originalDomainParts = originalDomain.split("\\.");
			String[] requestHostNameParts = requestHostName.split("\\.");
			int targetLength = Math.min(originalDomainParts.length, requestHostNameParts.length);
			if (targetLength == requestHostNameParts.length) {
				// The bigger domain we can use is request host name, it is like
				// returning null as domaine name!
				return null;
			}
			domain = "";
			for (int i = requestHostNameParts.length; i > requestHostNameParts.length - targetLength; i--) {
				domain = "." + requestHostNameParts[i - 1] + domain;
			}
		}
		return domain;
	}

	private static org.esigate.api.Cookie rewrite(Cookie cookie, ResourceContext resourceContext) {
		String name = cookie.getName();
		// Rewrite name if JSESSIONID because it will interfere with current server session
		if ("JSESSIONID".equalsIgnoreCase(name)) {
			name = "_" + name;
		}

		// Rewrite domain
		String domain = rewriteDomain(cookie.getDomain(), resourceContext.getBaseURLasURL().getHost(), resourceContext.getOriginalRequest().getUri().getHost());

		// Rewrite path
		String originalPath = cookie.getPath();
		String requestPath = resourceContext.getOriginalRequest().getUri().getRawQuery();
		String path = originalPath;
		if (requestPath == null || !requestPath.startsWith(originalPath)) {
			path = "/";
		}

		// Rewrite secure
		boolean secure = (cookie.isSecure() && resourceContext.getOriginalRequest().isSecure());

		org.esigate.api.Cookie cookieToForward = new BasicClientCookie(name, cookie.getValue());
		if (domain != null) {
			cookieToForward.setDomain(domain);
		}
		cookieToForward.setPath(path);
		cookieToForward.setSecure(secure);
		cookieToForward.setComment(cookie.getComment());
		cookieToForward.setVersion(cookie.getVersion());
		cookieToForward.setExpiryDate(cookie.getExpiryDate());

		LOG.debug("Forwarding cookie {} -> {}", cookie.toString(), cookieToForward.toString());
		return cookieToForward;
	}

	private String toString(Cookie cookie) {
		StringBuilder result = new StringBuilder();
		result.append(cookie.getName());
		result.append("=");
		result.append(cookie.getValue());
		if (cookie.getDomain() != null) {
			result.append(";domain=");
			result.append(cookie.getDomain());
		}
		if (cookie.getPath() != null) {
			result.append(";path=");
			result.append(cookie.getPath());
		}
		if (cookie.getExpiryDate() != null) {
			result.append(";expires=");
			result.append(cookie.getExpiryDate());
		}
		if (cookie.getCommentURL() != null) {
			result.append(";comment=");
			result.append(cookie.getComment());
		}
		if (cookie.getCommentURL() != null) {
			result.append(";comment=");
			result.append(cookie.getCommentURL());
		}
		return result.toString();
	}

	public boolean clearExpired(Date date, ResourceContext resourceContext) {
		if (date == null)
			return false;
		UserContext userContext = resourceContext.getUserContext();
		@SuppressWarnings("unchecked")
		List<Cookie> cookies = (List<Cookie>) userContext.getAttribute(COOKIES_LIST_SESSION_KEY);
		if (cookies != null) {
			boolean removed = false;
			for (Iterator<Cookie> it = cookies.iterator(); it.hasNext();) {
				if (it.next().isExpired(date)) {
					it.remove();
					removed = true;
				}
			}
			return removed;
		}
		return false;
	}

	public void clear(ResourceContext resourceContext) {
		UserContext userContext = resourceContext.getUserContext();
		@SuppressWarnings("unchecked")
		List<Cookie> cookies = (List<Cookie>) userContext.getAttribute(COOKIES_LIST_SESSION_KEY);
		if (cookies != null) {
			cookies.clear();
			userContext.setAttribute(COOKIES_LIST_SESSION_KEY, cookies);
		}
	}

}

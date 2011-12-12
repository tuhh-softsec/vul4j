/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.esigate.http;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.esigate.ResourceContext;
import org.esigate.cookie.BasicClientCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A CookieStore specific to current request.
 * <ul>
 * <li>It wraps the actual CookieStore for current user in order to store the
 * cookies and send them back to the server.</li>
 * <li>It retrieves the Cookies from incoming request to send them to the server
 * if they match the list of forwarded cookies.</li>
 * <li>It filters the Cookies according to the discardCookies list.</li>
 * <li>It forwards the cookies that should be forwarded according to
 * forwardCookies list.</li>
 * </ul>
 * 
 * @see CookieStore
 * 
 * @author Nicolas Richeton
 * 
 */
class RequestCookieStore implements CookieStore {
	private static final Logger LOG = LoggerFactory.getLogger(RequestCookieStore.class);

	private final Collection<String> discardCookies;
	private final Collection<String> forwardCookies;
	private final CookieStore actualCookieStore;
	private final ResourceContext resourceContext;

	public RequestCookieStore(Collection<String> discardCookies,
			Collection<String> forwardCookies, CookieStore actualCookieStore,
			HttpClientRequest request, ResourceContext resourceContext) {
		this.discardCookies = discardCookies;
		this.forwardCookies = forwardCookies;
		this.actualCookieStore = actualCookieStore;
		this.resourceContext = resourceContext;
	}

	public void addCookie(Cookie cookie) {
		String name = cookie.getName();
		if (discardCookies.contains(name) || (discardCookies.contains("*") && !forwardCookies.contains(name))) {
			LOG.info("Cookie " + toString(cookie) + " -> discarding");
		} else if (forwardCookies.contains(name) || forwardCookies.contains("*")) {
			LOG.info("Cookie " + toString(cookie) + " -> forwarding");
			resourceContext.getOriginalResponse().addCookie(rewrite(cookie, resourceContext));
		} else {
			LOG.info("Cookie " + toString(cookie) + " -> storing to context");
			actualCookieStore.addCookie(cookie);
		}

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

	static org.esigate.api.Cookie rewrite(Cookie cookie, ResourceContext resourceContext) {
		String name = cookie.getName();
		// Rewrite name if JSESSIONID because it will interfere with current server session
		if ("JSESSIONID".equalsIgnoreCase(name)) {
			name = "_" + name;
		}

		// Rewrite domain
		String domain = rewriteDomain(
				cookie.getDomain(),
				resourceContext.getDriver().getConfiguration().getBaseURLasURL().getHost(),
				resourceContext.getOriginalRequest().getServerName());

		// Rewrite path
		String originalPath = cookie.getPath();
		String requestPath = resourceContext.getOriginalRequest().getRequestURI();
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

	static String rewriteDomain(String originalDomain, String providerHostName,
			String requestHostName) {
		String domain = null;
		if (!providerHostName.equals(originalDomain)) {
			// if original domain starts with ".", remove it.
			if (originalDomain.startsWith(".")) {
				originalDomain = originalDomain.substring(1);
			}
			String[] originalDomainParts = originalDomain.split("\\.");
			String[] requestHostNameParts = requestHostName.split("\\.");
			int targetLength = Math.min(originalDomainParts.length,
					requestHostNameParts.length);
			if (targetLength == requestHostNameParts.length) {
				// The bigger domain we can use is request host name, it is like
				// returning null as domaine name!
				return null;
			}
			domain = "";
			for (int i = requestHostNameParts.length; i > requestHostNameParts.length
					- targetLength; i--) {
				domain = "." + requestHostNameParts[i - 1] + domain;
			}
		}
		return domain;
	}

	public List<Cookie> getCookies() {
		List<Cookie> cookies = new ArrayList<Cookie>();
		// Get all the cookies from actual CookieStore
		cookies.addAll(actualCookieStore.getCookies());
		// add all the cookies from browser to be accepted by policy
		// (should be right domain, unsecure, path /)
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

	static Cookie toApacheCookie(org.esigate.api.Cookie cookie, ResourceContext resourceContext) {
		String name = cookie.getName();
		if ("_JSESSIONID".equalsIgnoreCase(name)) {
			name = name.substring(1);
		}
		SerializableBasicClientCookie2 httpClientCookie = new SerializableBasicClientCookie2(
				name, cookie.getValue());
		httpClientCookie.setSecure(false);
		String domain;
		if (resourceContext.getDriver().getConfiguration().isPreserveHost()) {
			domain = resourceContext.getOriginalRequest().getServerName();
		} else {
			domain = resourceContext.getDriver().getConfiguration()
					.getBaseURLasURL().getHost();
		}
		httpClientCookie.setDomain(domain);
		httpClientCookie.setPath("/");
		httpClientCookie.setComment(cookie.getComment());
		httpClientCookie.setVersion(cookie.getVersion());
		return httpClientCookie;
	}

	public boolean clearExpired(Date date) {
		return actualCookieStore.clearExpired(date);
	}

	public void clear() {
		actualCookieStore.clear();
	}

}

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

package org.esigate.cookie;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BasicClientCookie2;
import org.esigate.ConfigurationException;
import org.esigate.Driver;
import org.esigate.Parameters;
import org.esigate.UserContext;
import org.esigate.util.HttpRequestHelper;
import org.esigate.util.UriUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This cookie manager supports rules for forwarding cookies to the user browser, ignore and discard cookies, or store
 * cookies in the user session.
 * 
 * <p>
 * When cookies are not forwarded or discarded, a the user session is used to store these cookies : in that case Esigate
 * is no longer stateful. For public deployment is is recommended to use cookie forwarding and discarding to prevent
 * session creation.
 * 
 * @author Francois-Xavier Bonnet
 * @author Nicolas Richeton
 * 
 */
public class DefaultCookieManager implements CookieManager {
    private static final Logger LOG = LoggerFactory.getLogger(CookieManager.class);
    private static final String COOKIES_LIST_SESSION_KEY = CookieManager.class.getName() + "#cookies";
    private Collection<String> discardCookies;
    private Collection<String> forwardCookies;

    protected Collection<String> getForwardCookies() {
        return forwardCookies;
    }

    /**
     * Init cookie manager. Reads parameters <b>discardCookies</b> and <b>forwardCookies</b>.
     */
    @Override
    public void init(Driver d, Properties properties) {
        // Cookies to forward
        this.forwardCookies = Parameters.FORWARD_COOKIES.getValueList(properties);
        // Cookies to discard
        this.discardCookies = Parameters.DISCARD_COOKIES.getValueList(properties);

        // Verify configuration
        if (this.forwardCookies.contains("*") && this.forwardCookies.size() > 1) {
            throw new ConfigurationException("forwardCookies must be a list of cookie names OR *");
        }
        if (this.discardCookies.contains("*") && this.discardCookies.size() > 1) {
            throw new ConfigurationException("discardCookies must be a list of cookie names OR *");
        }
        if (this.forwardCookies.contains("*") && this.discardCookies.contains("*")) {
            throw new ConfigurationException("cannot use * for forwardCookies AND discardCookies at the same time");
        }
    }

    @Override
    public void addCookie(Cookie cookie, HttpRequest originalRequest) {
        String name = cookie.getName();
        if (discardCookies.contains(name) || (discardCookies.contains("*") && !forwardCookies.contains(name))) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Cookie " + toString(cookie) + " -> discarding");
            }

            // Ignore cookie
        } else if (forwardCookies.contains(name) || forwardCookies.contains("*")) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Cookie " + toString(cookie) + " -> forwarding");
            }

            // Forward cookie in response.
            HttpRequestHelper.getMediator(originalRequest).addCookie(rewriteForBrowser(cookie, originalRequest));
        } else {
            if (LOG.isInfoEnabled()) {
                LOG.info("Cookie " + toString(cookie) + " -> storing to context");
            }

            // Store cookie in session
            UserContext userContext = HttpRequestHelper.getUserContext(originalRequest);

            BasicCookieStore cookies = (BasicCookieStore) userContext.getAttribute(COOKIES_LIST_SESSION_KEY);
            if (cookies == null) {
                cookies = new BasicCookieStore();
            }
            cookies.addCookie(cookie);

            userContext.setAttribute(COOKIES_LIST_SESSION_KEY, cookies);
        }
    }

    @Override
    public List<Cookie> getCookies(HttpRequest originalRequest) {
        BasicCookieStore cookies = new BasicCookieStore();
        UserContext userContext = HttpRequestHelper.getUserContext(originalRequest);

        // Read cookies from session
        BasicCookieStore sessionCookies = (BasicCookieStore) userContext.getAttribute(COOKIES_LIST_SESSION_KEY);
        if (sessionCookies != null) {
            for (Cookie c : sessionCookies.getCookies()) {
                cookies.addCookie(c);
            }
        }

        // Read cookie from request
        Cookie[] requestCookies = HttpRequestHelper.getMediator(originalRequest).getCookies();
        if (requestCookies != null) {
            for (Cookie cookie : requestCookies) {
                String name = cookie.getName();
                if (forwardCookies.contains(name) || (forwardCookies.contains("*") && !discardCookies.contains(name))) {
                    cookies.addCookie(rewriteForServer(cookie, originalRequest));
                }
            }
        }
        return cookies.getCookies();
    }

    private static Cookie rewriteForServer(Cookie cookie, HttpRequest originalRequest) {
        String name = cookie.getName();
        if ("_JSESSIONID".equalsIgnoreCase(name)) {
            name = name.substring(1);
        }
        BasicClientCookie2 httpClientCookie = new BasicClientCookie2(name, cookie.getValue());
        httpClientCookie.setSecure(false);
        String domain;

        if (HttpRequestHelper.getDriver(originalRequest).getConfiguration().isPreserveHost()) {
            domain = UriUtils.extractHostName(originalRequest.getRequestLine().getUri());
        } else {
            domain = HttpRequestHelper.getBaseUrl(originalRequest).getHost();
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
                // returning null as domain name!
                return null;
            }
            domain = "";
            for (int i = requestHostNameParts.length; i > requestHostNameParts.length - targetLength; i--) {
                domain = "." + requestHostNameParts[i - 1] + domain;
            }
        }
        return domain;
    }

    private static Cookie rewriteForBrowser(Cookie cookie, HttpRequest originalRequest) {
        String name = cookie.getName();
        // Rewrite name if JSESSIONID because it will interfere with current
        // server session
        if ("JSESSIONID".equalsIgnoreCase(name)) {
            name = "_" + name;
        }

        // Rewrite domain
        String domain = rewriteDomain(cookie.getDomain(), HttpRequestHelper.getBaseUrl(originalRequest).getHost(),
                UriUtils.extractHostName(originalRequest.getRequestLine().getUri()));

        // Rewrite path
        String originalPath = cookie.getPath();
        String requestPath = UriUtils.createUri(originalRequest.getRequestLine().getUri()).getPath();
        String path = originalPath;
        if (requestPath == null || !requestPath.startsWith(originalPath)) {
            path = "/";
        }

        // Rewrite secure
        boolean secure = (cookie.isSecure() && originalRequest.getRequestLine().getUri().startsWith("https"));

        BasicClientCookie cookieToForward = new BasicClientCookie(name, cookie.getValue());
        if (domain != null) {
            cookieToForward.setDomain(domain);
        }
        cookieToForward.setPath(path);
        cookieToForward.setSecure(secure);
        cookieToForward.setComment(cookie.getComment());
        cookieToForward.setVersion(cookie.getVersion());
        cookieToForward.setExpiryDate(cookie.getExpiryDate());

        if (LOG.isDebugEnabled()) {
            // Ensure .toString is only called if debug enabled.
            LOG.debug("Forwarding cookie {} -> {}", cookie.toString(), cookieToForward.toString());
        }
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

    @Override
    public boolean clearExpired(Date date, HttpRequest originalRequest) {
        UserContext userContext = HttpRequestHelper.getUserContext(originalRequest);
        BasicCookieStore cookies = (BasicCookieStore) userContext.getAttribute(COOKIES_LIST_SESSION_KEY);

        if (cookies != null) {
            return cookies.clearExpired(date);
        }

        return false;
    }

    @Override
    public void clear(HttpRequest originalRequest) {
        UserContext userContext = HttpRequestHelper.getUserContext(originalRequest);
        BasicCookieStore cookies = (BasicCookieStore) userContext.getAttribute(COOKIES_LIST_SESSION_KEY);
        if (cookies != null) {
            cookies.clear();
            userContext.setAttribute(COOKIES_LIST_SESSION_KEY, cookies);
        }
    }

}

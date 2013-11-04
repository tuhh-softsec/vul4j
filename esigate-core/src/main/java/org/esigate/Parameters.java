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

package org.esigate;

import org.esigate.authentication.RemoteUserAuthenticationHandler;
import org.esigate.cache.BasicCacheStorage;
import org.esigate.cookie.DefaultCookieManager;
import org.esigate.extension.Aggregate;
import org.esigate.extension.Esi;
import org.esigate.extension.FetchLogging;
import org.esigate.extension.FragmentLogging;
import org.esigate.extension.ResourceFixup;
import org.esigate.extension.XPoweredBy;
import org.esigate.extension.surrogate.Surrogate;
import org.esigate.util.Parameter;

/**
 * Configuration properties names and default values.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public final class Parameters {

    private Parameters() {

    }

    // Core parameters
    public static final Parameter REMOTE_URL_BASE = new Parameter("remoteUrlBase", null);
    public static final Parameter MAPPINGS = new Parameter("mappings", null);
    public static final Parameter URI_ENCODING = new Parameter("uriEncoding", "ISO-8859-1");
    public static final Parameter PARSABLE_CONTENT_TYPES = new Parameter("parsableContentTypes",
            "text/html, application/xhtml+xml");

    // Network settings
    public static final Parameter MAX_CONNECTIONS_PER_HOST = new Parameter("maxConnectionsPerHost", "20");
    public static final Parameter CONNECT_TIMEOUT = new Parameter("connectTimeout", "1000");
    public static final Parameter SOCKET_TIMEOUT = new Parameter("socketTimeout", "10000");

    // Proxy settings
    public static final Parameter PROXY_HOST = new Parameter("proxyHost", null);
    public static final Parameter PROXY_PORT = new Parameter("proxyPort", null);
    public static final Parameter PROXY_USER = new Parameter("proxyUser", null);
    public static final Parameter PROXY_PASSWORD = new Parameter("proxyPassword", null);

    // Http headers
    public static final Parameter PRESERVE_HOST = new Parameter("preserveHost", "false");
    public static final Parameter DISCARD_REQUEST_HEADERS = new Parameter("discardRequestHeaders",
            "Authorization,Connection,Content-Length,Cache-control,Cookie,Expect,Host,"
                    + "Max-Forwards,Pragma,Proxy-Authorization,TE,Trailer,Transfer-Encoding,Upgrade");
    public static final Parameter FORWARD_REQUEST_HEADERS = new Parameter("forwardRequestHeaders", null);
    public static final Parameter DISCARD_RESPONSE_HEADERS = new Parameter("discardResponseHeaders",
            "Connection,Content-Length,Content-MD5,Date,Keep-Alive,Proxy-Authenticate,Set-Cookie,"
                    + "Trailer,Transfer-Encoding,WWW-Authenticate");
    public static final Parameter FORWARD_RESPONSE_HEADERS = new Parameter("forwardResponseHeaders", null);

    // Cookies
    public static final Parameter COOKIE_MANAGER = new Parameter("cookieManager", DefaultCookieManager.class.getName());
    public static final Parameter DISCARD_COOKIES = new Parameter("discardCookies", null);
    public static final Parameter FORWARD_COOKIES = new Parameter("forwardCookies", null);

    // Url rewriting
    public static final Parameter FIX_RESOURCES = new Parameter("fixResources", "false");
    public static final Parameter FIX_MODE = new Parameter("fixMode", "relative");
    public static final Parameter VISIBLE_URL_BASE = new Parameter("visibleUrlBase", null);

    // Load-balancing
    public static final Parameter REMOTE_URL_BASE_STRATEGY = new Parameter("remoteUrlBaseStrategy",
            Parameters.ROUNDROBIN);
    // Possible values for remoteUrlBaseStrategy
    public static final String STICKYSESSION = "stickysession";
    public static final String IPHASH = "iphash";
    public static final String ROUNDROBIN = "roundrobin";

    // Extensions
    public static final Parameter EXTENSIONS = new Parameter("extensions", FragmentLogging.class.getName() + ","
            + FetchLogging.class.getName()
            + ","
            // + ErrorPages.class.getName() + ","
            + RemoteUserAuthenticationHandler.class.getName() + "," + Esi.class.getName() + ","
            + Aggregate.class.getName() + "," + ResourceFixup.class.getName() + "," + XPoweredBy.class.getName() + ","
            + Surrogate.class.getName());

    // Cache settings
    public static final Parameter USE_CACHE = new Parameter("useCache", "true");
    public static final Parameter MAX_CACHE_ENTRIES = new Parameter("maxCacheEntries", "1000");
    public static final Parameter MAX_OBJECT_SIZE = new Parameter("maxObjectSize", "1000000");
    public static final Parameter CACHE_STORAGE = new Parameter("cacheStorage", BasicCacheStorage.class.getName());
    public static final Parameter X_CACHE_HEADER = new Parameter("xCacheHeader", "false");
    public static final Parameter VIA_HEADER = new Parameter("viaHeader", "true");

    // Forced caching
    public static final Parameter TTL = new Parameter("ttl", "0");

    // Heuristic caching
    public static final Parameter HEURISTIC_CACHING_ENABLED = new Parameter("heuristicCachingEnabled", "true");
    // default value defined in
    // http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.2.4
    public static final Parameter HEURISTIC_COEFFICIENT = new Parameter("heuristicCoefficient", "0.1");
    // when no cache directive at all, nothing is cached by default
    public static final Parameter HEURISTIC_DEFAULT_LIFETIME_SECS = new Parameter("heuristicDefaultLifetimeSecs", "0");

    // Background revalidation
    public static final Parameter STALE_WHILE_REVALIDATE = new Parameter("staleWhileRevalidate", "0");
    public static final Parameter STALE_IF_ERROR = new Parameter("staleIfError", "0");
    public static final Parameter MIN_ASYNCHRONOUS_WORKERS = new Parameter("minAsynchronousWorkers", "0");
    public static final Parameter MAX_ASYNCHRONOUS_WORKERS = new Parameter("maxAsynchronousWorkers", "0");
    public static final Parameter ASYNCHRONOUS_WORKER_IDLE_LIFETIME_SECS = new Parameter(
            "asynchronousWorkerIdleLifetimeSecs", "60");
    public static final Parameter MAX_UPDATE_RETRIES = new Parameter("maxUpdateRetries", "1");
    public static final Parameter REVALIDATION_QUEUE_SIZE = new Parameter("revalidationQueueSize", "100");

    // EhCache
    public static final Parameter EHCACHE_CACHE_NAME_PROPERTY = new Parameter("ehcache.cacheName", "esigate");
    public static final Parameter EHCACHE_CONFIGURATION_FILE_PROPERTY = new Parameter("ehcache.configurationFile", null);

    // MemCached
    public static final Parameter MEMCACHED_SERVERS_PROPERTY = new Parameter("memcached.servers", null);

}

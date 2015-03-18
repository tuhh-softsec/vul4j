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
import org.esigate.extension.ConfigReloadOnChange;
import org.esigate.extension.Esi;
import org.esigate.extension.FetchLogging;
import org.esigate.extension.FragmentLogging;
import org.esigate.extension.ResourceFixup;
import org.esigate.extension.XPoweredBy;
import org.esigate.extension.surrogate.Surrogate;
import org.esigate.util.Parameter;
import org.esigate.util.ParameterArray;
import org.esigate.util.ParameterBoolean;
import org.esigate.util.ParameterCollection;
import org.esigate.util.ParameterFloat;
import org.esigate.util.ParameterInteger;
import org.esigate.util.ParameterString;

import java.util.Collection;

/**
 * Configuration properties names and default values.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public final class Parameters {

    // Core parameters
    public static final Parameter<String[]> REMOTE_URL_BASE = new ParameterArray("remoteUrlBase");
    public static final Parameter<Collection<String>> MAPPINGS = new ParameterCollection("mappings");
    public static final Parameter<Boolean> STRIP_MAPPING_PATH = new ParameterBoolean("stripMappingPath", false);
    public static final Parameter<String> URI_ENCODING = new ParameterString("uriEncoding", "ISO-8859-1");
    public static final Parameter<Collection<String>> PARSABLE_CONTENT_TYPES = new ParameterCollection(
            "parsableContentTypes", "text/html", "application/xhtml+xml");
    // Network settings
    public static final Parameter<Integer> MAX_CONNECTIONS_PER_HOST = new ParameterInteger("maxConnectionsPerHost", 20);
    public static final Parameter<Integer> CONNECT_TIMEOUT = new ParameterInteger("connectTimeout", 1000);
    public static final Parameter<Integer> SOCKET_TIMEOUT = new ParameterInteger("socketTimeout", 10000);
    // Proxy settings
    public static final Parameter<String> PROXY_HOST = new ParameterString("proxyHost");
    public static final Parameter<Integer> PROXY_PORT = new ParameterInteger("proxyPort", 0);
    public static final Parameter<String> PROXY_USER = new ParameterString("proxyUser");
    public static final Parameter<String> PROXY_PASSWORD = new ParameterString("proxyPassword");
    // Http headers
    public static final Parameter<Boolean> PRESERVE_HOST = new ParameterBoolean("preserveHost", true);
    // Cookies
    public static final Parameter<String> COOKIE_MANAGER = new ParameterString("cookieManager",
            DefaultCookieManager.class.getName());
    public static final Parameter<Collection<String>> DISCARD_COOKIES = new ParameterCollection("discardCookies");
    public static final Parameter<Collection<String>> STORE_COOKIES_IN_SESSION = new ParameterCollection(
            "storeCookiesInSession");
    public static final Parameter<String> VISIBLE_URL_BASE = new ParameterString("visibleUrlBase");
    // Possible values for remoteUrlBaseStrategy
    public static final String STICKYSESSION = "stickysession";
    public static final String IPHASH = "iphash";
    public static final String ROUNDROBIN = "roundrobin";
    // Load-balancing
    public static final Parameter<String> REMOTE_URL_BASE_STRATEGY = new ParameterString("remoteUrlBaseStrategy",
            Parameters.ROUNDROBIN);
    // Extensions
    public static final Parameter<Collection<String>> EXTENSIONS = new ParameterCollection("extensions",
            FragmentLogging.class.getName(), FetchLogging.class.getName(),
            RemoteUserAuthenticationHandler.class.getName(), Esi.class.getName(), ResourceFixup.class.getName(),
            XPoweredBy.class.getName(), Surrogate.class.getName(), ConfigReloadOnChange.class.getName());
    // Cache settings
    public static final Parameter<Boolean> USE_CACHE = new ParameterBoolean("useCache", true);
    public static final Parameter<Integer> MAX_CACHE_ENTRIES = new ParameterInteger("maxCacheEntries", 1000);
    public static final Parameter<Integer> MAX_OBJECT_SIZE = new ParameterInteger("maxObjectSize", 1000000);
    public static final Parameter<String> CACHE_STORAGE = new ParameterString("cacheStorage",
            BasicCacheStorage.class.getName());
    public static final Parameter<Boolean> X_CACHE_HEADER = new ParameterBoolean("xCacheHeader", false);
    public static final Parameter<Boolean> VIA_HEADER = new ParameterBoolean("viaHeader", true);
    // Forced caching
    public static final Parameter<Integer> TTL = new ParameterInteger("ttl", 0);
    // Heuristic caching
    public static final Parameter<Boolean> HEURISTIC_CACHING_ENABLED = new ParameterBoolean("heuristicCachingEnabled",
            true);
    // default value defined in
    // http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.2.4
    public static final Parameter<Float> HEURISTIC_COEFFICIENT = new ParameterFloat("heuristicCoefficient", 0.1f);
    // when no cache directive at all, nothing is cached by default
    public static final Parameter<Integer> HEURISTIC_DEFAULT_LIFETIME_SECS = new ParameterInteger(
            "heuristicDefaultLifetimeSecs", 0);
    // Background revalidation
    public static final Parameter<Integer> STALE_WHILE_REVALIDATE = new ParameterInteger("staleWhileRevalidate", 0);
    public static final Parameter<Integer> STALE_IF_ERROR = new ParameterInteger("staleIfError", 0);
    public static final Parameter<Integer> MIN_ASYNCHRONOUS_WORKERS = new ParameterInteger("minAsynchronousWorkers", 0);
    public static final Parameter<Integer> MAX_ASYNCHRONOUS_WORKERS = new ParameterInteger("maxAsynchronousWorkers", 0);
    public static final Parameter<Integer> ASYNCHRONOUS_WORKER_IDLE_LIFETIME_SECS = new ParameterInteger(
            "asynchronousWorkerIdleLifetimeSecs", 60);
    public static final Parameter<Integer> MAX_UPDATE_RETRIES = new ParameterInteger("maxUpdateRetries", 1);
    public static final Parameter<Integer> REVALIDATION_QUEUE_SIZE = new ParameterInteger("revalidationQueueSize", 100);
    // EhCache
    public static final Parameter<String> EHCACHE_CACHE_NAME_PROPERTY = new ParameterString("ehcache.cacheName",
            "esigate");
    public static final Parameter<String> EHCACHE_CONFIGURATION_FILE_PROPERTY = new ParameterString(
            "ehcache.configurationFile");
    // MemCached
    public static final Parameter<Collection<String>> MEMCACHED_SERVERS_PROPERTY = new ParameterCollection(
            "memcached.servers");
    // Default size for String or byte buffers used to manipulate html page contents
    public static final int DEFAULT_BUFFER_SIZE = 1024;
    // Default size for String or byte buffers used to manipulate small things like tags, cookie, log lines
    public static final int SMALL_BUFFER_SIZE = 256;

    private Parameters() {

    }
}

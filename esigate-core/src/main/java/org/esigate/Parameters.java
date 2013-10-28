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
interface Parameters {

	// Core parameters
	Parameter REMOTE_URL_BASE = new Parameter("remoteUrlBase", null);
	Parameter MAPPINGS = new Parameter("mappings", null);
	Parameter URI_ENCODING = new Parameter("uriEncoding", "ISO-8859-1");
	Parameter PARSABLE_CONTENT_TYPES = new Parameter("parsableContentTypes",
			"text/html, application/xhtml+xml");

	// Network settings
	Parameter MAX_CONNECTIONS_PER_HOST = new Parameter("maxConnectionsPerHost", "20");
	Parameter CONNECT_TIMEOUT = new Parameter("connectTimeout", "1000");
	Parameter SOCKET_TIMEOUT = new Parameter("socketTimeout", "10000");

	// Proxy settings
	Parameter PROXY_HOST = new Parameter("proxyHost", null);
	Parameter PROXY_PORT = new Parameter("proxyPort", null);
	Parameter PROXY_USER = new Parameter("proxyUser", null);
	Parameter PROXY_PASSWORD = new Parameter("proxyPassword", null);

	// Http headers
	Parameter PRESERVE_HOST = new Parameter("preserveHost", "false");
	Parameter DISCARD_REQUEST_HEADERS = new Parameter(
			"discardRequestHeaders",
			"Authorization,Connection,Content-Length,Cache-control,Cookie,Expect,Host,Max-Forwards,Pragma,Proxy-Authorization,TE,Trailer,Transfer-Encoding,Upgrade");
	Parameter FORWARD_REQUEST_HEADERS = new Parameter("forwardRequestHeaders", null);
	Parameter DISCARD_RESPONSE_HEADERS = new Parameter(
			"discardResponseHeaders",
			"Connection,Content-Length,Content-MD5,Date,Keep-Alive,Proxy-Authenticate,Set-Cookie,Trailer,Transfer-Encoding,WWW-Authenticate");
	Parameter FORWARD_RESPONSE_HEADERS = new Parameter("forwardResponseHeaders", null);

	// Cookies
	Parameter COOKIE_MANAGER = new Parameter("cookieManager", DefaultCookieManager.class.getName());
	Parameter DISCARD_COOKIES = new Parameter("discardCookies", null);
	Parameter FORWARD_COOKIES = new Parameter("forwardCookies", null);

	// Url rewriting
	Parameter FIX_RESOURCES = new Parameter("fixResources", "false");
	Parameter FIX_MODE = new Parameter("fixMode", "relative");
	Parameter VISIBLE_URL_BASE = new Parameter("visibleUrlBase", null);

	// Load-balancing
	Parameter REMOTE_URL_BASE_STRATEGY = new Parameter("remoteUrlBaseStrategy",
			Parameters.ROUNDROBIN);
	// Possible values for remoteUrlBaseStrategy
	String STICKYSESSION = "stickysession";
	String IPHASH = "iphash";
	String ROUNDROBIN = "roundrobin";

	// Extensions
	Parameter EXTENSIONS = new Parameter("extensions", FragmentLogging.class.getName() + ","
			+ FetchLogging.class.getName()
			+ ","
			// + ErrorPages.class.getName() + ","
			+ RemoteUserAuthenticationHandler.class.getName() + "," + Esi.class.getName() + ","
			+ Aggregate.class.getName() + "," + ResourceFixup.class.getName() + "," + XPoweredBy.class.getName() + ","
			+ Surrogate.class.getName());

	// Cache settings
	Parameter USE_CACHE = new Parameter("useCache", "true");
	Parameter MAX_CACHE_ENTRIES = new Parameter("maxCacheEntries", "1000");
	Parameter MAX_OBJECT_SIZE = new Parameter("maxObjectSize", "1000000");
	Parameter CACHE_STORAGE = new Parameter("cacheStorage", BasicCacheStorage.class.getName());
	Parameter X_CACHE_HEADER = new Parameter("xCacheHeader", "false");
	Parameter VIA_HEADER = new Parameter("viaHeader", "true");

	// Forced caching
	Parameter TTL = new Parameter("ttl", "0");

	// Heuristic caching
	Parameter HEURISTIC_CACHING_ENABLED = new Parameter("heuristicCachingEnabled", "true");
	// default value defined in
	// http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.2.4
	Parameter HEURISTIC_COEFFICIENT = new Parameter("heuristicCoefficient", "0.1");
	// when no cache directive at all, nothing is cached by default
	Parameter HEURISTIC_DEFAULT_LIFETIME_SECS = new Parameter("heuristicDefaultLifetimeSecs", "0");

	// Background revalidation
	Parameter STALE_WHILE_REVALIDATE = new Parameter("staleWhileRevalidate", "0");
	Parameter STALE_IF_ERROR = new Parameter("staleIfError", "0");
	Parameter MIN_ASYNCHRONOUS_WORKERS = new Parameter("minAsynchronousWorkers", "0");
	Parameter MAX_ASYNCHRONOUS_WORKERS = new Parameter("maxAsynchronousWorkers", "0");
	Parameter ASYNCHRONOUS_WORKER_IDLE_LIFETIME_SECS = new Parameter(
			"asynchronousWorkerIdleLifetimeSecs", "60");
	Parameter MAX_UPDATE_RETRIES = new Parameter("maxUpdateRetries", "1");
	Parameter REVALIDATION_QUEUE_SIZE = new Parameter("revalidationQueueSize", "100");

	// EhCache
	Parameter EHCACHE_CACHE_NAME_PROPERTY = new Parameter("ehcache.cacheName", "esigate");
	Parameter EHCACHE_CONFIGURATION_FILE_PROPERTY = new Parameter("ehcache.configurationFile", null);

	// MemCached
	Parameter MEMCACHED_SERVERS_PROPERTY = new Parameter("memcached.servers", null);

}

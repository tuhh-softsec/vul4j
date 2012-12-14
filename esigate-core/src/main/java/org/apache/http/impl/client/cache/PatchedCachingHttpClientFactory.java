package org.apache.http.impl.client.cache;

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

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheStorage;
import org.apache.http.client.cache.Resource;
import org.apache.http.client.cache.ResourceFactory;

/**
 * 
 * FIXME remove this class when tickets
 * https://issues.apache.org/jira/browse/HTTPCLIENT-1274 and
 * https://issues.apache.org/jira/browse/HTTPCLIENT-1276 will be closed
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class PatchedCachingHttpClientFactory {

	private PatchedCachingHttpClientFactory() {
		;
	}

	public final static CachingHttpClient buildCachingHttpClient(HttpClient backend, final HttpCacheStorage cacheStorage, CacheConfig cacheConfig) {

		final ResourceFactory resourceFactory = new HeapResourceFactory();
		return new CachingHttpClient(backend, new BasicHttpCache(resourceFactory, cacheStorage, cacheConfig) {

			private final CacheEntryUpdater cacheEntryUpdater = new CacheEntryUpdater(resourceFactory) {

				@Override
				public HttpCacheEntry updateCacheEntry(String requestId, HttpCacheEntry entry, Date requestDate, Date responseDate, HttpResponse response) throws IOException {
					if (response.getStatusLine().getStatusCode() != HttpStatus.SC_NOT_MODIFIED)
						throw new IllegalArgumentException("Response must have 304 status code");
					Header[] mergedHeaders = mergeHeaders(entry, response);
					Resource oldResource = entry.getResource();
					Resource resource = null;
					if (oldResource != null)
						resource = resourceFactory.copy(requestId, oldResource);
					return new HttpCacheEntry(requestDate, responseDate, entry.getStatusLine(), mergedHeaders, resource);
				}
			};

			@Override
			public HttpCacheEntry updateCacheEntry(HttpHost target, HttpRequest request, HttpCacheEntry stale, HttpResponse originResponse, Date requestSent, Date responseReceived) throws IOException {
				HttpCacheEntry updatedEntry = cacheEntryUpdater.updateCacheEntry(request.getRequestLine().getUri(), stale, requestSent, responseReceived, originResponse);
				storeInCache(target, request, updatedEntry);
				return updatedEntry;
			}

			@Override
			public HttpCacheEntry updateVariantCacheEntry(HttpHost target, HttpRequest request, HttpCacheEntry stale, HttpResponse originResponse, Date requestSent, Date responseReceived,
					String cacheKey) throws IOException {
				HttpCacheEntry updatedEntry = cacheEntryUpdater.updateCacheEntry(request.getRequestLine().getUri(), stale, requestSent, responseReceived, originResponse);
				cacheStorage.putEntry(cacheKey, updatedEntry);
				return updatedEntry;
			}

			@Override
			HttpCacheEntry doGetUpdatedParentEntry(final String requestId, final HttpCacheEntry existing, final HttpCacheEntry entry, final String variantKey, final String variantCacheKey)
					throws IOException {
				HttpCacheEntry src = existing;
				if (src == null) {
					src = entry;
				}

				Resource oldResource = entry.getResource();
				Resource resource = null;
				if (oldResource != null)
					resource = resourceFactory.copy(requestId, oldResource);
				Map<String, String> variantMap = new HashMap<String, String>(src.getVariantMap());
				variantMap.put(variantKey, variantCacheKey);
				return new HttpCacheEntry(src.getRequestDate(), src.getResponseDate(), src.getStatusLine(), src.getAllHeaders(), resource, variantMap);
			}

		}, cacheConfig);
	}
}

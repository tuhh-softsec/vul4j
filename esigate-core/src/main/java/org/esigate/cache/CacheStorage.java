/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.esigate.cache;

import java.io.IOException;

import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheStorage;
import org.apache.http.client.cache.HttpCacheUpdateCallback;
import org.apache.http.client.cache.HttpCacheUpdateException;
import org.esigate.extension.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CacheStorage implements HttpCacheStorage, Extension {
	private static final Logger LOG = LoggerFactory.getLogger(CacheStorage.class);

	protected HttpCacheStorage impl;

	public void putEntry(String key, HttpCacheEntry entry) throws IOException {
		LOG.info("putEntry(" + key + ")");
		impl.putEntry(key, entry);
	}

	public HttpCacheEntry getEntry(String key) throws IOException {
		LOG.info("getEntry(" + key + ")");
		return impl.getEntry(key);
	}

	public void removeEntry(String key) throws IOException {
		LOG.info("removeEntry(" + key + ")");
		impl.removeEntry(key);

	}

	public void updateEntry(String key, HttpCacheUpdateCallback callback) throws IOException, HttpCacheUpdateException {
		LOG.info("updateEntry(" + key + ")");
		impl.updateEntry(key, callback);
	}

}

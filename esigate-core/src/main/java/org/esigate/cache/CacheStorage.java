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
import java.util.Properties;

import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheStorage;
import org.apache.http.client.cache.HttpCacheUpdateCallback;
import org.apache.http.client.cache.HttpCacheUpdateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CacheStorage implements HttpCacheStorage {
    private static final Logger LOG = LoggerFactory.getLogger(CacheStorage.class);

    public abstract void init(Properties properties);

    private HttpCacheStorage impl;

    @Override
    public void putEntry(String key, HttpCacheEntry entry) throws IOException {
        LOG.debug("putEntry({})", key);
        impl.putEntry(key, entry);
    }

    @Override
    public HttpCacheEntry getEntry(String key) throws IOException {
        LOG.debug("getEntry({})", key);
        return impl.getEntry(key);
    }

    @Override
    public void removeEntry(String key) throws IOException {
        LOG.debug("removeEntry({})", key);
        impl.removeEntry(key);

    }

    @Override
    public void updateEntry(String key, HttpCacheUpdateCallback callback) throws IOException, HttpCacheUpdateException {
        LOG.debug("updateEntry({})", key);
        impl.updateEntry(key, callback);
    }

    public void setImpl(HttpCacheStorage impl) {
        this.impl = impl;
    }

}

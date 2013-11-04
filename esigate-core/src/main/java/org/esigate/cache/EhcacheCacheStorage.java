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

import java.util.Properties;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.ehcache.EhcacheHttpCacheStorage;
import org.esigate.Parameters;

public class EhcacheCacheStorage extends CacheStorage {
    public static final String DEFAULT_CACHE_NAME = "EsiGate";

    @Override
    public void init(Properties properties) {
        String cacheName = Parameters.EHCACHE_CACHE_NAME_PROPERTY.getValueString(properties);
        String configurationFileName = Parameters.EHCACHE_CONFIGURATION_FILE_PROPERTY.getValueString(properties);
        // Loaded from the Classpath, default will use /ehcache.xml or if not found /ehcache-failsafe.xml
        CacheManager cacheManager = CacheManager.create(configurationFileName);
        Ehcache ehcache = cacheManager.getEhcache(cacheName);
        if (ehcache == null) {
            cacheManager.addCache(cacheName);
            ehcache = cacheManager.getEhcache(cacheName);
        }
        CacheConfig cacheConfig = CacheConfigHelper.createCacheConfig(properties);
        setImpl(new EhcacheHttpCacheStorage(ehcache, cacheConfig));
    }

}

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

package org.esigate.cache;

import java.util.Properties;

import junit.framework.TestCase;

import org.esigate.ConfigurationException;
import org.esigate.Parameters;

public class MemcachedCacheStorageTest extends TestCase {
    public void testBasicOperations() throws Exception {
        // Cannot be really tested as we would need a running memcached server
        // CacheStorage cacheStorage = new MemcachedCacheStorage();
        // cacheStorage.init(new Properties());
        // CacheStorageTestUtils.testBasicOperations(cacheStorage);
    }

    public void testConfiguration() throws Exception {
        // Cannot be really tested as we would need a running memcached server
        Properties properties = new Properties();
        properties.put(Parameters.MEMCACHED_SERVERS_PROPERTY.getName(), "localhost:8080,127.0.0.1:8080");
        CacheStorage cacheStorage = new MemcachedCacheStorage();
        cacheStorage.init(properties);
    }

    public void testConfigurationNoServers() throws Exception {
        CacheStorage cacheStorage = new MemcachedCacheStorage();
        try {
            cacheStorage.init(new Properties());
        } catch (ConfigurationException e) {
            return;
        }
        fail("Configuration should fail as '" + Parameters.MEMCACHED_SERVERS_PROPERTY + "' is not defined.");
    }

    public void testConfigurationWrongValue() throws Exception {
        Properties properties = new Properties();
        properties.put(Parameters.MEMCACHED_SERVERS_PROPERTY, "foo");
        CacheStorage cacheStorage = new MemcachedCacheStorage();
        try {
            cacheStorage.init(properties);
        } catch (ConfigurationException e) {
            return;
        }
        fail("Configuration should fail as '" + Parameters.MEMCACHED_SERVERS_PROPERTY + "' is not defined.");
    }

    public void testConfigurationWrongPort() throws Exception {
        Properties properties = new Properties();
        properties.put(Parameters.MEMCACHED_SERVERS_PROPERTY, "foo:bar");
        CacheStorage cacheStorage = new MemcachedCacheStorage();
        try {
            cacheStorage.init(properties);
        } catch (ConfigurationException e) {
            return;
        }
        fail("Configuration should fail as '" + Parameters.MEMCACHED_SERVERS_PROPERTY + "' is not defined.");
    }
}

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
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import net.spy.memcached.MemcachedClient;

import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.memcached.MemcachedCacheEntryFactoryImpl;
import org.apache.http.impl.client.cache.memcached.MemcachedHttpCacheStorage;
import org.apache.http.impl.client.cache.memcached.SHA256KeyHashingScheme;
import org.esigate.ConfigurationException;
import org.esigate.Parameters;

public class MemcachedCacheStorage extends CacheStorage {
    @Override
    public void init(Properties properties) {
        Collection<String> serverStringList = Parameters.MEMCACHED_SERVERS_PROPERTY.getValueList(properties);
        if (serverStringList.isEmpty()) {
            throw new ConfigurationException("No memcached server defined. Property '"
                    + Parameters.MEMCACHED_SERVERS_PROPERTY + "' must be defined.");
        }
        List<InetSocketAddress> servers = new ArrayList<InetSocketAddress>();
        for (Iterator<String> iterator = serverStringList.iterator(); iterator.hasNext();) {
            String server = iterator.next();
            String[] serverHostPort = server.split(":");
            if (serverHostPort.length != 2) {
                throw new ConfigurationException("Invalid memcached server: '" + server
                        + "'. Each server must be in format 'host:port'.");
            }
            String host = serverHostPort[0];
            try {
                int port = Integer.parseInt(serverHostPort[1]);
                servers.add(new InetSocketAddress(host, port));
            } catch (NumberFormatException e) {
                throw new ConfigurationException("Invalid memcached server: '" + server
                        + "'. Each server must be in format 'host:port'. Port must be an integer.", e);
            }
        }
        MemcachedClient memcachedClient;
        try {
            memcachedClient = new MemcachedClient(servers);
        } catch (IOException e) {
            throw new ConfigurationException(e);
        }
        CacheConfig cacheConfig = CacheConfigHelper.createCacheConfig(properties);
        setImpl(new MemcachedHttpCacheStorage(memcachedClient, cacheConfig, new MemcachedCacheEntryFactoryImpl(),
                new SHA256KeyHashingScheme()));
    }
}

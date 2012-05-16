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
import org.apache.http.impl.client.cache.DefaultHttpCacheEntrySerializer;
import org.apache.http.impl.client.cache.memcached.MemcachedHttpCacheStorage;
import org.esigate.ConfigurationException;
import org.esigate.util.PropertiesUtil;

public class MemcachedCacheStorage extends CacheStorage {
	public final static String SERVERS_PROPERTY = "memcached.servers";

	public void init(Properties properties) {
		Collection<String> serverStringList = PropertiesUtil.getPropertyValueAsList(properties, SERVERS_PROPERTY);
		if (serverStringList.isEmpty())
			throw new ConfigurationException("No memcached server defined. Property '" + SERVERS_PROPERTY + "' must be defined.");
		List<InetSocketAddress> servers = new ArrayList<InetSocketAddress>();
		for (Iterator<String> iterator = serverStringList.iterator(); iterator.hasNext();) {
			String server = iterator.next();
			String[] serverHostPort = server.split(":");
			if (serverHostPort.length != 2)
				throw new ConfigurationException("Invalid memcached server: '" + server + "'. Each server must be in format 'host:port'.");
			String host = serverHostPort[0];
			try {
				int port = Integer.parseInt(serverHostPort[1]);
				servers.add(new InetSocketAddress(host, port));
			} catch (NumberFormatException e) {
				throw new ConfigurationException("Invalid memcached server: '" + server + "'. Each server must be in format 'host:port'. Port must be an integer.", e);
			}
		}
		MemcachedClient memcachedClient;
		try {
			memcachedClient = new MemcachedClient(servers);
		} catch (IOException e) {
			throw new ConfigurationException(e);
		}
		CacheConfig cacheConfig = CacheConfigHelper.createCacheConfig(properties);
		impl = new MemcachedHttpCacheStorage(memcachedClient, cacheConfig, new DefaultHttpCacheEntrySerializer());
	}
}

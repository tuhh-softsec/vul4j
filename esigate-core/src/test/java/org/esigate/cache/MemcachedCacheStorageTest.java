package org.esigate.cache;

import java.util.Properties;

import junit.framework.TestCase;

import org.esigate.ConfigurationException;

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
		properties.put(MemcachedCacheStorage.SERVERS_PROPERTY, "localhost:8080,127.0.0.1:8080");
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
		fail("Configuration should fail as '" + MemcachedCacheStorage.SERVERS_PROPERTY + "' is not defined.");
	}

	public void testConfigurationWrongValue() throws Exception {
		Properties properties = new Properties();
		properties.put(MemcachedCacheStorage.SERVERS_PROPERTY, "foo");
		CacheStorage cacheStorage = new MemcachedCacheStorage();
		try {
			cacheStorage.init(properties);
		} catch (ConfigurationException e) {
			return;
		}
		fail("Configuration should fail as '" + MemcachedCacheStorage.SERVERS_PROPERTY + "' is not defined.");
	}

	public void testConfigurationWrongPort() throws Exception {
		Properties properties = new Properties();
		properties.put(MemcachedCacheStorage.SERVERS_PROPERTY, "foo:bar");
		CacheStorage cacheStorage = new MemcachedCacheStorage();
		try {
			cacheStorage.init(properties);
		} catch (ConfigurationException e) {
			return;
		}
		fail("Configuration should fail as '" + MemcachedCacheStorage.SERVERS_PROPERTY + "' is not defined.");
	}
}

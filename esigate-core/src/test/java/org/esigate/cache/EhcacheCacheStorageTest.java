package org.esigate.cache;

import java.util.Properties;

import junit.framework.TestCase;

public class EhcacheCacheStorageTest extends TestCase {
    public void testBasicOperations() throws Exception {
        CacheStorage cacheStorage = new EhcacheCacheStorage();
        cacheStorage.init(new Properties());
        CacheStorageTestUtils.testBasicOperations(cacheStorage);
    }
}

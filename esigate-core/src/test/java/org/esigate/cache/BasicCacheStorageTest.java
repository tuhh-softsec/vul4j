package org.esigate.cache;

import java.util.Properties;

import junit.framework.TestCase;

public class BasicCacheStorageTest extends TestCase {
    public void testBasicOperations() throws Exception {
        CacheStorage cacheStorage = new BasicCacheStorage();
        cacheStorage.init(new Properties());
        CacheStorageTestUtils.testBasicOperations(cacheStorage);
    }
}

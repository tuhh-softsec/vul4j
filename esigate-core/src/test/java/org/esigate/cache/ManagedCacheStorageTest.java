package org.esigate.cache;

import java.util.Properties;

import junit.framework.TestCase;

public class ManagedCacheStorageTest extends TestCase {
    public void testBasicOperations() throws Exception {
        CacheStorage cacheStorage = new ManagedCacheStorage();
        cacheStorage.init(new Properties());
        CacheStorageTestUtils.testBasicOperations(cacheStorage);
    }
}

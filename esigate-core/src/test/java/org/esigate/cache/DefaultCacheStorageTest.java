package org.esigate.cache;

import org.esigate.cache.CacheStorage;
import org.esigate.cache.DefaultCacheStorage;

public class DefaultCacheStorageTest extends CacheStorageTest {

	@Override
	protected CacheStorage getCache() {
		return new DefaultCacheStorage();
	}

}

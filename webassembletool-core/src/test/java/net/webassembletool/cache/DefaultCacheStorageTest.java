package net.webassembletool.cache;

public class DefaultCacheStorageTest extends CacheStorageTest {

	@Override
	protected CacheStorage getCache() {
		return new DefaultCacheStorage();
	}

}

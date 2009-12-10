package net.webassembletool.cache;

import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;

class Storage {
	private final GeneralCacheAdministrator cache;

	public Storage() {
		cache = new GeneralCacheAdministrator();
	}

	public void put(String key, Object value) {
		cache.putInCache(key, value);
	}

	public Object get(String key) {
		Object result = null;
		try {
			result = cache.getFromCache(key);
		} catch (NeedsRefreshException e1) {
			// If the cache is configured to be blocking, we have to do this to
			// remove the lock.
			cache.cancelUpdate(key);
		}
		return result;
	}

	public void touch(String key) {
		get(key);
	}

}

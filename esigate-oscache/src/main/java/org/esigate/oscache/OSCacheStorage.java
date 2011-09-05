package org.esigate.oscache;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.esigate.cache.CacheStorage;

import com.opensymphony.oscache.base.CacheEntry;
import com.opensymphony.oscache.base.EntryRefreshPolicy;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;


public class OSCacheStorage implements CacheStorage {
	private final GeneralCacheAdministrator cache;

	public OSCacheStorage() {
		cache = new GeneralCacheAdministrator();

	}

	public void put(String key, Object value) {
		key = prepairKey(key);
		cache.putInCache(key, value);
	}

	private String prepairKey(String key) {
		// OsCache does not support empty String keys
		if (StringUtils.isEmpty(key)) {
			key = "/";
		}
		return key;
	}

	public Object get(String key) {
		key = prepairKey(key);
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

	public void put(String key, Object value, long ttl) {
		key = prepairKey(key);
		cache.putInCache(key, value, new StorageEntryRefreshPolicy(ttl));
		
	}

	public <T> T get(String key, Class<T> clazz) {
		Object ret = get(key);
		if(null == ret){
			return null;
		}
		return clazz.cast(ret);
	}

	public void touch(String key) {
		key = prepairKey(key);
		get(key);
	}
	
	private static class StorageEntryRefreshPolicy implements EntryRefreshPolicy{

		private static final long serialVersionUID = -4570614485500011699L;
		long expireTime;

		public StorageEntryRefreshPolicy(long ttl) {
			super();
			this.expireTime = System.currentTimeMillis()+ ttl;
		}

		public boolean needsRefresh(CacheEntry arg0) {
			if(expireTime <= System.currentTimeMillis()){
				return true;
			}
			return false;
		}
	}

	public void init(Properties properties) {
		
	}

}

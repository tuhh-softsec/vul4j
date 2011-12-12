package org.esigate.cache;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Properties;

public class DefaultCacheStorage implements CacheStorage {
	private static final Comparator<CacheEntry> CACHE_ENTRY_COMPARATOR = new Comparator<CacheEntry>() {
		public int compare(CacheEntry o1, CacheEntry o2) {
			return (int) (o1.getTtl() - o2.getTtl());
		}
	};

	private final Map<String, CacheEntry> cache = Collections.synchronizedMap(new LRUMap<String, CacheEntry>());
	private final PriorityQueue<CacheEntry> ttlQueue = new PriorityQueue<CacheEntry>(100, CACHE_ENTRY_COMPARATOR);

	public void put(String key, Object value) {
		removeExpiredEntries();
		cache.put(key, new CacheEntry(value));
	}

	public void put(String key, Object value, long ttl) {
		removeExpiredEntries();
		CacheEntry cacheEntry = new CacheEntry(value, key, System.currentTimeMillis() + ttl);
		ttlQueue.add(cacheEntry);
		cache.put(key, cacheEntry);
	}

	public Object get(String key) {
		removeExpiredEntries();
		CacheEntry cacheEntry = cache.get(key);
		return null != cacheEntry ? cacheEntry.getValue() : null;
	}

	public <T> T get(String key, Class<T> clazz) {
		return clazz.cast(this.get(key));
	}

	public void touch(String key) {
		this.get(key);
	}

	public void init(Properties properties) {

	}

	private void removeExpiredEntries() {
		if (ttlQueue.size() > 0) {
			synchronized (cache) {
				boolean needToProcess = true;
				while (needToProcess) {
					CacheEntry cacheEntry = ttlQueue.peek();
					if (cacheEntry != null
							&& cacheEntry.getTtl() < System.currentTimeMillis()) {
						ttlQueue.remove(cacheEntry);
						if (cache.containsValue(cacheEntry)) {
							cache.remove(cacheEntry.getKey());
						}
					} else {
						needToProcess = false;
					}
				}
			}

		}
	}

	private static class CacheEntry {
		private final Object value;
		private final long ttl;
		private final String key;

		public CacheEntry(Object value) {
			this(value, null, -1);
		}

		public CacheEntry(Object value, String key, long ttl) {
			this.value = value;
			this.key = key;
			this.ttl = ttl;
		}

		public long getTtl() {
			return ttl;
		}

		public Object getValue() {
			return value;
		}

		public String getKey() {
			return key;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			CacheEntry other = (CacheEntry) obj;
			if (key == null) {
				if (other.key != null) {
					return false;
				}
			} else if (!key.equals(other.key)) {
				return false;
			}
			if (ttl != other.ttl) {
				return false;
			}
			if (value == null) {
				if (other.value != null) {
					return false;
				}
			} else if (!value.equals(other.value)) {
				return false;
			}
			return true;
		}

	}
}

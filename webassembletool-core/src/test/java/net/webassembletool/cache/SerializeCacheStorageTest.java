package net.webassembletool.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SerializeCacheStorageTest extends CacheStorageTest {

	@Override
	protected CacheStorage getCache() {
		return new SerializeCacheStorage();
	}

	private static class SerializeCacheStorage implements CacheStorage {
		Map<String, SerializeCacheEntry> cache = new HashMap<String, SerializeCacheEntry>();

		public void put(String key, Object value) {
			try {
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(os);
				oos.writeObject(value);
				SerializeCacheEntry entry = new SerializeCacheEntry(
						os.toByteArray(), -1);
				cache.put(key, entry);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		}

		public void put(String key, Object value, long ttl) {
			try {
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(os);
				oos.writeObject(value);
				SerializeCacheEntry entry = new SerializeCacheEntry(
						os.toByteArray(), System.currentTimeMillis() + ttl);
				cache.put(key, entry);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		}

		public Object get(String key) {
			SerializeCacheEntry ret = cache.get(key);
			if (null != ret) {
				if (ret.getTtl() <= 0
						|| ret.getTtl() >= System.currentTimeMillis()) {
					try {
						ByteArrayInputStream is = new ByteArrayInputStream(
								ret.getContent());
						ObjectInputStream oin = new ObjectInputStream(is);
						Object obj = oin.readObject();
						if (null != obj && CacheEntry.class.isInstance(obj)) {
							CacheEntry.class.cast(obj).setStorage(this);
						}
						return obj;
					} catch (Exception e) {
						throw new RuntimeException(e);
					}

				}
			}
			return null;
		}

		public <T> T get(String key, Class<T> clazz) {
			Object ret = get(key);
			if (null != ret) {
				return clazz.cast(ret);
			}
			return null;
		}

		public void touch(String key) {
			this.get(key);

		}

		private class SerializeCacheEntry {
			private final byte[] content;
			private final long ttl;

			public SerializeCacheEntry(byte[] content, long ttl) {
				super();
				this.content = content;
				this.ttl = ttl;
			}

			public byte[] getContent() {
				return content;
			}

			public long getTtl() {
				return ttl;
			}
		}

		public void init(Properties properties) {

		}

	}
}

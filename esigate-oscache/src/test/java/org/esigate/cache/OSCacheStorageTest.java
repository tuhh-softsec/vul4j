package org.esigate.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.esigate.Driver;
import org.esigate.DriverConfiguration;
import org.esigate.HttpErrorPage;
import org.esigate.ResourceContext;
import org.esigate.cache.CacheEntry;
import org.esigate.cache.CacheStorage;
import org.esigate.cache.CachedResponse;
import org.esigate.oscache.OSCacheStorage;
import org.esigate.output.StringOutput;
import org.esigate.api.HttpRequest;
import org.esigate.api.HttpResponse;


public class OSCacheStorageTest extends TestCase{

	protected CacheStorage getCache() {
		return new OSCacheStorage();
	}
	
	public void testPutGet(){
		CacheStorage cache = getCache();
		assertNotNull(cache);
		String key = UUID.randomUUID().toString();
		cache.put(key, "test");
		assertEquals("test", cache.get(key));
		assertEquals("test", cache.get(key, String.class));
		assertNull(cache.get(key+"_123"));
		assertNull(cache.get(key+"_123", String.class));
	}
	
	public void testPutWithTtl() throws Exception{
		CacheStorage cache = getCache();
		assertNotNull(cache);
		String key = UUID.randomUUID().toString();
		cache.put(key, "test", 300);
		assertEquals("test", cache.get(key));
		assertEquals("test", cache.get(key, String.class));
		assertNull(cache.get(key+"_123"));
		assertNull(cache.get(key+"_123", String.class));
		Thread.sleep(350);
		assertNull(cache.get(key));
		assertNull(cache.get(key));
	}
	
	public void testCachingCachedResponse() throws Exception{
		CacheStorage cache = getCache();
		
		CachedResponse cachedResponse = new CachedResponse();
		String key = UUID.randomUUID().toString();
		cache.put(key, cachedResponse);
		CachedResponse newCachedResponse = cache.get(key, CachedResponse.class);
		
		assertEquals(cachedResponse, newCachedResponse);
		
		byte[] byteArray = "test".getBytes("utf-8");
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Date", "Wed, 02 Mar 2011 12:30:03 GMT");
		headers.put("Server", "Apache/2.2.12 (Ubuntu)");
		headers.put("Set-Cookie", "sdnsessionhash=5b55e10d5184470e607e0f0001781f77; path=/; HttpOnly");
		headers.put("Cache-Control", "private");
		headers.put("Pragma", "private");
		headers.put("x-ua-compatible", "IE=7");
		headers.put("Keep-Alive", "timeout=15, max=100");
		headers.put("Connection", "Keep-Alive");
		headers.put("Transfer-Encoding", "chunked");
		headers.put("Content-Type", "text/html; charset=windows-1251");
		
		int statusCode = 200;
		String statusMessage = "OK";
		
		cachedResponse = new CachedResponse(byteArray, "utf-8", createHeaders(headers), statusCode, statusMessage);
		key = UUID.randomUUID().toString();
		cache.put(key, cachedResponse);
		newCachedResponse = cache.get(key, CachedResponse.class);
		
		assertEquals(cachedResponse, newCachedResponse);
	}

	private Map<String, Set<String>> createHeaders(Map<String, String> src) {
		Map<String, Set<String>> result = new HashMap<String, Set<String>>(src.size());
		for (Entry<String, String> entry : src.entrySet()) {
			result.put(entry.getKey(), Collections.singleton(entry.getValue()));
		}
		return result;
	}

	public void testCachingCacheEntry() throws Exception{
		CacheStorage cache = getCache();
		String url = "http://google.com";
		CacheEntry cacheEntry = new CacheEntry(url, cache);
		String key = UUID.randomUUID().toString();
		
		byte[] byteArray = "test".getBytes("utf-8");
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Date", "Wed, 02 Mar 2011 12:30:03 GMT");
		headers.put("Server", "Apache/2.2.12 (Ubuntu)");
		headers.put("Set-Cookie", "sdnsessionhash=5b55e10d5184470e607e0f0001781f77; path=/; HttpOnly");
		headers.put("Cache-Control", "private");
		headers.put("Pragma", "private");
		headers.put("x-ua-compatible", "IE=7");
		headers.put("Keep-Alive", "timeout=15, max=100");
		headers.put("Connection", "Keep-Alive");
		headers.put("Transfer-Encoding", "chunked");
		headers.put("Content-Type", "text/html; charset=windows-1251");
		
		int statusCode = 200;
		String statusMessage = "OK";
		
		CachedResponse cachedResponse = new CachedResponse(byteArray, "utf-8", createHeaders(headers), statusCode, statusMessage);
		
		HttpRequest originalRequest = EasyMock.createMock(HttpRequest.class);
		HttpResponse originalResponse = EasyMock.createMock(HttpResponse.class);
		
		final Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Host", "google.com");
		requestHeaders.put("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; ru; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13");
		requestHeaders.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		requestHeaders.put("Accept-Language", "ru-ru,ru;q=0.8,en-us;q=0.5,en;q=0.3");
		requestHeaders.put("Accept-Encoding", "gzip,deflate");
		requestHeaders.put("Accept-Charset", "windows-1251,utf-8;q=0.7,*;q=0.7");
		requestHeaders.put("Keep-Alive", "115");
		requestHeaders.put("Connection", "keep-alive");
		requestHeaders.put("Cache-Control", "max-age=0");
		
		EasyMock.expect(originalRequest.getHeaderNames()).andReturn(requestHeaders.keySet()).anyTimes();
		EasyMock.expect(originalRequest.getHeader((String)EasyMock.anyObject())).andAnswer(new IAnswer<String>() {

			public String answer() throws Throwable {
				return requestHeaders.get(EasyMock.getCurrentArguments()[0]);
			}
		}).anyTimes();
		EasyMock.expect(originalRequest.getMethod()).andReturn("GET").anyTimes();
		
		EasyMock.replay(originalRequest, originalResponse);
		
		MockDriver mockDriver = new MockDriver("test");
		ResourceContext resourceContext = new ResourceContext(mockDriver, "/test", null, originalRequest, originalResponse);
		
		cacheEntry.put(resourceContext, cachedResponse);
		cache.put(key, cacheEntry);
		CacheEntry newCacheEntry = cache.get(key, CacheEntry.class);
		assertEquals(cacheEntry, newCacheEntry);
		assertEquals(cachedResponse, newCacheEntry.get(resourceContext));
		
	}
	
	public void testConfigaurationWithOsCache(){
		Properties props = new Properties();
		props.setProperty("remoteUrlBase", "http://localhost:8080");
		props.setProperty("cacheRefreshDelay", "1");
		props.setProperty("cacheStorageClassName", OSCacheStorage.class.getName()+"_abra-cadabra");
		try{
			new DriverConfiguration("test2", props);
		 fail();
		}catch (Exception e) {
			
		}
		
		props.setProperty("cacheStorageClassName", OSCacheStorage.class.getName());
		assertEquals(OSCacheStorage.class, new DriverConfiguration("test2", props).getCacheStorageClass());
	}
	
	private static class MockDriver extends Driver {
		private final HashMap<String, StringOutput> resources = new HashMap<String, StringOutput>();

		public MockDriver(String name) {
			this(name, new Properties());
		}
		
		public MockDriver(String name, Properties props) {
			super(name, props);
		}

		@Override
		protected StringOutput getResourceAsString(ResourceContext target)
				throws HttpErrorPage {
			StringOutput result = resources.get(target.getRelUrl());
			if (result == null)
				throw new HttpErrorPage(404, "Not found", "The page: "
						+ target.getRelUrl() + " does not exist");
			return result;
		}

	}

}

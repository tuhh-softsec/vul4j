package org.esigate.cache;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.esigate.MockDriver;
import org.esigate.ResourceContext;
import org.esigate.http.DateUtils;
import org.esigate.test.MockHttpRequest;
import org.esigate.test.MockResource;

public class CacheEntryTest extends TestCase{
	
	protected CacheStorage storage = new DefaultCacheStorage();
	
	private MockDriver mockDriver;
	private MockHttpRequest request;
	private ResourceContext resourceContext;
	private MockResource newResource;

	@Override
	protected void setUp() throws Exception {
		mockDriver = new MockDriver("mock");
		request = new MockHttpRequest();
		resourceContext = new ResourceContext(mockDriver, "/test", null, request, null);
		newResource = new MockResource();
	}
	
	public void testPutGet() throws Exception {
		CacheEntry cache = new CacheEntry("/test",storage);
		cache.setStorage(storage);

		CachedResponse cachedResponse = EasyMock.createMock(CachedResponse.class);
		
		assertNull(cache.get(resourceContext));
		EasyMock.expect(cachedResponse.getStatusCode()).andReturn(304).anyTimes();
		EasyMock.replay(cachedResponse);
		cache.put(resourceContext, cachedResponse);
		assertNull(cache.get(resourceContext));
		
		cachedResponse = new CachedResponse("test".getBytes("utf-8"), "utf-8",
				new HashMap<String, Set<String>>(), 200, "OK");
		
		cache.put(resourceContext, cachedResponse);
		assertEquals(cachedResponse, cache.get(resourceContext));
	}
	
	public void testSelect() throws Exception {
		CacheEntry cache = new CacheEntry("/test",storage);
		cache.setStorage(storage);

		CachedResponse cachedResponse = new CachedResponse("test".getBytes("utf-8"), "utf-8",
				new HashMap<String, Set<String>>(), 200, "OK");
		
		assertNull(cache.get(resourceContext));
		cache.put(resourceContext, cachedResponse);
		assertEquals(newResource, cache.select(resourceContext, cachedResponse, newResource));
		
		// check with If-Modified-Since header	
		newResource.setStatusCode(304);
		newResource.setStatusMessage("Not Modified");
		
		request.setHeader("If-Modified-Since",  DateUtils.formatDate(new Date()));
		resourceContext.setNeededForTransformation(false);
		assertEquals(newResource, cache.select(resourceContext, cachedResponse, newResource));
		
		//Check with Etag header
		newResource.setHeader("ETag", "686897696a7c876b7e");
		request.setHeader("If-Modified-Since",  null);
		request.setHeader("If-None-Match",  "686897696a7c876b7e");
		assertEquals(newResource, cache.select(resourceContext, cachedResponse, newResource));
		
		//find cache response by ETag
		cachedResponse.addHeader("ETag", "686897696a7c876b7e");
		cache.put(resourceContext, cachedResponse);
		request.setHeader("If-None-Match",  null);
		assertEquals(cachedResponse, cache.select(resourceContext, cachedResponse, newResource));
	}
	
	public void testGetValidators() throws Exception{
		CacheEntry cache = new CacheEntry("/test",storage);
		cache.setStorage(storage);
		String modifiedSince = DateUtils.formatDate(new Date());
		String etag = "686897696a7c876b7e";
		
		Map<String, Set<String>> headers = new HashMap<String, Set<String>>();
		headers.put("Cache-Control", Collections.singleton("private"));
		headers.put("ETag", Collections.singleton(etag));
		
		CachedResponse cachedResponse = new CachedResponse("test".getBytes("utf-8"), "utf-8",
				headers, 200, "OK");
		
		assertNull(cache.get(resourceContext));
		resourceContext.setNeededForTransformation(false);
		request.setHeader("If-Modified-Since", modifiedSince );
		request.setHeader("If-None-Match",  etag);
		cache.put(resourceContext, cachedResponse);
		assertEquals(modifiedSince, cache.getValidators(resourceContext, cachedResponse)
				.get("If-Modified-Since"));
		assertEquals(etag, cache.getValidators(resourceContext, cachedResponse)
				.get("If-None-Match"));
		
		//if is proxy
		resourceContext.setProxy(true);
		cache.put(resourceContext, cachedResponse);
		assertEquals(modifiedSince, cache.getValidators(resourceContext, cachedResponse)
				.get("If-Modified-Since"));
		assertEquals(etag, cache.getValidators(resourceContext, cachedResponse)
				.get("If-None-Match"));
	}
}

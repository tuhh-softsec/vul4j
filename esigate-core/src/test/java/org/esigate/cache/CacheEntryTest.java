package org.esigate.cache;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.esigate.MockDriver;
import org.esigate.ResourceContext;
import org.esigate.http.DateUtils;
import org.esigate.resource.Resource;
import org.esigate.test.MockHttpServletRequest;

public class CacheEntryTest extends TestCase{
	
	protected CacheStorage storage = new DefaultCacheStorage();
	
	private MockDriver mockDriver;
	private MockHttpServletRequest request;
	private ResourceContext resourceContext;

	@Override
	protected void setUp() throws Exception {
		mockDriver = new MockDriver("mock");
		request = new MockHttpServletRequest();
		resourceContext = new ResourceContext(mockDriver, "/test", null, request, null);
	}
	
	public void testPutGet() throws Exception {
		CacheEntry cache = new CacheEntry("/test",storage);
		cache.setStorage(storage);
		
		byte[] byteArray = "test".getBytes("utf-8");
		int statusCode = 200;
		String statusMessage = "OK";
		Map<String, Set<String>> headers = new HashMap<String, Set<String>>();
		headers.put("Cache-Control", Collections.singleton("private"));

		CachedResponse cachedResponse = EasyMock.createMock(CachedResponse.class);
		
		assertNull(cache.get(resourceContext));
		EasyMock.expect(cachedResponse.getStatusCode()).andReturn(304).anyTimes();
		EasyMock.replay(cachedResponse);
		cache.put(resourceContext, cachedResponse);
		assertNull(cache.get(resourceContext));
		
		cachedResponse = new CachedResponse(byteArray, "utf-8",
				headers, statusCode, statusMessage);
		
		cache.put(resourceContext, cachedResponse);
		assertEquals(cachedResponse, cache.get(resourceContext));
	}
	
	public void testSelect() throws Exception {
		CacheEntry cache = new CacheEntry("/test",storage);
		cache.setStorage(storage);
		
		byte[] byteArray = "test".getBytes("utf-8");
		int statusCode = 200;
		String statusMessage = "OK";
		Map<String, Set<String>> headers = new HashMap<String, Set<String>>();
		headers.put("Cache-Control", Collections.singleton("private"));

		CachedResponse cachedResponse = new CachedResponse(byteArray, "utf-8",
				headers, statusCode, statusMessage);
		Resource newResource = EasyMock
				.createMock(Resource.class);
		EasyMock.expect(newResource.getStatusCode()).andReturn(statusCode).anyTimes();
		EasyMock.expect(newResource.getStatusMessage()).andReturn(statusMessage).anyTimes();
		EasyMock.replay(newResource);
		
		assertNull(cache.get(resourceContext));
		cache.put(resourceContext, cachedResponse);
		assertEquals(newResource, cache.select(resourceContext, cachedResponse, newResource));
		
		// check with If-Modified-Since header
		EasyMock.reset(newResource);
		EasyMock.expect(newResource.getHeader("Etag")).andReturn(null).once();
		EasyMock.expect(newResource.getHeaders("ETag")).
						andReturn(new HashSet<String>(0)).anyTimes();
		setExpectations(newResource);
		
		request.setHeader("If-Modified-Since",  DateUtils.formatDate(new Date()));
		resourceContext.setNeededForTransformation(false);
		assertEquals(newResource, cache.select(resourceContext, cachedResponse, newResource));
		
		//Check with Etag header
		EasyMock.reset(newResource);
		EasyMock.expect(newResource.getHeader("Etag")).andReturn("686897696a7c876b7e").anyTimes();
		EasyMock.expect(newResource.getHeaders("ETag")).
						andReturn(Collections.singleton("686897696a7c876b7e")).anyTimes();
		setExpectations(newResource);
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
		
		byte[] byteArray = "test".getBytes("utf-8");
		Map<String, Set<String>> headers = new HashMap<String, Set<String>>();
		headers.put("Cache-Control", Collections.singleton("private"));
		headers.put("ETag", Collections.singleton(etag));
		
		CachedResponse cachedResponse = new CachedResponse(byteArray, "utf-8",
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
	
	private void setExpectations(Resource newResource)
	{
		Set<String> emptySet = Collections.emptySet();
		EasyMock.expect(newResource.getStatusCode()).andReturn(304).anyTimes();
		EasyMock.expect(newResource.getStatusMessage()).andReturn("Not Modified").anyTimes();
		EasyMock.expect(newResource.getHeaders("Date")).
						andReturn(Collections.singleton(DateUtils.formatDate(new Date()))).anyTimes();
		EasyMock.expect(newResource.getHeaders("Cache-control")).
						andReturn(Collections.singleton("max-age=60")).anyTimes();
		EasyMock.expect(newResource.getHeaders("Content-Type")).
						andReturn(emptySet).anyTimes();
		EasyMock.expect(newResource.getHeaders("Content-Length")).
						andReturn(emptySet).anyTimes();
		EasyMock.expect(newResource.getHeaders("Last-Modified")).
						andReturn(emptySet).anyTimes();
		EasyMock.expect(newResource.getHeaders("Expires")).
						andReturn(emptySet).anyTimes();
		EasyMock.expect(newResource.getHeaders("Cache-Control")).
						andReturn(emptySet).anyTimes();
		EasyMock.expect(newResource.getHeaders("Content-Encoding")).
						andReturn(emptySet).anyTimes();
		EasyMock.replay(newResource);
	}
}

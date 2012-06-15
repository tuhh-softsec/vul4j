/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.esigate.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.esigate.HttpErrorPage;
import org.esigate.Parameters;
import org.esigate.cache.EhcacheCacheStorage;
import org.esigate.test.MockHttpRequest;

/**
 * DriverConfiguration test case.
 * 
 * @author Alexis Thaveau
 * 
 */
public class HttpClientHelperTest extends TestCase {

	private HttpClientHelper httpClientHelper;
	private MockHttpClient mockHttpClient;
	private Properties properties;

	public void testIsBlackListed() {
		// by default only DriverConfiguration#DEFAULT_BLACK_LISTED_HEADERS are blacklisted
		Properties properties = new Properties();
		HttpClientHelper httpClientHelper = new HttpClientHelper();
		httpClientHelper.init(properties);

		assertRequestHeaderIsBlacklisted(httpClientHelper, "Content-Length", true);
		assertRequestHeaderIsBlacklisted(httpClientHelper, "Content-Length".toUpperCase(), true);
		assertRequestHeaderIsBlacklisted(httpClientHelper, "Content-Length".toLowerCase(), true);
		assertRequestHeaderIsBlacklisted(httpClientHelper, "Transfer-Encoding", true);
		assertResponseHeaderIsBlacklisted(httpClientHelper, "Set-Cookie", true);
		assertRequestHeaderIsBlacklisted(httpClientHelper, "Cookie", true);
		assertRequestHeaderIsBlacklisted(httpClientHelper, "Connection", true);
		assertResponseHeaderIsBlacklisted(httpClientHelper, "Keep-Alive", true);
		assertResponseHeaderIsBlacklisted(httpClientHelper, "Proxy-Authenticate", true);
		assertRequestHeaderIsBlacklisted(httpClientHelper, "Proxy-Authorization", true);
		assertRequestHeaderIsBlacklisted(httpClientHelper, "TE", true);
		assertResponseHeaderIsBlacklisted(httpClientHelper, "Trailer", true);
		assertRequestHeaderIsBlacklisted(httpClientHelper, "Upgrade", true);

		// blacklisted headers are specified via 'blackListedHeaders' property -> they are merged with default
		properties = new Properties();
		properties.setProperty(Parameters.DISCARD_REQUEST_HEADERS.name, "header");
		httpClientHelper = new HttpClientHelper();
		httpClientHelper.init(properties);

		assertRequestHeaderIsBlacklisted(httpClientHelper, "Content-Length", true);
		assertRequestHeaderIsBlacklisted(httpClientHelper, "Content-Length".toUpperCase(), true);
		assertRequestHeaderIsBlacklisted(httpClientHelper, "Content-Length".toLowerCase(), true);
		assertRequestHeaderIsBlacklisted(httpClientHelper, "Transfer-Encoding", true);
		assertResponseHeaderIsBlacklisted(httpClientHelper, "Set-Cookie", true);
		assertRequestHeaderIsBlacklisted(httpClientHelper, "Cookie", true);
		assertRequestHeaderIsBlacklisted(httpClientHelper, "Connection", true);
		assertResponseHeaderIsBlacklisted(httpClientHelper, "Keep-Alive", true);
		assertResponseHeaderIsBlacklisted(httpClientHelper, "Proxy-Authenticate", true);
		assertRequestHeaderIsBlacklisted(httpClientHelper, "Proxy-Authorization", true);
		assertRequestHeaderIsBlacklisted(httpClientHelper, "TE", true);
		assertResponseHeaderIsBlacklisted(httpClientHelper, "Trailer", true);
		assertRequestHeaderIsBlacklisted(httpClientHelper, "Upgrade", true);
		assertRequestHeaderIsBlacklisted(httpClientHelper, "header", true);
		assertRequestHeaderIsBlacklisted(httpClientHelper, "header".toUpperCase(), true);
	}

	private void assertRequestHeaderIsBlacklisted(HttpClientHelper httpClientHelper, String header, boolean blacklisted) {
		assertEquals("'" + header + "' header should " + (blacklisted ? "" : "not ") + "be blacklisted", !httpClientHelper.isForwardedRequestHeader(header), blacklisted);
	}

	private void assertResponseHeaderIsBlacklisted(HttpClientHelper httpClientHelper, String header, boolean blacklisted) {
		assertEquals("'" + header + "' header should " + (blacklisted ? "" : "not ") + "be blacklisted", !httpClientHelper.isForwardedResponseHeader(header), blacklisted);
	}

	public void testDiscardRequestHeader() {
		Properties properties = new Properties();
		properties.put(Parameters.DISCARD_REQUEST_HEADERS.name, "dummy1,dummy2");
		HttpClientHelper httpClientHelper = new HttpClientHelper();
		httpClientHelper.init(properties);
		assertFalse("Header should be discarded", httpClientHelper.isForwardedRequestHeader("dummy1"));
		assertFalse("Header should be discarded", httpClientHelper.isForwardedRequestHeader("dummy2"));
		assertTrue("Header should be forwarded", httpClientHelper.isForwardedRequestHeader("dummy3"));
	}

	public void testForwardRequestHeader() {
		Properties properties = new Properties();
		properties.put(Parameters.FORWARD_REQUEST_HEADERS.name, "Authorization");
		HttpClientHelper httpClientHelper = new HttpClientHelper();
		httpClientHelper.init(properties);
		assertTrue("Header should be forwarded", httpClientHelper.isForwardedRequestHeader("Authorization"));
	}

	public void testDiscardResponseHeader() {
		Properties properties = new Properties();
		properties.put(Parameters.DISCARD_RESPONSE_HEADERS.name, "dummy1,dummy2");
		HttpClientHelper httpClientHelper = new HttpClientHelper();
		httpClientHelper.init(properties);
		assertFalse("Header should be discarded", httpClientHelper.isForwardedResponseHeader("dummy1"));
		assertFalse("Header should be discarded", httpClientHelper.isForwardedResponseHeader("dummy2"));
		assertTrue("Header should be forwarded", httpClientHelper.isForwardedResponseHeader("dummy3"));
	}

	public void testForwardResponseHeader() {
		Properties properties = new Properties();
		properties.put(Parameters.FORWARD_RESPONSE_HEADERS.name, "WWW-Authenticate");
		HttpClientHelper httpClientHelper = new HttpClientHelper();
		httpClientHelper.init(properties);
		assertTrue("Header should be forwarded", httpClientHelper.isForwardedResponseHeader("WWW-Authenticate"));
	}

	private boolean compare(HttpResponse response1, HttpResponse response2) throws Exception, IOException {
		String entity1 = EntityUtils.toString(response1.getEntity());
		String entity2 = EntityUtils.toString(response2.getEntity());
		return entity1.equals(entity2);
	}

	private void createHttpClientHelper() {
		httpClientHelper = new HttpClientHelper();
		httpClientHelper.init(mockHttpClient, properties);
	}

	private HttpResponse createMockResponse(int statusCode, String entity) throws Exception {
		HttpResponse response = new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), statusCode, "OK");
		HttpEntity httpEntity = new StringEntity(entity);
		response.setEntity(httpEntity);
		return response;
	}

	private HttpResponse createMockResponse(String entity) throws Exception {
		HttpResponse response = new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), 200, "OK");
		HttpEntity httpEntity = new StringEntity(entity);
		response.setEntity(httpEntity);
		return response;
	}

	private HttpResponse createMockGzippedResponse(String content) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream gzos = new GZIPOutputStream(baos);
		byte[] uncompressedBytes = content.getBytes();
		gzos.write(uncompressedBytes, 0, uncompressedBytes.length);
		gzos.close();
		byte[] compressedBytes = baos.toByteArray();
		ByteArrayEntity httpEntity = new ByteArrayEntity(compressedBytes);
		httpEntity.setContentType("text/html; charset=ISO-8859-1");
		httpEntity.setContentEncoding("gzip");
		StatusLine statusLine = new BasicStatusLine(new ProtocolVersion("p", 1, 2), HttpServletResponse.SC_OK, "OK");
		BasicHttpResponse httpResponse = new BasicHttpResponse(statusLine);
		httpResponse.addHeader("Content-type", "text/html; charset=ISO-8859-1");
		httpResponse.addHeader("Content-encoding", "gzip");
		httpResponse.setEntity(httpEntity);
		return httpResponse;
	}

	private HttpResponse executeRequest() throws HttpErrorPage {
		org.esigate.api.HttpRequest httpRequest = new MockHttpRequest();
		HttpRequest apacheHttpRequest = httpClientHelper.createHttpRequest(httpRequest, "http://localhost:8080", true);
		HttpContext httpContext = new BasicHttpContext();
		return httpClientHelper.execute(apacheHttpRequest, httpContext);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		properties = new Properties();
		mockHttpClient = new MockHttpClient();
	}

	public void testCacheAndLoadBalancing() throws Exception {
		properties.put(Parameters.USE_CACHE.name, "true"); // Default value
		properties.put(Parameters.PRESERVE_HOST.name, "true");
		createHttpClientHelper();
		// First request
		HttpResponse response = createMockResponse("0");
		response.setHeader("Cache-control", "public, max-age=3600");
		mockHttpClient.setResponse(response);
		HttpResponse result = executeRequest();
		assertTrue("Response content should be '0'", compare(response, result));
		// Second request should reuse the cache entry even if it uses a different node
		HttpResponse response1 = createMockResponse("1");
		mockHttpClient.setResponse(response1);
		result = executeRequest();
		assertTrue("Response content should be unchanged as cache should be used on error.", compare(response, result));
	}

	public void testCacheStaleIfError() throws Exception {
		properties.put(Parameters.USE_CACHE.name, "true"); // Default value
		properties.put(Parameters.STALE_IF_ERROR.name, "60");
		properties.put(Parameters.STALE_WHILE_REVALIDATE.name, "60");
		properties.put(Parameters.MIN_ASYNCHRONOUS_WORKERS.name, "1");
		properties.put(Parameters.MAX_ASYNCHRONOUS_WORKERS.name, "10");
		properties.put(Parameters.HEURISTIC_CACHING_ENABLED.name, "false");
		createHttpClientHelper();
		// First request
		HttpResponse response = createMockResponse("0");
		response.setHeader("Last-modified", "Fri, 20 May 2011 00:00:00 GMT"); // HttpClient should store in cache and send a conditional request next time
		response.setHeader("Cache-control", "max-age=0"); // HttpClient should store in cache and send a conditional request next time
		mockHttpClient.setResponse(response);
		HttpResponse result = executeRequest();
		assertTrue("Response content should be '0'", compare(response, result));
		// Second request should use cache even if first response was a 404
		HttpResponse response1 = createMockResponse(500, "1");
		mockHttpClient.setResponse(response1);
		result = executeRequest();
		assertTrue("Response content should be unchanged as cache should be used on error.", compare(response, result));
		// Third request no more error but stale-while-refresh should trigger a background revalidation and serve the old version.
		HttpResponse response2 = createMockResponse(200, "2");
		mockHttpClient.setResponse(response2);
		result = executeRequest();
		assertTrue("Response should not have been refreshed yet.", compare(response, result));
		// Wait until revalidation is complete
		Thread.sleep(100);
		// Fourth request after cache has been updated at last
		result = executeRequest();
		assertTrue("Response should have been refreshed.", compare(response2, result));
	}

	public void testCacheTtl() throws Exception {
		properties.put(Parameters.USE_CACHE.name, "true"); // Default value
		properties.put(Parameters.TTL.name, "1");
		createHttpClientHelper();
		// First request
		HttpResponse response = createMockResponse("0");
		response.setHeader("Cache-control", "no-cache");
		mockHttpClient.setResponse(response);
		HttpResponse result = executeRequest();
		assertTrue("Response content should be '0'", compare(response, result));
		// Second request should use cache
		HttpResponse response1 = createMockResponse("1");
		response.setHeader("Cache-control", "no-cache");
		mockHttpClient.setResponse(response1);
		result = executeRequest();
		assertTrue("Response content should be unchanged as cache should be used.", compare(response, result));
		// Third request after cache has expired
		Thread.sleep(1000);
		result = executeRequest();
		assertTrue("Response should have been refreshed.", compare(response1, result));
	}

	public void testCacheTtlErrorPage() throws Exception {
		properties.put(Parameters.USE_CACHE.name, "true"); // Default value
		properties.put(Parameters.TTL.name, "1");
		createHttpClientHelper();
		// First request
		HttpResponse response = createMockResponse(404, "0");
		response.setHeader("Cache-control", "no-cache");
		mockHttpClient.setResponse(response);
		HttpResponse result = executeRequest();
		assertTrue("Response content should be '0'", compare(response, result));
		// Second request should use cache even if first response was a 404
		HttpResponse response1 = createMockResponse("1");
		response.setHeader("Cache-control", "no-cache");
		mockHttpClient.setResponse(response1);
		result = executeRequest();
		assertTrue("Response content should be unchanged as cache should be used.", compare(response, result));
		// Third request after cache has expired
		Thread.sleep(1000);
		result = executeRequest();
		assertTrue("Response should have been refreshed.", compare(response1, result));
	}

	public void testEhCache() throws Exception {
		properties.put(Parameters.USE_CACHE.name, "true"); // Default value
		properties.put(Parameters.CACHE_STORAGE.name, EhcacheCacheStorage.class.getName()); // Default value
		createHttpClientHelper();
		// First request
		HttpResponse response = createMockResponse("0");
		response.setHeader("Cache-control", "public, max-age=3600");
		mockHttpClient.setResponse(response);
		HttpResponse result = executeRequest();
		assertTrue("Response content should be '0'", compare(response, result));
		// Second request should reuse the cache entry even if it uses a different node
		HttpResponse response1 = createMockResponse("1");
		mockHttpClient.setResponse(response1);
		result = executeRequest();
		assertTrue("Response content should be unchanged as cache should be used on error.", compare(response, result));
	}

	public void testXCacheHeader() throws Exception {
		properties.put(Parameters.USE_CACHE.name, "true"); // Default value
		properties.put(Parameters.X_CACHE_HEADER.name, "true");
		createHttpClientHelper();
		// First request
		HttpResponse response = createMockResponse("0");
		response.setHeader("Cache-control", "public, max-age=3600");
		mockHttpClient.setResponse(response);
		HttpResponse result = executeRequest();
		assertNotNull("X-Cache header is missing", result.getFirstHeader("X-Cache"));
		assertTrue("X-Cache header should start with MISS", result.getFirstHeader("X-Cache").getValue().startsWith("MISS"));
		result = executeRequest();
		assertNotNull("X-Cache header is missing", result.getFirstHeader("X-Cache"));
		assertTrue("X-Cache header should start with HIT", result.getFirstHeader("X-Cache").getValue().startsWith("HIT"));
		result = executeRequest();
		assertNotNull("X-Cache header is missing", result.getFirstHeader("X-Cache"));
		assertTrue("There should be only 1 header X-Cache", result.getHeaders("X-Cache").length == 1);
		assertTrue("X-Cache header should start with HIT", result.getFirstHeader("X-Cache").getValue().startsWith("HIT"));
	}

	public void testXCacheHeaderWithLoadBalancingNoCache() throws Exception {
		// Use load balancing in round robin mode and check that the header indicates properly the host that was used for the request
		properties.put(Parameters.USE_CACHE.name, "true"); // Default value
		properties.put(Parameters.X_CACHE_HEADER.name, "true");
		properties.put(Parameters.REMOTE_URL_BASE_STRATEGY.name, Parameters.ROUNDROBIN);
		createHttpClientHelper();
		// First request
		HttpResponse response = createMockResponse("1");
		response.setHeader("Cache-control", "no-cache");
		mockHttpClient.setResponse(response);
		HttpContext httpContext = new BasicHttpContext();
		org.esigate.api.HttpRequest httpRequest = new MockHttpRequest("http://localhost:8080");
		HttpRequest apacheHttpRequest = httpClientHelper.createHttpRequest(httpRequest, "http://localhost:8080", true);
		HttpResponse result = httpClientHelper.execute(apacheHttpRequest, httpContext);
		Header xCacheHeader1 = result.getFirstHeader("X-Cache");
		assertNotNull("X-Cache header is missing", xCacheHeader1);
		response = createMockResponse("2");
		response.setHeader("Cache-control", "no-cache");
		mockHttpClient.setResponse(response);
		httpRequest = new MockHttpRequest("http://localhost:8080");
		apacheHttpRequest = httpClientHelper.createHttpRequest(httpRequest, "http://127.0.0.1:8080", true);
		result = httpClientHelper.execute(apacheHttpRequest, httpContext);
		Header xCacheHeader2 = result.getFirstHeader("X-Cache");
		assertNotNull("X-Cache header is missing", xCacheHeader2);
		assertTrue("X-Cache header should indicate the first backend used", xCacheHeader1.getValue().startsWith("MISS from localhost"));
		assertTrue("X-Cache header should indicate the second backend used", xCacheHeader2.getValue().startsWith("MISS from 127.0.0.1"));
		assertFalse("The 2 nodes should have been used", xCacheHeader1.getValue().equals(xCacheHeader2.getValue()));
	}

	public void testXCacheHeaderWithLoadBalancing() throws Exception {
		// Use load balancing in round robin mode and check that the header indicates properly the host that was used for the request
		properties.put(Parameters.USE_CACHE.name, "true"); // Default value
		properties.put(Parameters.PRESERVE_HOST.name, "true");
		properties.put(Parameters.X_CACHE_HEADER.name, "true");
		properties.put(Parameters.REMOTE_URL_BASE_STRATEGY.name, Parameters.ROUNDROBIN);
		createHttpClientHelper();
		// First request
		HttpResponse response = createMockResponse("1");
		response.setHeader("Cache-control", "max-age=60");
		mockHttpClient.setResponse(response);
		HttpContext httpContext = new BasicHttpContext();
		org.esigate.api.HttpRequest httpRequest = new MockHttpRequest("http://localhost:8080");
		HttpRequest apacheHttpRequest = httpClientHelper.createHttpRequest(httpRequest, "http://localhost:8080", true);
		HttpResponse result = httpClientHelper.execute(apacheHttpRequest, httpContext);
		Header xCacheHeader1 = result.getFirstHeader("X-Cache");
		assertNotNull("X-Cache header is missing", xCacheHeader1);
		response = createMockResponse("2");
		response.setHeader("Cache-control", "max-age=60");
		mockHttpClient.setResponse(response);
		httpRequest = new MockHttpRequest("http://localhost:8080");
		apacheHttpRequest = httpClientHelper.createHttpRequest(httpRequest, "http://127.0.0.1:8080", true);
		result = httpClientHelper.execute(apacheHttpRequest, httpContext);
		Header xCacheHeader2 = result.getFirstHeader("X-Cache");
		assertNotNull("X-Cache header is missing", xCacheHeader2);
		assertTrue("X-Cache header should indicate the first backend used", xCacheHeader1.getValue().startsWith("MISS from localhost"));
		assertTrue("X-Cache header should indicate reuse of the cache entry", xCacheHeader2.getValue().startsWith("HIT from 127.0.0.1"));
	}

	public void testDecompressStream() throws ClientProtocolException, IOException, HttpErrorPage {
		properties.put("default" + Parameters.REMOTE_URL_BASE.name, "http://localhost,http://127.0.0.1");
		properties.put(Parameters.USE_CACHE.name, "true"); // Default value
		createHttpClientHelper();
		String content = "To be compressed";
		HttpResponse httpResponse = createMockGzippedResponse(content);
		mockHttpClient.setResponse(httpResponse);
		HttpResponse result = executeRequest();
		String entityString = httpClientHelper.toString(result);
		assertEquals("Content should have been decompressed", content, entityString);
	}

}

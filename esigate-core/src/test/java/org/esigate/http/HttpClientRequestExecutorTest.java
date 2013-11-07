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
import java.util.Date;
import java.util.Properties;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.esigate.Driver;
import org.esigate.HttpErrorPage;
import org.esigate.Parameters;
import org.esigate.cache.EhcacheCacheStorage;
import org.esigate.cookie.CookieManager;
import org.esigate.extension.ExtensionFactory;
import org.esigate.test.TestUtils;
import org.esigate.test.conn.MockConnectionManager;

/**
 * DriverConfiguration test case.
 * 
 * @author Alexis Thaveau
 * 
 */
public class HttpClientRequestExecutorTest extends TestCase {

    private HttpClientRequestExecutor httpClientRequestExecutor;
    private MockConnectionManager mockConnectionManager;
    private Properties properties;
    private Driver driver;

    private void createHttpClientHelper() {
        driver = Driver
                .builder()
                .setName("default")
                .setProperties(properties)
                .setRequestExecutorBuilder(
                        HttpClientRequestExecutor
                                .builder()
                                .setConnectionManager(mockConnectionManager)
                                .setCookieManager(
                                        (CookieManager) ExtensionFactory.getExtension(properties,
                                                Parameters.COOKIE_MANAGER, null))).build();
        httpClientRequestExecutor = (HttpClientRequestExecutor) driver.getRequestExecutor();
    }

    private boolean compare(HttpResponse response1, HttpResponse response2) throws Exception {
        String entity1 = HttpResponseUtils.toString(response1, null);
        String entity2 = HttpResponseUtils.toString(response2, null);
        return entity1.equals(entity2);
    }

    private HttpResponse createMockResponse(int statusCode, String entity) throws Exception {
        HttpResponse response = new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), statusCode, "OK");
        if (entity != null) {
            HttpEntity httpEntity = new StringEntity(entity);
            response.setEntity(httpEntity);
        }
        return response;
    }

    private HttpResponse createMockResponse(String entity) throws Exception {
        HttpResponse response = new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_OK, "OK");
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
        HttpEntityEnclosingRequest httpRequest = TestUtils.createRequest(driver);
        GenericHttpRequest apacheHttpRequest = httpClientRequestExecutor.createHttpRequest(httpRequest,
                "http://localhost:8080", true);
        return httpClientRequestExecutor.execute(apacheHttpRequest);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE, "http://localhost:8080");
        mockConnectionManager = new MockConnectionManager();

    }

    public void testCacheAndLoadBalancing() throws Exception {
        properties.put(Parameters.REMOTE_URL_BASE, "http://localhost:8080, http://127.0.0.1:8080");
        properties.put(Parameters.USE_CACHE.getName(), "true"); // Default value
        properties.put(Parameters.PRESERVE_HOST.getName(), "true");
        createHttpClientHelper();
        // First request
        HttpResponse response = createMockResponse("0");
        response.setHeader("Cache-control", "public, max-age=3600");
        mockConnectionManager.setResponse(response);
        HttpResponse result = executeRequest();
        assertTrue("Response content should be '0'", compare(response, result));
        // Second request should reuse the cache entry even if it uses a
        // different node
        HttpResponse response1 = createMockResponse("1");
        mockConnectionManager.setResponse(response1);
        result = executeRequest();
        assertTrue("Response content should be unchanged as cache should be used.", compare(response, result));
    }

    public void testCacheAndLoadBalancingNoPreserveHost() throws Exception {
        // FIXME this test is currently broken
        // properties.put(Parameters.REMOTE_URL_BASE,
        // "http://localhost:8080, http://127.0.0.1:8080");
        // properties.put(Parameters.USE_CACHE.name, "false"); // Default value
        // properties.put(Parameters.PRESERVE_HOST.name, "false");
        // createHttpClientHelper();
        // // First request
        // HttpResponse response = createMockResponse("0");
        // response.setHeader("Cache-control", "public, max-age=3600");
        // mockHttpClient.setResponse(response);
        // HttpResponse result = executeRequest();
        // assertTrue("Response content should be '0'", compare(response,
        // result));
        // // Second request should reuse the cache entry even if it uses a
        // // different node
        // HttpResponse response1 = createMockResponse("1");
        // mockHttpClient.setResponse(response1);
        // result = executeRequest();
        // assertTrue("Response content should be unchanged as cache should be used.",
        // compare(response, result));
    }

    public void testCacheStaleIfError() throws Exception {
        properties.put(Parameters.USE_CACHE.getName(), "true"); // Default value
        properties.put(Parameters.STALE_IF_ERROR.getName(), "60");
        properties.put(Parameters.STALE_WHILE_REVALIDATE.getName(), "60");
        properties.put(Parameters.MIN_ASYNCHRONOUS_WORKERS.getName(), "1");
        properties.put(Parameters.MAX_ASYNCHRONOUS_WORKERS.getName(), "10");
        properties.put(Parameters.HEURISTIC_CACHING_ENABLED.getName(), "false");
        createHttpClientHelper();
        // First request
        HttpResponse response = createMockResponse("0");
        response.setHeader("Last-modified", "Fri, 20 May 2011 00:00:00 GMT"); // HttpClient
                                                                              // should
                                                                              // store
                                                                              // in
                                                                              // cache
                                                                              // and
                                                                              // send
                                                                              // a
                                                                              // conditional
                                                                              // request
                                                                              // next
                                                                              // time
        response.setHeader("Cache-control", "max-age=0"); // HttpClient should
                                                          // store in cache
                                                          // and send a
                                                          // conditional
                                                          // request next time
        mockConnectionManager.setResponse(response);
        HttpResponse result = executeRequest();
        assertTrue("Response content should be '0'", compare(response, result));
        // Second request should use cache even if first response was a 404
        HttpResponse response1 = createMockResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, "1");
        mockConnectionManager.setResponse(response1);
        result = executeRequest();
        assertTrue("Response content should be unchanged as cache should be used on error.", compare(response, result));
        Thread.sleep(100);
        // Third request no more error but stale-while-refresh should trigger a
        // background revalidation and serve the old version.
        HttpResponse response2 = createMockResponse(HttpStatus.SC_OK, "2");
        mockConnectionManager.setResponse(response2);
        result = executeRequest();
        assertTrue("Response should not have been refreshed yet.", compare(response, result));
        // Wait until revalidation is complete
        Thread.sleep(100);
        // Fourth request after cache has been updated at last
        result = executeRequest();
        assertTrue("Response should have been refreshed.", compare(response2, result));
    }

    public void testCacheTtl() throws Exception {
        properties.put(Parameters.USE_CACHE.getName(), "true"); // Default value
        properties.put(Parameters.TTL.getName(), "1");
        createHttpClientHelper();
        // First request
        HttpResponse response = createMockResponse("0");
        response.setHeader("Cache-control", "no-cache");
        mockConnectionManager.setResponse(response);
        HttpResponse result = executeRequest();
        assertTrue("Response content should be '0'", compare(response, result));
        // Second request should use cache
        HttpResponse response1 = createMockResponse("1");
        response.setHeader("Cache-control", "no-cache");
        mockConnectionManager.setResponse(response1);
        result = executeRequest();
        assertTrue("Response content should be unchanged as cache should be used.", compare(response, result));
        // Third request after cache has expired
        Thread.sleep(1000);
        result = executeRequest();
        assertTrue("Response should have been refreshed.", compare(response1, result));
    }

    public void assertStatusCodeIsCachedWithTtl(int statusCode, boolean responseHasBody) throws Exception {
        properties.put(Parameters.USE_CACHE.getName(), "true"); // Default value
        properties.put(Parameters.TTL.getName(), "1");
        properties.put(Parameters.X_CACHE_HEADER, "true");
        createHttpClientHelper();
        // First request
        HttpResponse response;
        if (responseHasBody) {
            response = createMockResponse(statusCode, "0");
        } else {
            response = createMockResponse(statusCode, null);
        }
        response.setHeader("Cache-control", "no-cache");
        if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
            response.setHeader("Location", "http://www.foo.com");
        }
        mockConnectionManager.setResponse(response);
        HttpResponse result = executeRequest();
        assertTrue(result.getFirstHeader("X-cache").getValue().startsWith("MISS"));
        // Second request should use cache even if first response was a 404
        HttpResponse response1 = createMockResponse("1");
        response.setHeader("Cache-control", "no-cache");
        mockConnectionManager.setResponse(response1);
        result = executeRequest();
        assertTrue("Response content should be unchanged as cache should be used.", result.getFirstHeader("X-cache")
                .getValue().startsWith("HIT"));
        // Third request after cache has expired
        Thread.sleep(1000);
        result = executeRequest();
        assertTrue("Response should have been refreshed.",
                result.getFirstHeader("X-cache").getValue().startsWith("MISS"));
    }

    public void test200OkPageIsCachedWithTTL() throws Exception {
        assertStatusCodeIsCachedWithTtl(HttpStatus.SC_OK, true);
    }

    public void test301RedirectPageIsCachedWithTTL() throws Exception {
        assertStatusCodeIsCachedWithTtl(HttpStatus.SC_MOVED_PERMANENTLY, false);
    }

    public void test302RedirectPageIsCachedWithTTL() throws Exception {
        assertStatusCodeIsCachedWithTtl(HttpStatus.SC_MOVED_TEMPORARILY, false);
    }

    public void test404ErrorPageIsCachedWithTTL() throws Exception {
        assertStatusCodeIsCachedWithTtl(HttpStatus.SC_NOT_FOUND, true);
    }

    public void test500ErrorPageIsCachedWithTTL() throws Exception {
        assertStatusCodeIsCachedWithTtl(HttpStatus.SC_INTERNAL_SERVER_ERROR, true);
    }

    public void test503ErrorPageIsCachedWithTTL() throws Exception {
        assertStatusCodeIsCachedWithTtl(HttpStatus.SC_SERVICE_UNAVAILABLE, true);
    }

    public void testEhCache() throws Exception {
        properties.put(Parameters.USE_CACHE.getName(), "true"); // Default value
        properties.put(Parameters.CACHE_STORAGE.getName(), EhcacheCacheStorage.class.getName()); // Default
        // value
        createHttpClientHelper();
        // First request
        HttpResponse response = createMockResponse("0");
        response.setHeader("Cache-control", "public, max-age=3600");
        mockConnectionManager.setResponse(response);
        HttpResponse result = executeRequest();
        assertTrue("Response content should be '0'", compare(response, result));
        // Second request should reuse the cache entry even if it uses a
        // different node
        HttpResponse response1 = createMockResponse("1");
        mockConnectionManager.setResponse(response1);
        result = executeRequest();
        assertTrue("Response content should be unchanged as cache should be used on error.", compare(response, result));
    }

    public void testXCacheHeader() throws Exception {
        properties.put(Parameters.USE_CACHE.getName(), "true"); // Default value
        properties.put(Parameters.X_CACHE_HEADER.getName(), "true");
        createHttpClientHelper();
        // First request
        HttpResponse response = createMockResponse("0");
        response.setHeader("Cache-control", "public, max-age=3600");
        mockConnectionManager.setResponse(response);
        HttpResponse result = executeRequest();
        assertNotNull("X-Cache header is missing", result.getFirstHeader("X-Cache"));
        assertTrue("X-Cache header should start with MISS",
                result.getFirstHeader("X-Cache").getValue().startsWith("MISS"));
        result = executeRequest();
        assertNotNull("X-Cache header is missing", result.getFirstHeader("X-Cache"));
        assertTrue("X-Cache header should start with HIT", result.getFirstHeader("X-Cache").getValue()
                .startsWith("HIT"));
        result = executeRequest();
        assertNotNull("X-Cache header is missing", result.getFirstHeader("X-Cache"));
        assertTrue("There should be only 1 header X-Cache", result.getHeaders("X-Cache").length == 1);
        assertTrue("X-Cache header should start with HIT", result.getFirstHeader("X-Cache").getValue()
                .startsWith("HIT"));
    }

    public void testXCacheHeaderWithLoadBalancingNoCache() throws Exception {
        // Use load balancing in round robin mode and check that the header
        // indicates properly the host that was used for the request
        properties.put(Parameters.USE_CACHE.getName(), "true"); // Default value
        properties.put(Parameters.X_CACHE_HEADER.getName(), "true");
        properties.put(Parameters.REMOTE_URL_BASE_STRATEGY.getName(), Parameters.ROUNDROBIN);
        createHttpClientHelper();
        // First request
        HttpResponse response = createMockResponse("1");
        response.setHeader("Cache-control", "no-cache");
        mockConnectionManager.setResponse(response);
        HttpEntityEnclosingRequest httpRequest = TestUtils.createRequest("http://localhost:8080", driver);
        driver.initHttpRequestParams(httpRequest, null);
        GenericHttpRequest apacheHttpRequest = httpClientRequestExecutor.createHttpRequest(httpRequest,
                "http://localhost:8080", true);
        HttpResponse result = httpClientRequestExecutor.execute(apacheHttpRequest);
        Header xCacheHeader1 = result.getFirstHeader("X-Cache");
        assertNotNull("X-Cache header is missing", xCacheHeader1);
        response = createMockResponse("2");
        response.setHeader("Cache-control", "no-cache");
        mockConnectionManager.setResponse(response);
        httpRequest = TestUtils.createRequest("http://localhost:8080", driver);
        apacheHttpRequest = httpClientRequestExecutor.createHttpRequest(httpRequest, "http://127.0.0.1:8080", true);
        result = httpClientRequestExecutor.execute(apacheHttpRequest);
        Header xCacheHeader2 = result.getFirstHeader("X-Cache");
        assertNotNull("X-Cache header is missing", xCacheHeader2);
        assertTrue("X-Cache header should indicate the first backend used",
                xCacheHeader1.getValue().startsWith("MISS from localhost"));
        assertTrue("X-Cache header should indicate the second backend used",
                xCacheHeader2.getValue().startsWith("MISS from 127.0.0.1"));
        assertFalse("The 2 nodes should have been used", xCacheHeader1.getValue().equals(xCacheHeader2.getValue()));
    }

    public void testXCacheHeaderWithLoadBalancing() throws Exception {
        // Use load balancing in round robin mode and check that the header
        // indicates properly the host that was used for the request
        properties.put(Parameters.USE_CACHE.getName(), "true"); // Default value
        properties.put(Parameters.PRESERVE_HOST.getName(), "true");
        properties.put(Parameters.X_CACHE_HEADER.getName(), "true");
        properties.put(Parameters.REMOTE_URL_BASE_STRATEGY.getName(), Parameters.ROUNDROBIN);
        createHttpClientHelper();
        // First request
        HttpResponse response = createMockResponse("1");
        response.setHeader("Cache-control", "max-age=60");
        mockConnectionManager.setResponse(response);
        HttpEntityEnclosingRequest httpRequest = TestUtils.createRequest("http://localhost:8080", driver);
        GenericHttpRequest apacheHttpRequest = httpClientRequestExecutor.createHttpRequest(httpRequest,
                "http://localhost:8080", true);
        HttpResponse result = httpClientRequestExecutor.execute(apacheHttpRequest);
        Header xCacheHeader1 = result.getFirstHeader("X-Cache");
        assertNotNull("X-Cache header is missing", xCacheHeader1);
        response = createMockResponse("2");
        response.setHeader("Cache-control", "max-age=60");
        mockConnectionManager.setResponse(response);
        httpRequest = TestUtils.createRequest("http://localhost:8080", driver);
        apacheHttpRequest = httpClientRequestExecutor.createHttpRequest(httpRequest, "http://127.0.0.1:8080", true);
        result = httpClientRequestExecutor.execute(apacheHttpRequest);
        Header xCacheHeader2 = result.getFirstHeader("X-Cache");
        assertNotNull("X-Cache header is missing", xCacheHeader2);
        assertTrue("X-Cache header should indicate the first backend used",
                xCacheHeader1.getValue().startsWith("MISS from localhost"));
        assertTrue("X-Cache header should indicate reuse of the cache entry",
                xCacheHeader2.getValue().startsWith("HIT from 127.0.0.1"));
    }

    public void testDecompressStream() throws IOException, HttpErrorPage {
        properties.put("default" + Parameters.REMOTE_URL_BASE.getName(), "http://localhost,http://127.0.0.1");
        properties.put(Parameters.USE_CACHE.getName(), "true"); // Default value
        createHttpClientHelper();
        String content = "To be compressed";
        HttpResponse httpResponse = createMockGzippedResponse(content);
        mockConnectionManager.setResponse(httpResponse);
        HttpResponse result = executeRequest();
        String entityString = HttpResponseUtils.toString(result, null);
        assertEquals("Content should have been decompressed", content, entityString);
    }

    private void sendRequestAndCheckHostHeader(String uri, String targetHost, String virtualHost,
            String expectedHostHeader) throws Exception {
        properties = new Properties();
        properties.put(Parameters.PRESERVE_HOST.getName(), "true");
        properties.put(Parameters.REMOTE_URL_BASE.getName(), targetHost);
        properties.put(Parameters.USE_CACHE.getName(), "false");
        createHttpClientHelper();

        mockConnectionManager.setResponse(createMockResponse(""));
        HttpEntityEnclosingRequest httpRequest = TestUtils.createRequest(uri, driver);
        if (virtualHost != null) {
            httpRequest.setHeader("Host", virtualHost);
        }
        // I dn't think it is possible to have a virtualHost that is different
        // from the host in request uri but let's check that Host header is
        // taken into account
        GenericHttpRequest apacheHttpRequest = httpClientRequestExecutor.createHttpRequest(httpRequest, targetHost,
                true);
        httpClientRequestExecutor.execute(apacheHttpRequest);
        Header[] headers = mockConnectionManager.getSentRequest().getHeaders("Host");
        assertEquals("We should have 1 Host header", 1, headers.length);
        assertEquals("Wrong Host header", expectedHostHeader, headers[0].getValue());
    }

    public void testPreserveHost() throws Exception {
        sendRequestAndCheckHostHeader("http://www.foo.com:123", "http://localhost:8080", null, "www.foo.com:123");
    }

    public void testPreserveHostWithHostHeader() throws Exception {
        sendRequestAndCheckHostHeader("http://www.foo.com:123", "http://localhost:8080", "www.bar.com:345",
                "www.bar.com:345");
        // Should be copied as is even when default port
        sendRequestAndCheckHostHeader("http://www.foo.com", "http://localhost:8080", "www.foo.com:80", "www.foo.com:80");
    }

    /**
     * 0000121: preserveHost adds port number even if default (ex: localhost:80).
     * https://sourceforge.net/apps/mantisbt/webassembletool/view.php?id=121
     * 
     * @throws Exception
     */
    public void testPreserveHostDoesNotAddPortIfDefault() throws Exception {
        sendRequestAndCheckHostHeader("http://www.foo.com", "http://localhost:8080", null, "www.foo.com");
        // Non standard port
        sendRequestAndCheckHostHeader("http://www.foo.com:443", "http://localhost:8080", null, "www.foo.com:443");
        // Remove port when standard port
        sendRequestAndCheckHostHeader("https://www.foo.com:443", "http://localhost:8080", null, "www.foo.com");
    }

    /**
     * 0000123: Incorrect Host header while making include where master application has preserveHost=true and provider.
     * preserveHost=false https://sourceforge.net/apps/mantisbt/webassembletool/view.php?id=123
     * 
     * @throws Exception
     */
    public void testIssue123() throws Exception {
        mockConnectionManager.setResponse(createMockResponse(""));

        // Create a first HttpClientHelper with preserveHost = true
        properties = new Properties();
        properties.put(Parameters.PRESERVE_HOST.getName(), "true");
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost:8080");
        properties.put(Parameters.USE_CACHE.getName(), "false");
        createHttpClientHelper();
        HttpClientRequestExecutor httpClientHelper1 = httpClientRequestExecutor;

        // Create a second HttpClientHelper with preserveHost = true
        properties = new Properties();
        properties.put(Parameters.PRESERVE_HOST.getName(), "false");
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost:8080");
        properties.put(Parameters.USE_CACHE.getName(), "false");
        createHttpClientHelper();
        HttpClientRequestExecutor httpClientHelper2 = httpClientRequestExecutor;

        HttpEntityEnclosingRequest httpRequest = TestUtils.createRequest("http://www.foo.com", driver);

        // Include something with first HttpClientHelper
        GenericHttpRequest apacheHttpRequest = httpClientHelper1.createHttpRequest(httpRequest,
                "http://localhost:8080", false);
        // Also manually add a fake param to see if it is set in original
        // request or copied to other requests
        apacheHttpRequest.getParams().setParameter("test", "test");
        httpClientHelper1.execute(apacheHttpRequest);
        Header[] headers = mockConnectionManager.getSentRequest().getHeaders("Host");
        assertEquals("We should have 1 Host header", 1, headers.length);
        assertEquals("www.foo.com", headers[0].getValue());

        GenericHttpRequest apacheHttpRequest2 = httpClientHelper2.createHttpRequest(httpRequest,
                "http://localhost:8080", false);
        httpClientHelper2.execute(apacheHttpRequest2);
        Header[] headers2 = mockConnectionManager.getSentRequest().getHeaders("Host");
        assertEquals("We should have 1 Host header", 1, headers2.length);
        assertEquals("localhost:8080", headers2[0].getValue());

        assertNull(httpRequest.getParams().getParameter("test"));
        assertNull(apacheHttpRequest2.getParams().getParameter("test"));
        assertNotNull(apacheHttpRequest.getParams().getParameter("test"));
    }

    /**
     * Executes 2 requests, there is a cookie sent in the response that contains spaces in the value.
     * 
     * @throws Exception
     */
    public void testCookieWithSpaces() throws Exception {
        properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost:8080");
        properties.put(Parameters.FORWARD_COOKIES.getName(), "*");
        properties.put(Parameters.USE_CACHE, "false");
        createHttpClientHelper();
        HttpEntityEnclosingRequest originalRequest = TestUtils.createRequest(driver);
        GenericHttpRequest request = httpClientRequestExecutor.createHttpRequest(originalRequest,
                "http://localhost:8080", false);
        HttpResponse response = createMockResponse("");
        response.addHeader("Set-Cookie", "test=\"a b\"; Version=1");
        mockConnectionManager.setResponse(response);
        httpClientRequestExecutor.execute(request);
        request = httpClientRequestExecutor.createHttpRequest(originalRequest, "http://localhost:8080", false);
        httpClientRequestExecutor.execute(request);
        HttpRequest sentRequest = mockConnectionManager.getSentRequest();
        assertNotNull(sentRequest.getHeaders("Cookie"));
        assertEquals(1, sentRequest.getHeaders("Cookie").length);
        assertEquals("test=\"a b\"", sentRequest.getHeaders("Cookie")[0].getValue());
    }

    /**
     * Test that we don't have a NullpointerException when forcing the caching (ttl).
     * 
     * @throws Exception
     */
    public void testForcedTtlWith304ResponseCode() throws Exception {
        properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost:8080");
        properties.put(Parameters.TTL.getName(), "1000");
        createHttpClientHelper();
        HttpEntityEnclosingRequest originalRequest = TestUtils.createRequest(driver);
        originalRequest.addHeader("If-Modified-Since", "Fri, 15 Jun 2012 21:06:25 GMT");
        GenericHttpRequest request = httpClientRequestExecutor.createHttpRequest(originalRequest,
                "http://localhost:8080", false);
        HttpResponse response = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1),
                HttpStatus.SC_NOT_MODIFIED, "Not Modified"));
        mockConnectionManager.setResponse(response);
        HttpResponse result = httpClientRequestExecutor.execute(request);
        if (result.getEntity() != null) {
            result.getEntity().writeTo(new NullOutputStream());
            // We should have had a NullpointerException
        }
    }

    /**
     * Test that we don't have a NullpointerException when forcing the caching (ttl).
     * 
     * @throws Exception
     */
    public void testForcedTtlWith301ResponseCode() throws Exception {
        properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost:8080");
        properties.put(Parameters.TTL.getName(), "1000");
        createHttpClientHelper();
        HttpEntityEnclosingRequest originalRequest = TestUtils.createRequest(driver);
        GenericHttpRequest request = httpClientRequestExecutor.createHttpRequest(originalRequest,
                "http://localhost:8080", false);
        HttpResponse response = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1),
                HttpStatus.SC_MOVED_PERMANENTLY, "Moved permanently"));
        response.addHeader("Location", "http://www.foo.com");
        mockConnectionManager.setResponse(response);
        HttpResponse result = httpClientRequestExecutor.execute(request);
        if (result.getEntity() != null) {
            result.getEntity().writeTo(new NullOutputStream());
            // We should have had a NullpointerException
        }
    }

    /**
     * Test that we don't have a NullpointerException when forcing the caching (ttl).
     * 
     * @throws Exception
     */
    public void testForcedTtlWith302ResponseCode() throws Exception {
        properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost:8080");
        properties.put(Parameters.TTL.getName(), "1000");
        createHttpClientHelper();
        HttpEntityEnclosingRequest originalRequest = TestUtils.createRequest(driver);
        GenericHttpRequest request = httpClientRequestExecutor.createHttpRequest(originalRequest,
                "http://localhost:8080", false);
        HttpResponse response = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1),
                HttpStatus.SC_MOVED_TEMPORARILY, "Moved temporarily"));
        response.addHeader("Location", "http://www.foo.com");
        mockConnectionManager.setResponse(response);
        HttpResponse result = httpClientRequestExecutor.execute(request);
        if (result.getEntity() != null) {
            result.getEntity().writeTo(new NullOutputStream());
            // We should have had a NullpointerException
        }
    }

    /**
     * Test Expires response header with ttl forced.
     * 
     * @throws Exception
     */
    public void testExpiresResponseHeaderWithForcedTtl() throws Exception {
        properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE, "http://localhost:8080");
        properties.put(Parameters.TTL, "1");
        properties.put(Parameters.X_CACHE_HEADER, "true");
        properties.put(Parameters.HEURISTIC_CACHING_ENABLED, "false");
        createHttpClientHelper();
        HttpEntityEnclosingRequest originalRequest = TestUtils.createRequest(driver);

        GenericHttpRequest request = httpClientRequestExecutor.createHttpRequest(originalRequest,
                "http://localhost:8080", false);

        HttpResponse response = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1),
                HttpStatus.SC_OK, "OK"));
        response.addHeader("Date", "Mon, 10 Dec 2012 19:37:52 GMT");
        response.addHeader("Last-Modified", "Mon, 10 Dec 2012 19:35:27 GMT");
        response.addHeader("Expires", "Mon, 10 Dec 2012 20:35:27 GMT");
        response.addHeader("Cache-Control", "private, no-cache, must-revalidate, proxy-revalidate");
        response.setEntity(new StringEntity("test"));
        mockConnectionManager.setResponse(response);

        // First call to load the cache
        HttpResponse result = httpClientRequestExecutor.execute(request);
        assertNotNull(result.getFirstHeader("Expires"));
        assertNotNull(result.getFirstHeader("Cache-control"));
        assertEquals("public, max-age=1", (result.getFirstHeader("Cache-control").getValue()));

        // Same test with the cache entry
        // Change the response to check that the cache is used
        response = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1),
                HttpStatus.SC_NOT_MODIFIED, "Not modified"));
        response.addHeader("Date", "Mon, 10 Dec 2012 19:37:52 GMT");
        response.addHeader("Expires", "Mon, 10 Dec 2012 20:35:27 GMT");
        response.addHeader("Cache-Control", "private, no-cache, must-revalidate, proxy-revalidate");
        mockConnectionManager.setResponse(response);

        result = httpClientRequestExecutor.execute(request);
        // Check that the cache has been used
        assertTrue(result.getFirstHeader("X-cache").getValue(),
                result.getFirstHeader("X-cache").getValue().startsWith("HIT"));
        assertNotNull(result.getFirstHeader("Expires"));
        assertNotNull(result.getFirstHeader("Cache-control"));
        assertEquals("public, max-age=1", (result.getFirstHeader("Cache-control").getValue()));

        // Wait for a revalidation to occur
        Thread.sleep(1000);

        result = httpClientRequestExecutor.execute(request);
        // Check that the revalidation occurred
        assertNotNull(result.getFirstHeader("Expires"));
        assertNotNull(result.getFirstHeader("Cache-control"));
        assertEquals("public, max-age=1", (result.getFirstHeader("Cache-control").getValue()));
        assertTrue(result.getFirstHeader("X-cache").getValue(),
                result.getFirstHeader("X-cache").getValue().startsWith("VALIDATED"));
    }

    /**
     * Test that we do not return a 304 to a non-conditional request when ttl forced.
     * 
     * @throws Exception
     */
    public void testDoNotReturn304ForNonConditionalRequestWhenTtlSet() throws Exception {
        properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE, "http://localhost:8080");
        properties.put(Parameters.TTL, "1");
        properties.put(Parameters.X_CACHE_HEADER, "true");
        createHttpClientHelper();

        HttpEntityEnclosingRequest originalRequest = TestUtils.createRequest(driver);
        GenericHttpRequest request1 = httpClientRequestExecutor.createHttpRequest(originalRequest,
                "http://localhost:8080", false);
        request1.addHeader("If-None-Match", "etag");

        HttpResponse response = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1),
                HttpStatus.SC_NOT_MODIFIED, "Not modified"));
        response.addHeader("Date", "Mon, 10 Dec 2012 19:37:52 GMT");
        response.addHeader("Etag", "etag");
        response.addHeader("Cache-Control", "max-age=0");
        mockConnectionManager.setResponse(response);

        // First request returns a 304
        HttpResponse result1 = httpClientRequestExecutor.execute(request1);
        assertEquals(HttpStatus.SC_NOT_MODIFIED, result1.getStatusLine().getStatusCode());
        assertTrue(result1.getFirstHeader("X-cache").getValue(), result1.getFirstHeader("X-cache").getValue()
                .startsWith("MISS"));
        assertNull(result1.getEntity());

        // Second request should use the cache and return a
        // 304 again
        HttpResponse result2 = httpClientRequestExecutor.execute(request1);
        assertEquals(HttpStatus.SC_NOT_MODIFIED, result1.getStatusLine().getStatusCode());
        assertTrue(result2.getFirstHeader("X-cache").getValue(), result2.getFirstHeader("X-cache").getValue()
                .startsWith("HIT"));
        assertNull(result2.getEntity());

        HttpResponse response2 = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1),
                HttpStatus.SC_OK, "Ok"));
        response2.addHeader("Date", "Mon, 10 Dec 2012 19:37:52 GMT");
        response2.addHeader("Etag", "etag");
        response2.addHeader("Cache-Control", "max-age=0");
        response2.setEntity(new StringEntity("test"));
        mockConnectionManager.setResponse(response2);

        // Third request not conditional ! Should call backend server as we
        // don't have the entity in the cache.
        GenericHttpRequest request2 = httpClientRequestExecutor.createHttpRequest(originalRequest,
                "http://localhost:8080", false);
        HttpResponse result3 = httpClientRequestExecutor.execute(request2);
        assertEquals(HttpStatus.SC_OK, result3.getStatusLine().getStatusCode());
        assertTrue(result3.getFirstHeader("X-cache").getValue(), !result3.getFirstHeader("X-cache").getValue()
                .startsWith("HIT"));
        assertNotNull(result3.getEntity());
    }

    /**
     * Test that we set a X-Forwarded-Proto header in backend requests.
     * 
     * @throws Exception
     */
    public void testXForwardedProtoHeader() throws Exception {
        properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost:8080");
        createHttpClientHelper();

        mockConnectionManager.setResponse(createMockResponse(""));
        HttpEntityEnclosingRequest httpRequest = TestUtils.createRequest("https://wwww.foo.com", driver);
        GenericHttpRequest apacheHttpRequest = httpClientRequestExecutor.createHttpRequest(httpRequest,
                "http://localhost:8080", true);
        httpClientRequestExecutor.execute(apacheHttpRequest);
        Header[] headers = mockConnectionManager.getSentRequest().getHeaders("X-Forwarded-Proto");
        assertEquals("We should have 1 X-Forwarded-Proto header", 1, headers.length);
        assertEquals("Wrong X-Forwarded-Proto header", "https", headers[0].getValue());
    }

    public void test304CachedResponseIsReusedWithIfModifiedSinceRequest() throws Exception {
        properties.put(Parameters.USE_CACHE, "true"); // Default value
        properties.put(Parameters.X_CACHE_HEADER, "true");
        createHttpClientHelper();
        // First request
        String now = DateUtils.formatDate(new Date());
        String yesterday = DateUtils.formatDate(new Date(System.currentTimeMillis() - 86400 * 1000));
        String inOneHour = DateUtils.formatDate(new Date(System.currentTimeMillis() + 3600 * 1000));
        HttpResponse response = createMockResponse(HttpStatus.SC_NOT_MODIFIED, null);
        response.setHeader("Date", now);
        response.setHeader("Expires", inOneHour);
        response.setHeader("Cache-Control", "max-age=3600");
        mockConnectionManager.setResponse(response);

        // First request to load the cache
        HttpEntityEnclosingRequest httpRequest = TestUtils.createRequest(driver);
        httpRequest.addHeader("If-Modified-Since", yesterday);
        GenericHttpRequest apacheHttpRequest = httpClientRequestExecutor.createHttpRequest(httpRequest,
                "http://localhost:8080", true);
        HttpResponse result = httpClientRequestExecutor.execute(apacheHttpRequest);
        assertTrue(result.getFirstHeader("X-cache").getValue().startsWith("MISS"));
        assertEquals(HttpStatus.SC_NOT_MODIFIED, result.getStatusLine().getStatusCode());

        // Second request should use cache
        HttpEntityEnclosingRequest httpRequest2 = TestUtils.createRequest(driver);
        httpRequest2.addHeader("If-Modified-Since", yesterday);
        GenericHttpRequest apacheHttpRequest2 = httpClientRequestExecutor.createHttpRequest(httpRequest2,
                "http://localhost:8080", true);
        HttpResponse result2 = httpClientRequestExecutor.execute(apacheHttpRequest2);
        assertTrue(result2.getFirstHeader("X-cache").getValue().startsWith("HIT"));
        assertEquals(HttpStatus.SC_NOT_MODIFIED, result2.getStatusLine().getStatusCode());
    }

}

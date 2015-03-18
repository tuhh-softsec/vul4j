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

package org.esigate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.zip.GZIPOutputStream;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;
import org.esigate.esi.EsiRenderer;
import org.esigate.extension.DefaultCharset;
import org.esigate.http.DateUtils;
import org.esigate.http.HttpClientRequestExecutor;
import org.esigate.http.HttpResponseUtils;
import org.esigate.http.IncomingRequest;
import org.esigate.tags.BlockRenderer;
import org.esigate.tags.TemplateRenderer;
import org.esigate.test.TestUtils;
import org.esigate.test.conn.IResponseHandler;
import org.esigate.test.conn.MockConnectionManager;
import org.esigate.test.http.HttpResponseBuilder;

public class DriverTest extends TestCase {
    private IncomingRequest.Builder request;
    private MockConnectionManager mockConnectionManager;

    @Override
    protected void setUp() {
        mockConnectionManager = new MockConnectionManager();

        MockRequestExecutor provider = MockRequestExecutor.createMockDriver("mock");
        provider.addResource("/testBlock",
                "abc some<!--$beginblock$A$-->some text goes here<!--$endblock$A$--> cdf hello");
        provider.addResource("/testTemplateFullPage",
                "some <!--$beginparam$key$-->some hidden text goes here<!--$endparam$key$--> printed");
        provider.addResource("/testTemplate",
                "abc some<!--$begintemplate$A$-->some text goes here<!--$endtemplate$A$--> cdf hello");
        request = TestUtils.createIncomingRequest();
    }

    public void testRenderBlock() throws IOException, HttpErrorPage {
        CloseableHttpResponse response =
                DriverFactory.getInstance("mock").render("/testBlock", request.build(),
                        new BlockRenderer("A", "/testBlock"));

        assertEquals("some text goes here", HttpResponseUtils.toString(response));

        response =
                DriverFactory.getInstance("mock").render("$(vartestBlock)", request.build(),
                        new BlockRenderer("A", "$(vartestBlock)"));
        assertEquals("some text goes here", HttpResponseUtils.toString(response));

        response =
                DriverFactory.getInstance("mock").render("/$(vartest)$(varBlock)", request.build(),
                        new BlockRenderer("A", "/$(vartest)$(varBlock)"));
        assertEquals("some text goes here", HttpResponseUtils.toString(response));

    }

    public void testRenderTemplateFullPage() throws IOException, HttpErrorPage {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", "'value'");
        params.put("some other key", "'another value'");
        CloseableHttpResponse response =
                DriverFactory.getInstance("mock").render("/testTemplateFullPage", request.build(),
                        new TemplateRenderer(null, params, "/testTemplateFullPage"));
        String result = HttpResponseUtils.toString(response);
        assertFalse(result.contains("key"));
        assertTrue(result.contains("'value'"));
        assertFalse(result.contains("some other key"));
        assertEquals("some 'value' printed", result);
    }

    public void testRenderTemplate() throws IOException, HttpErrorPage {
        IncomingRequest incomingRequest = request.build();

        CloseableHttpResponse response =
                DriverFactory.getInstance("mock").render("/testTemplate", incomingRequest,
                        new TemplateRenderer("A", null, "/testTemplate"));
        assertEquals("some text goes here", HttpResponseUtils.toString(response));

        response =
                DriverFactory.getInstance("mock").render("/test$(varTemplate)", incomingRequest,
                        new TemplateRenderer("A", null, "/test$(varTemplate)"));
        assertEquals("some text goes here", HttpResponseUtils.toString(response));

    }

    public void testHeadersPreservedWhenError500() throws Exception {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost");
        HttpResponse response =
                new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_INTERNAL_SERVER_ERROR,
                        "Internal Server Error");
        response.addHeader("Content-type", "Text/html;Charset=UTF-8");
        response.addHeader("Dummy", "dummy");
        HttpEntity httpEntity = new StringEntity("Error", "UTF-8");
        response.setEntity(httpEntity);
        mockConnectionManager.setResponse(response);
        Driver driver = createMockDriver(properties, mockConnectionManager);
        CloseableHttpResponse driverResponse;
        try {
            driverResponse = driver.proxy("/", request.build());
            fail("We should get an HttpErrorPage");
        } catch (HttpErrorPage e) {
            driverResponse = e.getHttpResponse();
        }
        int statusCode = driverResponse.getStatusLine().getStatusCode();
        assertEquals("Status code", HttpStatus.SC_INTERNAL_SERVER_ERROR, statusCode);
        assertTrue("Header 'Dummy'", driverResponse.containsHeader("Dummy"));
    }

    public void testHeadersFilteredWhenError500() throws Exception {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost");
        HttpResponse response =
                new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_INTERNAL_SERVER_ERROR,
                        "Internal Server Error");
        response.addHeader("Content-type", "Text/html;Charset=UTF-8");
        response.addHeader("Transfer-Encoding", "dummy");
        HttpEntity httpEntity = new StringEntity("Error", "UTF-8");
        response.setEntity(httpEntity);
        mockConnectionManager.setResponse(response);
        Driver driver = createMockDriver(properties, mockConnectionManager);
        CloseableHttpResponse driverResponse;
        try {
            driverResponse = driver.proxy("/", request.build());
            fail("We should get an HttpErrorPage");
        } catch (HttpErrorPage e) {
            driverResponse = e.getHttpResponse();
        }
        int statusCode = driverResponse.getStatusLine().getStatusCode();
        assertEquals("Status code", HttpStatus.SC_INTERNAL_SERVER_ERROR, statusCode);
        assertFalse("Header 'Transfer-Encoding'", driverResponse.containsHeader("Transfer-Encoding"));
    }

    public void testSpecialCharacterInErrorPage() throws Exception {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost");
        HttpResponse response =
                new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_INTERNAL_SERVER_ERROR,
                        "Internal Server Error");
        response.addHeader("Content-type", "Text/html;Charset=UTF-8");
        HttpEntity httpEntity = new StringEntity("é", "UTF-8");
        response.setEntity(httpEntity);
        mockConnectionManager.setResponse(response);
        Driver driver = createMockDriver(properties, mockConnectionManager);
        CloseableHttpResponse driverResponse;
        try {
            driverResponse = driver.proxy("/", request.build());
            fail("We should get an HttpErrorPage");
        } catch (HttpErrorPage e) {
            driverResponse = e.getHttpResponse();
        }
        assertEquals("é", HttpResponseUtils.toString(driverResponse));
    }

    public void testGzipErrorPage() throws Exception {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost");
        HttpResponse response =
                new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_INTERNAL_SERVER_ERROR,
                        "Internal Server Error");
        response.addHeader("Content-type", "Text/html;Charset=UTF-8");
        response.addHeader("Content-encoding", "gzip");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzos = new GZIPOutputStream(baos);
        byte[] uncompressedBytes = "é".getBytes("UTF-8");
        gzos.write(uncompressedBytes, 0, uncompressedBytes.length);
        gzos.close();
        byte[] compressedBytes = baos.toByteArray();
        ByteArrayEntity httpEntity = new ByteArrayEntity(compressedBytes);
        httpEntity.setContentType("Text/html;Charset=UTF-8");
        httpEntity.setContentEncoding("gzip");
        response.setEntity(httpEntity);
        mockConnectionManager.setResponse(response);
        Driver driver = createMockDriver(properties, mockConnectionManager);
        CloseableHttpResponse driverResponse;
        try {
            driverResponse = driver.proxy("/", request.build());
            fail("We should get an HttpErrorPage");
        } catch (HttpErrorPage e) {
            driverResponse = e.getHttpResponse();
        }
        assertEquals("é", HttpResponseUtils.toString(driverResponse));
    }

    private Driver createMockDriver(Properties properties, HttpClientConnectionManager connectionManager) {
        return createMockDriver(properties, connectionManager, "tested");
    }

    private Driver createMockDriver(Properties properties, HttpClientConnectionManager connectionManager, String name) {
        Driver driver =
                Driver.builder()
                        .setName(name)
                        .setProperties(properties)
                        .setRequestExecutorBuilder(
                                HttpClientRequestExecutor.builder().setConnectionManager(connectionManager)).build();
        DriverFactory.put(name, driver);
        return driver;
    }

    public void testRewriteRedirectResponse() throws Exception {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://www.foo.com:8080/");
        properties.put(Parameters.PRESERVE_HOST, "false");
        request = TestUtils.createIncomingRequest("http://www.bar.com/foo/");
        HttpResponse response =
                new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_MOVED_TEMPORARILY, "Found");
        response.addHeader("Location", "http://www.foo.com:8080/somewhere/");
        mockConnectionManager.setResponse(response);
        Driver driver = createMockDriver(properties, mockConnectionManager);
        CloseableHttpResponse driverResponse = driver.proxy("/foo/", request.build());
        assertEquals("http://www.bar.com/somewhere/", driverResponse.getFirstHeader("Location").getValue());
    }

    /**
     * 0000174: Redirect location with default port specified are incorrectly rewritten when preserveHost=true
     * <p>
     * https://sourceforge.net/apps/mantisbt/webassembletool/view.php?id=174
     * 
     * <p>
     * Issue with default ports, which results in invalid url creation.
     * 
     * @throws Exception
     */
    public void testRewriteRedirectResponseWithDefaultPortSpecifiedInLocation() throws Exception {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE, "http://www.foo.com:8080");
        properties.put(Parameters.PRESERVE_HOST, "true");
        HttpResponse response =
                new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_MOVED_TEMPORARILY, "Found");
        // The backend server sets the port even if default (OK it should not
        // but some servers do it)
        response.addHeader("Location", "http://www.foo.com:80/foo/bar");
        mockConnectionManager.setResponse(response);
        Driver driver = createMockDriver(properties, mockConnectionManager);
        request = TestUtils.createIncomingRequest("http://www.foo.com:80/foo");
        // HttpClientHelper will use the Host
        // header to rewrite the request sent to the backend
        // http://www.foo.com/foo
        CloseableHttpResponse driverResponse = driver.proxy("/foo", request.build());
        // The test initially failed with an invalid Location:
        // http://www.foo.com:80:80/foo/bar
        assertEquals("http://www.foo.com:80/foo/bar", driverResponse.getFirstHeader("Location").getValue());
    }

    /**
     * Ensure default ports are not added by esigate.
     * 
     * @throws Exception
     */
    public void testRewriteRedirectResponseWithLocation() throws Exception {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE, "http://127.0.0.1");
        properties.put(Parameters.PRESERVE_HOST, "true");

        mockConnectionManager.setResponseHandler(new IResponseHandler() {

            @Override
            public HttpResponse execute(final HttpRequest httpRequest) throws IOException {
                if (!httpRequest.getLastHeader("Host").getValue().equals("www.foo.com")) {
                    throw new IllegalArgumentException("Host must be www.foo.com");
                }
                return new HttpResponseBuilder().status(HttpStatus.SC_MOVED_TEMPORARILY).entity("Found")
                        .header("Location", "http://www.foo.com").build();
            }
        });
        Driver driver = createMockDriver(properties, mockConnectionManager);

        IncomingRequest request1 = TestUtils.createIncomingRequest("http://www.foo.com:80").build();
        assertEquals("www.foo.com", request1.getLastHeader("Host").getValue());

        CloseableHttpResponse driverResponse = driver.proxy("", request1);
        assertEquals("http://www.foo.com", driverResponse.getFirstHeader("Location").getValue());
    }

    /**
     * <p>
     * Test bug Bug 142 (1st case).
     * <p>
     * 0000142: Error with 304 backend responses
     * <p>
     * NPE in Apache HTTP CLient cache
     * <p>
     * See :https://sourceforge.net/apps/mantisbt/webassembletool/view.php?id=142
     * 
     * @throws Exception
     */
    public void testBug142() throws Exception {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://www.foo.com/");
        properties.put(Parameters.TTL.getName(), "43200");
        properties.put(Parameters.PRESERVE_HOST.getName(), "true");
        properties.put(Parameters.USE_CACHE.getName(), true);

        HttpResponse response =
                new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_NOT_MODIFIED, "Not Modified");
        response.addHeader("Etag", "b5e3f57c0e84fc7a197b849fdfd3d407");
        response.addHeader("Date", "Thu, 13 Dec 2012 07:28:01 GMT");
        mockConnectionManager.setResponse(response);
        Driver driver = createMockDriver(properties, mockConnectionManager);

        // First request
        request = TestUtils.createIncomingRequest("http://www.bar.com/foo/");
        request.addHeader("If-None-Match", "b5e3f57c0e84fc7a197b849fdfd3d407");
        request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml");
        request.addHeader("If-Modified-Since", "Fri, 15 Jun 2012 21:06:25 GMT");
        request.addHeader("Cache-Control", "max-age=0");
        CloseableHttpResponse driverResponse = driver.proxy("/foo/", request.build());
        assertEquals(HttpStatus.SC_NOT_MODIFIED, driverResponse.getStatusLine().getStatusCode());

        // Second request
        request = TestUtils.createIncomingRequest("http://www.bar.com/foo/");
        request.addHeader("If-None-Match", "b5e3f57c0e84fc7a197b849fdfd3d407");
        request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml");
        request.addHeader("If-Modified-Since", "Fri, 15 Jun 2012 21:06:25 GMT");
        request.addHeader("Cache-Control", "max-age=0");

        driverResponse = driver.proxy("/foo/", request.build());
        assertEquals(HttpStatus.SC_NOT_MODIFIED, driverResponse.getStatusLine().getStatusCode());
    }

    /**
     * <p>
     * Test bug Bug 142 (2nd case).
     * <p>
     * 0000142: Error with 304 backend responses
     * <p>
     * NPE in Apache HTTP CLient cache
     * <p>
     * See https://sourceforge.net/apps/mantisbt/webassembletool/view.php?id=142
     * 
     * @throws Exception
     */
    public void testBug142SecondCase() throws Exception {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://www.foo.com/");
        properties.put(Parameters.TTL.getName(), "43200");

        HttpResponse response =
                new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_NOT_MODIFIED, "Not Modified");
        response.addHeader("Etag", "a86ecd6cc6d361776ed05f063921aa34");
        response.addHeader("Date", "Thu, 13 Dec 2012 08:55:37 GMT");
        response.addHeader("Cache-Control", "max-age=2051, public");
        response.addHeader("Expires", "Thu, 13 Dec 2012 09:29:48 GMT");
        response.addHeader("Vary", "Accept-Encoding");

        mockConnectionManager.setResponse(response);
        Driver driver = createMockDriver(properties, mockConnectionManager);

        // First request
        request = TestUtils.createIncomingRequest("http://www.bar142-2.com/foobar142-2/");
        request.addHeader("If-None-Match", "a86ecd6cc6d361776ed05f063921aa34");
        CloseableHttpResponse driverResponse = driver.proxy("/foobar142-2/", request.build());
        assertEquals(HttpStatus.SC_NOT_MODIFIED, driverResponse.getStatusLine().getStatusCode());
    }

    /**
     * <p>
     * Test bug Bug 155
     * <p>
     * 0000155: 304 returned when not using If-XXX headers
     * <p>
     * When requesting a full response, a full response must be sent even if the cache has cached a 304 response.
     * <p>
     * See https://sourceforge.net/apps/mantisbt/webassembletool/view.php?id=155
     * 
     * @throws Exception
     */
    public void testBug155() throws Exception {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://www.foo.com/");
        properties.put(Parameters.TTL.getName(), "43200");
        properties.put(Parameters.PRESERVE_HOST.getName(), "true");
        properties.put(Parameters.USE_CACHE.getName(), true);

        HttpResponse response =
                new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_NOT_MODIFIED, "Not Modified");
        response.addHeader("Etag", "a86ecd6cc6d361776ed05f063921aa34");
        response.addHeader("Date", "Thu, 13 Dec 2012 08:55:37 GMT");
        response.addHeader("Cache-Control", "max-age=2051, public, must-revalidate, proxy-revalidate");
        response.addHeader("Expires", "Thu, 13 Dec 2012 09:29:48 GMT");
        response.addHeader("Set-Cookie",
                "w3tc_referrer=http%3A%2F%2Fblog.richeton.com%2Fcategory%2Fcomputer%2Fwat%2F; path=/");
        mockConnectionManager.setResponse(response);
        Driver driver = createMockDriver(properties, mockConnectionManager);

        // First request
        request = TestUtils.createIncomingRequest("http://www.bar142-2.com/foobar142-2/");
        request.addHeader("If-None-Match", "a86ecd6cc6d361776ed05f063921aa34");
        request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml");
        request.addHeader("Cache-Control", "max-age=0");
        request.addHeader("Accept-Encoding", "gzip, deflate");
        CloseableHttpResponse driverResponse = driver.proxy("/foobar142-2/", request.build());
        assertEquals(HttpStatus.SC_NOT_MODIFIED, driverResponse.getStatusLine().getStatusCode());

        response = new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_OK, "Ok");
        response.addHeader("Etag", "a86ecd6cc6d361776ed05f063921aa34");
        response.addHeader("Date", "Thu, 13 Dec 2012 08:55:37 GMT");
        response.addHeader("Cache-Control", "max-age=2051, public, must-revalidate, proxy-revalidate");
        response.addHeader("Expires", "Thu, 13 Dec 2012 09:29:48 GMT");
        response.addHeader("Set-Cookie",
                "w3tc_referrer=http%3A%2F%2Fblog.richeton.com%2Fcategory%2Fcomputer%2Fwat%2F; path=/");
        response.setEntity(new StringEntity("test"));
        mockConnectionManager.setResponse(response);

        // First request
        request = TestUtils.createIncomingRequest("http://www.bar142-2.com/foobar142-2/");
        request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml");
        request.addHeader("Cache-Control", "max-age=0");
        request.addHeader("Accept-Encoding", "gzip, deflate");
        driverResponse = driver.proxy("/foobar142-2/", request.build());
        assertEquals(HttpStatus.SC_OK, driverResponse.getStatusLine().getStatusCode());
        assertNotNull(driverResponse.getEntity());
    }

    /**
     * 0000162: Cookie forwarding (Browser->Server) does not work with preserveHost
     * <p>
     * This test is to ensure behavior with preserve host off (already working as of bug 162).
     * 
     * @throws Exception
     * @see <a href="http://sourceforge.net/apps/mantisbt/webassembletool/view.php?id=162">0000162</a>
     */
    public void testBug162PreserveHostOff() throws Exception {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost.mydomain.fr/");
        properties.put(Parameters.PRESERVE_HOST.getName(), "false");

        mockConnectionManager = new MockConnectionManager() {
            @Override
            public HttpResponse execute(HttpRequest httpRequest) {
                Assert.assertEquals("localhost.mydomain.fr", httpRequest.getFirstHeader("Host").getValue());
                Assert.assertTrue("Cookie must be forwarded", httpRequest.containsHeader("Cookie"));
                return new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_OK, "OK");
            }
        };

        Driver driver = createMockDriver(properties, mockConnectionManager);

        request =
                TestUtils.createIncomingRequest("http://test.mydomain.fr/foobar/").addCookie(
                        new BasicClientCookie("TEST_cookie", "233445436436346"));

        driver.proxy("/foobar/", request.build());

    }

    /**
     * 0000162: Cookie forwarding (Browser->Server) does not work with preserveHost
     * <p>
     * This test ensure cookies are forwarded with preserveHost. Specific code is Cookie preparation in
     * DefaultCookieManager#rewriteForServer().
     * <p>
     * Warning: HttpClient is not using Host header to validate cookies.
     * 
     * @throws Exception
     * @see <a href="http://sourceforge.net/apps/mantisbt/webassembletool/view.php?id=162">0000162</a>
     */
    public void testBug162PreserveHostOn() throws Exception {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost.mydomain.fr/");
        properties.put(Parameters.PRESERVE_HOST.getName(), "true");

        mockConnectionManager = new MockConnectionManager() {
            @Override
            public HttpResponse execute(HttpRequest httpRequest) {
                Assert.assertEquals("test.mydomain.fr", httpRequest.getFirstHeader("Host").getValue());
                Assert.assertTrue("Cookie must be forwarded", httpRequest.containsHeader("Cookie"));
                return new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_OK, "OK");
            }
        };

        Driver driver = createMockDriver(properties, mockConnectionManager);

        BasicClientCookie cookie = new BasicClientCookie("TEST_cookie", "233445436436346");
        request = TestUtils.createIncomingRequest("http://test.mydomain.fr/foobar/").addCookie(cookie);

        driver.proxy("/foobar/", request.build());

        // same test without forcing the host header
        request = TestUtils.createIncomingRequest("http://test.mydomain.fr/foobar/").addCookie(cookie);

    }

    /**
     * This test ensure Fetch events are fired when cache is disabled.
     * <p>
     * It uses {@link DefaultCharset} extension which processes the Contet-Type header on post-fetch events.
     * 
     * @throws Exception
     */
    public void testBug185() throws Exception {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://www.foo.com/");
        properties.put(Parameters.EXTENSIONS.getName(), DefaultCharset.class.getName());
        properties.put(Parameters.USE_CACHE.getName(), "false");

        HttpResponse response =
                new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_OK, "Not Modified");
        response.addHeader("Content-Type", "text/html");

        mockConnectionManager.setResponse(response);
        Driver driver = createMockDriver(properties, mockConnectionManager);

        // Request
        request = TestUtils.createIncomingRequest("http://www.bar142-2.com/foobar142-2/");
        CloseableHttpResponse driverResponse = driver.proxy("/foobar142-2/", request.build());
        assertEquals(HttpStatus.SC_OK, driverResponse.getStatusLine().getStatusCode());
        assertEquals("text/html; charset=ISO-8859-1", driverResponse.getHeaders("Content-Type")[0].getValue());

        // Same test with cache enabled
        properties.put(Parameters.USE_CACHE.getName(), "true");
        response = new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_OK, "Not Modified");
        response.addHeader("Content-Type", "text/html");

        mockConnectionManager.setResponse(response);
        driver = createMockDriver(properties, mockConnectionManager);

        // Request
        request = TestUtils.createIncomingRequest("http://www.bar142-2.com/foobar142-2/");
        driverResponse = driver.proxy("/foobar142-2/", request.build());
        assertEquals(HttpStatus.SC_OK, driverResponse.getStatusLine().getStatusCode());
        assertEquals("text/html; charset=ISO-8859-1", driverResponse.getHeaders("Content-Type")[0].getValue());
    }

    /**
     * 0000135: Special characters are lost when including a fragment with no charset specified into UTF-8 page.
     * 
     * @throws Exception
     * @see <a href="http://sourceforge.net/apps/mantisbt/webassembletool/view.php?id=135">0000135</a>
     */
    public void testSpecialCharacterInIncludeNoCharset() throws Exception {
        String now = DateUtils.formatDate(new Date());

        // Create master application
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost");
        HttpResponse response = new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_OK, "Ok");
        response.addHeader("Date", now);
        response.addHeader("Content-type", "Text/html;Charset=UTF-8");
        HttpEntity httpEntity = new StringEntity("à<esi:include src=\"$(PROVIDER{provider})/\"/>à", "UTF-8");
        response.setEntity(httpEntity);
        mockConnectionManager.setResponse(response);
        Driver driver = createMockDriver(properties, mockConnectionManager);

        // Create provider application
        properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost");
        mockConnectionManager = new MockConnectionManager();
        response = new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_OK, "Ok");
        response.addHeader("Date", now);
        response.addHeader("Content-type", "text/html");
        httpEntity = new ByteArrayEntity("é".getBytes("ISO-8859-1"), ContentType.create("text/html", (String) null));
        response.setEntity(httpEntity);
        mockConnectionManager.setResponse(response);
        createMockDriver(properties, mockConnectionManager, "provider");

        // Do the include and check the result
        CloseableHttpResponse driverResponse = driver.proxy("/", request.build(), new EsiRenderer());
        assertEquals("àéà", HttpResponseUtils.toString(driverResponse));
    }

    /**
     * 0000161: Cookie domain validation too strict with preserveHost.
     * 
     * @see <a href="https://sourceforge.net/apps/mantisbt/webassembletool/view.php?id=161">0000161</a>
     * 
     * @throws Exception
     */
    public void testBug161SetCookie() throws Exception {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost/");
        properties.put(Parameters.PRESERVE_HOST.getName(), "true");

        BasicHttpResponse response = new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_OK, "Ok");
        response.addHeader("Date", "Thu, 13 Dec 2012 08:55:37 GMT");
        response.addHeader("Set-Cookie", "mycookie=123456; domain=.mydomain.fr; path=/");
        response.setEntity(new StringEntity("test"));
        mockConnectionManager.setResponse(response);

        Driver driver = createMockDriver(properties, mockConnectionManager);

        request = TestUtils.createIncomingRequest("http://test.mydomain.fr/foobar/");

        IncomingRequest incomingRequest = request.build();

        driver.proxy("/foobar/", incomingRequest);

        assertTrue("Set-Cookie must be forwarded.", incomingRequest.getNewCookies().length > 0);
    }

    /**
     * 0000154: Warn on staleWhileRevalidate configuration issue.
     * https://sourceforge.net/apps/mantisbt/webassembletool/view.php?id=154
     */
    public void testConfigStaleWhileRevalidateWith0WorkerThreadsThrowsConfigurationException() {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE, "http://localhost/");
        properties.put(Parameters.STALE_WHILE_REVALIDATE, "600");

        try {
            createMockDriver(properties, mockConnectionManager);
            fail("We should have had a ConfigurationException");
        } catch (ConfigurationException e) {
            // This is exactly what we want
        }
    }

    /**
     * 0000141: Socket read timeout causes a stacktrace and may leak connection
     * https://sourceforge.net/apps/mantisbt/webassembletool/view.php?id=141
     * 
     * The warning will not be fixed in HttpClient but the leak is fixed.
     * 
     * @throws Exception
     */
    public void testSocketReadTimeoutWithCacheAndGzipDoesNotLeak() throws Exception {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE, "http://localhost/");
        properties.put(Parameters.USE_CACHE, "true");

        BasicHttpResponse response = new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_OK, "Ok");
        response.addHeader("Date", DateUtils.formatDate(new Date()));
        response.addHeader("Cache-control", "public, max-age=1000");
        response.addHeader("Content-Encoding", "gzip");
        response.setEntity(new InputStreamEntity(new InputStream() {
            @Override
            public int read() throws IOException {
                throw new SocketTimeoutException("Read timed out");
            }
        }, 1000));
        mockConnectionManager.setResponse(response);

        Driver driver = createMockDriver(properties, mockConnectionManager);

        request = TestUtils.createIncomingRequest("http://test.mydomain.fr/");
        request.addHeader("Accept-Encoding", "gzip, deflate");

        try {
            driver.proxy("/", request.build());
            fail("We should have had a SocketTimeoutException");
        } catch (HttpErrorPage e) {
            // That is what we expect
            assertEquals(HttpStatus.SC_GATEWAY_TIMEOUT, e.getHttpResponse().getStatusLine().getStatusCode());
        }
        assertFalse("All the connections should have been closed", mockConnectionManager.isOpen());
    }

    public void testForwardCookiesWithPortsAndPreserveHost() throws Exception {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost:8080/");
        properties.put(Parameters.PRESERVE_HOST.getName(), "true");

        mockConnectionManager = new MockConnectionManager() {
            @Override
            public HttpResponse execute(HttpRequest httpRequest) {
                Assert.assertNotNull("Cookie should be forwarded", httpRequest.getFirstHeader("Cookie"));
                Assert.assertEquals("JSESSIONID=926E1C6A52804A625DFB0139962D4E13", httpRequest.getFirstHeader("Cookie")
                        .getValue());
                return new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_OK, "OK");
            }
        };

        Driver driver = createMockDriver(properties, mockConnectionManager);

        BasicClientCookie cookie = new BasicClientCookie("_JSESSIONID", "926E1C6A52804A625DFB0139962D4E13");
        request = TestUtils.createIncomingRequest("http://127.0.0.1:8081/foobar.jsp").addCookie(cookie);

        driver.proxy("/foobar.jsp", request.build());
    }

    public void testForwardCookiesWithPorts() throws Exception {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost:8080/");
        properties.put(Parameters.PRESERVE_HOST.getName(), "false");

        mockConnectionManager = new MockConnectionManager() {
            @Override
            public HttpResponse execute(HttpRequest httpRequest) {
                Assert.assertNotNull(httpRequest.getFirstHeader("Cookie"));
                Assert.assertEquals("JSESSIONID=926E1C6A52804A625DFB0139962D4E13", httpRequest.getFirstHeader("Cookie")
                        .getValue());
                return new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_OK, "OK");
            }
        };

        Driver driver = createMockDriver(properties, mockConnectionManager);

        BasicClientCookie cookie = new BasicClientCookie("_JSESSIONID", "926E1C6A52804A625DFB0139962D4E13");
        request = TestUtils.createIncomingRequest("http://127.0.0.1:8081/foobar.jsp").addCookie(cookie);

        driver.proxy("/foobar.jsp", request.build());
    }

    /**
     * Cookies from external URLs calls should also be forwarded.
     * 
     * @see <a href="https://sourceforge.net/apps/mantisbt/webassembletool/view.php?id=255">0000255: forwardCookies does
     *      not work with esi:include if not using a provider variable</a>
     * @throws Exception
     */
    public void testForwardCookiesExternalUrl() throws Exception {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE, "http://localhost:8080/");
        properties.put(Parameters.PRESERVE_HOST, "true");

        mockConnectionManager = new MockConnectionManager() {
            @Override
            public HttpResponse execute(HttpRequest httpRequest) {
                BasicHttpResponse response =
                        new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_OK, "OK");
                response.addHeader(new BasicHeader("Set-Cookie", "name1=value1;domain=www.external.server"));
                return response;
            }
        };

        Driver driver = createMockDriver(properties, mockConnectionManager);

        request = TestUtils.createIncomingRequest("http://localhost:8080/foo/foobar.jsp");

        IncomingRequest incomingRequest = request.build();
        driver.proxy("http://wwww.external.server/foo/foobar.jsp", incomingRequest);

        Assert.assertEquals(1, incomingRequest.getNewCookies().length);
    }

    public void testRewriteCookiePath() throws Exception {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost:8080/");
        properties.put(Parameters.PRESERVE_HOST.getName(), "true");

        mockConnectionManager = new MockConnectionManager() {
            @Override
            public HttpResponse execute(HttpRequest httpRequest) {
                BasicHttpResponse response =
                        new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_OK, "OK");
                response.addHeader(new BasicHeader("Set-Cookie", "name1=value1;Path=/foo"));
                return response;
            }
        };

        Driver driver = createMockDriver(properties, mockConnectionManager);

        request = TestUtils.createIncomingRequest("http://localhost:8081/foo/foobar.jsp");

        IncomingRequest incomingRequest = request.build();
        driver.proxy("/foo/foobar.jsp", incomingRequest);

        Assert.assertEquals(1, incomingRequest.getNewCookies().length);
        Assert.assertEquals("/foo", incomingRequest.getNewCookies()[0].getPath());
    }

    public void testRewriteCookiePathNotMatching() throws Exception {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost:8080/");
        properties.put(Parameters.PRESERVE_HOST.getName(), "true");

        mockConnectionManager = new MockConnectionManager() {
            @Override
            public HttpResponse execute(HttpRequest httpRequest) {
                BasicHttpResponse response =
                        new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_OK, "OK");
                response.addHeader(new BasicHeader("Set-Cookie", "name1=value1;Path=/bar"));
                return response;
            }
        };

        Driver driver = createMockDriver(properties, mockConnectionManager);

        request = TestUtils.createIncomingRequest("http://localhost:8081/foo/foobar.jsp");

        IncomingRequest incomingRequest = request.build();

        driver.proxy("/bar/foobar.jsp", incomingRequest);

        Assert.assertEquals(1, incomingRequest.getNewCookies().length);
        Assert.assertEquals("/", incomingRequest.getNewCookies()[0].getPath());
    }

    /**
     * 0000231: ESIgate should be enable to mashup elements for an error/404 page.
     * 
     * @see "http://sourceforge.net/apps/mantisbt/webassembletool/view.php?id=231"
     * 
     * @throws Exception
     */
    public void testBug231RenderingOnProxyError() throws Exception {
        // Configuration
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost.mydomain.fr/");
        properties.put(Parameters.PRESERVE_HOST.getName(), "true");

        // Setup server responses.
        mockConnectionManager = new MockConnectionManager() {
            @Override
            public HttpResponse execute(HttpRequest httpRequest) {
                // The main page
                if (httpRequest.getRequestLine().getUri().equals("/foobar/")) {
                    return new HttpResponseBuilder()
                            .entity(new StringEntity("<esi:include src=\"http://test.mydomain.fr/esi/\"/>",
                                    ContentType.TEXT_HTML)).status(HttpStatus.SC_BAD_REQUEST).build();
                }

                // The ESI fragment
                if (httpRequest.getRequestLine().getUri().equals("/esi/")) {
                    try {
                        return new HttpResponseBuilder().entity("OK").build();
                    } catch (UnsupportedEncodingException e) {
                        Assert.fail("Unexpected exception" + e.getMessage());
                    }
                }

                // Other unexpected request ? -> Fail
                Assert.fail("Unexpected request " + httpRequest.getRequestLine().getUri());
                return null;
            }
        };

        // Build driver and request.
        Driver driver = createMockDriver(properties, mockConnectionManager);
        request = IncomingRequest.builder("http://test.mydomain.fr/foobar/");

        try {
            // Perform call with ESI rendering.
            driver.proxy("/foobar/", request.build(), new EsiRenderer());
            fail("HttpErrorPage expected");
        } catch (HttpErrorPage errorPage) {
            // Ensure request was esi-processed.
            HttpResponse response = errorPage.getHttpResponse();
            Assert.assertEquals("OK", EntityUtils.toString(response.getEntity()));
        }

    }

}

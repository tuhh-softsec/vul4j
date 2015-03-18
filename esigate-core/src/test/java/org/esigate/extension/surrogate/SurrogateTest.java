/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.esigate.extension.surrogate;

import java.util.Properties;

import junit.framework.TestCase;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.esigate.Driver;
import org.esigate.HttpErrorPage;
import org.esigate.Parameters;
import org.esigate.extension.Esi;
import org.esigate.http.IncomingRequest;
import org.esigate.test.TestUtils;
import org.esigate.test.conn.IResponseHandler;
import org.esigate.test.conn.SequenceResponse;
import org.junit.Assert;

/**
 * Edge-Arch test cases.
 * 
 * @author Nicolas Richeton
 * 
 */
public class SurrogateTest extends TestCase {

    /**
     * 4.2.4 content
     * <p>
     * Once processing takes place, the capability token that invoked it (as well as the 'content' directive, if
     * appropriate) is consumed; that is, it is not passed forward to surrogates.
     * 
     * @throws Exception
     *             test error.
     */
    public void testSurrogateControlWithSurrogate() throws Exception {

        // Conf
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://provider/");
        properties.put(Parameters.EXTENSIONS, Esi.class.getName() + "," + Surrogate.class.getName());

        // Setup remote server (provider) response.
        Driver driver =
                TestUtils.createMockDriver(
                        properties,
                        new SequenceResponse().response(
                                TestUtils.createHttpResponse().status(HttpStatus.SC_OK).reason("OK")
                                        .header("Surrogate-Control", "content=\"ESI/1.0 ESI-Inline/1.0\", no-store")
                                        .header("Content-Type", "text/html; charset=utf-8").build()).response(
                                TestUtils
                                        .createHttpResponse()
                                        .status(HttpStatus.SC_OK)
                                        .reason("OK")
                                        .header("Surrogate-Control",
                                                "content=\"ESI/1.0 ESI-Inline/1.0 ORAESI/9.0.2\", no-store")
                                        .header("Content-Type", "text/html; charset=utf-8").build()));

        // Request
        IncomingRequest requestWithSurrogate =
                TestUtils.createRequest("http://test.mydomain.fr/foobar/")
                        .addHeader("Surrogate-Capabilities", "ab=\"Surrogate/1.0\"").build();

        // content="" is completely removed
        HttpResponse response = TestUtils.driverProxy(driver, requestWithSurrogate);
        Assert.assertEquals("no-store", response.getFirstHeader("Surrogate-Control").getValue());

        // Capabilities are removed.
        response = TestUtils.driverProxy(driver, requestWithSurrogate);
        Assert.assertEquals("content=\"ORAESI/9.0.2\", no-store", response.getFirstHeader("Surrogate-Control")
                .getValue());

    }

    /**
     * 2.2 Surrogate-Control Header
     * <p>
     * If no downstream surrogates have identified themselves, the header should be stripped from responses.
     * 
     * 
     * @throws Exception
     *             test error.
     */
    public void testSurrogateControlWithNoSurrogate() throws Exception {

        // Conf
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://provider/");
        properties.put(Parameters.EXTENSIONS, Esi.class.getName() + "," + Surrogate.class.getName());

        // Setup remote server (provider) response.
        Driver driver =
                TestUtils.createMockDriver(properties, new SequenceResponse().response(TestUtils.createHttpResponse()
                        .status(HttpStatus.SC_OK).reason("OK")
                        .header("Surrogate-Control", "content=\"ESI/1.0 ESI-Inline/1.0\", max-age=600").build()));

        // Request
        IncomingRequest requestWithSurrogate = TestUtils.createRequest("http://test.mydomain.fr/foobar/").build();

        // Proxy
        HttpResponse response = TestUtils.driverProxy(driver, requestWithSurrogate);
        Assert.assertFalse(response.containsHeader("Surrogate-Control"));

    }

    /**
     * Ensure Surrogate-Capabilities header is added to fetch requests.
     * 
     * @throws Exception
     *             test error.
     */
    public void testSurrogateCapabilities() throws Exception {

        // Conf
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://provider/");
        properties.put(Parameters.EXTENSIONS, Esi.class.getName() + "," + Surrogate.class.getName());

        // Setup remote server (provider) response.
        Driver driver = TestUtils.createMockDriver(properties, new IResponseHandler() {
            @Override
            public HttpResponse execute(HttpRequest request) {
                Assert.assertNotNull(request.getFirstHeader("Surrogate-Capabilities"));
                Assert.assertEquals(
                        "esigate=\"Surrogate/1.0 ESI/1.0 ESI-Inline/1.0 X-ESI-Fragment/1.0 X-ESI-Replace/1.0 "
                                + "X-ESI-XSLT/1.0 ESIGATE/4.0\"", request.getFirstHeader("Surrogate-Capabilities")
                                .getValue());
                return TestUtils.createHttpResponse().status(HttpStatus.SC_OK).reason("OK").build();
            }
        });

        IncomingRequest requestWithSurrogate = TestUtils.createRequest("http://test.mydomain.fr/foobar/").build();
        TestUtils.driverProxy(driver, requestWithSurrogate);

    }

    /**
     * 2.1 Surrogate-Capability Header
     * <p>
     * The name in each capability set identifies a device token, which uniquely identifies the surrogate that appended
     * it. Device tokens must be unique within a request's Surrogate-Capabilities header.
     * 
     * @throws Exception
     *             test error.
     */
    public void testSurrogateCapabilitiesUniqueToken() throws Exception {

        // Conf
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://provider/");
        properties.put(Parameters.EXTENSIONS, Esi.class.getName() + "," + Surrogate.class.getName());

        // Setup remote server (provider) response.
        Driver driver = TestUtils.createMockDriver(properties, new IResponseHandler() {
            @Override
            public HttpResponse execute(HttpRequest request) {
                Assert.assertEquals(
                        "esigate=\"Surrogate/1.0\", esigate2=\"Surrogate/1.0 ESI/1.0 ESI-Inline/1.0 X-ESI-Fragment/1.0 "
                                + "X-ESI-Replace/1.0 X-ESI-XSLT/1.0 ESIGATE/4.0\"",
                        request.getFirstHeader("Surrogate-Capabilities").getValue());
                return TestUtils.createHttpResponse().status(HttpStatus.SC_OK).reason("OK").build();
            }
        });

        IncomingRequest requestWithSurrogate =
                TestUtils.createRequest("http://test.mydomain.fr/foobar/")
                        .addHeader("Surrogate-Capabilities", "esigate=\"Surrogate/1.0\"").build();
        TestUtils.driverProxy(driver, requestWithSurrogate);

    }

    /**
     * 4.2.4 content
     * <p/>
     * The content directive identifies what processing surrogates should perform on the response before forwarding it.
     * The value of the content directive is a left-to-right ordered, space-separated list of capabilities for
     * processing by surrogates.
     * <p/>
     * Empty control directive => no processing.
     * 
     * @throws Exception
     *             test error.
     */
    public void testSurrogateControlDisableCapability() throws Exception {

        // Conf
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://provider/");
        properties.put(Parameters.EXTENSIONS, Esi.class.getName() + "," + Surrogate.class.getName());

        // Setup remote server (provider) response.
        Driver driver =
                TestUtils.createMockDriver(
                        properties,
                        new SequenceResponse().response(TestUtils.createHttpResponse().status(HttpStatus.SC_OK)
                                .reason("OK").entity("before <esi:vars>$(HTTP_HOST)</esi:vars> after")
                                .header("Surrogate-Control", "content=\"\"")
                                .header("Content-Type", "text/html; charset=utf-8").build()));

        IncomingRequest requestWithSurrogate = TestUtils.createRequest("http://test.mydomain.fr/foobar/").build();
        HttpResponse response = TestUtils.driverProxy(driver, requestWithSurrogate);
        Assert.assertEquals("before <esi:vars>$(HTTP_HOST)</esi:vars> after",
                EntityUtils.toString(response.getEntity()));
    }

    /**
     * 4.2.4 content
     * <p/>
     * The content directive identifies what processing surrogates should perform on the response before forwarding it.
     * The value of the content directive is a left-to-right ordered, space-separated list of capabilities for
     * processing by surrogates.
     * 
     * @throws Exception
     *             test error.
     */
    public void testSurrogateControlEnable() throws Exception {
        // Conf
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://provider/");
        properties.put(Parameters.EXTENSIONS, Esi.class.getName() + "," + Surrogate.class.getName());

        // Setup remote server (provider) response.
        Driver driver =
                TestUtils.createMockDriver(
                        properties,
                        new SequenceResponse().response(TestUtils.createHttpResponse().status(HttpStatus.SC_OK)
                                .reason("OK").entity("before <esi:vars>$(HTTP_HOST)</esi:vars> after")
                                .header("Surrogate-Control", "content=\"ESI/1.0\"")
                                .header("Content-Type", "text/html; charset=utf-8").build()));

        IncomingRequest requestWithSurrogate = TestUtils.createRequest("http://test.mydomain.fr/foobar/").build();
        HttpResponse response = TestUtils.driverProxy(driver, requestWithSurrogate);
        Assert.assertEquals("before test.mydomain.fr after", EntityUtils.toString(response.getEntity()));
    }

    /**
     * 4.2.1 no-store
     * <p/>
     * The no-store directive specifies that the response entity should not be stored in cache; it is only to be used
     * for the original request, and may not be validated on the origin server.
     * 
     * @throws Exception
     *             test error.
     */
    public void testSurrogateControlNoStore() throws Exception {
        // Conf
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://provider/");
        properties.put(Parameters.EXTENSIONS, Esi.class.getName() + "," + Surrogate.class.getName());
        properties.put(Parameters.X_CACHE_HEADER, "true");

        // Setup remote server (provider) response.
        Driver driver =
                TestUtils.createMockDriver(
                        properties,
                        new SequenceResponse().response(
                                TestUtils.createHttpResponse().status(HttpStatus.SC_OK).reason("OK").entity("1")
                                        .header("Surrogate-Control", "no-store")
                                        .header("Cache-Control", "public, max-age=60")
                                        .header("Content-Type", "text/html; charset=utf-8").build()).response(
                                TestUtils.createHttpResponse().status(HttpStatus.SC_OK).reason("OK").entity("2")
                                        .header("Surrogate-Control", "").header("Cache-Control", "public, max-age=60")
                                        .header("Content-Type", "text/html; charset=utf-8").build()));

        IncomingRequest requestWithSurrogate = TestUtils.createRequest("http://test.mydomain.fr/foobar/").build();
        HttpResponse response = TestUtils.driverProxy(driver, requestWithSurrogate);
        assertEquals("1", EntityUtils.toString(response.getEntity()));
        assertTrue(response.getFirstHeader("X-Cache").getValue().startsWith("MISS"));

        // Caching has been disabled by Cache-Control header
        requestWithSurrogate = TestUtils.createRequest("http://test.mydomain.fr/foobar/").build();
        response = TestUtils.driverProxy(driver, requestWithSurrogate);
        assertEquals("2", EntityUtils.toString(response.getEntity()));
        assertTrue(response.getFirstHeader("X-Cache").getValue().startsWith("MISS"));

        // Caching was not altered by Surrogate-Control
        requestWithSurrogate = TestUtils.createRequest("http://test.mydomain.fr/foobar/").build();
        response = TestUtils.driverProxy(driver, requestWithSurrogate);
        assertEquals("2", EntityUtils.toString(response.getEntity()));
        assertTrue(response.getFirstHeader("X-Cache").getValue().startsWith("HIT"));

    }

    /**
     * 4.2.3 max-age
     * <p/>
     * The max-age directive specifies how long the response entity can be considered fresh, in seconds. After this
     * time, implementations must consider the cached entity stale.
     * 
     * 
     * @throws Exception
     *             test error.
     */
    public void testSurrogateControlMaxAge() throws Exception {
        // Conf
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://provider/");
        properties.put(Parameters.EXTENSIONS, Esi.class.getName() + "," + Surrogate.class.getName());
        properties.put(Parameters.X_CACHE_HEADER, "true");

        // Setup remote server (provider) response.
        Driver driver =
                TestUtils.createMockDriver(
                        properties,
                        new SequenceResponse().response(
                                TestUtils.createHttpResponse().status(HttpStatus.SC_OK).reason("OK").entity("1")
                                        .header("Cache-Control", "no-store")
                                        .header("Content-Type", "text/html; charset=utf-8").build()).response(
                                TestUtils.createHttpResponse().status(HttpStatus.SC_OK).reason("OK").entity("2")
                                        .header("Surrogate-Control", "max-age=60").header("Cache-Control", "no-store")
                                        .header("Content-Type", "text/html; charset=utf-8").build()));

        IncomingRequest requestWithSurrogate = TestUtils.createRequest("http://test.mydomain.fr/foobar/").build();
        HttpResponse response = TestUtils.driverProxy(driver, requestWithSurrogate);
        assertEquals("1", EntityUtils.toString(response.getEntity()));
        assertTrue(response.getFirstHeader("X-Cache").getValue().startsWith("MISS"));

        requestWithSurrogate = TestUtils.createRequest("http://test.mydomain.fr/foobar/").build();
        response = TestUtils.driverProxy(driver, requestWithSurrogate);
        assertEquals("2", EntityUtils.toString(response.getEntity()));
        assertTrue(response.getFirstHeader("X-Cache").getValue().startsWith("MISS"));

        // Caching was enabled with Surrogate-Control
        requestWithSurrogate = TestUtils.createRequest("http://test.mydomain.fr/foobar/").build();
        response = TestUtils.driverProxy(driver, requestWithSurrogate);
        assertEquals("2", EntityUtils.toString(response.getEntity()));
        assertTrue(response.getFirstHeader("X-Cache").getValue().startsWith("HIT"));

    }

    /**
     * 4.2.3 max-age
     * <p/>
     * Optionally, a '+' and a freshness extension can be appended, that specifies an additional period of time (in
     * seconds) the stale entity may be served, before it must be revalidated or refetched as appropriate.
     * 
     * @throws Exception
     *             test error.
     */
    public void testSurrogateControlMaxAgeExtended() throws Exception {
        // Conf
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://provider/");
        properties.put(Parameters.EXTENSIONS, Esi.class.getName() + "," + Surrogate.class.getName());
        properties.put(Parameters.X_CACHE_HEADER, "true");

        // Setup remote server (provider) response.
        Driver driver =
                TestUtils
                        .createMockDriver(
                                properties,
                                new SequenceResponse()
                                        .response(
                                                TestUtils.createHttpResponse().status(HttpStatus.SC_OK).reason("OK")
                                                        .header("Cache-Control", "no-store")
                                                        .header("Content-Type", "text/html; charset=utf-8").entity("1")
                                                        .build())
                                        .response(
                                                TestUtils.createHttpResponse().status(HttpStatus.SC_OK).reason("OK")
                                                        .header("Surrogate-Control", "max-age=1")
                                                        .header("Cache-Control", "no-store").header("Etag", "1")
                                                        .header("Content-Type", "text/html; charset=utf-8").entity("2")
                                                        .build())
                                        .response(
                                                TestUtils.createHttpResponse()
                                                        .status(HttpStatus.SC_INTERNAL_SERVER_ERROR).reason("Failed")
                                                        .header("Cache-Control", "no-store")
                                                        .header("Content-Type", "text/html; charset=utf-8").entity("3")
                                                        .build())
                                        .response(
                                                TestUtils.createHttpResponse().status(HttpStatus.SC_OK).reason("OK")
                                                        .header("Surrogate-Control", "max-age=1+60")
                                                        .header("Cache-Control", "no-store").header("Etag", "1")
                                                        .header("Content-Type", "text/html; charset=utf-8").entity("4")
                                                        .build())
                                        .response(
                                                TestUtils.createHttpResponse()
                                                        .status(HttpStatus.SC_INTERNAL_SERVER_ERROR).reason("Failed")
                                                        .header("Cache-Control", "no-store")
                                                        .header("Content-Type", "text/html; charset=utf-8").entity("5")
                                                        .build())

                        );

        IncomingRequest requestWithSurrogate = TestUtils.createRequest("http://test.mydomain.fr/foobar/").build();
        HttpResponse response = TestUtils.driverProxy(driver, requestWithSurrogate);
        assertEquals("1", EntityUtils.toString(response.getEntity()));
        assertTrue(response.getFirstHeader("X-Cache").getValue().startsWith("MISS"));

        // Previous response was not cacheable.
        requestWithSurrogate = TestUtils.createRequest("http://test.mydomain.fr/foobar/").build();
        response = TestUtils.driverProxy(driver, requestWithSurrogate);
        assertEquals("2", EntityUtils.toString(response.getEntity()));
        assertTrue(response.getFirstHeader("X-Cache").getValue().startsWith("MISS"));

        // Caching was enabled with Surrogate-Control
        requestWithSurrogate = TestUtils.createRequest("http://test.mydomain.fr/foobar/").build();
        response = TestUtils.driverProxy(driver, requestWithSurrogate);
        assertEquals("2", EntityUtils.toString(response.getEntity()));
        assertTrue(response.getFirstHeader("X-Cache").getValue().startsWith("HIT"));

        // Wait for the cache to expire
        Thread.sleep(1500);

        // Cache expired, this request fails.
        requestWithSurrogate = TestUtils.createRequest("http://test.mydomain.fr/foobar/").build();
        try {
            TestUtils.driverProxy(driver, requestWithSurrogate);
            fail("Should return 500");
        } catch (HttpErrorPage e) {
            assertEquals("3", EntityUtils.toString(e.getHttpResponse().getEntity()));
            assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getHttpResponse().getStatusLine().getStatusCode());
            assertTrue(e.getHttpResponse().getFirstHeader("X-Cache").getValue().startsWith("MISS"));
        }

        // New request
        requestWithSurrogate = TestUtils.createRequest("http://test.mydomain.fr/foobar/").build();
        response = TestUtils.driverProxy(driver, requestWithSurrogate);
        assertEquals("4", EntityUtils.toString(response.getEntity()));
        assertTrue(response.getFirstHeader("X-Cache").getValue().startsWith("VALIDATED"));

        // Caching was enabled with Surrogate-Control
        requestWithSurrogate = TestUtils.createRequest("http://test.mydomain.fr/foobar/").build();
        response = TestUtils.driverProxy(driver, requestWithSurrogate);
        assertEquals("4", EntityUtils.toString(response.getEntity()));
        assertTrue(response.getFirstHeader("X-Cache").getValue().startsWith("HIT"));

        // Wait for the cache to expire
        Thread.sleep(1500);

        // Extended cache was enabled. This request fails but extended cache
        // allows to return the stale content
        requestWithSurrogate = TestUtils.createRequest("http://test.mydomain.fr/foobar/").build();
        response = TestUtils.driverProxy(driver, requestWithSurrogate);
        assertEquals("4", EntityUtils.toString(response.getEntity()));
        assertTrue(response.getFirstHeader("X-Cache").getValue().startsWith("MISS"));
    }

    /**
     * 2.3 Surrogate-Control Targetting
     * 
     * Because surrogates can be deployed hetrogeneously in a hierarchy, it is necessary to enable the targetting of
     * directives at individual devices.
     * 
     * Surrogate-Control directives may have a parameter that identifies the surrogate that they are targetted at, as
     * identified by the device token in the request's Surrogate-Capabilities header. Directives without targetting
     * parameters are applied to all surrogates, unless a targetted directive overrides it.
     * 
     * @throws Exception
     *             test error.
     */

    public void testSurrogateControlTargeting() throws Exception {

        // Conf
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://provider/");
        properties.put(Parameters.EXTENSIONS, Esi.class.getName() + "," + Surrogate.class.getName());

        // Setup remote server (provider) response.
        Driver driver =
                TestUtils.createMockDriver(
                        properties,
                        new SequenceResponse().response(TestUtils.createHttpResponse().status(HttpStatus.SC_OK)
                                .reason("OK")
                                .header("Surrogate-Control", "content=\"ESI/1.0 ESI-Inline/1.0\";ab, no-store")
                                .header("Content-Type", "text/html; charset=utf-8").build()));

        // Request
        IncomingRequest requestWithSurrogate =
                TestUtils.createRequest("http://test.mydomain.fr/foobar/")
                        .addHeader("Surrogate-Capabilities", "ab=\"Surrogate/1.0 ESI/1.0 ESI-Inline/1.0\"").build();

        // content="" is kept since it is not targeted for esigate.
        HttpResponse response = TestUtils.driverProxy(driver, requestWithSurrogate);
        Assert.assertEquals("content=\"ESI/1.0 ESI-Inline/1.0\";ab, no-store",
                response.getFirstHeader("Surrogate-Control").getValue());

    }

    /**
     * Handle case when previous surrogate is an esigate instance.
     * 
     * @throws Exception
     *             test error.
     */
    public void testSurrogateControlTargeting2Esigate() throws Exception {

        // Conf
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://provider/");
        properties.put(Parameters.EXTENSIONS, Esi.class.getName() + "," + Surrogate.class.getName());

        // Setup remote server (provider) response.
        Driver driver =
                TestUtils.createMockDriver(
                        properties,
                        new SequenceResponse().response(TestUtils.createHttpResponse().status(HttpStatus.SC_OK)
                                .reason("OK")
                                .header("Surrogate-Control", "content=\"ESI/1.0 ESI-Inline/1.0\";esigate, no-store")
                                .header("Content-Type", "text/html; charset=utf-8").build()));

        // Request
        IncomingRequest requestWithSurrogate =
                TestUtils.createRequest("http://test.mydomain.fr/foobar/")
                        .addHeader("Surrogate-Capabilities", "esigate=\"Surrogate/1.0 ESI/1.0 ESI-Inline/1.0\"")
                        .build();

        // content="" is kept since it is not targeted for this esigate instance.
        HttpResponse response = TestUtils.driverProxy(driver, requestWithSurrogate);
        Assert.assertEquals("content=\"ESI/1.0 ESI-Inline/1.0\";esigate, no-store",
                response.getFirstHeader("Surrogate-Control").getValue());

    }

    /**
     * @throws Exception
     */
    public void testEsigateChaining() throws Exception {

        // Conf
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://provider/");
        properties.put(Parameters.EXTENSIONS, Esi.class.getName() + "," + Surrogate.class.getName());

        // Setup remote server (provider) response.
        Driver driver =
                TestUtils.createMockDriver(
                        properties,
                        new SequenceResponse().response(TestUtils.createHttpResponse().status(HttpStatus.SC_OK)
                                .reason("OK").header("Content-Type", "text/html; charset=utf-8")
                                .entity("before <esi:vars>$(HTTP_HOST)</esi:vars> after").build()));

        // Request
        IncomingRequest requestWithSurrogate =
                TestUtils
                        .createRequest("http://test.mydomain.fr/foobar/")
                        .addHeader(
                                "Surrogate-Capabilities",
                                "esigate=\"Surrogate/1.0 ESI/1.0 ESI-Inline/1.0 X-ESI-Fragment/1.0 "
                                        + "X-ESI-Replace/1.0 X-ESI-XSLT/1.0 ESIGATE/4.0\""
                                        + ", esigate2=\"Surrogate/1.0 ESIGATE/4.0\"").build();

        // content="" is added and targeted to the first esigate
        HttpResponse response = TestUtils.driverProxy(driver, requestWithSurrogate);
        Assert.assertEquals("content=\"ESI/1.0 ESI-Inline/1.0 X-ESI-Fragment/1.0 "
                + "X-ESI-Replace/1.0 X-ESI-XSLT/1.0 ESIGATE/4.0\";esigate", response
                .getFirstHeader("Surrogate-Control").getValue());

        // Directives are not processed, since they are targetted to the next esigate.
        Assert.assertEquals("before <esi:vars>$(HTTP_HOST)</esi:vars> after",
                EntityUtils.toString(response.getEntity()));

    }

    /**
     * Ensure cached content is not reused if the surrogate topology has changed. Reusing caching content would lead to
     * invalid targetting and/or invalid requested capabilities.
     * 
     * @throws Exception
     *             test error.
     */
    public void testSurrogateControlVarySurrogate() throws Exception {
        // Conf
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://provider/");
        properties.put(Parameters.EXTENSIONS, Esi.class.getName() + "," + Surrogate.class.getName());
        properties.put(Parameters.X_CACHE_HEADER, "true");

        // Setup remote server (provider) response.
        Driver driver =
                TestUtils.createMockDriver(
                        properties,
                        new SequenceResponse().response(
                                TestUtils.createHttpResponse().status(HttpStatus.SC_OK).reason("OK").entity("1")
                                        .header("Surrogate-Control", "content=\"ESI/1.0\";a")
                                        .header("Cache-Control", "public, max-age=60")
                                        .header("Content-Type", "text/html; charset=utf-8").build()).response(
                                TestUtils.createHttpResponse().status(HttpStatus.SC_OK).reason("OK").entity("2")
                                        .header("Surrogate-Control", "content=\"ESI/1.0\";esigate")
                                        .header("Cache-Control", "public, max-age=60")
                                        .header("Content-Type", "text/html; charset=utf-8").header("Vary", "Cookie")
                                        .build()));

        IncomingRequest requestWithSurrogate =
                TestUtils.createRequest("http://test.mydomain.fr/foobar/")
                        .addHeader("Surrogate-Capabilities", "a=\"Surrogate/1.0 ORAESI/4.0 ESI/1.0\"").build();
        HttpResponse response = TestUtils.driverProxy(driver, requestWithSurrogate);
        assertEquals("1", EntityUtils.toString(response.getEntity()));
        assertTrue(response.getFirstHeader("X-Cache").getValue().startsWith("MISS"));
        assertNull(response.getFirstHeader("Vary"));

        requestWithSurrogate =
                TestUtils.createRequest("http://test.mydomain.fr/foobar/")
                        .addHeader("Surrogate-Capabilities", "a=\"Surrogate/1.0 ORAESI/4.0 ESI/1.0\"").build();
        response = TestUtils.driverProxy(driver, requestWithSurrogate);
        assertEquals("1", EntityUtils.toString(response.getEntity()));
        assertTrue(response.getFirstHeader("X-Cache").getValue().startsWith("HIT"));

        requestWithSurrogate =
                TestUtils.createRequest("http://test.mydomain.fr/foobar/")
                        .addHeader("Surrogate-Capabilities", "esigate=\"Surrogate/1.0 ORAESI/4.0 ESI/1.0\"").build();
        response = TestUtils.driverProxy(driver, requestWithSurrogate);
        assertEquals("content=\"ESI/1.0\";esigate", response.getFirstHeader("Surrogate-Control").getValue());
        assertEquals("2", EntityUtils.toString(response.getEntity()));
        assertTrue(response.getFirstHeader("X-Cache").getValue().startsWith("MISS"));

    }
}

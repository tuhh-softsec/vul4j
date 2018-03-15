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
package org.esigate.extension.parallelesi;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.esigate.Driver;
import org.esigate.HttpErrorPage;
import org.esigate.Parameters;
import org.esigate.http.IncomingRequest;
import org.esigate.test.TestUtils;
import org.esigate.test.conn.IResponseHandler;
import org.junit.Assert;

import junit.framework.TestCase;

/**
 * Parallel ESI test cases.
 * 
 * @author Nicolas Richeton
 * 
 */
public class EsiTest extends TestCase {

    /**
     * Ensure Esi requests can be queued when no thread is available.
     * 
     * @throws Exception
     */
    public void testQueueBehaviorOK() throws Exception {

        // Conf
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://provider/");
        properties.put(Parameters.EXTENSIONS, Esi.class.getName());
        properties.put(Esi.MAX_THREADS, "1");
        properties.put(Esi.MAX_QUEUE, "10");

        // Setup remote server (provider) response.

        Driver driver = TestUtils.createMockDriver(properties, new IResponseHandler() {
            @Override
            public HttpResponse execute(HttpRequest request) throws UnsupportedEncodingException {

                if (request.getRequestLine().getUri().equals("/foobar/")) {
                    return TestUtils
                            .createHttpResponse()
                            .status(HttpStatus.SC_OK)
                            .reason("OK")
                            .header("Content-Type", "text/html; charset=utf-8")            
                            .entity("<esi:include src=\"http://test.mydomain.fr/esi/1\"/> "
                                    + "<esi:include src=\"http://test.mydomain.fr/esi/2\"/>").build();
                }

                if (request.getRequestLine().getUri().equals("/esi/1")) {
                    return TestUtils.createHttpResponse().status(HttpStatus.SC_OK).reason("OK")
                            .header("Content-Type", "text/html; charset=utf-8").entity("Fragment 1").build();
                }

                if (request.getRequestLine().getUri().equals("/esi/2")) {
                    return TestUtils.createHttpResponse().status(HttpStatus.SC_OK).reason("OK")
                            .header("Content-Type", "text/html; charset=utf-8").entity("Fragment 2").build();
                }

                throw new IllegalStateException("Unexpected request" + request.getRequestLine().getUri());
            }
        });

        // Request
        IncomingRequest requestWithSurrogate = TestUtils.createRequest("http://test.mydomain.fr/foobar/").build();

        HttpResponse response = TestUtils.driverProxy(driver, requestWithSurrogate);
        assertEquals("Fragment 1 Fragment 2", EntityUtils.toString(response.getEntity()));
    }

    /**
     * Ensure an error page is returned when capacity if overloaded.
     * 
     * @throws Exception
     */
    public void testQueueBehaviorNoRoom() throws Exception {

        // Conf
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://provider/");
        properties.put(Parameters.EXTENSIONS, Esi.class.getName());
        properties.put(Esi.MAX_THREADS, "1");
        properties.put(Esi.MAX_QUEUE, "1");

        // Setup remote server (provider) response.

        Driver driver = TestUtils.createMockDriver(properties, new IResponseHandler() {
            @Override
            public HttpResponse execute(HttpRequest request) throws UnsupportedEncodingException {

                if (request.getRequestLine().getUri().equals("/foobar/")) {
                    return TestUtils
                            .createHttpResponse()
                            .status(HttpStatus.SC_OK)
                            .reason("OK")
                            .header("Content-Type", "text/html; charset=utf-8")
                            .entity("<esi:include src=\"http://test.mydomain.fr/esi/1\"/>"
                                    + "<esi:include src=\"http://test.mydomain.fr/esi/2\"/>"
                                    + "<esi:include src=\"http://test.mydomain.fr/esi/3\"/>").build();
                }

                if (request.getRequestLine().getUri().equals("/esi/1")) {
                    return TestUtils.createHttpResponse().status(HttpStatus.SC_OK).reason("OK")
                            .header("Content-Type", "text/html; charset=utf-8").entity("Fragment 1").build();
                }

                if (request.getRequestLine().getUri().equals("/esi/2")) {
                    return TestUtils.createHttpResponse().status(HttpStatus.SC_OK).reason("OK")
                            .header("Content-Type", "text/html; charset=utf-8").entity("Fragment 2").build();
                }

                throw new IllegalStateException("Unexpected request" + request.getRequestLine().getUri());
            }
        });

        // Request
        IncomingRequest requestWithSurrogate = TestUtils.createRequest("http://test.mydomain.fr/foobar/").build();

        try {
            HttpResponse response = TestUtils.driverProxy(driver, requestWithSurrogate);
            Assert.fail("Must generate an error page.");
        } catch (HttpErrorPage error) {
            Assert.assertEquals(509, error.getHttpResponse().getStatusLine().getStatusCode());
        }

    }

    /**
     * Ensure ESI works when max_threads = 0 (parallel mode is disabled)
     * 
     * @throws Exception
     */
    public void testNoExecutor() throws Exception {

        // Conf
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://provider/");
        properties.put(Parameters.EXTENSIONS, Esi.class.getName());
        properties.put(Esi.MAX_THREADS, "0");

        // Setup remote server (provider) response.

        Driver driver = TestUtils.createMockDriver(properties, new IResponseHandler() {
            @Override
            public HttpResponse execute(HttpRequest request) throws UnsupportedEncodingException {

                if (request.getRequestLine().getUri().equals("/foobar/")) {
                    return TestUtils
                            .createHttpResponse()
                            .status(HttpStatus.SC_OK)
                            .reason("OK")
                            .header("Content-Type", "text/html; charset=utf-8")
                            .entity("<esi:try><esi:attempt><esi:include src=\"http://test.mydomain.fr/esi/1\"/> "
                                    + "<esi:include src=\"http://test.mydomain.fr/esi/2\"/></esi:attempt><esi:except></esi:except></esi:try>")
                            .build();
                }

                if (request.getRequestLine().getUri().equals("/esi/1")) {
                    return TestUtils.createHttpResponse().status(HttpStatus.SC_OK).reason("OK")
                            .header("Content-Type", "text/html; charset=utf-8").entity("Fragment 1").build();
                }

                if (request.getRequestLine().getUri().equals("/esi/2")) {
                    return TestUtils.createHttpResponse().status(HttpStatus.SC_OK).reason("OK")
                            .header("Content-Type", "text/html; charset=utf-8").entity("Fragment 2").build();
                }

                throw new IllegalStateException("Unexpected request" + request.getRequestLine().getUri());
            }
        });

        // Request
        IncomingRequest requestWithSurrogate = TestUtils.createRequest("http://test.mydomain.fr/foobar/").build();

        HttpResponse response = TestUtils.driverProxy(driver, requestWithSurrogate);
        assertEquals("Fragment 1 Fragment 2", EntityUtils.toString(response.getEntity()));
    }

    /**
     * This test ensure the parallel esi doesn't get locked by recursive includes.
     * 
     * @throws Exception
     */
    public void testNotEnoughThreads() throws Exception {

        // Conf
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://provider/");
        properties.put(Parameters.EXTENSIONS, Esi.class.getName());
        properties.put(Esi.MAX_THREADS, "1");

        // Setup remote server (provider) response.

        Driver driver = TestUtils.createMockDriver(properties, new IResponseHandler() {
            @Override
            public HttpResponse execute(HttpRequest request) throws UnsupportedEncodingException {

                if (request.getRequestLine().getUri().equals("/foobar/")) {
                    return TestUtils
                            .createHttpResponse()
                            .status(HttpStatus.SC_OK)
                            .reason("OK")
                            .header("Content-Type", "text/html; charset=utf-8")
                            .entity("<esi:try><esi:attempt><esi:include src=\"http://test.mydomain.fr/esi/1\"/> "
                                    + "<esi:include src=\"http://test.mydomain.fr/esi/2\"/></esi:attempt><esi:except></esi:except></esi:try>")
                            .build();
                }

                if (request.getRequestLine().getUri().equals("/esi/1")) {
                    return TestUtils.createHttpResponse().status(HttpStatus.SC_OK).reason("OK")
                            .header("Content-Type", "text/html; charset=utf-8")
                            .entity("Fragment 1 <esi:include src=\"http://test.mydomain.fr/esi/3\"/>").build();
                }

                if (request.getRequestLine().getUri().equals("/esi/2")) {
                    return TestUtils.createHttpResponse().status(HttpStatus.SC_OK).reason("OK")
                            .header("Content-Type", "text/html; charset=utf-8").entity("Fragment 2").build();
                }
                if (request.getRequestLine().getUri().equals("/esi/3")) {
                    return TestUtils.createHttpResponse().status(HttpStatus.SC_OK).reason("OK")
                            .header("Content-Type", "text/html; charset=utf-8")
                            .entity("Fragment 3 <esi:include src=\"http://test.mydomain.fr/esi/4\"/>").build();
                }

                if (request.getRequestLine().getUri().equals("/esi/4")) {
                    return TestUtils.createHttpResponse().status(HttpStatus.SC_OK).reason("OK")
                            .header("Content-Type", "text/html; charset=utf-8").entity("Fragment 4").build();
                }

                throw new IllegalStateException("Unexpected request" + request.getRequestLine().getUri());
            }
        });

        // Request
        IncomingRequest requestWithSurrogate = TestUtils.createRequest("http://test.mydomain.fr/foobar/").build();

        HttpResponse response = TestUtils.driverProxy(driver, requestWithSurrogate);
        assertEquals("Fragment 1 Fragment 3 Fragment 4 Fragment 2", EntityUtils.toString(response.getEntity()));
    }

    /**
     * This test ensure the parallel esi if faster than single thread mode.
     * 
     * @throws Exception
     */
    public void testParallelPerformance() throws Exception {

        // Conf
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://provider/");
        properties.put(Parameters.EXTENSIONS, Esi.class.getName());
        properties.put(Esi.MAX_THREADS, "10");
        properties.put(Esi.MIN_THREADS, "10");

        // Setup remote server (provider) response.

        Driver driver = TestUtils.createMockDriver(properties, new IResponseHandler() {
            @Override
            public HttpResponse execute(HttpRequest request) throws UnsupportedEncodingException {
                try {
                    if (request.getRequestLine().getUri().equals("/foobar/")) {
                        return TestUtils
                                .createHttpResponse()
                                .status(HttpStatus.SC_OK)
                                .reason("OK")
                                .header("Content-Type", "text/html; charset=utf-8")
                                .entity("<esi:try><esi:attempt><esi:include src=\"http://test.mydomain.fr/esi/1\"/> "
                                        + "<esi:include src=\"http://test.mydomain.fr/esi/2\"/></esi:attempt><esi:except></esi:except></esi:try>")
                                .build();
                    }

                    if (request.getRequestLine().getUri().equals("/esi/1")) {

                        Thread.sleep(200);

                        return TestUtils.createHttpResponse().status(HttpStatus.SC_OK).reason("OK")
                                .header("Content-Type", "text/html; charset=utf-8")
                                .entity("Fragment 1 <esi:include src=\"http://test.mydomain.fr/esi/3\"/>").build();
                    }

                    if (request.getRequestLine().getUri().equals("/esi/2")) {
                        Thread.sleep(200);
                        return TestUtils.createHttpResponse().status(HttpStatus.SC_OK).reason("OK")
                                .header("Content-Type", "text/html; charset=utf-8").entity("Fragment 2").build();
                    }
                    if (request.getRequestLine().getUri().equals("/esi/3")) {
                        Thread.sleep(200);
                        return TestUtils.createHttpResponse().status(HttpStatus.SC_OK).reason("OK")
                                .header("Content-Type", "text/html; charset=utf-8")
                                .entity("Fragment 3 <esi:include src=\"http://test.mydomain.fr/esi/4\"/>").build();
                    }

                    if (request.getRequestLine().getUri().equals("/esi/4")) {
                        Thread.sleep(200);
                        return TestUtils.createHttpResponse().status(HttpStatus.SC_OK).reason("OK")
                                .header("Content-Type", "text/html; charset=utf-8").entity("Fragment 4").build();
                    }

                    throw new IllegalStateException("Unexpected request" + request.getRequestLine().getUri());
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
            }
        });

        // Request
        IncomingRequest requestWithSurrogate = TestUtils.createRequest("http://test.mydomain.fr/foobar/").build();

        long start = System.currentTimeMillis();
        HttpResponse response = TestUtils.driverProxy(driver, requestWithSurrogate);
        long duration = System.currentTimeMillis() - start;

        assertEquals("Fragment 1 Fragment 3 Fragment 4 Fragment 2", EntityUtils.toString(response.getEntity()));
        assertTrue(duration < 800);
    }

}

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

package org.esigate.http;

import java.io.IOException;
import java.util.Properties;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.esigate.Driver;
import org.esigate.Parameters;
import org.esigate.test.MockMediator;
import org.esigate.test.conn.IResponseHandler;
import org.esigate.test.driver.AbstractDriverTestCase;

/**
 * Tests on X-Forwarded-For and X-Forwarded-Proto implementation.
 * 
 * @author Nicolas Richeton
 * 
 */
public class XForwardedHeadersTest extends AbstractDriverTestCase {

    /**
     * Ensure existing X-Forwarded headers are correctly altered.
     * 
     * @throws Exception
     *             on error.
     */
    public void testXForwardedHeaders() throws Exception {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost/");

        Driver driver = createMockDriver(properties, new IResponseHandler() {
            @Override
            public HttpResponse execute(HttpRequest request) throws IOException {
                assertEquals(1, request.getHeaders("X-Forwarded-For").length);
                assertEquals("192.168.0.1, 127.0.0.1", request.getFirstHeader("X-Forwarded-For").getValue());
                assertEquals(1, request.getHeaders("X-Forwarded-Proto").length);
                assertEquals("https", request.getFirstHeader("X-Forwarded-Proto").getValue());
                return createHttpResponse().status(HttpStatus.SC_OK).reason("OK").build();
            }
        });

        String uri = "http://test.mydomain.fr/foobar/";
        MockMediator mediator = new MockMediator(uri);
        mediator.setRemoteAddr("127.0.0.1");

        HttpEntityEnclosingRequest request =
                createHttpRequest().uri(uri).header("X-Forwarded-For", "192.168.0.1")
                        .header("X-Forwarded-Proto", "https").mediator(mediator).build();

        driverProxy(driver, request);

    }

    /**
     * Discard existing headers and ensure they are correctly replaced by default ones.
     * 
     * @throws Exception
     *             on error
     */
    public void testXForwardedHeadersDiscarded() throws Exception {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost/");
        properties.put(Parameters.DISCARD_REQUEST_HEADERS, "X-Forwarded-For,X-Forwarded-Proto");

        Driver driver = createMockDriver(properties, new IResponseHandler() {
            @Override
            public HttpResponse execute(HttpRequest request) throws IOException {
                assertEquals(1, request.getHeaders("X-Forwarded-For").length);
                assertEquals("127.0.0.1", request.getFirstHeader("X-Forwarded-For").getValue());
                assertEquals(1, request.getHeaders("X-Forwarded-Proto").length);
                assertEquals("http", request.getFirstHeader("X-Forwarded-Proto").getValue());
                return createHttpResponse().status(HttpStatus.SC_OK).reason("OK").build();
            }
        });

        String uri = "http://test.mydomain.fr/foobar/";
        MockMediator mediator = new MockMediator(uri);
        mediator.setRemoteAddr("127.0.0.1");

        HttpEntityEnclosingRequest request =
                createHttpRequest().uri(uri).header("X-Forwarded-For", "192.168.0.1")
                        .header("X-Forwarded-Proto", "https").mediator(mediator).build();

        driverProxy(driver, request);

    }

    /**
     * Ensure existing X-Forwarded headers are correctly altered.
     * <p>
     * (When Esigate is accessed via HTTPS).
     * 
     * @throws Exception
     *             on error.
     */
    public void testXForwardedHeadersHttps() throws Exception {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost/");

        Driver driver = createMockDriver(properties, new IResponseHandler() {
            @Override
            public HttpResponse execute(HttpRequest request) throws IOException {
                assertEquals(1, request.getHeaders("X-Forwarded-For").length);
                assertEquals("192.168.0.1, 127.0.0.1", request.getFirstHeader("X-Forwarded-For").getValue());
                assertEquals(1, request.getHeaders("X-Forwarded-Proto").length);
                assertEquals("http", request.getFirstHeader("X-Forwarded-Proto").getValue());
                return createHttpResponse().status(HttpStatus.SC_OK).reason("OK").build();
            }
        });

        String uri = "http://test.mydomain.fr/foobar/";
        MockMediator mediator = new MockMediator(uri);
        mediator.setRemoteAddr("127.0.0.1");

        HttpEntityEnclosingRequest request =
                createHttpRequest().uri(uri).header("X-Forwarded-For", "192.168.0.1")
                        .header("X-Forwarded-Proto", "http").mediator(mediator).build();

        driverProxy(driver, request);

    }

    /**
     * Ensure X-Forwarded headers are correctly added.
     * <ul>
     * <li>Ensure X-Forwarded-For is set to 127.0.0.1</li>
     * <li>(localhost) X-Forwarded-Proto is set to http for http requests.</li>
     * </ul>
     * 
     * @throws Exception
     *             on error.
     */
    public void testAddXForwardedHeadersHttp() throws Exception {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost/");

        Driver driver = createMockDriver(properties, new IResponseHandler() {
            @Override
            public HttpResponse execute(HttpRequest request) throws IOException {
                assertEquals(1, request.getHeaders("X-Forwarded-For").length);
                assertEquals("127.0.0.1", request.getFirstHeader("X-Forwarded-For").getValue());
                assertEquals(1, request.getHeaders("X-Forwarded-Proto").length);
                assertEquals("http", request.getFirstHeader("X-Forwarded-Proto").getValue());
                return createHttpResponse().status(HttpStatus.SC_OK).reason("OK").build();
            }
        });

        String uri = "http://test.mydomain.fr/foobar/";
        MockMediator mediator = new MockMediator(uri);
        mediator.setRemoteAddr("127.0.0.1");

        HttpEntityEnclosingRequest request = createHttpRequest().uri(uri).mediator(mediator).build();

        driverProxy(driver, request);
    }

    /**
     * Tests for https request with no existing X-Forwarded headers.
     * <ul>
     * <li>Ensure X-Forwarded-For is set to 127.0.0.1</li>
     * <li>(localhost) X-Forwarded-Proto is set to https for https requests.</li>
     * </ul>
     * 
     * @throws Exception
     *             on error.
     */
    public void testAddXForwardedHeadersHttps() throws Exception {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost/");

        Driver driver = createMockDriver(properties, new IResponseHandler() {
            @Override
            public HttpResponse execute(HttpRequest request) throws IOException {
                assertEquals(1, request.getHeaders("X-Forwarded-For").length);
                assertEquals("127.0.0.1", request.getFirstHeader("X-Forwarded-For").getValue());
                assertEquals(1, request.getHeaders("X-Forwarded-Proto").length);
                assertEquals("https", request.getFirstHeader("X-Forwarded-Proto").getValue());
                return createHttpResponse().status(HttpStatus.SC_OK).reason("OK").build();
            }
        });

        String uri = "https://test.mydomain.fr/foobar/";
        MockMediator mediator = new MockMediator(uri);
        mediator.setRemoteAddr("127.0.0.1");

        HttpEntityEnclosingRequest request = createHttpRequest().uri(uri).mediator(mediator).build();

        driverProxy(driver, request);

    }

}

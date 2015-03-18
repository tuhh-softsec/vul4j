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

import java.util.Properties;

import junit.framework.TestCase;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.esigate.Driver;
import org.esigate.Parameters;
import org.esigate.test.TestUtils;
import org.esigate.test.conn.IResponseHandler;

/**
 * Tests on X-Forwarded-For and X-Forwarded-Proto implementation.
 * 
 * @author Nicolas Richeton
 * 
 */
public class XForwardedHeadersTest extends TestCase {

    /**
     * Ensure existing X-Forwarded headers are correctly altered.
     * 
     * @throws Exception
     *             on error.
     */
    public void testXForwardedHeaders() throws Exception {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost/");

        Driver driver = TestUtils.createMockDriver(properties, new IResponseHandler() {
            @Override
            public HttpResponse execute(HttpRequest request) {
                assertEquals(1, request.getHeaders("X-Forwarded-For").length);
                assertEquals("192.168.0.1, 127.0.0.1", request.getFirstHeader("X-Forwarded-For").getValue());
                assertEquals(1, request.getHeaders("X-Forwarded-Proto").length);
                assertEquals("https", request.getFirstHeader("X-Forwarded-Proto").getValue());
                return TestUtils.createHttpResponse().status(HttpStatus.SC_OK).reason("OK").build();
            }
        });

        IncomingRequest request =
                TestUtils.createRequest("http://test.mydomain.fr/foobar/").addHeader("X-Forwarded-For", "192.168.0.1")
                        .addHeader("X-Forwarded-Proto", "https").setRemoteAddr("127.0.0.1").build();

        TestUtils.driverProxy(driver, request);

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

        Driver driver = TestUtils.createMockDriver(properties, new IResponseHandler() {
            @Override
            public HttpResponse execute(HttpRequest request) {
                assertEquals(1, request.getHeaders("X-Forwarded-For").length);
                assertEquals("192.168.0.1, 127.0.0.1", request.getFirstHeader("X-Forwarded-For").getValue());
                assertEquals(1, request.getHeaders("X-Forwarded-Proto").length);
                assertEquals("http", request.getFirstHeader("X-Forwarded-Proto").getValue());
                return TestUtils.createHttpResponse().status(HttpStatus.SC_OK).reason("OK").build();
            }
        });

        IncomingRequest request =
                TestUtils.createRequest("http://test.mydomain.fr/foobar/").addHeader("X-Forwarded-For", "192.168.0.1")
                        .addHeader("X-Forwarded-Proto", "http").setRemoteAddr("127.0.0.1").build();

        TestUtils.driverProxy(driver, request);

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

        Driver driver = TestUtils.createMockDriver(properties, new IResponseHandler() {
            @Override
            public HttpResponse execute(HttpRequest request) {
                assertEquals(1, request.getHeaders("X-Forwarded-For").length);
                assertEquals("127.0.0.1", request.getFirstHeader("X-Forwarded-For").getValue());
                assertEquals(1, request.getHeaders("X-Forwarded-Proto").length);
                assertEquals("http", request.getFirstHeader("X-Forwarded-Proto").getValue());
                return TestUtils.createHttpResponse().status(HttpStatus.SC_OK).reason("OK").build();
            }
        });

        IncomingRequest request =
                TestUtils.createRequest("http://test.mydomain.fr/foobar/").setRemoteAddr("127.0.0.1").build();

        TestUtils.driverProxy(driver, request);
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

        Driver driver = TestUtils.createMockDriver(properties, new IResponseHandler() {
            @Override
            public HttpResponse execute(HttpRequest request) {
                assertEquals(1, request.getHeaders("X-Forwarded-For").length);
                assertEquals("127.0.0.1", request.getFirstHeader("X-Forwarded-For").getValue());
                assertEquals(1, request.getHeaders("X-Forwarded-Proto").length);
                assertEquals("https", request.getFirstHeader("X-Forwarded-Proto").getValue());
                return TestUtils.createHttpResponse().status(HttpStatus.SC_OK).reason("OK").build();
            }
        });

        IncomingRequest request =
                TestUtils.createRequest("https://test.mydomain.fr/foobar/").setRemoteAddr("127.0.0.1").build();

        TestUtils.driverProxy(driver, request);

    }

}

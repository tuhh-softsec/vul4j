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

package org.esigate.extension;

import java.util.Properties;

import junit.framework.TestCase;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.esigate.Driver;
import org.esigate.Parameters;
import org.esigate.http.IncomingRequest;
import org.esigate.test.TestUtils;
import org.esigate.test.conn.SequenceResponse;

public class DefaultCharsetTest extends TestCase {

    public void testDefaultCharsetExt() throws Exception {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost/");
        properties.put(Parameters.EXTENSIONS.getName(), "org.esigate.extension.DefaultCharset");
        properties.put(Parameters.USE_CACHE.getName(), "true");

        Driver driver =
                TestUtils.createMockDriver(
                        properties,
                        new SequenceResponse().response(TestUtils.createHttpResponse().status(HttpStatus.SC_OK)
                                .reason("Ok").header("Date", "Thu, 13 Dec 2012 08:55:37 GMT")
                                .header("Content-Type", "text/html").entity("test").build()));

        IncomingRequest request = TestUtils.createRequest("http://test.mydomain.fr/foobar/").build();

        HttpResponse response = TestUtils.driverProxy(driver, request);

        assertEquals("Encoding should be added", "text/html; charset=ISO-8859-1",
                response.getFirstHeader("Content-Type").getValue());
    }

    public void testDefaultCharsetExtConfig() throws Exception {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost/");
        properties.put(Parameters.EXTENSIONS.getName(), "org.esigate.extension.DefaultCharset");
        properties.put(Parameters.USE_CACHE.getName(), "false");
        properties.put("defaultCharset", "utf-8");

        Driver driver =
                TestUtils.createMockDriver(
                        properties,
                        new SequenceResponse().response(TestUtils.createHttpResponse().status(HttpStatus.SC_OK)
                                .reason("Ok").header("Date", "Thu, 13 Dec 2012 08:55:37 GMT")
                                .header("Content-Type", "text/html").entity("test").build()));

        IncomingRequest request = TestUtils.createRequest("http://test.mydomain.fr/foobar/").build();

        HttpResponse response = TestUtils.driverProxy(driver, request);

        assertEquals("Encoding should be added", "text/html; charset=utf-8", response.getFirstHeader("Content-Type")
                .getValue());
    }

    public void testDefaultCharsetExtNonParsable() throws Exception {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost/");
        properties.put(Parameters.EXTENSIONS.getName(), "org.esigate.extension.DefaultCharset");
        properties.put(Parameters.USE_CACHE.getName(), "true");

        Driver driver =
                TestUtils.createMockDriver(
                        properties,
                        new SequenceResponse().response(TestUtils.createHttpResponse().status(HttpStatus.SC_OK)
                                .reason("Ok").header("Date", "Thu, 13 Dec 2012 08:55:37 GMT")
                                .header("Content-Type", "text/xml").entity("test").build()));

        IncomingRequest request = TestUtils.createRequest("http://test.mydomain.fr/foobar/").build();

        HttpResponse response = TestUtils.driverProxy(driver, request);

        assertEquals("Encoding should be added", "text/xml", response.getFirstHeader("Content-Type").getValue());
    }

}

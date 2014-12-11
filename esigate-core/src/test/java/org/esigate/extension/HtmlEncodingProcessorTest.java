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

package org.esigate.extension;

import java.io.IOException;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.util.EntityUtils;
import org.esigate.Driver;
import org.esigate.HttpErrorPage;
import org.esigate.Parameters;
import org.esigate.http.IncomingRequest;
import org.esigate.test.TestUtils;
import org.esigate.test.conn.SequenceResponse;

public class HtmlEncodingProcessorTest extends TestCase {

    public void testBug184HtmlEncodingProcessing() throws Exception {
        doEncodingTest("text/html", "<html><head><meta charset=\"utf-8\" /></head><body>testéèà</body></html>");
        doEncodingTest("text/html",
                "<html><head><meta content=\"text/html; charset=utf-8\" ></head><body>testéèà</body></html>");
        doEncodingTest("text/html",
                "<html><head><metA content=\"text/html; charset=utf-8\" /></head><body>testéèà</body></html>");
        doEncodingTest("text/html; charset=UTF-8",
                "<html><head><metA content=\"text/html; charset=utf-8\" /></head><body>testéèà</body></html>");
        doEncodingTest("text/html; charset=iso-8859-1",
                "<html><head><metA content=\"text/html; charset=utf-8\" /></head><body>testéèà</body></html>");
    }

    private void doEncodingTest(String contentType, String s) throws IOException, HttpErrorPage {
        Properties properties = new Properties();
        properties.put(Parameters.REMOTE_URL_BASE.getName(), "http://localhost/");
        properties.put(Parameters.EXTENSIONS.getName(), HtmlCharsetProcessor.class.getName());

        Driver driver =
                TestUtils.createMockDriver(
                        properties,
                        new SequenceResponse().response(TestUtils.createHttpResponse().status(HttpStatus.SC_OK)
                                .reason("Ok").header("Date", "Thu, 13 Dec 2012 08:55:37 GMT")
                                .header("Content-Type", contentType).entity(new ByteArrayEntity(s.getBytes("utf-8")))
                                .build()));

        IncomingRequest request = TestUtils.createIncomingRequest("http://test.mydomain.fr/foobar/").build();

        HttpResponse response = TestUtils.driverProxy(driver, request);

        assertEquals("Encoding should be added", s, EntityUtils.toString(response.getEntity()));
    }
}

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
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.util.EntityUtils;
import org.esigate.Driver;
import org.esigate.HttpErrorPage;
import org.esigate.Parameters;
import org.esigate.test.TestUtils;
import org.esigate.test.conn.SequenceResponse;
import org.esigate.test.driver.AbstractDriverTestCase;

public class HtmlEncodingProcessorTest extends AbstractDriverTestCase {

	public void testBug184_HtmlEncodingProcessing() throws Exception {
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

	private void doEncodingTest(String contentType, String s) throws IOException, HttpErrorPage, URISyntaxException {
		Properties properties = new Properties();
		properties.put(Parameters.REMOTE_URL_BASE.name, "http://localhost/");
		properties.put(Parameters.EXTENSIONS.name, HtmlCharsetProcessor.class.getName());

		Driver driver = createMockDriver(
				properties,
				new SequenceResponse().response(createHttpResponse().status(200).reason("Ok")
						.header("Date", "Thu, 13 Dec 2012 08:55:37 GMT").header("Content-Type", contentType)
						.entity(new ByteArrayEntity(s.getBytes("utf-8"))).build()));

		HttpEntityEnclosingRequest request = TestUtils.createRequest("http://test.mydomain.fr/foobar/");

		HttpResponse response = driverProxy(driver, request);

		assertEquals("Encoding should be added", s, EntityUtils.toString(response.getEntity()));
	}
}

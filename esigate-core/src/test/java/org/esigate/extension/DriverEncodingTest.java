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

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;
import org.esigate.Driver;
import org.esigate.DriverFactory;
import org.esigate.HttpErrorPage;
import org.esigate.Parameters;
import org.esigate.cookie.CookieManager;
import org.esigate.events.EventManager;
import org.esigate.http.HttpClientHelper;
import org.esigate.http.MockConnectionManager;
import org.esigate.test.TestUtils;

public class DriverEncodingTest extends TestCase {

	private Driver createMockDriver(Properties properties, HttpClientConnectionManager connectionManager) {
		return createMockDriver(properties, connectionManager, "tested");
	}

	private Driver createMockDriver(Properties properties, HttpClientConnectionManager connectionManager, String name) {
		CookieManager cookieManager = ExtensionFactory.getExtension(properties, Parameters.COOKIE_MANAGER, null);

		HttpClientHelper httpClientHelper = new HttpClientHelper(new EventManager(), cookieManager, properties, connectionManager);
		Driver driver = new Driver(name, properties, httpClientHelper);
		DriverFactory.put(name, driver);
		return driver;
	}

	
	public void testBug184_HtmlEncodingProcessing() throws Exception {
		doEncodingTest( "text/html","<html><head><meta charset=\"utf-8\" /></head><body>testéèà</body></html>" );
		doEncodingTest( "text/html","<html><head><meta content=\"text/html; charset=utf-8\" ></head><body>testéèà</body></html>" );
		doEncodingTest( "text/html","<html><head><metA content=\"text/html; charset=utf-8\" /></head><body>testéèà</body></html>" );
		doEncodingTest( "text/html; charset=UTF-8","<html><head><metA content=\"text/html; charset=utf-8\" /></head><body>testéèà</body></html>" );
		doEncodingTest( "text/html; charset=iso-8859-1","<html><head><metA content=\"text/html; charset=utf-8\" /></head><body>testéèà</body></html>" );
	}
	
	private void doEncodingTest(String contentType, String s ) throws IOException, HttpErrorPage{
		Properties properties = new Properties();
		properties.put(Parameters.REMOTE_URL_BASE.name, "http://localhost/");
		properties.put(Parameters.EXTENSIONS.name, HtmlCharsetProcessor.class.getName());

		MockConnectionManager mockHttpClient = new MockConnectionManager();
		BasicHttpResponse response = new BasicHttpResponse(new ProtocolVersion(
				"HTTP", 1, 1), HttpStatus.SC_OK, "Ok");
		response.addHeader("Date", "Thu, 13 Dec 2012 08:55:37 GMT");
		response.addHeader("Content-Type",contentType);
		response.setEntity(new ByteArrayEntity(s.getBytes("utf-8")));
		mockHttpClient.setResponse(response);

		Driver driver = createMockDriver(properties, mockHttpClient);

		HttpEntityEnclosingRequest request = TestUtils
				.createRequest("http://test.mydomain.fr/foobar/");

		driver.proxy("/foobar/", request);

		assertEquals("Encoding should be added", s, 
				EntityUtils.toString(TestUtils.getResponse(request).getEntity()));
	}
	
}

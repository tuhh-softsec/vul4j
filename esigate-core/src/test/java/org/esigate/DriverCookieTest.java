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
package org.esigate;

import java.io.IOException;
import java.util.Properties;

import junit.framework.Assert;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.cookie.ClientCookie;
import org.esigate.test.MockMediator;
import org.esigate.test.conn.IResponseHandler;
import org.esigate.test.driver.AbstractDriverTestCase;
import org.esigate.util.HttpRequestHelper;

/**
 * Tests for Cookie behavior
 * 
 * @author Nicolas Richeton
 * 
 */
public class DriverCookieTest extends AbstractDriverTestCase {

	/**
	 * Ensure Secure attribute is kept is connection uses https
	 * 
	 * @throws Exception
	 */
	public void testSecure() throws Exception {

		// Conf
		Properties properties = new Properties();
		properties.put(Parameters.REMOTE_URL_BASE.name, "http://provider/");
		properties.put(Parameters.FORWARD_COOKIES.name, "*");

		// Setup remote server (provider) response.
		Driver driver = createMockDriver(properties, new IResponseHandler() {
			@Override
			public HttpResponse execute(HttpRequest request) throws IOException {
				return createHttpResponse().status(200).reason("OK")
						.header("Set-Cookie", "testcookie=testvalue; Secure").entity("test").build();
			}
		});

		// Https request : Cookie is forwarded as Secure
		HttpEntityEnclosingRequest request = createHttpRequest().uri("https://test.mydomain.fr/foobar/").mockMediator()
				.build();

		driverProxy(driver, request);
		MockMediator m = (MockMediator) HttpRequestHelper.getMediator(request);
		Assert.assertTrue(((ClientCookie) m.getCookies()[0]).isSecure());

		// Http request : Cookie is forwarded as NOT secure
		request = createHttpRequest().uri("http://test.mydomain.fr/foobar/").mockMediator().build();
		driverProxy(driver, request);
		m = (MockMediator) HttpRequestHelper.getMediator(request);
		Assert.assertFalse(((ClientCookie) m.getCookies()[0]).isSecure());
	}

}

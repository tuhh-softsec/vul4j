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

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.esigate.Driver;
import org.esigate.Parameters;
import org.esigate.test.conn.SequenceResponse;
import org.esigate.test.driver.AbstractDriverTestCase;

public class DefaultCharsetTest extends AbstractDriverTestCase {

	public void testDefaultCharsetExt() throws Exception {
		Properties properties = new Properties();
		properties.put(Parameters.REMOTE_URL_BASE.name, "http://localhost/");
		properties.put(Parameters.EXTENSIONS.name, "org.esigate.extension.DefaultCharset");
		properties.put(Parameters.USE_CACHE.name, "true");

		Driver driver =
				createMockDriver(
						properties,
						new SequenceResponse().response(createHttpResponse().status(200).reason("Ok")
								.header("Date", "Thu, 13 Dec 2012 08:55:37 GMT").header("Content-Type", "text/html")
								.entity("test").build()));

		HttpEntityEnclosingRequest request =
				createHttpRequest().uri("http://test.mydomain.fr/foobar/").mockMediator().build();

		HttpResponse response = driverProxy(driver, request);

		assertEquals("Encoding should be added", "text/html; charset=ISO-8859-1",
				response.getFirstHeader("Content-Type").getValue());
	}

	public void testDefaultCharsetExtConfig() throws Exception {
		Properties properties = new Properties();
		properties.put(Parameters.REMOTE_URL_BASE.name, "http://localhost/");
		properties.put(Parameters.EXTENSIONS.name, "org.esigate.extension.DefaultCharset");
		properties.put(Parameters.USE_CACHE.name, "false");
		properties.put("defaultCharset", "utf-8");

		Driver driver =
				createMockDriver(
						properties,
						new SequenceResponse().response(createHttpResponse().status(200).reason("Ok")
								.header("Date", "Thu, 13 Dec 2012 08:55:37 GMT").header("Content-Type", "text/html")
								.entity("test").build()));

		HttpEntityEnclosingRequest request =
				createHttpRequest().uri("http://test.mydomain.fr/foobar/").mockMediator().build();

		HttpResponse response = driverProxy(driver, request);

		assertEquals("Encoding should be added", "text/html; charset=utf-8", response.getFirstHeader("Content-Type")
				.getValue());
	}

	public void testDefaultCharsetExtNonParsable() throws Exception {
		Properties properties = new Properties();
		properties.put(Parameters.REMOTE_URL_BASE.name, "http://localhost/");
		properties.put(Parameters.EXTENSIONS.name, "org.esigate.extension.DefaultCharset");
		properties.put(Parameters.USE_CACHE.name, "true");

		Driver driver =
				createMockDriver(
						properties,
						new SequenceResponse().response(createHttpResponse().status(200).reason("Ok")
								.header("Date", "Thu, 13 Dec 2012 08:55:37 GMT").header("Content-Type", "text/xml")
								.entity("test").build()));

		HttpEntityEnclosingRequest request =
				createHttpRequest().uri("http://test.mydomain.fr/foobar/").mockMediator().build();

		HttpResponse response = driverProxy(driver, request);

		assertEquals("Encoding should be added", "text/xml", response.getFirstHeader("Content-Type").getValue());
	}

}

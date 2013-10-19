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

import java.io.IOException;
import java.util.Properties;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.esigate.Driver;
import org.esigate.Parameters;
import org.esigate.extension.parallelesi.Esi;
import org.esigate.test.conn.IResponseHandler;
import org.esigate.test.conn.SequenceResponse;
import org.esigate.test.driver.AbstractDriverTestCase;
import org.junit.Assert;

/**
 * Edge-Arch test cases.
 * 
 * @author Nicolas Richeton
 * 
 */
public class SurrogateTest extends AbstractDriverTestCase {

	/**
	 * 4.2.4 content
	 * <p>
	 * Once processing takes place, the capability token that invoked it (as
	 * well as the 'content' directive, if appropriate) is consumed; that is, it
	 * is not passed forward to surrogates.
	 * 
	 * @throws Exception
	 */
	public void testSurrogateControlWithSurrogate() throws Exception {

		// Conf
		Properties properties = new Properties();
		properties.put(Parameters.REMOTE_URL_BASE.name, "http://provider/");
		properties.put(Parameters.EXTENSIONS, Esi.class.getName() + "," + Surrogate.class.getName());

		// Setup remote server (provider) response.
		IResponseHandler mockConnectionManager = new SequenceResponse().addReponse(
				createHttpResponse().status(200).reason("OK")
						.header("Surrogate-Control", "content=\"ESI/1.0 ESI-Inline/1.0\", max-age=600")
						.header("Content-Type", "text/html; charset=utf-8").build()).addReponse(
				createHttpResponse().status(200).reason("OK")
						.header("Surrogate-Control", "content=\"ESI/1.0 ESI-Inline/1.0 ORAESI/9.0.2\", max-age=600")
						.header("Content-Type", "text/html; charset=utf-8").build());

		// Driver
		Driver driver = createMockDriver(properties, mockConnectionManager);

		// Request
		HttpEntityEnclosingRequest requestWithSurrogate = createHttpRequest().uri("http://test.mydomain.fr/foobar/")
				.header("Surrogate-Capabilities", "ab=\"Surrogate/1.0\"").mockMediator().build();

		// content="" is completely removed
		HttpResponse response = driverProxy(driver, requestWithSurrogate);
		Assert.assertEquals("max-age=600", response.getFirstHeader("Surrogate-Control").getValue());

		// Capabilities are removed.
		response = driverProxy(driver, requestWithSurrogate);
		Assert.assertEquals("content=\"ORAESI/9.0.2\", max-age=600", response.getFirstHeader("Surrogate-Control")
				.getValue());

	}

	/**
	 * 2.2 Surrogate-Control Header
	 * <p>
	 * If no downstream surrogates have identified themselves, the header should
	 * be stripped from responses.
	 * 
	 * 
	 * @throws Exception
	 */
	public void testSurrogateControlWithNoSurrogate() throws Exception {

		// Conf
		Properties properties = new Properties();
		properties.put(Parameters.REMOTE_URL_BASE.name, "http://provider/");
		properties.put(Parameters.EXTENSIONS, Esi.class.getName() + "," + Surrogate.class.getName());

		// Setup remote server (provider) response.
		IResponseHandler mockConnectionManager = new SequenceResponse().addReponse(createHttpResponse().status(200)
				.reason("OK").header("Surrogate-Control", "content=\"ESI/1.0 ESI-Inline/1.0\", max-age=600").build());

		// Driver
		Driver driver = createMockDriver(properties, mockConnectionManager);

		// Request
		HttpEntityEnclosingRequest requestWithSurrogate = createHttpRequest().uri("http://test.mydomain.fr/foobar/")
				.mockMediator().build();

		// Proxy
		HttpResponse response = driverProxy(driver, requestWithSurrogate);
		Assert.assertFalse(response.containsHeader("Surrogate-Control"));

	}

	public void testSurrogateCapabilitiese() throws Exception {

		// Conf
		Properties properties = new Properties();
		properties.put(Parameters.REMOTE_URL_BASE.name, "http://provider/");
		properties.put(Parameters.EXTENSIONS, Esi.class.getName() + "," + Surrogate.class.getName());

		// Setup remote server (provider) response.
		IResponseHandler mockConnectionManager = new IResponseHandler() {
			@Override
			public HttpResponse execute(HttpRequest request) throws IOException {
				Assert.assertEquals("esigate=\"Surrogate/1.0 ESI/1.0 ESI-Inline/1.0 ESIGATE/4.0\"", request
						.getFirstHeader("Surrogate-Capabilities").getValue());
				return createHttpResponse().status(200).reason("OK").build();
			}
		};

		Driver driver = createMockDriver(properties, mockConnectionManager);
		HttpEntityEnclosingRequest requestWithSurrogate = createHttpRequest().uri("http://test.mydomain.fr/foobar/")
				.mockMediator().build();
		HttpResponse response = driverProxy(driver, requestWithSurrogate);

	}

	/**
	 * 2.1 Surrogate-Capability Header
	 * <p>
	 * The name in each capability set identifies a device token, which uniquely
	 * identifies the surrogate that appended it. Device tokens must be unique
	 * within a request's Surrogate-Capabilities header.
	 * 
	 * @throws Exception
	 */
	public void testSurrogateCapabilitieseUniqueToken() throws Exception {

		// Conf
		Properties properties = new Properties();
		properties.put(Parameters.REMOTE_URL_BASE.name, "http://provider/");
		properties.put(Parameters.EXTENSIONS, Esi.class.getName() + "," + Surrogate.class.getName());

		// Setup remote server (provider) response.
		IResponseHandler mockConnectionManager = new IResponseHandler() {
			@Override
			public HttpResponse execute(HttpRequest request) throws IOException {
				Assert.assertEquals(
						"esigate=\"Surrogate/1.0\", esigate2=\"Surrogate/1.0 ESI/1.0 ESI-Inline/1.0 ESIGATE/4.0\"",
						request.getFirstHeader("Surrogate-Capabilities").getValue());
				return createHttpResponse().status(200).reason("OK").build();
			}
		};

		Driver driver = createMockDriver(properties, mockConnectionManager);
		HttpEntityEnclosingRequest requestWithSurrogate = createHttpRequest().uri("http://test.mydomain.fr/foobar/")
				.header("Surrogate-Capabilities", "esigate=\"Surrogate/1.0\"").mockMediator().build();
		HttpResponse response = driverProxy(driver, requestWithSurrogate);

	}

	/**
	 * 4.2.4 content
	 * <p/>
	 * The content directive identifies what processing surrogates should
	 * perform on the response before forwarding it. The value of the content
	 * directive is a left-to-right ordered, space-separated list of
	 * capabilities for processing by surrogates.
	 * <p/>
	 * Empty control directive => no processing.
	 * 
	 * @throws Exception
	 */
	public void testSurrogateControlDisableCapability() throws Exception {

		// Conf
		Properties properties = new Properties();
		properties.put(Parameters.REMOTE_URL_BASE.name, "http://provider/");
		properties.put(Parameters.EXTENSIONS, Esi.class.getName() + "," + Surrogate.class.getName());

		// Setup remote server (provider) response.
		IResponseHandler mockConnectionManager = new SequenceResponse()
				.addReponse(createHttpResponse().status(200).reason("OK")
						.entity("before <esi:vars>$(HTTP_HOST)</esi:vars> after")
						.header("Surrogate-Control", "content=\"\"").header("Content-Type", "text/html; charset=utf-8")
						.build());

		Driver driver = createMockDriver(properties, mockConnectionManager);
		HttpEntityEnclosingRequest requestWithSurrogate = createHttpRequest().uri("http://test.mydomain.fr/foobar/")
				.mockMediator().build();
		HttpResponse response = driverProxy(driver, requestWithSurrogate);
		Assert.assertEquals("before <esi:vars>$(HTTP_HOST)</esi:vars> after",
				EntityUtils.toString(response.getEntity()));
	}

	/**
	 * 4.2.4 content
	 * <p/>
	 * The content directive identifies what processing surrogates should
	 * perform on the response before forwarding it. The value of the content
	 * directive is a left-to-right ordered, space-separated list of
	 * capabilities for processing by surrogates.
	 * 
	 * @throws Exception
	 */
	public void testSurrogateControlEnable() throws Exception {
		// Conf
		Properties properties = new Properties();
		properties.put(Parameters.REMOTE_URL_BASE.name, "http://provider/");
		properties.put(Parameters.EXTENSIONS, Esi.class.getName() + "," + Surrogate.class.getName());

		// Setup remote server (provider) response.
		IResponseHandler mockConnectionManager = new SequenceResponse().addReponse(createHttpResponse().status(200)
				.reason("OK").entity("before <esi:vars>$(HTTP_HOST)</esi:vars> after")
				.header("Surrogate-Control", "content=\"ESI/1.0\"").header("Content-Type", "text/html; charset=utf-8")
				.build());

		Driver driver = createMockDriver(properties, mockConnectionManager);
		HttpEntityEnclosingRequest requestWithSurrogate = createHttpRequest().uri("http://test.mydomain.fr/foobar/")
				.mockMediator().build();
		HttpResponse response = driverProxy(driver, requestWithSurrogate);
		Assert.assertEquals("before test.mydomain.fr after", EntityUtils.toString(response.getEntity()));
	}

}

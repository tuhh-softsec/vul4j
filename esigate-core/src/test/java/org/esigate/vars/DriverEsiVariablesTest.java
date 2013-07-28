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
package org.esigate.vars;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

import junit.framework.Assert;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.esigate.Driver;
import org.esigate.DriverFactory;
import org.esigate.HttpErrorPage;
import org.esigate.Parameters;
import org.esigate.esi.EsiRenderer;
import org.esigate.test.TestUtils;
import org.esigate.test.conn.IResponseHandler;
import org.esigate.test.driver.AbstractDriverTestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This test case ensure ESI variables are correctly replaced.
 * 
 * <p>
 * The backend reply a Http Response with &lt:esi:vars&gt; and the result
 * returned by esigate is compared with the expected result.
 * 
 * @author Nicolas Richeton
 * 
 */
public class DriverEsiVariablesTest extends AbstractDriverTestCase {
	private static final Logger LOG = LoggerFactory.getLogger(DriverEsiVariablesTest.class);

	/**
	 * 0000246: ESI variables are not available / replaced
	 * https://sourceforge.net/apps/mantisbt/webassembletool/view.php?id=246
	 * 
	 * @throws IOException
	 * @throws HttpErrorPage
	 * @throws URISyntaxException
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testEsiVariablesCase1() throws IOException, HttpErrorPage, URISyntaxException {
		// Reset Driverfactory (used for default driver with $(PROVIDER))
		Properties factoryProperties = new Properties();
		factoryProperties.put("tested." + Parameters.REMOTE_URL_BASE.name, "http://localhost.mydomain.fr/");
		DriverFactory.configure(factoryProperties);

		// Configuration
		Properties properties = new Properties();
		properties.put(Parameters.REMOTE_URL_BASE.name, "http://localhost.mydomain.fr/");

		// Test case
		HttpEntityEnclosingRequest request = createHttpRequest()
				.uri("http://test.mydomain.fr/foobar/?test=esigate&test2=esigate2")
				.header("Referer", "http://www.esigate.org")
				.header("User-Agent",
						"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_4) AppleWebKit/536.30.1 (KHTML, like Gecko) Version/6.0.5 Safari/536.30.1")
				.header("Accept-Language", "da, en-gb;q=0.8, en;q=0.7").cookie("test-cookie", "test-cookie-value")
				.cookie("test-cookie2", "test-cookie-value2").mockMediator().build();

		final StringBuilder expected = new StringBuilder();
		addVariable(expected, "HTTP_ACCEPT_LANGUAGE", "da, en-gb;q=0.8, en;q=0.7");
		addVariable(expected, "HTTP_ACCEPT_LANGUAGE{en}", "true");
		addVariable(expected, "HTTP_ACCEPT_LANGUAGE{fr}", "false");
		addVariable(expected, "QUERY_STRING{test}", "esigate");
		addVariable(expected, "QUERY_STRING", "test=esigate&test2=esigate2");
		addVariable(expected, "HTTP_REFERER", "http://www.esigate.org");
		addVariable(expected, "PROVIDER{tested}", "http://localhost.mydomain.fr/");
		addVariable(expected, "PROVIDER{missing}", "");
		addVariable(expected, "PROVIDER", "");
		addVariable(expected, "HTTP_HOST", "test.mydomain.fr");
		addVariable(expected, "HTTP_USER_AGENT",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_4) AppleWebKit/536.30.1 (KHTML, like Gecko) Version/6.0.5 Safari/536.30.1");
		addVariable(expected, "HTTP_USER_AGENT{browser}", "MOZILLA");
		addVariable(expected, "HTTP_USER_AGENT{os}", "MAC");
		addVariable(expected, "HTTP_COOKIE{test-cookie}", "test-cookie-value");
		addVariable(expected, "HTTP_COOKIE{missing}", "");
		addVariable(expected, "QUERY_STRING{missing}", "");
		addVariable(expected, "HTTP_USER_AGENT{version}", "5.0");
		
		addVariable(expected, "HTTP_COOKIE",
		 "test-cookie=test-cookie-value; test-cookie2=test-cookie-value2");
		addVariable(expected, "QUERY_STRING{missing}|default-value", "default-value");
		addVariable(expected, "QUERY_STRING{missing}|'default value'", "default value");

		// Setup remote server (provider) response.
		IResponseHandler mockExecutor = new IResponseHandler() {
			@Override
			public HttpResponse execute(HttpRequest request) throws IOException {

				StringBuilder content = new StringBuilder();
				content.append("<esi:vars>");

				String[] expectedArray = StringUtils.splitByWholeSeparator(expected.toString(), "<p>");

				for (String expr : expectedArray) {
					addVariable(content, expr.substring(0, expr.indexOf(":")));
				}

				content.append("</esi:vars>");

				LOG.info("Backend response:\n" + content.toString());

				return createHttpResponse().entity(new StringEntity(content.toString(), ContentType.TEXT_HTML)).build();
			}
		};

		// Build driver and request.
		Driver driver = createMockDriver(properties, mockExecutor);

		driverProxy(driver, request, new EsiRenderer());

		HttpResponse response = TestUtils.getResponse(request);
		String entityContent = EntityUtils.toString(response.getEntity());
		LOG.info("Esigate response: \n" + entityContent);

		String[] expectedArray = StringUtils.splitByWholeSeparator(expected.toString(), "<p>");
		String[] resultArray = StringUtils.splitByWholeSeparator(entityContent, "<p>");

		for (int i = 0; i < expectedArray.length; i++) {
			String varName = expectedArray[i].substring(0, expectedArray[i].indexOf(":"));
			Assert.assertEquals(varName, expectedArray[i], resultArray[i]);
			LOG.info("Success with variable {}", varName);
		}

	}

	/**
	 * 0000246: ESI variables are not available / replaced
	 * https://sourceforge.net/apps/mantisbt/webassembletool/view.php?id=246
	 * 
	 * @throws IOException
	 * @throws HttpErrorPage
	 * @throws URISyntaxException
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testEsiVariablesCase2() throws IOException, HttpErrorPage, URISyntaxException {
		// Configuration
		Properties properties = new Properties();
		properties.put(Parameters.REMOTE_URL_BASE.name, "http://localhost.mydomain.fr/");

		// Test case
		HttpEntityEnclosingRequest request = createHttpRequest().uri("http://test.mydomain.fr/foobar/")
				.header("User-Agent", "Mozilla/5.0 (Windows; U; MSIE 9.0; WIndows NT 9.0; en-US)").mockMediator()
				.build();

		final StringBuilder expected = new StringBuilder();
		addVariable(expected, "HTTP_ACCEPT_LANGUAGE{en}", "false");
		addVariable(expected, "HTTP_ACCEPT_LANGUAGE{fr}", "false");
		addVariable(expected, "HTTP_HOST", "test.mydomain.fr");
		addVariable(expected, "HTTP_USER_AGENT", "Mozilla/5.0 (Windows; U; MSIE 9.0; WIndows NT 9.0; en-US)");
		addVariable(expected, "HTTP_USER_AGENT{browser}", "MSIE");
		addVariable(expected, "HTTP_USER_AGENT{os}", "WIN");
		addVariable(expected, "HTTP_ACCEPT_LANGUAGE", "");
		addVariable(expected, "QUERY_STRING{test}", "");
		addVariable(expected, "QUERY_STRING", "");
		addVariable(expected, "HTTP_REFERER", "");
		addVariable(expected, "HTTP_COOKIE{test-cookie}", "");
		addVariable(expected, "HTTP_COOKIE", "");
		addVariable(expected, "QUERY_STRING{missing}", "");
		addVariable(expected, "HTTP_USER_AGENT{version}", "5.0");

		// Setup remote server (provider) response.
		IResponseHandler mockExecutor = new IResponseHandler() {
			@Override
			public HttpResponse execute(HttpRequest request) throws IOException {

				StringBuilder content = new StringBuilder();
				content.append("<esi:vars>");

				String[] expectedArray = StringUtils.splitByWholeSeparator(expected.toString(), "<p>");

				for (String expr : expectedArray) {
					addVariable(content, expr.substring(0, expr.indexOf(": ")));
				}

				content.append("</esi:vars>");
				LOG.info("Backend response:\n" + content.toString());

				return createHttpResponse().entity(new StringEntity(content.toString(), ContentType.TEXT_HTML)).build();
			}
		};

		// Build driver and request.
		Driver driver = createMockDriver(properties, mockExecutor);

		driverProxy(driver, request, new EsiRenderer());

		HttpResponse response = TestUtils.getResponse(request);
		String entityContent = EntityUtils.toString(response.getEntity());
		LOG.info("Esigate response: \n" + entityContent);

		String[] expectedArray = StringUtils.splitByWholeSeparator(expected.toString(), "<p>");
		String[] resultArray = StringUtils.splitByWholeSeparator(entityContent, "<p>");

		for (int i = 0; i < expectedArray.length; i++) {
			String varName = expectedArray[i].substring(0, expectedArray[i].indexOf(":"));
			Assert.assertEquals(varName, expectedArray[i], resultArray[i]);
			LOG.info("Success with variable {}", varName);
		}

	}

	static void addVariable(StringBuilder sb, String variable) {
		sb.append("<p>" + variable + ": $(" + variable + ")</p>\n");
		LOG.info("Adding {} for evaluation", variable);
	}

	static void addVariable(StringBuilder sb, String variable, String value) {
		sb.append("<p>" + variable + ": " + value + "</p>\n");
		LOG.info("Adding {} with expected result {}", variable, value);

	}

}

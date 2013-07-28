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
 * Test for &lt;esi:when &gt; esi tag.
 * 
 * @author Nicolas Richeton
 * 
 */
public class DriverEsiWhenTest extends AbstractDriverTestCase {
	private static final Logger LOG = LoggerFactory.getLogger(DriverEsiWhenTest.class);

	@SuppressWarnings("static-method")
	@Test
	public void testEsiWhenCase1() throws IOException, HttpErrorPage, URISyntaxException {
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
		addExpression(expected, "!(1==1)", false);
		addExpression(expected, "!(a==a)", false);
		addExpression(expected, "!(a==b)", true);
		addExpression(expected, "1==1", true);
		addExpression(expected, "a==a", true);
		addExpression(expected, "a==b", false);
		addExpression(expected, "2>=1", true);
		addExpression(expected, "1>=1", true);
		addExpression(expected, "b>=a", true);
		addExpression(expected, "a>=b", false);
		addExpression(expected, "2>1", true);
		addExpression(expected, "b>a", true);
		addExpression(expected, "1>2", false);
		addExpression(expected, "a>b", false);
		addExpression(expected, "2<1", false);
		addExpression(expected, "b<a", false);
		addExpression(expected, "1<2", true);
		addExpression(expected, "2<=1", false);
		addExpression(expected, "1<=2", true);
		addExpression(expected, "$(HTTP_COOKIE{test-cookie})==test-cookie-value", true);
		addExpression(expected, "$(HTTP_COOKIE{test-cookie})=='test-cookie-value'", true);
		addExpression(expected, "$(HTTP_COOKIE{test-cookie})!='test-cookie-not-this-value'", true);

		addExpression(expected, "$(HTTP_REFERER)==http://www.esigate.org", true);
		addExpression(expected, "$(HTTP_HOST)=='test.mydomain.fr'", true);
		addExpression(expected, "$(HTTP_HOST)==test.mydomain.fr", true);

		// Setup remote server (provider) response.
		IResponseHandler mockExecutor = new IResponseHandler() {
			@Override
			public HttpResponse execute(HttpRequest request) throws IOException {

				StringBuilder content = new StringBuilder();

				String[] expectedArray = StringUtils.splitByWholeSeparator(expected.toString(), "<p>");

				for (String expr : expectedArray) {
					addExpression(content, expr.substring(0, expr.indexOf(": ")));
				}

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

	protected static void addExpression(StringBuilder sb, String expr) {
		sb.append("<p>" + expr + ": " + "<esi:choose>" + "<esi:when test=\"" + expr + "\">true</esi:when>"
				+ "<esi:otherwise>false</esi:otherwise>" + "</esi:choose>" + "</p>\n");

		LOG.info("Adding {} for evaluation", expr);
	}

	protected static void addExpression(StringBuilder sb, String expr, boolean value) {
		sb.append("<p>" + expr + ": " + value + "</p>\n");
		if (LOG.isInfoEnabled()) {
			LOG.info("Adding {} with expected result {}", expr, String.valueOf(value));
		}

	}

}

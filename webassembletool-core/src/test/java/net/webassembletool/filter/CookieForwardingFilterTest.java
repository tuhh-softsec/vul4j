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
package net.webassembletool.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.Cookie;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.webassembletool.ConfigurationException;
import net.webassembletool.HttpErrorPage;
import net.webassembletool.MockDriver;
import net.webassembletool.ResourceContext;
import net.webassembletool.http.HttpClientRequest;
import net.webassembletool.http.HttpClientResponse;
import net.webassembletool.http.HttpHeaders;
import net.webassembletool.http.SerializableBasicHttpContext;
import net.webassembletool.test.MockHttpClient;
import net.webassembletool.test.MockHttpClientRequest;
import net.webassembletool.test.MockHttpRequest;
import net.webassembletool.test.MockStatusLine;

import org.apache.http.HttpHost;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.ExecutionContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Nicolas Richeton
 * 
 */
public class CookieForwardingFilterTest extends TestCase {
	private CookieForwardingFilter tested;

	private static final String V_FORWARD1 = "fw1";
	private static final String V_FORWARD2 = "fw2";
	private static final String K_FORWARD1 = "forward1";
	private static final String K_FORWARD2 = "forward2";
	private static final String K_NO_FORWARD = "no-forward";
	private static final String V_NO_FORWARD = "no-forward";

	@Override
	protected void setUp() {
		tested = new CookieForwardingFilter();
	}

	@Override
	protected void tearDown() {
		tested = null;
	}

	public void testFilter() throws IOException, HttpErrorPage {

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setCookies(new Cookie[] { new Cookie(K_FORWARD1, V_FORWARD1),
				new Cookie(K_NO_FORWARD, V_NO_FORWARD),
				new Cookie(K_FORWARD2, V_FORWARD2) });

		MockHttpServletResponse response = new MockHttpServletResponse();
		Properties prop = new Properties();
		prop.setProperty(CookieForwardingFilter.PROP_ATTRIBUTE, K_FORWARD1
				+ "   , " + K_FORWARD2);
		String uri = "test/request.html";

		ResourceContext rc = new ResourceContext(new MockDriver("MockDriver"),
				uri, null, request, response);

		CookieForwardingFilter f = new CookieForwardingFilter();

		// Test init
		f.init(prop);

		List<String> cookieNames = new ArrayList<String>(f.getForwardCookies());
		Collections.sort(cookieNames);
		Assert.assertEquals(2, cookieNames.size());
		Assert.assertEquals(K_FORWARD1, cookieNames.get(0));
		Assert.assertEquals(K_FORWARD2, cookieNames.get(1));

		// Test pre-request.
		MockHttpClientRequest clientRequest = new MockHttpClientRequest(uri,
				request, true, true);
		f.preRequest(clientRequest, rc);

		Assert.assertTrue(clientRequest.getHeaders().containsKey("Cookie"));
		Assert.assertEquals(K_FORWARD1 + "=" + V_FORWARD1 + "; " + K_FORWARD2
				+ "=" + V_FORWARD2, clientRequest.getHeaders().get("Cookie"));

		// Test post Request
		SerializableBasicHttpContext context = new SerializableBasicHttpContext();
		MockHttpRequest httpRequest = new MockHttpRequest(uri);
		HttpHost httpHost = new HttpHost("localhost", 8080);
		MockHttpClient httpClient = new MockHttpClient();
		httpClient.setResponse(new BasicHttpResponse(new MockStatusLine()));
		httpClient.getResponse().addHeader("Set-cookie",
				K_FORWARD1 + "= " + V_FORWARD1);
		httpClient.getResponse().addHeader("Set-cookie",
				K_FORWARD2 + "= " + V_FORWARD2);
		httpClient.getResponse().addHeader("Set-cookie",
				K_NO_FORWARD + "=" + V_NO_FORWARD);

		context.setAttribute(ExecutionContext.HTTP_REQUEST, httpRequest);

		context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, httpHost);

		HttpClientResponse clientResponse = HttpClientResponse.create(httpHost, httpRequest, httpClient, context);
		f.postRequest(clientResponse, rc);

		@SuppressWarnings("unchecked")
		List<String> headers = response.getHeaders("Set-cookie");

		// Only one cookie can be returned currently.
		Assert.assertTrue(headers.get(0).startsWith(K_FORWARD1));
		Assert.assertTrue(headers.get(1).startsWith(K_FORWARD2));
	}

	public void testPreRequest() {
		tested.getForwardCookies().addAll(Arrays.asList("a", "b", "c"));
		MockHttpServletRequest originalRequest = new MockHttpServletRequest();
		originalRequest.setCookies(new Cookie[] { new Cookie("a", "value a"), new Cookie("c", "value c"),
				new Cookie("d", "value d") });

		HttpClientRequest request = new HttpClientRequest("url", null, false, false);
		ResourceContext context = new ResourceContext(null, null, null, originalRequest, null);
		tested.preRequest(request, context);
		assertNotNull(request.getHeaders());
		assertEquals(1, request.getHeaders().size());
		String cookie = request.getHeaders().get(HttpHeaders.COOKIE);
		assertNotNull(cookie);
		assertEquals("a=value a; c=value c", cookie);
	}

	public void testPostRequest() {
		MockHttpServletResponse originalResponse = new MockHttpServletResponse();

		tested.getForwardCookies().addAll(Arrays.asList("a", "b", "c"));
		ResourceContext context = new ResourceContext(null, null, null, null, originalResponse);
		HttpClientResponse response = new HttpClientResponse(0, null) {
			@Override
			public String[] getHeaders(String name) {
				assertEquals(HttpHeaders.SET_COOKIE, name);
				return new String[] { "a=value a", "c=value c", "d=value d" };
			}
		};
		tested.postRequest(response, context);
		assertNotNull(originalResponse.getHeaders(HttpHeaders.SET_COOKIE));
		assertEquals(2, originalResponse.getHeaders(HttpHeaders.SET_COOKIE).size());
		assertEquals("a=value a", originalResponse.getHeaders(HttpHeaders.SET_COOKIE).get(0));
		assertEquals("c=value c", originalResponse.getHeaders(HttpHeaders.SET_COOKIE).get(1));
	}

	public void testInit() {
		try {
			tested.init(new Properties());
			fail("should throw ConfigurationException");
		} catch (ConfigurationException e) {
			assertEquals("drivername.forwardCookies is empty : no cookie to forward.", e.getMessage());
		}

		Properties props = new Properties();
		props.setProperty(CookieForwardingFilter.PROP_ATTRIBUTE, "a,b,c");
		tested.init(props);
		assertNotNull(tested.getForwardCookies());
		assertEquals(3, tested.getForwardCookies().size());
		List<String> cookieNames = new ArrayList<String>(tested.getForwardCookies());
		Collections.sort(cookieNames);
		assertEquals("a", cookieNames.get(0));
		assertEquals("b", cookieNames.get(1));
		assertEquals("c", cookieNames.get(2));
	}

}

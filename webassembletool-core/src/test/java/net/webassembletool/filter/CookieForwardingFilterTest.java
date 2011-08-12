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
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.Cookie;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.webassembletool.HttpErrorPage;
import net.webassembletool.MockDriver;
import net.webassembletool.ResourceContext;
import net.webassembletool.http.HttpClientRequest;
import net.webassembletool.test.MockHttpClientRequest;

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
		prop.setProperty("forwardCookies", K_FORWARD1 + "   , " + K_FORWARD2);
		String uri = "test/request.html";

		Properties driverProperties = new Properties();
		driverProperties.setProperty("remoteUrlBase", "http://localhost:8080/");
		ResourceContext rc = new ResourceContext(new MockDriver("MockDriver",
				driverProperties), uri, null, request, response);

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

		assertNotNull(clientRequest.getCookieStore().getCookies());
		assertFalse(clientRequest.getCookieStore().getCookies().isEmpty());
		assertTrue(clientRequest.getCookieStore().getCookies().size() == 2);

	}

	public void testPreRequest() {
		Properties driverProperties = new Properties();
		driverProperties.setProperty("remoteUrlBase", "http://localhost:8080/");
		MockDriver provider = new MockDriver("mock", driverProperties);
		Properties props = new Properties();
		props.put("forwardCookies", "a,b,c");
		tested.init(props);
		MockHttpServletRequest originalRequest = new MockHttpServletRequest();
		originalRequest.setCookies(new Cookie[] { new Cookie("a", "value a"),
				new Cookie("c", "value c"), new Cookie("d", "value d") });

		HttpClientRequest request = new HttpClientRequest("url", null, false,
				false);
		ResourceContext context = new ResourceContext(provider, null, null,
				originalRequest, null);
		tested.preRequest(request, context);
		List<org.apache.http.cookie.Cookie> cookies = request.getCookieStore()
				.getCookies();
		assertNotNull(cookies);
		assertFalse(cookies.isEmpty());
		assertEquals(2, cookies.size());
		org.apache.http.cookie.Cookie cookie1 = cookies.get(0);
		org.apache.http.cookie.Cookie cookie2 = cookies.get(1);
		assertEquals("a", cookie1.getName());
		assertEquals("c", cookie2.getName());
	}

	public void testInit() {
		Properties props = new Properties();
		props.setProperty("forwardCookies", "a,b,c");
		tested.init(props);
		assertNotNull(tested.getForwardCookies());
		assertEquals(3, tested.getForwardCookies().size());
		List<String> cookieNames = new ArrayList<String>(
				tested.getForwardCookies());
		Collections.sort(cookieNames);
		assertEquals("a", cookieNames.get(0));
		assertEquals("b", cookieNames.get(1));
		assertEquals("c", cookieNames.get(2));
	}

}

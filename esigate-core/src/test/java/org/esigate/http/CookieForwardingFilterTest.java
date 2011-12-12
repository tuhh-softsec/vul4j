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
package org.esigate.http;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.http.client.CookieStore;
import org.easymock.EasyMock;
import org.esigate.HttpErrorPage;
import org.esigate.MockDriver;
import org.esigate.ResourceContext;
import org.esigate.api.HttpRequest;
import org.esigate.api.HttpResponse;
import org.esigate.cookie.BasicClientCookie;
import org.esigate.test.MockHttpClientRequest;

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

	public void testFilter() throws IOException, HttpErrorPage, NoSuchFieldException, IllegalAccessException {

		HttpRequest request = EasyMock
				.createNiceMock(HttpRequest.class);
		EasyMock.expect(request.getCookies()).andReturn(
				new BasicClientCookie[] { new BasicClientCookie(K_FORWARD1, V_FORWARD1),
						new BasicClientCookie(K_NO_FORWARD, V_NO_FORWARD),
						new BasicClientCookie(K_FORWARD2, V_FORWARD2) });
		EasyMock.replay(request);

		HttpResponse response = EasyMock.createNiceMock(HttpResponse.class);
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

		Field cookieStoreField = HttpClientRequest.class.getDeclaredField("cookieStore");
		cookieStoreField.setAccessible(true);
		CookieStore cookieStore = CookieStore.class.cast(cookieStoreField.get(clientRequest));
		
		List<org.apache.http.cookie.Cookie> cookies = cookieStore.getCookies();
		assertNotNull(cookies);
		assertFalse(cookies.isEmpty());
		assertTrue(cookies.size() == 2);

	}

	public void testPreRequest() throws NoSuchFieldException, IllegalAccessException {
		Properties driverProperties = new Properties();
		driverProperties.setProperty("remoteUrlBase", "http://localhost:8080/");
		MockDriver provider = new MockDriver("mock", driverProperties);
		Properties props = new Properties();
		props.put("forwardCookies", "a,b,c");
		tested.init(props);
		HttpRequest originalRequest = EasyMock
				.createNiceMock(HttpRequest.class);
		EasyMock.expect(originalRequest.getCookies())
				.andReturn(
						new BasicClientCookie[] { new BasicClientCookie("a", "value a"),
								new BasicClientCookie("c", "value c"),
								new BasicClientCookie("d", "value d") });
		EasyMock.replay(originalRequest);

		HttpClientRequest request = new HttpClientRequest("url", null, false,
				false);
		ResourceContext context = new ResourceContext(provider, null, null,
				originalRequest, null);
		tested.preRequest(request, context);

		Field cookieStoreField = HttpClientRequest.class.getDeclaredField("cookieStore");
		cookieStoreField.setAccessible(true);
		CookieStore cookieStore = CookieStore.class.cast(cookieStoreField.get(request));
		List<org.apache.http.cookie.Cookie> cookies = cookieStore.getCookies();
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

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

package org.esigate.cookie;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.esigate.Driver;
import org.esigate.HttpErrorPage;
import org.esigate.MockDriver;
import org.esigate.Parameters;
import org.esigate.test.TestUtils;

public class DefaultCookieManagerTest extends TestCase {
	private DefaultCookieManager cookieManager;
	private HttpEntityEnclosingRequest request;

	@Override
	protected void setUp() throws HttpErrorPage {
		Properties properties = new Properties();
		properties.setProperty(Parameters.FORWARD_COOKIES.name, "a, c");
		properties.setProperty(Parameters.DISCARD_COOKIES.name, "D,e");
		cookieManager = new DefaultCookieManager();
		cookieManager.init(null, properties);
		request = TestUtils.createRequest();
		Driver driver = MockDriver.createDriver();
		driver.initHttpRequestParams(request, null);
	}

	@Override
	protected void tearDown() {
		cookieManager = null;
	}

	public void testAddCookie() {
		assertNotNull(cookieManager.getCookies(request));
		assertEquals(0, cookieManager.getCookies(request).size());
		cookieManager.addCookie(new org.apache.http.impl.cookie.BasicClientCookie("b", "value"), request);
		assertNotNull(cookieManager.getCookies(request));
		assertEquals(1, cookieManager.getCookies(request).size());
		org.apache.http.cookie.Cookie cookie = cookieManager.getCookies(request).get(0);
		assertNotNull(cookie);
		assertEquals("b", cookie.getName());
		assertEquals("value", cookie.getValue());
		assertEquals(org.apache.http.impl.cookie.BasicClientCookie.class, cookie.getClass());
	}

	public void testInit() {
		assertNotNull(cookieManager.forwardCookies);
		assertEquals(2, cookieManager.forwardCookies.size());
		List<String> cookieNames = new ArrayList<String>(cookieManager.forwardCookies);
		Collections.sort(cookieNames);
		assertEquals("a", cookieNames.get(0));
		assertEquals("c", cookieNames.get(1));
	}

	public void testFilter() throws IOException, HttpErrorPage, NoSuchFieldException, IllegalAccessException {
		TestUtils.addCookie(new BasicClientCookie("a", "value a"), request);
		TestUtils.addCookie(new BasicClientCookie("b", "value b"), request);
		TestUtils.addCookie(new BasicClientCookie("c", "value c"), request);
		List<org.apache.http.cookie.Cookie> cookies = cookieManager.getCookies(request);
		assertNotNull(cookies);
		assertFalse(cookies.isEmpty());
		assertTrue(cookies.size() == 2);
		assertEquals("a", cookies.get(0).getName());
		assertEquals("c", cookies.get(1).getName());
	}

	public void testRewriteDomain() {
		assertEquals(null, DefaultCookieManager.rewriteDomain("provider1", "provider1", "www.foo.com"));
		assertEquals(".foo.com", DefaultCookieManager.rewriteDomain(".provider1.net", "www.provider1.net", "www.foo.com"));
		assertEquals(".subdomain.foo.com", DefaultCookieManager.rewriteDomain(".subdomain1.provider1.net", "www.subdomain1.provider1.net", "www.subdomain.foo.com"));
		assertEquals(".foo.com", DefaultCookieManager.rewriteDomain(".provider1.net", "www.provider1.net", "www.subdomain.foo.com"));
		assertEquals(null, DefaultCookieManager.rewriteDomain("www.provider1.net", "www.provider1.net", "www.foo.com"));
		assertEquals(null, DefaultCookieManager.rewriteDomain("localhost", "localhost", "www.foo.com"));
		assertEquals(null, DefaultCookieManager.rewriteDomain("127.0.0.1", "127.0.0.1", "www.foo.com"));
		assertEquals(null, DefaultCookieManager.rewriteDomain("provider1.subdomain.net", "www.provider1.subdomain.net", "www.foo.com"));
	}

	/**
	 * 0000232: Cookie management : ESIGate should replace in user's cookie
	 * store new cookies received instead of always adding them
	 * 
	 * @see "http://sourceforge.net/apps/mantisbt/webassembletool/view.php?id=232"
	 * 
	 */
	public void testReplace() {
		assertEquals(0, cookieManager.getCookies(request).size());

		// Add cookie
		cookieManager.addCookie(new BasicClientCookie("b", "value1"), request);
		assertEquals(1, cookieManager.getCookies(request).size());

		// Replace cookie value
		cookieManager.addCookie(new BasicClientCookie("b", "value2"), request);

		// Ensure cookie is replaced
		assertEquals(1, cookieManager.getCookies(request).size());
		assertEquals("value2", cookieManager.getCookies(request).get(0).getValue());

	}

}

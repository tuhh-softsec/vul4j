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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.esigate.HttpErrorPage;
import org.esigate.MockDriver;
import org.esigate.Parameters;
import org.esigate.ResourceContext;
import org.esigate.api.HttpResponse;
import org.esigate.cookie.BasicClientCookie;
import org.esigate.test.MockHttpRequest;
import org.esigate.test.MockHttpResponse;

/**
 * @author Nicolas Richeton
 * 
 */
public class CookieForwardingFilterTest extends TestCase {

	public void testInit() {
		Properties properties = new Properties();
		properties.setProperty(Parameters.FORWARD_COOKIES.name, "a,b,c");
		CookieForwardingFilter cookieForwardingFilter = new CookieForwardingFilter();
		cookieForwardingFilter.init(properties);
		assertNotNull(cookieForwardingFilter.getForwardCookies());
		assertEquals(3, cookieForwardingFilter.getForwardCookies().size());
		List<String> cookieNames = new ArrayList<String>(cookieForwardingFilter.getForwardCookies());
		Collections.sort(cookieNames);
		assertEquals("a", cookieNames.get(0));
		assertEquals("b", cookieNames.get(1));
		assertEquals("c", cookieNames.get(2));
	}

	public void testFilter() throws IOException, HttpErrorPage, NoSuchFieldException, IllegalAccessException {

		MockHttpRequest request = new MockHttpRequest();
		request.addCookie(new BasicClientCookie("a", "value a"));
		request.addCookie(new BasicClientCookie("b", "value b"));
		request.addCookie(new BasicClientCookie("c", "value c"));

		HttpResponse response = new MockHttpResponse();

		Properties properties = new Properties();
		properties.setProperty(Parameters.REMOTE_URL_BASE.name, "http://localhost:8080/");
		properties.setProperty(Parameters.FORWARD_COOKIES.name, "a, c");

		String uri = "test/request.html";

		ResourceContext resourceContext = new ResourceContext(new MockDriver("MockDriver", properties), uri, null, request, response);

		CookieForwardingFilter cookieForwardingFilter = new CookieForwardingFilter();
		cookieForwardingFilter.init(properties);

		List<String> cookieNames = new ArrayList<String>(cookieForwardingFilter.getForwardCookies());
		Collections.sort(cookieNames);
		Assert.assertEquals(2, cookieNames.size());
		Assert.assertEquals("a", cookieNames.get(0));
		Assert.assertEquals("c", cookieNames.get(1));

		MockHttpClient httpClient = new MockHttpClient();
		HttpClientHelper httpClientHelper = new HttpClientHelper();
		httpClientHelper.init(httpClient, properties);

		GenericHttpRequest httpRequest = httpClientHelper.createHttpRequest(request, "http://localhost:8080", true);
		HttpContext httpContext = new BasicHttpContext();

		cookieForwardingFilter.preRequest(httpRequest, httpContext, resourceContext);

		CookieStore cookieStore = (CookieStore) httpContext.getAttribute(ClientContext.COOKIE_STORE);
		List<org.apache.http.cookie.Cookie> cookies = cookieStore.getCookies();
		assertNotNull(cookies);
		assertFalse(cookies.isEmpty());
		assertTrue(cookies.size() == 2);
		assertEquals("a", cookies.get(0).getName());
		assertEquals("c", cookies.get(1).getName());
	}

}

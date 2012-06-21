package org.esigate.cookie;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.esigate.Driver;
import org.esigate.HttpErrorPage;
import org.esigate.MockDriver;
import org.esigate.Parameters;
import org.esigate.ResourceContext;
import org.esigate.test.MockHttpRequest;
import org.esigate.test.MockHttpResponse;

public class DefaultCookieManagerTest extends TestCase {
	private DefaultCookieManager cookieManager;
	private MockHttpRequest httpRequest;
	private MockHttpResponse httpResponse;
	private ResourceContext resourceContext;

	protected void setUp() {
		Properties properties = new Properties();
		properties.setProperty(Parameters.FORWARD_COOKIES.name, "a, c");
		properties.setProperty(Parameters.DISCARD_COOKIES.name, "D,e");
		cookieManager = new DefaultCookieManager();
		cookieManager.init(properties);
		httpResponse = new MockHttpResponse();
		httpRequest = new MockHttpRequest();
		Driver driver = new MockDriver();
		resourceContext = new ResourceContext(driver, "/", null, httpRequest, httpResponse);
	}

	protected void tearDown() {
		cookieManager = null;
	}

	public void testAddCookie() {
		assertNotNull(cookieManager.getCookies(resourceContext));
		assertEquals(0, cookieManager.getCookies(resourceContext).size());
		cookieManager.addCookie(new org.apache.http.impl.cookie.BasicClientCookie("b", "value"), resourceContext);
		assertNotNull(cookieManager.getCookies(resourceContext));
		assertEquals(1, cookieManager.getCookies(resourceContext).size());
		org.apache.http.cookie.Cookie cookie = cookieManager.getCookies(resourceContext).get(0);
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
		httpRequest.addCookie(new BasicClientCookie("a", "value a"));
		httpRequest.addCookie(new BasicClientCookie("b", "value b"));
		httpRequest.addCookie(new BasicClientCookie("c", "value c"));
		List<org.apache.http.cookie.Cookie> cookies = cookieManager.getCookies(resourceContext);
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

}

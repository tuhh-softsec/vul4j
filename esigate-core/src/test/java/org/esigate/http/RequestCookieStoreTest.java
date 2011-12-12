package org.esigate.http;

import org.esigate.http.RequestCookieStore;

import junit.framework.TestCase;

public class RequestCookieStoreTest extends TestCase {
	public void testRewriteDomain() {
		assertEquals(null, RequestCookieStore.rewriteDomain("provider1",
				"provider1", "www.foo.com"));
		assertEquals(".foo.com", RequestCookieStore.rewriteDomain(
				".provider1.net", "www.provider1.net", "www.foo.com"));
		assertEquals(".subdomain.foo.com", RequestCookieStore.rewriteDomain(
				".subdomain1.provider1.net", "www.subdomain1.provider1.net",
				"www.subdomain.foo.com"));
		assertEquals(".foo.com", RequestCookieStore.rewriteDomain(
				".provider1.net", "www.provider1.net", "www.subdomain.foo.com"));
		assertEquals(null, RequestCookieStore.rewriteDomain(
				"www.provider1.net", "www.provider1.net", "www.foo.com"));
		assertEquals(null, RequestCookieStore.rewriteDomain("localhost",
				"localhost", "www.foo.com"));
		assertEquals(null, RequestCookieStore.rewriteDomain("127.0.0.1",
				"127.0.0.1", "www.foo.com"));
		assertEquals(null, RequestCookieStore.rewriteDomain(
				"provider1.subdomain.net", "www.provider1.subdomain.net",
				"www.foo.com"));
	}

}

package org.esigate.cookie;

import junit.framework.TestCase;

import org.esigate.api.Cookie;

public class SerializableBasicCookieStoreTest extends TestCase {
	private SerializableBasicCookieStore tested;

	protected void setUp() {
		tested = new SerializableBasicCookieStore();
	}

	protected void tearDown() {
		tested = null;
	}

	public void testAddCookie() {
		assertNotNull(tested.getCookies());
		assertEquals(0, tested.getCookies().size());

		tested.addCookie(new BasicClientCookie("a", "value"));
		assertNotNull(tested.getCookies());
		assertEquals(1, tested.getCookies().size());
		Cookie cookie = tested.getCookies().get(0);
		assertNotNull(cookie);
		assertEquals("a", cookie.getName());
		assertEquals("value", cookie.getValue());
		assertEquals(BasicClientCookie.class, cookie.getClass());
	}

}

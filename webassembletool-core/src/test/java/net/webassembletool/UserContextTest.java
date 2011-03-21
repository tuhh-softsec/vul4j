package net.webassembletool;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import net.webassembletool.cookie.CustomCookieStore;
import net.webassembletool.http.SerializableBasicHttpContext;

import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.protocol.HttpContext;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

public class UserContextTest extends TestCase {
	private UserContext tested;
	private IMocksControl control;
	private CustomCookieStore cookieStore;

	protected void setUp() {
		control = EasyMock.createControl();
		cookieStore = control.createMock(CustomCookieStore.class);

		tested = new UserContext(cookieStore);
	}

	protected void tearDown() {
		tested = null;

		control = null;
		cookieStore = null;
	}

	public void testCreation() {
		try {
			new UserContext(null);
			fail("should throw IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertEquals("cookieStore implementation not set", e.getMessage());
		}
	}

	public void testGetCookies() {
		Cookie cookie = control.createMock(Cookie.class);

		EasyMock.expect(cookieStore.getCookies()).andReturn(Arrays.asList(cookie));
		control.replay();

		List<Cookie> actual = tested.getCookies();
		assertNotNull(actual);
		assertEquals(1, actual.size());
		assertSame(cookie, actual.get(0));
		control.verify();
	}

	public void testGetHttpContext() {
		HttpContext actual = tested.getHttpContext();
		assertNotNull(actual);
		assertEquals(SerializableBasicHttpContext.class, actual.getClass());
		assertNotNull(actual.getAttribute(ClientContext.COOKIE_STORE));
		assertSame(cookieStore, actual.getAttribute(ClientContext.COOKIE_STORE));
	}

}

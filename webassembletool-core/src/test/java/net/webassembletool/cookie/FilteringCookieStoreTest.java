package net.webassembletool.cookie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.http.impl.cookie.BasicClientCookie;

public class FilteringCookieStoreTest extends TestCase {
	private FilteringCookieStore tested;

	@Override
	protected void setUp() {
		tested = new FilteringCookieStore();
	}

	@Override
	protected void tearDown() {
		tested = null;
	}

	public void testAddCookie() {
		tested.getForwardCookies().addAll(Arrays.asList("a", "b"));

		assertNotNull(tested.getCookies());
		assertEquals(0, tested.getCookies().size());

		tested.addCookie(new BasicClientCookie("a", "value"));
		tested.cleanUpCookies();
		assertNotNull(tested.getCookies());
		assertEquals(0, tested.getCookies().size());

		tested.addCookie(new BasicClientCookie("c", "value"));
		tested.cleanUpCookies();
		assertNotNull(tested.getCookies());
		assertEquals(1, tested.getCookies().size());
		assertEquals("c", tested.getCookies().get(0).getName());
		assertEquals("value", tested.getCookies().get(0).getValue());
	}

	public void testInit() {
		Properties properties = new Properties();
		properties.setProperty("discardCookies", "a,b,c");

		assertNotNull(tested.getForwardCookies());
		assertEquals(0, tested.getForwardCookies().size());

		tested.init(properties);
		assertNotNull(tested.getForwardCookies());
		assertEquals(3, tested.getForwardCookies().size());
		List<String> cookies = new ArrayList<String>(tested.getForwardCookies());
		Collections.sort(cookies);
		assertEquals("a", cookies.get(0));
		assertEquals("b", cookies.get(1));
		assertEquals("c", cookies.get(2));
	}

}

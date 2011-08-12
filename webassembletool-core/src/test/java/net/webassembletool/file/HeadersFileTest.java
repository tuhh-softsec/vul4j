package net.webassembletool.file;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;

public class HeadersFileTest extends TestCase {
	private HeadersFile tested;

	@Override
	protected void setUp() {
		Map<String, Set<String>> headers = new HashMap<String, Set<String>>();
		headers.put("single", Collections.singleton("header value"));
		Set<String> multiValuedHeader = new TreeSet<String>();
		multiValuedHeader.add("first");
		multiValuedHeader.add("second");
		headers.put("multiple", multiValuedHeader);
		tested = new HeadersFile(headers, -1, null);
	}

	@Override
	protected void tearDown() {
		tested = null;
	}

	public void testAddHeader() {
		assertFalse("no header named 'header'", tested.getHeadersMap()
				.containsKey("header"));

		tested.addHeader("header", "value");
		assertTrue(tested.getHeadersMap().containsKey("header"));
		assertEquals(1, tested.getHeadersMap().get("header").size());
		assertTrue(tested.getHeadersMap().get("header").contains("value"));
		assertFalse(tested.getHeadersMap().get("header").contains("another"));

		tested.addHeader("HeAdEr", "another");
		assertTrue(tested.getHeadersMap().containsKey("header"));
		assertEquals(2, tested.getHeadersMap().get("header").size());
		assertTrue(tested.getHeadersMap().get("header").contains("value"));
		assertTrue(tested.getHeadersMap().get("header").contains("another"));
	}

	public void testGetHeader() {
		assertNull(tested.getHeader("missing"));
		assertEquals("header value", tested.getHeader("single".toUpperCase()));
		assertEquals("first", tested.getHeader("MuLtIpLe"));
	}

	public void testGetHeaders() {
		Collection<String> actual = tested.getHeaders("missing");
		assertNotNull(actual);
		assertEquals(0, actual.size());

		actual = tested.getHeaders("single".toUpperCase());
		assertNotNull(actual);
		assertEquals(1, actual.size());
		assertTrue(actual.contains("header value"));

		actual = tested.getHeaders("MuLtIpLe");
		assertNotNull(actual);
		assertEquals(2, actual.size());
		assertTrue(actual.contains("first"));
		assertTrue(actual.contains("second"));
	}

}

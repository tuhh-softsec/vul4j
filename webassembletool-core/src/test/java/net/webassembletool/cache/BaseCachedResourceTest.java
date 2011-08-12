package net.webassembletool.cache;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;
import net.webassembletool.output.Output;

public class BaseCachedResourceTest extends TestCase {

	public void testGetStatusCode() {
		BaseCachedResource tested = new MockBaseCachedResource(null, 123, null);
		assertEquals(123, tested.getStatusCode());
	}

	public void testGetStatusMessage() {
		BaseCachedResource tested = new MockBaseCachedResource(null, -1,
				"expected");
		assertEquals("expected", tested.getStatusMessage());
	}

	public void testGetHeader() {
		Map<String, Set<String>> headers = new HashMap<String, Set<String>>();
		Set<String> values = new TreeSet<String>();
		values.add("header value");
		headers.put("some", Collections.singleton("some value"));

		BaseCachedResource tested = new MockBaseCachedResource(headers, -1,
				null);

		assertNull(tested.getHeader("header"));

		headers.put("header", values);
		assertEquals("header value", tested.getHeader("header"));
		assertEquals("header value", tested.getHeader("header".toUpperCase()));
		assertEquals("header value", tested.getHeader("HeAder"));

		values.add("next value");
		assertEquals("header value", tested.getHeader("header"));
	}

	public void testGetHeaders() {
		Map<String, Set<String>> headers = new HashMap<String, Set<String>>();
		Set<String> values = new TreeSet<String>();
		values.add("header value");
		headers.put("some", Collections.singleton("some value"));

		BaseCachedResource tested = new MockBaseCachedResource(headers, -1,
				null);

		assertEquals(0, tested.getHeaders("header").size());

		headers.put("header", values);
		Collection<String> actual = tested.getHeaders("header");
		assertNotNull(actual);
		assertEquals(Collections.singleton("header value"), actual);
		assertEquals(Collections.singleton("header value"),
				tested.getHeaders("header".toUpperCase()));
		assertEquals(Collections.singleton("header value"),
				tested.getHeaders("HeAder"));

		values.add("next value");
		actual = tested.getHeaders("header");
		assertEquals(2, actual.size());
		assertTrue(actual.contains("header value"));
		assertTrue(actual.contains("next value"));
	}

	public void testGetHeaderNames() {
		Map<String, Set<String>> headers = new HashMap<String, Set<String>>();
		headers.put("header-01", null);
		headers.put("header-02", null);

		BaseCachedResource tested = new MockBaseCachedResource(headers, -1,
				null);

		Collection<String> actual = tested.getHeaderNames();
		assertNotNull(actual);
		assertEquals(2, actual.size());
		assertTrue(actual.contains("header-01"));
		assertTrue(actual.contains("header-02"));
	}

	public void testAddHeader() {
		Map<String, Set<String>> headers = new HashMap<String, Set<String>>();
		BaseCachedResource tested = new MockBaseCachedResource(headers, -1,
				null);

		assertTrue(headers.isEmpty());

		tested.addHeader("header", "value");
		assertEquals(1, headers.size());
		assertEquals(1, headers.get("header").size());
		assertTrue(headers.get("header").contains("value"));
		assertFalse(headers.get("header").contains("another"));

		tested.addHeader("HeAdEr", "another");
		assertEquals(1, headers.size());
		assertEquals(2, headers.get("header").size());
		assertTrue(headers.get("header").contains("value"));
		assertTrue(headers.get("header").contains("another"));
	}

	private static class MockBaseCachedResource extends BaseCachedResource {

		public MockBaseCachedResource(Map<String, Set<String>> headers,
				int statusCode, String statusMessage) {
			super(headers, statusCode, statusMessage);
		}

		@Override
		public void render(Output output) throws IOException {
			throw new IllegalStateException("NYI");
		}

		@Override
		public void release() {
			throw new IllegalStateException("NYI");
		}

	}
}

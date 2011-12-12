package org.esigate.esi;

import junit.framework.TestCase;

import org.esigate.ResourceContext;
import org.esigate.parser.Element;
import org.esigate.parser.ParserContext;

public class BaseElementTest extends TestCase {
	private BaseElement tested;

	@Override
	public void setUp() {
		tested = new MockBaseElement();
	}

	@Override
	public void tearDown() {
		tested = null;
	}

	public void testOnTagStart() throws Exception {
		final Tag parsed[] = new Tag[1];
		tested = new MockBaseElement() {
			@Override
			protected void parseTag(Tag tag, ParserContext ctx) {
				parsed[0] = tag;
			}
		};
		ParserContext ctx = new MockParserContext();

		tested.onTagStart("<do:something />", ctx);
		assertEquals(true, tested.isClosed());

		assertNotNull(parsed[0]);
		assertEquals(true, parsed[0].isOpenClosed());
		assertEquals(false, parsed[0].isClosing());
		assertEquals("do:something", parsed[0].getName());

		tested.onTagStart("<do:something>", ctx);
		assertEquals(false, tested.isClosed());

		assertNotNull(parsed[0]);
		assertEquals(false, parsed[0].isOpenClosed());
		assertEquals(false, parsed[0].isClosing());
		assertEquals("do:something", parsed[0].getName());

		tested.onTagStart("<do:something name='value'>", ctx);
		assertEquals(false, tested.isClosed());

		assertNotNull(parsed[0]);
		assertEquals(false, parsed[0].isOpenClosed());
		assertEquals(false, parsed[0].isClosing());
		assertEquals("do:something", parsed[0].getName());
		assertEquals("value", parsed[0].getAttribute("name"));
	}

	private static class MockBaseElement extends BaseElement {
		public MockBaseElement() { }

	}

	private static class MockParserContext implements ParserContext {
		public ResourceContext getResourceContext() { return null; }

		public boolean reportError(Exception e) { return false; }

		public Element getCurrent() { return null; }

		public <T> T findAncestor(Class<T> type) { return null; }
	}
}

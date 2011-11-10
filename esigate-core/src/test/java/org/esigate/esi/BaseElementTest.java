package org.esigate.esi;

import java.io.IOException;

import junit.framework.TestCase;

import org.esigate.HttpErrorPage;
import org.esigate.parser.Element;
import org.esigate.parser.ElementStack;
import org.esigate.parser.ElementType;

public class BaseElementTest extends TestCase {
	private BaseElement tested;

	@Override
	public void setUp() {
		tested = new MockBaseElement(null);
	}

	@Override
	public void tearDown() {
		tested = null;
	}

	public void testAppend() throws IOException {
		assertSame(tested, tested.append('c'));
		
		assertSame(tested, tested.append(null));

		assertSame(tested, tested.append(null, -1, 255));
	}

	public void testGetType() {
		ElementType elementType = new BaseElementType(null, null) {
			public Element newInstance() {
				return null;
			}
		};
		tested = new MockBaseElement(elementType);

		assertSame(elementType, tested.getType());
	}

	public void testDoStartTag() throws IOException, HttpErrorPage {
		final Tag parsed[] = new Tag[1];
		tested = new MockBaseElement(null) {
			@Override
			protected void parseTag(Tag tag, Appendable parent, ElementStack stack) {
				parsed[0] = tag;
			}
		};

		tested.doStartTag("<do:something />", null, null);
		assertEquals(true, tested.isClosed());

		assertNotNull(parsed[0]);
		assertEquals(true, parsed[0].isOpenClosed());
		assertEquals("do:something", parsed[0].getName());
	}

	private static class MockBaseElement extends BaseElement {
		protected MockBaseElement(ElementType type) {
			super(type);
		}

	}
}

package org.esigate.esi;

import junit.framework.TestCase;

import org.esigate.parser.Element;
import org.esigate.parser.ElementType;

public class BaseElementTypeTest extends TestCase {
    private ElementType tested;

    @Override
    public void setUp() {
        tested = new MockBaseElementType("startStarted", "endStarted");
    }

    @Override
    public void tearDown() {
        tested = null;
    }

    public void testIsStartTag() {
        assertTrue(tested.isStartTag("startStarted somewhere here"));

        assertFalse(tested.isStartTag("start somewhere here"));
        assertFalse(tested.isStartTag("<startStarted somewhere here"));
    }

    public void testIsEndTag() {
        assertTrue(tested.isEndTag("endStarted somewhere here"));

        assertFalse(tested.isEndTag("end somewhere here"));
        assertFalse(tested.isEndTag("</endStarted somewhere here"));
    }

    private static class MockBaseElementType extends BaseElementType {

        protected MockBaseElementType(String startTag, String endTag) {
            super(startTag, endTag);
        }

        @Override
        public Element newInstance() {
            return null;
        }

    }
}

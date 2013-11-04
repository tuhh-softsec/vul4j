package org.esigate.extension.parallelesi;

import junit.framework.TestCase;

import org.esigate.parser.future.FutureElement;
import org.esigate.parser.future.FutureElementType;

public class BaseElementTypeTest extends TestCase {
    private FutureElementType tested;

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
        public FutureElement newInstance() {
            return null;
        }

    }
}

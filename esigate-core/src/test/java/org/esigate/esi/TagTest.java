package org.esigate.esi;

import junit.framework.TestCase;

public class TagTest extends TestCase {

    public void testCreate() {
        Tag tag = Tag.create("<esi:include toto='titi' hello =  \"World\"/>");
        assertEquals("esi:include", tag.getName());
        assertEquals("titi", tag.getAttribute("toto"));
        assertEquals("World", tag.getAttribute("hello"));
        assertFalse(tag.isClosing());
        assertTrue(tag.isOpenClosed());

        tag = Tag.create("<esi:include toto='titi' hello =  \"World\">");
        assertEquals("esi:include", tag.getName());
        assertEquals("titi", tag.getAttribute("toto"));
        assertEquals("World", tag.getAttribute("hello"));
        assertFalse(tag.isClosing());
        assertFalse(tag.isOpenClosed());

        tag = Tag.create("<esi:include>");
        assertEquals("esi:include", tag.getName());
        assertFalse(tag.isClosing());
        assertFalse(tag.isOpenClosed());

        tag = Tag.create("</esi:include>");
        assertEquals("esi:include", tag.getName());
        assertTrue(tag.isClosing());
        assertFalse(tag.isOpenClosed());

        try {
            Tag.create("<!--esi");
            fail("should fail with IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    public void testCreateWithQuotesInside() {
        Tag tag = Tag.create("<esi:include toto='titi \"World\"' hello =  \"World 'inside'\"/>");
        assertEquals("esi:include", tag.getName());
        assertEquals("titi \"World\"", tag.getAttribute("toto"));
        assertEquals("World 'inside'", tag.getAttribute("hello"));
        assertFalse(tag.isClosing());
        assertTrue(tag.isOpenClosed());
    }

    public void testCreateInvalidAttributesMarkup() {
        Tag tag = Tag.create("<esi:include toto='titi\" hello =  \"World\"/>");
        assertEquals("esi:include", tag.getName());
        assertFalse(tag.isClosing());
        assertTrue(tag.isOpenClosed());
        assertNull(tag.getAttribute("toto"));
        assertEquals("World", tag.getAttribute("hello"));
    }
}

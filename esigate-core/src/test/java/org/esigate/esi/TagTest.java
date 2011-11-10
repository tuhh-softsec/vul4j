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

		tag = Tag.create("</esi:include>");
		assertNull(tag.getName());
		assertTrue(tag.isClosing());
		assertFalse(tag.isOpenClosed());
	}
}

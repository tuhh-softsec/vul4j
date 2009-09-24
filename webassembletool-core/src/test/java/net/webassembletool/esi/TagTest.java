package net.webassembletool.esi;

import junit.framework.TestCase;

public class TagTest extends TestCase {
	public void testName() {
		Tag tag = new Tag("<esi:include toto='titi' hello =  \"world\"/>");
		assertEquals("esi:include", tag.getName());
	}

	public void testAttributes() {
		Tag tag = new Tag("<esi:include toto='titi' hello =  \"World\"/>");
		assertEquals(2, tag.getAttributes().size());
		assertEquals("titi", tag.getAttributes().get("toto"));
		assertEquals("World", tag.getAttributes().get("hello"));
	}

	public void testClosing() {
		Tag tag = new Tag("</esi:include>");
		assertTrue(tag.isClosing());
	}

	public void testOpenClose() {
		Tag tag = new Tag("<esi:include toto='titi' hello =  \"world\" />");
		assertTrue(tag.isOpenClosed());
	}

}

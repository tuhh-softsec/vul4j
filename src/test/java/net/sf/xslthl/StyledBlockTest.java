/*
 * xslthl - XSLT Syntax Highlighting
 * https://sourceforge.net/projects/xslthl/
 * Copyright (C) 2005-2008 Michal Molhanec, Jirka Kosek, Michiel Hendriks
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty.  In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 * 
 * Michal Molhanec <mol1111 at users.sourceforge.net>
 * Jirka Kosek <kosek at users.sourceforge.net>
 * Michiel Hendriks <elmuerte at users.sourceforge.net>
 */
package net.sf.xslthl;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sf.xslthl.highlighters.XMLHighlighter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Michiel Hendriks
 */
public class StyledBlockTest {

	static final String BLOCK_CONTENT = "Foo bar quux.";
	static final String STYLE = "FooBarQuux";
	StyledBlock block;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		block = new StyledBlock(BLOCK_CONTENT, STYLE);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		block = null;
	}

	/**
	 * Test method for {@link net.sf.xslthl.StyledBlock#getText()}.
	 */
	@Test
	public void testGetText() {
		assertEquals(BLOCK_CONTENT, block.getText());
	}

	/**
	 * Test method for {@link net.sf.xslthl.StyledBlock#toString()}.
	 */
	@Test
	public void testToString() {
		assertEquals("<" + STYLE + ">" + BLOCK_CONTENT + "</" + STYLE + ">",
		        block.toString());
		block = new StyledBlock(BLOCK_CONTENT, StyledBlock.NO_STYLE);
		assertEquals(BLOCK_CONTENT, block.toString());
		block = new StyledBlock(BLOCK_CONTENT, StyledBlock.HIDDEN_STYLE);
		assertEquals("", block.toString());
	}

	/**
	 * Test method for {@link net.sf.xslthl.StyledBlock#isStyled()}.
	 */
	@Test
	public void testIsStyled() {
		assertTrue(block.isStyled());
		block = new StyledBlock(BLOCK_CONTENT, StyledBlock.NO_STYLE);
		assertFalse(block.isStyled());
		block = new StyledBlock(BLOCK_CONTENT, StyledBlock.HIDDEN_STYLE);
		assertFalse(block.isStyled());
		block = new StyledBlock(BLOCK_CONTENT, null);
		assertFalse(block.isStyled());
	}

	/**
	 * Test method for
	 * {@link net.sf.xslthl.StyledBlock#StyledBlock(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testStyledBlock() {
		new StyledBlock(null, null);
		new StyledBlock("foo", null);
		new StyledBlock(null, "foo");
	}

	/**
	 * Test method for {@link net.sf.xslthl.StyledBlock#getStyle()}.
	 */
	@Test
	public void testGetStyle() {
		assertEquals(STYLE, block.getStyle());
		block = new StyledBlock(BLOCK_CONTENT, StyledBlock.NO_STYLE);
		assertEquals(StyledBlock.NO_STYLE, block.getStyle());
		block = new StyledBlock(BLOCK_CONTENT, StyledBlock.HIDDEN_STYLE);
		assertEquals(StyledBlock.HIDDEN_STYLE, block.getStyle());
		block = new StyledBlock(BLOCK_CONTENT, null);
		assertNull(block.getStyle());
	}

	/**
	 * Guard against cases in which the tag is not properly finished.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testXMLHighlighter() throws Exception {
		XMLHighlighter hl = new XMLHighlighter();
		ArrayList<Block> blocks = new ArrayList<Block>();
		hl.highlight(new CharIter(
		        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
		                + "        <!DOCTYPE x:root [\n"
		                + "            <!ENTITY ent \"entity\">\n"
		                + "        ]>\n" + "\n" + "        <x:root"), blocks);
		assertEquals(
		        "[, <directive><?xml version=\"1.0\" encoding=\"UTF-8\"?></directive>, \n"
		                + "        , <doctype><!DOCTYPE x:root [\n"
		                + "            <!ENTITY ent \"entity\">\n"
		                + "        ]></doctype>, \n" + "\n"
		                + "        , <tag><x:root</tag>]", blocks.toString());
	}

	/**
	 * Guard against cases in which the CSS tokenizer is fed XML content.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCSSHighlighterWithXML() throws Exception {
		Config c = Config
		        .getInstance(new File("highlighters/xslthl-config.xml").toURI()
		                .toString());
		MainHighlighter hl = c.getMainHighlighter("css");
		List<Block> blocks = new ArrayList<Block>();
		blocks = hl.highlight("<p padding-left=\"20\">....");
		assertEquals(
		        "[<p , <keyword>padding-left</keyword>, =, <string>\"20\"</string>, >....]",
		        blocks.toString());
	}
	
	/**
	 * Test for the JSON highlighter.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testJSONHighlighter() throws Exception {
		Config c = Config
		        .getInstance(new File("highlighters/xslthl-config.xml").toURI()
		                .toString());
		MainHighlighter hl = c.getMainHighlighter("json");
		List<Block> blocks = new ArrayList<Block>();
		blocks = hl.highlight("{\"personnel\": {\"person\": [\n" + 
				"    {\n" + 
				"        \"id\": \"Big.Boss\"");
		assertEquals(
		        "[{, <json_key>\"personnel\"</json_key>, : {, <json_key>\"person\"</json_key>, : [\n" + 
		        "    {\n" + 
		        "        , <json_key>\"id\"</json_key>, : , <string>\"Big.Boss\"</string>]",
		        blocks.toString());
	}

}

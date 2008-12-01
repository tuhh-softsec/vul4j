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

}

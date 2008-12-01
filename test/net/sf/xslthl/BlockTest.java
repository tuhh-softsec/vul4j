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
public class BlockTest {

    static final String BLOCK_CONTENT = "Foo bar quux.";
    Block block;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
	block = new Block(BLOCK_CONTENT);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
	block = null;
    }

    /**
     * Test method for {@link net.sf.xslthl.Block#Block(java.lang.String)}.
     */
    @Test
    public void testBlock() {
	new Block(null);
	new Block("");
	new Block(BLOCK_CONTENT);
    }

    /**
     * Test method for {@link net.sf.xslthl.Block#getText()}.
     */
    @Test
    public void testGetText() {
	assertEquals(BLOCK_CONTENT, block.getText());
    }

    /**
     * Test method for {@link net.sf.xslthl.Block#toString()}.
     */
    @Test
    public void testToString() {
	assertEquals(BLOCK_CONTENT, block.toString());
	assertEquals(block.getText(), block.toString());
    }

    /**
     * Test method for {@link net.sf.xslthl.Block#isStyled()}.
     */
    @Test
    public void testIsStyled() {
	assertFalse(block.isStyled());
    }

    /**
     * Test method for {@link net.sf.xslthl.Block#isEmpty()}.
     */
    @Test
    public void testEmpty() {
	assertFalse(block.isEmpty());
	Block empty = new Block(null);
	assertTrue(empty.isEmpty());
	empty = new Block("");
	assertTrue(empty.isEmpty());
    }

}

/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/test/org/apache/commons/digester/plugins/TestInline.java,v 1.3 2003/10/09 21:09:49 rdonkin Exp $
 * $Revision: 1.3 $
 * $Date: 2003/10/09 21:09:49 $
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache" nor may "Apache" appear in their names without prior 
 *    written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */ 


package org.apache.commons.digester.plugins;

import java.util.List;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.digester.*;
import org.apache.commons.digester.plugins.*;

/**
 * Test cases for declaration of plugin classes "inline" (ie by specifying
 * plugin-class).
 */

public class TestInline extends TestCase {
    /** Standard constructor */
    public TestInline(String name) { 
        super(name);
    }

    /** Set up instance variables required by this test case. */
    public void setUp() {}

    /** Return the tests included in this test suite. */
    public static Test suite() {

        return (new TestSuite(TestInline.class));

    }

    /** Tear down instance variables required by this test case.*/
    public void tearDown() {}
        
    // --------------------------------------------------------------- Test cases
    
    public void testInlineDeclaration() throws Exception {
        // * tests that plugins can be specified by class, and that the
        //   correct class gets loaded.
        // * tests that autosetproperties works
        // * tests that multiple different classes can be loaded via the
        //   same plugin rule (ie at the same pattern).
        Digester digester = new Digester();
        PluginRules rc = new PluginRules();
        digester.setRules(rc);
        
        PluginCreateRule pcr = new PluginCreateRule(Widget.class);
        digester.addRule("root/widget", pcr);
        digester.addSetNext("root/widget", "addChild");

        Container root = new Container();
        digester.push(root);
        
        try {
            digester.parse(
                TestAll.getInputStream(this, "test1.xml"));
        }
        catch(Exception e) {
            throw e;
        }
        
        Object child;
        List children = root.getChildren();
        assertTrue(children != null);
        assertEquals(2, children.size());
        
        child = children.get(0);
        assertTrue(child != null);
        assertEquals(TextLabel.class, child.getClass());
        TextLabel label1 = (TextLabel) child;
        assertEquals("anonymous", label1.getId());
        assertEquals("1", label1.getLabel());
        
        child = children.get(1);
        assertTrue(child != null);
        assertEquals(TextLabel.class, child.getClass());
        TextLabel label2 = (TextLabel) child;
        assertEquals("L1", label2.getId());
        assertEquals("2", label2.getLabel());
    }
}

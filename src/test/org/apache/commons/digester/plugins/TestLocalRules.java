/*
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
 * Test cases for defining custom rules on the plugin class itself.
 */

public class TestLocalRules extends TestCase {
    /** Standard constructor */
    public TestLocalRules(String name) { 
        super(name);
    }

    /** Set up instance variables required by this test case. */
    public void setUp() {}

    /** Return the tests included in this test suite. */
    public static Test suite() {

        return (new TestSuite(TestLocalRules.class));

    }

    /** Tear down instance variables required by this test case.*/
    public void tearDown() {}
        
    // --------------------------------------------------------------- Test cases
    
    public void testLocalRules() throws Exception {
        // * tests that the plugin class can define an addRules method,
        //   which gets detected and executed.
        Digester digester = new Digester();
        PluginRules rc = new PluginRules();
        digester.setRules(rc);
        
        PluginDeclarationRule pdr = new PluginDeclarationRule();
        digester.addRule("root/plugin", pdr);
        
        PluginCreateRule pcr2 = new PluginCreateRule(Widget.class);
        digester.addRule("root/widget", pcr2);
        digester.addSetNext("root/widget", "addChild");

        Container root = new Container();
        digester.push(root);
        
        try {
            digester.parse(
                TestAll.getInputStream(this, "test4a.xml"));
        }
        catch(Exception e) {
            throw e;
        }
        
        Object child;
        List children = root.getChildren();
        assertTrue(children != null);
        assertEquals(3, children.size());
        
        // min/max rules should be in effect
        // setproperties should be in effect
        child = children.get(0);
        assertTrue(child != null);
        assertEquals(Slider.class, child.getClass());
        Slider slider1 = (Slider) child;
        assertEquals("slider1", slider1.getLabel());
        assertEquals(1, slider1.getMin());
        assertEquals(2, slider1.getMax());
        
        // range rules should not be in effect
        // setproperties should be in effect
        child = children.get(1);
        assertTrue(child != null);
        assertEquals(Slider.class, child.getClass());
        Slider slider2 = (Slider) child;
        assertEquals("slider2", slider2.getLabel());
        assertEquals(0, slider2.getMin());
        assertEquals(0, slider2.getMax());
        
        // setproperties should be working on text label
        child = children.get(2);
        assertTrue(child != null);
        assertEquals(TextLabel.class, child.getClass());
        assertEquals("text1", ((TextLabel)child).getLabel());
    }
    
    public void testNonStandardLocalRules() throws Exception {
        // * tests that using PluginDeclarationRule to declare an alternate
        //   rule method name invokes that alternate method instead.
        // * tests that if a rule method is defined, then a SetProperties
        //   rule is not automatically added.
        // * tests that a SetProperties rule applying to one class doesn't
        //   apply to different plugin classes mounted at the same rule.
        Digester digester = new Digester();
        PluginRules rc = new PluginRules();
        digester.setRules(rc);
        
        PluginDeclarationRule pdr = new PluginDeclarationRule();
        digester.addRule("root/plugin", pdr);
        
        PluginCreateRule pcr2 = new PluginCreateRule(Widget.class);
        digester.addRule("root/widget", pcr2);
        digester.addSetNext("root/widget", "addChild");

        Container root = new Container();
        digester.push(root);
        
        try {
            digester.parse(
                TestAll.getInputStream(this, "test4b.xml"));
        }
        catch(Exception e) {
            throw e;
        }
        
        Object child;
        List children = root.getChildren();
        assertTrue(children != null);
        assertEquals(3, children.size());
        
        // min/max rules should not  be in effect
        // setproperties should not be in effect
        child = children.get(0);
        assertTrue(child != null);
        assertEquals(Slider.class, child.getClass());
        Slider slider1 = (Slider) child;
        assertEquals("nolabel", slider1.getLabel());
        assertEquals(0, slider1.getMin());
        assertEquals(0, slider1.getMax());
        
        // range rules should be in effect
        // setproperties should not be in effect
        child = children.get(1);
        assertTrue(child != null);
        assertEquals(Slider.class, child.getClass());
        Slider slider2 = (Slider) child;
        assertEquals("nolabel", slider2.getLabel());
        assertEquals(10, slider2.getMin());
        assertEquals(20, slider2.getMax());
        
        // setproperties should be working on text label
        child = children.get(2);
        assertTrue(child != null);
        assertEquals(TextLabel.class, child.getClass());
        assertEquals("text1", ((TextLabel)child).getLabel());
    }
}

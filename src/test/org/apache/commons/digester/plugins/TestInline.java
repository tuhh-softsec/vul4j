/* $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 


package org.apache.commons.digester.plugins;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.digester.Digester;

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
    @Override
    public void setUp() {}

    /** Return the tests included in this test suite. */
    public static Test suite() {

        return (new TestSuite(TestInline.class));

    }

    /** Tear down instance variables required by this test case.*/
    @Override
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
        List<Widget> children = root.getChildren();
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
    
    public void testLeadingSlash() throws Exception {
        // Tests that PluginRules handles patterns with a leading slash.
        // 
        // This test doesn't really belong in this class. If a separate test 
        // case class is created for PluginRules, then this method should be
        // moved there.

        Digester digester = new Digester();
        PluginRules rc = new PluginRules();
        digester.setRules(rc);
        
        PluginCreateRule pcr = new PluginCreateRule(Widget.class);
        digester.addRule("/root/widget", pcr);
        digester.addSetNext("/root/widget", "addChild");

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
        List<Widget> children = root.getChildren();
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

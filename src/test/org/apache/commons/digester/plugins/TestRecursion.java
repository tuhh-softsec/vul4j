/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.digester.*;
import org.apache.commons.digester.plugins.*;

/**
 * Test cases for plugins with custom rules which include PluginCreateRule
 * instances, allowing recursive datastructures to be processed.
 */

public class TestRecursion extends TestCase {
    /** Standard constructor */
    public TestRecursion(String name) { 
        super(name);
    }

    /** Set up instance variables required by this test case. */
    public void setUp() {}

    /** Return the tests included in this test suite. */
    public static Test suite() {

        return (new TestSuite(TestRecursion.class));

    }

    /** Tear down instance variables required by this test case.*/
    public void tearDown() {}
        
    // --------------------------------------------------------------- Test cases
    
    public void testRecursiveRules() throws Exception {
        // * tests that a rule can declare custom PluginCreateRules
        //   that allow it to plug in instances of itself below
        //   itself.

        Digester digester = new Digester();
        PluginRules rc = new PluginRules();
        digester.setRules(rc);
        
        PluginDeclarationRule pdr = new PluginDeclarationRule();
        digester.addRule("*/plugin", pdr);
        
        PluginCreateRule pcr = new PluginCreateRule(Widget.class);
        digester.addRule("root/widget", pcr);
        digester.addSetNext("root/widget", "addChild");

        Container root = new Container();
        digester.push(root);
        
        try {
            digester.parse(
                TestAll.getInputStream(this, "test6.xml"));
        }
        catch(Exception e) {
            throw e;
        }
        
        int nDescendants = countWidgets(root);
        assertEquals(10, nDescendants);
    }

    private int countWidgets(Container c) {
        List l = c.getChildren();
        int sum = 0;
        for(Iterator i = l.iterator(); i.hasNext(); ) {
            Widget w = (Widget) i.next();
            ++sum; 
            if (w instanceof Container) {
                sum += countWidgets((Container) w);
            }
        }
        return sum;
    }
}

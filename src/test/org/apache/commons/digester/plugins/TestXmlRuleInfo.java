/*
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

import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.digester.Digester;

/**
 * Test cases for the declaration of custom rules for a plugin using
 * xmlrules format files.
 */

public class TestXmlRuleInfo extends TestCase {
    /** Standard constructor */
    public TestXmlRuleInfo(String name) { 
        super(name);
    }

    /** Set up instance variables required by this test case. */
    @Override
    public void setUp() {}

    /** Return the tests included in this test suite. */
    public static Test suite() {

        return (new TestSuite(TestXmlRuleInfo.class));

    }

    /** Tear down instance variables required by this test case.*/
    @Override
    public void tearDown() {}
        
    // --------------------------------------------------------------- Test cases

    public void testXmlRuleInfoExplicitFile() throws Exception {
        // * tests that custom rules can be declared on a 
        //   separate class by explicitly declaring a file containing
        //   the rules, using a relative or absolute path name.
        
        StringBuffer input = new StringBuffer();
        input.append("<root>");
        input.append(" <plugin");
        input.append("  id='testobject'"); 
        input.append("  class='org.apache.commons.digester.plugins.TestObject'");
        input.append("  file='src/test/org/apache/commons/digester/plugins/xmlrules1.xml'");
        input.append("  />");
        input.append("  <object plugin-id='testobject'/>");
        input.append("</root>");

        Digester digester = new Digester();
        PluginRules rc = new PluginRules();
        digester.setRules(rc);
        
        PluginDeclarationRule pdr = new PluginDeclarationRule();
        digester.addRule("root/plugin", pdr);
        
        PluginCreateRule pcr = new PluginCreateRule(TestObject.class);
        digester.addRule("root/object", pcr);
        
        try {
            digester.parse(new StringReader(input.toString()));
        }
        catch(Exception e) {
            throw e;
        }

        Object root = digester.getRoot();
        assertEquals(TestObject.class, root.getClass());
        TestObject testObject = (TestObject) root;
        assertEquals("xmlrules1", testObject.getValue());
    }

    public void testXmlRuleInfoExplicitResource() throws Exception {
        // * tests that custom rules can be declared on a 
        //   separate class by explicitly declaring the rule class.
        //   and explicitly declaring a file which is somewhere in the 
        //   classpath.

        StringBuffer input = new StringBuffer();
        input.append("<root>");
        input.append(" <plugin");
        input.append("  id='testobject'"); 
        input.append("  class='org.apache.commons.digester.plugins.TestObject'");
        input.append("  resource='org/apache/commons/digester/plugins/xmlrules2.xml'");
        input.append("  />");
        input.append("  <object plugin-id='testobject'/>");
        input.append("</root>");

        Digester digester = new Digester();
        PluginRules rc = new PluginRules();
        digester.setRules(rc);
        
        PluginDeclarationRule pdr = new PluginDeclarationRule();
        digester.addRule("root/plugin", pdr);
        
        PluginCreateRule pcr = new PluginCreateRule(TestObject.class);
        digester.addRule("root/object", pcr);

        try {
            digester.parse(new StringReader(input.toString()));
        }
        catch(Exception e) {
            throw e;
        }

        Object root = digester.getRoot();
        assertEquals(TestObject.class, root.getClass());
        TestObject testObject = (TestObject) root;
        assertEquals("xmlrules2", testObject.getValue());
    }
    
    public void testXmlRuleImplicitResource() throws Exception {
        // * tests that custom rules can be declared on a 
        //   separate class by explicitly declaring the rule class.
        //   and explicitly declaring a file which is somewhere in the 
        //   classpath.

        StringBuffer input = new StringBuffer();
        input.append("<root>");
        input.append(" <plugin");
        input.append("  id='testobject'"); 
        input.append("  class='org.apache.commons.digester.plugins.TestObject'");
        input.append("  />");
        input.append("  <object plugin-id='testobject'/>");
        input.append("</root>");

        Digester digester = new Digester();
        PluginRules rc = new PluginRules();
        digester.setRules(rc);
        
        PluginDeclarationRule pdr = new PluginDeclarationRule();
        digester.addRule("root/plugin", pdr);
        
        PluginCreateRule pcr = new PluginCreateRule(TestObject.class);
        digester.addRule("root/object", pcr);

        try {
            digester.parse(new StringReader(input.toString()));
        }
        catch(Exception e) {
            throw e;
        }

        Object root = digester.getRoot();
        assertEquals(TestObject.class, root.getClass());
        TestObject testObject = (TestObject) root;
        assertEquals("xmlrules-ruleinfo", testObject.getValue());
    }
}

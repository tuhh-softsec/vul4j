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


package org.apache.commons.digester;


import java.io.IOException;
import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xml.sax.SAXException;

/**
 * <p>Tests for situations where CallMethodRule instances and their
 * parameters overlap each other.</p>
 */
public class OverlappingCallMethodRuleTestCase extends TestCase {

    // ----------------------------------------------------------- Constructors


    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public OverlappingCallMethodRuleTestCase(String name) {

        super(name);

    }


    // --------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    @Override
    public void setUp() {
    }


    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {
        return (new TestSuite(OverlappingCallMethodRuleTestCase.class));
    }


    /**
     * Tear down instance variables required by this test case.
     */
    @Override
    public void tearDown() {
    }



    String itemId;
    String itemName;
    
    public void setItemId(String id) { itemId = id; }
    public void setItemName(String name) { itemName = name; }
    
    // ------------------------------------------------ Individual Test Methods


    public void testItem1() throws SAXException, IOException {
        StringBuffer input = new StringBuffer();
        input.append("<root>");
        input.append(" <item id='1'>anitem</item>");
        input.append("</root>");

        Digester digester = new Digester();
        
        digester.addCallMethod("root/item", "setItemId", 1);
        digester.addCallParam("root/item", 0, "id");
        digester.addCallMethod("root/item", "setItemName", 1);
        digester.addCallParam("root/item", 0);

        this.itemId = null;
        this.itemName = null;
        digester.push(this);
        digester.parse(new StringReader(input.toString()));

        assertEquals("1", this.itemId);
        assertEquals("anitem", this.itemName);
    }

    public void testItem2() throws SAXException, IOException {
        StringBuffer input = new StringBuffer();
        input.append("<root>");
        input.append(" <item id='1'>anitem</item>");
        input.append("</root>");

        Digester digester = new Digester();

        digester.addCallMethod("root/item", "setItemName", 1);
        digester.addCallParam("root/item", 0);
        digester.addCallMethod("root/item", "setItemId", 1);
        digester.addCallParam("root/item", 0, "id");

        this.itemId = null;
        this.itemName = null;
        digester.push(this);
        digester.parse(new StringReader(input.toString()));

        assertEquals("1", this.itemId);
        assertEquals("anitem", this.itemName);
    }

    public void testItem3() throws SAXException, IOException {
        StringBuffer input = new StringBuffer();
        input.append("<root>");
        input.append(" <item>1</item>");
        input.append("</root>");

        Digester digester = new Digester();

        digester.addCallMethod("root/item", "setItemId", 1);
        digester.addCallParam("root/item", 0);
        digester.addCallMethod("root/item", "setItemName", 1);
        digester.addCallParam("root/item", 0);

        this.itemId = null;
        this.itemName = null;
        digester.push(this);
        digester.parse(new StringReader(input.toString()));

        assertEquals("1", this.itemId);
        assertEquals("1", this.itemName);
    }

    /**
     * This is an "anti-test" that demonstrates how digester can <i>fails</i> 
     * to produce the correct results, due to a design flaw (or at least
     * limitation) in the way that CallMethodRule and CallParamRule work. 
     * <p>
     * The following sequence always fails:
     * <ul>
     * <li>CallMethodRule A fires (pushing params array)</li>
     * <li>CallMethodRule B fires (pushing params array)</li>
     * <li>params rule for A fires --> writes to params of method B!</li>
     * <li>params rule for B fires --> overwrites params for method B</li>
     * </ul>
     * The result is that method "b" appears to work ok, but method "a"
     * loses its input parameters.
     * <p>
     * One solution is for CallParamRule objects to know which CallMethodRule
     * they are associated with. Even this might fail in corner cases where
     * the same rule is associated with multiple patterns, or with wildcard
     * patterns which cause a rule to fire in a "recursive" manner. However
     * implementing this is not possible with the current digester design.
     */
    
    public void testItem4() throws SAXException, IOException {
        StringBuffer input = new StringBuffer();
        input.append("<root>");
        input.append(" <item>");
        input.append("  <id value='1'/>");
        input.append("  <name value='name'/>");
        input.append(" </item>");
        input.append("</root>");

        Digester digester = new Digester();

        digester.addCallMethod("root/item", "setItemId", 1);
        digester.addCallParam("root/item/id", 0, "value");
        digester.addCallMethod("root/item", "setItemName", 1);
        digester.addCallParam("root/item/name", 0, "value");

        this.itemId = null;
        this.itemName = null;
        digester.push(this);
        digester.parse(new StringReader(input.toString()));

        // These are the "correct" results
        //assertEquals("1", this.itemId);
        //assertEquals("name", this.itemName);

        // These are what actually happens
        assertEquals(null, this.itemId);
        assertEquals("name", this.itemName);
    }

    /**
     * This test checks that CallParamRule instances which fetch data
     * from xml attributes work ok when invoked "recursively",
     * ie a rule instances' methods gets called in the order
     * begin[1]/begin[2]/body[2]/end[2]/body[1]/end[1]
     */
    public void testWildcard1() throws SAXException, IOException {
        StringBuffer input = new StringBuffer();
        input.append("<box id='A1'>");
        input.append(" <box id='B1'>");
        input.append("  <box id='C1'/>");
        input.append("  <box id='C2'/>");
        input.append(" </box>");
        input.append("</box>");

        Digester digester = new Digester();

        digester.addObjectCreate("*/box", Box.class);
        digester.addCallMethod("*/box", "setId", 1);
        digester.addCallParam("*/box", 0, "id");
        digester.addSetNext("*/box", "addChild");

        Box root = new Box();
        root.setId("root");
        digester.push(root);
        digester.parse(new StringReader(input.toString()));

        // walk the object tree, concatenating the id strings
        String ids = root.getIds();
        assertEquals("root A1 B1 C1 C2", ids);
    }

    /**
     * This test checks that CallParamRule instances which fetch data
     * from the xml element body work ok when invoked "recursively",
     * ie a rule instances' methods gets called in the order
     * begin[1]/begin[2]/body[2]/end[2]/body[1]/end[1]
     */
    public void testWildcard2() throws SAXException, IOException {
        StringBuffer input = new StringBuffer();
        input.append("<box>A1");
        input.append(" <box>B1");
        input.append("  <box>C1</box>");
        input.append("  <box>C2</box>");
        input.append(" </box>");
        input.append("</box>");

        Digester digester = new Digester();

        digester.addObjectCreate("*/box", Box.class);
        digester.addCallMethod("*/box", "setId", 1);
        digester.addCallParam("*/box", 0);
        digester.addSetNext("*/box", "addChild");

        Box root = new Box();
        root.setId("root");
        digester.push(root);
        digester.parse(new StringReader(input.toString()));

        // walk the object tree, concatenating the id strings
        String ids = root.getIds();
        assertEquals("root A1 B1 C1 C2", ids);
    }
}


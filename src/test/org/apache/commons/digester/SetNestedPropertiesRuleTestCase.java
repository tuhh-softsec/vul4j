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


package org.apache.commons.digester;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xml.sax.SAXException;


/**
 * <p> Test case for <code>SetNestedPropertiesRule</code>.
 * This contains tests for the main applications of the rule
 * and two more general tests of digester functionality used by this rule.
 */
public class SetNestedPropertiesRuleTestCase extends TestCase {


    // ----------------------------------------------------- Instance Variables

    /**
     * Simple test xml document used in the tests.
     */
    protected final static String TEST_XML =
        "<?xml version='1.0'?>" +
        "<root>ROOT BODY" +
        "<alpha>ALPHA BODY</alpha>" +
        "<beta>BETA BODY</beta>" +
        "<gamma>GAMMA BODY</gamma>" +
        "<delta>DELTA BODY</delta>" +
        "</root>";


    /**
     * The digester instance we will be processing.
     */
    protected Digester digester = null;


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public SetNestedPropertiesRuleTestCase(String name) {

        super(name);

    }


    // --------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    public void setUp() {

        digester = new Digester();

    }


    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {

        return (new TestSuite(SetNestedPropertiesRuleTestCase.class));

    }


    /**
     * Tear down instance variables required by this test case.
     */
    public void tearDown() {

        digester = null;

    }



    // ------------------------------------------------ Individual Test Methods


    /**
     * Test that you can successfully automatically set properties.
     */
    public void testAutomaticallySetProperties()
        throws SAXException, IOException {

        // going to be setting properties on a SimpleTestBean
        digester.addObjectCreate("root",
                                 "org.apache.commons.digester.SimpleTestBean");

        // match all children of root with this rule
        digester.addRule("root", new SetNestedPropertiesRule());

        SimpleTestBean bean = (SimpleTestBean) digester.parse(xmlTestReader());

        // check properties are set correctly
        assertEquals(
                "Property alpha not set correctly",
                "ALPHA BODY",
                bean.getAlpha());

        assertEquals(
                "Property beta not set correctly",
                "BETA BODY",
                bean.getBeta());

        assertEquals(
                "Property gamma not set correctly",
                "GAMMA BODY",
                bean.getGamma());


    }

    /**
     * Test that you can customise the property mappings.
     */
    public void testCustomisedProperties()
        throws SAXException, IOException {

        // going to be setting properties on a SimpleTestBean
        digester.addObjectCreate("root",
                                 "org.apache.commons.digester.SimpleTestBean");

        // ignorethe "alpha" element
        // map the "beta" element into the gamma property
        // map the gamma element into the delta property
        // ignore the delta element
        
        Rule rule = new SetNestedPropertiesRule(
            new String[]{"alpha", "beta", "gamma", "delta"},
            new String[]{null, "gamma", "delta"});
            
        digester.addRule("root", rule);

        SimpleTestBean bean = (SimpleTestBean) digester.parse(xmlTestReader());

        // check properties are set correctly
        assertEquals(
                "Property alpha not set correctly",
                null,
                bean.getAlpha());

        assertEquals(
                "Property beta not set correctly",
                null,
                bean.getBeta());

        assertEquals(
                "Property gamma not set correctly",
                "BETA BODY",
                bean.getGamma());

        assertEquals(
                "Property delta not set correctly",
                "GAMMA BODY",
                bean.getDeltaValue());
                
         // check no bad rules object is left
         assertEquals(
            "Digester rules object not reset.",
            RulesBase.class, digester.getRules().getClass());
    }


    /**
     * Test that:
     * <ul>
     * <li> you can have rules matching the same pattern as the 
     *  SetNestedPropertiesRule, </li>
     * <li> you can have rules matching child elements of the rule, </li>
     * <li> the Rules object is reset nicely. </li>
     * </ul>
     */
    public void testMultiRuleMatch()
        throws SAXException, IOException {

        String testXml =
            "<?xml version='1.0'?>" +
            "<root>" +
                "<testbean alpha='alpha-attr'>ROOT BODY" +
                    "<beta>BETA BODY</beta>" +
                    "<gamma>GAMMA " +
                    "<prop name='delta' value='delta-prop'/>" +
                    "BODY" +
                    "</gamma>" +
                "</testbean>" +
            "</root>";

        Reader reader = new StringReader(testXml);

        // going to be setting properties on a SimpleTestBean
        digester.addObjectCreate("root/testbean",
                                 "org.apache.commons.digester.SimpleTestBean");

        digester.addRule("root/testbean", new SetNestedPropertiesRule());
        digester.addSetProperties("root/testbean");
        digester.addSetProperty("root/testbean/gamma/prop", "name", "value");

        SimpleTestBean bean = (SimpleTestBean) digester.parse(reader);

        assertNotNull("No object created", bean);
        
        // check properties are set correctly
        assertEquals(
                "Property alpha not set correctly",
                "alpha-attr",
                bean.getAlpha());

        assertEquals(
                "Property beta not set correctly",
                "BETA BODY",
                bean.getBeta());

        assertEquals(
                "Property gamma not set correctly",
                "GAMMA BODY",
                bean.getGamma());

        assertEquals(
                "Property delta not set correctly",
                "delta-prop",
                bean.getDeltaValue());

         // check no bad rules object is left
         assertEquals(
            "Digester rules object not reset.",
            RulesBase.class, digester.getRules().getClass());
    }

    /**
     * Test that unknown child elements trigger an exception.
     */
    public void testUnknownChildrenCausesException()
        throws SAXException, IOException {

        String testXml =
            "<?xml version='1.0'?>" +
            "<root>" +
                "<testbean>" +
                    "<beta>BETA BODY</beta>" +
                    "<foo>GAMMA</foo>" +
                "</testbean>" +
            "</root>";

        Reader reader = new StringReader(testXml);

        // going to be setting properties on a SimpleTestBean
        digester.addObjectCreate("root",
                                 "org.apache.commons.digester.SimpleTestBean");

        Rule rule = new SetNestedPropertiesRule();
        digester.addRule("root", rule);

        try {
            SimpleTestBean bean = (SimpleTestBean) digester.parse(reader);
            fail("Expected to generate an exception.");
        } catch(SAXException e) {
            Exception nested = e.getException();
            if ((nested==null) || !(nested instanceof NoSuchMethodException)) {
                // nope, not the sort of exception we expected
                throw e;
            }
        }
    }

    /**
     * Test that unknown child elements are allowed if the appropriate
     * flag is set.
     */
    public void testUnknownChildrenExceptionOverride()
        throws SAXException, IOException {

        String testXml =
            "<?xml version='1.0'?>" +
            "<root>" +
                "<testbean>" +
                    "<beta>BETA BODY</beta>" +
                    "<foo>GAMMA</foo>" +
                "</testbean>" +
            "</root>";

        Reader reader = new StringReader(testXml);

        // going to be setting properties on a SimpleTestBean
        digester.addObjectCreate("root",
                                 "org.apache.commons.digester.SimpleTestBean");

        SetNestedPropertiesRule rule = new SetNestedPropertiesRule();
        rule.setAllowUnknownChildElements(true);
        digester.addRule("root", rule);

        SimpleTestBean bean = (SimpleTestBean) digester.parse(reader);
        assertNotNull(bean);
    }


    /**
     * Get input stream from {@link #TEST_XML}.
     */
    private Reader xmlTestReader() throws IOException {
        return new StringReader(TEST_XML);
    }

}




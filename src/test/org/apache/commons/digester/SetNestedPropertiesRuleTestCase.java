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


package org.apache.commons.digester;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

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
    @Override
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
    @Override
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

        assertEquals(
                "Property delta not set correctly",
                "DELTA BODY",
                bean.getDeltaValue());
    }

    /**
     * Test that it is an error when a child element exists but no corresponding
     * java property exists.
     */
    public void testMandatoryProperties()
        throws SAXException, IOException {

        String TEST_XML =
            "<?xml version='1.0'?>" +
            "<root>ROOT BODY" +
            "<badprop>ALPHA BODY</badprop>" +
            "</root>";

        // going to be setting properties on a SimpleTestBean
        digester.addObjectCreate("root",
                                 "org.apache.commons.digester.SimpleTestBean");

        // match all children of root with this rule
        digester.addRule("root", new SetNestedPropertiesRule());

        try {
            SimpleTestBean bean = (SimpleTestBean) digester.parse(
                new StringReader(TEST_XML));

            // we should never get here...
            fail("No exception thrown by parse when unknown child element found.");
            assertNotNull(bean); // just to prevent compiler warning on unused var
        } catch(org.xml.sax.SAXParseException e) {
            String msg = e.getMessage();
            if (msg.indexOf("badprop") >= 0) {
                // ok, this is expected; there is no "setBadprop" method on the
                // SimpleTestBean class...
            } else {
                fail("Unexpected parse exception:" + e.getMessage());
            }
        }
    }

    /**
     * Test that you can customise the property mappings using the
     * constructor which takes arrays-of-strings.
     */
    public void testCustomisedProperties1()
        throws SAXException, IOException {

        String TEST_XML =
            "<?xml version='1.0'?>" +
            "<root>ROOT BODY" +
            "<alpha>ALPHA BODY</alpha>" +
            "<beta>BETA BODY</beta>" +
            "<gamma-alt>GAMMA BODY</gamma-alt>" +
            "<delta>DELTA BODY</delta>" +
            "</root>";

        // going to be setting properties on a SimpleTestBean
        digester.addObjectCreate("root",
                                 "org.apache.commons.digester.SimpleTestBean");

        // ignore the "alpha" element (target=null)
        // don't remap the "beta" element
        // map the gamma-alt element into the gamma property
        // ignore the delta element (no matching element in array)
        
        Rule rule = new SetNestedPropertiesRule(
            new String[]{"alpha", "gamma-alt", "delta"},
            new String[]{null, "gamma"});
            
        digester.addRule("root", rule);

        SimpleTestBean bean = (SimpleTestBean) digester.parse(
            new StringReader(TEST_XML));

        // check properties are set correctly
        assertEquals(
                "Property alpha was not ignored (it should be)",
                null,
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
                "Property delta was not ignored (it should be)",
                null,
                bean.getDeltaValue());
                
         // check no bad rules object is left
         assertEquals(
            "Digester rules object not reset.",
            RulesBase.class, digester.getRules().getClass());
    }

    /**
     * Test that you can ignore a single input xml element using the
     * constructor which takes a single remapping.
     */
    public void testCustomisedProperties2a()
        throws SAXException, IOException {

        String TEST_XML =
            "<?xml version='1.0'?>" +
            "<root>ROOT BODY" +
            "<alpha>ALPHA BODY</alpha>" +
            "<beta>BETA BODY</beta>" +
            "<gamma>GAMMA BODY</gamma>" +
            "<delta>DELTA BODY</delta>" +
            "</root>";

        // going to be setting properties on a SimpleTestBean
        digester.addObjectCreate("root",
                                 "org.apache.commons.digester.SimpleTestBean");

        // ignore the "alpha" element (target=null)
        Rule rule = new SetNestedPropertiesRule("alpha", null);
        digester.addRule("root", rule);

        SimpleTestBean bean = (SimpleTestBean) digester.parse(
            new StringReader(TEST_XML));

        // check properties are set correctly
        assertEquals(
                "Property alpha was not ignored (it should be)",
                null,
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
                "DELTA BODY",
                bean.getDeltaValue());

        // check no bad rules object is left
        assertEquals(
            "Digester rules object not reset.",
            RulesBase.class, digester.getRules().getClass());
    }

    /**
     * Test that you can customise the property mappings using the
     * constructor which takes a single remapping.
     */
    public void testCustomisedProperties2b()
        throws SAXException, IOException {

        String TEST_XML =
            "<?xml version='1.0'?>" +
            "<root>ROOT BODY" +
            "<alpha-alt>ALPHA BODY</alpha-alt>" +
            "<beta>BETA BODY</beta>" +
            "<gamma>GAMMA BODY</gamma>" +
            "<delta>DELTA BODY</delta>" +
            "</root>";

        // going to be setting properties on a SimpleTestBean
        digester.addObjectCreate("root",
                                 "org.apache.commons.digester.SimpleTestBean");

        // map the contents of the alpha-alt xml child into the
        // "alpha" java property.
        Rule rule = new SetNestedPropertiesRule("alpha-alt", "alpha");
        digester.addRule("root", rule);

        SimpleTestBean bean = (SimpleTestBean) digester.parse(
            new StringReader(TEST_XML));

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

        assertEquals(
                "Property delta not set correctly",
                "DELTA BODY",
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
            assertNotNull(bean); // just to prevent compiler warning on unused var
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
     * Test that the rule works in a sane manner when the associated pattern
     * is a wildcard such that the rule matches one of its own child elements.
     * <p>
     * See bugzilla entry 31393.
     */
    public void testRecursiveNestedProperties()
        throws SAXException, IOException {

        String testXml =
            "<?xml version='1.0'?>" +
            "<testbean>" +
                "<beta>BETA BODY</beta>" +
                "<testbean>" +
                    "<beta>BETA BODY</beta>" +
                "</testbean>" +
            "</testbean>";

        Reader reader = new StringReader(testXml);

        // going to be setting properties on a SimpleTestBean
        digester.addObjectCreate("*/testbean",
                                 "org.apache.commons.digester.SimpleTestBean");

        SetNestedPropertiesRule rule = new SetNestedPropertiesRule();
        rule.setAllowUnknownChildElements(true);
        digester.addRule("*/testbean", rule);

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




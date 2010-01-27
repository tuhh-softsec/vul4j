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
 * <p> Test case for <code>SetPropertiesRule</code>.</p>
 */
public class SetPropertiesRuleTestCase extends TestCase {


    // ----------------------------------------------------- Instance Variables

    /**
     * Simple test xml document used in the tests.
     */
    protected final static String TEST_XML_1 =
        "<?xml version='1.0'?><root alpha='ALPHA VALUE' beta='BETA VALUE' delta='DELTA VALUE'/>";

    /**
     * Simple test xml document used in the tests.
     */
    protected final static String TEST_XML_2 =
        "<?xml version='1.0'?><root alpa='ALPA VALUE' beta='BETA VALUE' delta='DELTA VALUE'/>";

    /**
     * Simple test xml document used in the tests.
     */
    protected final static String TEST_XML_3 =
        "<?xml version='1.0'?><root alpha='ALPHA VALUE' beta='BETA VALUE' delta='DELTA VALUE' ignore='ignore value'/>";

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
    public SetPropertiesRuleTestCase(String name) {

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

        return (new TestSuite(SetPropertiesRuleTestCase.class));

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
     * Positive test for SetPropertiesRule.
     */
    public void testPositive() throws Exception {

        // Set up the rules we need
        digester.addObjectCreate("root",
                                 "org.apache.commons.digester.SimpleTestBean");
        digester.addSetProperties("root");

        // Parse the input
        SimpleTestBean bean =
            (SimpleTestBean) digester.parse(xmlTestReader(TEST_XML_1));

        // Check that the properties were set correctly
        assertEquals("alpha property set",
                     "ALPHA VALUE",
                     bean.getAlpha());
        assertEquals("beta property set",
                     "BETA VALUE",
                     bean.getBeta());
        assertNull("gamma property not set",
                   bean.getGamma());
        assertEquals("delta property set",
                     "DELTA VALUE",
                     bean.getDeltaValue());

    }

    /**
     * Positive test for SetPropertyRule ignoring missing properties.
     */
    public void testIgnoreMissing() throws Exception {

        // Set up the rules we need
        digester.addObjectCreate("root",
                                 "org.apache.commons.digester.SimpleTestBean");
        digester.addSetProperties("root");

        // Parse the input
        SimpleTestBean bean =
            (SimpleTestBean) digester.parse(xmlTestReader(TEST_XML_2));

        // Check that the properties were set correctly
        assertNull("alpha property not set",
                     bean.getAlpha());
        assertEquals("beta property set",
                     "BETA VALUE",
                     bean.getBeta());
        assertNull("gamma property not set",
                   bean.getGamma());
        assertEquals("delta property set",
                     "DELTA VALUE",
                     bean.getDeltaValue());

    }

    /**
     * Negative test for SetPropertyRule ignoring missing properties.
     */
    public void testNegativeNotIgnoreMissing() throws Exception {

        // Set up the rules we need
        digester.addObjectCreate("root",
                                 "org.apache.commons.digester.SimpleTestBean");
        SetPropertiesRule rule = new SetPropertiesRule();
        rule.setIgnoreMissingProperty(false);
        digester.addRule("root", rule);

        try {
            // Parse the input
            SimpleTestBean bean =
                (SimpleTestBean) digester.parse(xmlTestReader(TEST_XML_2));
            fail("Should have thrown NoSuchMethodException");
            assertNotNull(bean); // just to prevent compiler warning on unused var
        } catch (Exception e) {
            if (e instanceof NoSuchMethodException) {
                // Expected;
            } else if (e instanceof SAXException) {
                Exception ee = ((SAXException) e).getException();
                if (ee != null) {
                    if (ee instanceof NoSuchMethodException) {
                        // Expected result
                    } else {
                        fail("Should have thrown SE->NoSuchMethodException, threw " + ee);
                    }
                } else {
                    fail("Should have thrown NoSuchMethodException, threw " +
                         e.getClass().getName());
                }
            } else {
                fail("Should have thrown NoSuchMethodException, threw " + e);
            }
        }
    }

    /**
     * Negative test for SetPropertyRule ignoring missing properties.
     */
    public void testPositiveNotIgnoreMissingWithIgnoreAttributes() throws Exception {

        // Set up the rules we need
        digester.addObjectCreate("root",
                                 "org.apache.commons.digester.SimpleTestBean");
        SetPropertiesRule rule = new SetPropertiesRule(new String[] {"ignore"}, new String[] {});
        rule.setIgnoreMissingProperty(false);
        digester.addRule("root", rule);

        // Parse the input
        SimpleTestBean bean =
            (SimpleTestBean) digester.parse(xmlTestReader(TEST_XML_3));
        
        // Check that the properties were set correctly
        assertEquals("alpha property set",
                     "ALPHA VALUE",
                     bean.getAlpha());
        assertEquals("beta property set",
                     "BETA VALUE",
                     bean.getBeta());
        assertNull("gamma property not set",
                   bean.getGamma());
        assertEquals("delta property set",
                     "DELTA VALUE",
                     bean.getDeltaValue());
    }


    /**
     * Get input stream from specified String containing XML data.
     */
    private Reader xmlTestReader(String xml) throws IOException {
        return new StringReader(xml);
    }

}



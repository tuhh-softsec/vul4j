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
package org.apache.commons.digester3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Test case for {@link SetPropertyRule}.
 */
public class SetPropertyRuleTestCase extends AbstractTestCase {

    /**
     * Simple test xml document used in the tests.
     */
    protected final static String TEST_XML_1 =
        "<?xml version='1.0'?><root>" +
        "<set name='alpha' value='ALPHA VALUE'/>" +
        "<set name='beta' value='BETA VALUE'/>" +
        "<set name='delta' value='DELTA VALUE'/>" +
        "</root>";

    /**
     * Simple test xml document used in the tests.
     */
    protected final static String TEST_XML_2 =
        "<?xml version='1.0'?><root>" +
        "<set name='unknown' value='UNKNOWN VALUE'/>" +
        "</root>";

    private Digester digester;

    @Before
    public void setUp() {
        this.digester = newBasicDigester(new SetPropertyTestCaseRuleModule());
    }

    @After
    public void tearDown() {
        this.digester = null;
    }

    /**
     * Positive test for SetPropertyRule.
     */
    @Test
    public void testPositive() throws Exception {
        // Parse the input
        SimpleTestBean bean = (SimpleTestBean) this.digester.parse(xmlTestReader(TEST_XML_1));

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
     * Negative test for SetPropertyRule.
     */
    @Test
    public void testNegative() {
        // Parse the input (should fail)
        try {
            SimpleTestBean bean = (SimpleTestBean) this.digester.parse(xmlTestReader(TEST_XML_2));

            fail("Should have thrown NoSuchMethodException");
            assertNotNull(bean); // just to prevent compiler warning on unused var
        } catch (Exception e) {
            if (e instanceof NoSuchMethodException) {
                // Expected result
            } else if (e instanceof InvocationTargetException) {
                Throwable t =
                    ((InvocationTargetException) e).getTargetException();
                if (t instanceof NoSuchMethodException) {
                    // Expected result
                } else {
                    fail("Should have thrown ITE->NoSuchMethodException, threw " + t);
                }
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
     * Get input stream from specified String containing XML data.
     */
    private Reader xmlTestReader(String xml) {
        return new StringReader(xml);
    }

    /**
     * Test SetPropertyRule when matched XML element has no attributes.
     * See: DIGESTER-114
     */
    @Test
    public void testElementWithNoAttributes() throws Exception {
        String TEST_XML_3 = "<?xml version='1.0'?><root><set/></root>";

        // Parse the input - should not throw an exception
        @SuppressWarnings("unused")
        SimpleTestBean bean = (SimpleTestBean) this.digester.parse(xmlTestReader(TEST_XML_3));
    }

    /**
     * Shared configuration module for SetPropertyTestCase tests.
     */
    private static class SetPropertyTestCaseRuleModule extends AbstractRulesModule {

        @Override
        protected void configure() {
            forPattern("root").createObject().ofType("org.apache.commons.digester3.SimpleTestBean");
            forPattern("root/set").setProperty("name").extractingValueFromAttribute("value");
        }

    }

}


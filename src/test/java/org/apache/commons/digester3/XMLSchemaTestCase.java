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

import static org.apache.commons.digester3.DigesterLoader.newLoader;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Tests for XInclude aware parsing.
 */
public class XMLSchemaTestCase extends AbstractTestCase {

    /**
     * The digester instance we will be processing.
     */
    private Digester digester = null;

    /**
     * Set up instance variables required by this test case.
     */
    @Before
    public void setUp() throws SAXException {
        Schema test13schema = SchemaFactory.
            newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).
            newSchema(this.getClass().getResource("Test13.xsd"));

        digester = newLoader(new EmployeeModule())
            .setNamespaceAware(true)
            .setSchema(test13schema)
            .newDigester();
    }

    /**
     * Tear down instance variables required by this test case.
     */
    @After
    public void tearDown() {
        digester = null;
    }

    /**
     * Test XML Schema validation.
     */
    @Test
    public void testGoodDocument() throws SAXException, IOException {
        // Listen to validation errors
        TestErrorHandler teh = new TestErrorHandler();
        digester.setErrorHandler(teh);

        // Parse our test input
        Employee employee = (Employee) digester.parse(getInputStream("Test13-01.xml"));
        assertNotNull("failed to parsed an employee", employee);
        assertTrue("Test13-01 should not generate errors in Schema validation", teh.clean);

        // Test document has been processed
        Address ha = employee.getAddress("home");
        assertNotNull(ha);
        assertEquals("Home City", ha.getCity());
        assertEquals("HS", ha.getState());
    }

    @Test
    public void testBadDocument() throws SAXException, IOException {
        // Listen to validation errors
        TestErrorHandler teh = new TestErrorHandler();
        digester.setErrorHandler(teh);

        // Parse our test input
        digester.parse(getInputStream("Test13-02.xml"));
        assertFalse("Test13-02 should generate errors in Schema validation", teh.clean);
    }

    // ------------------------------------ Utility Support Methods and Classes

    static final class TestErrorHandler implements ErrorHandler {

        public boolean clean = true;

        public TestErrorHandler() { }

        public void error(SAXParseException exception) {
            clean = false;
        }

        public void fatalError(SAXParseException exception) {
            clean = false;
        }

        public void warning(SAXParseException exception) {
            clean = false;
        }

    }

}

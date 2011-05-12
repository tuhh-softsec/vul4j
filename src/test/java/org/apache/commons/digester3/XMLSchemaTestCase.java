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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.digester3.Digester;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * <p>
 * Tests for XInclude aware parsing.
 * </p>
 */
public class XMLSchemaTestCase
{

    // ----------------------------------------------------- Instance Variables

    /**
     * The digester instance we will be processing.
     */
    protected Digester digester = null;

    // --------------------------------------------------- Overall Test Methods

    /**
     * Set up instance variables required by this test case.
     */
    @Before
    public void setUp()
        throws SAXException
    {

        digester = new Digester();

        // Use the test schema
        digester.setNamespaceAware( true );
        Schema test13schema =
            SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI ).newSchema( this.getClass().getClassLoader().getResource( "org/apache/commons/digester3/Test13.xsd" ) );
        digester.setXMLSchema( test13schema );

        // Configure the digester as required
        digester.addObjectCreate( "employee", Employee.class );
        digester.addCallMethod( "employee/firstName", "setFirstName", 0 );
        digester.addCallMethod( "employee/lastName", "setLastName", 0 );

        digester.addObjectCreate( "employee/address", Address.class );
        digester.addCallMethod( "employee/address/type", "setType", 0 );
        digester.addCallMethod( "employee/address/city", "setCity", 0 );
        digester.addCallMethod( "employee/address/state", "setState", 0 );
        digester.addSetNext( "employee/address", "addAddress" );

    }

    /**
     * Tear down instance variables required by this test case.
     */
    @After
    public void tearDown()
    {

        digester = null;

    }

    // ------------------------------------------------ Individual Test Methods

    /**
     * Test XML Schema validation.
     */
    @Test
    public void testGoodDocument()
        throws SAXException, IOException
    {

        // Listen to validation errors
        TestErrorHandler teh = new TestErrorHandler();
        digester.setErrorHandler( teh );

        // Parse our test input
        Employee employee = digester.parse( getInputStream( "Test13-01.xml" ) );
        assertNotNull( "failed to parsed an employee", employee );
        assertTrue( "Test13-01 should not generate errors in Schema validation", teh.clean );

        // Test document has been processed
        Address ha = employee.getAddress( "home" );
        assertNotNull( ha );
        assertEquals( "Home City", ha.getCity() );
        assertEquals( "HS", ha.getState() );

    }

    @Test
    public void testBadDocument()
        throws SAXException, IOException
    {

        // Listen to validation errors
        TestErrorHandler teh = new TestErrorHandler();
        digester.setErrorHandler( teh );

        // Parse our test input
        digester.parse( getInputStream( "Test13-02.xml" ) );
        assertFalse( "Test13-02 should generate errors in Schema validation", teh.clean );

    }

    // ------------------------------------ Utility Support Methods and Classes

    /**
     * Return an appropriate InputStream for the specified test file (which must be inside our current package.
     * 
     * @param name Name of the test file we want
     * @exception IOException if an input/output error occurs
     */
    protected InputStream getInputStream( String name )
        throws IOException
    {

        return ( this.getClass().getResourceAsStream( "/org/apache/commons/digester3/" + name ) );

    }

    static final class TestErrorHandler
        implements ErrorHandler
    {
        public boolean clean = true;

        public TestErrorHandler()
        {
        }

        public void error( SAXParseException exception )
        {
            clean = false;
        }

        public void fatalError( SAXParseException exception )
        {
            clean = false;
        }

        public void warning( SAXParseException exception )
        {
            clean = false;
        }
    }

}

/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */

package org.apache.directory.shared.dsmlv2.modifyRequest;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.dsmlv2.AbstractTest;
import org.apache.directory.shared.dsmlv2.Dsmlv2Parser;
import org.apache.directory.shared.ldap.model.entry.EntryAttribute;
import org.apache.directory.shared.ldap.model.entry.Modification;
import org.apache.directory.shared.ldap.model.entry.ModificationOperation;
import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.directory.shared.ldap.model.message.ModifyRequest;
import org.apache.directory.shared.util.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests for the Modify Request parsing
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
public class ModifyRequestTest extends AbstractTest
{
    /**
     * Test parsing of a request with the (optional) requestID attribute
     */
    @Test
    public void testRequestWithRequestId()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInput(
                ModifyRequestTest.class.getResource( "request_with_requestID_attribute.xml" ).openStream(), "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        ModifyRequest modifyRequest = ( ModifyRequest ) parser.getBatchRequest().getCurrentRequest();

        assertEquals( 456, modifyRequest.getMessageId() );
    }


    /**
     * Test parsing of a request with the (optional) requestID attribute equals to 0
     */
    @Test
    public void testRequestWithRequestIdEquals0()
    {
        testParsingFail( ModifyRequestTest.class, "request_with_requestID_equals_0.xml" );
    }


    /**
     * Test parsing of a request with a (optional) Control element
     */
    @Test
    public void testRequestWith1Control()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInput( ModifyRequestTest.class.getResource( "request_with_1_control.xml" ).openStream(), "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        ModifyRequest modifyRequest = ( ModifyRequest ) parser.getBatchRequest().getCurrentRequest();
        Map<String, Control> controls = modifyRequest.getControls();

        assertEquals( 1, modifyRequest.getControls().size() );

        Control control = controls.get( "1.2.840.113556.1.4.643" );

        assertNotNull( control );
        assertTrue( control.isCritical() );
        assertEquals( "1.2.840.113556.1.4.643", control.getOid() );
        assertEquals( "Some text", Strings.utf8ToString((byte[]) control.getValue()) );
    }


    /**
     * Test parsing of a request with a (optional) Control element with Base64 Value
     */
    @Test
    public void testRequestWith1ControlBase64Value()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInput( ModifyRequestTest.class.getResource( "request_with_1_control_base64_value.xml" )
                .openStream(), "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        ModifyRequest modifyRequest = ( ModifyRequest ) parser.getBatchRequest().getCurrentRequest();
        Map<String, Control> controls = modifyRequest.getControls();

        assertEquals( 1, modifyRequest.getControls().size() );

        Control control = controls.get( "1.2.840.113556.1.4.643" );

        assertNotNull( control );
        assertTrue( control.isCritical() );
        assertEquals( "1.2.840.113556.1.4.643", control.getOid() );
        assertEquals( "DSMLv2.0 rocks!!", Strings.utf8ToString((byte[]) control.getValue()) );
    }


    /**
     * Test parsing of a request with a (optional) Control element with empty value
     */
    @Test
    public void testRequestWith1ControlEmptyValue()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInput( ModifyRequestTest.class.getResource( "request_with_1_control_empty_value.xml" )
                .openStream(), "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        ModifyRequest modifyRequest = ( ModifyRequest ) parser.getBatchRequest().getCurrentRequest();
        Map<String, Control> controls = modifyRequest.getControls();

        assertEquals( 1, modifyRequest.getControls().size() );

        Control control = controls.get( "1.2.840.113556.1.4.643" );

        assertNotNull( control );
        assertTrue( control.isCritical() );
        assertEquals( "1.2.840.113556.1.4.643", control.getOid() );
        assertFalse( control.hasValue() );
    }


    /**
     * Test parsing of a request with 2 (optional) Control elements
     */
    @Test
    public void testRequestWith2Controls()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser
                .setInput( ModifyRequestTest.class.getResource( "request_with_2_controls.xml" ).openStream(), "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        ModifyRequest modifyRequest = ( ModifyRequest ) parser.getBatchRequest().getCurrentRequest();
        Map<String, Control> controls = modifyRequest.getControls();

        assertEquals( 2, modifyRequest.getControls().size() );

        Control control = controls.get( "1.2.840.113556.1.4.789" );

        assertNotNull( control );
        assertFalse( control.isCritical() );
        assertEquals( "1.2.840.113556.1.4.789", control.getOid() );
        assertEquals( "Some other text", Strings.utf8ToString((byte[]) control.getValue()) );
    }


    /**
     * Test parsing of a request with 3 (optional) Control elements without value
     */
    @Test
    public void testRequestWith3ControlsWithoutValue()
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInput( ModifyRequestTest.class.getResource( "request_with_3_controls_without_value.xml" )
                .openStream(), "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        ModifyRequest modifyRequest = ( ModifyRequest ) parser.getBatchRequest().getCurrentRequest();
        Map<String, Control> controls = modifyRequest.getControls();

        assertEquals( 3, modifyRequest.getControls().size() );

        Control control = controls.get( "1.2.840.113556.1.4.456" );

        assertNotNull( control );
        assertTrue( control.isCritical() );
        assertEquals( "1.2.840.113556.1.4.456", control.getOid() );
        assertFalse( control.hasValue() );
    }


    /**
     * Test parsing of a request without dn attribute
     */
    @Test
    public void testRequestWithoutDnAttribute()
    {
        testParsingFail( ModifyRequestTest.class, "request_without_dn_attribute.xml" );
    }


    /**
     * Test parsing of a request with a Modification element
     * @throws NamingException 
     */
    @Test
    public void testRequestWith1Modification() throws NamingException
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInput( ModifyRequestTest.class.getResource( "request_with_1_modification.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        ModifyRequest modifyRequest = ( ModifyRequest ) parser.getBatchRequest().getCurrentRequest();
        Collection<Modification> modifications = modifyRequest.getModifications();
        assertEquals( 1, modifications.size() );

        Modification modification = modifications.iterator().next();

        assertEquals( ModificationOperation.ADD_ATTRIBUTE, modification.getOperation() );

        EntryAttribute attribute = modification.getAttribute();

        assertEquals( "directreport", attribute.getId() );
        assertEquals( "CN=John Smith, DC=microsoft, DC=com", attribute.get( 0 ).getString() );
    }


    /**
     * Test parsing of a request with a Modification element with Base64 Value
     * @throws NamingException 
     * @throws UnsupportedEncodingException 
     */
    @Test
    public void testRequestWith1ModificationBase64Value() throws NamingException, UnsupportedEncodingException
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInput( ModifyRequestTest.class.getResource( "request_with_1_modification_base64_value.xml" )
                .openStream(), "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        ModifyRequest modifyRequest = ( ModifyRequest ) parser.getBatchRequest().getCurrentRequest();

        Collection<Modification> modifications = modifyRequest.getModifications();
        assertEquals( 1, modifications.size() );

        Modification modification = modifications.iterator().next();
        EntryAttribute attribute = modification.getAttribute();

        assertEquals( ModificationOperation.ADD_ATTRIBUTE, modification.getOperation() );

        assertEquals( "directreport", attribute.getId() );

        String expected = new String( new byte[]
            { 'c', 'n', '=', 'E', 'm', 'm', 'a', 'n', 'u', 'e', 'l', ' ', 'L', ( byte ) 0xc3, ( byte ) 0xa9, 'c', 'h',
                'a', 'r', 'n', 'y', ',', ' ', 'o', 'u', '=', 'p', 'e', 'o', 'p', 'l', 'e', ',', ' ', 'd', 'c', '=',
                'e', 'x', 'a', 'm', 'p', 'l', 'e', ',', ' ', 'd', 'c', '=', 'c', 'o', 'm' }, "UTF-8" );

        assertEquals( expected, attribute.get( 0 ).getString() );
    }


    /**
     * Test parsing of a request with 2 Modification elements
     * @throws NamingException 
     */
    @Test
    public void testRequestWith2Modifications() throws NamingException
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInput( ModifyRequestTest.class.getResource( "request_with_2_modifications.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        ModifyRequest modifyRequest = ( ModifyRequest ) parser.getBatchRequest().getCurrentRequest();

        Collection<Modification> modifications = modifyRequest.getModifications();
        assertEquals( 2, modifications.size() );

        Iterator<Modification> iter = modifications.iterator();

        Modification modification = iter.next();

        assertEquals( ModificationOperation.ADD_ATTRIBUTE, modification.getOperation() );
        EntryAttribute attribute = modification.getAttribute();
        assertEquals( "directreport", attribute.getId() );

        assertEquals( "CN=John Smith, DC=microsoft, DC=com", attribute.get( 0 ).getString() );

        modification = iter.next();

        attribute = modification.getAttribute();

        assertEquals( "sn", attribute.getId() );
        assertEquals( ModificationOperation.REPLACE_ATTRIBUTE, modification.getOperation() );

        assertEquals( "CN=Steve Jobs, DC=apple, DC=com", attribute.get( 0 ).getString() );
    }


    /**
     * Test parsing of a request without name attribute to the Modification element
     */
    @Test
    public void testRequestWithoutNameAttribute()
    {
        testParsingFail( ModifyRequestTest.class, "request_without_name_attribute.xml" );
    }


    /**
     * Test parsing of a request without operation attribute to the Modification element
     */
    @Test
    public void testRequestWithoutOperationAttribute()
    {
        testParsingFail( ModifyRequestTest.class, "request_without_operation_attribute.xml" );
    }


    /**
     * Test parsing of a request with operation attribute to Add value
     * @throws NamingException 
     */
    @Test
    public void testRequestWithOperationAdd() throws NamingException
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInput( ModifyRequestTest.class.getResource( "request_with_operation_add.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        ModifyRequest modifyRequest = ( ModifyRequest ) parser.getBatchRequest().getCurrentRequest();

        Collection<Modification> modifications = modifyRequest.getModifications();
        assertEquals( 1, modifications.size() );

        Modification modification = modifications.iterator().next();

        assertEquals( ModificationOperation.ADD_ATTRIBUTE, modification.getOperation() );
    }


    /**
     * Test parsing of a request with operation attribute to Delete value
     * @throws NamingException 
     */
    @Test
    public void testRequestWithOperationDelete() throws NamingException
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInput( ModifyRequestTest.class.getResource( "request_with_operation_delete.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        ModifyRequest modifyRequest = ( ModifyRequest ) parser.getBatchRequest().getCurrentRequest();

        Collection<Modification> modifications = modifyRequest.getModifications();
        assertEquals( 1, modifications.size() );

        Modification modification = modifications.iterator().next();

        assertEquals( ModificationOperation.REMOVE_ATTRIBUTE, modification.getOperation() );
    }


    /**
     * Test parsing of a request with operation attribute to Replace value
     * @throws NamingException 
     */
    @Test
    public void testRequestWithOperationReplace() throws NamingException
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInput( ModifyRequestTest.class.getResource( "request_with_operation_replace.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        ModifyRequest modifyRequest = ( ModifyRequest ) parser.getBatchRequest().getCurrentRequest();

        Collection<Modification> modifications = modifyRequest.getModifications();
        assertEquals( 1, modifications.size() );

        Modification modification = modifications.iterator().next();

        assertEquals( ModificationOperation.REPLACE_ATTRIBUTE, modification.getOperation() );
    }


    /**
     * Test parsing of a request without operation attribute to the Modification element
     */
    @Test
    public void testRequestWithOperationError()
    {
        testParsingFail( ModifyRequestTest.class, "request_with_operation_error.xml" );
    }


    /**
     * Test parsing of a request with a Modification element without Value element
     * @throws NamingException 
     */
    @Test
    public void testRequestWithModificationWithoutValue() throws NamingException
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInput( ModifyRequestTest.class.getResource( "request_with_modification_without_value.xml" )
                .openStream(), "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        ModifyRequest modifyRequest = ( ModifyRequest ) parser.getBatchRequest().getCurrentRequest();

        Collection<Modification> modifications = modifyRequest.getModifications();
        assertEquals( 1, modifications.size() );

        Modification modification = modifications.iterator().next();

        assertEquals( ModificationOperation.ADD_ATTRIBUTE, modification.getOperation() );
        EntryAttribute attribute = modification.getAttribute();

        assertEquals( "directreport", attribute.getId() );
        assertEquals( 0, attribute.size() );
    }


    /**
     * Test parsing of a request with a Modification element
     * @throws NamingException 
     */
    @Test
    public void testRequestWithModificationWith2Values() throws NamingException
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInput( ModifyRequestTest.class.getResource( "request_with_modification_with_2_values.xml" )
                .openStream(), "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        ModifyRequest modifyRequest = ( ModifyRequest ) parser.getBatchRequest().getCurrentRequest();

        Collection<Modification> modifications = modifyRequest.getModifications();
        assertEquals( 1, modifications.size() );

        Modification modification = modifications.iterator().next();

        assertEquals( ModificationOperation.ADD_ATTRIBUTE, modification.getOperation() );
        EntryAttribute attribute = modification.getAttribute();

        assertEquals( "directreport", attribute.getId() );

        assertEquals( 2, attribute.size() );
        assertEquals( "CN=John Smith, DC=microsoft, DC=com", attribute.get( 0 ).getString() );
        assertEquals( "CN=Steve Jobs, DC=apple, DC=com", attribute.get( 1 ).getString() );
    }


    /**
     * Test parsing of a request with a Modification element with an empty value
     * @throws NamingException 
     */
    @Test
    public void testRequestWithModificationWithEmptyValue() throws NamingException
    {
        Dsmlv2Parser parser = null;
        try
        {
            parser = new Dsmlv2Parser();

            parser.setInput( ModifyRequestTest.class.getResource( "request_with_modification_with_empty_value.xml" )
                .openStream(), "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        ModifyRequest modifyRequest = ( ModifyRequest ) parser.getBatchRequest().getCurrentRequest();

        Collection<Modification> modifications = modifyRequest.getModifications();
        assertEquals( 1, modifications.size() );

        Modification modification = modifications.iterator().next();

        assertEquals( ModificationOperation.ADD_ATTRIBUTE, modification.getOperation() );
        EntryAttribute attribute = modification.getAttribute();

        assertEquals( "directreport", attribute.getId() );

        assertEquals( 1, attribute.size() );
        assertEquals( "", attribute.get( 0 ).getString() );
    }


    /**
     * Test parsing of a request with a needed requestID attribute
     * 
     * DIRSTUDIO-1
     */
    @Test
    public void testRequestWithNeededRequestId()
    {
        testParsingFail( ModifyRequestTest.class, "request_with_needed_requestID.xml" );
    }
}

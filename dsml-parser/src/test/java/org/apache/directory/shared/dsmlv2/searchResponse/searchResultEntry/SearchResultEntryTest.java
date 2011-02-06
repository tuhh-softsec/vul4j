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
package org.apache.directory.shared.dsmlv2.searchResponse.searchResultEntry;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.dsmlv2.AbstractResponseTest;
import org.apache.directory.shared.dsmlv2.DsmlControl;
import org.apache.directory.shared.dsmlv2.Dsmlv2ResponseParser;
import org.apache.directory.shared.dsmlv2.reponse.SearchResponse;
import org.apache.directory.shared.dsmlv2.reponse.SearchResponseDsml;
import org.apache.directory.shared.ldap.model.entry.Entry;
import org.apache.directory.shared.ldap.model.entry.EntryAttribute;
import org.apache.directory.shared.ldap.model.entry.Value;
import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.directory.shared.ldap.model.message.SearchResultEntry;
import org.apache.directory.shared.util.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests for the Search Result Entry Response parsing
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
public class SearchResultEntryTest extends AbstractResponseTest
{
    /**
     * Test parsing of a response with a (optional) Control element
     */
    @Test
    public void testResponseWith1Control()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser( getCodec() );

            parser.setInput( SearchResultEntryTest.class.getResource( "response_with_1_control.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        SearchResponse searchResponse = ( SearchResponse ) 
            parser.getBatchResponse().getCurrentResponse().getDecorated(); 
        SearchResultEntry searchResultEntry = 
            searchResponse.getCurrentSearchResultEntry();
        Map<String, Control> controls = 
            searchResponse.getCurrentSearchResultEntry().getControls();

        assertEquals( 1, searchResultEntry.getControls().size() );

        Control control = controls.get( "1.2.840.113556.1.4.643" );

        assertNotNull( control );
        assertTrue( control.isCritical() );
        assertEquals( "1.2.840.113556.1.4.643", control.getOid() );
        assertEquals( "Some text", Strings.utf8ToString((byte[]) ( ( DsmlControl<?> ) control ).getValue()) );
    }


    /**
     * Test parsing of a response with a (optional) Control element with empty value
     */
    @Test
    public void testResponseWith1ControlEmptyValue()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser( getCodec() );

            parser.setInput( SearchResultEntryTest.class.getResource( "response_with_1_control_empty_value.xml" )
                .openStream(), "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        SearchResponse searchResponse = ( SearchResponse ) 
            parser.getBatchResponse().getCurrentResponse().getDecorated(); 
        SearchResultEntry searchResultEntry = 
            searchResponse.getCurrentSearchResultEntry();
        Map<String, Control> controls = searchResultEntry.getControls();

        assertEquals( 1, searchResultEntry.getControls().size() );

        Control control = controls.get( "1.2.840.113556.1.4.643" );

        assertNotNull( control );
        assertTrue( control.isCritical() );
        assertEquals( "1.2.840.113556.1.4.643", control.getOid() );
        assertFalse( ( ( DsmlControl<?> ) control ).hasValue() );
    }


    /**
     * Test parsing of a response with 2 (optional) Control elements
     */
    @Test
    public void testResponseWith2Controls()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser( getCodec() );

            parser.setInput( SearchResultEntryTest.class.getResource( "response_with_2_controls.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        SearchResponse searchResponse = ( SearchResponse ) 
            parser.getBatchResponse().getCurrentResponse().getDecorated(); 
        SearchResultEntry searchResultEntry = 
            searchResponse.getCurrentSearchResultEntry();
        Map<String, Control> controls = searchResultEntry.getControls();

        assertEquals( 2, searchResultEntry.getControls().size() );

        Control control = controls.get( "1.2.840.113556.1.4.789" );

        assertNotNull( control );
        assertFalse( control.isCritical() );
        assertEquals( "1.2.840.113556.1.4.789", control.getOid() );
        assertEquals( "Some other text", Strings.utf8ToString((byte[]) ( ( DsmlControl<?> ) control ).getValue()) );
    }


    /**
     * Test parsing of a response with 3 (optional) Control elements without value
     */
    @Test
    public void testResponseWith3ControlsWithoutValue()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser( getCodec() );

            parser.setInput( SearchResultEntryTest.class.getResource( "response_with_3_controls_without_value.xml" )
                .openStream(), "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        SearchResponse searchResponse = ( SearchResponse ) 
            parser.getBatchResponse().getCurrentResponse().getDecorated(); 
        SearchResultEntry searchResultEntry = 
            searchResponse.getCurrentSearchResultEntry();
        Map<String, Control> controls = searchResultEntry.getControls();

        assertEquals( 3, searchResultEntry.getControls().size() );

        Control control = controls.get( "1.2.840.113556.1.4.456" );

        assertNotNull( control );
        assertTrue( control.isCritical() );
        assertEquals( "1.2.840.113556.1.4.456", control.getOid() );
        assertFalse( ( ( DsmlControl<?> ) control ).hasValue() );
    }


    /**
     * Test parsing of a response without dn Attribute
     */
    @Test
    public void testResponseWithoutDnAttribute()
    {
        testParsingFail( SearchResultEntryTest.class, "response_without_dn_attribute.xml" );
    }


    /**
     * Test parsing of a response with wrong dn Attribute
     */
    @Test
    public void testResponseWithWrongDnAttribute()
    {
        testParsingFail( SearchResultEntryTest.class, "response_with_wrong_dn_attribute.xml" );
    }


    /**
     * Test parsing of a response with dn Attribute
     */
    @Test
    public void testResponseWithDnAttribute()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser( getCodec() );

            parser.setInput( SearchResultEntryTest.class.getResource( "response_with_dn_attribute.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        SearchResponseDsml searchResponseDsml = ( SearchResponseDsml ) 
            parser.getBatchResponse().getCurrentResponse();
        SearchResponse response = ( SearchResponse ) searchResponseDsml.getDecorated();
        SearchResultEntry searchResultEntry = response.getSearchResultEntryList().get( 0 );
        
        assertEquals( "dc=example,dc=com", searchResultEntry.getObjectName().toString() );
    }


    /**
     * Test parsing of a Response with the (optional) requestID attribute
     */
    @Test
    public void testResponseWithRequestId()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser( getCodec() );

            parser.setInput( SearchResultEntryTest.class.getResource( "response_with_requestID_attribute.xml" )
                .openStream(), "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        SearchResultEntry searchResultEntry = ( ( SearchResponse ) parser.getBatchResponse().getCurrentResponse().getDecorated() )
            .getCurrentSearchResultEntry();

        assertEquals( 456, searchResultEntry.getMessageId() );
    }


    /**
     * Test parsing of a Response with the (optional) requestID attribute equals 0
     */
    @Test
    public void testResponseWithRequestIdEquals0()
    {
        testParsingFail( SearchResultEntryTest.class, "response_with_requestID_equals_0.xml" );
    }


    /**
     * Test parsing of a response with 0 Attr
     */
    @Test
    public void testResponseWith0Attr()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser( getCodec() );

            parser.setInput( SearchResultEntryTest.class.getResource( "response_with_0_attr.xml" ).openStream(),
                "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        assertTrue( true );
    }


    /**
     * Test parsing of a response with 1 Attr 0 Value
     */
    @Test
    public void testResponseWith1Attr0Value()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser( getCodec() );

            parser.setInput(
                SearchResultEntryTest.class.getResource( "response_with_1_attr_0_value.xml" ).openStream(), "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        SearchResultEntry searchResultEntry = ( ( SearchResponse ) parser.getBatchResponse().getCurrentResponse().getDecorated() )
            .getCurrentSearchResultEntry();

        Entry entry = searchResultEntry.getEntry();
        assertEquals( 1, entry.size() );

        Iterator<EntryAttribute> attributeIterator = entry.iterator();
        EntryAttribute attribute = attributeIterator.next();
        assertEquals( "dc", attribute.getUpId() );
    }


    /**
     * Test parsing of a response with 1 Attr 1 Value
     */
    @Test
    public void testResponseWith1Attr1Value()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser( getCodec() );

            parser.setInput(
                SearchResultEntryTest.class.getResource( "response_with_1_attr_1_value.xml" ).openStream(), "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        SearchResultEntry searchResultEntry = ( ( SearchResponse ) parser.getBatchResponse().getCurrentResponse().getDecorated() )
            .getCurrentSearchResultEntry();

        Entry entry = searchResultEntry.getEntry();
        assertEquals( 1, entry.size() );

        Iterator<EntryAttribute> attributeIterator = entry.iterator();
        EntryAttribute attribute = attributeIterator.next();
        assertEquals( "dc", attribute.getUpId() );

        Iterator<Value<?>> valueIterator = attribute.iterator();
        assertTrue( valueIterator.hasNext() );
        Value<?> value = valueIterator.next();
        assertEquals( "example", value.getString() );
    }


    /**
     * Test parsing of a response with 1 Attr 1 Base64 Value
     * @throws UnsupportedEncodingException 
     */
    @Test
    public void testResponseWith1Attr1Base64Value() throws UnsupportedEncodingException
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser( getCodec() );

            parser.setInput( SearchResultEntryTest.class.getResource( "response_with_1_attr_1_base64_value.xml" )
                .openStream(), "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        SearchResultEntry searchResultEntry = ( ( SearchResponse ) parser.getBatchResponse().getCurrentResponse().getDecorated() )
            .getCurrentSearchResultEntry();

        Entry entry = searchResultEntry.getEntry();
        assertEquals( 1, entry.size() );

        Iterator<EntryAttribute> attributeIterator = entry.iterator();
        EntryAttribute attribute = attributeIterator.next();
        assertEquals( "cn", attribute.getUpId() );
        assertEquals( 1, attribute.size() );

        Iterator<Value<?>> valueIterator = attribute.iterator();
        assertTrue( valueIterator.hasNext() );
        Value<?> value = valueIterator.next();

        String expected = new String( new byte[]
            { 'E', 'm', 'm', 'a', 'n', 'u', 'e', 'l', ' ', 'L', ( byte ) 0xc3, ( byte ) 0xa9, 'c', 'h', 'a', 'r', 'n',
                'y' }, "UTF-8" );
        assertEquals( expected, value.getString() );
    }


    /**
     * Test parsing of a response with 1 Attr 1 empty Value
     */
    @Test
    public void testResponseWith1Attr1EmptyValue()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser( getCodec() );

            parser.setInput( SearchResultEntryTest.class.getResource( "response_with_1_attr_1_empty_value.xml" )
                .openStream(), "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        SearchResultEntry searchResultEntry = ( ( SearchResponse ) parser.getBatchResponse().getCurrentResponse().getDecorated() )
            .getCurrentSearchResultEntry();

        Entry entry = searchResultEntry.getEntry();
        assertEquals( 1, entry.size() );

        Iterator<EntryAttribute> attributeIterator = entry.iterator();
        EntryAttribute attribute = attributeIterator.next();
        assertEquals( "dc", attribute.getUpId() );
        assertEquals( 1, attribute.size() );

        Iterator<Value<?>> valueIterator = attribute.iterator();
        assertTrue( valueIterator.hasNext() );
        Value<?> value = valueIterator.next();
        assertEquals( "", value.getString() );
    }


    /**
     * Test parsing of a response with 1 Attr 2 Value
     */
    @Test
    public void testResponseWith1Attr2Value()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser( getCodec() );

            parser.setInput(
                SearchResultEntryTest.class.getResource( "response_with_1_attr_2_value.xml" ).openStream(), "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        SearchResultEntry searchResultEntry = ( ( SearchResponse ) parser.getBatchResponse().getCurrentResponse().getDecorated() )
            .getCurrentSearchResultEntry();

        Entry entry = searchResultEntry.getEntry();
        assertEquals( 1, entry.size() );

        Iterator<EntryAttribute> attributeIterator = entry.iterator();
        EntryAttribute attribute = attributeIterator.next();
        assertEquals( "objectclass", attribute.getUpId() );
        assertEquals( 2, attribute.size() );

        Iterator<Value<?>> valueIterator = attribute.iterator();
        assertTrue( valueIterator.hasNext() );
        Value<?> value = valueIterator.next();
        assertEquals( "top", value.getString() );
        assertTrue( valueIterator.hasNext() );
        value = valueIterator.next();
        assertEquals( "domain", value.getString() );
        assertFalse( valueIterator.hasNext() );
    }


    /**
     * Test parsing of a response with 2 Attr 1 Value
     */
    @Test
    public void testResponseWith2Attr1Value()
    {
        Dsmlv2ResponseParser parser = null;
        try
        {
            parser = new Dsmlv2ResponseParser( getCodec() );

            parser.setInput(
                SearchResultEntryTest.class.getResource( "response_with_2_attr_1_value.xml" ).openStream(), "UTF-8" );

            parser.parse();
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }

        SearchResultEntry searchResultEntry = ( ( SearchResponse ) parser.getBatchResponse().getCurrentResponse().getDecorated() )
            .getCurrentSearchResultEntry();

        Entry entry = searchResultEntry.getEntry();
        assertEquals( 2, entry.size() );

        EntryAttribute objectClassAttribute = entry.get( "objectclass" );
        assertEquals( 1, objectClassAttribute.size() );

        Iterator<Value<?>> valueIterator = objectClassAttribute.iterator();
        assertTrue( valueIterator.hasNext() );
        Value<?> value = valueIterator.next();
        assertEquals( "top", value.getString() );
        assertFalse( valueIterator.hasNext() );

        EntryAttribute dcAttribute = entry.get( "dc" );
        assertEquals( 1, objectClassAttribute.size() );

        valueIterator = dcAttribute.iterator();
        assertTrue( valueIterator.hasNext() );
        value = valueIterator.next();
        assertEquals( "example", value.getString() );
        assertFalse( valueIterator.hasNext() );
    }


    /**
     * Test parsing of a response with 1 Attr without name Attribute
     */
    @Test
    public void testResponseWith1AttrWithoutNameAttribute()
    {
        testParsingFail( SearchResultEntryTest.class, "response_with_1_attr_without_name_attribute.xml" );
    }
}

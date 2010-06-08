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
package org.apache.directory.shared.ldap.codec.search.controls;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.asn1.ber.Asn1Decoder;
import org.apache.directory.shared.asn1.codec.DecoderException;
import org.apache.directory.shared.ldap.codec.search.controls.pagedSearch.PagedResultsControl;
import org.apache.directory.shared.ldap.codec.search.controls.pagedSearch.PagedResultsControlContainer;
import org.apache.directory.shared.ldap.codec.search.controls.pagedSearch.PagedResultsControlDecoder;
import org.apache.directory.shared.ldap.util.StringTools;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test the PagedSearchControlTest codec
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent(threads = 6)
public class PagedSearchControlTest
{
    /**
     * Test encoding of a PagedSearchControl.
     */
    @Test
    public void testEncodePagedSearchControl() throws Exception
    {
        Asn1Decoder decoder = new PagedResultsControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 0x2A );
        bb.put( new byte[]
            { 
                0x30, 0x09,                        // realSearchControlValue ::= SEQUENCE {
                  0x02, 0x01, 0x20,                // size INTEGER,
                  0x04, 0x04, 't', 'e', 's', 't'   // cookie OCTET STRING,
            } );
        bb.flip();

        PagedResultsControlContainer container = new PagedResultsControlContainer();
        container.setPagedSearchControl( new PagedResultsControl() );
        
        try
        {
            decoder.decode( bb, container );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        PagedResultsControl pagedSearch = container.getPagedSearchControl();
        assertEquals( 32, pagedSearch.getSize() );
        assertTrue( Arrays.equals( StringTools.getBytesUtf8( "test" ), 
            pagedSearch.getCookie() ) );
            
        bb.flip();

        ByteBuffer buffer = ByteBuffer.allocate( 0x27 );
        buffer.put( new byte[]
            { 
              0x30, 0x25,                            // Control
                0x04, 0x16,                          // OID (PagedSearch)
                  '1', '.', '2', '.', '8', '4', '0', '.', 
                  '1', '1', '3', '5', '5', '6', '.', '1', 
                  '.', '4', '.', '3', '1', '9',
                0x04, 0x0B,
                  0x30, 0x09,                        // realSearchControlValue ::= SEQUENCE {
                    0x02, 0x01, 0x20,                // size INTEGER,
                    0x04, 0x04, 't', 'e', 's', 't'   // cookie OCTET STRING,
            } );
        buffer.flip();

        PagedResultsControl ctrl = new PagedResultsControl();
        ctrl.setSize( 32 );
        ctrl.setCookie( StringTools.getBytesUtf8( "test" ) );

        bb = ctrl.encode( ByteBuffer.allocate( ctrl.computeLength() ) );
        String decoded = StringTools.dumpBytes( bb.array() );
        String expected = StringTools.dumpBytes( buffer.array() );
        assertEquals( expected, decoded );
    }
    
    
    /**
     * Test the decoding of a PagedSearchControl with no cookie
     */
    @Test
    public void testDecodePagedSearchRequestNoCookie()
    {
        Asn1Decoder decoder = new PagedResultsControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 0x05 );
        bb.put( new byte[]
            { 
            0x30, 0x03,         // realSearchControlValue ::= SEQUENCE {
              0x02, 0x01, 0x20  // size INTEGER,
            } );
        bb.flip();

        PagedResultsControlContainer container = new PagedResultsControlContainer();
        container.setPagedSearchControl( new PagedResultsControl() );
        
        try
        {
            decoder.decode( bb, container );
            fail();
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
        }
    }
    
    
    /**
     * Test the decoding of a PagedSearchControl with no size
     */
    @Test
    public void testDecodePagedSearchRequestNoSize()
    {
        Asn1Decoder decoder = new PagedResultsControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 0x08 );
        bb.put( new byte[]
            { 
            0x30, 0x06,                       // realSearchControlValue ::= SEQUENCE {
              0x04, 0x04, 't', 'e', 's', 't'  // cookie OCTET STRING,
            } );
        bb.flip();

        PagedResultsControlContainer container = new PagedResultsControlContainer();
        container.setPagedSearchControl( new PagedResultsControl() );
        
        try
        {
            decoder.decode( bb, container );
            fail();
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
        }
    }
    
    
    /**
     * Test the decoding of a PagedSearchControl with no size  and no cookie
     */
    @Test
    public void testDecodePagedSearchRequestNoSizeNoCookie()
    {
        Asn1Decoder decoder = new PagedResultsControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 0x02 );
        bb.put( new byte[]
            { 
            0x30, 0x00,         // realSearchControlValue ::= SEQUENCE 
            } );
        bb.flip();

        PagedResultsControlContainer container = new PagedResultsControlContainer();
        container.setPagedSearchControl( new PagedResultsControl() );
        
        try
        {
            decoder.decode( bb, container );
            fail();
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
        }
    }
    
    
    /**
     * Test encoding of a PagedSearchControl with a negative size
     */
    @Test
    public void testEncodePagedSearchControlNegativeSize() throws Exception
    {
        Asn1Decoder decoder = new PagedResultsControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 0x0b );
        bb.put( new byte[]
            { 
              0x30, 0x09,                        // realSearchControlValue ::= SEQUENCE {
                0x02, 0x01, (byte)0xFF,          // size INTEGER,
                0x04, 0x04, 't', 'e', 's', 't'   // cookie OCTET STRING,
            } );
        bb.flip();

        PagedResultsControlContainer container = new PagedResultsControlContainer();
        container.setPagedSearchControl( new PagedResultsControl() );
        
        try
        {
            decoder.decode( bb, container );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        PagedResultsControl pagedSearch = container.getPagedSearchControl();
        assertEquals( Integer.MAX_VALUE, pagedSearch.getSize() );
        assertTrue( Arrays.equals( StringTools.getBytesUtf8( "test" ), 
            pagedSearch.getCookie() ) );
            
        bb.flip();


        ByteBuffer buffer = ByteBuffer.allocate( 0x27 );
        buffer.put( new byte[]
            { 
              0x30, 0x25,                            // Control
                0x04, 0x16,                          // OID (PagedSearch)
                  '1', '.', '2', '.', '8', '4', '0', '.', 
                  '1', '1', '3', '5', '5', '6', '.', '1', 
                  '.', '4', '.', '3', '1', '9',
                0x04, 0x0B,
                  0x30, 0x09,                        // realSearchControlValue ::= SEQUENCE {
                    0x02, 0x01, (byte)0xFF,          // size INTEGER,
                    0x04, 0x04, 't', 'e', 's', 't'   // cookie OCTET STRING,
            } );
        buffer.flip();

        PagedResultsControl ctrl = new PagedResultsControl();
        ctrl.setSize( -1 );
        ctrl.setCookie( StringTools.getBytesUtf8( "test" ) );

        bb = ctrl.encode( ByteBuffer.allocate( ctrl.computeLength() ) );
        String decoded = StringTools.dumpBytes( bb.array() );
        String expected = StringTools.dumpBytes( buffer.array() );
        assertEquals( expected, decoded );
    }
    
    
    /**
     * Test encoding of a PagedSearchControl with a empty size
     */
    @Test
    public void testEncodePagedSearchControlEmptySize() throws Exception
    {
        Asn1Decoder decoder = new PagedResultsControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 0x0a );
        bb.put( new byte[]
            { 
              0x30, 0x08,                        // realSearchControlValue ::= SEQUENCE {
                0x02, 0x00,                      // size INTEGER,
                0x04, 0x04, 't', 'e', 's', 't'   // cookie OCTET STRING,
            } );
        bb.flip();

        PagedResultsControlContainer container = new PagedResultsControlContainer();
        container.setPagedSearchControl( new PagedResultsControl() );

        try
        {
            decoder.decode( bb, container );
            fail();
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
        }
    }
    
    
    /**
     * Test encoding of a PagedSearchControl with a empty cookie
     */
    @Test
    public void testEncodePagedSearchControlEmptyCookie() throws Exception
    {
        Asn1Decoder decoder = new PagedResultsControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 0x07 );
        bb.put( new byte[]
            { 
              0x30, 0x05,           // realSearchControlValue ::= SEQUENCE {
                0x02, 0x01, 0x20,   // size INTEGER,
                0x04, 0x00          // cookie OCTET STRING,
            } );
        bb.flip();

        PagedResultsControlContainer container = new PagedResultsControlContainer();
        container.setPagedSearchControl( new PagedResultsControl() );

        try
        {
            decoder.decode( bb, container );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        PagedResultsControl pagedSearch = container.getPagedSearchControl();
        assertEquals( 32, pagedSearch.getSize() );
        assertNotNull( pagedSearch.getCookie() );
        assertEquals( StringTools.EMPTY_BYTES, pagedSearch.getCookie() );
            
        ByteBuffer buffer = ByteBuffer.allocate( 0x23 );
        buffer.put( new byte[]
            { 
              0x30, 0x21,                            // Control
                0x04, 0x16,                          // OID (PagedSearch)
                  '1', '.', '2', '.', '8', '4', '0', '.', 
                  '1', '1', '3', '5', '5', '6', '.', '1', 
                  '.', '4', '.', '3', '1', '9',
                0x04, 0x07,
                  0x30, 0x05,                        // realSearchControlValue ::= SEQUENCE {
                    0x02, 0x01, 0x20,                // size INTEGER,
                    0x04, 0x00                       // cookie OCTET STRING,
            } );
        buffer.flip();

        PagedResultsControl ctrl = new PagedResultsControl();
        ctrl.setSize( 32 );
        ctrl.setCookie( null );

        bb = ctrl.encode( ByteBuffer.allocate( ctrl.computeLength() ) );
        String decoded = StringTools.dumpBytes( bb.array() );
        String expected = StringTools.dumpBytes( buffer.array() );
        assertEquals( expected, decoded );

    }
}
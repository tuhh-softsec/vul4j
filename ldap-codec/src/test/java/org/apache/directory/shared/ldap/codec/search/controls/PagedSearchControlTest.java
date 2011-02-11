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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.mycila.junit.concurrent.Concurrency;
import com.mycila.junit.concurrent.ConcurrentJunitRunner;
import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.ldap.codec.api.DefaultLdapCodecService;
import org.apache.directory.shared.ldap.codec.api.LdapCodecService;
import org.apache.directory.shared.ldap.codec.controls.search.pagedSearch.PagedResultsDecorator;
import org.apache.directory.shared.ldap.model.message.controls.PagedResults;
import org.apache.directory.shared.util.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test the PagedSearchControlTest codec
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrency()
public class PagedSearchControlTest
{
    private LdapCodecService codec = new DefaultLdapCodecService();

    /**
     * Test encoding of a PagedSearchControl.
     */
    @Test
    public void testEncodePagedSearchControl() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x0B );
        bb.put( new byte[]
            { 
                0x30, 0x09,                        // realSearchControlValue ::= SEQUENCE {
                  0x02, 0x01, 0x20,                // size INTEGER,
                  0x04, 0x04, 't', 'e', 's', 't'   // cookie OCTET STRING,
            } );
        bb.flip();

        PagedResultsDecorator decorator = new PagedResultsDecorator( codec );
        
        PagedResults pagedSearch = (PagedResults)decorator.decode( bb.array() );

        assertEquals( 32, pagedSearch.getSize() );
        assertTrue( Arrays.equals( Strings.getBytesUtf8("test"),
            pagedSearch.getCookie() ) );
            
        bb.flip();

        PagedResultsDecorator ctrl = new PagedResultsDecorator( codec );
        ctrl.setSize( 32 );
        ctrl.setCookie( Strings.getBytesUtf8("test") );

        ByteBuffer buffer = ctrl.encode( ByteBuffer.allocate( ctrl.computeLength() ) );
        String decoded = Strings.dumpBytes( buffer.array() );
        String expected = Strings.dumpBytes( bb.array() );
        assertEquals( expected, decoded );
    }
    
    
    /**
     * Test the decoding of a PagedSearchControl with no cookie
     */
    @Test( expected=DecoderException.class )
    public void testDecodePagedSearchRequestNoCookie() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x05 );
        bb.put( new byte[]
            { 
            0x30, 0x03,         // realSearchControlValue ::= SEQUENCE {
              0x02, 0x01, 0x20  // size INTEGER,
            } );
        bb.flip();

        PagedResultsDecorator decorator = new PagedResultsDecorator( codec );
        
        decorator.decode( bb.array() );
    }
    
    
    /**
     * Test the decoding of a PagedSearchControl with no size
     */
    @Test( expected=DecoderException.class )
    public void testDecodePagedSearchRequestNoSize() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x08 );
        bb.put( new byte[]
            { 
            0x30, 0x06,                       // realSearchControlValue ::= SEQUENCE {
              0x04, 0x04, 't', 'e', 's', 't'  // cookie OCTET STRING,
            } );
        bb.flip();

        PagedResultsDecorator decorator = new PagedResultsDecorator( codec );
        
        decorator.decode( bb.array() );
    }
    
    
    /**
     * Test the decoding of a PagedSearchControl with no size  and no cookie
     */
    @Test( expected=DecoderException.class )
    public void testDecodePagedSearchRequestNoSizeNoCookie() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x02 );
        bb.put( new byte[]
            { 
            0x30, 0x00,         // realSearchControlValue ::= SEQUENCE 
            } );
        bb.flip();

        PagedResultsDecorator decorator = new PagedResultsDecorator( codec );
        
        decorator.decode( bb.array() );
    }
    
    
    /**
     * Test encoding of a PagedSearchControl with a negative size
     */
    @Test
    public void testEncodePagedSearchControlNegativeSize() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x0b );
        bb.put( new byte[]
            { 
              0x30, 0x09,                        // realSearchControlValue ::= SEQUENCE {
                0x02, 0x01, (byte)0xFF,          // size INTEGER,
                0x04, 0x04, 't', 'e', 's', 't'   // cookie OCTET STRING,
            } );
        bb.flip();

        PagedResultsDecorator decorator = new PagedResultsDecorator( codec );
        
        PagedResults pagedSearch = (PagedResults)decorator.decode( bb.array() );

        assertEquals( Integer.MAX_VALUE, pagedSearch.getSize() );
        assertTrue( Arrays.equals( Strings.getBytesUtf8("test"),
            pagedSearch.getCookie() ) );
            
        bb.flip();


        PagedResultsDecorator ctrl = new PagedResultsDecorator( codec );
        ctrl.setSize( -1 );
        ctrl.setCookie( Strings.getBytesUtf8("test") );

        ByteBuffer buffer = ctrl.encode( ByteBuffer.allocate( ctrl.computeLength() ) );
        String decoded = Strings.dumpBytes( buffer.array() );
        String expected = Strings.dumpBytes( bb.array() );
        assertEquals( expected, decoded );
    }
    
    
    /**
     * Test encoding of a PagedSearchControl with a empty size
     */
    @Test( expected=DecoderException.class )
    public void testEncodePagedSearchControlEmptySize() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x0a );
        bb.put( new byte[]
            { 
              0x30, 0x08,                        // realSearchControlValue ::= SEQUENCE {
                0x02, 0x00,                      // size INTEGER,
                0x04, 0x04, 't', 'e', 's', 't'   // cookie OCTET STRING,
            } );
        bb.flip();

        PagedResultsDecorator decorator = new PagedResultsDecorator( codec );
        
        decorator.decode( bb.array() );
    }
    
    
    /**
     * Test encoding of a PagedSearchControl with a empty cookie
     */
    @Test
    public void testEncodePagedSearchControlEmptyCookie() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x07 );
        bb.put( new byte[]
            { 
              0x30, 0x05,           // realSearchControlValue ::= SEQUENCE {
                0x02, 0x01, 0x20,   // size INTEGER,
                0x04, 0x00          // cookie OCTET STRING,
            } );
        bb.flip();

        PagedResultsDecorator decorator = new PagedResultsDecorator( codec );
        
        PagedResults pagedSearch = (PagedResults)decorator.decode( bb.array() );

        assertEquals( 32, pagedSearch.getSize() );
        assertNull( pagedSearch.getCookie() );
            
        PagedResultsDecorator ctrl = new PagedResultsDecorator( codec );
        ctrl.setSize( 32 );
        ctrl.setCookie( null );

        ByteBuffer buffer = ctrl.encode( ByteBuffer.allocate( ctrl.computeLength() ) );
        String decoded = Strings.dumpBytes( buffer.array() );
        String expected = Strings.dumpBytes( bb.array() );
        assertEquals( expected, decoded );
    }
}
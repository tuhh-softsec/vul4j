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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.ldap.codec.api.DefaultLdapCodecService;
import org.apache.directory.shared.ldap.codec.api.LdapCodecService;
import org.apache.directory.shared.ldap.codec.controls.search.subentries.SubentriesDecorator;
import org.apache.directory.shared.ldap.model.message.controls.Subentries;
import org.apache.directory.shared.util.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test the SubEntryControlTest codec
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
public class SubEntryControlTest
{
    private LdapCodecService codec = new DefaultLdapCodecService();

    /**
     * Test the decoding of a SubEntryControl with a true visibility
     */
    @Test
    public void testDecodeSubEntryVisibilityTrue() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x03 );
        bb.put( new byte[]
            { 
              0x01, 0x01, ( byte ) 0xFF // Visibility ::= BOOLEAN
            } );
        bb.flip();

        SubentriesDecorator decorator = new SubentriesDecorator( codec );
        
        Subentries subentries = (Subentries)decorator.decode( bb.array() );

        assertTrue( subentries.isVisible() );
        
        // test encoding
        try
        {
            ByteBuffer buffer = decorator.encode( ByteBuffer.allocate( decorator.computeLength() ) );
            String expected = Strings.dumpBytes( bb.array() );
            String decoded = Strings.dumpBytes( buffer.array() );
            assertEquals( expected, decoded );
        }
        catch( EncoderException e )
        {
            fail( e.getMessage() );
        }
    }


    /**
     * Test the decoding of a SubEntryControl with a false visibility
     */
    @Test
    public void testDecodeSubEntryVisibilityFalse() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x03 );
        bb.put( new byte[]
            { 
              0x01, 0x01, 0x00 // Visibility ::= BOOLEAN
            } );
        bb.flip();

        SubentriesDecorator decorator = new SubentriesDecorator( codec );
        
        Subentries subentries = (Subentries)decorator.decode( bb.array() );

        assertFalse( subentries.isVisible() );
        
        // test encoding
        try
        {
            ByteBuffer buffer = decorator.encode( ByteBuffer.allocate( decorator.computeLength() ) );
            String expected = Strings.dumpBytes( bb.array() );
            String decoded = Strings.dumpBytes( buffer.array() );
            assertEquals( expected, decoded );
        }
        catch( EncoderException e )
        {
            fail( e.getMessage() );
        }
    }


    /**
     * Test the decoding of a SubEntryControl with an empty visibility
     */
    @Test( expected=DecoderException.class )
    public void testDecodeSubEntryEmptyVisibility() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x02 );

        bb.put( new byte[]
            { 
              0x01, 0x00 // Visibility ::= BOOLEAN
            } );

        bb.flip();

        // Allocate a LdapMessage Container
        SubentriesDecorator decorator = new SubentriesDecorator( codec );
        
        decorator.decode( bb.array() );
    }


    /**
     * Test the decoding of a bad SubEntryControl
     */
    @Test( expected=DecoderException.class )
    public void testDecodeSubEntryBad() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x03 );

        bb.put( new byte[]
            { 
              0x02, 0x01, 0x01 // Visibility ::= BOOLEAN
            } );

        bb.flip();

        // Allocate a LdapMessage Container
        SubentriesDecorator decorator = new SubentriesDecorator( codec );
        
        decorator.decode( bb.array() );
    }
}

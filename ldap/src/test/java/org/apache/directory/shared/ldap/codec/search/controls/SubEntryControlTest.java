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
import org.apache.directory.shared.asn1.ber.Asn1Decoder;
import org.apache.directory.shared.asn1.codec.DecoderException;
import org.apache.directory.shared.asn1.codec.EncoderException;
import org.apache.directory.shared.ldap.codec.search.controls.subentries.SubentriesControl;
import org.apache.directory.shared.ldap.codec.search.controls.subentries.SubentriesControlContainer;
import org.apache.directory.shared.ldap.codec.search.controls.subentries.SubentriesControlDecoder;
import org.apache.directory.shared.ldap.util.StringTools;
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
    /**
     * Test the decoding of a SubEntryControl with a true visibility
     */
    @Test
    public void testDecodeSubEntryVisibilityTrue()
    {
        Asn1Decoder decoder = new SubentriesControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 0x03 );
        bb.put( new byte[]
            { 
              0x01, 0x01, ( byte ) 0xFF // Visibility ::= BOOLEAN
            } );
        bb.flip();

        SubentriesControlContainer container = new SubentriesControlContainer();
        container.setSubEntryControl( new SubentriesControl() );
        
        try
        {
            decoder.decode( bb, container );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        SubentriesControl control = container.getSubEntryControl();
        assertTrue( control.isVisible() );
        // test encoding
        try
        {
            ByteBuffer buffer = ByteBuffer.allocate( 0x20 );
            buffer.put( new byte[]
                { 
                0x30, 0x1E,                            // Control
                  0x04, 0x17,                          // OID (Subentries)
                    '1', '.', '3', '.', '6', '.', '1', '.', 
                    '4', '.', '1', '.', '4', '2', '0', '3', 
                    '.', '1', '.', '1', '0', '.', '1',
                  0x04, 0x03,
                    0x01, 0x01, (byte)0xFF // Visibility ::= BOOLEAN
                } );

            buffer.flip();

            bb = control.encode( ByteBuffer.allocate( control.computeLength() ) );
            String expected = StringTools.dumpBytes( buffer.array() );
            String decoded = StringTools.dumpBytes( bb.array() );
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
    public void testDecodeSubEntryVisibilityFalse()
    {
        Asn1Decoder decoder = new SubentriesControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 0x03 );
        bb.put( new byte[]
            { 
              0x01, 0x01, 0x00 // Visibility ::= BOOLEAN
            } );
        bb.flip();

        SubentriesControlContainer container = new SubentriesControlContainer();
        container.setSubEntryControl( new SubentriesControl() );

        try
        {
            decoder.decode( bb, container );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        SubentriesControl control = container.getSubEntryControl();
        assertFalse( control.isVisible() );
        
        // test encoding
        try
        {
            ByteBuffer buffer = ByteBuffer.allocate( 0x20 );
            buffer.put( new byte[]
                { 
                0x30, 0x1E,                            // Control
                  0x04, 0x17,                          // OID (Subentries)
                    '1', '.', '3', '.', '6', '.', '1', '.', 
                    '4', '.', '1', '.', '4', '2', '0', '3', 
                    '.', '1', '.', '1', '0', '.', '1',
                  0x04, 0x03,
                    0x01, 0x01, 0x00 // Visibility ::= BOOLEAN
                } );

            buffer.flip();

            bb = control.encode( ByteBuffer.allocate( control.computeLength() ) );
            String expected = StringTools.dumpBytes( buffer.array() );
            String decoded = StringTools.dumpBytes( bb.array() );
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
    @Test
    public void testDecodeSubEntryEmptyVisibility()
    {
        Asn1Decoder decoder = new SubentriesControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 0x02 );

        bb.put( new byte[]
            { 
              0x01, 0x00 // Visibility ::= BOOLEAN
            } );

        bb.flip();

        // Allocate a LdapMessage Container
        SubentriesControlContainer container = new SubentriesControlContainer();
        container.setSubEntryControl( new SubentriesControl() );

        // Decode a SubEntryControl PDU
        try
        {
            decoder.decode( bb, container );
            fail( "We should never reach this point !!!" );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
        }
    }


    /**
     * Test the decoding of a bad SubEntryControl
     */
    @Test
    public void testDecodeSubEntryBad()
    {
        Asn1Decoder decoder = new SubentriesControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 0x03 );

        bb.put( new byte[]
            { 
              0x02, 0x01, 0x01 // Visibility ::= BOOLEAN
            } );

        bb.flip();

        // Allocate a LdapMessage Container
        SubentriesControlContainer container = new SubentriesControlContainer();
        container.setSubEntryControl( new SubentriesControl() );

        // Decode a SubEntryControl PDU
        try
        {
            decoder.decode( bb, container );
            fail( "We should never reach this point !!!" );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
        }
    }
}

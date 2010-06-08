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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.asn1.ber.Asn1Decoder;
import org.apache.directory.shared.asn1.codec.DecoderException;
import org.apache.directory.shared.ldap.codec.search.controls.persistentSearch.PersistentSearchControl;
import org.apache.directory.shared.ldap.codec.search.controls.persistentSearch.PersistentSearchControlContainer;
import org.apache.directory.shared.ldap.codec.search.controls.persistentSearch.PersistentSearchControlDecoder;
import org.apache.directory.shared.ldap.util.StringTools;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test the PSearchControlTest codec
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent(threads = 6)
public class PSearchControlTest
{
    /**
     * Test encoding of a PSearchControl.
     */
    @Test
    public void testEncodePSearchControl() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x28 );
        bb.put( new byte[]
            { 
            0x30, 0x26,                            // Control
              0x04, 0x17,                          // OID (SyncRequestValue)
                '2', '.', '1', '6', '.', '8', '4', '0', 
                '.', '1', '.', '1', '1', '3', '7', '3', 
                '0', '.', '3', '.', '4', '.', '3',
              0x04, 0x0B,
                0x30, 0x09,           // PersistentSearch ::= SEQUENCE {
                  0x02, 0x01, 0x01,   // changeTypes INTEGER,
                  0x01, 0x01, 0x00,   // changesOnly BOOLEAN,
                  0x01, 0x01, 0x00    // returnECs BOOLEAN
            } );

        String expected = StringTools.dumpBytes( bb.array() );
        bb.flip();

        PersistentSearchControl ctrl = new PersistentSearchControl();
        ctrl.setChangesOnly( false );
        ctrl.setReturnECs( false );
        ctrl.setChangeTypes( 1 );
        bb = ctrl.encode(ByteBuffer.allocate( ctrl.computeLength() ) );
        String decoded = StringTools.dumpBytes( bb.array() );
        assertEquals( expected, decoded );
    }

    /**
     * Test the decoding of a PSearchControl with combined changes types
     */
    @Test
    public void testDecodeModifyDNRequestSuccessChangeTypesAddModDN()
    {
        Asn1Decoder decoder = new PersistentSearchControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 0x0b );
        bb.put( new byte[]
            { 
            0x30, 0x09,         // PersistentSearch ::= SEQUENCE {
              0x02, 0x01, 0x09, // changeTypes INTEGER,
              0x01, 0x01, 0x00, // changesOnly BOOLEAN,
              0x01, 0x01, 0x00  // returnECs BOOLEAN
            } );
        bb.flip();

        PersistentSearchControlContainer container = new PersistentSearchControlContainer();
        container.setPSearchControl( new PersistentSearchControl() );

        try
        {
            decoder.decode( bb, container );
        }
        catch ( DecoderException de )
        {
            fail( de.getMessage() );
        }

        PersistentSearchControl control = container.getPSearchControl();
        int changeTypes = control.getChangeTypes();
        assertEquals( PersistentSearchControl.CHANGE_TYPE_ADD, changeTypes & PersistentSearchControl.CHANGE_TYPE_ADD );
        assertEquals( PersistentSearchControl.CHANGE_TYPE_MODDN, changeTypes & PersistentSearchControl.CHANGE_TYPE_MODDN );
        assertEquals( false, control.isChangesOnly() );
        assertEquals( false, control.isReturnECs() );
    }

    /**
     * Test the decoding of a PSearchControl with a changes types which
     * value is 0
     */
    @Test
    public void testDecodeModifyDNRequestSuccessChangeTypes0()
    {
        Asn1Decoder decoder = new PersistentSearchControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 0x0b );
        bb.put( new byte[]
            { 
            0x30, 0x09,         // PersistentSearch ::= SEQUENCE {
              0x02, 0x01, 0x00, // changeTypes INTEGER,
              0x01, 0x01, 0x00, // changesOnly BOOLEAN,
              0x01, 0x01, 0x00  // returnECs BOOLEAN
            } );
        bb.flip();

        PersistentSearchControlContainer container = new PersistentSearchControlContainer();
        container.setPSearchControl( new PersistentSearchControl() );
        
        try
        {
            decoder.decode( bb, container );
            fail( "We should never reach this point" );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
        }
    }

    /**
     * Test the decoding of a PSearchControl with a changes types which
     * value is above 15
     */
    @Test
    public void testDecodeModifyDNRequestSuccessChangeTypes22()
    {
        Asn1Decoder decoder = new PersistentSearchControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 0x0b );
        bb.put( new byte[]
            { 
            0x30, 0x09,         // PersistentSearch ::= SEQUENCE {
              0x02, 0x01, 0x22, // changeTypes INTEGER,
              0x01, 0x01, 0x00, // changesOnly BOOLEAN,
              0x01, 0x01, 0x00  // returnECs BOOLEAN
            } );
        bb.flip();

        PersistentSearchControlContainer container = new PersistentSearchControlContainer();
        container.setPSearchControl( new PersistentSearchControl() );

        try
        {
            decoder.decode( bb, container );
            fail( "We should never reach this point" );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
        }
    }

    /**
     * Test the decoding of a PSearchControl with a null sequence
     */
    @Test
    public void testDecodeModifyDNRequestSuccessNullSequence()
    {
        Asn1Decoder decoder = new PersistentSearchControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 0x02 );
        bb.put( new byte[]
            { 
            0x30, 0x00,         // PersistentSearch ::= SEQUENCE {
            } );
        bb.flip();

        PersistentSearchControlContainer container = new PersistentSearchControlContainer();
        container.setPSearchControl( new PersistentSearchControl() );

        try
        {
            decoder.decode( bb, container );
            fail( "We should never reach this point" );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
        }
    }

    /**
     * Test the decoding of a PSearchControl without changeTypes
     */
    @Test
    public void testDecodeModifyDNRequestSuccessWithoutChangeTypes()
    {
        Asn1Decoder decoder = new PersistentSearchControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 0x08 );
        bb.put( new byte[]
            { 
            0x30, 0x06,         // PersistentSearch ::= SEQUENCE {
              0x01, 0x01, 0x00, // changesOnly BOOLEAN,
              0x01, 0x01, 0x00  // returnECs BOOLEAN
            } );
        bb.flip();

        PersistentSearchControlContainer container = new PersistentSearchControlContainer();
        container.setPSearchControl( new PersistentSearchControl() );

        try
        {
            decoder.decode( bb, container );
            fail( "We should never reach this point" );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
        }
    }

    /**
     * Test the decoding of a PSearchControl without changeOnly
     */
    @Test
    public void testDecodeModifyDNRequestSuccessWithoutChangesOnly()
    {
        Asn1Decoder decoder = new PersistentSearchControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 0x08 );
        bb.put( new byte[]
            { 
            0x30, 0x06,         // PersistentSearch ::= SEQUENCE {
              0x02, 0x01, 0x01, // changeTypes INTEGER,
              0x01, 0x01, 0x00  // returnECs BOOLEAN
            } );
        bb.flip();

        PersistentSearchControlContainer container = new PersistentSearchControlContainer();
        container.setPSearchControl( new PersistentSearchControl() );

        try
        {
            decoder.decode( bb, container );
            fail( "We should never reach this point" );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
        }
    }

    /**
     * Test the decoding of a PSearchControl without returnECs
     */
    @Test
    public void testDecodeModifyDNRequestSuccessWithoutReturnECs()
    {
        Asn1Decoder decoder = new PersistentSearchControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 0x08 );
        bb.put( new byte[]
            { 
            0x30, 0x06,         // PersistentSearch ::= SEQUENCE {
              0x02, 0x01, 0x01, // changeTypes INTEGER,
              0x01, 0x01, 0x00, // changesOnly BOOLEAN,
            } );
        bb.flip();

        PersistentSearchControlContainer container = new PersistentSearchControlContainer();
        container.setPSearchControl( new PersistentSearchControl() );

        try
        {
            decoder.decode( bb, container );
            fail( "We should never reach this point" );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
        }
    }
}

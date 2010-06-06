/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */

package org.apache.directory.shared.ldap.codec.controls.replication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;

import org.apache.directory.shared.asn1.ber.Asn1Decoder;
import org.apache.directory.shared.asn1.codec.DecoderException;
import org.apache.directory.shared.asn1.codec.EncoderException;
import org.apache.directory.shared.ldap.codec.controls.replication.syncmodifydn.SyncModifyDnControl;
import org.apache.directory.shared.ldap.codec.controls.replication.syncmodifydn.SyncModifyDnControlContainer;
import org.apache.directory.shared.ldap.codec.controls.replication.syncmodifydn.SyncModifyDnControlDecoder;
import org.apache.directory.shared.ldap.util.StringTools;
import org.junit.Test;

/**
 * TODO SyncModifyDnControlTest.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SyncModifyDnControlTest
{
    
    @Test
    public void testDecodeSyncModifyDnControlWithMoveOperation()
    {
        Asn1Decoder decoder = new SyncModifyDnControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 0x12 );
        bb.put( new byte[]
            { 
              0x30, 0x10,                                // SyncModifyDnControl ::= SEQUENCE {
                0x04, 0x07, 'u','i','d','=','j','i','m', //     entryDn LDAPDN
                ( byte )0x80, 0x05,                     //     move
                  'o','u','=','d','c'         //     newSuperiorDn LDAPDN
            } );
        bb.flip();

        SyncModifyDnControlContainer container = new SyncModifyDnControlContainer();
        container.setSyncModifyDnControl( new SyncModifyDnControl() );

        try
        {
            decoder.decode( bb, container );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        SyncModifyDnControl syncmodDnControl = container.getSyncModifyDnControl();
        assertEquals( "uid=jim", syncmodDnControl.getEntryDn() );
        assertEquals( "ou=dc", syncmodDnControl.getNewSuperiorDn() );
        assertFalse( syncmodDnControl.isDeleteOldRdn() );

        // Check the encoding
        try
        {

            ByteBuffer buffer = ByteBuffer.allocate( 48 );
            buffer.put( new byte[]
                { 
                  0x30, 0x2E,                            // Control
                    0x04, 0x18,                          // OID (SuncStateValue)
                      '1', '.', '3', '.', '6', '.', '1', '.', 
                      '4', '.', '1', '.', '4', '2', '0', '3', 
                      '.', '1', '.', '9', '.', '1', '.', '5',
                     0x04, 0x12, 
                     0x30, 0x10, 
                      0x04, 0x07, 'u','i','d','=','j','i','m', //     entryDn entryDn
                      ( byte )0x80, 0x05,                     //     move
                      'o','u','=','d','c'         //     newSuperiorDn LDAPDN
                } );
            buffer.flip();

            ByteBuffer encoded = syncmodDnControl.encode( ByteBuffer.allocate( syncmodDnControl.computeLength() ) );
            assertEquals( StringTools.dumpBytes( buffer.array() ), StringTools.dumpBytes( encoded.array() ) );
                }
        catch ( EncoderException ee )
        {
            fail( ee.getMessage() );
        }
    }

    
    @Test
    public void testDecodeSyncModifyDnControlWithRenameOperation()
    {
        Asn1Decoder decoder = new SyncModifyDnControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 0x17 );
        bb.put( new byte[]
            { 
              0x30, 0x15,                                // SyncModifyDnControl ::= SEQUENCE {
                0x04, 0x07, 'u','i','d','=','j','i','m', //     entryDn LDAPDN
                ( byte )0x00A1, 0x0A,                    //     rename
                  0x04, 0x05, 'u','i','d','=','j',       //     newRdn
                  0x01, 0x01, ( byte ) 0xFF                       //     deleteOldRdn
            } );
        bb.flip();

        SyncModifyDnControlContainer container = new SyncModifyDnControlContainer();
        container.setSyncModifyDnControl( new SyncModifyDnControl() );

        try
        {
            decoder.decode( bb, container );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        SyncModifyDnControl syncmodDnControl = container.getSyncModifyDnControl();
        assertEquals( "uid=jim", syncmodDnControl.getEntryDn() );
        assertEquals( "uid=j", syncmodDnControl.getNewRdn() );
        assertTrue( syncmodDnControl.isDeleteOldRdn() );

        // Check the encoding
        try
        {

            ByteBuffer buffer = ByteBuffer.allocate( 53 );
            buffer.put( new byte[]
                { 
                  0x30, 0x33,                            // Control
                    0x04, 0x18,                          // OID (SuncStateValue)
                      '1', '.', '3', '.', '6', '.', '1', '.', 
                      '4', '.', '1', '.', '4', '2', '0', '3', 
                      '.', '1', '.', '9', '.', '1', '.', '5',
                      0x04, 0x17,
                      0x30, 0x15,                                // SyncModifyDnControl ::= SEQUENCE {
                      0x04, 0x07, 'u','i','d','=','j','i','m', //     entryDn LDAPDN
                      ( byte )0x00A1, 0x0A,                    //     rename
                        0x04, 0x05, 'u','i','d','=','j',       //     newRdn
                        0x01, 0x01, ( byte ) 0xFF                       //     deleteOldRdn
                } );
            buffer.flip();

            ByteBuffer encoded = syncmodDnControl.encode( ByteBuffer.allocate( syncmodDnControl.computeLength() ) );
            assertEquals( StringTools.dumpBytes( buffer.array() ), StringTools.dumpBytes( encoded.array() ) );
                }
        catch ( EncoderException ee )
        {
            fail( ee.getMessage() );
        }
    }

    
    @Test
    public void testDecodeSyncModifyDnControlWithRenameAndMoveOperation()
    {
        Asn1Decoder decoder = new SyncModifyDnControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 0x1E );
        bb.put( new byte[]
            { 
              0x30, 0x1C,                                // SyncModifyDnControl ::= SEQUENCE {
                0x04, 0x07, 'u','i','d','=','j','i','m', //     entryDn LDAPDN
                ( byte )0x00A2, 0x11,                    //     rename
                  0x04, 0x05, 'o','u','=','d','c',       //     newSuperiorDn
                  0x04, 0x05, 'u','i','d','=','j',       //     newRdn
                  0x01, 0x01, ( byte ) 0xFF                       //     deleteOldRdn
            } );
        bb.flip();

        SyncModifyDnControlContainer container = new SyncModifyDnControlContainer();
        container.setSyncModifyDnControl( new SyncModifyDnControl() );

        try
        {
            decoder.decode( bb, container );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        SyncModifyDnControl syncmodDnControl = container.getSyncModifyDnControl();
        assertEquals( "uid=jim", syncmodDnControl.getEntryDn() );
        assertEquals( "ou=dc", syncmodDnControl.getNewSuperiorDn() );
        assertEquals( "uid=j", syncmodDnControl.getNewRdn() );
        assertTrue( syncmodDnControl.isDeleteOldRdn() );

        // Check the encoding
        try
        {

            ByteBuffer buffer = ByteBuffer.allocate( 60 );
            buffer.put( new byte[]
                { 
                  0x30, 0x3A,                            // Control
                    0x04, 0x18,                          // OID (SuncStateValue)
                      '1', '.', '3', '.', '6', '.', '1', '.', 
                      '4', '.', '1', '.', '4', '2', '0', '3', 
                      '.', '1', '.', '9', '.', '1', '.', '5',
                      0x04, 0x1E,
                      0x30, 0x1C,                                // SyncModifyDnControl ::= SEQUENCE {
                      0x04, 0x07, 'u','i','d','=','j','i','m', //     entryDn LDAPDN
                      ( byte )0x00A2, 0x11,                    //     rename
                        0x04, 0x05, 'o','u','=','d','c',       //     newSuperiorDn
                        0x04, 0x05, 'u','i','d','=','j',       //     newRdn
                        0x01, 0x01, ( byte ) 0xFF                       //     deleteOldRdn
                } );
            buffer.flip();

            ByteBuffer encoded = syncmodDnControl.encode( ByteBuffer.allocate( syncmodDnControl.computeLength() ) );
            assertEquals( StringTools.dumpBytes( buffer.array() ), StringTools.dumpBytes( encoded.array() ) );
                }
        catch ( EncoderException ee )
        {
            fail( ee.getMessage() );
        }
    }

    
    @Test( expected=DecoderException.class)
    public void testDecodeSyncModifyDnControlWithIncorrectRenameOperationData() throws DecoderException
    {
        Asn1Decoder decoder = new SyncModifyDnControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 0xE );
        bb.put( new byte[]
            { 
              0x30, 0xC,                                // SyncModifyDnControl ::= SEQUENCE {
                0x04, 0x07, 'u','i','d','=','j','i','m', //     entryDn LDAPDN
                0x01, 0x01, ( byte ) 0xFF             //     deleteOldRdn
            } );
        bb.flip();

        SyncModifyDnControlContainer container = new SyncModifyDnControlContainer();
        container.setSyncModifyDnControl( new SyncModifyDnControl() );

        decoder.decode( bb, container );
    }
}

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
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;

import org.apache.directory.shared.asn1.ber.Asn1Decoder;
import org.apache.directory.shared.asn1.codec.DecoderException;
import org.apache.directory.shared.ldap.codec.controls.replication.syncmodifydn.SyncModifyDnControl;
import org.apache.directory.shared.ldap.codec.controls.replication.syncmodifydn.SyncModifyDnControlContainer;
import org.apache.directory.shared.ldap.codec.controls.replication.syncmodifydn.SyncModifyDnControlDecoder;
import org.junit.Test;

/**
 * TODO SyncModifyDnControlTest.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SyncModifyDnControlTest
{
    
    @Test
    public void testDecodeSyncModifyDnControlWithMoveOperation()
    {
        Asn1Decoder decoder = new SyncModifyDnControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 28 );
        bb.put( new byte[]
            { 
              0x30, 0x1A,                                // SyncModifyDnControl ::= SEQUENCE {
                 0x04, 0x07, 'u','i','d','=','j','i','m', //     entryDn LDAPDN
                ( byte )0x0080, 0x07,                     //     move
                  0x04, 0x05, 'o','u','=','d','c'         //     newSuperiorDn LDAPDN
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

        /*
        // Check the encoding
        try
        {

            ByteBuffer buffer = ByteBuffer.allocate( 56 );
            buffer.put( new byte[]
                { 
                  0x30, 0x36,                            // Control
                    0x04, 0x18,                          // OID (SuncStateValue)
                      '1', '.', '3', '.', '6', '.', '1', '.', 
                      '4', '.', '1', '.', '4', '2', '0', '3', 
                      '.', '1', '.', '9', '.', '1', '.', '5',
                    0x04, 0x1A,
                     0x30, 0x18, 
                      0x04, 0x07, 'u','i','d','=','j','i','m', //     entryDn entryDn
                      0x04, 0x05, 'u','i','d','=','j',         //     newSuperiorDn newSuperiorDn OPTIONAL,
                      0x04, 0x03, 'x', '=', 'y',               //     newRdn newRdn OPTIONAL,
                      0x01, 0x01, 0x00                         //     deleteOldRdn deleteOldRdn OPTIONAL
                } );
            buffer.flip();

            ByteBuffer encoded = syncmodDnControl.encode( ByteBuffer.allocate( syncmodDnControl.computeLength() ) );
            assertEquals( StringTools.dumpBytes( buffer.array() ), StringTools.dumpBytes( encoded.array() ) );
                }
        catch ( EncoderException ee )
        {
            fail( ee.getMessage() );
        }
        */
    }
}

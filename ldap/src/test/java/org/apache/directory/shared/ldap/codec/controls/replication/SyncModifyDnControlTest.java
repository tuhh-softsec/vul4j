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

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.ldap.codec.DefaultLdapCodecService;
import org.apache.directory.shared.ldap.codec.ILdapCodecService;
import org.apache.directory.shared.ldap.codec.controls.replication.syncmodifydn.ISyncModifyDn;
import org.apache.directory.shared.ldap.codec.controls.replication.syncmodifydn.SyncModifyDnContainer;
import org.apache.directory.shared.ldap.codec.controls.replication.syncmodifydn.SyncModifyDnDecorator;
import org.apache.directory.shared.util.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * TODO SyncModifyDnControlTest.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
public class SyncModifyDnControlTest
{
    private ILdapCodecService codec = new DefaultLdapCodecService();
    
    
    @Test
    public void testDecodeSyncModifyDnControlWithMoveOperation() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x12 );
        bb.put( new byte[]
            { 
              0x30, 0x10,                                // SyncModifyDnControl ::= SEQUENCE {
                0x04, 0x07, 'u','i','d','=','j','i','m', //     entryDn LDAPDN
                ( byte )0x80, 0x05,                     //     move
                  'o','u','=','d','c'         //     newSuperiorDn LDAPDN
            } );
        bb.flip();

        SyncModifyDnContainer container = new SyncModifyDnContainer( codec );
        SyncModifyDnDecorator decorator = container.getSyncModifyDnControl();

        ISyncModifyDn syncmodDnControl = (ISyncModifyDn)decorator.decode( bb.array() );

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

            ByteBuffer encoded = ((SyncModifyDnDecorator)syncmodDnControl).encode( ByteBuffer.allocate( ((SyncModifyDnDecorator)syncmodDnControl).computeLength() ) );
            assertEquals( Strings.dumpBytes(buffer.array()), Strings.dumpBytes(encoded.array()) );
        }
        catch ( EncoderException ee )
        {
            fail( ee.getMessage() );
        }
    }

    
    @Test
    public void testDecodeSyncModifyDnControlWithRenameOperation() throws Exception
    {
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

        SyncModifyDnContainer container = new SyncModifyDnContainer( codec );
        SyncModifyDnDecorator decorator = container.getSyncModifyDnControl();

        ISyncModifyDn syncmodDnControl = (ISyncModifyDn)decorator.decode( bb.array() );

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

            ByteBuffer encoded = ((SyncModifyDnDecorator)syncmodDnControl).encode( ByteBuffer.allocate( ((SyncModifyDnDecorator)syncmodDnControl).computeLength() ) );
            assertEquals( Strings.dumpBytes(buffer.array()), Strings.dumpBytes(encoded.array()) );
                }
        catch ( EncoderException ee )
        {
            fail( ee.getMessage() );
        }
    }

    
    @Test
    public void testDecodeSyncModifyDnControlWithRenameAndMoveOperation() throws Exception
    {
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

        SyncModifyDnContainer container = new SyncModifyDnContainer( codec );
        SyncModifyDnDecorator decorator = container.getSyncModifyDnControl();

        ISyncModifyDn syncmodDnControl = (ISyncModifyDn)decorator.decode( bb.array() );

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

            ByteBuffer encoded = ((SyncModifyDnDecorator)syncmodDnControl).encode( ByteBuffer.allocate( ((SyncModifyDnDecorator)syncmodDnControl).computeLength() ) );
            assertEquals( Strings.dumpBytes(buffer.array()), Strings.dumpBytes(encoded.array()) );
                }
        catch ( EncoderException ee )
        {
            fail( ee.getMessage() );
        }
    }

    
    @Test( expected=DecoderException.class)
    public void testDecodeSyncModifyDnControlWithIncorrectRenameOperationData() throws DecoderException
    {
        ByteBuffer bb = ByteBuffer.allocate( 0xE );
        bb.put( new byte[]
            { 
              0x30, 0xC,                                // SyncModifyDnControl ::= SEQUENCE {
                0x04, 0x07, 'u','i','d','=','j','i','m', //     entryDn LDAPDN
                0x01, 0x01, ( byte ) 0xFF             //     deleteOldRdn
            } );
        bb.flip();

        SyncModifyDnContainer container = new SyncModifyDnContainer( codec );
        SyncModifyDnDecorator decorator = container.getSyncModifyDnControl();
        decorator.decode( bb.array() );
    }
}

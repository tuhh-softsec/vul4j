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
package org.apache.directory.shared.ldap.codec.controls.replication;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.asn1.ber.Asn1Decoder;
import org.apache.directory.shared.asn1.codec.DecoderException;
import org.apache.directory.shared.asn1.codec.EncoderException;
import org.apache.directory.shared.ldap.codec.controls.replication.syncStateValue.SyncStateValueControl;
import org.apache.directory.shared.ldap.codec.controls.replication.syncStateValue.SyncStateValueControlContainer;
import org.apache.directory.shared.ldap.codec.controls.replication.syncStateValue.SyncStateValueControlDecoder;
import org.apache.directory.shared.ldap.message.control.replication.SyncStateTypeEnum;
import org.apache.directory.shared.ldap.util.StringTools;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test the SyncStateControlValue codec
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent(threads = 6)
public class SyncStateValueControlTest
{
    /**
     * Test the decoding of a SyncStateValue control with a refreshOnly mode
     */
    @Test
    public void testDecodeSyncStateValueControlWithStateType()
    {
        Asn1Decoder decoder = new SyncStateValueControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 16 );
        bb.put( new byte[]
            { 
              0x30, ( byte ) 14,               // SyncStateValue ::= SEQUENCE {
                0x0A, 0x01, 0x00,              //     state ENUMERATED {
                                               //         present (0)
                                               //     }
                0x04, 0x03, 'a', 'b', 'c',     //     entryUUID syncUUID OPTIONAL,
                0x04, 0x04, 'x', 'k', 'c', 'd' //     cookie syncCookie OPTIONAL,
            } );
        bb.flip();

        SyncStateValueControlContainer container = new SyncStateValueControlContainer();
        container.setSyncStateValueControl( new SyncStateValueControl() );

        try
        {
            decoder.decode( bb, container );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        SyncStateValueControl syncStateValue = container.getSyncStateValueControl();
        assertEquals( SyncStateTypeEnum.PRESENT, syncStateValue.getSyncStateType() );
        assertEquals( "abc", StringTools.utf8ToString( syncStateValue.getEntryUUID() ) );
        assertEquals( "xkcd", StringTools.utf8ToString( syncStateValue.getCookie() ) );

        // Check the encoding
        try
        {
            ByteBuffer buffer = ByteBuffer.allocate( 0x2E );
            buffer.put( new byte[]
                { 
                  0x30, 0x2C,                            // Control
                    0x04, 0x18,                          // OID (SuncStateValue)
                      '1', '.', '3', '.', '6', '.', '1', '.', 
                      '4', '.', '1', '.', '4', '2', '0', '3', 
                      '.', '1', '.', '9', '.', '1', '.', '2',
                    0x04, 0x10,
                      0x30, 0x0E,                        // SyncStateValue ::= SEQUENCE {
                        0x0A, 0x01, 0x00,                //     state ENUMERATED {
                                                         //         present (0)
                                                         //     }
                        0x04, 0x03, 'a', 'b', 'c',       //     entryUUID syncUUID OPTIONAL,
                        0x04, 0x04, 'x', 'k', 'c', 'd'   //     cookie syncCookie OPTIONAL,
                } );
            buffer.flip();

            ByteBuffer encoded = syncStateValue.encode( ByteBuffer.allocate( syncStateValue.computeLength() ) );
            assertEquals( StringTools.dumpBytes( buffer.array() ), StringTools.dumpBytes( encoded.array() ) );
        }
        catch ( EncoderException ee )
        {
            fail( ee.getMessage() );
        }
    }


    /**
     * Test the decoding of a SyncStateValue control with no cookie
     */
    @Test
    public void testDecodeSyncStateValueControlNoCookie()
    {
        Asn1Decoder decoder = new SyncStateValueControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 10 );
        bb.put( new byte[]
            { 0x30, 0x08,                 // SyncStateValue ::= SEQUENCE {
                0x0A, 0x01, 0x01,         //     state ENUMERATED {
                                          //         add (1)
                                          //     }
                0x04, 0x03, 'a', 'b', 'c' //     entryUUID syncUUID OPTIONAL
            } );
        bb.flip();

        SyncStateValueControlContainer container = new SyncStateValueControlContainer();
        container.setSyncStateValueControl( new SyncStateValueControl() );

        try
        {
            decoder.decode( bb, container );
        }
        catch ( DecoderException de )
        {
            fail( de.getMessage() );
        }

        SyncStateValueControl syncStateValue = container.getSyncStateValueControl();
        assertEquals( SyncStateTypeEnum.ADD, syncStateValue.getSyncStateType() );
        assertEquals( "abc", StringTools.utf8ToString( syncStateValue.getEntryUUID() ) );
        assertNull( syncStateValue.getCookie() );

        // Check the encoding
        try
        {
            ByteBuffer buffer = ByteBuffer.allocate( 0x28 );
            buffer.put( new byte[]
                { 
                  0x30, 0x26,                            // Control
                    0x04, 0x18,                          // OID (SuncStateValue)
                      '1', '.', '3', '.', '6', '.', '1', '.', 
                      '4', '.', '1', '.', '4', '2', '0', '3', 
                      '.', '1', '.', '9', '.', '1', '.', '2',
                    0x04, 0x0A,
                      0x30, 0x08,                        // SyncStateValue ::= SEQUENCE {
                        0x0A, 0x01, 0x01,                //     state ENUMERATED {
                                                         //         add (1)
                                                         //     }
                        0x04, 0x03, 'a', 'b', 'c'        //     entryUUID syncUUID OPTIONAL,
                } );
            buffer.flip();

            ByteBuffer encoded = syncStateValue.encode( ByteBuffer.allocate( syncStateValue.computeLength() ) );
            assertEquals( StringTools.dumpBytes( buffer.array() ), StringTools.dumpBytes( encoded.array() ) );
        }
        catch ( EncoderException ee )
        {
            fail( ee.getMessage() );
        }
    }


    /**
     * Test the decoding of a SyncStateValue control with an empty cookie
     */
    @Test
    public void testDecodeSyncStateValueControlEmptyCookie()
    {
        Asn1Decoder decoder = new SyncStateValueControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 0x0C );
        bb.put( new byte[]
            { 0x30, 0x0A,                  // SyncStateValue ::= SEQUENCE {
                0x0A, 0x01, 0x02,          //     state ENUMERATED {
                                           //         modify (2)
                                           //     }
                0x04, 0x03, 'a', 'b', 'c', //     entryUUID syncUUID OPTIONAL
                0x04, 0x00                 //     cookie syncCookie OPTIONAL,
            } );
        bb.flip();

        SyncStateValueControlContainer container = new SyncStateValueControlContainer();
        container.setSyncStateValueControl( new SyncStateValueControl() );

        try
        {
            decoder.decode( bb, container );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        SyncStateValueControl syncStateValue = container.getSyncStateValueControl();
        assertEquals( SyncStateTypeEnum.MODIFY, syncStateValue.getSyncStateType() );
        assertEquals( "abc", StringTools.utf8ToString( syncStateValue.getEntryUUID() ) );
        assertEquals( "", StringTools.utf8ToString( syncStateValue.getCookie() ) );

        // Check the encoding
        try
        {
            ByteBuffer buffer = ByteBuffer.allocate( 0x28 );
            buffer.put( new byte[]
                { 
                  0x30, 0x26,                            // Control
                    0x04, 0x18,                          // OID (SuncStateValue)
                      '1', '.', '3', '.', '6', '.', '1', '.', 
                      '4', '.', '1', '.', '4', '2', '0', '3', 
                      '.', '1', '.', '9', '.', '1', '.', '2',
                    0x04, 0x0A,
                      0x30, 0x08,                        // SyncStateValue ::= SEQUENCE {
                        0x0A, 0x01, 0x02,                //     state ENUMERATED {
                                                         //         modify (2)
                                                         //     }
                        0x04, 0x03, 'a', 'b', 'c'        //     entryUUID syncUUID OPTIONAL,
                } );
            buffer.flip();

            ByteBuffer encoded = syncStateValue.encode( ByteBuffer.allocate( syncStateValue.computeLength() ) );
            assertEquals( StringTools.dumpBytes( buffer.array() ), StringTools.dumpBytes( encoded.array() ) );
        }
        catch ( EncoderException ee )
        {
            fail( ee.getMessage() );
        }
    }


    /**
     * Test the decoding of a SyncStateValue control with an empty sequence
     */
    @Test
    public void testDecodeSyncStateValueControlEmptySequence()
    {
        Asn1Decoder decoder = new SyncStateValueControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 0x02 );
        bb.put( new byte[]
            { 0x30, 0x00 // SyncStateValue ::= SEQUENCE {
            } );
        bb.flip();

        SyncStateValueControlContainer container = new SyncStateValueControlContainer();
        container.setSyncStateValueControl( new SyncStateValueControl() );

        try
        {
            decoder.decode( bb, container );
            fail( "we should not get there" );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
        }
    }


    /**
     * Test the decoding of a SyncStateValue control with no syncState
     */
    @Test
    public void testDecodeSyncStateValueControlNoSyancState()
    {
        Asn1Decoder decoder = new SyncStateValueControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 0x07 );
        bb.put( new byte[]
            { 0x30, 0x05,                 // SyncStateValue ::= SEQUENCE {
                0x04, 0x03, 'a', 'b', 'c' //     cookie syncCookie OPTIONAL,
            } );
        bb.flip();

        SyncStateValueControlContainer container = new SyncStateValueControlContainer();
        container.setSyncStateValueControl( new SyncStateValueControl() );

        try
        {
            decoder.decode( bb, container );
            fail( "we should not get there" );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
        }
    }


    /**
     * Test the decoding of a SyncStateValue control with no syncUUID
     */
    @Test
    public void testDecodeSyncStateValueControlNoSyncUUID()
    {
        Asn1Decoder decoder = new SyncStateValueControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 0x05 );
        bb.put( new byte[]
            { 0x30, 0x03,                  // SyncStateValue ::= SEQUENCE {
                0x0A, 0x01, 0x02,          //     state ENUMERATED {
                                           //         modify (2)
                                           //     }
            } );
        bb.flip();

        SyncStateValueControlContainer container = new SyncStateValueControlContainer();
        container.setSyncStateValueControl( new SyncStateValueControl() );

        try
        {
            decoder.decode( bb, container );
            fail( "we should not get there" );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
        }
    }
    
    
    /**
     * Test the decoding of a SyncStateValue control with a refreshOnly mode
     * and MODDN state type
     */
    @Test
    public void testDecodeSyncStateValueControlWithModDnStateType()
    {
        Asn1Decoder decoder = new SyncStateValueControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 16 );
        bb.put( new byte[]
            { 
              0x30, ( byte ) 14,               // SyncStateValue ::= SEQUENCE {
                0x0A, 0x01, 0x04,              //     state ENUMERATED {
                                               //         present (0)
                                               //     }
                0x04, 0x03, 'a', 'b', 'c',     //     entryUUID syncUUID OPTIONAL,
                0x04, 0x04, 'x', 'k', 'c', 'd' //     cookie syncCookie OPTIONAL,
            } );
        bb.flip();

        SyncStateValueControlContainer container = new SyncStateValueControlContainer();
        container.setSyncStateValueControl( new SyncStateValueControl() );

        try
        {
            decoder.decode( bb, container );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        SyncStateValueControl syncStateValue = container.getSyncStateValueControl();
        assertEquals( SyncStateTypeEnum.MODDN, syncStateValue.getSyncStateType() );
        assertEquals( "abc", StringTools.utf8ToString( syncStateValue.getEntryUUID() ) );
        assertEquals( "xkcd", StringTools.utf8ToString( syncStateValue.getCookie() ) );

        // Check the encoding
        try
        {
            ByteBuffer buffer = ByteBuffer.allocate( 0x2E );
            buffer.put( new byte[]
                { 
                  0x30, 0x2C,                            // Control
                    0x04, 0x18,                          // OID (SuncStateValue)
                      '1', '.', '3', '.', '6', '.', '1', '.', 
                      '4', '.', '1', '.', '4', '2', '0', '3', 
                      '.', '1', '.', '9', '.', '1', '.', '2',
                    0x04, 0x10,
                      0x30, 0x0E,                        // SyncStateValue ::= SEQUENCE {
                        0x0A, 0x01, 0x04,                //     state ENUMERATED {
                                                         //         present (0)
                                                         //     }
                        0x04, 0x03, 'a', 'b', 'c',       //     entryUUID syncUUID OPTIONAL,
                        0x04, 0x04, 'x', 'k', 'c', 'd'   //     cookie syncCookie OPTIONAL,
                } );
            buffer.flip();

            ByteBuffer encoded = syncStateValue.encode( ByteBuffer.allocate( syncStateValue.computeLength() ) );
            assertEquals( StringTools.dumpBytes( buffer.array() ), StringTools.dumpBytes( encoded.array() ) );
        }
        catch ( EncoderException ee )
        {
            fail( ee.getMessage() );
        }
    }
}

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
import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.ldap.codec.controls.replication.syncRequestValue.SyncRequestValueContainer;
import org.apache.directory.shared.ldap.codec.controls.replication.syncRequestValue.SyncRequestValueDecorator;
import org.apache.directory.shared.ldap.message.control.replication.SynchronizationModeEnum;
import org.apache.directory.shared.util.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test the SyncRequestControlValue codec
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
public class SyncRequestValueControlTest
{
    /**
     * Test the decoding of a SyncRequestValue control with a refreshOnly mode
     */
    @Test
    public void testDecodeSyncRequestValueControlRefreshOnlySuccess()
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x0D );
        bb.put( new byte[]
            { 
            0x30, 0x0B,                     // syncRequestValue ::= SEQUENCE {
              0x0A, 0x01, 0x01,             //     mode ENUMERATED {
                                            //         refreshOnly (1)
                                            //     }
              0x04, 0x03, 'a', 'b', 'c',    //     cookie syncCookie OPTIONAL,
              0x01, 0x01, 0x00              //     reloadHint BOOLEAN DEFAULT FALSE
            } );
        bb.flip();

        SyncRequestValueDecorator decorator = new SyncRequestValueDecorator();
        SyncRequestValueContainer container = new SyncRequestValueContainer( decorator );
        
        try
        {
            decorator.decode( bb.array() );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        SyncRequestValueDecorator syncRequestValue = container.getSyncRequestValueControl();
        assertEquals( SynchronizationModeEnum.REFRESH_ONLY, syncRequestValue.getMode() );
        assertEquals( "abc", Strings.utf8ToString(syncRequestValue.getCookie()) );
        assertEquals( false, syncRequestValue.isReloadHint() );

        // Check the encoding
        try
        {
            ByteBuffer buffer = ByteBuffer.allocate( 0x28 );
            buffer.put( new byte[]
                { 
                  0x30, 0x26,                            // Control
                    0x04, 0x18,                          // OID (SyncRequestValue)
                      '1', '.', '3', '.', '6', '.', '1', '.', 
                      '4', '.', '1', '.', '4', '2', '0', '3',
                      '.', '1', '.', '9', '.', '1', '.', '1',
                    0x04, 0x0A,
                      0x30, 0x08,                        // syncRequestValue ::= SEQUENCE {
                        0x0A, 0x01, 0x01,                //     mode ENUMERATED {
                                                         //         refreshOnly (1)
                                                         //     }
                        0x04, 0x03, 'a', 'b', 'c'        //     cookie syncCookie OPTIONAL,
                } );
            buffer.flip();

            bb = syncRequestValue.encode( ByteBuffer.allocate( syncRequestValue.computeLength() ) );
            String decoded = Strings.dumpBytes(bb.array());
            String expected = Strings.dumpBytes(buffer.array());
            assertEquals( expected, decoded );
        }
        catch ( EncoderException ee )
        {
            fail();
        }
    }


    /**
     * Test the decoding of a SyncRequestValue control with a refreshAndPersist mode
     */
    @Test
    public void testDecodeSyncRequestValueControlRefreshAndPersistSuccess()
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x0D );
        bb.put( new byte[]
            { 
            0x30, 0x0B,                     // syncRequestValue ::= SEQUENCE {
              0x0A, 0x01, 0x03,             //     mode ENUMERATED {
                                            //         refreshAndPersist (3)
                                            //     }
              0x04, 0x03, 'a', 'b', 'c',    //     cookie syncCookie OPTIONAL,
              0x01, 0x01, 0x00              //     reloadHint BOOLEAN DEFAULT FALSE
            } );
        bb.flip();

        SyncRequestValueContainer container = new SyncRequestValueContainer();
        SyncRequestValueDecorator decorator = container.getSyncRequestValueControl();
        
        try
        {
            decorator.decode( bb.array() );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        SyncRequestValueDecorator syncRequestValue = container.getSyncRequestValueControl();
        assertEquals( SynchronizationModeEnum.REFRESH_AND_PERSIST, syncRequestValue.getMode() );
        assertEquals( "abc", Strings.utf8ToString(syncRequestValue.getCookie()) );
        assertEquals( false, syncRequestValue.isReloadHint() );

        // Check the encoding
        try
        {
            ByteBuffer buffer = ByteBuffer.allocate( 0x28 );
            buffer.put( new byte[]
                { 
                  0x30, 0x26,                            // Control
                    0x04, 0x18,                          // OID (SyncRequestValue)
                      '1', '.', '3', '.', '6', '.', '1', '.', 
                      '4', '.', '1', '.', '4', '2', '0', '3',
                      '.', '1', '.', '9', '.', '1', '.', '1',
                    0x04, 0x0A,
                      0x30, 0x08,                        // syncRequestValue ::= SEQUENCE {
                        0x0A, 0x01, 0x03,                //     mode ENUMERATED {
                                                         //         refreshAndPersist (3)
                                                         //     }
                        0x04, 0x03, 'a', 'b', 'c'        //     cookie syncCookie OPTIONAL,
                } );
            buffer.flip();

            bb = syncRequestValue.encode( ByteBuffer.allocate( syncRequestValue.computeLength() ) );
            String decoded = Strings.dumpBytes(bb.array());
            String expected = Strings.dumpBytes(buffer.array());
            assertEquals( expected, decoded );
        }
        catch ( EncoderException ee )
        {
            fail();
        }
    }


    /**
     * Test the decoding of a SyncRequestValue control with no cookie
     */
    @Test
    public void testDecodeSyncRequestValueControlNoCookie()
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x08 );
        bb.put( new byte[]
            { 
            0x30, 0x06,                     // syncRequestValue ::= SEQUENCE {
              0x0A, 0x01, 0x03,             //     mode ENUMERATED {
                                            //         refreshAndPersist (3)
                                            //     }
              0x01, 0x01, 0x00              //     reloadHint BOOLEAN DEFAULT FALSE
            } );
        bb.flip();

        SyncRequestValueContainer container = new SyncRequestValueContainer();
        SyncRequestValueDecorator decorator = container.getSyncRequestValueControl();
        
        try
        {
            decorator.decode( bb.array() );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        SyncRequestValueDecorator syncRequestValue = container.getSyncRequestValueControl();
        assertEquals( SynchronizationModeEnum.REFRESH_AND_PERSIST, syncRequestValue.getMode() );
        assertNull( syncRequestValue.getCookie() );
        assertEquals( false, syncRequestValue.isReloadHint() );

        // Check the encoding
        try
        {
            ByteBuffer buffer = ByteBuffer.allocate( 0x23 );
            buffer.put( new byte[]
                { 
                  0x30, 0x21,                            // Control
                    0x04, 0x18,                          // OID (SyncRequestValue)
                      '1', '.', '3', '.', '6', '.', '1', '.', 
                      '4', '.', '1', '.', '4', '2', '0', '3',
                      '.', '1', '.', '9', '.', '1', '.', '1',
                    0x04, 0x05,
                      0x30, 0x03,                        // syncRequestValue ::= SEQUENCE {
                        0x0A, 0x01, 0x03                 //     mode ENUMERATED {
                                                         //         refreshAndPersist (3)
                                                         //     }
                } );
            buffer.flip();

            bb = syncRequestValue.encode( ByteBuffer.allocate( syncRequestValue.computeLength() ) );
            String decoded = Strings.dumpBytes(bb.array());
            String expected = Strings.dumpBytes(buffer.array());
            assertEquals( expected, decoded );
        }
        catch ( EncoderException ee )
        {
            fail();
        }
    }


    /**
     * Test the decoding of a SyncRequestValue control with no cookie, a true
     * reloadHint
     */
    @Test
    public void testDecodeSyncRequestValueControlNoCookieReloadHintTrue()
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x08 );
        bb.put( new byte[]
            { 
            0x30, 0x06,                     // syncRequestValue ::= SEQUENCE {
              0x0A, 0x01, 0x03,             //     mode ENUMERATED {
                                            //         refreshAndPersist (3)
                                            //     }
              0x01, 0x01, (byte)0xFF        //     reloadHint BOOLEAN DEFAULT FALSE
            } );
        bb.flip();

        SyncRequestValueContainer container = new SyncRequestValueContainer();
        SyncRequestValueDecorator decorator = container.getSyncRequestValueControl();
        
        try
        {
            decorator.decode( bb.array() );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        SyncRequestValueDecorator syncRequestValue = container.getSyncRequestValueControl();
        assertEquals( SynchronizationModeEnum.REFRESH_AND_PERSIST, syncRequestValue.getMode() );
        assertNull( syncRequestValue.getCookie() );
        assertEquals( true, syncRequestValue.isReloadHint() );

        // Check the encoding
        try
        {
            ByteBuffer buffer = ByteBuffer.allocate( 0x26 );
            buffer.put( new byte[]
                { 
                  0x30, 0x24,                            // Control
                    0x04, 0x18,                          // OID (SyncRequestValue)
                      '1', '.', '3', '.', '6', '.', '1', '.', 
                      '4', '.', '1', '.', '4', '2', '0', '3',
                      '.', '1', '.', '9', '.', '1', '.', '1',
                    0x04, 0x08,
                      0x30, 0x06,                        // syncRequestValue ::= SEQUENCE {
                        0x0A, 0x01, 0x03,                //     mode ENUMERATED {
                                                         //         refreshAndPersist (3)
                                                         //     }
                        0x01, 0x01, (byte)0xFF           //     reloadHint
                } );
            buffer.flip();

            bb = syncRequestValue.encode( ByteBuffer.allocate( syncRequestValue.computeLength() ) );
            String decoded = Strings.dumpBytes(bb.array());
            String expected = Strings.dumpBytes(buffer.array());
            assertEquals( expected, decoded );
        }
        catch ( EncoderException ee )
        {
            fail();
        }
    }


    /**
     * Test the decoding of a SyncRequestValue control with no cookie, no
     * reloadHint
     */
    @Test
    public void testDecodeSyncRequestValueControlNoCookieNoReloadHint()
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x05 );
        bb.put( new byte[]
            { 
            0x30, 0x03,                     // syncRequestValue ::= SEQUENCE {
              0x0A, 0x01, 0x03              //     mode ENUMERATED {
                                            //         refreshAndPersist (3)
                                            //     }
            } );
        bb.flip();

        SyncRequestValueContainer container = new SyncRequestValueContainer();
        SyncRequestValueDecorator decorator = container.getSyncRequestValueControl();
        
        try
        {
            decorator.decode( bb.array() );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        SyncRequestValueDecorator syncRequestValue = container.getSyncRequestValueControl();
        assertEquals( SynchronizationModeEnum.REFRESH_AND_PERSIST, syncRequestValue.getMode() );
        assertNull( syncRequestValue.getCookie() );
        assertEquals( false, syncRequestValue.isReloadHint() );

        // Check the encoding
        try
        {
            ByteBuffer buffer = ByteBuffer.allocate( 0x23 );
            buffer.put( new byte[]
                { 
                  0x30, 0x21,                            // Control
                    0x04, 0x18,                          // OID (SyncRequestValue)
                      '1', '.', '3', '.', '6', '.', '1', '.', 
                      '4', '.', '1', '.', '4', '2', '0', '3',
                      '.', '1', '.', '9', '.', '1', '.', '1',
                    0x04, 0x05,
                      0x30, 0x03,                        // syncRequestValue ::= SEQUENCE {
                        0x0A, 0x01, 0x03,                //     mode ENUMERATED {
                                                         //         refreshAndPersist (3)
                                                         //     }
                } );
            buffer.flip();

            bb = syncRequestValue.encode( ByteBuffer.allocate( syncRequestValue.computeLength() ) );
            String decoded = Strings.dumpBytes(bb.array());
            String expected = Strings.dumpBytes(buffer.array());
            assertEquals( expected, decoded );
        }
        catch ( EncoderException ee )
        {
            fail();
        }
    }


    /**
     * Test the decoding of a SyncRequestValue control with no reloadHint
     */
    @Test
    public void testDecodeSyncRequestValueControlNoReloadHintSuccess()
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x0D );
        bb.put( new byte[]
            { 
            0x30, 0x08,                     // syncRequestValue ::= SEQUENCE {
              0x0A, 0x01, 0x03,             //     mode ENUMERATED {
                                            //         refreshAndPersist (3)
                                            //     }
              0x04, 0x03, 'a', 'b', 'c'     //     cookie syncCookie OPTIONAL,
            } );
        bb.flip();

        SyncRequestValueContainer container = new SyncRequestValueContainer();
        SyncRequestValueDecorator decorator = container.getSyncRequestValueControl();
        
        try
        {
            decorator.decode( bb.array() );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        SyncRequestValueDecorator syncRequestValue = container.getSyncRequestValueControl();
        assertEquals( SynchronizationModeEnum.REFRESH_AND_PERSIST, syncRequestValue.getMode() );
        assertEquals( "abc", Strings.utf8ToString(syncRequestValue.getCookie()) );
        assertEquals( false, syncRequestValue.isReloadHint() );

        // Check the encoding
        try
        {
            ByteBuffer buffer = ByteBuffer.allocate( 0x28 );
            buffer.put( new byte[]
                { 
                  0x30, 0x26,                            // Control
                    0x04, 0x18,                          // OID (SyncRequestValue)
                      '1', '.', '3', '.', '6', '.', '1', '.', 
                      '4', '.', '1', '.', '4', '2', '0', '3',
                      '.', '1', '.', '9', '.', '1', '.', '1',
                    0x04, 0x0A,
                      0x30, 0x08,                        // syncRequestValue ::= SEQUENCE {
                        0x0A, 0x01, 0x03,                //     mode ENUMERATED {
                                                         //         refreshAndPersist (3)
                                                         //     }
                        0x04, 0x03, 'a', 'b', 'c'        //     cookie syncCookie OPTIONAL,
                } );
            buffer.flip();

            bb = syncRequestValue.encode( ByteBuffer.allocate( syncRequestValue.computeLength() ) );
            String decoded = Strings.dumpBytes(bb.array());
            String expected = Strings.dumpBytes(buffer.array());
            assertEquals( expected, decoded );
        }
        catch ( EncoderException ee )
        {
            fail();
        }
    }


    /**
     * Test the decoding of a SyncRequestValue control with an empty cookie
     */
    @Test
    public void testDecodeSyncRequestValueControlEmptyCookie()
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x07 );
        bb.put( new byte[]
            { 
            0x30, 0x05,                     // syncRequestValue ::= SEQUENCE {
              0x0A, 0x01, 0x03,             //     mode ENUMERATED {
                                            //         refreshAndPersist (3)
                                            //     }
              0x04, 0x00,                   //     cookie syncCookie OPTIONAL,
            } );
        bb.flip();

        SyncRequestValueContainer container = new SyncRequestValueContainer();
        SyncRequestValueDecorator decorator = container.getSyncRequestValueControl();
        
        try
        {
            decorator.decode( bb.array() );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        SyncRequestValueDecorator syncRequestValue = container.getSyncRequestValueControl();
        assertEquals( SynchronizationModeEnum.REFRESH_AND_PERSIST, syncRequestValue.getMode() );
        assertEquals( "", Strings.utf8ToString(syncRequestValue.getCookie()) );
        assertEquals( false, syncRequestValue.isReloadHint() );

        // Check the encoding
        try
        {
            ByteBuffer buffer = ByteBuffer.allocate( 0x23 );
            buffer.put( new byte[]
                { 
                  0x30, 0x21,                            // Control
                    0x04, 0x18,                          // OID (SyncRequestValue)
                      '1', '.', '3', '.', '6', '.', '1', '.', 
                      '4', '.', '1', '.', '4', '2', '0', '3',
                      '.', '1', '.', '9', '.', '1', '.', '1',
                    0x04, 0x05,
                      0x30, 0x03,                        // syncRequestValue ::= SEQUENCE {
                        0x0A, 0x01, 0x03,                //     mode ENUMERATED {
                                                         //         refreshAndPersist (3)
                                                         //     }
                } );
            buffer.flip();

            bb = syncRequestValue.encode( ByteBuffer.allocate( syncRequestValue.computeLength() ) );
            String decoded = Strings.dumpBytes(bb.array());
            String expected = Strings.dumpBytes(buffer.array());
            assertEquals( expected, decoded );
        }
        catch ( EncoderException ee )
        {
            fail();
        }
    }


    /**
     * Test the decoding of a SyncRequestValue control with an empty sequence
     */
    @Test
    public void testDecodeSyncRequestValueControlEmptySequence()
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x02 );
        bb.put( new byte[]
            { 
            0x30, 0x00                      // syncRequestValue ::= SEQUENCE {
            } );
        bb.flip();

        SyncRequestValueContainer container = new SyncRequestValueContainer();
        SyncRequestValueDecorator decorator = container.getSyncRequestValueControl();
        
        try
        {
            decorator.decode( bb.array() );
            fail( "we should not get there" );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
        }
    }


    /**
     * Test the decoding of a SyncRequestValue control with no mode
     */
    @Test
    public void testDecodeSyncRequestValueControlNoMode()
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x07 );
        bb.put( new byte[]
            { 
            0x30, 0x05,                     // syncRequestValue ::= SEQUENCE {
              0x04, 0x03, 'a', 'b', 'c'     //     cookie syncCookie OPTIONAL,
            } );
        bb.flip();

        SyncRequestValueContainer container = new SyncRequestValueContainer();
        SyncRequestValueDecorator decorator = container.getSyncRequestValueControl();
        
        try
        {
            decorator.decode( bb.array() );
            fail( "we should not get there" );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
        }
    }
}

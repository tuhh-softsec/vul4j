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
package org.apache.directory.shared.ldap.extras.controls.syncrepl_impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;

import com.mycila.junit.concurrent.Concurrency;
import com.mycila.junit.concurrent.ConcurrentJunitRunner;
import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.ldap.codec.api.DefaultLdapCodecService;
import org.apache.directory.shared.ldap.codec.api.LdapCodecService;
import org.apache.directory.shared.ldap.extras.controls.SyncRequestValue;
import org.apache.directory.shared.ldap.extras.controls.SynchronizationModeEnum;
import org.apache.directory.shared.ldap.extras.controls.syncrepl_impl.SyncRequestValueDecorator;
import org.apache.directory.shared.util.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test the SyncRequestControlValue codec
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrency()
public class SyncRequestValueControlTest
{
    private LdapCodecService codec = new DefaultLdapCodecService();
    
    
    /**
     * Test the decoding of a SyncRequestValue control with a refreshOnly mode
     */
    @Test
    public void testDecodeSyncRequestValueControlRefreshOnlySuccess() throws Exception
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

        SyncRequestValueDecorator decorator = new SyncRequestValueDecorator( codec );
        
        SyncRequestValue syncRequestValue = (SyncRequestValue)decorator.decode( bb.array() );

        assertEquals( SynchronizationModeEnum.REFRESH_ONLY, syncRequestValue.getMode() );
        assertEquals( "abc", Strings.utf8ToString(syncRequestValue.getCookie()) );
        assertEquals( false, syncRequestValue.isReloadHint() );

        // Check the encoding
        try
        {
            bb = ByteBuffer.allocate( 0x0A );
            bb.put( new byte[]
                { 
                0x30, 0x08,                     // syncRequestValue ::= SEQUENCE {
                  0x0A, 0x01, 0x01,             //     mode ENUMERATED {
                                                //         refreshOnly (1)
                                                //     }
                  0x04, 0x03, 'a', 'b', 'c'     //     cookie syncCookie OPTIONAL,
                } );
            bb.flip();

            ByteBuffer buffer = ((SyncRequestValueDecorator)syncRequestValue).encode( ByteBuffer.allocate( ((SyncRequestValueDecorator)syncRequestValue).computeLength() ) );
            String decoded = Strings.dumpBytes( bb.array()) ;
            String expected = Strings.dumpBytes( buffer.array() );
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
    public void testDecodeSyncRequestValueControlRefreshAndPersistSuccess() throws Exception
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

        SyncRequestValueDecorator decorator = new SyncRequestValueDecorator( codec );
        
        SyncRequestValue syncRequestValue = (SyncRequestValue)decorator.decode( bb.array() );

        assertEquals( SynchronizationModeEnum.REFRESH_AND_PERSIST, syncRequestValue.getMode() );
        assertEquals( "abc", Strings.utf8ToString(syncRequestValue.getCookie()) );
        assertEquals( false, syncRequestValue.isReloadHint() );

        // Check the encoding
        try
        {
            ByteBuffer buffer = ByteBuffer.allocate( 0x0A );
            buffer.put( new byte[]
                { 
                  0x30, 0x08,                        // syncRequestValue ::= SEQUENCE {
                    0x0A, 0x01, 0x03,                //     mode ENUMERATED {
                                                     //         refreshAndPersist (3)
                                                     //     }
                    0x04, 0x03, 'a', 'b', 'c'        //     cookie syncCookie OPTIONAL,
                } );
            buffer.flip();

            bb = ((SyncRequestValueDecorator)syncRequestValue).encode( ByteBuffer.allocate( ((SyncRequestValueDecorator)syncRequestValue).computeLength() ) );
            String decoded = Strings.dumpBytes( bb.array() );
            String expected = Strings.dumpBytes( buffer.array() );
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
    public void testDecodeSyncRequestValueControlNoCookie() throws Exception
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

        SyncRequestValueDecorator decorator = new SyncRequestValueDecorator(  codec );
        
        SyncRequestValue syncRequestValue = (SyncRequestValue)decorator.decode( bb.array() );

        assertEquals( SynchronizationModeEnum.REFRESH_AND_PERSIST, syncRequestValue.getMode() );
        assertNull( syncRequestValue.getCookie() );
        assertEquals( false, syncRequestValue.isReloadHint() );

        // Check the encoding
        try
        {
            bb = ByteBuffer.allocate( 0x05 );
            bb.put( new byte[]
                { 
                0x30, 0x03,                     // syncRequestValue ::= SEQUENCE {
                  0x0A, 0x01, 0x03              //     mode ENUMERATED {
                                                //         refreshAndPersist (3)
                                                //     }
                } );
            bb.flip();
            
            ByteBuffer buffer = ((SyncRequestValueDecorator)syncRequestValue).encode( ByteBuffer.allocate( ((SyncRequestValueDecorator)syncRequestValue).computeLength() ) );
            String decoded = Strings.dumpBytes( buffer.array() );
            String expected = Strings.dumpBytes( bb.array() );
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
    public void testDecodeSyncRequestValueControlNoCookieReloadHintTrue() throws Exception
    {
        ByteBuffer buffer = ByteBuffer.allocate( 0x08 );
        buffer.put( new byte[]
            { 
            0x30, 0x06,                     // syncRequestValue ::= SEQUENCE {
              0x0A, 0x01, 0x03,             //     mode ENUMERATED {
                                            //         refreshAndPersist (3)
                                            //     }
              0x01, 0x01, (byte)0xFF        //     reloadHint BOOLEAN DEFAULT FALSE
            } );
        buffer.flip();

        SyncRequestValueDecorator decorator = new SyncRequestValueDecorator( codec );
        
        SyncRequestValue syncRequestValue = (SyncRequestValue)decorator.decode( buffer.array() );

        assertEquals( SynchronizationModeEnum.REFRESH_AND_PERSIST, syncRequestValue.getMode() );
        assertNull( syncRequestValue.getCookie() );
        assertEquals( true, syncRequestValue.isReloadHint() );

        // Check the encoding
        try
        {
            ByteBuffer bb = ((SyncRequestValueDecorator)syncRequestValue).encode( ByteBuffer.allocate( ((SyncRequestValueDecorator)syncRequestValue).computeLength() ) );
            String decoded = Strings.dumpBytes( bb.array() );
            String expected = Strings.dumpBytes( buffer.array() );
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
    public void testDecodeSyncRequestValueControlNoCookieNoReloadHint() throws Exception
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

        SyncRequestValueDecorator decorator = new SyncRequestValueDecorator( codec );
        
        SyncRequestValue syncRequestValue = (SyncRequestValue)decorator.decode( bb.array() );

        assertEquals( SynchronizationModeEnum.REFRESH_AND_PERSIST, syncRequestValue.getMode() );
        assertNull( syncRequestValue.getCookie() );
        assertEquals( false, syncRequestValue.isReloadHint() );

        // Check the encoding
        try
        {
            ByteBuffer buffer = ((SyncRequestValueDecorator)syncRequestValue).encode( ByteBuffer.allocate( ((SyncRequestValueDecorator)syncRequestValue).computeLength() ) );
            String decoded = Strings.dumpBytes( buffer.array() );
            String expected = Strings.dumpBytes( bb.array() );
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
    public void testDecodeSyncRequestValueControlNoReloadHintSuccess() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x0A );
        bb.put( new byte[]
            { 
            0x30, 0x08,                     // syncRequestValue ::= SEQUENCE {
              0x0A, 0x01, 0x03,             //     mode ENUMERATED {
                                            //         refreshAndPersist (3)
                                            //     }
              0x04, 0x03, 'a', 'b', 'c'     //     cookie syncCookie OPTIONAL,
            } );
        bb.flip();

        SyncRequestValueDecorator decorator = new SyncRequestValueDecorator( codec );
        
        SyncRequestValue syncRequestValue = (SyncRequestValue)decorator.decode( bb.array() );

        assertEquals( SynchronizationModeEnum.REFRESH_AND_PERSIST, syncRequestValue.getMode() );
        assertEquals( "abc", Strings.utf8ToString(syncRequestValue.getCookie()) );
        assertEquals( false, syncRequestValue.isReloadHint() );

        // Check the encoding
        try
        {
            ByteBuffer buffer = ((SyncRequestValueDecorator)syncRequestValue).encode( ByteBuffer.allocate( ((SyncRequestValueDecorator)syncRequestValue).computeLength() ) );
            String decoded = Strings.dumpBytes( buffer.array() );
            String expected = Strings.dumpBytes( bb.array() );
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
    public void testDecodeSyncRequestValueControlEmptyCookie() throws Exception
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

        SyncRequestValueDecorator decorator = new SyncRequestValueDecorator( codec );
        
        SyncRequestValue syncRequestValue = (SyncRequestValue)decorator.decode( bb.array() );

        assertEquals( SynchronizationModeEnum.REFRESH_AND_PERSIST, syncRequestValue.getMode() );
        assertEquals( "", Strings.utf8ToString(syncRequestValue.getCookie()) );
        assertEquals( false, syncRequestValue.isReloadHint() );

        // Check the encoding
        try
        {
            bb = ByteBuffer.allocate( 0x05 );
            bb.put( new byte[]
                { 
                0x30, 0x03,                     // syncRequestValue ::= SEQUENCE {
                  0x0A, 0x01, 0x03              //     mode ENUMERATED {
                                                //         refreshAndPersist (3)
                                                //     }
                } );
            bb.flip();

            ByteBuffer buffer = ((SyncRequestValueDecorator)syncRequestValue).encode( ByteBuffer.allocate( ((SyncRequestValueDecorator)syncRequestValue).computeLength() ) );
            String decoded = Strings.dumpBytes( buffer.array() );
            String expected = Strings.dumpBytes( bb.array() );
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

        SyncRequestValueDecorator decorator = new SyncRequestValueDecorator( codec );
        
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

        SyncRequestValueDecorator decorator = new SyncRequestValueDecorator( codec );
        
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

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
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;

import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.ldap.codec.api.DefaultLdapCodecService;
import org.apache.directory.shared.ldap.codec.api.LdapCodecService;
import org.apache.directory.shared.ldap.extras.controls.SyncStateTypeEnum;
import org.apache.directory.shared.ldap.extras.controls.SyncStateValue;
import org.apache.directory.shared.ldap.extras.controls.syncrepl_impl.SyncStateValueDecorator;
import org.apache.directory.shared.util.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mycila.junit.concurrent.Concurrency;
import com.mycila.junit.concurrent.ConcurrentJunitRunner;


/**
 * Test the SyncStateControlValue codec
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrency()
public class SyncStateValueControlTest
{
    private LdapCodecService codec = new DefaultLdapCodecService();
    
    /**
     * Test the decoding of a SyncStateValue control with a refreshOnly mode
     */
    @Test
    public void testDecodeSyncStateValueControlWithStateType() throws Exception
    {
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

        SyncStateValueDecorator decorator = new SyncStateValueDecorator( codec );

        SyncStateValue syncStateValue = (SyncStateValue)decorator.decode( bb.array() );

        assertEquals( SyncStateTypeEnum.PRESENT, syncStateValue.getSyncStateType() );
        assertEquals( "abc", Strings.utf8ToString(syncStateValue.getEntryUUID()) );
        assertEquals( "xkcd", Strings.utf8ToString(syncStateValue.getCookie()) );

        // Check the encoding
        try
        {
            ByteBuffer encoded = ((SyncStateValueDecorator)syncStateValue).encode( ByteBuffer.allocate( ((SyncStateValueDecorator)syncStateValue).computeLength() ) );
            assertEquals( Strings.dumpBytes( bb.array() ), Strings.dumpBytes( encoded.array() ) );
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
    public void testDecodeSyncStateValueControlNoCookie() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 10 );
        bb.put( new byte[]
            { 0x30, 0x08,                 // SyncStateValue ::= SEQUENCE {
                0x0A, 0x01, 0x01,         //     state ENUMERATED {
                                          //         add (1)
                                          //     }
                0x04, 0x03, 'a', 'b', 'c' //     entryUUID syncUUID OPTIONAL
            } );
        bb.flip();

        SyncStateValueDecorator decorator = new SyncStateValueDecorator( codec );

        SyncStateValue syncStateValue = (SyncStateValue)decorator.decode( bb.array() );

        assertEquals( SyncStateTypeEnum.ADD, syncStateValue.getSyncStateType() );
        assertEquals( "abc", Strings.utf8ToString(syncStateValue.getEntryUUID()) );
        assertNull( syncStateValue.getCookie() );

        // Check the encoding
        try
        {
            ByteBuffer encoded = ((SyncStateValueDecorator)syncStateValue).encode( ByteBuffer.allocate( ((SyncStateValueDecorator)syncStateValue).computeLength() ) );
            assertEquals( Strings.dumpBytes( bb.array() ), Strings.dumpBytes( encoded.array() ) );
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
    public void testDecodeSyncStateValueControlEmptyCookie() throws Exception
    {
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

        SyncStateValueDecorator decorator = new SyncStateValueDecorator( codec );

        SyncStateValue syncStateValue = (SyncStateValue)decorator.decode( bb.array() );

        assertEquals( SyncStateTypeEnum.MODIFY, syncStateValue.getSyncStateType() );
        assertEquals( "abc", Strings.utf8ToString(syncStateValue.getEntryUUID()) );
        assertEquals( "", Strings.utf8ToString(syncStateValue.getCookie()) );

        // Check the encoding
        try
        {
            bb = ByteBuffer.allocate( 0x0A );
            bb.put( new byte[]
                { 0x30, 0x08,                  // SyncStateValue ::= SEQUENCE {
                    0x0A, 0x01, 0x02,          //     state ENUMERATED {
                                               //         modify (2)
                                               //     }
                    0x04, 0x03, 'a', 'b', 'c'  //     entryUUID syncUUID OPTIONAL
                } );
            bb.flip();

            ByteBuffer encoded = ((SyncStateValueDecorator)syncStateValue).encode( ByteBuffer.allocate( ((SyncStateValueDecorator)syncStateValue).computeLength() ) );
            assertEquals( Strings.dumpBytes( bb.array() ), Strings.dumpBytes( encoded.array() ) );
        }
        catch ( EncoderException ee )
        {
            fail( ee.getMessage() );
        }
    }


    /**
     * Test the decoding of a SyncStateValue control with an empty sequence
     */
    @Test( expected = DecoderException.class )
    public void testDecodeSyncStateValueControlEmptySequence() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x02 );
        bb.put( new byte[]
            { 
              0x30, 0x00 // SyncStateValue ::= SEQUENCE {
            } );
        bb.flip();

        SyncStateValueDecorator decorator = new SyncStateValueDecorator( codec );

        decorator.decode( bb.array() );
    }


    /**
     * Test the decoding of a SyncStateValue control with no syncState
     */
    @Test( expected=DecoderException.class )
    public void testDecodeSyncStateValueControlNoSyancState() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x07 );
        bb.put( new byte[]
            { 
              0x30, 0x05,                 // SyncStateValue ::= SEQUENCE {
                0x04, 0x03, 'a', 'b', 'c' //     cookie syncCookie OPTIONAL,
            } );
        bb.flip();

        SyncStateValueDecorator decorator = new SyncStateValueDecorator( codec );

        decorator.decode( bb.array() );
    }


    /**
     * Test the decoding of a SyncStateValue control with no syncUUID
     */
    @Test( expected=DecoderException.class )
    public void testDecodeSyncStateValueControlNoSyncUUID() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0x05 );
        bb.put( new byte[]
            { 
              0x30, 0x03,                  // SyncStateValue ::= SEQUENCE {
                0x0A, 0x01, 0x02,          //     state ENUMERATED {
                                           //         modify (2)
                                           //     }
            } );
        bb.flip();

        SyncStateValueDecorator decorator = new SyncStateValueDecorator( codec );

        decorator.decode( bb.array() );
    }
    
    
    /**
     * Test the decoding of a SyncStateValue control with a refreshOnly mode
     * and MODDN state type
     */
    @Test
    public void testDecodeSyncStateValueControlWithModDnStateType() throws Exception
    {
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

        SyncStateValueDecorator decorator = new SyncStateValueDecorator( codec );

        SyncStateValue syncStateValue = (SyncStateValue)decorator.decode( bb.array() );

        assertEquals( SyncStateTypeEnum.MODDN, syncStateValue.getSyncStateType() );
        assertEquals( "abc", Strings.utf8ToString(syncStateValue.getEntryUUID()) );
        assertEquals( "xkcd", Strings.utf8ToString(syncStateValue.getCookie()) );

        // Check the encoding
        try
        {
            ByteBuffer encoded = ((SyncStateValueDecorator)syncStateValue).encode( ByteBuffer.allocate( ((SyncStateValueDecorator)syncStateValue).computeLength() ) );
            assertEquals( Strings.dumpBytes( bb.array() ), Strings.dumpBytes( encoded.array() ) );
        }
        catch ( EncoderException ee )
        {
            fail( ee.getMessage() );
        }
    }
}

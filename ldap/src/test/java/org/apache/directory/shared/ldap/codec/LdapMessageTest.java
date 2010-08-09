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
package org.apache.directory.shared.ldap.codec;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.asn1.ber.Asn1Decoder;
import org.apache.directory.shared.asn1.ber.IAsn1Container;
import org.apache.directory.shared.asn1.codec.DecoderException;
import org.apache.directory.shared.asn1.codec.EncoderException;
import org.apache.directory.shared.ldap.codec.unbind.UnBindRequestCodec;
import org.apache.directory.shared.ldap.message.internal.InternalMessage;
import org.apache.directory.shared.ldap.util.StringTools;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * A global Ldap Decoder test
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
public class LdapMessageTest
{
    // ~ Methods
    // ------------------------------------------------------------------------------------

    /**
     * Test the decoding of null length messageId
     */
    @Test
    public void testDecodeMessageLengthNull()
    {

        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x02 );
        stream.put( new byte[]
            { 0x30, 0x00, // LDAPMessage ::=SEQUENCE {
            } );

        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode a BindRequest PDU
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
            return;
        }

        fail( "We should not reach this point !" );
    }


    /**
     * Test the decoding of null length messageId
     */
    @Test
    public void testDecodeMessageIdLengthNull()
    {

        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x04 );
        stream.put( new byte[]
            { 0x30, 0x02, // LDAPMessage ::=SEQUENCE {
                0x02, 0x00 // messageID MessageID
            } );

        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode a BindRequest PDU
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
            return;
        }

        fail( "We should not reach this point !" );
    }


    /**
     * Test the decoding of null length messageId
     */
    @Test
    public void testDecodeMessageIdMinusOne()
    {

        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x05 );
        stream.put( new byte[]
            { 0x30, 0x03, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, ( byte ) 0xff // messageID MessageID = -1
            } );

        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode a BindRequest PDU
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
            return;
        }

        fail( "We should not reach this point !" );
    }


    /**
     * Test the decoding of messageId which value is -1
     */
    @Test
    public void testDecodeMessageIdMaxInt()
    {

        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x08 );
        stream.put( new byte[]
            { 0x30, 0x06, // LDAPMessage ::=SEQUENCE {
                // messageID MessageID = -1
                0x02, 0x04, ( byte ) 0x7f, ( byte ) 0xff, ( byte ) 0xff, ( byte ) 0xff } );

        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode a BindRequest PDU
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
            return;
        }

        fail( "We should not reach this point !" );
    }


    /**
     * Test the decoding of a message with a wrong protocol operation
     */
    @Test
    public void testDecodeWrongProtocolOpMaxInt()
    {

        byte[] buffer = new byte[]
            { 0x30, 0x05, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID = 1
                0x42, 0x00 // ProtocolOp
            };

        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x07 );

        for ( int i = 0; i < 256; i++ )
        {
            buffer[5] = ( byte ) i;
            stream.put( buffer );
            stream.flip();

            // Allocate a LdapMessage Container
            IAsn1Container ldapMessageContainer = new LdapMessageContainer();

            // Decode a BindRequest PDU
            try
            {
                ldapDecoder.decode( stream, ldapMessageContainer );
            }
            catch ( DecoderException de )
            {
                switch ( i )
                {
                    case 0x42:
                    case 0x4A:
                    case 0x50: // AbandonRequest
                    case 0x60:
                    case 0x61:
                    case 0x63:
                    case 0x64:
                    case 0x65:
                    case 0x66:
                    case 0x67:
                    case 0x68:
                    case 0x69:
                    case 0x6B:
                    case 0x6C:
                    case 0x6D:
                    case 0x6E:
                    case 0x6F:
                    case 0x73:
                    case 0x77:
                    case 0x78:
                        assertTrue( true );
                        break;

                    default:
                        String res = de.getMessage();

                        if ( res.equals( "ERR_00002 Bad transition !" ) || res.startsWith( "Universal tag " )
                            || res.startsWith( "ERR_00010 Truncated PDU" ) )
                        {
                            assertTrue( true );
                        }
                        else
                        {
                            fail( "Bad exception : " + res );
                            return;
                        }

                        break;
                }
            }

            stream.clear();
        }

        assertTrue( true );
    }


    /**
     * Test the decoding of a LdapMessage with a large MessageId
     */
    @Test
    public void testDecodeUnBindRequestNoControls()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x08 );
        stream.put( new byte[]
            { 0x30, 0x06, // LDAPMessage ::=SEQUENCE {
                0x02, 0x02, 0x01, ( byte ) 0xF4, // messageID MessageID (500)
                0x42, 0x00, // CHOICE { ..., unbindRequest UnbindRequest,...
            // UnbindRequest ::= [APPLICATION 2] NULL
            } );

        String decodedPdu = StringTools.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a BindRequest Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        InternalMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getInternalMessage();

        assertEquals( 500, message.getMessageId() );

        // Check the length
        UnBindRequestCodec unbindRequestCodec = new UnBindRequestCodec();
        unbindRequestCodec.setMessageId( message.getMessageId() );

        assertEquals( 8, unbindRequestCodec.computeLength() );

        try
        {
            ByteBuffer bb = unbindRequestCodec.encode();

            String encodedPdu = StringTools.dumpBytes( bb.array() );

            assertEquals( encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            fail( ee.getMessage() );
        }
    }

}

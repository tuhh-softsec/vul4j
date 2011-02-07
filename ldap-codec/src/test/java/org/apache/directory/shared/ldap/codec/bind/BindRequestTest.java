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
package org.apache.directory.shared.ldap.codec.bind;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.util.Map;

import com.mycila.junit.concurrent.Concurrency;
import com.mycila.junit.concurrent.ConcurrentJunitRunner;
import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.asn1.ber.Asn1Container;
import org.apache.directory.shared.asn1.ber.Asn1Decoder;
import org.apache.directory.shared.ldap.codec.LdapEncoder;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.api.CodecControl;
import org.apache.directory.shared.ldap.codec.api.DefaultLdapCodecService;
import org.apache.directory.shared.ldap.codec.api.LdapCodecService;
import org.apache.directory.shared.ldap.codec.api.ResponseCarryingException;
import org.apache.directory.shared.ldap.codec.decorators.BindRequestDecorator;
import org.apache.directory.shared.ldap.codec.decorators.MessageDecorator;
import org.apache.directory.shared.ldap.model.message.BindRequest;
import org.apache.directory.shared.ldap.model.message.BindResponseImpl;
import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.directory.shared.ldap.model.message.Message;
import org.apache.directory.shared.ldap.model.message.ResultCodeEnum;
import org.apache.directory.shared.util.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrency()
public class BindRequestTest
{
    /** The encoder instance */
    LdapEncoder encoder = new LdapEncoder();

    LdapCodecService codec = new DefaultLdapCodecService();

    /**
     * Test the decoding of a BindRequest with Simple authentication and no
     * controls
     */
    /* Not used in unit tests
    @Test
    public void testDecodeBindRequestSimpleNoControlsPerf()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x52 );
        stream.put( new byte[]
             { 
             0x30, 0x50,                 // LDAPMessage ::=SEQUENCE {
               0x02, 0x01, 0x01,         // messageID MessageID
               0x60, 0x2E,               // CHOICE { ..., bindRequest BindRequest, ...
                                         // BindRequest ::= APPLICATION[0] SEQUENCE {
                 0x02, 0x01, 0x03,       // version INTEGER (1..127),
                 0x04, 0x1F,             // name LDAPDN,
                 'u', 'i', 'd', '=', 'a', 'k', 'a', 'r', 'a', 's', 'u', 'l', 'u', ',', 'd', 'c', '=', 'e', 'x', 'a',
                 'm', 'p', 'l', 'e', ',', 'd', 'c', '=', 'c', 'o', 'm', 
                 ( byte ) 0x80, 0x08,    // authentication AuthenticationChoice
                                         // AuthenticationChoice ::= CHOICE { simple [0] OCTET STRING,
                                         // ...
                   'p', 'a', 's', 's', 'w', 'o', 'r', 'd', 
               ( byte ) 0xA0, 0x1B, // A control
                 0x30, 0x19, 
                   0x04, 0x17, 
                     0x32, 0x2E, 0x31, 0x36, 0x2E, 0x38, 0x34, 0x30, 0x2E, 0x31, 0x2E, 0x31, 0x31, 0x33, 0x37, 0x33, 
                     0x30, 0x2E, 0x33, 0x2E, 0x34, 0x2E, 0x32 
             } );

        String decodedPdu = StringTools.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a LdapMessage Container
        Asn1Container container = new LdapMessageContainer();

        // Decode the BindRequest PDU
        try
        {
            long t0 = System.currentTimeMillis();
            for ( int i = 0; i < 10000; i++ )
            {
                ldapDecoder.decode( stream, container );
                container).clean();
                stream.flip();
            }
            long t1 = System.currentTimeMillis();
            System.out.println( "Delta = " + ( t1 - t0 ) );
            
            ldapDecoder.decode( stream, container );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        // Check the decoded BindRequest
        LdapMessage message = container.getLdapMessage();
        BindRequest br = message.getMessage();

        assertEquals( 1, message.getMessageId() );
        assertEquals( 3, br.getVersion() );
        assertEquals( "uid=akarasulu,dc=example,dc=com", br.getName().toString() );
        assertEquals( true, ( br.getAuthentication() instanceof SimpleAuthentication ) );
        assertEquals( "password", StringTools.utf8ToString( ( ( SimpleAuthentication ) br.getAuthentication() )
            .getSimple() ) );

        // Check the Control
        List controls = message.getControls();

        assertEquals( 1, controls.size() );

        Control control = message.getControls( 0 );
        assertEquals( "2.16.840.1.113730.3.4.2", control.getOid() );
        assertEquals( "", StringTools.dumpBytes( ( byte[] ) control.getValue() ) );

        // Check the length
        assertEquals( 0x52, message.computeLength() );

        // Check the encoding
        try
        {
            ByteBuffer bb = message.encode();

            String encodedPdu = StringTools.dumpBytes( bb.array() );

            assertEquals( encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            fail( ee.getMessage() );
        }
    }
    */

    /**
     * Test the decoding of a BindRequest with Simple authentication and
     * controls
     */
    @Test
    public void testDecodeBindRequestSimpleWithControls()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x35 );
        stream.put( new byte[]
            { 0x30,
                0x33, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01,
                0x01, // messageID MessageID
                0x60,
                0x2E, // CHOICE { ..., bindRequest BindRequest, ...
                // BindRequest ::= APPLICATION[0] SEQUENCE {
                0x02, 0x01,
                0x03, // version INTEGER (1..127),
                0x04,
                0x1F, // name LDAPDN,
                'u', 'i', 'd', '=', 'a', 'k', 'a', 'r', 'a', 's', 'u', 'l', 'u', ',', 'd', 'c', '=', 'e', 'x', 'a',
                'm', 'p', 'l', 'e', ',', 'd', 'c', '=', 'c', 'o', 'm', ( byte ) 0x80, 0x08, // authentication AuthenticationChoice
                // AuthenticationChoice ::= CHOICE { simple [0] OCTET STRING,
                // ...
                'p', 'a', 's', 's', 'w', 'o', 'r', 'd' } );

        String decodedPdu = Strings.dumpBytes(stream.array());
        stream.flip();

        // Allocate a LdapMessage Container
        LdapMessageContainer<BindRequestDecorator> container = new LdapMessageContainer<BindRequestDecorator>( codec );

        // Decode the BindRequest PDU
        try
        {
            ldapDecoder.decode( stream, container );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        // Check the decoded BindRequest
        BindRequest bindRequest = container.getMessage();

        assertEquals( 1, bindRequest.getMessageId() );
        assertTrue( bindRequest.isVersion3() );
        assertEquals( "uid=akarasulu,dc=example,dc=com", bindRequest.getName().toString() );
        assertTrue( bindRequest.isSimple() );
        assertEquals( "password", Strings.utf8ToString(bindRequest.getCredentials()) );

        // Check the encoding
        try
        {
            ByteBuffer bb = encoder.encodeMessage( bindRequest );

            // Check the length
            assertEquals( 0x35, bb.limit() );

            String encodedPdu = Strings.dumpBytes(bb.array());

            assertEquals( encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            fail( ee.getMessage() );
        }
    }


    /**
     * Test the decoding of a BindRequest with Simple authentication and
     * controls
     */
    @Test
    public void testDecodeBindRequestBadDN()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x35 );
        stream.put( new byte[]
            { 0x30,
                0x33, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01,
                0x01, // messageID MessageID
                0x60,
                0x2E, // CHOICE { ..., bindRequest BindRequest, ...
                // BindRequest ::= APPLICATION[0] SEQUENCE {
                0x02, 0x01,
                0x03, // version INTEGER (1..127),
                0x04,
                0x1F, // name LDAPDN,
                'u', 'i', 'd', ':', 'a', 'k', 'a', 'r', 'a', 's', 'u', 'l', 'u', ',', 'd', 'c', '=', 'e', 'x', 'a',
                'm', 'p', 'l', 'e', ',', 'd', 'c', '=', 'c', 'o', 'm', ( byte ) 0x80, 0x08, // authentication AuthenticationChoice
                // AuthenticationChoice ::= CHOICE { simple [0] OCTET STRING,
                // ...
                'p', 'a', 's', 's', 'w', 'o', 'r', 'd' } );

        stream.flip();

        // Allocate a LdapMessage Container
        LdapMessageContainer<MessageDecorator<? extends Message>> container = 
            new LdapMessageContainer<MessageDecorator<? extends Message>>( codec );

        // Decode the BindRequest PDU
        try
        {
            ldapDecoder.decode( stream, container );
        }
        catch ( DecoderException de )
        {
            assertTrue( de instanceof ResponseCarryingException );
            Message response = ( ( ResponseCarryingException ) de ).getResponse();
            assertTrue( response instanceof BindResponseImpl );
            assertEquals( ResultCodeEnum.INVALID_DN_SYNTAX, ( ( BindResponseImpl ) response ).getLdapResult()
                .getResultCode() );
            return;
        }

        fail( "We should not reach this point" );
    }


    /**
     * Test the decoding of a BindRequest with Simple authentication, no name
     * and no controls
     */
    @Test
    public void testDecodeBindRequestSimpleNoName()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x15 );
        stream.put( new byte[]
            { 0x30, 0x13, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x60, 0x0D, // CHOICE { ..., bindRequest BindRequest, ...
                // BindRequest ::= APPLICATION[0] SEQUENCE {
                0x02, 0x01, 0x03, // version INTEGER (1..127),
                ( byte ) 0x80, 0x08, // authentication AuthenticationChoice
                // AuthenticationChoice ::= CHOICE { simple [0] OCTET STRING,
                // ...
                'p', 'a', 's', 's', 'w', 'o', 'r', 'd' } );

        stream.flip();

        // Allocate a LdapMessage Container
        Asn1Container container = new LdapMessageContainer<MessageDecorator<? extends Message>>( codec );

        // Decode the BindRequest PDU
        try
        {
            ldapDecoder.decode( stream, container );
        }
        catch ( DecoderException de )
        {
            assertEquals( "ERR_00001_BAD_TRANSITION_FROM_STATE Bad transition from state VERSION_STATE, tag 0x80", de.getMessage() );
            return;
        }

        fail( "Should never reach this point." );
    }


    /**
     * Test the decoding of a BindRequest with Simple authentication, empty name
     * (an anonymous bind) and no controls
     */
    @Test
    public void testDecodeBindRequestSimpleEmptyName()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x16 );
        stream.put( new byte[]
            { 0x30, 0x14, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x60, 0x0F, // CHOICE { ..., bindRequest BindRequest, ...
                // BindRequest ::= APPLICATION[0] SEQUENCE {
                0x02, 0x01, 0x03, // version INTEGER (1..127),
                0x04, 0x00, // name LDAPDN,
                ( byte ) 0x80, 0x08, // authentication AuthenticationChoice
                // AuthenticationChoice ::= CHOICE { simple [0] OCTET STRING,
                // ...
                'p', 'a', 's', 's', 'w', 'o', 'r', 'd' } );

        String decodedPdu = Strings.dumpBytes(stream.array());

        stream.flip();

        // Allocate a LdapMessage Container
        LdapMessageContainer<BindRequestDecorator> container = new LdapMessageContainer<BindRequestDecorator>( codec );

        // Decode the BindRequest PDU
        try
        {
            ldapDecoder.decode( stream, container );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        // Check the decoded BindRequest
        BindRequest bindRequest = container.getMessage();

        assertEquals( 1, bindRequest.getMessageId() );
        assertTrue( bindRequest.isVersion3() );
        assertEquals( "", bindRequest.getName().toString() );
        assertTrue( bindRequest.isSimple() );
        assertEquals( "password", Strings.utf8ToString(bindRequest.getCredentials()) );

        // Check the encoding
        try
        {
            ByteBuffer bb = encoder.encodeMessage( bindRequest );

            // Check the length
            assertEquals( 0x16, bb.limit() );

            String encodedPdu = Strings.dumpBytes(bb.array());

            assertEquals( encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            fail( ee.getMessage() );
        }
    }


    /**
     * Test the decoding of a BindRequest with Sasl authentication, no
     * credentials and no controls
     */
    @Test
    public void testDecodeBindRequestSaslNoCredsNoControls()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x3A );
        stream.put( new byte[]
            { 0x30,
                0x38, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01,
                0x01, // messageID MessageID
                0x60,
                0x33, // CHOICE { ..., bindRequest BindRequest, ...
                // BindRequest ::= APPLICATION[0] SEQUENCE {
                0x02, 0x01,
                0x03, // version INTEGER (1..127),
                0x04,
                0x1F, // name LDAPDN,
                'u', 'i', 'd', '=', 'a', 'k', 'a', 'r', 'a', 's', 'u', 'l', 'u', ',', 'd', 'c', '=', 'e', 'x', 'a',
                'm', 'p', 'l', 'e', ',', 'd', 'c', '=', 'c', 'o', 'm', ( byte ) 0xA3, 0x0D, // authentication AuthenticationChoice
                // AuthenticationChoice ::= CHOICE { ... sasl [3]
                // SaslCredentials }
                // SaslCredentials ::= SEQUENCE {
                // mechanism LDAPSTRING,
                // ...
                0x04, 0x0B, 'K', 'E', 'R', 'B', 'E', 'R', 'O', 'S', '_', 'V', '4' } );

        String decodedPdu = Strings.dumpBytes(stream.array());
        stream.flip();

        // Allocate a LdapMessage Container
        LdapMessageContainer<BindRequestDecorator> container = new LdapMessageContainer<BindRequestDecorator>( codec );

        // Decode the BindRequest PDU
        try
        {
            ldapDecoder.decode( stream, container );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        // Check the decoded BindRequest
        BindRequest bindRequest = container.getMessage();

        assertEquals( 1, bindRequest.getMessageId() );
        assertTrue( bindRequest.isVersion3() );
        assertEquals( "uid=akarasulu,dc=example,dc=com", bindRequest.getName().toString() );
        assertFalse( bindRequest.isSimple() );
        assertEquals( "KERBEROS_V4", bindRequest.getSaslMechanism() );

        // Check the encoding
        try
        {
            ByteBuffer bb = encoder.encodeMessage( bindRequest );

            // Check the length
            assertEquals( 0x3A, bb.limit() );

            String encodedPdu = Strings.dumpBytes(bb.array());

            assertEquals( encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            fail( ee.getMessage() );
        }
    }


    /**
     * Test the decoding of a BindRequest with Sasl authentication, a
     * credentials and no controls
     */
    @Test
    public void testDecodeBindRequestSaslCredsNoControls()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x42 );
        stream.put( new byte[]
            { 0x30,
                0x40, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01,
                0x01, // messageID MessageID
                0x60,
                0x3B, // CHOICE { ..., bindRequest BindRequest, ...
                // BindRequest ::= APPLICATION[0] SEQUENCE {
                0x02, 0x01,
                0x03, // version INTEGER (1..127),
                0x04,
                0x1F, // name LDAPDN,
                'u', 'i', 'd', '=', 'a', 'k', 'a', 'r', 'a', 's', 'u', 'l', 'u', ',', 'd', 'c', '=', 'e', 'x', 'a',
                'm', 'p', 'l', 'e', ',', 'd', 'c', '=', 'c', 'o', 'm', ( byte ) 0xA3, 0x15, // authentication AuthenticationChoice
                // }
                // AuthenticationChoice ::= CHOICE { ... sasl [3]
                // SaslCredentials }
                // SaslCredentials ::= SEQUENCE {
                // mechanism LDAPSTRING,
                // ...
                0x04, 0x0B, 'K', 'E', 'R', 'B', 'E', 'R', 'O', 'S', '_', 'V', '4', ( byte ) 0x04, 0x06, // SaslCredentials ::= SEQUENCE {
                // ...
                // credentials OCTET STRING OPTIONAL }
                // 
                'a', 'b', 'c', 'd', 'e', 'f' } );

        String decodedPdu = Strings.dumpBytes(stream.array());
        stream.flip();

        // Allocate a LdapMessage Container
        LdapMessageContainer<BindRequestDecorator> container = new LdapMessageContainer<BindRequestDecorator>( codec );

        // Decode the BindRequest PDU
        try
        {
            ldapDecoder.decode( stream, container );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        // Check the decoded BindRequest
        BindRequest bindRequest = container.getMessage();

        assertEquals( 1, bindRequest.getMessageId() );
        assertTrue( bindRequest.isVersion3() );
        assertEquals( "uid=akarasulu,dc=example,dc=com", bindRequest.getName().toString() );
        assertFalse( bindRequest.isSimple() );
        assertEquals( "KERBEROS_V4", bindRequest.getSaslMechanism() );
        assertEquals( "abcdef", Strings.utf8ToString(bindRequest.getCredentials()) );

        // Check the encoding
        try
        {
            ByteBuffer bb = encoder.encodeMessage( bindRequest );

            // Check the length
            assertEquals( 0x42, bb.limit() );

            String encodedPdu = Strings.dumpBytes(bb.array());

            assertEquals( encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            fail( ee.getMessage() );
        }
    }


    /**
     * Test the decoding of a BindRequest with Sasl authentication, no name, a
     * credentials and no controls
     */
    @Test
    public void testDecodeBindRequestSaslNoNameCredsNoControls()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x23 );
        stream.put( new byte[]
            { 0x30, 0x21, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x60, 0x1C, // CHOICE { ..., bindRequest BindRequest, ...
                // BindRequest ::= APPLICATION[0] SEQUENCE {
                0x02, 0x01, 0x03, // version INTEGER (1..127),
                0x04, 0x00, // name LDAPDN,
                ( byte ) 0xA3, 0x15, // authentication AuthenticationChoice
                // }
                // AuthenticationChoice ::= CHOICE { ... sasl [3]
                // SaslCredentials }
                // SaslCredentials ::= SEQUENCE {
                // mechanism LDAPSTRING,
                // ...
                0x04, 0x0B, 'K', 'E', 'R', 'B', 'E', 'R', 'O', 'S', '_', 'V', '4', ( byte ) 0x04, 0x06, // SaslCredentials ::= SEQUENCE {
                // ...
                // credentials OCTET STRING OPTIONAL }
                // 
                'a', 'b', 'c', 'd', 'e', 'f' } );

        String decodedPdu = Strings.dumpBytes(stream.array());
        stream.flip();

        // Allocate a LdapMessage Container
        LdapMessageContainer<BindRequestDecorator> container = new LdapMessageContainer<BindRequestDecorator>( codec );

        // Decode the BindRequest PDU
        try
        {
            ldapDecoder.decode( stream, container );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        // Check the decoded BindRequest
        BindRequest bindRequest = container.getMessage();

        assertEquals( 1, bindRequest.getMessageId() );
        assertTrue( bindRequest.isVersion3() );
        assertEquals( "", bindRequest.getName().toString() );
        assertFalse( bindRequest.isSimple() );
        assertEquals( "KERBEROS_V4", bindRequest.getSaslMechanism() );
        assertEquals( "abcdef", Strings.utf8ToString(bindRequest.getCredentials()) );

        // Check the encoding
        try
        {
            ByteBuffer bb = encoder.encodeMessage( bindRequest );

            // Check the length
            assertEquals( 0x23, bb.limit() );

            String encodedPdu = Strings.dumpBytes(bb.array());

            assertEquals( encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            fail( ee.getMessage() );
        }
    }


    /**
     * Test the decoding of a BindRequest with an empty body
     */
    @Test
    public void testDecodeBindRequestEmptyBody()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x07 );
        stream.put( new byte[]
            { 0x30, 0x05, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x60, 0x00 // CHOICE { ..., bindRequest BindRequest, ...
            } );

        stream.flip();

        // Allocate a LdapMessage Container
        LdapMessageContainer<BindRequestDecorator> container = new LdapMessageContainer<BindRequestDecorator>( codec );

        // Decode a BindRequest message
        try
        {
            ldapDecoder.decode( stream, container );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
            return;
        }

        fail( "We should not reach this point" );
    }


    /**
     * Test the decoding of a BindRequest with an empty version
     */
    @Test
    public void testDecodeBindRequestEmptyVersion()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x09 );
        stream.put( new byte[]
            { 0x30, 0x07, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x60, 0x02, // CHOICE { ..., bindRequest BindRequest, ...
                0x02, 0x00 // version INTEGER (1..127),
            } );

        stream.flip();

        // Allocate a LdapMessage Container
        LdapMessageContainer<BindRequestDecorator> container = new LdapMessageContainer<BindRequestDecorator>( codec );

        // Decode a BindRequest message
        try
        {
            ldapDecoder.decode( stream, container );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
            return;
        }

        fail( "We should not reach this point" );
    }


    /**
     * Test the decoding of a BindRequest with a bad version (0)
     */
    @Test
    public void testDecodeBindRequestBadVersion0()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x0A );
        stream.put( new byte[]
            { 0x30, 0x08, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x60, 0x03, // CHOICE { ..., bindRequest BindRequest, ...
                0x02, 0x01, 0x00 // version INTEGER (1..127),
            } );

        stream.flip();

        // Allocate a LdapMessage Container
        LdapMessageContainer<BindRequestDecorator> container = new LdapMessageContainer<BindRequestDecorator>( codec );

        // Decode a BindRequest message
        try
        {
            ldapDecoder.decode( stream, container );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
            return;
        }

        fail( "We should not reach this point" );
    }


    /**
     * Test the decoding of a BindRequest with a bad version (4)
     */
    @Test
    public void testDecodeBindRequestBadVersion4()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x0A );
        stream.put( new byte[]
            { 0x30, 0x08, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x60, 0x03, // CHOICE { ..., bindRequest BindRequest, ...
                0x02, 0x01, 0x04 // version INTEGER (1..127),
            } );

        stream.flip();

        // Allocate a LdapMessage Container
        LdapMessageContainer<BindRequestDecorator> container = new LdapMessageContainer<BindRequestDecorator>( codec );

        // Decode a BindRequest message
        try
        {
            ldapDecoder.decode( stream, container );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
            return;
        }

        fail( "We should not reach this point" );
    }


    /**
     * Test the decoding of a BindRequest with a bad version (128)
     */
    @Test
    public void testDecodeBindRequestBadVersion128()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x0C );
        stream.put( new byte[]
            { 0x30, 0x0A, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x60, 0x04, // CHOICE { ..., bindRequest BindRequest, ...
                0x02, 0x02, 0x00, ( byte ) 0x80 // version INTEGER (1..127),
            } );

        stream.flip();

        // Allocate a LdapMessage Container
        LdapMessageContainer<BindRequestDecorator> container = new LdapMessageContainer<BindRequestDecorator>( codec );

        // Decode a BindRequest message
        try
        {
            ldapDecoder.decode( stream, container );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
            return;
        }

        fail( "We should not reach this point" );
    }


    /**
     * Test the decoding of a BindRequest with no name
     */
    @Test
    public void testDecodeBindRequestNoName()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x0A );
        stream.put( new byte[]
            { 0x30, 0x08, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x60, 0x03, // CHOICE { ..., bindRequest BindRequest, ...
                0x02, 0x01, 0x03 // version INTEGER (1..127),
            } );

        stream.flip();

        // Allocate a LdapMessage Container
        LdapMessageContainer<BindRequestDecorator> container = new LdapMessageContainer<BindRequestDecorator>( codec );

        // Decode a BindRequest message
        try
        {
            ldapDecoder.decode( stream, container );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
            return;
        }

        fail( "We should not reach this point" );
    }


    /**
     * Test the decoding of a BindRequest with an empty name
     */
    @Test
    public void testDecodeBindRequestEmptyName()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x0C );
        stream.put( new byte[]
            { 0x30, 0x0A, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x60, 0x05, // CHOICE { ..., bindRequest BindRequest, ...
                0x02, 0x01, 0x03, // version INTEGER (1..127),
                0x04, 0x00 } );

        stream.flip();

        // Allocate a LdapMessage Container
        LdapMessageContainer<BindRequestDecorator> container = new LdapMessageContainer<BindRequestDecorator>( codec );

        // Decode a BindRequest message
        try
        {
            ldapDecoder.decode( stream, container );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
            return;
        }

        fail( "We should not reach this point" );
    }


    /**
     * Test the decoding of a BindRequest with an empty simple
     */
    @Test
    public void testDecodeBindRequestEmptysimple()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x0E );
        stream.put( new byte[]
            { 0x30, 0x0C, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x60, 0x07, // CHOICE { ..., bindRequest BindRequest, ...
                0x02, 0x01, 0x03, // version INTEGER (1..127),
                0x04, 0x00, ( byte ) 0x80, 0x00 } );

        String decodedPdu = Strings.dumpBytes(stream.array());
        stream.flip();

        // Allocate a LdapMessage Container
        LdapMessageContainer<BindRequestDecorator> container = new LdapMessageContainer<BindRequestDecorator>( codec );

        // Decode the BindRequest PDU
        try
        {
            ldapDecoder.decode( stream, container );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        // Check the decoded BindRequest
        BindRequest bindRequest = container.getMessage();

        assertEquals( 1, bindRequest.getMessageId() );
        assertTrue( bindRequest.isVersion3() );
        assertEquals( "", bindRequest.getName().toString() );
        assertTrue( bindRequest.isSimple() );
        assertEquals( "", Strings.utf8ToString(bindRequest.getCredentials()) );

        // Check the encoding
        try
        {
            ByteBuffer bb = encoder.encodeMessage( bindRequest );

            // Check the length
            assertEquals( 0x0E, bb.limit() );

            String encodedPdu = Strings.dumpBytes(bb.array());

            assertEquals( encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            fail( ee.getMessage() );
        }
    }


    /**
     * Test the decoding of a BindRequest with an empty sasl
     */
    @Test
    public void testDecodeBindRequestEmptySasl()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x0E );
        stream.put( new byte[]
            { 0x30, 0x0C, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x60, 0x07, // CHOICE { ..., bindRequest BindRequest, ...
                0x02, 0x01, 0x03, // version INTEGER (1..127),
                0x04, 0x00, ( byte ) 0xA3, 0x00 } );

        stream.flip();

        // Allocate a LdapMessage Container
        LdapMessageContainer<BindRequestDecorator> container = new LdapMessageContainer<BindRequestDecorator>( codec );

        // Decode a BindRequest message
        try
        {
            ldapDecoder.decode( stream, container );
        }
        catch ( DecoderException de )
        {
            assertTrue( de instanceof ResponseCarryingException );
            Message response = ( ( ResponseCarryingException ) de ).getResponse();
            assertTrue( response instanceof BindResponseImpl );
            assertEquals( ResultCodeEnum.INVALID_CREDENTIALS, ( ( BindResponseImpl ) response ).getLdapResult()
                .getResultCode() );
            return;
        }

        fail( "We should not reach this point" );
    }


    /**
     * Test the decoding of a BindRequest with an empty mechanism
     */
    @Test
    public void testDecodeBindRequestEmptyMechanism()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x10 );
        stream.put( new byte[]
            { 0x30, 0x0E, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x60, 0x09, // CHOICE { ..., bindRequest BindRequest, ...
                0x02, 0x01, 0x03, // version INTEGER (1..127),
                0x04, 0x00, ( byte ) 0xA3, 0x02, 0x04, 0x00 } );

        String decodedPdu = Strings.dumpBytes(stream.array());
        stream.flip();

        // Allocate a LdapMessage Container
        LdapMessageContainer<BindRequestDecorator> container = new LdapMessageContainer<BindRequestDecorator>( codec );

        // Decode the BindRequest PDU
        try
        {
            ldapDecoder.decode( stream, container );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        // Check the decoded BindRequest
        BindRequest bindRequest = container.getMessage();

        assertEquals( 1, bindRequest.getMessageId() );
        assertTrue( bindRequest.isVersion3() );
        assertEquals( "", bindRequest.getName().toString() );
        assertFalse( bindRequest.isSimple() );
        assertEquals( "", bindRequest.getSaslMechanism() );

        // Check the encoding
        try
        {
            ByteBuffer bb = encoder.encodeMessage( bindRequest );

            // Check the length
            assertEquals( 0x10, bb.limit() );

            String encodedPdu = Strings.dumpBytes(bb.array());

            assertEquals( encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            fail( ee.getMessage() );
        }
    }


    /**
     * Test the decoding of a BindRequest with an bad mechanism
     */
    /* This test is not valid. I don't know how to generate a UnsupportedEncodingException ...
    @Test
    public void testDecodeBindRequestBadMechanism()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x11 );
        stream.put( new byte[]
            { 
            0x30, 0x0F,                 // LDAPMessage ::=SEQUENCE {
              0x02, 0x01, 0x01,         // messageID MessageID
              0x60, 0x0A,               // CHOICE { ..., bindRequest BindRequest, ...
                0x02, 0x01, 0x03,       // version INTEGER (1..127),
                0x04, 0x00, 
                ( byte ) 0xA3, 0x03, 
                  0x04, 0x01, (byte)0xFF
            } );

        stream.flip();

        // Allocate a LdapMessage Container
        Asn1Container container = new LdapMessageContainer();

        // Decode the BindRequest PDU
        try
        {
            ldapDecoder.decode( stream, container );
        }
        catch ( DecoderException de )
        {
            assertTrue( de instanceof ResponseCarryingException );
            Message response = ((ResponseCarryingException)de).getResponse();
            assertTrue( response instanceof BindResponseImpl );
            assertEquals( ResultCodeEnum.INAPPROPRIATEAUTHENTICATION, ((BindResponseImpl)response).getLdapResult().getResultCode() );
            return;
        }

        fail( "We should not reach this point" );
    }
    */

    /**
     * Test the decoding of a BindRequest with an empty credentials
     */
    @Test
    public void testDecodeBindRequestEmptyCredentials()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x12 );
        stream.put( new byte[]
            { 0x30, 0x10, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x60, 0x0B, // CHOICE { ..., bindRequest BindRequest, ...
                0x02, 0x01, 0x03, // version INTEGER (1..127),
                0x04, 0x00, ( byte ) 0xA3, 0x04, 0x04, 0x00, 0x04, 0x00, } );

        String decodedPdu = Strings.dumpBytes(stream.array());
        stream.flip();

        // Allocate a LdapMessage Container
        LdapMessageContainer<BindRequestDecorator> container = new LdapMessageContainer<BindRequestDecorator>( codec );

        // Decode the BindRequest PDU
        try
        {
            ldapDecoder.decode( stream, container );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        // Check the decoded BindRequest
        BindRequest bindRequest = container.getMessage();

        assertEquals( 1, bindRequest.getMessageId() );
        assertTrue( bindRequest.isVersion3() );
        assertEquals( "", bindRequest.getName().toString() );
        assertFalse( bindRequest.isSimple() );
        assertEquals( "", bindRequest.getSaslMechanism() );
        assertEquals( "", Strings.utf8ToString(bindRequest.getCredentials()) );

        // Check the encoding
        try
        {
            ByteBuffer bb = encoder.encodeMessage( bindRequest );

            // Check the length
            assertEquals( 0x12, bb.limit() );

            String encodedPdu = Strings.dumpBytes(bb.array());

            assertEquals( encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            fail( ee.getMessage() );
        }
    }


    /**
     * Test the decoding of a BindRequest with an empty credentials with
     * controls
     */
    @Test
    public void testDecodeBindRequestEmptyCredentialsWithControls()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x2F );
        stream.put( new byte[]
            { 0x30,
                0x2D, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01,
                0x01, // messageID MessageID
                0x60,
                0x0B, // CHOICE { ..., bindRequest BindRequest, ...
                0x02, 0x01,
                0x03, // version INTEGER (1..127),
                0x04, 0x00, ( byte ) 0xA3, 0x04, 0x04, 0x00, 0x04, 0x00, ( byte ) 0xA0,
                0x1B, // A control
                0x30, 0x19, 0x04, 0x17, 0x32, 0x2E, 0x31, 0x36, 0x2E, 0x38, 0x34, 0x30, 0x2E, 0x31, 0x2E, 0x31, 0x31,
                0x33, 0x37, 0x33, 0x30, 0x2E, 0x33, 0x2E, 0x34, 0x2E, 0x32 } );

        String decodedPdu = Strings.dumpBytes(stream.array());
        stream.flip();

        // Allocate a LdapMessage Container
        LdapMessageContainer<BindRequestDecorator> container = new LdapMessageContainer<BindRequestDecorator>( codec );

        // Decode the BindRequest PDU
        try
        {
            ldapDecoder.decode( stream, container );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        // Check the decoded BindRequest
        BindRequest bindRequest = container.getMessage();

        assertEquals( 1, bindRequest.getMessageId() );
        assertTrue( bindRequest.isVersion3() );
        assertEquals( "", bindRequest.getName().toString() );
        assertFalse( bindRequest.isSimple() );
        assertEquals( "", bindRequest.getSaslMechanism() );
        assertEquals( "", Strings.utf8ToString(bindRequest.getCredentials()) );

        // Check the Control
        Map<String, Control> controls = bindRequest.getControls();

        assertEquals( 1, controls.size() );

        @SuppressWarnings("unchecked")
        CodecControl<Control> control = (org.apache.directory.shared.ldap.codec.api.CodecControl<Control> ) controls.get( "2.16.840.1.113730.3.4.2" );
        assertEquals( "2.16.840.1.113730.3.4.2", control.getOid() );
        assertEquals( "", Strings.dumpBytes( ( byte[] ) control.getValue() ) );

        // Check the encoding
        try
        {
            ByteBuffer bb = encoder.encodeMessage( bindRequest );

            // Check the length
            assertEquals( 0x2F, bb.limit() );

            String encodedPdu = Strings.dumpBytes(bb.array());

            assertEquals( encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            fail( ee.getMessage() );
        }
    }


    /**
     * Test the decoding of a BindRequest with an empty mechanisms with controls
     */
    @Test
    public void testDecodeBindRequestEmptyMechanismWithControls()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x2D );
        stream.put( new byte[]
            { 0x30,
                0x2B, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01,
                0x01, // messageID MessageID
                0x60,
                0x09, // CHOICE { ..., bindRequest BindRequest, ...
                0x02, 0x01,
                0x03, // version INTEGER (1..127),
                0x04, 0x00, ( byte ) 0xA3, 0x02, 0x04, 0x00, ( byte ) 0xA0,
                0x1B, // A control
                0x30, 0x19, 0x04, 0x17, 0x32, 0x2E, 0x31, 0x36, 0x2E, 0x38, 0x34, 0x30, 0x2E, 0x31, 0x2E, 0x31, 0x31,
                0x33, 0x37, 0x33, 0x30, 0x2E, 0x33, 0x2E, 0x34, 0x2E, 0x32 } );

        String decodedPdu = Strings.dumpBytes(stream.array());
        stream.flip();

        // Allocate a LdapMessage Container
        LdapMessageContainer<BindRequestDecorator> container = new LdapMessageContainer<BindRequestDecorator>( codec );

        // Decode the BindRequest PDU
        try
        {
            ldapDecoder.decode( stream, container );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        // Check the decoded BindRequest
        BindRequest bindRequest = container.getMessage();

        assertEquals( 1, bindRequest.getMessageId() );
        assertTrue( bindRequest.isVersion3() );
        assertEquals( "", bindRequest.getName().toString() );
        assertFalse( bindRequest.isSimple() );
        assertEquals( "", bindRequest.getSaslMechanism() );
        assertEquals( "", Strings.utf8ToString(bindRequest.getCredentials()) );

        // Check the Control
        Map<String, Control> controls = bindRequest.getControls();

        assertEquals( 1, controls.size() );

        @SuppressWarnings("unchecked")
        CodecControl<Control> control = (org.apache.directory.shared.ldap.codec.api.CodecControl<Control> ) controls.get( "2.16.840.1.113730.3.4.2" );
        assertEquals( "2.16.840.1.113730.3.4.2", control.getOid() );
        assertEquals( "", Strings.dumpBytes( ( byte[] ) control.getValue() ) );

        // Check the encoding
        try
        {
            ByteBuffer bb = encoder.encodeMessage( bindRequest );

            // Check the length
            assertEquals( 0x2D, bb.limit() );

            String encodedPdu = Strings.dumpBytes(bb.array());

            assertEquals( encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            fail( ee.getMessage() );
        }
    }

    /**
     * Test the decoding of a BindRequest with Simple authentication and no
     * controls
     */
    /* No used by unit tests
    @Test
    public void testPerf() throws Exception
    {
        Dn name = new Dn( "uid=akarasulu,dc=example,dc=com" );
        long t0 = System.currentTimeMillis();
        
        for ( int i = 0; i< 10000; i++)
        {
            // Check the decoded BindRequest
            LdapMessage message = new LdapMessage();
            message.setMessageId( 1 );
            
            BindRequest br = new BindRequest();
            br.setMessageId( 1 );
            br.setName( name );
            
            Control control = new Control();
            control.setControlType( "2.16.840.1.113730.3.4.2" );

            LdapAuthentication authentication = new SimpleAuthentication();
            ((SimpleAuthentication)authentication).setSimple( StringTools.getBytesUtf8( "password" ) );

            br.addControl( control );
            br.setAuthentication( authentication );
            message.setProtocolOP( br );
    
            // Check the encoding
            try
            {
                message.encode();
            }
            catch ( EncoderException ee )
            {
                ee.printStackTrace();
                fail( ee.getMessage() );
            }
        }

        long t1 = System.currentTimeMillis();
        System.out.println( "Delta = " + (t1 - t0));
    }
    */
}

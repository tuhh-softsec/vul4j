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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.asn1.ber.Asn1Container;
import org.apache.directory.shared.asn1.ber.Asn1Decoder;
import org.apache.directory.shared.ldap.codec.DefaultLdapCodecService;
import org.apache.directory.shared.ldap.codec.CodecControl;
import org.apache.directory.shared.ldap.codec.LdapCodecService;
import org.apache.directory.shared.ldap.codec.LdapEncoder;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.controls.search.pagedSearch.PagedResultsDecorator;
import org.apache.directory.shared.ldap.codec.decorators.BindResponseDecorator;
import org.apache.directory.shared.ldap.codec.decorators.MessageDecorator;
import org.apache.directory.shared.ldap.model.message.BindResponse;
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
@Concurrent()
public class BindResponseTest
{
    /** The encoder instance */
    LdapEncoder encoder = new LdapEncoder();

    LdapCodecService codec = new DefaultLdapCodecService();

    /**
     * Test the decoding of a BindResponse
     */
    @Test
    public void testDecodeBindResponseSuccess()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x0E );

        stream.put( new byte[]
            { 0x30, 0x0C, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x61, 0x07, // CHOICE { ..., bindResponse BindResponse, ...
                // BindResponse ::= APPLICATION[1] SEQUENCE {
                // COMPONENTS OF LDAPResult,
                0x0A, 0x01, 0x00, // LDAPResult ::= SEQUENCE {
                // resultCode ENUMERATED {
                // success (0), ...
                // },
                0x04, 0x00, // matchedDN LDAPDN,
                0x04, 0x00 // errorMessage LDAPString,
            // referral [3] Referral OPTIONAL }
            // serverSaslCreds [7] OCTET STRING OPTIONAL }
            } );

        String decodedPdu = Strings.dumpBytes(stream.array());
        stream.flip();

        // Allocate a LdapMessage Container
        LdapMessageContainer<BindResponseDecorator> container = new LdapMessageContainer<BindResponseDecorator>( codec );

        // Decode the BindResponse PDU
        try
        {
            ldapDecoder.decode( stream, container );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        // Check the decoded BindResponse
        BindResponse bindResponse = container.getMessage();

        assertEquals( 1, bindResponse.getMessageId() );
        assertEquals( ResultCodeEnum.SUCCESS, bindResponse.getLdapResult().getResultCode() );
        assertEquals( "", bindResponse.getLdapResult().getMatchedDn().getName() );
        assertEquals( "", bindResponse.getLdapResult().getErrorMessage() );

        // Check the encoding
        try
        {
            ByteBuffer bb = encoder.encodeMessage( bindResponse );

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
     * Test the decoding of a BindResponse with a control
     */
    @Test
    public void testDecodeBindResponseWithControlSuccess()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x3C );

        stream.put( new byte[]
            { 0x30, 0x3A,                               // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01,                       // messageID MessageID
                0x61, 0x07,                             // CHOICE { ..., bindResponse BindResponse, ...
                                                        // BindResponse ::= APPLICATION[1] SEQUENCE {
                                                        // COMPONENTS OF LDAPResult,
                  0x0A,  0x01, 0x00,                    // LDAPResult ::= SEQUENCE {
                                                        // resultCode ENUMERATED {
                                                        // success (0), ...
                                                        // },
                  0x04, 0x00,                           // matchedDN LDAPDN,
                  0x04, 0x00,                           // errorMessage LDAPString,
                                                        // referral [3] Referral OPTIONAL }
                                                        // serverSaslCreds [7] OCTET STRING OPTIONAL }
                ( byte ) 0xa0, 0x2C,                    // controls
                  0x30, 0x2A,                           // The PagedSearchControl
                    0x04, 0x16,                         // Oid : 1.2.840.113556.1.4.319
                      0x31, 0x2e, 0x32, 0x2e, 0x38, 0x34, 0x30, 0x2e, 
                      0x31, 0x31, 0x33, 0x35, 0x35, 0x36, 0x2e, 0x31, 
                      0x2e, 0x34, 0x2e, 0x33, 0x31, 0x39, 
                    0x01, 0x01, ( byte ) 0xff,          // criticality: false
                    0x04, 0x0D, 
                      0x30, 0x0B, 
                        0x02, 0x01, 0x05,               // Size = 5, cookie = "abcdef" 
                        0x04, 0x06, 'a', 'b', 'c', 'd', 'e', 'f' } );

        String decodedPdu = Strings.dumpBytes(stream.array());
        stream.flip();

        // Allocate a LdapMessage Container
        LdapMessageContainer<BindResponseDecorator> container = new LdapMessageContainer<BindResponseDecorator>( codec );

        // Decode the BindResponse PDU
        try
        {
            ldapDecoder.decode( stream, container );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        // Check the decoded BindResponse
        BindResponse bindResponse = container.getMessage();

        assertEquals( 1, bindResponse.getMessageId() );
        assertEquals( ResultCodeEnum.SUCCESS, bindResponse.getLdapResult().getResultCode() );
        assertEquals( "", bindResponse.getLdapResult().getMatchedDn().getName() );
        assertEquals( "", bindResponse.getLdapResult().getErrorMessage() );

        // Check the Control
        Map<String, Control> controls = bindResponse.getControls();

        assertEquals( 1, controls.size() );

        Control control = controls.get( "1.2.840.113556.1.4.319" );
        assertEquals( "1.2.840.113556.1.4.319", control.getOid() );
        assertTrue( control instanceof PagedResultsDecorator );

        PagedResultsDecorator pagedSearchControl = ( PagedResultsDecorator ) control;

        assertEquals( 5, pagedSearchControl.getSize() );
        assertTrue( Arrays.equals( "abcdef".getBytes(), pagedSearchControl.getCookie() ) );

        // Check the encoding
        try
        {
            ByteBuffer bb = encoder.encodeMessage( bindResponse );

            // Check the length
            assertEquals( 0x3C, bb.limit() );

            String encodedPdu = Strings.dumpBytes( bb.array() );

            assertEquals( encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            fail( ee.getMessage() );
        }
    }


    /**
     * Test the decoding of a BindResponse with an empty credentials
     */
    @Test
    public void testDecodeBindResponseServerSASLEmptyCredentials()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x10 );

        stream.put( new byte[]
            { 0x30, 0x0E, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x61, 0x09, // CHOICE { ..., bindResponse BindResponse, ...
                // BindResponse ::= APPLICATION[1] SEQUENCE {
                // COMPONENTS OF LDAPResult,
                0x0A, 0x01, 0x00, // LDAPResult ::= SEQUENCE {
                // resultCode ENUMERATED {
                // success (0), ...
                // },
                0x04, 0x00, // matchedDN LDAPDN,
                0x04, 0x00, // errorMessage LDAPString,
                // referral [3] Referral OPTIONAL }
                ( byte ) 0x87, 0x00 // serverSaslCreds [7] OCTET STRING OPTIONAL
            // }
            } );

        String decodedPdu = Strings.dumpBytes(stream.array());
        stream.flip();

        // Allocate a LdapMessage Container
        LdapMessageContainer<BindResponseDecorator> container = new LdapMessageContainer<BindResponseDecorator>( codec );

        // Decode the BindResponse PDU
        try
        {
            ldapDecoder.decode( stream, container );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        // Check the decoded BindResponse
        BindResponse bindResponse = container.getMessage();

        assertEquals( 1, bindResponse.getMessageId() );
        assertEquals( ResultCodeEnum.SUCCESS, bindResponse.getLdapResult().getResultCode() );
        assertEquals( "", bindResponse.getLdapResult().getMatchedDn().getName() );
        assertEquals( "", bindResponse.getLdapResult().getErrorMessage() );
        assertEquals( "", Strings.utf8ToString(bindResponse.getServerSaslCreds()) );

        // Check the encoding
        try
        {
            ByteBuffer bb = encoder.encodeMessage( bindResponse );

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
     * Test the decoding of a BindResponse with an empty credentials with
     * controls
     */
    @Test
    public void testDecodeBindResponseServerSASLEmptyCredentialsWithControls()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x2D );

        stream.put( new byte[]
            { 0x30, 0x2B, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x61, 0x09, // CHOICE { ..., bindResponse BindResponse, ...
                // BindResponse ::= APPLICATION[1] SEQUENCE {
                // COMPONENTS OF LDAPResult,
                  0x0A, 0x01, 0x00, // LDAPResult ::= SEQUENCE {
                // resultCode ENUMERATED {
                // success (0), ...
                // },
                  0x04, 0x00, // matchedDN LDAPDN,
                  0x04, 0x00, // errorMessage LDAPString,
                // referral [3] Referral OPTIONAL }
                  ( byte ) 0x87, 0x00, // serverSaslCreds [7] OCTET STRING
                // OPTIONAL }
                ( byte ) 0xA0, 0x1B, // A control
                  0x30, 0x19, 
                    0x04, 0x17, 
                      0x32, 0x2E, 0x31, 0x36, 0x2E, 0x38, 0x34, 0x30,
                      0x2E, 0x31, 0x2E, 0x31, 0x31, 0x33, 0x37, 0x33, 
                      0x30, 0x2E, 0x33, 0x2E, 0x34, 0x2E, 0x32 
              } );

        String decodedPdu = Strings.dumpBytes(stream.array());
        stream.flip();

        // Allocate a LdapMessage Container
        LdapMessageContainer<BindResponseDecorator> container = new LdapMessageContainer<BindResponseDecorator>( codec );

        // Decode the BindResponse PDU
        try
        {
            ldapDecoder.decode( stream, container );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        // Check the decoded BindResponse
        BindResponse bindResponse = container.getMessage();

        assertEquals( 1, bindResponse.getMessageId() );
        assertEquals( ResultCodeEnum.SUCCESS, bindResponse.getLdapResult().getResultCode() );
        assertEquals( "", bindResponse.getLdapResult().getMatchedDn().getName() );
        assertEquals( "", bindResponse.getLdapResult().getErrorMessage() );
        assertEquals( "", Strings.utf8ToString(bindResponse.getServerSaslCreds()) );

        // Check the Control
        Map<String, Control> controls = bindResponse.getControls();

        assertEquals( 1, controls.size() );

        @SuppressWarnings("unchecked")
        CodecControl<Control> control = ( CodecControl<Control> ) controls.get( "2.16.840.1.113730.3.4.2" );
        assertEquals( "2.16.840.1.113730.3.4.2", control.getOid() );
        assertEquals( "", Strings.dumpBytes( ( byte[] ) control.getValue() ) );

        // Check the encoding
        try
        {
            ByteBuffer bb = encoder.encodeMessage( bindResponse );

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
     * Test the decoding of a BindResponse with a credentials
     */
    @Test
    public void testDecodeBindResponseServerSASL()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x12 );

        stream.put( new byte[]
            { 0x30, 0x10, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x61, 0x0B, // CHOICE { ..., bindResponse BindResponse, ...
                // BindResponse ::= APPLICATION[1] SEQUENCE {
                // COMPONENTS OF LDAPResult,
                0x0A, 0x01, 0x00, // LDAPResult ::= SEQUENCE {
                // resultCode ENUMERATED {
                // success (0), ...
                // },
                0x04, 0x00, // matchedDN LDAPDN,
                0x04, 0x00, // errorMessage LDAPString,
                // referral [3] Referral OPTIONAL }
                ( byte ) 0x87, 0x02, 'A', 'B' // serverSaslCreds [7] OCTET
            // STRING OPTIONAL }
            } );

        String decodedPdu = Strings.dumpBytes(stream.array());
        stream.flip();

        // Allocate a LdapMessage Container
        LdapMessageContainer<BindResponseDecorator> container = 
            new LdapMessageContainer<BindResponseDecorator>( codec );

        // Decode the BindResponse PDU
        try
        {
            ldapDecoder.decode( stream, container );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        // Check the decoded BindResponse
        BindResponse bindResponse = container.getMessage();

        assertEquals( 1, bindResponse.getMessageId() );
        assertEquals( ResultCodeEnum.SUCCESS, bindResponse.getLdapResult().getResultCode() );
        assertEquals( "", bindResponse.getLdapResult().getMatchedDn().getName() );
        assertEquals( "", bindResponse.getLdapResult().getErrorMessage() );
        assertEquals( "AB", Strings.utf8ToString(bindResponse.getServerSaslCreds()) );

        // Check the encoding
        try
        {
            ByteBuffer bb = encoder.encodeMessage( bindResponse );

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
     * Test the decoding of a BindResponse with no LdapResult
     */
    @Test
    public void testDecodeAddResponseEmptyResult()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x07 );

        stream.put( new byte[]
            { 0x30, 0x05, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x61, 0x00, // CHOICE { ..., bindResponse BindResponse, ...
            } );

        stream.flip();

        // Allocate a LdapMessage Container
        Asn1Container container = new LdapMessageContainer<MessageDecorator<? extends Message>>( codec );

        // Decode a BindResponse message
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
}

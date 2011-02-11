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
package org.apache.directory.shared.ldap.codec.add;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.util.Map;

import com.mycila.junit.concurrent.Concurrency;
import com.mycila.junit.concurrent.ConcurrentJunitRunner;
import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.asn1.ber.Asn1Decoder;
import org.apache.directory.shared.asn1.ber.Asn1Container;
import org.apache.directory.shared.ldap.codec.AbstractCodecServiceTest;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.api.CodecControl;
import org.apache.directory.shared.ldap.codec.decorators.AddResponseDecorator;
import org.apache.directory.shared.ldap.codec.decorators.MessageDecorator;
import org.apache.directory.shared.ldap.model.message.AddResponse;
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
public class AddResponseTest extends AbstractCodecServiceTest
{
    /**
     * Test the decoding of a AddResponse
     */
    @Test
    public void testDecodeAddResponseSuccess()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x0E );

        stream.put( new byte[]
            { 0x30, 0x0C, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x69, 0x07, // CHOICE { ..., addResponse AddResponse, ...
                // AddResponse ::= [APPLICATION 9] LDAPResult
                0x0A, 0x01, 0x00, // LDAPResult ::= SEQUENCE {
                // resultCode ENUMERATED {
                // success (0), ...
                // },
                0x04, 0x00, // matchedDN LDAPDN,
                0x04, 0x00 // errorMessage LDAPString,
            // referral [3] Referral OPTIONAL }
            // }
            } );

        String decodedPdu = Strings.dumpBytes(stream.array());
        stream.flip();

        // Allocate a LdapMessage Container
        LdapMessageContainer<AddResponseDecorator> container = new LdapMessageContainer<AddResponseDecorator>( codec );

        // Decode the AddResponse PDU
        try
        {
            ldapDecoder.decode( stream, container );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        // Check the decoded AddResponse
        AddResponse addResponse = container.getMessage();

        assertEquals( 1, addResponse.getMessageId() );
        assertEquals( ResultCodeEnum.SUCCESS, addResponse.getLdapResult().getResultCode() );
        assertEquals( "", addResponse.getLdapResult().getMatchedDn().getName() );
        assertEquals( "", addResponse.getLdapResult().getErrorMessage() );

        try
        {
            ByteBuffer bb = encoder.encodeMessage( addResponse );

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
     * Test the decoding of a AddResponse with no LdapResult
     */
    @Test
    public void testDecodeAddResponseEmptyResult()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x0E );

        stream.put( new byte[]
            { 0x30, 0x0C, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x69, 0x00, // CHOICE { ..., addResponse AddResponse, ...
            } );

        stream.flip();

        // Allocate a LdapMessage Container
        Asn1Container ldapMessageContainer = new LdapMessageContainer<MessageDecorator<? extends Message>>( codec );

        // Decode a AddResponse message
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
            return;
        }

        fail( "We should not reach this point" );
    }


    /**
     * Test the decoding of a AddResponse with a control
     */
    @Test
    public void testDecodeAddResponseSuccessWithControl()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x2B );

        stream.put( new byte[]
            { 0x30,
                0x29, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01,
                0x01, // messageID MessageID
                0x69,
                0x07, // CHOICE { ..., addResponse AddResponse, ...
                // AddResponse ::= [APPLICATION 9] LDAPResult
                0x0A,
                0x01,
                0x00,// LDAPResult ::= SEQUENCE {
                // resultCode ENUMERATED {
                // success (0), ...
                // },
                0x04,
                0x00, // matchedDN LDAPDN,
                0x04,
                0x00, // errorMessage LDAPString,
                // referral [3] Referral OPTIONAL }
                // }
                ( byte ) 0xA0,
                0x1B, // A control
                0x30, 0x19, 0x04, 0x17, 0x32, 0x2E, 0x31, 0x36, 0x2E, 0x38, 0x34, 0x30, 0x2E, 0x31, 0x2E, 0x31, 0x31,
                0x33, 0x37, 0x33, 0x30, 0x2E, 0x33, 0x2E, 0x34, 0x2E, 0x32 } );

        String decodedPdu = Strings.dumpBytes(stream.array());
        stream.flip();

        // Allocate a LdapMessage Container
        LdapMessageContainer<AddResponseDecorator> container 
            = new LdapMessageContainer<AddResponseDecorator>( codec );

        // Decode the AddResponse PDU
        try
        {
            ldapDecoder.decode( stream, container );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        // Check the decoded AddResponse
        AddResponse addResponse = container.getMessage();

        assertEquals( 1, addResponse.getMessageId() );
        assertEquals( ResultCodeEnum.SUCCESS, addResponse.getLdapResult().getResultCode() );
        assertEquals( "", addResponse.getLdapResult().getMatchedDn().getName() );
        assertEquals( "", addResponse.getLdapResult().getErrorMessage() );

        // Check the Control
        Map<String, Control> controls = addResponse.getControls();

        assertEquals( 1, controls.size() );

        @SuppressWarnings("unchecked")
        CodecControl<Control> control = (org.apache.directory.shared.ldap.codec.api.CodecControl<Control> ) controls.get( "2.16.840.1.113730.3.4.2" );
        assertEquals( "2.16.840.1.113730.3.4.2", control.getOid() );
        assertEquals( "", Strings.dumpBytes( ( byte[] ) control.getValue()) );

        try
        {
            /** The encoder instance */
            ByteBuffer bb = encoder.encodeMessage( addResponse );

            // Check the length
            assertEquals( 0x02B, bb.limit() );

            String encodedPdu = Strings.dumpBytes(bb.array());

            assertEquals( encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            fail( ee.getMessage() );
        }
    }
}

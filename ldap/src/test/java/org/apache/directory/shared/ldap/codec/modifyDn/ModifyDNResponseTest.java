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
package org.apache.directory.shared.ldap.codec.modifyDn;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.util.Map;

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.asn1.ber.Asn1Decoder;
import org.apache.directory.shared.asn1.ber.IAsn1Container;
import org.apache.directory.shared.asn1.codec.DecoderException;
import org.apache.directory.shared.asn1.codec.EncoderException;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.message.LdapProtocolEncoder;
import org.apache.directory.shared.ldap.message.ResultCodeEnum;
import org.apache.directory.shared.ldap.message.control.Control;
import org.apache.directory.shared.ldap.message.internal.InternalModifyDnResponse;
import org.apache.directory.shared.ldap.util.StringTools;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test the ModifyDNResponse codec
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
public class ModifyDNResponseTest
{
    /** The encoder instance */
    LdapProtocolEncoder encoder = new LdapProtocolEncoder();


    /**
     * Test the decoding of a ModifyDNResponse
     */
    @Test
    public void testDecodeModifyResponseSuccess()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x0E );

        stream.put( new byte[]
            { 0x30, 0x0C, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x6D, 0x07, // CHOICE { ..., modifyDNResponse ModifyDNResponse,
                // ...
                // ModifyDNResponse ::= [APPLICATION 13] LDAPResult
                0x0A, 0x01, 0x00, // LDAPResult ::= SEQUENCE {
                // resultCode ENUMERATED {
                // success (0), ...
                // },
                0x04, 0x00, // matchedDN LDAPDN,
                0x04, 0x00 // errorMessage LDAPString,
            // referral [3] Referral OPTIONAL }
            // }
            } );

        String decodedPdu = StringTools.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode the ModifyDNResponse PDU
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        // Check the decoded ModifyDNResponse PDU
        InternalModifyDnResponse modifyDnResponse = ( ( LdapMessageContainer ) ldapMessageContainer )
            .getInternalModifyDnResponse();

        assertEquals( 1, modifyDnResponse.getMessageId() );
        assertEquals( ResultCodeEnum.SUCCESS, modifyDnResponse.getLdapResult().getResultCode() );
        assertEquals( "", modifyDnResponse.getLdapResult().getMatchedDn().getName() );
        assertEquals( "", modifyDnResponse.getLdapResult().getErrorMessage() );

        // Check the encoding
        try
        {
            ByteBuffer bb = encoder.encodeMessage( modifyDnResponse );

            // Check the length
            assertEquals( 0x0E, bb.limit() );

            String encodedPdu = StringTools.dumpBytes( bb.array() );

            assertEquals( encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            fail( ee.getMessage() );
        }
    }


    /**
     * Test the decoding of a ModifyDNResponse with controls
     */
    @Test
    public void testDecodeModifyResponseSuccessWithControls()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x2B );

        stream.put( new byte[]
            { 0x30,
                0x29, // LDAPMessage ::=SEQUENCE {
                0x02,
                0x01,
                0x01, // messageID MessageID
                0x6D,
                0x07, // CHOICE { ..., modifyDNResponse ModifyDNResponse,
                // ...
                // ModifyDNResponse ::= [APPLICATION 13] LDAPResult
                0x0A,
                0x01,
                0x00, // LDAPResult ::= SEQUENCE {
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

        String decodedPdu = StringTools.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode the ModifyDNResponse PDU
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        // Check the decoded ModifyDNResponse PDU
        InternalModifyDnResponse modifyDnResponse = ( ( LdapMessageContainer ) ldapMessageContainer )
            .getInternalModifyDnResponse();

        assertEquals( 1, modifyDnResponse.getMessageId() );
        assertEquals( ResultCodeEnum.SUCCESS, modifyDnResponse.getLdapResult().getResultCode() );
        assertEquals( "", modifyDnResponse.getLdapResult().getMatchedDn().getName() );
        assertEquals( "", modifyDnResponse.getLdapResult().getErrorMessage() );

        // Check the Control
        Map<String, Control> controls = modifyDnResponse.getControls();

        assertEquals( 1, controls.size() );

        Control control = controls.get( "2.16.840.1.113730.3.4.2" );
        assertEquals( "2.16.840.1.113730.3.4.2", control.getOid() );
        assertEquals( "", StringTools.dumpBytes( ( byte[] ) control.getValue() ) );

        // Check the encoding
        try
        {
            ByteBuffer bb = encoder.encodeMessage( modifyDnResponse );

            // Check the length
            assertEquals( 0x2B, bb.limit() );

            String encodedPdu = StringTools.dumpBytes( bb.array() );

            assertEquals( encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            fail( ee.getMessage() );
        }
    }


    /**
     * Test the decoding of a ModifyDNResponse with no LdapResult
     */
    @Test
    public void testDecodeModifyDNResponseEmptyResult()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x07 );

        stream.put( new byte[]
            { 0x30, 0x05, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x6D, 0x00 // CHOICE { ..., modifyDNResponse ModifyDNResponse,
            // ...
            } );

        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode a ModifyDNResponse message
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
}

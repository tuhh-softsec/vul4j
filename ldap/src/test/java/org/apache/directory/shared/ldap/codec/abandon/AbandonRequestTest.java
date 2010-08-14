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
package org.apache.directory.shared.ldap.codec.abandon;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import org.apache.directory.shared.ldap.message.AbandonRequestImpl;
import org.apache.directory.shared.ldap.message.LdapProtocolEncoder;
import org.apache.directory.shared.ldap.message.control.Control;
import org.apache.directory.shared.ldap.message.internal.InternalAbandonRequest;
import org.apache.directory.shared.ldap.util.StringTools;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test an AbandonRequest
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
public class AbandonRequestTest
{
    /** The encoder instance */
    LdapProtocolEncoder encoder = new LdapProtocolEncoder();


    /**
     * Test the decoding of a AbandonRequest with controls
     */
    @Test
    public void testDecodeAbandonRequestWithControls()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x64 );
        stream.put( new byte[]
            { 0x30, 0x62, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x03, // messageID MessageID
                0x50, 0x01, 0x02, // CHOICE { ..., abandonRequest
                // AbandonRequest,...
                ( byte ) 0xA0, 0x5A, // controls [0] Controls OPTIONAL }
                0x30, 0x1A, // Control ::= SEQUENCE {
                // controlType LDAPOID,
                0x04, 0x0D, '1', '.', '3', '.', '6', '.', '1', '.', '5', '.', '5', '.', '1',
                // criticality BOOLEAN DEFAULT FALSE,
                0x01, 0x01, ( byte ) 0xFF,
                // controlValue OCTET STRING OPTIONAL }
                0x04, 0x06, 'a', 'b', 'c', 'd', 'e', 'f', 0x30, 0x17, // Control ::= SEQUENCE {
                // controlType LDAPOID,
                0x04, 0x0D, '1', '.', '3', '.', '6', '.', '1', '.', '5', '.', '5', '.', '2',
                // controlValue OCTET STRING OPTIONAL }
                0x04, 0x06, 'g', 'h', 'i', 'j', 'k', 'l', 0x30, 0x12, // Control ::= SEQUENCE {
                // controlType LDAPOID,
                0x04, 0x0D, '1', '.', '3', '.', '6', '.', '1', '.', '5', '.', '5', '.', '3',
                // criticality BOOLEAN DEFAULT FALSE }
                0x01, 0x01, ( byte ) 0xFF, 0x30, 0x0F, // Control ::= SEQUENCE {
                // controlType LDAPOID}
                0x04, 0x0D, '1', '.', '3', '.', '6', '.', '1', '.', '5', '.', '5', '.', '4' } );

        stream.flip();

        // Allocate a LdapMessageContainer Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode the PDU
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        // Check that everything is OK
        InternalAbandonRequest abandonRequest = ( ( LdapMessageContainer ) ldapMessageContainer ).getAbandonRequest();

        // Copy the message
        InternalAbandonRequest internalAbandonRequest = new AbandonRequestImpl( abandonRequest.getMessageId() );
        internalAbandonRequest.setAbandoned( abandonRequest.getAbandoned() );

        assertEquals( 3, abandonRequest.getMessageId() );
        assertEquals( 2, abandonRequest.getAbandoned() );

        // Check the Controls
        Map<String, Control> controls = abandonRequest.getControls();

        assertEquals( 4, controls.size() );

        Control control = controls.get( "1.3.6.1.5.5.1" );
        assertEquals( "1.3.6.1.5.5.1", control.getOid() );
        assertEquals( "0x61 0x62 0x63 0x64 0x65 0x66 ", StringTools.dumpBytes( ( byte[] ) control.getValue() ) );
        assertTrue( control.isCritical() );
        internalAbandonRequest.add( control );

        control = controls.get( "1.3.6.1.5.5.2" );
        assertEquals( "1.3.6.1.5.5.2", control.getOid() );
        assertEquals( "0x67 0x68 0x69 0x6A 0x6B 0x6C ", StringTools.dumpBytes( ( byte[] ) control.getValue() ) );
        assertFalse( control.isCritical() );
        internalAbandonRequest.add( control );

        control = controls.get( "1.3.6.1.5.5.3" );
        assertEquals( "1.3.6.1.5.5.3", control.getOid() );
        assertEquals( "", StringTools.dumpBytes( ( byte[] ) control.getValue() ) );
        assertTrue( control.isCritical() );
        internalAbandonRequest.add( control );

        control = controls.get( "1.3.6.1.5.5.4" );
        assertEquals( "1.3.6.1.5.5.4", control.getOid() );
        assertEquals( "", StringTools.dumpBytes( ( byte[] ) control.getValue() ) );
        assertFalse( control.isCritical() );
        internalAbandonRequest.add( control );

        // Check the encoding
        try
        {
            ByteBuffer bb = encoder.encodeMessage( internalAbandonRequest );

            // Check the length
            assertEquals( 0x64, bb.limit() );

            // Don't check the PDU, as control are in a Map, and can be in a different order
            // So we decode the generated PDU, and we compare it with the initial message
            try
            {
                ldapDecoder.decode( bb, ldapMessageContainer );
            }
            catch ( DecoderException de )
            {
                de.printStackTrace();
                fail( de.getMessage() );
            }

            InternalAbandonRequest abandonRequest2 = ( ( LdapMessageContainer ) ldapMessageContainer )
                .getAbandonRequest();

            assertEquals( abandonRequest, abandonRequest2 );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            fail( ee.getMessage() );
        }
    }


    /**
     * Test the decoding of a AbandonRequest with no controls
     */
    @Test
    public void testDecodeAbandonRequestNoControlsHighMessageId()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x0A );
        stream.put( new byte[]
            { 0x30, 0x08, // LDAPMessage ::=SEQUENCE {
                // messageID MessageID
                0x02, 0x03, 0x00, ( byte ) 0x80, 0x13, 0x50, 0x01, 0x02 // CHOICE { ..., abandonRequest
            // AbandonRequest,...
            // AbandonRequest ::= [APPLICATION 16] MessageID
            } );

        String decodedPdu = StringTools.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a LdapMessageContainer Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode the PDU
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        // Check that everything is OK
        InternalAbandonRequest abandonRequest = ( ( LdapMessageContainer ) ldapMessageContainer ).getAbandonRequest();

        assertEquals( 32787, abandonRequest.getMessageId() );
        assertEquals( 2, abandonRequest.getAbandoned() );

        // Check the length
        InternalAbandonRequest internalAbandonRequest = new AbandonRequestImpl( abandonRequest.getMessageId() );
        internalAbandonRequest.setAbandoned( abandonRequest.getAbandoned() );

        // Check the encoding
        try
        {
            ByteBuffer bb = encoder.encodeMessage( internalAbandonRequest );

            // Check the length
            assertEquals( 0x0A, bb.limit() );

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
     * Test the decoding of a AbandonRequest with a null messageId
     */
    @Test
    public void testDecodeAbandonRequestNoMessageId()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x0A );
        stream.put( new byte[]
            { 0x30, 0x08, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x50, 0x00 // CHOICE { ..., abandonRequest AbandonRequest,...
            // AbandonRequest ::= [APPLICATION 16] MessageID
            } );

        stream.flip();

        // Allocate a LdapMessageContainer Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode the PDU
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
     * Test the decoding of a AbandonRequest with a bad Message Id
     */
    @Test
    public void testDecodeAbandonRequestBadMessageId()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x0B );
        stream.put( new byte[]
            { 0x30, 0x09, // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                0x50, 0x01, ( byte ) 0xFF // CHOICE { ..., abandonRequest AbandonRequest,...
            // AbandonRequest ::= [APPLICATION 16] MessageID
            } );

        stream.flip();

        // Allocate a LdapMessageContainer Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode the PDU
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

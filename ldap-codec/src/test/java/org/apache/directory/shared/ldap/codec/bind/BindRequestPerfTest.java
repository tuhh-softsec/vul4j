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
import java.util.Map;

import com.mycila.junit.concurrent.Concurrency;
import com.mycila.junit.concurrent.ConcurrentJunitRunner;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.asn1.ber.Asn1Decoder;
import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.ldap.codec.LdapEncoder;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.api.CodecControl;
import org.apache.directory.shared.ldap.codec.api.DefaultLdapCodecService;
import org.apache.directory.shared.ldap.codec.api.LdapCodecService;
import org.apache.directory.shared.ldap.codec.decorators.BindRequestDecorator;
import org.apache.directory.shared.ldap.model.message.BindRequest;
import org.apache.directory.shared.ldap.model.message.BindRequestImpl;
import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.directory.shared.ldap.model.message.controls.AbstractControl;
import org.apache.directory.shared.ldap.model.message.controls.OpaqueControl;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.util.Strings;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrency()
public class BindRequestPerfTest
{
    /** The encoder instance */
    LdapEncoder encoder = new LdapEncoder();

    LdapCodecService codec = new DefaultLdapCodecService();

    /**
     * Test the decoding of a BindRequest with Simple authentication and no
     * controls
     */
    @Test
    @Ignore
    public void testDecodeBindRequestSimpleNoControlsPerf()
    {
        Asn1Decoder ldapDecoder = new Asn1Decoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x52 );
        stream.put( new byte[]
            {
                0x30,
                0x50, // LDAPMessage ::=SEQUENCE {
                0x02,
                0x01,
                0x01, // messageID MessageID
                0x60,
                0x2E, // CHOICE { ..., bindRequest BindRequest, ...
                // BindRequest ::= APPLICATION[0] SEQUENCE {
                0x02,
                0x01,
                0x03, // version INTEGER (1..127),
                0x04,
                0x1F, // name LDAPDN,
                'u', 'i', 'd', '=', 'a', 'k', 'a', 'r', 'a', 's', 'u', 'l', 'u', ',', 'd', 'c', '=', 'e', 'x', 'a',
                'm', 'p', 'l', 'e', ',', 'd', 'c', '=', 'c', 'o', 'm',
                ( byte ) 0x80,
                0x08, // authentication AuthenticationChoice
                // AuthenticationChoice ::= CHOICE { simple [0] OCTET STRING,
                // ...
                'p', 'a', 's', 's', 'w', 'o', 'r', 'd', ( byte ) 0xA0,
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
            int nbLoops = 1000000;
            long t0 = System.currentTimeMillis();

            for ( int i = 0; i < nbLoops; i++ )
            {
                ldapDecoder.decode( stream, container );
                container.clean();
                stream.flip();
            }

            long t1 = System.currentTimeMillis();
            System.out.println( "testDecodeBindRequestSimpleNoControlsPerf, " + nbLoops + " loops, Delta = "
                + ( t1 - t0 ) );

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
            assertEquals( 0x52, bb.limit() );

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
    @Test
    @Ignore
    public void testEncodeBindRequestPerf() throws Exception
    {
        Dn name = new Dn( "uid=akarasulu,dc=example,dc=com" );
        int nbLoops = 1000000;
        long t0 = System.currentTimeMillis();

        for ( int i = 0; i < nbLoops; i++ )
        {
            // Check the decoded BindRequest
            BindRequest bindRequest = new BindRequestImpl( 1 );

            bindRequest.setSimple( true );
            bindRequest.setName( name );
            bindRequest.setCredentials( Strings.getBytesUtf8("password") );
            Control control = new OpaqueControl( "2.16.840.1.113730.3.4.2" );

            bindRequest.addControl( control );

            // Check the encoding
            try
            {
                encoder.encodeMessage( bindRequest );
            }
            catch ( EncoderException ee )
            {
                ee.printStackTrace();
                fail( ee.getMessage() );
            }
        }

        long t1 = System.currentTimeMillis();
        System.out.println( "BindRequest testEncodeBindRequestPerf, " + nbLoops + " loops, Delta = " + ( t1 - t0 ) );
    }
}

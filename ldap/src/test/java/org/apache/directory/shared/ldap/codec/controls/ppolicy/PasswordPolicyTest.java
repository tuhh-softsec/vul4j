/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */

package org.apache.directory.shared.ldap.codec.controls.ppolicy;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;

import org.apache.directory.shared.ldap.codec.DefaultLdapCodecService;
import org.apache.directory.shared.ldap.codec.ILdapCodecService;
import org.apache.directory.shared.ldap.extras.controls.IPasswordPolicy;
import org.apache.directory.shared.util.Strings;
import org.junit.Test;


/**
 * PasswordPolicyResponseControlTest.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PasswordPolicyTest
{
    ILdapCodecService codec = new DefaultLdapCodecService();
    
    @Test
    public void testDecodeRespWithExpiryWarningAndError() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0xA );

        bb.put( new byte[]
            { 
                      0x30, 0x08,
             ( byte ) 0xA0, 0x03,         // timeBeforeExpiration
             ( byte ) 0x80, 0x01, 0x01, 
             ( byte ) 0x81, 0x01, 0x01    // ppolicyError
            } );

        bb.flip();

        PasswordPolicyDecorator control = new PasswordPolicyDecorator( codec, true );
        IPasswordPolicy passwordPolicy = ( IPasswordPolicy ) control.decode( bb.array() );

        assertTrue( passwordPolicy.hasResponse() );
        assertEquals( 1, passwordPolicy.getResponse().getTimeBeforeExpiration() );
        assertEquals( 1, passwordPolicy.getResponse().getPasswordPolicyError().getValue() );
        
        ByteBuffer encoded = ( ( PasswordPolicyDecorator ) passwordPolicy ).encode( 
            ByteBuffer.allocate( ( ( PasswordPolicyDecorator ) passwordPolicy ).computeLength() ) );
        assertEquals( Strings.dumpBytes( bb.array() ), Strings.dumpBytes( encoded.array() ) );
    }
    

    @Test
    public void testDecodeRespWithGraceAuthWarningAndError() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0xA );

        bb.put( new byte[]
            { 
                       0x30, 0x08,
              ( byte ) 0xA0, 0x03,           // warning
              ( byte ) 0x81, 0x01, 0x01,     // graceAuthNsRemaining
              ( byte ) 0x81, 0x01, 0x01      // error
            } );

        bb.flip();

        PasswordPolicyDecorator control = new PasswordPolicyDecorator( codec, true );
        IPasswordPolicy passwordPolicy = ( IPasswordPolicy ) control.decode( bb.array() );
        
        assertTrue( passwordPolicy.hasResponse() );
        assertEquals( 1, passwordPolicy.getResponse().getGraceAuthNsRemaining() );
        assertEquals( 1, passwordPolicy.getResponse().getPasswordPolicyError().getValue() );
        
        ByteBuffer encoded = ( ( PasswordPolicyDecorator ) passwordPolicy ).encode( 
            ByteBuffer.allocate( ( ( PasswordPolicyDecorator ) passwordPolicy ).computeLength() ) );
        assertEquals( Strings.dumpBytes( bb.array() ), Strings.dumpBytes( encoded.array())  );
    }

    
    @Test
    public void testDecodeRespWithTimeBeforeExpiryWarningOnly() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 7 );

        bb.put( new byte[]
            { 
                       0x30, 0x05,
              ( byte ) 0xA0, 0x03,
              ( byte ) 0x80, 0x01, 0x01 //  timeBeforeExpiration
            } );

        bb.flip();

        PasswordPolicyDecorator control = new PasswordPolicyDecorator( codec, true );
        IPasswordPolicy passwordPolicy = ( IPasswordPolicy ) control.decode( bb.array() );

        assertTrue( passwordPolicy.hasResponse() );
        assertEquals( 1, passwordPolicy.getResponse().getTimeBeforeExpiration() );
        
        ByteBuffer encoded = ( ( PasswordPolicyDecorator ) passwordPolicy ).encode( 
            ByteBuffer.allocate( ( ( PasswordPolicyDecorator ) passwordPolicy ).computeLength() ) );
        assertEquals( Strings.dumpBytes( bb.array() ), Strings.dumpBytes( encoded.array() ) );
    }
    

    @Test
    public void testDecodeRespWithGraceAuthWarningOnly() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 7 );

        bb.put( new byte[]
            { 
                       0x30, 0x05,
              ( byte ) 0xA0, 0x03,
              ( byte ) 0x81, 0x01, 0x01 //  graceAuthNsRemaining
            } );

        bb.flip();

        PasswordPolicyDecorator control = new PasswordPolicyDecorator( codec, true );
        IPasswordPolicy passwordPolicy = ( IPasswordPolicy ) control.decode( bb.array() );

        assertTrue( passwordPolicy.hasResponse() );
        assertEquals( 1, passwordPolicy.getResponse().getGraceAuthNsRemaining() );
        
        ByteBuffer encoded = ( ( PasswordPolicyDecorator) passwordPolicy ).encode( 
            ByteBuffer.allocate( ( ( PasswordPolicyDecorator) passwordPolicy ).computeLength() ) );
        assertEquals( Strings.dumpBytes( bb.array() ), Strings.dumpBytes( encoded.array() ) );
    }
    
    
    @Test
    public void testDecodeRespWithErrorOnly() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 5 );

        bb.put( new byte[]
            { 
                       0x30, 0x03,
              ( byte ) 0x81, 0x01, 0x01 //  error
            } );

        bb.flip();

        PasswordPolicyDecorator control = new PasswordPolicyDecorator( codec, true );
        IPasswordPolicy passwordPolicy = ( IPasswordPolicy ) control.decode( bb.array() );
        
        assertTrue( passwordPolicy.hasResponse() );
        assertEquals( 1, passwordPolicy.getResponse().getPasswordPolicyError().getValue() );
        
        ByteBuffer encoded = ( ( PasswordPolicyDecorator ) passwordPolicy ).encode( 
            ByteBuffer.allocate( ( ( PasswordPolicyDecorator) passwordPolicy ).computeLength() ) );
        assertEquals( Strings.dumpBytes( bb.array() ), Strings.dumpBytes( encoded.array() ) );
    }

    
    @Test
    public void testDecodeRespWithoutWarningAndError() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 2 );

        bb.put( new byte[]
            { 
             0x30, 0x00
            } );

        bb.flip();

        PasswordPolicyDecorator control = new PasswordPolicyDecorator( codec, true );
        IPasswordPolicy passwordPolicy = ( IPasswordPolicy ) control.decode( bb.array() );
        
        assertNotNull( passwordPolicy );
        assertTrue( passwordPolicy.hasResponse() );
        
        ByteBuffer encoded = ( ( PasswordPolicyDecorator ) passwordPolicy ).encode( 
            ByteBuffer.allocate( ( ( PasswordPolicyDecorator ) passwordPolicy ).computeLength() ) );
        assertEquals( "", Strings.dumpBytes( encoded.array() ) );
    }
}

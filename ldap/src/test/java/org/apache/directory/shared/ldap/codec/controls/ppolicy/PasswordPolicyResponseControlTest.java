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

import java.nio.ByteBuffer;

import org.apache.directory.shared.asn1.ber.Asn1Decoder;
import org.apache.directory.shared.ldap.util.StringTools;
import org.junit.Test;


/**
 * PasswordPolicyResponseControlTest.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PasswordPolicyResponseControlTest
{

    @Test
    public void testDecodeRespWithExpiryWarningAndError() throws Exception
    {
        Asn1Decoder decoder = new PasswordPolicyResponseControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 8 );

        bb.put( new byte[]
            { 
             0x30, 0x06,
              (byte)0x80, 1, 1, //  
              0x0A, 0x01, 1 
            } );

        bb.flip();

        PasswordPolicyResponseControlContainer container = new PasswordPolicyResponseControlContainer();
        container.setPasswordPolicyResponseControl( new PasswordPolicyResponseControl() );

        decoder.decode( bb, container );

        PasswordPolicyResponseControl control = container.getPasswordPolicyResponseControl();
        assertEquals( 1, control.getTimeBeforeExpiration() );
        assertEquals( 1, control.getPasswordPolicyError().getValue() );
        
        ByteBuffer buffer = ByteBuffer.allocate( 0x25 );
        buffer.put( new byte[]
               {
                 0x30, 0x23,
                  0x04, 0x19,
                   '1','.', '3', '.', '6', '.', '1', '.', '4',
                   '.', '1', '.', '4', '2', '.', '2', '.', '2',
                   '7', '.', '8', '.', '5', '.', '1',
                   0x04, 0x06,
                   (byte)0x80, 1, 0x01, //  
                   0x0A, 0x01, 0x01
               } );
        buffer.flip();
        
        ByteBuffer encoded = control.encode( ByteBuffer.allocate( control.computeLength() ) );
        assertEquals( StringTools.dumpBytes( buffer.array() ), StringTools.dumpBytes( encoded.array() ) );
    }
    

    @Test
    public void testDecodeRespWithGraceAuthWarningAndError() throws Exception
    {
        Asn1Decoder decoder = new PasswordPolicyResponseControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 8 );

        bb.put( new byte[]
            { 
             0x30, 0x06,
              (byte)0x81, 1, 1, //  
              0x0A, 0x01, 1 
            } );

        bb.flip();

        PasswordPolicyResponseControlContainer container = new PasswordPolicyResponseControlContainer();
        container.setPasswordPolicyResponseControl( new PasswordPolicyResponseControl() );

        decoder.decode( bb, container );

        PasswordPolicyResponseControl control = container.getPasswordPolicyResponseControl();
        assertEquals( 1, control.getGraceAuthNsRemaining() );
        assertEquals( 1, control.getPasswordPolicyError().getValue() );
        
        ByteBuffer buffer = ByteBuffer.allocate( 0x25 );
        buffer.put( new byte[]
               {
                 0x30, 0x23,
                  0x04, 0x19,
                   '1','.', '3', '.', '6', '.', '1', '.', '4',
                   '.', '1', '.', '4', '2', '.', '2', '.', '2',
                   '7', '.', '8', '.', '5', '.', '1',
                   0x04, 0x06,
                   (byte)0x81, 1, 0x01, //  
                   0x0A, 0x01, 0x01
               } );
        buffer.flip();
        
        ByteBuffer encoded = control.encode( ByteBuffer.allocate( control.computeLength() ) );
        assertEquals( StringTools.dumpBytes( buffer.array() ), StringTools.dumpBytes( encoded.array() ) );
    }

    
    @Test
    public void testDecodeRespWithExpiryWarningOnly() throws Exception
    {
        Asn1Decoder decoder = new PasswordPolicyResponseControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 5 );

        bb.put( new byte[]
            { 
             0x30, 0x03,
              (byte)0x81, 1, 1 //  
            } );

        bb.flip();

        PasswordPolicyResponseControlContainer container = new PasswordPolicyResponseControlContainer();
        container.setPasswordPolicyResponseControl( new PasswordPolicyResponseControl() );

        decoder.decode( bb, container );

        PasswordPolicyResponseControl control = container.getPasswordPolicyResponseControl();
        assertEquals( 1, control.getGraceAuthNsRemaining() );
        
        ByteBuffer buffer = ByteBuffer.allocate( 0x22 );
        buffer.put( new byte[]
               {
                 0x30, 0x20,
                  0x04, 0x19,
                   '1','.', '3', '.', '6', '.', '1', '.', '4',
                   '.', '1', '.', '4', '2', '.', '2', '.', '2',
                   '7', '.', '8', '.', '5', '.', '1',
                   0x04, 0x03,
                   (byte)0x81, 1, 0x01  
               } );
        buffer.flip();
        
        ByteBuffer encoded = control.encode( ByteBuffer.allocate( control.computeLength() ) );
        assertEquals( StringTools.dumpBytes( buffer.array() ), StringTools.dumpBytes( encoded.array() ) );
    }

    
    @Test
    public void testDecodeRespWithErrorOnly() throws Exception
    {
        Asn1Decoder decoder = new PasswordPolicyResponseControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 5 );

        bb.put( new byte[]
            { 
             0x30, 0x03,
              0x0A, 1, 1 //  
            } );

        bb.flip();

        PasswordPolicyResponseControlContainer container = new PasswordPolicyResponseControlContainer();
        container.setPasswordPolicyResponseControl( new PasswordPolicyResponseControl() );

        decoder.decode( bb, container );

        PasswordPolicyResponseControl control = container.getPasswordPolicyResponseControl();
        assertEquals( 1, control.getPasswordPolicyError().getValue() );
        
        ByteBuffer buffer = ByteBuffer.allocate( 0x22 );
        buffer.put( new byte[]
               {
                 0x30, 0x20,
                  0x04, 0x19,
                   '1','.', '3', '.', '6', '.', '1', '.', '4',
                   '.', '1', '.', '4', '2', '.', '2', '.', '2',
                   '7', '.', '8', '.', '5', '.', '1',
                   0x04, 0x03,
                   0x0A, 1, 0x01  
               } );
        buffer.flip();
        
        ByteBuffer encoded = control.encode( ByteBuffer.allocate( control.computeLength() ) );
        assertEquals( StringTools.dumpBytes( buffer.array() ), StringTools.dumpBytes( encoded.array() ) );
    }

    
    @Test
    public void testDecodeRespWithoutWarningAndError() throws Exception
    {
        Asn1Decoder decoder = new PasswordPolicyResponseControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 2 );

        bb.put( new byte[]
            { 
             0x30, 0x00
            } );

        bb.flip();

        PasswordPolicyResponseControlContainer container = new PasswordPolicyResponseControlContainer();
        container.setPasswordPolicyResponseControl( new PasswordPolicyResponseControl() );

        decoder.decode( bb, container );

        PasswordPolicyResponseControl control = container.getPasswordPolicyResponseControl();
        assertNotNull( control );
        
        ByteBuffer buffer = ByteBuffer.allocate( 0x1D );
        buffer.put( new byte[]
               {
                 0x30, 0x1B,
                  0x04, 0x19,
                   '1','.', '3', '.', '6', '.', '1', '.', '4',
                   '.', '1', '.', '4', '2', '.', '2', '.', '2',
                   '7', '.', '8', '.', '5', '.', '1'
               } );
        buffer.flip();
        
        ByteBuffer encoded = control.encode( ByteBuffer.allocate( control.computeLength() ) );
        assertEquals( StringTools.dumpBytes( buffer.array() ), StringTools.dumpBytes( encoded.array() ) );
    }
}

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

import org.apache.directory.shared.util.Strings;
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
        ByteBuffer bb = ByteBuffer.allocate( 0xA );

        bb.put( new byte[]
            { 
             0x30, 0x08,
              (byte)0xA0, 0x03,         // timeBeforeExpiration
                     (byte)0x80, 0x01, 0x01, 
              (byte)0x81, 0x01, 0x01   // ppolicyError
            } );

        bb.flip();

        PasswordPolicyResponseContainer container = new PasswordPolicyResponseContainer();
        container.setPasswordPolicyResponseControl( new PasswordPolicyResponseDecorator() );

        PasswordPolicyResponseDecorator control = container.getPasswordPolicyResponseControl();
        control.decode( bb.array() );
        assertEquals( 1, control.getTimeBeforeExpiration() );
        assertEquals( 1, control.getPasswordPolicyError().getValue() );
        
        ByteBuffer buffer = ByteBuffer.allocate( 0x29 );
        buffer.put( new byte[]
               {
                 0x30, 0x27,
                  0x04, 0x19,
                   '1','.', '3', '.', '6', '.', '1', '.', '4',
                   '.', '1', '.', '4', '2', '.', '2', '.', '2',
                   '7', '.', '8', '.', '5', '.', '1',
                   0x04, 0x0A,
                    0x30, 0x08,
                     (byte)0xA0, 0x03,         // timeBeforeExpiration
                            (byte)0x80, 0x01, 0x01, 
                     (byte)0x81, 0x01, 0x01   // ppolicyError
               } );
        buffer.flip();
        
        ByteBuffer encoded = control.encode( ByteBuffer.allocate( control.computeLength() ) );
        assertEquals( Strings.dumpBytes(buffer.array()), Strings.dumpBytes(encoded.array()) );
    }
    

    @Test
    public void testDecodeRespWithGraceAuthWarningAndError() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 0xA );

        bb.put( new byte[]
            { 
             0x30, 0x08,
              (byte)0xA0, 0x03,           // warning
                (byte)0x81, 0x01, 0x01,   // graceAuthNsRemaining
              (byte)0x81, 0x01, 0x01      // error
            } );

        bb.flip();

        PasswordPolicyResponseContainer container = new PasswordPolicyResponseContainer();
        container.setPasswordPolicyResponseControl( new PasswordPolicyResponseDecorator() );

        PasswordPolicyResponseDecorator control = container.getPasswordPolicyResponseControl();
        control.decode( bb.array() );
        assertEquals( 1, control.getGraceAuthNsRemaining() );
        assertEquals( 1, control.getPasswordPolicyError().getValue() );
        
        ByteBuffer buffer = ByteBuffer.allocate( 0x29 );
        buffer.put( new byte[]
               {
                 0x30, 0x27,
                  0x04, 0x19,
                   '1','.', '3', '.', '6', '.', '1', '.', '4',
                   '.', '1', '.', '4', '2', '.', '2', '.', '2',
                   '7', '.', '8', '.', '5', '.', '1',
                   0x04, 0x0A,
                     0x30, 0x08,
                       (byte)0xA0, 0x03,           // warning
                         (byte)0x81, 0x01, 0x01,   // graceAuthNsRemaining
                       (byte)0x81, 0x01, 0x01      // error
               } );
        buffer.flip();
        
        ByteBuffer encoded = control.encode( ByteBuffer.allocate( control.computeLength() ) );
        assertEquals( Strings.dumpBytes(buffer.array()), Strings.dumpBytes(encoded.array()) );
    }

    
    @Test
    public void testDecodeRespWithTimeBeforeExpiryWarningOnly() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 7 );

        bb.put( new byte[]
            { 
             0x30, 0x05,
              (byte)0xA0, 0x03,
                     (byte)0x80, 0x01, 0x01 //  timeBeforeExpiration
            } );

        bb.flip();

        PasswordPolicyResponseContainer container = new PasswordPolicyResponseContainer();
        container.setPasswordPolicyResponseControl( new PasswordPolicyResponseDecorator() );

        PasswordPolicyResponseDecorator control = container.getPasswordPolicyResponseControl();
        control.decode( bb.array() );
        assertEquals( 1, control.getTimeBeforeExpiration() );
        
        ByteBuffer buffer = ByteBuffer.allocate( 0x26 );
        buffer.put( new byte[]
               {
                 0x30, 0x24,
                  0x04, 0x19,
                   '1','.', '3', '.', '6', '.', '1', '.', '4',
                   '.', '1', '.', '4', '2', '.', '2', '.', '2',
                   '7', '.', '8', '.', '5', '.', '1',
                   0x04, 0x07,
                    0x30, 0x05,
                     (byte)0xA0, 0x03,
                            (byte)0x80, 0x01, 0x01  // timeBeforeExpiration
               } );
        buffer.flip();
        
        ByteBuffer encoded = control.encode( ByteBuffer.allocate( control.computeLength() ) );
        assertEquals( Strings.dumpBytes(buffer.array()), Strings.dumpBytes(encoded.array()) );
    }
    

    @Test
    public void testDecodeRespWithGraceAuthWarningOnly() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 7 );

        bb.put( new byte[]
            { 
             0x30, 0x05,
              (byte)0xA0, 0x03,
                     (byte)0x81, 0x01, 0x01 //  graceAuthNsRemaining
            } );

        bb.flip();

        PasswordPolicyResponseContainer container = new PasswordPolicyResponseContainer();
        container.setPasswordPolicyResponseControl( new PasswordPolicyResponseDecorator() );

        PasswordPolicyResponseDecorator control = container.getPasswordPolicyResponseControl();
        
        assertEquals( 1, control.getGraceAuthNsRemaining() );
        
        ByteBuffer buffer = ByteBuffer.allocate( 0x26 );
        buffer.put( new byte[]
               {
                 0x30, 0x24,
                  0x04, 0x19,
                   '1','.', '3', '.', '6', '.', '1', '.', '4',
                   '.', '1', '.', '4', '2', '.', '2', '.', '2',
                   '7', '.', '8', '.', '5', '.', '1',
                   0x04, 0x07,
                    0x30, 0x05,
                     (byte)0xA0, 0x03,
                          (byte)0x81, 0x01, 0x01  // graceAuthNsRemaining
               } );
        buffer.flip();
        
        ByteBuffer encoded = control.encode( ByteBuffer.allocate( control.computeLength() ) );
        assertEquals( Strings.dumpBytes(buffer.array()), Strings.dumpBytes(encoded.array()) );
    }
    
    
    @Test
    public void testDecodeRespWithErrorOnly() throws Exception
    {
        ByteBuffer bb = ByteBuffer.allocate( 5 );

        bb.put( new byte[]
            { 
             0x30, 0x03,
              (byte)0x81, 0x01, 0x01 //  error
            } );

        bb.flip();

        PasswordPolicyResponseContainer container = new PasswordPolicyResponseContainer();
        container.setPasswordPolicyResponseControl( new PasswordPolicyResponseDecorator() );

        PasswordPolicyResponseDecorator control = container.getPasswordPolicyResponseControl();
        control.decode( bb.array() );
        assertEquals( 1, control.getPasswordPolicyError().getValue() );
        
        ByteBuffer buffer = ByteBuffer.allocate( 0x24 );
        buffer.put( new byte[]
               {
                 0x30, 0x22,
                  0x04, 0x19,
                   '1','.', '3', '.', '6', '.', '1', '.', '4',
                   '.', '1', '.', '4', '2', '.', '2', '.', '2',
                   '7', '.', '8', '.', '5', '.', '1',
                   0x04, 0x05,
                     0x30, 0x03,
                      (byte)0x81, 0x01, 0x01  // error
               } );
        buffer.flip();
        
        ByteBuffer encoded = control.encode( ByteBuffer.allocate( control.computeLength() ) );
        assertEquals( Strings.dumpBytes(buffer.array()), Strings.dumpBytes(encoded.array()) );
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

        PasswordPolicyResponseContainer container = new PasswordPolicyResponseContainer();
        container.setPasswordPolicyResponseControl( new PasswordPolicyResponseDecorator() );

        PasswordPolicyResponseDecorator control = container.getPasswordPolicyResponseControl();
        control.decode( bb.array() );
        assertNotNull( control );
        
        ByteBuffer buffer = ByteBuffer.allocate( 0x1D );
        buffer.put( new byte[]
               {
                 0x30, 0x1B,
                   0x04, 0x19,
                     '1','.', '3', '.', '6', '.', '1', '.', '4',
                     '.', '1', '.', '4', '2', '.', '2', '.', '2',
                     '7', '.', '8', '.', '5', '.', '1',
               } );
        buffer.flip();
        
        ByteBuffer encoded = control.encode( ByteBuffer.allocate( control.computeLength() ) );
        assertEquals( Strings.dumpBytes(buffer.array()), Strings.dumpBytes(encoded.array()) );
    }
}

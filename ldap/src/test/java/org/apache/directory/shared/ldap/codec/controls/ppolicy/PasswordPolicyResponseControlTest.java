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


import java.nio.ByteBuffer;

import org.apache.directory.shared.asn1.ber.Asn1Decoder;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * PasswordPolicyResponseControlTest.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PasswordPolicyResponseControlTest
{
    @Test
    @Ignore( "failing due to a unknown issue related to reading the data from PasswordPolicyResponseControlTags.GRACE_AUTHNS_REMAINING_TAG" )
    public void testDecodeResp() throws Exception
    {
        Asn1Decoder decoder = new PasswordPolicyResponseControlDecoder();
        ByteBuffer bb = ByteBuffer.allocate( 5 );

        bb.put( new byte[]
            { 
             0x30, 0x03,
             // for the below 0x0080 tag the data is coming correctly
              //(byte)0x0080, 1, 1, //  
             // FIXME if I give the below tag 0x00A1 the data is coming as empty array
             // the below tag is PasswordPolicyResponseControlTags.GRACE_AUTHNS_REMAINING_TAG
               ( byte ) 0x00A1, 0x01, 0 // 
            } );

        bb.flip();

        PasswordPolicyResponseControlContainer container = new PasswordPolicyResponseControlContainer();
        container.setPasswordPolicyResponseControl( new PasswordPolicyResponseControl() );

        decoder.decode( bb, container );

        PasswordPolicyResponseControl control = container.getPasswordPolicyResponseControl();
        //assertEquals( 1, control.getTimeBeforeExpiration() );
        assertEquals( 1, control.getGraceAuthNsRemaining() );
    }
}

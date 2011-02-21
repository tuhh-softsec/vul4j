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

package org.apache.directory.shared.ldap.extras.extended.ads_impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;

import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.asn1.ber.Asn1Decoder;
import org.apache.directory.shared.asn1.ber.Asn1Container;
import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.ldap.extras.extended.ads_impl.StoredProcedure;
import org.apache.directory.shared.ldap.extras.extended.ads_impl.StoredProcedureContainer;
import org.apache.directory.shared.ldap.extras.extended.ads_impl.StoredProcedureDecoder;
import org.apache.directory.shared.util.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mycila.junit.concurrent.Concurrency;
import com.mycila.junit.concurrent.ConcurrentJunitRunner;


/*
 * TestCase for a Stored Procedure Extended Operation ASN.1 codec
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrency()
public class StoredProcedureTest
{
    @Test
    public void testDecodeStoredProcedureNParams()
    {
        Asn1Decoder storedProcedureDecoder = new StoredProcedureDecoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x44 );

        stream.put( new byte[]
            {
                0x30, 0x42,
                    0x04, 0x04, 'J', 'a', 'v', 'a',
                    0x04, 0x07, 'e', 'x', 'e', 'c', 'u', 't', 'e',
                    0x30, 0x31,
                      0x30, 0x08,
                        0x04, 0x03, 'i', 'n', 't', 
                        0x04, 0x01, 0x01,
                      0x30, 0x0F,
                        0x04, 0x07, 'b', 'o', 'o', 'l', 'e', 'a', 'n', 
                        0x04, 0x04, 't', 'r', 'u', 'e',
                      0x30, 0x14,
                        0x04, 0x06, 'S', 't', 'r', 'i', 'n', 'g', 
                        0x04, 0x0A, 'p', 'a', 'r', 'a', 'm', 'e', 't', 'e', 'r', '3' 
            } );

        String decodedPdu = Strings.dumpBytes(stream.array());
        stream.flip();

        // Allocate a StoredProcedure Container
        Asn1Container storedProcedureContainer = new StoredProcedureContainer();

        // Decode a StoredProcedure message
        try
        {
            storedProcedureDecoder.decode( stream, storedProcedureContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        StoredProcedure storedProcedure = ( ( StoredProcedureContainer ) storedProcedureContainer ).getStoredProcedure();

        assertEquals("Java", storedProcedure.getLanguage());
        
        assertEquals( "execute", storedProcedure.getProcedureSpecification() );

        assertEquals( 3, storedProcedure.size() );

        assertEquals( "int", storedProcedure.getParameterType( 0 ) );
        assertEquals( 1, storedProcedure.getParameterValue( 0 ) );

        assertEquals( "boolean", storedProcedure.getParameterType( 1 ) );
        assertEquals( "true", storedProcedure.getParameterValue( 1 ) );

        assertEquals( "String", storedProcedure.getParameterType( 2 ) );
        assertEquals( "parameter3", storedProcedure.getParameterValue( 2 ) );

        // Check the encoding
        try
        {
            ByteBuffer bb = storedProcedure.encode();
            String encodedPdu = Strings.dumpBytes( bb.array() );
            assertEquals( encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            fail( ee.getMessage() );
        }
    }

    @Test
    public void testDecodeStoredProcedureNoParam()
    {
        Asn1Decoder storedProcedureDecoder = new StoredProcedureDecoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x13 );

        stream.put( new byte[]
            {
                0x30, 0x11,
                    0x04, 0x04, 'J', 'a', 'v', 'a',
                    0x04, 0x07, 'e', 'x', 'e', 'c', 'u', 't', 'e',
                    0x30, 0x00
            } );

        String decodedPdu = Strings.dumpBytes(stream.array());
        stream.flip();

        // Allocate a StoredProcedure Container
        Asn1Container storedProcedureContainer = new StoredProcedureContainer();

        // Decode a StoredProcedure message
        try
        {
            storedProcedureDecoder.decode( stream, storedProcedureContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        StoredProcedure storedProcedure = ( ( StoredProcedureContainer ) storedProcedureContainer ).getStoredProcedure();

        assertEquals("Java", storedProcedure.getLanguage());
        
        assertEquals( "execute", storedProcedure.getProcedureSpecification() );

        assertEquals( 0, storedProcedure.size() );
        
        // Check the encoding
        try
        {
            ByteBuffer bb = storedProcedure.encode();

            String encodedPdu = Strings.dumpBytes(bb.array());

            assertEquals( encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            fail( ee.getMessage() );
        }
    }

    
    @Test
    public void testDecodeStoredProcedureOneParam()
    {
        Asn1Decoder storedProcedureDecoder = new StoredProcedureDecoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x1D );

        stream.put( new byte[]
            {
                0x30, 0x1B,
                  0x04, 0x04, 'J', 'a', 'v', 'a',
                  0x04, 0x07, 'e', 'x', 'e', 'c', 'u', 't', 'e',
                  0x30, 0x0A,
                      0x30, 0x08,
                        0x04, 0x03, 'i', 'n', 't', 
                        0x04, 0x01, 0x01,
            } );

        String decodedPdu = Strings.dumpBytes(stream.array());
        stream.flip();

        // Allocate a StoredProcedure Container
        Asn1Container storedProcedureContainer = new StoredProcedureContainer();

        // Decode a StoredProcedure message
        try
        {
            storedProcedureDecoder.decode( stream, storedProcedureContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        StoredProcedure storedProcedure = ( ( StoredProcedureContainer ) storedProcedureContainer ).getStoredProcedure();

        assertEquals("Java", storedProcedure.getLanguage());
        
        assertEquals( "execute", storedProcedure.getProcedureSpecification() );

        assertEquals( 1, storedProcedure.size() );

        assertEquals( "int", storedProcedure.getParameterType( 0 ) );
        assertEquals( 1, storedProcedure.getParameterValue( 0 ) );

        // Check the encoding
        try
        {
            ByteBuffer bb = storedProcedure.encode();

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

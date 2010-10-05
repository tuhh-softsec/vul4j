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
package org.apache.directory.shared.ldap.codec;


import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.apache.directory.shared.asn1.codec.EncoderException;
import org.apache.directory.shared.asn1.codec.stateful.EncoderCallback;
import org.apache.directory.shared.ldap.message.spi.Provider;
import org.apache.directory.shared.ldap.message.spi.ProviderEncoder;
import org.apache.directory.shared.ldap.message.spi.ProviderException;


/**
 * LDAP BER provider's encoder.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdapEncoder implements ProviderEncoder
{
    /** The associated Provider */
    final Provider provider;


    /**
     * Creates an instance of a Ldap Encoder implementation.
     * 
     * @param provider The associated Provider
     */
    public LdapEncoder( Provider provider )
    {
        this.provider = provider;
    }


    /**
     * Encodes a LdapMessage, and calls the callback.
     * 
     * @param lock Not used...
     * @param out Not used ...
     * @param obj The LdapMessage to encode
     * @throws ProviderException If anything went wrong
     */
    public void encodeBlocking( Object lock, OutputStream out, Object obj ) throws ProviderException
    {
    }


    /**
     * Encodes a LdapMessage, and return a ByteBuffer containing the resulting
     * PDU
     * 
     * @param obj The LdapMessage to encode
     * @return The ByteBuffer containing the PDU
     * @throws ProviderException If anything went wrong
     */
    public ByteBuffer encodeBlocking( Object obj ) throws ProviderException
    {
        return null;
    }


    /**
     * Gets the Provider associated with this SPI implementation object.
     * 
     * @return Provider The provider
     */
    public Provider getProvider()
    {
        return provider;
    }


    /**
     * Encodes a LdapMessage, and calls the callback
     * 
     * @param obj The LdapMessage to encode
     * @throws EncoderException If anything went wrong
     */
    public void encode( Object request ) throws EncoderException
    {
    }


    /**
     * Set the callback called when the encoding is done.
     * 
     * @param cb The callback.
     */
    public void setCallback( EncoderCallback cb )
    {
    }
}

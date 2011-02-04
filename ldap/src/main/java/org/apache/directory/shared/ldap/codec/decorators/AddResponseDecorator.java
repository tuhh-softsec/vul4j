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
package org.apache.directory.shared.ldap.codec.decorators;


import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.LdapCodecService;
import org.apache.directory.shared.ldap.codec.LdapConstants;
import org.apache.directory.shared.ldap.model.message.AddResponse;


/**
 * A decorator for the AddResponse message
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AddResponseDecorator extends ResponseDecorator<AddResponse> implements AddResponse
{
    /** The encoded addResponse length */
    private int addResponseLength;


    /**
     * Makes a AddResponse a MessageDecorator.
     *
     * @param decoratedMessage the decorated AddResponse
     */
    public AddResponseDecorator( LdapCodecService codec, AddResponse decoratedMessage )
    {
        super( codec, decoratedMessage );
    }


    /**
     * @return The decorated AddResponse
     */
    public AddResponse getAddResponse()
    {
        return ( AddResponse ) getDecorated();
    }


    /**
     * Stores the encoded length for the AddResponse
     * @param addResponseLength The encoded length
     */
    public void setAddResponseLength( int addResponseLength )
    {
        this.addResponseLength = addResponseLength;
    }


    /**
     * @return The encoded AddResponse's length
     */
    public int getAddResponseLength()
    {
        return addResponseLength;
    }

    
    //-------------------------------------------------------------------------
    // The Decorator methods
    //-------------------------------------------------------------------------
    /**
     * Compute the AddResponse length 
     * 
     * AddResponse : 
     * 
     * 0x69 L1
     *  |
     *  +--> LdapResult
     * 
     * L1 = Length(LdapResult)
     * 
     * Length(AddResponse) = Length(0x69) + Length(L1) + L1
     */
    public int computeLength()
    {
        AddResponse addResponse = getAddResponse();
        setLdapResult( new LdapResultDecorator( addResponse.getLdapResult() ) );
        int addResponseLength = ((LdapResultDecorator)getLdapResult()).computeLength();

        setAddResponseLength( addResponseLength );

        return 1 + TLV.getNbBytes( addResponseLength ) + addResponseLength;
    }


    /**
     * Encode the AddResponse message to a PDU.
     * 
     * @param buffer The buffer where to put the PDU
     * @param addResponseDecorator the AddResponse decorator
     */
    public ByteBuffer encode( ByteBuffer buffer ) throws EncoderException
    {
        try
        {
            // The AddResponse Tag
            buffer.put( LdapConstants.ADD_RESPONSE_TAG );
            buffer.put( TLV.getBytes( getAddResponseLength() ) );

            // The LdapResult
            ((LdapResultDecorator)getLdapResult()).encode( buffer );
            
            return buffer;
        }
        catch ( BufferOverflowException boe )
        {
            throw new EncoderException( I18n.err( I18n.ERR_04005 ) );
        }
    }
}

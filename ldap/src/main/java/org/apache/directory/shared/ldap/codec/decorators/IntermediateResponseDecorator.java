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
import org.apache.directory.shared.ldap.codec.ILdapCodecService;
import org.apache.directory.shared.ldap.codec.LdapConstants;
import org.apache.directory.shared.ldap.model.message.IntermediateResponse;
import org.apache.directory.shared.util.Strings;


/**
 * A decorator for the IntermediateResponse message
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class IntermediateResponseDecorator extends MessageDecorator<IntermediateResponse> 
    implements IntermediateResponse
{
    /** The response name as a byte[] */
    private byte[] responseNameBytes;

    /** The encoded intermediateResponse length */
    private int intermediateResponseLength;


    /**
     * Makes a IntermediateResponse encodable.
     *
     * @param decoratedMessage the decorated IntermediateResponse
     */
    public IntermediateResponseDecorator( ILdapCodecService codec, IntermediateResponse decoratedMessage )
    {
        super( codec, decoratedMessage );
    }


    /**
     * Stores the encoded length for the IntermediateResponse
     *
     * @param intermediateResponseLength The encoded length
     */
    public void setIntermediateResponseLength( int intermediateResponseLength )
    {
        this.intermediateResponseLength = intermediateResponseLength;
    }


    /**
     * @return The encoded IntermediateResponse's length
     */
    public int getIntermediateResponseLength()
    {
        return intermediateResponseLength;
    }


    /**
     * Gets the ResponseName bytes
     *
     * @return the ResponseName bytes of the Intermediate response type.
     */
    public byte[] getResponseNameBytes()
    {
        return responseNameBytes;
    }


    /**
     * Sets the ResponseName bytes
     *
     * @param responseNameBytes the ResponseName bytes of the Intermediate response type.
     */
    public void setResponseNameBytes( byte[] responseNameBytes )
    {
        this.responseNameBytes = responseNameBytes;
    }

    
    //-------------------------------------------------------------------------
    // The IntermediateResponse methods
    //-------------------------------------------------------------------------
    
    
    /**
     * {@inheritDoc}
     */
    public String getResponseName()
    {
        return getDecorated().getResponseName();
    }


    /**
     * {@inheritDoc}
     */
    public void setResponseName( String oid )
    {
        getDecorated().setResponseName( oid );
    }


    /**
     * {@inheritDoc}
     */
    public byte[] getResponseValue()
    {
        return getDecorated().getResponseValue();
    }


    /**
     * {@inheritDoc}
     */
    public void setResponseValue( byte[] value )
    {
        getDecorated().setResponseValue( value );
    }
    
    
    //-------------------------------------------------------------------------
    // The Decorator methods
    //-------------------------------------------------------------------------
    /**
     * Compute the intermediateResponse length
     * 
     * intermediateResponse :
     * 
     * 0x79 L1
     *  |
     * [+--> 0x80 L2 name
     * [+--> 0x81 L3 response]]
     * 
     * L1 = [ + Length(0x80) + Length(L2) + L2
     *      [ + Length(0x81) + Length(L3) + L3]]
     * 
     * Length(IntermediateResponse) = Length(0x79) + Length(L1) + L1
     * 
     * @return The IntermediateResponse length
     */
    public int computeLength()
    {
        int intermediateResponseLength = 0;

        if ( !Strings.isEmpty( getResponseName() ) )
        {
            byte[] responseNameBytes = Strings.getBytesUtf8( getResponseName() );

            int responseNameLength = responseNameBytes.length;
            intermediateResponseLength += 1 + TLV.getNbBytes( responseNameLength ) + responseNameLength;
            setResponseNameBytes( responseNameBytes );
        }

        byte[] encodedValue = getResponseValue();

        if ( encodedValue != null )
        {
            intermediateResponseLength += 1 + TLV.getNbBytes( encodedValue.length ) + encodedValue.length;
        }

        setIntermediateResponseLength( intermediateResponseLength );

        return 1 + TLV.getNbBytes( intermediateResponseLength ) + intermediateResponseLength;
    }


    /**
     * Encode the IntermediateResponse message to a PDU. 
     * IntermediateResponse :
     *   0x79 LL
     *     [0x80 LL response name]
     *     [0x81 LL responseValue]
     * 
     * @param buffer The buffer where to put the PDU
     */
    public ByteBuffer encode( ByteBuffer buffer ) throws EncoderException
    {
        try
        {
            // The ExtendedResponse Tag
            buffer.put( LdapConstants.INTERMEDIATE_RESPONSE_TAG );
            buffer.put( TLV.getBytes( getIntermediateResponseLength() ) );

            // The responseName, if any
            byte[] responseNameBytes = getResponseNameBytes();

            if ( ( responseNameBytes != null ) && ( responseNameBytes.length != 0 ) )
            {
                buffer.put( ( byte ) LdapConstants.INTERMEDIATE_RESPONSE_NAME_TAG );
                buffer.put( TLV.getBytes( responseNameBytes.length ) );
                buffer.put( responseNameBytes );
            }

            // The encodedValue, if any
            byte[] encodedValue = getResponseValue();

            if ( encodedValue != null )
            {
                buffer.put( ( byte ) LdapConstants.INTERMEDIATE_RESPONSE_VALUE_TAG );

                buffer.put( TLV.getBytes( encodedValue.length ) );

                if ( encodedValue.length != 0 )
                {
                    buffer.put( encodedValue );
                }
            }
        }
        catch ( BufferOverflowException boe )
        {
            throw new EncoderException( I18n.err( I18n.ERR_04005 ) );
        }
        
        return buffer;
    }
}

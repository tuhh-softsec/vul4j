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
package org.apache.directory.shared.ldap.codec.api;


import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.decorators.SingleReplyRequestDecorator;
import org.apache.directory.shared.ldap.model.message.ExtendedRequest;
import org.apache.directory.shared.ldap.model.message.ExtendedResponse;
import org.apache.directory.shared.util.Strings;


/**
 * A decorator for the ExtendedRequest message
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExtendedRequestDecorator extends SingleReplyRequestDecorator<ExtendedRequest<ExtendedResponse>,ExtendedResponse> 
    implements ExtendedRequest<ExtendedResponse>
{
    /** The extended request length */
    private int extendedRequestLength;

    /** The OID length */
    private byte[] requestNameBytes;

    private byte[] requestValue;


    /**
     * Makes a ExtendedRequest a MessageDecorator.
     *
     * @param decoratedMessage the decorated ExtendedRequest
     */
    public ExtendedRequestDecorator( LdapCodecService codec, ExtendedRequest<ExtendedResponse> decoratedMessage )
    {
        super( codec, decoratedMessage );
    }


    /**
     * Stores the encoded length for the ExtendedRequest
     *
     * @param extendedRequestLength The encoded length
     */
    public void setExtendedRequestLength( int extendedRequestLength )
    {
        this.extendedRequestLength = extendedRequestLength;
    }


    /**
     * @return The encoded ExtendedRequest's length
     */
    public int getExtendedRequestLength()
    {
        return extendedRequestLength;
    }


    /**
     * Gets the requestName bytes.
     *
     * @return the requestName bytes of the extended request type.
     */
    public byte[] getRequestNameBytes()
    {
        return requestNameBytes;
    }


    /**
     * Sets the requestName bytes.
     *
     * @param requestNameBytes the OID bytes of the extended request type.
     */
    public void setRequestNameBytes( byte[] requestNameBytes )
    {
        this.requestNameBytes = requestNameBytes;
    }


    //-------------------------------------------------------------------------
    // The ExtendedRequest methods
    //-------------------------------------------------------------------------
    
    
    /**
     * {@inheritDoc}
     */
    public String getRequestName()
    {
        return getDecorated().getRequestName();
    }


    /**
     * {@inheritDoc}
     */
    public void setRequestName( String oid )
    {
        getDecorated().setRequestName( oid );
    }


    /**
     * {@inheritDoc}
     */
    public byte[] getRequestValue()
    {
        return requestValue;
    }


    /**
     * {@inheritDoc}
     */
    public void setRequestValue( byte[] requestValue )
    {
        this.requestValue = requestValue;
    }

    
    //-------------------------------------------------------------------------
    // The Decorator methods
    //-------------------------------------------------------------------------
    
    
    /**
     * Compute the ExtendedRequest length
     * 
     * ExtendedRequest :
     * 
     * 0x77 L1
     *  |
     *  +--> 0x80 L2 name
     *  [+--> 0x81 L3 value]
     * 
     * L1 = Length(0x80) + Length(L2) + L2
     *      [+ Length(0x81) + Length(L3) + L3]
     * 
     * Length(ExtendedRequest) = Length(0x77) + Length(L1) + L1
     */
    public int computeLength()
    {
        byte[] requestNameBytes = Strings.getBytesUtf8( getRequestName() );

        setRequestNameBytes( requestNameBytes );

        int extendedRequestLength = 1 + TLV.getNbBytes( requestNameBytes.length ) + requestNameBytes.length;

        if ( getRequestValue() != null )
        {
            extendedRequestLength += 1 + TLV.getNbBytes( getRequestValue().length )
                + getRequestValue().length;
        }

        setExtendedRequestLength( extendedRequestLength );

        return 1 + TLV.getNbBytes( extendedRequestLength ) + extendedRequestLength;
    }


    /**
     * Encode the ExtendedRequest message to a PDU. 
     * 
     * ExtendedRequest :
     * 
     * 0x80 LL resquest name
     * [0x81 LL request value]
     * 
     * @param buffer The buffer where to put the PDU
     * @return The PDU.
     */
    public ByteBuffer encode( ByteBuffer buffer ) throws EncoderException
    {
        try
        {
            // The BindResponse Tag
            buffer.put( LdapConstants.EXTENDED_REQUEST_TAG );
            buffer.put( TLV.getBytes( getExtendedRequestLength() ) );

            // The requestName, if any
            if ( getRequestNameBytes() == null )
            {
                throw new EncoderException( I18n.err( I18n.ERR_04043 ) );
            }

            buffer.put( ( byte ) LdapConstants.EXTENDED_REQUEST_NAME_TAG );
            buffer.put( TLV.getBytes( getRequestNameBytes().length ) );

            if ( getRequestNameBytes().length != 0 )
            {
                buffer.put( getRequestNameBytes() );
            }

            // The requestValue, if any
            if ( getRequestValue() != null )
            {
                buffer.put( ( byte ) LdapConstants.EXTENDED_REQUEST_VALUE_TAG );

                buffer.put( TLV.getBytes( getRequestValue().length ) );

                if ( getRequestValue().length != 0 )
                {
                    buffer.put( getRequestValue() );
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

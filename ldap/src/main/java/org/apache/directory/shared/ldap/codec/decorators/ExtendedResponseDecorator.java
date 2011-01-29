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
import org.apache.directory.shared.ldap.model.message.ExtendedResponse;
import org.apache.directory.shared.util.Strings;


/**
 * A decorator for the ExtendedResponse message
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExtendedResponseDecorator extends ResponseDecorator<ExtendedResponse> 
    implements ExtendedResponse
{
    private static final long serialVersionUID = -9029282485890195506L;

    /** The response name as a byte[] */
    private byte[] responseNameBytes;

    /** The encoded extendedResponse length */
    private int extendedResponseLength;


    /**
     * Makes a ExtendedResponse encodable.
     *
     * @param decoratedMessage the decorated ExtendedResponse
     */
    public ExtendedResponseDecorator( ILdapCodecService codec, ExtendedResponse decoratedMessage )
    {
        super( codec, decoratedMessage );
    }


    /**
     * Gets the responseName bytes.
     *
     * @return the responseName bytes of the extended response type.
     */
    public byte[] getResponseNameBytes()
    {
        return responseNameBytes;
    }


    /**
     * Sets the OID bytes.
     *
     * @param responseNameBytes the OID bytes of the extended response type.
     */
    public void setResponseNameBytes( byte[] responseNameBytes )
    {
        this.responseNameBytes = responseNameBytes;
    }


    /**
     * Stores the encoded length for the ExtendedResponse
     *
     * @param extendedResponseLength The encoded length
     */
    public void setExtendedResponseLength( int extendedResponseLength )
    {
        this.extendedResponseLength = extendedResponseLength;
    }


    /**
     * @return The encoded ExtendedResponse's length
     */
    public int getExtendedResponseLength()
    {
        return extendedResponseLength;
    }

    
    //-------------------------------------------------------------------------
    // The ExtendedResponse methods
    //-------------------------------------------------------------------------
    
    
    /**
     * {@inheritDoc}
     */
    public byte[] getEncodedValue()
    {
        return getDecorated().getEncodedValue();
    }


    /**
     * {@inheritDoc}
     */
    public String getID()
    {
        return getDecorated().getID();
    }


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
        return getDecorated().getEncodedValue();
    }


    /**
     * {@inheritDoc}
     */
    public void setResponseValue( byte[] responseValue )
    {
        getDecorated().setResponseValue( responseValue );
    }

    
    //-------------------------------------------------------------------------
    // The Decorator methods
    //-------------------------------------------------------------------------
    /**
     * Compute the ExtendedResponse length
     * 
     * ExtendedResponse :
     * 
     * 0x78 L1
     *  |
     *  +--> LdapResult
     * [+--> 0x8A L2 name
     * [+--> 0x8B L3 response]]
     * 
     * L1 = Length(LdapResult)
     *      [ + Length(0x8A) + Length(L2) + L2
     *       [ + Length(0x8B) + Length(L3) + L3]]
     * 
     * Length(ExtendedResponse) = Length(0x78) + Length(L1) + L1
     * 
     * @return The ExtendedResponse length
     */
    public int computeLength()
    {
        int ldapResultLength = ( (LdapResultDecorator) getLdapResult() ).computeLength();

        int extendedResponseLength = ldapResultLength;

        String id = getResponseName();

        if ( !Strings.isEmpty(id) )
        {
            byte[] idBytes = Strings.getBytesUtf8(id);
            setResponseNameBytes( idBytes );
            int idLength = idBytes.length;
            extendedResponseLength += 1 + TLV.getNbBytes( idLength ) + idLength;
        }

        byte[] encodedValue = getResponseValue();

        if ( encodedValue != null )
        {
            extendedResponseLength += 1 + TLV.getNbBytes( encodedValue.length ) + encodedValue.length;
        }

        setExtendedResponseLength( extendedResponseLength );

        return 1 + TLV.getNbBytes( extendedResponseLength ) + extendedResponseLength;
    }


    /**
     * Encode the ExtendedResponse message to a PDU. 
     * ExtendedResponse :
     * LdapResult.encode()
     * [0x8A LL response name]
     * [0x8B LL response]
     * 
     * @param buffer The buffer where to put the PDU
     * @return The PDU.
     */
    public ByteBuffer encode( ByteBuffer buffer ) throws EncoderException
    {
        try
        {
            // The ExtendedResponse Tag
            buffer.put( LdapConstants.EXTENDED_RESPONSE_TAG );
            buffer.put( TLV.getBytes( getExtendedResponseLength() ) );

            // The LdapResult
            ((LdapResultDecorator)getLdapResult()).encode( buffer );

            // The ID, if any
            byte[] idBytes = getResponseNameBytes();

            if ( idBytes != null )
            {
                buffer.put( ( byte ) LdapConstants.EXTENDED_RESPONSE_RESPONSE_NAME_TAG );
                buffer.put( TLV.getBytes( idBytes.length ) );

                if ( idBytes.length != 0 )
                {
                    buffer.put( idBytes );
                }
            }

            // The encodedValue, if any
            byte[] encodedValue = getResponseValue();

            if ( encodedValue != null )
            {
                buffer.put( ( byte ) LdapConstants.EXTENDED_RESPONSE_RESPONSE_TAG );

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

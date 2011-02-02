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
import org.apache.directory.shared.ldap.model.message.ModifyResponse;


/**
 * A decorator for the ModifyResponse message
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ModifyResponseDecorator extends ResponseDecorator<ModifyResponse> 
    implements ModifyResponse
{
    /** The encoded modifyResponse length */
    private int modifyResponseLength;


    /**
     * Makes a ModifyResponse encodable.
     *
     * @param decoratedMessage the decorated ModifyResponse
     */
    public ModifyResponseDecorator( ILdapCodecService codec, ModifyResponse decoratedMessage )
    {
        super( codec, decoratedMessage );
    }


    /**
     * Stores the encoded length for the ModifyResponse
     * @param modifyResponseLength The encoded length
     */
    public void setModifyResponseLength( int modifyResponseLength )
    {
        this.modifyResponseLength = modifyResponseLength;
    }


    /**
     * @return The encoded ModifyResponse's length
     */
    public int getModifyResponseLength()
    {
        return modifyResponseLength;
    }

    
    //-------------------------------------------------------------------------
    // The Decorator methods
    //-------------------------------------------------------------------------
    
    
    /**
     * Compute the ModifyResponse length 
     * 
     * ModifyResponse : 
     * <pre>
     * 0x67 L1 
     *   | 
     *   +--> LdapResult 
     *   
     * L1 = Length(LdapResult) 
     * Length(ModifyResponse) = Length(0x67) + Length(L1) + L1
     * </pre>
     */
    public int computeLength()
    {
        int modifyResponseLength = ((LdapResultDecorator)getLdapResult()).computeLength();

        setModifyResponseLength( modifyResponseLength );

        return 1 + TLV.getNbBytes( modifyResponseLength ) + modifyResponseLength;
    }


    /**
     * Encode the ModifyResponse message to a PDU.
     * 
     * @param buffer The buffer where to put the PDU
     */
    public ByteBuffer encode( ByteBuffer buffer ) throws EncoderException
    {
        try
        {
            // The ModifyResponse Tag
            buffer.put( LdapConstants.MODIFY_RESPONSE_TAG );
            buffer.put( TLV.getBytes( getModifyResponseLength() ) );

            // The LdapResult
            ((LdapResultDecorator)getLdapResult()).encode( buffer );
        }
        catch ( BufferOverflowException boe )
        {
            throw new EncoderException( I18n.err( I18n.ERR_04005 ) );
        }
        
        return buffer;
    }
}

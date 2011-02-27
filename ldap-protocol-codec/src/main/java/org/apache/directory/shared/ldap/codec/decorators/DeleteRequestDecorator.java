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
import org.apache.directory.shared.ldap.codec.api.LdapCodecService;
import org.apache.directory.shared.ldap.codec.api.LdapConstants;
import org.apache.directory.shared.ldap.model.message.DeleteRequest;
import org.apache.directory.shared.ldap.model.message.DeleteResponse;
import org.apache.directory.shared.ldap.model.name.Dn;


/**
 * A decorator for the DeleteRequest message
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DeleteRequestDecorator extends SingleReplyRequestDecorator<DeleteRequest, DeleteResponse> 
    implements DeleteRequest
{
    /**
     * Makes a DeleteRequest a MessageDecorator.
     *
     * @param decoratedMessage the decorated DeleteRequest
     */
    public DeleteRequestDecorator( LdapCodecService codec, DeleteRequest decoratedMessage )
    {
        super( codec, decoratedMessage );
    }


    //-------------------------------------------------------------------------
    // The DeleteRequest methods
    //-------------------------------------------------------------------------

    
    /**
     * {@inheritDoc}
     */
    public Dn getName()
    {
        return getDecorated().getName();
    }


    /**
     * {@inheritDoc}
     */
    public void setName( Dn name )
    {
        getDecorated().setName( name );
    }

    
    //-------------------------------------------------------------------------
    // The Decorator methods
    //-------------------------------------------------------------------------
    /**
     * Compute the DelRequest length 
     * 
     * DelRequest : 
     * 0x4A L1 entry 
     * 
     * L1 = Length(entry) 
     * Length(DelRequest) = Length(0x4A) + Length(L1) + L1
     */
    public int computeLength()
    {
        // The entry
        return 1 + TLV.getNbBytes( Dn.getNbBytes( getName() ) ) + Dn.getNbBytes( getName() );
    }


    /**
     * Encode the DelRequest message to a PDU. 
     * 
     * DelRequest : 
     * 0x4A LL entry
     * 
     * @param buffer The buffer where to put the PDU
     */
    public ByteBuffer encode( ByteBuffer buffer ) throws EncoderException
    {
        try
        {
            // The DelRequest Tag
            buffer.put( LdapConstants.DEL_REQUEST_TAG );

            // The entry
            buffer.put( TLV.getBytes( Dn.getNbBytes( getName() ) ) );
            buffer.put( Dn.getBytes( getName() ) );
        }
        catch ( BufferOverflowException boe )
        {
            throw new EncoderException( I18n.err( I18n.ERR_04005 ) );
        }
        
        return buffer;
    }
}

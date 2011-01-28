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
import org.apache.directory.shared.asn1.ber.tlv.Value;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.LdapConstants;
import org.apache.directory.shared.ldap.model.message.ModifyDnRequest;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.ldap.model.name.Rdn;
import org.apache.directory.shared.util.Strings;


/**
 * A decorator for the ModifyDnRequest message
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ModifyDnRequestDecorator extends SingleReplyRequestDecorator implements ModifyDnRequest
{
    /** The modify Dn request length */
    private int modifyDnRequestLength;


    /**
     * Makes a ModifyDnRequest encodable.
     *
     * @param decoratedMessage the decorated ModifyDnRequest
     */
    public ModifyDnRequestDecorator( ModifyDnRequest decoratedMessage )
    {
        super( decoratedMessage );
    }


    /**
     * @return The decorated ModifyDnRequest
     */
    public ModifyDnRequest getModifyDnRequest()
    {
        return ( ModifyDnRequest ) getDecoratedMessage();
    }


    /**
     * @param modifyDnRequestLength The encoded ModifyDnRequest's length
     */
    public void setModifyDnRequestLength( int modifyDnRequestLength )
    {
        this.modifyDnRequestLength = modifyDnRequestLength;
    }


    /**
     * Stores the encoded length for the ModifyDnRequest
     * @return the encoded length
     */
    public int getModifyDnResponseLength()
    {
        return modifyDnRequestLength;
    }


    //-------------------------------------------------------------------------
    // The ModifyDnResponse methods
    //-------------------------------------------------------------------------
    
    
    /**
     * {@inheritDoc}
     */
    public Dn getName()
    {
        return getModifyDnRequest().getName();
    }


    /**
     * {@inheritDoc}
     */
    public void setName( Dn name )
    {
        getModifyDnRequest().setName( name );
    }


    /**
     * {@inheritDoc}
     */
    public Rdn getNewRdn()
    {
        return getModifyDnRequest().getNewRdn();
    }


    /**
     * {@inheritDoc}
     */
    public void setNewRdn( Rdn newRdn )
    {
        getModifyDnRequest().setNewRdn( newRdn );
    }


    /**
     * {@inheritDoc}
     */
    public boolean getDeleteOldRdn()
    {
        return getModifyDnRequest().getDeleteOldRdn();
    }


    /**
     * {@inheritDoc}
     */
    public void setDeleteOldRdn( boolean deleteOldRdn )
    {
        getModifyDnRequest().setDeleteOldRdn( deleteOldRdn );
    }


    /**
     * {@inheritDoc}
     */
    public Dn getNewSuperior()
    {
        return getModifyDnRequest().getNewSuperior();
    }


    /**
     * {@inheritDoc}
     */
    public void setNewSuperior( Dn newSuperior )
    {
        getModifyDnRequest().setNewSuperior( newSuperior );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isMove()
    {
        return getModifyDnRequest().isMove();
    }

    
    //-------------------------------------------------------------------------
    // The Decorator methods
    //-------------------------------------------------------------------------
    /**
     * Compute the ModifyDNRequest length
     * 
     * ModifyDNRequest :
     * <pre>
     * 0x6C L1
     *  |
     *  +--> 0x04 L2 entry
     *  +--> 0x04 L3 newRDN
     *  +--> 0x01 0x01 (true/false) deleteOldRDN (3 bytes)
     * [+--> 0x80 L4 newSuperior ] 
     * 
     * L2 = Length(0x04) + Length(Length(entry)) + Length(entry) 
     * L3 = Length(0x04) + Length(Length(newRDN)) + Length(newRDN) 
     * L4 = Length(0x80) + Length(Length(newSuperior)) + Length(newSuperior)
     * L1 = L2 + L3 + 3 [+ L4] 
     * 
     * Length(ModifyDNRequest) = Length(0x6C) + Length(L1) + L1
     * </pre>
     * 
     * @return The PDU's length of a ModifyDN Request
     */
    public int computeLength()
    {
        int newRdnlength = Strings.getBytesUtf8( getNewRdn().getName() ).length;

        int modifyDNRequestLength = 1 + TLV.getNbBytes( Dn.getNbBytes( getName() ) )
            + Dn.getNbBytes( getName() ) + 1 + TLV.getNbBytes( newRdnlength ) + newRdnlength + 1 + 1
            + 1; // deleteOldRDN

        if ( getNewSuperior() != null )
        {
            modifyDNRequestLength += 1 + TLV.getNbBytes( Dn.getNbBytes( getNewSuperior() ) )
                + Dn.getNbBytes( getNewSuperior() );
        }

        setModifyDnRequestLength( modifyDNRequestLength );

        return 1 + TLV.getNbBytes( modifyDNRequestLength ) + modifyDNRequestLength;
    }


    /**
     * Encode the ModifyDNRequest message to a PDU. 
     * 
     * ModifyDNRequest :
     * <pre>
     * 0x6C LL
     *   0x04 LL entry
     *   0x04 LL newRDN
     *   0x01 0x01 deleteOldRDN
     *   [0x80 LL newSuperior]
     * </pre>
     * @param buffer The buffer where to put the PDU
     * @return The PDU.
     */
    public ByteBuffer encode( ByteBuffer buffer ) throws EncoderException
    {
        try
        {
            // The ModifyDNRequest Tag
            buffer.put( LdapConstants.MODIFY_DN_REQUEST_TAG );
            buffer.put( TLV.getBytes( getModifyDnResponseLength() ) );

            // The entry

            Value.encode( buffer, Dn.getBytes( getName() ) );

            // The newRDN
            Value.encode( buffer, getNewRdn().getName() );

            // The flag deleteOldRdn
            Value.encode( buffer, getDeleteOldRdn() );

            // The new superior, if any
            if ( getNewSuperior() != null )
            {
                // Encode the reference
                buffer.put( ( byte ) LdapConstants.MODIFY_DN_REQUEST_NEW_SUPERIOR_TAG );

                int newSuperiorLength = Dn.getNbBytes( getNewSuperior() );

                buffer.put( TLV.getBytes( newSuperiorLength ) );

                if ( newSuperiorLength != 0 )
                {
                    buffer.put( Dn.getBytes( getNewSuperior() ) );
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

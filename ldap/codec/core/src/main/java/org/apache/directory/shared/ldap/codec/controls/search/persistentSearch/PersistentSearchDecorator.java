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
package org.apache.directory.shared.ldap.codec.controls.search.persistentSearch;


import java.nio.ByteBuffer;

import org.apache.directory.shared.asn1.Asn1Object;
import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.asn1.ber.Asn1Decoder;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.asn1.ber.tlv.Value;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.api.LdapCodecService;
import org.apache.directory.shared.ldap.codec.api.ControlDecorator;
import org.apache.directory.shared.ldap.model.message.controls.ChangeType;
import org.apache.directory.shared.ldap.model.message.controls.PersistentSearch;
import org.apache.directory.shared.ldap.model.message.controls.PersistentSearchImpl;


/**
 * A persistence search object
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PersistentSearchDecorator extends ControlDecorator<PersistentSearch> implements PersistentSearch
{
    /** A temporary storage for a psearch length */
    private int psearchSeqLength;

    /** An instance of this decoder */
    private static final Asn1Decoder decoder = new Asn1Decoder();

    
    /**
     * Default constructor creates a PersistentSearch Control automatically
     * wrapped in a decorator object inside this container.
     */
    public PersistentSearchDecorator( LdapCodecService codec )
    {
        this( codec, new PersistentSearchImpl() );
    }


    /**
     * Creates a PersistentSearch Control wrapping a supplied PersistentSearch
     * Control.
     *
     * @param control The PersistentSearch Control to wrap.
     */
    public PersistentSearchDecorator( LdapCodecService codec, PersistentSearch control )
    {
        super( codec, control );
    }


    /**
     * Compute the PagedSearchControl length, which is the sum
     * of the control length and the value length.
     * 
     * <pre>
     * PersistentSearchDecorator value length :
     * 
     * 0x30 L1 
     *   | 
     *   +--> 0x02 0x0(1-4) [0..2^31-1] (changeTypes) 
     *   +--> 0x01 0x01 [0x00 | 0xFF] (changeOnly) 
     *   +--> 0x01 0x01 [0x00 | 0xFF] (returnRCs)
     * </pre> 
     */
    public int computeLength()
    {
        int changeTypesLength = 1 + 1 + Value.getNbBytes( getChangeTypes() );
        int changesOnlyLength = 1 + 1 + 1;
        int returnRCsLength = 1 + 1 + 1;

        psearchSeqLength = changeTypesLength + changesOnlyLength + returnRCsLength;
        int valueLength = 1 + TLV.getNbBytes( psearchSeqLength ) + psearchSeqLength;

        return valueLength;
    }


    /**
     * Encodes the persistent search control.
     * 
     * @param buffer The encoded sink
     * @return A ByteBuffer that contains the encoded PDU
     * @throws EncoderException If anything goes wrong.
     */
    public ByteBuffer encode( ByteBuffer buffer ) throws EncoderException
    {
        if ( buffer == null )
        {
            throw new EncoderException( I18n.err( I18n.ERR_04023 ) );
        }

        // Now encode the PagedSearch specific part
        buffer.put( UniversalTag.SEQUENCE.getValue() );
        buffer.put( TLV.getBytes( psearchSeqLength ) );

        Value.encode( buffer, getChangeTypes() );
        Value.encode( buffer, isChangesOnly() );
        Value.encode( buffer, isReturnECs() );
        
        return buffer;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public byte[] getValue()
    {
        if ( value == null )
        {
            try
            { 
                computeLength();
                ByteBuffer buffer = ByteBuffer.allocate( valueLength );
                
                // Now encode the PagedSearch specific part
                buffer.put( UniversalTag.SEQUENCE.getValue() );
                buffer.put( TLV.getBytes( psearchSeqLength ) );

                Value.encode( buffer, getChangeTypes() );
                Value.encode( buffer, isChangesOnly() );
                Value.encode( buffer, isReturnECs() );

                value = buffer.array();
            }
            catch ( Exception e )
            {
                return null;
            }
        }
        
        return value;
    }



    private PersistentSearch getPersistentSearch()
    {
        return ( PersistentSearch ) getDecorated();
    }


    public void setChangesOnly( boolean changesOnly )
    {
        getPersistentSearch().setChangesOnly( changesOnly );
    }


    public boolean isChangesOnly()
    {
        return getPersistentSearch().isChangesOnly();
    }


    public void setReturnECs( boolean returnECs )
    {
        getPersistentSearch().setReturnECs( returnECs );
    }


    public boolean isReturnECs()
    {
        return getPersistentSearch().isReturnECs();
    }


    public void setChangeTypes( int changeTypes )
    {
        getPersistentSearch().setChangeTypes( changeTypes );
    }


    public int getChangeTypes()
    {
        return getPersistentSearch().getChangeTypes();
    }


    public boolean isNotificationEnabled( ChangeType changeType )
    {
        return getPersistentSearch().isNotificationEnabled( changeType );
    }


    public void enableNotification( ChangeType changeType )
    {
        getPersistentSearch().enableNotification( changeType );
    }


    /**
     * {@inheritDoc}
     */
    public Asn1Object decode( byte[] controlBytes ) throws DecoderException
    {
        ByteBuffer bb = ByteBuffer.wrap( controlBytes );
        PersistentSearchContainer container = new PersistentSearchContainer( getCodecService(), this );
        decoder.decode( bb, container );
        return this;
    }
}

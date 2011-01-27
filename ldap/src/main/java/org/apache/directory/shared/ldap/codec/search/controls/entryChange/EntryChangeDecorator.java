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
package org.apache.directory.shared.ldap.codec.search.controls.entryChange;


import java.nio.ByteBuffer;

import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.asn1.ber.tlv.Value;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.controls.ControlDecorator;
import org.apache.directory.shared.ldap.codec.search.controls.ChangeType;
import org.apache.directory.shared.ldap.model.message.controls.EntryChange;
import org.apache.directory.shared.ldap.model.message.controls.SimpleEntryChange;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.util.Strings;


/**
 * An EntryChange implementation, that wraps and decorates the Control with codec
 * specific functionality.
 *
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EntryChangeDecorator extends ControlDecorator implements EntryChange
{

    public static final int UNDEFINED_CHANGE_NUMBER = -1;

    /** A temporary storage for the previous Dn */
    private byte[] previousDnBytes = null;

    /** The entry change global length */
    private int eccSeqLength;


    /**
     * Creates a new instance of EntryChangeDecoder wrapping a newly created
     * EntryChange Control object.
     */
    public EntryChangeDecorator()
    {
        super( new SimpleEntryChange(), new EntryChangeDecoder() );
    }


    /**
     * Creates a new instance of EntryChangeDecorator wrapping the supplied
     * EntryChange Control.
     *
     * @param control The EntryChange Control to be decorated.
     */
    public EntryChangeDecorator( EntryChange control )
    {
        super( control, new EntryChangeDecoder() );
    }


    /**
     * Internally used to not have to cast the decorated Control.
     *
     * @return the decorated Control.
     */
    private EntryChange getEntryChange()
    {
        return ( EntryChange ) getDecorated();
    }


    /**
     * Compute the EntryChangeControl length 
     * 
     * 0x30 L1 
     *   | 
     *   +--> 0x0A 0x0(1-4) [1|2|4|8] (changeType) 
     *  [+--> 0x04 L2 previousDN] 
     *  [+--> 0x02 0x0(1-4) [0..2^63-1] (changeNumber)]
     */
    public int computeLength()
    {
        int changeTypesLength = 1 + 1 + 1;

        int previousDnLength = 0;
        int changeNumberLength = 0;

        if ( getPreviousDn() != null )
        {
            previousDnBytes = Strings.getBytesUtf8( getPreviousDn().getName() );
            previousDnLength = 1 + TLV.getNbBytes( previousDnBytes.length ) + previousDnBytes.length;
        }

        if ( getChangeNumber() != UNDEFINED_CHANGE_NUMBER )
        {
            changeNumberLength = 1 + 1 + Value.getNbBytes( getChangeNumber() );
        }

        eccSeqLength = changeTypesLength + previousDnLength + changeNumberLength;
        valueLength = 1 + TLV.getNbBytes( eccSeqLength ) + eccSeqLength;

        // Call the super class to compute the global control length
        return super.computeLength( valueLength );
    }


    /**
     * Encodes the entry change control.
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

        // Encode the Control envelop
        super.encode( buffer );

        // Encode the OCTET_STRING tag
        buffer.put( UniversalTag.OCTET_STRING.getValue() );
        buffer.put( TLV.getBytes( valueLength ) );

        buffer.put( UniversalTag.SEQUENCE.getValue() );
        buffer.put( TLV.getBytes( eccSeqLength ) );

        buffer.put( UniversalTag.ENUMERATED.getValue() );
        buffer.put( ( byte ) 1 );
        buffer.put( Value.getBytes( getChangeType().getValue() ) );

        if ( getPreviousDn() != null )
        {
            Value.encode( buffer, previousDnBytes );
        }

        if ( getChangeNumber() != UNDEFINED_CHANGE_NUMBER )
        {
            Value.encode( buffer, getChangeNumber() );
        }

        return buffer;
    }


    /**
     * {@inheritDoc}
     */
    public byte[] getValue()
    {
        if ( getDecorated().getValue() == null )
        {
            try
            {
                computeLength();
                ByteBuffer buffer = ByteBuffer.allocate( valueLength );

                buffer.put( UniversalTag.SEQUENCE.getValue() );
                buffer.put( TLV.getBytes( eccSeqLength ) );

                buffer.put( UniversalTag.ENUMERATED.getValue() );
                buffer.put( ( byte ) 1 );
                buffer.put( Value.getBytes( getChangeType().getValue() ) );

                if ( getPreviousDn() != null )
                {
                    Value.encode( buffer, previousDnBytes );
                }

                if ( getChangeNumber() != UNDEFINED_CHANGE_NUMBER )
                {
                    Value.encode( buffer, getChangeNumber() );
                }

                getDecorated().setValue( buffer.array() );
            }
            catch ( Exception e )
            {
                return null;
            }
        }

        return getDecorated().getValue();
    }


    /**
     * {@inheritDoc}
     */
    public ChangeType getChangeType ()
    {
        return getEntryChange().getChangeType();
    }


    /**
     * {@inheritDoc}
     */
    public void setChangeType ( ChangeType changeType )
    {
        getEntryChange().setChangeType( changeType );
    }


    /**
     * {@inheritDoc}
     */
    public Dn getPreviousDn ()
    {
        return getEntryChange().getPreviousDn();
    }


    /**
     * {@inheritDoc}
     */
    public void setPreviousDn ( Dn previousDn )
    {
        getEntryChange().setPreviousDn( previousDn );
    }


    /**
     * {@inheritDoc}
     */
    public long getChangeNumber ()
    {
        return getEntryChange().getChangeNumber();
    }


    /**
     * {@inheritDoc}
     */
    public void setChangeNumber ( long changeNumber )
    {
        getEntryChange().setChangeNumber( changeNumber );
    }
}

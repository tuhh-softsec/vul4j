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
package org.apache.directory.shared.ldap.extras.controls.syncrepl_impl;


import java.nio.ByteBuffer;

import org.apache.directory.shared.asn1.Asn1Object;
import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.asn1.ber.Asn1Decoder;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.asn1.ber.tlv.Value;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.LdapCodecService;
import org.apache.directory.shared.ldap.codec.controls.ControlDecorator;
import org.apache.directory.shared.ldap.extras.controls.SyncStateTypeEnum;
import org.apache.directory.shared.ldap.extras.controls.SyncStateValue;
import org.apache.directory.shared.ldap.extras.controls.SyncStateValueImpl;


/**
 * A syncStateValue object, as defined in RFC 4533
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SyncStateValueDecorator extends ControlDecorator<SyncStateValue> implements SyncStateValue
{
    /** Global length for the control */
    private int syncStateSeqLength;

    /** An instance of this decoder */
    private static final Asn1Decoder decoder = new Asn1Decoder();


    public SyncStateValueDecorator( LdapCodecService codec )
    {
        super( codec, new SyncStateValueImpl() );
    }


    public SyncStateValueDecorator( LdapCodecService codec, SyncStateValue value )
    {
        super( codec, value );
    }


    /**
     * {@inheritDoc}
     */
    public byte[] getCookie()
    {
        return getDecorated().getCookie();
    }


    /**
     * {@inheritDoc}
     */
    public void setCookie( byte[] cookie )
    {
        getDecorated().setCookie( cookie );
    }


    /**
     * {@inheritDoc}
     */
    public SyncStateTypeEnum getSyncStateType()
    {
        return getDecorated().getSyncStateType();
    }


    /**
     * {@inheritDoc}
     */
    public void setSyncStateType( SyncStateTypeEnum syncStateType )
    {
        getDecorated().setSyncStateType( syncStateType );
    }


    /**
     * {@inheritDoc}
     */
    public byte[] getEntryUUID()
    {
        return getDecorated().getEntryUUID();
    }


    /**
     * {@inheritDoc}
     */
    public void setEntryUUID( byte[] entryUUID )
    {
        getDecorated().setEntryUUID( entryUUID );
    }


    /**
     * Compute the SyncStateValue length.
     *
     * SyncStateValue :
     * 0x30 L1
     *  |
     *  +--> 0x0A 0x01 [0x00|0x01|0x02|0x03] (type)
     * [+--> 0x04 L2 abcd...                 (entryUUID)
     * [+--> 0x04 L3 abcd...                 (cookie)
     *
     */
    @Override
    public int computeLength()
    {
        // The sync state type length
        syncStateSeqLength = 1 + 1 + 1;

        syncStateSeqLength += 1 + TLV.getNbBytes( getEntryUUID().length ) + getEntryUUID().length;

        // The cookie length, if we have a cookie
        if ( getCookie() != null )
        {
            syncStateSeqLength += 1 + TLV.getNbBytes( getCookie().length ) + getCookie().length;
        }

        valueLength = 1 + TLV.getNbBytes( syncStateSeqLength ) + syncStateSeqLength;

        return valueLength;
    }


    /**
     * Encode the SyncStateValue control
     *
     * @param buffer The encoded sink
     * @return A ByteBuffer that contains the encoded PDU
     * @throws EncoderException If anything goes wrong.
     */
    @Override
    public ByteBuffer encode( ByteBuffer buffer ) throws EncoderException
    {
        if ( buffer == null )
        {
            throw new EncoderException( I18n.err( I18n.ERR_04023 ) );
        }

        // Encode the SEQ
        buffer.put( UniversalTag.SEQUENCE.getValue() );
        buffer.put( TLV.getBytes( syncStateSeqLength ) );

        // The mode
        buffer.put( UniversalTag.ENUMERATED.getValue() );
        buffer.put( ( byte ) 0x01 );
        buffer.put( Value.getBytes( getSyncStateType().getValue() ) );

        // the entryUUID
        Value.encode( buffer, getEntryUUID() );

        // The cookie
        if ( getCookie() != null )
        {
            Value.encode( buffer, getCookie() );
        }

        return buffer;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getValue()
    {
        if ( value == null )
        {
            try
            {
                computeLength();
                ByteBuffer buffer = ByteBuffer.allocate( valueLength );

                // Encode the SEQ
                buffer.put( UniversalTag.SEQUENCE.getValue() );
                buffer.put( TLV.getBytes( syncStateSeqLength ) );

                // The mode
                buffer.put( UniversalTag.ENUMERATED.getValue() );
                buffer.put( ( byte ) 0x01 );
                buffer.put( Value.getBytes( getSyncStateType().getValue() ) );

                // the entryUUID
                Value.encode( buffer, getEntryUUID() );

                // The cookie
                if ( getCookie() != null )
                {
                    Value.encode( buffer, getCookie() );
                }

                value = buffer.array();
            }
            catch ( Exception e )
            {
                return null;
            }
        }

        return value;
    }


    /**
     * {@inheritDoc}
     */
    public Asn1Object decode( byte[] controlBytes ) throws DecoderException
    {
        ByteBuffer bb = ByteBuffer.wrap( controlBytes );
        SyncStateValueContainer container = new SyncStateValueContainer( this );
        decoder.decode( bb, container );
        return this;
    }
}

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
import org.apache.directory.shared.ldap.codec.api.LdapCodecService;
import org.apache.directory.shared.ldap.codec.controls.ControlDecorator;
import org.apache.directory.shared.ldap.extras.controls.SyncRequestValue;
import org.apache.directory.shared.ldap.extras.controls.SyncRequestValueImpl;
import org.apache.directory.shared.ldap.extras.controls.SynchronizationModeEnum;
import org.apache.directory.shared.util.Strings;


/**
 * A syncRequestValue object, as defined in RFC 4533
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SyncRequestValueDecorator  extends ControlDecorator<SyncRequestValue> implements SyncRequestValue
{
    /** The global length for this control */
    private int syncRequestValueLength;

    /** An instance of this decoder */
    private static final Asn1Decoder decoder = new Asn1Decoder();


    public SyncRequestValueDecorator( LdapCodecService codec )
    {
        super( codec, new SyncRequestValueImpl() );
    }


    public SyncRequestValueDecorator( LdapCodecService codec, SyncRequestValue control )
    {
        super( codec, control );
    }


    /**
     * {@inheritDoc}
     */
    public SynchronizationModeEnum getMode()
    {
        return getDecorated().getMode();
    }


    /**
     * {@inheritDoc}
     */
    public void setMode( SynchronizationModeEnum mode )
    {
        getDecorated().setMode( mode );
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
        // Copy the bytes
        if ( !Strings.isEmpty( cookie ) )
        {
            byte[] copy = new byte[cookie.length];
            System.arraycopy( cookie, 0, copy, 0, cookie.length );
            getDecorated().setCookie( copy );
        }
        else
        {
            getDecorated().setCookie( null );
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isReloadHint()
    {
        return getDecorated().isReloadHint();
    }


    /**
     * {@inheritDoc}
     */
    public void setReloadHint( boolean reloadHint )
    {
        getDecorated().setReloadHint( reloadHint );
    }


    /**
     * Compute the SyncRequestValue length.
     *
     * SyncRequestValue :
     * 0x30 L1
     *  |
     *  +--> 0x0A 0x01 [0x00|0x01|0x02|0x03] (mode)
     * [+--> 0x04 L2 abcd...                 (cookie)
     *  +--> 0x01 0x01 [0x00|0xFF]           (reloadHint)
     *
     */
    @Override
    public int computeLength()
    {
        // The mode length
        syncRequestValueLength = 1 + 1 + 1;

        // The cookie length, if we have a cookie
        if ( getCookie() != null )
        {
            syncRequestValueLength += 1 + TLV.getNbBytes( getCookie().length ) + getCookie().length;
        }

        // The reloadHint length, default to false
        if ( isReloadHint() )
        {
            syncRequestValueLength += 1 + 1 + 1;
        }

        valueLength =  1 + TLV.getNbBytes( syncRequestValueLength ) + syncRequestValueLength;

        // Call the super class to compute the global control length
        return valueLength;
    }


    /**
     * Encode the SyncRequestValue control
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
        buffer.put( TLV.getBytes( syncRequestValueLength ) );

        // The mode
        buffer.put(  UniversalTag.ENUMERATED.getValue() );
        buffer.put( ( byte )0x01 );
        buffer.put( Value.getBytes( getMode().getValue() ) );

        // The cookie
        if ( getCookie() != null )
        {
            Value.encode( buffer, getCookie() );
        }

        // The reloadHint if not false
        if ( isReloadHint() )
        {
            Value.encode( buffer, isReloadHint() );
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
                buffer.put( TLV.getBytes( syncRequestValueLength ) );

                // The mode
                buffer.put(  UniversalTag.ENUMERATED.getValue() );
                buffer.put( ( byte ) 0x01 );
                buffer.put( Value.getBytes( getMode().getValue() ) );

                // The cookie
                if ( getCookie() != null )
                {
                    Value.encode( buffer, getCookie() );
                }

                // The reloadHint if not false
                if ( isReloadHint() )
                {
                    Value.encode( buffer, isReloadHint() );
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
        SyncRequestValueContainer container = new SyncRequestValueContainer( this );
        decoder.decode( bb, container );
        return this;
    }
}

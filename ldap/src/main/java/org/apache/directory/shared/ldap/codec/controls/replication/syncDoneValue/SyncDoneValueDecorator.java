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
package org.apache.directory.shared.ldap.codec.controls.replication.syncDoneValue;


import java.nio.ByteBuffer;

import org.apache.directory.shared.asn1.Asn1Object;
import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.asn1.ber.Asn1Decoder;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.asn1.ber.tlv.Value;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.ILdapCodecService;
import org.apache.directory.shared.ldap.codec.controls.ControlDecorator;
import org.apache.directory.shared.ldap.extras.controls.SyncDoneValue;
import org.apache.directory.shared.ldap.extras.controls.SyncDoneValueImpl;
import org.apache.directory.shared.util.Strings;


/**
 * A syncDoneValue object as described in rfc4533.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SyncDoneValueDecorator extends ControlDecorator<SyncDoneValue> implements SyncDoneValue
{
    /** The global length for this control */
    private int syncDoneValueLength;

    /** An instance of this decoder */
    private static final Asn1Decoder decoder = new Asn1Decoder();


    /**
     * Creates a new instance of SyncDoneValueControlCodec.
     */
    public SyncDoneValueDecorator( ILdapCodecService codec )
    {
        super( codec, new SyncDoneValueImpl() );
    }


    /**
     * Creates a new instance of SyncDoneValueDecorator.
     *
     * @param codec The LDAP codec
     * @param control The control to be decorated
     */
    public SyncDoneValueDecorator( ILdapCodecService codec, SyncDoneValue control )
    {
        super( codec, control );
    }


    /**
     * Compute the syncDoneValue length.
     * 0x30 L1
     * |
     * +--> 0x04 L2 xkcd!!!...     (cookie)
     * +--> 0x01 0x01 [0x00|0xFF]  (refreshDeletes)
     */
    @Override
    public int computeLength()
    {
        // cookie's length
        if ( getCookie() != null )
        {
            syncDoneValueLength = 1 + TLV.getNbBytes( getCookie().length ) + getCookie().length;
        }

        // the refreshDeletes flag length
        if ( isRefreshDeletes() )
        {
            syncDoneValueLength += 1 + 1 + 1;
        }

        valueLength = 1 + TLV.getNbBytes( syncDoneValueLength ) + syncDoneValueLength;

        // Call the super class to compute the global control length
        return valueLength;
    }


    /**
     * Encode the SyncDoneValue control
     *
     * @param buffer The encoded sink
     * @return A ByteBuffer that contains the encoded PDU
     * @throws EncoderException If anything goes wrong while encoding.
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
        buffer.put( TLV.getBytes( syncDoneValueLength ) );

        if ( getCookie() != null )
        {
            Value.encode( buffer, getCookie() );
        }

        if ( isRefreshDeletes() )
        {
            Value.encode( buffer, isRefreshDeletes() );
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
                buffer.put( TLV.getBytes( syncDoneValueLength ) );

                if ( getCookie() != null )
                {
                    Value.encode( buffer, getCookie() );
                }

                if ( isRefreshDeletes() )
                {
                    Value.encode( buffer, isRefreshDeletes() );
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
    public boolean isRefreshDeletes()
    {
        return getDecorated().isRefreshDeletes();
    }


    /**
     * {@inheritDoc}
     */
    public void setRefreshDeletes( boolean refreshDeletes )
    {
        getDecorated().setRefreshDeletes( refreshDeletes );
    }


    /**
     * {@inheritDoc}
     */
   public Asn1Object decode( byte[] controlBytes ) throws DecoderException
    {
        ByteBuffer bb = ByteBuffer.wrap( controlBytes );
        SyncDoneValueContainer container = new SyncDoneValueContainer( getCodecService(), this );
        decoder.decode( bb, container );
        return this;
    }
}
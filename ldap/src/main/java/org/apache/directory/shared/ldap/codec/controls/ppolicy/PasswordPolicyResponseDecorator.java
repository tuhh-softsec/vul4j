/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.shared.ldap.codec.controls.ppolicy;


import java.nio.ByteBuffer;

import org.apache.directory.shared.asn1.ber.Asn1Decoder;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.asn1.ber.tlv.Value;
import org.apache.directory.shared.asn1.Asn1Object;
import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.controls.ControlDecorator;


/**
 * PasswordPolicyResponseControl.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PasswordPolicyResponseDecorator extends ControlDecorator<IPasswordPolicyResponse> implements IPasswordPolicyResponse
{
    /** An instance of this decoder */
    private static final Asn1Decoder decoder = new Asn1Decoder();

    // Storage for computed lengths
    private transient int valueLength = 0;
    private transient int ppolicySeqLength = 0;
    private transient int warningLength = 0;
    private transient int timeBeforeExpirationTagLength;
    private transient int graceAuthNsRemainingTagLength;
    
    
    public PasswordPolicyResponseDecorator()
    {
        super( new PasswordPolicyResponse() );
    }


    public PasswordPolicyResponseDecorator( IPasswordPolicyResponse response )
    {
        super( response );
    }


    @Override
    public int computeLength()
    {
        if ( getTimeBeforeExpiration() >= 0 )
        {
            timeBeforeExpirationTagLength = TLV.getNbBytes( getTimeBeforeExpiration() );
            warningLength = 1 + TLV.getNbBytes( timeBeforeExpirationTagLength ) + timeBeforeExpirationTagLength;
        }
        else if ( getGraceAuthNsRemaining() >= 0 )
        {
            graceAuthNsRemainingTagLength = TLV.getNbBytes( getGraceAuthNsRemaining() );
            warningLength = 1 + TLV.getNbBytes( graceAuthNsRemainingTagLength ) + graceAuthNsRemainingTagLength;
        }

        if ( warningLength != 0 )
        {
            ppolicySeqLength = 1 + TLV.getNbBytes( warningLength ) + warningLength;
        }

        if ( getPasswordPolicyError() != null )
        {
            ppolicySeqLength += 1 + 1 + 1;
        }
        
        if ( ppolicySeqLength > 0 )
        {
            valueLength = 1 + TLV.getNbBytes( ppolicySeqLength ) + ppolicySeqLength;
        }

        return super.computeLength( valueLength );
    }


    @Override
    public ByteBuffer encode( ByteBuffer buffer ) throws EncoderException
    {
        if ( buffer == null )
        {
            throw new EncoderException( I18n.err( I18n.ERR_04023 ) );
        }

        // Encode the Control envelop
        super.encode( buffer );

        if ( valueLength > 0 )
        {
            // Encode the OCTET_STRING tag
            buffer.put( UniversalTag.OCTET_STRING.getValue() );
            buffer.put( TLV.getBytes( valueLength ) );
        }

        if ( ( getTimeBeforeExpiration() < 0 ) && ( getGraceAuthNsRemaining() < 0 ) && ( getPasswordPolicyError() == null ) )
        {
            return buffer;
        }
        else
        {
            // Encode the Sequence tag
            buffer.put( UniversalTag.SEQUENCE.getValue() );
            buffer.put( TLV.getBytes( ppolicySeqLength ) );

            if ( warningLength > 0 )
            {
                // Encode the Warning tag
                buffer.put( ( byte )PasswordPolicyResponseTags.PPOLICY_WARNING_TAG.getValue() );
                buffer.put( TLV.getBytes( warningLength ) );

                if ( getTimeBeforeExpiration() >= 0 )
                {
                    buffer.put( ( byte ) PasswordPolicyResponseTags.TIME_BEFORE_EXPIRATION_TAG.getValue() );
                    buffer.put( TLV.getBytes( timeBeforeExpirationTagLength ) );
                    buffer.put( Value.getBytes( getTimeBeforeExpiration() ) );
                }
                else if ( getGraceAuthNsRemaining() >= 0 )
                {
                    buffer.put( ( byte ) PasswordPolicyResponseTags.GRACE_AUTHNS_REMAINING_TAG.getValue() );
                    buffer.put( TLV.getBytes( graceAuthNsRemainingTagLength ) );
                    buffer.put( Value.getBytes( getGraceAuthNsRemaining() ) );
                }
            }
    
            if ( getPasswordPolicyError() != null )
            {
                buffer.put( (byte)PasswordPolicyResponseTags.PPOLICY_ERROR_TAG.getValue() );
                buffer.put( ( byte ) 0x01 );
                buffer.put( Value.getBytes( getPasswordPolicyError().getValue() ) );
            }
        }

        return buffer;
    }


    /**
     * {@inheritDoc}
     */
    public int getTimeBeforeExpiration()
    {
        return getDecorated().getTimeBeforeExpiration();
    }


    /**
     * {@inheritDoc}
     */
    public void setTimeBeforeExpiration( int timeBeforeExpiration )
    {
        getDecorated().setTimeBeforeExpiration( timeBeforeExpiration );
    }


    /**
     * {@inheritDoc}
     */
    public int getGraceAuthNsRemaining()
    {
        return getDecorated().getGraceAuthNsRemaining();
    }


    /**
     * {@inheritDoc}
     */
    public void setGraceAuthNsRemaining( int graceAuthNsRemaining )
    {
        getDecorated().setGraceAuthNsRemaining( graceAuthNsRemaining );
    }


    /**
     * {@inheritDoc}
     */
    public PasswordPolicyErrorEnum getPasswordPolicyError()
    {
        return getDecorated().getPasswordPolicyError();
    }


    /**
     * {@inheritDoc}
     */
    public void setPasswordPolicyError( PasswordPolicyErrorEnum ppolicyError )
    {
        getDecorated().setPasswordPolicyError( ppolicyError );
    }


    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append( "  PasswordPolicyResponse control :\n" );
        sb.append( "   oid          : '" ).append( getOid() ).append( '\n' );
        
        if ( getTimeBeforeExpiration() >= 0 )
        {
            sb.append( "   timeBeforeExpiration          : '" ).append( getTimeBeforeExpiration() ).append( '\n' );
        }
        else if ( getGraceAuthNsRemaining() >= 0 )
        {
            sb.append( "   graceAuthNsRemaining          : '" ).append( getGraceAuthNsRemaining() ).append( '\n' );
        }

        if ( getPasswordPolicyError() != null )
        {
            sb.append( "   ppolicyError          : '" ).append( getPasswordPolicyError().toString() ).append( '\n' );
        }

        return sb.toString();
    }


    @Override
    public Asn1Object decode( byte[] controlBytes ) throws DecoderException
    {
        ByteBuffer bb = ByteBuffer.wrap( controlBytes );
        PasswordPolicyResponseContainer container = new PasswordPolicyResponseContainer( this );
        decoder.decode( bb, container );
        return this;
    }
}

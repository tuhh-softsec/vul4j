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

import org.apache.directory.shared.asn1.Asn1Object;
import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.asn1.ber.Asn1Decoder;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.asn1.ber.tlv.Value;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.api.ILdapCodecService;
import org.apache.directory.shared.ldap.codec.controls.ControlDecorator;


/**
 * PasswordPolicyResponseControl.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PasswordPolicyDecorator extends ControlDecorator<IPasswordPolicy> implements IPasswordPolicy
{
    /** An instance of this decoder */
    private static final Asn1Decoder decoder = new Asn1Decoder();
    
    // Storage for computed lengths
    private transient int valueLength = 0;
    private transient int ppolicySeqLength = 0;
    private transient int warningLength = 0;
    private transient int timeBeforeExpirationTagLength;
    private transient int graceAuthNsRemainingTagLength;
    
    
    public PasswordPolicyDecorator( ILdapCodecService codec )
    {
        super( codec, new PasswordPolicy() );
    }
    
    
    public PasswordPolicyDecorator( ILdapCodecService codec, boolean hasResponse )
    {
        super( codec, new PasswordPolicy( hasResponse ) );
    }


    public PasswordPolicyDecorator( ILdapCodecService codec, IPasswordPolicy policy )
    {
        super( codec, policy );
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue( byte[] value )
    {
        if ( value == null || value.length == 0 )
        {
            setResponse( null );
        }
        
        if ( value != null && ! hasResponse() )
        {
            setResponse( true );
        }
        
        super.setValue( value );
    }


    @Override
    public int computeLength()
    {
        if ( ! hasResponse() )
        {
            return 0;
        }
        
        if ( getResponse().getTimeBeforeExpiration() >= 0 )
        {
            timeBeforeExpirationTagLength = TLV.getNbBytes( getResponse().getTimeBeforeExpiration() );
            warningLength = 1 + TLV.getNbBytes( timeBeforeExpirationTagLength ) + timeBeforeExpirationTagLength;
        }
        else if ( getResponse().getGraceAuthNsRemaining() >= 0 )
        {
            graceAuthNsRemainingTagLength = TLV.getNbBytes( getResponse().getGraceAuthNsRemaining() );
            warningLength = 1 + TLV.getNbBytes( graceAuthNsRemainingTagLength ) + graceAuthNsRemainingTagLength;
        }

        if ( warningLength != 0 )
        {
            ppolicySeqLength = 1 + TLV.getNbBytes( warningLength ) + warningLength;
        }

        if ( getResponse().getPasswordPolicyError() != null )
        {
            ppolicySeqLength += 1 + 1 + 1;
        }
        
        if ( ppolicySeqLength > 0 )
        {
            valueLength = 1 + TLV.getNbBytes( ppolicySeqLength ) + ppolicySeqLength;
        }

        return valueLength;
    }


    @Override
    public ByteBuffer encode( ByteBuffer buffer ) throws EncoderException
    {
        if ( ! hasResponse() )
        {
            return buffer;
        }
        
        if ( buffer == null )
        {
            throw new EncoderException( I18n.err( I18n.ERR_04023 ) );
        }

        if ( ( getResponse().getTimeBeforeExpiration() < 0 ) && ( getResponse().getGraceAuthNsRemaining() < 0 ) && ( 
            getResponse().getPasswordPolicyError() == null ) )
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
                buffer.put( ( byte )PasswordPolicyTags.PPOLICY_WARNING_TAG.getValue() );
                buffer.put( TLV.getBytes( warningLength ) );

                if ( getResponse().getTimeBeforeExpiration() >= 0 )
                {
                    buffer.put( ( byte ) PasswordPolicyTags.TIME_BEFORE_EXPIRATION_TAG.getValue() );
                    buffer.put( TLV.getBytes( timeBeforeExpirationTagLength ) );
                    buffer.put( Value.getBytes( getResponse().getTimeBeforeExpiration() ) );
                }
                else if ( getResponse().getGraceAuthNsRemaining() >= 0 )
                {
                    buffer.put( ( byte ) PasswordPolicyTags.GRACE_AUTHNS_REMAINING_TAG.getValue() );
                    buffer.put( TLV.getBytes( graceAuthNsRemainingTagLength ) );
                    buffer.put( Value.getBytes( getResponse().getGraceAuthNsRemaining() ) );
                }
            }
    
            if ( getResponse().getPasswordPolicyError() != null )
            {
                buffer.put( (byte)PasswordPolicyTags.PPOLICY_ERROR_TAG.getValue() );
                buffer.put( ( byte ) 0x01 );
                buffer.put( Value.getBytes( getResponse().getPasswordPolicyError().getValue() ) );
            }
        }

        return buffer;
    }

    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append( "  PasswordPolicyResponse control :\n" );
        sb.append( "   oid          : '" ).append( getOid() ).append( '\n' );
        
        if ( getResponse().getTimeBeforeExpiration() >= 0 )
        {
            sb.append( "   timeBeforeExpiration          : '" ).append( getResponse().getTimeBeforeExpiration() ).append( '\n' );
        }
        else if ( getResponse().getGraceAuthNsRemaining() >= 0 )
        {
            sb.append( "   graceAuthNsRemaining          : '" ).append( getResponse().getGraceAuthNsRemaining() ).append( '\n' );
        }

        if ( getResponse().getPasswordPolicyError() != null )
        {
            sb.append( "   ppolicyError          : '" ).append( getResponse().getPasswordPolicyError().toString() ).append( '\n' );
        }

        return sb.toString();
    }


    @Override
    public Asn1Object decode( byte[] controlBytes ) throws DecoderException
    {
        if ( ! hasResponse() )
        {
            return this;
        }
        
        ByteBuffer bb = ByteBuffer.wrap( controlBytes );
        PasswordPolicyContainer container = new PasswordPolicyContainer( getCodecService(), this );
        decoder.decode( bb, container );
        return this;
    }


    /**
     * 
     * {@inheritDoc}
     */
    public boolean hasResponse()
    {
        return getDecorated().hasResponse();
    }
    
    
    /**
     * 
     * {@inheritDoc}
     */
    public void setResponse( IPasswordPolicyResponse response )
    {
        getDecorated().setResponse( response );
    }

    
    /**
     * 
     * {@inheritDoc}
     */
    public IPasswordPolicyResponse setResponse( boolean hasResponse )
    {
        return getDecorated().setResponse( hasResponse );
    }
    

    /**
     * 
     * {@inheritDoc}
     */
    public IPasswordPolicyResponse getResponse()
    {
        return getDecorated().getResponse();
    }
}

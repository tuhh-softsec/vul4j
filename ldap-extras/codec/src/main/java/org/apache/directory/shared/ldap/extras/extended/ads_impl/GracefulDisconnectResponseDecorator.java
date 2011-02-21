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
package org.apache.directory.shared.ldap.extras.extended.ads_impl;


import java.nio.ByteBuffer;
import java.util.List;

import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.asn1.ber.Asn1Decoder;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.api.ExtendedResponseDecorator;
import org.apache.directory.shared.ldap.codec.api.LdapCodecService;
import org.apache.directory.shared.ldap.extras.extended.GracefulDisconnectResponse;
import org.apache.directory.shared.ldap.extras.extended.GracefulDisconnectResponseImpl;
import org.apache.directory.shared.ldap.model.exception.LdapURLEncodingException;
import org.apache.directory.shared.ldap.model.filter.LdapURL;
import org.apache.directory.shared.ldap.model.message.Referral;
import org.apache.directory.shared.ldap.model.message.ReferralImpl;
import org.apache.directory.shared.ldap.model.message.ResultCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A Decorator for CancelResponses.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class GracefulDisconnectResponseDecorator extends ExtendedResponseDecorator<GracefulDisconnectResponse> implements GracefulDisconnectResponse
{
    /** The logger. */
    private static final Logger LOG = LoggerFactory.getLogger( GracefulDisconnectResponseDecorator.class );

    
    /**
     * Creates a new instance of CancelResponseDecorator.
     *
     * @param codec
     * @param decoratedMessage
     */
    public GracefulDisconnectResponseDecorator( LdapCodecService codec, GracefulDisconnectResponse decoratedMessage )
    {
        super( codec, decoratedMessage );
        responseValue = null;
        encodeResponse();
    }
    
    
    /**
     * Creates a new instance of CancelResponseDecorator.
     *
     * @param codec
     * @param responseValue
     */
    public GracefulDisconnectResponseDecorator( LdapCodecService codec, byte[] responseValue ) throws DecoderException
    {
        super( codec, new GracefulDisconnectResponseImpl() );
        this.responseValue = responseValue;
        decodeValue();
    }
    
    
    private void decodeValue() throws DecoderException
    {
        GracefulDisconnectDecoder decoder = new GracefulDisconnectDecoder();
        org.apache.directory.shared.ldap.extras.extended.ads_impl.GracefulDisconnect codec = null;

        try
        {
            codec = (org.apache.directory.shared.ldap.extras.extended.ads_impl.GracefulDisconnect) decoder
                .decode( responseValue );
            getDecorated().setTimeOffline( codec.getTimeOffline() );
            getDecorated().setDelay( codec.getDelay() );
            getDecorated().getLdapResult().setResultCode( ResultCodeEnum.SUCCESS );
            List<LdapURL> contexts = codec.getReplicatedContexts();

            for ( LdapURL ldapUrl : contexts )
            {
                getDecorated().getLdapResult().getReferral().addLdapUrl( ldapUrl.toString() );
            }
        }
        catch ( DecoderException e )
        {
            LOG.error( I18n.err( I18n.ERR_04169 ), e );
            throw e;
        }
    }


    private void encodeResponse()
    {
        org.apache.directory.shared.ldap.extras.extended.ads_impl.GracefulDisconnect codec = 
            new org.apache.directory.shared.ldap.extras.extended.ads_impl.GracefulDisconnect();
        codec.setTimeOffline( getDecorated().getTimeOffline() );
        codec.setDelay( getDecorated().getDelay() );

        for ( String ldapUrlStr : getDecorated().getLdapResult().getReferral().getLdapUrls() )
        {
            LdapURL ldapUrl = null;

            try
            {
                ldapUrl = new LdapURL( ldapUrlStr );
            }
            catch ( LdapURLEncodingException e )
            {
                LOG.error( I18n.err( I18n.ERR_04170, ldapUrlStr ), e );
                continue;
            }

            codec.addReplicatedContexts( ldapUrl );
        }

        try
        {
            super.responseValue = codec.encode().array();
        }
        catch ( EncoderException e )
        {
            LOG.error( I18n.err( I18n.ERR_04171 ), e );
            throw new RuntimeException( e );
        }
    }


    // ------------------------------------------------------------------------
    // ExtendedResponse Interface Method Implementations
    // ------------------------------------------------------------------------

    
    /**
     * Gets the response OID specific encoded response values.
     * 
     * @return the response specific encoded response values.
     */
    public byte[] getResponseValue()
    {
        if ( responseValue == null )
        {
            encodeResponse();
        }

        final byte[] copy = new byte[responseValue.length];
        System.arraycopy( responseValue, 0, copy, 0, responseValue.length );
        return copy;
    }


    /**
     * Sets the response OID specific encoded response values.
     * 
     * @param responseValue the response specific encoded response values.
     */
    public void setResponseValue( byte[] responseValue )
    {
        if ( responseValue == null )
        {
            this.responseValue = null;
            getDecorated().setDelay( 0 );
            getDecorated().setTimeOffline( 0 );
            getDecorated().getLdapResult().setReferral( new ReferralImpl() );
            return;
        }
        
        ByteBuffer bb = ByteBuffer.wrap( responseValue );
        GracefulDisconnectContainer container = new GracefulDisconnectContainer();
        Asn1Decoder decoder = new Asn1Decoder();

        try
        {
            decoder.decode( bb, container );
        }
        catch ( DecoderException e )
        {
            LOG.error( I18n.err( I18n.ERR_04172 ), e );
        }

        org.apache.directory.shared.ldap.extras.extended.ads_impl.GracefulDisconnect codec = container
            .getGracefulDisconnect();
        
        getDecorated().setDelay( codec.getDelay() );
        getDecorated().setTimeOffline( codec.getTimeOffline() );

        for ( LdapURL ldapUrl : codec.getReplicatedContexts() )
        {
            getDecorated().getLdapResult().getReferral().addLdapUrl( ldapUrl.toString() );
        }

        this.responseValue = new byte[responseValue.length];
        System.arraycopy( responseValue, 0, this.responseValue, 0, responseValue.length );
    }
    

    /**
     * {@inheritDoc}
     */
    public int getDelay()
    {
        return getDecorated().getDelay();
    }

    
    /**
     * {@inheritDoc}
     */
    public void setDelay( int delay )
    {
        getDecorated().setDelay( delay );
    }

    
    /**
     * {@inheritDoc}
     */
    public int getTimeOffline()
    {
        return getDecorated().getTimeOffline();
    }
    

    /**
     * {@inheritDoc}
     */
    public void setTimeOffline( int timeOffline )
    {
        getDecorated().setTimeOffline( timeOffline );
    }

    
    /**
     * {@inheritDoc}
     */
    public Referral getReplicatedContexts()
    {
        return getDecorated().getReplicatedContexts();
    }
}

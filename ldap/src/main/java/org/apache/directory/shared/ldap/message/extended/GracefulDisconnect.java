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
package org.apache.directory.shared.ldap.message.extended;


import java.nio.ByteBuffer;
import java.util.List;

import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.asn1.ber.Asn1Decoder;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.extended.operations.gracefulDisconnect.GracefulDisconnectContainer;
import org.apache.directory.shared.ldap.codec.extended.operations.gracefulDisconnect.GracefulDisconnectDecoder;
import org.apache.directory.shared.ldap.model.exception.LdapURLEncodingException;
import org.apache.directory.shared.ldap.model.message.ExtendedResponseImpl;
import org.apache.directory.shared.ldap.model.message.Referral;
import org.apache.directory.shared.ldap.model.message.ReferralImpl;
import org.apache.directory.shared.ldap.model.message.ResultCodeEnum;
import org.apache.directory.shared.ldap.model.filter.LdapURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An unsolicited notification, extended response, intended for notifying
 * clients of upcoming disconnection due to intended service windows. Unlike the
 * {@link NoticeOfDisconnect} this response contains additional information about
 * the amount of time the server will be offline and exactly when it intends to
 * shutdown.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class GracefulDisconnect extends ExtendedResponseImpl
{
    /** The serialVersionUID. */
    private static final long serialVersionUID = -4682291068700593492L;

    /** The OID for the graceful disconnect extended operation response. */
    public static final String EXTENSION_OID = "1.3.6.1.4.1.18060.0.1.5";

    /** The logger. */
    private static final Logger LOG = LoggerFactory.getLogger( GracefulDisconnect.class );

    /** Offline time after disconnection */
    private int timeOffline;

    /** Delay before disconnection */
    private int delay;

    /** String based LDAP URL that may be followed for replicated namingContexts */
    private Referral replicatedContexts = new ReferralImpl();


    /**
     * Instantiates a new graceful disconnect.
     *
     * @param responseValue the response value
     * @throws DecoderException if the response value can't be decoded
     */
    public GracefulDisconnect( byte[] responseValue ) throws DecoderException
    {
        super( 0, EXTENSION_OID );

        if ( responseValue != null )
        {
            this.responseValue = new byte[responseValue.length];
            System.arraycopy( responseValue, 0, this.responseValue, 0, responseValue.length );
        }
        else
        {
            this.responseValue = null;
        }

        decodeValue();
    }


    /**
     * Instantiates a new graceful disconnect.
     *
     * @param timeOffline the offline time after disconnect, in minutes
     * @param delay the delay before disconnect, in seconds
     */
    public GracefulDisconnect( int timeOffline, int delay )
    {
        super( 0, EXTENSION_OID );
        responseName = EXTENSION_OID;
        this.timeOffline = timeOffline;
        this.delay = delay;

        StringBuffer buf = new StringBuffer();
        buf.append( "The server will disconnect and will be unavailable for " ).append( timeOffline );
        buf.append( " minutes in " ).append( delay ).append( " seconds." );

        ldapResult.setErrorMessage( buf.toString() );
        ldapResult.setMatchedDn( null );
        ldapResult.setResultCode( ResultCodeEnum.UNAVAILABLE );

        encodeResponse();
    }


    private void decodeValue() throws DecoderException
    {
        GracefulDisconnectDecoder decoder = new GracefulDisconnectDecoder();
        org.apache.directory.shared.ldap.codec.extended.operations.gracefulDisconnect.GracefulDisconnect codec = null;

        try
        {
            codec = ( org.apache.directory.shared.ldap.codec.extended.operations.gracefulDisconnect.GracefulDisconnect ) decoder
                .decode( responseValue );
            this.timeOffline = codec.getTimeOffline();
            this.delay = codec.getDelay();
            ldapResult.setResultCode( ResultCodeEnum.SUCCESS );
            List<LdapURL> contexts = codec.getReplicatedContexts();

            for ( LdapURL ldapUrl : contexts )
            {
                replicatedContexts.addLdapUrl( ldapUrl.toString() );
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
        org.apache.directory.shared.ldap.codec.extended.operations.gracefulDisconnect.GracefulDisconnect codec = new org.apache.directory.shared.ldap.codec.extended.operations.gracefulDisconnect.GracefulDisconnect();
        codec.setTimeOffline( this.timeOffline );
        codec.setDelay( this.delay );

        for ( String ldapUrlStr : replicatedContexts.getLdapUrls() )
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
     * Sets the reponse OID specific encoded response values.
     * 
     * @param responseValue the response specific encoded response values.
     */
    public void setResponseValue( byte[] responseValue )
    {
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

        org.apache.directory.shared.ldap.codec.extended.operations.gracefulDisconnect.GracefulDisconnect codec = container
            .getGracefulDisconnect();
        this.delay = codec.getDelay();
        this.timeOffline = codec.getTimeOffline();

        for ( LdapURL ldapUrl : codec.getReplicatedContexts() )
        {
            replicatedContexts.addLdapUrl( ldapUrl.toString() );
        }

        if ( responseValue != null )
        {
            this.responseValue = new byte[responseValue.length];
            System.arraycopy( responseValue, 0, this.responseValue, 0, responseValue.length );
        }
        else
        {
            this.responseValue = null;
        }
    }


    /**
     * Gets the OID uniquely identifying this extended response (a.k.a. its
     * name).
     * 
     * @return the OID of the extended response type.
     */
    public String getResponseName()
    {
        return EXTENSION_OID;
    }


    /**
     * Sets the OID uniquely identifying this extended response (a.k.a. its
     * name).
     * 
     * @param oid the OID of the extended response type.
     */
    public void setResponseName( String oid )
    {
        throw new UnsupportedOperationException( I18n.err( I18n.ERR_04168, EXTENSION_OID ) );
    }


    // -----------------------------------------------------------------------
    // Parameters of the Extended Response Value
    // -----------------------------------------------------------------------

    /**
     * Gets the delay before disconnection, in seconds.
     *
     * @return the delay before disconnection
     */
    public int getDelay()
    {
        return delay;
    }


    /**
     * Sets the delay befor disconnection, in seconds.
     *
     * @param delay the new delay before disconnection
     */
    public void setDelay( int delay )
    {
        this.delay = delay;
    }


    /**
     * Gets the offline time after disconnection, in minutes.
     *
     * @return the offline time after disconnection
     */
    public int getTimeOffline()
    {
        return timeOffline;
    }


    /**
     * Sets the time offline after disconnection, in minutes.
     *
     * @param timeOffline the new time offline after disconnection
     */
    public void setTimeOffline( int timeOffline )
    {
        this.timeOffline = timeOffline;
    }


    /**
     * Gets the replicated contexts.
     *
     * @return the replicated contexts
     */
    public Referral getReplicatedContexts()
    {
        return replicatedContexts;
    }
}

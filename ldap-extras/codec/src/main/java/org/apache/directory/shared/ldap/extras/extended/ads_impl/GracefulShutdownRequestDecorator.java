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


import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.api.ExtendedRequestDecorator;
import org.apache.directory.shared.ldap.codec.api.LdapCodecService;
import org.apache.directory.shared.ldap.extras.extended.GracefulShutdownRequest;
import org.apache.directory.shared.ldap.extras.extended.GracefulShutdownResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A Decorator for GracefulShutdownRequests.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class GracefulShutdownRequestDecorator extends ExtendedRequestDecorator<GracefulShutdownRequest,GracefulShutdownResponse> 
    implements GracefulShutdownRequest
{
    private static final Logger LOG = LoggerFactory.getLogger( GracefulShutdownRequestDecorator.class );
    

    /**
     * Creates a new instance of GracefulShutdownRequestDecorator.
     *
     * @param codec
     * @param decoratedMessage
     */
    public GracefulShutdownRequestDecorator( LdapCodecService codec, GracefulShutdownRequest decoratedMessage )
    {
        super( codec, decoratedMessage );
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void setRequestValue( byte[] requestValue )
    {
        GracefulShutdownDecoder decoder = new GracefulShutdownDecoder();

        try
        {
            GracefulShutdown gs = (GracefulShutdown) decoder.decode( requestValue );

            if ( requestValue != null )
            {
                this.requestValue = new byte[requestValue.length];
                System.arraycopy( requestValue, 0, this.requestValue, 0, requestValue.length );
            }
            else
            {
                this.requestValue = null;
            }

            setTimeOffline( gs.getTimeOffline() );
            setDelay( gs.getDelay() );
        }
        catch ( DecoderException e )
        {
            LOG.error( I18n.err( I18n.ERR_04165 ), e );
            throw new RuntimeException( e );
        }
    }


    /**
     * {@inheritDoc}
     */
    public byte[] getRequestValue()
    {
        if ( requestValue == null )
        {
            try
            {
                GracefulShutdown gs = new GracefulShutdown();
                gs.setDelay( getDecorated().getDelay() );
                gs.setTimeOffline( getDecorated().getTimeOffline() );
                requestValue = gs.encode().array();
            }
            catch ( EncoderException e )
            {
                LOG.error( I18n.err( I18n.ERR_04164 ), e );
                throw new RuntimeException( e );
            }
        }

        return requestValue;
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
}

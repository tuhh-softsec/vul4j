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


import javax.naming.NamingException;
import javax.naming.ldap.ExtendedResponse;

import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.extended.operations.gracefulShutdown.GracefulShutdown;
import org.apache.directory.shared.ldap.codec.extended.operations.gracefulShutdown.GracefulShutdownDecoder;
import org.apache.directory.shared.ldap.model.message.ExtendedRequestImpl;
import org.apache.directory.shared.ldap.model.message.ResultResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An extended operation requesting the server to shutdown it's LDAP service
 * port while allowing established clients to complete or abandon operations
 * already in progress. More information about this extended request is
 * available here: <a href="http://docs.safehaus.org:8080/x/GR">LDAP Extensions
 * for Graceful Shutdown</a>.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class GracefulShutdownRequest extends ExtendedRequestImpl
{
    /** The logger. */
    private static final Logger LOG = LoggerFactory.getLogger( GracefulShutdownRequest.class );

    /** The serialVersionUID. */
    private static final long serialVersionUID = -4682291068700593492L;

    /** The OID for the graceful shutdown extended operation request. */
    public static final String EXTENSION_OID = "1.3.6.1.4.1.18060.0.1.3";

    /** Undetermined value used for offline time */
    public static final int UNDETERMINED = 0;

    /** The shutdown is immediate */
    public static final int NOW = 0;

    /** Offline time after disconnection */
    private int timeOffline;

    /** Delay before disconnection */
    private int delay;


    /**
     * Instantiates a new graceful shutdown request.
     *
     * @param messageId the message id
     */
    public GracefulShutdownRequest( int messageId )
    {
        this( messageId, UNDETERMINED, NOW );
    }


    /**
     * Instantiates a new graceful shutdown request.
     *
     * @param messageId the message id
     * @param timeOffline the offline time after disconnection, in minutes
     * @param delay the delay before disconnection, in seconds
     */
    public GracefulShutdownRequest( int messageId, int timeOffline, int delay )
    {
        super( messageId );
        setRequestName( EXTENSION_OID );
        this.timeOffline = timeOffline;
        this.delay = delay;
    }


    /**
     * {@inheritDoc}
     */
    public void setRequestValue( byte[] requestValue )
    {
        GracefulShutdownDecoder decoder = new GracefulShutdownDecoder();

        try
        {
            GracefulShutdown gs = ( GracefulShutdown ) decoder.decode( requestValue );

            if ( requestValue != null )
            {
                this.requestValue = new byte[requestValue.length];
                System.arraycopy( requestValue, 0, this.requestValue, 0, requestValue.length );
            }
            else
            {
                this.requestValue = null;
            }

            this.timeOffline = gs.getTimeOffline();
            this.delay = gs.getDelay();
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
    public ExtendedResponse createExtendedResponse( String id, byte[] berValue, int offset, int length )
        throws NamingException
    {
        return ( ExtendedResponse ) getResultResponse();
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
                gs.setDelay( this.delay );
                gs.setTimeOffline( this.timeOffline );
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
    public ResultResponse getResultResponse()
    {
        if ( response == null )
        {
            GracefulShutdownResponse gsr = new GracefulShutdownResponse( getMessageId() );
            response = gsr;
        }

        return response;
    }


    // -----------------------------------------------------------------------
    // Parameters of the Extended Request Payload
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
}

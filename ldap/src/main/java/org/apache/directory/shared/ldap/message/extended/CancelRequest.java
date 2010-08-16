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

import org.apache.directory.shared.asn1.codec.DecoderException;
import org.apache.directory.shared.asn1.codec.EncoderException;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.extended.operations.cancel.Cancel;
import org.apache.directory.shared.ldap.codec.extended.operations.cancel.CancelDecoder;
import org.apache.directory.shared.ldap.message.ExtendedRequestImpl;
import org.apache.directory.shared.ldap.message.internal.ResultResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implement the extended Cancel Request as described in RFC 3909.
 * 
 * It's grammar is :
 * 
 * cancelRequestValue ::= SEQUENCE {
 *        cancelID        MessageID
 *                        -- MessageID is as defined in [RFC2251]
 * }
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CancelRequest extends ExtendedRequestImpl
{
    /** The serial version UUID */
    private static final long serialVersionUID = 1L;

    /** A logger for this class */
    private static final Logger LOG = LoggerFactory.getLogger( CancelRequest.class );

    /** The cancelId of the request to be canceled */
    private int cancelId;

    /** The requestName for this extended request */
    public static final String EXTENSION_OID = "1.3.6.1.1.8";


    /**
     * 
     * Creates a new instance of CancelRequest.
     *
     * @param messageId the message id
     * @param cancelId the message id of the request to cancel
     */
    public CancelRequest( int messageId, int cancelId )
    {
        super( messageId );
        setRequestName( EXTENSION_OID );

        this.cancelId = cancelId;
    }


    /**
     * Sets the extended request's <b>requestValue</b> portion of the PDU.
     * 
     * @param payload byte array of data encapsulating ext. req. parameters
     */
    public void setRequestValue( byte[] requestValue )
    {
        CancelDecoder decoder = new CancelDecoder();

        try
        {
            Cancel cancel = ( Cancel ) decoder.decode( requestValue );

            if ( requestValue != null )
            {
                this.requestValue = new byte[requestValue.length];
                System.arraycopy( requestValue, 0, this.requestValue, 0, requestValue.length );
            }
            else
            {
                this.requestValue = null;
            }

            this.cancelId = cancel.getCancelId();
        }
        catch ( DecoderException e )
        {
            LOG.error( I18n.err( I18n.ERR_04165 ), e );
            throw new RuntimeException( e );
        }
    }


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
                Cancel cancel = new Cancel();
                cancel.setCancelId( this.cancelId );

                requestValue = cancel.encode().array();
            }
            catch ( EncoderException e )
            {
                LOG.error( I18n.err( I18n.ERR_04164 ), e );
                throw new RuntimeException( e );
            }
        }

        return requestValue;
    }


    public ResultResponse getResultResponse()
    {
        if ( response == null )
        {
            response = new CancelResponse( cancelId );
        }

        return response;
    }
}

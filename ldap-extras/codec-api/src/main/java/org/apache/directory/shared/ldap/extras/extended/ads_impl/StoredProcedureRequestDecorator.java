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

import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.api.ExtendedRequestDecorator;
import org.apache.directory.shared.ldap.codec.api.LdapCodecService;
import org.apache.directory.shared.ldap.extras.extended.StoredProcedureRequest;
import org.apache.directory.shared.ldap.extras.extended.StoredProcedureRequestImpl;
import org.apache.directory.shared.ldap.extras.extended.StoredProcedureResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A Decorator for stored procedure extended operation requests.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StoredProcedureRequestDecorator 
    extends ExtendedRequestDecorator<StoredProcedureRequest,StoredProcedureResponse> 
    implements StoredProcedureRequest
{
    private static final Logger LOG = LoggerFactory.getLogger( StoredProcedureRequestDecorator.class );

    private StoredProcedure procedure;
    
    
    public StoredProcedureRequestDecorator( LdapCodecService codec, StoredProcedureRequest decoratedMessage )
    {
        super( codec, decoratedMessage );
        
        procedure = new StoredProcedure( ( StoredProcedureRequestImpl ) decoratedMessage );
    }


    /**
     * {@inheritDoc}
     */
    public void setRequestValue( byte[] payload )
    {
        StoredProcedureDecoder decoder = new StoredProcedureDecoder();
        StoredProcedureContainer container = new StoredProcedureContainer();

        try
        {
            decoder.decode( ByteBuffer.wrap( payload ), container );
            this.procedure = container.getStoredProcedure();
        }
        catch ( Exception e )
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
                requestValue = procedure.encode().array();
            }
            catch ( EncoderException e )
            {
                LOG.error( I18n.err( I18n.ERR_04174 ), e );
                throw new RuntimeException( e );
            }
        }

        return requestValue;
    }


    /**
     * {@inheritDoc}
     */
    public String getLanguage()
    {
        return getDecorated().getLanguage();
    }


    /**
     * {@inheritDoc}
     */
    public void setLanguage( String language )
    {
        getDecorated().setLanguage( language );
    }


    /**
     * {@inheritDoc}
     */
    public void setProcedure( String procedure )
    {
        getDecorated().setProcedure( procedure );
    }


    /**
     * {@inheritDoc}
     */
    public String getProcedureSpecification()
    {
        return getDecorated().getProcedureSpecification();
    }


    /**
     * {@inheritDoc}
     */
    public int size()
    {
        return getDecorated().size();
    }


    /**
     * {@inheritDoc}
     */
    public Object getParameterType( int index )
    {
        return getDecorated().getParameterType( index );
    }


    /**
     * {@inheritDoc}
     */

    public Class<?> getJavaParameterType( int index )
    {
        return getDecorated().getJavaParameterType( index );
    }


    /**
     * {@inheritDoc}
     */

    public Object getParameterValue( int index )
    {
        return getDecorated().getParameterValue( index );
    }


    /**
     * {@inheritDoc}
     */
    public Object getJavaParameterValue( int index )
    {
        return getDecorated().getJavaParameterValue( index );
    }


    /**
     * {@inheritDoc}
     */
    public void addParameter( Object type, Object value )
    {
        getDecorated().addParameter( type, value );
    }
}

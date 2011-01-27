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
package org.apache.directory.shared.ldap.codec.decorators;


import org.apache.directory.shared.ldap.model.message.BindRequest;
import org.apache.directory.shared.ldap.model.name.Dn;


/**
 * A decorator for the BindRequest message
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class BindRequestDecorator extends SingleReplyRequestDecorator implements BindRequest
{
    /** The bind request length */
    private int bindRequestLength;

    /** The SASL Mechanism length */
    private int saslMechanismLength;

    /** The SASL credentials length */
    private int saslCredentialsLength;


    /**
     * Makes a BindRequest a MessageDecorator.
     *
     * @param decoratedMessage the decorated BindRequests.
     */
    public BindRequestDecorator( BindRequest decoratedMessage )
    {
        super( decoratedMessage );
    }


    /**
     * @return The decorated BindRequest
     */
    public BindRequest getBindRequest()
    {
        return ( BindRequest ) getDecoratedMessage();
    }


    /**
     * Stores the encoded length for the BindRequest
     * @param bindRequestLength The encoded length
     */
    public void setBindRequestLength( int bindRequestLength )
    {
        this.bindRequestLength = bindRequestLength;
    }


    /**
     * @return The encoded BindRequest's length
     */
    public int getBindRequestLength()
    {
        return bindRequestLength;
    }


    /**
     * Stores the encoded length for the SaslCredentials
     * @param saslCredentialsLength The encoded length
     */
    public void setSaslCredentialsLength( int saslCredentialsLength )
    {
        this.saslCredentialsLength = saslCredentialsLength;
    }


    /**
     * @return The encoded SaslCredentials's length
     */
    public int getSaslCredentialsLength()
    {
        return saslCredentialsLength;
    }


    /**
     * Stores the encoded length for the Mechanism
     * @param saslMechanismLength The encoded length
     */
    public void setSaslMechanismLength( int saslMechanismLength )
    {
        this.saslMechanismLength = saslMechanismLength;
    }


    /**
     * @return The encoded SaslMechanism's length
     */
    public int getSaslMechanismLength()
    {
        return saslMechanismLength;
    }
    
    
    //-------------------------------------------------------------------------
    // The BindRequest methods
    //-------------------------------------------------------------------------


    /**
     * {@inheritDoc}
     */
    public boolean isSimple()
    {
        return getBindRequest().isSimple();
    }


    /**
     * {@inheritDoc}
     */
    public boolean getSimple()
    {
        return getBindRequest().getSimple();
    }


    /**
     * {@inheritDoc}
     */
    public void setSimple( boolean isSimple )
    {
        getBindRequest().setSimple( isSimple );
    }


    /**
     * {@inheritDoc}
     */
    public byte[] getCredentials()
    {
        return getBindRequest().getCredentials();
    }


    /**
     * {@inheritDoc}
     */
    public void setCredentials( String credentials )
    {
        getBindRequest().setCredentials( credentials );
    }


    /**
     * {@inheritDoc}
     */
    public void setCredentials( byte[] credentials )
    {
        getBindRequest().setCredentials( credentials );
    }


    /**
     * {@inheritDoc}
     */
    public Dn getName()
    {
        return getBindRequest().getName();
    }


    /**
     * {@inheritDoc}
     */
    public void setName( Dn name )
    {
        getBindRequest().setName( name );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isVersion3()
    {
        return getBindRequest().isVersion3();
    }


    /**
     * {@inheritDoc}
     */
    public boolean getVersion3()
    {
        return getBindRequest().getVersion3();
    }


    /**
     * {@inheritDoc}
     */
    public void setVersion3( boolean isVersion3 )
    {
        getBindRequest().setVersion3( isVersion3 );
    }


    /**
     * {@inheritDoc}
     */
    public String getSaslMechanism()
    {
        return getBindRequest().getSaslMechanism();
    }


    /**
     * {@inheritDoc}
     */
    public void setSaslMechanism( String saslMechanism )
    {
        getBindRequest().setSaslMechanism( saslMechanism );
    }
}

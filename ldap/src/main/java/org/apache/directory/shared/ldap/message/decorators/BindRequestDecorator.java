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
package org.apache.directory.shared.ldap.message.decorators;


import org.apache.directory.shared.ldap.model.message.BindRequest;


/**
 * Doc me!
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class BindRequestDecorator extends MessageDecorator
{

    /** The bind request length */
    private int bindRequestLength;

    /** The SASL Mechanism length */
    private int saslMechanismLength;

    /** The SASL credentials length */
    private int saslCredentialsLength;


    /**
     * Makes a BindRequest encodable.
     *
     * @param decoratedMessage the decorated BindRequests.
     */
    public BindRequestDecorator( BindRequest decoratedMessage )
    {
        super( decoratedMessage );
    }


    public BindRequest getBindRequest()
    {
        return ( BindRequest ) getMessage();
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
}

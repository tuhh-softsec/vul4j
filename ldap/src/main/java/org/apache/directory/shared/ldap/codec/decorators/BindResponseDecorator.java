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


import org.apache.directory.shared.ldap.model.message.BindResponse;
import org.apache.directory.shared.ldap.model.message.LdapResult;


/**
 * A decorator for the BindResponse message
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class BindResponseDecorator extends ResponseDecorator implements BindResponse
{
    /** The encoded bindResponse length */
    private int bindResponseLength;


    /**
     * Makes a BindResponse encodable.
     *
     * @param decoratedMessage the decorated BindResponse
     */
    public BindResponseDecorator( BindResponse decoratedMessage )
    {
        super( decoratedMessage );
    }


    /**
     * @return The decorated BindResponse
     */
    public BindResponse getBindResponse()
    {
        return ( BindResponse ) getDecoratedMessage();
    }


    /**
     * Stores the encoded length for the BindResponse
     * @param bindResponseLength The encoded length
     */
    public void setBindResponseLength( int bindResponseLength )
    {
        this.bindResponseLength = bindResponseLength;
    }


    /**
     * @return The encoded BindResponse's length
     */
    public int getBindResponseLength()
    {
        return bindResponseLength;
    }


    //-------------------------------------------------------------------------
    // The BindResponse methods
    //-------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public LdapResult getLdapResult()
    {
        return getBindResponse().getLdapResult();
    }
    
    
    /**
     * {@inheritDoc}
     */
    public byte[] getServerSaslCreds()
    {
        return getBindResponse().getServerSaslCreds();
    }


    /**
     * {@inheritDoc}
     */
    public void setServerSaslCreds( byte[] serverSaslCreds )
    {
        getBindResponse().setServerSaslCreds( serverSaslCreds );
    }

    
    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return getBindResponse().toString();
    }
}

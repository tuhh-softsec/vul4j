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

package org.apache.directory.ldap.client.api;


import java.util.HashMap;
import java.util.Map;

import org.apache.directory.shared.ldap.message.BindRequest;
import org.apache.directory.shared.ldap.util.StringTools;


/**
 * Holds the data required to complete the SASL operation
 *  
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SaslRequest
{
    /** the bind request */
    private BindRequest bindRequest;

    /** the sasl mechaism's properties */
    private Map<String, String> saslMechProps = new HashMap<String, String>();

    /** SASL realm name on the server */
    private String realmName;

    /** the authorization ID of the entity */
    private String authorizationId;


    /**
     * Creates a new instance of SaslRequest.
     *
     * @param bindRequest The included BindRequest
     */
    protected SaslRequest( BindRequest bindRequest )
    {
        this.bindRequest = bindRequest;
    }


    /**
     * @return The interned BindRequest
     */
    public BindRequest getBindRequest()
    {
        return bindRequest;
    }


    /**
     * @return The supported SASL mechanisms
     */
    public Map<String, String> getSaslMechProps()
    {
        return saslMechProps;
    }


    /**
     * Set the supported SASL mechanisms
     *
     * @param saslMechProps The list of supported mechanisms
     */
    public void setSaslMechProps( Map<String, String> saslMechProps )
    {
        this.saslMechProps = saslMechProps;
    }


    /**
     * @return The realm name
     */
    public String getRealmName()
    {
        return realmName;
    }


    /**
     * Set the realm Name
     * @param realmName The realm name
     */
    public void setRealmName( String realmName )
    {
        this.realmName = realmName;
    }


    /**
     * @return The authorization Id
     */
    public String getAuthorizationId()
    {
        return authorizationId;
    }


    /**
     * Sets the Authorization ID
     *
     * @param authorizationId The authorization ID
     */
    public void setAuthorizationId( String authorizationId )
    {
        this.authorizationId = authorizationId;
    }


    /**
     * Sets the interned BindRequest
     *
     * @param bindRequest The interned BindRequest
     */
    public void setBindRequest( BindRequest bindRequest )
    {
        this.bindRequest = bindRequest;
    }


    /**
     * @return the credentials
     */
    public byte[] getCredentials()
    {
        byte[] credentials = bindRequest.getCredentials();

        if ( credentials != null )
        {
            return credentials;
        }
        else
        {
            return StringTools.EMPTY_BYTES;
        }
    }
}

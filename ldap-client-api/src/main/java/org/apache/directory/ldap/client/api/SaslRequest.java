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


/**
 * Holds the data required to complete the SASL operation
 *  
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SaslRequest
{
    /** the bind request */
    private BindRequest bindReq;

    /** the sasl mechaism's properties */
    private Map<String, String> saslMechProps = new HashMap<String, String>();

    /** SASL realm name on the server */
    private String realmName;

    /** the authorization ID of the entity */
    private String authorizationId;


    protected SaslRequest( BindRequest bindReq )
    {
        this.bindReq = bindReq;
    }


    public BindRequest getBindReq()
    {
        return bindReq;
    }


    public Map<String, String> getSaslMechProps()
    {
        return saslMechProps;
    }


    public void setSaslMechProps( Map<String, String> saslMechProps )
    {
        this.saslMechProps = saslMechProps;
    }


    public String getRealmName()
    {
        return realmName;
    }


    public void setRealmName( String realmName )
    {
        this.realmName = realmName;
    }


    public String getAuthorizationId()
    {
        return authorizationId;
    }


    public void setAuthorizationId( String authorizationId )
    {
        this.authorizationId = authorizationId;
    }


    public void setBindReq( BindRequest bindReq )
    {
        this.bindReq = bindReq;
    }

}

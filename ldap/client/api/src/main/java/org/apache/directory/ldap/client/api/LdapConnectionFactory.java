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


/**
 * A LdapConnection factory.
 *  
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class LdapConnectionFactory
{

    /**
     * Private constructor.
     *
     */
    private LdapConnectionFactory()
    {
    }


    /**
     * Gets the core session connection.
     *
     * @return a connection based on the the CoreSession
     */
    public static LdapConnection getCoreSessionConnection()
    {
        try
        {
            Class<?> cl = Class.forName( "org.apache.directory.server.core.LdapCoreSessionConnection" );
            return ( LdapConnection ) cl.newInstance();
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }


    /**
     * Gets the network connection.
     *
     * @param host the host
     * @param port the port
     * @return the LdapNetworkConnection
     */
    public static LdapAsyncConnection getNetworkConnection( String host, int port )
    {
        try
        {
            Class<?> cl = Class.forName( "org.apache.directory.ldap.client.api.LdapNetworkConnection" );

            LdapAsyncConnection networkConnection = ( LdapAsyncConnection ) cl.newInstance();
            networkConnection.getConfig().setLdapHost( host );
            networkConnection.getConfig().setLdapPort( port );

            return networkConnection;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }
}

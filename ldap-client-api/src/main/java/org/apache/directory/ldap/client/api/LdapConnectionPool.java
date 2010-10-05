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


import org.apache.commons.pool.impl.GenericObjectPool;


/**
 * A pool implementation for LdapConnection objects.
 * 
 * This class is just a wrapper around the commons GenericObjectPool, and has 
 * a more meaningful name to represent the pool type.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdapConnectionPool extends GenericObjectPool
{

    /**
     * Instantiates a new LDAP connection pool.
     *
     * @param factory the LDAP connection factory
     */
    public LdapConnectionPool( PoolableLdapConnectionFactory factory )
    {
        super( factory );
    }


    /**
     * Gives a LdapConnection fetched from the pool.
     *
     * @return an LdapConnection object from pool 
     * @throws Exception if an error occurs while obtaining a connection from the factory
     */
    public LdapNetworkConnection getConnection() throws Exception
    {
        return ( LdapNetworkConnection ) super.borrowObject();
    }


    /**
     * Places the given LdapConnection back in the pool.
     *  
     * @param connection the LdapConnection to be released
     * @throws Exception if an error occurs while releasing the connection
     */
    public void releaseConnection( LdapNetworkConnection connection ) throws Exception
    {
        super.returnObject( connection );
    }

}

/*
 * Licensed to the Apache Software Foundation (ASF) under one
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
package org.apache.directory.ldap.client.api.future;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.shared.ldap.message.ModifyDnResponse;


/**
 * A Future to manage ModifyDnRequest
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ModifyDnFuture extends ResponseFuture<ModifyDnResponse>
{
    /**
     * Creates a new instance of ModifyDnFuture.
     *
     * @param connection the LDAP connection
     * @param messageId The associated messageId
     */
    // Implicit super constructor ResponseFuture<BindResponse>() is undefined for default constructor. 
    @SuppressWarnings("PMD.UselessOverridingMethod")
    public ModifyDnFuture( LdapConnection connection, int messageId )
    {
        super( connection, messageId );
    }


    /**
     * Get the ModifyDnResponse, blocking until one is received.
     * 
     * @return the modify Dn response
     * @throws InterruptedException {@inheritDoc}
     * @throws ExecutionException {@inheritDoc}
     */
    @SuppressWarnings("PMD.UselessOverridingMethod")
    public ModifyDnResponse get() throws InterruptedException, ExecutionException
    {
        return super.get();
    }


    /**
     * Get the ModifyDnResponse, blocking until one is received, or until the
     * given timeout is reached.
     * 
     * @param timeout {@inheritDoc}
     * @param unit {@inheritDoc}
     * @return the modify Dn response
     * @throws InterruptedException {@inheritDoc}
     * @throws ExecutionException {@inheritDoc}
     * @throws TimeoutException {@inheritDoc}
     */
    public ModifyDnResponse get( long timeout, TimeUnit unit ) throws InterruptedException, ExecutionException,
        TimeoutException
    {
        return super.get( timeout, unit );
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append( "ModifyDnFuture" ).append( super.toString() );

        return sb.toString();
    }
}

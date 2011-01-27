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


import org.apache.directory.shared.ldap.model.message.LdapResult;
import org.apache.directory.shared.ldap.model.message.LdapResultImpl;
import org.apache.directory.shared.ldap.model.message.Message;
import org.apache.directory.shared.ldap.model.message.ResultResponse;


/**
 * A decorator for the Response message. It will store the LdapResult.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class ResponseDecorator extends MessageDecorator implements ResultResponse
{
    /** The LdapResult decorator */
    private LdapResultDecorator ldapResultDecorator = new LdapResultDecorator( new LdapResultImpl() );


    /**
     * Makes a AddRequest encodable.
     *
     * @param decoratedMessage the decorated AddRequest
     */
    public ResponseDecorator( Message decoratedMessage )
    {
        super( decoratedMessage );
    }


    /**
     * @return the ldapResultDecorator
     */
    public LdapResult getLdapResult()
    {
        return ldapResultDecorator;
    }


    /**
     * @return the ldapResultDecorator as a decorator to reduce casting.
     */
    public LdapResultDecorator getLdapResultDecorator()
    {
        return ldapResultDecorator;
    }


    /**
     * @param ldapResultDecorator the ldapResultDecorator to set
     */
    public void setLdapResultDecorator( LdapResultDecorator ldapResultDecorator )
    {
        this.ldapResultDecorator = ldapResultDecorator;
    }
}

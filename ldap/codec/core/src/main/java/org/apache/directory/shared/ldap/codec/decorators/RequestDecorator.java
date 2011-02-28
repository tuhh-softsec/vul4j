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


import org.apache.directory.shared.ldap.codec.api.LdapCodecService;
import org.apache.directory.shared.ldap.codec.api.MessageDecorator;
import org.apache.directory.shared.ldap.model.message.Request;


/**
 * A decorator for the LdapResultResponse message
 *
 * @TODO make this extend AbstractDsmlMessageDecorator instead of codec's 
 * MessageDecorator.
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class RequestDecorator<M extends Request> extends MessageDecorator<M> implements Request
{
    /**
     * Makes Request a MessageDecorator.
     *
     * @param decoratedMessage the decorated message
     */
    public RequestDecorator( LdapCodecService codec, M decoratedMessage )
    {
        super( codec, decoratedMessage );
    }
    

    /**
     * {@inheritDoc}
     */
    public boolean hasResponse()
    {
        return ( getDecorated() ).hasResponse();
    }
}

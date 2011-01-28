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


import org.apache.directory.shared.ldap.model.message.Message;
import org.apache.directory.shared.ldap.model.message.Request;
import org.apache.directory.shared.ldap.model.message.ResultResponse;
import org.apache.directory.shared.ldap.model.message.ResultResponseRequest;


/**
 * A decorator for the LdapResultResponse message
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbandonableResultResponseRequestDecorator extends AbandonableRequestDecorator implements ResultResponseRequest
{
    /**
     * Makes Request a MessageDecorator.
     *
     * @param decoratedMessage the decorated message
     */
    public AbandonableResultResponseRequestDecorator( Message decoratedMessage )
    {
        super( decoratedMessage );
    }
    

    /**
     * {@inheritDoc}
     */
    public boolean hasResponse()
    {
        return ( ( Request ) getDecoratedMessage() ).hasResponse();
    }


    /**
     * {@inheritDoc}
     */
    public ResultResponse getResultResponse()
    {
        return ( ( ResultResponseRequest ) getDecoratedMessage() ).getResultResponse();
    }
}

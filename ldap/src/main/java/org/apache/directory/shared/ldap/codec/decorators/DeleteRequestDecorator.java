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


import org.apache.directory.shared.ldap.model.message.AbandonListener;
import org.apache.directory.shared.ldap.model.message.DeleteRequest;
import org.apache.directory.shared.ldap.model.message.MessageTypeEnum;
import org.apache.directory.shared.ldap.model.message.ResultResponse;
import org.apache.directory.shared.ldap.model.name.Dn;


/**
 * A decorator for the DeleteRequest message
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DeleteRequestDecorator extends MessageDecorator implements DeleteRequest
{
    /**
     * Makes a DeleteRequest a MessageDecorator.
     *
     * @param decoratedMessage the decorated DeleteRequest
     */
    public DeleteRequestDecorator( DeleteRequest decoratedMessage )
    {
        super( decoratedMessage );
    }


    /**
     * @return The decorated DeleteRequest
     */
    public DeleteRequest getDeleteRequest()
    {
        return ( DeleteRequest ) getDecoratedMessage();
    }


    //-------------------------------------------------------------------------
    // The DeleteRequest methods
    //-------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public MessageTypeEnum getResponseType()
    {
        return getDeleteRequest().getResponseType();
    }


    /**
     * {@inheritDoc}
     */
    public ResultResponse getResultResponse()
    {
        return getDeleteRequest().getResultResponse();
    }


    /**
     * {@inheritDoc}
     */
    public boolean hasResponse()
    {
        return getDeleteRequest().hasResponse();
    }


    /**
     * {@inheritDoc}
     */
    public void abandon()
    {
        getDeleteRequest().abandon();
    }


    /**
     * {@inheritDoc}
     */
    public boolean isAbandoned()
    {
        return getDeleteRequest().isAbandoned();
    }


    /**
     * {@inheritDoc}
     */
    public void addAbandonListener( AbandonListener listener )
    {
        getDeleteRequest().addAbandonListener( listener );
    }


    /**
     * {@inheritDoc}
     */
    public Dn getName()
    {
        return getDeleteRequest().getName();
    }


    /**
     * {@inheritDoc}
     */
    public void setName( Dn name )
    {
        getDeleteRequest().setName( name );
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return getDeleteRequest().toString();
    }
}

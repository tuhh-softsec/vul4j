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

package org.apache.directory.shared.dsmlv2;


import java.util.Map;

import org.apache.directory.shared.ldap.model.message.*;
import org.apache.directory.shared.ldap.model.message.AbandonRequest;
import org.apache.directory.shared.ldap.model.message.AddRequest;
import org.apache.directory.shared.ldap.model.message.AddResponse;
import org.apache.directory.shared.ldap.model.message.BindRequest;
import org.apache.directory.shared.ldap.model.message.BindResponse;
import org.apache.directory.shared.ldap.model.message.CompareRequest;
import org.apache.directory.shared.ldap.model.message.CompareResponse;
import org.apache.directory.shared.ldap.model.message.DeleteRequest;
import org.apache.directory.shared.ldap.model.message.DeleteResponse;
import org.apache.directory.shared.ldap.model.message.ExtendedRequest;
import org.apache.directory.shared.ldap.model.message.ExtendedResponse;
import org.apache.directory.shared.ldap.model.message.ModifyDnRequest;
import org.apache.directory.shared.ldap.model.message.ModifyRequest;
import org.apache.directory.shared.ldap.model.message.ModifyResponse;
import org.apache.directory.shared.ldap.model.message.Response;
import org.apache.directory.shared.ldap.model.message.SearchRequest;
import org.apache.directory.shared.ldap.model.message.SearchResultDone;
import org.apache.directory.shared.ldap.model.message.SearchResultEntry;
import org.apache.directory.shared.ldap.model.message.SearchResultReference;
import org.apache.directory.shared.ldap.model.message.UnbindRequest;
import org.apache.directory.shared.ldap.model.message.Control;


/**
 * Decorator class for LDAP Message. This is the top level class, the one 
 * that holds the instance.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class LdapMessageDecorator implements Message
{
    /** The decorated instance */
    protected Message instance;


    /**
     * Creates a new instance of LdapMessageDecorator.
     *
     * @param ldapMessage the message to decorate
     */
    public LdapMessageDecorator( Message ldapMessage )
    {
        instance = ldapMessage;
    }


    /**
     * {@inheritDoc}
     */
    public void addControl( Control control )
    {
        instance.addControl( control );
    }


    /**
     * {@inheritDoc}
     */
    public AbandonRequest getAbandonRequest()
    {
        return ( AbandonRequest ) instance;
    }


    /**
     * {@inheritDoc}
     */
    public AddRequest getAddRequest()
    {
        return ( AddRequest ) instance;
    }


    /**
     * {@inheritDoc}
     */
    public AddResponse getAddResponse()
    {
        return ( AddResponse ) instance;
    }


    /**
     * {@inheritDoc}
     */
    public BindRequest getBindRequest()
    {
        return ( BindRequest ) instance;
    }


    /**
     * {@inheritDoc}
     */
    public BindResponse getBindResponse()
    {
        return ( BindResponse ) instance;
    }


    /**
     * {@inheritDoc}
     */
    public CompareRequest getCompareRequest()
    {
        return ( CompareRequest ) instance;
    }


    /**
     * {@inheritDoc}
     */
    public CompareResponse getCompareResponse()
    {
        return ( CompareResponse ) instance;
    }


    /**
     * {@inheritDoc}
     */
    public Map<String, Control> getControls()
    {
        return instance.getControls();
    }


    /**
     * {@inheritDoc}
     */
    public Control getControl( String oid )
    {
        return instance.getControl( oid );
    }


    /**
     * {@inheritDoc}
     */
    public DeleteRequest getDelRequest()
    {
        return ( DeleteRequest ) instance;
    }


    /**
     * {@inheritDoc}
     */
    public DeleteResponse getDelResponse()
    {
        return (DeleteResponse) instance;
    }


    /**
     * {@inheritDoc}
     */
    public ExtendedRequest getExtendedRequest()
    {
        return ( ExtendedRequest ) instance;
    }


    /**
     * {@inheritDoc}
     */
    public ExtendedResponse getExtendedResponse()
    {
        return ( ExtendedResponse ) instance;
    }


    /**
     * {@inheritDoc}
     */
    public Response getLdapResponse()
    {
        return ( Response ) instance;
    }


    /**
     * {@inheritDoc}
     */
    public int getMessageId()
    {
        return instance.getMessageId();
    }


    /**
     * {@inheritDoc}
     */
    public MessageTypeEnum getType()
    {
        return instance.getType();
    }


    /**
     * {@inheritDoc}
     */
    public ModifyDnRequest getModifyDNRequest()
    {
        return ( ModifyDnRequest ) instance;
    }


    /**
     * {@inheritDoc}
     */
    public ModifyDnResponse getModifyDNResponse()
    {
        return ( ModifyDnResponse ) instance;
    }


    /**
     * {@inheritDoc}
     */
    public ModifyRequest getModifyRequest()
    {
        return ( ModifyRequest ) instance;
    }


    /**
     * {@inheritDoc}
     */
    public ModifyResponse getModifyResponse()
    {
        return ( ModifyResponse ) instance;
    }


    /**
     * {@inheritDoc}
     */
    public SearchRequest getSearchRequest()
    {
        return ( SearchRequest ) instance;
    }


    /**
     * {@inheritDoc}
     */
    public SearchResultDone getSearchResultDone()
    {
        return ( SearchResultDone ) instance;
    }


    /**
     * {@inheritDoc}
     */
    public SearchResultEntry getSearchResultEntry()
    {
        return ( SearchResultEntry ) instance;
    }


    /**
     * {@inheritDoc}
     */
    public SearchResultReference getSearchResultReference()
    {
        return ( SearchResultReference ) instance;
    }


    /**
     * {@inheritDoc}
     */
    public UnbindRequest getUnBindRequest()
    {
        return ( UnbindRequest ) instance;
    }


    /**
     * {@inheritDoc}
     */
    public void setMessageId( int messageId )
    {
        instance.setMessageId( messageId );
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return instance.toString();
    }
}

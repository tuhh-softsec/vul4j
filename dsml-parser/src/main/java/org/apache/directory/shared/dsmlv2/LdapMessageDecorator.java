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

import org.apache.directory.shared.ldap.codec.MessageTypeEnum;
import org.apache.directory.shared.ldap.message.AbandonRequest;
import org.apache.directory.shared.ldap.message.AddRequest;
import org.apache.directory.shared.ldap.message.AddResponse;
import org.apache.directory.shared.ldap.message.BindRequest;
import org.apache.directory.shared.ldap.message.BindResponse;
import org.apache.directory.shared.ldap.message.CompareRequest;
import org.apache.directory.shared.ldap.message.CompareResponse;
import org.apache.directory.shared.ldap.message.DeleteRequest;
import org.apache.directory.shared.ldap.message.DeleteResponse;
import org.apache.directory.shared.ldap.message.ExtendedRequest;
import org.apache.directory.shared.ldap.message.ExtendedResponse;
import org.apache.directory.shared.ldap.message.Message;
import org.apache.directory.shared.ldap.message.ModifyDnRequest;
import org.apache.directory.shared.ldap.message.ModifyDnResponse;
import org.apache.directory.shared.ldap.message.ModifyRequest;
import org.apache.directory.shared.ldap.message.ModifyResponse;
import org.apache.directory.shared.ldap.message.Response;
import org.apache.directory.shared.ldap.message.SearchRequest;
import org.apache.directory.shared.ldap.message.SearchResultDone;
import org.apache.directory.shared.ldap.message.SearchResultEntry;
import org.apache.directory.shared.ldap.message.SearchResultReference;
import org.apache.directory.shared.ldap.message.UnbindRequest;
import org.apache.directory.shared.ldap.message.control.Control;


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


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#addControl(org.apache.directory.shared.ldap.codec.Control)
     */
    public void addControl( Control control )
    {
        instance.addControl( control );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getAbandonRequest()
     */
    public AbandonRequest getAbandonRequest()
    {
        return ( AbandonRequest ) instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getAddRequest()
     */
    public AddRequest getAddRequest()
    {
        return ( AddRequest ) instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getAddResponse()
     */
    public AddResponse getAddResponse()
    {
        return ( AddResponse ) instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getBindRequest()
     */
    public BindRequest getBindRequest()
    {
        return ( BindRequest ) instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getBindResponse()
     */
    public BindResponse getBindResponse()
    {
        return ( BindResponse ) instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getCompareRequest()
     */
    public CompareRequest getCompareRequest()
    {
        return ( CompareRequest ) instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getCompareResponse()
     */
    public CompareResponse getCompareResponse()
    {
        return ( CompareResponse ) instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getControls()
     */
    public Map<String, Control> getControls()
    {
        return instance.getControls();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getControls(int)
     */
    public Control getControl( String oid )
    {
        return instance.getControl( oid );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getDelRequest()
     */
    public DeleteRequest getDelRequest()
    {
        return ( DeleteRequest ) instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getDelResponse()
     */
    public DeleteResponse getDelResponse()
    {
        return ( DeleteResponse ) instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getExtendedRequest()
     */
    public ExtendedRequest getExtendedRequest()
    {
        return ( ExtendedRequest ) instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getExtendedResponse()
     */
    public ExtendedResponse getExtendedResponse()
    {
        return ( ExtendedResponse ) instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getLdapResponse()
     */
    public Response getLdapResponse()
    {
        return ( Response ) instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getMessageId()
     */
    public int getMessageId()
    {
        return instance.getMessageId();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getType()
     */
    public MessageTypeEnum getType()
    {
        return instance.getType();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getModifyDNRequest()
     */
    public ModifyDnRequest getModifyDNRequest()
    {
        return ( ModifyDnRequest ) instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getModifyDNResponse()
     */
    public ModifyDnResponse getModifyDNResponse()
    {
        return ( ModifyDnResponse ) instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getModifyRequest()
     */
    public ModifyRequest getModifyRequest()
    {
        return ( ModifyRequest ) instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getModifyResponse()
     */
    public ModifyResponse getModifyResponse()
    {
        return ( ModifyResponse ) instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getSearchRequest()
     */
    public SearchRequest getSearchRequest()
    {
        return ( SearchRequest ) instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getSearchResultDone()
     */
    public SearchResultDone getSearchResultDone()
    {
        return ( SearchResultDone ) instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getSearchResultEntry()
     */
    public SearchResultEntry getSearchResultEntry()
    {
        return ( SearchResultEntry ) instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getSearchResultReference()
     */
    public SearchResultReference getSearchResultReference()
    {
        return ( SearchResultReference ) instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getUnBindRequest()
     */
    public UnbindRequest getUnBindRequest()
    {
        return ( UnbindRequest ) instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#setMessageId(int)
     */
    public void setMessageId( int messageId )
    {
        instance.setMessageId( messageId );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#toString()
     */
    @Override
    public String toString()
    {
        return instance.toString();
    }
}

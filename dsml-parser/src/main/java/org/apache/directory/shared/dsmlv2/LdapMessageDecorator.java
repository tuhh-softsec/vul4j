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


import java.nio.ByteBuffer;
import java.util.List;

import org.apache.directory.shared.asn1.AbstractAsn1Object;
import org.apache.directory.shared.asn1.codec.DecoderException;
import org.apache.directory.shared.asn1.codec.EncoderException;
import org.apache.directory.shared.ldap.codec.LdapMessageCodec;
import org.apache.directory.shared.ldap.codec.LdapResponseCodec;
import org.apache.directory.shared.ldap.codec.MessageTypeEnum;
import org.apache.directory.shared.ldap.codec.abandon.AbandonRequestCodec;
import org.apache.directory.shared.ldap.codec.add.AddRequestCodec;
import org.apache.directory.shared.ldap.codec.add.AddResponseCodec;
import org.apache.directory.shared.ldap.codec.bind.BindRequestCodec;
import org.apache.directory.shared.ldap.codec.bind.BindResponseCodec;
import org.apache.directory.shared.ldap.codec.compare.CompareRequestCodec;
import org.apache.directory.shared.ldap.codec.compare.CompareResponseCodec;
import org.apache.directory.shared.ldap.codec.del.DelRequestCodec;
import org.apache.directory.shared.ldap.codec.del.DelResponseCodec;
import org.apache.directory.shared.ldap.codec.extended.ExtendedRequestCodec;
import org.apache.directory.shared.ldap.codec.extended.ExtendedResponseCodec;
import org.apache.directory.shared.ldap.codec.modify.ModifyRequestCodec;
import org.apache.directory.shared.ldap.codec.modify.ModifyResponseCodec;
import org.apache.directory.shared.ldap.codec.modifyDn.ModifyDNRequestCodec;
import org.apache.directory.shared.ldap.codec.modifyDn.ModifyDNResponseCodec;
import org.apache.directory.shared.ldap.codec.search.SearchRequestCodec;
import org.apache.directory.shared.ldap.codec.search.SearchResultDoneCodec;
import org.apache.directory.shared.ldap.codec.search.SearchResultEntryCodec;
import org.apache.directory.shared.ldap.codec.search.SearchResultReferenceCodec;
import org.apache.directory.shared.ldap.codec.unbind.UnBindRequestCodec;
import org.apache.directory.shared.ldap.message.control.Control;


/**
 * Decorator class for LDAP Message. This is the top level class, the one 
 * that holds the instance.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class LdapMessageDecorator extends LdapMessageCodec
{
    /** The decorated instance */
    protected LdapMessageCodec instance;


    /**
     * Creates a new instance of LdapMessageDecorator.
     *
     * @param ldapMessage
     *      the message to decorate
     */
    public LdapMessageDecorator( LdapMessageCodec ldapMessage )
    {
        instance = ldapMessage;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#addControl(org.apache.directory.shared.ldap.codec.Control)
     */
    @Override
    public void addControl( Control control )
    {
        instance.addControl( control );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#computeLength()
     */
    @Override
    public int computeLength()
    {
        return 0;
    }

    @Override
    public int computeLengthProtocolOp()
    {
        return 0;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#encode(java.nio.ByteBuffer)
     */
    @Override
    public ByteBuffer encode( ByteBuffer buffer ) throws EncoderException
    {
        return null;
    }


    @Override
    public void encodeProtocolOp( ByteBuffer buffer ) throws EncoderException
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getAbandonRequest()
     */
    public AbandonRequestCodec getAbandonRequest()
    {
        return (AbandonRequestCodec)instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getAddRequest()
     */
    public AddRequestCodec getAddRequest()
    {
        return (AddRequestCodec)instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getAddResponse()
     */
    public AddResponseCodec getAddResponse()
    {
        return (AddResponseCodec)instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getBindRequest()
     */
    public BindRequestCodec getBindRequest()
    {
        return (BindRequestCodec)instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getBindResponse()
     */
    public BindResponseCodec getBindResponse()
    {
        return (BindResponseCodec)instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getCompareRequest()
     */
    public CompareRequestCodec getCompareRequest()
    {
        return (CompareRequestCodec)instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getCompareResponse()
     */
    public CompareResponseCodec getCompareResponse()
    {
        return (CompareResponseCodec)instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getControls()
     */
    @Override
    public List<Control> getControls()
    {
        return instance.getControls();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getControls(int)
     */
    @Override
    public Control getControls( int i )
    {
        return instance.getControls( i );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getCurrentControl()
     */
    @Override
    public Control getCurrentControl()
    {
        return instance.getCurrentControl();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getDelRequest()
     */
    public DelRequestCodec getDelRequest()
    {
        return (DelRequestCodec)instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getDelResponse()
     */
    public DelResponseCodec getDelResponse()
    {
        return (DelResponseCodec)instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getExtendedRequest()
     */
    public ExtendedRequestCodec getExtendedRequest()
    {
        return (ExtendedRequestCodec)instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getExtendedResponse()
     */
    public ExtendedResponseCodec getExtendedResponse()
    {
        return (ExtendedResponseCodec)instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getLdapResponse()
     */
    public LdapResponseCodec getLdapResponse()
    {
        return (LdapResponseCodec)instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getMessageId()
     */
    @Override
    public int getMessageId()
    {
        return instance.getMessageId();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getMessageType()
     */
    @Override
    public MessageTypeEnum getMessageType()
    {
        return instance.getMessageType();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getMessageTypeName()
     */
    @Override
    public String getMessageTypeName()
    {
        return instance.getMessageTypeName();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getModifyDNRequest()
     */
    public ModifyDNRequestCodec getModifyDNRequest()
    {
        return (ModifyDNRequestCodec)instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getModifyDNResponse()
     */
    public ModifyDNResponseCodec getModifyDNResponse()
    {
        return (ModifyDNResponseCodec)instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getModifyRequest()
     */
    public ModifyRequestCodec getModifyRequest()
    {
        return (ModifyRequestCodec)instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getModifyResponse()
     */
    public ModifyResponseCodec getModifyResponse()
    {
        return (ModifyResponseCodec)instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getSearchRequest()
     */
    public SearchRequestCodec getSearchRequest()
    {
        return (SearchRequestCodec)instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getSearchResultDone()
     */
    public SearchResultDoneCodec getSearchResultDone()
    {
        return (SearchResultDoneCodec)instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getSearchResultEntry()
     */
    public SearchResultEntryCodec getSearchResultEntry()
    {
        return (SearchResultEntryCodec)instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getSearchResultReference()
     */
    public SearchResultReferenceCodec getSearchResultReference()
    {
        return (SearchResultReferenceCodec)instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#getUnBindRequest()
     */
    public UnBindRequestCodec getUnBindRequest()
    {
        return (UnBindRequestCodec)instance;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.codec.LdapMessageCodec#setMessageId(int)
     */
    @Override
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


    /* (non-Javadoc)
     * @see org.apache.directory.shared.asn1.Asn1Object#addLength(int)
     */
    @Override
    public void addLength( int length ) throws DecoderException
    {
        instance.addLength( length );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.asn1.Asn1Object#getCurrentLength()
     */
    @Override
    public int getCurrentLength()
    {
        return instance.getCurrentLength();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.asn1.Asn1Object#getExpectedLength()
     */
    @Override
    public int getExpectedLength()
    {
        return instance.getExpectedLength();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.asn1.Asn1Object#getParent()
     */
    @Override
    public AbstractAsn1Object getParent()
    {
        return instance.getParent();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.asn1.Asn1Object#setCurrentLength(int)
     */
    @Override
    public void setCurrentLength( int currentLength )
    {
        instance.setCurrentLength( currentLength );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.asn1.Asn1Object#setExpectedLength(int)
     */
    @Override
    public void setExpectedLength( int expectedLength )
    {
        instance.setExpectedLength( expectedLength );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.asn1.Asn1Object#setParent(org.apache.directory.shared.asn1.Asn1Object)
     */
    public void setParent( AbstractAsn1Object parent )
    {
        instance.setParent( parent );
    }
}

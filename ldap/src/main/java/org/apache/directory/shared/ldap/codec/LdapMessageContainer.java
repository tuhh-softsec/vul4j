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
package org.apache.directory.shared.ldap.codec;


import org.apache.directory.shared.asn1.ber.AbstractContainer;
import org.apache.directory.shared.ldap.codec.controls.AbstractControl;
import org.apache.directory.shared.ldap.message.internal.AddResponse;
import org.apache.directory.shared.ldap.message.internal.BindResponse;
import org.apache.directory.shared.ldap.message.internal.CompareResponse;
import org.apache.directory.shared.ldap.message.internal.DeleteResponse;
import org.apache.directory.shared.ldap.message.internal.ExtendedResponse;
import org.apache.directory.shared.ldap.message.internal.IntermediateResponse;
import org.apache.directory.shared.ldap.message.internal.AbandonRequest;
import org.apache.directory.shared.ldap.message.internal.AddRequest;
import org.apache.directory.shared.ldap.message.internal.BindRequest;
import org.apache.directory.shared.ldap.message.internal.CompareRequest;
import org.apache.directory.shared.ldap.message.internal.DeleteRequest;
import org.apache.directory.shared.ldap.message.internal.ExtendedRequest;
import org.apache.directory.shared.ldap.message.internal.Message;
import org.apache.directory.shared.ldap.message.internal.ModifyDnRequest;
import org.apache.directory.shared.ldap.message.internal.ModifyRequest;
import org.apache.directory.shared.ldap.message.internal.SearchRequest;
import org.apache.directory.shared.ldap.message.internal.UnbindRequest;
import org.apache.directory.shared.ldap.message.internal.ModifyDnResponse;
import org.apache.directory.shared.ldap.message.internal.ModifyResponse;
import org.apache.directory.shared.ldap.message.internal.SearchResultDone;
import org.apache.directory.shared.ldap.message.internal.SearchResultEntry;
import org.apache.directory.shared.ldap.message.internal.SearchResultReference;
import org.apache.directory.shared.ldap.message.spi.BinaryAttributeDetector;


/**
 * The LdapMessage container stores all the messages decoded by the Asn1Decoder.
 * When dealing whith an incoding PDU, we will obtain a LdapMessage in the
 * ILdapContainer.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdapMessageContainer extends AbstractContainer
{
    // ~ Instance fields
    // ----------------------------------------------------------------------------

    /** The internal ldap message */
    private Message internalMessage;

    /** checks if attribute is binary */
    private final BinaryAttributeDetector binaryAttributeDetector;

    /** The message ID */
    private int messageId;

    /** The current control */
    private AbstractControl currentControl;


    // ~ Constructors
    // -------------------------------------------------------------------------------

    /**
     * Creates a new LdapMessageContainer object. We will store ten grammars,
     * it's enough ...
     */
    public LdapMessageContainer()
    {
        this( new BinaryAttributeDetector()
        {
            public boolean isBinary( String attributeId )
            {
                return false;
            }
        } );
    }


    /**
     * Creates a new LdapMessageContainer object. We will store ten grammars,
     * it's enough ...
     *
     * @param binaryAttributeDetector checks if an attribute is binary
     */
    public LdapMessageContainer( BinaryAttributeDetector binaryAttributeDetector )
    {
        super();
        this.stateStack = new int[10];
        this.grammar = LdapMessageGrammar.getInstance();
        this.states = LdapStatesEnum.getInstance();
        this.binaryAttributeDetector = binaryAttributeDetector;
    }


    // ~ Methods
    // ------------------------------------------------------------------------------------
    /**
     * @return Returns the ldapMessage.
     */
    public Message getInternalMessage()
    {
        return internalMessage;
    }


    /**
     * @return Returns the AbandonRequest stored in the container
     */
    public AbandonRequest getAbandonRequest()
    {
        return ( AbandonRequest ) internalMessage;
    }


    /**
     * @return Returns the AddRequest stored in the container
     */
    public AddRequest getAddRequest()
    {
        return ( AddRequest ) internalMessage;
    }


    /**
     * @return Returns the AddResponse stored in the container
     */
    public AddResponse getAddResponse()
    {
        return ( AddResponse ) internalMessage;
    }


    /**
     * @return Returns the BindRequest stored in the container
     */
    public BindRequest getBindRequest()
    {
        return ( BindRequest ) internalMessage;
    }


    /**
     * @return Returns the BindResponse stored in the container
     */
    public BindResponse getBindResponse()
    {
        return ( BindResponse ) internalMessage;
    }


    /**
     * @return Returns the CompareRequest stored in the container
     */
    public CompareRequest getCompareRequest()
    {
        return ( CompareRequest ) internalMessage;
    }


    /**
     * @return Returns the CompareResponse stored in the container
     */
    public CompareResponse getCompareResponse()
    {
        return ( CompareResponse ) internalMessage;
    }


    /**
     * @return Returns the DelRequest stored in the container
     */
    public DeleteRequest getDeleteRequest()
    {
        return ( DeleteRequest ) internalMessage;
    }


    /**
     * @return Returns the DelResponse stored in the container
     */
    public DeleteResponse getDeleteResponse()
    {
        return ( DeleteResponse ) internalMessage;
    }


    /**
     * @return Returns the ExtendedRequest stored in the container
     */
    public ExtendedRequest getExtendedRequest()
    {
        return ( ExtendedRequest ) internalMessage;
    }


    /**
     * @return Returns the ExtendedResponse stored in the container
     */
    public ExtendedResponse getExtendedResponse()
    {
        return ( ExtendedResponse ) internalMessage;
    }


    /**
     * @return Returns the IntermediateResponse stored in the container
     */
    public IntermediateResponse getIntermediateResponse()
    {
        return ( IntermediateResponse ) internalMessage;
    }


    /**
     * @return Returns the ModifyRequest stored in the container
     */
    public ModifyRequest getModifyRequest()
    {
        return ( ModifyRequest ) internalMessage;
    }


    /**
     * @return Returns the ModifyResponse stored in the container
     */
    public ModifyResponse getModifyResponse()
    {
        return ( ModifyResponse ) internalMessage;
    }


    /**
     * @return Returns the ModifyDnRequest stored in the container
     */
    public ModifyDnRequest getModifyDnRequest()
    {
        return ( ModifyDnRequest ) internalMessage;
    }


    /**
     * @return Returns the ModifyDnResponse stored in the container
     */
    public ModifyDnResponse getModifyDnResponse()
    {
        return ( ModifyDnResponse ) internalMessage;
    }


    /**
     * @return Returns the SearchRequest stored in the container
     */
    public SearchRequest getSearchRequest()
    {
        return ( SearchRequest ) internalMessage;
    }


    /**
     * @return Returns the SearchResultEntry stored in the container
     */
    public SearchResultEntry getSearchResultEntry()
    {
        return ( SearchResultEntry ) internalMessage;
    }


    /**
     * @return Returns the SearchResultReference stored in the container
     */
    public SearchResultReference getSearchResultReference()
    {
        return ( SearchResultReference ) internalMessage;
    }


    /**
     * @return Returns the SearchResultDone stored in the container
     */
    public SearchResultDone getSearchResultDone()
    {
        return ( SearchResultDone ) internalMessage;
    }


    /**
     * @return Returns the UnbindRequest stored in the container
     */
    public UnbindRequest getUnbindRequest()
    {
        return ( UnbindRequest ) internalMessage;
    }


    /**
     * Set a InternalMessage Object into the container. It will be completed by the
     * ldapDecoder.
     * 
     * @param internalMessage The message to set.
     */
    public void setInternalMessage( Message internalMessage )
    {
        this.internalMessage = internalMessage;
    }


    public void clean()
    {
        super.clean();

        internalMessage = null;
        messageId = 0;
        currentControl = null;
        decodeBytes = 0;
    }


    /**
     * @return Returns true if the attribute is binary.
     * @param id checks if an attribute id is binary
     */
    public boolean isBinary( String id )
    {
        return binaryAttributeDetector.isBinary( id );
    }


    /**
     * @return The message ID
     */
    public int getMessageId()
    {
        return messageId;
    }


    /**
     * Set the message ID
     * @param messageId the id of the message
     */
    public void setMessageId( int messageId )
    {
        this.messageId = messageId;
    }


    /**
     * @return the current control being created
     */
    public AbstractControl getCurrentControl()
    {
        return currentControl;
    }


    /**
     * Store a newly created control
     * @param currentControl The control to store
     */
    public void setCurrentControl( AbstractControl currentControl )
    {
        this.currentControl = currentControl;
    }
}

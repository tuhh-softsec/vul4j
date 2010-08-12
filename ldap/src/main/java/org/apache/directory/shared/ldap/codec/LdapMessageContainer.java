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
import org.apache.directory.shared.ldap.codec.abandon.AbandonRequestCodec;
import org.apache.directory.shared.ldap.codec.add.AddRequestCodec;
import org.apache.directory.shared.ldap.codec.bind.BindRequestCodec;
import org.apache.directory.shared.ldap.codec.compare.CompareRequestCodec;
import org.apache.directory.shared.ldap.codec.controls.AbstractControl;
import org.apache.directory.shared.ldap.codec.del.DelRequestCodec;
import org.apache.directory.shared.ldap.codec.extended.ExtendedRequestCodec;
import org.apache.directory.shared.ldap.codec.intermediate.IntermediateResponseCodec;
import org.apache.directory.shared.ldap.codec.modify.ModifyRequestCodec;
import org.apache.directory.shared.ldap.codec.modifyDn.ModifyDNRequestCodec;
import org.apache.directory.shared.ldap.codec.modifyDn.ModifyDNResponseCodec;
import org.apache.directory.shared.ldap.codec.search.SearchRequestCodec;
import org.apache.directory.shared.ldap.codec.search.SearchResultDoneCodec;
import org.apache.directory.shared.ldap.codec.search.SearchResultEntryCodec;
import org.apache.directory.shared.ldap.codec.search.SearchResultReferenceCodec;
import org.apache.directory.shared.ldap.codec.unbind.UnBindRequestCodec;
import org.apache.directory.shared.ldap.message.internal.InternalAbandonRequest;
import org.apache.directory.shared.ldap.message.internal.InternalAddRequest;
import org.apache.directory.shared.ldap.message.internal.InternalAddResponse;
import org.apache.directory.shared.ldap.message.internal.InternalBindRequest;
import org.apache.directory.shared.ldap.message.internal.InternalBindResponse;
import org.apache.directory.shared.ldap.message.internal.InternalCompareRequest;
import org.apache.directory.shared.ldap.message.internal.InternalCompareResponse;
import org.apache.directory.shared.ldap.message.internal.InternalDeleteRequest;
import org.apache.directory.shared.ldap.message.internal.InternalDeleteResponse;
import org.apache.directory.shared.ldap.message.internal.InternalExtendedResponse;
import org.apache.directory.shared.ldap.message.internal.InternalMessage;
import org.apache.directory.shared.ldap.message.internal.InternalModifyResponse;
import org.apache.directory.shared.ldap.message.internal.InternalUnbindRequest;
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

    /** The ldap message */
    private LdapMessageCodec ldapMessage;

    /** The internal ldap message */
    private InternalMessage internalMessage;

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
    public LdapMessageCodec getLdapMessage()
    {
        return ldapMessage;
    }


    /**
     * @return Returns the ldapMessage.
     */
    public InternalMessage getInternalMessage()
    {
        return internalMessage;
    }


    /**
     * @return Returns the LdapResponse.
     */
    public LdapResponseCodec getLdapResponse()
    {
        return ( LdapResponseCodec ) ldapMessage;
    }


    /**
     * @return Returns the AbandonRequest stored in the container
     */
    public AbandonRequestCodec getAbandonRequest()
    {
        return ( AbandonRequestCodec ) ldapMessage;
    }


    /**
     * @return Returns the AbandonRequest stored in the container
     */
    public InternalAbandonRequest getInternalAbandonRequest()
    {
        return ( InternalAbandonRequest ) internalMessage;
    }


    /**
     * @return Returns the AddRequest stored in the container
     */
    public AddRequestCodec getAddRequest()
    {
        return ( AddRequestCodec ) ldapMessage;
    }


    /**
     * @return Returns the InternalAddRequest stored in the container
     */
    public InternalAddRequest getInternalAddRequest()
    {
        return ( InternalAddRequest ) internalMessage;
    }


    /**
     * @return Returns the AddResponse stored in the container
     */
    public InternalAddResponse getInternalAddResponse()
    {
        return ( InternalAddResponse ) internalMessage;
    }


    /**
     * @return Returns the BindRequest stored in the container
     */
    public BindRequestCodec getBindRequest()
    {
        return ( BindRequestCodec ) ldapMessage;
    }


    /**
     * @return Returns the BindRequest stored in the container
     */
    public InternalBindRequest getInternalBindRequest()
    {
        return ( InternalBindRequest ) internalMessage;
    }


    /**
     * @return Returns the BindResponse stored in the container
     */
    public InternalBindResponse getInternalBindResponse()
    {
        return ( InternalBindResponse ) internalMessage;
    }


    /**
     * @return Returns the CompareRequest stored in the container
     */
    public CompareRequestCodec getCompareRequest()
    {
        return ( CompareRequestCodec ) ldapMessage;
    }


    /**
     * @return Returns the CompareRequest stored in the container
     */
    public InternalCompareRequest getInternalCompareRequest()
    {
        return ( InternalCompareRequest ) internalMessage;
    }


    /**
     * @return Returns the CompareResponse stored in the container
     */
    public InternalCompareResponse getInternalCompareResponse()
    {
        return ( InternalCompareResponse ) internalMessage;
    }


    /**
     * @return Returns the DelRequest stored in the container
     */
    public DelRequestCodec getDelRequest()
    {
        return ( DelRequestCodec ) ldapMessage;
    }


    /**
     * @return Returns the InternalDeleleteRequest stored in the container
     */
    public InternalDeleteRequest getInternalDeleteRequest()
    {
        return ( InternalDeleteRequest ) internalMessage;
    }


    /**
     * @return Returns the DelResponse stored in the container
     */
    public InternalDeleteResponse getInternalDelResponse()
    {
        return ( InternalDeleteResponse ) internalMessage;
    }


    /**
     * @return Returns the ExtendedRequest stored in the container
     */
    public ExtendedRequestCodec getExtendedRequest()
    {
        return ( ExtendedRequestCodec ) ldapMessage;
    }


    /**
     * @return Returns the ExtendedResponse stored in the container
     */
    public InternalExtendedResponse getInternalExtendedResponse()
    {
        return ( InternalExtendedResponse ) internalMessage;
    }


    /**
     * @return Returns the IntermediateResponse stored in the container
     */
    public IntermediateResponseCodec getIntermediateResponse()
    {
        return ( IntermediateResponseCodec ) ldapMessage;
    }


    /**
     * @return Returns the ModifyRequest stored in the container
     */
    public ModifyRequestCodec getModifyRequest()
    {
        return ( ModifyRequestCodec ) ldapMessage;
    }


    /**
     * @return Returns the ModifyResponse stored in the container
     */
    public InternalModifyResponse getInternalModifyResponse()
    {
        return ( InternalModifyResponse ) internalMessage;
    }


    /**
     * @return Returns the ModifyDnRequest stored in the container
     */
    public ModifyDNRequestCodec getModifyDnRequest()
    {
        return ( ModifyDNRequestCodec ) ldapMessage;
    }


    /**
     * @return Returns the ModifyDnResponse stored in the container
     */
    public ModifyDNResponseCodec getModifyDnResponse()
    {
        return ( ModifyDNResponseCodec ) ldapMessage;
    }


    /**
     * @return Returns the SearchRequest stored in the container
     */
    public SearchRequestCodec getSearchRequest()
    {
        return ( SearchRequestCodec ) ldapMessage;
    }


    /**
     * @return Returns the SearchResultEntryCodec stored in the container
     */
    public SearchResultEntryCodec getSearchResultEntry()
    {
        return ( SearchResultEntryCodec ) ldapMessage;
    }


    /**
     * @return Returns the SearchResultReferenceCodec stored in the container
     */
    public SearchResultReferenceCodec getSearchResultReference()
    {
        return ( SearchResultReferenceCodec ) ldapMessage;
    }


    /**
     * @return Returns the SearchResultDone stored in the container
     */
    public SearchResultDoneCodec getSearchResultDone()
    {
        return ( SearchResultDoneCodec ) ldapMessage;
    }


    /**
     * @return Returns the UnbindRequest stored in the container
     */
    public UnBindRequestCodec getUnbindRequest()
    {
        return ( UnBindRequestCodec ) ldapMessage;
    }


    /**
     * @return Returns the UnbindRequest stored in the container
     */
    public InternalUnbindRequest getInternalUnbindRequest()
    {
        return ( InternalUnbindRequest ) internalMessage;
    }


    /**
     * Set a ldapMessage Object into the container. It will be completed by the
     * ldapDecoder .
     * 
     * @param ldapMessage The message to set.
     */
    public void setLdapMessage( LdapMessageCodec ldapMessage )
    {
        this.ldapMessage = ldapMessage;
    }


    /**
     * Set a InternalMessage Object into the container. It will be completed by the
     * ldapDecoder.
     * 
     * @param internalMessage The message to set.
     */
    public void setInternalMessage( InternalMessage internalMessage )
    {
        this.internalMessage = internalMessage;
    }


    public void clean()
    {
        super.clean();

        ldapMessage = null;
        internalMessage = null;
        messageId = 0;
        currentControl = null;
        decodeBytes = 0;
    }


    public boolean isInternal()
    {
        return internalMessage != null;
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

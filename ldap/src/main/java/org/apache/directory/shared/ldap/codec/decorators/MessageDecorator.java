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


import java.util.Map;

import org.apache.directory.shared.ldap.model.exception.MessageException;
import org.apache.directory.shared.ldap.model.message.AddRequest;
import org.apache.directory.shared.ldap.model.message.AddResponse;
import org.apache.directory.shared.ldap.model.message.BindRequest;
import org.apache.directory.shared.ldap.model.message.BindResponse;
import org.apache.directory.shared.ldap.model.message.CompareRequest;
import org.apache.directory.shared.ldap.model.message.CompareResponse;
import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.directory.shared.ldap.model.message.DeleteResponse;
import org.apache.directory.shared.ldap.model.message.ExtendedRequest;
import org.apache.directory.shared.ldap.model.message.ExtendedResponse;
import org.apache.directory.shared.ldap.model.message.IntermediateResponse;
import org.apache.directory.shared.ldap.model.message.Message;
import org.apache.directory.shared.ldap.model.message.MessageTypeEnum;
import org.apache.directory.shared.ldap.model.message.ModifyDnRequest;
import org.apache.directory.shared.ldap.model.message.ModifyDnResponse;
import org.apache.directory.shared.ldap.model.message.ModifyRequest;
import org.apache.directory.shared.ldap.model.message.ModifyResponse;
import org.apache.directory.shared.ldap.model.message.SearchRequest;
import org.apache.directory.shared.ldap.model.message.SearchResultDone;
import org.apache.directory.shared.ldap.model.message.SearchResultEntry;
import org.apache.directory.shared.ldap.model.message.SearchResultReference;


/**
 * A decorator for the generic LDAP Message
 *
 * @TODO make this class abstract, after finishing switch and all types and make default blow an EncoderException
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class MessageDecorator implements Message
{
    /** The decorated Control */
    private final Message decoratedMessage;

    /** The encoded Message length */
    protected int messageLength;

    /** The length of the controls */
    private int controlsLength;

    /** The current control */
    private Control currentControl;

    
    public static MessageDecorator getDecorator( Message decoratedMessage )
    {
        switch ( decoratedMessage.getType() )
        {
            case ABANDON_REQUEST:
                return new MessageDecorator( decoratedMessage );

            case ADD_REQUEST:
                return new AddRequestDecorator( ( AddRequest ) decoratedMessage );
                
            case ADD_RESPONSE:
                return new AddResponseDecorator( ( AddResponse ) decoratedMessage );
                
            case BIND_REQUEST:
                return new BindRequestDecorator( ( BindRequest ) decoratedMessage );
                
            case BIND_RESPONSE:
                return new BindResponseDecorator( ( BindResponse ) decoratedMessage );
                
            case COMPARE_REQUEST:
                return new CompareRequestDecorator( ( CompareRequest ) decoratedMessage );
                
            case COMPARE_RESPONSE:
                return new CompareResponseDecorator( ( CompareResponse ) decoratedMessage );
                
            case DEL_REQUEST:
                return new MessageDecorator( decoratedMessage );

            case DEL_RESPONSE:
                return new DeleteResponseDecorator( ( DeleteResponse ) decoratedMessage );
                
            case EXTENDED_REQUEST:
                return new ExtendedRequestDecorator( ( ExtendedRequest ) decoratedMessage );
                
            case EXTENDED_RESPONSE:
                return new ExtendedResponseDecorator( ( ExtendedResponse ) decoratedMessage );
                
            case INTERMEDIATE_RESPONSE:
                return new IntermediateResponseDecorator( ( IntermediateResponse ) decoratedMessage );
                
            case MODIFY_REQUEST:
                return new ModifyRequestDecorator( ( ModifyRequest ) decoratedMessage );
                
            case MODIFY_RESPONSE:
                return new ModifyResponseDecorator( ( ModifyResponse ) decoratedMessage );
                
            case MODIFYDN_REQUEST:
                return new ModifyDnRequestDecorator( ( ModifyDnRequest ) decoratedMessage );
                
            case MODIFYDN_RESPONSE:
                return new ModifyDnResponseDecorator( ( ModifyDnResponse ) decoratedMessage );
                
            case SEARCH_REQUEST:
                return new SearchRequestDecorator( ( SearchRequest ) decoratedMessage );
                
            case SEARCH_RESULT_DONE:
                return new SearchResultDoneDecorator( ( SearchResultDone ) decoratedMessage );
                
            case SEARCH_RESULT_ENTRY:
                return new SearchResultEntryDecorator( ( SearchResultEntry ) decoratedMessage );
                
            case SEARCH_RESULT_REFERENCE:
                return new SearchResultReferenceDecorator( ( SearchResultReference ) decoratedMessage );
            
            case UNBIND_REQUEST:
                return new MessageDecorator( decoratedMessage );
                
            default:
                return new MessageDecorator( decoratedMessage );
        }
    }


    /**
     * Makes a Message an Encodeable object.
     *
     * @TODO make me protected after making this class abstract
     */
    public MessageDecorator( Message decoratedMessage )
    {
        this.decoratedMessage = decoratedMessage;
    }


    /**
     * @return The decorated LDAP Message
     */
    public Message getDecoratedMessage()
    {
        return decoratedMessage;
    }


    /**
     * @param controlsLength the encoded controls length
     */
    public void setControlsLength( int controlsLength )
    {
        this.controlsLength = controlsLength;
    }


    /**
     * @return the encoded controls length
     */
    public int getControlsLength()
    {
        return controlsLength;
    }


    /**
     * @param messageLength The encoded message length
     */
    public void setMessageLength( int messageLength )
    {
        this.messageLength = messageLength;
    }


    /**
     * @return The encoded message length
     */
    public int getMessageLength()
    {
        return messageLength;
    }


    public MessageTypeEnum getType()
    {
        return decoratedMessage.getType();
    }


    public Map<String, Control> getControls()
    {
        return decoratedMessage.getControls();
    }


    public Control getControl( String oid )
    {
        return decoratedMessage.getControl( oid );
    }


    /**
     * Get the current Control Object
     * 
     * @return The current Control Object
     */
    public Control getCurrentControl()
    {
        return currentControl;
    }


    public boolean hasControl( String oid )
    {
        return decoratedMessage.hasControl( oid );
    }


    public void addControl( Control control ) throws MessageException
    {
        decoratedMessage.addControl( control );
        currentControl = control;
    }


    public void addAllControls( Control[] controls ) throws MessageException
    {
        decoratedMessage.addAllControls( controls );
    }


    public void removeControl( Control control ) throws MessageException
    {
        decoratedMessage.removeControl( control );
    }


    public int getMessageId()
    {
        return decoratedMessage.getMessageId();
    }


    public Object get( Object key )
    {
        return decoratedMessage.get( key );
    }


    public Object put( Object key, Object value )
    {
        return decoratedMessage.put( key, value );
    }


    public void setMessageId( int messageId )
    {
        decoratedMessage.setMessageId( messageId );
    }


    public String toString()
    {
        return decoratedMessage.toString();
    }
}

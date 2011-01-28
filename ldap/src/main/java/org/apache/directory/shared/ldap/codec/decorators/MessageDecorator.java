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


import java.util.HashMap;
import java.util.Map;

import org.apache.directory.shared.ldap.codec.controls.ControlDecorator;
import org.apache.directory.shared.ldap.model.exception.MessageException;
import org.apache.directory.shared.ldap.model.message.AbandonRequest;
import org.apache.directory.shared.ldap.model.message.AddRequest;
import org.apache.directory.shared.ldap.model.message.AddResponse;
import org.apache.directory.shared.ldap.model.message.BindRequest;
import org.apache.directory.shared.ldap.model.message.BindResponse;
import org.apache.directory.shared.ldap.model.message.CompareRequest;
import org.apache.directory.shared.ldap.model.message.CompareResponse;
import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.directory.shared.ldap.model.message.DeleteRequest;
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
import org.apache.directory.shared.ldap.model.message.UnbindRequest;


/**
 * A decorator for the generic LDAP Message
 *
 * @TODO make this class abstract, after finishing switch and all types and make default blow an EncoderException
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class MessageDecorator implements Message, Decorator
{
    /** The decorated Control */
    private final Message decoratedMessage;

    /** Map of message controls using OID Strings for keys and Control values */
    private final Map<String, Control> controls;

    /** The encoded Message length */
    protected int messageLength;

    /** The length of the controls */
    private int controlsLength;

    /** The current control */
    private Control currentControl;

    
    public static MessageDecorator getDecorator( Message decoratedMessage )
    {
        if ( decoratedMessage instanceof MessageDecorator )
        {
            return (MessageDecorator)decoratedMessage;
        }
        
        MessageDecorator decorator = null;
        
        switch ( decoratedMessage.getType() )
        {
            case ABANDON_REQUEST:
                decorator = new AbandonRequestDecorator( ( AbandonRequest ) decoratedMessage );
                break;

            case ADD_REQUEST:
                decorator = new AddRequestDecorator( ( AddRequest ) decoratedMessage );
                break;
                
            case ADD_RESPONSE:
                decorator = new AddResponseDecorator( ( AddResponse ) decoratedMessage );
                break;
                
            case BIND_REQUEST:
                decorator = new BindRequestDecorator( ( BindRequest ) decoratedMessage );
                break;
                
            case BIND_RESPONSE:
                decorator = new BindResponseDecorator( ( BindResponse ) decoratedMessage );
                break;
                
            case COMPARE_REQUEST:
                decorator = new CompareRequestDecorator( ( CompareRequest ) decoratedMessage );
                break;
                
            case COMPARE_RESPONSE:
                decorator = new CompareResponseDecorator( ( CompareResponse ) decoratedMessage );
                break;
                
            case DEL_REQUEST:
                decorator = new DeleteRequestDecorator( ( DeleteRequest ) decoratedMessage );
                break;

            case DEL_RESPONSE:
                decorator = new DeleteResponseDecorator( ( DeleteResponse ) decoratedMessage );
                break;
                
            case EXTENDED_REQUEST:
                decorator = new ExtendedRequestDecorator( ( ExtendedRequest ) decoratedMessage );
                break;
                
            case EXTENDED_RESPONSE:
                decorator = new ExtendedResponseDecorator( ( ExtendedResponse ) decoratedMessage );
                break;
                
            case INTERMEDIATE_RESPONSE:
                decorator = new IntermediateResponseDecorator( ( IntermediateResponse ) decoratedMessage );
                break;
                
            case MODIFY_REQUEST:
                decorator = new ModifyRequestDecorator( ( ModifyRequest ) decoratedMessage );
                break;
                
            case MODIFY_RESPONSE:
                decorator = new ModifyResponseDecorator( ( ModifyResponse ) decoratedMessage );
                break;
                
            case MODIFYDN_REQUEST:
                decorator = new ModifyDnRequestDecorator( ( ModifyDnRequest ) decoratedMessage );
                break;
                
            case MODIFYDN_RESPONSE:
                decorator = new ModifyDnResponseDecorator( ( ModifyDnResponse ) decoratedMessage );
                break;
                
            case SEARCH_REQUEST:
                decorator = new SearchRequestDecorator( ( SearchRequest ) decoratedMessage );
                break;
                
            case SEARCH_RESULT_DONE:
                decorator = new SearchResultDoneDecorator( ( SearchResultDone ) decoratedMessage );
                break;
                
            case SEARCH_RESULT_ENTRY:
                decorator = new SearchResultEntryDecorator( ( SearchResultEntry ) decoratedMessage );
                break;
                
            case SEARCH_RESULT_REFERENCE:
                decorator = new SearchResultReferenceDecorator( ( SearchResultReference ) decoratedMessage );
                break;
            
            case UNBIND_REQUEST:
                decorator = new UnbindRequestDecorator( ( UnbindRequest ) decoratedMessage );
                break;
                
            default :
                return null;
        }
        
        Map<String, Control> controls = decoratedMessage.getControls();
        
        if ( controls != null )
        {
            for ( Control control : controls.values() )
            {
                Control controlDecorator = ControlDecorator.getDecorator( control );
                
                decorator.addControl( controlDecorator );
            }
        }
        
        return decorator;
    }


    /**
     * Makes a Message an Decorator object.
     */
    protected MessageDecorator( Message decoratedMessage )
    {
        this.decoratedMessage = decoratedMessage;
        controls = new HashMap<String, Control>();
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


    /**
     * Get the current Control Object
     * 
     * @return The current Control Object
     */
    public Control getCurrentControl()
    {
        return currentControl;
    }

    
    //-------------------------------------------------------------------------
    // The Message methods
    //-------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public MessageTypeEnum getType()
    {
        return decoratedMessage.getType();
    }


    /**
     * {@inheritDoc}
     */
    public Map<String, Control> getControls()
    {
        return controls;
    }


    /**
     * {@inheritDoc}
     */
    public Control getControl( String oid )
    {
        return controls.get( oid );
    }


    /**
     * {@inheritDoc}
     */
    public boolean hasControl( String oid )
    {
        return controls.containsKey( oid );
    }


    /**
     * {@inheritDoc}
     */
    public void addControl( Control control ) throws MessageException
    {
        ControlDecorator controlDecorator = (ControlDecorator)control;
        Control decoratedControl = controlDecorator.getDecorated();
        decoratedMessage.addControl( decoratedControl );
        controls.put( control.getOid(), control );
        currentControl = control;
    }


    /**
     * {@inheritDoc}
     */
    public void addAllControls( Control[] controls ) throws MessageException
    {
        for ( Control control : controls )
        {
            decoratedMessage.addControl( ((ControlDecorator)control).getDecorated() );
            this.controls.put( control.getOid(), control );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void removeControl( Control control ) throws MessageException
    {
        decoratedMessage.removeControl( control );
        controls.remove( control.getOid() );
    }


    /**
     * {@inheritDoc}
     */
    public int getMessageId()
    {
        return decoratedMessage.getMessageId();
    }


    /**
     * {@inheritDoc}
     */
    public Object get( Object key )
    {
        return decoratedMessage.get( key );
    }


    /**
     * {@inheritDoc}
     */
    public Object put( Object key, Object value )
    {
        return decoratedMessage.put( key, value );
    }


    /**
     * {@inheritDoc}
     */
    public void setMessageId( int messageId )
    {
        decoratedMessage.setMessageId( messageId );
    }


    /**
     * Delegates to the toString() method of the decorated Message.
     */
    public String toString()
    {
        return decoratedMessage.toString();
    }
}

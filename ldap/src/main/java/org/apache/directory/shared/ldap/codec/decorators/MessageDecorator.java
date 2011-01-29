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


import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.ldap.codec.ICodecControl;
import org.apache.directory.shared.ldap.codec.IDecorator;
import org.apache.directory.shared.ldap.codec.ILdapCodecService;
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
public abstract class MessageDecorator<E extends Message> implements Message, IDecorator<E>
{
    /** The decorated Control */
    private final E decoratedMessage;

    /** Map of message controls using OID Strings for keys and Control values */
    private final Map<String, Control> controls;

    /** The encoded Message length */
    protected int messageLength;

    /** The length of the controls */
    private int controlsLength;

    /** The current control */
    private ICodecControl<? extends Control> currentControl;
    
    /** The LdapCodecService */
    private final ILdapCodecService codec;

    
    public static MessageDecorator<? extends Message> getDecorator( ILdapCodecService codec, Message decoratedMessage )
    {
        if ( decoratedMessage instanceof MessageDecorator )
        {
            return ( MessageDecorator<?> ) decoratedMessage;
        }
        
        MessageDecorator<?> decorator = null;
        
        switch ( decoratedMessage.getType() )
        {
            case ABANDON_REQUEST:
                decorator = new AbandonRequestDecorator( codec, ( AbandonRequest ) decoratedMessage );
                break;

            case ADD_REQUEST:
                decorator = new AddRequestDecorator( codec, ( AddRequest ) decoratedMessage );
                break;
                
            case ADD_RESPONSE:
                decorator = new AddResponseDecorator( codec, ( AddResponse ) decoratedMessage );
                break;
                
            case BIND_REQUEST:
                decorator = new BindRequestDecorator( codec, ( BindRequest ) decoratedMessage );
                break;
                
            case BIND_RESPONSE:
                decorator = new BindResponseDecorator( codec, ( BindResponse ) decoratedMessage );
                break;
                
            case COMPARE_REQUEST:
                decorator = new CompareRequestDecorator( codec, ( CompareRequest ) decoratedMessage );
                break;
                
            case COMPARE_RESPONSE:
                decorator = new CompareResponseDecorator( codec, ( CompareResponse ) decoratedMessage );
                break;
                
            case DEL_REQUEST:
                decorator = new DeleteRequestDecorator( codec, ( DeleteRequest ) decoratedMessage );
                break;

            case DEL_RESPONSE:
                decorator = new DeleteResponseDecorator( codec, ( DeleteResponse ) decoratedMessage );
                break;
                
            case EXTENDED_REQUEST:
                decorator = new ExtendedRequestDecorator( codec, ( ExtendedRequest ) decoratedMessage );
                break;
                
            case EXTENDED_RESPONSE:
                decorator = new ExtendedResponseDecorator( codec, ( ExtendedResponse ) decoratedMessage );
                break;
                
            case INTERMEDIATE_RESPONSE:
                decorator = new IntermediateResponseDecorator( codec, ( IntermediateResponse ) decoratedMessage );
                break;
                
            case MODIFY_REQUEST:
                decorator = new ModifyRequestDecorator( codec, ( ModifyRequest ) decoratedMessage );
                break;
                
            case MODIFY_RESPONSE:
                decorator = new ModifyResponseDecorator( codec, ( ModifyResponse ) decoratedMessage );
                break;
                
            case MODIFYDN_REQUEST:
                decorator = new ModifyDnRequestDecorator( codec, ( ModifyDnRequest ) decoratedMessage );
                break;
                
            case MODIFYDN_RESPONSE:
                decorator = new ModifyDnResponseDecorator( codec, ( ModifyDnResponse ) decoratedMessage );
                break;
                
            case SEARCH_REQUEST:
                decorator = new SearchRequestDecorator( codec, ( SearchRequest ) decoratedMessage );
                break;
                
            case SEARCH_RESULT_DONE:
                decorator = new SearchResultDoneDecorator( codec, ( SearchResultDone ) decoratedMessage );
                break;
                
            case SEARCH_RESULT_ENTRY:
                decorator = new SearchResultEntryDecorator( codec, ( SearchResultEntry ) decoratedMessage );
                break;
                
            case SEARCH_RESULT_REFERENCE:
                decorator = new SearchResultReferenceDecorator( codec, ( SearchResultReference ) decoratedMessage );
                break;
            
            case UNBIND_REQUEST:
                decorator = new UnbindRequestDecorator( codec, ( UnbindRequest ) decoratedMessage );
                break;
                
            default :
                return null;
        }
        
        Map<String, Control> controls = decoratedMessage.getControls();
        
        if ( controls != null )
        {
            for ( Control control : controls.values() )
            {
                decorator.addControl( control );
            }
        }
        
        return decorator;
    }


    /**
     * Makes a Message an Decorator object.
     */
    protected MessageDecorator( ILdapCodecService codec, E decoratedMessage )
    {
        this.codec = codec;
        this.decoratedMessage = decoratedMessage;
        controls = new HashMap<String, Control>();
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
    public ICodecControl<? extends Control> getCurrentControl()
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
    @SuppressWarnings("unchecked")
    public void addControl( Control control ) throws MessageException
    {
        Control decorated;
        ICodecControl<? extends Control> controlDecorator;
        
        if ( control instanceof ControlDecorator )
        {
            controlDecorator = ( ICodecControl<? extends Control> ) control;
            decorated = controlDecorator.getDecorated();
        }
        else
        {
            controlDecorator = codec.decorate( control );
            decorated = control;
        }
        
        decoratedMessage.addControl( decorated );
        controls.put( control.getOid(), controlDecorator );
        currentControl = controlDecorator;
    }


    /**
     * {@inheritDoc}
     */
    public void addAllControls( Control[] controls ) throws MessageException
    {
        for ( Control control : controls )
        {
            addControl( control );
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


    public E getDecorated()
    {
        return decoratedMessage;
    }


    public int computeLength()
    {
        return 0;
    }


    public ByteBuffer encode( ByteBuffer buffer ) throws EncoderException
    {
        return null;
    }
}

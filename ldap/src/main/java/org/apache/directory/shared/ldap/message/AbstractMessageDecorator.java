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
package org.apache.directory.shared.ldap.message;


import org.apache.directory.shared.ldap.model.exception.MessageException;
import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.directory.shared.ldap.model.message.Message;
import org.apache.directory.shared.ldap.model.message.MessageTypeEnum;

import java.util.Iterator;
import java.util.Map;


/**
 * Abstract message decorator base class.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractMessageDecorator implements Message
{
    static final long serialVersionUID = 7601738291101182094L;


    /** The Message decorated by this decorator */
    private final Message decoratedMessage;


    /** The encoded controls length */
    private int controlsLength;

    /** The encoded message length */
    private int messageLength;


    /**
     * Completes the instantiation of a Message.
     *
     * @param decoratedMessage the message to be decorated.
     */
    protected AbstractMessageDecorator( Message decoratedMessage )
    {
        this.decoratedMessage = decoratedMessage;
    }


    /**
     * Gets the session unique message sequence id for this message. Requests
     * and their responses if any have the same message id. Clients at the
     * initialization of a session start with the first message's id set to 1
     * and increment it with each transaction.
     *
     * @return the session unique message id.
     */
    public int getMessageId()
    {
        return decoratedMessage.getMessageId();
    }


    public void setMessageId( int id )
    {
        decoratedMessage.setMessageId( id );
    }


    /**
     * {@inheritDoc}
     */
    public Map<String, Control> getControls()
    {
        return decoratedMessage.getControls();
    }


    /**
     * {@inheritDoc}
     */
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
        return decoratedMessage.getCurrentControl();
    }


    /**
     * {@inheritDoc}
     */
    public boolean hasControl( String oid )
    {
        return decoratedMessage.hasControl( oid );
    }


    /**
     * {@inheritDoc}
     */
    public void addControl( Control control ) throws MessageException
    {
        decoratedMessage.addControl( control );
    }


    /**
     * Deletes a control removing it from this Message.
     *
     * @param control the control to remove.
     * @throws org.apache.directory.shared.ldap.model.exception.MessageException if controls cannot be added to this Message or the control is
     *             not known etc.
     */
    public void removeControl( Control control ) throws MessageException
    {
        decoratedMessage.removeControl( control );
    }


    /**
     * Gets the LDAP message type code associated with this Message. Each
     * request and response type has a unique message type code defined by the
     * protocol in <a href="http://www.faqs.org/rfcs/rfc2251.html">RFC 2251</a>.
     * 
     * @return the message type code.
     */
    public MessageTypeEnum getType()
    {
        return decoratedMessage.getType();
    }


    /**
     * Gets a message scope parameter. Message scope parameters are temporary
     * variables associated with a message and are set locally to be used to
     * associate housekeeping information with a request or its processing.
     * These parameters are never transmitted nor recieved, think of them as
     * transient data associated with the message or its processing. These
     * transient parameters are not locked down so modifications can occur
     * without firing LockExceptions even when this Lockable is in the locked
     * state.
     * 
     * @param key the key used to access a message parameter.
     * @return the transient message parameter value.
     */
    public Object get( Object key )
    {
        return decoratedMessage.get( key );
    }


    /**
     * Sets a message scope parameter. These transient parameters are not locked
     * down so modifications can occur without firing LockExceptions even when
     * this Lockable is in the locked state.
     * 
     * @param key the parameter key
     * @param value the parameter value
     * @return the old value or null
     */
    public Object put( Object key, Object value )
    {
        return decoratedMessage.put( key, value );
    }


    /**
     * Checks to see if two messages are equivalent. Messages equivalence does
     * not factor in parameters accessible through the get() and put()
     * operations, nor do they factor in the Lockable properties of the Message.
     * Only the type, controls, and the messageId are evaluated for equality.
     * 
     * @param obj the object to compare this Message to for equality
     */
    public boolean equals( Object obj )
    {
        if ( obj == this )
        {
            return true;
        }

        if ( ( obj == null ) || !( obj instanceof Message ) )
        {
            return false;
        }

        Message msg = ( Message ) obj;

        if ( msg.getMessageId() != decoratedMessage.getMessageId() )
        {
            return false;
        }

        if ( msg.getType() != decoratedMessage.getType() )
        {
            return false;
        }

        Map<String, Control> controls = msg.getControls();

        if ( controls.size() != decoratedMessage.getControls().size() )
        {
            return false;
        }

        Iterator<String> list = decoratedMessage.getControls().keySet().iterator();

        while ( list.hasNext() )
        {
            if ( ! controls.containsKey( list.next() ) )
            {
                return false;
            }
        }

        return true;
    }


    /**
     * @see Object#hashCode()
     * @return the instance's hash code 
     */
    public int hashCode()
    {
        int hash = 37;
        hash = hash * 17 + decoratedMessage.getMessageId();
        hash = hash * 17 + ( decoratedMessage.getType() == null ? 0 : decoratedMessage.getType().hashCode() );
        hash = hash * 17 + ( decoratedMessage.getControls() == null ? 0 : decoratedMessage.getControls().hashCode() );

        return hash;
    }


    /**
     * {@inheritDoc}
     */
    public void addAllControls( Control[] controls ) throws MessageException
    {
        decoratedMessage.addAllControls( controls );
    }


    /**
     * Get a String representation of a LdapMessage
     * 
     * @return A LdapMessage String
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        if ( decoratedMessage.getControls() != null )
        {
            for ( Control control : decoratedMessage.getControls().values() )
            {
                sb.append( control );
            }
        }

        return sb.toString();
    }


    /**
     * {@inheritDoc}
     */
    public void setControlsLength( int controlsLength )
    {
        this.controlsLength = controlsLength;
    }


    /**
     * {@inheritDoc}
     */
    public int getControlsLength()
    {
        return controlsLength;
    }


    /**
     * {@inheritDoc}
     */
    public void setMessageLength( int messageLength )
    {
        this.messageLength = messageLength;
    }


    /**
     * {@inheritDoc}
     */
    public int getMessageLength()
    {
        return messageLength;
    }
}

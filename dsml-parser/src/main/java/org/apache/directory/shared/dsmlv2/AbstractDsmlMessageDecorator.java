/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.shared.dsmlv2;


import java.util.Map;

import org.apache.directory.shared.ldap.codec.LdapCodecService;
import org.apache.directory.shared.ldap.model.exception.MessageException;
import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.directory.shared.ldap.model.message.Message;
import org.apache.directory.shared.ldap.model.message.MessageTypeEnum;


/**
 * An abstract DSML Message decorator base class.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractDsmlMessageDecorator<E extends Message> 
    implements DsmlDecorator<E>, Message
{
    /** The LDAP message codec */
    private final LdapCodecService codec;
    /** The LDAP message */
    private final E message;
    
    
    public AbstractDsmlMessageDecorator( LdapCodecService codec, E message )
    {
        this.codec = codec;
        this.message = message;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public LdapCodecService getCodecService()
    {
        return codec;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public MessageTypeEnum getType()
    {
        return message.getType();
    }

    
    /**
     * {@inheritDoc}
     */
    public Map<String, Control> getControls()
    {
        return message.getControls();
    }

    
    /**
     * {@inheritDoc}
     */
    public Control getControl( String oid )
    {
        return message.getControl( oid );
    }

    
    /**
     * {@inheritDoc}
     */
    public boolean hasControl( String oid )
    {
        return message.hasControl( oid );
    }

    
    /**
     * {@inheritDoc}
     */
    public void addControl( Control control ) throws MessageException
    {
        message.addControl( control );
    }

    
    /**
     * {@inheritDoc}
     */
    public void addAllControls( Control[] controls ) throws MessageException
    {
        message.addAllControls( controls );
    }

    
    /**
     * {@inheritDoc}
     */
    public void removeControl( Control control ) throws MessageException
    {
        message.removeControl( control );
    }

    
    /**
     * {@inheritDoc}
     */
    public int getMessageId()
    {
        return message.getMessageId();
    }

    
    /**
     * {@inheritDoc}
     */
    public Object get( Object key )
    {
        return message.get( key );
    }

    
    /**
     * {@inheritDoc}
     */
    public Object put( Object key, Object value )
    {
        return message.put( key, value );
    }

    
    /**
     * {@inheritDoc}
     */
    public void setMessageId( int messageId )
    {
        message.setMessageId( messageId );
    }

    
    /**
     * {@inheritDoc}
     */
    public E getDecorated()
    {
        return message;
    }
}

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


import org.apache.directory.shared.ldap.codec.LdapCodecService;
import org.apache.directory.shared.ldap.model.message.Control;
import org.dom4j.Element;


/**
 * A DSML decorator for a {@link Control}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DsmlControl<E extends Control> 
    implements Control, DsmlDecorator<E>
{
    /** The decorated Control */
    private E decorated;

    /** The encoded value of the control. */
    protected byte[] value;
    
    /** The codec service responsible for encoding decoding this object */
    private LdapCodecService codec;
    
    
    public DsmlControl( LdapCodecService codec, E decorated )
    {
        this.codec = codec;
        this.decorated = decorated;
    }
    
    
    /**
     * Gets the LDAP codec service.
     */
    public LdapCodecService getCodecService()
    {
        return codec;
    }


    /**
     * Checks to see if this DSML control decorator has a value.
     *
     * @return true if the DSML control has a value, false otherwise.
     */
    public boolean hasValue()
    {
        return value != null;
    }


    /**
     * Get the control value
     * 
     * @return The control value
     */
    public byte[] getValue()
    {
        return value;
    }


    /**
     * Set the encoded control value
     * 
     * @param value The encoded control value to store
     */
    public void setValue( byte[] value )
    {
        if ( value != null )
        {
            byte[] copy = new byte[ value.length ];
            System.arraycopy( value, 0, copy, 0, value.length );
            this.value = copy;
        } 
        else 
        {
            this.value = null;
        }
    }

    
    /**
     * {@inheritDoc}
     */
    public String getOid()
    {
        return decorated.getOid();
    }

    
    /**
     * {@inheritDoc}
     */
    public boolean isCritical()
    {
        return decorated.isCritical();
    }


    /**
     * {@inheritDoc}
     */
    public void setCritical( boolean isCritical )
    {
        decorated.setCritical( isCritical );
    }


    /**
     * {@inheritDoc}
     */
    public Element toDsml( Element root )
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public E getDecorated()
    {
        return decorated;
    }
}

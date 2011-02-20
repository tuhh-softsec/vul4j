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
package org.apache.directory.shared.ldap.codec.api;


import org.apache.directory.shared.asn1.AbstractAsn1Object;
import org.apache.directory.shared.ldap.model.message.Control;


/**
 * Decorates Control objects by wrapping them, and enabling them as CodecControls
 * so the codec to store transient information associated with the Control in the
 * decorator while processing.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @param <E>
 */
public abstract class ControlDecorator<E extends Control> extends AbstractAsn1Object implements CodecControl<E>
{
    /** The decorated Control */
    private E decorated;

    /** The encoded value length */
    protected int valueLength;

    /** The encoded value of the control. */
    protected byte[] value;
    
    /** The codec service responsible for encoding decoding this object */
    private LdapCodecService codec;

    
    /**
     * Creates a ControlDecorator to codec enable it.
     *
     * @param decoratedControl The Control to decorate.
     */
    public ControlDecorator( LdapCodecService codec, E decoratedControl )
    {
        this.decorated = decoratedControl;
        this.codec = codec;
    }


    /**
     * {@inheritDoc}
     */
    public E getDecorated()
    {
        return decorated;
    }

    
    /**
     * {@inheritDoc}
     */
    public void setDecorated( E decorated )
    {
        this.decorated = decorated;
    }

    
    /**
     * {@inheritDoc}
     */
    public LdapCodecService getCodecService()
    {
        return codec;
    }
    

    // ------------------------------------------------------------------------
    // Control Methods
    // ------------------------------------------------------------------------
    
    
    /**
     * Get the OID
     * 
     * @return A string which represent the control oid
     */
    public String getOid()
    {
        return decorated.getOid();
    }


    /**
     * {@inheritDoc}
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
     * Get the criticality
     * 
     * @return <code>true</code> if the criticality flag is true.
     */
    public boolean isCritical()
    {
        return decorated.isCritical();
    }


    /**
     * Set the criticality
     * 
     * @param criticality The criticality value
     */
    public void setCritical( boolean criticality )
    {
        decorated.setCritical( criticality );
    }

    
    // ------------------------------------------------------------------------
    // CodecControl Methods
    // ------------------------------------------------------------------------
    
    
    /**
     * {@inheritDoc}
     */
    public int computeLength()
    {
        return 0;
    }


    // ------------------------------------------------------------------------
    // Object Method Overrides
    // ------------------------------------------------------------------------
    
    
    /**
     * @see Object#hashCode()
     */
    public int hashCode()
    {
        return decorated.hashCode();
    }

    /**
     * @see Object#equals(Object)
     */
    public boolean equals( Object o )
    {
        if ( decorated == null )
        {
            return o == null;
        }
        else
        {
            return decorated.equals( o );
        }
    }


    /**
     * Return a String representing a Control
     */
    public String toString()
    {
        return decorated.toString();
    }
}

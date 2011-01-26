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
package org.apache.directory.shared.ldap.codec.controls;


import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.directory.shared.util.Strings;


/**
 * A Control base class.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DefaultControl implements Control
{
    /** The control type */
    private String oid;

    /** The criticality (default value is false) */
    private boolean criticality = false;

    /** Optional control value */
    protected byte[] value;


    /**
     * Creates a Control with a specific OID.
     *
     * @param oid The OID of this Control.
     */
    public DefaultControl( String oid )
    {
        this.oid = oid;
    }


    /**
     * Get the OID
     * 
     * @return A string which represent the control oid
     */
    public String getOid()
    {
        return oid == null ? "" : oid;
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
            this.value = new byte[ value.length ];
            System.arraycopy( value, 0, this.value, 0, value.length );
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
        return criticality;
    }


    /**
     * Set the criticality
     * 
     * @param criticality The criticality value
     */
    public void setCritical( boolean criticality )
    {
        this.criticality = criticality;
    }

    
    /**
     * {@inheritDoc}
     */
    public boolean hasValue()
    {
        return value != null;
    }


    /**
     * @see Object#equals(Object)
     */
    public boolean equals( Object o )
    {
        if ( o == this )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !( o instanceof Control) )
        {
            return false;
        }

        Control otherControl = ( Control ) o;

        if ( !oid.equalsIgnoreCase( otherControl.getOid() ) )
        {
            return false;
        }

        //noinspection SimplifiableIfStatement
        if ( criticality != otherControl.isCritical() )
        {
            return false;
        }

        return hasValue() == otherControl.hasValue();
    }


    /**
     * Return a String representing a Control
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append( "    " ).append( getClass().getSimpleName() ).append( " " );
        sb.append( "Control\n" );
        sb.append( "        Type OID    : '" ).append( oid ).append( "'\n" );
        sb.append( "        Criticality : '" ).append( criticality ).append( "'\n" );

        if ( value != null )
        {
            sb.append( "        Value (HEX) : '" ).append( Strings.dumpBytes(value) )
                .append( "'\n" );
        }

        return sb.toString();
    }
}

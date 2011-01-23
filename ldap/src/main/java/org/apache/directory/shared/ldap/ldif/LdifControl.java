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
package org.apache.directory.shared.ldap.ldif;


import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.directory.shared.util.Strings;


/**
 * The LdifControl class stores a control defined for an entry found in a LDIF
 * file.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdifControl implements Control
{
    /** The serial version UID */
    private static final long serialVersionUID = 1L;

    // ~ Instance fields
    // ----------------------------------------------------------------------------
    /** The control type */
    private String oid;

    /** The criticality (default value is false) */
    private boolean criticality = false;

    /** Optional control value */
    protected byte[] value;


    /**
     * Create a new Control
     * 
     * @param oid OID of the created control
     */
    public LdifControl( String oid )
    {
        this.oid = oid;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return "LdifControl : {" + getOid() + ", " + isCritical() + ", " + Strings.dumpBytes( getValue() ) + "}";
    }


    /**
     * {@inheritDoc}
     */
    public String getOid()
    {
        return oid;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isCritical()
    {
        return criticality;
    }


    /**
     * {@inheritDoc}
     */
    public void setCritical( boolean criticality )
    {
        this.criticality = criticality;
    }


    /**
     * {@inheritDoc}
     */
    public byte[] getValue()
    {
        return value;
    }


    /**
     * {@inheritDoc}
     */
    public void setValue( byte[] value )
    {
        this.value = value;
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

        if ( criticality != otherControl.isCritical() )
        {
            return false;
        }

        return hasValue() == otherControl.hasValue();
    }
}

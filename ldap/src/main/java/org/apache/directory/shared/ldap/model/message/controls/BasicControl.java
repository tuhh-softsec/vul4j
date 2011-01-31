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
package org.apache.directory.shared.ldap.model.message.controls;


import org.apache.directory.shared.ldap.codec.ILdapCodecService;
import org.apache.directory.shared.ldap.model.message.Control;


/**
 * A simple implementation of the {@link Control} interface with storage for 
 * the OID and the criticality properties. When a {@link ILdapCodecService} 
 * implementation does not have specific control factories available, hence 
 * the control is unrecognized, it creates instances of this control for them.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class BasicControl implements Control
{
    /** The control type */
    private String oid;

    /** The criticality (default value is false) */
    private boolean criticality = false;


    /**
     * Creates a Control with a specific OID.
     *
     * @param oid The OID of this Control.
     */
    public BasicControl( String oid )
    {
        this.oid = oid;
    }


    /**
     * Creates a Control with a specific OID, and criticality set.
     *
     * @param oid The OID of this Control.
     * @param criticality true if this Control is critical, false otherwise. 
     */
    public BasicControl( String oid, boolean criticality )
    {
        this.oid = oid;
        this.criticality = criticality;
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
        return criticality == otherControl.isCritical();
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

        sb.append( "'\n" );

        return sb.toString();
    }
}

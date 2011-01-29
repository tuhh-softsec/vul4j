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


import org.apache.directory.shared.ldap.model.name.Dn;


/**
 * A simple implementation of the EntryChange response control.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EntryChangeImpl extends BasicControl implements EntryChange
{
    /** The changeType */
    private ChangeType changeType = ChangeType.ADD;

    private long changeNumber = UNDEFINED_CHANGE_NUMBER;

    /** The previous Dn */
    private Dn previousDn = null;


    /**
     *
     * Creates a new instance of EntryChangeControl.
     *
     */
    public EntryChangeImpl()
    {
        super( OID );
    }


    public ChangeType getChangeType()
    {
        return changeType;
    }


    public void setChangeType( ChangeType changeType )
    {
        this.changeType = changeType;
    }


    public Dn getPreviousDn()
    {
        return previousDn;
    }


    public void setPreviousDn( Dn previousDn )
    {
        this.previousDn = previousDn;
    }


    public long getChangeNumber()
    {
        return changeNumber;
    }


    public void setChangeNumber( long changeNumber )
    {
        this.changeNumber = changeNumber;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object o )
    {
        if ( !super.equals( o ) )
        {
            return false;
        }

        EntryChange otherControl = ( EntryChange ) o;

        return ( changeNumber == otherControl.getChangeNumber() ) &&
             ( changeType == otherControl.getChangeType() ) &&
             ( previousDn.equals( otherControl.getPreviousDn() ) );
    }

    
    /**
     * Return a String representing this EntryChangeControl.
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append( "    Entry Change Control\n" );
        sb.append( "        oid : " ).append( getOid() ).append( '\n' );
        sb.append( "        critical : " ).append( isCritical() ).append( '\n' );
        sb.append( "        changeType   : '" ).append( changeType ).append( "'\n" );
        sb.append( "        previousDN   : '" ).append( previousDn ).append( "'\n" );

        if ( changeNumber == UNDEFINED_CHANGE_NUMBER )
        {
            sb.append( "        changeNumber : '" ).append( "UNDEFINED" ).append( "'\n" );
        }
        else
        {
            sb.append( "        changeNumber : '" ).append( changeNumber ).append( "'\n" );
        }

        return sb.toString();
    }
}

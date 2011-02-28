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
package org.apache.directory.shared.ldap.extras.controls;


import java.util.Arrays;

import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.directory.shared.ldap.model.message.controls.AbstractControl;
import org.apache.directory.shared.util.Strings;


/**
 * A simple SyncStateValue {@link Control} implementation.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SyncStateValueImpl extends AbstractControl implements SyncStateValue
{
    /** The syncStateEnum type */
    private SyncStateTypeEnum type;

    /** The Sync cookie */
    private byte[] cookie;

    /** The entryUUID */
    private byte[] entryUuid;


    /**SyncStateValueImpl
     * Creates a new instance of SyncDoneValueImpl.
     */
    public SyncStateValueImpl()
    {
        super( OID );
    }


    /**
     *
     * Creates a new instance of SyncStateValueImpl.
     *
     * @param isCritical The critical flag
     */
    public SyncStateValueImpl( boolean isCritical )
    {
        super( OID, isCritical );
    }


    /**
     * {@inheritDoc}
     */
    public byte[] getCookie()
    {
        return cookie;
    }


    /**
     * {@inheritDoc}
     */
    public void setCookie( byte[] cookie )
    {
        this.cookie = cookie;
    }


    /**
     * {@inheritDoc}
     */
    public SyncStateTypeEnum getSyncStateType()
    {
        return type;
    }


    /**
     * {@inheritDoc}
     */
    public void setSyncStateType( SyncStateTypeEnum syncStateType )
    {
        this.type = syncStateType;
    }


    /**
     * {@inheritDoc}
     */
    public byte[] getEntryUUID()
    {
        return entryUuid;
    }


    /**
     * {@inheritDoc}
     */
    public void setEntryUUID( byte[] entryUUID )
    {
        this.entryUuid = entryUUID;
    }


    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        int h = 37;

        h = h*17 + super.hashCode();
        h = h*17 + type.getValue();

        if ( cookie != null )
        {
            for ( byte b : cookie )
            {
                h = h*17 + b;
            }
        }

        if ( entryUuid != null )
        {
            for ( byte b : entryUuid )
            {
                h = h*17 + b;
            }
        }

        return h;
    }


    /**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals( Object o )
    {
        if ( !super.equals( o ) )
        {
            return false;
        }

        if ( !( o instanceof SyncStateValue ) )
        {
            return false;
        }

        SyncStateValue otherControl = ( SyncStateValue ) o;

        return ( type == otherControl.getSyncStateType() ) &&
            ( Arrays.equals( entryUuid, otherControl.getEntryUUID() ) ) &&
            ( Arrays.equals( cookie, otherControl.getCookie() ) &&
            ( isCritical() == otherControl.isCritical() ) );
    }


    /**
     * @see Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append( "    SyncStateValue control :\n" );
        sb.append( "        oid : " ).append( getOid() ).append( '\n' );
        sb.append( "        critical : " ).append( isCritical() ).append( '\n' );
        sb.append( "        syncStateType     : '" ).append( getSyncStateType() ).append( "'\n" );
        sb.append( "        entryUUID         : '" ).append( Strings.dumpBytes( getEntryUUID() ) ).append( "'\n" );
        sb.append( "        cookie            : '" ).append( Strings.dumpBytes( getCookie()) ).append( "'\n" );

        return sb.toString();
    }
}

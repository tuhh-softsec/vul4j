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
package org.apache.directory.shared.ldap.model.message.controls;

import java.util.Arrays;

import org.apache.directory.shared.ldap.codec.controls.replication.syncDoneValue.SyncDoneValueDecorator;
import org.apache.directory.shared.util.Strings;


/**
 * TODO SyncDoneValue.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SyncDoneValueImpl extends AbstractControl implements SyncDoneValue
{
    /** The Sync cookie */
    private byte[] cookie;

    /** the refreshDeletes flag */
    private boolean refreshDeletes;


    /**
     * Creates a new instance of SyncDoneValueImpl.
     */
    public SyncDoneValueImpl()
    {
        super( OID );
    }


    /**
     *
     * Creates a new instance of SyncDoneValueImpl.
     *
     * @param isCritical The critical flag
     */
    public SyncDoneValueImpl( boolean isCritical )
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
    public boolean isRefreshDeletes()
    {
        return refreshDeletes;
    }


    /**
     * {@inheritDoc}
     */
    public void setRefreshDeletes( boolean refreshDeletes )
    {
        this.refreshDeletes = refreshDeletes;
    }


    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        int h = 37;

        h = h*17 + super.hashCode();
        h = h*17 + ( refreshDeletes ? 1 : 0 );

        if ( cookie != null )
        {
            for ( byte b : cookie )
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
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof SyncDoneValue ) )
        {
            return false;
        }

        SyncDoneValueDecorator otherControl = ( SyncDoneValueDecorator ) o;

        return  ( isRefreshDeletes() == otherControl.isRefreshDeletes() ) &&
                ( Arrays.equals( getCookie(), otherControl.getCookie() ) &&
                ( isCritical() == otherControl.isCritical() ) );
    }


    /**
     * @see Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append( "    SyncDoneValue control :\n" );
        sb.append( "        oid : " ).append( getOid() ).append( '\n' );
        sb.append( "        critical : " ).append( isCritical() ).append( '\n' );
        sb.append( "        cookie            : '" ).append( Strings.dumpBytes( getCookie() ) ).append( "'\n" );
        sb.append( "        refreshDeletes : '" ).append( isRefreshDeletes() ).append( "'\n" );

        return sb.toString();
    }
}

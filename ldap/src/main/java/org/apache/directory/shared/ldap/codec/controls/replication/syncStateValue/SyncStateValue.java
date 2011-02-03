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
package org.apache.directory.shared.ldap.codec.controls.replication.syncStateValue;


import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.directory.shared.ldap.model.message.controls.SyncStateTypeEnum;


/**
 * A simple SyncStateValue {@link Control} implementation.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SyncStateValue implements ISyncStateValue
{
    /** The syncStateEnum type */
    private SyncStateTypeEnum type;

    /** The Sync cookie */
    private byte[] cookie;

    /** The entryUUID */
    private byte[] entryUuid;

    private boolean critical;
    
    
    /**
     * {@inheritDoc}
     */
    public String getOid()
    {
        return OID;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isCritical()
    {
        return critical;
    }


    /**
     * {@inheritDoc}
     */
    public void setCritical( boolean isCritical )
    {
        this.critical = isCritical;
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
}

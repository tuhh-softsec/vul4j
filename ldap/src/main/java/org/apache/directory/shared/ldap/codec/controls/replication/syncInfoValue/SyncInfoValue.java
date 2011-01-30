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
package org.apache.directory.shared.ldap.codec.controls.replication.syncInfoValue;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.shared.ldap.message.control.replication.SynchronizationInfoEnum;


/**
 * A simple {@link ISyncInfoValue} implementation to store control properties.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SyncInfoValue implements ISyncInfoValue
{
    /** The kind of syncInfoValue we are dealing with */
    private SynchronizationInfoEnum type;
    
    /** The cookie */
    private byte[] cookie;
    
    /** The refreshDone flag if we are dealing with refreshXXX syncInfo. Default to true */
    private boolean refreshDone = true;
    
    /** The refreshDeletes flag if we are dealing with syncIdSet syncInfo. Defaults to false */
    private boolean refreshDeletes = false;
    
    /** The list of UUIDs if we are dealing with syncIdSet syncInfo */
    private List<byte[]> syncUUIDs;
    
    private boolean isCritical;
    

    /**
     * {@inheritDoc}
     */
    public SynchronizationInfoEnum getType()
    {
        return type;
    }


    /**
     * {@inheritDoc}
     */
    public void setType( SynchronizationInfoEnum type )
    {
        this.type = type;
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
    public boolean isRefreshDone()
    {
        return refreshDone;
    }


    /**
     * {@inheritDoc}
     */
    public void setRefreshDone( boolean refreshDone )
    {
        this.refreshDone = refreshDone;
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
     * {@inheritDoc}
     */
    public List<byte[]> getSyncUUIDs()
    {
        return syncUUIDs;
    }


    /**
     * {@inheritDoc}
     */
    public void setSyncUUIDs( List<byte[]> syncUUIDs )
    {
        this.syncUUIDs = syncUUIDs;
    }


    /**
     * {@inheritDoc}
     */
    public void addSyncUUID( byte[] syncUUID )
    {
        if ( syncUUIDs == null )
        {
            syncUUIDs = new ArrayList<byte[]>();
        }
        
        syncUUIDs.add( syncUUID );
    }


    public String getOid()
    {
        return OID;
    }


    public boolean isCritical()
    {
        return isCritical;
    }


    public void setCritical( boolean isCritical )
    {
        this.isCritical = isCritical;
    }
}

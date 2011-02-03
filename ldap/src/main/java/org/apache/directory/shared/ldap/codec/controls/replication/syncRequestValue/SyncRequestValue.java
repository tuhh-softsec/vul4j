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
package org.apache.directory.shared.ldap.codec.controls.replication.syncRequestValue;


import org.apache.directory.shared.ldap.model.message.controls.SynchronizationModeEnum;


/**
 * A syncRequestValue object, as defined in RFC 4533
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SyncRequestValue implements ISyncRequestValue
{
    private boolean isCritical;
    
    /** The synchronization type */
    private SynchronizationModeEnum mode;
    
    /** The Sync cookie */
    private byte[] cookie;
    
    /** The reloadHint flag */
    private boolean isReloadHint;
    
    
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
        return isCritical;
    }


    /**
     * {@inheritDoc}
     */
    public void setCritical( boolean isCritical )
    {
        this.isCritical = isCritical;
    }


    /**
     * {@inheritDoc}
     */
    public byte[] getCookie()
    {
        return this.cookie;
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
    public SynchronizationModeEnum getMode()
    {
        return mode;
    }


    /**
     * {@inheritDoc}
     */
    public void setMode( SynchronizationModeEnum mode )
    {
        this.mode = mode;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isReloadHint()
    {
        return isReloadHint;
    }


    /**
     * {@inheritDoc}
     */
    public void setReloadHint( boolean reloadHint )
    {
        this.isReloadHint = reloadHint;
    }
}

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
package org.apache.directory.shared.ldap.codec.controls.replication.syncmodifydn;


import org.apache.directory.shared.ldap.message.control.replication.SyncModifyDnType;


/**
 * A simple {@link ISyncModifyDn} implementation to hold properties.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SyncModifyDn implements ISyncModifyDn
{
    /** the entry's Dn to be changed */
    private String entryDn;

    /** target entry's new parent Dn */
    private String newSuperiorDn;

    /** the new Rdn */
    private String newRdn;

    /** flag to indicate whether to delete the old Rdn */
    private boolean deleteOldRdn = false;

    private SyncModifyDnType modDnType;
    
    

    /**
     * {@inheritDoc}
     */
    public String getOid()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isCritical()
    {
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public void setCritical( boolean isCritical )
    {
    }


    /**
     * {@inheritDoc}
     */
    public String getEntryDn()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public void setEntryDn( String entryDn )
    {
    }


    /**
     * {@inheritDoc}
     */
    public String getNewSuperiorDn()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public void setNewSuperiorDn( String newSuperiorDn )
    {
    }


    /**
     * {@inheritDoc}
     */
    public String getNewRdn()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public void setNewRdn( String newRdn )
    {
    }


    /**
     * {@inheritDoc}
     */
    public boolean isDeleteOldRdn()
    {
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public void setDeleteOldRdn( boolean deleteOldRdn )
    {
    }


    /**
     * {@inheritDoc}
     */
    public SyncModifyDnType getModDnType()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public void setModDnType( SyncModifyDnType modDnType )
    {
    }

}

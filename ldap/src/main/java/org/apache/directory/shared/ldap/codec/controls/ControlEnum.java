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


import java.util.HashMap;
import java.util.Map;

import org.apache.directory.shared.ldap.codec.controls.replication.syncDoneValue.SyncDoneValueControl;
import org.apache.directory.shared.ldap.codec.controls.replication.syncInfoValue.SyncInfoValueControl;
import org.apache.directory.shared.ldap.codec.controls.replication.syncRequestValue.SyncRequestValueControl;
import org.apache.directory.shared.ldap.codec.controls.replication.syncStateValue.SyncStateValueControl;
import org.apache.directory.shared.ldap.codec.search.controls.pagedSearch.PagedResultsControl;
import org.apache.directory.shared.ldap.codec.search.controls.persistentSearch.PersistentSearchControl;
import org.apache.directory.shared.ldap.codec.search.controls.subentries.SubentriesControl;
import org.apache.directory.shared.ldap.message.control.Control;
import org.apache.directory.shared.ldap.message.control.replication.SynchronizationInfoEnum;


/**
 * An enumeration of all the existng controls. It currently includes :
 * <ul>
 *   <li></li>
 *   <li></li>
 *   <li></li>
 *   <li></li>
 *   <li></li>
 *   <li></li>
 *   <li></li>
 *   <li></li>
 *   <li></li>
 *   <li></li>
 * </ul>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum ControlEnum
{
    /** The ManageDsaIt control */
    MANAGE_DSA_IT(new ManageDsaITControl()),

    /** The PagedResults control */
    PAGED_RESULTS(new PagedResultsControl()),

    /** The PersistentSearch control */
    PERSISTENT_SEARCH(new PersistentSearchControl()),

    /** The Subentries control */
    SUBENTRIES(new SubentriesControl()),

    /** The SyncDoneValue control */
    SYNC_DONE_VALUE(new SyncDoneValueControl()),

    /** The SyncInfoValue (new cookie) control */
    SYNC_INFO_VALUE_NEW_COOKIE(new SyncInfoValueControl( SynchronizationInfoEnum.NEW_COOKIE )),

    /** The SyncInfoValue (refresh delete) control */
    SYNC_INFO_VALUE_REFRESH_DELETE(new SyncInfoValueControl( SynchronizationInfoEnum.REFRESH_DELETE )),

    /** The SyncInfoValue (refresh present) control */
    SYNC_INFO_VALUE_REFRESH_PRESENT(new SyncInfoValueControl( SynchronizationInfoEnum.REFRESH_PRESENT )),

    /** The SyncInfoValue (sync id set) control */
    SYNC_INFO_VALUE_SYNC_ID_SET(new SyncInfoValueControl( SynchronizationInfoEnum.SYNC_ID_SET )),

    /** The SyncRequestValueControl control */
    SYNC_REQUEST_VALUE(new SyncRequestValueControl()),

    /** The SyncStateValueControl control */
    SYNC_STATE_VALUE(new SyncStateValueControl());

    /** The internal control instance */
    private Control control;

    /** The control's oid */
    private String oid;

    private static Map<String, Control> codecControls = new HashMap<String, Control>();

    static
    {
        // Initialize the different known Controls
        codecControls.put( MANAGE_DSA_IT.getOid(), MANAGE_DSA_IT.getControl() );
        codecControls.put( PAGED_RESULTS.getOid(), PAGED_RESULTS.getControl() );
        codecControls.put( PERSISTENT_SEARCH.getOid(), PERSISTENT_SEARCH.getControl() );
        codecControls.put( SUBENTRIES.getOid(), SUBENTRIES.getControl() );
        codecControls.put( SYNC_DONE_VALUE.getOid(), SYNC_DONE_VALUE.getControl() );
        codecControls.put( SYNC_INFO_VALUE_NEW_COOKIE.getOid(), SYNC_INFO_VALUE_NEW_COOKIE.getControl() );
        codecControls.put( SYNC_INFO_VALUE_REFRESH_DELETE.getOid(), SYNC_INFO_VALUE_REFRESH_DELETE.getControl() );
        codecControls.put( SYNC_INFO_VALUE_REFRESH_PRESENT.getOid(), SYNC_INFO_VALUE_REFRESH_PRESENT.getControl() );
        codecControls.put( SYNC_INFO_VALUE_SYNC_ID_SET.getOid(), SYNC_INFO_VALUE_SYNC_ID_SET.getControl() );
        codecControls.put( SYNC_REQUEST_VALUE.getOid(), SYNC_REQUEST_VALUE.getControl() );
        codecControls.put( SYNC_STATE_VALUE.getOid(), SYNC_STATE_VALUE.getControl() );
    }


    /**
     * Create a new instance of the control identified by its oid
     * @param oid The Control's oid
     */
    private ControlEnum( Control control )
    {
        this.control = control;
        this.oid = control.getOid();
    }


    /**
     * @return The associated control instance
     */
    public Control getControl()
    {
        return control;
    }


    /**
     * @return The associated control instance
     */
    public static Control getControl( String oid )
    {
        return codecControls.get( oid );
    }


    /**
     * @return The control's OID
     */
    public String getOid()
    {
        return oid;
    }
}

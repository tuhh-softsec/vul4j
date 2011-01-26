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

import org.apache.directory.shared.ldap.codec.controls.ppolicy.PasswordPolicyRequestControl;
import org.apache.directory.shared.ldap.codec.controls.replication.syncDoneValue.SyncDoneValueControl;
import org.apache.directory.shared.ldap.codec.controls.replication.syncInfoValue.SyncInfoValueControl;
import org.apache.directory.shared.ldap.codec.controls.replication.syncRequestValue.SyncRequestValueControl;
import org.apache.directory.shared.ldap.codec.controls.replication.syncStateValue.SyncStateValueControl;
import org.apache.directory.shared.ldap.codec.controls.replication.syncmodifydn.SyncModifyDnControl;
import org.apache.directory.shared.ldap.codec.search.controls.entryChange.EntryChange;
import org.apache.directory.shared.ldap.codec.search.controls.pagedSearch.PagedResults;
import org.apache.directory.shared.ldap.codec.search.controls.persistentSearch.PersistentSearch;
import org.apache.directory.shared.ldap.codec.search.controls.subentries.Subentries;


/**
 * An enum listing all the existing controls
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public enum ControlEnum
{
    CASCADE_CONTROL( Cascade.OID ),
    ENTRY_CHANGE_CONTROL( EntryChange.OID ),
    MANAGE_DSA_IT_CONTROL( ManageDsaIT.OID ),
    PAGED_RESULTS_CONTROL( PagedResults.OID ),
    PASSWORD_POLICY_REQUEST_CONTROL( PasswordPolicyRequestControl.CONTROL_OID ),
    PERSISTENT_SEARCH_CONTROL( PersistentSearch.OID ),
    SUBENTRIES_CONTROL( Subentries.OID ),
    SYNC_DONE_VALUE_CONTROL( SyncDoneValueControl.CONTROL_OID ),
    SYNC_INFO_VALUE_CONTROL( SyncInfoValueControl.CONTROL_OID ),
    SYNC_MODIFY_DN_CONTROL( SyncModifyDnControl.CONTROL_OID ),
    SYNC_REQUEST_VALUE_CONTROL( SyncRequestValueControl.CONTROL_OID ),
    SYNC_STATE_VALUE_CONTROL(SyncStateValueControl.CONTROL_OID);

    private String oid;
    
    private ControlEnum( String oid )
    {
        this.oid = oid;
    }
}

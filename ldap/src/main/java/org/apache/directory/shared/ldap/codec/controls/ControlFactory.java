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


import org.apache.directory.shared.ldap.codec.controls.replication.syncDoneValue.SyncDoneValueControl;
import org.apache.directory.shared.ldap.codec.controls.replication.syncInfoValue.SyncInfoValueControl;
import org.apache.directory.shared.ldap.codec.controls.replication.syncRequestValue.SyncRequestValueControl;
import org.apache.directory.shared.ldap.codec.controls.replication.syncStateValue.SyncStateValueControl;
import org.apache.directory.shared.ldap.model.message.controls.ManageDsaIT;
import org.apache.directory.shared.ldap.model.message.controls.PagedResults;
import org.apache.directory.shared.ldap.codec.search.controls.pagedSearch.PagedResultsDecorator;
import org.apache.directory.shared.ldap.model.message.controls.PersistentSearch;
import org.apache.directory.shared.ldap.codec.search.controls.persistentSearch.PersistentSearchDecorator;
import org.apache.directory.shared.ldap.model.message.controls.Subentries;
import org.apache.directory.shared.ldap.codec.search.controls.subentries.SubentriesDecorator;
import org.apache.directory.shared.ldap.model.message.Control;


/**
 * A factory for Controls.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ControlFactory
{

    /**
     * Creates a new Control instance. If the OID of a known control is provided
     * a concrete Control instance is returned, otherwise an instance of 
     * ControlImpl is returned. The following Controls are known:
     * <ul>
     * <li>ManageDsaITControlDecorator</li>
     * <li>PagedResultsDecorator</li>
     * <li>PersistentSearchDecorator</li>
     * <li>SubentriesControl</li>
     * <li>SyncDoneValueControl</li>
     * <li>SyncInfoValueControl</li>
     * <li>SyncRequestValueControl</li>
     * <li>SyncStateValueControl</li>
     * </ul>
     * 
     * Note that the created Control is empty, criticality and value are not set.
     * Some Controls also need additional initialization.
     *
     * @param oid the control OID
     * @return the control instance
     */
    public static Control createControl( String oid )
    {
        if ( ManageDsaIT.OID.equals( oid ) )
        {
            return new ManageDsaITDecorator();
        }

        if ( PagedResults.OID.equals( oid ) )
        {
            return new PagedResultsDecorator();
        }

        if ( PersistentSearch.OID.equals( oid ) )
        {
            return new PersistentSearchDecorator();
        }

        if ( Subentries.OID.equals( oid ) )
        {
            return new SubentriesDecorator();
        }

        if ( SyncDoneValueControl.CONTROL_OID.equals( oid ) )
        {
            return new SyncDoneValueControl();
        }

        if ( SyncInfoValueControl.CONTROL_OID.equals( oid ) )
        {
            return new SyncInfoValueControl();
        }

        if ( SyncRequestValueControl.CONTROL_OID.equals( oid ) )
        {
            return new SyncRequestValueControl();
        }

        if ( SyncStateValueControl.CONTROL_OID.equals( oid ) )
        {
            return new SyncStateValueControl();
        }

        // This control is unknown, we will create a neutral control
        return new ControlImpl( oid );
    }
}

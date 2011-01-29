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
import org.apache.directory.shared.ldap.model.message.Control;


/**
 * A SyncModifyDnControl object, to send the parameters used in a MODIFYDN operation
 * that was carried out on a syncrepl provider server.
 * 
 * The consumer will use the values present in this control to perform the same operation
 * on its local data, which helps in avoiding huge number of updates to the consumer.
 * 
 * NOTE: syncrepl, defined in RFC 4533, doesn't mention about this approach, this is a special
 *       extension provided by Apache Directory Server
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface ISyncModifyDn extends Control
{

    /** This control OID */
    public static final String OID = "1.3.6.1.4.1.4203.1.9.1.5";
    

    String getEntryDn();


    void setEntryDn( String entryDn );


    String getNewSuperiorDn();


    void setNewSuperiorDn( String newSuperiorDn );


    String getNewRdn();


    void setNewRdn( String newRdn );


    boolean isDeleteOldRdn();


    void setDeleteOldRdn( boolean deleteOldRdn );


    SyncModifyDnType getModDnType();


    void setModDnType( SyncModifyDnType modDnType );
}
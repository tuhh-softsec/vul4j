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
package org.apache.directory.shared.ldap.codec.controls.replication.syncInfoValue;


import java.util.List;

import org.apache.directory.shared.ldap.message.control.replication.SynchronizationInfoEnum;
import org.apache.directory.shared.ldap.model.message.Control;


/**
 * A syncInfoValue object, as defined in RFC 4533
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface ISyncInfoValue extends Control
{

    /** This control OID */
    public static final String OID = "1.3.6.1.4.1.4203.1.9.1.4";


    /**
     * Get the control type.
     * 
     * @return the type : one of newCookie, refreshDelete, refreshPresent or syncIdSet
     */
    SynchronizationInfoEnum getType();


    /**
     * @param syncMode the syncMode to set
     */
    void setType( SynchronizationInfoEnum type );


    /**
     * @return the cookie
     */
    byte[] getCookie();


    /**
     * @param cookie the cookie to set
     */
    void setCookie( byte[] cookie );


    /**
     * @return the refreshDone
     */
    boolean isRefreshDone();


    /**
     * @param refreshDone the refreshDone to set
     */
    void setRefreshDone( boolean refreshDone );


    /**
     * @return the refreshDeletes
     */
    boolean isRefreshDeletes();


    /**
     * @param refreshDeletes the refreshDeletes to set
     */
    void setRefreshDeletes( boolean refreshDeletes );


    /**
     * @return the syncUUIDs
     */
    List<byte[]> getSyncUUIDs();


    /**
     * @param syncUUIDs the syncUUIDs to set
     */
    void setSyncUUIDs( List<byte[]> syncUUIDs );


    /**
     * @param syncUUIDs the syncUUIDs to set
     */
    void addSyncUUID( byte[] syncUUID );
}
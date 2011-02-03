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
 * A syncStateValue object, as defined in RFC 4533
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface ISyncStateValue extends Control
{
    /** This control OID */
    public static final String OID = "1.3.6.1.4.1.4203.1.9.1.2";


    /**
     * @return the cookie
     */
    public abstract byte[] getCookie();


    /**
     * @param cookie the cookie to set
     */
    public abstract void setCookie( byte[] cookie );


    /**
     * @return the syncState's type
     */
    public abstract SyncStateTypeEnum getSyncStateType();


    /**
     * set the syncState's type
     * 
     * @param syncStateType the syncState's type
     */
    public abstract void setSyncStateType( SyncStateTypeEnum syncStateType );


    /**
     * @return the entryUUID
     */
    public abstract byte[] getEntryUUID();


    /**
     * set the entryUUID
     * 
     * @param entryUUID the entryUUID
     */
    public abstract void setEntryUUID( byte[] entryUUID );

}
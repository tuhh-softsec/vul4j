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
package org.apache.directory.shared.ldap.extras.controls;


import org.apache.directory.shared.ldap.model.message.Control;


/**
 * A syncStateValue object, as defined in RFC 4533 :
 * <pre>
 * 2.3.  Sync State Control
 *
 *    The Sync State Control is an LDAP Control [RFC4511] where the
 *    controlType is the object identifier 1.3.6.1.4.1.4203.1.9.1.2 and the
 *    controlValue, an OCTET STRING, contains a BER-encoded syncStateValue.
 *    The criticality is FALSE.
 *
 *       syncStateValue ::= SEQUENCE {
 *           state ENUMERATED {
 *               present (0),
 *               add (1),
 *               modify (2),
 *               delete (3)
 *           },
 *           entryUUID syncUUID,
 *           cookie    syncCookie OPTIONAL
 *       }
 *
 *    The Sync State Control is only applicable to SearchResultEntry and
 *    SearchResultReference Messages.
 * </pre>
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface SyncStateValue extends Control
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
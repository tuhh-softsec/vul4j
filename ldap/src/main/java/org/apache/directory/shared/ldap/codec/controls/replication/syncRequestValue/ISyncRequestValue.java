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
package org.apache.directory.shared.ldap.codec.controls.replication.syncRequestValue;


import org.apache.directory.shared.ldap.model.message.Control;
import org.apache.directory.shared.ldap.model.message.controls.SynchronizationModeEnum;


/**
 * A syncRequestValue object, as defined in RFC 4533
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface ISyncRequestValue extends Control
{

    /** This control OID */
    public static final String OID = "1.3.6.1.4.1.4203.1.9.1.1";


    /**
     * @return the mode
     */
    public abstract SynchronizationModeEnum getMode();


    /**
     * @param syncMode the syncMode to set
     */
    public abstract void setMode( SynchronizationModeEnum mode );


    /**
     * @return the cookie
     */
    public abstract byte[] getCookie();


    /**
     * @param cookie the cookie to set
     */
    public abstract void setCookie( byte[] cookie );


    /**
     * @return the reloadHint
     */
    public abstract boolean isReloadHint();


    /**
     * @param reloadHint the reloadHint to set
     */
    public abstract void setReloadHint( boolean reloadHint );

}
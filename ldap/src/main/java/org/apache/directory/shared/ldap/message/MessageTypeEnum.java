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
package org.apache.directory.shared.ldap.message;

/**
 * Type safe enumeration over the various LDAPv3 message types.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Revision$
 */
public enum MessageTypeEnum
{
    /** Bind request protocol message type value */
    BIND_REQUEST,

    /** Bind response protocol message type value */
    BIND_RESPONSE,

    /** Unbind request protocol message type value */
    UNBIND_REQUEST,

    /** Search request protocol message type value */
    SEARCH_REQUEST,

    /** Search entry response protocol message type value */
    SEARCH_RES_ENTRY,

    /** Search done response protocol message type value */
    SEARCH_RES_DONE,

    /** Search reference response protocol message type value */
    SEARCH_RES_REF,

    /** Modify request protocol message type value */
    MODIFY_REQUEST,

    /** Modify response protocol message type value */
    MODIFY_RESPONSE,

    /** Add request protocol message type value */
    ADD_REQUEST,

    /** Add response protocol message type value */
    ADD_RESPONSE,

    /** Delete request protocol message type value */
    DEL_REQUEST,

    /** Delete response protocol message type value */
    DEL_RESPONSE,

    /** Modify DN request protocol message type value */
    MOD_DN_REQUEST,

    /** Modify DN response protocol message type value */
    MOD_DN_RESPONSE,

    /** Compare request protocol message type value */
    COMPARE_REQUEST,

    /** Compare response protocol message type value */
    COMPARE_RESPONSE,

    /** Abandon request protocol message type value */
    ABANDON_REQUEST,

    /** Extended request protocol message type value */
    EXTENDED_REQ,

    /** Extended response protocol message type value */
    EXTENDED_RESP,
    
    /** Intermediate response message type */
    INTERMEDIATE_RESP;
}

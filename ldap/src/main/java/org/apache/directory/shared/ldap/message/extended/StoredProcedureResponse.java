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
package org.apache.directory.shared.ldap.message.extended;


import org.apache.directory.shared.ldap.model.message.ExtendedResponseImpl;


/**
 * The response sent back from the server when a {@link StoredProcedureRequest}
 * is sent.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StoredProcedureResponse extends ExtendedResponseImpl
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7689434378578829994L;

    /** The OID for the stored procedure extended operation response. */
    public static final String EXTENSION_OID = "1.3.6.1.4.1.18060.0.1.7";


    /**
     * Instantiates a new stored procedure response.
     *
     * @param messageId the message id
     */
    public StoredProcedureResponse( int messageId )
    {
        super( messageId, EXTENSION_OID );
    }
}

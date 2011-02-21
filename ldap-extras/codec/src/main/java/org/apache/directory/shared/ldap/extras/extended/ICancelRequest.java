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
package org.apache.directory.shared.ldap.extras.extended;

import org.apache.directory.shared.ldap.model.message.ExtendedRequest;


/**
 * The CancelRequest interface.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface ICancelRequest extends ExtendedRequest<ICancelResponse>
{

    /** The requestName for this extended request */
    public static final String EXTENSION_OID = "1.3.6.1.1.8";


    /**
     *  @return The id of the Message to cancel.
     */
    public abstract int getCancelId();


    /**
     * Sets the message to cancel by id.
     *
     * @param cancelId The id of the message to cancel.
     */
    public abstract void setCancelId( int cancelId );

}
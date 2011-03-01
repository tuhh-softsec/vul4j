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


import org.apache.directory.shared.ldap.model.message.AbstractExtendedRequest;


/**
 * Implement the extended Cancel Request as described in RFC 3909.
 * 
 * It's grammar is :
 * 
 * cancelRequestValue ::= SEQUENCE {
 *        cancelID        MessageID
 *                        -- MessageID is as defined in [RFC2251]
 * }
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CancelRequestImpl extends AbstractExtendedRequest<CancelResponse> implements CancelRequest
{
    /** The serial version UUID */
    private static final long serialVersionUID = 1L;

    /** The cancelId of the request to be canceled */
    private int cancelId;

    /**
     * Creates a new instance of CancelRequest.
     *
     * @param messageId the message id
     * @param cancelId the message id of the request to cancel
     */
    public CancelRequestImpl( int messageId, int cancelId )
    {
        super( messageId );
        setRequestName( EXTENSION_OID );

        this.cancelId = cancelId;
    }


    /**
     * Creates a new instance of CancelRequest.
     */
    public CancelRequestImpl()
    {
        setRequestName( EXTENSION_OID );
    }

    
    public int getCancelId()
    {
        return cancelId;
    }
    
    
    public void setCancelId( int cancelId )
    {
        this.cancelId = cancelId;
    }
    

    public CancelResponse getResultResponse()
    {
        if ( response == null )
        {
            response = new CancelResponseImpl( cancelId );
        }

        return response;
    }
}

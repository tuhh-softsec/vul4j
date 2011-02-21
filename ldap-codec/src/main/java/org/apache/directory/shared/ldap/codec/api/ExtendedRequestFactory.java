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
package org.apache.directory.shared.ldap.codec.api;


import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.ldap.model.message.ExtendedRequest;
import org.apache.directory.shared.ldap.model.message.ExtendedResponse;


/**
 * The factor interface, defined by the codec API for creating new 
 * ExtendedRequests.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface ExtendedRequestFactory<Q extends ExtendedRequest<P>,P extends ExtendedResponse>
{
    /**
     * Gets the OID of the extended requests this factory generates.
     *
     * @return the extended request OID
     */
    String getOid();
    
    
    /**
     *  @return A new instance of the ExtendedRequest.
     */
    Q newRequest();
    
    
    /**
     * Creates a new ExtendedResponse, for the ExtendedRequest with a specific
     * encoded value.
     * 
     * @param encodedValue The encoded value for the ExtendedResponse instance.
     * @return The new ExtendedResponse.
     */
    P newResponse( byte[] encodedValue ) throws DecoderException;
}

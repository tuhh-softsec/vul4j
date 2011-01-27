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
package org.apache.directory.shared.ldap.codec.decorators;


import org.apache.directory.shared.ldap.model.message.ExtendedResponse;


/**
 * A decorator for the ExtendedResponse message
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExtendedResponseDecorator extends ResponseDecorator
{
    /** The response name as a byte[] */
    private byte[] responseNameBytes;

    /** The encoded extendedResponse length */
    private int extendedResponseLength;


    /**
     * Makes a ExtendedResponse encodable.
     *
     * @param decoratedMessage the decorated ExtendedResponse
     */
    public ExtendedResponseDecorator( ExtendedResponse decoratedMessage )
    {
        super( decoratedMessage );
    }


    /**
     * @return The decorated ExtendedResponse
     */
    public ExtendedResponse getExtendedResponse()
    {
        return ( ExtendedResponse ) getDecoratedMessage();
    }


    /**
     * Gets the responseName bytes.
     *
     * @return the responseName bytes of the extended response type.
     */
    public byte[] getResponseNameBytes()
    {
        return responseNameBytes;
    }


    /**
     * Sets the OID bytes.
     *
     * @param responseNameBytes the OID bytes of the extended response type.
     */
    public void setResponseNameBytes( byte[] responseNameBytes )
    {
        this.responseNameBytes = responseNameBytes;
    }


    /**
     * Stores the encoded length for the ExtendedResponse
     *
     * @param extendedResponseLength The encoded length
     */
    public void setExtendedResponseLength( int extendedResponseLength )
    {
        this.extendedResponseLength = extendedResponseLength;
    }


    /**
     * @return The encoded ExtendedResponse's length
     */
    public int getExtendedResponseLength()
    {
        return extendedResponseLength;
    }
}

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


import org.apache.directory.shared.ldap.model.message.IntermediateResponse;


/**
 * A decorator for the IntermediateResponse message
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class IntermediateResponseDecorator extends MessageDecorator
{
    /** The response name as a byte[] */
    private byte[] responseNameBytes;

    /** The encoded intermediateResponse length */
    private int intermediateResponseLength;


    /**
     * Makes a IntermediateResponse encodable.
     *
     * @param decoratedMessage the decorated IntermediateResponse
     */
    public IntermediateResponseDecorator( IntermediateResponse decoratedMessage )
    {
        super( decoratedMessage );
    }


    /**
     * @return The decorated IntermediateResponse
     */
    public IntermediateResponse getIntermediateResponse()
    {
        return ( IntermediateResponse ) getMessage();
    }


    /**
     * Stores the encoded length for the IntermediateResponse
     *
     * @param intermediateResponseLength The encoded length
     */
    public void setIntermediateResponseLength( int intermediateResponseLength )
    {
        this.intermediateResponseLength = intermediateResponseLength;
    }


    /**
     * @return The encoded IntermediateResponse's length
     */
    public int getIntermediateResponseLength()
    {
        return intermediateResponseLength;
    }


    /**
     * Gets the ResponseName bytes
     *
     * @return the ResponseName bytes of the Intermediate response type.
     */
    public byte[] getResponseNameBytes()
    {
        return responseNameBytes;
    }


    /**
     * Sets the ResponseName bytes
     *
     * @param responseNameBytes the ResponseName bytes of the Intermediate response type.
     */
    public void setResponseNameBytes( byte[] responseNameBytes )
    {
        this.responseNameBytes = responseNameBytes;
    }
}

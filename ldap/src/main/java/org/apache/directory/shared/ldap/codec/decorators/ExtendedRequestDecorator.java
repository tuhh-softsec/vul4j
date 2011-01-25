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


import org.apache.directory.shared.ldap.model.message.ExtendedRequest;


/**
 * A decorator for the ExtendedRequest message
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExtendedRequestDecorator extends MessageDecorator
{
    /** The extended request length */
    private int extendedRequestLength;

    /** The OID length */
    private byte[] requestNameBytes;


    /**
     * Makes a ExtendedRequest encodable.
     *
     * @param decoratedMessage the decorated ExtendedRequest
     */
    public ExtendedRequestDecorator( ExtendedRequest decoratedMessage )
    {
        super( decoratedMessage );
    }


    /**
     * @return The decorated ExtendedRequest
     */
    public ExtendedRequest getExtendedRequest()
    {
        return ( ExtendedRequest ) getMessage();
    }


    /**
     * Stores the encoded length for the ExtendedRequest
     *
     * @param extendedRequestLength The encoded length
     */
    public void setExtendedRequestLength( int extendedRequestLength )
    {
        this.extendedRequestLength = extendedRequestLength;
    }


    /**
     * @return The encoded ExtendedRequest's length
     */
    public int getExtendedRequestLength()
    {
        return extendedRequestLength;
    }


    /**
     * Gets the requestName bytes.
     *
     * @return the requestName bytes of the extended request type.
     */
    public byte[] getRequestNameBytes()
    {
        return requestNameBytes;
    }


    /**
     * Sets the requestName bytes.
     *
     * @param requestNameBytes the OID bytes of the extended request type.
     */
    public void setRequestNameBytes( byte[] requestNameBytes )
    {
        this.requestNameBytes = requestNameBytes;
    }
}

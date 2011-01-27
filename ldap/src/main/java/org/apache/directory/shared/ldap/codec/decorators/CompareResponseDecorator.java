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


import org.apache.directory.shared.ldap.model.message.CompareResponse;


/**
 * A decorator for the CompareResponse message
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CompareResponseDecorator extends ResponseDecorator
{
    /** The encoded compareResponse length */
    private int compareResponseLength;


    /**
     * Makes a CompareResponse encodable.
     *
     * @param decoratedMessage the decorated CompareResponse
     */
    public CompareResponseDecorator( CompareResponse decoratedMessage )
    {
        super( decoratedMessage );
    }


    /**
     * @return The decorated CompareResponse
     */
    public CompareResponse getCompareResponse()
    {
        return ( CompareResponse ) getDecoratedMessage();
    }


    /**
     * Stores the encoded length for the CompareResponse
     * @param compareResponseLength The encoded length
     */
    public void setCompareResponseLength( int compareResponseLength )
    {
        this.compareResponseLength = compareResponseLength;
    }


    /**
     * @return The encoded CompareResponse's length
     */
    public int getCompareResponseLength()
    {
        return compareResponseLength;
    }
}

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


import org.apache.directory.shared.ldap.model.message.AddResponse;


/**
 * A decorator for the AddResponse message
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AddResponseDecorator extends ResponseDecorator implements AddResponse
{
    /** The encoded addResponse length */
    private int addResponseLength;


    /**
     * Makes a AddResponse a MessageDecorator.
     *
     * @param decoratedMessage the decorated AddResponse
     */
    public AddResponseDecorator( AddResponse decoratedMessage)
    {
        super( decoratedMessage );
    }


    /**
     * @return The decorated AddResponse
     */
    public AddResponse getAddResponse()
    {
        return ( AddResponse ) getDecoratedMessage();
    }


    /**
     * Stores the encoded length for the AddResponse
     * @param addResponseLength The encoded length
     */
    public void setAddResponseLength( int addResponseLength )
    {
        this.addResponseLength = addResponseLength;
    }


    /**
     * @return The encoded AddResponse's length
     */
    public int getAddResponseLength()
    {
        return addResponseLength;
    }


    //-------------------------------------------------------------------------
    // The AddResponse methods
    //-------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return getAddResponse().toString();
    }
}

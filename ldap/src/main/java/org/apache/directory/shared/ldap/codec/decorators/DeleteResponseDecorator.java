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


import org.apache.directory.shared.ldap.model.message.DeleteResponse;


/**
 * A decorator for the DeleteRequest message
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DeleteResponseDecorator extends ResponseDecorator implements DeleteResponse
{
    /** The encoded deleteResponse length */
    private int deleteResponseLength;


    /**
     * Makes a DeleteResponse a MessageDecorator.
     *
     * @param decoratedMessage the decorated DeleteResponse
     */
    public DeleteResponseDecorator( DeleteResponse decoratedMessage )
    {
        super( decoratedMessage );
    }


    /**
     * @return The decorated DeleteResponse
     */
    public DeleteResponse getDeleteResponse()
    {
        return ( DeleteResponse ) getDecoratedMessage();
    }


    /**
     * Stores the encoded length for the DeleteResponse
     * @param deleteResponseLength The encoded length
     */
    public void setDeleteResponseLength( int deleteResponseLength )
    {
        this.deleteResponseLength = deleteResponseLength;
    }


    /**
     * @return The encoded DeleteResponse's length
     */
    public int getDeleteResponseLength()
    {
        return deleteResponseLength;
    }


    //-------------------------------------------------------------------------
    // The DeleteResponse methods
    //-------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return getDeleteResponse().toString();
    }
}

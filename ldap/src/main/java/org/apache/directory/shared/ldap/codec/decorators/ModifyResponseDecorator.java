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


import org.apache.directory.shared.ldap.model.message.ModifyResponse;


/**
 * A decorator for the ModifyResponse message
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ModifyResponseDecorator extends ResponseDecorator implements ModifyResponse
{
    /** The encoded modifyResponse length */
    private int modifyResponseLength;


    /**
     * Makes a ModifyResponse encodable.
     *
     * @param decoratedMessage the decorated ModifyResponse
     */
    public ModifyResponseDecorator( ModifyResponse decoratedMessage )
    {
        super( decoratedMessage );
    }


    /**
     * @return The decorated ModifyResponse
     */
    public ModifyResponse getModifyResponse()
    {
        return ( ModifyResponse ) getDecoratedMessage();
    }


    /**
     * Stores the encoded length for the ModifyResponse
     * @param modifyResponseLength The encoded length
     */
    public void setModifyResponseLength( int modifyResponseLength )
    {
        this.modifyResponseLength = modifyResponseLength;
    }


    /**
     * @return The encoded ModifyResponse's length
     */
    public int getModifyResponseLength()
    {
        return modifyResponseLength;
    }
}

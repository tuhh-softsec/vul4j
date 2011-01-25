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
package org.apache.directory.shared.ldap.message.decorators;


import org.apache.directory.shared.ldap.model.message.ModifyDnResponse;


/**
 * Doc me!
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ModifyDnResponseDecorator extends MessageDecorator
{
    /** The encoded modifyDnResponse length */
    private int modifyDnResponseLength;


    /**
     * Makes a ModifyDnResponse encodable.
     *
     * @param decoratedMessage the decorated ModifyDnResponse
     */
    public ModifyDnResponseDecorator( ModifyDnResponse decoratedMessage )
    {
        super( decoratedMessage );
    }


    public ModifyDnResponse getModifyDnResponse()
    {
        return ( ModifyDnResponse ) getMessage();
    }


    /**
     * @param modifyDnResponseLength The encoded ModifyDnResponse's length
     */
    public void setModifyDnResponseLength( int modifyDnResponseLength )
    {
        this.modifyDnResponseLength = modifyDnResponseLength;
    }


    /**
     * Stores the encoded length for the ModifyDnResponse
     * @return The encoded length
     */
    public int getModifyDnResponseLength()
    {
        return modifyDnResponseLength;
    }
}

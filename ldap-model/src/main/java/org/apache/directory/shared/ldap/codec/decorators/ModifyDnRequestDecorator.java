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


import org.apache.directory.shared.ldap.model.message.ModifyDnRequest;


/**
 * A decorator for the ModifyDnRequest message
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ModifyDnRequestDecorator extends MessageDecorator
{
    /** The modify Dn request length */
    private int modifyDnRequestLength;


    /**
     * Makes a ModifyDnRequest encodable.
     *
     * @param decoratedMessage the decorated ModifyDnRequest
     */
    public ModifyDnRequestDecorator( ModifyDnRequest decoratedMessage )
    {
        super( decoratedMessage );
    }


    /**
     * @return The decorated ModifyDnRequest
     */
    public ModifyDnRequest getModifyDnRequest()
    {
        return ( ModifyDnRequest ) getMessage();
    }


    /**
     * @param modifyDnRequestLength The encoded ModifyDnRequest's length
     */
    public void setModifyDnRequestLength( int modifyDnRequestLength )
    {
        this.modifyDnRequestLength = modifyDnRequestLength;
    }


    /**
     * Stores the encoded length for the ModifyDnRequest
     * @return the encoded length
     */
    public int getModifyDnResponseLength()
    {
        return modifyDnRequestLength;
    }
}

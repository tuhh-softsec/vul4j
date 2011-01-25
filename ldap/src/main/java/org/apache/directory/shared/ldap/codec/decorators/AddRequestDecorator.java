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


import java.util.List;

import org.apache.directory.shared.ldap.model.message.AddRequest;


/**
 * A decorator for the AddRequest message
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AddRequestDecorator extends MessageDecorator
{
    /** The add request length */
    private int addRequestLength;

    /** The Entry length */
    private int entryLength;

    /** The list of all attributes length */
    private List<Integer> attributesLength;

    /** The list of all vals length */
    private List<Integer> valuesLength;


    /**
     * Makes a AddRequest encodable.
     *
     * @param decoratedMessage the decorated AddRequest
     */
    public AddRequestDecorator( AddRequest decoratedMessage )
    {
        super( decoratedMessage );
    }


    /**
     * @return The decorated AddRequest
     */
    public AddRequest getAddRequest()
    {
        return ( AddRequest ) getMessage();
    }


    /**
     * Stores the encoded length for the AddRequest
     * @param addRequestLength The encoded length
     */
    public void setAddRequestLength( int addRequestLength )
    {
        this.addRequestLength = addRequestLength;
    }


    /**
     * @return The encoded AddRequest's length
     */
    public int getAddRequestLength()
    {
        return addRequestLength;
    }


    /**
     * Stores the encoded length for the Entry
     * @param entryLength The encoded length
     */
    public void setEntryLength( int entryLength )
    {
        this.entryLength = entryLength;
    }


    /**
     * @return The encoded Entry's length
     */
    public int getEntryLength()
    {
        return entryLength;
    }


    /**
     * Stores the encoded length for the attributes
     * @param attributesLength The encoded length
     */
    public void setAttributesLength( List<Integer> attributesLength )
    {
        this.attributesLength = attributesLength;
    }


    /**
     * @return The encoded values length
     */
    public List<Integer> getAttributesLength()
    {
        return attributesLength;
    }


    /**
     * Stores the encoded length for the values
     * @param valuesLength The encoded length
     */
    public void setValuesLength( List<Integer> valuesLength )
    {
        this.valuesLength = valuesLength;
    }


    /**
     * @return The encoded values length
     */
    public List<Integer> getValuesLength()
    {
        return valuesLength;
    }
}

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
package org.apache.directory.shared.ldap.aci.protectedItem;

import org.apache.directory.shared.ldap.schema.AttributeType;


/**
 * An element of {@link MaxValueCount}.
 */
public class MaxValueCountElem
{
    /** The targeted AttributeType */
    private AttributeType attributeType;

    /** The maximum number of accepted values for this attributeType */
    private int maxCount;


    /**
     * Creates a new instance.
     * 
     * @param attributeType the attribute ID to limit the maximum count
     * @param maxCount the maximum count of the attribute allowed
     */

    public MaxValueCountElem( AttributeType attributeType, int maxCount )
    {
        this.attributeType = attributeType;
        this.maxCount = maxCount;
    }


    /**
     * Returns the attribute to limit the maximum count.
     */
    public AttributeType getAttributeType()
    {
        return attributeType;
    }


    /**
     * Returns the maximum count of the attribute allowed.
     */
    public int getMaxCount()
    {
        return maxCount;
    }


    /**
     * @see Object#toString()
     */
    public String toString()
    {
        return "{ type " + attributeType.getName() + ", maxCount " + maxCount + " }";
    }
}

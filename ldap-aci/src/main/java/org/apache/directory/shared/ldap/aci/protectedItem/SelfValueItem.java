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

import java.util.Set;

/**
 * The attribute value assertion corresponding to the current requestor. The
 * protected item selfValue applies only when the access controls are to be
 * applied with respect to a specific authenticated user. It can only apply
 * in the specific case where the attribute specified is of DN and the
 * attribute value within the specified attribute matches the DN of the
 * originator of the operation.
 */
public class SelfValueItem extends AbstractAttributeTypeProtectedItem
{
    /**
     * Creates a new instance.
     * 
     * @param attributeTypes the collection of attribute IDs.
     */
    public SelfValueItem( Set<String> attributeTypes )
    {
        super( attributeTypes );
    }


    public String toString()
    {
        return "selfValue " + super.toString();
    }
}


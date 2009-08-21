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
package org.apache.directory.shared.ldap.schema;

/**
 * The SchemaObject types
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public enum SchemaObjectType
{
    ATTRIBUTE_TYPE(0),
    COMPARATOR(1),
    DIT_CONTENT_RULE(2),
    DIT_STRUCTURE_RULE(3),
    LDAP_SYNTAX(4),
    MATCHING_RULE(5),
    MATCHING_RULE_USE(6),
    NAME_FORM(7),
    NORMALIZER(8),
    OBJECT_CLASS(9),
    SYNTAX_CHECKER(10);
    
    /** The inner value*/
    private int value;
    
    /**
     * A private constructor to associated a number to the type
     */
    private SchemaObjectType( int value )
    {
        this.value = value;
    }

    /**
     * @return The numeric value for this type
     */
    public int getValue()
    {
        return value;
    }
}

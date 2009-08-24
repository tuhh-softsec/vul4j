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

package org.apache.directory.shared.ldap.schema.parsers;


import java.util.List;

import org.apache.directory.shared.ldap.schema.SchemaObject;
import org.apache.directory.shared.ldap.schema.SchemaObjectType;


/**
 * RFC 4512 - 4.1.4. Matching Rule Use Description
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class MatchingRuleUseDescription extends SchemaObject
{
    /** The serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** The list of attributes types the matching rule applies to */
    private List<String> applicableAttributes;


    /**
     * Creates a new instance of MatchingRuleUseDescription
     */
    public MatchingRuleUseDescription( String oid )
    {
        super(  SchemaObjectType.MATCHING_RULE_USE, oid );
    }
    

    /**
     * @return The matchingRule's list of Attribute types the MRU applies to
     */
    public List<String> getApplicableAttributes()
    {
        return applicableAttributes;
    }


    /**
     * Set the matchingRule's Attribute types the MRU applies to. description
     *
     * @param applicableAttributes The Attribute types list
     */
    public void setApplicableAttributes( List<String> applicableAttributes )
    {
        this.applicableAttributes = applicableAttributes;
    }
}

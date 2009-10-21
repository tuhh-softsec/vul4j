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
package org.apache.directory.shared.ldap.schema.registries;


import org.apache.directory.shared.ldap.schema.MatchingRule;
import org.apache.directory.shared.ldap.schema.SchemaObjectType;


/**
 * A registry used to track system matchingRules.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class MatchingRuleRegistry extends SchemaObjectRegistry<MatchingRule>
{
    /**
     * Creates a new default MatchingRuleRegistry instance.
     * 
     * @param oidRegistry The global OID registry 
     */
    public MatchingRuleRegistry( OidRegistry oidRegistry )
    {
        super( SchemaObjectType.MATCHING_RULE, oidRegistry );
    }
    
    
    /**
     * Clone the MatchingRuleRegistry
     */
    public MatchingRuleRegistry clone() throws CloneNotSupportedException
    {
        MatchingRuleRegistry clone = (MatchingRuleRegistry)super.clone();
        
        return clone;
    }
    
    
    /**
     *  @return The number of MatchingRule stored
     */
    public int size()
    {
        return oidRegistry.size();
    }
}

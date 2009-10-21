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

import org.apache.directory.shared.ldap.schema.LdapSyntax;
import org.apache.directory.shared.ldap.schema.SchemaObjectType;


/**
 * Manages the lookup and registration of LdapSyntaxes within the system by OID.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class LdapSyntaxRegistry extends SchemaObjectRegistry<LdapSyntax>
{
    /**
     * Creates a new default LdapSyntaxRegistry instance.
     * 
     * @param oidRegistry The global OID registry 
     */
    public LdapSyntaxRegistry( OidRegistry oidRegistry )
    {
        super( SchemaObjectType.LDAP_SYNTAX, oidRegistry );
    }
    
    
    /**
     * Clone the LdapSyntaxRegistry
     */
    public LdapSyntaxRegistry clone() throws CloneNotSupportedException
    {
        LdapSyntaxRegistry clone = (LdapSyntaxRegistry)super.clone();
        
        return clone;
    }
    
    
    /**
     *  @return The number of Syntaxes stored
     */
    public int size()
    {
        return oidRegistry.size();
    }
}

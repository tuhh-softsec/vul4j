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


import java.util.Iterator;

import javax.naming.NamingException;

import org.apache.directory.shared.ldap.schema.SyntaxChecker;
import org.apache.directory.shared.ldap.schema.parsers.SyntaxCheckerDescription;


/**
 * SyntaxChecker registry component's service interface.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public interface SyntaxCheckerRegistry extends SchemaObjectRegistry<SyntaxChecker>
{
    /**
     * Registers a SyntaxChecker with this registry.
     * 
     * @param description the syntaxCheckerDescription for this syntaxChecker
     * @param syntaxChecker the SyntaxChecker to register
     * @throws NamingException if the SyntaxChecker is already registered or the
     *      registration operation is not supported
     */
    void register( SyntaxCheckerDescription description, SyntaxChecker syntaxChecker ) throws NamingException;


    /**
     * Get's an iterator over all the syntaxCheckerDescriptions associated with this registry.
     * 
     * @return an Iterator over all the syntaxCheckerDescriptions
     */
    Iterator<SyntaxCheckerDescription> syntaxCheckerDescriptionIterator();


    /**
     * Unregisters all syntaxCheckers defined for a specific schema from
     * this registry.
     * 
     * @param schemaName the name of the schema whose syntaxCheckers will be removed
     */
    void unregisterSchemaElements( String schemaName );


    /**
     * Renames the schemaName associated with entities within this 
     * registry to a new schema name.
     * 
     * @param originalSchemaName the original schema name
     * @param newSchemaName the new name to give to the schema
     */
    void renameSchema( String originalSchemaName, String newSchemaName );
}

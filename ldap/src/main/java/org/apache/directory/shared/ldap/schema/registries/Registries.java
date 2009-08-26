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

import org.apache.directory.server.schema.bootstrap.Schema;
import org.apache.directory.server.schema.registries.DefaultAttributeTypeRegistry;
import org.apache.directory.server.schema.registries.DefaultComparatorRegistry;
import org.apache.directory.server.schema.registries.DefaultDitContentRuleRegistry;
import org.apache.directory.server.schema.registries.DefaultDitStructureRuleRegistry;
import org.apache.directory.server.schema.registries.DefaultLdapSyntaxRegistry;
import org.apache.directory.server.schema.registries.DefaultMatchingRuleRegistry;
import org.apache.directory.server.schema.registries.DefaultMatchingRuleUseRegistry;
import org.apache.directory.server.schema.registries.DefaultNameFormRegistry;
import org.apache.directory.server.schema.registries.DefaultNormalizerRegistry;
import org.apache.directory.server.schema.registries.DefaultObjectClassRegistry;
import org.apache.directory.server.schema.registries.DefaultSyntaxCheckerRegistry;
import org.apache.directory.server.schema.registries.SchemaLoader;
import org.apache.directory.server.schema.registries.SchemaLoaderListener;


/**
 * Document this class.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class Registries
{
    /** The AttributeType registry */
    protected AttributeTypeRegistry attributeTypeRegistry;
    
    /** The ObjectClass registry */
    protected ObjectClassRegistry objectClassRegistry;

    /** The LdapSyntax registry */
    protected ComparatorRegistry comparatorRegistry;

    /** The DitContentRule registry */
    protected DITContentRuleRegistry ditContentRuleRegistry;

    /** The DitStructureRule registry */
    protected DITStructureRuleRegistry ditStructureRuleRegistry;

    /** The MatchingRule registry */
    protected MatchingRuleRegistry matchingRuleRegistry;

    /** The MatchingRuleUse registry */
    protected MatchingRuleUseRegistry matchingRuleUseRegistry;

    /** The NameForm registry */
    protected NameFormRegistry nameFormRegistry;

    /** The Normalizer registry */
    protected NormalizerRegistry normalizerRegistry;

    /** The OID registry */
    protected OidRegistry oidRegistry;

    /** The SyntaxChecker registry */
    protected SyntaxCheckerRegistry syntaxCheckerRegistry;

    /** The LdapSyntax registry */
    protected LdapSyntaxRegistry ldapSyntaxRegistry;
    
    /**
     * Creates a new instance of Registries.
     *
     * @param oidRegistry
     */
    public Registries( OidRegistry oidRegistry )
    {
        this.oidRegistry = oidRegistry;
        normalizerRegistry = new NormalizerRegistry( oidRegistry );
        comparatorRegistry = new ComparatorRegistry( oidRegistry );
        syntaxCheckerRegistry = new SyntaxCheckerRegistry( oidRegistry );
        ldapSyntaxRegistry = new LdapSyntaxRegistry( oidRegistry );
        matchingRuleRegistry = new MatchingRuleRegistry( oidRegistry );
        attributeTypeRegistry = new AttributeTypeRegistry( oidRegistry );
        objectClassRegistry = new ObjectClassRegistry( oidRegistry );
        ditContentRuleRegistry = new DITContentRuleRegistry( oidRegistry );
        ditStructureRuleRegistry = new DITStructureRuleRegistry( oidRegistry );
        matchingRuleUseRegistry = new MatchingRuleUseRegistry( oidRegistry );
        nameFormRegistry = new NameFormRegistry( oidRegistry );
    }

    
    /**
     * @return The AttributeType registry
     */
    public AttributeTypeRegistry getAttributeTypeRegistry()
    {
        return attributeTypeRegistry;
    }

    
    /**
     * @return The Comparator registry
     */
    public ComparatorRegistry getComparatorRegistry()
    {
        return comparatorRegistry;
    }

    
    /**
     * @return The DITContentRule registry
     */
    public DITContentRuleRegistry getDitContentRuleRegistry()
    {
        return ditContentRuleRegistry;
    }

    
    /**
     * @return The DITStructureRule registry
     */
    public DITStructureRuleRegistry getDitStructureRuleRegistry()
    {
        return ditStructureRuleRegistry;
    }

    
    /**
     * @return The MatchingRule registry
     */
    public MatchingRuleRegistry getMatchingRuleRegistry()
    {
        return matchingRuleRegistry;
    }

    
    /**
     * @return The MatchingRuleUse registry
     */
    public MatchingRuleUseRegistry getMatchingRuleUseRegistry()
    {
        return matchingRuleUseRegistry;
    }

    
    /**
     * @return The NameForm registry
     */
    public NameFormRegistry getNameFormRegistry()
    {
        return nameFormRegistry;
    }

    
    /**
     * @return The Normalizer registry
     */
    public NormalizerRegistry getNormalizerRegistry()
    {
        return normalizerRegistry;
    }

    
    /**
     * @return The ObjectClass registry
     */
    public ObjectClassRegistry getObjectClassRegistry()
    {
        return objectClassRegistry;
    }

    
    /**
     * @return The getOid registry
     */
    public OidRegistry getOidRegistry()
    {
        return oidRegistry;
    }

    
    /**
     * @return The SyntaxChecker registry
     */
    public SyntaxCheckerRegistry getSyntaxCheckerRegistry()
    {
        return syntaxCheckerRegistry;
    }

    
    /**
     * @return The LdapSyntax registry
     */
    public LdapSyntaxRegistry getLdapSyntaxRegistry()
    {
        return ldapSyntaxRegistry;
    }
    

    //List<Throwable> checkRefInteg();

    //Schema getSchema( String schemaName );

    /**
     * Removes a schema from the loaded set without unloading the schema.
     * This should be used ONLY when an enabled schema is deleted.
     * 
     * @param schemaName the name of the schema to remove
     */
    //void removeFromLoadedSet( String schemaName );
    
    /**
     * Adds a schema to the loaded set but does not load the schema in 
     * question.  This may be a temporary fix for new schemas being added
     * which are enabled yet do not have any schema entities associated 
     * with them to load.  In this case all objects added under this 
     * schema will load when added instead of in bulk.
     * 
     * @param schema the schema object to add to the loaded set.
     */
    //void addToLoadedSet( Schema schema );
}

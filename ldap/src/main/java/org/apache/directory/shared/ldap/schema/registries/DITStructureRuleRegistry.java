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


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.directory.shared.ldap.schema.DITStructureRule;
import org.apache.directory.shared.ldap.schema.SchemaObject;
import org.apache.directory.shared.ldap.schema.SchemaObjectType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An DITStructureRule registry service interface.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class DITStructureRuleRegistry extends SchemaObjectRegistry<DITStructureRule>
{
    /** static class logger */
    private static final Logger LOG = LoggerFactory.getLogger( DITStructureRuleRegistry.class );

    /** A speedup for debug */
    private static final boolean DEBUG = LOG.isDebugEnabled();
    
    /** a map of DITStructureRule looked up by RuleId */
    protected Map<Integer, DITStructureRule> byRuleId;
    
    /**
     * Creates a new default NormalizerRegistry instance.
     * 
     * @param oidRegistry The global OID registry 
     */
    public DITStructureRuleRegistry( OidRegistry oidRegistry )
    {
        super( SchemaObjectType.DIT_STRUCTURE_RULE, oidRegistry );
        byRuleId = new HashMap<Integer, DITStructureRule>();
    }


    /**
     * Checks to see if an DITStructureRule exists in the registry, by its
     * ruleId. 
     * 
     * @param oid the object identifier or name of the DITStructureRule
     * @return true if a DITStructureRule definition exists for the ruleId, false
     * otherwise
     */
    public boolean contains( int ruleId )
    {
        return byRuleId.containsKey( ruleId );
    }

    
    /**
     * Gets an iterator over the registered descriptions in the registry.
     *
     * @return an Iterator of descriptions
     */
    public Iterator<DITStructureRule> iterator()
    {
        return byRuleId.values().iterator();
    }
    
    
    /**
     * Gets an iterator over the registered ruleId in the registry.
     *
     * @return an Iterator of ruleId
     */
    public Iterator<Integer> ruleIdIterator()
    {
        return byRuleId.keySet().iterator();
    }
    
    
    /**
     * Gets the name of the schema this schema object is associated with.
     *
     * @param id the object identifier or the name
     * @return the schema name
     * @throws NamingException if the schema object does not exist
     */
    public String getSchemaName( int ruleId ) throws NamingException
    {
        DITStructureRule ditStructureRule = byRuleId.get( ruleId );

        if ( ditStructureRule != null )
        {
            return ditStructureRule.getSchemaName();
        }
        
        String msg = "RuleId " + ruleId + " not found in ruleId to schema name map!";
        LOG.warn( msg );
        throw new NamingException( msg );
    }

    
    /**
     * Registers a new DITStructureRule with this registry.
     *
     * @param ditStructureRule the DITStructureRule to register
     * @throws NamingException if the DITStructureRule is already registered or
     * the registration operation is not supported
     */
    public void register( DITStructureRule ditStructureRule ) throws NamingException
    {
        int ruleId = ditStructureRule.getRuleId();
        
        if ( byRuleId.containsKey( ruleId ) )
        {
            String msg = "DITStructureRule with RuleId " + ruleId + " already registered!";
            LOG.warn( msg );
            throw new NamingException( msg );
        }

        byRuleId.put( ruleId, ditStructureRule );
        
        if ( LOG.isDebugEnabled() )
        {
            LOG.debug( "registered {} for OID {}", ditStructureRule, ruleId );
        }
    }

    
    /**
     * Looks up an dITStructureRule by its unique Object IDentifier or by its
     * name.
     * 
     * @param ruleId the rule identifier for the DITStructureRule
     * @return the DITStructureRule instance for rule identifier
     * @throws NamingException if the DITStructureRule does not exist
     */
    public DITStructureRule lookup( int ruleId ) throws NamingException
    {
        DITStructureRule ditStructureRule = byRuleId.get( ruleId );

        if ( ditStructureRule == null )
        {
            String msg = "DITStructureRule for ruleId " + ruleId + " does not exist!";
            LOG.debug( msg );
            throw new NamingException( msg );
        }

        if ( DEBUG )
        {
            LOG.debug( "Found {} with ruleId: {}", ditStructureRule, ruleId );
        }
        
        return ditStructureRule;
    }


    /**
     * Unregisters a DITStructureRule using it's rule identifier. 
     * 
     * @param ruleId the rule identifier for the DITStructureRule to unregister
     * @throws NamingException if no such DITStructureRule exists
     */
    public void unregister( int ruleId ) throws NamingException
    {
        DITStructureRule ditStructureRule = byRuleId.remove( ruleId );
        
        if ( DEBUG )
        {
            LOG.debug( "Removed {} with ruleId {} from the registry", ditStructureRule, ruleId );
        }
    }
    
    
    /**
     * Unregisters all DITStructureRules defined for a specific schema from
     * this registry.
     * 
     * @param schemaName the name of the schema whose syntaxCheckers will be removed from
     */
    public void unregisterSchemaElements( String schemaName )
    {
        if ( schemaName == null )
        {
            return;
        }
        
        // Loop on all the SchemaObjects stored and remove those associated
        // with the give schemaName
        for ( DITStructureRule ditStructureRule : this )
        {
            if ( schemaName.equalsIgnoreCase( ditStructureRule.getSchemaName() ) )
            {
                int ruleId = ditStructureRule.getRuleId();
                SchemaObject removed = byRuleId.remove( ruleId );
                
                if ( DEBUG )
                {
                    LOG.debug( "Removed {} with ruleId {} from the registry", removed, ruleId );
                }
            }
        }
    }

    
    /**
     * Modify all the DITStructureRule using a schemaName when this name changes.
     *
     * @param originalSchemaName The original Schema name
     * @param newSchemaName The new Schema name
     */
    public void renameSchema( String originalSchemaName, String newSchemaName )
    {
        // Loop on all the SchemaObjects stored and remove those associated
        // with the give schemaName
        for ( DITStructureRule ditStructureRule : this )
        {
            if ( originalSchemaName.equalsIgnoreCase( ditStructureRule.getSchemaName() ) )
            {
                ditStructureRule.setSchemaName( newSchemaName );

                if ( DEBUG )
                {
                    LOG.debug( "Renamed {} schemaName to {}", ditStructureRule, newSchemaName );
                }
            }
        }
    }
    
    
    /**
     * Clone the DITStructureRuleRegistry
     */
    public DITStructureRuleRegistry clone() throws CloneNotSupportedException
    {
        DITStructureRuleRegistry clone = (DITStructureRuleRegistry)super.clone();
        
        // Clone the RuleId map
        clone.byRuleId = new HashMap<Integer, DITStructureRule>();
        
        return clone;
    }
}

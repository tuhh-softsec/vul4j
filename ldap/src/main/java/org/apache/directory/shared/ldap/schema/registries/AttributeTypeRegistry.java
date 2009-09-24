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


import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.NamingException;
import javax.naming.directory.NoSuchAttributeException;

import org.apache.directory.shared.ldap.schema.AttributeType;
import org.apache.directory.shared.ldap.schema.MatchingRule;
import org.apache.directory.shared.ldap.schema.SchemaObject;
import org.apache.directory.shared.ldap.schema.SchemaObjectType;
import org.apache.directory.shared.ldap.schema.normalizers.NoOpNormalizer;
import org.apache.directory.shared.ldap.schema.normalizers.OidNormalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An AttributeType registry service interface.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class AttributeTypeRegistry extends SchemaObjectRegistry<AttributeType>
{
    /** static class logger */
    private static final Logger LOG = LoggerFactory.getLogger( AttributeTypeRegistry.class );

    /** Speedup for DEBUG mode */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();
    
    /** cached Oid/normalizer mapping */
    private transient Map<String, OidNormalizer> oidNormalizerMap;
    
    /** maps OIDs to a Set of descendants for that OID */
    private final Map<String,Set<AttributeType>> oidToDescendantSet;

    
    /**
     * Creates a new default AttributeTypeRegistry instance.
     */
    public AttributeTypeRegistry( OidRegistry oidRegistry )
    {
        super( SchemaObjectType.ATTRIBUTE_TYPE, oidRegistry );
        oidNormalizerMap = new ConcurrentHashMap<String, OidNormalizer>();
        oidToDescendantSet= new ConcurrentHashMap<String,Set<AttributeType>>();
    }
    
    
    /**
     * Gets an oid/name to normalizer mapping used to normalize distinguished 
     * names.
     *
     * @return a map of OID Strings to OidNormalizer instances
     */
    public Map<String, OidNormalizer> getNormalizerMapping()
    {
        return Collections.unmodifiableMap( oidNormalizerMap );
    }
    
    
    /**
     * Quick lookup to see if an attribute has descendants.
     * 
     * @param ancestorId the name alias or OID for an attributeType
     * @return an Iterator over the AttributeTypes which have the ancestor
     * within their superior chain to the top
     * @throws NamingException if the ancestor attributeType cannot be 
     * discerned from the ancestorId supplied
     */
    public boolean hasDescendants( String ancestorId ) throws NamingException
    {
        try
        {
            String oid = getOidByName( ancestorId );
            Set<AttributeType> descendants = oidToDescendantSet.get( oid );
            return (descendants != null) && !descendants.isEmpty();
        }
        catch ( NamingException ne )
        {
            throw new NoSuchAttributeException( ne.getMessage() );
        }
    }
    
    
    /**
     * Get's an iterator over the set of descendant attributeTypes for
     * some ancestor's name alias or their OID.
     * 
     * @param ancestorId the name alias or OID for an attributeType
     * @return an Iterator over the AttributeTypes which have the ancestor
     * within their superior chain to the top
     * @throws NamingException if the ancestor attributeType cannot be 
     * discerned from the ancestorId supplied
     */
    @SuppressWarnings("unchecked")
    public Iterator<AttributeType> descendants( String ancestorId ) throws NamingException
    {
        try
        {
            String oid = getOidByName( ancestorId );
            Set<AttributeType> descendants = oidToDescendantSet.get( oid );
            
            if ( descendants == null )
            {
                return Collections.EMPTY_SET.iterator();
            }
            
            return descendants.iterator();
        }
        catch ( NamingException ne )
        {
            throw new NoSuchAttributeException( ne.getMessage() );
        }
    }
    
    
    /**
     * Registers a new AttributeType with this registry.
     *
     * @param attributeType the AttributeType to register
     * @throws NamingException if the AttributeType is already registered or
     * the registration operation is not supported
     */
    public void register( AttributeType attributeType ) throws NamingException
    {
        try
        {
            super.register( attributeType );
            
            // Inject the attributeType into the oid/normalizer map
            addMappingFor( attributeType );
    
            // Register this AttributeType into the Descendant map
            registerDescendants( attributeType, attributeType.getSuperior() );
            
            // Internally associate the OID to the registered AttributeType
            if ( IS_DEBUG )
            {
                LOG.debug( "registred attributeType: {}", attributeType );
            }
        }
        catch ( NamingException ne )
        {
            throw new NoSuchAttributeException( ne.getMessage() );
        }
    }

    
    /**
     * Store the AttributeType into a map associating an AttributeType to its
     * descendants.
     * 
     * @param attributeType The attributeType to register
     * @throws NamingException If something went wrong
     */
    public void registerDescendants( AttributeType attributeType, AttributeType ancestor ) 
        throws NamingException
    {
        // add this attribute to descendant list of other attributes in superior chain
        if ( ancestor == null )
        {
            return;
        }
        
        // Get the ancestor's descendant, if any
        Set<AttributeType> descendants = oidToDescendantSet.get( ancestor.getOid() );

        // Initialize the descendant Set to store the descendants for the attributeType
        if ( descendants == null )
        {
            descendants = new HashSet<AttributeType>( 1 );
            oidToDescendantSet.put( ancestor.getOid(), descendants );
        }
        
        // Add the current type as a descendant
        descendants.add( attributeType );
        
        try
        {
            // And recurse until we reach the top of the hierarchy
            registerDescendants( attributeType, ancestor.getSuperior() );
        }
        catch ( NamingException ne )
        {
            throw new NoSuchAttributeException( ne.getMessage() );
        }
    }
    
    
    /**
     * Remove the AttributeType from the map associating an AttributeType to its
     * descendants.
     * 
     * @param attributeType The attributeType to unregister
     * @param ancestor its ancestor 
     * @throws NamingException If something went wrong
     */
    public void unregisterDescendants( AttributeType attributeType, AttributeType ancestor ) 
        throws NamingException
    {
        // add this attribute to descendant list of other attributes in superior chain
        if ( ancestor == null )
        {
            return;
        }
        
        // Get the ancestor's descendant, if any
        Set<AttributeType> descendants = oidToDescendantSet.get( ancestor.getOid() );

        if ( descendants != null )
        {
            descendants.remove( attributeType );
            
            if ( descendants.size() == 0 )
            {
                oidToDescendantSet.remove( descendants );
            }
        }
        
        try
        {
            // And recurse until we reach the top of the hierarchy
            unregisterDescendants( attributeType, ancestor.getSuperior() );
        }
        catch ( NamingException ne )
        {
            throw new NoSuchAttributeException( ne.getMessage() );
        }
    }
    
    
    /**
     * Removes the AttributeType registered with this registry.
     * 
     * @param numericOid the numeric identifier
     * @throws NamingException if the numeric identifier is invalid
     */
    public AttributeType unregister( String numericOid ) throws NamingException
    {
        try
        {
            AttributeType removed = super.unregister( numericOid );
    
            removeMappingFor( removed );
            
            // Deleting an AT which might be used as a superior means we have
            // to recursively update the descendant map. We also have to remove
            // the at.oid -> descendant relation
            oidToDescendantSet.remove( numericOid );
            
            // Now recurse if needed
            unregisterDescendants( removed, removed.getSuperior() );
            
            return removed;
        }
        catch ( NamingException ne )
        {
            throw new NoSuchAttributeException( ne.getMessage() );
        }
    }

    
    /**
     * Add a new Oid/Normalizer couple in the OidNormalizer map
     */
    private void addMappingFor( AttributeType attributeType ) throws NamingException
    {
        try
        {
            MatchingRule matchingRule = attributeType.getEquality();
            OidNormalizer oidNormalizer;
            String oid = attributeType.getOid();
    
            if ( matchingRule == null )
            {
                LOG.debug( "Attribute {} does not have normalizer : using NoopNormalizer", attributeType.getName() );
                oidNormalizer = new OidNormalizer( oid, new NoOpNormalizer( attributeType.getOid() ) );
            }
            else
            {
                oidNormalizer = new OidNormalizer( oid, matchingRule.getNormalizer() );
            }
    
            oidNormalizerMap.put( oid, oidNormalizer );
            
            // Also inject the attributeType's short names in the map
            for ( String name : attributeType.getNames() )
            {
                oidNormalizerMap.put( name.toLowerCase(), oidNormalizer );
            }
        }
        catch ( NamingException ne )
        {
            throw new NoSuchAttributeException( ne.getMessage() );
        }
    }


    /**
     * Remove the AttributeType normalizer from the OidNormalizer map 
     */
    private void removeMappingFor( SchemaObject attributeType )
    {
        if ( attributeType == null )
        {
            return;
        }
        
        oidNormalizerMap.remove( attributeType.getOid() );

        // We also have to remove all the short names for this attribute
        for ( String name : attributeType.getNames() )
        {
            oidNormalizerMap.remove( name.toLowerCase() );
        }
    }


    /**
     * {@inheritDoc}
     */
    public AttributeType lookup( String oid ) throws NamingException
    {
        try
        {
           return  super.lookup( oid );
        }
        catch ( NamingException ne )
        {
            throw new NoSuchAttributeException( ne.getMessage() );
        }
    }
    
    public String toString()
    {
        return byName.toString();
    }
}

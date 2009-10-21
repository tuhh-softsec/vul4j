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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.directory.NoSuchAttributeException;

import org.apache.directory.shared.ldap.schema.ObjectClass;
import org.apache.directory.shared.ldap.schema.SchemaObjectType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * ObjectClass registry service interface.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class ObjectClassRegistry extends SchemaObjectRegistry<ObjectClass>
{
    /** static class logger */
    private static final Logger LOG = LoggerFactory.getLogger( ObjectClassRegistry.class );

    /** Speedup for DEBUG mode */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();

    /** maps OIDs to a Set of descendants for that OID */
    private Map<String,Set<ObjectClass>> oidToDescendants;

    /**
     * Creates a new default ObjectClassRegistry instance.
     * 
     * @param oidRegistry The global OID registry 
     */
    public ObjectClassRegistry( OidRegistry oidRegistry )
    {
        super( SchemaObjectType.OBJECT_CLASS, oidRegistry );
        oidToDescendants = new HashMap<String,Set<ObjectClass>>();
    }
    
    
    /**
     * Quick lookup to see if an objectClass has descendants.
     * 
     * @param ancestorId the name alias or OID for an ObjectClass
     * @return an Iterator over the ObjectClasses which have the ancestor
     * within their superior chain to the top
     * @throws NamingException if the ancestor ObjectClass cannot be 
     * discerned from the ancestorId supplied
     */
    public boolean hasDescendants( String ancestorId ) throws NamingException
    {
        try
        {
            String oid = getOidByName( ancestorId );
            Set<ObjectClass> descendants = oidToDescendants.get( oid );
            return (descendants != null) && !descendants.isEmpty();
        }
        catch ( NamingException ne )
        {
            throw new NoSuchAttributeException( ne.getMessage() );
        }
    }
    
    
    /**
     * Get's an iterator over the set of descendant ObjectClasses for
     * some ancestor's name alias or their OID.
     * 
     * @param ancestorId the name alias or OID for an ObjectClass
     * @return an Iterator over the ObjectClasses which have the ancestor
     * within their superior chain to the top
     * @throws NamingException if the ancestor ObjectClass cannot be 
     * discerned from the ancestorId supplied
     */
    @SuppressWarnings("unchecked")
    public Iterator<ObjectClass> descendants( String ancestorId ) throws NamingException
    {
        try
        {
            String oid = getOidByName( ancestorId );
            Set<ObjectClass> descendants = oidToDescendants.get( oid );
            
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
     * Store the ObjectClass into a map associating an ObjectClass to its
     * descendants.
     * 
     * @param attributeType The ObjectClass to register
     * @throws NamingException If something went wrong
     */
    public void registerDescendants( ObjectClass objectClass, List<ObjectClass> ancestors ) 
        throws NamingException
    {
        // add this attribute to descendant list of other attributes in superior chain
        if ( ( ancestors == null ) || ( ancestors.size() == 0 ) ) 
        {
            return;
        }
        
        for ( ObjectClass ancestor : ancestors )
        {
            // Get the ancestor's descendant, if any
            Set<ObjectClass> descendants = oidToDescendants.get( ancestor.getOid() );
    
            // Initialize the descendant Set to store the descendants for the attributeType
            if ( descendants == null )
            {
                descendants = new HashSet<ObjectClass>( 1 );
                oidToDescendants.put( ancestor.getOid(), descendants );
            }
            
            // Add the current ObjectClass as a descendant
            descendants.add( objectClass );
            
            try
            {
                // And recurse until we reach the top of the hierarchy
                registerDescendants( objectClass, ancestor.getSuperiors() );
            }
            catch ( NamingException ne )
            {
                throw new NoSuchAttributeException( ne.getMessage() );
            }
        }
    }
    
    
    /**
     * Remove the ObjectClass from the map associating an ObjectClass to its
     * descendants.
     * 
     * @param attributeType The ObjectClass to unregister
     * @param ancestor its ancestor 
     * @throws NamingException If something went wrong
     */
    public void unregisterDescendants( ObjectClass attributeType, List<ObjectClass> ancestors ) 
        throws NamingException
    {
        // add this attribute to descendant list of other attributes in superior chain
        if ( ( ancestors == null ) || ( ancestors.size() == 0 ) ) 
        {
            return;
        }
        
        for ( ObjectClass ancestor : ancestors )
        {
            // Get the ancestor's descendant, if any
            Set<ObjectClass> descendants = oidToDescendants.get( ancestor.getOid() );
    
            if ( descendants != null )
            {
                descendants.remove( attributeType );
                
                if ( descendants.size() == 0 )
                {
                    oidToDescendants.remove( descendants );
                }
            }
            
            try
            {
                // And recurse until we reach the top of the hierarchy
                unregisterDescendants( attributeType, ancestor.getSuperiors() );
            }
            catch ( NamingException ne )
            {
                throw new NoSuchAttributeException( ne.getMessage() );
            }
        }
    }
    
    
    /**
     * Registers a new ObjectClass with this registry.
     *
     * @param objectClass the ObjectClass to register
     * @throws NamingException if the ObjectClass is already registered or
     * the registration operation is not supported
     */
    public void register( ObjectClass objectClass ) throws NamingException
    {
        try
        {
            super.register( objectClass );
            
            // Register this ObjectClass into the Descendant map
            registerDescendants( objectClass, objectClass.getSuperiors() );
            
            // Internally associate the OID to the registered AttributeType
            if ( IS_DEBUG )
            {
                LOG.debug( "registred objectClass: {}", objectClass );
            }
        }
        catch ( NamingException ne )
        {
            throw new NoSuchAttributeException( ne.getMessage() );
        }
    }
    
    
    /**
     * Removes the ObjectClass registered with this registry.
     * 
     * @param numericOid the numeric identifier
     * @throws NamingException if the numeric identifier is invalid
     */
    public ObjectClass unregister( String numericOid ) throws NamingException
    {
        try
        {
            ObjectClass removed = super.unregister( numericOid );
    
            // Deleting an ObjectClass which might be used as a superior means we have
            // to recursively update the descendant map. We also have to remove
            // the at.oid -> descendant relation
            oidToDescendants.remove( numericOid );
            
            // Now recurse if needed
            unregisterDescendants( removed, removed.getSuperiors() );
            
            return removed;
        }
        catch ( NamingException ne )
        {
            throw new NoSuchAttributeException( ne.getMessage() );
        }
    }
    
    
    /**
     * Clone the ObjectClassRegistry
     */
    public ObjectClassRegistry clone() throws CloneNotSupportedException
    {
        ObjectClassRegistry clone = (ObjectClassRegistry)super.clone();
        
        // Clone the oidToDescendantSet (will be empty)
        clone.oidToDescendants = new HashMap<String, Set<ObjectClass>>();
        
        return clone;
    }
    
    
    /**
     *  @return The number of ObjectClasses stored
     */
    public int size()
    {
        return oidRegistry.size();
    }
}

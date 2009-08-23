/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.shared.ldap.schema.registries;


import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.NamingException;

import org.apache.directory.shared.asn1.primitives.OID;
import org.apache.directory.shared.ldap.schema.SchemaObject;
import org.apache.directory.shared.ldap.schema.SchemaObjectType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Common schema object registry interface.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemaObjectRegistry<T extends SchemaObject, U extends SchemaObject> implements Iterable<T>
{
    /** static class logger */
    private static final Logger LOG = LoggerFactory.getLogger( SchemaObjectRegistry.class );

    /** A speedup for debug */
    private static final boolean DEBUG = LOG.isDebugEnabled();
    
    /** a map of Normalizers looked up by OID or Name */
    protected final Map<String, T> byOid;
    
    /** maps an OID to a SchemaObject Description */
    private final Map<String, U> oidToDescription;

    
    /** The SchemaObject type */
    protected SchemaObjectType type;
    
    
    /**
     * Creates a new SchemaObjectRegistry instance.
     */
    protected SchemaObjectRegistry( SchemaObjectType schemaObjectType )
    {
        byOid = new ConcurrentHashMap<String, T>();
        oidToDescription = new ConcurrentHashMap<String, U>();
        type = schemaObjectType;
    }
    
    
    /**
     * Checks to see if an SchemaObject exists in the registry, by its
     * OID or name. 
     * 
     * @param oid the object identifier or name of the SchemaObject
     * @return true if a SchemaObject definition exists for the oid, false
     * otherwise
     */
    public boolean contains( String oid )
    {
        return byOid.containsKey( oid );
    }
    
    
    /**
     * Gets the name of the schema this schema object is associated with.
     *
     * @param id the object identifier or the name
     * @return the schema name
     * @throws NamingException if the schema object does not exist
     */
    public String getSchemaName( String oid ) throws NamingException
    {
        if ( ! OID.isOID( oid ) )
        {
            String msg = "Looks like the arg is not a numeric OID";
            LOG.warn( msg );
            throw new NamingException( msg );
        }
        
        SchemaObject schemaObject = byOid.get( oid );

        if ( schemaObject != null )
        {
            return schemaObject.getSchemaName();
        }
        
        String msg = "OID " + oid + " not found in oid to schema name map!";
        LOG.warn( msg );
        throw new NamingException( msg );
    }

    
    /**
     * Modify all the SchemaObject using a schemaName when this name changes.
     *
     * @param originalSchemaName The original Schema name
     * @param newSchemaName The new Schema name
     */
    public void renameSchema( String originalSchemaName, String newSchemaName )
    {
        // Loop on all the SchemaObjects stored and remove those associated
        // with the give schemaName
        for ( T schemaObject : this )
        {
            if ( originalSchemaName.equalsIgnoreCase( schemaObject.getSchemaName() ) )
            {
                schemaObject.setSchemaName( newSchemaName );
                SchemaObject description = oidToDescription.get( schemaObject.getOid() );
                
                if ( description != null )
                {
                    description.setSchemaName( newSchemaName );
                }

                if ( DEBUG )
                {
                    LOG.debug( "Renamed {} schemaName to {}", schemaObject, newSchemaName );
                }
            }
        }
    }
    
    
    /**
     * Gets an iterator over the registered schema objects in the registry.
     *
     * @return an Iterator of homogeneous schema objects
     */
    public Iterator<T> iterator()
    {
        return byOid.values().iterator();
    }

    
    /**
     * Gets an iterator over the registered schema objects'OID in the registry.
     *
     * @return an Iterator of OIDs
     */
    public Iterator<String> oidsIterator()
    {
        return byOid.keySet().iterator();
    }

    
    /**
     * Gets an iterator over the registered descriptions in the registry.
     *
     * @return an Iterator of descriptions
     */
    public Iterator<U> descriptionsIterator()
    {
        return oidToDescription.values().iterator();
    }

    
    /**
     * Looks up a SchemaObject by its unique Object Identifier or by name.
     *
     * @param oid the object identifier or name
     * @return the SchemaObject instance for the id
     * @throws NamingException if the SchemaObject does not exist
     */
    public T lookup( String oid ) throws NamingException
    {
        T schemaObject = byOid.get( oid );

        if ( schemaObject == null )
        {
            String msg = type.name() + " for OID " + oid + " does not exist!";
            LOG.debug( msg );
            throw new NamingException( msg );
        }

        if ( DEBUG )
        {
            LOG.debug( "Found {} with oid: {}", schemaObject, oid );
        }
        
        return schemaObject;
    }
    
    
    /**
     * Registers a new SchemaObject with this registry.
     *
     * @param schemaObject the SchemaObject to register
     * @throws NamingException if the SchemaObject is already registered or
     * the registration operation is not supported
     */
    public void register( T schemaObject ) throws NamingException
    {
        String oid = schemaObject.getOid();
        
        if ( byOid.containsKey( oid ) )
        {
            String msg = type.name() + " with OID " + oid + " already registered!";
            LOG.warn( msg );
            throw new NamingException( msg );
        }

        byOid.put( oid, schemaObject );
        
        if ( LOG.isDebugEnabled() )
        {
            LOG.debug( "registered {} for OID {}", schemaObject, oid );
        }
    }


    /**
     * Registers a new SchemaObject with this registry.
     *
     * @param schemaObjectDescription The SchemaObject description to register
     * @param schemaObject the SchemaObject to register
     * @throws NamingException if the SchemaObject is already registered or
     * the registration operation is not supported
     */
    public void register( U schemaObjectDescription, T schemaObject ) throws NamingException
    {
        String oid = schemaObject.getOid();
        
        if ( byOid.containsKey( oid ) )
        {
            String msg = type.name() + " already registered for OID " + oid;
            LOG.warn( msg );
            throw new NamingException( msg );
        }

        byOid.put( oid, schemaObject );
        
        // Register the name -> schemaObject couples.
        // We will also add the oid -> schemaObject into this map
        if ( schemaObject.getNames() != null )
        {
            for ( String name : schemaObject.getNames() )
            {
                byOid.put( name, schemaObject );
            }
        }
        
        // And register the description too
        oidToDescription.put( oid, schemaObjectDescription );
        
        if ( DEBUG )
        {
            LOG.debug( "registered {} with oid: {}", type.name(), oid );
        }
    }


    /**
     * Removes the SchemaObject registered with this registry, using its
     * numeric OID.
     * 
     * @param numericOid the numeric identifier
     * @throws NamingException if the numeric identifier is invalid
     */
    public void unregister( String numericOid ) throws NamingException
    {
        if ( !OID.isOID( numericOid ) )
        {
            String msg = "OID " + numericOid + " is not a numeric OID";
            LOG.error( msg );
            throw new NamingException( msg );
        }

        SchemaObject schemaObject = byOid.remove( numericOid );

        if ( DEBUG )
        {
            LOG.debug( "Removed {} with oid {} from the registry", schemaObject, numericOid );
        }
    }
    
    
    /**
     * Unregisters all syntaxCheckers defined for a specific schema from
     * this registry.
     * 
     * @param schemaName the name of the schema whose syntaxCheckers will be removed
     */
    public void unregisterSchemaElements( String schemaName )
    {
        if ( schemaName == null )
        {
            return;
        }
        
        // Loop on all the SchemaObjects stored and remove those associated
        // with the give schemaName
        for ( T schemaObject : this )
        {
            if ( schemaName.equalsIgnoreCase( schemaObject.getSchemaName() ) )
            {
                String oid = schemaObject.getOid();
                SchemaObject removed = byOid.remove( oid );

                if ( DEBUG )
                {
                    LOG.debug( "Removed {} with oid {} from the registry", removed, oid );
                }
            }
        }
    }
}

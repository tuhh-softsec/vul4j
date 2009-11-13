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


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.directory.shared.asn1.primitives.OID;
import org.apache.directory.shared.ldap.schema.SchemaObject;
import org.apache.directory.shared.ldap.schema.SchemaObjectType;
import org.apache.directory.shared.ldap.util.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Common schema object registry interface.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DefaultSchemaObjectRegistry<T extends SchemaObject> implements SchemaObjectRegistry<T>, Iterable<T>, Cloneable
{
    /** static class logger */
    private static final Logger LOG = LoggerFactory.getLogger( DefaultSchemaObjectRegistry.class );

    /** A speedup for debug */
    private static final boolean DEBUG = LOG.isDebugEnabled();
    
    /** a map of SchemaObject looked up by name */
    protected Map<String, T> byName;
    
    /** The SchemaObject type */
    protected SchemaObjectType type;

    /** the global OID Registry */
    protected OidRegistry oidRegistry;
    
    
    /**
     * Creates a new DefaultSchemaObjectRegistry instance.
     */
    protected DefaultSchemaObjectRegistry( SchemaObjectType schemaObjectType, OidRegistry oidRegistry )
    {
        byName = new HashMap<String, T>();
        type = schemaObjectType;
        this.oidRegistry = oidRegistry;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public boolean contains( String oid )
    {
        if ( !byName.containsKey( oid ) )
        {
            return byName.containsKey( StringTools.toLowerCase( oid ) );
        }
        
        return true;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public String getSchemaName( String oid ) throws NamingException
    {
        if ( ! OID.isOID( oid ) )
        {
            String msg = "Looks like the arg is not a numeric OID";
            LOG.warn( msg );
            throw new NamingException( msg );
        }
        
        SchemaObject schemaObject = byName.get( oid );

        if ( schemaObject != null )
        {
            return schemaObject.getSchemaName();
        }
        
        String msg = "OID " + oid + " not found in oid to schema name map!";
        LOG.warn( msg );
        throw new NamingException( msg );
    }

    
    /**
     * {@inheritDoc}
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
                
                if ( DEBUG )
                {
                    LOG.debug( "Renamed {} schemaName to {}", schemaObject, newSchemaName );
                }
            }
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    public Iterator<T> iterator()
    {
        return byName.values().iterator();
    }

    
    /**
     * {@inheritDoc}
     */
    public Iterator<String> oidsIterator()
    {
        return byName.keySet().iterator();
    }

    
    /**
     * {@inheritDoc}
     */
    public T lookup( String oid ) throws NamingException
    {
        if ( oid == null )
        {
            return null;
        }
        
        T schemaObject = byName.get( oid );
        
        if ( schemaObject == null )
        {
            // let's try with trimming and lowercasing now
            schemaObject = byName.get( StringTools.trim( StringTools.toLowerCase( oid ) ) );
        }

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
     * {@inheritDoc}
     */
    public void register( T schemaObject ) throws NamingException
    {
        String oid = schemaObject.getOid();
        
        if ( schemaObject.isEnabled() )
        {
            if ( byName.containsKey( oid ) )
            {
                String msg = type.name() + " with OID " + oid + " already registered!";
                LOG.warn( msg );
                throw new NamingException( msg );
            }
    
            byName.put( oid, schemaObject );
            
            /*
             * add the aliases/names to the name map along with their toLowerCase
             * versions of the name: this is used to make sure name lookups work
             */
            for ( String name : schemaObject.getNames() )
            {
            	byName.put( StringTools.trim( StringTools.toLowerCase( name ) ), schemaObject );
            }
        }
        
        if ( LOG.isDebugEnabled() )
        {
            LOG.debug( "registered " + schemaObject.getName() + " for OID {}", oid );
        }
        
        // And register the oid -> schemaObject relation
        oidRegistry.register( schemaObject );
    }


    /**
     * {@inheritDoc}
     */
    public T unregister( String numericOid ) throws NamingException
    {
        if ( !OID.isOID( numericOid ) )
        {
            String msg = "OID " + numericOid + " is not a numeric OID";
            LOG.error( msg );
            throw new NamingException( msg );
        }

        T schemaObject = byName.remove( numericOid );
        
        for ( String name : schemaObject.getNames() )
        {
            byName.remove( name );
        }
        
        // And remove the SchemaObject from the oidRegistry
        oidRegistry.unregister( numericOid );
        
        if ( DEBUG )
        {
            LOG.debug( "Removed {} with oid {} from the registry", schemaObject, numericOid );
        }
        
        return schemaObject;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void unregisterSchemaElements( String schemaName ) throws NamingException
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
                SchemaObject removed = unregister( oid );
                oidRegistry.unregister( oid );
                
                if ( DEBUG )
                {
                    LOG.debug( "Removed {} with oid {} from the registry", removed, oid );
                }
            }
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    public String getOidByName( String name ) throws NamingException
    {
        T schemaObject = byName.get( name );

    	if ( schemaObject == null )
    	{
    		// last resort before giving up check with lower cased version
        	String lowerCased = name.toLowerCase();
    		
        	schemaObject = byName.get( lowerCased );
        	
        	// ok this name is not for a schema object in the registry
    		if ( schemaObject == null )
    		{
    	        throw new NamingException( "Can't find an OID for the name " + name );
    		}
    	}
    	
    	// we found the schema object by key on the first lookup attempt
        return schemaObject.getOid();
    }


    /**
     * {@inheritDoc}
     */
    public boolean containsName( String name )
    {
    	if ( ! byName.containsKey( name ) )
    	{
    		// last resort before giving up check with lower cased version
        	return byName.containsKey( name.toLowerCase() );
    	}

        return true;
    }
    

    /**
     * {@inheritDoc}
     */
    public DefaultSchemaObjectRegistry<T> clone() throws CloneNotSupportedException
    {
        // Clone the base object
        DefaultSchemaObjectRegistry<T> clone = (DefaultSchemaObjectRegistry<T>)super.clone();
        
        // Clone the byName Map
        clone.byName = new HashMap<String, T>();
        
        for ( String key : byName.keySet() )
        {
            // Clone each SchemaObject
            SchemaObject value = byName.get( key );
            clone.byName.put( key, (T)value.clone() );
        }
        
        // Clone the oidRegistry
        clone.oidRegistry = oidRegistry.clone();
        
        return clone;
    }


    /**
     * {@inheritDoc}
     */
    public SchemaObjectType getType()
    {
        return type;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public int size()
    {
        return 0;
    }
    
    
    /**
     * @see Object#toString()
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append( type ).append( ':' );
        sb.append( StringTools.setToString( byName.keySet() ) );
        return sb.toString();
    }
}

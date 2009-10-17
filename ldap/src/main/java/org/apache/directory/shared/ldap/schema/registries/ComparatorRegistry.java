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


import javax.naming.NamingException;

import org.apache.directory.shared.asn1.primitives.OID;
import org.apache.directory.shared.ldap.schema.LdapComparator;
import org.apache.directory.shared.ldap.schema.SchemaObject;
import org.apache.directory.shared.ldap.schema.SchemaObjectType;
import org.apache.directory.shared.ldap.util.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Comparator registry component's service class.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class ComparatorRegistry extends SchemaObjectRegistry<LdapComparator<?>>
{
    /** static class logger */
    private static final Logger LOG = LoggerFactory.getLogger( ComparatorRegistry.class );

    /** A speedup for debug */
    private static final boolean DEBUG = LOG.isDebugEnabled();
    
    /**
     * Creates a new default ComparatorRegistry instance.
     * 
     * @param oidRegistry The global OID registry 
     */
    public ComparatorRegistry( OidRegistry oidRegistry )
    {
        super( SchemaObjectType.COMPARATOR, oidRegistry );
    }
    
    
    /**
     * Registers a new LdapComparator with this registry.
     *
     * @param comparator the LdapComparator to register
     * @throws NamingException if the LdapComparator is already registered or
     * the registration operation is not supported
     */
    public void register( LdapComparator<?> comparator ) throws NamingException
    {
        String oid = comparator.getOid();
        
        if ( byName.containsKey( oid ) )
        {
            String msg = type.name() + " with OID " + oid + " already registered!";
            LOG.warn( msg );
            throw new NamingException( msg );
        }

        byName.put( oid, comparator );
        
        /*
         * add the aliases/names to the name map along with their toLowerCase
         * versions of the name: this is used to make sure name lookups work
         */
        for ( String name : comparator.getNames() )
        {
            byName.put( StringTools.trim( StringTools.toLowerCase( name ) ), comparator );
        }
        
        if ( LOG.isDebugEnabled() )
        {
            LOG.debug( "registered " + comparator.getName() + " for OID {}", oid );
        }
    }


    /**
     * Removes the LdapComparator registered with this registry, using its
     * numeric OID.
     * 
     * @param numericOid the numeric identifier
     * @throws NamingException if the numeric identifier is invalid
     */
    public LdapComparator<?> unregister( String numericOid ) throws NamingException
    {
        if ( !OID.isOID( numericOid ) )
        {
            String msg = "OID " + numericOid + " is not a numeric OID";
            LOG.error( msg );
            throw new NamingException( msg );
        }

        LdapComparator<?> comparator = byName.remove( numericOid );
        
        for ( String name : comparator.getNames() )
        {
            byName.remove( name );
        }
        
        if ( DEBUG )
        {
            LOG.debug( "Removed {} with oid {} from the registry", comparator, numericOid );
        }
        
        return comparator;
    }
    
    
    /**
     * Unregisters all LdapComparators defined for a specific schema from
     * this registry.
     * 
     * @param schemaName the name of the schema whose LdapComparators will be removed from
     */
    public void unregisterSchemaElements( String schemaName ) throws NamingException
    {
        if ( schemaName == null )
        {
            return;
        }
        
        // Loop on all the SchemaObjects stored and remove those associated
        // with the give schemaName
        for ( LdapComparator<?> comparator : this )
        {
            if ( schemaName.equalsIgnoreCase( comparator.getSchemaName() ) )
            {
                String oid = comparator.getOid();
                SchemaObject removed = unregister( oid );
                
                if ( DEBUG )
                {
                    LOG.debug( "Removed {} with oid {} from the registry", removed, oid );
                }
            }
        }
    }
    
    
    /**
     * Clone the ComparatorRegistry
     */
    public ComparatorRegistry clone() throws CloneNotSupportedException
    {
        ComparatorRegistry clone = (ComparatorRegistry)super.clone();
        
        return clone;
    }
}

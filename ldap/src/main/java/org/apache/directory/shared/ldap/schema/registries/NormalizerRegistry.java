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
import org.apache.directory.shared.ldap.schema.Normalizer;
import org.apache.directory.shared.ldap.schema.SchemaObject;
import org.apache.directory.shared.ldap.schema.SchemaObjectType;
import org.apache.directory.shared.ldap.util.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Normalizer registry service class.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class NormalizerRegistry extends SchemaObjectRegistry<Normalizer>
{
    /** static class logger */
    private static final Logger LOG = LoggerFactory.getLogger( NormalizerRegistry.class );

    /** A speedup for debug */
    private static final boolean DEBUG = LOG.isDebugEnabled();
    
    /**
     * Creates a new default NormalizerRegistry instance.
     * 
     * @param oidRegistry The global OID registry 
     */
    public NormalizerRegistry( OidRegistry oidRegistry )
    {
        super( SchemaObjectType.NORMALIZER, oidRegistry );
    }
    
    
    /**
     * Registers a new Normalizer with this registry.
     *
     * @param normalizer the Normalizer to register
     * @throws NamingException if the Normalizer is already registered or
     * the registration operation is not supported
     */
    public void register( Normalizer normalizer ) throws NamingException
    {
        String oid = normalizer.getOid();
        
        if ( byName.containsKey( oid ) )
        {
            String msg = type.name() + " with OID " + oid + " already registered!";
            LOG.warn( msg );
            throw new NamingException( msg );
        }

        byName.put( oid, normalizer );
        
        /*
         * add the aliases/names to the name map along with their toLowerCase
         * versions of the name: this is used to make sure name lookups work
         */
        for ( String name : normalizer.getNames() )
        {
            byName.put( StringTools.trim( StringTools.toLowerCase( name ) ), normalizer );
        }
        
        if ( LOG.isDebugEnabled() )
        {
            LOG.debug( "registered " + normalizer.getName() + " for OID {}", oid );
        }
    }


    /**
     * Removes the Normalizer registered with this registry, using its
     * numeric OID.
     * 
     * @param numericOid the numeric identifier
     * @throws NamingException if the numeric identifier is invalid
     */
    public Normalizer unregister( String numericOid ) throws NamingException
    {
        if ( !OID.isOID( numericOid ) )
        {
            String msg = "OID " + numericOid + " is not a numeric OID";
            LOG.error( msg );
            throw new NamingException( msg );
        }

        Normalizer normalizer = byName.remove( numericOid );
        
        for ( String name : normalizer.getNames() )
        {
            byName.remove( name );
        }
        
        if ( DEBUG )
        {
            LOG.debug( "Removed {} with oid {} from the registry", normalizer, numericOid );
        }
        
        return normalizer;
    }
    
    
    /**
     * Unregisters all Normalizers defined for a specific schema from
     * this registry.
     * 
     * @param schemaName the name of the schema whose Normalizers will be removed from
     */
    public void unregisterSchemaElements( String schemaName ) throws NamingException
    {
        if ( schemaName == null )
        {
            return;
        }
        
        // Loop on all the SchemaObjects stored and remove those associated
        // with the give schemaName
        for ( Normalizer normalizer : this )
        {
            if ( schemaName.equalsIgnoreCase( normalizer.getSchemaName() ) )
            {
                String oid = normalizer.getOid();
                SchemaObject removed = unregister( oid );
                
                if ( DEBUG )
                {
                    LOG.debug( "Removed {} with oid {} from the registry", removed, oid );
                }
            }
        }
    }
}

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
import org.apache.directory.shared.ldap.schema.SchemaObject;
import org.apache.directory.shared.ldap.schema.SchemaObjectType;
import org.apache.directory.shared.ldap.schema.SyntaxChecker;
import org.apache.directory.shared.ldap.util.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * SyntaxChecker registry component's service interface.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class SyntaxCheckerRegistry extends  SchemaObjectRegistry<SyntaxChecker>
{
    /** static class logger */
    private static final Logger LOG = LoggerFactory.getLogger( SyntaxCheckerRegistry.class );

    /** A speedup for debug */
    private static final boolean DEBUG = LOG.isDebugEnabled();
    
    /**
     * Creates a new default SyntaxCheckerRegistry instance.
     * 
     * @param oidRegistry The global OID registry 
     */
    public SyntaxCheckerRegistry( OidRegistry oidRegistry )
    {
        super( SchemaObjectType.SYNTAX_CHECKER, oidRegistry );
    }
    
    
    /**
     * Registers a new SyntaxChecker with this registry.
     *
     * @param syntaxChecker the SyntaxChecker to register
     * @throws NamingException if the SyntaxChecker is already registered or
     * the registration operation is not supported
     */
    public void register( SyntaxChecker syntaxChecker ) throws NamingException
    {
        String oid = syntaxChecker.getOid();
        
        if ( byName.containsKey( oid ) )
        {
            String msg = type.name() + " with OID " + oid + " already registered!";
            LOG.warn( msg );
            //throw new NamingException( msg );
        }

        byName.put( oid, syntaxChecker );
        
        /*
         * add the aliases/names to the name map along with their toLowerCase
         * versions of the name: this is used to make sure name lookups work
         */
        for ( String name : syntaxChecker.getNames() )
        {
            byName.put( StringTools.trim( StringTools.toLowerCase( name ) ), syntaxChecker );
        }
        
        if ( LOG.isDebugEnabled() )
        {
            LOG.debug( "registered " + syntaxChecker.getName() + " for OID {}", oid );
        }
    }


    /**
     * Removes the SyntaxChecker registered with this registry, using its
     * numeric OID.
     * 
     * @param numericOid the numeric identifier
     * @throws NamingException if the numeric identifier is invalid
     */
    public SyntaxChecker unregister( String numericOid ) throws NamingException
    {
        if ( !OID.isOID( numericOid ) )
        {
            String msg = "OID " + numericOid + " is not a numeric OID";
            LOG.error( msg );
            throw new NamingException( msg );
        }

        SyntaxChecker syntaxChecker = byName.remove( numericOid );
        
        for ( String name : syntaxChecker.getNames() )
        {
            byName.remove( name );
        }
        
        if ( DEBUG )
        {
            LOG.debug( "Removed {} with oid {} from the registry", syntaxChecker, numericOid );
        }
        
        return syntaxChecker;
    }
    
    
    /**
     * Unregisters all SyntaxCheckers defined for a specific schema from
     * this registry.
     * 
     * @param schemaName the name of the schema whose SyntaxCheckers will be removed from
     */
    public void unregisterSchemaElements( String schemaName ) throws NamingException
    {
        if ( schemaName == null )
        {
            return;
        }
        
        // Loop on all the SchemaObjects stored and remove those associated
        // with the give schemaName
        for ( SyntaxChecker syntaxChecker : this )
        {
            if ( schemaName.equalsIgnoreCase( syntaxChecker.getSchemaName() ) )
            {
                String oid = syntaxChecker.getOid();
                SchemaObject removed = unregister( oid );
                
                if ( DEBUG )
                {
                    LOG.debug( "Removed {} with oid {} from the registry", removed, oid );
                }
            }
        }
    }
}

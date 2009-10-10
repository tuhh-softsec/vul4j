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


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.naming.NamingException;

import org.apache.directory.shared.ldap.NotImplementedException;
import org.apache.directory.shared.ldap.constants.MetaSchemaConstants;
import org.apache.directory.shared.ldap.ldif.LdifEntry;
import org.apache.directory.shared.ldap.schema.AttributeType;
import org.apache.directory.shared.ldap.schema.DITContentRule;
import org.apache.directory.shared.ldap.schema.DITStructureRule;
import org.apache.directory.shared.ldap.schema.EntityFactory;
import org.apache.directory.shared.ldap.schema.LdapComparator;
import org.apache.directory.shared.ldap.schema.LdapSyntax;
import org.apache.directory.shared.ldap.schema.MatchingRule;
import org.apache.directory.shared.ldap.schema.MatchingRuleUse;
import org.apache.directory.shared.ldap.schema.NameForm;
import org.apache.directory.shared.ldap.schema.Normalizer;
import org.apache.directory.shared.ldap.schema.ObjectClass;
import org.apache.directory.shared.ldap.schema.SyntaxChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An abstract class with a utility method and setListener() implemented.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public abstract class AbstractSchemaLoader implements SchemaLoader
{
    /** static class logger */
    private static final Logger LOG = LoggerFactory.getLogger( AbstractSchemaLoader.class );
    

    protected SchemaLoaderListener listener;
    
    /** the factory that generates respective SchemaObjects from LDIF entries */
    protected final EntityFactory factory;
    
    
    public AbstractSchemaLoader( EntityFactory factory )
    {
        this.factory = factory;
    }
    
    /** 
     * A map of all available schema names to schema objects. This map is 
     * populated when this class is created with all the schemas present in
     * the LDIF based schema repository.
     */
    protected final Map<String,Schema> schemaMap = new HashMap<String,Schema>();
    

    public void setListener( SchemaLoaderListener listener )
    {
        this.listener = listener;
    }
    
    
    protected final void notifyListenerOrRegistries( Schema schema, Registries registries )
    {
        if ( listener != null )
        {
            listener.schemaLoaded( schema );
        }
        
        if ( registries instanceof SchemaLoaderListener )
        {
            if ( registries != listener )
            {
                SchemaLoaderListener listener = ( SchemaLoaderListener ) registries;
                listener.schemaLoaded( schema );
            }
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    public final void loadAllEnabled( Registries registries ) throws Exception
    {
        Map<String,Schema> notloaded = new HashMap<String,Schema>( schemaMap );
        
        for ( String schemaName : schemaMap.keySet() )
        {
            if ( registries.isSchemaLoaded( schemaName ) )
            {
                notloaded.remove( schemaName );
            }
        }
         
        for ( Schema schema : schemaMap.values() )
        {
            loadDepsFirst( schema, new Stack<String>(), 
                notloaded, schema, registries );
        }
    }
    
    
    /**
     * Recursive method which loads schema's with their dependent schemas first
     * and tracks what schemas it has seen so the recursion does not go out of
     * control with dependency cycle detection.
     *
     * @param rootAncestor the triggering schema load request: the root ancestor of dependency chain
     * @param beenthere stack of schema names we have visited and have yet to load
     * @param notLoaded hash of schemas keyed by name which have yet to be loaded
     * @param schema the current schema we are attempting to load
     * @param registries the set of registries to use while loading
     * @param props to use while trying to resolve other schemas
     * @throws Exception if there is a cycle detected and/or another
     * failure results while loading, producing and or registering schema objects
     */
    protected final void loadDepsFirst( Schema rootAncestor, Stack<String> beenthere, Map<String, Schema> notLoaded,
                                        Schema schema, Registries registries ) throws Exception
    {
        if ( registries.isSchemaLoaded( schema.getSchemaName() ) )
        {
            LOG.warn( "{} schema has already been loaded" + schema.getSchemaName() );
            return;
        }
        
        beenthere.push( schema.getSchemaName() );
        String[] deps = schema.getDependencies();

        // if no deps then load this guy and return
        if ( deps == null || deps.length == 0 )
        {
            if ( rootAncestor == schema )
            {
                load( schema, registries, false );
            }
            else
            {
                load( schema, registries, true );
            }
            
            notLoaded.remove( schema.getSchemaName() );
            beenthere.pop();
            return;
        }

        /*
         * We got deps and need to load them before this schema.  We go through
         * all deps loading them with their deps first if they have not been
         * loaded.
         */
        for ( String depName : deps )
        {
            // @todo if a dependency is not loaded it's not in this list
            // @todo why is it not in this list?  Without being in this list
            // @todo this for loop is absolutely useless - we will not load
            // @todo any disabled dependencies at all.  I'm shocked that the
            // @todo samba schema is actually loading when the nis dependency
            // @todo is not loaded.

            if ( !notLoaded.containsKey( depName ) )
            {
                continue;
            }

            Schema dep = notLoaded.get( depName );

            // dep is not in the set of schema objects we need to try to resolve it
            if ( dep == null )
            {
                // try to load dependency with the provided properties default
                dep = getSchema( depName );
            }

            if ( beenthere.contains( dep.getSchemaName() ) )
            {
                // push again so we show the cycle in output
                beenthere.push( dep.getSchemaName() );
                throw new NamingException( "schema dependency cycle detected: " + beenthere );
            }

            loadDepsFirst( rootAncestor, beenthere, notLoaded, dep, registries );
        }

        // We have loaded all our deps so we can load this schema
        if ( rootAncestor == schema )
        {
            load( schema, registries, false );
        }
        else
        {
            load( schema, registries, true );
        }
        
        notLoaded.remove( schema.getSchemaName() );
        beenthere.pop();
    }


    /**
     * {@inheritDoc}
     */
    public Schema getSchema( String schemaName ) throws Exception
    {
        return this.schemaMap.get( schemaName );
    }


    /**
     * {@inheritDoc}
     */
    public void loadWithDependencies( Collection<Schema> schemas, Registries registries ) throws Exception
    {
        Map<String,Schema> notLoaded = new HashMap<String,Schema>();
        
        for ( Schema schema : schemas )
        {
            if ( ! registries.isSchemaLoaded( schema.getSchemaName() ) )
            {
                notLoaded.put( schema.getSchemaName(), schema );
            }
        }
        
        for ( Schema schema : notLoaded.values() )
        {
            Stack<String> beenthere = new Stack<String>();
            loadDepsFirst( schema, beenthere, notLoaded, schema, registries );
        }
    }
    
    
    /**
     * Register the comparator contained in the given LdifEntry into the registries. 
     *
     * @param registries The Registries
     * @param entry The LdifEntry containing the comparator description
     * @param schema The associated schema
     * @throws Exception If the registering failed
     */
    protected void registerComparator( Registries registries, LdifEntry entry, Schema schema) 
        throws Exception
    {
        LdapComparator<?> comparator = 
            factory.getLdapComparator( entry.getEntry(), registries, schema.getSchemaName() );
        comparator.setOid( entry.get( MetaSchemaConstants.M_OID_AT ).getString() );

        if ( schema.isEnabled() && comparator.isEnabled() )
        {
            comparator.applyRegistries( registries );
            registries.register( comparator );
        }
    }
    
    
    /**
     * Register the SyntaxChecker contained in the given LdifEntry into the registries. 
     *
     * @param registries The Registries
     * @param entry The LdifEntry containing the SyntaxChecker description
     * @param schema The associated schema
     * @return the created SyntaxChecker instance
     * @throws Exception If the registering failed
     */
    protected SyntaxChecker registerSyntaxChecker( Registries registries, LdifEntry entry, Schema schema) 
        throws Exception
    {
        SyntaxChecker syntaxChecker = 
            factory.getSyntaxChecker( entry.getEntry(), registries, schema.getSchemaName() );
        syntaxChecker.setOid( entry.get( MetaSchemaConstants.M_OID_AT ).getString() );

        if ( schema.isEnabled() && syntaxChecker.isEnabled() )
        {
            syntaxChecker.applyRegistries( registries );
            registries.register( syntaxChecker );
        }
        
        return syntaxChecker;
    }
    
    
    /**
     * Register the Normalizer contained in the given LdifEntry into the registries. 
     *
     * @param registries The Registries
     * @param entry The LdifEntry containing the Normalizer description
     * @param schema The associated schema
     * @return the created Normalizer instance
     * @throws Exception If the registering failed
     */
    protected Normalizer registerNormalizer( Registries registries, LdifEntry entry, Schema schema) 
        throws Exception
    {
        Normalizer normalizer =
            factory.getNormalizer( entry.getEntry(), registries, schema.getSchemaName() );
        
        if ( schema.isEnabled() && normalizer.isEnabled() )
        {
            normalizer.applyRegistries( registries );
            registries.register( normalizer );
        }
        
        return normalizer;
    }
    
    
    /**
     * Register the MatchingRule contained in the given LdifEntry into the registries. 
     *
     * @param registries The Registries
     * @param entry The LdifEntry containing the MatchingRule description
     * @param schema The associated schema
     * @return the created MatchingRule instance
     * @throws Exception If the registering failed
     */
    protected MatchingRule registerMatchingRule( Registries registries, LdifEntry entry, Schema schema) 
        throws Exception
    {
        MatchingRule matchingRule = factory.getMatchingRule( 
            entry.getEntry(), registries, schema.getSchemaName() );

        if ( schema.isEnabled() && matchingRule.isEnabled() )
        {
            matchingRule.applyRegistries( registries );
            registries.register( matchingRule );
        }
        
        return matchingRule;
    }
    
    
    /**
     * Register the Syntax contained in the given LdifEntry into the registries. 
     *
     * @param registries The Registries
     * @param entry The LdifEntry containing the Syntax description
     * @param schema The associated schema
     * @return the created Syntax instance
     * @throws Exception If the registering failed
     */
    protected LdapSyntax registerSyntax( Registries registries, LdifEntry entry, Schema schema) 
        throws Exception
    {
        LdapSyntax syntax = factory.getSyntax( 
            entry.getEntry(), registries, schema.getSchemaName() );

        if ( schema.isEnabled() && syntax.isEnabled() )
        {
            syntax.applyRegistries( registries );
            registries.register( syntax );
        }
        
        return syntax;
    }
    
    
    /**
     * Register the AttributeType contained in the given LdifEntry into the registries. 
     *
     * @param registries The Registries
     * @param entry The LdifEntry containing the AttributeType description
     * @param schema The associated schema
     * @return the created AttributeType instance
     * @throws Exception If the registering failed
     */
    protected AttributeType registerAttributeType( Registries registries, LdifEntry entry, Schema schema) 
        throws Exception
    {
        AttributeType attributeType = factory.getAttributeType( entry.getEntry(), registries, schema.getSchemaName() );
        
        if ( schema.isEnabled() && attributeType.isEnabled() )
        {
            attributeType.applyRegistries( registries );
            registries.register( attributeType );
        }
        
        return attributeType;
    }
    
    
    /**
     * Register the MatchingRuleUse contained in the given LdifEntry into the registries. 
     *
     * @param registries The Registries
     * @param entry The LdifEntry containing the MatchingRuleUse description
     * @param schema The associated schema
     * @return the created MatchingRuleUse instance
     * @throws Exception If the registering failed
     */
    protected MatchingRuleUse registerMatchingRuleUse( Registries registries, LdifEntry entry, Schema schema) 
        throws Exception
    {
        throw new NotImplementedException( "Need to implement factory " +
                "method for creating a MatchingRuleUse" );
    }
    
    
    /**
     * Register the NameForm contained in the given LdifEntry into the registries. 
     *
     * @param registries The Registries
     * @param entry The LdifEntry containing the NameForm description
     * @param schema The associated schema
     * @return the created NameForm instance
     * @throws Exception If the registering failed
     */
    protected NameForm registerNameForm( Registries registries, LdifEntry entry, Schema schema) 
        throws Exception
    {
        throw new NotImplementedException( "Need to implement factory " +
                "method for creating a NameForm" );
    }
    
    
    /**
     * Register the DitContentRule contained in the given LdifEntry into the registries. 
     *
     * @param registries The Registries
     * @param entry The LdifEntry containing the DitContentRule description
     * @param schema The associated schema
     * @return the created DitContentRule instance
     * @throws Exception If the registering failed
     */
    protected DITContentRule registerDitContentRule( Registries registries, LdifEntry entry, Schema schema) 
        throws Exception
    {
        throw new NotImplementedException( "Need to implement factory " +
                "method for creating a DitContentRule" );
    }
    
    
    /**
     * Register the DitStructureRule contained in the given LdifEntry into the registries. 
     *
     * @param registries The Registries
     * @param entry The LdifEntry containing the DitStructureRule description
     * @param schema The associated schema
     * @return the created DitStructureRule instance
     * @throws Exception If the registering failed
     */
    protected DITStructureRule registerDitStructureRule( Registries registries, LdifEntry entry, Schema schema) 
        throws Exception
    {
        throw new NotImplementedException( "Need to implement factory " +
                "method for creating a DitStructureRule" );
    }


    /**
     * Register the ObjectClass contained in the given LdifEntry into the registries. 
     *
     * @param registries The Registries
     * @param entry The LdifEntry containing the ObjectClass description
     * @param schema The associated schema
     * @return the created ObjectClass instance
     * @throws Exception If the registering failed
     */
    protected ObjectClass registerObjectClass( Registries registries, LdifEntry entry, Schema schema) 
        throws Exception
    {
        ObjectClass objectClass = factory.getObjectClass( entry.getEntry(), registries, schema.getSchemaName() );

        if ( schema.isEnabled() && objectClass.isEnabled() )
        {
            objectClass.applyRegistries( registries );
            registries.register( objectClass );
        }
        
        return objectClass;
    }
}

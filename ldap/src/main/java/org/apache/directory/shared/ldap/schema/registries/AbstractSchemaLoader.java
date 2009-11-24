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


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.directory.shared.ldap.constants.MetaSchemaConstants;
import org.apache.directory.shared.ldap.constants.SchemaConstants;
import org.apache.directory.shared.ldap.entry.Entry;
import org.apache.directory.shared.ldap.entry.EntryAttribute;
import org.apache.directory.shared.ldap.entry.Value;
import org.apache.directory.shared.ldap.util.StringTools;
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
    public final Collection<Schema> getAllEnabled() throws Exception
    {
        return schemaMap.values();
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
     *
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
         *
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

    
    protected Schema getSchema( Entry entry ) throws Exception
    {
        String name;
        String owner;
        String[] dependencies = StringTools.EMPTY_STRINGS;
        boolean isDisabled = false;
        
        if ( entry == null )
        {
            throw new NullPointerException( "entry cannot be null" );
        }
        
        if ( entry.get( SchemaConstants.CN_AT ) == null )
        {
            throw new NullPointerException( "entry must have a valid cn attribute" );
        }
        
        name = entry.get( SchemaConstants.CN_AT ).getString();
        
        if ( entry.get( SchemaConstants.CREATORS_NAME_AT ) == null )
        {
            throw new NullPointerException( "entry must have a valid " 
                + SchemaConstants.CREATORS_NAME_AT + " attribute" );
        }
        
        owner = entry.get( SchemaConstants.CREATORS_NAME_AT ).getString();
        
        if ( entry.get( MetaSchemaConstants.M_DISABLED_AT ) != null )
        {
            String value = entry.get( MetaSchemaConstants.M_DISABLED_AT ).getString();
            value = value.toUpperCase();
            isDisabled = value.equals( "TRUE" );
        }
        
        if ( entry.get( MetaSchemaConstants.M_DEPENDENCIES_AT ) != null )
        {
            Set<String> depsSet = new HashSet<String>();
            EntryAttribute depsAttr = entry.get( MetaSchemaConstants.M_DEPENDENCIES_AT );
            
            for ( Value<?> value:depsAttr )
            {
                depsSet.add( value.getString() );
            }

            dependencies = depsSet.toArray( StringTools.EMPTY_STRINGS );
        }
        
        return new DefaultSchema( name, owner, dependencies, isDisabled ){};
    }
    

    /**
     * {@inheritDoc}
     *
    public List<Throwable> loadWithDependencies( Registries registries, boolean check, Schema... schemas ) throws Exception
    {
        // Relax the controls at first
        List<Throwable> errors = new ArrayList<Throwable>();
        boolean wasRelaxed = registries.isRelaxed();
        registries.setRelaxed( true );

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
        
        // At the end, check the registries if required
        if ( check )
        {
            errors = registries.checkRefInteg();
        }
        
        // Restore the Registries isRelaxed flag
        registries.setRelaxed( wasRelaxed );
        
        return errors;
    }
    
    
    /**
     * Register the comparator contained in the given LdifEntry into the registries. 
     *
     * @param registries The Registries
     * @param entry The LdifEntry containing the comparator description
     * @param schema The associated schema
     * @throws Exception If the registering failed
     *
    protected LdapComparator<?> registerComparator( Registries registries, LdifEntry entry, Schema schema ) 
        throws Exception
    {
        return registerComparator( registries, entry.getEntry(), schema );
    }
    
    
    /**
     * Register the comparator contained in the given Entry into the registries. 
     *
     * @param registries The Registries
     * @param entry The Entry containing the comparator description
     * @param schema The associated schema
     * @throws Exception If the registering failed
     *
    protected LdapComparator<?> registerComparator( Registries registries, Entry entry, Schema schema ) 
        throws Exception
    {
        LdapComparator<?> comparator = 
            factory.getLdapComparator( entry, registries, schema.getSchemaName() );
        comparator.setOid( entry.get( MetaSchemaConstants.M_OID_AT ).getString() );

        if ( registries.isRelaxed() )
        {
            if ( registries.acceptDisabled() )
            {
                registries.register( comparator );
            }
            else if ( schema.isEnabled() && comparator.isEnabled() )
            {
                registries.register( comparator );
            }
        }
        else
        {
            if ( schema.isEnabled() && comparator.isEnabled() )
            {
                registries.register( comparator );
            }
        }
        
        return comparator;
    }
    
    
    /**
     * Register the SyntaxChecker contained in the given LdifEntry into the registries. 
     *
     * @param registries The Registries
     * @param entry The LdifEntry containing the SyntaxChecker description
     * @param schema The associated schema
     * @return the created SyntaxChecker instance
     * @throws Exception If the registering failed
     *
    protected SyntaxChecker registerSyntaxChecker( Registries registries, LdifEntry entry, Schema schema) 
        throws Exception
    {
        SyntaxChecker syntaxChecker = 
            factory.getSyntaxChecker( entry.getEntry(), registries, schema.getSchemaName() );
        syntaxChecker.setOid( entry.get( MetaSchemaConstants.M_OID_AT ).getString() );

        if ( registries.isRelaxed() )
        {
            if ( registries.acceptDisabled() )
            {
                registries.register( syntaxChecker );
            }
            else if ( schema.isEnabled() && syntaxChecker.isEnabled() )
            {
                registries.register( syntaxChecker );
            }
        }
        else
        {
            if ( schema.isEnabled() && syntaxChecker.isEnabled() )
            {
                registries.register( syntaxChecker );
            }
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
     *
    protected Normalizer registerNormalizer( Registries registries, LdifEntry entry, Schema schema) 
        throws Exception
    {
        Normalizer normalizer =
            factory.getNormalizer( entry.getEntry(), registries, schema.getSchemaName() );
        
        if ( registries.isRelaxed() )
        {
            if ( registries.acceptDisabled() )
            {
                registries.register( normalizer );
            }
            else if ( schema.isEnabled() && normalizer.isEnabled() )
            {
                registries.register( normalizer );
            }
        }
        else
        {
            if ( schema.isEnabled() && normalizer.isEnabled() )
            {
                registries.register( normalizer );
            }
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
     *
    protected MatchingRule registerMatchingRule( Registries registries, LdifEntry entry, Schema schema) 
        throws Exception
    {
        MatchingRule matchingRule = factory.getMatchingRule( 
            entry.getEntry(), registries, schema.getSchemaName() );

        if ( registries.isRelaxed() )
        {
            if ( registries.acceptDisabled() )
            {
                registries.register( matchingRule );
            }
            else if ( schema.isEnabled() && matchingRule.isEnabled() )
            {
                registries.register( matchingRule );
            }
        }
        else
        {
            if ( schema.isEnabled() && matchingRule.isEnabled() )
            {
                registries.register( matchingRule );
            }
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
     *
    protected LdapSyntax registerSyntax( Registries registries, LdifEntry entry, Schema schema) 
        throws Exception
    {
        LdapSyntax syntax = factory.getSyntax( 
            entry.getEntry(), registries, schema.getSchemaName() );

        if ( registries.isRelaxed() )
        {
            if ( registries.acceptDisabled() )
            {
                registries.register( syntax );
            }
            else if ( schema.isEnabled() && syntax.isEnabled() )
            {
                registries.register( syntax );
            }
        }
        else
        {
            if ( schema.isEnabled() && syntax.isEnabled() )
            {
                registries.register( syntax );
            }
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
     *
    protected AttributeType registerAttributeType( Registries registries, LdifEntry entry, Schema schema ) 
        throws Exception
    {
        AttributeType attributeType = factory.getAttributeType( entry.getEntry(), registries, schema.getSchemaName() );
        
        if ( registries.isRelaxed() )
        {
            if ( registries.acceptDisabled() )
            {
                registries.register( attributeType );
            }
            else if ( schema.isEnabled() && attributeType.isEnabled() )
            {
                registries.register( attributeType );
            }
        }
        else
        {
            if ( schema.isEnabled() && attributeType.isEnabled() )
            {
                registries.register( attributeType );
            }
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
     *
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
     *
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
     *
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
     *
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
     *
    protected ObjectClass registerObjectClass( Registries registries, LdifEntry entry, Schema schema) 
        throws Exception
    {
        return registerObjectClass( registries, entry.getEntry(), schema );
    }


    /**
     * Register the ObjectClass contained in the given LdifEntry into the registries. 
     *
     * @param registries The Registries
     * @param entry The Entry containing the ObjectClass description
     * @param schema The associated schema
     * @return the created ObjectClass instance
     * @throws Exception If the registering failed
     *
    protected ObjectClass registerObjectClass( Registries registries, Entry entry, Schema schema) 
        throws Exception
    {
        ObjectClass objectClass = factory.getObjectClass( entry, registries, schema.getSchemaName() );

        if ( registries.isRelaxed() )
        {
            if ( registries.acceptDisabled() )
            {
                registries.register( objectClass );
            }
            else if ( schema.isEnabled() && objectClass.isEnabled() )
            {
                registries.register( objectClass );
            }
        }
        else
        {
            if ( schema.isEnabled() && objectClass.isEnabled() )
            {
                registries.register( objectClass );
            }
        }
        
        return objectClass;
    }
    
    
    public EntityFactory getFactory()
    {
        return factory;
    }
    */

    public Object getDao()
    {
        return null;
    }
    
    
    private Schema[] buildSchemaArray( String... schemaNames ) throws Exception
    {
        Schema[] schemas = new Schema[schemaNames.length];
        int pos = 0;
        
        for ( String schemaName : schemaNames )
        {
            schemas[pos++] = getSchema( schemaName );
        }
        
        return schemas;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadAttributeTypes( String... schemaNames ) throws Exception
    {
        if ( schemaNames == null )
        {
            return new ArrayList<Entry>();
        }
        
        return loadAttributeTypes( buildSchemaArray( schemaNames ) );
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadComparators( String... schemaNames ) throws Exception
    {
        if ( schemaNames == null )
        {
            return new ArrayList<Entry>();
        }
        
        return loadComparators( buildSchemaArray( schemaNames ) );
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadDitContentRules( String... schemaNames ) throws Exception
    {
        if ( schemaNames == null )
        {
            return new ArrayList<Entry>();
        }
        
        return loadDitContentRules( buildSchemaArray( schemaNames ) );
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadDitStructureRules( String... schemaNames ) throws Exception
    {
        if ( schemaNames == null )
        {
            return new ArrayList<Entry>();
        }
        
        return loadDitStructureRules( buildSchemaArray( schemaNames ) );
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadMatchingRules( String... schemaNames ) throws Exception
    {
        if ( schemaNames == null )
        {
            return new ArrayList<Entry>();
        }
        
        return loadMatchingRules( buildSchemaArray( schemaNames ) );
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadMatchingRuleUses( String... schemaNames ) throws Exception
    {
        if ( schemaNames == null )
        {
            return new ArrayList<Entry>();
        }
        
        return loadMatchingRuleUses( buildSchemaArray( schemaNames ) );
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadNameForms( String... schemaNames ) throws Exception
    {
        if ( schemaNames == null )
        {
            return new ArrayList<Entry>();
        }
        
        return loadNameForms( buildSchemaArray( schemaNames ) );
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadNormalizers( String... schemaNames ) throws Exception
    {
        if ( schemaNames == null )
        {
            return new ArrayList<Entry>();
        }
        
        return loadNormalizers( buildSchemaArray( schemaNames ) );
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadObjectClasses( String... schemaNames ) throws Exception
    {
        if ( schemaNames == null )
        {
            return new ArrayList<Entry>();
        }
        
        return loadObjectClasses( buildSchemaArray( schemaNames ) );
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadSyntaxes( String... schemaNames ) throws Exception
    {
        if ( schemaNames == null )
        {
            return new ArrayList<Entry>();
        }
        
        return loadSyntaxes( buildSchemaArray( schemaNames ) );
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadSyntaxCheckers( String... schemaNames ) throws Exception
    {
        if ( schemaNames == null )
        {
            return new ArrayList<Entry>();
        }
        
        return loadSyntaxCheckers( buildSchemaArray( schemaNames ) );
    }
}

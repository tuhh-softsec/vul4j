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
package org.apache.directory.shared.schema;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.directory.shared.ldap.NotImplementedException;
import org.apache.directory.shared.ldap.constants.MetaSchemaConstants;
import org.apache.directory.shared.ldap.constants.SchemaConstants;
import org.apache.directory.shared.ldap.entry.Entry;
import org.apache.directory.shared.ldap.name.LdapDN;
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
import org.apache.directory.shared.ldap.schema.SchemaManager;
import org.apache.directory.shared.ldap.schema.SyntaxChecker;
import org.apache.directory.shared.ldap.schema.registries.Registries;
import org.apache.directory.shared.ldap.schema.registries.Schema;
import org.apache.directory.shared.ldap.schema.registries.SchemaLoader;
import org.apache.directory.shared.schema.loader.ldif.SchemaEntityFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * TODO DefaultSchemaManager.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DefaultSchemaManager implements SchemaManager
{
    /** static class logger */
    private static final Logger LOG = LoggerFactory.getLogger( DefaultSchemaManager.class );

    /** The NamingContext this SchemaManager is associated with */
    private LdapDN namingContext;
    
    /** The global registries for this namingContext */
    private volatile Registries registries;
    
    /** The list of errors produced when loading some schema elements */
    private List<Throwable> errors;
    
    /** The Schema loader used by this SchemaManager */
    private SchemaLoader loader;
    
    /** the factory that generates respective SchemaObjects from LDIF entries */
    protected final EntityFactory factory;
    
    /** the normalized name for the schema modification attributes */
    private LdapDN schemaModificationAttributesDN;
    
    /**
     * Creates a new instance of DefaultSchemaManager with the default schema loader
     *
     * @param loader
     */
    public DefaultSchemaManager( SchemaLoader loader ) throws Exception
    {
        // Default to the the root (one schemaManager for all the entries
        namingContext = LdapDN.EMPTY_LDAPDN;
        this.loader = loader;
        errors = null;
        registries = new Registries();
        factory = new SchemaEntityFactory();
    }

    /**
     * Creates a new instance of DefaultSchemaManager, for a specific
     * naming context
     *
     * @param namingContext The associated NamingContext
     */
    public DefaultSchemaManager( SchemaLoader loader, LdapDN namingContext ) throws Exception
    {
        this.namingContext = namingContext;
        this.loader = loader;
        errors = null;
        registries = new Registries();
        factory = new SchemaEntityFactory();
    }

    
    //-----------------------------------------------------------------------
    // Helper methods
    //-----------------------------------------------------------------------
    /**
     * Clone the registries before doing any modification on it. Relax it
     * too so that we can update it. 
     */
    private Registries cloneRegistries() throws Exception
    {
        // Relax the controls at first
        errors = new ArrayList<Throwable>();

        // Clone the Registries
        Registries clonedRegistries = registries.clone();
        
        // And update references. We may have errors, that may be fixed
        // by the new loaded schemas.
        errors = clonedRegistries.checkRefInteg();
        
        // Now, relax the cloned Registries if there is no error
        clonedRegistries.setRelaxed();

        return clonedRegistries;
    }
    
    
    /***
     * Swap the registries, deleting all the schemaObjects and links from the old one
     * to avoid memory leaks.
     */
    private boolean swapRegistries( Registries clonedRegistries )
    {
        // Check the resulting registries
        errors = clonedRegistries.checkRefInteg();
        
        // if we have no more error, we can swap the registries
        if ( errors.size() == 0 )
        {
            clonedRegistries.setStrict();
            
            // Rebuild the references
            errors = clonedRegistries.checkRefInteg();

            if ( errors.size() == 0 )
            {
                Registries oldRegistries = registries;
                registries = clonedRegistries;

                // Delete the old registries to avoid memory leaks
                //destroy( oldRegistries );
                
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            // We can't use this new registries.
            return false;
        }
    }
    
    
    /**
     * Transform a String[] array of schema to a Schema[]
     */
    private Schema[] toArray( String... schemas ) throws Exception
    {
        Schema[] schemaArray = new Schema[schemas.length];
        int n = 0;
        
        for ( String schemaName:schemas )
        {
            schemaArray[n++] = loader.getSchema( schemaName );
        }
        
        return schemaArray;
    }
    
    
    public boolean disable( Schema... schemas )
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean disable( String... schemas )
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean disabledRelaxed( Schema... schemas )
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean disabledRelaxed( String... schemas )
    {
        // TODO Auto-generated method stub
        return false;
    }

    
    /**
     * {@inheritDoc}
     */
    public boolean enable( Schema... schemas ) throws Exception
    {
        // Work on a cloned and relaxed registries
        Registries clonedRegistries = cloneRegistries();

        for ( Schema schema:schemas )
        {
            schema.enable();
            load( clonedRegistries, schema  );
        }
        
        // Swap the registries if it is consistent
        return swapRegistries( clonedRegistries );
    }


    /**
     * {@inheritDoc}
     */
    public boolean enable( String... schemas ) throws Exception
    {
        return enable( toArray( schemas ) );
    }

    public boolean enableRelaxed( Schema... schemas )
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean enableRelaxed( String... schemas )
    {
        // TODO Auto-generated method stub
        return false;
    }

    
    /**
     * {@inheritDoc}
     */
    public List<Throwable> getErrors()
    {
        return errors;
    }

    public Registries getRegistries()
    {
        return registries;
    }

    public boolean isDisabledAccepter()
    {
        // TODO Auto-generated method stub
        return false;
    }

    
    /**
     * {@inheritDoc}
     */
    public boolean load( Schema... schemas ) throws Exception
    {
        // Work on a cloned and relaxed registries
        Registries clonedRegistries = cloneRegistries();

        //Load the schemas
        for ( Schema schema : schemas )
        {
            load( clonedRegistries, schema  );
        }

        // Swap the registries if it is consistent
        return swapRegistries( clonedRegistries );
        
    }

    
    /**
     * {@inheritDoc}
     */
    public boolean load( String... schemas ) throws Exception
    {
        return load( toArray( schemas ) );
    }

    
    
    
    private void registerSchemaObjects( Schema schema, Registries registries ) throws Exception
    {
        registerComparators( schema, registries );
        registerNormalizers( schema, registries );
        registerSyntaxCheckers( schema, registries );
        registerSyntaxes( schema, registries );
        registerMatchingRules( schema, registries );
        registerAttributeTypes( schema, registries );
        registerObjectClasses( schema, registries );
        registerMatchingRuleUses( schema, registries );
        registerDitContentRules( schema, registries );
        registerNameForms( schema, registries );
        registerDitStructureRules( schema, registries );

        //notifyListenerOrRegistries( schema, registries );
    }
    
    /**
     * Load the schema in the registries. We will load everything accordingly to the two flags :
     * - isRelaxed
     * - disabledAccepted
     *
     * @param registries
     * @param schemas
     * @return
     * @throws Exception
     */
    private boolean load( Registries registries, Schema schema ) throws Exception
    {
        // First avoid loading twice the same schema
        if ( registries.isSchemaLoaded( schema.getSchemaName() ) )
        {
            return true;
        }
        
        if ( schema.isDisabled() )
        {
            if ( registries.isDisabledAccepted() )
            {
                LOG.info( "Loading {} schema: \n{}", schema.getSchemaName(), schema );
                
                registries.schemaLoaded( schema );
                
                registerSchemaObjects( schema, registries );
            }
            else
            {
                return false;
            }
        }
        else
        {
            LOG.info( "Loading {} schema: \n{}", schema.getSchemaName(), schema );
            
            registries.schemaLoaded( schema );
            registerSchemaObjects( schema, registries );
        }
        
        return true;
    }

    
    /**
     * Register all the Schema's AttributeTypes
     */
    private void registerAttributeTypes( Schema schema, Registries registries ) throws Exception
    {
        for ( Entry entry : loader.loadAttributeTypes( schema ) )
        {
            registerAttributeType( registries, entry, schema );
        }
    }

    
    /**
     * Register all the Schema's comparators
     */
    private void registerComparators( Schema schema, Registries registries ) throws Exception
    {
        for ( Entry entry : loader.loadComparators( schema ) )
        {
            registerComparator( registries, entry, schema );
        }
    }

    
    /**
     * Register all the Schema's DitContentRules
     */
    private void registerDitContentRules( Schema schema, Registries registries ) throws Exception
    {
        for ( Entry entry : loader.loadDitContentRules( schema ) )
        {
            registerDitContentRule( registries, entry, schema );
        }
    }

    
    /**
     * Register all the Schema's DitStructureRules
     */
    private void registerDitStructureRules( Schema schema, Registries registries ) throws Exception
    {
        for ( Entry entry : loader.loadDitStructureRules( schema ) )
        {
            registerDitStructureRule( registries, entry, schema );
        }
    }

    
    /**
     * Register all the Schema's MatchingRules
     */
    private void registerMatchingRules( Schema schema, Registries registries ) throws Exception
    {
        for ( Entry entry : loader.loadMatchingRules( schema ) )
        {
            registerMatchingRule( registries, entry, schema );
        }
    }

    
    /**
     * Register all the Schema's MatchingRuleUses
     */
    private void registerMatchingRuleUses( Schema schema, Registries registries ) throws Exception
    {
        for ( Entry entry : loader.loadMatchingRuleUses( schema ) )
        {
            registerMatchingRuleUse( registries, entry, schema );
        }
    }

    
    /**
     * Register all the Schema's NameForms
     */
    private void registerNameForms( Schema schema, Registries registries ) throws Exception
    {
        for ( Entry entry : loader.loadNameForms( schema ) )
        {
            registerNameForm( registries, entry, schema );
        }
    }

    
    /**
     * Register all the Schema's Normalizers
     */
    private void registerNormalizers( Schema schema, Registries registries ) throws Exception
    {
        for ( Entry entry : loader.loadNormalizers( schema ) )
        {
            registerNormalizer( registries, entry, schema );
        }
    }

    
    /**
     * Register all the Schema's ObjectClasses
     */
    private void registerObjectClasses( Schema schema, Registries registries ) throws Exception
    {
        for ( Entry entry : loader.loadObjectClasses( schema ) )
        {
            registerObjectClass( registries, entry, schema );
        }
    }

    
    /**
     * Register all the Schema's Syntaxes
     */
    private void registerSyntaxes( Schema schema, Registries registries ) throws Exception
    {
        for ( Entry entry : loader.loadSyntaxes( schema ) )
        {
            registerSyntax( registries, entry, schema );
        }
    }

    
    /**
     * Register all the Schema's SyntaxCheckers
     */
    private void registerSyntaxCheckers( Schema schema, Registries registries ) throws Exception
    {
        for ( Entry entry : loader.loadSyntaxCheckers( schema ) )
        {
            registerSyntaxChecker( registries, entry, schema );
        }
    }

    
    /**
     * Register the AttributeType contained in the given Entry into the registries. 
     *
     * @param registries The Registries
     * @param entry The Entry containing the AttributeType description
     * @param schema The associated schema
     * @return the created AttributeType instance
     * @throws Exception If the registering failed
     */
    private AttributeType registerAttributeType( Registries registries, Entry entry, Schema schema ) 
        throws Exception
    {
        AttributeType attributeType = factory.getAttributeType( entry, registries, schema.getSchemaName() );
        
        if ( registries.isRelaxed() )
        {
            if ( registries.isDisabledAccepted() )
            {
                registries.register( attributeType );
            }
            else if ( schema.isEnabled() && attributeType.isEnabled() )
            {
                registries.register( attributeType );
            }
            else
            {
                errors.add( new Throwable() );
            }
        }
        else
        {
            if ( schema.isEnabled() && attributeType.isEnabled() )
            {
                registries.register( attributeType );
            }
            else
            {
                errors.add( new Throwable() );
            }
        }
        
        // And register the AT in the OidRegister
        registries.getOidRegistry().register( attributeType );
        
        return attributeType;
    }

    
    /**
     * Register the comparator contained in the given Entry into the registries. 
     *
     * @param registries The Registries
     * @param entry The Entry containing the comparator description
     * @param schema The associated schema
     * @throws Exception If the registering failed
     */
    private LdapComparator<?> registerComparator( Registries registries, Entry entry, Schema schema ) 
        throws Exception
    {
        LdapComparator<?> comparator = 
            factory.getLdapComparator( entry, registries, schema.getSchemaName() );
        
        comparator.setOid( entry.get( MetaSchemaConstants.M_OID_AT ).getString() );

        if ( registries.isRelaxed() )
        {
            if ( registries.isDisabledAccepted() )
            {
                registries.register( comparator );
            }
            else if ( schema.isEnabled() && comparator.isEnabled() )
            {
                registries.register( comparator );
            }
            else
            {
                errors.add( new Throwable() );
            }
        }
        else
        {
            if ( schema.isEnabled() && comparator.isEnabled() )
            {
                registries.register( comparator );
            }
            else
            {
                errors.add( new Throwable() );
            }
        }
        
        return comparator;
    }
    
    
    /**
     * Register the DitContentRule contained in the given Entry into the registries. 
     *
     * @param registries The Registries
     * @param entry The Entry containing the DitContentRule description
     * @param schema The associated schema
     * @return the created DitContentRule instance
     * @throws Exception If the registering failed
     */
    private DITContentRule registerDitContentRule( Registries registries, Entry entry, Schema schema) 
        throws Exception
    {
        throw new NotImplementedException( "Need to implement factory " +
                "method for creating a DitContentRule" );
    }
    
    
    /**
     * Register the DitStructureRule contained in the given Entry into the registries. 
     *
     * @param registries The Registries
     * @param entry The Entry containing the DitStructureRule description
     * @param schema The associated schema
     * @return the created DitStructureRule instance
     * @throws Exception If the registering failed
     */
    private DITStructureRule registerDitStructureRule( Registries registries, Entry entry, Schema schema) 
        throws Exception
    {
        throw new NotImplementedException( "Need to implement factory " +
                "method for creating a DitStructureRule" );
    }

    
    /**
     * Register the MatchingRule contained in the given Entry into the registries. 
     *
     * @param registries The Registries
     * @param entry The Entry containing the MatchingRule description
     * @param schema The associated schema
     * @return the created MatchingRule instance
     * @throws Exception If the registering failed
     */
    private MatchingRule registerMatchingRule( Registries registries, Entry entry, Schema schema) 
        throws Exception
    {
        MatchingRule matchingRule = factory.getMatchingRule( 
            entry, registries, schema.getSchemaName() );

        if ( registries.isRelaxed() )
        {
            if ( registries.isDisabledAccepted() )
            {
                registries.register( matchingRule );
            }
            else if ( schema.isEnabled() && matchingRule.isEnabled() )
            {
                registries.register( matchingRule );
            }
            else
            {
                errors.add( new Throwable() );
            }
        }
        else
        {
            if ( schema.isEnabled() && matchingRule.isEnabled() )
            {
                registries.register( matchingRule );
            }
            else
            {
                errors.add( new Throwable() );
            }
        }
        
        
        // And register the MR in the OidRegister
        registries.getOidRegistry().register( matchingRule );
        
        return matchingRule;
    }
    
    
    /**
     * Register the MatchingRuleUse contained in the given Entry into the registries. 
     *
     * @param registries The Registries
     * @param entry The Entry containing the MatchingRuleUse description
     * @param schema The associated schema
     * @return the created MatchingRuleUse instance
     * @throws Exception If the registering failed
     */
    private MatchingRuleUse registerMatchingRuleUse( Registries registries, Entry entry, Schema schema) 
        throws Exception
    {
        throw new NotImplementedException( "Need to implement factory " +
                "method for creating a MatchingRuleUse" );
    }
    
    
    /**
     * Register the NameForm contained in the given Entry into the registries. 
     *
     * @param registries The Registries
     * @param entry The Entry containing the NameForm description
     * @param schema The associated schema
     * @return the created NameForm instance
     * @throws Exception If the registering failed
     */
    private NameForm registerNameForm( Registries registries, Entry entry, Schema schema) 
        throws Exception
    {
        throw new NotImplementedException( "Need to implement factory " +
                "method for creating a NameForm" );
    }

    
    /**
     * Register the Normalizer contained in the given Entry into the registries. 
     *
     * @param registries The Registries
     * @param entry The Entry containing the Normalizer description
     * @param schema The associated schema
     * @return the created Normalizer instance
     * @throws Exception If the registering failed
     */
    private Normalizer registerNormalizer( Registries registries, Entry entry, Schema schema) 
        throws Exception
    {
        Normalizer normalizer =
            factory.getNormalizer( entry, registries, schema.getSchemaName() );
        
        if ( registries.isRelaxed() )
        {
            if ( registries.isDisabledAccepted() )
            {
                registries.register( normalizer );
            }
            else if ( schema.isEnabled() && normalizer.isEnabled() )
            {
                registries.register( normalizer );
            }
            else
            {
                errors.add( new Throwable() );
            }
        }
        else
        {
            if ( schema.isEnabled() && normalizer.isEnabled() )
            {
                registries.register( normalizer );
            }
            else
            {
                errors.add( new Throwable() );
            }
        }
        
        return normalizer;
    }
    
    
    /**
     * Register the ObjectClass contained in the given Entry into the registries. 
     *
     * @param registries The Registries
     * @param entry The Entry containing the ObjectClass description
     * @param schema The associated schema
     * @return the created ObjectClass instance
     * @throws Exception If the registering failed
     */
    private ObjectClass registerObjectClass( Registries registries, Entry entry, Schema schema) 
        throws Exception
    {
        ObjectClass objectClass = factory.getObjectClass( entry, registries, schema.getSchemaName() );

        if ( registries.isRelaxed() )
        {
            if ( registries.isDisabledAccepted() )
            {
                registries.register( objectClass );
            }
            else if ( schema.isEnabled() && objectClass.isEnabled() )
            {
                registries.register( objectClass );
            }
            else
            {
                errors.add( new Throwable() );
            }
        }
        else
        {
            if ( schema.isEnabled() && objectClass.isEnabled() )
            {
                registries.register( objectClass );
            }
            else
            {
                errors.add( new Throwable() );
            }
        }
        
        // And register the OC in the OidRegister
        registries.getOidRegistry().register( objectClass );
        
        return objectClass;
    }

    
    /**
     * Register the SyntaxChecker contained in the given Entry into the registries. 
     *
     * @param registries The Registries
     * @param entry The Entry containing the SyntaxChecker description
     * @param schema The associated schema
     * @return the created SyntaxChecker instance
     * @throws Exception If the registering failed
     */
    private SyntaxChecker registerSyntaxChecker( Registries registries, Entry entry, Schema schema) 
        throws Exception
    {
        SyntaxChecker syntaxChecker = 
            factory.getSyntaxChecker( entry, registries, schema.getSchemaName() );
        syntaxChecker.setOid( entry.get( MetaSchemaConstants.M_OID_AT ).getString() );

        if ( registries.isRelaxed() )
        {
            if ( registries.isDisabledAccepted() )
            {
                registries.register( syntaxChecker );
            }
            else if ( schema.isEnabled() && syntaxChecker.isEnabled() )
            {
                registries.register( syntaxChecker );
            }
            else
            {
                errors.add( new Throwable() );
            }
        }
        else
        {
            if ( schema.isEnabled() && syntaxChecker.isEnabled() )
            {
                registries.register( syntaxChecker );
            }
            else
            {
                errors.add( new Throwable() );
            }
        }
        
        return syntaxChecker;
    }
    
    

    /**
     * Register the Syntax contained in the given Entry into the registries. 
     *
     * @param registries The Registries
     * @param entry The Entry containing the Syntax description
     * @param schema The associated schema
     * @return the created Syntax instance
     * @throws Exception If the registering failed
     */
    private LdapSyntax registerSyntax( Registries registries, Entry entry, Schema schema) 
        throws Exception
    {
        LdapSyntax syntax = factory.getSyntax( 
            entry, registries, schema.getSchemaName() );

        if ( registries.isRelaxed() )
        {
            if ( registries.isDisabledAccepted() )
            {
                registries.register( syntax );
            }
            else if ( schema.isEnabled() && syntax.isEnabled() )
            {
                registries.register( syntax );
            }
            else
            {
                errors.add( new Throwable() );
            }
        }
        else
        {
            if ( schema.isEnabled() && syntax.isEnabled() )
            {
                registries.register( syntax );
            }
            else
            {
                errors.add( new Throwable() );
            }
        }
        
        // And register the Syntax in the OidRegister
        registries.getOidRegistry().register( syntax );
        
        return syntax;
    }

    
    /**
     * {@inheritDoc}
     */
    public boolean loadAllEnabled() throws Exception
    {
        Schema[] schemas = loader.getAllEnabled().toArray( new Schema[0] );
        
        return loadWithDeps( schemas );
    }

    public boolean loadAllEnabledRelaxed() throws Exception
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean loadDisabled( Schema... schemas ) throws Exception
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean loadDisabled( String... schemas ) throws Exception
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean loadRelaxed( Schema... schemas ) throws Exception
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean loadRelaxed( String... schemas ) throws Exception
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public boolean loadWithDeps( Schema... schemas ) throws Exception
    {
        // Work on a cloned and relaxed registries
        Registries clonedRegistries = cloneRegistries();

        //Load the schemas
        for ( Schema schema : schemas )
        {
            loadDepsFirst( schema, clonedRegistries );
        }
        
        // Swap the registries if it is consistent
        return swapRegistries( clonedRegistries );
    }
    
    
    /**
     * {@inheritDoc}
     */
    public boolean loadWithDeps( String... schemas ) throws Exception
    {
        return loadWithDeps( toArray( schemas ) );
    }

    
    /**
     * Recursive method which loads schema's with their dependent schemas first
     * and tracks what schemas it has seen so the recursion does not go out of
     * control with dependency cycle detection.
     *
     * @param schema the current schema we are attempting to load
     * @param registries The Registries in which the schemas will be loaded
     * @throws Exception if there is a cycle detected and/or another
     * failure results while loading, producing and or registering schema objects
     */
    private final void loadDepsFirst( Schema schema, Registries registries ) throws Exception
    {
        if ( schema.isDisabled() && !registries.isDisabledAccepted() )
        {
            LOG.info( "The schema is disabled and the registries does not accepted disabled schema" );
            return;
        }
        
        String schemaName = schema.getSchemaName();
        
        if ( registries.isSchemaLoaded( schemaName ) )
        {
            LOG.info( "{} schema has already been loaded" + schema.getSchemaName() );
            return;
        }
        
        String[] deps = schema.getDependencies();

        // if no deps then load this guy and return
        if ( ( deps == null ) || ( deps.length == 0 ) )
        {
            load( registries, schema );
            
            return;
        }

        /*
         * We got deps and need to load them before this schema.  We go through
         * all deps loading them with their deps first if they have not been
         * loaded.
         */
        for ( String depName : deps )
        {
            if ( registries.isSchemaLoaded( schemaName ) )
            {
                // The schema is already loaded. Loop on the next schema
                continue;
            }
            else
            {
                // Call recursively this method
                Schema schemaDep = loader.getSchema( depName );
                loadDepsFirst( schemaDep, registries );
            }
        }

        // Now load the current schema
        load( registries, schema );
    }

    
    public boolean loadWithDepsRelaxed( Schema... schemas ) throws Exception
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean loadWithDepsRelaxed( String... schemas ) throws Exception
    {
        // TODO Auto-generated method stub
        return false;
    }

    public void setRegistries( Registries registries )
    {
        // TODO Auto-generated method stub
        
    }

    public boolean unload( Schema... schemas )
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean unload( String... schemas )
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean verify( Schema... schemas )
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean verify( String... schemas )
    {
        // TODO Auto-generated method stub
        return false;
    }

    public void setSchemaLoader( SchemaLoader schemaLoader )
    {
        // TODO Auto-generated method stub
        
    }


    /**
     * @return the namingContext
     */
    public LdapDN getNamingContext()
    {
        return namingContext;
    }


    /**
     * Initializes the SchemaService
     *
     * @throws Exception If the initialization fails
     */
    public void initialize() throws Exception
    {
        try
        {
            schemaModificationAttributesDN = new LdapDN( SchemaConstants.SCHEMA_MODIFICATIONS_DN );
            schemaModificationAttributesDN.normalize( 
                getRegistries().getAttributeTypeRegistry().getNormalizerMapping() );
        }
        catch ( NamingException e )
        {
            throw new RuntimeException( e );
        }
    }


    /**
     * {@inheritDoc}
     */
    public SchemaLoader getLoader()
    {
        return loader;
    }
}

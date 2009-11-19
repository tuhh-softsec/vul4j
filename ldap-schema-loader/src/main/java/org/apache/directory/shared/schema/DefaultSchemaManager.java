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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;

import org.apache.directory.shared.ldap.NotImplementedException;
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
import org.apache.directory.shared.ldap.schema.SchemaObject;
import org.apache.directory.shared.ldap.schema.SchemaObjectWrapper;
import org.apache.directory.shared.ldap.schema.SyntaxChecker;
import org.apache.directory.shared.ldap.schema.normalizers.OidNormalizer;
import org.apache.directory.shared.ldap.schema.registries.AttributeTypeRegistry;
import org.apache.directory.shared.ldap.schema.registries.ComparatorRegistry;
import org.apache.directory.shared.ldap.schema.registries.DITContentRuleRegistry;
import org.apache.directory.shared.ldap.schema.registries.DITStructureRuleRegistry;
import org.apache.directory.shared.ldap.schema.registries.ImmutableAttributeTypeRegistry;
import org.apache.directory.shared.ldap.schema.registries.ImmutableComparatorRegistry;
import org.apache.directory.shared.ldap.schema.registries.ImmutableDITContentRuleRegistry;
import org.apache.directory.shared.ldap.schema.registries.ImmutableDITStructureRuleRegistry;
import org.apache.directory.shared.ldap.schema.registries.ImmutableLdapSyntaxRegistry;
import org.apache.directory.shared.ldap.schema.registries.ImmutableMatchingRuleRegistry;
import org.apache.directory.shared.ldap.schema.registries.ImmutableMatchingRuleUseRegistry;
import org.apache.directory.shared.ldap.schema.registries.ImmutableNameFormRegistry;
import org.apache.directory.shared.ldap.schema.registries.ImmutableNormalizerRegistry;
import org.apache.directory.shared.ldap.schema.registries.ImmutableObjectClassRegistry;
import org.apache.directory.shared.ldap.schema.registries.ImmutableSyntaxCheckerRegistry;
import org.apache.directory.shared.ldap.schema.registries.LdapSyntaxRegistry;
import org.apache.directory.shared.ldap.schema.registries.MatchingRuleRegistry;
import org.apache.directory.shared.ldap.schema.registries.MatchingRuleUseRegistry;
import org.apache.directory.shared.ldap.schema.registries.NameFormRegistry;
import org.apache.directory.shared.ldap.schema.registries.NormalizerRegistry;
import org.apache.directory.shared.ldap.schema.registries.ObjectClassRegistry;
import org.apache.directory.shared.ldap.schema.registries.OidRegistry;
import org.apache.directory.shared.ldap.schema.registries.Registries;
import org.apache.directory.shared.ldap.schema.registries.Schema;
import org.apache.directory.shared.ldap.schema.registries.SchemaLoader;
import org.apache.directory.shared.ldap.schema.registries.SchemaObjectRegistry;
import org.apache.directory.shared.ldap.schema.registries.SyntaxCheckerRegistry;
import org.apache.directory.shared.ldap.util.StringTools;
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
    
    /** The Schema schemaLoader used by this SchemaManager */
    private SchemaLoader schemaLoader;
    
    /** the factory that generates respective SchemaObjects from LDIF entries */
    protected final EntityFactory factory;
    
    /** the normalized name for the schema modification attributes */
    private LdapDN schemaModificationAttributesDN;
    
    /**
     * Creates a new instance of DefaultSchemaManager with the default schema schemaLoader
     *
     * @param schemaLoader
     */
    public DefaultSchemaManager( SchemaLoader loader ) throws Exception
    {
        // Default to the the root (one schemaManager for all the entries
        namingContext = LdapDN.EMPTY_LDAPDN;
        this.schemaLoader = loader;
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
        this.schemaLoader = loader;
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
    
    
    /**
     * Transform a String[] array of schema to a Schema[]
     */
    private Schema[] toArray( String... schemas ) throws Exception
    {
        Schema[] schemaArray = new Schema[schemas.length];
        int n = 0;
        
        for ( String schemaName:schemas )
        {
            schemaArray[n++] = schemaLoader.getSchema( schemaName );
        }
        
        return schemaArray;
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

        // Now that we have loaded all the SchemaObjects, we have to create the 
        // cross references
        registries.buildReferences();
        
        //notifyListenerOrRegistries( schema, registries );
    }
    
    
    //-----------------------------------------------------------------------
    // API methods
    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public void destroy( Registries registries )
    {
        if( registries == null )
        {
            return;
        }
        
        registries.getAttributeTypeRegistry().getNormalizerMapping().clear();
        
        destroy_( registries.getComparatorRegistry() );
        destroy_( registries.getDitStructureRuleRegistry() );
        destroy_( registries.getLdapSyntaxRegistry( ) );
        destroy_( registries.getMatchingRuleRegistry( ) );
        destroy_( registries.getMatchingRuleUseRegistry( ) );
        destroy_( registries.getNameFormRegistry( ) );
        destroy_( registries.getNormalizerRegistry( ) );
        destroy_( registries.getObjectClassRegistry( ) );
        destroy_( registries.getSyntaxCheckerRegistry( ) );

        // clearing the schemaObjectsBySchemaName, usedBy and using maps
        Map<String, Set<SchemaObjectWrapper>> schemaObjectsBySchemaName = registries.getObjectBySchemaName();
        
        Set<java.util.Map.Entry<String, Set<SchemaObjectWrapper>>> entries = schemaObjectsBySchemaName.entrySet();
        for( java.util.Map.Entry<String, Set<SchemaObjectWrapper>> e : entries )
        {
            Set<SchemaObjectWrapper> schemaObjWrappers = e.getValue();
            // for each SchemaObject present in the wrapper
            // get the Set<SchemaObjectWrapper> values from used and using maps and
            // clear them
            // TODO how to clear the used and using maps? here it is only clearing the
            // Set<SchemaObjectWrapper> values which are part of those maps
            for( SchemaObjectWrapper sow : schemaObjWrappers )
            {
                Set<SchemaObjectWrapper> tmp = registries.getUsedBy( sow.get() );
                if( tmp != null )
                {
                    tmp.clear();
                }
                
                tmp = registries.getUsing( sow.get() );
                if( tmp != null )
                {
                    tmp.clear();
                }
            }
            
            // clear the Set<SchemaObjectWrapper> value of schemaObjectsBySchemaName map 
            schemaObjWrappers.clear();
        }
        
        // finally clear the schemaObjectsBySchemaName map
        schemaObjectsBySchemaName.clear();
    }

    
    /**
     * tries to unregister the SchemaObjectS associated with OIDs
     * by getting the OID iterator of given SchemaObjectRegistry
     */
    private void destroy_( SchemaObjectRegistry objRegistry )
    {
        if( objRegistry == null )
        {
            return;
        }
        
        Iterator<String> oidIterator = objRegistry.oidsIterator();
        while( oidIterator.hasNext() )
        {
            String oid = oidIterator.next();
            try
            {
                objRegistry.unregister( oid );
            }
            catch( Exception e )
            {
                // just log at debug level
                LOG.debug( "Failed to unregister OID {}", oid );
            }
        }
    }

    
    /***
     * {@inheritDoc}
     */
    public boolean swapRegistries( Registries targetRegistries )
    {
        // Check the resulting registries
        errors = targetRegistries.checkRefInteg();
        
        // Rebuild the using and usedBy references
        // errors.addAll( targetRegistries.buildReferences() );

        // if we have no more error, we can swap the registries
        if ( errors.size() == 0 )
        {
            targetRegistries.setStrict();
            
            Registries oldRegistries = registries;
            registries = targetRegistries;

            // Delete the old registries to avoid memory leaks
            destroy( oldRegistries );
            
            return true;
        }
        else
        {
            // We can't use this new registries.
            return false;
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    public boolean disable( Schema... schemas )
    {
        // TODO Auto-generated method stub
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public boolean disable( String... schemas )
    {
        // TODO Auto-generated method stub
        return false;
    }

    
    /**
     * {@inheritDoc}
     */
    public boolean disabledRelaxed( Schema... schemas )
    {
        // TODO Auto-generated method stub
        return false;
    }

    
    /**
     * {@inheritDoc}
     */
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

    
    /**
     * {@inheritDoc}
     */
    public boolean enableRelaxed( Schema... schemas )
    {
        // TODO Auto-generated method stub
        return false;
    }

    
    /**
     * {@inheritDoc}
     */
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

    
    /**
     * {@inheritDoc}
     */
    public Registries getRegistries()
    {
        return registries;
    }
    

    /**
     * {@inheritDoc}
     */
    public boolean isDisabledAccepted()
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
        for ( Entry entry : schemaLoader.loadAttributeTypes( schema ) )
        {
            registerAttributeType( registries, entry, schema );
        }
    }

    
    /**
     * Register all the Schema's comparators
     */
    private void registerComparators( Schema schema, Registries registries ) throws Exception
    {
        for ( Entry entry : schemaLoader.loadComparators( schema ) )
        {
            registerComparator( registries, entry, schema );
        }
    }

    
    /**
     * Register all the Schema's DitContentRules
     */
    private void registerDitContentRules( Schema schema, Registries registries ) throws Exception
    {
        for ( Entry entry : schemaLoader.loadDitContentRules( schema ) )
        {
            registerDitContentRule( registries, entry, schema );
        }
    }

    
    /**
     * Register all the Schema's DitStructureRules
     */
    private void registerDitStructureRules( Schema schema, Registries registries ) throws Exception
    {
        for ( Entry entry : schemaLoader.loadDitStructureRules( schema ) )
        {
            registerDitStructureRule( registries, entry, schema );
        }
    }

    
    /**
     * Register all the Schema's MatchingRules
     */
    private void registerMatchingRules( Schema schema, Registries registries ) throws Exception
    {
        for ( Entry entry : schemaLoader.loadMatchingRules( schema ) )
        {
            registerMatchingRule( registries, entry, schema );
        }
    }

    
    /**
     * Register all the Schema's MatchingRuleUses
     */
    private void registerMatchingRuleUses( Schema schema, Registries registries ) throws Exception
    {
        for ( Entry entry : schemaLoader.loadMatchingRuleUses( schema ) )
        {
            registerMatchingRuleUse( registries, entry, schema );
        }
    }

    
    /**
     * Register all the Schema's NameForms
     */
    private void registerNameForms( Schema schema, Registries registries ) throws Exception
    {
        for ( Entry entry : schemaLoader.loadNameForms( schema ) )
        {
            registerNameForm( registries, entry, schema );
        }
    }

    
    /**
     * Register all the Schema's Normalizers
     */
    private void registerNormalizers( Schema schema, Registries registries ) throws Exception
    {
        for ( Entry entry : schemaLoader.loadNormalizers( schema ) )
        {
            registerNormalizer( registries, entry, schema );
        }
    }

    
    /**
     * Register all the Schema's ObjectClasses
     */
    private void registerObjectClasses( Schema schema, Registries registries ) throws Exception
    {
        for ( Entry entry : schemaLoader.loadObjectClasses( schema ) )
        {
            registerObjectClass( registries, entry, schema );
        }
    }

    
    /**
     * Register all the Schema's Syntaxes
     */
    private void registerSyntaxes( Schema schema, Registries registries ) throws Exception
    {
        for ( Entry entry : schemaLoader.loadSyntaxes( schema ) )
        {
            registerSyntax( registries, entry, schema );
        }
    }

    
    /**
     * Register all the Schema's SyntaxCheckers
     */
    private void registerSyntaxCheckers( Schema schema, Registries registries ) throws Exception
    {
        for ( Entry entry : schemaLoader.loadSyntaxCheckers( schema ) )
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
        AttributeType attributeType = factory.getAttributeType( this, entry, registries, schema.getSchemaName() );
        
        if ( registries.isRelaxed() )
        {
            if ( registries.isDisabledAccepted() || ( schema.isEnabled() && attributeType.isEnabled() ) )
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
            factory.getLdapComparator( this, entry, registries, schema.getSchemaName() );
        
        if ( registries.isRelaxed() )
        {
            if ( registries.isDisabledAccepted() || ( schema.isEnabled() && comparator.isEnabled() ) )
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
            this, entry, registries, schema.getSchemaName() );

        if ( registries.isRelaxed() )
        {
            if ( registries.isDisabledAccepted() || ( schema.isEnabled() && matchingRule.isEnabled() ) )
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
            factory.getNormalizer( this, entry, registries, schema.getSchemaName() );
        
        if ( registries.isRelaxed() )
        {
            if ( registries.isDisabledAccepted() || ( schema.isEnabled() && normalizer.isEnabled() ) )
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
        ObjectClass objectClass = factory.getObjectClass( this, entry, registries, schema.getSchemaName() );

        if ( registries.isRelaxed() )
        {
            if ( registries.isDisabledAccepted() || ( schema.isEnabled() && objectClass.isEnabled() ) )
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
            factory.getSyntaxChecker( this, entry, registries, schema.getSchemaName() );

        if ( registries.isRelaxed() )
        {
            if ( registries.isDisabledAccepted() || ( schema.isEnabled() && syntaxChecker.isEnabled() ) )
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
        LdapSyntax syntax = factory.getSyntax( this,
            entry, registries, schema.getSchemaName() );

        if ( registries.isRelaxed() )
        {
            if ( registries.isDisabledAccepted() || ( schema.isEnabled() && syntax.isEnabled() ) )
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
        
        return syntax;
    }

    
    /**
     * {@inheritDoc}
     */
    public boolean loadAllEnabled() throws Exception
    {
        Schema[] schemas = schemaLoader.getAllEnabled().toArray( new Schema[0] );
        
        return loadWithDeps( schemas );
    }

    
    /**
     * {@inheritDoc}
     */
    public boolean loadAllEnabledRelaxed() throws Exception
    {
        // TODO Auto-generated method stub
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public boolean loadDisabled( Schema... schemas ) throws Exception
    {
        // Work on a cloned and relaxed registries
        Registries clonedRegistries = cloneRegistries();
        
        // Accept the disabled schemas
        clonedRegistries.setDisabledAccepted( true );

        // Load the schemas
        for ( Schema schema : schemas )
        {
            // Enable the Schema object before loading it
            schema.enable();
            load( clonedRegistries, schema  );
        }

        // Swap the registries if it is consistent
        return swapRegistries( clonedRegistries );
    }

    
    /**
     * {@inheritDoc}
     */
    public boolean loadDisabled( String... schemas ) throws Exception
    {
        return loadDisabled( toArray( schemas ) );
    }

    
    /**
     * {@inheritDoc}
     */
    public boolean loadRelaxed( Schema... schemas ) throws Exception
    {
        // TODO Auto-generated method stub
        return false;
    }

    
    /**
     * {@inheritDoc}
     */
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
                Schema schemaDep = schemaLoader.getSchema( depName );
                loadDepsFirst( schemaDep, registries );
            }
        }

        // Now load the current schema
        load( registries, schema );
    }

    
    /**
     * {@inheritDoc}
     */
    public boolean loadWithDepsRelaxed( Schema... schemas ) throws Exception
    {
        // TODO Auto-generated method stub
        return false;
    }

    
    /**
     * {@inheritDoc}
     */
    public boolean loadWithDepsRelaxed( String... schemas ) throws Exception
    {
        // TODO Auto-generated method stub
        return false;
    }

    
    /**
     * {@inheritDoc}
     */
    public void setRegistries( Registries registries )
    {
        // TODO Auto-generated method stub
        
    }

    
    /**
     * {@inheritDoc}
     */
    public boolean unload( Schema... schemas )
    {
        // TODO Auto-generated method stub
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public boolean unload( String... schemas )
    {
        // TODO Auto-generated method stub
        return false;
    }

    
    /**
     * {@inheritDoc}
     */
    public boolean verify( Schema... schemas ) throws Exception
    {
        // Work on a cloned registries
        Registries clonedRegistries = cloneRegistries();
        
        // Loop on all the schemas 
        for ( Schema schema : schemas )
        {
            try
            {
                // Inject the schema
                boolean loaded = load( clonedRegistries, schema );
                
                if ( !loaded )
                {
                    // We got an error : exit
                    destroy( clonedRegistries );
                    return false;
                }
                
                // Now, check the registries
                List<Throwable> errors = clonedRegistries.checkRefInteg();
                
                if ( errors.size() != 0 )
                {
                    // We got an error : exit
                    destroy( clonedRegistries );
                    return false;
                }
            }
            catch ( Exception e )
            {
                // We got an error : exit
                destroy( clonedRegistries );
                return false;
            }
        }
        
        // We can now delete the cloned registries before exiting
        destroy( clonedRegistries );
        
        return true;
    }

    
    /**
     * {@inheritDoc}
     */
    public boolean verify( String... schemas ) throws Exception
    {
        return verify( toArray( schemas ) );
    }

    
    /**
     * {@inheritDoc}
     */
    public void setSchemaLoader( SchemaLoader schemaLoader )
    {
        this.schemaLoader = schemaLoader;
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
        return schemaLoader;
    }

    
    /**
     * {@inheritDoc}
     */
    public AttributeTypeRegistry getAttributeTypeRegistry()
    {
        return new ImmutableAttributeTypeRegistry( registries.getAttributeTypeRegistry() );
    }


    /**
     * {@inheritDoc}
     */
    public ComparatorRegistry getComparatorRegistry()
    {
        return new ImmutableComparatorRegistry( registries.getComparatorRegistry() );
    }
    

    /**
     * {@inheritDoc}
     */
    public DITContentRuleRegistry getDITContentRuleRegistry()
    {
        return new ImmutableDITContentRuleRegistry( registries.getDitContentRuleRegistry() );
    }

    
    /**
     * {@inheritDoc}
     */
    public DITStructureRuleRegistry getDITStructureRuleRegistry()
    {
        return new ImmutableDITStructureRuleRegistry( registries.getDitStructureRuleRegistry() );
    }

    
    /**
     * {@inheritDoc}
     */
    public MatchingRuleRegistry getMatchingRuleRegistry()
    {
        return new ImmutableMatchingRuleRegistry( registries.getMatchingRuleRegistry() );
    }

    
    /**
     * {@inheritDoc}
     */
    public MatchingRuleUseRegistry getMatchingRuleUseRegistry()
    {
        return new ImmutableMatchingRuleUseRegistry( registries.getMatchingRuleUseRegistry() );
    }

    
    /**
     * {@inheritDoc}
     */
    public NameFormRegistry getNameFormRegistry()
    {
        return new ImmutableNameFormRegistry( registries.getNameFormRegistry() );
    }

    
    /**
     * {@inheritDoc}
     */
    public NormalizerRegistry getNormalizerRegistry()
    {
        return new ImmutableNormalizerRegistry( registries.getNormalizerRegistry() );
    }

    
    /**
     * {@inheritDoc}
     */
    public ObjectClassRegistry getObjectClassRegistry()
    {
        return new ImmutableObjectClassRegistry( registries.getObjectClassRegistry() );
    }


    /**
     * {@inheritDoc}
     */
    public LdapSyntaxRegistry getLdapSyntaxRegistry()
    {
        return new ImmutableLdapSyntaxRegistry( registries.getLdapSyntaxRegistry() );
    }


    /**
     * {@inheritDoc}
     */
    public SyntaxCheckerRegistry getSyntaxCheckerRegistry()
    {
        return new ImmutableSyntaxCheckerRegistry( registries.getSyntaxCheckerRegistry() );
    }


    /**
     * {@inheritDoc}
     */
    public AttributeType lookupAttributeTypeRegistry( String oid ) throws NamingException
    {
        return registries.getAttributeTypeRegistry().lookup( StringTools.toLowerCase( oid ).trim() );
    }


    /**
     * {@inheritDoc}
     */
    public LdapComparator<?> lookupComparatorRegistry( String oid ) throws NamingException
    {
        return registries.getComparatorRegistry().lookup( oid );
    }


    /**
     * {@inheritDoc}
     */
    public void register( SchemaObject schemaObject ) throws NamingException
    {
        registries.register( schemaObject );
    }


    /**
     * {@inheritDoc}
     */
    public SchemaObject unregister( SchemaObject schemaObject ) throws NamingException
    {
        return registries.unregister( schemaObject );
    }

    
    /**
     * {@inheritDoc}
     */
    public Map<String, OidNormalizer> getNormalizerMapping()
    {
        return registries.getAttributeTypeRegistry().getNormalizerMapping();
    }
    
    
    /**
     * {@inheritDoc}
     */
    public OidRegistry getOidRegistry()
    {
        return registries.getGlobalOidRegistry();
    }


    /**
     * {@inheritDoc}
     */
    public Schema getLoadedSchema( String schemaName )
    {
        try
        {
            return schemaLoader.getSchema( schemaName );
        }
        catch ( Exception e )
        {
            return null;
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isSchemaLoaded( String schemaName )
    {
        try
        {
            Schema schema = schemaLoader.getSchema( schemaName );
            return schema != null;
        }
        catch ( Exception e )
        {
            return false;
        }
    }
    

    /**
     * {@inheritDoc}
     */
    public SchemaObject unregisterAttributeType( String attributeTypeOid ) throws NamingException
    {
        return registries.getAttributeTypeRegistry().unregister( attributeTypeOid );
    }


    /**
     * {@inheritDoc}
     */
    public SchemaObject unregisterComparator( String comparatorOid ) throws NamingException
    {
        return registries.getComparatorRegistry().unregister( comparatorOid );
    }


    /**
     * {@inheritDoc}
     */
    public SchemaObject unregisterDitControlRule( String ditControlRuleOid ) throws NamingException
    {
        return registries.getDitContentRuleRegistry().unregister( ditControlRuleOid );
    }


    /**
     * {@inheritDoc}
     */
    public SchemaObject unregisterDitStructureRule( String ditStructureRuleOid ) throws NamingException
    {
        return registries.getDitStructureRuleRegistry().unregister( ditStructureRuleOid );
    }


    /**
     * {@inheritDoc}
     */
    public SchemaObject unregisterLdapSyntax( String ldapSyntaxOid ) throws NamingException
    {
        return registries.getLdapSyntaxRegistry().unregister( ldapSyntaxOid );
    }


    /**
     * {@inheritDoc}
     */
    public SchemaObject unregisterMatchingRule( String matchingRuleOid ) throws NamingException
    {
        return registries.getMatchingRuleRegistry().unregister( matchingRuleOid );
    }


    /**
     * {@inheritDoc}
     */
    public SchemaObject unregisterMatchingRuleUse( String matchingRuleUseOid ) throws NamingException
    {
        return registries.getMatchingRuleUseRegistry().unregister( matchingRuleUseOid );
    }


    /**
     * {@inheritDoc}
     */
    public SchemaObject unregisterNameForm( String nameFormOid ) throws NamingException
    {
        return registries.getNameFormRegistry().unregister( nameFormOid );
    }


    /**
     * {@inheritDoc}
     */
    public SchemaObject unregisterNormalizer( String normalizerOid ) throws NamingException
    {
        return registries.getNormalizerRegistry().unregister( normalizerOid );
    }


    /**
     * {@inheritDoc}
     */
    public SchemaObject unregisterObjectClass( String objectClassOid ) throws NamingException
    {
        return registries.getObjectClassRegistry().unregister( objectClassOid );
    }


    /**
     * {@inheritDoc}
     */
    public SchemaObject unregisterSyntaxChecker( String syntaxCheckerOid ) throws NamingException
    {
        return registries.getSyntaxCheckerRegistry().unregister( syntaxCheckerOid );
    }
}

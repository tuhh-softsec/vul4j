
package org.apache.directory.shared.ldap.schema;

import javax.naming.NamingException;

import org.apache.directory.shared.ldap.entry.Entry;
import org.apache.directory.shared.ldap.schema.parsers.LdapComparatorDescription;
import org.apache.directory.shared.ldap.schema.parsers.NormalizerDescription;
import org.apache.directory.shared.ldap.schema.parsers.SyntaxCheckerDescription;
import org.apache.directory.shared.ldap.schema.registries.Registries;
import org.apache.directory.shared.ldap.schema.registries.Schema;

public interface EntityFactory
{
    Schema getSchema( Entry entry ) throws Exception;
    
    /**
     * Retrieve and load a syntaxChecker class from the DIT.
     * 
     * @param entry the entry to load the syntaxChecker from
     * @return the loaded SyntaxChecker
     * @throws NamingException if anything fails during loading
     */
    SyntaxChecker getSyntaxChecker( SchemaManager schemaManager, Entry entry, Registries targetRegistries, String schemaName ) throws Exception;
    

    /**
     * Create a new instance of a SyntaxChecker 
     *
     * @param syntaxCheckerDescription
     * @param targetRegistries
     * @param schemaName
     * @return A new instance of a syntaxChecker
     * @throws Exception If the creation has failed
     */
    SyntaxChecker getSyntaxChecker( SchemaManager schemaManager, SyntaxCheckerDescription syntaxCheckerDescription, 
        Registries targetRegistries, String schemaName ) throws Exception;


    /**
     * Create a new instance of a LdapComparator 
     *
     * @param comparatorDescription
     * @param targetRegistries
     * @param schemaName
     * @return A new instance of a LdapComparator
     * @throws Exception If the creation has failed
     */
    LdapComparator<?> getLdapComparator( SchemaManager schemaManager, 
        LdapComparatorDescription comparatorDescription, 
        Registries targetRegistries, String schemaName ) throws Exception;


    /**
     * Retrieve and load a Comparator class from the DIT.
     * 
     * @param entry the entry to load the Comparator from
     * @param targetRegistries The registries
     * @param schemaName The schema this SchemaObject will be part of
     * @return the loaded Comparator
     * @throws NamingException if anything fails during loading
     */
    LdapComparator<?> getLdapComparator( SchemaManager schemaManager, Entry entry, 
        Registries targetRegistries, String schemaName ) throws Exception;
    
    
    /**
     * Create a new instance of a Normalizer 
     *
     * @param normalizerDescription
     * @param targetRegistries
     * @param schemaName
     * @return A new instance of a normalizer
     * @throws Exception If the creation has failed
     */
    Normalizer getNormalizer( SchemaManager schemaManager, NormalizerDescription normalizerDescription, 
        Registries targetRegistries, String schemaName ) throws Exception;
    
    
    /**
     * Retrieve and load a Normalizer class from the DIT.
     * 
     * @param entry the entry to load the Normalizer from
     * @return the loaded Normalizer
     * @throws NamingException if anything fails during loading
     */
    Normalizer getNormalizer( SchemaManager schemaManager, Entry entry, Registries targetRegistries, String schemaName ) 
        throws Exception;
    
    
    LdapSyntax getSyntax( Entry entry, Registries targetRegistries, String schemaName ) throws NamingException;
    
    
    /**
     * Construct an MatchingRule from an entry get from the Dit
     *
     * @param entry The entry containing all the informations to build a MatchingRule
     * @param targetRegistries The registries containing all the enabled SchemaObjects
     * @param schemaName The schema containing this MatchingRule
     * @return A MatchingRule SchemaObject
     * @throws NamingException If the MatchingRule is invalid
     */
    MatchingRule getMatchingRule( Entry entry, Registries targetRegistries, String schemaName ) throws NamingException;


    ObjectClass getObjectClass( Entry entry, Registries targetRegistries, String schemaName ) throws Exception;
    
    
    /**
     * Construct an AttributeType from an entry representing an AttributeType.
     *
     * @param entry The entry containing all the informations to build an AttributeType
     * @param targetRegistries The registries containing all the enabled SchemaObjects
     * @param schemaName The schema containing this AttributeType
     * @return An AttributeType SchemaObject
     * @throws NamingException If the AttributeType is invalid
     */
    AttributeType getAttributeType( Entry entry, Registries targetRegistries, String schemaName ) throws NamingException;
}

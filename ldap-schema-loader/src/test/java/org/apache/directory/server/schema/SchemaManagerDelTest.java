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
package org.apache.directory.server.schema;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.NoSuchAttributeException;

import org.apache.commons.io.FileUtils;
import org.apache.directory.shared.ldap.exception.LdapSchemaViolationException;
import org.apache.directory.shared.ldap.schema.AttributeType;
import org.apache.directory.shared.ldap.schema.LdapComparator;
import org.apache.directory.shared.ldap.schema.LdapSyntax;
import org.apache.directory.shared.ldap.schema.MatchingRule;
import org.apache.directory.shared.ldap.schema.Normalizer;
import org.apache.directory.shared.ldap.schema.ObjectClass;
import org.apache.directory.shared.ldap.schema.SchemaManager;
import org.apache.directory.shared.ldap.schema.SyntaxChecker;
import org.apache.directory.shared.ldap.schema.comparators.BooleanComparator;
import org.apache.directory.shared.ldap.schema.ldif.extractor.SchemaLdifExtractor;
import org.apache.directory.shared.ldap.schema.normalizers.BooleanNormalizer;
import org.apache.directory.shared.ldap.schema.syntaxCheckers.BooleanSyntaxChecker;
import org.apache.directory.shared.schema.DefaultSchemaManager;
import org.apache.directory.shared.schema.loader.ldif.LdifSchemaLoader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * A test class for SchemaManager, testing the deletion of a SchemaObject.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemaManagerDelTest
{
    // A directory in which the ldif files will be stored
    private static String workingDirectory;

    // The schema repository
    private static File schemaRepository;


    @BeforeClass
    public static void setup() throws Exception
    {
        workingDirectory = System.getProperty( "workingDirectory" );

        if ( workingDirectory == null )
        {
            String path = SchemaManagerDelTest.class.getResource( "" ).getPath();
            int targetPos = path.indexOf( "target" );
            workingDirectory = path.substring( 0, targetPos + 6 );
        }

        schemaRepository = new File( workingDirectory, "schema" );

        // Cleanup the target directory
        FileUtils.deleteDirectory( schemaRepository );

        SchemaLdifExtractor extractor = new SchemaLdifExtractor( new File( workingDirectory ) );
        extractor.extractOrCopy();
    }


    @AfterClass
    public static void cleanup() throws IOException
    {
        // Cleanup the target directory
        FileUtils.deleteDirectory( schemaRepository );
    }


    private SchemaManager loadSchema( String schemaName ) throws Exception
    {
        LdifSchemaLoader loader = new LdifSchemaLoader( schemaRepository );
        SchemaManager schemaManager = new DefaultSchemaManager( loader );

        schemaManager.loadWithDeps( schemaName );

        return schemaManager;
    }


    private boolean isATPresent( SchemaManager schemaManager, String oid )
    {
        try
        {
            AttributeType attributeType = schemaManager.lookupAttributeTypeRegistry( oid );

            return attributeType != null;
        }
        catch ( NoSuchAttributeException nsae )
        {
            return false;
        }
        catch ( NamingException ne )
        {
            return false;
        }
    }


    //=========================================================================
    // For each test, we will check many different things.
    // If the test is successful, we want to know if the SchemaObject
    // Registry has shrunk : its size must be one lower. If the SchemaObject
    // is not loadable, then the GlobalOidRegistry must also have grown.
    //=========================================================================
    // AttributeType deletion tests
    //-------------------------------------------------------------------------
    // First, not defined descendant
    //-------------------------------------------------------------------------
    /**
     * Try to delete an AttributeType not existing in the schemaManager
     */
    @Test
    public void testDelNonExistentAttributeType() throws Exception
    {
        SchemaManager schemaManager = loadSchema( "Core" );
        int atrSize = schemaManager.getAttributeTypeRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        AttributeType attributeType = new AttributeType( "1.1.0" );
        attributeType.setEqualityOid( "2.5.13.1" );
        attributeType.setOrderingOid( null );
        attributeType.setSubstringOid( null );

        // It should fail
        assertFalse( schemaManager.delete( attributeType ) );

        List<Throwable> errors = schemaManager.getErrors();
        assertFalse( errors.isEmpty() );

        assertEquals( atrSize, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );
    }


    /**
     * Delete an existing AT not referenced by any object
     */
    @Test
    public void testDelExistingAttributeTypeNoReference() throws Exception
    {
        // First inject such an AT
        SchemaManager schemaManager = loadSchema( "Core" );
        int atrSize = schemaManager.getAttributeTypeRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        AttributeType attributeType = new AttributeType( "generationQualifier" );
        attributeType.setOid( "2.5.4.44" );

        // It should not fail
        assertTrue( schemaManager.delete( attributeType ) );

        assertFalse( isATPresent( schemaManager, "generationQualifier" ) );
        assertEquals( atrSize - 1, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( goidSize - 1, schemaManager.getOidRegistry().size() );
    }


    /**
     * Delete an existing AT referenced by some other OC
     */
    @Test
    public void testDelExistingAttributeTypeReferencedByOC() throws Exception
    {
        SchemaManager schemaManager = loadSchema( "Core" );

        int atrSize = schemaManager.getAttributeTypeRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        // Try to delete an AT which is referenced by at least one OC
        // (modifiersName has one descendant : schemaModifiersName)
        AttributeType attributeType = schemaManager.lookupAttributeTypeRegistry( "cn" );

        // It should fail
        assertFalse( schemaManager.delete( attributeType ) );

        assertTrue( isATPresent( schemaManager, "cn" ) );
        assertEquals( atrSize, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );
    }


    /**
     * Delete an existing AT stored in some disabled schema
     */
    @Test
    public void testDelAttributeTypeFromDisabledSchema() throws Exception
    {
        SchemaManager schemaManager = loadSchema( "Core" );

        int atrSize = schemaManager.getAttributeTypeRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        // Try to delete an AT which is contained by a disabled schema
        AttributeType attributeType = new AttributeType( "gecos" );
        attributeType.setOid( "1.3.6.1.1.1.1.2" );

        // It should fail
        assertFalse( schemaManager.delete( attributeType ) );

        assertFalse( isATPresent( schemaManager, "gecos" ) );
        assertEquals( atrSize, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );
    }


    /**
     * Delete an existing AT referenced by some descendant
     */
    @Test
    public void testDelExistingAttributeTypeReferencedByDescendant() throws Exception
    {
        SchemaManager schemaManager = loadSchema( "Apache" );

        int atrSize = schemaManager.getAttributeTypeRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        // Try to delete an AT which has descendant 
        // (modifiersName has one descendant : schemaModifiersName)
        AttributeType attributeType = schemaManager.lookupAttributeTypeRegistry( "modifiersName" );

        // It should fail
        assertFalse( schemaManager.delete( attributeType ) );

        assertTrue( isATPresent( schemaManager, "modifiersName" ) );
        assertEquals( atrSize, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );
    }


    //=========================================================================
    // Comparator deletion tests
    //-------------------------------------------------------------------------

    @Test
    public void testDeleteExistingComparator() throws Exception
    {
        SchemaManager schemaManager = loadSchema( "system" );
        int ctrSize = schemaManager.getComparatorRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        LdapComparator<?> lc = new BooleanComparator( "0.1.1" );
        assertTrue( schemaManager.add( lc ) );

        assertEquals( ctrSize + 1, schemaManager.getComparatorRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );

        lc = schemaManager.lookupComparatorRegistry( "0.1.1" );
        assertNotNull( lc );
        assertTrue( schemaManager.delete( lc ) );
        
        try
        {
            schemaManager.lookupComparatorRegistry( "0.1.1" );
            fail();
        }
        catch ( Exception e )
        {
            // expected
        }

        assertEquals( ctrSize, schemaManager.getComparatorRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );
    }


    @Test
    public void testDeleteNonExistingComparator() throws Exception
    {
        SchemaManager schemaManager = loadSchema( "system" );
        int ctrSize = schemaManager.getComparatorRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        LdapComparator<?> lc = new BooleanComparator( "0.0" );
        assertFalse( schemaManager.delete( lc ) );

        List<Throwable> errors = schemaManager.getErrors();
        assertFalse( errors.isEmpty() );

        assertEquals( ctrSize, schemaManager.getComparatorRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );
    }


    @Test
    public void testDeleteExistingComaparatorUsedByMatchingRule() throws Exception
    {
        SchemaManager schemaManager = loadSchema( "system" );
        int ctrSize = schemaManager.getComparatorRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        LdapComparator<?> lc = schemaManager.lookupComparatorRegistry( "2.5.13.0" );
        
        // shouldn't be deleted cause there is a MR associated with it
        assertFalse( schemaManager.delete( lc ) );

        List<Throwable> errors = schemaManager.getErrors();
        assertFalse( errors.isEmpty() );
        assertTrue( errors.get( 0 ) instanceof LdapSchemaViolationException );

        assertNotNull( schemaManager.lookupComparatorRegistry( "2.5.13.0" ) );
        assertEquals( ctrSize, schemaManager.getComparatorRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );
    }
    

    //=========================================================================
    // DITContentRule deletion tests
    //-------------------------------------------------------------------------
    // TODO

    //=========================================================================
    // DITStructureRule deletion tests
    //-------------------------------------------------------------------------
    // TODO

    //=========================================================================
    // MatchingRule deletion tests
    //-------------------------------------------------------------------------
    
    @Test
    public void testDeleteExistingMatchingRule() throws Exception
    {
        SchemaManager schemaManager = loadSchema( "system" );
        int mrSize = schemaManager.getMatchingRuleRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();
        
        MatchingRule mr = new MatchingRule( "2.5.13.33" );
        assertTrue( schemaManager.delete( mr ) );
        
        assertEquals( mrSize - 1, schemaManager.getMatchingRuleRegistry().size() );
        assertEquals( goidSize - 1, schemaManager.getOidRegistry().size() );
    }

    
    @Test
    public void testDeleteNonExistingMatchingRule() throws Exception
    {
        SchemaManager schemaManager = loadSchema( "system" );
        int mrSize = schemaManager.getMatchingRuleRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();
        
        MatchingRule mr = new MatchingRule( "0.1.1" );
        assertFalse( schemaManager.delete( mr ) );
        
        assertEquals( mrSize, schemaManager.getMatchingRuleRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );
    }

    
    @Test
    public void testDeleteExistingMatchingRuleUsedByAttributeType() throws Exception
    {
        SchemaManager schemaManager = loadSchema( "system" );
        int mrSize = schemaManager.getMatchingRuleRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        // AT with OID 2.5.18.4 has syntax 1.3.6.1.4.1.1466.115.121.1.12 which is used by MR 2.5.13.1
        MatchingRule mr = new MatchingRule( "2.5.13.1" );
        assertFalse( schemaManager.delete( mr ) );
        
        assertEquals( mrSize, schemaManager.getMatchingRuleRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );
    }

    //=========================================================================
    // MatchingRuleUse deletion tests
    //-------------------------------------------------------------------------
    // TODO

    //=========================================================================
    // NameForm deletion tests
    //-------------------------------------------------------------------------
    // TODO

    //=========================================================================
    // Normalizer deletion tests
    //-------------------------------------------------------------------------

    @Test
    public void testDeleteExistingNormalizer() throws Exception
    {
        SchemaManager schemaManager = loadSchema( "system" );
        int nrSize = schemaManager.getNormalizerRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        Normalizer nr = new BooleanNormalizer();
        nr.setOid( "0.1.1" );
        assertTrue( schemaManager.add( nr ) );

        assertEquals( nrSize + 1, schemaManager.getNormalizerRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );

        // FIXME this lookup is failing ! but it shouldn't be
        nr = schemaManager.lookupNormalizerRegistry( "0.1.1" );
        assertNotNull( nr );
        assertTrue( schemaManager.delete( nr ) );

        try
        {
            schemaManager.lookupNormalizerRegistry( "0.1.1" );
            fail();
        }
        catch ( Exception e )
        {
            // expected
        }

        assertEquals( nrSize, schemaManager.getNormalizerRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );
    }
    
    
    @Test
    public void testDeleteNonExistingNormalizer() throws Exception
    {
        SchemaManager schemaManager = loadSchema( "system" );
        int nrSize = schemaManager.getNormalizerRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        Normalizer nr = new BooleanNormalizer();
        nr.setOid( "0.0" ); 
        assertFalse( schemaManager.delete( nr ) );

        List<Throwable> errors = schemaManager.getErrors();
        assertFalse( errors.isEmpty() );

        assertEquals( nrSize, schemaManager.getNormalizerRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );
    }

    
    @Test
    public void testDeleteExistingNormalizerUsedByMatchingRule() throws Exception
    {
        SchemaManager schemaManager = loadSchema( "system" );
        int nrSize = schemaManager.getNormalizerRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        Normalizer nr = schemaManager.lookupNormalizerRegistry( "2.5.13.0" );
        // shouldn't be deleted cause there is a MR associated with it
        assertFalse( schemaManager.delete( nr ) );

        List<Throwable> errors = schemaManager.getErrors();
        assertFalse( errors.isEmpty() );
        assertTrue( errors.get( 0 ) instanceof LdapSchemaViolationException );

        assertNotNull( schemaManager.lookupNormalizerRegistry( "2.5.13.0" ) );
        assertEquals( nrSize, schemaManager.getNormalizerRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );
    }

    //=========================================================================
    // ObjectClass deletion tests
    //-------------------------------------------------------------------------

    @Test
    public void testDeleteExistingObjectClass() throws Exception
    {
        SchemaManager schemaManager = loadSchema( "system" );
        int ocSize = schemaManager.getObjectClassRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();
        
        ObjectClass oc = new ObjectClass( "2.5.17.2" );
        
        assertTrue( schemaManager.delete( oc ) );
        
        assertEquals( ocSize - 1, schemaManager.getObjectClassRegistry().size() );
        assertEquals( goidSize - 1, schemaManager.getOidRegistry().size() );
        
        try
        {
            schemaManager.lookupObjectClassRegistry( "2.5.17.2" );
            fail();
        }
        catch( Exception e )
        {
            // expected
        }
    }
    

    @Test
    public void testDeleteNonExistingObjectClass() throws Exception
    {
        SchemaManager schemaManager = loadSchema( "system" );
        int ocSize = schemaManager.getObjectClassRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();
        
        ObjectClass oc = new ObjectClass( "0.1.1" );
        
        assertFalse( schemaManager.delete( oc ) );
        
        assertEquals( ocSize, schemaManager.getObjectClassRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );
    }
    

    @Test
    public void testDeleteExistingObjectClassReferencedByAnotherObjectClass() throws Exception
    {
        SchemaManager schemaManager = loadSchema( "system" );
        int ocSize = schemaManager.getObjectClassRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();
        
        ObjectClass oc = new ObjectClass( "2.5.6.0" );
        
        // shouldn't delete the 'top' OC
        assertFalse( schemaManager.delete( oc ) );
        
        List<Throwable> errors = schemaManager.getErrors();
        assertFalse( errors.isEmpty() );
        assertTrue( errors.get( 0 ) instanceof LdapSchemaViolationException );

        assertEquals( ocSize, schemaManager.getObjectClassRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );
    }
    

    //=========================================================================
    // Syntax deletion tests
    //-------------------------------------------------------------------------
    
    @Test
    public void testDeleteExistingSyntax() throws Exception
    {
        SchemaManager schemaManager = loadSchema( "system" );
        int sSize = schemaManager.getLdapSyntaxRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        // delete a existing syntax not used by AT and MR
        LdapSyntax syntax = schemaManager.lookupLdapSyntaxRegistry( "1.3.6.1.4.1.1466.115.121.1.10" );
        assertTrue( schemaManager.delete( syntax ) );
        
        assertEquals( sSize - 1, schemaManager.getLdapSyntaxRegistry().size() );
        assertEquals( goidSize -1, schemaManager.getOidRegistry().size() );

        // add a syntax and then delete (should behave same as above )
        syntax = new LdapSyntax( "0.1.1" );
        assertTrue( schemaManager.add( syntax ) );

        assertEquals( sSize, schemaManager.getLdapSyntaxRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );
        
        syntax = schemaManager.lookupLdapSyntaxRegistry( "0.1.1" );
        assertTrue( schemaManager.delete( syntax ) );

        try
        {
            schemaManager.lookupLdapSyntaxRegistry( "0.1.1" );
            fail( "shouldn't find the syntax" );
        }
        catch( Exception e )
        {
            // expected behaviour
        }
        
        assertEquals( sSize - 1, schemaManager.getLdapSyntaxRegistry().size() );
        assertEquals( goidSize - 1, schemaManager.getOidRegistry().size() );
    }

    
    @Test
    public void testDeleteNonExistingSyntax() throws Exception
    {
        SchemaManager schemaManager = loadSchema( "system" );
        int sSize = schemaManager.getLdapSyntaxRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        LdapSyntax syntax = new LdapSyntax( "0.1.1" );
        
        assertFalse( schemaManager.delete( syntax ) );
        
        assertEquals( sSize, schemaManager.getLdapSyntaxRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );
    }

    
    @Test
    public void testDeleteSyntaxUsedByMatchingRule() throws Exception
    {

        SchemaManager schemaManager = loadSchema( "system" );
        int sSize = schemaManager.getLdapSyntaxRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        //1.3.6.1.4.1.1466.115.121.1.26 is used by MR 1.3.6.1.4.1.1466.109.114.2
        LdapSyntax syntax = new LdapSyntax( "1.3.6.1.4.1.1466.115.121.1.26" );
        assertFalse( schemaManager.delete( syntax ) );
        
        // syntax 1.3.6.1.4.1.1466.115.121.1.12 is used by MR 2.5.13.1 and many AT
        syntax = new LdapSyntax( "1.3.6.1.4.1.1466.115.121.1.12" );
        
        assertFalse( schemaManager.delete( syntax ) );
        
        assertEquals( sSize, schemaManager.getLdapSyntaxRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );
    }

    
    @Test
    public void testDeleteSyntaxUsedByAttributeType() throws Exception
    {
       // syntax 1.3.6.1.4.1.1466.115.121.1.15 is used by AT 1.3.6.1.1.4

        SchemaManager schemaManager = loadSchema( "system" );
        int sSize = schemaManager.getLdapSyntaxRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        LdapSyntax syntax = new LdapSyntax( "1.3.6.1.4.1.1466.115.121.1.15" );
        
        assertFalse( schemaManager.delete( syntax ) );
        
        assertEquals( sSize, schemaManager.getLdapSyntaxRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );
    }

    //=========================================================================
    // SyntaxChecker deletion tests
    //-------------------------------------------------------------------------
    
    @Test
    public void testDeleteExistingSyntaxChecker() throws Exception
    {
        SchemaManager schemaManager = loadSchema( "system" );
        int scrSize = schemaManager.getSyntaxCheckerRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        SyntaxChecker sc = new BooleanSyntaxChecker();
        sc.setOid( "0.1.1" );
        assertTrue( schemaManager.add( sc ) );

        assertEquals( scrSize + 1, schemaManager.getSyntaxCheckerRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );

        sc = schemaManager.lookupSyntaxCheckerRegistry( "0.1.1" );
        assertNotNull( sc );
        assertTrue( schemaManager.delete( sc ) );

        try
        {
            schemaManager.lookupSyntaxCheckerRegistry( "0.1.1" );
            fail();
        }
        catch ( Exception e )
        {
            // expected
        }

        assertEquals( scrSize, schemaManager.getSyntaxCheckerRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );
    }
    
    
    @Test
    public void testDeleteNonExistingSyntaxChecker() throws Exception
    {
        SchemaManager schemaManager = loadSchema( "system" );
        int scrSize = schemaManager.getSyntaxCheckerRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        SyntaxChecker sc = new BooleanSyntaxChecker();
        sc.setOid( "0.0" ); 
        assertFalse( schemaManager.delete( sc ) );

        List<Throwable> errors = schemaManager.getErrors();
        assertFalse( errors.isEmpty() );

        assertEquals( scrSize, schemaManager.getSyntaxCheckerRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );
    }

    
    @Test
    public void testDeleteSyntaxCheckerUsedBySyntax() throws Exception
    {
        SchemaManager schemaManager = loadSchema( "system" );
        int scrSize = schemaManager.getSyntaxCheckerRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        SyntaxChecker sc = schemaManager.lookupSyntaxCheckerRegistry( "1.3.6.1.4.1.1466.115.121.1.1" );
        
        //FIXME should return false but is returning true
        assertFalse( schemaManager.delete( sc ) );

        List<Throwable> errors = schemaManager.getErrors();
        assertFalse( errors.isEmpty() );
        assertTrue( errors.get( 0 ) instanceof LdapSchemaViolationException );

        assertEquals( scrSize, schemaManager.getSyntaxCheckerRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );
    }
}

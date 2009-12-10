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
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.NoSuchAttributeException;

import org.apache.commons.io.FileUtils;
import org.apache.directory.shared.ldap.exception.LdapSchemaViolationException;
import org.apache.directory.shared.ldap.schema.AttributeType;
import org.apache.directory.shared.ldap.schema.LdapComparator;
import org.apache.directory.shared.ldap.schema.SchemaManager;
import org.apache.directory.shared.ldap.schema.UsageEnum;
import org.apache.directory.shared.ldap.schema.comparators.BooleanComparator;
import org.apache.directory.shared.ldap.schema.comparators.CsnComparator;
import org.apache.directory.shared.ldap.schema.ldif.extractor.SchemaLdifExtractor;
import org.apache.directory.shared.schema.DefaultSchemaManager;
import org.apache.directory.shared.schema.loader.ldif.LdifSchemaLoader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;


/**
 * A test class for SchemaManager, testig the addition of a SchemaObject.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemaManagerAddTest
{
    // A directory in which the ldif files will be stored
    private static String workingDirectory;

    // The schema repository
    private static File   schemaRepository;


    @BeforeClass
    public static void setup() throws Exception
    {
        workingDirectory = System.getProperty( "workingDirectory" );

        if ( workingDirectory == null )
        {
            String path = SchemaManagerAddTest.class.getResource( "" ).getPath();
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


    private SchemaManager loadSystem() throws Exception
    {
        LdifSchemaLoader loader = new LdifSchemaLoader( schemaRepository );
        SchemaManager schemaManager = new DefaultSchemaManager( loader );

        String schemaName = "system";

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
    // Registry has grown : its size must be one bigger. If the SchemaObject
    // is not loadable, then the GlobalOidRegistry must also have grown.
    //=========================================================================
    // AttributeType addition tests
    //-------------------------------------------------------------------------
    // First, not defined superior
    //-------------------------------------------------------------------------
    /**
     * Try to inject an AttributeType without any superior nor Syntax : it's invalid
     */
    @Test
    public void testAddAttributeTypeNoSupNoSyntaxNoSuperior() throws Exception
    {
        SchemaManager schemaManager = loadSystem();
        int atrSize = schemaManager.getAttributeTypeRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        AttributeType attributeType = new AttributeType( "1.1.0" );
        attributeType.setEqualityOid( "2.5.13.1" );
        attributeType.setOrderingOid( null );
        attributeType.setSubstringOid( null );

        // It should fail
        assertFalse( schemaManager.add( attributeType ) );

        List<Throwable> errors = schemaManager.getErrors();
        assertEquals( 1, errors.size() );
        Throwable error = errors.get( 0 );

        assertTrue( error instanceof LdapSchemaViolationException );

        assertFalse( isATPresent( schemaManager, "1.1.0" ) );
        assertEquals( atrSize, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );
    }


    /**
     * Try to inject an AttributeType which is Collective, and userApplication AT
     */
    @Test
    public void testAddAttributeTypeNoSupCollectiveUser() throws Exception
    {
        SchemaManager schemaManager = loadSystem();
        int atrSize = schemaManager.getAttributeTypeRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        AttributeType attributeType = new AttributeType( "1.1.0" );
        attributeType.setEqualityOid( "2.5.13.1" );
        attributeType.setOrderingOid( null );
        attributeType.setSubstringOid( null );
        attributeType.setSyntaxOid( "1.3.6.1.4.1.1466.115.121.1.26" );
        attributeType.setUsage( UsageEnum.USER_APPLICATIONS );
        attributeType.setCollective( true );

        // It should not fail
        assertTrue( schemaManager.add( attributeType ) );

        assertTrue( isATPresent( schemaManager, "1.1.0" ) );
        assertEquals( atrSize + 1, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( goidSize + 1, schemaManager.getOidRegistry().size() );
    }


    /**
     * Try to inject an AttributeType which is Collective, but an operational AT
     */
    @Test
    public void testAddAttributeTypeNoSupCollectiveOperational() throws Exception
    {
        SchemaManager schemaManager = loadSystem();
        int atrSize = schemaManager.getAttributeTypeRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        AttributeType attributeType = new AttributeType( "1.1.0" );
        attributeType.setEqualityOid( "2.5.13.1" );
        attributeType.setOrderingOid( null );
        attributeType.setSubstringOid( null );
        attributeType.setSyntaxOid( "1.3.6.1.4.1.1466.115.121.1.26" );
        attributeType.setUsage( UsageEnum.DIRECTORY_OPERATION );
        attributeType.setCollective( true );

        // It should fail
        assertFalse( schemaManager.add( attributeType ) );

        List<Throwable> errors = schemaManager.getErrors();
        assertEquals( 1, errors.size() );
        Throwable error = errors.get( 0 );

        assertTrue( error instanceof LdapSchemaViolationException );

        assertFalse( isATPresent( schemaManager, "1.1.0" ) );
        assertEquals( atrSize, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );
    }


    /**
     * Try to inject an AttributeType which is a NO-USER-MODIFICATION and userApplication
     */
    @Test
    public void testAddAttributeTypeNoSupNoUserModificationUserAplication() throws Exception
    {
        SchemaManager schemaManager = loadSystem();
        int atrSize = schemaManager.getAttributeTypeRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        AttributeType attributeType = new AttributeType( "1.1.0" );
        attributeType.setEqualityOid( "2.5.13.1" );
        attributeType.setOrderingOid( null );
        attributeType.setSubstringOid( null );
        attributeType.setSyntaxOid( "1.3.6.1.4.1.1466.115.121.1.26" );
        attributeType.setUsage( UsageEnum.USER_APPLICATIONS );
        attributeType.setUserModifiable( false );

        // It should fail
        assertFalse( schemaManager.add( attributeType ) );

        List<Throwable> errors = schemaManager.getErrors();
        assertEquals( 1, errors.size() );
        Throwable error = errors.get( 0 );

        assertTrue( error instanceof LdapSchemaViolationException );

        assertFalse( isATPresent( schemaManager, "1.1.0" ) );
        assertEquals( atrSize, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );
    }


    /**
     * Try to inject an AttributeType which is a NO-USER-MODIFICATION and is operational
     */
    @Test
    public void testAddAttributeTypeNoSupNoUserModificationOpAttr() throws Exception
    {
        SchemaManager schemaManager = loadSystem();
        int atrSize = schemaManager.getAttributeTypeRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        AttributeType attributeType = new AttributeType( "1.1.0" );
        attributeType.setEqualityOid( "2.5.13.1" );
        attributeType.setOrderingOid( null );
        attributeType.setSubstringOid( null );
        attributeType.setSyntaxOid( "1.3.6.1.4.1.1466.115.121.1.26" );
        attributeType.setUsage( UsageEnum.DISTRIBUTED_OPERATION );
        attributeType.setUserModifiable( false );

        // It should not fail
        assertTrue( schemaManager.add( attributeType ) );

        assertTrue( isATPresent( schemaManager, "1.1.0" ) );
        assertEquals( atrSize + 1, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( goidSize + 1, schemaManager.getOidRegistry().size() );
    }


    /**
     * Try to inject an AttributeType with an invalid EQUALITY MR
     */
    @Test
    public void testAddAttributeTypeNoSupInvalidEqualityMR() throws Exception
    {
        SchemaManager schemaManager = loadSystem();
        int atrSize = schemaManager.getAttributeTypeRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        AttributeType attributeType = new AttributeType( "1.1.0" );
        attributeType.setEqualityOid( "0.0" );
        attributeType.setOrderingOid( null );
        attributeType.setSubstringOid( null );
        attributeType.setSyntaxOid( "1.3.6.1.4.1.1466.115.121.1.26" );
        attributeType.setUsage( UsageEnum.USER_APPLICATIONS );

        // It should fail
        assertFalse( schemaManager.add( attributeType ) );

        List<Throwable> errors = schemaManager.getErrors();
        assertEquals( 1, errors.size() );
        Throwable error = errors.get( 0 );

        assertTrue( error instanceof LdapSchemaViolationException );

        assertFalse( isATPresent( schemaManager, "1.1.0" ) );
        assertEquals( atrSize, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );
    }


    /**
     * Try to inject an AttributeType with an invalid ORDERING MR
     */
    @Test
    public void testAddAttributeTypeNoSupInvalidOrderingMR() throws Exception
    {
        SchemaManager schemaManager = loadSystem();
        int atrSize = schemaManager.getAttributeTypeRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        AttributeType attributeType = new AttributeType( "1.1.0" );
        attributeType.setEqualityOid( null );
        attributeType.setOrderingOid( "0.0" );
        attributeType.setSubstringOid( null );
        attributeType.setSyntaxOid( "1.3.6.1.4.1.1466.115.121.1.26" );
        attributeType.setUsage( UsageEnum.USER_APPLICATIONS );

        // It should fail
        assertFalse( schemaManager.add( attributeType ) );

        List<Throwable> errors = schemaManager.getErrors();
        assertEquals( 1, errors.size() );
        Throwable error = errors.get( 0 );

        assertTrue( error instanceof LdapSchemaViolationException );

        assertFalse( isATPresent( schemaManager, "1.1.0" ) );
        assertEquals( atrSize, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );
    }


    /**
     * Try to inject an AttributeType with an invalid SUBSTR MR
     */
    @Test
    public void testAddAttributeTypeNoSupInvalidSubstringMR() throws Exception
    {
        SchemaManager schemaManager = loadSystem();
        int atrSize = schemaManager.getAttributeTypeRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        AttributeType attributeType = new AttributeType( "1.1.0" );
        attributeType.setEqualityOid( null );
        attributeType.setOrderingOid( null );
        attributeType.setSubstringOid( "0.0" );
        attributeType.setSyntaxOid( "1.3.6.1.4.1.1466.115.121.1.26" );
        attributeType.setUsage( UsageEnum.USER_APPLICATIONS );

        // It should fail
        assertFalse( schemaManager.add( attributeType ) );

        List<Throwable> errors = schemaManager.getErrors();
        assertEquals( 1, errors.size() );
        Throwable error = errors.get( 0 );

        assertTrue( error instanceof LdapSchemaViolationException );

        assertFalse( isATPresent( schemaManager, "1.1.0" ) );
        assertEquals( atrSize, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );
    }


    /**
     * Try to inject an AttributeType with valid MRs
     */
    @Test
    public void testAddAttributeTypeNoSupValidMR() throws Exception
    {
        SchemaManager schemaManager = loadSystem();
        int atrSize = schemaManager.getAttributeTypeRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        AttributeType attributeType = new AttributeType( "1.1.0" );
        attributeType.setEqualityOid( "2.5.13.1" );
        attributeType.setOrderingOid( "2.5.13.1" );
        attributeType.setSubstringOid( "2.5.13.1" );
        attributeType.setSyntaxOid( "1.3.6.1.4.1.1466.115.121.1.26" );
        attributeType.setUsage( UsageEnum.USER_APPLICATIONS );

        // It should not fail
        assertTrue( schemaManager.add( attributeType ) );

        assertTrue( isATPresent( schemaManager, "1.1.0" ) );
        assertEquals( atrSize + 1, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( goidSize + 1, schemaManager.getOidRegistry().size() );
    }


    /**
     * Try to inject an AttributeType which already exist
     */
    @Test
    public void testAddAttributeTypeAlreadyExist() throws Exception
    {
        SchemaManager schemaManager = loadSystem();
        int atrSize = schemaManager.getAttributeTypeRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        AttributeType attributeType = new AttributeType( "2.5.18.4" );
        attributeType.setEqualityOid( "2.5.13.1" );
        attributeType.setOrderingOid( "2.5.13.1" );
        attributeType.setSubstringOid( "2.5.13.1" );

        // It should fail
        assertFalse( schemaManager.add( attributeType ) );

        List<Throwable> errors = schemaManager.getErrors();
        assertEquals( 1, errors.size() );
        Throwable error = errors.get( 0 );

        assertTrue( error instanceof LdapSchemaViolationException );

        // The AT must be there
        assertTrue( isATPresent( schemaManager, "2.5.18.4" ) );

        // Check that it hasen't changed
        AttributeType original = schemaManager.lookupAttributeTypeRegistry( "2.5.18.4" );
        assertEquals( "distinguishedNameMatch", original.getEqualityOid() );
        assertEquals( atrSize, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );
    }


    //-------------------------------------------------------------------------
    // Then, with a superior
    //-------------------------------------------------------------------------
    /**
     * Try to inject an AttributeType with a superior and no Syntax : it should
     * take its superior' syntax and MR
     */
    @Test
    public void testAddAttributeTypeSupNoSyntaxNoSuperior() throws Exception
    {
        SchemaManager schemaManager = loadSystem();
        int atrSize = schemaManager.getAttributeTypeRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        AttributeType attributeType = new AttributeType( "1.1.0" );
        attributeType.setEqualityOid( null );
        attributeType.setOrderingOid( null );
        attributeType.setSubstringOid( null );
        attributeType.setSuperiorOid( "2.5.18.4" );
        attributeType.setUsage( UsageEnum.DIRECTORY_OPERATION );

        // It should not fail
        assertTrue( schemaManager.add( attributeType ) );

        AttributeType result = schemaManager.lookupAttributeTypeRegistry( "1.1.0" );

        assertEquals( "1.3.6.1.4.1.1466.115.121.1.12", result.getSyntaxOid() );
        assertEquals( "2.5.13.1", result.getEqualityOid() );
        assertEquals( atrSize + 1, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( goidSize + 1, schemaManager.getOidRegistry().size() );
    }


    /**
     * Try to inject an AttributeType with a superior and different USAGE
     */
    @Test
    public void testAddAttributeTypeSupDifferentUsage() throws Exception
    {
        SchemaManager schemaManager = loadSystem();
        int atrSize = schemaManager.getAttributeTypeRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        AttributeType attributeType = new AttributeType( "1.1.0" );
        attributeType.setEqualityOid( null );
        attributeType.setOrderingOid( null );
        attributeType.setSubstringOid( null );
        attributeType.setSuperiorOid( "2.5.18.4" );
        attributeType.setUsage( UsageEnum.DISTRIBUTED_OPERATION );

        // It should fail
        assertFalse( schemaManager.add( attributeType ) );

        List<Throwable> errors = schemaManager.getErrors();
        assertEquals( 1, errors.size() );
        Throwable error = errors.get( 0 );

        assertTrue( error instanceof LdapSchemaViolationException );

        assertFalse( isATPresent( schemaManager, "1.1.0" ) );
        assertEquals( atrSize, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );
    }


    /**
     * Try to inject an AttributeType with itself as a superior
     */
    @Test
    public void testAddAttributeTypeSupWithOwnSup() throws Exception
    {
        SchemaManager schemaManager = loadSystem();
        int atrSize = schemaManager.getAttributeTypeRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        AttributeType attributeType = new AttributeType( "1.1.0" );
        attributeType.setEqualityOid( null );
        attributeType.setOrderingOid( null );
        attributeType.setSubstringOid( null );
        attributeType.setSuperiorOid( "1.1.0" );
        attributeType.setUsage( UsageEnum.DISTRIBUTED_OPERATION );

        // It should fail
        assertFalse( schemaManager.add( attributeType ) );

        List<Throwable> errors = schemaManager.getErrors();
        assertEquals( 1, errors.size() );
        Throwable error = errors.get( 0 );

        assertTrue( error instanceof LdapSchemaViolationException );

        assertFalse( isATPresent( schemaManager, "1.1.0" ) );
        assertEquals( atrSize, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );
    }


    /**
     * Try to inject an AttributeType with a bad superior
     */
    @Test
    public void testAddAttributeTypeSupBadSup() throws Exception
    {
        SchemaManager schemaManager = loadSystem();
        int atrSize = schemaManager.getAttributeTypeRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        AttributeType attributeType = new AttributeType( "1.1.0" );
        attributeType.setEqualityOid( null );
        attributeType.setOrderingOid( null );
        attributeType.setSubstringOid( null );
        attributeType.setSuperiorOid( "0.0" );
        attributeType.setUsage( UsageEnum.DISTRIBUTED_OPERATION );

        // It should fail
        assertFalse( schemaManager.add( attributeType ) );

        List<Throwable> errors = schemaManager.getErrors();
        assertEquals( 1, errors.size() );
        Throwable error = errors.get( 0 );

        assertTrue( error instanceof LdapSchemaViolationException );

        assertFalse( isATPresent( schemaManager, "1.1.0" ) );
        assertEquals( atrSize, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );
    }


    //=========================================================================
    // Comparator addition tests
    //-------------------------------------------------------------------------
    @Test
    public void testAddNewComparator() throws Exception
    {
        SchemaManager schemaManager = loadSystem();
        int ctrSize = schemaManager.getComparatorRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        String oid = "0.0.0";
        LdapComparator<?> lc = new BooleanComparator( oid );

        assertTrue( schemaManager.add( lc ) );

        List<Throwable> errors = schemaManager.getErrors();
        assertEquals( 0, errors.size() );

        assertEquals( ctrSize + 1, schemaManager.getComparatorRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );

        try
        {
            LdapComparator<?> added = schemaManager.lookupComparatorRegistry( oid );

            assertNotNull( added );
        }
        catch ( NamingException ne )
        {
            fail();
        }
    }


    //@Test
    public void testAddAlreadyExistingComparator() throws Exception
    {
        SchemaManager schemaManager = loadSystem();
        int ctrSize = schemaManager.getComparatorRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        String oid = "0.0.0";
        LdapComparator<?> lc = new BooleanComparator( oid );

        assertTrue( schemaManager.add( lc ) );

        try
        {
            LdapComparator<?> added = schemaManager.lookupComparatorRegistry( oid );

            assertNotNull( added );
        }
        catch ( NamingException ne )
        {
            fail();
        }

        List<Throwable> errors = schemaManager.getErrors();
        assertEquals( 0, errors.size() );
        assertEquals( ctrSize + 1, schemaManager.getComparatorRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );

        lc = new CsnComparator( oid );

        assertFalse( schemaManager.add( lc ) );

        errors = schemaManager.getErrors();
        assertEquals( 1, errors.size() );

        assertEquals( ctrSize + 1, schemaManager.getComparatorRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );

        try
        {
            LdapComparator<?> added = schemaManager.lookupComparatorRegistry( oid );

            assertNotNull( added );
        }
        catch ( NamingException ne )
        {
            fail();
        }
    }


    /**
     * Test that we can't add two comparators with the same class code.
     * 
     * This is a questionable test, as there is no real reason why it could
     * be a problem.
     */
    @Test
    public void testAddComparatorWithWrongFQCN() throws Exception
    {
        SchemaManager schemaManager = loadSystem();
        int ctrSize = schemaManager.getComparatorRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        String oid = "0.0.0";
        LdapComparator<?> lc = new BooleanComparator( oid );
        // using java.sql.ResultSet cause it is very unlikely to get loaded
        // in ADS, as the FQCN is not the one expected
        lc.setFqcn( "java.sql.ResultSet" );

        assertFalse( schemaManager.add( lc ) );

        List<Throwable> errors = schemaManager.getErrors();
        errors = schemaManager.getErrors();
        assertEquals( 1, errors.size() );

        assertEquals( ctrSize, schemaManager.getComparatorRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );

        try
        {
            LdapComparator<?> added = schemaManager.lookupComparatorRegistry( oid );
            fail();
        }
        catch ( Exception e )
        {
            // Expected
            assertTrue( true );
        }
    }


    @Ignore
    // Definitively not an issue.
    @Test
    public void testAddNewComparatorWithDuplicateName() throws Exception
    {
        SchemaManager schemaManager = loadSystem();
        int ctrSize = schemaManager.getComparatorRegistry().size();
        int goidSize = schemaManager.getOidRegistry().size();

        List<String> names = new ArrayList<String>();
        names.add( "name" );
        names.add( "name" );

        String oid = "0.0.0";
        LdapComparator<?> lc = new BooleanComparator( oid );
        lc.setNames( names );

        // FIXME this should fail cause the same name was set twice
        assertTrue( schemaManager.add( lc ) );

        List<Throwable> errors = schemaManager.getErrors();
        errors = schemaManager.getErrors();
        assertEquals( 1, errors.size() );

        assertEquals( ctrSize + 1, schemaManager.getComparatorRegistry().size() );
        assertEquals( goidSize, schemaManager.getOidRegistry().size() );

        try
        {
            LdapComparator<?> added = schemaManager.lookupComparatorRegistry( oid );

            assertNotNull( added );
        }
        catch ( NamingException ne )
        {
            fail();
        }

    }

    //=========================================================================
    // DITContentRule addition tests
    //-------------------------------------------------------------------------
    // TODO

    //=========================================================================
    // DITStructureRule addition tests
    //-------------------------------------------------------------------------
    // TODO

    //=========================================================================
    // MatchingRule addition tests
    //-------------------------------------------------------------------------
    // TODO

    //=========================================================================
    // MatchingRuleUse addition tests
    //-------------------------------------------------------------------------
    // TODO

    //=========================================================================
    // NameForm addition tests
    //-------------------------------------------------------------------------
    // TODO

    //=========================================================================
    // Normalizer addition tests
    //-------------------------------------------------------------------------
    // TODO

    //=========================================================================
    // ObjectClass addition tests
    //-------------------------------------------------------------------------
    // TODO

    //=========================================================================
    // Syntax addition tests
    //-------------------------------------------------------------------------
    // TODO

    //=========================================================================
    // SyntaxChecker addition tests
    //-------------------------------------------------------------------------
    // TODO

}

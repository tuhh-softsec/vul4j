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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.NoSuchAttributeException;

import org.apache.commons.io.FileUtils;
import org.apache.directory.shared.ldap.exception.LdapSchemaViolationException;
import org.apache.directory.shared.ldap.schema.AttributeType;
import org.apache.directory.shared.ldap.schema.SchemaManager;
import org.apache.directory.shared.ldap.schema.UsageEnum;
import org.apache.directory.shared.ldap.schema.ldif.extractor.SchemaLdifExtractor;
import org.apache.directory.shared.schema.DefaultSchemaManager;
import org.apache.directory.shared.schema.loader.ldif.LdifSchemaLoader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * A test class for SchemaManager.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemaManagerTest
{
    // A directory in which the ldif files will be stored
    private static String workingDirectory;

    // The schema repository
    private static File schemaRepository;


    // A LDIF loader
    //private static LdifSchemaLoader ldifLoader;

    // A SchemaObject factory
    //private static SchemaEntityFactory factory;

    @BeforeClass
    public static void setup() throws Exception
    {
        workingDirectory = System.getProperty( "workingDirectory" );

        if ( workingDirectory == null )
        {
            String path = SchemaManagerTest.class.getResource( "" ).getPath();
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
    // is not loadable, then the GlobalOidRegistry must also have grwon.
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


    //-------------------------------------------------------------------------
    // Test the load( String... schemaName) method
    //-------------------------------------------------------------------------
    /**
     * test loading the "system" schema 
     */
    @Test
    public void testLoadSystem() throws Exception
    {
        LdifSchemaLoader loader = new LdifSchemaLoader( schemaRepository );
        SchemaManager schemaManager = new DefaultSchemaManager( loader );

        schemaManager.loadWithDeps( "system" );

        assertTrue( schemaManager.getErrors().isEmpty() );
        assertEquals( 38, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( 35, schemaManager.getComparatorRegistry().size() );
        assertEquals( 35, schemaManager.getMatchingRuleRegistry().size() );
        assertEquals( 35, schemaManager.getNormalizerRegistry().size() );
        assertEquals( 9, schemaManager.getObjectClassRegistry().size() );
        assertEquals( 59, schemaManager.getSyntaxCheckerRegistry().size() );
        assertEquals( 59, schemaManager.getLdapSyntaxRegistry().size() );
        assertEquals( 141, schemaManager.getOidRegistry().size() );

        assertEquals( 1, schemaManager.getRegistries().getLoadedSchemas().size() );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "system" ) );
    }


    /**
     * test loading the "core" schema, which depends on "system"
     */
    @Test
    public void testLoadCore() throws Exception
    {
        LdifSchemaLoader loader = new LdifSchemaLoader( schemaRepository );
        SchemaManager schemaManager = new DefaultSchemaManager( loader );

        schemaManager.loadWithDeps( "core" );

        assertTrue( schemaManager.getErrors().isEmpty() );
        assertEquals( 92, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( 35, schemaManager.getComparatorRegistry().size() );
        assertEquals( 35, schemaManager.getMatchingRuleRegistry().size() );
        assertEquals( 35, schemaManager.getNormalizerRegistry().size() );
        assertEquals( 36, schemaManager.getObjectClassRegistry().size() );
        assertEquals( 59, schemaManager.getSyntaxCheckerRegistry().size() );
        assertEquals( 59, schemaManager.getLdapSyntaxRegistry().size() );
        assertEquals( 222, schemaManager.getOidRegistry().size() );

        assertEquals( 2, schemaManager.getRegistries().getLoadedSchemas().size() );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "system" ) );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "core" ) );
    }


    /**
     * test loading the "apache" schema, which depends on "system" and "core"
     */
    @Test
    public void testLoadApache() throws Exception
    {
        LdifSchemaLoader loader = new LdifSchemaLoader( schemaRepository );
        SchemaManager schemaManager = new DefaultSchemaManager( loader );

        schemaManager.loadWithDeps( "apache" );

        assertTrue( schemaManager.getErrors().isEmpty() );
        assertEquals( 145, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( 43, schemaManager.getComparatorRegistry().size() );
        assertEquals( 43, schemaManager.getMatchingRuleRegistry().size() );
        assertEquals( 43, schemaManager.getNormalizerRegistry().size() );
        assertEquals( 53, schemaManager.getObjectClassRegistry().size() );
        assertEquals( 62, schemaManager.getSyntaxCheckerRegistry().size() );
        assertEquals( 66, schemaManager.getLdapSyntaxRegistry().size() );
        assertEquals( 307, schemaManager.getOidRegistry().size() );

        assertEquals( 3, schemaManager.getRegistries().getLoadedSchemas().size() );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "system" ) );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "core" ) );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "apache" ) );
    }


    /**
     * test loading the "apacheMeta" schema, which depends on "system"
     */
    @Test
    public void testLoadApacheMeta() throws Exception
    {
        LdifSchemaLoader loader = new LdifSchemaLoader( schemaRepository );
        SchemaManager schemaManager = new DefaultSchemaManager( loader );

        schemaManager.loadWithDeps( "apacheMeta" );

        assertTrue( schemaManager.getErrors().isEmpty() );
        assertEquals( 69, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( 40, schemaManager.getComparatorRegistry().size() );
        assertEquals( 40, schemaManager.getMatchingRuleRegistry().size() );
        assertEquals( 42, schemaManager.getNormalizerRegistry().size() );
        assertEquals( 22, schemaManager.getObjectClassRegistry().size() );
        assertEquals( 63, schemaManager.getSyntaxCheckerRegistry().size() );
        assertEquals( 64, schemaManager.getLdapSyntaxRegistry().size() );
        assertEquals( 195, schemaManager.getOidRegistry().size() );

        assertEquals( 2, schemaManager.getRegistries().getLoadedSchemas().size() );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "system" ) );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "apachemeta" ) );
    }


    /**
     * test loading the "java" schema, which depends on "system" and "core"
     */
    @Test
    public void testLoadJava() throws Exception
    {
        LdifSchemaLoader loader = new LdifSchemaLoader( schemaRepository );
        SchemaManager schemaManager = new DefaultSchemaManager( loader );

        schemaManager.loadWithDeps( "Java" );

        assertTrue( schemaManager.getErrors().isEmpty() );
        assertEquals( 99, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( 35, schemaManager.getComparatorRegistry().size() );
        assertEquals( 35, schemaManager.getMatchingRuleRegistry().size() );
        assertEquals( 35, schemaManager.getNormalizerRegistry().size() );
        assertEquals( 41, schemaManager.getObjectClassRegistry().size() );
        assertEquals( 59, schemaManager.getSyntaxCheckerRegistry().size() );
        assertEquals( 59, schemaManager.getLdapSyntaxRegistry().size() );
        assertEquals( 234, schemaManager.getOidRegistry().size() );

        assertEquals( 3, schemaManager.getRegistries().getLoadedSchemas().size() );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "system" ) );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "core" ) );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "Java" ) );
    }


    /**
     * test loading the "other" schema, which depends on "system", "core",
     * "apache" and "apacheMeta"
     */
    @Test
    public void testLoadOther() throws Exception
    {
        LdifSchemaLoader loader = new LdifSchemaLoader( schemaRepository );
        SchemaManager schemaManager = new DefaultSchemaManager( loader );

        schemaManager.loadWithDeps( "other" );

        assertTrue( schemaManager.getErrors().isEmpty() );
        assertEquals( 176, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( 48, schemaManager.getComparatorRegistry().size() );
        assertEquals( 48, schemaManager.getMatchingRuleRegistry().size() );
        assertEquals( 50, schemaManager.getNormalizerRegistry().size() );
        assertEquals( 66, schemaManager.getObjectClassRegistry().size() );
        assertEquals( 66, schemaManager.getSyntaxCheckerRegistry().size() );
        assertEquals( 71, schemaManager.getLdapSyntaxRegistry().size() );
        assertEquals( 361, schemaManager.getOidRegistry().size() );

        assertEquals( 5, schemaManager.getRegistries().getLoadedSchemas().size() );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "system" ) );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "core" ) );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "apache" ) );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "apacheMeta" ) );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "other" ) );
    }


    /**
     * test loading the "cosine" schema, which depends on "system" and "core"
     */
    @Test
    public void testLoadCosine() throws Exception
    {
        LdifSchemaLoader loader = new LdifSchemaLoader( schemaRepository );
        SchemaManager schemaManager = new DefaultSchemaManager( loader );

        schemaManager.loadWithDeps( "cosine" );

        assertTrue( schemaManager.getErrors().isEmpty() );
        assertEquals( 133, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( 35, schemaManager.getComparatorRegistry().size() );
        assertEquals( 35, schemaManager.getMatchingRuleRegistry().size() );
        assertEquals( 35, schemaManager.getNormalizerRegistry().size() );
        assertEquals( 49, schemaManager.getObjectClassRegistry().size() );
        assertEquals( 59, schemaManager.getSyntaxCheckerRegistry().size() );
        assertEquals( 59, schemaManager.getLdapSyntaxRegistry().size() );
        assertEquals( 276, schemaManager.getOidRegistry().size() );

        assertEquals( 3, schemaManager.getRegistries().getLoadedSchemas().size() );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "system" ) );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "core" ) );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "cosine" ) );
    }


    /**
     * test loading the "InetOrgPerson" schema, which depends on "system", "core"
     * and "cosine"
     */
    @Test
    public void testLoadInetOrgPerson() throws Exception
    {
        LdifSchemaLoader loader = new LdifSchemaLoader( schemaRepository );
        SchemaManager schemaManager = new DefaultSchemaManager( loader );

        schemaManager.loadWithDeps( "InetOrgPerson" );

        assertTrue( schemaManager.getErrors().isEmpty() );
        assertEquals( 142, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( 35, schemaManager.getComparatorRegistry().size() );
        assertEquals( 35, schemaManager.getMatchingRuleRegistry().size() );
        assertEquals( 35, schemaManager.getNormalizerRegistry().size() );
        assertEquals( 50, schemaManager.getObjectClassRegistry().size() );
        assertEquals( 59, schemaManager.getSyntaxCheckerRegistry().size() );
        assertEquals( 59, schemaManager.getLdapSyntaxRegistry().size() );
        assertEquals( 286, schemaManager.getOidRegistry().size() );

        assertEquals( 4, schemaManager.getRegistries().getLoadedSchemas().size() );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "system" ) );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "core" ) );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "cosine" ) );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "InetOrgPerson" ) );
    }


    /**
     * test loading the "Collective" schema, which depends on "system" and "core"
     */
    @Test
    public void testLoadCollective() throws Exception
    {
        LdifSchemaLoader loader = new LdifSchemaLoader( schemaRepository );
        SchemaManager schemaManager = new DefaultSchemaManager( loader );

        schemaManager.loadWithDeps( "Collective" );

        assertTrue( schemaManager.getErrors().isEmpty() );
        assertEquals( 105, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( 35, schemaManager.getComparatorRegistry().size() );
        assertEquals( 35, schemaManager.getMatchingRuleRegistry().size() );
        assertEquals( 35, schemaManager.getNormalizerRegistry().size() );
        assertEquals( 36, schemaManager.getObjectClassRegistry().size() );
        assertEquals( 59, schemaManager.getSyntaxCheckerRegistry().size() );
        assertEquals( 59, schemaManager.getLdapSyntaxRegistry().size() );
        assertEquals( 235, schemaManager.getOidRegistry().size() );

        assertEquals( 3, schemaManager.getRegistries().getLoadedSchemas().size() );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "system" ) );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "core" ) );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "collective" ) );
    }


    /**
     * test loading the "Krb5Kdc" schema, which depends on "system" and "core"
     */
    @Test
    public void testLoadKrb5Kdc() throws Exception
    {
        LdifSchemaLoader loader = new LdifSchemaLoader( schemaRepository );
        SchemaManager schemaManager = new DefaultSchemaManager( loader );

        schemaManager.loadWithDeps( "Krb5Kdc" );

        assertTrue( schemaManager.getErrors().isEmpty() );
        assertEquals( 107, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( 35, schemaManager.getComparatorRegistry().size() );
        assertEquals( 35, schemaManager.getMatchingRuleRegistry().size() );
        assertEquals( 35, schemaManager.getNormalizerRegistry().size() );
        assertEquals( 39, schemaManager.getObjectClassRegistry().size() );
        assertEquals( 59, schemaManager.getSyntaxCheckerRegistry().size() );
        assertEquals( 59, schemaManager.getLdapSyntaxRegistry().size() );
        assertEquals( 240, schemaManager.getOidRegistry().size() );

        assertEquals( 3, schemaManager.getRegistries().getLoadedSchemas().size() );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "system" ) );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "core" ) );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "Krb5Kdc" ) );
    }


    /**
     * test loading the "nis" schema, which depends on "system", "core" and "cosine",
     * but is disabled
     */
    @Test
    public void testLoadNis() throws Exception
    {
        LdifSchemaLoader loader = new LdifSchemaLoader( schemaRepository );
        SchemaManager schemaManager = new DefaultSchemaManager( loader );

        schemaManager.loadWithDeps( "nis" );

        assertTrue( schemaManager.getErrors().isEmpty() );
        assertEquals( 0, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( 0, schemaManager.getComparatorRegistry().size() );
        assertEquals( 0, schemaManager.getMatchingRuleRegistry().size() );
        assertEquals( 0, schemaManager.getNormalizerRegistry().size() );
        assertEquals( 0, schemaManager.getObjectClassRegistry().size() );
        assertEquals( 0, schemaManager.getSyntaxCheckerRegistry().size() );
        assertEquals( 0, schemaManager.getLdapSyntaxRegistry().size() );
        assertEquals( 0, schemaManager.getOidRegistry().size() );

        assertEquals( 0, schemaManager.getRegistries().getLoadedSchemas().size() );
        assertNull( schemaManager.getRegistries().getLoadedSchema( "system" ) );
        assertNull( schemaManager.getRegistries().getLoadedSchema( "core" ) );
        assertNull( schemaManager.getRegistries().getLoadedSchema( "cosine" ) );
    }


    /**
     * Test loading a wrong schema
     */
    @Test
    public void testLoadWrongSchema() throws Exception
    {
        LdifSchemaLoader loader = new LdifSchemaLoader( schemaRepository );
        SchemaManager schemaManager = new DefaultSchemaManager( loader );

        schemaManager.loadWithDeps( "bad" );

        assertTrue( schemaManager.getErrors().isEmpty() );
        assertEquals( 0, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( 0, schemaManager.getComparatorRegistry().size() );
        assertEquals( 0, schemaManager.getMatchingRuleRegistry().size() );
        assertEquals( 0, schemaManager.getNormalizerRegistry().size() );
        assertEquals( 0, schemaManager.getObjectClassRegistry().size() );
        assertEquals( 0, schemaManager.getSyntaxCheckerRegistry().size() );
        assertEquals( 0, schemaManager.getLdapSyntaxRegistry().size() );
        assertEquals( 0, schemaManager.getOidRegistry().size() );

        assertEquals( 0, schemaManager.getRegistries().getLoadedSchemas().size() );
    }


    /**
     * test loading the "InetOrgPerson" and "core" schema, which depends on "system" and "cosine"
     */
    @Test
    public void testLoadCoreAndInetOrgPerson() throws Exception
    {
        LdifSchemaLoader loader = new LdifSchemaLoader( schemaRepository );
        SchemaManager schemaManager = new DefaultSchemaManager( loader );

        schemaManager.loadWithDeps( "core", "InetOrgPerson" );

        assertTrue( schemaManager.getErrors().isEmpty() );
        assertEquals( 142, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( 35, schemaManager.getComparatorRegistry().size() );
        assertEquals( 35, schemaManager.getMatchingRuleRegistry().size() );
        assertEquals( 35, schemaManager.getNormalizerRegistry().size() );
        assertEquals( 50, schemaManager.getObjectClassRegistry().size() );
        assertEquals( 59, schemaManager.getSyntaxCheckerRegistry().size() );
        assertEquals( 59, schemaManager.getLdapSyntaxRegistry().size() );
        assertEquals( 286, schemaManager.getOidRegistry().size() );

        assertEquals( 4, schemaManager.getRegistries().getLoadedSchemas().size() );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "system" ) );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "core" ) );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "cosine" ) );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "InetOrgPerson" ) );
    }


    /**
     * test loading the "InetOrgPerson", "core" and a bad schema
     */
    @Test
    public void testLoadCoreInetOrgPersonAndBad() throws Exception
    {
        LdifSchemaLoader loader = new LdifSchemaLoader( schemaRepository );
        SchemaManager schemaManager = new DefaultSchemaManager( loader );

        schemaManager.loadWithDeps( "core", "bad", "InetOrgPerson" );

        assertTrue( schemaManager.getErrors().isEmpty() );
        assertEquals( 142, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( 35, schemaManager.getComparatorRegistry().size() );
        assertEquals( 35, schemaManager.getMatchingRuleRegistry().size() );
        assertEquals( 35, schemaManager.getNormalizerRegistry().size() );
        assertEquals( 50, schemaManager.getObjectClassRegistry().size() );
        assertEquals( 59, schemaManager.getSyntaxCheckerRegistry().size() );
        assertEquals( 59, schemaManager.getLdapSyntaxRegistry().size() );
        assertEquals( 286, schemaManager.getOidRegistry().size() );

        assertEquals( 4, schemaManager.getRegistries().getLoadedSchemas().size() );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "system" ) );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "core" ) );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "cosine" ) );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "InetOrgPerson" ) );
    }


    /**
     * test loading the "InetOrgPerson", "core" and a disabled schema
     */
    @Test
    public void testLoadCoreInetOrgPersonAndNis() throws Exception
    {
        LdifSchemaLoader loader = new LdifSchemaLoader( schemaRepository );
        SchemaManager schemaManager = new DefaultSchemaManager( loader );

        schemaManager.loadWithDeps( "core", "nis", "InetOrgPerson" );

        assertTrue( schemaManager.getErrors().isEmpty() );
        assertEquals( 142, schemaManager.getAttributeTypeRegistry().size() );
        assertEquals( 35, schemaManager.getComparatorRegistry().size() );
        assertEquals( 35, schemaManager.getMatchingRuleRegistry().size() );
        assertEquals( 35, schemaManager.getNormalizerRegistry().size() );
        assertEquals( 50, schemaManager.getObjectClassRegistry().size() );
        assertEquals( 59, schemaManager.getSyntaxCheckerRegistry().size() );
        assertEquals( 59, schemaManager.getLdapSyntaxRegistry().size() );
        assertEquals( 286, schemaManager.getOidRegistry().size() );

        assertEquals( 4, schemaManager.getRegistries().getLoadedSchemas().size() );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "system" ) );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "core" ) );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "cosine" ) );
        assertNotNull( schemaManager.getRegistries().getLoadedSchema( "InetOrgPerson" ) );
    }
}

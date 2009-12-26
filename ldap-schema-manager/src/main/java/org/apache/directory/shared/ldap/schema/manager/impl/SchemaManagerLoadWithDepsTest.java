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
package org.apache.directory.shared.ldap.schema.manager.impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.directory.shared.ldap.exception.LdapOperationNotSupportedException;
import org.apache.directory.shared.ldap.schema.manager.impl.DefaultSchemaManager;
import org.apache.directory.shared.ldap.schema.SchemaManager;
import org.apache.directory.shared.ldap.schema.ldif.extractor.impl.DefaultSchemaLdifExtractor;
import org.apache.directory.shared.ldap.schema.ldif.extractor.SchemaLdifExtractor;
import org.apache.directory.shared.ldap.schema.loader.ldif.LdifSchemaLoader;
import org.apache.directory.shared.ldap.schema.registries.DefaultSchema;
import org.apache.directory.shared.ldap.schema.registries.Schema;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * A test class for SchemaManager loadWithDeps() method. We test those methods here :
 * 
 *  Server API
 *     boolean loadWithDeps( Schema... schemas ) throws Exception
 *     boolean loadWithDeps( String... schemas ) throws Exception
 *
 *  Studio API :
 *     boolean loadWithDepsRelaxed( Schema... schemas ) throws Exception
 *     boolean loadWithDepsRelaxed( String... schemas ) throws Exception
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemaManagerLoadWithDepsTest
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
            String path = SchemaManagerLoadWithDepsTest.class.getResource( "" ).getPath();
            int targetPos = path.indexOf( "target" );
            workingDirectory = path.substring( 0, targetPos + 6 );
        }

        schemaRepository = new File( workingDirectory, "schema" );

        // Cleanup the target directory
        FileUtils.deleteDirectory( schemaRepository );

        SchemaLdifExtractor extractor = new DefaultSchemaLdifExtractor( new File( workingDirectory ) );
        extractor.extractOrCopy();
    }


    @AfterClass
    public static void cleanup() throws IOException
    {
        // Cleanup the target directory
        FileUtils.deleteDirectory( schemaRepository );
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

        Assert.assertTrue( schemaManager.getErrors().isEmpty() );
        Assert.assertEquals( 38, schemaManager.getAttributeTypeRegistry().size() );
        Assert.assertEquals( 35, schemaManager.getComparatorRegistry().size() );
        Assert.assertEquals( 35, schemaManager.getMatchingRuleRegistry().size() );
        Assert.assertEquals( 35, schemaManager.getNormalizerRegistry().size() );
        Assert.assertEquals( 9, schemaManager.getObjectClassRegistry().size() );
        Assert.assertEquals( 59, schemaManager.getSyntaxCheckerRegistry().size() );
        Assert.assertEquals( 59, schemaManager.getLdapSyntaxRegistry().size() );
        Assert.assertEquals( 141, schemaManager.getGlobalOidRegistry().size() );

        Assert.assertEquals( 1, schemaManager.getRegistries().getLoadedSchemas().size() );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "system" ) );
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

        Assert.assertTrue( schemaManager.getErrors().isEmpty() );
        Assert.assertEquals( 92, schemaManager.getAttributeTypeRegistry().size() );
        Assert.assertEquals( 35, schemaManager.getComparatorRegistry().size() );
        Assert.assertEquals( 35, schemaManager.getMatchingRuleRegistry().size() );
        Assert.assertEquals( 35, schemaManager.getNormalizerRegistry().size() );
        Assert.assertEquals( 36, schemaManager.getObjectClassRegistry().size() );
        Assert.assertEquals( 59, schemaManager.getSyntaxCheckerRegistry().size() );
        Assert.assertEquals( 59, schemaManager.getLdapSyntaxRegistry().size() );
        Assert.assertEquals( 222, schemaManager.getGlobalOidRegistry().size() );

        Assert.assertEquals( 2, schemaManager.getRegistries().getLoadedSchemas().size() );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "system" ) );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "core" ) );
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

        Assert.assertTrue( schemaManager.getErrors().isEmpty() );
        Assert.assertEquals( 145, schemaManager.getAttributeTypeRegistry().size() );
        Assert.assertEquals( 43, schemaManager.getComparatorRegistry().size() );
        Assert.assertEquals( 43, schemaManager.getMatchingRuleRegistry().size() );
        Assert.assertEquals( 43, schemaManager.getNormalizerRegistry().size() );
        Assert.assertEquals( 53, schemaManager.getObjectClassRegistry().size() );
        Assert.assertEquals( 62, schemaManager.getSyntaxCheckerRegistry().size() );
        Assert.assertEquals( 66, schemaManager.getLdapSyntaxRegistry().size() );
        Assert.assertEquals( 307, schemaManager.getGlobalOidRegistry().size() );

        Assert.assertEquals( 3, schemaManager.getRegistries().getLoadedSchemas().size() );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "system" ) );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "core" ) );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "apache" ) );
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

        Assert.assertTrue( schemaManager.getErrors().isEmpty() );
        Assert.assertEquals( 69, schemaManager.getAttributeTypeRegistry().size() );
        Assert.assertEquals( 40, schemaManager.getComparatorRegistry().size() );
        Assert.assertEquals( 40, schemaManager.getMatchingRuleRegistry().size() );
        Assert.assertEquals( 42, schemaManager.getNormalizerRegistry().size() );
        Assert.assertEquals( 22, schemaManager.getObjectClassRegistry().size() );
        Assert.assertEquals( 63, schemaManager.getSyntaxCheckerRegistry().size() );
        Assert.assertEquals( 64, schemaManager.getLdapSyntaxRegistry().size() );
        Assert.assertEquals( 195, schemaManager.getGlobalOidRegistry().size() );

        Assert.assertEquals( 2, schemaManager.getRegistries().getLoadedSchemas().size() );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "system" ) );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "apachemeta" ) );
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

        Assert.assertTrue( schemaManager.getErrors().isEmpty() );
        Assert.assertEquals( 99, schemaManager.getAttributeTypeRegistry().size() );
        Assert.assertEquals( 35, schemaManager.getComparatorRegistry().size() );
        Assert.assertEquals( 35, schemaManager.getMatchingRuleRegistry().size() );
        Assert.assertEquals( 35, schemaManager.getNormalizerRegistry().size() );
        Assert.assertEquals( 41, schemaManager.getObjectClassRegistry().size() );
        Assert.assertEquals( 59, schemaManager.getSyntaxCheckerRegistry().size() );
        Assert.assertEquals( 59, schemaManager.getLdapSyntaxRegistry().size() );
        Assert.assertEquals( 234, schemaManager.getGlobalOidRegistry().size() );

        Assert.assertEquals( 3, schemaManager.getRegistries().getLoadedSchemas().size() );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "system" ) );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "core" ) );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "Java" ) );
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

        Assert.assertTrue( schemaManager.getErrors().isEmpty() );
        Assert.assertEquals( 176, schemaManager.getAttributeTypeRegistry().size() );
        Assert.assertEquals( 48, schemaManager.getComparatorRegistry().size() );
        Assert.assertEquals( 48, schemaManager.getMatchingRuleRegistry().size() );
        Assert.assertEquals( 50, schemaManager.getNormalizerRegistry().size() );
        Assert.assertEquals( 66, schemaManager.getObjectClassRegistry().size() );
        Assert.assertEquals( 66, schemaManager.getSyntaxCheckerRegistry().size() );
        Assert.assertEquals( 71, schemaManager.getLdapSyntaxRegistry().size() );
        Assert.assertEquals( 361, schemaManager.getGlobalOidRegistry().size() );

        Assert.assertEquals( 5, schemaManager.getRegistries().getLoadedSchemas().size() );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "system" ) );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "core" ) );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "apache" ) );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "apacheMeta" ) );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "other" ) );
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

        Assert.assertTrue( schemaManager.getErrors().isEmpty() );
        Assert.assertEquals( 133, schemaManager.getAttributeTypeRegistry().size() );
        Assert.assertEquals( 35, schemaManager.getComparatorRegistry().size() );
        Assert.assertEquals( 35, schemaManager.getMatchingRuleRegistry().size() );
        Assert.assertEquals( 35, schemaManager.getNormalizerRegistry().size() );
        Assert.assertEquals( 49, schemaManager.getObjectClassRegistry().size() );
        Assert.assertEquals( 59, schemaManager.getSyntaxCheckerRegistry().size() );
        Assert.assertEquals( 59, schemaManager.getLdapSyntaxRegistry().size() );
        Assert.assertEquals( 276, schemaManager.getGlobalOidRegistry().size() );

        Assert.assertEquals( 3, schemaManager.getRegistries().getLoadedSchemas().size() );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "system" ) );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "core" ) );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "cosine" ) );
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

        Assert.assertTrue( schemaManager.getErrors().isEmpty() );
        Assert.assertEquals( 142, schemaManager.getAttributeTypeRegistry().size() );
        Assert.assertEquals( 35, schemaManager.getComparatorRegistry().size() );
        Assert.assertEquals( 35, schemaManager.getMatchingRuleRegistry().size() );
        Assert.assertEquals( 35, schemaManager.getNormalizerRegistry().size() );
        Assert.assertEquals( 50, schemaManager.getObjectClassRegistry().size() );
        Assert.assertEquals( 59, schemaManager.getSyntaxCheckerRegistry().size() );
        Assert.assertEquals( 59, schemaManager.getLdapSyntaxRegistry().size() );
        Assert.assertEquals( 286, schemaManager.getGlobalOidRegistry().size() );

        Assert.assertEquals( 4, schemaManager.getRegistries().getLoadedSchemas().size() );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "system" ) );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "core" ) );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "cosine" ) );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "InetOrgPerson" ) );
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

        Assert.assertTrue( schemaManager.getErrors().isEmpty() );
        Assert.assertEquals( 105, schemaManager.getAttributeTypeRegistry().size() );
        Assert.assertEquals( 35, schemaManager.getComparatorRegistry().size() );
        Assert.assertEquals( 35, schemaManager.getMatchingRuleRegistry().size() );
        Assert.assertEquals( 35, schemaManager.getNormalizerRegistry().size() );
        Assert.assertEquals( 36, schemaManager.getObjectClassRegistry().size() );
        Assert.assertEquals( 59, schemaManager.getSyntaxCheckerRegistry().size() );
        Assert.assertEquals( 59, schemaManager.getLdapSyntaxRegistry().size() );
        Assert.assertEquals( 235, schemaManager.getGlobalOidRegistry().size() );

        Assert.assertEquals( 3, schemaManager.getRegistries().getLoadedSchemas().size() );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "system" ) );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "core" ) );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "collective" ) );
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

        Assert.assertTrue( schemaManager.getErrors().isEmpty() );
        Assert.assertEquals( 107, schemaManager.getAttributeTypeRegistry().size() );
        Assert.assertEquals( 35, schemaManager.getComparatorRegistry().size() );
        Assert.assertEquals( 35, schemaManager.getMatchingRuleRegistry().size() );
        Assert.assertEquals( 35, schemaManager.getNormalizerRegistry().size() );
        Assert.assertEquals( 39, schemaManager.getObjectClassRegistry().size() );
        Assert.assertEquals( 59, schemaManager.getSyntaxCheckerRegistry().size() );
        Assert.assertEquals( 59, schemaManager.getLdapSyntaxRegistry().size() );
        Assert.assertEquals( 240, schemaManager.getGlobalOidRegistry().size() );

        Assert.assertEquals( 3, schemaManager.getRegistries().getLoadedSchemas().size() );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "system" ) );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "core" ) );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "Krb5Kdc" ) );
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

        Assert.assertTrue( schemaManager.getErrors().isEmpty() );
        Assert.assertEquals( 0, schemaManager.getAttributeTypeRegistry().size() );
        Assert.assertEquals( 0, schemaManager.getComparatorRegistry().size() );
        Assert.assertEquals( 0, schemaManager.getMatchingRuleRegistry().size() );
        Assert.assertEquals( 0, schemaManager.getNormalizerRegistry().size() );
        Assert.assertEquals( 0, schemaManager.getObjectClassRegistry().size() );
        Assert.assertEquals( 0, schemaManager.getSyntaxCheckerRegistry().size() );
        Assert.assertEquals( 0, schemaManager.getLdapSyntaxRegistry().size() );
        Assert.assertEquals( 0, schemaManager.getGlobalOidRegistry().size() );

        Assert.assertEquals( 0, schemaManager.getRegistries().getLoadedSchemas().size() );
        Assert.assertNull( schemaManager.getRegistries().getLoadedSchema( "system" ) );
        Assert.assertNull( schemaManager.getRegistries().getLoadedSchema( "core" ) );
        Assert.assertNull( schemaManager.getRegistries().getLoadedSchema( "cosine" ) );
    }


    /**
     * Test loading a wrong schema
     */
    @Test
    public void testLoadWrongSchema() throws Exception
    {
        LdifSchemaLoader loader = new LdifSchemaLoader( schemaRepository );
        SchemaManager schemaManager = new DefaultSchemaManager( loader );

        try
        {
            schemaManager.loadWithDeps( "bad" );
            Assert.fail();
        }
        catch ( LdapOperationNotSupportedException lonse )
        {
            // expected
        }

        Assert.assertTrue( schemaManager.getErrors().isEmpty() );
        Assert.assertEquals( 0, schemaManager.getAttributeTypeRegistry().size() );
        Assert.assertEquals( 0, schemaManager.getComparatorRegistry().size() );
        Assert.assertEquals( 0, schemaManager.getMatchingRuleRegistry().size() );
        Assert.assertEquals( 0, schemaManager.getNormalizerRegistry().size() );
        Assert.assertEquals( 0, schemaManager.getObjectClassRegistry().size() );
        Assert.assertEquals( 0, schemaManager.getSyntaxCheckerRegistry().size() );
        Assert.assertEquals( 0, schemaManager.getLdapSyntaxRegistry().size() );
        Assert.assertEquals( 0, schemaManager.getGlobalOidRegistry().size() );

        Assert.assertEquals( 0, schemaManager.getRegistries().getLoadedSchemas().size() );
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

        Assert.assertTrue( schemaManager.getErrors().isEmpty() );
        Assert.assertEquals( 142, schemaManager.getAttributeTypeRegistry().size() );
        Assert.assertEquals( 35, schemaManager.getComparatorRegistry().size() );
        Assert.assertEquals( 35, schemaManager.getMatchingRuleRegistry().size() );
        Assert.assertEquals( 35, schemaManager.getNormalizerRegistry().size() );
        Assert.assertEquals( 50, schemaManager.getObjectClassRegistry().size() );
        Assert.assertEquals( 59, schemaManager.getSyntaxCheckerRegistry().size() );
        Assert.assertEquals( 59, schemaManager.getLdapSyntaxRegistry().size() );
        Assert.assertEquals( 286, schemaManager.getGlobalOidRegistry().size() );

        Assert.assertEquals( 4, schemaManager.getRegistries().getLoadedSchemas().size() );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "system" ) );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "core" ) );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "cosine" ) );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "InetOrgPerson" ) );
    }


    /**
     * test loading the "InetOrgPerson", "core" and a bad schema
     */
    @Test
    public void testLoadCoreInetOrgPersonAndBad() throws Exception
    {
        LdifSchemaLoader loader = new LdifSchemaLoader( schemaRepository );
        SchemaManager schemaManager = new DefaultSchemaManager( loader );

        try
        {
            schemaManager.loadWithDeps( "core", "bad", "InetOrgPerson" );
            Assert.fail();
        }
        catch ( LdapOperationNotSupportedException lonse )
        {
            // expected
        }

        // No SchemaObject should be loaded as we had an error
        Assert.assertTrue( schemaManager.getErrors().isEmpty() );
        Assert.assertEquals( 0, schemaManager.getAttributeTypeRegistry().size() );
        Assert.assertEquals( 0, schemaManager.getComparatorRegistry().size() );
        Assert.assertEquals( 0, schemaManager.getMatchingRuleRegistry().size() );
        Assert.assertEquals( 0, schemaManager.getNormalizerRegistry().size() );
        Assert.assertEquals( 0, schemaManager.getObjectClassRegistry().size() );
        Assert.assertEquals( 0, schemaManager.getSyntaxCheckerRegistry().size() );
        Assert.assertEquals( 0, schemaManager.getLdapSyntaxRegistry().size() );
        Assert.assertEquals( 0, schemaManager.getGlobalOidRegistry().size() );

        Assert.assertEquals( 0, schemaManager.getRegistries().getLoadedSchemas().size() );
        Assert.assertNull( schemaManager.getRegistries().getLoadedSchema( "system" ) );
        Assert.assertNull( schemaManager.getRegistries().getLoadedSchema( "core" ) );
        Assert.assertNull( schemaManager.getRegistries().getLoadedSchema( "cosine" ) );
        Assert.assertNull( schemaManager.getRegistries().getLoadedSchema( "InetOrgPerson" ) );
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

        Assert.assertTrue( schemaManager.getErrors().isEmpty() );
        Assert.assertEquals( 142, schemaManager.getAttributeTypeRegistry().size() );
        Assert.assertEquals( 35, schemaManager.getComparatorRegistry().size() );
        Assert.assertEquals( 35, schemaManager.getMatchingRuleRegistry().size() );
        Assert.assertEquals( 35, schemaManager.getNormalizerRegistry().size() );
        Assert.assertEquals( 50, schemaManager.getObjectClassRegistry().size() );
        Assert.assertEquals( 59, schemaManager.getSyntaxCheckerRegistry().size() );
        Assert.assertEquals( 59, schemaManager.getLdapSyntaxRegistry().size() );
        Assert.assertEquals( 286, schemaManager.getGlobalOidRegistry().size() );

        Assert.assertEquals( 4, schemaManager.getRegistries().getLoadedSchemas().size() );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "system" ) );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "core" ) );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "cosine" ) );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "InetOrgPerson" ) );
    }


    /**
     * test loading the "InetOrgPerson", "core" and a disabled schema
     */
    @Test
    public void testLoadWithDepsCoreInetOrgPersonAndNis() throws Exception
    {
        LdifSchemaLoader loader = new LdifSchemaLoader( schemaRepository );
        SchemaManager schemaManager = new DefaultSchemaManager( loader );

        Schema system = loader.getSchema( "system" );
        Schema core = loader.getSchema( "core" );
        Schema empty = new DefaultSchema( "empty" );
        Schema cosine = loader.getSchema( "cosine" );
        Schema inetOrgPerson = loader.getSchema( "InetOrgPerson" );

        Assert.assertTrue( schemaManager.load( system, core, empty, cosine, inetOrgPerson ) );

        Assert.assertTrue( schemaManager.getErrors().isEmpty() );
        Assert.assertEquals( 142, schemaManager.getAttributeTypeRegistry().size() );
        Assert.assertEquals( 35, schemaManager.getComparatorRegistry().size() );
        Assert.assertEquals( 35, schemaManager.getMatchingRuleRegistry().size() );
        Assert.assertEquals( 35, schemaManager.getNormalizerRegistry().size() );
        Assert.assertEquals( 50, schemaManager.getObjectClassRegistry().size() );
        Assert.assertEquals( 59, schemaManager.getSyntaxCheckerRegistry().size() );
        Assert.assertEquals( 59, schemaManager.getLdapSyntaxRegistry().size() );
        Assert.assertEquals( 286, schemaManager.getGlobalOidRegistry().size() );

        Assert.assertEquals( 5, schemaManager.getRegistries().getLoadedSchemas().size() );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "system" ) );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "core" ) );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "cosine" ) );
        Assert.assertNotNull( schemaManager.getRegistries().getLoadedSchema( "InetOrgPerson" ) );
    }
}

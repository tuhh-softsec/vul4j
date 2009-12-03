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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.directory.server.schema.loader.ldif.LdifSchemaLoaderTest;
import org.apache.directory.shared.ldap.exception.LdapSchemaViolationException;
import org.apache.directory.shared.ldap.schema.AttributeType;
import org.apache.directory.shared.ldap.schema.SchemaManager;
import org.apache.directory.shared.ldap.schema.UsageEnum;
import org.apache.directory.shared.ldap.schema.ldif.extractor.SchemaLdifExtractor;
import org.apache.directory.shared.schema.DefaultSchemaManager;
import org.apache.directory.shared.schema.loader.ldif.JarLdifSchemaLoader;
import org.apache.directory.shared.schema.loader.ldif.LdifSchemaLoader;
import org.apache.directory.shared.schema.loader.ldif.SchemaEntityFactory;
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

    // A LDIF loader
    private static LdifSchemaLoader ldifLoader;

    // A SchemaObject factory
    private static SchemaEntityFactory factory;


    @BeforeClass
    public static void setup() throws Exception
    {
        workingDirectory = System.getProperty( "workingDirectory" );

        if ( workingDirectory == null )
        {
            String path = LdifSchemaLoaderTest.class.getResource( "" ).getPath();
            int targetPos = path.indexOf( "target" );
            workingDirectory = path.substring( 0, targetPos + 6 );
        }

        // Cleanup the target directory
        FileUtils.deleteDirectory( new File( workingDirectory + "/schema" ) );

        SchemaLdifExtractor extractor = new SchemaLdifExtractor( new File( workingDirectory ) );
        extractor.extractOrCopy();

        ldifLoader = new LdifSchemaLoader( new File( workingDirectory, "schema" ) );
        factory = new SchemaEntityFactory();
    }


    @AfterClass
    public static void cleanup() throws IOException
    {
        // Cleanup the target directory
        FileUtils.deleteDirectory( new File( workingDirectory + "/schema" ) );
    }


    private SchemaManager loadSystem() throws Exception
    {
        JarLdifSchemaLoader loader = new JarLdifSchemaLoader();
        SchemaManager schemaManager = new DefaultSchemaManager( loader );

        String schemaName = "system";

        schemaManager.loadWithDeps( schemaName );

        return schemaManager;
    }


    //-------------------------------------------------------------------------
    // AttributeType addition tests
    //-------------------------------------------------------------------------
    /**
     * Try to inject an AttributeType without any superior nor Syntax : it's invalid
     */
    @Test
    public void testAddAttributeTypeNoSyntaxNoSuperior() throws Exception
    {
        SchemaManager schemaManager = loadSystem();

        AttributeType attributeType = new AttributeType( "1.1.0" );
        attributeType.setEqualityOid( "2.5.13.1" );
        attributeType.setOrderingOid( null );
        attributeType.setSubstringOid( null );
        attributeType.setSuperior( ( String ) null );

        // It should fail
        assertFalse( schemaManager.add( attributeType ) );

        List<Throwable> errors = schemaManager.getErrors();
        assertEquals( 1, errors.size() );
        Throwable error = errors.get( 0 );

        assertTrue( error instanceof LdapSchemaViolationException );
    }


    /**
     * Try to inject an AttributeType which is Collective, and userApplication AT
     */
    @Test
    public void testAddAttributeTypeCollectiveUser() throws Exception
    {
        SchemaManager schemaManager = loadSystem();

        AttributeType attributeType = new AttributeType( "1.1.0" );
        attributeType.setEqualityOid( "2.5.13.1" );
        attributeType.setOrderingOid( null );
        attributeType.setSubstringOid( null );
        attributeType.setSyntaxOid( "1.3.6.1.4.1.1466.115.121.1.26" );
        attributeType.setUsage( UsageEnum.USER_APPLICATIONS );
        attributeType.setCollective( true );

        // It should not fail
        schemaManager.add( attributeType );
    }


    /**
     * Try to inject an AttributeType which is Collective, but an operational AT
     */
    @Test
    public void testAddAttributeTypeCollectiveOperational() throws Exception
    {
        SchemaManager schemaManager = loadSystem();

        AttributeType attributeType = new AttributeType( "1.1.0" );
        attributeType.setEqualityOid( "2.5.13.1" );
        attributeType.setOrderingOid( null );
        attributeType.setSubstringOid( null );
        attributeType.setSyntaxOid( "1.3.6.1.4.1.1466.115.121.1.26" );
        attributeType.setUsage( UsageEnum.DIRECTORY_OPERATION );
        attributeType.setCollective( true );

        // It should fail
        // It should fail
        assertFalse( schemaManager.add( attributeType ) );

        List<Throwable> errors = schemaManager.getErrors();
        assertEquals( 1, errors.size() );
        Throwable error = errors.get( 0 );

        assertTrue( error instanceof LdapSchemaViolationException );

    }
}

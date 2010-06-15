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
package org.apache.directory.shared.ldap.schema.loader.ldif;


import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.ldap.schema.AttributeType;
import org.apache.directory.shared.ldap.schema.MatchingRule;
import org.apache.directory.shared.ldap.schema.SchemaManager;
import org.apache.directory.shared.ldap.schema.comparators.GeneralizedTimeComparator;
import org.apache.directory.shared.ldap.schema.comparators.IntegerComparator;
import org.apache.directory.shared.ldap.schema.comparators.NumericStringComparator;
import org.apache.directory.shared.ldap.schema.comparators.TelephoneNumberComparator;
import org.apache.directory.shared.ldap.schema.ldif.extractor.SchemaLdifExtractor;
import org.apache.directory.shared.ldap.schema.ldif.extractor.impl.DefaultSchemaLdifExtractor;
import org.apache.directory.shared.ldap.schema.manager.impl.DefaultSchemaManager;
import org.apache.directory.shared.ldap.schema.normalizers.GeneralizedTimeNormalizer;
import org.apache.directory.shared.ldap.schema.normalizers.NumericNormalizer;
import org.apache.directory.shared.ldap.schema.normalizers.TelephoneNumberNormalizer;
import org.apache.directory.shared.ldap.schema.registries.Schema;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests that matching rules of the schema use correct normalizers and comparators.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
public class MatchingRuleTest
{
    // A directory in which the ldif files will be stored
    private static String workingDirectory;

    // The schema repository
    private static File schemaRepository;

    // The schema manager
    private static SchemaManager schemaManager;


    @BeforeClass
    public static void setup() throws Exception
    {
        workingDirectory = System.getProperty( "workingDirectory" );

        if ( workingDirectory == null )
        {
            String path = MatchingRuleTest.class.getResource( "" ).getPath();
            int targetPos = path.indexOf( "target" );
            workingDirectory = path.substring( 0, targetPos + 6 );
        }

        schemaRepository = new File( workingDirectory, "schema" );

        // Cleanup the target directory
        FileUtils.deleteDirectory( schemaRepository );

        SchemaLdifExtractor extractor = new DefaultSchemaLdifExtractor( new File( workingDirectory ) );
        extractor.extractOrCopy();

        LdifSchemaLoader loader = new LdifSchemaLoader( schemaRepository );
        schemaManager = new DefaultSchemaManager( loader );
        for ( Schema schema : loader.getAllSchemas() )
        {
            schema.enable();
        }
        schemaManager.loadAllEnabled();
    }


    @AfterClass
    public static void cleanup() throws IOException
    {
        // Cleanup the target directory
        FileUtils.deleteDirectory( schemaRepository );
    }


    @Test
    public void testTelephoneNumberMatch() throws Exception
    {
        // matching rule: telephoneNumberMatch
        MatchingRule mr1 = schemaManager.lookupMatchingRuleRegistry( "telephoneNumberMatch" );
        assertEquals( TelephoneNumberNormalizer.class.getName(), mr1.getNormalizer().getClass().getName() );
        assertEquals( "+1234567890", mr1.getNormalizer().normalize( " +1 234-567 890 " ) );
        assertEquals( TelephoneNumberComparator.class.getName(), mr1.getLdapComparator().getClass().getName() );
        assertEquals( 0, mr1.getLdapComparator().compare( " +1 234-567 890 ", "+1234567890" ) );

        // matching rule: telephoneNumberSubstringsMatch
        MatchingRule mr2 = schemaManager.lookupMatchingRuleRegistry( "telephoneNumberSubstringsMatch" );
        assertEquals( TelephoneNumberNormalizer.class.getName(), mr2.getNormalizer().getClass().getName() );
        assertEquals( "+1234567890", mr2.getNormalizer().normalize( " +1 234-567 890 " ) );
        assertEquals( TelephoneNumberComparator.class.getName(), mr2.getLdapComparator().getClass().getName() );
        assertEquals( 0, mr2.getLdapComparator().compare( " +1 234-567 890 ", "+1234567890" ) );

        // test a real attribute: telephoneNumber
        AttributeType at = schemaManager.lookupAttributeTypeRegistry( "telephoneNumber" );
        assertNotNull( at.getEquality() );
        assertEquals( TelephoneNumberNormalizer.class.getName(), at.getEquality().getNormalizer().getClass().getName() );
        assertEquals( "+1234567890", at.getEquality().getNormalizer().normalize( " +1 234-567 890 " ) );
        assertEquals( TelephoneNumberComparator.class.getName(), at.getEquality().getLdapComparator().getClass()
            .getName() );
        assertEquals( 0, at.getEquality().getLdapComparator().compare( " +1 234-567 890 ", "+1234567890" ) );
        assertNotNull( at.getSubstring() );
        assertEquals( TelephoneNumberNormalizer.class.getName(), at.getEquality().getNormalizer().getClass().getName() );
        assertEquals( "+1234567890", at.getSubstring().getNormalizer().normalize( " +1 234-567 890 " ) );
        assertNull( at.getOrdering() );
    }


    @Test
    public void testIntegerMatch() throws Exception
    {
        MatchingRule mr1 = schemaManager.lookupMatchingRuleRegistry( "integerMatch" );
        assertEquals( NumericNormalizer.class.getName(), mr1.getNormalizer().getClass().getName() );
        assertEquals( "1234567890", mr1.getNormalizer().normalize( " 1 234 567 890 " ) );
        //assertEquals( IntegerComparator.class.getName(), mr1.getLdapComparator().getClass().getName() );
        //assertEquals( 0, mr1.getLdapComparator().compare( " 1 234 567 890 ", "1234567890" ) );

        MatchingRule mr2 = schemaManager.lookupMatchingRuleRegistry( "integerOrderingMatch" );
        assertEquals( NumericNormalizer.class.getName(), mr2.getNormalizer().getClass().getName() );
        assertEquals( "1234567890", mr2.getNormalizer().normalize( " 1 234 567 890 " ) );
        assertEquals( IntegerComparator.class.getName(), mr2.getLdapComparator().getClass().getName() );
        assertEquals( 0, mr2.getLdapComparator().compare( 1234567890L, 1234567890L ) );
        assertTrue( mr2.getLdapComparator().compare( 123L, 234L ) < 0 );
        assertTrue( mr2.getLdapComparator().compare( 1234L, 234L ) > 0 );

        // test a real attribute type: uidNumber
        AttributeType at = schemaManager.lookupAttributeTypeRegistry( "uidNumber" );
        assertNotNull( at.getEquality() );
        assertEquals( NumericNormalizer.class.getName(), at.getEquality().getNormalizer().getClass().getName() );
        assertEquals( "123", at.getEquality().getNormalizer().normalize( " 1 2 3 " ) );
        //assertEquals( 0, at.getEquality().getLdapComparator().compare( " 1 2 3 ", "123" ) );
        assertNull( at.getSubstring() );
        assertNull( at.getOrdering() );
    }


    @Test
    public void testNumericStringMatch() throws Exception
    {
        MatchingRule mr1 = schemaManager.lookupMatchingRuleRegistry( "numericStringMatch" );
        assertEquals( NumericNormalizer.class.getName(), mr1.getNormalizer().getClass().getName() );
        assertEquals( "1234567890", mr1.getNormalizer().normalize( " 1 234 567 890 " ) );
        assertEquals( NumericStringComparator.class.getName(), mr1.getLdapComparator().getClass().getName() );
        assertEquals( 0, mr1.getLdapComparator().compare( " 1 234 567 890 ", "1234567890" ) );

        MatchingRule mr2 = schemaManager.lookupMatchingRuleRegistry( "numericStringSubstringsMatch" );
        assertEquals( NumericNormalizer.class.getName(), mr2.getNormalizer().getClass().getName() );
        assertEquals( "1234567890", mr2.getNormalizer().normalize( " 1 234 567 890 " ) );
        assertEquals( NumericStringComparator.class.getName(), mr2.getLdapComparator().getClass().getName() );
        assertEquals( 0, mr2.getLdapComparator().compare( " 1 234 567 890 ", "1234567890" ) );

        MatchingRule mr3 = schemaManager.lookupMatchingRuleRegistry( "numericStringOrderingMatch" );
        assertEquals( NumericNormalizer.class.getName(), mr3.getNormalizer().getClass().getName() );
        assertEquals( "1234567890", mr3.getNormalizer().normalize( " 1 234 567 890 " ) );
        assertEquals( NumericStringComparator.class.getName(), mr3.getLdapComparator().getClass().getName() );
        assertEquals( 0, mr3.getLdapComparator().compare( " 1 234 567 890 ", "1234567890" ) );
        assertTrue( mr3.getLdapComparator().compare( " 1 2 3  ", " 2 3 4" ) < 0 );
        assertTrue( mr3.getLdapComparator().compare( " 1 2 3 4 ", " 2 3 4" ) < 0 );
    }


    @Test
    public void testGeneralizedTimeStringMatch() throws Exception
    {
        MatchingRule mr1 = schemaManager.lookupMatchingRuleRegistry( "generalizedTimeMatch" );
        assertEquals( GeneralizedTimeNormalizer.class.getName(), mr1.getNormalizer().getClass().getName() );
        assertEquals( "20100314150000.000Z", mr1.getNormalizer().normalize( "2010031415Z" ) );
        assertEquals( "20100314133102.003Z", mr1.getNormalizer().normalize( "20100314150102.003+0130" ) );
        assertEquals( GeneralizedTimeComparator.class.getName(), mr1.getLdapComparator().getClass().getName() );
        assertEquals( 0, mr1.getLdapComparator().compare( "2010031415Z", "20100314150000.000+0000" ) );

        MatchingRule mr2 = schemaManager.lookupMatchingRuleRegistry( "generalizedTimeOrderingMatch" );
        assertEquals( GeneralizedTimeNormalizer.class.getName(), mr2.getNormalizer().getClass().getName() );
        assertEquals( "20100314150000.000Z", mr2.getNormalizer().normalize( "2010031415Z" ) );
        assertEquals( "20100314133102.003Z", mr2.getNormalizer().normalize( "20100314150102.003+0130" ) );
        assertEquals( GeneralizedTimeComparator.class.getName(), mr2.getLdapComparator().getClass().getName() );
        assertEquals( 0, mr2.getLdapComparator().compare( "2010031415Z", "20100314150000.000+0000" ) );
        assertTrue( mr2.getLdapComparator().compare( "2010031415Z", "2010031414Z" ) > 0 );
        assertTrue( mr2.getLdapComparator().compare( "2010031415Z", "2010031416Z" ) < 0 );
    }

}

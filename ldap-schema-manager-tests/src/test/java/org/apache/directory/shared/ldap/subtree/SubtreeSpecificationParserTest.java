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

package org.apache.directory.shared.ldap.subtree;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.directory.junit.tools.Concurrent;
import org.apache.directory.junit.tools.ConcurrentJunitRunner;
import org.apache.directory.shared.ldap.name.DN;
import org.apache.directory.shared.ldap.schema.ObjectClass;
import org.apache.directory.shared.ldap.schema.SchemaManager;
import org.apache.directory.shared.ldap.schema.loader.ldif.JarLdifSchemaLoader;
import org.apache.directory.shared.ldap.schema.manager.impl.DefaultSchemaManager;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit tests class for Subtree Specification parser (wrapper).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(ConcurrentJunitRunner.class)
@Concurrent()
public class SubtreeSpecificationParserTest
{
    /** the ACIItem checker wrapper */
    private static SubtreeSpecificationChecker checker;

    /** the ss parser wrapper */
    private static SubtreeSpecificationParser parser;

    /** A valid empty specification with single white space between brackets */
    private static final String EMPTY_SPEC = "{ }";

    /** A valid specification only with base set */
    private static final String SPEC_WITH_BASE = "{ base \"ou=system\" }";

    /** An invalid specification with missing white space and base set */
    private static final String INVALID_SPEC_WITH_BASE_AND_MISSING_WS = "{ base\"ou=system\"}";

    /** A valid specification with some specific exclusions set */
    private static final String SPEC_WITH_SPECIFICEXCLUSIONS = "{ specificExclusions { chopAfter:\"ef=gh\", chopBefore:\"ab=cd\" } }";

    /** A valid specification with empty specific exclusions set */
    private static final String SPEC_WITH_EMPTY_SPECIFICEXCLUSIONS = "{ specificExclusions { } }";

    /** A valid specification with minimum and maximum set */
    private static final String SPEC_WITH_MINIMUM_AND_MAXIMUM = "{ minimum 1, maximum 2 }";

    /** A valid specification with base and minimum and maximum set */
    private static final String SPEC_WITH_BASE_AND_MINIMUM_AND_MAXIMUM = "{ base \"ou=ORGANIZATION UNIT\", minimum  1, maximum   2 }";

    /**
     * A valid specification with base and specific exclusions and minimum and
     * maximum set
     */
    private static final String SPEC_WITH_BASE_AND_SPECIFICEXCLUSIONS_AND_MINIMUM_AND_MAXIMUM = "{ base \"ou=people\", specificExclusions { chopBefore:\"x=y\""
        + ", chopAfter:\"k=l\", chopBefore:\"y=z\", chopAfter:\"l=m\" }, minimum   7, maximum 77 }";

    /** A valid specification with refinement set */
    private static final String SPEC_WITH_REFINEMENT = "{ base \"ou=system\", specificationFilter and:{ and:{ item:2.5.6.0"
        + ", or:{ item:2.5.6.1, item:person } }, not: item:2.5.6.2 } }";

    /** A valid specification with base and an empty refinement set */
    private static final String SPEC_WITH_BASE_AND_EMPTY_REFINEMENT = "{ base \"ou=system\", specificationFilter and:{ } }";

    /** A valid specification with ALL IN ONE */
    private static final String SPEC_WITH_ALL_IN_ONE = "{ base    \"ou=departments\""
        + ", specificExclusions { chopBefore:\"x=y\", chopAfter:\"k=l\", chopBefore:\"y=z\", chopAfter:\"l=m\" }"
        + ", minimum 7, maximum   77"
        + ", specificationFilter     and:{ and:{ item:2.5.6.0, or:{ item:2.5.6.1, item:2.5.6.2 } }, not: item:2.5.6.3 } }";

    /** An valid specification with unordinary component order */
    private static final String SPEC_ORDER_OF_COMPONENTS_DOES_NOT_MATTER = "{ base \"ou=system\", minimum 3, specificExclusions { chopBefore:\"x=y\" } }";

    /** An invalid specification with completely unrelated content */
    private static final String INVALID_SILLY_THING = "How much wood would a wood chuck chuck if a wood chuck would chuck wood?";

    /** holds multithreaded success value */
    boolean isSuccessMultithreaded = true;

    /** The schema manager */
    private static SchemaManager schemaManager;

    /** Some global OC */
    private static ObjectClass TOP_OC;      // 2.5.6.0
    private static ObjectClass ALIAS_OC;    // 2.5.6.1
    private static ObjectClass COUNTRY_OC;  // 2.5.6.2
    private static ObjectClass LOCALITY_OC; // 2.5.6.3
    private static ObjectClass PERSON_OC;   // 2.5.6.6

    /**
     * Initialization
     */
    @BeforeClass
    public static void init() throws Exception
    {
        JarLdifSchemaLoader loader = new JarLdifSchemaLoader();
        schemaManager = new DefaultSchemaManager( loader );

        schemaManager.loadAllEnabled();

        checker = new SubtreeSpecificationChecker( schemaManager );
        parser = new SubtreeSpecificationParser( schemaManager );

        TOP_OC = schemaManager.lookupObjectClassRegistry( "top" );
        ALIAS_OC = schemaManager.lookupObjectClassRegistry( "alias" );
        COUNTRY_OC = schemaManager.lookupObjectClassRegistry( "country" );
        LOCALITY_OC = schemaManager.lookupObjectClassRegistry( "locality" );
        PERSON_OC = schemaManager.lookupObjectClassRegistry( "person" );
    }


    /**
     * Tests the parser with a valid empty specification.
     */
    @Test
    public void testEmptySpec() throws Exception
    {
        SubtreeSpecification ss = parser.parse( EMPTY_SPEC );
        assertNotNull( ss );

        // try a second time
        ss = parser.parse( EMPTY_SPEC );
        assertNotNull( ss );

        // try a third time
        ss = parser.parse( EMPTY_SPEC );
        assertNotNull( ss );
    }


    /**
     * Tests the parser with a valid specification with base set.
     */
    @Test
    public void testSpecWithBase() throws Exception
    {
        SubtreeSpecification ss = parser.parse( SPEC_WITH_BASE );
        assertNotNull( ss );

        assertEquals( "ou=system", ss.getBase().toString() );
    }


    /**
     * Tests the parser with an invalid specification with missing white spaces
     * and base set.
     */
    @Test
    public void testInvalidSpecWithBaseAndMissingWS() throws Exception
    {
        try
        {
            parser.parse( INVALID_SPEC_WITH_BASE_AND_MISSING_WS );
            fail( "testInvalidSpecWithBaseAndMissingWS() should never come here..." );
        }
        catch ( ParseException e )
        {
            assertNotNull( e );
        }
    }


    /**
     * Tests the parser with a valid specification with some specific exclusions
     * set.
     */
    @Test
    public void testSpecWithSpecificExclusions() throws Exception
    {
        SubtreeSpecification ss = parser.parse( SPEC_WITH_SPECIFICEXCLUSIONS );
        assertFalse( ss.getChopBeforeExclusions().isEmpty() );
        assertFalse( ss.getChopAfterExclusions().isEmpty() );
        assertTrue( ss.getChopBeforeExclusions().contains( new DN( "ab=cd" ) ) );
        assertTrue( ss.getChopAfterExclusions().contains( new DN( "ef=gh" ) ) );

        // try a second time
        ss = parser.parse( SPEC_WITH_SPECIFICEXCLUSIONS );
        assertFalse( ss.getChopBeforeExclusions().isEmpty() );
        assertFalse( ss.getChopAfterExclusions().isEmpty() );
        assertTrue( ss.getChopBeforeExclusions().contains( new DN( "ab=cd" ) ) );
        assertTrue( ss.getChopAfterExclusions().contains( new DN( "ef=gh" ) ) );

        // try a third time
        ss = parser.parse( SPEC_WITH_SPECIFICEXCLUSIONS );
        assertFalse( ss.getChopBeforeExclusions().isEmpty() );
        assertFalse( ss.getChopAfterExclusions().isEmpty() );
        assertTrue( ss.getChopBeforeExclusions().contains( new DN( "ab=cd" ) ) );
        assertTrue( ss.getChopAfterExclusions().contains( new DN( "ef=gh" ) ) );
    }


    /**
     * Tests the parser with a valid specification with an empty specific
     * exclusions set.
     */
    @Test
    public void testSpecWithEmptySpecificExclusions() throws Exception
    {
        SubtreeSpecification ss = parser.parse( SPEC_WITH_EMPTY_SPECIFICEXCLUSIONS );
        assertNotNull( ss );

        assertTrue( ss.getChopBeforeExclusions().isEmpty() );
    }


    /**
     * Tests the parser with a valid specification with minimum and maximum set.
     */
    @Test
    public void testSpecWithMinimumAndMaximum() throws Exception
    {
        SubtreeSpecification ss = parser.parse( SPEC_WITH_MINIMUM_AND_MAXIMUM );
        assertEquals( 1, ss.getMinBaseDistance() );
        assertEquals( 2, ss.getMaxBaseDistance() );

        // try a second time
        ss = parser.parse( SPEC_WITH_MINIMUM_AND_MAXIMUM );
        assertEquals( 1, ss.getMinBaseDistance() );
        assertEquals( 2, ss.getMaxBaseDistance() );

        // try a third time
        ss = parser.parse( SPEC_WITH_MINIMUM_AND_MAXIMUM );
        assertEquals( 1, ss.getMinBaseDistance() );
        assertEquals( 2, ss.getMaxBaseDistance() );
    }


    /**
     * Tests the parser with a valid specification with base and minimum and
     * maximum set.
     */
    @Test
    public void testWithBaseAndMinimumAndMaximum() throws Exception
    {
        SubtreeSpecification ss = parser.parse( SPEC_WITH_BASE_AND_MINIMUM_AND_MAXIMUM );

        assertEquals( new DN( "ou=ORGANIZATION UNIT" ).getName(), ss.getBase().getName() );
        assertEquals( 1, ss.getMinBaseDistance() );
        assertEquals( 2, ss.getMaxBaseDistance() );
    }


    /**
     * Tests the parser with a valid specification with base and specific
     * exclusions and minimum and maximum set.
     */
    @Test
    public void testSpecWithBaseAndSpecificExclusionsAndMinimumAndMaximum() throws Exception
    {
        SubtreeSpecification ss = parser.parse( SPEC_WITH_BASE_AND_SPECIFICEXCLUSIONS_AND_MINIMUM_AND_MAXIMUM );
        assertNotNull( ss );

        assertEquals( "ou=people", ss.getBase().toString() );
        assertTrue( ss.getChopBeforeExclusions().contains( new DN( "x=y" ).normalize( schemaManager ) ) );
        assertTrue( ss.getChopBeforeExclusions().contains( new DN( "y=z" ).normalize( schemaManager ) ) );
        assertTrue( ss.getChopAfterExclusions().contains( new DN( "k=l" ).normalize( schemaManager ) ) );
        assertTrue( ss.getChopAfterExclusions().contains( new DN( "l=m" ).normalize( schemaManager ) ) );
        assertEquals( 7, ss.getMinBaseDistance() );
        assertEquals( 77, ss.getMaxBaseDistance() );
    }


    /**
     * Tests the parser with a valid specification with refinement set.
     */
    @Test
    public void testSpecWithRefinement() throws Exception
    {
        SubtreeSpecification ss = parser.parse( SPEC_WITH_REFINEMENT );

        // The items
        Refinement topItem = new ItemRefinement( TOP_OC );
        Refinement aliasItem = new ItemRefinement( ALIAS_OC );
        Refinement personItem = new ItemRefinement( PERSON_OC );
        Refinement countryItem = new ItemRefinement( COUNTRY_OC );

        // The inner OR refinement or:{item:2.5.6.1, item:person}
        List<Refinement> orList = new ArrayList<Refinement>();
        orList.add( aliasItem );
        orList.add( personItem );

        Refinement orRefinement = new OrRefinement( orList );

        // The inner AND refinement and:{ item:2.5.6.0, or:... }
        List<Refinement> innerAndList = new ArrayList<Refinement>();
        innerAndList.add( topItem );
        innerAndList.add( orRefinement );

        Refinement innerAndRefinement = new AndRefinement( innerAndList );

        // The NOT refinement not:item:2.5.6.2
        Refinement notRefinement = new NotRefinement( countryItem );

        // The outer AND refinement and:{and:..., not:...}
        List<Refinement> outerAndList = new ArrayList<Refinement>();
        outerAndList.add( innerAndRefinement );
        outerAndList.add( notRefinement );

        Refinement outerAndRefinement = new AndRefinement( outerAndList );

        StringBuilder buffer = new StringBuilder();
        ss.getRefinement().printRefinementToBuffer( buffer );

        //assertEquals( outerAndRefinement.toString(), buffer );
        assertEquals( "and: { and: { item: 2.5.6.0, or: { item: 2.5.6.1, item: person } }, not: item: 2.5.6.2 }", buffer.toString() );
    }


    /**
     * Tests the parser with a valid specification with base and empty
     * refinement set.
     */
    @Test
    public void testSpecWithBaseAndEmptyRefinement() throws Exception
    {
        SubtreeSpecification ss = parser.parse( SPEC_WITH_BASE_AND_EMPTY_REFINEMENT );

        assertEquals( "ou=system", ss.getBase().toString() );
    }


    /**
     * Tests the parser with a valid specification with all components set.
     */
    @Test
    public void testSpecWithAllInOne() throws Exception
    {
        SubtreeSpecification ss = parser.parse( SPEC_WITH_ALL_IN_ONE );
        assertNotNull( ss );
    }


    /**
     * Tests the parser with a valid specification with unordinary component
     * order.
     */
    @Test
    public void testSpecOrderOfComponentsDoesNotMatter() throws Exception
    {
        SubtreeSpecification ss = parser.parse( SPEC_ORDER_OF_COMPONENTS_DOES_NOT_MATTER );
        assertNotNull( ss );
    }


    /**
     * Tests the parser with an invalid specification with silly things in.
     */
    @Test
    public void testInvalidSillyThing() throws Exception
    {
        try
        {
            parser.parse( INVALID_SILLY_THING );
            fail( "testInvalidSillyThing() should never come here..." );
        }
        catch ( ParseException e )
        {
            assertNotNull( e );
        }
    }


    /**
     * Test reusability, especially if the state is resetted.
     */
    @Test
    public void testReusabiltiy() throws Exception
    {
        DN firstDN = new DN("k=l");
        String firstExclusion = "{ specificExclusions { chopAfter:\"k=l\" } }";
        SubtreeSpecification firstSpec = parser.parse( firstExclusion );
        assertEquals( 1, firstSpec.getChopAfterExclusions().size() );
        assertEquals( firstDN, firstSpec.getChopAfterExclusions().iterator().next() );

        DN secondDN = new DN("x=y");
        String secondExclusion = "{ specificExclusions { chopAfter:\"x=y\" } }";
        SubtreeSpecification secondSpec = parser.parse( secondExclusion );
        assertEquals( 1, secondSpec.getChopAfterExclusions().size() );
        assertEquals( secondDN, secondSpec.getChopAfterExclusions().iterator().next() );

    }


    /**
     * Tests the multithreaded use of a single parser.
     */
    @Test
    public void testMultiThreaded() throws Exception
    {
        // start up and track all threads (40 threads)
        List<Thread> threads = new ArrayList<Thread>();
        for ( int ii = 0; ii < 10; ii++ )
        {
            Thread t0 = new Thread( new ParseSpecification( EMPTY_SPEC ) );
            Thread t1 = new Thread( new ParseSpecification( SPEC_WITH_SPECIFICEXCLUSIONS ) );
            Thread t2 = new Thread( new ParseSpecification( SPEC_WITH_MINIMUM_AND_MAXIMUM ) );
            Thread t3 = new Thread( new ParseSpecification( SPEC_WITH_ALL_IN_ONE ) );
            threads.add( t0 );
            threads.add( t1 );
            threads.add( t2 );
            threads.add( t3 );
            t0.start();
            t1.start();
            t2.start();
            t3.start();
        }

        // wait until all threads have died
        boolean hasLiveThreads = false;
        do
        {
            hasLiveThreads = false;

            for ( int ii = 0; ii < threads.size(); ii++ )
            {
                Thread t = threads.get( ii );
                hasLiveThreads = hasLiveThreads || t.isAlive();
            }
        }
        while ( hasLiveThreads );

        // check that no one thread failed to parse and generate a SS object
        assertTrue( isSuccessMultithreaded );
    }

    /**
     * Used to test multithreaded use of a single parser.
     */
    class ParseSpecification implements Runnable
    {
        private final String specStr;

        SubtreeSpecification result;


        public ParseSpecification(String specStr)
        {
            this.specStr = specStr;
        }


        public void run()
        {
            try
            {
                result = parser.parse( specStr );
            }
            catch ( ParseException e )
            {
                e.printStackTrace();
            }

            isSuccessMultithreaded = isSuccessMultithreaded && ( result != null );
        }
    }
}

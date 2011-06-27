/* $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.digester3;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.Rule;
import org.apache.commons.digester3.Rules;
import org.apache.commons.digester3.RulesBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * <p>
 * Test Case for the RulesBase matching rules. Most of this material was original contained in the digester test case
 * but was moved into this class so that extensions of the basic matching rules behaviour can extend this test case.
 * </p>
 * 
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class RulesBaseTestCase
{

    // ----------------------------------------------------- Instance Variables

    /**
     * The digester instance we will be processing.
     */
    protected Digester digester = null;

    // -------------------------------------------------- Overall Test Methods

    /**
     * Set up instance variables required by this test case.
     */
    @Before
    public void setUp()
    {

        digester = new Digester();
        digester.setRules( createMatchingRulesForTest() );

    }

    /**
     * <p>
     * This should be overriden by subclasses.
     * 
     * @return the matching rules to be tested.
     */
    protected Rules createMatchingRulesForTest()
    {
        return new RulesBase();
    }

    /**
     * Tear down instance variables required by this test case.
     */
    @After
    public void tearDown()
    {

        digester = null;

    }

    // ------------------------------------------------ Individual Test Methods

    /**
     * Basic test for rule creation and matching.
     */
    @Test
    public void testRules()
    {

        // clear any existing rules
        digester.getRules().clear();

        // perform tests

        assertEquals( "Initial rules list is empty", 0, digester.getRules().match( null, "a", null, null ).size() );
        digester.addSetProperties( "a" );
        assertEquals( "Add a matching rule", 1, digester.getRules().match( null, "a", null, null ).size() );
        digester.addSetProperties( "b" );
        assertEquals( "Add a non-matching rule", 1, digester.getRules().match( null, "a", null, null ).size() );
        digester.addSetProperties( "a/b" );
        assertEquals( "Add a non-matching nested rule", 1, digester.getRules().match( null, "a", null, null ).size() );
        digester.addSetProperties( "a/b" );
        assertEquals( "Add a second matching rule", 2, digester.getRules().match( null, "a/b", null, null ).size() );

        // clean up
        digester.getRules().clear();

    }

    /**
     * <p>
     * Test matching rules in {@link RulesBase}.
     * </p>
     * <p>
     * Tests:
     * </p>
     * <ul>
     * <li>exact match</li>
     * <li>tail match</li>
     * <li>longest pattern rule</li>
     * </ul>
     */
    @Test
    public void testRulesBase()
    {

        // clear any existing rules
        digester.getRules().clear();

        assertEquals( "Initial rules list is empty", 0, digester.getRules().rules().size() );

        // We're going to set up
        digester.addRule( "a/b/c/d", new TestRule( "a/b/c/d" ) );
        digester.addRule( "*/d", new TestRule( "*/d" ) );
        digester.addRule( "*/c/d", new TestRule( "*/c/d" ) );

        // Test exact match
        assertEquals( "Exact match takes precedence 1", 1, digester.getRules().match( null, "a/b/c/d", null, null ).size() );
        assertEquals( "Exact match takes precedence 2", "a/b/c/d",
                      ( (TestRule) digester.getRules().match( null, "a/b/c/d", null, null ).iterator().next() ).getIdentifier() );

        // Test wildcard tail matching
        assertEquals( "Wildcard tail matching rule 1", 1, digester.getRules().match( null, "a/b/d", null, null ).size() );
        assertEquals( "Wildcard tail matching rule 2", "*/d",
                      ( (TestRule) digester.getRules().match( null, "a/b/d", null, null ).iterator().next() ).getIdentifier() );

        // Test the longest matching pattern rule
        assertEquals( "Longest tail rule 1", 1, digester.getRules().match( null, "x/c/d", null, null ).size() );
        assertEquals( "Longest tail rule 2", "*/c/d",
                      ( (TestRule) digester.getRules().match( null, "x/c/d", null, null ).iterator().next() ).getIdentifier() );

        // Test wildcard tail matching at the top level,
        // i.e. the wildcard is nothing
        digester.addRule( "*/a", new TestRule( "*/a" ) );
        assertEquals( "Wildcard tail matching rule 3", 1, digester.getRules().match( null, "a", null, null ).size() );

        assertEquals( "Wildcard tail matching rule 3 (match too much)", 0,
                      digester.getRules().match( null, "aa", null, null ).size() );
        // clean up
        digester.getRules().clear();

    }

    /**
     * Test basic matchings involving namespaces.
     */
    @Test
    public void testBasicNamespaceMatching()
    {

        List<Rule> list = null;
        Iterator<Rule> it = null;

        // clear any existing rules
        digester.getRules().clear();

        assertEquals( "Initial rules list is empty", 0, digester.getRules().rules().size() );

        // Set up rules
        digester.addRule( "alpha/beta/gamma", new TestRule( "No-Namespace" ) );
        digester.addRule( "alpha/beta/gamma", new TestRule( "Euclidean-Namespace", "euclidean" ) );

        list = digester.getRules().rules();

        // test that matching null namespace brings back namespace and non-namespace rules
        list = digester.getRules().match( null, "alpha/beta/gamma", null, null );

        assertEquals( "Null namespace match (A)", 2, list.size() );

        it = list.iterator();
        assertEquals( "Null namespace match (B)", "No-Namespace", ( (TestRule) it.next() ).getIdentifier() );
        assertEquals( "Null namespace match (C)", "Euclidean-Namespace", ( (TestRule) it.next() ).getIdentifier() );

        // test that matching euclid namespace brings back namespace and non-namespace rules
        list = digester.getRules().match( "euclidean", "alpha/beta/gamma", null, null );

        assertEquals( "Matching namespace match (A)", 2, list.size() );

        it = list.iterator();
        assertEquals( "Matching namespace match (B)", "No-Namespace", ( (TestRule) it.next() ).getIdentifier() );
        assertEquals( "Matching namespace match (C)", "Euclidean-Namespace", ( (TestRule) it.next() ).getIdentifier() );

        // test that matching another namespace brings back only non-namespace rule
        list = digester.getRules().match( "hyperbolic", "alpha/beta/gamma", null, null );

        assertEquals( "Non matching namespace match (A)", 1, list.size() );

        it = list.iterator();
        assertEquals( "Non matching namespace match (B)", "No-Namespace", ( (TestRule) it.next() ).getIdentifier() );

        // clean up
        digester.getRules().clear();

    }

    /**
     * Rules must always be returned in the correct order.
     */
    @Test
    public void testOrdering()
    {

        // clear any existing rules
        digester.getRules().clear();

        assertEquals( "Initial rules list is empty", 0, digester.getRules().rules().size() );

        // Set up rules
        digester.addRule( "alpha/beta/gamma", new TestRule( "one" ) );
        digester.addRule( "alpha/beta/gamma", new TestRule( "two" ) );
        digester.addRule( "alpha/beta/gamma", new TestRule( "three" ) );

        // test that rules are returned in set order
        List<Rule> list = digester.getRules().match( null, "alpha/beta/gamma", null, null );

        assertEquals( "Testing ordering mismatch (A)", 3, list.size() );

        Iterator<Rule> it = list.iterator();
        assertEquals( "Testing ordering mismatch (B)", "one", ( (TestRule) it.next() ).getIdentifier() );
        assertEquals( "Testing ordering mismatch (C)", "two", ( (TestRule) it.next() ).getIdentifier() );
        assertEquals( "Testing ordering mismatch (D)", "three", ( (TestRule) it.next() ).getIdentifier() );

        // clean up
        digester.getRules().clear();

    }

    /** Tests the behaviour when a rule is added with a trailing slash */
    @Test
    public void testTrailingSlash()
    {
        // clear any existing rules
        digester.getRules().clear();

        assertEquals( "Initial rules list is empty", 0, digester.getRules().rules().size() );

        // Set up rules
        digester.addRule( "alpha/beta/gamma/", new TestRule( "one" ) );
        digester.addRule( "alpha/beta/", new TestRule( "two" ) );
        digester.addRule( "beta/gamma/alpha", new TestRule( "three" ) );

        // test that rules are returned in set order
        List<Rule> list = digester.getRules().match( null, "alpha/beta/gamma", null, null );

        assertEquals( "Testing number of matches", 1, list.size() );

        Iterator<Rule> it = list.iterator();
        assertEquals( "Testing ordering (A)", "one", ( (TestRule) it.next() ).getIdentifier() );

        // clean up
        digester.getRules().clear();
    }
}

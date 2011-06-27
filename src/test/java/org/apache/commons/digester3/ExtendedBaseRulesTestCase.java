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

import org.apache.commons.digester3.ExtendedBaseRules;
import org.apache.commons.digester3.Rule;
import org.apache.commons.digester3.Rules;
import org.junit.Test;

/**
 * <p>
 * Runs standard tests for RulesBase as well as tests of extensions.
 * 
 * @author Robert Burrell Donkin <robertdonkin@mac.com>
 * @version $Revision$ $Date$
 */

public class ExtendedBaseRulesTestCase
    extends RulesBaseTestCase
{

    // -------------------------------------------------- Overall Test Methods

    /**
     * <p>
     * This should be overriden by subclasses.
     * 
     * @return the matching rules to be tested.
     */
    @Override
    protected Rules createMatchingRulesForTest()
    {

        return new ExtendedBaseRules();
    }

    /**
     * Basic test of parent matching rules. A parent match matches any child of a particular kind of parent. A wild
     * parent has a wildcard prefix. This method tests non-universal wildcards.
     */
    @Test
    public void testBasicParentMatch()
    {

        // clear any existing rules
        digester.getRules().clear();

        assertEquals( "Initial rules list is empty", 0, digester.getRules().rules().size() );

        // Set up rules
        // since these are all NON-UNIVERSAL matches
        // only expect one match at each stage
        digester.addRule( "alpha/beta/gamma/delta", new TestRule( "exact" ) );
        digester.addRule( "*/beta/gamma/epsilon", new TestRule( "wild_child" ) );
        digester.addRule( "alpha/beta/gamma/?", new TestRule( "exact_parent" ) );
        digester.addRule( "*/beta/gamma/?", new TestRule( "wild_parent" ) );

        List<Rule> list = null;
        Iterator<Rule> it = null;

        // this should match just the exact since this has presidence
        list = digester.getRules().match( null, "alpha/beta/gamma/delta", null, null );

        // all three rules should match
        assertEquals( "Testing basic parent mismatch (A)", 1, list.size() );

        it = list.iterator();
        assertEquals( "Testing basic parent mismatch (B)", "exact", ( (TestRule) it.next() ).getIdentifier() );

        // we don't have an exact match for this child so we should get the exact parent
        list = digester.getRules().match( null, "alpha/beta/gamma/epsilon", null, null );

        // all three rules should match
        assertEquals( "Testing basic parent mismatch (C)", 1, list.size() );

        it = list.iterator();
        assertEquals( "Testing basic parent mismatch (D)", "exact_parent", ( (TestRule) it.next() ).getIdentifier() );

        // wild child overrides wild parent
        list = digester.getRules().match( null, "alpha/omega/beta/gamma/epsilon", null, null );

        // all three rules should match
        assertEquals( "Testing basic parent mismatch (E)", 1, list.size() );

        it = list.iterator();
        assertEquals( "Testing basic parent mismatch (F)", "wild_child", ( (TestRule) it.next() ).getIdentifier() );

        // nothing else matches so return wild parent
        list = digester.getRules().match( null, "alpha/omega/beta/gamma/zeta", null, null );

        // all three rules should match
        assertEquals( "Testing basic parent mismatch (G)", 1, list.size() );

        it = list.iterator();
        assertEquals( "Testing basic parent mismatch (H)", "wild_parent", ( (TestRule) it.next() ).getIdentifier() );

        // clean up
        digester.getRules().clear();

    }

    /**
     * Basic test of universal matching rules. Universal rules act independent.
     */
    @Test
    public void testBasicUniversal()
    {

        // clear any existing rules
        digester.getRules().clear();

        assertEquals( "Initial rules list is empty", 0, digester.getRules().rules().size() );

        // Set up rules
        // set up universal matches against non-universal ones
        digester.addRule( "alpha/beta/gamma", new TestRule( "exact" ) );
        digester.addRule( "*/beta/gamma", new TestRule( "non_wild_head" ) );
        digester.addRule( "!*/beta/gamma", new TestRule( "universal_wild_head" ) );
        digester.addRule( "!alpha/beta/gamma/?", new TestRule( "universal_wild_child" ) );
        digester.addRule( "alpha/beta/gamma/?", new TestRule( "non_wild_child" ) );
        digester.addRule( "alpha/beta/gamma/epsilon", new TestRule( "exact2" ) );
        digester.addRule( "alpha/epsilon/beta/gamma/zeta", new TestRule( "exact3" ) );
        digester.addRule( "*/gamma/?", new TestRule( "non_wildhead_child" ) );
        digester.addRule( "!*/epsilon/beta/gamma/?", new TestRule( "universal_wildhead_child" ) );

        List<Rule> list = null;
        Iterator<Rule> it = null;

        // test universal wild head
        list = digester.getRules().match( null, "alpha/beta/gamma", null, null );

        assertEquals( "Testing universal wildcard mismatch (A)", 2, list.size() );

        it = list.iterator();
        assertEquals( "Testing universal wildcard mismatch (B)", "exact", ( (TestRule) it.next() ).getIdentifier() );
        assertEquals( "Testing universal wildcard mismatch (C)", "universal_wild_head",
                      ( (TestRule) it.next() ).getIdentifier() );

        // test universal parent
        list = digester.getRules().match( null, "alpha/beta/gamma/epsilon", null, null );

        assertEquals( "Testing universal wildcard mismatch (D)", 2, list.size() );

        it = list.iterator();
        assertEquals( "Testing universal wildcard mismatch (E)", "universal_wild_child",
                      ( (TestRule) it.next() ).getIdentifier() );
        assertEquals( "Testing universal wildcard mismatch (F)", "exact2", ( (TestRule) it.next() ).getIdentifier() );

        // test universal parent
        list = digester.getRules().match( null, "alpha/beta/gamma/zeta", null, null );

        assertEquals( "Testing universal wildcard mismatch (G)", 2, list.size() );

        it = list.iterator();
        assertEquals( "Testing universal wildcard mismatch (H)", "universal_wild_child",
                      ( (TestRule) it.next() ).getIdentifier() );
        assertEquals( "Testing universal wildcard mismatch (I)", "non_wild_child",
                      ( (TestRule) it.next() ).getIdentifier() );

        // test wildcard universal parent
        list = digester.getRules().match( null, "alpha/epsilon/beta/gamma/alpha", null, null );

        assertEquals( "Testing universal wildcard mismatch (J)", 2, list.size() );

        it = list.iterator();
        assertEquals( "Testing universal wildcard mismatch (K)", "non_wildhead_child",
                      ( (TestRule) it.next() ).getIdentifier() );
        assertEquals( "Testing universal wildcard mismatch (L)", "universal_wildhead_child",
                      ( (TestRule) it.next() ).getIdentifier() );

        // test wildcard universal parent
        list = digester.getRules().match( null, "alpha/epsilon/beta/gamma/zeta", null, null );

        assertEquals( "Testing universal wildcard mismatch (M)", 2, list.size() );

        it = list.iterator();
        assertEquals( "Testing universal wildcard mismatch (M)", "exact3", ( (TestRule) it.next() ).getIdentifier() );
        assertEquals( "Testing universal wildcard mismatch (O)", "universal_wildhead_child",
                      ( (TestRule) it.next() ).getIdentifier() );

        // clean up
        digester.getRules().clear();

    }

    /**
     * Basic test of wild matches. A universal will match matches anything! A non-universal will match matches anything
     * not matched by something else. This method tests non-universal and universal wild matches.
     */
    @Test
    public void testWildMatch()
    {

        // clear any existing rules
        digester.getRules().clear();

        assertEquals( "Initial rules list is empty", 0, digester.getRules().rules().size() );

        // Set up rules
        // The combinations a little large to test everything but we'll pick a couple and try them.
        digester.addRule( "*", new TestRule( "basic_wild" ) );
        digester.addRule( "!*", new TestRule( "universal_wild" ) );
        digester.addRule( "alpha/beta/gamma/delta", new TestRule( "exact" ) );
        digester.addRule( "*/beta/gamma/?", new TestRule( "wild_parent" ) );

        List<Rule> list = null;
        Iterator<Rule> it = null;

        // The universal wild will always match whatever else does
        list = digester.getRules().match( null, "alpha/beta/gamma/delta", null, null );

        // all three rules should match
        assertEquals( "Testing wild mismatch (A)", 2, list.size() );

        it = list.iterator();
        assertEquals( "Testing wild mismatch (B)", "universal_wild", ( (TestRule) it.next() ).getIdentifier() );
        assertEquals( "Testing wild mismatch (C)", "exact", ( (TestRule) it.next() ).getIdentifier() );

        // The universal wild will always match whatever else does
        list = digester.getRules().match( null, "alpha/beta/gamma/epsilon", null, null );

        assertEquals( "Testing wild mismatch (D)", 2, list.size() );

        it = list.iterator();
        assertEquals( "Testing wild mismatch (E)", "universal_wild", ( (TestRule) it.next() ).getIdentifier() );
        assertEquals( "Testing wild mismatch (F)", "wild_parent", ( (TestRule) it.next() ).getIdentifier() );

        // The universal wild will always match whatever else does
        // we have no other non-universal matching so this will match the non-universal wild as well
        list = digester.getRules().match( null, "alpha/gamma", null, null );

        assertEquals( "Testing wild mismatch (G)", 2, list.size() );

        it = list.iterator();
        assertEquals( "Testing wild mismatch (H)", "basic_wild", ( (TestRule) it.next() ).getIdentifier() );
        assertEquals( "Testing wild mismatch (I)", "universal_wild", ( (TestRule) it.next() ).getIdentifier() );

        // clean up
        digester.getRules().clear();

    }

    /**
     * Basic test of wild matches. A universal will match matches anything! A non-universal will match matches anything
     * not matched by something else. This method tests non-universal and universal wild matches.
     */
    @Test
    public void testRootTailMatch()
    {

        // clear any existing rules
        digester.getRules().clear();

        assertEquals( "Initial rules list is empty", 0, digester.getRules().rules().size() );

        // Set up rules
        // The combinations a little large to test everything but we'll pick a couple and try them.
        digester.addRule( "*/a", new TestRule( "a_tail" ) );

        List<Rule> list = null;

        list = digester.getRules().match( null, "a", null, null );

        assertEquals( "Testing tail wrong size (A)", 1, list.size() );
        assertEquals( "Testing tail mismatch (B)", "a_tail", ( (TestRule) list.get( 0 ) ).getIdentifier() );

        list = digester.getRules().match( null, "beta/a", null, null );

        assertEquals( "Testing tail wrong size (C)", 1, list.size() );
        assertEquals( "Testing tail mismatch (D)", "a_tail", ( (TestRule) list.get( 0 ) ).getIdentifier() );

        list = digester.getRules().match( null, "be/aaa", null, null );

        assertEquals( "Testing tail no matches (E)", 0, list.size() );

        list = digester.getRules().match( null, "aaa", null, null );

        assertEquals( "Testing tail no matches (F)", 0, list.size() );

        list = digester.getRules().match( null, "a/beta", null, null );

        assertEquals( "Testing tail no matches (G)", 0, list.size() );

        // clean up
        digester.getRules().clear();

    }

    @Test
    public void testAncesterMatch()
        throws Exception
    {
        // test fixed root ancester
        digester.getRules().clear();

        digester.addRule( "!a/b/*", new TestRule( "uni-a-b-star" ) );
        digester.addRule( "a/b/*", new TestRule( "a-b-star" ) );
        digester.addRule( "a/b/c", new TestRule( "a-b-c" ) );
        digester.addRule( "a/b/?", new TestRule( "a-b-child" ) );

        List<Rule> list = digester.getRules().match( null, "a/b/c", null, null );

        assertEquals( "Simple ancester matches (1)", 2, list.size() );
        assertEquals( "Univeral ancester mismatch (1)", "uni-a-b-star", ( (TestRule) list.get( 0 ) ).getIdentifier() );
        assertEquals( "Parent precedence failure", "a-b-c", ( (TestRule) list.get( 1 ) ).getIdentifier() );

        list = digester.getRules().match( null, "a/b/b", null, null );
        assertEquals( "Simple ancester matches (2)", 2, list.size() );
        assertEquals( "Univeral ancester mismatch (2)", "uni-a-b-star", ( (TestRule) list.get( 0 ) ).getIdentifier() );
        assertEquals( "Child precedence failure", "a-b-child", ( (TestRule) list.get( 1 ) ).getIdentifier() );

        list = digester.getRules().match( null, "a/b/d", null, null );
        assertEquals( "Simple ancester matches (3)", 2, list.size() );
        assertEquals( "Univeral ancester mismatch (3)", "uni-a-b-star", ( (TestRule) list.get( 0 ) ).getIdentifier() );
        assertEquals( "Ancester mismatch (1)", "a-b-child", ( (TestRule) list.get( 1 ) ).getIdentifier() );

        list = digester.getRules().match( null, "a/b/d/e/f", null, null );
        assertEquals( "Simple ancester matches (4)", 2, list.size() );
        assertEquals( "Univeral ancester mismatch (4)", "uni-a-b-star", ( (TestRule) list.get( 0 ) ).getIdentifier() );
        assertEquals( "Ancester mismatch (2)", "a-b-star", ( (TestRule) list.get( 1 ) ).getIdentifier() );

        // test wild root ancester
        digester.getRules().clear();

        digester.addRule( "!*/a/b/*", new TestRule( "uni-star-a-b-star" ) );
        digester.addRule( "*/b/c/*", new TestRule( "star-b-c-star" ) );
        digester.addRule( "*/b/c/d", new TestRule( "star-b-c-d" ) );
        digester.addRule( "a/b/c", new TestRule( "a-b-c" ) );

        list = digester.getRules().match( null, "a/b/c", null, null );
        assertEquals( "Wild ancester match (1)", 2, list.size() );
        assertEquals( "Univeral ancester mismatch (5)", "uni-star-a-b-star",
                      ( (TestRule) list.get( 0 ) ).getIdentifier() );
        assertEquals( "Match missed (1)", "a-b-c", ( (TestRule) list.get( 1 ) ).getIdentifier() );

        list = digester.getRules().match( null, "b/c", null, null );
        assertEquals( "Wild ancester match (2)", 1, list.size() );
        assertEquals( "Match missed (2)", "star-b-c-star", ( (TestRule) list.get( 0 ) ).getIdentifier() );

        list = digester.getRules().match( null, "a/b/c/d", null, null );
        assertEquals( "Wild ancester match (3)", 2, list.size() );
        assertEquals( "Match missed (3)", "uni-star-a-b-star", ( (TestRule) list.get( 0 ) ).getIdentifier() );
        assertEquals( "Match missed (4)", "star-b-c-d", ( (TestRule) list.get( 1 ) ).getIdentifier() );

        list = digester.getRules().match( null, "b/b/c/e/d", null, null );
        assertEquals( "Wild ancester match (2)", 1, list.size() );
        assertEquals( "Match missed (5)", "star-b-c-star", ( (TestRule) list.get( 0 ) ).getIdentifier() );
    }

    @Test
    public void testLongMatch()
    {

        digester.getRules().clear();

        digester.addRule( "a/b/c/d/*", new TestRule( "a-b-c-d-star" ) );

        List<Rule> list = digester.getRules().match( null, "a/b/c/d/e", null, null );
        assertEquals( "Long match (1)", 1, list.size() );
        assertEquals( "Match missed (1)", "a-b-c-d-star", ( (TestRule) list.get( 0 ) ).getIdentifier() );

        list = digester.getRules().match( null, "a/b/c/d/e/f", null, null );
        assertEquals( "Long match (2)", 1, list.size() );
        assertEquals( "Match missed (2)", "a-b-c-d-star", ( (TestRule) list.get( 0 ) ).getIdentifier() );

        list = digester.getRules().match( null, "a/b/c/d/e/f/g", null, null );
        assertEquals( "Long match (3)", 1, list.size() );
        assertEquals( "Match missed (3)", "a-b-c-d-star", ( (TestRule) list.get( 0 ) ).getIdentifier() );

        list = digester.getRules().match( null, "a/b/c/d", null, null );
        assertEquals( "Long match (4)", 0, list.size() );
    }

    @Test
    public void testInstructors()
    {
        digester.getRules().clear();

        digester.addRule( "!instructors/*", new TestRule( "instructors" ) );
        digester.addRule( "!instructor/*", new TestRule( "instructor" ) );

        List<Rule> list = digester.getRules().match( null, "instructors", null, null );
        assertEquals( "Only expect to match instructors", 1, list.size() );
        assertEquals( "Instructors expected", "instructors", ( (TestRule) list.get( 0 ) ).getIdentifier() );

    }

    @Test
    public void testMiddleInstructors()
    {
        digester.getRules().clear();

        digester.addRule( "!instructors/*", new TestRule( "instructors" ) );

        List<Rule> list = digester.getRules().match( null, "/tosh/instructors/fiddlesticks", null, null );
        assertEquals( "No matches expected", 0, list.size() );

    }
}

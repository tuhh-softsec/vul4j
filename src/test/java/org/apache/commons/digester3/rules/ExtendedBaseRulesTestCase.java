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
package org.apache.commons.digester3.rules;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.digester3.OrderRule;
import org.apache.commons.digester3.Rule;
import org.apache.commons.digester3.spi.Rules;
import org.junit.Test;

/**
 * <p> Runs standard tests for RulesBase as well as tests of extensions.
 *
 * @author Robert Burrell Donkin <robertdonkin@mac.com>
 * @version $Revision$ $Date$
 */
public class ExtendedBaseRulesTestCase extends BaseRulesTestCase {

    /**
     * <p> This should be overriden by subclasses.
     *
     * @return the matching rules to be tested.
     */
    @Override
    protected Rules createMatchingRulesForTest() {
        return new ExtendedBaseRules();
    }

    /**
     * Basic test of parent matching rules.
     * A parent match matches any child of a particular kind of parent.
     * A wild parent has a wildcard prefix.
     * This method tests non-universal wildcards.
     */
    @Test
    public void testBasicParentMatch() {
        Rules rules = this.createMatchingRulesForTest();

        assertEquals("Initial rules list is empty",
                0, rules.rules().size());

        // Set up rules
        // since these are all NON-UNIVERSAL matches
        // only expect one match at each stage
        rules.add("alpha/beta/gamma/delta", new OrderRule("exact"));
        rules.add("*/beta/gamma/epsilon", new OrderRule("wild_child"));
        rules.add("alpha/beta/gamma/?", new OrderRule("exact_parent"));
        rules.add("*/beta/gamma/?", new OrderRule("wild_parent"));


        List<Rule> list = null;
        Iterator<Rule> it = null;

        // this should match just the exact since this has presidence
        list = rules.match(null, "alpha/beta/gamma/delta");

        // all three rules should match
        assertEquals("Testing basic parent mismatch (A)", 1, list.size());

        it = list.iterator();
        assertEquals("Testing basic parent mismatch (B)", "exact", ((OrderRule) it.next()).getIdentifier());


        // we don't have an exact match for this child so we should get the exact parent
        list = rules.match(null, "alpha/beta/gamma/epsilon");

        // all three rules should match
        assertEquals("Testing basic parent mismatch (C)", 1, list.size());

        it = list.iterator();
        assertEquals("Testing basic parent mismatch (D)", "exact_parent", ((OrderRule) it.next()).getIdentifier());


        // wild child overrides wild parent
        list = rules.match(null, "alpha/omega/beta/gamma/epsilon");

        // all three rules should match
        assertEquals("Testing basic parent mismatch (E)", 1, list.size());

        it = list.iterator();
        assertEquals("Testing basic parent mismatch (F)", "wild_child", ((OrderRule) it.next()).getIdentifier());


        // nothing else matches so return wild parent
        list = rules.match(null, "alpha/omega/beta/gamma/zeta");

        // all three rules should match
        assertEquals("Testing basic parent mismatch (G)", 1, list.size());

        it = list.iterator();
        assertEquals("Testing basic parent mismatch (H)", "wild_parent", ((OrderRule) it.next()).getIdentifier());
    }

    /**
     * Basic test of universal matching rules.
     * Universal rules act independent.
     */
    @Test
    public void testBasicUniversal() {
        Rules rules = this.createMatchingRulesForTest();

        assertEquals("Initial rules list is empty",
                0, rules.rules().size());

        // Set up rules
        // set up universal matches against non-universal ones
        rules.add("alpha/beta/gamma", new OrderRule("exact"));
        rules.add("*/beta/gamma", new OrderRule("non_wild_head"));
        rules.add("!*/beta/gamma", new OrderRule("universal_wild_head"));
        rules.add("!alpha/beta/gamma/?", new OrderRule("universal_wild_child"));
        rules.add("alpha/beta/gamma/?", new OrderRule("non_wild_child"));
        rules.add("alpha/beta/gamma/epsilon", new OrderRule("exact2"));
        rules.add("alpha/epsilon/beta/gamma/zeta", new OrderRule("exact3"));
        rules.add("*/gamma/?", new OrderRule("non_wildhead_child"));
        rules.add("!*/epsilon/beta/gamma/?", new OrderRule("universal_wildhead_child"));


        List<Rule> list = null;
        Iterator<Rule> it = null;

        // test universal wild head
        list = rules.match(null, "alpha/beta/gamma");

        assertEquals("Testing universal wildcard mismatch (A)", 2, list.size());

        it = list.iterator();
        assertEquals("Testing universal wildcard mismatch (B)", "exact", ((OrderRule) it.next()).getIdentifier());
        assertEquals("Testing universal wildcard mismatch (C)", "universal_wild_head", ((OrderRule) it.next()).getIdentifier());


        // test universal parent
        list = rules.match(null, "alpha/beta/gamma/epsilon");

        assertEquals("Testing universal wildcard mismatch (D)", 2, list.size());

        it = list.iterator();
        assertEquals("Testing universal wildcard mismatch (E)", "universal_wild_child", ((OrderRule) it.next()).getIdentifier());
        assertEquals("Testing universal wildcard mismatch (F)", "exact2", ((OrderRule) it.next()).getIdentifier());

        // test universal parent
        list = rules.match(null, "alpha/beta/gamma/zeta");

        assertEquals("Testing universal wildcard mismatch (G)", 2, list.size());

        it = list.iterator();
        assertEquals("Testing universal wildcard mismatch (H)", "universal_wild_child", ((OrderRule) it.next()).getIdentifier());
        assertEquals("Testing universal wildcard mismatch (I)", "non_wild_child", ((OrderRule) it.next()).getIdentifier());


        // test wildcard universal parent
        list = rules.match(null, "alpha/epsilon/beta/gamma/alpha");

        assertEquals("Testing universal wildcard mismatch (J)", 2, list.size());

        it = list.iterator();
        assertEquals("Testing universal wildcard mismatch (K)", "non_wildhead_child", ((OrderRule) it.next()).getIdentifier());
        assertEquals("Testing universal wildcard mismatch (L)", "universal_wildhead_child", ((OrderRule) it.next()).getIdentifier());

        // test wildcard universal parent
        list = rules.match(null, "alpha/epsilon/beta/gamma/zeta");

        assertEquals("Testing universal wildcard mismatch (M)", 2, list.size());

        it = list.iterator();
        assertEquals("Testing universal wildcard mismatch (M)", "exact3", ((OrderRule) it.next()).getIdentifier());
        assertEquals("Testing universal wildcard mismatch (O)", "universal_wildhead_child", ((OrderRule) it.next()).getIdentifier());
    }

    /**
     * Basic test of wild matches.
     * A universal will match matches anything!
     * A non-universal will match matches anything not matched by something else.
     * This method tests non-universal and universal wild matches.
     */
    @Test
    public void testWildMatch() {
        Rules rules = this.createMatchingRulesForTest();

        assertEquals("Initial rules list is empty",
                0, rules.rules().size());

        // Set up rules
        // The combinations a little large to test everything but we'll pick a couple and try them.
        rules.add("*", new OrderRule("basic_wild"));
        rules.add("!*", new OrderRule("universal_wild"));
        rules.add("alpha/beta/gamma/delta", new OrderRule("exact"));
        rules.add("*/beta/gamma/?", new OrderRule("wild_parent"));

        List<Rule> list = null;
        Iterator<Rule> it = null;

        // The universal wild will always match whatever else does
        list = rules.match(null, "alpha/beta/gamma/delta");

        // all three rules should match
        assertEquals("Testing wild mismatch (A)", 2, list.size());

        it = list.iterator();
        assertEquals("Testing wild mismatch (B)", "universal_wild", ((OrderRule) it.next()).getIdentifier());
        assertEquals("Testing wild mismatch (C)", "exact", ((OrderRule) it.next()).getIdentifier());

        // The universal wild will always match whatever else does
        list = rules.match(null, "alpha/beta/gamma/epsilon");

        assertEquals("Testing wild mismatch (D)", 2, list.size());

        it = list.iterator();
        assertEquals("Testing wild mismatch (E)", "universal_wild", ((OrderRule) it.next()).getIdentifier());
        assertEquals("Testing wild mismatch (F)", "wild_parent", ((OrderRule) it.next()).getIdentifier());

        // The universal wild will always match whatever else does
        // we have no other non-universal matching so this will match the non-universal wild as well
        list = rules.match(null, "alpha/gamma");

        assertEquals("Testing wild mismatch (G)", 2, list.size());

        it = list.iterator();
        assertEquals("Testing wild mismatch (H)", "basic_wild", ((OrderRule) it.next()).getIdentifier());
        assertEquals("Testing wild mismatch (I)", "universal_wild", ((OrderRule) it.next()).getIdentifier());
    }

    /**
     * Basic test of wild matches.
     * A universal will match matches anything!
     * A non-universal will match matches anything not matched by something else.
     * This method tests non-universal and universal wild matches.
     */
    @Test
    public void testRootTailMatch() {
        Rules rules = this.createMatchingRulesForTest();

        assertEquals("Initial rules list is empty",
                0, rules.rules().size());

        // Set up rules
        // The combinations a little large to test everything but we'll pick a couple and try them.
        rules.add("*/a", new OrderRule("a_tail"));


        List<Rule> list = null;

        list = rules.match(null, "a");

        assertEquals("Testing tail wrong size (A)", 1, list.size());
        assertEquals("Testing tail mismatch (B)", "a_tail", ((OrderRule) list.get(0)).getIdentifier());


        list = rules.match(null, "beta/a");

        assertEquals("Testing tail wrong size (C)", 1, list.size());
        assertEquals("Testing tail mismatch (D)", "a_tail", ((OrderRule) list.get(0)).getIdentifier());

        list = rules.match(null, "be/aaa");

        assertEquals("Testing tail no matches (E)", 0, list.size());

        list = rules.match(null, "aaa");

        assertEquals("Testing tail no matches (F)", 0, list.size());

        list = rules.match(null, "a/beta");

        assertEquals("Testing tail no matches (G)", 0, list.size());
    }

    @Test
    public void testAncesterMatch() throws Exception {
        // test fixed root ancester
        Rules rules = this.createMatchingRulesForTest();

        rules.add("!a/b/*", new OrderRule("uni-a-b-star"));
        rules.add("a/b/*", new OrderRule("a-b-star"));
        rules.add("a/b/c", new OrderRule("a-b-c"));
        rules.add("a/b/?", new OrderRule("a-b-child"));

        List<Rule> list = rules.match(null, "a/b/c");

        assertEquals("Simple ancester matches (1)", 2, list.size());
        assertEquals("Univeral ancester mismatch (1)", "uni-a-b-star" , ((OrderRule) list.get(0)).getIdentifier());
        assertEquals("Parent precedence failure", "a-b-c" , ((OrderRule) list.get(1)).getIdentifier());

        list = rules.match(null, "a/b/b");
        assertEquals("Simple ancester matches (2)", 2, list.size());
        assertEquals("Univeral ancester mismatch (2)", "uni-a-b-star" , ((OrderRule) list.get(0)).getIdentifier());
        assertEquals("Child precedence failure", "a-b-child" , ((OrderRule) list.get(1)).getIdentifier());

        list = rules.match(null, "a/b/d");
        assertEquals("Simple ancester matches (3)", 2, list.size());
        assertEquals("Univeral ancester mismatch (3)", "uni-a-b-star" , ((OrderRule) list.get(0)).getIdentifier());
        assertEquals("Ancester mismatch (1)", "a-b-child" , ((OrderRule) list.get(1)).getIdentifier());

        list = rules.match(null, "a/b/d/e/f");
        assertEquals("Simple ancester matches (4)", 2, list.size());
        assertEquals("Univeral ancester mismatch (4)", "uni-a-b-star" , ((OrderRule) list.get(0)).getIdentifier());
        assertEquals("Ancester mismatch (2)", "a-b-star" , ((OrderRule) list.get(1)).getIdentifier());

        // test wild root ancester
        rules.clear();

        rules.add("!*/a/b/*", new OrderRule("uni-star-a-b-star"));
        rules.add("*/b/c/*", new OrderRule("star-b-c-star"));
        rules.add("*/b/c/d", new OrderRule("star-b-c-d"));
        rules.add("a/b/c", new OrderRule("a-b-c"));

        list = rules.match(null, "a/b/c");  
        assertEquals("Wild ancester match (1)", 2, list.size());
        assertEquals(
                    "Univeral ancester mismatch (5)", 
                    "uni-star-a-b-star" , 
                    ((OrderRule) list.get(0)).getIdentifier());
        assertEquals("Match missed (1)", "a-b-c" , ((OrderRule) list.get(1)).getIdentifier());

        list = rules.match(null, "b/c");  
        assertEquals("Wild ancester match (2)", 1, list.size());
        assertEquals("Match missed (2)", "star-b-c-star" , ((OrderRule) list.get(0)).getIdentifier());

        list = rules.match(null, "a/b/c/d");
        assertEquals("Wild ancester match (3)", 2, list.size());
        assertEquals("Match missed (3)", "uni-star-a-b-star" , ((OrderRule) list.get(0)).getIdentifier());
        assertEquals("Match missed (4)", "star-b-c-d" , ((OrderRule) list.get(1)).getIdentifier());

        list = rules.match(null, "b/b/c/e/d");
        assertEquals("Wild ancester match (2)", 1, list.size());
        assertEquals("Match missed (5)", "star-b-c-star" , ((OrderRule) list.get(0)).getIdentifier());
    }

    @Test
    public void testLongMatch() {
        Rules rules = this.createMatchingRulesForTest();

        rules.add("a/b/c/d/*", new OrderRule("a-b-c-d-star"));

        List<Rule> list = rules.match(null, "a/b/c/d/e"); 
        assertEquals("Long match (1)", 1, list.size());
        assertEquals("Match missed (1)", "a-b-c-d-star" , ((OrderRule) list.get(0)).getIdentifier());

        list = rules.match(null, "a/b/c/d/e/f");
        assertEquals("Long match (2)", 1, list.size());
        assertEquals("Match missed (2)", "a-b-c-d-star" , ((OrderRule) list.get(0)).getIdentifier());

        list = rules.match(null, "a/b/c/d/e/f/g");
        assertEquals("Long match (3)", 1, list.size());
        assertEquals("Match missed (3)", "a-b-c-d-star" , ((OrderRule) list.get(0)).getIdentifier());

        list = rules.match(null, "a/b/c/d");
        assertEquals("Long match (4)", 0, list.size());
    }

    @Test
    public void testInstructors() {
        Rules rules = this.createMatchingRulesForTest();

        rules.add("!instructors/*", new OrderRule("instructors"));
        rules.add("!instructor/*", new OrderRule("instructor"));

        List<Rule> list = rules.match(null, "instructors");
        assertEquals("Only expect to match instructors", 1, list.size());
        assertEquals("Instructors expected", "instructors" , ((OrderRule) list.get(0)).getIdentifier()); 
    }

    @Test
    public void testMiddleInstructors() {
        Rules rules = this.createMatchingRulesForTest();

        rules.add("!instructors/*", new OrderRule("instructors"));

        List<Rule> list = rules.match(null, "/tosh/instructors/fiddlesticks");
        assertEquals("No matches expected", 0, list.size());
    }

}

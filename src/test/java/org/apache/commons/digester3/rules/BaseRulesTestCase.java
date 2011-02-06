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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.digester3.OrderRule;
import org.apache.commons.digester3.Rule;
import org.apache.commons.digester3.SetPropertiesRule;
import org.apache.commons.digester3.spi.Rules;
import org.junit.Test;

/**
 * <p>Test Case for the BaseRules matching rules.
 * Most of this material was original contained in the digester test case
 * but was moved into this class so that extensions of the basic matching rules
 * behaviour can extend this test case.
 * </p>
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */
public class BaseRulesTestCase {

    /**
     * <p> This should be overriden by subclasses.
     *
     * @return the matching rules to be tested.
     */
    protected Rules createMatchingRulesForTest() {
        return new BaseRules();
    }

    /**
     * Basic test for rule creation and matching.
     */
    @Test
    public void testRules() {
        Rules rules = this.createMatchingRulesForTest();

        assertEquals("Initial rules list is empty", 0, rules.match(null, "a").size());

        rules.add("a", new SetPropertiesRule(new HashMap<String, String>(), true));
        assertEquals("Add a matching rule", 1, rules.match(null, "a").size());

        rules.add("b", new SetPropertiesRule(new HashMap<String, String>(), true));
        assertEquals("Add a non-matching rule", 1, rules.match(null, "a").size());

        rules.add("a/b", new SetPropertiesRule(new HashMap<String, String>(), true));
        assertEquals("Add a non-matching nested rule", 1, rules.match(null, "a").size());

        rules.add("a/b", new SetPropertiesRule(new HashMap<String, String>(), true));
        assertEquals("Add a second matching rule", 2, rules.match(null, "a/b").size());
    }

    /**
     * <p>Test matching rules in {@link RulesBase}.</p>
     *
     * <p>Tests:</p>
     * <ul>
     * <li>exact match</li>
     * <li>tail match</li>
     * <li>longest pattern rule</li>
     * </ul>
     */
    @Test
    public void testRulesBase() {
        Rules rules = this.createMatchingRulesForTest();

        assertEquals("Initial rules list is empty", 0, rules.rules().size());

        // We're going to set up
        rules.add("a/b/c/d", new OrderRule("a/b/c/d"));
        rules.add("*/d", new OrderRule("*/d"));
        rules.add("*/c/d", new OrderRule("*/c/d"));

        // Test exact match
        assertEquals("Exact match takes precedence 1", 1, rules.match(null, "a/b/c/d").size());
        assertEquals("Exact match takes precedence 2",
                "a/b/c/d",
                ((OrderRule) rules.match(null, "a/b/c/d").iterator().next()).getIdentifier());

        // Test wildcard tail matching
        assertEquals("Wildcard tail matching rule 1", 1, rules.match(null, "a/b/d").size());
        assertEquals("Wildcard tail matching rule 2",
                "*/d",
                ((OrderRule) rules.match(null, "a/b/d").iterator().next()).getIdentifier());

        // Test the longest matching pattern rule
        assertEquals("Longest tail rule 1", 1, rules.match(null, "x/c/d").size());
        assertEquals("Longest tail rule 2",
                "*/c/d",
                ((OrderRule) rules.match(null, "x/c/d").iterator().next()).getIdentifier());

        // Test wildcard tail matching at the top level,
        // i.e. the wildcard is nothing
        rules.add("*/a", new OrderRule("*/a"));
        assertEquals("Wildcard tail matching rule 3", 1, rules.match(null,"a").size());

        assertEquals("Wildcard tail matching rule 3 (match too much)", 0, rules.match(null,"aa").size());
    }

    /**
     * Test basic matchings involving namespaces.
     */
    @Test
    public void testBasicNamespaceMatching() {
        List<Rule> list = null;
        Iterator<Rule> it = null;

        Rules rules = this.createMatchingRulesForTest();

        assertEquals("Initial rules list is empty", 0, rules.rules().size());

        // Set up rules
        rules.add("alpha/beta/gamma", new OrderRule("No-Namespace"));
        rules.add("alpha/beta/gamma", new OrderRule("Euclidean-Namespace", "euclidean"));

        list = rules.rules();

        // test that matching null namespace brings back namespace and non-namespace rules
        list = rules.match(null, "alpha/beta/gamma");

        assertEquals("Null namespace match (A)", 2, list.size());

        it = list.iterator();
        assertEquals("Null namespace match (B)", "No-Namespace", ((OrderRule) it.next()).getIdentifier());
        assertEquals("Null namespace match (C)", "Euclidean-Namespace", ((OrderRule) it.next()).getIdentifier());

        // test that matching euclid namespace brings back namespace and non-namespace rules
        list = rules.match("euclidean", "alpha/beta/gamma");

        assertEquals("Matching namespace match (A)", 2, list.size());

        it = list.iterator();
        assertEquals("Matching namespace match (B)", "No-Namespace", ((OrderRule) it.next()).getIdentifier());
        assertEquals("Matching namespace match (C)", "Euclidean-Namespace", ((OrderRule) it.next()).getIdentifier());

        // test that matching another namespace brings back only non-namespace rule
        list = rules.match("hyperbolic", "alpha/beta/gamma");

        assertEquals("Non matching namespace match (A)", 1, list.size());

        it = list.iterator();
        assertEquals("Non matching namespace match (B)", "No-Namespace", ((OrderRule) it.next()).getIdentifier());
    }

    /**
     * Rules must always be returned in the correct order.
     */
    @Test
    public void testOrdering() {
        Rules rules = this.createMatchingRulesForTest();

        assertEquals("Initial rules list is empty", 0, rules.rules().size());

        // Set up rules
        rules.add("alpha/beta/gamma", new OrderRule("one"));
        rules.add("alpha/beta/gamma", new OrderRule("two"));
        rules.add("alpha/beta/gamma", new OrderRule("three"));

        // test that rules are returned in set order
        List<Rule> list = rules.match(null, "alpha/beta/gamma");

        assertEquals("Testing ordering mismatch (A)", 3, list.size());

        Iterator<Rule> it = list.iterator();
        assertEquals("Testing ordering mismatch (B)", "one", ((OrderRule) it.next()).getIdentifier());
        assertEquals("Testing ordering mismatch (C)", "two", ((OrderRule) it.next()).getIdentifier());
        assertEquals("Testing ordering mismatch (D)", "three", ((OrderRule) it.next()).getIdentifier());
    }

    /** Tests the behaviour when a rule is added with a trailing slash*/
    @Test
    public void testTrailingSlash() {
        Rules rules = this.createMatchingRulesForTest();

        assertEquals("Initial rules list is empty", 0, rules.rules().size());

        // Set up rules
        rules.add("alpha/beta/gamma/", new OrderRule("one"));
        rules.add("alpha/beta/", new OrderRule("two"));
        rules.add("beta/gamma/alpha", new OrderRule("three"));

        // test that rules are returned in set order
        List<Rule> list = rules.match(null, "alpha/beta/gamma");

        assertEquals("Testing number of matches", 1, list.size());

        Iterator<Rule> it = list.iterator();
        assertEquals("Testing ordering (A)", "one", ((OrderRule) it.next()).getIdentifier());

        // clean up
        rules.clear();
    }

}

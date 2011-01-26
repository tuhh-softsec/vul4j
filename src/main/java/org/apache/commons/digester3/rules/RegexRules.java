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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.digester3.Rule;
import org.apache.commons.digester3.spi.Rules;

/**
 * <p>Rules implementation that uses regular expression matching for paths.</p>
 *
 * <p>The regex implementation is pluggable, allowing different strategies to be used.
 * The basic way that this class work does not vary.
 * All patterns are tested to see if they match the path using the regex matcher.
 * All those that do are return in the order which the rules were added.</p>
 */
public class RegexRules implements Rules {

    /** All registered <code>Rule</code>'s  */
    private final List<RegisteredRule> registeredRules = new ArrayList<RegisteredRule>();

    /** The regex strategy used by this RegexRules */
    private final RegexMatcher matcher;

    /**
     * Construct sets the Regex matching strategy.
     *
     * @param matcher the regex strategy to be used, not null
     * @throws IllegalArgumentException if the strategy is null
     */
    public RegexRules(RegexMatcher matcher) {
        if (matcher == null) {
            throw new IllegalArgumentException("RegexMatcher must not be null.");
        }
        this.matcher = matcher;
    }

    /**
     * {@inheritDoc}
     */
    public void add(String pattern, Rule rule) {
        this.registeredRules.add(new RegisteredRule(pattern, rule));
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        this.registeredRules.clear();
    }

    /**
     * {@inheritDoc}
     */
    public List<Rule> match(String namespaceURI, String pattern) {
        //
        // not a particularly quick implementation
        // regex is probably going to be slower than string equality
        // so probably should have a set of strings
        // and test each only once
        //
        // XXX FIX ME - Time And Optimize
        //
        List<Rule> rules = new ArrayList<Rule>(this.registeredRules.size());
        for (RegisteredRule rr : this.registeredRules) {
            if (this.matcher.match(pattern, rr.getPattern())) {
                rules.add(rr.getRule());
            }
        }
        return rules;
    }

    /**
     * Return a List of all registered Rule instances, or a zero-length List
     * if there are no registered Rule instances.  If more than one Rule
     * instance has been registered, they <strong>must</strong> be returned
     * in the order originally registered through the <code>add()</code>
     * method.
     */
    public List<Rule> rules() {
        List<Rule> rules = new ArrayList<Rule>(registeredRules.size());
        for (RegisteredRule rr : this.registeredRules) {
            rules.add(rr.getRule());
        }
        return rules;
    }

    /** Used to associate rules with paths in the rules list */
    private class RegisteredRule {

        private final String pattern;

        private final Rule rule;

        public RegisteredRule(String pattern, Rule rule) {
            this.pattern = pattern;
            this.rule = rule;
        }

        public String getPattern() {
            return this.pattern;
        }

        public Rule getRule() {
            return this.rule;
        }

    }

}

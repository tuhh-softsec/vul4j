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

package org.apache.commons.digester;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Rules implementation that uses regular expression matching for paths.</p>
 *
 * <p>The regex implementation is pluggable, allowing different strategies to be used.
 * The basic way that this class work does not vary.
 * All patterns are tested to see if they match the path using the regex matcher.
 * All those that do are return in the order which the rules were added.</p>
 *
 * @since 1.5
 */

public class RegexRules extends AbstractRulesImpl {

    // --------------------------------------------------------- Fields
    
    /** All registered <code>Rule</code>'s  */
    private ArrayList<RegisteredRule> registeredRules = new ArrayList<RegisteredRule>();
    /** The regex strategy used by this RegexRules */
    private RegexMatcher matcher;

    // --------------------------------------------------------- Constructor

    /**
     * Construct sets the Regex matching strategy.
     *
     * @param matcher the regex strategy to be used, not null
     * @throws IllegalArgumentException if the strategy is null
     */
    public RegexRules(RegexMatcher matcher) {
        setRegexMatcher(matcher);
    }

    // --------------------------------------------------------- Properties
    
    /** 
     * Gets the current regex matching strategy.
     */
    public RegexMatcher getRegexMatcher() {
        return matcher;
    }
    
    /** 
     * Sets the current regex matching strategy.
     *
     * @param matcher use this RegexMatcher, not null
     * @throws IllegalArgumentException if the strategy is null
     */
    public void setRegexMatcher(RegexMatcher matcher) {
        if (matcher == null) {
            throw new IllegalArgumentException("RegexMatcher must not be null.");
        }
        this.matcher = matcher;
    }
    
    // --------------------------------------------------------- Public Methods

    /**
     * Register a new Rule instance matching the specified pattern.
     *
     * @param pattern Nesting pattern to be matched for this Rule
     * @param rule Rule instance to be registered
     */
    @Override
    protected void registerRule(String pattern, Rule rule) {
        registeredRules.add(new RegisteredRule(pattern, rule));
    }

    /**
     * Clear all existing Rule instance registrations.
     */
    @Override
    public void clear() {
        registeredRules.clear();
    }

    /**
     * Finds matching rules by using current regex matching strategy.
     * The rule associated with each path that matches is added to the list of matches.
     * The order of matching rules is the same order that they were added.
     *
     * @param namespaceURI Namespace URI for which to select matching rules,
     *  or <code>null</code> to match regardless of namespace URI
     * @param pattern Nesting pattern to be matched
     * @return a list of matching <code>Rule</code>'s
     */
    @Override
    public List<Rule> match(String namespaceURI, String pattern) {
        //
        // not a particularly quick implementation
        // regex is probably going to be slower than string equality
        // so probably should have a set of strings
        // and test each only once
        //
        // XXX FIX ME - Time And Optimize
        //
        ArrayList<Rule> rules = new ArrayList<Rule>(registeredRules.size());
        for (RegisteredRule rr : registeredRules) {
            if (matcher.match(pattern, rr.pattern)) {
                rules.add(rr.rule);
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
    @Override
    public List<Rule> rules() {
        ArrayList<Rule> rules = new ArrayList<Rule>(registeredRules.size());
        for (RegisteredRule rr : registeredRules) {
            rules.add(rr.rule);
        }
        return rules;
    }
    
    /** Used to associate rules with paths in the rules list */
    private class RegisteredRule {
        String pattern;
        Rule rule;
        
        RegisteredRule(String pattern, Rule rule) {
            this.pattern = pattern;
            this.rule = rule;
        }
    }
}

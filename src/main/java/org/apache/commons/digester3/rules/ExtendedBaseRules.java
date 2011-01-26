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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.digester3.Rule;

/**
 * <p>Extension of {@link RulesBase} for complex schema.</p>
 *
 * <p>This is an extension of the basic pattern matching scheme
 * intended to improve support for mapping complex xml-schema.
 * It is intended to be a minimal extension of the standard rules
 * big enough to support complex schema but without the full generality
 * offered by more exotic matching pattern rules.</p>
 *
 * <h4>When should you use this rather than the original?</h4>
 *
 * <p>
 * This pattern-matching engine is complex and slower than the basic
 * default RulesBase class, but offers more functionality:
 * <ul>
 * <li>Universal patterns allow patterns to be specified which will match
 *  regardless of whether there are "better matching" patterns available.</li>
 * <li>Parent-match patterns (eg "a/b/?") allow matching for all direct
 *  children of a specified element.</li>
 * <li>Ancestor-match patterns (eg "a/b/*") allow matching all elements
 *  nested within a specified element to any nesting depth.</li>
 * <li>Completely-wild patterns ("*" or "!*") allow matching all elements.</li>
 * </ul>
 * </p>
 *
 * <h4>Universal Match Patterns</h4>
 *
 * <p>The default RulesBase pattern-matching engine always attempts to find
 * the "best matching pattern", and will ignore rules associated with other
 * patterns that match but are not "as good". As an example, if the pattern
 * "a/b/c" is associated with rules 1 and 2, and "*&#47;c" is associated with
 * rules 3 and 4 then element "a/b/c" will cause only rules 1 and 2 to execute.
 * Rules 3 and 4 do have matching patterns, but because the patterns are shorter
 * and include wildcard characters they are regarded as being "not as good" as
 * a direct match. In general, exact patterns are better than wildcard patterns,
 * and among multiple patterns with wildcards, the longest is preferred.
 * See the RulesBase class for more information.</p>
 *
 * <p>This feature of preferring "better" patterns can be a powerful tool.
 * However it also means that patterns can interact in unexpected ways.</p>
 *
 * <p>When using the ExtendedBaseRules, any pattern prefixed with '!' bypasses 
 * the "best match" feature. Even if there is an exact match or a longer 
 * wildcard match, patterns prefixed by '!' will still be tested to see if 
 * they match, and if so their associated Rule objects will be included in
 * the set of rules to be executed in the normal manner.</p>
 *
 * <ul>
 * <li>Pattern <code>"!*&#47;a/b"</code> matches whenever an 'b' element
 *     is inside an 'a'.</li>
 * <li>Pattern <code>"!a/b/?"</code> matches any child of a parent
 *     matching <code>"a/b"</code> (see "Parent Match Patterns").</li>
 * <li>Pattern <code>"!*&#47;a/b/?"</code> matches any child of a parent
 *     matching <code>"!*&#47;a/b"</code> (see "Parent Match Patterns").</li>
 * <li>Pattern <code>"!a/b/*"</code> matches any element whose path
 *     starts with "a" then "b" (see "Ancestor Match Patterns").</li>
 * <li>Pattern <code>"!*&#47;a/b/*"</code> matches any elements whose path
 *     contains 'a/b' (see "Ancestor Match Patterns").</li>
 * </ul>
 *
 * <h4>Parent Match Patterns</h4>
 *
 * <p>
 * These will match direct child elements of a particular parent element.
 * <ul>
 * <li>
 *  <code>"a/b/c/?"</code> matches any child whose parent matches
 *  <code>"a/b/c"</code>.  Exact parent rules take precedence over Ancestor
 *  Match patterns.
 * </li>
 * <li>
 *  <code>"*&#47;a/b/c/?"</code> matches any child whose parent matches
 *  <code>"*&#47;a/b/c"</code>.  The longest matching still applies to parent
 *  matches but the length excludes the '?', which effectively means
 *  that standard wildcard matches with the same level of depth are
 *  chosen in preference.
 * </li>
 * </ul>
 * </p>
 *
 * <h4>Ancestor Match Patterns</h4>
 *
 * <p>
 * These will match elements whose parentage includes a particular sequence
 * of elements.
 * <ul>
 * <li>
 *  <code>"a/b/*"</code> matches any element whose path starts with
 *  'a' then 'b'. Exact parent and parent match rules take precedence. 
 *  The longest ancestor match will take precedence.
 * </li>
 * <li>
 *  <code>"*&#47;a/b/*"</code> matches any elements whose path contains
 *  an element 'a' followed by an element 'b'. The longest matching still 
 *  applies but the length excludes the '*' at the end.
 * </li>
 * </ul>
 * </p>
 *
 * <h4>Completely Wild Patterns</h4>
 *
 * <p>Pattern <code>"*"</code> matches every pattern that isn't matched by
 * any other basic rule.</p>
 *
 * <p>Pattern <code>"!*"</code> matches every pattern.</p>
 *
 * <h4>Using The Extended Rules</h4>
 *
 * <p>By default, a Digester instance uses a {@link RulesBase} instance as
 * its pattern matching engine. To use an ExtendedBaseRules instance, call
 * the Digester.setRules method before adding any Rule objects to the digester
 * instance:
 * <pre>
 *     Digester digester = new Digester();
 *     digester.setRules( new ExtendedBaseRules() );
 * </pre></p>
 *
 * <p>The most important thing to remember when using the extended rules is 
 * that universal and non-universal patterns are completely independent.
 * Universal patterns are never affected by the addition of new patterns
 * or the removal of existing ones. Non-universal patterns are never affected
 * by the addition of new <em>universal</em> patterns or the removal of 
 * existing <em>universal</em> patterns. As in the basic matching rules, 
 * non-universal (basic) patterns <strong>can</strong> be affected by the 
 * addition of new <em>non-universal</em> patterns or the removal of existing 
 * <em>non-universal</em> patterns, because only rules associated with the
 * "best matching" pattern for each xml element are executed.
 *
 * <p> This means that you can use universal patterns to build up the simple 
 * parts of your structure - for example defining universal creation and 
 * property setting rules. More sophisticated and complex mapping will require 
 * non-universal patterns and this might mean that some of the universal rules 
 * will need to be replaced by a series of special cases using non-universal 
 * rules. But by using universal rules as your backbone, these additions 
 * should not break your existing rules.</p>
 */
public class ExtendedBaseRules extends BaseRules {

    /**
     * Counts the entry number for the rules.
     */
    private int counter = 0;

    /**
     * The decision algorithm used (unfortunately) doesn't preserve the entry
     * order.
     * This map is used by a comparator which orders the list of matches
     * before it's returned.
     * This map stores the entry number keyed by the rule.
     */
    private Map<Rule, Integer> order = new HashMap<Rule, Integer>();

    /**
     * Register a new Rule instance matching the specified pattern.
     *
     * @param pattern Nesting pattern to be matched for this Rule
     * @param rule Rule instance to be registered
     */
    @Override
    public void add(String pattern, Rule rule) {
        super.add(pattern, rule);
        this.counter++;
        this.order.put(rule, this.counter);
    }

    /**
     * Return a List of all registered Rule instances that match the specified
     * nesting pattern, or a zero-length List if there are no matches.  If more
     * than one Rule instance matches, they <strong>must</strong> be returned
     * in the order originally registered through the <code>add()</code>
     * method.
     *
     * @param pattern Nesting pattern to be matched
     */
    @Override
    public List<Rule> match(String namespace, String pattern) {
        // calculate the pattern of the parent
        // (if the element has one)
        String parentPattern = "";
        int lastIndex = pattern.lastIndexOf('/');

        boolean hasParent = true;

        if (lastIndex == -1) {
            // element has no parent
            hasParent = false;
        } else {
            // calculate the pattern of the parent
            parentPattern = pattern.substring(0, lastIndex);
        }

        // we keep the list of universal matches separate
        List<Rule> universalList = new ArrayList<Rule>(counter);

        // Universal all wildards ('!*')
        // These are always matched so always add them
        List<Rule> tempList = this.lookup("!*");
        if (tempList != null) {
            universalList.addAll(tempList);
        }

        // Universal exact parent match
        // need to get this now since only wildcards are considered later
        tempList = this.lookup("!" + parentPattern + "/?");
        if (tempList != null) {
            universalList.addAll(tempList);
        }

        // base behaviour means that if we certain matches, we don't continue
        // but we just have a single combined loop and so we have to set
        // a variable
        boolean ignoreBasicMatches = false;

        // see if we have an exact basic pattern match
        List<Rule> rulesList = this.lookup(pattern);

        if (rulesList != null) {
            // we have a match!
            // so ignore all basic matches from now on
            ignoreBasicMatches = true;
        } else {
            // see if we have an exact child match
            if (hasParent) {
                // matching children takes preference
                rulesList = this.lookup(parentPattern + "/?");
                if (rulesList != null) {
                    // we have a match!
                    // so ignore all basic matches from now on
                    ignoreBasicMatches = true;
                    
                } else {
                    // we don't have a match yet - so try exact ancester
                    //
                    rulesList = findExactAncesterMatch(pattern);
                    if (rulesList != null) {
                        // we have a match!
                        // so ignore all basic matches from now on
                        ignoreBasicMatches = true;
                    }
                }
            }
        }

        // OK - we're ready for the big loop!
        // Unlike the basic rules case,
        // we have to go through for all those universal rules in all cases.

        // Find the longest key, ie more discriminant
        String longKey = "";
        int longKeyLength = 0;

        for (String key : this.patterns()) {
            // find out if it's a univeral pattern
            // set a flag
            boolean isUniversal = key.startsWith("!");
            if (isUniversal) {
                // and find the underlying key
                key = key.substring(1, key.length());
            }

            // don't need to check exact matches
            boolean wildcardMatchStart = key.startsWith("*/");
            boolean wildcardMatchEnd = key.endsWith("/*");

            if (wildcardMatchStart || (isUniversal && wildcardMatchEnd)) {
                boolean parentMatched = false;
                boolean basicMatched = false;
                boolean ancesterMatched = false;

                boolean parentMatchEnd = key.endsWith("/?");

                if (parentMatchEnd) {
                    // try for a parent match
                    parentMatched = parentMatch(key, pattern, parentPattern);
                } else if (wildcardMatchEnd) {
                    // check for ancester match
                    if (wildcardMatchStart) {
                        String patternBody = key.substring(2, key.length() - 2);
                        if (pattern.endsWith(patternBody)) {
                            ancesterMatched = true;
                        } else {
                            ancesterMatched = (pattern.indexOf(patternBody + "/") > -1);
                        }
                    } else {
                        String bodyPattern = key.substring(0, key.length() - 2);
                        if (pattern.startsWith(bodyPattern)) {
                            if (pattern.length() == bodyPattern.length()) {
                                // exact match
                                ancesterMatched = true;
                            } else {
                                ancesterMatched = ( pattern.charAt(bodyPattern.length()) == '/' );
                            }
                        } else {
                            ancesterMatched = false;
                        }
                    }
                } else {
                    // try for a base match
                    basicMatched = basicMatch(key, pattern);
                }

                if (parentMatched || basicMatched || ancesterMatched) {
                    if (isUniversal) {
                        // universal rules go straight in
                        // (no longest matching rule)
                        tempList = this.lookup("!" + key);
                        if (tempList != null) {
                            universalList.addAll(tempList);
                        }
                    } else {
                        if (!ignoreBasicMatches) {
                            // ensure that all parent matches are SHORTER
                            // than rules with same level of matching.
                            //
                            // the calculations below don't work for universal
                            // matching, but we don't care because in that case
                            // this if-stmt is not entered.
                            int keyLength = key.length();
                            if (wildcardMatchStart) {
                                --keyLength;
                            }
                            if (wildcardMatchEnd) {
                                --keyLength;
                            } else if (parentMatchEnd) {
                                --keyLength;
                            }

                            if (keyLength > longKeyLength) {
                                rulesList = this.lookup(key);
                                longKey = key;
                                longKeyLength = keyLength;
                            }
                        }
                    }
                }
            }
        }

        // '*' works in practice as a default matching
        // (this is because anything is a deeper match!)
        if (rulesList == null) {
            rulesList = this.lookup("*");
        }

        // if we've matched a basic pattern, then add to the universal list
        if (rulesList != null) {
            universalList.addAll(rulesList);
        }

        // don't filter if namespace is null
        if (namespace != null) {
            // remove invalid namespaces
            Iterator<Rule> it = universalList.iterator();
            while (it.hasNext()) {
                Rule rule = it.next();
                String ns_uri = rule.getNamespaceURI();
                if (ns_uri != null && !ns_uri.equals(namespace)) {
                    it.remove();
                }
            }
        }

        // need to make sure that the collection is sort in the order
        // of addition.  We use a custom comparator for this
        Collections.sort(
                universalList,
                new Comparator<Rule>() {

                    public int compare(Rule r1, Rule r2) throws ClassCastException {
                        // Get the entry order from the map
                        Integer i1 = order.get(r1);
                        Integer i2 = order.get(r2);

                        // and use that to perform the comparison
                        if (i1 == null) {
                            if (i2 == null) {

                                return 0;

                            } else {

                                return -1;

                            }
                        } else if (i2 == null) {
                            return 1;
                        }

                        return (i1.intValue() - i2.intValue());
                    }
                });

        return universalList;
    }

    /**
     * Matching parent.
     */
    private boolean parentMatch(String key, String pattern, String parentPattern) {
        return parentPattern.endsWith(key.substring(1, key.length() - 2));
    }

    /**
     * Standard match.
     * Matches the end of the pattern to the key.
     */
    private boolean basicMatch(String key, String pattern) {
        return (pattern.equals(key.substring(2)) || pattern.endsWith(key.substring(1)));
    }

    /**
     * Finds an exact ancester match for given pattern
     */
    private List<Rule> findExactAncesterMatch(String parentPattern) {
        List<Rule> matchingRules = null;
        int lastIndex = parentPattern.length();
        while (lastIndex-- > 0) {
            lastIndex = parentPattern.lastIndexOf('/', lastIndex);
            if (lastIndex > 0) {
                matchingRules = this.lookup(parentPattern.substring(0, lastIndex) + "/*");
                if (matchingRules != null) {
                    return matchingRules;
                }
            }
        }
        return null;
    }

}

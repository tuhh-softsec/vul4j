/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/java/org/apache/commons/digester/ExtendedBaseRules.java,v 1.2 2002/01/09 20:22:49 sanders Exp $
 * $Revision: 1.2 $
 * $Date: 2002/01/09 20:22:49 $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */


package org.apache.commons.digester;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;


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
 * <p>These rules are complex and slower but offer more functionality.
 * The <code>RulesBase</code> matching set allows interaction between patterns.
 * This allows sophisticated matching schema to be created
 * but it also means that it can be hard to create and debug mappings
 * for complex schema.
 * This extension introduces <em>universal</em> versions of these patterns
 * that always act independently.</p>
 *
 * <p>Another three kinds of matching pattern are also introduced.
 * The parent matchs allow common method to be easily called for children.
 * The wildcard match allows rules to be specified for all elements.</p>
 *
 * <h4>The additional matching patterns:</h4>
 *
 * <ul>
 * <li><em>Parent Match </em> - Will match child elements of a particular
 *     kind of parent.  This is useful if a parent has a particular method
 *     to call.
 *     <ul>
 *     <li><code>"a/b/c/?"</code> matches any child whose parent matches
 *         <code>"a/b/c"</code>.  Exact parent rules take precendence over
 *         standard wildcard tail endings.</li>
 *     <li><code>"*&#47;a/b/c/?"</code> matches any child whose parent matches
 *         "*&#47;a/b/c"</code>.  The longest matching still applies to parent
 *         matches but the length excludes the '?', which effectively means
 *         that standard wildcard matches with the same level of depth are
 *         chosen in preference.</li>
 *     </ul></li>
 * <li><em>Universal Wildcard Match </em> -  Any pattern prefixed with '!'
 *     bypasses the longest matching rule.  Even if there is an exact match
 *     or a longer wildcard match,  patterns prefixed by '!' will still be
 *     tested to see if they match.  This can be used for example to specify
 *     universal construction rules.
 *     <ul>
 *     <li>Pattern <code>"!*&#47;a/b"</code> matches whenever an 'b' element
 *         is inside an 'a'.</li>
 *     <li>Pattern <code>"!a/b/?"</code> matches any child of a parent
 *         matching <code>"a/b"</code>.</li>
 *     <li>Pattern <code>"!*&#47;a/b/?"</code> matches any child of a parent
 *         matching <code>"!*&#47;a/b"</code></li>
 *    </ul></li>
 * <li><em>Wild Match</em>
 *     <ul>
 *     <li>Pattern <code>"*"</code> matches every pattern that isn't matched
 *         by any other basic rule.</li>
 *     <li>Pattern <code>"!*"</code> matches every pattern.</li>
 *     </ul></li>
 * </ul>
 *
 * <h4>Using The Extended Rules</h4>
 *
 * <p>The most important thing to remember
 * when using the extended rules is that universal
 * and non-universal patterns are completely independent.
 * Universal patterns are never effected by the addition of new patterns
 * or the removal of existing ones.
 * Non-universal patterns are never effected
 * by the addition of new <em>universal</em> patterns
 * or the removal of existing <em>universal</em> patterns.
 * As in the basic matching rules, non-universal (basic) patterns
 * <strong>can</strong> be effected
 * by the addition of new <em>non-universal</em> patterns
 * or the removal of existing <em>non-universal</em> patterns.
 * <p> This means that you can use universal patterns
 * to build up the simple parts of your structure
 * - for example defining universal creation and property setting rules.
 * More sophisticated and complex mapping will require non-universal patterns
 * and this might mean that some of the universal rules will need to be
 * replaced by a series of
 * special cases using non-universal rules.
 * But by using universal rules as your backbone,
 * these additions should not break your existing rules.</p>
 *
 * @author Robert Burrell Donkin <robertdonkin@mac.com>
 * @version $Revision: 1.2 $ $Date: 2002/01/09 20:22:49 $
 */


public class ExtendedBaseRules extends RulesBase {


    // ----------------------------------------------------- Instance Variables

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
    private Map order = new HashMap();


    // --------------------------------------------------------- Public Methods


    /**
     * Register a new Rule instance matching the specified pattern.
     *
     * @param pattern Nesting pattern to be matched for this Rule
     * @param rule Rule instance to be registered
     */
    public void add(String pattern, Rule rule) {
        super.add(pattern, rule);
        counter++;
        order.put(rule, new Integer(counter));
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
    public List match(String namespace, String pattern) {

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
        List universalList = new ArrayList(counter);

        // Universal all wildards ('!*')
        // These are always matched so always add them
        List tempList = (List) this.cache.get("!*");
        if (tempList != null) {
            universalList.addAll(tempList);
        }

        // Universal exact parent match
        // need to get this now since only wildcards are considered later
        tempList = (List) this.cache.get("!" + parentPattern + "/?");
        if (tempList != null) {
            universalList.addAll(tempList);
        }


        // base behaviour means that if we certain matches, we don't continue
        // but we just have a single combined loop and so we have to set
        // a variable
        boolean ignoreBasicMatches = false;


        // see if we have an exact basic pattern match
        List rulesList = (List) this.cache.get(pattern);
        if (rulesList != null) {
            // we have a match!
            // so ignore all basic matches from now on
            ignoreBasicMatches = true;

        } else {

            // see if we have an exact child match
            if (hasParent) {
                // matching children takes preference
                rulesList = (List) this.cache.get(parentPattern + "/?");
                if (rulesList != null) {
                    // we have a match!
                    // so ignore all basic matches from now on
                    ignoreBasicMatches = true;
                }
            }
        }


        // OK - we're ready for the big loop!
        // Unlike the basic rules case,
        // we have to go through for all those universal rules in all cases.

        // Find the longest key, ie more discriminant
        String longKey = "";
        Iterator keys = this.cache.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();

            // find out if it's a univeral pattern
            // set a flag
            boolean isUniversal = key.startsWith("!");
            if (isUniversal) {
                // and find the underlying key
                key = key.substring(1, key.length());
            }


            // don't need to check exact matches
            if (key.startsWith("*/")) {

                boolean parentMatched = false;
                boolean basicMatched = false;
                if (key.endsWith("/?")) {
                    // try for a parent match
                    parentMatched = parentMatch(key, pattern, parentPattern);

                } else {
                    // try for a base match
                    basicMatched = basicMatch(key, pattern);
                }

                if (parentMatched || basicMatched) {
                    if (isUniversal) {
                        // universal rules go straight in
                        // (no longest matching rule)
                        tempList = (List) this.cache.get("!" + key);
                        if (tempList != null) {
                            universalList.addAll(tempList);
                        }

                    } else {
                        if (!ignoreBasicMatches) {
                            // ensure that all parent matches are SHORTER
                            // than rules with same level of matching
                            int keyLength = key.length();
                            if (parentMatched) {
                                keyLength--;
                            }


                            if (keyLength > longKey.length()) {
                                rulesList = (List) this.cache.get(key);
                                longKey = key;
                            }
                        }
                    }
                }
            }
        }


        // '*' works in practice as a default matching
        // (this is because anything is a deeper match!)
        if (rulesList == null) {
            rulesList = (List) this.cache.get("*");
        }

        // if we've matched a basic pattern, then add to the universal list
        if (rulesList != null) {
            universalList.addAll(rulesList);
        }


        // don't filter if namespace is null
        if (namespace != null) {
            // remove invalid namespaces
            Iterator it = universalList.iterator();
            while (it.hasNext()) {
                Rule rule = (Rule) it.next();
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
                new Comparator() {

                    public int compare(Object o1, Object o2) throws ClassCastException {
                        // Get the entry order from the map
                        Integer i1 = (Integer) order.get(o1);
                        Integer i2 = (Integer) order.get(o2);

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
        return pattern.endsWith(key.substring(1));
    }

}

/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/java/org/apache/commons/digester/RegexRules.java,v 1.2 2003/04/16 11:23:50 jstrachan Exp $
 * $Revision: 1.2 $
 * $Date: 2003/04/16 11:23:50 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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
import java.util.Iterator;
import java.util.List;

/**
 * <p>Rules implementation that uses regular expression matching for paths.</p>
 *
 * <p>The regex implementation is pluggable, allowing different strategies to be used.
 * The basic way that this class work does not vary.
 * All patterns are tested to see if they match the path using the regex matcher.
 * All those that do are return in the order which the rules were added.</p>
 *
 * @author Robert Burrell Donkin
 * @version $Revision: 1.2 $ $Date: 2003/04/16 11:23:50 $
 */

public class RegexRules extends AbstractRulesImpl {

    // --------------------------------------------------------- Fields
    
    /** All registered <code>Rule</code>'s  */
    private ArrayList registeredRules = new ArrayList();
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
    protected void registerRule(String pattern, Rule rule) {
        registeredRules.add(new RegisteredRule(pattern, rule));
    }

    /**
     * Clear all existing Rule instance registrations.
     */
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
    public List match(String namespaceURI, String pattern) {
        //
        // not a particularly quick implementation
        // regex is probably going to be slower than string equality
        // so probably should have a set of strings
        // and test each only once
        //
        // XXX FIX ME - Time And Optimize
        //
        ArrayList rules = new ArrayList(registeredRules.size());
        Iterator it = registeredRules.iterator();
        while (it.hasNext()) {
            RegisteredRule next = (RegisteredRule) it.next();
            if (matcher.match(pattern, next.pattern)) {
                rules.add(next.rule);
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
    public List rules() {
        ArrayList rules = new ArrayList(registeredRules.size());
        Iterator it = registeredRules.iterator();
        while (it.hasNext()) {
            rules.add(((RegisteredRule) it.next()).rule);
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

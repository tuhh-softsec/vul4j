/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/test/org/apache/commons/digester/RegexRulesTestCase.java,v 1.1 2003/04/02 19:04:58 rdonkin Exp $
 * $Revision: 1.1 $
 * $Date: 2003/04/02 19:04:58 $
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

import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.logging.impl.SimpleLog;

/**
 * Test case for RegexRules
 *
 * @author Robert Burrell Donkin
 * @version $Revision: 1.1 $ $Date: 2003/04/02 19:04:58 $
 */

public class RegexRulesTestCase extends TestCase {
    
    /** Base constructor */
    public RegexRulesTestCase(String name) {
        super(name);
    }
    
    /** Test regex that matches everything */
    public void testMatchAll() {
        // set up which should match every rule
        RegexRules rules = new RegexRules(
            new RegexMatcher() {
                public boolean match(String pathPattern, String rulePattern) {
                    return true;
                }
            });
        
        rules.add("/a/b/b", new TestRule("alpha"));
        rules.add("/a/d", new TestRule("beta"));
        rules.add("/b", new TestRule("gamma"));
        
        // now test a few patterns
        // check that all are return in the order which they were added
        List matches = rules.match("", "x/g/e");
        assertEquals("Wrong number of rules returned (1)", 3, matches.size());
        assertEquals("Rule Out Of Order (1)", "alpha", ((TestRule) matches.get(0)).getIdentifier());
        assertEquals("Rule Out Of Order (2)", "beta", ((TestRule) matches.get(1)).getIdentifier());
        assertEquals("Rule Out Of Order (3)", "gamma", ((TestRule) matches.get(2)).getIdentifier());
        
        matches = rules.match("", "/a");
        assertEquals("Wrong number of rules returned (2)", 3, matches.size());
        assertEquals("Rule Out Of Order (4)", "alpha", ((TestRule) matches.get(0)).getIdentifier());
        assertEquals("Rule Out Of Order (5)", "beta", ((TestRule) matches.get(1)).getIdentifier());
        assertEquals("Rule Out Of Order (6)", "gamma", ((TestRule) matches.get(2)).getIdentifier());        
    }
    
    /** Test regex matcher that matches nothing */
    public void testMatchNothing() {
        // set up which should match every rule
        RegexRules rules = new RegexRules(
            new RegexMatcher() {
                public boolean match(String pathPattern, String rulePattern) {
                    return false;
                }
            });
        
        rules.add("/b/c/f", new TestRule("alpha"));
        rules.add("/c/f", new TestRule("beta"));
        rules.add("/b", new TestRule("gamma"));
        
        // now test a few patterns
        // check that all are return in the order which they were added
        List matches = rules.match("", "/b/c");
        assertEquals("Wrong number of rules returned (1)", 0, matches.size());
        
        matches = rules.match("", "/b/c/f");
        assertEquals("Wrong number of rules returned (2)", 0, matches.size());
    }

    /** Test a mixed regex - in other words, one that sometimes returns true and sometimes false */
    public void testMatchMixed() {
        // set up which should match every rule
        RegexRules rules = new RegexRules(
            new RegexMatcher() {
                public boolean match(String pathPattern, String rulePattern) {
                    return (rulePattern.equals("/match/me"));
                }
            });
        
        rules.add("/match", new TestRule("alpha"));
        rules.add("/match/me", new TestRule("beta"));
        rules.add("/match", new TestRule("gamma"));
        
        // now test a few patterns
        // check that all are return in the order which they were added
        List matches = rules.match("", "/match");
        assertEquals("Wrong number of rules returned (1)", 1, matches.size());
        assertEquals("Wrong Rule (1)", "beta", ((TestRule) matches.get(0)).getIdentifier());
        
        matches = rules.match("", "/a/match");
        assertEquals("Wrong Rule (2)", "beta", ((TestRule) matches.get(0)).getIdentifier());
    }
        
    /** Test rules and clear methods */
    public void testClear() {
        // set up which should match every rule
        RegexRules rules = new RegexRules(
            new RegexMatcher() {
                public boolean match(String pathPattern, String rulePattern) {
                    return true;
                }
            });
        
        rules.add("/abba", new TestRule("alpha"));
        rules.add("/ad/ma", new TestRule("beta"));
        rules.add("/gamma", new TestRule("gamma"));
        
        // check that rules returns all rules in the order which they were added
        List matches = rules.rules();
        assertEquals("Wrong number of rules returned (1)", 3, matches.size());
        assertEquals("Rule Out Of Order (1)", "alpha", ((TestRule) matches.get(0)).getIdentifier());
        assertEquals("Rule Out Of Order (2)", "beta", ((TestRule) matches.get(1)).getIdentifier());
        assertEquals("Rule Out Of Order (3)", "gamma", ((TestRule) matches.get(2)).getIdentifier());
        
        matches = rules.match("", "/eggs");
        assertEquals("Wrong number of rules returned (2)", 3, matches.size());
        assertEquals("Rule Out Of Order (4)", "alpha", ((TestRule) matches.get(0)).getIdentifier());
        assertEquals("Rule Out Of Order (5)", "beta", ((TestRule) matches.get(1)).getIdentifier());
        assertEquals("Rule Out Of Order (6)", "gamma", ((TestRule) matches.get(2)).getIdentifier());
        
        rules.clear();
        matches = rules.rules();
        assertEquals("Wrong number of rules returned (3)", 0, matches.size());
        
        matches = rules.match("", "/eggs");
        assertEquals("Wrong number of rules returned (4)", 0, matches.size());
    }
    
    public void testSimpleRegexMatch() {
        
        SimpleRegexMatcher matcher = new SimpleRegexMatcher();
        
//        SimpleLog log = new SimpleLog("{testSimpleRegexMatch:SimpleRegexMatcher]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
        
        
        assertEquals(
                "Simple Regex Match '/alpha/beta/gamma' to '/alpha/beta/gamma' ", 
                true, 
                matcher.match("/alpha/beta/gamma", "/alpha/beta/gamma"));
        assertEquals(
                "Simple Regex Match '/alpha/beta/gamma' to '/alpha/beta/gamma/epsilon' ", 
                false, 
                matcher.match("/alpha/beta/gamma", "/alpha/beta/gamma/epsilon"));
        assertEquals(
                "Simple Regex Match '/alpha/beta/gamma' to '/alpha/*' ", 
                true, 
                matcher.match("/alpha/beta/gamma", "/alpha/*"));
        assertEquals(
                "Simple Regex Match '/alpha/beta/gamma' to '/alpha/*/gamma' ", 
                true, 
                matcher.match("/alpha/beta/gamma", "/alpha/*/gamma"));
        assertEquals(
                "Simple Regex Match '/alpha/beta/gamma' to '/alpha/*me' ", 
                false, 
                matcher.match("/alpha/beta/gamma", "/alpha/*me"));
        assertEquals(
                "Simple Regex Match '/alpha/beta/gamma' to '*/beta/gamma' ", 
                true, 
                matcher.match("/alpha/beta/gamma", "*/beta/gamma"));
        assertEquals(
                "Simple Regex Match '/alpha/beta/gamma' to '*/alpha/beta/gamma' ", 
                true, 
                matcher.match("/alpha/beta/gamma", "*/alpha/beta/gamma"));
        assertEquals(
                "Simple Regex Match '/alpha/beta/gamma' to '*/bet/gamma' ", 
                false, 
                matcher.match("/alpha/beta/gamma", "*/bet/gamma"));
        assertEquals(
                "Simple Regex Match '/alpha/beta/gamma' to 'alph?/beta/gamma' ", 
                true, 
                matcher.match("/alpha/beta/gamma", "/alph?/beta/gamma"));
        assertEquals(
                "Simple Regex Match '/alpha/beta/gamma' to '/?lpha/beta/gamma' ", 
                true, 
                matcher.match("/alpha/beta/gamma", "/?lpha/beta/gamma"));
        assertEquals(
                "Simple Regex Match '/alpha/beta/gamma' to '/alpha/?beta/gamma' ", 
                false, 
                matcher.match("/alpha/beta/gamma", "/alpha/?beta/gamma"));
        assertEquals(
                "Simple Regex Match '/alpha/beta/gamma' to '/alpha/?eta/*' ", 
                true, 
                matcher.match("/alpha/beta/gamma", "/alpha/?eta/*"));
        assertEquals(
                "Simple Regex Match '/alpha/beta/gamma' to '/alpha/?eta/*e' ", 
                false, 
                matcher.match("/alpha/beta/gamma", "/alpha/?eta/*e"));
        assertEquals(
                "Simple Regex Match '/alpha/beta/gamma' to '*/?et?/?amma' ", 
                true, 
                matcher.match("/alpha/beta/gamma", "*/?et?/?amma"));
        assertEquals(
                "Simple Regex Match '/alpha/beta/gamma/beta/epsilon/beta/gamma/epsilon' to "
                + " '*/beta/gamma/?p*n' ", 
                true, 
                matcher.match("/alpha/beta/gamma/beta/epsilon/beta/gamma/epsilon", "*/beta/gamma/?p*n"));     
        assertEquals(
                "Simple Regex Match '/alpha/beta/gamma/beta/epsilon/beta/gamma/epsilon' to "
                + " '*/beta/gamma/?p*no' ", 
                false, 
                matcher.match("/alpha/beta/gamma", "*/beta/gamma/?p*no"));  
    }
}

/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/java/org/apache/commons/digester/SimpleRegexMatcher.java,v 1.1 2003/04/02 19:04:42 rdonkin Exp $
 * $Revision: 1.1 $
 * $Date: 2003/04/02 19:04:42 $
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>Simple regex pattern matching algorithm.</p>
 * 
 * <p>This uses just two wildcards:
 * <ul>
 * 	<li><code>*</code> matches any sequence of none, one or more characters
 * 	<li><code>?</code> matches any one character 
 * </ul>
 * Escaping these wildcards is not supported .</p>
 *
 * @author Robert Burrell Donkin
 * @version $Revision: 1.1 $ $Date: 2003/04/02 19:04:42 $
 */

public class SimpleRegexMatcher extends RegexMatcher {
    
    // --------------------------------------------------------- Fields
    
    /** Default log (class wide) */
    private static final Log baseLog = LogFactory.getLog(SimpleRegexMatcher.class);
    
    /** Custom log (can be set per object) */
    private Log log = baseLog;
    
    // --------------------------------------------------------- Properties
    
    /** 
     * Gets the <code>Log</code> implementation.
     */
    public Log getLog() {
        return log;
    }
    
    /**
     * Sets the current <code>Log</code> implementation used by this class.
     */
    public void setLog(Log log) {
        this.log = log;
    }
    
    // --------------------------------------------------------- Public Methods
    
    /** 
     * Matches using simple regex algorithm.
     * 
     *
     * @param basePattern the standard digester path representing the element
     * @param regexPattern the regex pattern the path will be tested against
     * @return true if the given pattern matches the given path
     */
    public boolean match(String basePattern, String regexPattern) {
        // check for nulls
        if (basePattern == null || regexPattern == null) {
            return false;
        }
        return match(basePattern, regexPattern, 0, 0);
    }
    
    // --------------------------------------------------------- Implementations Methods
    
    /**
     * Implementation of regex matching algorithm.
     * This calls itself recursively.
     */
    private boolean match(String basePattern, String regexPattern, int baseAt, int regexAt) {
        if (log.isTraceEnabled()) {
            log.trace("Base: " + basePattern);
            log.trace("Regex: " + regexPattern);
            log.trace("Base@" + baseAt);
            log.trace("Regex@" + regexAt);
        }
        
        // check bounds
        if (regexAt >= regexPattern.length()) {
            // maybe we've got a match
            if (baseAt >= basePattern.length()) {
                // ok!
                return true;
            }
            // run out early
            return false;
            
        } else {
            if (baseAt >= basePattern.length()) {
                // run out early
                return false;
            }
        }
        
        // ok both within bounds
        char regexCurrent = regexPattern.charAt(regexAt);
        switch (regexCurrent) {
            case '*':
                // this is the tricky case
                // check for terminal 
                if (++regexAt >= regexPattern.length()) {
                    // this matches anything let - so return true
                    return true;
                }
                // go through every subsequent apperance of the next character
                // and so if the rest of the regex matches
                char nextRegex = regexPattern.charAt(regexAt);
                if (log.isTraceEnabled()) {
                    log.trace("Searching for next '" + nextRegex + "' char");
                }
                int nextMatch = basePattern.indexOf(nextRegex, baseAt);
                while (nextMatch != -1) {
                    if (log.isTraceEnabled()) {
                        log.trace("Trying '*' match@" + nextMatch);
                    }
                    if (match(basePattern, regexPattern, nextMatch, regexAt)) {
                        return true;
                    }
                    nextMatch = basePattern.indexOf(nextRegex, nextMatch + 1);
                }
                log.trace("No matches found.");
                return false;
                
            case '?':
                // this matches anything
                return match(basePattern, regexPattern, ++baseAt, ++regexAt);
            
            default:
                if (log.isTraceEnabled()) {
                    log.trace("Camparing " + regexCurrent + " to " + basePattern.charAt(baseAt));
                }
                if (regexCurrent == basePattern.charAt(baseAt)) {
                    // still got more to go
                    return match(basePattern, regexPattern, ++baseAt, ++regexAt);
                }
                return false;
        }
    }
}

/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/test/org/apache/commons/digester/WDRulesWrapperTestCase.java,v 1.1 2003/04/28 17:51:32 rdonkin Exp $
 * $Revision: 1.1 $
 * $Date: 2003/04/28 17:51:32 $
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

import junit.framework.TestCase;

/**
 * Test case for WithDefaultsRulesWrapper
 *
 * @author Robert Burrell Donkin
 * @version $Revision: 1.1 $ $Date: 2003/04/28 17:51:32 $
 */

public class WDRulesWrapperTestCase extends TestCase {
    
    /** Base constructor */
    public WDRulesWrapperTestCase(String name) {
        super(name);
    }
    
    public void testClear() {
        // test clear wrapped
        WithDefaultsRulesWrapper rules = new WithDefaultsRulesWrapper(new RulesBase());
        rules.add("alpha", new TestRule("Tom"));
        rules.add("alpha", new TestRule("Dick"));
        rules.add("alpha", new TestRule("Harry"));
        
        assertNotNull("Rules should not be null",  rules.rules());
        assertEquals("Wrong number of rules registered (1)", 3 , rules.rules().size());
        rules.clear();
        assertEquals("Clear Failed (1)", 0 , rules.rules().size());
        
        // mixed
        rules.add("alpha", new TestRule("Tom"));
        rules.add("alpha", new TestRule("Dick"));
        rules.add("alpha", new TestRule("Harry"));
        rules.addDefault(new TestRule("Roger"));
        assertEquals("Wrong number of rules registered (2)", 4 , rules.rules().size());
        rules.clear();
        assertEquals("Clear Failed (2)", 0 , rules.rules().size());
        
        rules.addDefault(new TestRule("Roger"));
        assertEquals("Wrong number of rules registered (3)", 1 , rules.rules().size());
        rules.clear();
        assertEquals("Clear Failed (3)", 0 , rules.rules().size());
    }
    
    public void testRules() {
        // test rules
        WithDefaultsRulesWrapper rules = new WithDefaultsRulesWrapper(new RulesBase());
        rules.add("alpha", new TestRule("Tom"));
        rules.add("alpha", new TestRule("Dick"));
        rules.addDefault(new TestRule("Roger"));
        rules.add("alpha", new TestRule("Harry"));
        
        assertNotNull("Rules should not be null",  rules.rules());
        assertEquals("Wrong order (1)", "Tom" , ((TestRule) rules.rules().get(0)).getIdentifier());
        assertEquals("Wrong order (2)", "Dick" , ((TestRule) rules.rules().get(1)).getIdentifier());
        assertEquals("Wrong order (3)", "Roger" , ((TestRule) rules.rules().get(2)).getIdentifier());
        assertEquals("Wrong order (4)", "Harry" , ((TestRule) rules.rules().get(3)).getIdentifier());
    }
    
    public void testMatch() {
        // test no defaults
        WithDefaultsRulesWrapper rules = new WithDefaultsRulesWrapper(new RulesBase());
        rules.add("alpha", new TestRule("Tom"));
        rules.add("alpha", new TestRule("Dick"));
        rules.add("alpha", new TestRule("Harry"));
        rules.addDefault(new TestRule("Roger"));
        rules.addDefault(new TestRule("Rabbit"));
        
        List matches = rules.match("", "alpha");
        assertEquals("Wrong size (1)", 3 , matches.size());
        assertEquals("Wrong order (1)", "Tom" , ((TestRule) matches.get(0)).getIdentifier());
        assertEquals("Wrong order (2)", "Dick" , ((TestRule) matches.get(1)).getIdentifier());
        assertEquals("Wrong order (3)", "Harry" , ((TestRule) matches.get(2)).getIdentifier());
        
        matches = rules.match("", "not-alpha");
        assertEquals("Wrong size (2)", 2 , matches.size());
        assertEquals("Wrong order (4)", "Roger" , ((TestRule) matches.get(0)).getIdentifier());
        assertEquals("Wrong order (5)", "Rabbit" , ((TestRule) matches.get(1)).getIdentifier());
    }
}

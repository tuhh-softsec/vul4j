/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/test/org/apache/commons/digester/plugins/TestAll.java,v 1.2 2003/10/05 15:30:03 rdonkin Exp $
 * $Revision: 1.2 $
 * $Date: 2003/10/05 15:30:03 $
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache" nor may "Apache" appear in their names without prior 
 *    written permission of the Apache Software Foundation.
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
package org.apache.commons.digester.plugins;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.InputStream;
import java.io.IOException;

/**
 * Entry point for all plugins package tests.
 * 
 * @author Simon Kitching
 */
public class TestAll extends TestCase {
    public TestAll(String testName) {
        super(testName);
    }

    // MORE TESTS REQUIRED::
    // test that problem is detected if rules object not PluginRules
    // test that problem is detected if wildcard pattern used
    // test that problem is detected if rule mounted with multiple patterns
    // test that problem is detected if specified class doesn't descend
    // from required base class.
    // test scenario where bodytext of actual plugin element is accessed
    
    // test rules in resource
    // test rules in explicit file
    // test autosetdefaults on/off


    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(TestInline.suite());
        suite.addTest(TestDelegate.suite());
        suite.addTest(TestDeclaration.suite());
        suite.addTest(TestDefaultPlugin.suite());
        suite.addTest(TestLocalRules.suite());
        suite.addTest(TestRuleInfo.suite());
        suite.addTest(TestRecursion.suite());
        return suite;
    }
        
    public static void main(String args[]) {
        String[] testCaseName = { TestAll.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }
    
    // ------------------------------------------------ Utility Support Methods

    /**
     * Return an appropriate InputStream for the specified test file (which
     * must be inside our current package.
     *
     * @param caller is always "this" for the calling object.
     * @param name is the test file we want
     *
     * @exception IOException if an input/output error occurs
     */
    public static InputStream getInputStream(Object caller, String name) 
    throws IOException {
        return (caller.getClass().getResourceAsStream
                ("/org/apache/commons/digester/plugins/" + name));
    }
}

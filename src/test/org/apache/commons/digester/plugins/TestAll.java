/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        suite.addTest(TestConfigurablePluginAttributes.suite());
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

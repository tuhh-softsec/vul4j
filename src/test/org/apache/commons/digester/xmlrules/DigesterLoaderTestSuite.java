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


package org.apache.commons.digester.xmlrules;


import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Tests loading Digester rules from an XML file.
 *
 * @author Scott Sanders
 */

public class DigesterLoaderTestSuite extends TestCase {

    public DigesterLoaderTestSuite(java.lang.String testName) {
        super(testName);
    }

    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static junit.framework.Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(DigesterLoaderTest.class);
        suite.addTestSuite(DigesterPatternStackTest.class);
        suite.addTestSuite(DigesterLoaderRulesTest.class);
        suite.addTestSuite(IncludeTest.class);

        return suite;
    }
}
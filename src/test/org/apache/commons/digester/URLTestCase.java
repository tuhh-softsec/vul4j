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

import java.net.URL;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * <p>Unit tests that exercise the new (in 1.8) methods for passing in
 * <code>URL</code> arguments instead of strings.</p>
 */
public class URLTestCase extends TestCase {
    

    // ------------------------------------------------------------ Constructors


    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public URLTestCase(String name) {

        super(name);

    }


    // ----------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    @Override
    public void setUp() {

        digester = new Digester();

    }


    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {

        return (new TestSuite(URLTestCase.class));

    }


    /**
     * Tear down instance variables required by this test case.
     */
    @Override
    public void tearDown() {

        digester = null;

    }


    // ------------------------------------------------------ Manifest Constants


    /**
     * <p>Public identifier of the Digester Rules DTD.</p>
     */
    private static final String DIGESTER_RULES_PUBLIC_ID =
            "-//Jakarta Apache //DTD digester-rules XML V1.0//EN";


    /**
     * <p>System identifier of the Digester Rules DTD.</p>
     */
    private static final String DIGESTER_RULES_SYSTEM_ID =
            "/org/apache/commons/digester/xmlrules/digester-rules.dtd";


    /**
     * <p>System identifier for the Digester Rules file that we will parse.</p>
     */
    private static final String TEST_INPUT_SYSTEM_ID =
            "/org/apache/commons/digester/xmlrules/test-call-param-rules.xml";


    // ------------------------------------------------------ Instance Variables


    /**
     * <p>The <code>Digester</code> instance under test.</p>
     */
    private Digester digester = null;



    // ------------------------------------------------------------ Test Methods


    // Test a pristine instance
    public void testPristine() {

        assertNotNull(digester);

    }


    // Test parsing a resource, using a registered DTD, both passed with URLs
    public void testResource() throws Exception {

        // Register the Digester Rules DTD
        URL dtd = URLTestCase.class.getResource(DIGESTER_RULES_SYSTEM_ID);
        assertNotNull(dtd);
        digester.register(DIGESTER_RULES_PUBLIC_ID, dtd);

        // Parse one of the existing test resources twice with
        // the same Digester instance
        URL xml = URLTestCase.class.getResource(TEST_INPUT_SYSTEM_ID);
        assertNotNull(xml);
        digester.parse(xml);
        digester.parse(xml);

    }


}

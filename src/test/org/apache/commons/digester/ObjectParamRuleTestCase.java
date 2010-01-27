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


import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xml.sax.SAXException;


/**
 * <p>Tests for the <code>ObjectParamRuleTestCase</code>
 *
 * @author Mark Huisman
 */
public class ObjectParamRuleTestCase extends TestCase {


    // ----------------------------------------------------- Instance Variables


    /**
     * The digester instance we will be processing.
     */
    protected Digester digester = null;


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public ObjectParamRuleTestCase(String name) {

        super(name);

    }


    public static void main(String[] args){

        // so we can run standalone
        junit.textui.TestRunner.run(suite());

    }


    // --------------------------------------------------- Overall Test Methods


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

        return (new TestSuite(ObjectParamRuleTestCase.class));

    }


    /**
     * Tear down instance variables required by this test case.
     */
    @Override
    public void tearDown() {

        digester = null;

    }



    // ------------------------------------------------ Individual Test Methods

    private StringBuffer sb = new StringBuffer().
        append("<arraylist><A/><B/><C/><D desc=\"the fourth\"/><E/></arraylist>");


    /**
     * Test method calls with the ObjectParamRule rule.  It should be possible to
     * pass any subclass of Object as a parameter, provided that either the element
     * or the element + attribute has been matched.
     */
    public void testBasic() throws SAXException, IOException {

        // Configure the digester as required
        digester.addObjectCreate("arraylist", ArrayList.class);

        // Test adding a variety of objects
        digester.addCallMethod("arraylist/A", "add", 1);
        ObjectParamRule opr = new ObjectParamRule(0, new Integer(-9));
        digester.addRule("arraylist/A", opr);
        digester.addCallMethod("arraylist/B", "add", 1);
        opr = new ObjectParamRule(0, new Float(3.14159));
        digester.addRule("arraylist/B", opr);
        digester.addCallMethod("arraylist/C", "add", 1);
        opr = new ObjectParamRule(0, new Long(999999999));
        digester.addRule("arraylist/C", opr);
        digester.addCallMethod("arraylist/D", "add", 1);
        opr = new ObjectParamRule(0, "desc", new String("foobarbazbing"));
        digester.addRule("arraylist/D", opr);
        // note that this will add a null parameter to the method call and will
        // not be added to the arraylist.
        digester.addCallMethod("arraylist/E", "add", 1);
        opr = new ObjectParamRule(0, "nonexistentattribute", new String("ignore"));
        digester.addRule("arraylist/E", opr);

        //Parse it and obtain the ArrayList
        ArrayList<?> al = (ArrayList<?>)digester.parse(new StringReader(sb.toString()));
        assertNotNull(al);
        assertEquals(al.size(), 4);
        assertTrue(al.contains(new Integer(-9)));
        assertTrue(al.contains(new Float(3.14159)));
        assertTrue(al.contains(new Long(999999999)));
        assertTrue(al.contains(new String("foobarbazbing")));
        assertTrue(!(al.contains(new String("ignore"))));
    }

}


/*
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
        ArrayList al = (ArrayList)digester.parse(new StringReader(sb.toString()));
        this.assertNotNull(al);
        this.assertEquals(al.size(), 4);
        this.assertTrue(al.contains(new Integer(-9)));
        this.assertTrue(al.contains(new Float(3.14159)));
        this.assertTrue(al.contains(new Long(999999999)));
        this.assertTrue(al.contains(new String("foobarbazbing")));
        this.assertTrue(!(al.contains(new String("ignore"))));
    }

}


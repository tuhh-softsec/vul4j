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
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xml.sax.SAXException;


/**
 * <p> Test case for <code>SetPropertyRule</code>.</p>
 */
public class SetPropertyRuleTestCase extends TestCase {


    // ----------------------------------------------------- Instance Variables

    /**
     * Simple test xml document used in the tests.
     */
    protected final static String TEST_XML_1 =
        "<?xml version='1.0'?><root>" +
        "<set name='alpha' value='ALPHA VALUE'/>" +
        "<set name='beta' value='BETA VALUE'/>" +
        "<set name='delta' value='DELTA VALUE'/>" +
        "</root>";

    /**
     * Simple test xml document used in the tests.
     */
    protected final static String TEST_XML_2 =
        "<?xml version='1.0'?><root>" +
        "<set name='unknown' value='UNKNOWN VALUE'/>" +
        "</root>";


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
    public SetPropertyRuleTestCase(String name) {

        super(name);

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

        return (new TestSuite(SetPropertyRuleTestCase.class));

    }


    /**
     * Tear down instance variables required by this test case.
     */
    public void tearDown() {

        digester = null;

    }



    // ------------------------------------------------ Individual Test Methods


    /**
     * Positive test for SetPropertyRule.
     */
    public void testPositive() throws Exception {

        // Set up the rules we need
        digester.addObjectCreate("root",
                                 "org.apache.commons.digester.SimpleTestBean");
        digester.addSetProperty("root/set", "name", "value");

        // Parse the input
        SimpleTestBean bean =
            (SimpleTestBean) digester.parse(xmlTestReader(TEST_XML_1));

        // Check that the properties were set correctly
        assertEquals("alpha property set",
                     "ALPHA VALUE",
                     bean.getAlpha());
        assertEquals("beta property set",
                     "BETA VALUE",
                     bean.getBeta());
        assertNull("gamma property not set",
                   bean.getGamma());
        assertEquals("delta property set",
                     "DELTA VALUE",
                     bean.getDeltaValue());

    }


    /**
     * Negative test for SetPropertyRule.
     */
    public void testNegative() {

        // Set up the rules we need
        digester.addObjectCreate("root",
                                 "org.apache.commons.digester.SimpleTestBean");
        digester.addSetProperty("root/set", "name", "value");

        // Parse the input (should fail)
        try {
            SimpleTestBean bean =
                (SimpleTestBean) digester.parse(xmlTestReader(TEST_XML_2));
            fail("Should have thrown NoSuchMethodException");
        } catch (Exception e) {
            if (e instanceof NoSuchMethodException) {
                ; // Expected result
            } else if (e instanceof InvocationTargetException) {
                Throwable t =
                    ((InvocationTargetException) e).getTargetException();
                if (t instanceof NoSuchMethodException) {
                    ; // Expected result
                } else {
                    fail("Should have thrown ITE->NoSuchMethodException, threw " + t);
                }
            } else if (e instanceof SAXException) {
                Exception ee = ((SAXException) e).getException();
                if (ee != null) {
                    if (ee instanceof NoSuchMethodException) {
                        ; // Expected result
                    } else {
                        fail("Should have thrown SE->NoSuchMethodException, threw " + ee);
                    }
                } else {
                    fail("Should have thrown NoSuchMethodException, threw " +
                         e.getClass().getName());
                }
            } else {
                fail("Should have thrown NoSuchMethodException, threw " + e);
            }
        }

    }


    /**
     * Get input stream from specified String containing XML data.
     */
    private Reader xmlTestReader(String xml) throws IOException {
        return new StringReader(xml);
    }

}



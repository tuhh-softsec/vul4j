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
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xml.sax.SAXException;


/**
 * <p> Test case for <code>BeanPropertySetterRule</code>.
 * This contains tests for the main applications of the rule
 * and two more general tests of digester functionality used by this rule.
 */
public class BeanPropertySetterRuleTestCase extends TestCase {


    // ----------------------------------------------------- Instance Variables

    /**
     * Simple test xml document used in the tests.
     */
    protected final static String TEST_XML =
        "<?xml version='1.0'?>" +
        "<root>ROOT BODY" +
        "<alpha>ALPHA BODY</alpha>" +
        "<beta>BETA BODY</beta>" +
        "<gamma>GAMMA BODY</gamma>" +
        "<delta>DELTA BODY</delta>" +
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
    public BeanPropertySetterRuleTestCase(String name) {

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

        return (new TestSuite(BeanPropertySetterRuleTestCase.class));

    }


    /**
     * Tear down instance variables required by this test case.
     */
    public void tearDown() {

        digester = null;

    }



    // ------------------------------------------------ Individual Test Methods


    /**
     * This is a general digester test but it fits into here pretty well.
     * This tests that the rule calling order is properly enforced.
     */
    public void testDigesterRuleCallOrder() throws SAXException, IOException {

        List callOrder = new ArrayList();

        // use the standard rules
        digester.setRules(new RulesBase());

        // add first test rule
        TestRule firstRule = new TestRule("first");
        firstRule.setOrder(callOrder);
        digester.addRule("root/alpha", firstRule);

        // add second test rule
        TestRule secondRule = new TestRule("second");
        secondRule.setOrder(callOrder);
        digester.addRule("root/alpha", secondRule);

        // add third test rule
        TestRule thirdRule = new TestRule("third");
        thirdRule.setOrder(callOrder);
        digester.addRule("root/alpha", thirdRule);


        digester.parse(xmlTestReader());

        // we should have nine entries in our list of calls

        assertEquals(
                "Nine calls should have been made.",
                9,
                callOrder.size());

        // begin should be called in the order added
        assertEquals(
                "First rule begin not called first.",
                "first",
                ((TestRule) callOrder.get(0)).getIdentifier());

        assertEquals(
                "Second rule begin not called second.",
                "second",
                ((TestRule) callOrder.get(1)).getIdentifier());

        assertEquals(
                "Third rule begin not called third.",
                "third",
                ((TestRule) callOrder.get(2)).getIdentifier());

        // body text should be called in the order added
        assertEquals(
                "First rule body text not called first.",
                "first",
                ((TestRule) callOrder.get(3)).getIdentifier());

        assertEquals(
                "Second rule body text not called second.",
                "second",
                ((TestRule) callOrder.get(4)).getIdentifier());

        assertEquals(
                "Third rule body text not called third.",
                "third",
                ((TestRule) callOrder.get(5)).getIdentifier());

        // end should be called in reverse order
        assertEquals(
                "Third rule end not called first.",
                "third",
                ((TestRule) callOrder.get(6)).getIdentifier());

        assertEquals(
                "Second rule end not called second.",
                "second",
                ((TestRule) callOrder.get(7)).getIdentifier());

        assertEquals(
                "First rule end not called third.",
                "first",
                ((TestRule) callOrder.get(8)).getIdentifier());


    }


    /**
     * This is a general digester test but it fits into here pretty well.
     * This tests that the body text stack is functioning correctly.
     */
    public void testDigesterBodyTextStack() throws SAXException, IOException {

        // use the standard rules
        digester.setRules(new RulesBase());

        // add test rule to catch body text
        TestRule rootRule = new TestRule("root");
        digester.addRule("root", rootRule);

        // add test rule to catch body text
        TestRule alphaRule = new TestRule("root/alpha");
        digester.addRule("root/alpha", alphaRule);

        // add test rule to catch body text
        TestRule betaRule = new TestRule("root/beta");
        digester.addRule("root/beta", betaRule);

        // add test rule to catch body text
        TestRule gammaRule = new TestRule("root/gamma");
        digester.addRule("root/gamma", gammaRule);

        digester.parse(xmlTestReader());

        assertEquals(
                "Root body text not set correct.",
                "ROOT BODY",
                rootRule.getBodyText());

        assertEquals(
                "Alpha body text not set correct.",
                "ALPHA BODY",
                alphaRule.getBodyText());

        assertEquals(
                "Beta body text not set correct.",
                "BETA BODY",
                betaRule.getBodyText());

        assertEquals(
                "Gamma body text not set correct.",
                "GAMMA BODY",
                gammaRule.getBodyText());

    }


    /**
     * Test that you can successfully set a given property
     */
    public void testSetGivenProperty() throws SAXException, IOException {

        // use the standard rules
        digester.setRules(new RulesBase());

        // going to be setting properties on a SimpleTestBean
        digester.addObjectCreate("root",
                                 "org.apache.commons.digester.SimpleTestBean");

        // we'll set property alpha with the body text of root
        digester.addRule("root", new BeanPropertySetterRule("alpha"));

        // we'll set property beta with the body text of child element alpha
        digester.addRule("root/alpha", new BeanPropertySetterRule("beta"));

        // we'll leave property gamma alone

        // we'll set property delta (a write-only property) also
        digester.addRule("root/delta", new BeanPropertySetterRule("delta"));

        SimpleTestBean bean = (SimpleTestBean) digester.parse(xmlTestReader());

        // check properties are set correctly
        assertEquals(
                "Property alpha not set correctly",
                "ROOT BODY",
                bean.getAlpha());

        assertEquals(
                "Property beta not set correctly",
                "ALPHA BODY",
                bean.getBeta());

        assertTrue(
                "Property gamma not set correctly",
                bean.getGamma() == null);

        assertEquals("Property delta not set correctly",
                     "DELTA BODY",
                     bean.getDeltaValue());
                    

    }


    /**
     * Test that trying to set an unknown property throws an exception.
     */
    public void testSetUnknownProperty() {

        // going to be setting properties on a SimpleTestBean
        digester.addObjectCreate("root",
                                 "org.apache.commons.digester.SimpleTestBean");

        // attempt to set an unknown property name
        digester.addRule("root/alpha",
                         new BeanPropertySetterRule("unknown"));

        // Attempt to parse the input
        try {
            SimpleTestBean bean = (SimpleTestBean)
                digester.parse(xmlTestReader());
            fail("Should have thrown NoSuchMethodException");
        } catch (Exception e) {
            e.printStackTrace(System.err);
            if (e instanceof InvocationTargetException) {
                Throwable t =
                    ((InvocationTargetException) e).getTargetException();
                if (t instanceof NoSuchMethodException) {
                    ; // Expected result
                } else {
                    fail("Should have thrown NoSuchMethodException, threw " + t);
                }
            }
        }

    }


    /**
     * Test that you can successfully automatically set properties.
     */
    public void testAutomaticallySetProperties()
        throws SAXException, IOException {

        // need the extended rules
        digester.setRules(new ExtendedBaseRules());

        // going to be setting properties on a SimpleTestBean
        digester.addObjectCreate("root",
                                 "org.apache.commons.digester.SimpleTestBean");

        // match all children of root with this rule
        digester.addRule("root/?", new BeanPropertySetterRule());

        SimpleTestBean bean = (SimpleTestBean) digester.parse(xmlTestReader());

        // check properties are set correctly
        assertEquals(
                "Property alpha not set correctly",
                "ALPHA BODY",
                bean.getAlpha());

        assertEquals(
                "Property beta not set correctly",
                "BETA BODY",
                bean.getBeta());

        assertEquals(
                "Property gamma not set correctly",
                "GAMMA BODY",
                bean.getGamma());


    }

    /**
     * Get input stream from {@link #TEST_XML}.
     */
    private Reader xmlTestReader() throws IOException {
        return new StringReader(TEST_XML);
    }

}



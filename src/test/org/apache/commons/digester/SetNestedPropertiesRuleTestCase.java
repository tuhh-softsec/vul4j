/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/test/org/apache/commons/digester/SetNestedPropertiesRuleTestCase.java,v 1.1 2003/11/19 21:07:06 rdonkin Exp $
 * $Revision: 1.1 $
 * $Date: 2003/11/19 21:07:06 $
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
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
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
 * <p> Test case for <code>SetNestedPropertiesRule</code>.
 * This contains tests for the main applications of the rule
 * and two more general tests of digester functionality used by this rule.
 */
public class SetNestedPropertiesRuleTestCase extends TestCase {


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
    public SetNestedPropertiesRuleTestCase(String name) {

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

        return (new TestSuite(SetNestedPropertiesRuleTestCase.class));

    }


    /**
     * Tear down instance variables required by this test case.
     */
    public void tearDown() {

        digester = null;

    }



    // ------------------------------------------------ Individual Test Methods


    /**
     * Test that you can successfully automatically set properties.
     */
    public void testAutomaticallySetProperties()
        throws SAXException, IOException {

        // going to be setting properties on a SimpleTestBean
        digester.addObjectCreate("root",
                                 "org.apache.commons.digester.SimpleTestBean");

        // match all children of root with this rule
        digester.addRule("root", new SetNestedPropertiesRule());

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
     * Test that you can customise the property mappings.
     */
    public void testCustomisedProperties()
        throws SAXException, IOException {

        // going to be setting properties on a SimpleTestBean
        digester.addObjectCreate("root",
                                 "org.apache.commons.digester.SimpleTestBean");

        // ignorethe "alpha" element
        // map the "beta" element into the gamma property
        // map the gamma element into the delta property
        // ignore the delta element
        
        Rule rule = new SetNestedPropertiesRule(
            new String[]{"alpha", "beta", "gamma", "delta"},
            new String[]{null, "gamma", "delta"});
            
        digester.addRule("root", rule);

        SimpleTestBean bean = (SimpleTestBean) digester.parse(xmlTestReader());

        // check properties are set correctly
        assertEquals(
                "Property alpha not set correctly",
                null,
                bean.getAlpha());

        assertEquals(
                "Property beta not set correctly",
                null,
                bean.getBeta());

        assertEquals(
                "Property gamma not set correctly",
                "BETA BODY",
                bean.getGamma());

        assertEquals(
                "Property delta not set correctly",
                "GAMMA BODY",
                bean.getDeltaValue());
                
         // check no bad rules object is left
         assertEquals(
            "Digester rules object not reset.",
            RulesBase.class, digester.getRules().getClass());
    }


    /**
     * Test that:
     * <ul>
     * <li> you can have rules matching the same pattern as the 
     *  SetNestedPropertiesRule, </li>
     * <li> you can have rules matching child elements of the rule, </li>
     * <li> the Rules object is reset nicely. </li>
     * </ul>
     */
    public void testMultiRuleMatch()
        throws SAXException, IOException {

        String testXml =
            "<?xml version='1.0'?>" +
            "<root>" +
                "<testbean alpha='alpha-attr'>ROOT BODY" +
                    "<beta>BETA BODY</beta>" +
                    "<gamma>GAMMA " +
                    "<prop name='delta' value='delta-prop'/>" +
                    "BODY" +
                    "</gamma>" +
                "</testbean>" +
            "</root>";

        Reader reader = new StringReader(testXml);

        // going to be setting properties on a SimpleTestBean
        digester.addObjectCreate("root/testbean",
                                 "org.apache.commons.digester.SimpleTestBean");

        digester.addRule("root/testbean", new SetNestedPropertiesRule());
        digester.addSetProperties("root/testbean");
        digester.addSetProperty("root/testbean/gamma/prop", "name", "value");

        SimpleTestBean bean = (SimpleTestBean) digester.parse(reader);

        assertNotNull("No object created", bean);
        
        // check properties are set correctly
        assertEquals(
                "Property alpha not set correctly",
                "alpha-attr",
                bean.getAlpha());

        assertEquals(
                "Property beta not set correctly",
                "BETA BODY",
                bean.getBeta());

        assertEquals(
                "Property gamma not set correctly",
                "GAMMA BODY",
                bean.getGamma());

        assertEquals(
                "Property delta not set correctly",
                "delta-prop",
                bean.getDeltaValue());

         // check no bad rules object is left
         assertEquals(
            "Digester rules object not reset.",
            RulesBase.class, digester.getRules().getClass());
    }

    /**
     * Test that unknown child elements trigger an exception.
     */
    public void testUnknownChildrenCausesException()
        throws SAXException, IOException {

        String testXml =
            "<?xml version='1.0'?>" +
            "<root>" +
                "<testbean>" +
                    "<beta>BETA BODY</beta>" +
                    "<foo>GAMMA</foo>" +
                "</testbean>" +
            "</root>";

        Reader reader = new StringReader(testXml);

        // going to be setting properties on a SimpleTestBean
        digester.addObjectCreate("root",
                                 "org.apache.commons.digester.SimpleTestBean");

        Rule rule = new SetNestedPropertiesRule();
        digester.addRule("root", rule);

        try {
            SimpleTestBean bean = (SimpleTestBean) digester.parse(reader);
            fail("Expected to generate an exception.");
        } catch(SAXException e) {
            Exception nested = e.getException();
            if ((nested==null) || !(nested instanceof NoSuchMethodException)) {
                // nope, not the sort of exception we expected
                throw e;
            }
        }
    }

    /**
     * Test that unknown child elements are allowed if the appropriate
     * flag is set.
     */
    public void testUnknownChildrenExceptionOverride()
        throws SAXException, IOException {

        String testXml =
            "<?xml version='1.0'?>" +
            "<root>" +
                "<testbean>" +
                    "<beta>BETA BODY</beta>" +
                    "<foo>GAMMA</foo>" +
                "</testbean>" +
            "</root>";

        Reader reader = new StringReader(testXml);

        // going to be setting properties on a SimpleTestBean
        digester.addObjectCreate("root",
                                 "org.apache.commons.digester.SimpleTestBean");

        SetNestedPropertiesRule rule = new SetNestedPropertiesRule();
        rule.setAllowUnknownChildElements(true);
        digester.addRule("root", rule);

        SimpleTestBean bean = (SimpleTestBean) digester.parse(reader);
        assertNotNull(bean);
    }


    /**
     * Get input stream from {@link #TEST_XML}.
     */
    private Reader xmlTestReader() throws IOException {
        return new StringReader(TEST_XML);
    }

}




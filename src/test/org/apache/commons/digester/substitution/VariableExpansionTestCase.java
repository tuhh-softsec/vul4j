/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/test/org/apache/commons/digester/substitution/VariableExpansionTestCase.java,v 1.1 2003/12/02 23:23:10 rdonkin Exp $
 * $Revision: 1.1 $
 * $Date: 2003/12/02 23:23:10 $
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


package org.apache.commons.digester.substitution;

import org.apache.commons.digester.SimpleTestBean;
import org.apache.commons.digester.Digester;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xml.sax.SAXException;

/**
 * <p>Test Case for the variable expansion facility in Digester.
 *
 * @author Simon Kitching
 * @version $Revision: 1.1 $ $Date: 2003/12/02 23:23:10 $
 */

public class VariableExpansionTestCase extends TestCase {

    // ----------------------------------------------------------- Constructors

    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public VariableExpansionTestCase(String name) {

        super(name);

    }

    // --------------------------------------------------- Overall Test Methods

    /**
     * Set up instance variables required by this test case.
     */
    public void setUp() {
    }


    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {
        return (new TestSuite(VariableExpansionTestCase.class));
    }


    /**
     * Tear down instance variables required by this test case.
     */
    public void tearDown() {
    }

    // method used in tests4
    private LinkedList simpleTestBeans = new LinkedList();
    public void addSimpleTestBean(SimpleTestBean bean) {
        simpleTestBeans.add(bean);
    }
    
    // ------------------------------------------------ Individual Test Methods

    /**
     * Test that by default no expansion occurs.
     */
    public void testNoExpansion() throws SAXException, IOException {

        String xml = "<root alpha='${attr1}' beta='var{attr2}'/>";
        StringReader input = new StringReader(xml);
        Digester digester = new Digester();
        
        // Configure the digester as required
        digester.addObjectCreate("root", SimpleTestBean.class);
        digester.addSetProperties("root");

        // Parse our test input.
        Object root = digester.parse(input);

        assertNotNull("Digester returned no object", root);
        SimpleTestBean bean = (SimpleTestBean) root;
        
        assertEquals("${attr1}", bean.getAlpha());
        assertEquals("var{attr2}", bean.getBeta());
    }

    /**
     * Test that a MultiVariableExpander with no sources does no expansion.
     */
    public void testExpansionWithNoSource() throws SAXException, IOException {

        String xml = "<root alpha='${attr1}' beta='var{attr2}'/>";
        StringReader input = new StringReader(xml);
        Digester digester = new Digester();
        
        // Configure the digester as required
        MultiVariableExpander expander = new MultiVariableExpander();
        digester.setSubstitutor(new VariableSubstitutor(expander));
        digester.addObjectCreate("root", SimpleTestBean.class);
        digester.addSetProperties("root");

        // Parse our test input.
        Object root = digester.parse(input);

        assertNotNull("Digester returned no object", root);
        SimpleTestBean bean = (SimpleTestBean) root;
        
        assertEquals("${attr1}", bean.getAlpha());
        assertEquals("var{attr2}", bean.getBeta());
    }

    /**
     * Test that a MultiVariableExpander with multiple sources works.
     * It also tests that expansion works ok where multiple elements
     * exist.
     */
    public void testExpansionWithMultipleSources() throws SAXException, IOException {

        String xml = 
            "<root>" +
              "<bean alpha='${attr1}' beta='var{attr1}'/>" +
              "<bean alpha='${attr2}' beta='var{attr2}'/>" +
            "</root>";
            
        StringReader input = new StringReader(xml);
        Digester digester = new Digester();
        
        // Configure the digester as required
        HashMap source1 = new HashMap();
        source1.put("attr1", "source1.attr1");
        source1.put("attr2", "source1.attr2"); // should not be used
        
        HashMap source2 = new HashMap();
        source2.put("attr1", "source2.attr1"); // should not be used
        source2.put("attr2", "source2.attr2");
        
        
        MultiVariableExpander expander = new MultiVariableExpander();
        expander.addSource("$", source1);
        expander.addSource("var", source2);
        
        digester.setSubstitutor(new VariableSubstitutor(expander));
        digester.addObjectCreate("root/bean", SimpleTestBean.class);
        digester.addSetProperties("root/bean");
        digester.addSetNext("root/bean", "addSimpleTestBean");

        // Parse our test input.
        this.simpleTestBeans.clear();
        digester.push(this);
        digester.parse(input);

        assertEquals(2, this.simpleTestBeans.size());

        {
        SimpleTestBean bean = (SimpleTestBean) this.simpleTestBeans.get(0);
        assertEquals("source1.attr1", bean.getAlpha());
        assertEquals("source2.attr1", bean.getBeta());
        }

        {
        SimpleTestBean bean = (SimpleTestBean) this.simpleTestBeans.get(1);
        assertEquals("source1.attr2", bean.getAlpha());
        assertEquals("source2.attr2", bean.getBeta());
        }
    }

    /**
     * Test expansion of text in element bodies.
     */
    public void testBodyExpansion() throws SAXException, IOException {

        String xml = 
            "<root>" + 
            "Twas noun{1} and the noun{2}" +
            " did verb{1} and verb{2} in the noun{3}" +
            "</root>";

        StringReader input = new StringReader(xml);
        Digester digester = new Digester();
        
        // Configure the digester as required
        HashMap nouns = new HashMap();
        nouns.put("1", "brillig");
        nouns.put("2", "slithy toves");
        nouns.put("3", "wabe");
        
        HashMap verbs = new HashMap();
        verbs.put("1", "gyre");
        verbs.put("2", "gimble");
        
        MultiVariableExpander expander = new MultiVariableExpander();
        expander.addSource("noun", nouns);
        expander.addSource("verb", verbs);
        digester.setSubstitutor(new VariableSubstitutor(expander));
        
        digester.addObjectCreate("root", SimpleTestBean.class);
        digester.addCallMethod("root", "setAlpha", 0);

        // Parse our test input.
        Object root = digester.parse(input);

        assertNotNull("Digester returned no object", root);
        SimpleTestBean bean = (SimpleTestBean) root;
        
        assertEquals(
            "Twas brillig and the slithy toves" +
            " did gyre and gimble in the wabe",
            bean.getAlpha());
    }

    /**
     * Test that an unknown variable causes a RuntimeException.
     */
    public void testExpansionException() throws IOException {

        String xml = "<root alpha='${attr1}'/>";
        StringReader input = new StringReader(xml);
        Digester digester = new Digester();
        
        // Configure the digester as required
        MultiVariableExpander expander = new MultiVariableExpander();
        expander.addSource("$", new HashMap());
        digester.setSubstitutor(new VariableSubstitutor(expander));
        
        digester.addObjectCreate("root", SimpleTestBean.class);
        digester.addSetProperties("root");

        // Parse our test input.
        try {
            digester.parse(input);
            fail("Exception expected due to unknown variable.");
        } catch(SAXException e) {
            // expected, due to reference to undefined variable
        }
    }
}


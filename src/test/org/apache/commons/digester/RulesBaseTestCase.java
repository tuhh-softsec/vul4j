/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/test/org/apache/commons/digester/RulesBaseTestCase.java,v 1.3 2002/01/09 20:22:50 sanders Exp $
 * $Revision: 1.3 $
 * $Date: 2002/01/09 20:22:50 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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


import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xml.sax.ErrorHandler;



/**
 * <p>Test Case for the RulesBase matching rules.
 * Most of this material was original contained in the digester test case
 * but was moved into this class so that extensions of the basic matching rules
 * behaviour can extend this test case.
 * </p>
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.3 $ $Date: 2002/01/09 20:22:50 $
 */

public class RulesBaseTestCase extends TestCase {


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
    public RulesBaseTestCase(String name) {

        super(name);

    }


    // -------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    public void setUp() {

        digester = new Digester();
        digester.setRules(createMatchingRulesForTest());

    }

    /**
     * <p> This should be overriden by subclasses.
     *
     * @return the matching rules to be tested.
     */
    protected Rules createMatchingRulesForTest()
    {
        return new RulesBase();
    }

    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {

        return (new TestSuite(RulesBaseTestCase.class));

    }


    /**
     * Tear down instance variables required by this test case.
     */
    public void tearDown() {

        digester = null;

    }



    // ------------------------------------------------ Individual Test Methods

    /**
     * Basic test for rule creation and matching.
     */
    public void testRules() {

        // clear any existing rules
        digester.getRules().clear();

        // perform tests
        List list = null;

        assertEquals("Initial rules list is empty",
                     0, digester.getRules().match("a").size());
        digester.addSetProperties("a");
        assertEquals("Add a matching rule",
                     1, digester.getRules().match(null,"a").size());
        digester.addSetProperties("b");
        assertEquals("Add a non-matching rule",
                     1, digester.getRules().match(null,"a").size());
        digester.addSetProperties("a/b");
        assertEquals("Add a non-matching nested rule",
                     1, digester.getRules().match(null,"a").size());
        digester.addSetProperties("a/b");
        assertEquals("Add a second matching rule",
                     2, digester.getRules().match(null,"a/b").size());

        // clean up
        digester.getRules().clear();

    }

    /**
     * <p>Test matching rules in {@link RulesBase}.</p>
     *
     * <p>Tests:</p>
     * <ul>
     * <li>exact match</li>
     * <li>tail match</li>
     * <li>longest pattern rule</li>
     * </ul>
     */
    public void testRulesBase() {

        // clear any existing rules
        digester.getRules().clear();

        assertEquals("Initial rules list is empty",
                     0, digester.getRules().rules().size());

        // We're going to set up
        digester.addRule("a/b/c/d", new TestRule(digester, "a/b/c/d"));
        digester.addRule("*/d", new TestRule(digester, "*/d"));
        digester.addRule("*/c/d", new TestRule(digester, "*/c/d"));

        // Test exact match
        assertEquals("Exact match takes precedence 1",
                     1, digester.getRules().match(null,"a/b/c/d").size());
        assertEquals("Exact match takes precedence 2",
                     "a/b/c/d",
                     ((TestRule) digester.getRules().match(null,"a/b/c/d").iterator().next()).getIdentifier());

        // Test wildcard tail matching
        assertEquals("Wildcard tail matching rule 1",
                     1, digester.getRules().match(null,"a/b/d").size());
        assertEquals("Wildcard tail matching rule 2",
                     "*/d",
                     ((TestRule) digester.getRules().match(null,"a/b/d").iterator().next()).getIdentifier());

        // Test the longest matching pattern rule
        assertEquals("Longest tail rule 1",
                     1, digester.getRules().match(null,"x/c/d").size());
        assertEquals("Longest tail rule 2",
                     "*/c/d",
                     ((TestRule) digester.getRules().match(null,"x/c/d").iterator().next()).getIdentifier());

        // clean up
        digester.getRules().clear();

    }

    /**
     * Test basic matchings involving namespaces.
     */
    public void testBasicNamespaceMatching() {

        List list=null;
        Iterator it=null;

        // clear any existing rules
        digester.getRules().clear();

        assertEquals("Initial rules list is empty",
                     0, digester.getRules().rules().size());

        // Set up rules
        digester.addRule("alpha/beta/gamma", new TestRule(digester, "No-Namespace"));
        digester.addRule("alpha/beta/gamma", new TestRule(digester, "Euclidean-Namespace","euclidean"));


        list=digester.getRules().rules();

        // test that matching null namespace brings back namespace and non-namespace rules
        list=digester.getRules().match(null,"alpha/beta/gamma");

        assertEquals("Null namespace match (A)",2,list.size());

        it=list.iterator();
        assertEquals("Null namespace match (B)","No-Namespace",((TestRule)it.next()).getIdentifier());
        assertEquals("Null namespace match (C)","Euclidean-Namespace",((TestRule)it.next()).getIdentifier());



        // test that matching euclid namespace brings back namespace and non-namespace rules
        list=digester.getRules().match("euclidean","alpha/beta/gamma");

        assertEquals("Matching namespace match (A)",2,list.size());

        it=list.iterator();
        assertEquals("Matching namespace match (B)","No-Namespace",((TestRule)it.next()).getIdentifier());
        assertEquals("Matching namespace match (C)","Euclidean-Namespace",((TestRule)it.next()).getIdentifier());



        // test that matching another namespace brings back only non-namespace rule
        list=digester.getRules().match("hyperbolic","alpha/beta/gamma");

        assertEquals("Non matching namespace match (A)",1,list.size());

        it=list.iterator();
        assertEquals("Non matching namespace match (B)","No-Namespace",((TestRule)it.next()).getIdentifier());

        // clean up
        digester.getRules().clear();

    }

    /**
     * Rules must always be returned in the correct order.
     */
    public void testOrdering() {

        // clear any existing rules
        digester.getRules().clear();

        assertEquals("Initial rules list is empty",
                     0, digester.getRules().rules().size());

        // Set up rules
        digester.addRule("alpha/beta/gamma", new TestRule(digester, "one"));
        digester.addRule("alpha/beta/gamma", new TestRule(digester, "two"));
        digester.addRule("alpha/beta/gamma", new TestRule(digester, "three"));

        // test that rules are returned in set order
        List list=digester.getRules().match(null,"alpha/beta/gamma");

        assertEquals("Testing ordering mismatch (A)",3,list.size());

        Iterator it=list.iterator();
        assertEquals("Testing ordering mismatch (B)","one",((TestRule)it.next()).getIdentifier());
        assertEquals("Testing ordering mismatch (C)","two",((TestRule)it.next()).getIdentifier());
        assertEquals("Testing ordering mismatch (D)","three",((TestRule)it.next()).getIdentifier());

        // clean up
        digester.getRules().clear();

    }
}

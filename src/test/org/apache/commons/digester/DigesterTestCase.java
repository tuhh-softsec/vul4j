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


package org.apache.commons.digester;

import java.math.BigDecimal;
import java.net.URL;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.EmptyStackException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xml.sax.ErrorHandler;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.InputSource;


/**
 * <p>Test Case for the Digester class.  These tests exercise the individual
 * methods of a Digester, but do not attempt to process complete documents.
 * </p>
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.19 $ $Date: 2004/03/15 21:44:53 $
 */

public class DigesterTestCase extends TestCase {


    // ----------------------------------------------------- Instance Variables


    /**
     * The digester instance we will be processing.
     */
    protected Digester digester = null;


    /**
     * The set of public identifiers, and corresponding resource names,
     * for the versions of the DTDs that we know about.  There
     * <strong>MUST</strong> be an even number of Strings in this array.
     */
    protected static final String registrations[] = {
        "-//Netscape Communications//DTD RSS 0.9//EN",
        "/org/apache/commons/digester/rss/rss-0.9.dtd",
        "-//Netscape Communications//DTD RSS 0.91//EN",
        "/org/apache/commons/digester/rss/rss-0.91.dtd",
    };


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public DigesterTestCase(String name) {

        super(name);

    }


    // -------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    public void setUp() {

        digester = new Digester();
        digester.setRules(new RulesBase());

    }


    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {

        return (new TestSuite(DigesterTestCase.class));

    }


    /**
     * Tear down instance variables required by this test case.
     */
    public void tearDown() {

        digester = null;

    }



    // ------------------------------------------------ Individual Test Methods


    /**
     * Test the basic property getters and setters.
     */
    public void testProperties() {

        assertNull("Initial error handler is null",
                digester.getErrorHandler());
        digester.setErrorHandler((ErrorHandler) digester);
        assertTrue("Set error handler is digester",
                digester.getErrorHandler() == digester);
        digester.setErrorHandler(null);
        assertNull("Reset error handler is null",
                digester.getErrorHandler());

        assertTrue("Initial namespace aware is false",
                !digester.getNamespaceAware());
        digester.setNamespaceAware(true);
        assertTrue("Set namespace aware is true",
                digester.getNamespaceAware());
        digester.setNamespaceAware(false);
        assertTrue("Reset namespace aware is false",
                !digester.getNamespaceAware());

        assertTrue("Initial validating is false",
                !digester.getValidating());
        digester.setValidating(true);
        assertTrue("Set validating is true",
                digester.getValidating());
        digester.setValidating(false);
        assertTrue("Reset validating is false",
                !digester.getValidating());

    }


    /**
     * Test registration of URLs for specified public identifiers.
     */
    public void testRegistrations() {

        Map map = digester.getRegistrations();
        assertEquals("Initially zero registrations", 0, map.size());
        int n = 0;
        for (int i = 0; i < registrations.length; i += 2) {
            URL url = this.getClass().getResource(registrations[i + 1]);
            if (url != null) {
                digester.register(registrations[i], url.toString());
                n++;
            }
        }
        map = digester.getRegistrations();
        assertEquals("Registered two URLs", n, map.size());

        int count[] = new int[n];
        for (int i = 0; i < n; i++)
            count[i] = 0;
        Iterator keys = map.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            for (int i = 0; i < n; i++) {
                if (key.equals(registrations[i * 2])) {
                    count[i]++;
                    break;
                }
            }
        }
        for (int i = 0; i < n; i++)
            assertEquals("Count for key " + registrations[i * 2],
                    1, count[i]);

    }


    /**
     * Basic test for rule creation and matching.
     */
    public void testRules() {

        List list = null;

        assertEquals("Initial rules list is empty",
                0, digester.getRules().match(null, "a").size());
        digester.addSetProperties("a");
        assertEquals("Add a matching rule",
                1, digester.getRules().match(null, "a").size());
        digester.addSetProperties("b");
        assertEquals("Add a non-matching rule",
                1, digester.getRules().match(null, "a").size());
        digester.addSetProperties("a/b");
        assertEquals("Add a non-matching nested rule",
                1, digester.getRules().match(null, "a").size());
        digester.addSetProperties("a/b");
        assertEquals("Add a second matching rule",
                2, digester.getRules().match(null, "a/b").size());

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

        assertEquals("Initial rules list is empty",
                0, digester.getRules().rules().size());

        // We're going to set up
        digester.addRule("a/b/c/d", new TestRule("a/b/c/d"));
        digester.addRule("*/d", new TestRule("*/d"));
        digester.addRule("*/c/d", new TestRule("*/c/d"));

        // Test exact match
        assertEquals("Exact match takes precedence 1",
                1, digester.getRules().match(null, "a/b/c/d").size());
        assertEquals("Exact match takes precedence 2",
                "a/b/c/d",
                ((TestRule) digester.getRules().match(null, "a/b/c/d").iterator().next()).getIdentifier());

        // Test wildcard tail matching
        assertEquals("Wildcard tail matching rule 1",
                1, digester.getRules().match(null, "a/b/d").size());
        assertEquals("Wildcard tail matching rule 2",
                "*/d",
                ((TestRule) digester.getRules().match(null, "a/b/d").iterator().next()).getIdentifier());

        // Test the longest matching pattern rule
        assertEquals("Longest tail rule 1",
                1, digester.getRules().match(null, "x/c/d").size());
        assertEquals("Longest tail rule 2",
                "*/c/d",
                ((TestRule) digester.getRules().match(null, "x/c/d").iterator().next()).getIdentifier());

    }


    /**
     * Test the basic stack mechanisms.
     */
    public void testStackMethods() {

        Object value = null;

        // New stack must be empty
        assertEquals("New stack is empty", 0, digester.getCount());
        value = digester.peek();
        assertNull("New stack peek() returns null", value);
        value = digester.pop();
        assertNull("New stack pop() returns null", value);

        // Test pushing and popping activities
        digester.push("First Item");
        assertEquals("Pushed one item size", 1, digester.getCount());
        value = digester.peek();
        assertNotNull("Peeked first item is not null", value);
        assertEquals("Peeked first item value", "First Item", (String) value);

        digester.push("Second Item");
        assertEquals("Pushed two items size", 2, digester.getCount());
        value = digester.peek();
        assertNotNull("Peeked second item is not null", value);
        assertEquals("Peeked second item value", "Second Item", (String) value);

        value = digester.pop();
        assertEquals("Popped stack size", 1, digester.getCount());
        assertNotNull("Popped second item is not null", value);
        assertEquals("Popped second item value", "Second Item", (String) value);
        value = digester.peek();
        assertNotNull("Remaining item is not null", value);
        assertEquals("Remaining item value", "First Item", (String) value);
        assertEquals("Remaining stack size", 1, digester.getCount());

        // Cleared stack is empty
        digester.push("Dummy Item");
        digester.clear();
        assertEquals("Cleared stack is empty", 0, digester.getCount());
        value = digester.peek();
        assertNull("Cleared stack peek() returns null", value);
        value = digester.pop();
        assertNull("Cleared stack pop() returns null", value);

    }

    public void testOnceAndOnceOnly() throws Exception {
        
        class TestConfigureDigester extends Digester {
            public int called=0;
            public TestConfigureDigester() {}
            
            protected void initialize() {
                called++;
            }
        }
        
        TestConfigureDigester digester = new TestConfigureDigester();
        
        String xml = "<?xml version='1.0'?><document/>";
        digester.parse(new StringReader(xml));
        
        assertEquals("Initialize should be called once and only once", 1, digester.called);
    }
    
    public void testBasicSubstitution() throws Exception {
        class TestSubRule extends Rule {
            public String body;
            public Attributes attributes;
            
            public void begin(String namespace, String name, Attributes attributes) {
                this.attributes = new AttributesImpl(attributes);
            }
            
            public void body(String namespace, String name, String text) {
                this.body = text;
            }
        }
        
        TestSubRule tsr = new TestSubRule();
        Digester digester = new Digester();
        digester.addRule("alpha/beta", tsr);
            
        // it's not easy to transform dirty harry into the mighty circus - but let's give it a try
        String xml = "<?xml version='1.0'?><alpha><beta forname='Dirty' surname='Harry'>Do you feel luck punk?</beta></alpha>";
        InputSource in = new InputSource(new StringReader(xml));
        
        digester.parse(in);
        
        assertEquals("Unsubstituted body text", "Do you feel luck punk?", tsr.body);
        assertEquals("Unsubstituted number of attributes", 2, tsr.attributes.getLength());
        assertEquals("Unsubstituted forname attribute value", "Dirty", tsr.attributes.getValue("forname"));
        assertEquals("Unsubstituted surname attribute value", "Harry", tsr.attributes.getValue("surname"));

        digester.setSubstitutor(
            new Substitutor() {
                public Attributes substitute(Attributes attributes) {
                    AttributesImpl results = new AttributesImpl();
                    results.addAttribute("", "python", "python", "CDATA", "Cleese");
                    return results;
                }   
                
                public String substitute(String bodyText) {
                    return "And now for something completely different...";
                }
            });
        
        // now transform into the full monty
        in = new InputSource(new StringReader(xml));
        digester.parse(in);
        
        assertEquals("Substituted body text", "And now for something completely different...", tsr.body);
        assertEquals("Substituted number of attributes", 1, tsr.attributes.getLength());
        assertEquals("Substituted python attribute value", "Cleese", tsr.attributes.getValue("", "python"));
    }
    
    /** Tests the push-peek-pop cycle for a named stack */
    public void testNamedStackPushPeekPop() throws Exception
    {
        BigDecimal archimedesAveragePi = new BigDecimal("3.1418");
        String testStackName = "org.apache.commons.digester.tests.testNamedStackPushPeekPop";
        Digester digester = new Digester();
        assertTrue("Stack starts empty:", digester.isEmpty(testStackName));
        digester.push(testStackName, archimedesAveragePi);
        assertEquals("Peeked value:", archimedesAveragePi, digester.peek(testStackName));
        assertEquals("Popped value:", archimedesAveragePi, digester.pop(testStackName));
        assertTrue("Stack ends empty:", digester.isEmpty(testStackName));
    }
    
    /** Tests that values are stored independently */
    public void testNamedIndependence()
    {
        String testStackOneName = "org.apache.commons.digester.tests.testNamedIndependenceOne";
        String testStackTwoName = "org.apache.commons.digester.tests.testNamedIndependenceTwo";
        Digester digester = new Digester();
        digester.push(testStackOneName, "Tweedledum");
        digester.push(testStackTwoName, "Tweedledee");
        assertEquals("Popped value one:", "Tweedledum", digester.pop(testStackOneName));
        assertEquals("Popped value two:", "Tweedledee", digester.pop(testStackTwoName));
    }
    
    /** Tests popping named stack not yet pushed */
    public void testPopNamedStackNotPushed() 
    {
        String testStackName = "org.apache.commons.digester.tests.testPopNamedStackNotPushed";
        Digester digester = new Digester();
        try {
        
            digester.pop(testStackName);
            fail("Expected an EmptyStackException");
            
        } catch (EmptyStackException e) {
            // expected
        }
        
        try {
        
            digester.peek(testStackName);
            fail("Expected an EmptyStackException");
            
        } catch (EmptyStackException e) {
            // expected
        }
    }
    
    /** Tests for isEmpty */
    public void testNamedStackIsEmpty()
    {
        String testStackName = "org.apache.commons.digester.tests.testNamedStackIsEmpty";
        Digester digester = new Digester();
        assertTrue(
            "A named stack that has no object pushed onto it yet should be empty", 
            digester.isEmpty(testStackName));
            
        digester.push(testStackName, "Some test value");
        assertFalse(
            "A named stack that has an object pushed onto it should be not empty",
            digester.isEmpty(testStackName));
            
        digester.peek(testStackName);
        assertFalse(
            "Peek should not effect whether the stack is empty",
            digester.isEmpty(testStackName));
        
        digester.pop(testStackName);
        assertTrue(
            "A named stack that has it's last object popped is empty", 
            digester.isEmpty(testStackName));
    }
    
}

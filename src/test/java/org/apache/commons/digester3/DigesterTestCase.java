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
package org.apache.commons.digester3;

import static org.apache.commons.digester3.DigesterLoader.newLoader;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URL;
import java.util.EmptyStackException;
import java.util.Map;

import org.apache.commons.digester3.spi.Substitutor;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.AttributesImpl;

/**
 * <p>Test Case for the Digester class.  These tests exercise the individual
 * methods of a Digester, but do not attempt to process complete documents.
 * </p>
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */
public class DigesterTestCase extends AbstractTestCase {

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

    private DigesterLoader newEmptyLoader() {
        return newLoader(new AbstractRulesModule() {

            @Override
            protected void configure() {
                // do nothing
            }

        });
    }

    private Digester newEmptyDigester() {
        return newEmptyLoader().newDigester();
    }

    /**
     * Test <code>null</code> parsing.
     * (should lead to <code>IllegalArgumentException</code>s)
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullFileParse() throws Exception {
        newEmptyDigester().parse((File) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullInputSourceParse() throws Exception {
        newEmptyDigester().parse((InputSource) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullInputStreamParse() throws Exception {
        newEmptyDigester().parse((InputStream) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullReaderParse() throws Exception {
        newEmptyDigester().parse((Reader) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullStringParse() throws Exception {
        newEmptyDigester().parse((String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullURLParse() throws Exception {
        newEmptyDigester().parse((URL) null);
    }

    /**
     * Test the basic property getters and setters.
     */
    @Test
    public void testProperties() {
        DigesterLoader loader = newEmptyLoader();

        assertTrue("Initial namespace aware is false", !loader.isNamespaceAware());
        loader.setNamespaceAware(true);
        assertTrue("Set namespace aware is true", loader.isNamespaceAware());
        loader.setNamespaceAware(false);
        assertTrue("Reset namespace aware is false", !loader.isNamespaceAware());

        assertTrue("Initial validating is false", !loader.isValidating());
        loader.setValidating(true);
        assertTrue("Set validating is true", loader.isValidating());
        loader.setValidating(false);
        assertTrue("Reset validating is false", !loader.isValidating());

        Digester digester = loader.newDigester();

        assertNotNull("Initial error handler is null", digester.getErrorHandler());
        assertTrue("Set error handler is digester", digester.getErrorHandler() == digester);
        digester.setErrorHandler(null);
        assertNull("Reset error handler is null", digester.getErrorHandler());
    }

    /**
     * Test registration of URLs for specified public identifiers.
     */
    @Test
    public void testRegistrations() {
        DigesterLoader loader = newEmptyLoader();

        Map<String, URL> map = loader.getRegistrations();
        assertEquals("Initially zero registrations", 0, map.size());
        int n = 0;
        for (int i = 0; i < registrations.length; i += 2) {
            URL url = this.getClass().getResource(registrations[i + 1]);
            if (url != null) {
                loader.register(registrations[i], url);
                n++;
            }
        }
        map = loader.getRegistrations();
        assertEquals("Registered two URLs", n, map.size());

        int count[] = new int[n];
        for (int i = 0; i < n; i++)
            count[i] = 0;
        for (String key : map.keySet()) {
            for (int i = 0; i < n; i++) {
                if (key.equals(registrations[i * 2])) {
                    count[i]++;
                    break;
                }
            }
        }
        for (int i = 0; i < n; i++)
            assertEquals("Count for key " + registrations[i * 2], 1, count[i]);
    }

    /**
     * Test the basic stack mechanisms.
     */
    @Test
    public void testStackMethods() {
        Digester digester = newEmptyDigester();

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

    @Test
    public void testBasicSubstitution() throws Exception {
        class TestSubRule extends Rule {

            public String body;

            public Attributes attributes;

            @Override
            public void begin(String namespace, String name, Attributes attributes) {
                this.attributes = new AttributesImpl(attributes);
            }

            @Override
            public void body(String namespace, String name, String text) {
                this.body = text;
            }
        }

        final TestSubRule tsr = new TestSubRule();
        DigesterLoader loader = newLoader(new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern("alpha/beta").addRule(tsr);
            }

        });

        Digester digester = loader.newDigester();

        // it's not easy to transform dirty harry into the mighty circus - but let's give it a try
        String xml = "<?xml version='1.0'?><alpha><beta forname='Dirty' surname='Harry'>Do you feel luck punk?</beta></alpha>";
        InputSource in = new InputSource(new StringReader(xml));

        digester.parse(in);

        assertEquals("Unsubstituted body text", "Do you feel luck punk?", tsr.body);
        assertEquals("Unsubstituted number of attributes", 2, tsr.attributes.getLength());
        assertEquals("Unsubstituted forname attribute value", "Dirty", tsr.attributes.getValue("forname"));
        assertEquals("Unsubstituted surname attribute value", "Harry", tsr.attributes.getValue("surname"));

        digester = loader.setSubstitutor(new Substitutor() {

            public Attributes substitute(Attributes attributes) {
                AttributesImpl results = new AttributesImpl();
                results.addAttribute("", "python", "python", "CDATA", "Cleese");
                return results;
            }

            public String substitute(String bodyText) {
                return "And now for something completely different...";
            }

        }).newDigester();

        // now transform into the full monty
        in = new InputSource(new StringReader(xml));
        digester.parse(in);

        assertEquals("Substituted body text", "And now for something completely different...", tsr.body);
        assertEquals("Substituted number of attributes", 1, tsr.attributes.getLength());
        assertEquals("Substituted python attribute value", "Cleese", tsr.attributes.getValue("", "python"));
    }

    /** Tests the push-peek-pop cycle for a named stack */
    @Test
    public void testNamedStackPushPeekPop() throws Exception {
        BigDecimal archimedesAveragePi = new BigDecimal("3.1418");
        String testStackName = "org.apache.commons.digester.tests.testNamedStackPushPeekPop";
        Digester digester = newEmptyDigester();
        assertTrue("Stack starts empty:", digester.isEmpty(testStackName));
        digester.push(testStackName, archimedesAveragePi);
        assertEquals("Peeked value:", archimedesAveragePi, digester.peek(testStackName));
        assertEquals("Popped value:", archimedesAveragePi, digester.pop(testStackName));
        assertTrue("Stack ends empty:", digester.isEmpty(testStackName));

        digester.push(testStackName, "1");
        digester.push(testStackName, "2");
        digester.push(testStackName, "3");

        assertEquals("Peek#1", "1", digester.peek(testStackName, 2));
        assertEquals("Peek#2", "2", digester.peek(testStackName, 1));
        assertEquals("Peek#3", "3", digester.peek(testStackName, 0));
        assertEquals("Peek#3a", "3", digester.peek(testStackName));

        try {
            // peek beyond stack
            digester.peek(testStackName, 3);
            fail("Peek#4 failed to throw an exception.");
        } catch(EmptyStackException ex) {
            // ok, expected
        }

        try {
            // peek a nonexistent named stack
            digester.peek("no.such.stack", 0);
            fail("Peeking a non-existent stack failed to throw an exception.");
        } catch(EmptyStackException ex) {
            // ok, expected
        }
    }

    /** Tests that values are stored independently */
    @Test
    public void testNamedIndependence() {
        String testStackOneName = "org.apache.commons.digester.tests.testNamedIndependenceOne";
        String testStackTwoName = "org.apache.commons.digester.tests.testNamedIndependenceTwo";
        Digester digester = newEmptyDigester();
        digester.push(testStackOneName, "Tweedledum");
        digester.push(testStackTwoName, "Tweedledee");
        assertEquals("Popped value one:", "Tweedledum", digester.pop(testStackOneName));
        assertEquals("Popped value two:", "Tweedledee", digester.pop(testStackTwoName));
    }

    /** Tests popping named stack not yet pushed */
    @Test
    public void testPopNamedStackNotPushed() {
        String testStackName = "org.apache.commons.digester.tests.testPopNamedStackNotPushed";
        Digester digester = newEmptyDigester();
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
    @Test
    public void testNamedStackIsEmpty() {
        String testStackName = "org.apache.commons.digester.tests.testNamedStackIsEmpty";
        Digester digester = newEmptyDigester();
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

    /**
     * Test the Digester.getRoot method.
     */
    @Test
    public void testGetRoot() throws Exception {
        Digester digester = newBasicDigester(new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern("root").createObject().ofType(SimpleTestBean.class);
            }

        });

        String xml = "<root/>";
        InputSource in = new InputSource(new StringReader(xml));

        digester.parse(in);

        Object root = digester.getRoot();
        assertNotNull("root object not retrieved", root);
        assertTrue("root object not a TestRule instance", (root instanceof SimpleTestBean));
    }

}

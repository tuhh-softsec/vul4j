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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Map;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.ObjectCreateRule;
import org.apache.commons.digester3.Rule;
import org.apache.commons.digester3.RulesBase;
import org.apache.commons.digester3.StackAction;
import org.apache.commons.digester3.Substitutor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.AttributesImpl;

/**
 * <p>
 * Test Case for the Digester class. These tests exercise the individual methods of a Digester, but do not attempt to
 * process complete documents.
 * </p>
 * 
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class DigesterTestCase
{

    // ----------------------------------------------------- Instance Variables

    /**
     * The digester instance we will be processing.
     */
    protected Digester digester = null;

    /**
     * The set of public identifiers, and corresponding resource names, for the versions of the DTDs that we know about.
     * There <strong>MUST</strong> be an even number of Strings in this array.
     */
    protected static final String registrations[] = { "-//Netscape Communications//DTD RSS 0.9//EN",
        "/org/apache/commons/digester3/rss/rss-0.9.dtd", "-//Netscape Communications//DTD RSS 0.91//EN",
        "/org/apache/commons/digester3/rss/rss-0.91.dtd", };

    // -------------------------------------------------- Overall Test Methods

    /**
     * Set up instance variables required by this test case.
     */
    @Before
    public void setUp()
    {

        digester = new Digester();
        digester.setRules( new RulesBase() );

    }

    /**
     * Tear down instance variables required by this test case.
     */
    @After
    public void tearDown()
    {

        digester = null;

    }

    // ------------------------------------------------ Individual Test Methods

    /**
     * Test <code>null</code> parsing. (should lead to <code>IllegalArgumentException</code>s)
     */
    @Test
    public void testNullFileParse()
        throws Exception
    {

        try
        {
            digester.parse( (File) null );
            fail( "Expected IllegalArgumentException with null argument" );
        }
        catch ( IllegalArgumentException e )
        {
            // expected
        }

    }

    @Test
    public void testNullInputSourceParse()
        throws Exception
    {

        try
        {
            digester.parse( (InputSource) null );
            fail( "Expected IllegalArgumentException with null argument" );
        }
        catch ( IllegalArgumentException e )
        {
            // expected
        }

    }

    @Test
    public void testNullInputStreamParse()
        throws Exception
    {

        try
        {
            digester.parse( (InputStream) null );
            fail( "Expected IllegalArgumentException with null argument" );
        }
        catch ( IllegalArgumentException e )
        {
            // expected
        }

    }

    @Test
    public void testNullReaderParse()
        throws Exception
    {

        try
        {
            digester.parse( (Reader) null );
            fail( "Expected IllegalArgumentException with null argument" );
        }
        catch ( IllegalArgumentException e )
        {
            // expected
        }

    }

    @Test
    public void testNullStringParse()
        throws Exception
    {

        try
        {
            digester.parse( (String) null );
            fail( "Expected IllegalArgumentException with null argument" );
        }
        catch ( IllegalArgumentException e )
        {
            // expected
        }

    }

    @Test
    public void testNullURLParse()
        throws Exception
    {

        try
        {
            digester.parse( (URL) null );
            fail( "Expected IllegalArgumentException with null argument" );
        }
        catch ( IllegalArgumentException e )
        {
            // expected
        }

    }

    /**
     * Test the basic property getters and setters.
     */
    @Test
    public void testProperties()
    {

        assertNull( "Initial error handler is null", digester.getErrorHandler() );
        digester.setErrorHandler( digester );
        assertTrue( "Set error handler is digester", digester.getErrorHandler() == digester );
        digester.setErrorHandler( null );
        assertNull( "Reset error handler is null", digester.getErrorHandler() );

        assertTrue( "Initial namespace aware is false", !digester.getNamespaceAware() );
        digester.setNamespaceAware( true );
        assertTrue( "Set namespace aware is true", digester.getNamespaceAware() );
        digester.setNamespaceAware( false );
        assertTrue( "Reset namespace aware is false", !digester.getNamespaceAware() );

        assertTrue( "Initial validating is false", !digester.getValidating() );
        digester.setValidating( true );
        assertTrue( "Set validating is true", digester.getValidating() );
        digester.setValidating( false );
        assertTrue( "Reset validating is false", !digester.getValidating() );

    }

    /**
     * Test registration of URLs for specified public identifiers.
     */
    @Test
    public void testRegistrations()
    {

        Map<String, URL> map = digester.getRegistrations();
        assertEquals( "Initially zero registrations", 0, map.size() );
        int n = 0;
        for ( int i = 0; i < registrations.length; i += 2 )
        {
            URL url = this.getClass().getResource( registrations[i + 1] );
            if ( url != null )
            {
                digester.register( registrations[i], url );
                n++;
            }
        }
        map = digester.getRegistrations();
        assertEquals( "Registered two URLs", n, map.size() );

        int count[] = new int[n];
        for ( int i = 0; i < n; i++ )
            count[i] = 0;
        for ( String key : map.keySet() )
        {
            for ( int i = 0; i < n; i++ )
            {
                if ( key.equals( registrations[i * 2] ) )
                {
                    count[i]++;
                    break;
                }
            }
        }
        for ( int i = 0; i < n; i++ )
            assertEquals( "Count for key " + registrations[i * 2], 1, count[i] );

    }

    /**
     * Basic test for rule creation and matching.
     */
    @Test
    public void testRules()
    {

        assertEquals( "Initial rules list is empty", 0, digester.getRules().match( null, "a", null, null ).size() );
        digester.addSetProperties( "a" );
        assertEquals( "Add a matching rule", 1, digester.getRules().match( null, "a", null, null ).size() );
        digester.addSetProperties( "b" );
        assertEquals( "Add a non-matching rule", 1, digester.getRules().match( null, "a", null, null ).size() );
        digester.addSetProperties( "a/b" );
        assertEquals( "Add a non-matching nested rule", 1, digester.getRules().match( null, "a", null, null ).size() );
        digester.addSetProperties( "a/b" );
        assertEquals( "Add a second matching rule", 2, digester.getRules().match( null, "a/b", null, null ).size() );

    }

    /**
     * <p>
     * Test matching rules in {@link RulesBase}.
     * </p>
     * <p>
     * Tests:
     * </p>
     * <ul>
     * <li>exact match</li>
     * <li>tail match</li>
     * <li>longest pattern rule</li>
     * </ul>
     */
    @Test
    public void testRulesBase()
    {

        assertEquals( "Initial rules list is empty", 0, digester.getRules().rules().size() );

        // We're going to set up
        digester.addRule( "a/b/c/d", new TestRule( "a/b/c/d" ) );
        digester.addRule( "*/d", new TestRule( "*/d" ) );
        digester.addRule( "*/c/d", new TestRule( "*/c/d" ) );

        // Test exact match
        assertEquals( "Exact match takes precedence 1", 1, digester.getRules().match( null, "a/b/c/d", null, null ).size() );
        assertEquals( "Exact match takes precedence 2", "a/b/c/d",
                      ( (TestRule) digester.getRules().match( null, "a/b/c/d", null, null ).iterator().next() ).getIdentifier() );

        // Test wildcard tail matching
        assertEquals( "Wildcard tail matching rule 1", 1, digester.getRules().match( null, "a/b/d", null, null ).size() );
        assertEquals( "Wildcard tail matching rule 2", "*/d",
                      ( (TestRule) digester.getRules().match( null, "a/b/d", null, null ).iterator().next() ).getIdentifier() );

        // Test the longest matching pattern rule
        assertEquals( "Longest tail rule 1", 1, digester.getRules().match( null, "x/c/d", null, null ).size() );
        assertEquals( "Longest tail rule 2", "*/c/d",
                      ( (TestRule) digester.getRules().match( null, "x/c/d", null, null ).iterator().next() ).getIdentifier() );

    }

    /**
     * Test the basic stack mechanisms.
     */
    @Test
    public void testStackMethods()
    {

        Object value = null;

        // New stack must be empty
        assertEquals( "New stack is empty", 0, digester.getCount() );
        value = digester.peek();
        assertNull( "New stack peek() returns null", value );
        value = digester.pop();
        assertNull( "New stack pop() returns null", value );

        // Test pushing and popping activities
        digester.push( "First Item" );
        assertEquals( "Pushed one item size", 1, digester.getCount() );
        value = digester.peek();
        assertNotNull( "Peeked first item is not null", value );
        assertEquals( "Peeked first item value", "First Item", value );

        digester.push( "Second Item" );
        assertEquals( "Pushed two items size", 2, digester.getCount() );
        value = digester.peek();
        assertNotNull( "Peeked second item is not null", value );
        assertEquals( "Peeked second item value", "Second Item", value );

        value = digester.pop();
        assertEquals( "Popped stack size", 1, digester.getCount() );
        assertNotNull( "Popped second item is not null", value );
        assertEquals( "Popped second item value", "Second Item", value );
        value = digester.peek();
        assertNotNull( "Remaining item is not null", value );
        assertEquals( "Remaining item value", "First Item", value );
        assertEquals( "Remaining stack size", 1, digester.getCount() );

        // Cleared stack is empty
        digester.push( "Dummy Item" );
        digester.clear();
        assertEquals( "Cleared stack is empty", 0, digester.getCount() );
        value = digester.peek();
        assertNull( "Cleared stack peek() returns null", value );
        value = digester.pop();
        assertNull( "Cleared stack pop() returns null", value );

    }

    @Test
    public void testOnceAndOnceOnly()
        throws Exception
    {

        class TestConfigureDigester
            extends Digester
        {
            public int called = 0;

            public TestConfigureDigester()
            {
            }

            @Override
            protected void initialize()
            {
                called++;
            }
        }

        TestConfigureDigester digester = new TestConfigureDigester();

        String xml = "<?xml version='1.0'?><document/>";
        digester.parse( new StringReader( xml ) );

        assertEquals( "Initialize should be called once and only once", 1, digester.called );
    }

    @Test
    public void testBasicSubstitution()
        throws Exception
    {
        class TestSubRule
            extends Rule
        {
            public String body;

            public Attributes attributes;

            @Override
            public void begin( String namespace, String name, Attributes attributes )
            {
                this.attributes = new AttributesImpl( attributes );
            }

            @Override
            public void body( String namespace, String name, String text )
            {
                this.body = text;
            }
        }

        TestSubRule tsr = new TestSubRule();
        Digester digester = new Digester();
        digester.addRule( "alpha/beta", tsr );

        // it's not easy to transform dirty harry into the mighty circus - but let's give it a try
        String xml =
            "<?xml version='1.0'?><alpha><beta forname='Dirty' surname='Harry'>Do you feel luck punk?</beta></alpha>";
        InputSource in = new InputSource( new StringReader( xml ) );

        digester.parse( in );

        assertEquals( "Unsubstituted body text", "Do you feel luck punk?", tsr.body );
        assertEquals( "Unsubstituted number of attributes", 2, tsr.attributes.getLength() );
        assertEquals( "Unsubstituted forname attribute value", "Dirty", tsr.attributes.getValue( "forname" ) );
        assertEquals( "Unsubstituted surname attribute value", "Harry", tsr.attributes.getValue( "surname" ) );

        digester.setSubstitutor( new Substitutor()
        {
            @Override
            public Attributes substitute( Attributes attributes )
            {
                AttributesImpl results = new AttributesImpl();
                results.addAttribute( "", "python", "python", "CDATA", "Cleese" );
                return results;
            }

            @Override
            public String substitute( String bodyText )
            {
                return "And now for something completely different...";
            }
        } );

        // now transform into the full monty
        in = new InputSource( new StringReader( xml ) );
        digester.parse( in );

        assertEquals( "Substituted body text", "And now for something completely different...", tsr.body );
        assertEquals( "Substituted number of attributes", 1, tsr.attributes.getLength() );
        assertEquals( "Substituted python attribute value", "Cleese", tsr.attributes.getValue( "", "python" ) );
    }

    /** Tests the push-peek-pop cycle for a named stack */
    @Test
    public void testNamedStackPushPeekPop()
        throws Exception
    {
        BigDecimal archimedesAveragePi = new BigDecimal( "3.1418" );
        String testStackName = "org.apache.commons.digester3.tests.testNamedStackPushPeekPop";
        Digester digester = new Digester();
        assertTrue( "Stack starts empty:", digester.isEmpty( testStackName ) );
        digester.push( testStackName, archimedesAveragePi );
        assertEquals( "Peeked value:", archimedesAveragePi, digester.peek( testStackName ) );
        assertEquals( "Popped value:", archimedesAveragePi, digester.pop( testStackName ) );
        assertTrue( "Stack ends empty:", digester.isEmpty( testStackName ) );

        digester.push( testStackName, "1" );
        digester.push( testStackName, "2" );
        digester.push( testStackName, "3" );

        assertEquals( "Peek#1", "1", digester.peek( testStackName, 2 ) );
        assertEquals( "Peek#2", "2", digester.peek( testStackName, 1 ) );
        assertEquals( "Peek#3", "3", digester.peek( testStackName, 0 ) );
        assertEquals( "Peek#3a", "3", digester.peek( testStackName ) );

        try
        {
            // peek beyond stack
            digester.peek( testStackName, 3 );
            fail( "Peek#4 failed to throw an exception." );
        }
        catch ( EmptyStackException ex )
        {
            // ok, expected
        }

        try
        {
            // peek a nonexistent named stack
            digester.peek( "no.such.stack", 0 );
            fail( "Peeking a non-existent stack failed to throw an exception." );
        }
        catch ( EmptyStackException ex )
        {
            // ok, expected
        }
    }

    /** Tests that values are stored independently */
    @Test
    public void testNamedIndependence()
    {
        String testStackOneName = "org.apache.commons.digester3.tests.testNamedIndependenceOne";
        String testStackTwoName = "org.apache.commons.digester3.tests.testNamedIndependenceTwo";
        Digester digester = new Digester();
        digester.push( testStackOneName, "Tweedledum" );
        digester.push( testStackTwoName, "Tweedledee" );
        assertEquals( "Popped value one:", "Tweedledum", digester.pop( testStackOneName ) );
        assertEquals( "Popped value two:", "Tweedledee", digester.pop( testStackTwoName ) );
    }

    /** Tests popping named stack not yet pushed */
    @Test
    public void testPopNamedStackNotPushed()
    {
        String testStackName = "org.apache.commons.digester3.tests.testPopNamedStackNotPushed";
        Digester digester = new Digester();
        try
        {

            digester.pop( testStackName );
            fail( "Expected an EmptyStackException" );

        }
        catch ( EmptyStackException e )
        {
            // expected
        }

        try
        {

            digester.peek( testStackName );
            fail( "Expected an EmptyStackException" );

        }
        catch ( EmptyStackException e )
        {
            // expected
        }
    }

    /** Tests for isEmpty */
    @Test
    public void testNamedStackIsEmpty()
    {
        String testStackName = "org.apache.commons.digester3.tests.testNamedStackIsEmpty";
        Digester digester = new Digester();
        assertTrue( "A named stack that has no object pushed onto it yet should be empty",
                    digester.isEmpty( testStackName ) );

        digester.push( testStackName, "Some test value" );
        assertFalse( "A named stack that has an object pushed onto it should be not empty",
                     digester.isEmpty( testStackName ) );

        digester.peek( testStackName );
        assertFalse( "Peek should not effect whether the stack is empty", digester.isEmpty( testStackName ) );

        digester.pop( testStackName );
        assertTrue( "A named stack that has it's last object popped is empty", digester.isEmpty( testStackName ) );
    }

    /**
     * Test the Digester.getRoot method.
     */
    @Test
    public void testGetRoot()
        throws Exception
    {
        Digester digester = new Digester();
        digester.addRule( "root", new ObjectCreateRule( TestBean.class ) );

        String xml = "<root/>";
        InputSource in = new InputSource( new StringReader( xml ) );

        digester.parse( in );

        Object root = digester.getRoot();
        assertNotNull( "root object not retrieved", root );
        assertTrue( "root object not a TestRule instance", ( root instanceof TestBean ) );
    }

    /** Utility class for method testStackAction */
    private static class TrackingStackAction
        implements StackAction
    {
        public ArrayList<String> events = new ArrayList<String>();

        public Object onPush( Digester d, String stackName, Object o )
        {
            String msg = "push:" + stackName + ":" + o.toString();
            events.add( msg );

            String str = o.toString();
            if ( str.startsWith( "replpush" ) )
            {
                return new String( str );
            }
            return o;
        }

        public Object onPop( Digester d, String stackName, Object o )
        {
            String msg = "pop:" + stackName + ":" + o.toString();
            events.add( msg );
            String str = o.toString();
            if ( str.startsWith( "replpop" ) )
            {
                return new String( str );
            }
            return o;
        }
    }

    /**
     * Test custom StackAction subclasses.
     */
    @Test
    public void testStackAction()
    {
        TrackingStackAction action = new TrackingStackAction();

        Object obj1 = new String( "obj1" );
        Object obj2 = new String( "obj2" );
        Object obj3 = new String( "replpop.obj3" );
        Object obj4 = new String( "replpush.obj4" );

        Object obj8 = new String( "obj8" );
        Object obj9 = new String( "obj9" );

        Digester d = new Digester();
        d.setStackAction( action );

        assertEquals( 0, action.events.size() );
        d.push( obj1 );
        d.push( obj2 );
        d.push( obj3 );
        d.push( obj4 );

        assertNotNull( d.peek( 0 ) );
        // for obj4, a copy should have been pushed
        assertNotSame( obj4, d.peek( 0 ) );
        assertEquals( obj4, d.peek( 0 ) );
        // for obj3, replacement only occurs on pop
        assertSame( obj3, d.peek( 1 ) );
        assertSame( obj2, d.peek( 2 ) );
        assertSame( obj1, d.peek( 3 ) );

        Object obj4a = d.pop();
        Object obj3a = d.pop();
        Object obj2a = d.pop();
        Object obj1a = d.pop();

        assertFalse( obj4 == obj4a );
        assertEquals( obj4, obj4a );
        assertFalse( obj3 == obj4a );
        assertEquals( obj3, obj3a );
        assertSame( obj2, obj2a );
        assertSame( obj1, obj1a );

        d.push( "stack1", obj8 );
        d.push( "stack1", obj9 );
        Object obj9a = d.pop( "stack1" );
        Object obj8a = d.pop( "stack1" );

        assertSame( obj8, obj8a );
        assertSame( obj9, obj9a );

        assertEquals( 12, action.events.size() );
        assertEquals( "push:null:obj1", action.events.get( 0 ) );
        assertEquals( "push:null:obj2", action.events.get( 1 ) );
        assertEquals( "push:null:replpop.obj3", action.events.get( 2 ) );
        assertEquals( "push:null:replpush.obj4", action.events.get( 3 ) );
        assertEquals( "pop:null:replpush.obj4", action.events.get( 4 ) );
        assertEquals( "pop:null:replpop.obj3", action.events.get( 5 ) );
        assertEquals( "pop:null:obj2", action.events.get( 6 ) );
        assertEquals( "pop:null:obj1", action.events.get( 7 ) );

        assertEquals( "push:stack1:obj8", action.events.get( 8 ) );
        assertEquals( "push:stack1:obj9", action.events.get( 9 ) );
        assertEquals( "pop:stack1:obj9", action.events.get( 10 ) );
        assertEquals( "pop:stack1:obj8", action.events.get( 11 ) );
    }
}

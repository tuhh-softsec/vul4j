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

import static org.apache.commons.digester3.binder.DigesterLoader.newLoader;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * <p>
 * Test case for <code>BeanPropertySetterRule</code>. This contains tests for the main applications of the rule and two
 * more general tests of digester functionality used by this rule.
 */
public class BeanPropertySetterRuleTestCase
{

    // ----------------------------------------------------- Instance Variables

    /**
     * Simple test xml document used in the tests.
     */
    protected final static String TEST_XML = "<?xml version='1.0'?>" + "<root>ROOT BODY" + "<alpha>ALPHA BODY</alpha>"
        + "<beta>BETA BODY</beta>" + "<gamma>GAMMA BODY</gamma>" + "<delta>DELTA BODY</delta>" + "</root>";

    // ------------------------------------------------ Individual Test Methods

    /**
     * This is a general digester test but it fits into here pretty well. This tests that the rule calling order is
     * properly enforced.
     */
    @Test
    public void testDigesterRuleCallOrder()
        throws SAXException, IOException
    {

        final List<Rule> callOrder = new ArrayList<Rule>();

        Digester digester = newLoader( new AbstractRulesModule()
        {

            @Override
            protected void configure()
            {
                // add first test rule
                forPattern( "root/alpha" ).addRuleCreatedBy( new TestRule.TestRuleProvider( "first", callOrder ) );
                // add second test rule
                forPattern( "root/alpha" ).addRuleCreatedBy( new TestRule.TestRuleProvider( "second", callOrder ) );
                // add third test rule
                forPattern( "root/alpha" ).addRuleCreatedBy( new TestRule.TestRuleProvider( "third", callOrder ) );
            }

        }).newDigester();

        digester.parse( xmlTestReader() );

        // we should have nine entries in our list of calls

        assertEquals( "Nine calls should have been made.", 9, callOrder.size() );

        // begin should be called in the order added
        assertEquals( "First rule begin not called first.", "first", ( (TestRule) callOrder.get( 0 ) ).getIdentifier() );

        assertEquals( "Second rule begin not called second.", "second",
                      ( (TestRule) callOrder.get( 1 ) ).getIdentifier() );

        assertEquals( "Third rule begin not called third.", "third", ( (TestRule) callOrder.get( 2 ) ).getIdentifier() );

        // body text should be called in the order added
        assertEquals( "First rule body text not called first.", "first",
                      ( (TestRule) callOrder.get( 3 ) ).getIdentifier() );

        assertEquals( "Second rule body text not called second.", "second",
                      ( (TestRule) callOrder.get( 4 ) ).getIdentifier() );

        assertEquals( "Third rule body text not called third.", "third",
                      ( (TestRule) callOrder.get( 5 ) ).getIdentifier() );

        // end should be called in reverse order
        assertEquals( "Third rule end not called first.", "third", ( (TestRule) callOrder.get( 6 ) ).getIdentifier() );

        assertEquals( "Second rule end not called second.", "second", ( (TestRule) callOrder.get( 7 ) ).getIdentifier() );

        assertEquals( "First rule end not called third.", "first", ( (TestRule) callOrder.get( 8 ) ).getIdentifier() );

    }

    /**
     * This is a general digester test but it fits into here pretty well. This tests that the body text stack is
     * functioning correctly.
     */
    @Test
    public void testDigesterBodyTextStack()
        throws SAXException, IOException
    {
        final List<Rule> callOrder = new ArrayList<Rule>();

        Digester digester = newLoader(new AbstractRulesModule()
        {

            @Override
            protected void configure()
            {
                forPattern( "root" ).addRuleCreatedBy( new TestRule.TestRuleProvider( "root", callOrder ) );
                forPattern( "root/alpha" ).addRuleCreatedBy( new TestRule.TestRuleProvider( "root/alpha", callOrder ) );
                forPattern( "root/beta" ).addRuleCreatedBy( new TestRule.TestRuleProvider( "root/beta", callOrder ) );
                forPattern( "root/gamma" ).addRuleCreatedBy( new TestRule.TestRuleProvider( "root/gamma", callOrder ) );
            }

        }).newDigester();

        digester.parse( xmlTestReader() );

        assertEquals( "Root body text not set correct.", "ROOT BODY", ( (TestRule) callOrder.get( 0 ) ).getBodyText() );

        assertEquals( "Alpha body text not set correct.", "ALPHA BODY", ( (TestRule) callOrder.get( 1 ) ).getBodyText() );

        assertEquals( "Beta body text not set correct.", "BETA BODY", ( (TestRule) callOrder.get( 4 ) ).getBodyText() );

        assertEquals( "Gamma body text not set correct.", "GAMMA BODY", ( (TestRule) callOrder.get( 7 ) ).getBodyText() );

    }

    /**
     * Test that you can successfully set a given property
     */
    @Test
    public void testSetGivenProperty()
        throws SAXException, IOException
    {
        Digester digester = newLoader(new AbstractRulesModule()
        {

            @Override
            protected void configure()
            {
                forPattern( "root" ).createObject().ofType( SimpleTestBean.class );
                forPattern( "root" ).setBeanProperty().withName( "alpha" );

                // we'll set property beta with the body text of child element alpha
                forPattern( "root/alpha" ).setBeanProperty().withName( "beta" );
                // we'll leave property gamma alone

                // we'll set property delta (a write-only property) also
                forPattern( "root/delta" ).setBeanProperty().withName( "delta" );
            }

        }).newDigester();

        SimpleTestBean bean = digester.parse( xmlTestReader() );

        // check properties are set correctly
        assertEquals( "Property alpha not set correctly", "ROOT BODY", bean.getAlpha() );

        assertEquals( "Property beta not set correctly", "ALPHA BODY", bean.getBeta() );

        assertTrue( "Property gamma not set correctly", bean.getGamma() == null );

        assertEquals( "Property delta not set correctly", "DELTA BODY", bean.getDeltaValue() );

    }

    /**
     * Test that trying to set an unknown property throws an exception.
     */
    @Test
    public void testSetUnknownProperty()
    {
        Digester digester = newLoader(new AbstractRulesModule()
        {

            @Override
            protected void configure()
            {
                forPattern( "root" ).createObject().ofType( "org.apache.commons.digester3.SimpleTestBean" );
                forPattern( "root" ).setBeanProperty().withName( "alpha" );

                // attempt to set an unknown property name
                forPattern( "root/alpha" ).setBeanProperty().withName( "unknown" );
            }

        }).newDigester();

        // Attempt to parse the input
        try
        {
            SimpleTestBean bean = digester.parse( xmlTestReader() );
            fail( "Should have thrown NoSuchMethodException" );
            assertNotNull( bean ); // just to avoid compiler warning on unused variable
        }
        catch ( Exception e )
        {
            if ( e instanceof InvocationTargetException )
            {
                Throwable t = ( (InvocationTargetException) e ).getTargetException();
                if ( t instanceof NoSuchMethodException )
                {
                    // Expected result
                }
                else
                {
                    fail( "Should have thrown NoSuchMethodException, threw " + t );
                }
            }
        }

    }

    /**
     * Test that you can successfully automatically set properties.
     */
    @Test
    public void testAutomaticallySetProperties()
        throws SAXException, IOException
    {
        Digester digester = newLoader(new AbstractRulesModule()
        {

            @Override
            protected void configure()
            {
                forPattern( "root" ).createObject().ofType( "org.apache.commons.digester3.SimpleTestBean" );
                forPattern( "root/?" ).setBeanProperty();
            }

        }).newDigester( new ExtendedBaseRules() );

        SimpleTestBean bean = digester.parse( xmlTestReader() );

        // check properties are set correctly
        assertEquals( "Property alpha not set correctly", "ALPHA BODY", bean.getAlpha() );

        assertEquals( "Property beta not set correctly", "BETA BODY", bean.getBeta() );

        assertEquals( "Property gamma not set correctly", "GAMMA BODY", bean.getGamma() );

    }

    @Test
    public void extractPropertyNameFromAttribute() throws Exception
    {
        Employee expected = new Employee( "John", "Doe" );

        Employee actual = newLoader( new AbstractRulesModule()
        {

            @Override
            protected void configure()
            {
                forPattern( "employee" ).createObject().ofType( Employee.class );
                forPattern( "employee/property" ).setBeanProperty().extractPropertyNameFromAttribute( "name" );
            }

        } )
        .newDigester()
        .parse( getClass().getResource( "extractPropertyNameFromAttribute.xml" ) );

        assertEquals( expected.getFirstName(), actual.getFirstName() );
        assertEquals( expected.getLastName(), actual.getLastName() );
    }

    /**
     * Get input stream from {@link #TEST_XML}.
     */
    private Reader xmlTestReader()
    {
        return new StringReader( TEST_XML );
    }

}

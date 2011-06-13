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
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * <p>
 * Test case for <code>SetNestedPropertiesRule</code>. This contains tests for the main applications of the rule and two
 * more general tests of digester functionality used by this rule.
 */
public class SetNestedPropertiesRuleTestCase
{

    // ----------------------------------------------------- Instance Variables

    /**
     * Simple test xml document used in the tests.
     */
    protected final static String TEST_XML = "<?xml version='1.0'?>" + "<root>ROOT BODY" + "<alpha>ALPHA BODY</alpha>"
        + "<beta>BETA BODY</beta>" + "<gamma>GAMMA BODY</gamma>" + "<delta>DELTA BODY</delta>" + "</root>";

    // ------------------------------------------------ Individual Test Methods

    /**
     * Test that you can successfully automatically set properties.
     */
    @Test
    public void testAutomaticallySetProperties()
        throws SAXException, IOException
    {
        Digester digester = newLoader( new AbstractRulesModule()
        {

            @Override
            protected void configure()
            {
                forPattern( "root" ).createObject().ofType( "org.apache.commons.digester3.SimpleTestBean" )
                    .then()
                    .setNestedProperties();
            }

        }).newDigester();

        SimpleTestBean bean = digester.parse( xmlTestReader() );

        // check properties are set correctly
        assertEquals( "Property alpha not set correctly", "ALPHA BODY", bean.getAlpha() );

        assertEquals( "Property beta not set correctly", "BETA BODY", bean.getBeta() );

        assertEquals( "Property gamma not set correctly", "GAMMA BODY", bean.getGamma() );

        assertEquals( "Property delta not set correctly", "DELTA BODY", bean.getDeltaValue() );
    }

    /**
     * Test that it is an error when a child element exists but no corresponding java property exists.
     */
    @Test
    public void testMandatoryProperties()
        throws SAXException, IOException
    {
        Digester digester = newLoader( new AbstractRulesModule()
        {

            @Override
            protected void configure()
            {
                forPattern( "root" ).createObject().ofType( "org.apache.commons.digester3.SimpleTestBean" )
                    .then()
                    .setNestedProperties();
            }

        }).newDigester();

        String TEST_XML = "<?xml version='1.0'?>" + "<root>ROOT BODY" + "<badprop>ALPHA BODY</badprop>" + "</root>";

        try
        {
            SimpleTestBean bean = digester.parse( new StringReader( TEST_XML ) );

            // we should never get here...
            fail( "No exception thrown by parse when unknown child element found." );
            assertNotNull( bean ); // just to prevent compiler warning on unused var
        }
        catch ( org.xml.sax.SAXParseException e )
        {
            String msg = e.getMessage();
            if ( msg.indexOf( "badprop" ) >= 0 )
            {
                // ok, this is expected; there is no "setBadprop" method on the
                // SimpleTestBean class...
            }
            else
            {
                fail( "Unexpected parse exception:" + e.getMessage() );
            }
        }
    }

    /**
     * Test that you can customise the property mappings using the constructor which takes arrays-of-strings.
     */
    @Test
    public void testCustomisedProperties1()
        throws SAXException, IOException
    {
        Digester digester = newLoader( new AbstractRulesModule()
        {

            @Override
            protected void configure()
            {
                forPattern( "root" ).createObject().ofType( "org.apache.commons.digester3.SimpleTestBean" )
                    .then()
                    .setNestedProperties()
                        .addAlias( "alpha", null )
                        .addAlias( "gamma-alt", "gamma" )
                        .addAlias( "delta", null );
            }

        }).newDigester();

        String TEST_XML =
            "<?xml version='1.0'?>" + "<root>ROOT BODY" + "<alpha>ALPHA BODY</alpha>" + "<beta>BETA BODY</beta>"
                + "<gamma-alt>GAMMA BODY</gamma-alt>" + "<delta>DELTA BODY</delta>" + "</root>";

        SimpleTestBean bean = digester.parse( new StringReader( TEST_XML ) );

        // check properties are set correctly
        assertEquals( "Property alpha was not ignored (it should be)", null, bean.getAlpha() );

        assertEquals( "Property beta not set correctly", "BETA BODY", bean.getBeta() );

        assertEquals( "Property gamma not set correctly", "GAMMA BODY", bean.getGamma() );

        assertEquals( "Property delta was not ignored (it should be)", null, bean.getDeltaValue() );

        // check no bad rules object is left
        assertEquals( "Digester rules object not reset.", RulesBase.class, digester.getRules().getClass() );
    }

    /**
     * Test that you can ignore a single input xml element using the constructor which takes a single remapping.
     */
    @Test
    public void testCustomisedProperties2a()
        throws SAXException, IOException
    {
        Digester digester = newLoader( new AbstractRulesModule()
        {

            @Override
            protected void configure()
            {
                forPattern( "root" ).createObject().ofType( "org.apache.commons.digester3.SimpleTestBean" )
                    .then()
                    .setNestedProperties()
                        .addAlias( "alpha", null );
            }

        }).newDigester();

        String TEST_XML =
            "<?xml version='1.0'?>" + "<root>ROOT BODY" + "<alpha>ALPHA BODY</alpha>" + "<beta>BETA BODY</beta>"
                + "<gamma>GAMMA BODY</gamma>" + "<delta>DELTA BODY</delta>" + "</root>";

        SimpleTestBean bean = digester.parse( new StringReader( TEST_XML ) );

        // check properties are set correctly
        assertEquals( "Property alpha was not ignored (it should be)", null, bean.getAlpha() );

        assertEquals( "Property beta not set correctly", "BETA BODY", bean.getBeta() );

        assertEquals( "Property gamma not set correctly", "GAMMA BODY", bean.getGamma() );

        assertEquals( "Property delta not set correctly", "DELTA BODY", bean.getDeltaValue() );

        // check no bad rules object is left
        assertEquals( "Digester rules object not reset.", RulesBase.class, digester.getRules().getClass() );
    }

    /**
     * Test that you can customise the property mappings using the constructor which takes a single remapping.
     */
    @Test
    public void testCustomisedProperties2b()
        throws SAXException, IOException
    {
        Digester digester = newLoader( new AbstractRulesModule()
        {

            @Override
            protected void configure()
            {
                forPattern( "root" ).createObject().ofType( "org.apache.commons.digester3.SimpleTestBean" )
                    .then()
                    .setNestedProperties()
                        .addAlias( "alpha-alt", "alpha" );
            }

        }).newDigester();

        String TEST_XML =
            "<?xml version='1.0'?>" + "<root>ROOT BODY" + "<alpha-alt>ALPHA BODY</alpha-alt>"
                + "<beta>BETA BODY</beta>" + "<gamma>GAMMA BODY</gamma>" + "<delta>DELTA BODY</delta>" + "</root>";

        SimpleTestBean bean = digester.parse( new StringReader( TEST_XML ) );

        // check properties are set correctly
        assertEquals( "Property alpha not set correctly", "ALPHA BODY", bean.getAlpha() );

        assertEquals( "Property beta not set correctly", "BETA BODY", bean.getBeta() );

        assertEquals( "Property gamma not set correctly", "GAMMA BODY", bean.getGamma() );

        assertEquals( "Property delta not set correctly", "DELTA BODY", bean.getDeltaValue() );

        // check no bad rules object is left
        assertEquals( "Digester rules object not reset.", RulesBase.class, digester.getRules().getClass() );
    }

    /**
     * Test that:
     * <ul>
     * <li>you can have rules matching the same pattern as the SetNestedPropertiesRule,</li>
     * <li>you can have rules matching child elements of the rule,</li>
     * <li>the Rules object is reset nicely.</li>
     * </ul>
     */
    @Test
    public void testMultiRuleMatch()
        throws SAXException, IOException
    {
        Digester digester = newLoader( new AbstractRulesModule()
        {

            @Override
            protected void configure()
            {
                forPattern( "root/testbean" ).createObject().ofType( "org.apache.commons.digester3.SimpleTestBean" )
                    .then()
                    .setProperties()
                    .then()
                    .setNestedProperties();
                forPattern( "root/testbean/gamma/prop" ).setProperty( "name" ).extractingValueFromAttribute( "value" );
            }

        }).newDigester();

        String testXml =
            "<?xml version='1.0'?>" + "<root>" + "<testbean alpha='alpha-attr'>ROOT BODY" + "<beta>BETA BODY</beta>"
                + "<gamma>GAMMA " + "<prop name='delta' value='delta-prop'/>" + "BODY" + "</gamma>" + "</testbean>"
                + "</root>";

        Reader reader = new StringReader( testXml );

        SimpleTestBean bean = digester.parse( reader );

        assertNotNull( "No object created", bean );

        // check properties are set correctly
        assertEquals( "Property alpha not set correctly", "alpha-attr", bean.getAlpha() );

        assertEquals( "Property beta not set correctly", "BETA BODY", bean.getBeta() );

        assertEquals( "Property gamma not set correctly", "GAMMA BODY", bean.getGamma() );

        assertEquals( "Property delta not set correctly", "delta-prop", bean.getDeltaValue() );

        // check no bad rules object is left
        assertEquals( "Digester rules object not reset.", RulesBase.class, digester.getRules().getClass() );
    }

    /**
     * Test that unknown child elements trigger an exception.
     */
    @Test
    public void testUnknownChildrenCausesException()
        throws SAXException, IOException
    {
        Digester digester = newLoader( new AbstractRulesModule()
        {

            @Override
            protected void configure()
            {
                forPattern( "root" ).createObject().ofType( "org.apache.commons.digester3.SimpleTestBean" )
                    .then()
                    .setNestedProperties();
            }

        }).newDigester();

        String testXml =
            "<?xml version='1.0'?>" + "<root>" + "<testbean>" + "<beta>BETA BODY</beta>" + "<foo>GAMMA</foo>"
                + "</testbean>" + "</root>";

        Reader reader = new StringReader( testXml );

        try
        {
            SimpleTestBean bean = digester.parse( reader );
            fail( "Expected to generate an exception." );
            assertNotNull( bean ); // just to prevent compiler warning on unused var
        }
        catch ( SAXException e )
        {
            Exception nested = e.getException();
            if ( ( nested == null ) || !( nested instanceof NoSuchMethodException ) )
            {
                // nope, not the sort of exception we expected
                throw e;
            }
        }
    }

    /**
     * Test that unknown child elements are allowed if the appropriate flag is set.
     */
    @Test
    public void testUnknownChildrenExceptionOverride()
        throws SAXException, IOException
    {
        Digester digester = newLoader( new AbstractRulesModule()
        {

            @Override
            protected void configure()
            {
                forPattern( "root" ).createObject().ofType( "org.apache.commons.digester3.SimpleTestBean" )
                    .then()
                    .setNestedProperties().allowUnknownChildElements( true );
            }

        }).newDigester();

        String testXml =
            "<?xml version='1.0'?>" + "<root>" + "<testbean>" + "<beta>BETA BODY</beta>" + "<foo>GAMMA</foo>"
                + "</testbean>" + "</root>";

        Reader reader = new StringReader( testXml );

        SimpleTestBean bean = digester.parse( reader );
        assertNotNull( bean );
    }

    /**
     * Test that the rule works in a sane manner when the associated pattern is a wildcard such that the rule matches
     * one of its own child elements.
     * <p>
     * See bugzilla entry 31393.
     */
    @Test
    public void testRecursiveNestedProperties()
        throws SAXException, IOException
    {
        Digester digester = newLoader( new AbstractRulesModule()
        {

            @Override
            protected void configure()
            {
                forPattern( "*/testbean" ).createObject().ofType( "org.apache.commons.digester3.SimpleTestBean" )
                    .then()
                    .setNestedProperties().allowUnknownChildElements( true );
            }

        }).newDigester();

        String testXml =
            "<?xml version='1.0'?>" + "<testbean>" + "<beta>BETA BODY</beta>" + "<testbean>" + "<beta>BETA BODY</beta>"
                + "</testbean>" + "</testbean>";

        Reader reader = new StringReader( testXml );

        SimpleTestBean bean = digester.parse( reader );
        assertNotNull( bean );
    }

    /**
     * Get input stream from {@link #TEST_XML}.
     */
    private Reader xmlTestReader()
    {
        return new StringReader( TEST_XML );
    }

}

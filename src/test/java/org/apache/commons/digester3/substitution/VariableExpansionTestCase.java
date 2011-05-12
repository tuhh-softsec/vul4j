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

package org.apache.commons.digester3.substitution;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.commons.digester3.CallMethodRule;
import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.SimpleTestBean;
import org.apache.commons.digester3.substitution.MultiVariableExpander;
import org.apache.commons.digester3.substitution.VariableSubstitutor;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * <p>
 * Test Case for the variable expansion facility in Digester.
 * 
 * @author Simon Kitching
 * @version $Revision$ $Date$
 */

public class VariableExpansionTestCase
{

    // --------------------------------------------------- Overall Test Methods

    // method used in tests4
    private LinkedList<SimpleTestBean> simpleTestBeans = new LinkedList<SimpleTestBean>();

    public void addSimpleTestBean( SimpleTestBean bean )
    {
        simpleTestBeans.add( bean );
    }

    // implementation of source shared by the variable expander and
    // is updatable during digesting via an Ant-like property element
    private HashMap<String, Object> mutableSource = new HashMap<String, Object>();

    /**
     * Used in test case "testExpansionWithMutableSource", where the set of variables available to be substituted into
     * the xml is updated as the xml is parsed.
     */
    public void addProperty( String key, String value )
    {
        mutableSource.put( key, value );
    }

    /**
     * Creates a Digester configured to show Ant-like capability.
     * 
     * @return a Digester with rules and variable substitutor
     */
    private Digester createDigesterThatCanDoAnt()
    {
        Digester digester = new Digester();

        MultiVariableExpander expander = new MultiVariableExpander();
        expander.addSource( "$", mutableSource );
        digester.setSubstitutor( new VariableSubstitutor( expander ) );

        int useRootObj = -1;
        Class<?>[] callerArgTypes = new Class[] { String.class, String.class };
        CallMethodRule caller = new CallMethodRule( useRootObj, "addProperty", callerArgTypes.length, callerArgTypes );
        digester.addRule( "root/property", caller );
        digester.addCallParam( "root/property", 0, "name" );
        digester.addCallParam( "root/property", 1, "value" );

        digester.addObjectCreate( "root/bean", SimpleTestBean.class );
        digester.addSetProperties( "root/bean" );
        digester.addSetNext( "root/bean", "addSimpleTestBean" );
        return digester;
    }

    // ------------------------------------------------ Individual Test Methods

    /**
     * Test that by default no expansion occurs.
     */
    @Test
    public void testNoExpansion()
        throws SAXException, IOException
    {

        String xml = "<root alpha='${attr1}' beta='var{attr2}'/>";
        StringReader input = new StringReader( xml );
        Digester digester = new Digester();

        // Configure the digester as required
        digester.addObjectCreate( "root", SimpleTestBean.class );
        digester.addSetProperties( "root" );

        // Parse our test input.
        SimpleTestBean root = digester.parse( input );

        assertNotNull( "Digester returned no object", root );

        assertEquals( "${attr1}", root.getAlpha() );
        assertEquals( "var{attr2}", root.getBeta() );
    }

    /**
     * Test that a MultiVariableExpander with no sources does no expansion.
     */
    @Test
    public void testExpansionWithNoSource()
        throws SAXException, IOException
    {

        String xml = "<root alpha='${attr1}' beta='var{attr2}'/>";
        StringReader input = new StringReader( xml );
        Digester digester = new Digester();

        // Configure the digester as required
        MultiVariableExpander expander = new MultiVariableExpander();
        digester.setSubstitutor( new VariableSubstitutor( expander ) );
        digester.addObjectCreate( "root", SimpleTestBean.class );
        digester.addSetProperties( "root" );

        // Parse our test input.
        SimpleTestBean root = digester.parse( input );

        assertNotNull( "Digester returned no object", root );

        assertEquals( "${attr1}", root.getAlpha() );
        assertEquals( "var{attr2}", root.getBeta() );
    }

    /**
     * Test that a MultiVariableExpander with multiple sources works. It also tests that expansion works ok where
     * multiple elements exist.
     */
    @Test
    public void testExpansionWithMultipleSources()
        throws SAXException, IOException
    {

        String xml =
            "<root>" + "<bean alpha='${attr1}' beta='var{attr1}'/>" + "<bean alpha='${attr2}' beta='var{attr2}'/>"
                + "</root>";

        StringReader input = new StringReader( xml );
        Digester digester = new Digester();

        // Configure the digester as required
        HashMap<String, Object> source1 = new HashMap<String, Object>();
        source1.put( "attr1", "source1.attr1" );
        source1.put( "attr2", "source1.attr2" ); // should not be used

        HashMap<String, Object> source2 = new HashMap<String, Object>();
        source2.put( "attr1", "source2.attr1" ); // should not be used
        source2.put( "attr2", "source2.attr2" );

        MultiVariableExpander expander = new MultiVariableExpander();
        expander.addSource( "$", source1 );
        expander.addSource( "var", source2 );

        digester.setSubstitutor( new VariableSubstitutor( expander ) );
        digester.addObjectCreate( "root/bean", SimpleTestBean.class );
        digester.addSetProperties( "root/bean" );
        digester.addSetNext( "root/bean", "addSimpleTestBean" );

        // Parse our test input.
        this.simpleTestBeans.clear();
        digester.push( this );
        digester.parse( input );

        assertEquals( 2, this.simpleTestBeans.size() );

        {
            SimpleTestBean bean = this.simpleTestBeans.get( 0 );
            assertEquals( "source1.attr1", bean.getAlpha() );
            assertEquals( "source2.attr1", bean.getBeta() );
        }

        {
            SimpleTestBean bean = this.simpleTestBeans.get( 1 );
            assertEquals( "source1.attr2", bean.getAlpha() );
            assertEquals( "source2.attr2", bean.getBeta() );
        }
    }

    /**
     * Test expansion of text in element bodies.
     */
    @Test
    public void testBodyExpansion()
        throws SAXException, IOException
    {

        String xml = "<root>" + "Twas noun{1} and the noun{2}" + " did verb{1} and verb{2} in the noun{3}" + "</root>";

        StringReader input = new StringReader( xml );
        Digester digester = new Digester();

        // Configure the digester as required
        HashMap<String, Object> nouns = new HashMap<String, Object>();
        nouns.put( "1", "brillig" );
        nouns.put( "2", "slithy toves" );
        nouns.put( "3", "wabe" );

        HashMap<String, Object> verbs = new HashMap<String, Object>();
        verbs.put( "1", "gyre" );
        verbs.put( "2", "gimble" );

        MultiVariableExpander expander = new MultiVariableExpander();
        expander.addSource( "noun", nouns );
        expander.addSource( "verb", verbs );
        digester.setSubstitutor( new VariableSubstitutor( expander ) );

        digester.addObjectCreate( "root", SimpleTestBean.class );
        digester.addCallMethod( "root", "setAlpha", 0 );

        // Parse our test input.
        SimpleTestBean root = digester.parse( input );

        assertNotNull( "Digester returned no object", root );

        assertEquals( "Twas brillig and the slithy toves" + " did gyre and gimble in the wabe", root.getAlpha() );
    }

    /**
     * Test that an unknown variable causes a RuntimeException.
     */
    @Test
    public void testExpansionException()
        throws IOException
    {

        String xml = "<root alpha='${attr1}'/>";
        StringReader input = new StringReader( xml );
        Digester digester = new Digester();

        // Configure the digester as required
        MultiVariableExpander expander = new MultiVariableExpander();
        expander.addSource( "$", new HashMap<String, Object>() );
        digester.setSubstitutor( new VariableSubstitutor( expander ) );

        digester.addObjectCreate( "root", SimpleTestBean.class );
        digester.addSetProperties( "root" );

        // Parse our test input.
        try
        {
            digester.parse( input );
            fail( "Exception expected due to unknown variable." );
        }
        catch ( SAXException e )
        {
            // expected, due to reference to undefined variable
        }
    }

    /**
     * First of two tests added to verify that the substitution framework is capable of processing Ant-like properties.
     * The tests above essentially verify that if a property was pre-set (e.g. using the "-D" option to Ant), then the
     * property could be expanded via a variable used either in an attribute or in body text. This test shows that if
     * properties were also set while processing a document, you could still perform variable expansion (i.e. just like
     * using the "property" task in Ant).
     * 
     * @throws IOException
     * @throws SAXException
     */
    @Test
    public void testExpansionWithMutableSource()
        throws SAXException, IOException
    {
        String xml = "<root>" + "<property name='attr' value='prop.value'/>" + "<bean alpha='${attr}'/>" + "</root>";
        StringReader input = new StringReader( xml );
        Digester digester = createDigesterThatCanDoAnt();

        simpleTestBeans.clear();
        digester.push( this );
        digester.parse( input );

        assertEquals( 1, simpleTestBeans.size() );
        SimpleTestBean bean = simpleTestBeans.get( 0 );
        assertEquals( "prop.value", bean.getAlpha() );
    }

    /**
     * Second of two tests added to verify that the substitution framework is capable of processing Ant-like properties.
     * This test shows that if properties were also set while processing a document, the resulting variables could also
     * be expanded within a property element. This is thus effectively a "closure" test, since it shows that the
     * mechanism used to bind properties is also capable of having property values that are driven by property
     * variables.
     * 
     * @throws IOException
     * @throws SAXException
     */
    @Test
    public void testExpansionOfPropertyInProperty()
        throws SAXException, IOException
    {
        String xml =
            "<root>" + "<property name='attr1' value='prop.value1'/>"
                + "<property name='attr2' value='substituted-${attr1}'/>" + "<bean alpha='${attr2}'/>" + "</root>";
        StringReader input = new StringReader( xml );
        Digester digester = createDigesterThatCanDoAnt();

        simpleTestBeans.clear();
        digester.push( this );
        digester.parse( input );

        assertEquals( 1, simpleTestBeans.size() );
        SimpleTestBean bean = simpleTestBeans.get( 0 );
        assertEquals( "substituted-prop.value1", bean.getAlpha() );
    }

}

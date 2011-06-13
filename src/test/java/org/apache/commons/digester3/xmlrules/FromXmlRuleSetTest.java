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

package org.apache.commons.digester3.xmlrules;

import static org.apache.commons.digester3.binder.DigesterLoader.newLoader;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.digester3.Address;
import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.ObjectCreationFactoryTestImpl;
import org.apache.commons.digester3.binder.RulesModule;
import org.junit.Test;
import org.xml.sax.InputSource;

/**
 * Tests loading Digester rules from an XML file.
 */

public class FromXmlRuleSetTest
{

    private final RulesModule createRules( final URL xmlRules )
    {
        return new FromXmlRulesModule()
        {

            @Override
            protected void loadRules()
            {
                loadXMLRules( xmlRules );
            }

        };
    }

    private final RulesModule createRules( final String xmlText )
    {
        return new FromXmlRulesModule()
        {

            @Override
            protected void loadRules()
            {
                loadXMLRulesFromText( xmlText );
            }

        };
    }

    /**
     * Tests the DigesterLoader.createDigester(), with multiple
     * included rule sources: testrules.xml includes another rules xml
     * file, and also includes programmatically created rules.
     */
    @Test
    public void testCreateDigester()
        throws Exception
    {
        URL rules = getClass().getResource( "testrules.xml" );
        URL input = getClass().getResource( "test.xml" );

        Digester digester = newLoader( createRules( rules ) ).newDigester();
        digester.push( new ArrayList<Object>() );
        Object root = digester.parse( input.openStream() );
        assertEquals( "[foo1 baz1 foo2, foo3 foo4]", root.toString() );
    }

    /**
     * Tests the DigesterLoader.load(), with multiple included rule
     * sources: testrules.xml includes another rules xml file, and
     * also includes programmatically created rules.
     */
    @Test
    public void testLoad1()
        throws Exception
    {
        ClassLoader classLoader = getClass().getClassLoader();
        URL rules = getClass().getResource( "testrules.xml" );
        URL input = getClass().getResource( "test.xml" );

        Digester digester = newLoader( createRules( rules ) ).setClassLoader( classLoader ).newDigester();
        digester.push( new ArrayList<Object>() );

        Object root = digester.parse( input );
        if ( !( root instanceof ArrayList<?> ) )
        {
            fail( "Unexpected object returned from DigesterLoader. Expected ArrayList; got "
                + root.getClass().getName() );
        }
        assertEquals( "[foo1 baz1 foo2, foo3 foo4]", root.toString() );

        @SuppressWarnings( "unchecked" )
        // root is an ArrayList
        ArrayList<Object> al = (ArrayList<Object>) root;
        Object obj = al.get( 0 );
        if ( !( obj instanceof ObjectTestImpl ) )
        {
            fail( "Unexpected object returned from DigesterLoader. Expected TestObject; got "
                + obj.getClass().getName() );
        }
        ObjectTestImpl to = (ObjectTestImpl) obj;
        assertEquals( new Long( 555 ), to.getLongValue() );
        assertEquals( "foo", to.getMapValue( "test1" ) );
        assertEquals( "bar", to.getMapValue( "test2" ) );
    }

    /**
     * The same as testLoad1, exception the input file is passed to
     * DigesterLoader as an InputStream instead of a URL.
     */
    @Test
    public void testLoad2()
        throws Exception
    {
        URL rules = getClass().getResource( "testrules.xml" );
        InputStream input = getClass().getResourceAsStream( "test.xml" );
        Digester digester =
            newLoader( createRules( rules ) ).setClassLoader( this.getClass().getClassLoader() ).newDigester();
        digester.push( new ArrayList<Object>() );

        ArrayList<Object> list = digester.parse( input );

        assertEquals( list.toString(), "[foo1 baz1 foo2, foo3 foo4]" );
        assertEquals( "Wrong number of classes created", 2, list.size() );
        assertEquals( "Pushed first", true, ( (ObjectTestImpl) list.get( 0 ) ).isPushed() );
        assertEquals( "Didn't push second", false, ( (ObjectTestImpl) list.get( 1 ) ).isPushed() );
        assertTrue( "Property was set properly",
                    ( (ObjectTestImpl) list.get( 0 ) ).getProperty().equals( "I am a property!" ) );
    }

    /**
     */
    @Test
    public void testSetCustomProperties()
        throws Exception
    {
        URL rules = this.getClass().getResource( "testPropertyAliasRules.xml" );
        InputStream input =
            getClass().getClassLoader().getResource( "org/apache/commons/digester3/Test7.xml" ).openStream();

        Digester digester =
            newLoader( createRules( rules ) ).setClassLoader( this.getClass().getClassLoader() ).newDigester();
        digester.push( new ArrayList<Object>() );

        Object obj = digester.parse( input );

        if ( !( obj instanceof ArrayList<?> ) )
        {
            fail( "Unexpected object returned from DigesterLoader. Expected ArrayList; got " + obj.getClass().getName() );
        }

        @SuppressWarnings("unchecked") // root is an ArrayList of Address
        ArrayList<Address> root = (ArrayList<Address>) obj;

        assertEquals( "Wrong array size", 4, root.size() );

        // note that the array is in popped order (rather than pushed)

        Address add = root.get( 0 );
        Address addressOne = add;
        assertEquals( "(1) Street attribute", "New Street", addressOne.getStreet() );
        assertEquals( "(1) City attribute", "Las Vegas", addressOne.getCity() );
        assertEquals( "(1) State attribute", "Nevada", addressOne.getState() );

        add = root.get( 1 );
        Address addressTwo = add;
        assertEquals( "(2) Street attribute", "Old Street", addressTwo.getStreet() );
        assertEquals( "(2) City attribute", "Portland", addressTwo.getCity() );
        assertEquals( "(2) State attribute", "Oregon", addressTwo.getState() );

        add = root.get( 2 );
        Address addressThree = add;
        assertEquals( "(3) Street attribute", "4th Street", addressThree.getStreet() );
        assertEquals( "(3) City attribute", "Dayton", addressThree.getCity() );
        assertEquals( "(3) State attribute", "US", addressThree.getState() );

        add = root.get( 3 );
        Address addressFour = add;
        assertEquals( "(4) Street attribute", "6th Street", addressFour.getStreet() );
        assertEquals( "(4) City attribute", "Cleveland", addressFour.getCity() );
        assertEquals( "(4) State attribute", "Ohio", addressFour.getState() );
    }

    @Test
    public void testFactoryCreateRule()
        throws Exception
    {
        URL rules = getClass().getResource( "testfactory.xml" );
        String xml = "<?xml version='1.0' ?><root one='good' two='bad' three='ugly'><foo/></root>";

        Digester digester =
            newLoader( createRules( rules ) ).setClassLoader( this.getClass().getClassLoader() ).newDigester();
        digester.push( new ArrayList<ObjectCreationFactoryTestImpl>() );

        Object obj = digester.parse( new StringReader( xml ) );
        if ( !( obj instanceof ArrayList<?> ) )
        {
            fail( "Unexpected object returned from DigesterLoader. Expected ArrayList; got " + obj.getClass().getName() );
        }

        @SuppressWarnings("unchecked") // root is an ArrayList of TestObjectCreationFactory
        ArrayList<ObjectCreationFactoryTestImpl> list = (ArrayList<ObjectCreationFactoryTestImpl>) obj;

        assertEquals( "List should contain only the factory object", list.size(), 1 );
        ObjectCreationFactoryTestImpl factory = list.get( 0 );
        assertEquals( "Object create not called(1)", factory.called, true );
        assertEquals( "Attribute not passed (1)", factory.attributes.getValue( "one" ), "good" );
        assertEquals( "Attribute not passed (2)", factory.attributes.getValue( "two" ), "bad" );
        assertEquals( "Attribute not passed (3)", factory.attributes.getValue( "three" ), "ugly" );
    }

    @Test
    public void testFactoryIgnoreCreateRule()
        throws Exception
    {
        URL rules = getClass().getResource( "testfactoryignore.xml" );

        String xml = "<?xml version='1.0' ?><root one='good' two='bad' three='ugly'><foo/></root>";
        try {
            newLoader(createRules(rules)).newDigester().parse(new StringReader(xml));
        } catch (Exception e) {
            fail("This exception should have been ignored: " + e.getClass().getName());
        }
    }

    @Test
    public void testFactoryNotIgnoreCreateRule()
        throws Exception
    {
        URL rules = getClass().getResource( "testfactorynoignore.xml" );

        String xml = "<?xml version='1.0' ?><root one='good' two='bad' three='ugly'><foo/></root>";
        try
        {
            newLoader( createRules( rules ) ).newDigester().parse( new StringReader( xml ) );
            fail( "Exception should have been propagated from create method." );
        }
        catch ( Exception e )
        {
            /* What we expected */
            assertEquals( org.xml.sax.SAXParseException.class, e.getClass() );
        }
    }

    @Test
    public void testCallParamRule()
        throws Exception
    {
        URL rules = getClass().getResource( "test-call-param-rules.xml" );

         String xml = "<?xml version='1.0' ?>"
                      + "<root><foo attr='long'><bar>short</bar><foobar><ping>tosh</ping></foobar></foo></root>";

        CallParamTestObject testObject = new CallParamTestObject();

        Digester digester =
            newLoader( createRules( rules ) ).setClassLoader( this.getClass().getClassLoader() ).newDigester();
        digester.push( testObject );
        digester.parse( new StringReader( xml ) );

        assertEquals( "Incorrect left value", "long", testObject.getLeft() );
        assertEquals( "Incorrect middle value", "short", testObject.getMiddle() );
        assertEquals( "Incorrect right value", "", testObject.getRight() );
     }

    @Test
    public void testInputSourceLoader() throws Exception {
        String rulesXml = "<?xml version='1.0'?>"
                + "<digester-rules>"
                + " <pattern value='root'>"
                + "   <pattern value='foo'>"
                + "     <call-method-rule methodname='triple' paramcount='3'"
                + "            paramtypes='java.lang.String,java.lang.String,java.lang.String'/>"
                + "     <call-param-rule paramnumber='0' attrname='attr'/>"
                + "        <pattern value='bar'>"
                + "            <call-param-rule paramnumber='1' from-stack='false'/>"
                + "        </pattern>"
                + "        <pattern value='foobar'>"
                + "            <object-create-rule classname='java.lang.String'/>"
                + "            <pattern value='ping'>"
                + "                <call-param-rule paramnumber='2' from-stack='true'/>"
                + "            </pattern>"
                + "         </pattern>"
                + "   </pattern>"
                + " </pattern>"
                + "</digester-rules>";
                
        String xml = "<?xml version='1.0' ?>"
                     + "<root><foo attr='long'><bar>short</bar><foobar><ping>tosh</ping></foobar></foo></root>";

        CallParamTestObject testObject = new CallParamTestObject();

        Digester digester = newLoader( createRules( rulesXml ) ).newDigester();
        digester.push( testObject );
        digester.parse( new StringReader( xml ) );

        assertEquals( "Incorrect left value", "long", testObject.getLeft() );
        assertEquals( "Incorrect middle value", "short", testObject.getMiddle() );
        assertEquals( "Incorrect right value", "", testObject.getRight() );
    }

    /** 
     * Test the FromXmlRules.addRuleInstances(digester, path) method, ie
     * test loading rules at a base position other than the root.
     */
    @Test
    public void testBasePath()
        throws Exception
    {
        String xmlRules =
            "<?xml version='1.0'?>"
            + "<digester-rules>"
            + "   <pattern value='root/foo'>"
            + "      <call-method-rule methodname='setProperty' usingElementBodyAsArgument='true' />"
            + "   </pattern>"
            + "</digester-rules>";

        String xml =
            "<?xml version='1.0'?>"
            + "<root>"
            + "  <foo>success</foo>"
            + "</root>";

        ObjectTestImpl testObject = new ObjectTestImpl();
        Digester digester = newLoader( createRules( xmlRules ) ).newDigester();

        digester.push( testObject );
        digester.parse( new InputSource( new StringReader( xml ) ) );

        assertEquals( "success", testObject.getProperty() );
    }
}

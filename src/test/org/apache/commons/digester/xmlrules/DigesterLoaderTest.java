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


package org.apache.commons.digester.xmlrules;


import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.digester.Address;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.TestObjectCreationFactory;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Tests loading Digester rules from an XML file.
 *
 * @author David H. Martin - Initial Contribution
 * @author Scott Sanders   - Added ASL, removed external dependencies
 */

public class DigesterLoaderTest extends TestCase {

    public DigesterLoaderTest(java.lang.String testName) {
        super(testName);
    }

    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static junit.framework.Test suite() {
        TestSuite suite = new TestSuite(DigesterLoaderTest.class);

        return suite;
    }

    /**
     * Tests the DigesterLoader.createDigester(), with multiple
     * included rule sources: testrules.xml includes another rules xml
     * file, and also includes programmatically created rules.
     */
    public void testCreateDigester() throws Exception {
        URL rules = getClass().getClassLoader().getResource("org/apache/commons/digester/xmlrules/testrules.xml");
        URL input = getClass().getClassLoader().getResource("org/apache/commons/digester/xmlrules/test.xml");
        assertNotNull("The test could not locate testrules.xml", rules);
        assertNotNull("The test could not locate test.xml", input);
        Digester digester = DigesterLoader.createDigester(rules);
        digester.push(new ArrayList<Object>());
        Object root = digester.parse(input.openStream());
        assertEquals("[foo1 baz1 foo2, foo3 foo4]",root.toString());
    }

    /**
     * Tests the DigesterLoader.load(), with multiple included rule
     * sources: testrules.xml includes another rules xml file, and
     * also includes programmatically created rules.
     */
    public void testLoad1() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        URL rules = classLoader.getResource("org/apache/commons/digester/xmlrules/testrules.xml");
        URL input = classLoader.getResource("org/apache/commons/digester/xmlrules/test.xml");
        assertNotNull("The test could not locate testrules.xml", rules);
        assertNotNull("The test could not locate test.xml", input);
        Object root = DigesterLoader.load(rules, classLoader, input, new ArrayList<Object>());
        if (!(root instanceof ArrayList<?>)) {
            fail("Unexpected object returned from DigesterLoader. Expected ArrayList; got " + root.getClass().getName());
        }
        assertEquals( "[foo1 baz1 foo2, foo3 foo4]",root.toString());

        @SuppressWarnings("unchecked") // root is an ArrayList
        ArrayList<Object> al = (ArrayList<Object>)root;
        Object obj = al.get(0);
        if (! (obj instanceof TestObject)) {
            fail("Unexpected object returned from DigesterLoader. Expected TestObject; got " + obj.getClass().getName());
        }
        TestObject to = (TestObject)obj;
        assertEquals(new Long(555),to.getLongValue());
        assertEquals( "foo", to.getMapValue( "test1" ) );
        assertEquals( "bar", to.getMapValue( "test2" ) );
    }

    /**
     * The same as testLoad1, exception the input file is passed to
     * DigesterLoader as an InputStream instead of a URL.
     */
    public void testLoad2() throws Exception {
        URL rules = getClass().getClassLoader().getResource("org/apache/commons/digester/xmlrules/testrules.xml");
        InputStream input = getClass().getClassLoader().getResource("org/apache/commons/digester/xmlrules/test.xml").openStream();
        Object root = DigesterLoader.load(rules, getClass().getClassLoader(), input, new ArrayList<Object>());
        if (!(root instanceof ArrayList<?>)) {
            fail("Unexpected object returned from DigesterLoader. Expected ArrayList; got " + root.getClass().getName());
        }
        @SuppressWarnings("unchecked") // root is an ArrayList
        ArrayList<Object> list = (ArrayList<Object>) root;
        assertEquals(root.toString(), "[foo1 baz1 foo2, foo3 foo4]");
        assertEquals("Wrong number of classes created", 2 , list.size());
        assertEquals("Pushed first", true , ((TestObject)list.get(0)).isPushed());
        assertEquals("Didn't push second", false , ((TestObject)list.get(1)).isPushed());
        assertTrue("Property was set properly", ((TestObject)list.get(0)).getProperty().equals("I am a property!") ); 
    }


    /**
     * Validates that circular includes are detected and result in an exception
     */
    public void testCircularInclude1() {
        URL rules = ClassLoader.getSystemResource("org/apache/commons/digester/xmlrules/testCircularRules.xml");
        try {
            Digester digester = DigesterLoader.createDigester(rules);
            assertNotNull(digester); // just to prevent compiler warning on unused var
        } catch (Exception ex) {
            return;
        }
        fail("Creating a digester with circular rules should have thrown CircularIncludeException.");
    }


    /**
     */
    public void testSetCustomProperties() throws Exception {
        URL rules = getClass().getClassLoader().getResource
            ("org/apache/commons/digester/xmlrules/testPropertyAliasRules.xml");
        InputStream input = getClass().getClassLoader().getResource
            ("org/apache/commons/digester/Test7.xml").openStream();
            
        Object obj = DigesterLoader.load(
                                        rules, 
                                        getClass().getClassLoader(), 
                                        input, 
                                        new ArrayList<Address>());
                                        
        if (!(obj instanceof ArrayList<?>)) {
            fail(
                "Unexpected object returned from DigesterLoader. Expected ArrayList; got " 
                + obj.getClass().getName());
        }
        
        @SuppressWarnings("unchecked") // root is an ArrayList of Address
        ArrayList<Address> root = (ArrayList<Address>) obj;                
        
        assertEquals("Wrong array size", 4, root.size());
        
        // note that the array is in popped order (rather than pushed)
         
        Address add = root.get(0);
        Address addressOne = add;
        assertEquals("(1) Street attribute", "New Street", addressOne.getStreet());
        assertEquals("(1) City attribute", "Las Vegas", addressOne.getCity());
        assertEquals("(1) State attribute", "Nevada", addressOne.getState());
        
        add = root.get(1);
        Address addressTwo = add;
        assertEquals("(2) Street attribute", "Old Street", addressTwo.getStreet());
        assertEquals("(2) City attribute", "Portland", addressTwo.getCity());
        assertEquals("(2) State attribute", "Oregon", addressTwo.getState());
        
        add = root.get(2);
        Address addressThree = add;
        assertEquals("(3) Street attribute", "4th Street", addressThree.getStreet());
        assertEquals("(3) City attribute", "Dayton", addressThree.getCity());
        assertEquals("(3) State attribute", "US" , addressThree.getState());
       
        add = root.get(3);
        Address addressFour = add;
        assertEquals("(4) Street attribute", "6th Street", addressFour.getStreet());
        assertEquals("(4) City attribute", "Cleveland", addressFour.getCity());
        assertEquals("(4) State attribute", "Ohio", addressFour.getState());
        
    }
    
   public void testFactoryCreateRule() throws Exception {
        URL rules = getClass().getClassLoader().getResource
            ("org/apache/commons/digester/xmlrules/testfactory.xml");
            
        String xml = "<?xml version='1.0' ?><root one='good' two='bad' three='ugly'><foo/></root>";
        Object obj = DigesterLoader.load(
                                        rules, 
                                        getClass().getClassLoader(), 
                                        new StringReader(xml), 
                                        new ArrayList<TestObjectCreationFactory>());
                                        
        if (!(obj instanceof ArrayList<?>)) {
            fail(
                "Unexpected object returned from DigesterLoader. Expected ArrayList; got " 
                + obj.getClass().getName());
        }
        
        @SuppressWarnings("unchecked") // root is an ArrayList of TestObjectCreationFactory
        ArrayList<TestObjectCreationFactory> list = (ArrayList<TestObjectCreationFactory>) obj;                
         
        assertEquals("List should contain only the factory object", list.size() , 1);
        TestObjectCreationFactory factory = list.get(0);
        assertEquals("Object create not called(1)", factory.called , true);
        assertEquals(
                    "Attribute not passed (1)", 
                    factory.attributes.getValue("one"), 
                    "good");
        assertEquals(
                    "Attribute not passed (2)", 
                    factory.attributes.getValue("two"), 
                    "bad");
        assertEquals(
                    "Attribute not passed (3)", 
                    factory.attributes.getValue("three"), 
                    "ugly");   

        
        rules = getClass().getClassLoader().getResource
            ("org/apache/commons/digester/xmlrules/testfactoryignore.xml");
            
        xml = "<?xml version='1.0' ?><root one='good' two='bad' three='ugly'><foo/></root>";
        try {
            DigesterLoader.load(
                                    rules, 
                                    getClass().getClassLoader(), 
                                    new StringReader(xml));
        } catch (Exception e) {
            fail("This exception should have been ignored: " + e.getClass().getName());
        }
        
        rules = getClass().getClassLoader().getResource
            ("org/apache/commons/digester/xmlrules/testfactorynoignore.xml");
            
        xml = "<?xml version='1.0' ?><root one='good' two='bad' three='ugly'><foo/></root>";
        try {
            DigesterLoader.load(
                                    rules, 
                                    getClass().getClassLoader(), 
                                    new StringReader(xml));
            fail("Exception should have been propagated from create method.");
        } catch (Exception e) {
            /* What we expected */
            assertEquals(org.xml.sax.SAXParseException.class, e.getClass());
        }        
    }

    public void testCallParamRule() throws Exception {
    
        URL rules = getClass().getClassLoader().getResource
            ("org/apache/commons/digester/xmlrules/test-call-param-rules.xml");
        
        String xml = "<?xml version='1.0' ?>"
                     + "<root><foo attr='long'><bar>short</bar><foobar><ping>tosh</ping></foobar></foo></root>";
        
        CallParamTestObject testObject = new CallParamTestObject();
        
        DigesterLoader.load(
                                    rules, 
                                    getClass().getClassLoader(), 
                                    new StringReader(xml),
                                    testObject);        
                                                                        
        assertEquals("Incorrect left value", "long", testObject.getLeft());
        assertEquals("Incorrect middle value", "short", testObject.getMiddle());
        assertEquals("Incorrect right value", "", testObject.getRight());
    }
    
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
        
        Digester digester = DigesterLoader.createDigester(new InputSource(new StringReader(rulesXml)));
        digester.push(testObject);
        digester.parse(new StringReader(xml));        
                                                                        
        assertEquals("Incorrect left value", "long", testObject.getLeft());
        assertEquals("Incorrect middle value", "short", testObject.getMiddle());
        assertEquals("Incorrect right value", "", testObject.getRight());
    }

    public void testNodeCreateRule() throws Exception {
        
        URL rules = getClass().getClassLoader().getResource("org/apache/commons/digester/xmlrules/test-node-create-rules.xml");
        URL input = getClass().getClassLoader().getResource("org/apache/commons/digester/xmlrules/test-node-create-rules-input.xml");
        assertNotNull("The test could not locate test-node-create-rules.xml", rules);
        assertNotNull("The test could not locate test-node-create-rules-input.xml", input);
        Digester digester = DigesterLoader.createDigester(rules);
        digester.push(new ArrayList<Node>());
        Object root = digester.parse(input.openStream());

        assertNotNull("root was null", root);
        @SuppressWarnings("unchecked") // if the assumption is incorrect, the test will fail
        List<Node> nlist = (List<Node>) root;
        assertTrue("no nodes were captured.", nlist.size() > 0);
        Node[] nodeArray = nlist.toArray(new Node[0]);
        assertNotNull("resulting node array from array list was null", nodeArray);
        
        // test foo1 structure        
        Node foo1 = nodeArray[0];
        assertTrue("foo1 didn't have any children", foo1.hasChildNodes());
        
        Node foo1Bar1 = foo1.getFirstChild();
        assertTrue("foo1's child was not named bar1", "bar1".equals(foo1Bar1.getNodeName()));
        assertTrue("foo1/bar1 value was not bar-1-value", "bar1-value".equals(foo1Bar1.getFirstChild().getNodeValue()));       
    }    
    
        
}

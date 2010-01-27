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


package org.apache.commons.digester;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xml.sax.Attributes;

/**
 * Test case for factory create rules.
 *
 * @author Robert Burrell Donkin
 */

public class TestFactoryCreate extends TestCase {
    /** Standard constructor */
    public TestFactoryCreate(String name) { 
        super(name);
    }

    /** Set up instance variables required by this test case. */
    @Override
    public void setUp() {}

    /** Return the tests included in this test suite. */
    public static Test suite() {

        return (new TestSuite(TestFactoryCreate.class));

    }

    /** Tear down instance variables required by this test case.*/
    @Override
    public void tearDown() {}
        
    // --------------------------------------------------------------- Test cases
    
       
    
    public void testPropagateException() throws Exception {
    
        // only used with this method
        class ThrowExceptionCreateRule extends AbstractObjectCreationFactory {
            @Override
            public Object createObject(Attributes attributes) throws Exception {
                throw new RuntimeException();
            }
        }   
        
        
        // now for the tests
        String xml = "<?xml version='1.0' ?><root><element/></root>";
        
        // test default - which is to propagate the exception
        Digester digester = new Digester();
        digester.addFactoryCreate("root", new ThrowExceptionCreateRule());
        try {
        
            digester.parse(new StringReader(xml));
            fail("Exception not propagated from create rule (1)");
        
        } catch (Exception e) { 
            /* This is what's expected */ 
        }
        
        // test propagate exception
        digester = new Digester();
        digester.addFactoryCreate("root", new ThrowExceptionCreateRule(), false);
        try {
        
            digester.parse(new StringReader(xml));
            fail("Exception not propagated from create rule (1)");
        
        } catch (Exception e) { 
            /* This is what's expected */ 
        }
        
        // test don't propagate exception
        digester = new Digester();
        digester.addFactoryCreate("root", new ThrowExceptionCreateRule(), true);
        try {
        
            digester.parse(new StringReader(xml));
        
        } catch (Exception e) {
            // this shouldn't happen
            fail("Exception should not be propagated");
        }
    }
    
    public void testFactoryCreateRule() throws Exception {
        tryVariations(true);
        tryVariations(false);
    }
    
    private void tryVariations(boolean propagateExceptions) throws Exception {
        
        
        // test passing object create
        Digester digester = new Digester();
        TestObjectCreationFactory factory = new TestObjectCreationFactory();
        digester.addFactoryCreate("root", factory, propagateExceptions);
        String xml = new String (
            "<?xml version='1.0' ?><root one='good' two='bad' three='ugly'><element/></root>");
        digester.parse(new StringReader(xml));
        
        assertEquals("Object create not called(1)[" + propagateExceptions + "]", factory.called , true);
        assertEquals(
                    "Attribute not passed (1)[" + propagateExceptions + "]", 
                    factory.attributes.getValue("one"), 
                    "good");
        assertEquals(
                    "Attribute not passed (2)[" + propagateExceptions + "]", 
                    factory.attributes.getValue("two"), 
                    "bad");
        assertEquals(
                    "Attribute not passed (3)[" + propagateExceptions + "]", 
                    factory.attributes.getValue("three"), 
                    "ugly");   
                    
        digester = new Digester();
        digester.addFactoryCreate(
                                "root", 
                                "org.apache.commons.digester.TestObjectCreationFactory",
                                propagateExceptions);
        digester.addSetNext("root", "add");
        xml = new String (
            "<?xml version='1.0' ?><root one='good' two='bad' three='ugly'><element/></root>");        
        List<TestObjectCreationFactory> list = new ArrayList<TestObjectCreationFactory>();
        digester.push(list);
        digester.parse(new StringReader(xml));
        
        assertEquals("List should contain only the factory object", list.size() , 1);
        factory = list.get(0);
        assertEquals("Object create not called(2)[" + propagateExceptions + "]", factory.called , true);
        assertEquals(
                    "Attribute not passed (4)[" + propagateExceptions + "]", 
                    factory.attributes.getValue("one"), 
                    "good");
        assertEquals(
                    "Attribute not passed (5)[" + propagateExceptions + "]", 
                    factory.attributes.getValue("two"), 
                    "bad");
        assertEquals(
                    "Attribute not passed (6)[" + propagateExceptions + "]", 
                    factory.attributes.getValue("three"), 
                    "ugly");   
                    
    
        digester = new Digester();
        digester.addFactoryCreate(
                                "root", 
                                "org.apache.commons.digester.TestObjectCreationFactory",
                                "override",
                                propagateExceptions);
        digester.addSetNext("root", "add");
        xml = new String (
            "<?xml version='1.0' ?><root one='good' two='bad' three='ugly'><element/></root>");        
        list = new ArrayList<TestObjectCreationFactory>();
        digester.push(list);
        digester.parse(new StringReader(xml));
        
        assertEquals("List should contain only the factory object", list.size() , 1);
        factory = list.get(0);
        assertEquals("Object create not called(3)[" + propagateExceptions + "]", factory.called , true);
        assertEquals(
                    "Attribute not passed (7)[" + propagateExceptions + "]", 
                    factory.attributes.getValue("one"), 
                    "good");
        assertEquals(
                    "Attribute not passed (8)[" + propagateExceptions + "]", 
                    factory.attributes.getValue("two"), 
                    "bad");
        assertEquals(
                    "Attribute not passed (8)[" + propagateExceptions + "]", 
                    factory.attributes.getValue("three"), 
                    "ugly");   
        
        digester = new Digester();
        digester.addFactoryCreate(
                                "root", 
                                "org.apache.commons.digester.TestObjectCreationFactory",
                                "override",
                                propagateExceptions);
        digester.addSetNext("root", "add");
        xml = new String (
            "<?xml version='1.0' ?><root one='good' two='bad' three='ugly' "
            + " override='org.apache.commons.digester.OtherTestObjectCreationFactory' >"
            + "<element/></root>");        
        list = new ArrayList<TestObjectCreationFactory>();
        digester.push(list);
        digester.parse(new StringReader(xml));
        
        assertEquals("List should contain only the factory object", list.size() , 1);
        factory = list.get(0);
        assertEquals(
                    "Attribute Override Failed (1)", 
                    factory.getClass().getName() , 
                    "org.apache.commons.digester.OtherTestObjectCreationFactory");
        assertEquals("Object create not called(4)[" + propagateExceptions + "]", factory.called , true);
        assertEquals(
                    "Attribute not passed (10)[" + propagateExceptions + "]", 
                    factory.attributes.getValue("one"), 
                    "good");
        assertEquals(
                    "Attribute not passed (11)[" + propagateExceptions + "]", 
                    factory.attributes.getValue("two"), 
                    "bad");
        assertEquals(
                    "Attribute not passed (12)[" + propagateExceptions + "]", 
                    factory.attributes.getValue("three"), 
                    "ugly");   
    
        digester = new Digester();
        digester.addFactoryCreate(
                                "root", 
                                TestObjectCreationFactory.class,
                                "override",
                                propagateExceptions);
        digester.addSetNext("root", "add");
        xml = new String (
            "<?xml version='1.0' ?><root one='good' two='bad' three='ugly'><element/></root>");        
        list = new ArrayList<TestObjectCreationFactory>();
        digester.push(list);
        digester.parse(new StringReader(xml));
        
        assertEquals("List should contain only the factory object", list.size() , 1);
        factory = list.get(0);
        assertEquals("Object create not called(5)[" + propagateExceptions + "]", factory.called , true);
        assertEquals(
                    "Attribute not passed (13)[" + propagateExceptions + "]", 
                    factory.attributes.getValue("one"), 
                    "good");
        assertEquals(
                    "Attribute not passed (14)[" + propagateExceptions + "]", 
                    factory.attributes.getValue("two"), 
                    "bad");
        assertEquals(
                    "Attribute not passed (15)[" + propagateExceptions + "]", 
                    factory.attributes.getValue("three"), 
                    "ugly");   
        
        digester = new Digester();
        digester.addFactoryCreate(
                                "root", 
                                TestObjectCreationFactory.class,
                                "override",
                                propagateExceptions);
        digester.addSetNext("root", "add");
        xml = new String (
            "<?xml version='1.0' ?><root one='good' two='bad' three='ugly' "
            + " override='org.apache.commons.digester.OtherTestObjectCreationFactory' >"
            + "<element/></root>");        
        list = new ArrayList<TestObjectCreationFactory>();
        digester.push(list);
        digester.parse(new StringReader(xml));
        
        assertEquals("List should contain only the factory object", list.size() , 1);
        factory = list.get(0);
        assertEquals(
                    "Attribute Override Failed (2)", 
                    factory.getClass().getName() , 
                    "org.apache.commons.digester.OtherTestObjectCreationFactory");
        assertEquals("Object create not called(6)[" + propagateExceptions + "]", factory.called , true);
        assertEquals(
                    "Attribute not passed (16)[" + propagateExceptions + "]", 
                    factory.attributes.getValue("one"), 
                    "good");
        assertEquals(
                    "Attribute not passed (17)[" + propagateExceptions + "]", 
                    factory.attributes.getValue("two"), 
                    "bad");
        assertEquals(
                    "Attribute not passed (18)[" + propagateExceptions + "]", 
                    factory.attributes.getValue("three"), 
                    "ugly");   
    }
}

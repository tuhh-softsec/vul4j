/*
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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
    public void setUp() {}

    /** Return the tests included in this test suite. */
    public static Test suite() {

        return (new TestSuite(TestFactoryCreate.class));

    }

    /** Tear down instance variables required by this test case.*/
    public void tearDown() {}
        
    // --------------------------------------------------------------- Test cases
    
       
    
    public void testPropagateException() throws Exception {
    
        // only used with this method
        class ThrowExceptionCreateRule extends AbstractObjectCreationFactory {
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
        List list = new ArrayList();
        digester.push(list);
        digester.parse(new StringReader(xml));
        
        assertEquals("List should contain only the factory object", list.size() , 1);
        factory = (TestObjectCreationFactory) list.get(0);
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
        list = new ArrayList();
        digester.push(list);
        digester.parse(new StringReader(xml));
        
        assertEquals("List should contain only the factory object", list.size() , 1);
        factory = (TestObjectCreationFactory) list.get(0);
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
        list = new ArrayList();
        digester.push(list);
        digester.parse(new StringReader(xml));
        
        assertEquals("List should contain only the factory object", list.size() , 1);
        factory = (TestObjectCreationFactory) list.get(0);
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
        list = new ArrayList();
        digester.push(list);
        digester.parse(new StringReader(xml));
        
        assertEquals("List should contain only the factory object", list.size() , 1);
        factory = (TestObjectCreationFactory) list.get(0);
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
        list = new ArrayList();
        digester.push(list);
        digester.parse(new StringReader(xml));
        
        assertEquals("List should contain only the factory object", list.size() , 1);
        factory = (TestObjectCreationFactory) list.get(0);
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

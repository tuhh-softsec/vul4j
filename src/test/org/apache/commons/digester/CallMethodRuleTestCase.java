/*
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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


import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.SAXException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * <p>Tests for the <code>CallMethodRule</code> and associated 
 * <code>CallParamRule</code>.
 *
 * @author Christopher Lenz 
 */
public class CallMethodRuleTestCase extends TestCase {


    // ----------------------------------------------------- Instance Variables


    /**
     * The digester instance we will be processing.
     */
    protected Digester digester = null;


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public CallMethodRuleTestCase(String name) {

        super(name);

    }


    // --------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    public void setUp() {

        digester = new Digester();

    }


    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {

        return (new TestSuite(CallMethodRuleTestCase.class));

    }


    /**
     * Tear down instance variables required by this test case.
     */
    public void tearDown() {

        digester = null;

    }



    // ------------------------------------------------ Individual Test Methods


    /**
     * Test method calls with the CallMethodRule rule. It should be possible
     * to call any accessible method of the object on the top of the stack,
     * even methods with no arguments.
     */
    public void testBasic() throws SAXException, IOException {
        
        // Configure the digester as required
        digester.addObjectCreate("employee", Employee.class);
        // try all syntax permutations
        digester.addCallMethod("employee", "toString", 0, (Class[])null);
        digester.addCallMethod("employee", "toString", 0, (String[])null);
        digester.addCallMethod("employee", "toString", 0, new Class[] {});
        digester.addCallMethod("employee", "toString", 0, new String[] {});
        digester.addCallMethod("employee", "toString");

        // Parse our test input
        Object root1 = null;
        // an exception will be thrown if the method can't be found
        root1 = digester.parse(getInputStream("Test5.xml"));

    }


    /**
     * Test method calls with the CallMethodRule rule. It should be possible
     * to call any accessible method of the object on the top of the stack,
     * even methods with no arguments.
     */
    public void testSettingProperties() throws SAXException, IOException {
            
        // Configure the digester as required
        digester.addObjectCreate("employee", Employee.class);
        // try all syntax permutations
        digester.addCallMethod("employee", "setLastName", 1, 
                                new String[] {"java.lang.String"});
        digester.addCallParam("employee/lastName", 0);
                
        // Parse our test input
        Object root1 = null;
        
        // an exception will be thrown if the method can't be found
        root1 = digester.parse(getInputStream("Test5.xml"));
        Employee employee = (Employee) root1;
        assertEquals("Failed to call Employee.setLastName", 
                    "Last Name", employee.getLastName()); 
        

        digester = new Digester();
        // Configure the digester as required
        digester.addObjectCreate("employee", Employee.class);
        // try out primitive convertion
        digester.addCallMethod("employee", "setAge", 1, 
                                new Class[] {int.class});
        digester.addCallParam("employee/age", 0);         
                
        // Parse our test input
        root1 = null;
        
        // an exception will be thrown if the method can't be found
        root1 = digester.parse(getInputStream("Test5.xml"));
        employee = (Employee) root1;
        assertEquals("Failed to call Employee.setAge", 21, employee.getAge()); 
        
        digester = new Digester();
        // Configure the digester as required
        digester.addObjectCreate("employee", Employee.class);      
        digester.addCallMethod("employee", "setActive", 1, 
                                new Class[] {boolean.class});
        digester.addCallParam("employee/active", 0);    
                
        // Parse our test input
        root1 = null;

        // an exception will be thrown if the method can't be found
        root1 = digester.parse(getInputStream("Test5.xml"));
        employee = (Employee) root1;
        assertEquals("Failed to call Employee.setActive", 
                        true, employee.isActive()); 
        
        digester = new Digester();            
        // Configure the digester as required
        digester.addObjectCreate("employee", Employee.class); 
        digester.addCallMethod("employee", "setSalary", 1, 
                                new Class[] {float.class});
        digester.addCallParam("employee/salary", 0);    
                
        // Parse our test input
        root1 = null;
        // an exception will be thrown if the method can't be found
        root1 = digester.parse(getInputStream("Test5.xml"));
        employee = (Employee) root1;
        assertEquals("Failed to call Employee.setSalary", 
                        1000000.0f, employee.getSalary(), 0.1f); 
    }


    /**
     * This tests the call methods params enhancement that provides 
     * for more complex stack-based calls.
     */
    public void testParamsFromStack() throws SAXException, IOException {

        StringBuffer xml = new StringBuffer().
            append("<?xml version='1.0'?>").
            append("<map>").
            append("  <key name='The key'/>").
            append("  <value name='The value'/>").
            append("</map>");

        digester.addObjectCreate("map", HashMap.class);
        digester.addCallMethod("map", "put", 2);
        digester.addObjectCreate("map/key", AlphaBean.class);
        digester.addSetProperties("map/key");
        digester.addCallParam("map/key", 0, true);
        digester.addObjectCreate("map/value", BetaBean.class);
        digester.addSetProperties("map/value");
        digester.addCallParam("map/value", 1, true);

        Map map = (Map) digester.parse(new StringReader(xml.toString()));

        assertNotNull(map);
        assertEquals(1, map.size());
        assertEquals("The key",
                     ((AlphaBean)map.keySet().iterator().next()).getName());
        assertEquals("The value",
                     ((BetaBean)map.values().iterator().next()).getName());
    }


    /**
     * Test nested CallMethod rules.
     */
    public void testOrderNested() throws Exception {
        
        // Configure the digester as required
        StringBuffer word = new StringBuffer();
        digester.push(word);
        digester.addCallMethod("*/element", "append", 1);
        digester.addCallParam("*/element", 0, "name");
        
        // Parse our test input
        Object root1 = null;
        try {
            // an exception will be thrown if the method can't be found
            root1 = digester.parse(getInputStream("Test8.xml"));
            
        } catch (Throwable t) {
            // this means that the method can't be found and so the test fails
            fail("Digester threw Exception:  " + t);
        }
        
        assertEquals("Wrong method call order", "ABA", word.toString());

    }


    // ------------------------------------------------ Utility Support Methods


    /**
     * Return an appropriate InputStream for the specified test file (which
     * must be inside our current package.
     *
     * @param name Name of the test file we want
     *
     * @exception IOException if an input/output error occurs
     */
    protected InputStream getInputStream(String name) throws IOException {

        return (this.getClass().getResourceAsStream
                ("/org/apache/commons/digester/" + name));

    }


}


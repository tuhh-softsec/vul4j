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


import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xml.sax.SAXException;

//import org.apache.commons.logging.impl.SimpleLog;

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
     * Test method calls with the CallMethodRule reading from the element
     * body, with no CallParamMethod rules added.
     */
    public void testCallMethodOnly() throws Exception {

        // Configure the digester as required
        digester.addObjectCreate("employee", Employee.class);
        digester.addCallMethod("employee/firstName", "setFirstName", 0);
        digester.addCallMethod("employee/lastName", "setLastName", 0);

        // Parse our test input
        Employee employee = (Employee)
            digester.parse(getInputStream("Test9.xml"));
        assertNotNull("parsed an employee", employee);

        // Validate that the property setters were called
        assertEquals("Set first name", "First Name", employee.getFirstName());
        assertEquals("Set last name", "Last Name", employee.getLastName());
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


    public void testPrimitiveReading() throws Exception {
        StringReader reader = new StringReader(
            "<?xml version='1.0' ?><root><bean good='true'/><bean good='false'/><bean/>"
            + "<beanie bad='Fee Fie Foe Fum' good='true'/><beanie bad='Fee Fie Foe Fum' good='false'/>"
            + "<beanie bad='Fee Fie Foe Fum'/></root>");
            
        Digester digester = new Digester();
        
        //SimpleLog log = new SimpleLog("[testPrimitiveReading:Digester]");
        //log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
        //digester.setLogger(log);
        
        digester.addObjectCreate("root/bean", PrimitiveBean.class);
        digester.addSetNext("root/bean", "add");
        Class [] params = { Boolean.TYPE };
        digester.addCallMethod("root/bean", "setBoolean", 1, params);
        digester.addCallParam("root/bean", 0, "good");
        
        digester.addObjectCreate("root/beanie", PrimitiveBean.class);
        digester.addSetNext("root/beanie", "add");
        Class [] beanieParams = { String.class, Boolean.TYPE };
        digester.addCallMethod("root/beanie", "testSetBoolean", 2, beanieParams);
        digester.addCallParam("root/beanie", 0, "bad");
        digester.addCallParam("root/beanie", 1, "good");
        
        ArrayList list = new ArrayList();
        digester.push(list);
        digester.parse(reader);
        
        assertEquals("Wrong number of beans in list", 6, list.size());
        PrimitiveBean bean = (PrimitiveBean) list.get(0);
        assertTrue("Bean 0 property not called", bean.getSetBooleanCalled());
        assertEquals("Bean 0 property incorrect", true, bean.getBoolean());
        bean = (PrimitiveBean) list.get(1);
        assertTrue("Bean 1 property not called", bean.getSetBooleanCalled());
        assertEquals("Bean 1 property incorrect", false, bean.getBoolean());
        bean = (PrimitiveBean) list.get(2);
        // no attibute, no call is what's expected
        assertTrue("Bean 2 property called", !bean.getSetBooleanCalled());
        bean = (PrimitiveBean) list.get(3);
        assertTrue("Bean 3 property not called", bean.getSetBooleanCalled());
        assertEquals("Bean 3 property incorrect", true, bean.getBoolean());
        bean = (PrimitiveBean) list.get(4);
        assertTrue("Bean 4 property not called", bean.getSetBooleanCalled());
        assertEquals("Bean 4 property incorrect", false, bean.getBoolean());
        bean = (PrimitiveBean) list.get(5);
        assertTrue("Bean 5 property not called", bean.getSetBooleanCalled());
        assertEquals("Bean 5 property incorrect", false, bean.getBoolean());       
    }
    
    public void testFromStack() throws Exception {
    
        StringReader reader = new StringReader(
            "<?xml version='1.0' ?><root><one/><two/><three/><four/><five/></root>");
            
        Digester digester = new Digester();
        
        Class [] params = { String.class };
        
        digester.addObjectCreate("root/one", NamedBean.class);
        digester.addSetNext("root/one", "add");
        digester.addCallMethod("root/one", "setName", 1, params);
        digester.addCallParam("root/one", 0, 2);
        
        digester.addObjectCreate("root/two", NamedBean.class);
        digester.addSetNext("root/two", "add");
        digester.addCallMethod("root/two", "setName", 1, params);
        digester.addCallParam("root/two", 0, 3);
        
        digester.addObjectCreate("root/three", NamedBean.class);
        digester.addSetNext("root/three", "add");
        digester.addCallMethod("root/three", "setName", 1, params);
        digester.addCallParam("root/three", 0, 4);
        
        digester.addObjectCreate("root/four", NamedBean.class);
        digester.addSetNext("root/four", "add");
        digester.addCallMethod("root/four", "setName", 1, params);
        digester.addCallParam("root/four", 0, 5);
        
        digester.addObjectCreate("root/five", NamedBean.class);
        digester.addSetNext("root/five", "add");
        Class [] newParams = { String.class, String.class };
        digester.addCallMethod("root/five", "test", 2, newParams);
        digester.addCallParam("root/five", 0, 10);
        digester.addCallParam("root/five", 1, 3);
        
        // prepare stack
        digester.push("That lamb was sure to go.");
        digester.push("And everywhere that Mary went,");
        digester.push("It's fleece was white as snow.");
        digester.push("Mary had a little lamb,");
        
        ArrayList list = new ArrayList();
        digester.push(list);
        digester.parse(reader);
        
        assertEquals("Wrong number of beans in list", 5, list.size());
        NamedBean bean = (NamedBean) list.get(0);
        assertEquals("Parameter not set from stack (1)", "Mary had a little lamb,", bean.getName());
        bean = (NamedBean) list.get(1);
        assertEquals("Parameter not set from stack (2)", "It's fleece was white as snow.", bean.getName());
        bean = (NamedBean) list.get(2);
        assertEquals("Parameter not set from stack (3)", "And everywhere that Mary went,", bean.getName());
        bean = (NamedBean) list.get(3);
        assertEquals("Parameter not set from stack (4)", "That lamb was sure to go.", bean.getName());
        bean = (NamedBean) list.get(4);
        assertEquals("Out of stack not set to null", null , bean.getName());
    }
    
    public void testTwoCalls() throws Exception {
        
    
        StringReader reader = new StringReader(
            "<?xml version='1.0' ?><root>"
            + "<param class='int' coolness='true'>25</param>"
            + "<param class='long'>50</param>"
            + "<param class='float' coolness='false'>90</param></root>");
            
        Digester digester = new Digester();
        //SimpleLog log = new SimpleLog("{testTwoCalls:Digester]");
        //log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
        //digester.setLogger(log);
        
        digester.addObjectCreate( "root/param", ParamBean.class );
        digester.addSetNext( "root/param", "add" );
        digester.addCallMethod( "root/param", "setThisAndThat", 2 );
        digester.addCallParam( "root/param", 0, "class" );
        digester.addCallParam( "root/param", 1 );
        digester.addCallMethod( "root/param", "setCool", 1, new Class[] {boolean.class } );
        digester.addCallParam( "root/param", 0, "coolness" );
        
        ArrayList list = new ArrayList();
        digester.push(list);
        digester.parse(reader);
    
        assertEquals("Wrong number of objects created", 3, list.size());
        ParamBean bean = (ParamBean) list.get(0);
        assertEquals("Coolness wrong (1)", true, bean.isCool());
        assertEquals("This wrong (1)", "int", bean.getThis());
        assertEquals("That wrong (1)", "25", bean.getThat());
        bean = (ParamBean) list.get(1);
        assertEquals("Coolness wrong (2)", false, bean.isCool());
        assertEquals("This wrong (2)", "long", bean.getThis());
        assertEquals("That wrong (2)", "50", bean.getThat());
        bean = (ParamBean) list.get(2);
        assertEquals("Coolness wrong (3)", false, bean.isCool());
        assertEquals("This wrong (3)", "float", bean.getThis());
        assertEquals("That wrong (3)", "90", bean.getThat());
    }

    public void testNestedBody() throws Exception {
        
        StringReader reader = new StringReader(
            "<?xml version='1.0' ?><root>"
            + "<spam>Simple</spam>"
            + "<spam>Complex<spam>Deep<spam>Deeper<spam>Deepest</spam></spam></spam></spam>"
            + "</root>");
            
        Digester digester = new Digester();        

        //SimpleLog log = new SimpleLog("[testPrimitiveReading:Digester]");
        //log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
        //digester.setLogger(log);
        
        
        digester.addObjectCreate("root/spam", NamedBean.class);
        digester.addSetRoot("root/spam", "add");
        digester.addCallMethod( "root/spam", "setName", 1 );
        digester.addCallParam( "root/spam", 0);
        
        digester.addObjectCreate("root/spam/spam", NamedBean.class);
        digester.addSetRoot("root/spam/spam", "add");
        digester.addCallMethod( "root/spam/spam", "setName", 1 );
        digester.addCallParam( "root/spam/spam", 0);        
        
        digester.addObjectCreate("root/spam/spam/spam", NamedBean.class);
        digester.addSetRoot("root/spam/spam/spam", "add");
        digester.addCallMethod( "root/spam/spam/spam", "setName", 1 );
        digester.addCallParam( "root/spam/spam/spam", 0);      

        
        digester.addObjectCreate("root/spam/spam/spam/spam", NamedBean.class);
        digester.addSetRoot("root/spam/spam/spam/spam", "add");
        digester.addCallMethod( "root/spam/spam/spam/spam", "setName", 1 );
        digester.addCallParam( "root/spam/spam/spam/spam", 0);   
        
        ArrayList list = new ArrayList();
        digester.push(list);
        digester.parse(reader);
        
        System.out.println(list);
        
        NamedBean bean = (NamedBean) list.get(0);
        assertEquals("Wrong name (1)", "Simple", bean.getName());
        // these are added in deepest first order by the addRootRule
        bean = (NamedBean) list.get(4);
        assertEquals("Wrong name (2)", "Complex", bean.getName());
        bean = (NamedBean) list.get(3);
        assertEquals("Wrong name (3)", "Deep", bean.getName());
        bean = (NamedBean) list.get(2);
        assertEquals("Wrong name (4)", "Deeper", bean.getName());
        bean = (NamedBean) list.get(1);
        assertEquals("Wrong name (5)", "Deepest", bean.getName());
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


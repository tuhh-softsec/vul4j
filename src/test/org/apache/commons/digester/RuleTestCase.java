/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/test/org/apache/commons/digester/RuleTestCase.java,v 1.19 2002/07/31 10:48:08 jstrachan Exp $
 * $Revision: 1.19 $
 * $Date: 2002/07/31 10:48:08 $
 *
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

import java.util.ArrayList;
import java.util.Hashtable;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * <p>Test Case for the Digester class.  These tests perform parsing of
 * XML documents to exercise the built-in rules.</p>
 *
 * @author Craig R. McClanahan
 * @author Janek Bogucki
 * @version $Revision: 1.19 $ $Date: 2002/07/31 10:48:08 $
 */

public class RuleTestCase extends TestCase {


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
    public RuleTestCase(String name) {

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

        return (new TestSuite(RuleTestCase.class));

    }


    /**
     * Tear down instance variables required by this test case.
     */
    public void tearDown() {

        digester = null;

    }



    // ------------------------------------------------ Individual Test Methods


    /**
     * Test object creation (and associated property setting) with nothing on
     * the stack, which should cause an appropriate Employee object to be
     * returned.
     */
    public void testObjectCreate1() {

        // Configure the digester as required
        digester.addObjectCreate("employee",
                "org.apache.commons.digester.Employee");
        digester.addSetProperties("employee");

        // Parse our test input.
        Object root = null;
        try {
            root = digester.parse(getInputStream("Test1.xml"));
        } catch (Throwable t) {
            fail("Digester threw IOException: " + t);
        }
        assertNotNull("Digester returned an object", root);
        assertTrue("Digester returned an Employee",
                root instanceof Employee);
        Employee employee = (Employee) root;
        assertEquals("First name is correct",
                "First Name",
                employee.getFirstName());
        assertEquals("Last name is correct",
                "Last Name",
                employee.getLastName());


    }


    /**
     * Test object creation (and associated property setting) with nothing on
     * the stack, which should cause an appropriate Employee object to be
     * returned.  The processing rules will process the nested Address elements
     * as well, but will not attempt to add them to the Employee.
     */
    public void testObjectCreate2() {

        // Configure the digester as required
        digester.addObjectCreate("employee", Employee.class);
        digester.addSetProperties("employee");
        digester.addObjectCreate("employee/address",
                "org.apache.commons.digester.Address");
        digester.addSetProperties("employee/address");

        // Parse our test input.
        Object root = null;
        try {
            root = digester.parse(getInputStream("Test1.xml"));
        } catch (Throwable t) {
            fail("Digester threw IOException: " + t);
        }
        assertNotNull("Digester returned an object", root);
        assertTrue("Digester returned an Employee",
                root instanceof Employee);
        Employee employee = (Employee) root;
        assertEquals("First name is correct",
                "First Name",
                employee.getFirstName());
        assertEquals("Last name is correct",
                "Last Name",
                employee.getLastName());


    }


    /**
     * Test object creation (and associated property setting) with nothing on
     * the stack, which should cause an appropriate Employee object to be
     * returned.  The processing rules will process the nested Address elements
     * as well, and will add them to the owning Employee.
     */
    public void testObjectCreate3() {

        // Configure the digester as required
        digester.addObjectCreate("employee", Employee.class);
        digester.addSetProperties("employee");
        digester.addObjectCreate("employee/address",
                "org.apache.commons.digester.Address");
        digester.addSetProperties("employee/address");
        digester.addSetNext("employee/address",
                "addAddress");

        // Parse our test input once
        Object root = null;
        try {
            root = digester.parse(getInputStream("Test1.xml"));
        } catch (Throwable t) {
            fail("Digester threw IOException: " + t);
        }
        validateObjectCreate3(root);

        // Parse the same input again
        try {
            root = digester.parse(getInputStream("Test1.xml"));
        } catch (Throwable t) {
            fail("Digester threw IOException: " + t);
        }
        validateObjectCreate3(root);

    }


    /**
     * Same as testObjectCreate1(), except use individual call method rules
     * to set the properties of the Employee.
     */
    public void testObjectCreate4() {

        // Configure the digester as required
        digester.addObjectCreate("employee", Employee.class);
        digester.addCallMethod("employee",
                "setFirstName", 1);
        digester.addCallParam("employee", 0, "firstName");
        digester.addCallMethod("employee",
                "setLastName", 1);
        digester.addCallParam("employee", 0, "lastName");


        // Parse our test input.
        Object root = null;
        try {
            root = digester.parse(getInputStream("Test1.xml"));
        } catch (Throwable t) {
            fail("Digester threw IOException: " + t);
        }
        assertNotNull("Digester returned an object", root);
        assertTrue("Digester returned an Employee",
                root instanceof Employee);
        Employee employee = (Employee) root;
        assertEquals("First name is correct",
                "First Name",
                employee.getFirstName());
        assertEquals("Last name is correct",
                "Last Name",
                employee.getLastName());

    }


    /**
     * Same as testObjectCreate1(), except use individual call method rules
     * to set the properties of the Employee. Bean data are defined using 
     * elements instead of attributes. The purpose is to test CallMethod with
     * a paramCount=0 (ie the body of the element is the argument of the 
     * method).
     */
    public void testObjectCreate5() {

        // Configure the digester as required
        digester.addObjectCreate("employee", Employee.class);
        digester.addCallMethod("employee/firstName", "setFirstName", 0);
        digester.addCallMethod("employee/lastName", "setLastName", 0);


        // Parse our test input.
        Object root = null;
        try {
            root = digester.parse(getInputStream("Test5.xml"));
        } catch (Throwable t) {
            fail("Digester threw IOException: " + t);
        }
        assertNotNull("Digester returned an object", root);
        assertTrue("Digester returned an Employee",
                root instanceof Employee);
        Employee employee = (Employee) root;
        assertEquals("First name is correct",
                "First Name",
                employee.getFirstName());
        assertEquals("Last name is correct",
                "Last Name",
                employee.getLastName());

    }


    /**
     * It should be possible to parse the same input twice, and get trees
     * of objects that are isomorphic but not be identical object instances.
     */
    public void testRepeatedParse() {

        // Configure the digester as required
        digester.addObjectCreate("employee", Employee.class);
        digester.addSetProperties("employee");
        digester.addObjectCreate("employee/address",
                "org.apache.commons.digester.Address");
        digester.addSetProperties("employee/address");
        digester.addSetNext("employee/address",
                "addAddress");

        // Parse our test input the first time
        Object root1 = null;
        try {
            root1 = digester.parse(getInputStream("Test1.xml"));
        } catch (Throwable t) {
            fail("Digester #1 threw Exception:  " + t);
        }
        validateObjectCreate3(root1);

        // Parse our test input the second time
        Object root2 = null;
        try {
            root2 = digester.parse(getInputStream("Test1.xml"));
        } catch (Throwable t) {
            fail("Digester #2 threw Exception:  " + t);
        }
        validateObjectCreate3(root2);

        // Make sure that it was a different root
        assertTrue("Different tree instances were returned",
                root1 != root2);

    }


    /**
     * Test object creation (and associated property setting) with nothing on
     * the stack, which should cause an appropriate Employee object to be
     * returned.  The processing rules will process the nested Address elements
     * as well, but will not attempt to add them to the Employee.
     */
    public void testRuleSet1() {

        // Configure the digester as required
        RuleSet rs = new TestRuleSet();
        digester.addRuleSet(rs);

        // Parse our test input.
        Object root = null;
        try {
            root = digester.parse(getInputStream("Test1.xml"));
        } catch (Throwable t) {
            fail("Digester threw IOException: " + t);
        }

        assertNotNull("Digester returned an object", root);
        assertTrue("Digester returned an Employee",
                root instanceof Employee);
        Employee employee = (Employee) root;
        assertEquals("First name is correct",
                "First Name",
                employee.getFirstName());
        assertEquals("Last name is correct",
                "Last Name",
                employee.getLastName());
        assertNotNull("Can retrieve home address",
                employee.getAddress("home"));
        assertNotNull("Can retrieve office address",
                employee.getAddress("office"));

    }


    /**
     * Same as <code>testRuleSet1</code> except using a single namespace.
     */
    public void testRuleSet2() {

        // Configure the digester as required
        digester.setNamespaceAware(true);
        RuleSet rs = new TestRuleSet(null,
                "http://jakarta.apache.org/digester/Foo");
        digester.addRuleSet(rs);

        // Parse our test input.
        Object root = null;
        try {
            root = digester.parse(getInputStream("Test2.xml"));
        } catch (Throwable t) {
            fail("Digester threw IOException: " + t);
        }

        assertNotNull("Digester returned an object", root);
        assertTrue("Digester returned an Employee",
                root instanceof Employee);
        Employee employee = (Employee) root;
        assertEquals("First name is correct",
                "First Name",
                employee.getFirstName());
        assertEquals("Last name is correct",
                "Last Name",
                employee.getLastName());
        assertNotNull("Can retrieve home address",
                employee.getAddress("home"));
        assertNotNull("Can retrieve office address",
                employee.getAddress("office"));

    }


    /**
     * Same as <code>testRuleSet2</code> except using a namespace
     * for employee that we should recognize, and a namespace for
     * address that we should skip.
     */
    public void testRuleSet3() {

        // Configure the digester as required
        digester.setNamespaceAware(true);
        RuleSet rs = new TestRuleSet(null,
                "http://jakarta.apache.org/digester/Foo");
        digester.addRuleSet(rs);

        // Parse our test input.
        Object root = null;
        try {
            root = digester.parse(getInputStream("Test3.xml"));
        } catch (Throwable t) {
            fail("Digester threw IOException: " + t);
        }

        assertNotNull("Digester returned an object", root);
        assertTrue("Digester returned an Employee",
                root instanceof Employee);
        Employee employee = (Employee) root;
        assertEquals("First name is correct",
                "First Name",
                employee.getFirstName());
        assertEquals("Last name is correct",
                "Last Name",
                employee.getLastName());
        assertNull("Can not retrieve home address",
                employee.getAddress("home"));
        assertNull("Can not retrieve office address",
                employee.getAddress("office"));

    }


    /**
     * Test the two argument version of the SetTopRule rule. This test is
     * based on testObjectCreate3 and should result in the same tree of
     * objects.  Instead of using the SetNextRule rule which results in
     * a method invocation on the (top-1) (parent) object with the top
     * object (child) as an argument, this test uses the SetTopRule rule
     * which results in a method invocation on the top object (child)
     * with the top-1 (parent) object as an argument.  The three argument
     * form is tested in <code>testSetTopRule2</code>.
     */
    public void testSetTopRule1() {

        // Configure the digester as required
        digester.addObjectCreate("employee",
                "org.apache.commons.digester.Employee");
        digester.addSetProperties("employee");
        digester.addObjectCreate("employee/address",
                "org.apache.commons.digester.Address");
        digester.addSetProperties("employee/address");
        digester.addSetTop("employee/address", "setEmployee");

        // Parse our test input.
        Object root = null;
        try {
            root = digester.parse(getInputStream("Test1.xml"));
        } catch (Exception t) {
            fail("Digester threw Exception: " + t);
        }
        validateObjectCreate3(root);

    }


    /**
     * Same as <code>testSetTopRule1</code> except using the three argument
     * form of the SetTopRule rule.
     */
    public void testSetTopRule2() {

        // Configure the digester as required
        digester.addObjectCreate("employee",
                "org.apache.commons.digester.Employee");
        digester.addSetProperties("employee");
        digester.addObjectCreate("employee/address",
                "org.apache.commons.digester.Address");
        digester.addSetProperties("employee/address");
        digester.addSetTop("employee/address", "setEmployee",
                "org.apache.commons.digester.Employee");

        // Parse our test input.
        Object root = null;
        try {
            root = digester.parse(getInputStream("Test1.xml"));
        } catch (Exception t) {
            fail("Digester threw Exception: " + t);
        }
        validateObjectCreate3(root);

    }

    /**
     * Test rule addition - this boils down to making sure that 
     * digester is set properly on rule addition.
     */
    public void testAddRule() {
        Digester digester = new Digester();
        TestRule rule =  new TestRule("Test");
        digester.addRule("/root", rule);
        
        assertEquals("Digester is not properly on rule addition.", digester, rule.getDigester());

    }
    

    public void testSetNext() throws Exception {
        Digester digester = new Digester();
        digester.setRules(new ExtendedBaseRules());
        digester.setValidating(false);
        
        
        digester.addObjectCreate("!*/b", BetaBean.class);
        digester.addObjectCreate("!*/a", AlphaBean.class);
        digester.addObjectCreate("root", ArrayList.class);
        digester.addSetProperties("!*");
        digester.addSetNext("!*/b/?", "setChild");
        digester.addSetNext("!*/a/?", "setChild");
        digester.addSetNext("!root/?", "add");
        ArrayList root = (ArrayList) digester.parse(getInputStream("Test4.xml"));
        
        assertEquals("Wrong array size", 2, root.size());
        AlphaBean one = (AlphaBean) root.get(0);
        assertTrue(one.getChild() instanceof BetaBean);
        BetaBean two = (BetaBean) one.getChild();
        assertEquals("Wrong name (1)", two.getName() , "TWO");
        assertTrue(two.getChild() instanceof AlphaBean);
        AlphaBean three = (AlphaBean) two.getChild(); 
        assertEquals("Wrong name (2)", three.getName() , "THREE");       
        BetaBean four = (BetaBean) root.get(1);
        assertEquals("Wrong name (3)", four.getName() , "FOUR");
        assertTrue(four.getChild() instanceof BetaBean);
        BetaBean five = (BetaBean) four.getChild(); 
        assertEquals("Wrong name (4)", five.getName() , "FIVE");               
        
    }
    
    
    public void testSetTop() throws Exception {
        Digester digester = new Digester();
        digester.setRules(new ExtendedBaseRules());
        digester.setValidating(false);
        
        
        digester.addObjectCreate("!*/b", BetaBean.class);
        digester.addObjectCreate("!*/a", AlphaBean.class);
        digester.addObjectCreate("root", ArrayList.class);
        digester.addSetProperties("!*");
        digester.addSetTop("!*/b/?", "setParent");
        digester.addSetTop("!*/a/?", "setParent");
        digester.addSetRoot("!*/a", "add");
        digester.addSetRoot("!*/b", "add");
        ArrayList root = (ArrayList) digester.parse(getInputStream("Test4.xml"));
        
        assertEquals("Wrong array size", 5, root.size());
        
        // note that the array is in popped order (rather than pushed)
        
        Object obj = root.get(1);
        assertTrue("TWO should be a BetaBean", obj instanceof BetaBean);
        BetaBean two = (BetaBean) obj;
        assertNotNull("Two's parent should not be null", two.getParent());
        assertEquals("Wrong name (1)", "TWO", two.getName());
        assertEquals("Wrong name (2)", "ONE", two.getParent().getName() );
        
        obj = root.get(0);
        assertTrue("THREE should be an AlphaBean", obj instanceof AlphaBean);
        AlphaBean three = (AlphaBean) obj;
        assertNotNull("Three's parent should not be null", three.getParent());
        assertEquals("Wrong name (3)", "THREE", three.getName());
        assertEquals("Wrong name (4)", "TWO", three.getParent().getName());
        
        obj = root.get(3);
        assertTrue("FIVE should be a BetaBean", obj instanceof BetaBean);
        BetaBean five = (BetaBean) obj;
        assertNotNull("Five's parent should not be null", five.getParent());
        assertEquals("Wrong name (5)", "FIVE", five.getName());
        assertEquals("Wrong name (6)", "FOUR", five.getParent().getName());

    }


    /**
     * Test method calls with the CallMethodRule rule. It should be possible
     * to call any accessible method of the object on the top of the stack,
     * even methods with no arguments.
     */
    public void testCallMethod() throws Exception {
        
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
        try {
            // an exception will be thrown if the method can't be found
            root1 = digester.parse(getInputStream("Test5.xml"));
            
        } catch (Throwable t) {
            // this means that the method can't be found and so the test fails
            fail("Digester threw Exception:  " + t);
        }

    }

    /**
     * Test method calls with the CallMethodRule rule. It should be possible
     * to call any accessible method of the object on the top of the stack,
     * even methods with no arguments.
     */
    public void testCallMethod2() throws Exception {
        
        //I was preparing this test case to fix another bug
        //    i'll uncomment it once i've fixed it
            
        // Configure the digester as required
        digester.addObjectCreate("employee", Employee.class);
        // try all syntax permutations
        digester.addCallMethod("employee", "setLastName", 1, new String[] {"java.lang.String"});
        digester.addCallParam("employee/lastName", 0);
                
        // Parse our test input
        Object root1 = null;
        try {
            // an exception will be thrown if the method can't be found
            root1 = digester.parse(getInputStream("Test5.xml"));
            Employee employee = (Employee) root1;
            assertEquals("Failed to call Employee.setLastName", "Last Name", employee.getLastName()); 
            
        } catch (Throwable t) {
            // this means that the method can't be found and so the test fails
            fail("Digester threw Exception:  " + t);
        }
        

        digester = new Digester();
        // Configure the digester as required
        digester.addObjectCreate("employee", Employee.class);
        // try out primitive convertion
        digester.addCallMethod("employee", "setAge", 1, new Class[] {int.class});
        digester.addCallParam("employee/age", 0);         
                
        // Parse our test input
        root1 = null;
        try {
            // an exception will be thrown if the method can't be found
            root1 = digester.parse(getInputStream("Test5.xml"));
            Employee employee = (Employee) root1;
            assertEquals("Failed to call Employee.setAge", 21, employee.getAge()); 
            
        } catch (Throwable t) {
            // this means that the method can't be found and so the test fails
            fail("Digester threw Exception:  " + t);
        }
        
        digester = new Digester();
        // Configure the digester as required
        digester.addObjectCreate("employee", Employee.class);      
        digester.addCallMethod("employee", "setActive", 1, new Class[] {boolean.class});
        digester.addCallParam("employee/active", 0);    
                
        // Parse our test input
        root1 = null;
        try {
            // an exception will be thrown if the method can't be found
            root1 = digester.parse(getInputStream("Test5.xml"));
            Employee employee = (Employee) root1;
            assertEquals("Failed to call Employee.setActive", true, employee.isActive()); 
            
        } catch (Throwable t) {
            // this means that the method can't be found and so the test fails
            fail("Digester threw Exception:  " + t);
        }
        
        digester = new Digester();            
        // Configure the digester as required
        digester.addObjectCreate("employee", Employee.class); 
        digester.addCallMethod("employee", "setSalary", 1, new Class[] {float.class});
        digester.addCallParam("employee/salary", 0);    
                
        // Parse our test input
        root1 = null;
        try {
            // an exception will be thrown if the method can't be found
            root1 = digester.parse(getInputStream("Test5.xml"));
            Employee employee = (Employee) root1;
            assertEquals("Failed to call Employee.setSalary", 1000000.0f, employee.getSalary(), 0.1f); 

        } catch (Throwable t) {
            // this means that the method can't be found and so the test fails
            fail("Digester threw Exception:  " + t);
        }
    }
    
    /**
     */
    public void testSetCustomProperties() throws Exception {
        
        Digester digester = new Digester();
        
        digester.setValidating(false);
        
        digester.addObjectCreate("toplevel", ArrayList.class);
        digester.addObjectCreate("toplevel/one", Address.class);
        digester.addSetNext("toplevel/one", "add");
        digester.addObjectCreate("toplevel/two", Address.class);
        digester.addSetNext("toplevel/two", "add");
        digester.addObjectCreate("toplevel/three", Address.class);
        digester.addSetNext("toplevel/three", "add");
        digester.addObjectCreate("toplevel/four", Address.class);
        digester.addSetNext("toplevel/four", "add");
        digester.addSetProperties("toplevel/one");
        digester.addSetProperties(
                    "toplevel/two", 
                    new String[] {"alt-street", "alt-city", "alt-state"}, 
                    new String[] {"street", "city", "state"});
        digester.addSetProperties(
                    "toplevel/three", 
                    new String[] {"aCity", "state"}, 
                    new String[] {"city"});
        digester.addSetProperties("toplevel/four", "alt-city", "city");
        

        ArrayList root = (ArrayList) digester.parse(getInputStream("Test7.xml"));
        
        assertEquals("Wrong array size", 4, root.size());
        
        // note that the array is in popped order (rather than pushed)
         
        Object 
        obj = root.get(0);
        assertTrue("(1) Should be an Address ", obj instanceof Address);
        Address addressOne = (Address) obj;
        assertEquals("(1) Street attribute", "New Street", addressOne.getStreet());
        assertEquals("(1) City attribute", "Las Vegas", addressOne.getCity());
        assertEquals("(1) State attribute", "Nevada", addressOne.getState());
        
        obj = root.get(1);
        assertTrue("(2) Should be an Address ", obj instanceof Address);
        Address addressTwo = (Address) obj;
        assertEquals("(2) Street attribute", "Old Street", addressTwo.getStreet());
        assertEquals("(2) City attribute", "Portland", addressTwo.getCity());
        assertEquals("(2) State attribute", "Oregon", addressTwo.getState());
        
        obj = root.get(2);
        assertTrue("(3) Should be an Address ", obj instanceof Address);
        Address addressThree = (Address) obj;
        assertEquals("(3) Street attribute", "4th Street", addressThree.getStreet());
        assertEquals("(3) City attribute", "Dayton", addressThree.getCity());
        assertEquals("(3) State attribute", "US" , addressThree.getState());
       
        obj = root.get(3);
        assertTrue("(4) Should be an Address ", obj instanceof Address);
        Address addressFour = (Address) obj;
        assertEquals("(4) Street attribute", "6th Street", addressFour.getStreet());
        assertEquals("(4) City attribute", "Cleveland", addressFour.getCity());
        assertEquals("(4) State attribute", "Ohio", addressFour.getState());
        
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


    /**
     * Validate the assertions for ObjectCreateRule3.
     *
     * @param object Root object returned by <code>digester.parse()</code>
     */
    protected void validateObjectCreate3(Object root) {

        // Validate the retrieved Employee
        assertNotNull("Digester returned an object", root);
        assertTrue("Digester returned an Employee",
                root instanceof Employee);
        Employee employee = (Employee) root;
        assertEquals("First name is correct",
                "First Name",
                employee.getFirstName());
        assertEquals("Last name is correct",
                "Last Name",
                employee.getLastName());

        // Validate the corresponding "home" Address
        Address home = employee.getAddress("home");
        assertNotNull("Retrieved home address", home);
        assertEquals("Home street", "Home Street",
                home.getStreet());
        assertEquals("Home city", "Home City",
                home.getCity());
        assertEquals("Home state", "HS",
                home.getState());
        assertEquals("Home zip", "HmZip",
                home.getZipCode());

        // Validate the corresponding "office" Address
        Address office = employee.getAddress("office");
        assertNotNull("Retrieved office address", office);
        assertEquals("Office street", "Office Street",
                office.getStreet());
        assertEquals("Office city", "Office City",
                office.getCity());
        assertEquals("Office state", "OS",
                office.getState());
        assertEquals("Office zip", "OfZip",
                office.getZipCode());

    }
    

}

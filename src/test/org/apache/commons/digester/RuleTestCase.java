/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/test/org/apache/commons/digester/RuleTestCase.java,v 1.9 2002/01/23 22:38:01 sanders Exp $
 * $Revision: 1.9 $
 * $Date: 2002/01/23 22:38:01 $
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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * <p>Test Case for the Digester class.  These tests perform parsing of
 * XML documents to exercise the built-in rules.</p>
 *
 * @author Craig R. McClanahan
 * @author Janek Bogucki
 * @version $Revision: 1.9 $ $Date: 2002/01/23 22:38:01 $
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

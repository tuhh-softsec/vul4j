/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/test/org/apache/commons/digester/RuleTestCase.java,v 1.2 2001/08/20 22:06:23 craigmcc Exp $
 * $Revision: 1.2 $
 * $Date: 2001/08/20 22:06:23 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
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


import java.io.InputStream;
import java.io.IOException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;



/**
 * <p>Test Case for the Digester class.  These tests perform parsing of
 * XML documents to exercise the built-in rules.</p>
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.2 $ $Date: 2001/08/20 22:06:23 $
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
        digester.addObjectCreate("employee",
                                 "org.apache.commons.digester.Employee");
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
        digester.addObjectCreate("employee",
                                 "org.apache.commons.digester.Employee");
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

/*
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


package org.apache.commons.digester.xmlrules;


import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import org.apache.commons.digester.Digester;
import junit.framework.TestCase;
import junit.framework.TestSuite;


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
        TestSuite suite = new TestSuite(DigesterLoaderTestSuite.class);

        return suite;
    }
    
    /**
     * Tests the DigesterLoader.createDigester(), with multiple
     * included rule sources: testrules.xml includes another rules xml
     * file, and also includes programmatically created rules.
     */
    public void testCreateDigester() throws Exception {
        URL rules = ClassLoader.getSystemResource("org/apache/commons/digester/xmlrules/testrules.xml");
        URL input = ClassLoader.getSystemResource("org/apache/commons/digester/xmlrules/test.xml");
        assertNotNull("The test could not locate testrules.xml", rules);
        assertNotNull("The test could not locate test.xml", input);
        Digester digester = DigesterLoader.createDigester(rules);
        digester.push(new ArrayList());
        digester.setDebug(0);
        Object root = digester.parse(input.openStream());
        assertEquals(root.toString(), "[foo1 baz1 foo2, foo3 foo4]");
        //System.out.println(root);
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
        Object root = DigesterLoader.load(rules, classLoader, input, new ArrayList());
        if (! (root instanceof ArrayList)) {
            fail("Unexpected object returned from DigesterLoader. Expected ArrayList; got " + root.getClass().getName());
        }
        assertEquals(root.toString(), "[foo1 baz1 foo2, foo3 foo4]");
    }
    
    /**
     * The same as testLoad1, exception the input file is passed to
     * DigesterLoader as an InputStream instead of a URL.
     */
    public void testLoad2() throws Exception {
        URL rules = ClassLoader.getSystemResource("org/apache/commons/digester/xmlrules/testrules.xml");
        InputStream input = ClassLoader.getSystemResource("org/apache/commons/digester/xmlrules/test.xml").openStream();
        Object root = DigesterLoader.load(rules, getClass().getClassLoader(), input, new ArrayList());
        if (! (root instanceof ArrayList)) {
            fail("Unexpected object returned from DigesterLoader. Expected ArrayList; got " + root.getClass().getName());
        }
        assertEquals(root.toString(), "[foo1 baz1 foo2, foo3 foo4]");
    }
 

    /**
     * Validates that circular includes are detected and result in an exception
     */
    public void testCircularInclude1() {
        URL rules = ClassLoader.getSystemResource("org/apache/commons/digester/xmlrules/testCircularRules.xml");
        try {
            Digester digester = DigesterLoader.createDigester(rules);
        } catch (Exception ex) {
            return;
        }
        fail("Creating a digester with circular rules should have thrown CircularIncludeException.");
    }
        
        
}

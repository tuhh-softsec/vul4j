/*
 * Copyright 2006-2009 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
/*
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 */
package javax.xml.crypto.test.dsig.dom;

import java.io.*;
import javax.xml.crypto.dsig.dom.DOMValidateContext;

import junit.framework.*;

import javax.xml.crypto.test.dsig.TestUtils;

/**
 * Unit test for javax.xml.crypto.dsig.dom.DOMValidateContext
 *
 * @version $Id$
 * @author Valerie Peng
 */
public class DOMValidateContextTest extends TestCase {
    private DOMValidateContext domVC;

    public DOMValidateContextTest() {
	super("DOMValidateContext");
    }

    public DOMValidateContextTest(String name) {
	super(name);
    }

    public void setUp() throws Exception {
	String fs = System.getProperty("file.separator");
	String base = System.getProperty("basedir") == null ? "./": System.getProperty("basedir");
	File dir = new File(base + fs +
	    "data" + fs + "ie" + fs + "baltimore" + fs + "merlin-examples",
	    "merlin-xmldsig-twenty-three");
	File input = new File(dir, "signature.xml");
	domVC = (DOMValidateContext)
	    TestUtils.getXMLValidateContext("DOM", input, "Reference");
    }

    public void tearDown() {}

    public void testConstructor() throws Exception {
	assertNotNull(domVC);
	try {
	    new DOMValidateContext(TestUtils.getPublicKey("RSA"), null);
	    fail("Should throw a NPE for null node");
	} catch (NullPointerException npe) {
	} catch (Exception ex) {
	    fail("Should throw a NPE instead of " + ex + " for null node");
	}
    }

    public void testsetngetProperty() throws Exception {
	try {
	    domVC.setProperty(null, "value");
	} catch (NullPointerException npe) {
	} catch (Exception ex) {
	    fail("Should throw a NPE instead of " + ex + " for null name");
	}
	try {
	    domVC.getProperty(null);
	} catch (NullPointerException npe) {
	} catch (Exception ex) {
	    fail("Should throw a NPE instead of " + ex + " for null name");
	}
	String pname = "name";
	String pvalue1 = "value";
	String pvalue2 = "newvalue";
	assertNull(domVC.setProperty(pname, pvalue1));
	assertEquals((String)domVC.getProperty(pname), pvalue1);
	assertEquals(domVC.setProperty(pname, pvalue2), pvalue1);
       	assertEquals((String)domVC.getProperty(pname), pvalue2);
    }

    public void testsetngetNode() throws Exception {
	try {
	    domVC.setNode(null);
	} catch (NullPointerException npe) {
	} catch (Exception ex) {
	    fail("Should throw a NPE instead of " + ex + " for null node");
	}
	assertNotNull(domVC.getNode());
    }
}

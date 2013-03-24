/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/*
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 */
package javax.xml.crypto.test.dsig.dom;

import java.io.*;
import javax.xml.crypto.dsig.dom.DOMValidateContext;

import javax.xml.crypto.test.dsig.TestUtils;

/**
 * Unit test for javax.xml.crypto.dsig.dom.DOMValidateContext
 *
 * @author Valerie Peng
 */
public class DOMValidateContextTest extends org.junit.Assert {
    private DOMValidateContext domVC;

    public DOMValidateContextTest() throws Exception {
        String fs = System.getProperty("file.separator");
        String base = System.getProperty("basedir") == null ? "./": System.getProperty("basedir");
        File dir = new File(base + fs +
            "src/test/resources" + fs + "ie" + fs + "baltimore" + fs + "merlin-examples",
            "merlin-xmldsig-twenty-three");
        File input = new File(dir, "signature.xml");
        domVC = (DOMValidateContext)
            TestUtils.getXMLValidateContext("DOM", input, "Reference");
    }

    @org.junit.Test
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

    @org.junit.Test
    public void testSetGetProperty() throws Exception {
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
        assertEquals(domVC.getProperty(pname), pvalue1);
        assertEquals(domVC.setProperty(pname, pvalue2), pvalue1);
        assertEquals(domVC.getProperty(pname), pvalue2);
    }

    @org.junit.Test
    public void testSetGetNode() throws Exception {
        try {
            domVC.setNode(null);
        } catch (NullPointerException npe) {
        } catch (Exception ex) {
            fail("Should throw a NPE instead of " + ex + " for null node");
        }
        assertNotNull(domVC.getNode());
    }
    
}

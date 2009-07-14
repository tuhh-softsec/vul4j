/*
 * Copyright 2008-2009 The Apache Software Foundation.
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
package javax.xml.crypto.test.dsig;

import java.io.File;
import java.security.Security;
import javax.xml.crypto.KeySelector;

import junit.framework.*;

import javax.xml.crypto.test.KeySelectors;

/**
 * This is a testcase to validate all the W3C xmldsig C14N11 testcases.
 *
 * @author Sean Mullan
 */
public class C14N11Test extends TestCase {

    private SignatureValidator validator;
    private File dir;
    private KeySelector sks;
    private static String[] vendors = { "IAIK", "IBM", "ORCL", "SUN", "UPC" };

    static {
        Security.insertProviderAt
            (new org.jcp.xml.dsig.internal.dom.XMLDSigRI(), 1);
    }

    public C14N11Test(String name) throws Exception {
        super(name);
        String fs = System.getProperty("file.separator");
        String base = System.getProperty("basedir") == null ? "./": System.getProperty("basedir");
    	dir = new File(base + fs + "data" + fs
            + "org" + fs + "w3c" + fs + "www" + fs
	    + "interop" + fs + "xmldsig" + fs + "c14n11");
        validator = new SignatureValidator(dir);
	sks = new KeySelectors.SecretKeySelector("secret".getBytes("ASCII"));
    }

    public void test_xmlid_1() throws Exception {
        test_c14n11("xmlid-1");
    }

    public void test_xmlid_2() throws Exception {
        test_c14n11("xmlid-2");
    }

    public void test_xmlspace_1() throws Exception {
        test_c14n11("xmlspace-1");
    }

    public void test_xmlspace_2() throws Exception {
        test_c14n11("xmlspace-2");
    }

    public void test_xmlspace_3() throws Exception {
        test_c14n11("xmlspace-3");
    }

    public void test_xmlspace_4() throws Exception {
        test_c14n11("xmlspace-4");
    }

    public void test_xmllang_1() throws Exception {
        test_c14n11("xmllang-1");
    }

    public void test_xmllang_2() throws Exception {
        test_c14n11("xmllang-2");
    }

    public void test_xmllang_3() throws Exception {
        test_c14n11("xmllang-3");
    }

    public void test_xmllang_4() throws Exception {
        test_c14n11("xmllang-4");
    }

    public void test_xmlbase_prop_1() throws Exception {
        test_c14n11("xmlbase-prop-1");
    }

    public void test_xmlbase_prop_2() throws Exception {
        test_c14n11("xmlbase-prop-2");
    }

    public void test_xmlbase_prop_3() throws Exception {
        test_c14n11("xmlbase-prop-3");
    }

    public void test_xmlbase_prop_4() throws Exception {
        test_c14n11("xmlbase-prop-4");
    }

    public void test_xmlbase_prop_5() throws Exception {
        test_c14n11("xmlbase-prop-5");
    }

    public void test_xmlbase_prop_6() throws Exception {
        test_c14n11("xmlbase-prop-6");
    }

    public void test_xmlbase_prop_7() throws Exception {
        test_c14n11("xmlbase-prop-7");
    }

    public void test_xmlbase_c14n11spec_102() throws Exception {
	String[] vendors = {"IAIK", "IBM", "ORCL", "SUN", "UPC"};
        test_c14n11("xmlbase-c14n11spec-102", vendors);
    }

    public void test_xmlbase_c14n11spec2_102() throws Exception {
	String[] vendors = {"IAIK", "IBM", "ORCL", "SUN"};
        test_c14n11("xmlbase-c14n11spec2-102", vendors);
    }

    public void test_xmlbase_c14n11spec3_103() throws Exception {
	String[] vendors = {"IAIK", "IBM", "ORCL", "SUN", "UPC"};
        test_c14n11("xmlbase-c14n11spec3-103", vendors);
    }

    private void test_c14n11(String test) throws Exception {
	for (int i=0; i<vendors.length; i++) {
	    String file = test + "-" + vendors[i] + ".xml";
	    // System.out.println("Validating " + file);
            boolean coreValidity = validator.validate(file, sks);
            assertTrue(file + " failed core validation", coreValidity);
	}
    }

    private void test_c14n11(String test, String[] vendors) throws Exception {
	for (int i=0; i<vendors.length; i++) {
	    String file = test + "-" + vendors[i] + ".xml";
	    // System.out.println("Validating " + file);
            boolean coreValidity = validator.validate(file, sks);
            assertTrue(file + " failed core validation", coreValidity);
	}
    }
}

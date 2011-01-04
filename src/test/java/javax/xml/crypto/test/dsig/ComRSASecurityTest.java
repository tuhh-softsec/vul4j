/*
 * Copyright 2006 The Apache Software Foundation.
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
package javax.xml.crypto.test.dsig;

import java.io.File;
import java.security.Security;

import junit.framework.*;

import javax.xml.crypto.test.KeySelectors;

/**
 * This is a testcase to validate all "bdournaee" 
 * testcases from RSA
 *
 * @author Sean Mullan
 */
public class ComRSASecurityTest extends TestCase {

    private SignatureValidator validator;

    static {
        Security.insertProviderAt
            (new org.jcp.xml.dsig.internal.dom.XMLDSigRI(), 1);
    }

    public ComRSASecurityTest(String name) {
        super(name);
	String fs = System.getProperty("file.separator");
	String base = System.getProperty("basedir") == null ? "./": System.getProperty("basedir");
	
	base += fs + "data" + fs + "com";
	validator = new SignatureValidator(new File
	    (base, "rsasecurity/bdournaee"));
    }
    public void test_certj201_enveloping() throws Exception {
        String file = "certj201_enveloping.xml";

	boolean coreValidity = validator.validate
	    (file, new KeySelectors.KeyValueKeySelector());
	assertTrue("Signature failed core validation", coreValidity);
    }
    public void test_certj201_enveloped() throws Exception {
        String file = "certj201_enveloped.xml";

	boolean coreValidity = validator.validate
	    (file, new KeySelectors.KeyValueKeySelector());
	assertTrue("Signature failed core validation", coreValidity);
    }
    public static void main(String[] args) throws Exception {
        ComRSASecurityTest ct = new ComRSASecurityTest("");
	ct.test_certj201_enveloped();
	ct.test_certj201_enveloping();
    }
}

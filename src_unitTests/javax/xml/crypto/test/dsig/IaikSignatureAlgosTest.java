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
package javax.xml.crypto.test.dsig;

import java.io.File;
import java.security.Security;
import javax.xml.crypto.dsig.XMLSignatureException;

import junit.framework.*;

import javax.xml.crypto.test.KeySelectors;

/**
 * This is a testcase to validate all "signatureAlgorithms" 
 * testcases from IAIK
 *
 * @author Sean Mullan
 */
public class IaikSignatureAlgosTest extends TestCase {

    private SignatureValidator validator;

    static {
        Security.insertProviderAt
            (new org.jcp.xml.dsig.internal.dom.XMLDSigRI(), 1);
    }

    public IaikSignatureAlgosTest(String name) {
        super(name);
	String fs = System.getProperty("file.separator");
	String base = System.getProperty("basedir") == null ? "./": System.getProperty("basedir");
	base +=  fs + "data" + fs
	    + "at" + fs + "iaik" + fs + "ixsil";
	validator = new SignatureValidator(new File
	    (base, "signatureAlgorithms/signatures"));
    }
    public void test_dsaSignature() throws Exception {
        String file = "dSASignature.xml";

	boolean coreValidity = validator.validate(file, new 
	    KeySelectors.KeyValueKeySelector());
	assertTrue("Signature failed core validation", coreValidity);
    }
    public void test_rsaSignature() throws Exception {
        String file = "rSASignature.xml";

	boolean coreValidity = validator.validate(file, new 
	    KeySelectors.KeyValueKeySelector());
	assertTrue("Signature failed core validation", coreValidity);
    }
    public void test_hmacShortSignature() throws Exception {
        String file = "hMACShortSignature.xml";

        try {
	    boolean coreValidity = validator.validate(file, new 
	        KeySelectors.SecretKeySelector("secret".getBytes("ASCII")));
            fail("Expected HMACOutputLength Exception");
        } catch (XMLSignatureException xse) {
            System.out.println(xse.getMessage());
            // pass
        }
    }
    public void test_hmacSignature() throws Exception {
        String file = "hMACSignature.xml";

	boolean coreValidity = validator.validate(file, new
	    KeySelectors.SecretKeySelector("secret".getBytes("ASCII")));
	assertTrue("Signature failed core validation", coreValidity);
    }
    public static void main(String[] args) throws Exception {
        IaikSignatureAlgosTest it = new IaikSignatureAlgosTest("");
	it.test_dsaSignature();
	it.test_rsaSignature();
	it.test_hmacShortSignature();
	it.test_hmacSignature();
    }
}

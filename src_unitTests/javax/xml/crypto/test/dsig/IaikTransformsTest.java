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
 * This is a testcase to validate all "transforms" 
 * testcases from IAIK
 *
 * @author Sean Mullan
 */
public class IaikTransformsTest extends TestCase {

    private SignatureValidator validator;

    static {
        Security.insertProviderAt
            (new org.jcp.xml.dsig.internal.dom.XMLDSigRI(), 1);
    }

    public IaikTransformsTest(String name) {
        super(name);
	String fs = System.getProperty("file.separator");
	String base = System.getProperty("basedir") == null ? "./": System.getProperty("basedir");
	base +=  fs + "data" + fs +
	    "at" + fs + "iaik" + fs + "ixsil";
	validator = new SignatureValidator(new File
	    (base, "transforms/signatures"));
    }
    public void test_base64DecodeSignature() throws Exception {
        String file = "base64DecodeSignature.xml";

	boolean coreValidity = validator.validate
	    (file, new KeySelectors.KeyValueKeySelector());
	assertTrue("Signature failed core validation", coreValidity);

    }
    public void test_envelopedSignatureSignature() throws Exception {
        String file = "envelopedSignatureSignature.xml";

	boolean coreValidity = validator.validate
	    (file, new KeySelectors.KeyValueKeySelector());
	assertTrue("Signature failed core validation", coreValidity);
    }
    public void test_c14nSignature() throws Exception {
        String file = "c14nSignature.xml";

	boolean coreValidity = validator.validate
	    (file, new KeySelectors.KeyValueKeySelector());
	assertTrue("Signature failed core validation", coreValidity);
    }
    public void test_xPathSignature() throws Exception {
        String file = "xPathSignature.xml";

	boolean coreValidity = validator.validate
	    (file, new KeySelectors.KeyValueKeySelector());
	assertTrue("Signature failed core validation", coreValidity);
    }
    public static void main(String[] args) throws Exception {
        IaikTransformsTest it = new IaikTransformsTest("");
	it.test_xPathSignature();
	it.test_c14nSignature();
	it.test_base64DecodeSignature();
	it.test_envelopedSignatureSignature();
    }
}

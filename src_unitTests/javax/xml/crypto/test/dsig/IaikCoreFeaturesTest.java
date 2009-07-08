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
import java.io.FileInputStream;
import java.security.Security;
import javax.xml.crypto.*;

import junit.framework.*;

import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.test.KeySelectors;

/**
 * This is a testcase to validate all "coreFeatures" 
 * testcases from IAIK
 *
 * @author Sean Mullan
 */
public class IaikCoreFeaturesTest extends TestCase {

    private SignatureValidator validator;
    private String base;

    static {
        Security.insertProviderAt
            (new org.jcp.xml.dsig.internal.dom.XMLDSigRI(), 1);
    }

    public IaikCoreFeaturesTest(String name) {
        super(name);
	String fs = System.getProperty("file.separator");
	base = System.getProperty("basedir") == null ? "./": System.getProperty("basedir");
	base = base + fs + "data" + fs +
	    "at" + fs + "iaik" + fs + "ixsil";
	validator = new SignatureValidator(new File
	    (base, "coreFeatures/signatures"));
    }
    
    public void test_anonymousReferenceSignature() throws Exception {
        String file = "anonymousReferenceSignature.xml";

	boolean coreValidity = validator.validate
	    (file, new KeySelectors.KeyValueKeySelector(), 
	     new NullURIDereferencer(base));
	assertTrue("Signature failed core validation", coreValidity);
    }
    
    public void test_manifestSignature() throws Exception {
        String file = "manifestSignature.xml";

	boolean coreValidity = validator.validate
	    (file, new KeySelectors.KeyValueKeySelector());
	assertTrue("Signature failed core validation", coreValidity);
    }
    
    public void test_signatureTypesSignature() throws Exception {
        String file = "signatureTypesSignature.xml";

	boolean coreValidity = validator.validate
	    (file, new KeySelectors.KeyValueKeySelector(),
	            new OfflineDereferencer());
	assertTrue("Signature failed core validation", coreValidity);
    }
    
    public static void main(String[] args) throws Exception {
        IaikCoreFeaturesTest it = new IaikCoreFeaturesTest("");
	it.test_anonymousReferenceSignature();
	it.test_manifestSignature();
	it.test_signatureTypesSignature();
    }

    private static class NullURIDereferencer implements URIDereferencer {

	private OctetStreamData osd;

	NullURIDereferencer(String base) throws Exception {
	    File content = new File
		(base, "coreFeatures/samples/anonymousReferenceContent.xml");
	    osd = new OctetStreamData(new FileInputStream(content));
	}

        public Data dereference(URIReference uriReference, 
	    XMLCryptoContext context) throws URIReferenceException {

	    if (uriReference.getURI() != null) {
		throw new URIReferenceException("must be a null URI");
	    }

	    return osd;
	}
    }
    
    private static class OfflineDereferencer implements URIDereferencer {
        private String w3cRec;
        private URIDereferencer defaultDereferencer;

        OfflineDereferencer() throws Exception {
            String fs = System.getProperty("file.separator");
            String base = System.getProperty("basedir") == null ? "./" : 
                System.getProperty("basedir");
            w3cRec = base + fs + "data" + fs + "org" + fs + "w3c" + fs + "www" + 
                fs + "TR" + fs + "2000";
            defaultDereferencer =
                XMLSignatureFactory.getInstance().getURIDereferencer();
        }

        public Data dereference(URIReference uriReference, 
                XMLCryptoContext context) throws URIReferenceException {

            try {
                if ("http://www.w3.org/TR/2000/REC-xml-20001006".equals(uriReference.getURI())) {
                    File content = new File(w3cRec, "REC-xml-20001006");
                    return new OctetStreamData(new FileInputStream(content));
                } else {
                    return defaultDereferencer.dereference(uriReference, context);
                }
            } catch (java.io.FileNotFoundException ex) {
                throw new URIReferenceException(ex.getMessage(), ex);
            }
        }
    }
}

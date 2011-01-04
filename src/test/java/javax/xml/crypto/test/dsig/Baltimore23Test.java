/*
 * Copyright 2006-2010 The Apache Software Foundation.
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
import java.security.KeyStore;
import java.security.Security;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.dsig.XMLSignatureException;

import junit.framework.*;

import javax.xml.crypto.test.KeySelectors;

/**
 * This is a testcase to validate all "merlin-xmldsig-twenty-three" 
 * testcases from Baltimore
 *
 * @author Sean Mullan
 */
public class Baltimore23Test extends TestCase {

    private SignatureValidator validator;
    private File dir;
    private final URIDereferencer ud;

    static {
        Security.insertProviderAt
            (new org.jcp.xml.dsig.internal.dom.XMLDSigRI(), 1);
    }

    public Baltimore23Test(String name) {
        super(name);
	String fs = System.getProperty("file.separator");
	String base = System.getProperty("basedir") == null ? "./": System.getProperty("basedir");
	
	dir = new File(base + fs + "data" + fs 
	    + "ie" + fs + "baltimore" + fs + "merlin-examples",
            "merlin-xmldsig-twenty-three");
	validator = new SignatureValidator(dir);
        ud = new LocalHttpCacheURIDereferencer();
    }

    public void test_signature_enveloped_dsa() throws Exception {
        String file = "signature-enveloped-dsa.xml";

	boolean coreValidity = validator.validate
	    (file, new KeySelectors.KeyValueKeySelector());
	assertTrue("Signature failed core validation", coreValidity);
    }
    
    public void test_signature_enveloping_b64_dsa() throws Exception {
        String file = "signature-enveloping-b64-dsa.xml";
     
	boolean coreValidity = validator.validate
	    (file, new KeySelectors.KeyValueKeySelector());
	assertTrue("Signature failed core validation", coreValidity);
    }
    
    public void test_signature_enveloping_dsa() throws Exception {
        String file = "signature-enveloping-dsa.xml";
       
	boolean coreValidity = validator.validate
	    (file, new KeySelectors.KeyValueKeySelector());
	assertTrue("Signature failed core validation", coreValidity);
    }

    public void test_signature_external_b64_dsa() throws Exception {
        String file = "signature-external-b64-dsa.xml";
       
	boolean coreValidity = validator.validate
	    (file, new KeySelectors.KeyValueKeySelector());
	assertTrue("Signature failed core validation", coreValidity);
    }

    public void test_signature_external_dsa() throws Exception {
        String file = "signature-external-dsa.xml";
        
	boolean coreValidity = validator.validate
	    (file, new KeySelectors.KeyValueKeySelector(), ud);
	assertTrue("Signature failed core validation", coreValidity);
    }
    
    public void test_signature_enveloping_rsa() throws Exception {
        String file = "signature-enveloping-rsa.xml";
       
	boolean coreValidity = validator.validate
	    (file, new KeySelectors.KeyValueKeySelector());
	assertTrue("Signature failed core validation", coreValidity);
    }

    public void test_signature_enveloping_hmac_sha1() throws Exception {
        String file = "signature-enveloping-hmac-sha1.xml";
        
	KeySelector ks = new KeySelectors.SecretKeySelector
	    ("secret".getBytes("ASCII") );
	boolean coreValidity = validator.validate(file, ks);
	assertTrue("Signature failed core validation", coreValidity);
    }

    public void test_signature_enveloping_hmac_sha1_40() throws Exception {
        String file = "signature-enveloping-hmac-sha1-40.xml";
        
	KeySelector ks = new KeySelectors.SecretKeySelector
	    ("secret".getBytes("ASCII") );
        try {
	    boolean coreValidity = validator.validate(file, ks);
            fail("Expected HMACOutputLength exception");
        } catch (XMLSignatureException xse) {
            System.out.println(xse.getMessage());
            // pass
        }
    }

    public void test_signature_keyname() throws Exception {
        String file = "signature-keyname.xml";
        
	boolean coreValidity = validator.validate
	    (file, new KeySelectors.CollectionKeySelector(dir), ud);
	assertTrue("Signature failed core validation", coreValidity);
    }
    
    public void test_signature_retrievalmethod_rawx509crt() throws Exception {
        String file = "signature-retrievalmethod-rawx509crt.xml";
        
	boolean coreValidity = validator.validate
	    (file, new KeySelectors.CollectionKeySelector(dir), ud);
	assertTrue("Signature failed core validation", coreValidity);
    }
    
    public void test_signature_x509_crt_crl() throws Exception {
        String file = "signature-x509-crt-crl.xml";
        
	boolean coreValidity = validator.validate
	    (file, new KeySelectors.RawX509KeySelector(), ud);
	assertTrue("Signature failed core validation", coreValidity);
    }

    public void test_signature_x509_crt() throws Exception {
        String file = "signature-x509-crt.xml";
       
	boolean coreValidity = validator.validate
	    (file, new KeySelectors.RawX509KeySelector(), ud);
	assertTrue("Signature failed core validation", coreValidity);
    }
    
    public void test_signature_x509_is() throws Exception {
        String file = "signature-x509-is.xml";
        
	boolean coreValidity = validator.validate
	    (file, new KeySelectors.CollectionKeySelector(dir), ud);
	assertTrue("Signature failed core validation", coreValidity);
    }
    
    public void test_signature_x509_ski() throws Exception {
        String file = "signature-x509-ski.xml";
        
	boolean coreValidity = validator.validate
	    (file, new KeySelectors.CollectionKeySelector(dir), ud);
	assertTrue("Signature failed core validation", coreValidity);
    }

    public void test_signature_x509_sn() throws Exception {
        String file = "signature-x509-sn.xml";
        
	boolean coreValidity = validator.validate
	    (file, new KeySelectors.CollectionKeySelector(dir), ud);
	assertTrue("Signature failed core validation", coreValidity);
    }

    public void test_signature() throws Exception {
        String file = "signature.xml";
	String fs = System.getProperty("file.separator");
	String base = System.getProperty("basedir") == null ? "./": System.getProperty("basedir");
	
	String keystore = base + fs + "data" + fs +
	     "ie" + fs + "baltimore" + fs + "merlin-examples" + fs +
	     "merlin-xmldsig-twenty-three" + fs + "certs" + fs + "xmldsig.jks";
	KeyStore ks = KeyStore.getInstance("JKS");
	ks.load(new FileInputStream(keystore), "changeit".toCharArray());

        boolean cv = validator.validate(file, new X509KeySelector(ks, false), ud);
        assertTrue("Signature failed core validation", cv);
    }

    public static void main(String[] args) throws Exception {
        Baltimore23Test bt = new Baltimore23Test("");
        bt.test_signature_enveloped_dsa();
	bt.test_signature(); 
	bt.test_signature_enveloping_b64_dsa();
	bt.test_signature_enveloping_dsa();
	bt.test_signature_enveloping_hmac_sha1();
	bt.test_signature_enveloping_hmac_sha1_40();
	bt.test_signature_enveloping_rsa();
	bt.test_signature_external_b64_dsa();
	bt.test_signature_external_dsa();
	bt.test_signature_keyname();
	bt.test_signature_retrievalmethod_rawx509crt();
	bt.test_signature_x509_crt_crl();
	bt.test_signature_x509_crt();
	bt.test_signature_x509_is();
	bt.test_signature_x509_ski();
	bt.test_signature_x509_sn();
    }
}

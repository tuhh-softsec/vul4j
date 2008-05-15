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

import java.io.*;
import java.util.*;
import java.security.*;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.keyinfo.*;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.transform.*;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import junit.framework.*;

/**
 * Unit test for javax.xml.crypto.dsig.XMLSignature
 *
 * @version $Id$
 * @author Valerie Peng
 */
public class XMLSignatureTest extends TestCase {
    private XMLSignatureFactory fac;
    private KeyInfoFactory kifac;
    private SignedInfo defSi;
    private KeyInfo defKi;
    private List objs;
    private String id = "id";
    private String sigValueId = "signatureValueId";
    private Key[] SIGN_KEYS;
    private Key[] VALIDATE_KEYS;
    private SignatureMethod[] SIG_METHODS;

    static {
        Security.insertProviderAt
            (new org.jcp.xml.dsig.internal.dom.XMLDSigRI(), 1);
    }

    public XMLSignatureTest() {
	super("XMLSignatureTest");
    }

    public XMLSignatureTest(String name) {
	super(name);
    }

    public void setUp() throws Exception {
	fac = XMLSignatureFactory.getInstance
            ("DOM", new org.jcp.xml.dsig.internal.dom.XMLDSigRI());
	kifac = KeyInfoFactory.getInstance
            ("DOM", new org.jcp.xml.dsig.internal.dom.XMLDSigRI());

	// set up the corresponding SignatureMethod 
	SIG_METHODS = new SignatureMethod[3];
	SIG_METHODS[0] = fac.newSignatureMethod(SignatureMethod.DSA_SHA1, null);
	SIG_METHODS[1] = fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null);
	SIG_METHODS[2] = fac.newSignatureMethod(SignatureMethod.HMAC_SHA1, null);
	// set up the signingKeys
	SIGN_KEYS = new Key[3];
	SIGN_KEYS[0] = TestUtils.getPrivateKey("DSA");
	SIGN_KEYS[1] = TestUtils.getPrivateKey("RSA");
	SIGN_KEYS[2] = new SecretKeySpec(new byte[16], "HmacSHA1");
	// set up the validatingKeys
	VALIDATE_KEYS = new Key[3];
	VALIDATE_KEYS[0] = TestUtils.getPublicKey("DSA");
	VALIDATE_KEYS[1] = TestUtils.getPublicKey("RSA");
	VALIDATE_KEYS[2] = new SecretKeySpec(new byte[16], "HmacSHA1");
	defSi = createSignedInfo(SIG_METHODS[0]);
	defKi = kifac.newKeyInfo
	    (Collections.singletonList(kifac.newKeyName("Alice")));
	objs = Collections.singletonList
	    (fac.newXMLObject(null, null, null, null));
    }

    public void tearDown() {}

    public void testConstructor() throws Exception {
	XMLSignature sig = null;
	// test  XMLSignatureFactory.newXMLSignature(SignedInfo, KeyInfo)
	// and  XMLSignatureFactory.newXMLSignature(SignedInfo,
	//          KeyInfo, List, String, String)
	// for generating XMLSignature objects
	for (int i = 0; i < 2; i++) {
	    try {
		switch (i) {
		case 0:
		    sig = fac.newXMLSignature(null, defKi);
		    break;
		case 1:
		    sig = fac.newXMLSignature(null, defKi, objs, id, sigValueId);
		    break;
		}
		fail("Should throw a NPE for null references");
	    } catch (NullPointerException npe) {
	    } catch (Exception ex) {
		fail("Should throw a NPE instead of " + ex + 
		     " for null references");
	    }
	}
	try {
	    sig = fac.newXMLSignature(defSi, defKi, 
		     Collections.singletonList("wrongType"), id, sigValueId);
	    fail("Should throw a CCE for invalid objects");
	} catch (ClassCastException cce) {
	} catch (Exception ex) {
	    fail("Should throw a CCE instead of " + ex + 
		 " for invalid objects");
	}
	sig = fac.newXMLSignature(defSi, defKi, objs, id, sigValueId);
	assertEquals(sig.getId(), id);
	assertEquals(sig.getKeyInfo(), defKi);
	assertTrue(Arrays.equals(sig.getObjects().toArray(), objs.toArray()));
	assertNull(sig.getSignatureValue().getValue());
	assertEquals(sig.getSignatureValue().getId(), sigValueId);
	assertEquals(sig.getSignedInfo(), defSi);

	sig = fac.newXMLSignature(defSi, defKi);
	assertNull(sig.getId());
	assertEquals(sig.getKeyInfo(), defKi);
	assertTrue(sig.getObjects().size()==0);
	assertNull(sig.getSignatureValue().getValue());
	assertNull(sig.getSignatureValue().getId());
	assertEquals(sig.getSignedInfo(), defSi);
    }
    
    public void testisFeatureSupported() throws Exception {

	XMLSignature sig = fac.newXMLSignature(defSi, null);
	
	try {
	    sig.isFeatureSupported(null); 
	    fail("Should raise a NPE for null feature"); 
	} catch (NullPointerException npe) {}
	    
	assertTrue(!sig.isFeatureSupported("not supported"));
    }

    public void testsignANDvalidate() throws Exception {
	XMLSignature sig;
	SignedInfo si;
	KeyInfo ki = null;
	XMLSignContext signContext;
	XMLValidateContext validateContext;
	boolean status = true;
	for (int i = SIGN_KEYS.length-1; i>=0 ; i--) {
	    si = createSignedInfo(SIG_METHODS[i]);
	    if (VALIDATE_KEYS[i] instanceof PublicKey) {
		ki = kifac.newKeyInfo(Collections.singletonList
		    (kifac.newKeyValue((PublicKey) VALIDATE_KEYS[i])));
	    } else {
		ki = kifac.newKeyInfo(Collections.singletonList
		    (kifac.newKeyName("testuser")));
	    }
	    sig = fac.newXMLSignature(si, ki, objs, id, sigValueId); 
	    Document doc = TestUtils.newDocument();
	    signContext = new DOMSignContext(SIGN_KEYS[i], doc);
	    sig.sign(signContext);
	    validateContext = new DOMValidateContext
		(VALIDATE_KEYS[i], doc.getDocumentElement());
	    if (sig.validate(validateContext) == false) {
		status = false;
		TestUtils.dumpDocument(doc, "signatureTest_out"+i+".xml");
	    }
	}
	assertTrue(status);
    }

    public void testsignWithProvider() throws Exception {
	XMLSignature sig;
	SignedInfo si;
	KeyInfo ki = null;
	XMLSignContext signContext;
	Provider p = new TestProvider();
	for (int i = SIGN_KEYS.length-2; i>=0 ; i--) {
	    si = createSignedInfo(SIG_METHODS[i]);
	    if (VALIDATE_KEYS[i] instanceof PublicKey) {
		ki = kifac.newKeyInfo(Collections.singletonList
		    (kifac.newKeyValue((PublicKey) VALIDATE_KEYS[i])));
	    } else {
		ki = kifac.newKeyInfo(Collections.singletonList
		    (kifac.newKeyName("testuser")));
	    }
	    sig = fac.newXMLSignature(si, ki, objs, id, sigValueId); 
	    Document doc = TestUtils.newDocument();
	    signContext = new DOMSignContext(SIGN_KEYS[i], doc);
	    signContext.setProperty
		("org.jcp.xml.dsig.internal.dom.SignatureProvider", p);
	    try {
	        sig.sign(signContext);
	        fail("Should have failed because TestProvider does not " +
		     "support " + SIGN_KEYS[i].getAlgorithm());
	    } catch (Exception e) {
		assertTrue(e.getMessage(), 
		    e.getCause() instanceof NoSuchAlgorithmException);
	    }
	}
    }

    public void testSignWithEmptyNSPrefix() throws Exception {
	SignedInfo si = createSignedInfo(SIG_METHODS[1]);
	KeyInfo	ki = kifac.newKeyInfo(Collections.singletonList
		    (kifac.newKeyValue((PublicKey) VALIDATE_KEYS[1])));
	XMLSignature sig = fac.newXMLSignature(si, ki, objs, id, sigValueId); 
	Document doc = TestUtils.newDocument();
	XMLSignContext signContext = new DOMSignContext(SIGN_KEYS[1], doc);
	signContext.putNamespacePrefix(XMLSignature.XMLNS, "");
        sig.sign(signContext);
/*
	StringWriter sw = new StringWriter();
	dumpDocument(doc, sw);
	System.out.println(sw);
*/
    }

    private SignedInfo createSignedInfo(SignatureMethod sm) throws Exception {
	// set up the building blocks
	CanonicalizationMethod cm = fac.newCanonicalizationMethod
	    (CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS, 
	     (C14NMethodParameterSpec) null);
	DigestMethod dm = fac.newDigestMethod(DigestMethod.SHA1, null);
	List refs = Collections.singletonList(fac.newReference
	    ("http://www.w3.org/Signature/2002/04/xml-stylesheet.b64", dm));
        return fac.newSignedInfo(cm, sm, refs);
    }

    static class TestProvider extends Provider {
	TestProvider() {
	    super("TestProvider", 0, "TestProvider");
	}
    }

    private void dumpDocument(Document doc, Writer w) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();
//      trans.setOutputProperty(OutputKeys.INDENT, "yes");
        trans.transform(new DOMSource(doc), new StreamResult(w));
    }
}

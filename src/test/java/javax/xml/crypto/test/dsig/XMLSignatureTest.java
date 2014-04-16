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
package javax.xml.crypto.test.dsig;

import java.io.*;
import java.util.*;
import java.security.*;

import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.keyinfo.*;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.crypto.spec.SecretKeySpec;

import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.*;

/**
 * Unit test for javax.xml.crypto.dsig.XMLSignature
 *
 * @version $Id$
 * @author Valerie Peng
 */
public class XMLSignatureTest extends org.junit.Assert {
    private XMLSignatureFactory fac;
    private KeyInfoFactory kifac;
    private SignedInfo defSi;
    private KeyInfo defKi;
    private List<XMLObject> objs;
    private String id = "id";
    private String sigValueId = "signatureValueId";
    private Key[] SIGN_KEYS;
    private Key[] VALIDATE_KEYS;
    private SignatureMethod[] SIG_METHODS;
    private URIDereferencer ud;

    static {
        Security.insertProviderAt
            (new org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI(), 1);
    }

    public XMLSignatureTest() throws Exception {
        fac = XMLSignatureFactory.getInstance
            ("DOM", new org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI());
        kifac = KeyInfoFactory.getInstance
            ("DOM", new org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI());

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
        ud = new LocalHttpCacheURIDereferencer();
    }

    @org.junit.Test
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
    
    @org.junit.Test
    public void testisFeatureSupported() throws Exception {

        XMLSignature sig = fac.newXMLSignature(defSi, null);
        
        try {
            sig.isFeatureSupported(null); 
            fail("Should raise a NPE for null feature"); 
        } catch (NullPointerException npe) {}
            
        assertTrue(!sig.isFeatureSupported("not supported"));
    }

    @org.junit.Test
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
            signContext.setURIDereferencer(ud);
            sig.sign(signContext);
            TestUtils.validateSecurityOrEncryptionElement(doc.getDocumentElement());
            validateContext = new DOMValidateContext
                (VALIDATE_KEYS[i], doc.getDocumentElement());
            validateContext.setURIDereferencer(ud);
            if (sig.validate(validateContext) == false) {
                status = false;
                TestUtils.dumpDocument(doc, "signatureTest_out"+i+".xml");
            }
        }
        assertTrue(status);
    }

    @org.junit.Test
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
            signContext.setURIDereferencer(ud);
            try {
                sig.sign(signContext);
                // note - don't bother validating the returned XML here, because there shouldn't be any.
                fail("Should have failed because TestProvider does not " +
                     "support " + SIGN_KEYS[i].getAlgorithm());
            } catch (Exception e) {
                assertTrue(e.getMessage(), 
                    e.getCause() instanceof NoSuchAlgorithmException);
            }
        }
    }

    @org.junit.Test
    public void testSignWithEmptyNSPrefix() throws Exception {
        SignedInfo si = createSignedInfo(SIG_METHODS[1]);
        KeyInfo	ki = kifac.newKeyInfo(Collections.singletonList
                    (kifac.newKeyValue((PublicKey) VALIDATE_KEYS[1])));
        XMLSignature sig = fac.newXMLSignature(si, ki, objs, id, sigValueId); 
        Document doc = TestUtils.newDocument();
        XMLSignContext signContext = new DOMSignContext(SIGN_KEYS[1], doc);
        signContext.putNamespacePrefix(XMLSignature.XMLNS, "");
        signContext.setURIDereferencer(ud);
        sig.sign(signContext);
        TestUtils.validateSecurityOrEncryptionElement(doc.getDocumentElement());
/*
        StringWriter sw = new StringWriter();
        dumpDocument(doc, sw);
        System.out.println(sw);
*/
    }

    @org.junit.Test
    public void testSignWithReferenceManifestDependencies() throws Exception {
        // create references
        DigestMethod dm = fac.newDigestMethod(DigestMethod.SHA1, null);
        List<Reference> refs = Collections.singletonList(fac.newReference("#object-1", dm));

        // create SignedInfo
        CanonicalizationMethod cm = fac.newCanonicalizationMethod
            (CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null);
        SignedInfo si = fac.newSignedInfo(cm, SIG_METHODS[1], refs);

        // create objects
        List<XMLObject> objs = new ArrayList<XMLObject>();

        // Object 1
        List<Reference> manRefs = Collections.singletonList
            (fac.newReference("#object-2", dm));
        objs.add(fac.newXMLObject(Collections.singletonList
            (fac.newManifest(manRefs, "manifest-1")), "object-1", null, null));

        // Object 2
        Document doc = TestUtils.newDocument();
        Element nc = doc.createElementNS(null, "NonCommentandus");
        nc.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "");
        nc.appendChild(doc.createComment(" Commentandum "));
        objs.add(fac.newXMLObject(Collections.singletonList
            (new DOMStructure(nc)), "object-2", null, null));

        KeyInfo	ki = kifac.newKeyInfo(Collections.singletonList
                    (kifac.newKeyValue((PublicKey) VALIDATE_KEYS[1])));

        // create XMLSignature
        XMLSignature sig = fac.newXMLSignature(si, ki, objs, "signature", null);
        DOMSignContext dsc = new DOMSignContext(SIGN_KEYS[1], doc);

        sig.sign(dsc);
        TestUtils.validateSecurityOrEncryptionElement(doc.getDocumentElement());

/*
        StringWriter sw = new StringWriter();
        dumpDocument(doc, sw);
        System.out.println(sw);
*/

        DOMValidateContext dvc = new DOMValidateContext
            (VALIDATE_KEYS[1], doc.getDocumentElement());
        XMLSignature sig2 = fac.unmarshalXMLSignature(dvc);

        if (sig.equals(sig2) == false) {
            throw new Exception
                ("Unmarshalled signature is not equal to generated signature");
        }
        if (sig2.validate(dvc) == false) {
            throw new Exception("Validation of generated signature failed");
        }
    }

    @org.junit.Test
    public void testSignTemplateWithObjectNSDefs() throws Exception {
        String base = System.getProperty("basedir") == null ? "./"
                      : System.getProperty("basedir");
 
        File f = new File(base + "/src/test/resources/javax/xml/crypto/dsig/" +
            "signature-enveloping-rsa-template.xml");

        Document doc = XMLUtils.createDocumentBuilder(false).parse(new FileInputStream(f));

        // Find Signature element
        NodeList nl =
            doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
        if (nl.getLength() == 0) {
            throw new Exception("Cannot find Signature element");
        }
        DOMStructure domSignature = new DOMStructure(nl.item(0));
        // unmarshal the XMLSignature
        XMLSignature signature = fac.unmarshalXMLSignature(domSignature);

        // create copy of Signature
        XMLSignature newSignature = fac.newXMLSignature
            (signature.getSignedInfo(), null, signature.getObjects(),
             signature.getId(), signature.getSignatureValue().getId());

        // Sign the template
        Node parent = domSignature.getNode().getParentNode();
        DOMSignContext signContext = new DOMSignContext(SIGN_KEYS[0], parent);
        // remove the signature node (since it will get recreated)
        parent.removeChild(domSignature.getNode());
        newSignature.sign(signContext);
        TestUtils.validateSecurityOrEncryptionElement(parent.getLastChild());

        // check that Object element retained namespace definitions
        Element objElem = (Element)parent.getFirstChild().getLastChild();
        Attr a = objElem.getAttributeNode("xmlns:test");
        if (!a.getValue().equals("http://www.example.org/ns"))
            throw new Exception("Object namespace definition not retained");
    }

    @org.junit.Test
    public void testCreateSignatureWithEmptyId() throws Exception {
        // create references
        DigestMethod dm = fac.newDigestMethod(DigestMethod.SHA1, null);
        List<Reference> refs = Collections.singletonList
            (fac.newReference("#", dm));

        // create SignedInfo
        CanonicalizationMethod cm = fac.newCanonicalizationMethod
            (CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null);
        SignedInfo si = fac.newSignedInfo(cm, SIG_METHODS[1], refs);

        // create object with empty id
        Document doc = TestUtils.newDocument();
        XMLObject obj = fac.newXMLObject(Collections.singletonList
            (new DOMStructure(doc.createTextNode("I am the text."))),
            "", "text/plain", null);

        KeyInfo	ki = kifac.newKeyInfo(Collections.singletonList
                    (kifac.newKeyValue((PublicKey) VALIDATE_KEYS[1])));

        // create XMLSignature
        XMLSignature sig = fac.newXMLSignature(si, ki,
                                               Collections.singletonList(obj),
                                               "signature", null);
        DOMSignContext dsc = new DOMSignContext(SIGN_KEYS[1], doc);
        sig.sign(dsc);
    }

    private SignedInfo createSignedInfo(SignatureMethod sm) throws Exception {
        // set up the building blocks
        CanonicalizationMethod cm = fac.newCanonicalizationMethod
            (CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS, 
             (C14NMethodParameterSpec) null);
        DigestMethod dm = fac.newDigestMethod(DigestMethod.SHA1, null);
        List<Reference> refs = Collections.singletonList(fac.newReference
            ("http://www.w3.org/Signature/2002/04/xml-stylesheet.b64", dm));
        return fac.newSignedInfo(cm, sm, refs);
    }

    static class TestProvider extends Provider {
        private static final long serialVersionUID = 1L;

        TestProvider() {
            super("TestProvider", 0, "TestProvider");
        }
    }
}

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
package javax.xml.crypto.test.dsig;

import java.lang.reflect.Constructor;
import java.security.Key;
import java.security.Provider;
import java.security.Security;
import java.util.Collections;

import javax.xml.crypto.KeySelector;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.test.KeySelectors;
import javax.xml.parsers.DocumentBuilder;

import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * Test signing using all available digest methods
 */
public class SignatureDigestMethodTest extends org.junit.Assert {

    private KeySelector kvks;
    private CanonicalizationMethod withoutComments;
    private DigestMethod sha1, sha256, sha384, sha512, ripemd160;
    private SignatureMethod rsaSha1;
    private KeyInfo rsaki;
    private XMLSignatureFactory fac;
    private DocumentBuilder db;

    static {
        Security.insertProviderAt
            (new org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI(), 1);
    }

    public SignatureDigestMethodTest() throws Exception {
        //
        // If the BouncyCastle provider is not installed, then try to load it 
        // via reflection.
        //
        if (Security.getProvider("BC") == null) {
            Constructor<?> cons = null;
            try {
                Class<?> c = Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider");
                cons = c.getConstructor(new Class[] {});
            } catch (Exception e) {
                //ignore
            }
            if (cons == null) {
                // BouncyCastle is not available so just return
                return;
            } else {
                Provider provider = (java.security.Provider)cons.newInstance();
                Security.insertProviderAt(provider, 2);
            }
        }
        
        db = XMLUtils.createDocumentBuilder(false);
        // create common objects
        fac = XMLSignatureFactory.getInstance();
        KeyInfoFactory kifac = fac.getKeyInfoFactory();
        withoutComments = fac.newCanonicalizationMethod
            (CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null);
        sha1 = fac.newDigestMethod(DigestMethod.SHA1, null);
        sha256 = fac.newDigestMethod(DigestMethod.SHA256, null);
        sha384 = fac.newDigestMethod("http://www.w3.org/2001/04/xmldsig-more#sha384", null);
        sha512 = fac.newDigestMethod(DigestMethod.SHA512, null);
        ripemd160 = fac.newDigestMethod(DigestMethod.RIPEMD160, null);
        
        rsaSha1 = fac.newSignatureMethod
            ("http://www.w3.org/2000/09/xmldsig#rsa-sha1", null);
        
        rsaki = kifac.newKeyInfo(Collections.singletonList
                                 (kifac.newKeyValue(
                                  TestUtils.getPublicKey("RSA"))));
        
        kvks = new KeySelectors.KeyValueKeySelector();
    }

    @org.junit.Test
    public void testSHA1() throws Exception {
        test_create_signature_enveloping(rsaSha1, sha1, rsaki,
                                         TestUtils.getPrivateKey("RSA"), kvks);
    }
    
    @org.junit.Test
    public void testSHA256() throws Exception {
        test_create_signature_enveloping(rsaSha1, sha256, rsaki,
                                         TestUtils.getPrivateKey("RSA"), kvks);
    }
    
    
    @org.junit.Test
    public void testSHA384() throws Exception {
        test_create_signature_enveloping(rsaSha1, sha384, rsaki,
                                         TestUtils.getPrivateKey("RSA"), kvks);
    }
    
    @org.junit.Test
    public void testSHA512() throws Exception {
        test_create_signature_enveloping(rsaSha1, sha512, rsaki,
                                         TestUtils.getPrivateKey("RSA"), kvks);
    }
    
    @org.junit.Test
    public void testRIPEMD160() throws Exception {
        test_create_signature_enveloping(rsaSha1, ripemd160, rsaki,
                                         TestUtils.getPrivateKey("RSA"), kvks);
    }
    
    private void test_create_signature_enveloping(
        SignatureMethod sm, DigestMethod dm, KeyInfo ki, Key signingKey, KeySelector ks
    ) throws Exception {

        // create reference
        Reference ref = fac.newReference("#DSig.Object_1", dm, null,
                                         XMLObject.TYPE, null);

        // create SignedInfo
        SignedInfo si = fac.newSignedInfo(withoutComments, sm,
                                          Collections.singletonList(ref));

        Document doc = db.newDocument();
        // create Objects
        Element webElem = doc.createElementNS(null, "Web");
        Text text = doc.createTextNode("up up and away");
        webElem.appendChild(text);
        XMLObject obj = fac.newXMLObject(Collections.singletonList
                                         (new DOMStructure(webElem)), "DSig.Object_1", "text/xml", null);

        // create XMLSignature
        XMLSignature sig = fac.newXMLSignature
        (si, ki, Collections.singletonList(obj), null, null);

        DOMSignContext dsc = new DOMSignContext(signingKey, doc);
        dsc.setDefaultNamespacePrefix("dsig");

        sig.sign(dsc);
        TestUtils.validateSecurityOrEncryptionElement(doc.getDocumentElement());
        
        // XMLUtils.outputDOM(doc.getDocumentElement(), System.out);

        DOMValidateContext dvc = new DOMValidateContext
        (ks, doc.getDocumentElement());
        XMLSignature sig2 = fac.unmarshalXMLSignature(dvc);

        assertTrue(sig.equals(sig2));
        assertTrue(sig2.validate(dvc));
    }

}

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

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Collections;

import javax.xml.crypto.KeySelector;
import javax.xml.crypto.dom.*;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.*;
import javax.xml.crypto.dsig.keyinfo.*;
import javax.xml.crypto.dsig.spec.*;
import javax.xml.parsers.DocumentBuilder;

import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.*;

import javax.xml.crypto.test.KeySelectors;

/**
 * This is a testcase to generate all the W3C xmldsig XMLDSig11 testcases.
 *
 * @author Sean Mullan
 */
public class CreateInteropXMLDSig11Test extends org.junit.Assert {

    private KeySelector kvks, sks;
    private CanonicalizationMethod withoutComments;
    private DigestMethod sha1, sha256, sha384, sha512;
    private SignatureMethod ecdsaSha1, ecdsaSha256, ecdsaSha384, ecdsaSha512,
                            rsaSha256, rsaSha384, rsaSha512, hmacSha256,
                            hmacSha384, hmacSha512;
    private KeyInfo p256ki, p384ki, p521ki, rsaki, rsa2048ki;
    private XMLSignatureFactory fac;
    private DocumentBuilder db;
    private KeyPair p256, p384, p521, rsa2048;
    private boolean ecSupport = true;

    static {
        Security.insertProviderAt
            (new org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI(), 1);
    }

    public CreateInteropXMLDSig11Test() throws Exception {
        // Create KeyPairs
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
            kpg.initialize(new ECGenParameterSpec("1.2.840.10045.3.1.7"));
            p256 = kpg.generateKeyPair();
            kpg.initialize(new ECGenParameterSpec("1.3.132.0.34"));
            p384 = kpg.generateKeyPair();
            kpg.initialize(new ECGenParameterSpec("1.3.132.0.35"));
            p521 = kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException nsae) {
            // EC not supported on this platform
            ecSupport = false;
        }
        KeyPairGenerator rsakpg = KeyPairGenerator.getInstance("RSA");
        rsakpg.initialize(2048);
        rsa2048 = rsakpg.generateKeyPair();

        db = XMLUtils.createDocumentBuilder(false);
        // create common objects
        fac = XMLSignatureFactory.getInstance();
        KeyInfoFactory kifac = fac.getKeyInfoFactory();
        withoutComments = fac.newCanonicalizationMethod
            (CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null);
        sha1 = fac.newDigestMethod(DigestMethod.SHA1, null);
        sha256 = fac.newDigestMethod(DigestMethod.SHA256, null);
        sha384 = fac.newDigestMethod(
                     "http://www.w3.org/2001/04/xmldsig-more#sha384", null);
        sha512 = fac.newDigestMethod(DigestMethod.SHA512, null);
        if (ecSupport) {
            p256ki = kifac.newKeyInfo(Collections.singletonList(
                                      kifac.newKeyValue(p256.getPublic())));
            p384ki = kifac.newKeyInfo(Collections.singletonList(
                                      kifac.newKeyValue(p384.getPublic())));
            p521ki = kifac.newKeyInfo(Collections.singletonList(
                                      kifac.newKeyValue(p521.getPublic())));
            ecdsaSha1 = fac.newSignatureMethod
                ("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1", null);
            ecdsaSha256 = fac.newSignatureMethod
                ("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256", null);
            ecdsaSha384 = fac.newSignatureMethod
                ("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384", null);
            ecdsaSha512 = fac.newSignatureMethod
                ("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512", null);
        }
        rsaki = kifac.newKeyInfo(Collections.singletonList
                                 (kifac.newKeyValue(
                                  TestUtils.getPublicKey("RSA"))));
        rsa2048ki = kifac.newKeyInfo(Collections.singletonList
                                     (kifac.newKeyValue(rsa2048.getPublic())));
        rsaSha256 = fac.newSignatureMethod
            ("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", null);
        rsaSha384 = fac.newSignatureMethod
            ("http://www.w3.org/2001/04/xmldsig-more#rsa-sha384", null);
        rsaSha512 = fac.newSignatureMethod
            ("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512", null);
        hmacSha256 = fac.newSignatureMethod
            ("http://www.w3.org/2001/04/xmldsig-more#hmac-sha256", null);
        hmacSha384 = fac.newSignatureMethod
            ("http://www.w3.org/2001/04/xmldsig-more#hmac-sha384", null);
        hmacSha512 = fac.newSignatureMethod
            ("http://www.w3.org/2001/04/xmldsig-more#hmac-sha512", null);
        kvks = new KeySelectors.KeyValueKeySelector();
        sks = new KeySelectors.SecretKeySelector("testkey".getBytes("ASCII"));
    }

    @org.junit.Test
    public void test_create_enveloping_p256_sha1() throws Exception {
        if (ecSupport) {
            test_create_signature_enveloping(ecdsaSha1, sha1, p256ki,
                                             p256.getPrivate(), kvks);
        }
    }

    @org.junit.Test
    public void test_create_enveloping_p256_sha256() throws Exception {
        if (ecSupport) {
            test_create_signature_enveloping(ecdsaSha256, sha256, p256ki,
                                             p256.getPrivate(), kvks);
        }
    }

    @org.junit.Test
    public void test_create_enveloping_p256_sha384() throws Exception {
        if (ecSupport) {
            test_create_signature_enveloping(ecdsaSha384, sha384, p256ki,
                                             p256.getPrivate(), kvks);
        }
    }

    @org.junit.Test
    public void test_create_enveloping_p256_sha512() throws Exception {
        if (ecSupport) {
            test_create_signature_enveloping(ecdsaSha512, sha512, p256ki,
                                             p256.getPrivate(), kvks);
        }
    }

    @org.junit.Test
    public void test_create_enveloping_p384_sha1() throws Exception {
        if (ecSupport) {
            test_create_signature_enveloping(ecdsaSha1, sha1, p384ki,
                                             p384.getPrivate(), kvks);
        }
    }

    @org.junit.Test
    public void test_create_enveloping_p384_sha256() throws Exception {
        if (ecSupport) {
            test_create_signature_enveloping(ecdsaSha256, sha256, p384ki,
                                             p384.getPrivate(), kvks);
        }
    }

    @org.junit.Test
    public void test_create_enveloping_p384_sha384() throws Exception {
        if (ecSupport) {
            test_create_signature_enveloping(ecdsaSha384, sha384, p384ki,
                                             p384.getPrivate(), kvks);
        }
    }

    @org.junit.Test
    public void test_create_enveloping_p384_sha512() throws Exception {
        if (ecSupport) {
            test_create_signature_enveloping(ecdsaSha512, sha512, p384ki,
                                             p384.getPrivate(), kvks);
        }
    }

    @org.junit.Test
    public void test_create_enveloping_p521_sha1() throws Exception {
        if (ecSupport) {
            test_create_signature_enveloping(ecdsaSha1, sha1, p521ki,
                                             p521.getPrivate(), kvks);
        }
    }

    @org.junit.Test
    public void test_create_enveloping_p521_sha256() throws Exception {
        if (ecSupport) {
            test_create_signature_enveloping(ecdsaSha256, sha256, p521ki,
                                             p521.getPrivate(), kvks);
        }
    }

    @org.junit.Test
    public void test_create_enveloping_p521_sha384() throws Exception {
        if (ecSupport) {
            test_create_signature_enveloping(ecdsaSha384, sha384, p521ki,
                                             p521.getPrivate(), kvks);
        }
    }

    @org.junit.Test
    public void test_create_enveloping_p521_sha512() throws Exception {
        if (ecSupport) {
            test_create_signature_enveloping(ecdsaSha512, sha512, p521ki,
                                             p521.getPrivate(), kvks);
        }
    }

    @org.junit.Test
    public void test_create_enveloping_rsa_sha256() throws Exception {
        test_create_signature_enveloping(rsaSha256, sha1, rsaki,
                                         TestUtils.getPrivateKey("RSA"), kvks);
    }

    @org.junit.Test
    public void test_create_enveloping_rsa_sha384() throws Exception {
        test_create_signature_enveloping(rsaSha384, sha1, rsa2048ki,
                                         rsa2048.getPrivate(), kvks);
    }

    @org.junit.Test
    public void test_create_enveloping_rsa_sha512() throws Exception {
        test_create_signature_enveloping(rsaSha512, sha1, rsa2048ki,
                                         rsa2048.getPrivate(), kvks);
    }

    @org.junit.Test
    public void test_create_enveloping_sha256_rsa_sha256() throws Exception {
        test_create_signature_enveloping(rsaSha256, sha256, rsaki,
                                         TestUtils.getPrivateKey("RSA"), kvks);
    }

    @org.junit.Test
    public void test_create_enveloping_sha384_rsa_sha256() throws Exception {
        test_create_signature_enveloping(rsaSha256, sha384, rsaki,
                                         TestUtils.getPrivateKey("RSA"), kvks);
    }

    @org.junit.Test
    public void test_create_enveloping_sha512_rsa_sha256() throws Exception {
        test_create_signature_enveloping(rsaSha256, sha512, rsaki,
                                         TestUtils.getPrivateKey("RSA"), kvks);
    }

    @org.junit.Test
    public void test_create_enveloping_hmac_sha256() throws Exception {
        test_create_signature_enveloping(hmacSha256, sha1, rsaki,
                                         TestUtils.getSecretKey
                                         ("testkey".getBytes("ASCII")), sks);
    }

    @org.junit.Test
    public void test_create_enveloping_hmac_sha384() throws Exception {
        test_create_signature_enveloping(hmacSha384, sha1, rsaki,
                                         TestUtils.getSecretKey
                                         ("testkey".getBytes("ASCII")), sks);
    }

    @org.junit.Test
    public void test_create_enveloping_hmac_sha512() throws Exception {
        test_create_signature_enveloping(hmacSha512, sha1, rsaki,
                                         TestUtils.getSecretKey
                                         ("testkey".getBytes("ASCII")), sks);
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

        DOMValidateContext dvc = new DOMValidateContext
        (ks, doc.getDocumentElement());
        XMLSignature sig2 = fac.unmarshalXMLSignature(dvc);

        assertTrue(sig.equals(sig2));
        assertTrue(sig2.validate(dvc));
    }

}

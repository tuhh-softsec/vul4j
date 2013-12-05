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
package org.apache.xml.security.test.dom.signature;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.X509Certificate;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.xml.security.algorithms.SignatureAlgorithm;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.test.dom.DSNamespaceContext;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Element;

/**
 * Tests that creates and verifies ECDSA signatures.
 *
 * @author Wolfgang Glas
 */
public class ECDSASignatureTest extends org.junit.Assert {

    private static final String BASEDIR = System.getProperty("basedir");
    private static final String SEP = System.getProperty("file.separator");
    private static final String ECDSA_JKS = 
        "src/test/resources/org/apache/xml/security/samples/input/ecdsa.jks";
    private static final String ECDSA_JKS_PASSWORD = "security";
    
    private KeyStore keyStore;
    
    private javax.xml.parsers.DocumentBuilder db;

    public ECDSASignatureTest() throws Exception {
        //
        // If the BouncyCastle provider is not installed, then try to load it 
        // via reflection. If it is not available, then skip this test as it is
        // required for elliptic curves
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
                Security.insertProviderAt(provider, 1);
            }
        }

        //String id = "http://apache.org/xml/properties/dom/document-class-name";
        //dbf.setAttribute(id, IndexedDocument.class.getName());
        
        db = XMLUtils.createDocumentBuilder(false);
        org.apache.xml.security.Init.init();
    }
    
    @org.junit.Test
    public void testOne() throws Exception {
        if (Security.getProvider("BC") == null) {
            return;
        }
        
        keyStore = KeyStore.getInstance("JKS");
        keyStore.load(
            new java.io.FileInputStream(ECDSA_JKS), 
            ECDSA_JKS_PASSWORD.toCharArray()
        );
        
        doVerify(doSign()); 
        doVerify(doSign());
    }

    // Failing with more recent BouncyCastle libraries
    @org.junit.Test
    @org.junit.Ignore
    public void testTwo() throws Exception {
        if (Security.getProvider("BC") == null) {
            return;
        }
        
        File file = 
            makeDataFile("src/test/resources/org/apache/xml/security/samples/input/ecdsaSignature.xml");
        InputStream is = new FileInputStream(file);
        
        doVerify(is);
    }

    @org.junit.Test
    @org.junit.Ignore
    public void testThree()  throws Exception {
        if (Security.getProvider("BC") == null) {
            return;
        }

        File file = makeDataFile("src/test/resources/at/buergerkarte/testresp.xml");
        InputStream is = new FileInputStream(file);
        
        doVerify(is);
    }
    
    private byte[] doSign() throws Exception {
        PrivateKey privateKey = 
            (PrivateKey)keyStore.getKey("ECDSA", ECDSA_JKS_PASSWORD.toCharArray());
        org.w3c.dom.Document doc = db.newDocument();
        doc.appendChild(doc.createComment(" Comment before "));
        Element root = doc.createElementNS("", "RootElement");

        doc.appendChild(root);
        root.appendChild(doc.createTextNode("Some simple text\n"));

        Element canonElem = 
            XMLUtils.createElementInSignatureSpace(doc, Constants._TAG_CANONICALIZATIONMETHOD);
        canonElem.setAttributeNS(
            null, Constants._ATT_ALGORITHM, Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS
        );

        SignatureAlgorithm signatureAlgorithm = 
            new SignatureAlgorithm(doc, XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA1);
        XMLSignature sig = 
            new XMLSignature(doc, null, signatureAlgorithm.getElement(), canonElem);

        root.appendChild(sig.getElement());
        doc.appendChild(doc.createComment(" Comment after "));
        Transforms transforms = new Transforms(doc);
        transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
        transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);
        sig.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);

        X509Certificate x509 = (X509Certificate)keyStore.getCertificate("ECDSA");
        sig.addKeyInfo(x509);
        sig.sign(privateKey);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        XMLUtils.outputDOMc14nWithComments(doc, bos);
        return bos.toByteArray();
    }

    private void doVerify(byte[] signedXml) throws Exception {
        doVerify(new ByteArrayInputStream(signedXml));
    }
    
    private void doVerify(InputStream is) throws Exception {
        org.w3c.dom.Document doc = this.db.parse(is);
        
        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        xpath.setNamespaceContext(new DSNamespaceContext());

        String expression = "//ds:Signature[1]";
        Element sigElement = 
            (Element) xpath.evaluate(expression, doc, XPathConstants.NODE);
        XMLSignature signature = new XMLSignature(sigElement, "");

        signature.addResourceResolver(new XPointerResourceResolver(sigElement));

        KeyInfo ki = signature.getKeyInfo();
        if (ki == null) {
            throw new RuntimeException("No keyinfo");
        }
        X509Certificate cert = signature.getKeyInfo().getX509Certificate();

        if (cert == null) {
            throw new RuntimeException("No certificate");
        }
        assertTrue(signature.checkSignatureValue(cert) );
    }
    
    private File makeDataFile(String relPath) {
        if (BASEDIR != null && !"".equals(BASEDIR)) {
            return new File(BASEDIR + SEP + relPath);
        } else {
            return new File(relPath);
        }
    }
     
    /**
     * DO NOT DELETE THIS COMMENTED OUT METHOD!
     * 
     * The reason this method is commented out is to avoid introducing explicit 
     * BouncyCastle dependencies.
     * 
     * Create an X.509 Certificate and associated private key using the Elliptic Curve
     * DSA algorithm, and store in a KeyStore. This method was used to generate the 
     * keystore used for this test 
     * ("src/test/resources/org/apache/xml/security/samples/input/ecdsa.jks").
    private static void setUpKeyAndCertificate() throws Exception {
        java.security.spec.ECGenParameterSpec ecGenParameterSpec = 
            new java.security.spec.ECGenParameterSpec("B-409");
        
        java.security.KeyPairGenerator kpg = 
            java.security.KeyPairGenerator.getInstance("ECDH");

        kpg.initialize(ecGenParameterSpec, new java.security.SecureRandom());

        java.security.KeyPair kp = kpg.generateKeyPair();
        
        org.bouncycastle.x509.X509V3CertificateGenerator certGen = 
            new org.bouncycastle.x509.X509V3CertificateGenerator();

        long now = System.currentTimeMillis();
        certGen.setSerialNumber(java.math.BigInteger.valueOf(now));

        org.bouncycastle.jce.X509Principal subject = 
            new org.bouncycastle.jce.X509Principal(
                "CN=XML ECDSA Signature Test,DC=apache,DC=org"
            );
        certGen.setIssuerDN(subject);
        certGen.setSubjectDN(subject);

        java.util.Date from_date = new java.util.Date(now);
        certGen.setNotBefore(from_date);
        java.util.Calendar cal = new java.util.GregorianCalendar();
        cal.setTime(from_date);
        cal.add(java.util.Calendar.YEAR, 4);
        java.util.Date to_date = cal.getTime();
        certGen.setNotAfter(to_date);

        certGen.setPublicKey(kp.getPublic());
        certGen.setSignatureAlgorithm("SHA1withECDSA");
        certGen.addExtension(
            org.bouncycastle.asn1.x509.X509Extensions.BasicConstraints, 
            true, 
            new org.bouncycastle.asn1.x509.BasicConstraints(false)
        );
        certGen.addExtension(
            org.bouncycastle.asn1.x509.X509Extensions.KeyUsage, 
            true, 
            new org.bouncycastle.asn1.x509.KeyUsage(
                org.bouncycastle.asn1.x509.KeyUsage.digitalSignature | 
                org.bouncycastle.asn1.x509.KeyUsage.keyEncipherment | 
                org.bouncycastle.asn1.x509.KeyUsage.keyCertSign | 
                org.bouncycastle.asn1.x509.KeyUsage.cRLSign
            )
        );

        X509Certificate x509 = certGen.generateX509Certificate(kp.getPrivate());

        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(null, ECDSA_JKS_PASSWORD.toCharArray());
        keyStore.setKeyEntry(
            "ECDSA", kp.getPrivate(), 
            ECDSA_JKS_PASSWORD.toCharArray(), new java.security.cert.Certificate[]{x509}
        );
        keyStore.store(
            new java.io.FileOutputStream(ECDSA_JKS), ECDSA_JKS_PASSWORD.toCharArray()
        );

    }
    */

}

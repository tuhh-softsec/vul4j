/*
 * Copyright  1999-2009 The Apache Software Foundation.
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

package org.apache.xml.security.test.signature;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.xml.security.algorithms.SignatureAlgorithm;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Element;

/**
 * Tests that creates and verifies ECDSA signatures.
 *
 * @author Wolfgang Glas
 */
public class ECDSASignatureTest extends TestCase {

    /** {@link org.apache.commons.logging} logging facility */
    private static org.apache.commons.logging.Log log =
        org.apache.commons.logging.LogFactory.getLog(ECDSASignatureTest.class.getName());

    private static final String BASEDIR = System.getProperty("basedir");
    private static final String SEP = System.getProperty("file.separator");
    private static final String ECDSA_JKS = 
        "data/org/apache/xml/security/samples/input/ecdsa.jks";
    private static final String ECDSA_JKS_PASSWORD = "security";
    
    private KeyStore keyStore;
    
    private javax.xml.parsers.DocumentBuilder db;

    private File makeDataFile(String relPath) {
        if (BASEDIR != null && !"".equals(BASEDIR)) {
            return new File(BASEDIR + SEP + relPath);
        } else {
            return new File(relPath);
        }
    }

    public static Test suite() {
        return new TestSuite(ECDSASignatureTest.class);
    }

    public ECDSASignatureTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] testCaseName = {"-noloading", ECDSASignatureTest.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }
    
    protected void setUp() throws Exception {
        //
        // If the BouncyCastle provider is not installed, then try to load it 
        // via reflection. If it is not available, then skip this test as it is
        // required for elliptic curves
        //
        if (Security.getProvider("BC") == null) {
            Constructor cons = null;
            try {
                Class c = Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider");
                cons = c.getConstructor(new Class[] {});
            } catch (Exception e) {
                //ignore
            }
            if (cons == null) {
                // BouncyCastle is not available so just return
                return;
            } else {
                Security.addProvider((java.security.Provider)cons.newInstance(new Object[]{}));
            }
        }

        javax.xml.parsers.DocumentBuilderFactory dbf = 
            javax.xml.parsers.DocumentBuilderFactory.newInstance();
    	dbf.setNamespaceAware(true);
        log.info("dbf.isIgnoringComments()=" + dbf.isIgnoringComments());
        log.info("dbf.isIgnoringElementContentWhitespace()=" + dbf.isIgnoringElementContentWhitespace());
        
        String id = "http://apache.org/xml/properties/dom/document-class-name";
       
        dbf.setAttribute(id, IndexedDocument.class.getName());
        
    	db = dbf.newDocumentBuilder();
    	org.apache.xml.security.Init.init();
    }
    
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

    public void testTwo() throws Exception {
        if (Security.getProvider("BC") == null) {
            return;
        }
        
        File file = 
            makeDataFile("data/org/apache/xml/security/samples/input/ecdsaSignature.xml");
        InputStream is = new FileInputStream(file);
        
        doVerify(is);
    }

    public void testThree()  throws Exception {
        if (Security.getProvider("BC") == null) {
            return;
        }
        
        File file = makeDataFile("data/at/buergerkarte/testresp.xml");
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

        Element canonElem = XMLUtils.createElementInSignatureSpace(doc,
                Constants._TAG_CANONICALIZATIONMETHOD);
        canonElem.setAttributeNS(null, Constants._ATT_ALGORITHM,
                Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);

        SignatureAlgorithm signatureAlgorithm = new SignatureAlgorithm(doc,
                XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA1);
        XMLSignature sig = new XMLSignature(doc, null, signatureAlgorithm
                .getElement(), canonElem);

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
        Element nscontext = XMLUtils.createDSctx(doc, "ds",Constants.SignatureSpecNS);
        Element sigElement = (Element) XPathAPI.selectSingleNode(doc,"//ds:Signature[1]", nscontext);
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
     
    /**
     * DO NOT DELETE THIS COMMENTED OUT METHOD!
     * 
     * The reason this method is commented out is to avoid introducing explicit 
     * BouncyCastle dependencies.
     * 
     * Create an X.509 Certificate and associated private key using the Elliptic Curve
     * DSA algorithm, and store in a KeyStore. This method was used to generate the 
     * keystore used for this test ("data/org/apache/xml/security/samples/input/ecdsa.jks").
     *
    private static void setUpKeyAndCertificate() throws Exception {
        
        java.security.KeyPairGenerator kpg = 
            java.security.KeyPairGenerator.getInstance("ECDSA");
         
        java.math.BigInteger mod_p = 
            new java.math.BigInteger("115792089237316195423570985008687907853269984665640564039457584007913129639319");
        java.math.BigInteger mod_q = 
            new java.math.BigInteger("115792089237316195423570985008687907853073762908499243225378155805079068850323");

        org.bouncycastle.math.ec.ECCurve.Fp curve = 
            new org.bouncycastle.math.ec.ECCurve.Fp(
                    mod_p, // p
                    new java.math.BigInteger("115792089237316195423570985008687907853269984665640564039457584007913129639316"), // a
                    new java.math.BigInteger("166")
            ); // b

        java.security.spec.AlgorithmParameterSpec spec = 
            new org.bouncycastle.jce.spec.ECParameterSpec(
                    curve,
                    new org.bouncycastle.math.ec.ECPoint.Fp(curve,
                            new org.bouncycastle.math.ec.ECFieldElement.Fp(
                                curve.getQ(), new java.math.BigInteger("1")
                            ), // x
                            new org.bouncycastle.math.ec.ECFieldElement.Fp(
                                curve.getQ(), new java.math.BigInteger("64033881142927202683649881450433473985931760268884941288852745803908878638612")
                            )
                    ), // y
                    mod_q
            );

        kpg.initialize(spec);

        java.security.KeyPair kp=kpg.genKeyPair();
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

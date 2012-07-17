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
package org.apache.xml.security.test.stax.signature;

import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.stax.config.Init;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityException;
import org.apache.xml.security.stax.impl.securityToken.KeyNameSecurityToken;
import org.apache.xml.security.stax.impl.securityToken.X509IssuerSerialSecurityToken;
import org.apache.xml.security.stax.impl.securityToken.X509SecurityToken;
import org.apache.xml.security.stax.impl.securityToken.X509SubjectNameSecurityToken;
import org.apache.xml.security.stax.securityEvent.*;
import org.apache.xml.security.test.dom.DSNamespaceContext;
import org.apache.xml.security.test.stax.utils.XMLSecEventAllocator;
import org.apache.xml.security.transforms.Transforms;
import org.junit.Assert;
import org.junit.Before;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.security.Key;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.UUID;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class AbstractSignatureVerificationTest extends org.junit.Assert {

    protected static String BASEDIR;

    protected XMLInputFactory xmlInputFactory;
    protected DocumentBuilderFactory documentBuilderFactory;
    protected TransformerFactory transformerFactory = TransformerFactory.newInstance();

    @Before
    public void setUp() throws Exception {

        BASEDIR = System.getProperty("basedir");
        if (BASEDIR == null) {
            BASEDIR = new File(".").getCanonicalPath();
        }

        Init.init(AbstractSignatureVerificationTest.class.getClassLoader().getResource("security-config.xml").toURI());
        org.apache.xml.security.Init.init();

        xmlInputFactory = XMLInputFactory.newInstance();
        xmlInputFactory.setEventAllocator(new XMLSecEventAllocator());

        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setIgnoringComments(false);
        documentBuilderFactory.setCoalescing(false);
        documentBuilderFactory.setIgnoringElementContentWhitespace(false);
    }

    /**
     * Sign the document using DOM
     */
    protected XMLSignature signUsingDOM(
            String algorithm,
            Document document,
            List<String> localNames,
            Key signingKey
    ) throws Exception {
        String c14nMethod = "http://www.w3.org/2001/10/xml-exc-c14n#";
        return signUsingDOM(algorithm, document, localNames, signingKey, c14nMethod, (List<ReferenceInfo>)null);
    }

    /**
     * Sign the document using DOM
     */
    protected XMLSignature signUsingDOM(
            String algorithm,
            Document document,
            List<String> localNames,
            String referenceC14Nmethod,
            Key signingKey
    ) throws Exception {
        String c14nMethod = "http://www.w3.org/2001/10/xml-exc-c14n#";
        return signUsingDOM(algorithm, document, localNames, signingKey, c14nMethod, (List<ReferenceInfo>)null);
    }


    /**
     * Sign the document using DOM
     */
    protected XMLSignature signUsingDOM(
            String algorithm,
            Document document,
            List<String> localNames,
            Key signingKey,
            List<ReferenceInfo> additionalReferences
    ) throws Exception {
        String c14nMethod = "http://www.w3.org/2001/10/xml-exc-c14n#";
        return signUsingDOM(algorithm, document, localNames, signingKey, c14nMethod, additionalReferences);
    }

    /**
     * Sign the document using DOM
     */
    protected XMLSignature signUsingDOM(
            String algorithm,
            Document document,
            List<String> localNames,
            Key signingKey,
            String c14nMethod
    ) throws Exception {
        String digestMethod = "http://www.w3.org/2000/09/xmldsig#sha1";
        return signUsingDOM(algorithm, document, localNames, signingKey, c14nMethod, digestMethod, null, c14nMethod);
    }

    /**
     * Sign the document using DOM
     */
    protected XMLSignature signUsingDOM(
            String algorithm,
            Document document,
            List<String> localNames,
            Key signingKey,
            String c14nMethod,
            List<ReferenceInfo> additionalReferences
    ) throws Exception {
        String digestMethod = "http://www.w3.org/2000/09/xmldsig#sha1";
        return signUsingDOM(algorithm, document, localNames, signingKey, c14nMethod, digestMethod, additionalReferences, c14nMethod);
    }

    /**
     * Sign the document using DOM
     */
    protected XMLSignature signUsingDOM(
            String algorithm,
            Document document,
            List<String> localNames,
            Key signingKey,
            String c14nMethod,
            String digestMethod
    ) throws Exception {
        return signUsingDOM(algorithm, document, localNames, signingKey, c14nMethod, digestMethod, null, c14nMethod);
    }

    /**
     * Sign the document using DOM
     */
    protected XMLSignature signUsingDOM(
            String algorithm,
            Document document,
            List<String> localNames,
            Key signingKey,
            String c14nMethod,
            String digestMethod,
            List<ReferenceInfo> additionalReferences,
            String referenceC14NMethod
    ) throws Exception {
        XMLSignature sig = new XMLSignature(document, "", algorithm, c14nMethod);
        Element root = document.getDocumentElement();
        root.appendChild(sig.getElement());

        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        xpath.setNamespaceContext(new DSNamespaceContext());

        for (String localName : localNames) {
            String expression = "//*[local-name()='" + localName + "']";
            Element elementToSign =
                    (Element) xpath.evaluate(expression, document, XPathConstants.NODE);
            Assert.assertNotNull(elementToSign);
            String id = UUID.randomUUID().toString();
            elementToSign.setAttributeNS(null, "Id", id);
            elementToSign.setIdAttributeNS(null, "Id", true);

            if (additionalReferences != null) {
                for (int i = 0; i < additionalReferences.size(); i++) {
                    ReferenceInfo referenceInfo = additionalReferences.get(i);
                    if (referenceInfo.isBinary()) {
                        sig.addDocument(referenceInfo.getResource(), null, referenceInfo.getDigestMethod());
                    } else {
                        Transforms transforms = new Transforms(document);
                        transforms.addTransform(referenceInfo.getC14NMethod());
                        sig.addDocument(referenceInfo.getResource(), transforms, referenceInfo.getDigestMethod());
                    }
                }
            }

            Transforms transforms = new Transforms(document);
            transforms.addTransform(referenceC14NMethod);
            sig.addDocument("#" + id, transforms, digestMethod);
        }

        sig.sign(signingKey);

        String expression = "//ds:Signature[1]";
        Element sigElement =
                (Element) xpath.evaluate(expression, document, XPathConstants.NODE);
        Assert.assertNotNull(sigElement);

        return sig;
    }

    protected void checkSecurityEvents(TestSecurityEventListener securityEventListener) {
        String c14nAlgorithm = "http://www.w3.org/2001/10/xml-exc-c14n#";
        String digestAlgorithm = "http://www.w3.org/2000/09/xmldsig#sha1";
        String signatureMethod = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
        checkSecurityEvents(securityEventListener, c14nAlgorithm, digestAlgorithm, signatureMethod);
    }

    protected void checkSecurityEvents(
            TestSecurityEventListener securityEventListener,
            String c14nAlgorithm,
            String digestAlgorithm,
            String signatureMethod
    ) {
        SignatureValueSecurityEvent sigValueEvent =
                (SignatureValueSecurityEvent) securityEventListener.getTokenEvent(SecurityEventConstants.SignatureValue);
        assertNotNull(sigValueEvent);
        assertNotNull(sigValueEvent.getSignatureValue());

        List<SecurityEvent> algorithmEvents =
                securityEventListener.getTokenEvents(SecurityEventConstants.AlgorithmSuite);
        assertFalse(algorithmEvents.isEmpty());

        // C14n algorithm
        for (SecurityEvent event : algorithmEvents) {
            AlgorithmSuiteSecurityEvent algorithmEvent = (AlgorithmSuiteSecurityEvent) event;
            if (algorithmEvent.getKeyUsage() == XMLSecurityConstants.C14n) {
                assertEquals(c14nAlgorithm, algorithmEvent.getAlgorithmURI());
            }
        }

        // Digest algorithm
        for (SecurityEvent event : algorithmEvents) {
            AlgorithmSuiteSecurityEvent algorithmEvent = (AlgorithmSuiteSecurityEvent) event;
            if (algorithmEvent.getKeyUsage() == XMLSecurityConstants.Dig) {
                assertEquals(digestAlgorithm, algorithmEvent.getAlgorithmURI());
            }
        }

        // Signature method
        for (SecurityEvent event : algorithmEvents) {
            AlgorithmSuiteSecurityEvent algorithmEvent = (AlgorithmSuiteSecurityEvent) event;
            if (algorithmEvent.getKeyUsage() == XMLSecurityConstants.Asym_Sig
                    || algorithmEvent.getKeyUsage() == XMLSecurityConstants.Sym_Sig) {
                assertEquals(signatureMethod, algorithmEvent.getAlgorithmURI());
            }
        }
    }

    protected void checkSignedElementSecurityEvents(TestSecurityEventListener securityEventListener) {
        SignedElementSecurityEvent signedElementEvent =
                (SignedElementSecurityEvent) securityEventListener.getTokenEvent(SecurityEventConstants.SignedElement);
        assertNotNull(signedElementEvent);
        assertEquals(signedElementEvent.getElementPath().size(), 2);
        assertEquals("{urn:example:po}PurchaseOrder", signedElementEvent.getElementPath().get(0).toString());
        assertEquals("{urn:example:po}PaymentInfo", signedElementEvent.getElementPath().get(1).toString());
        assertTrue(signedElementEvent.isSigned());
    }

    protected void checkSignedElementMultipleSecurityEvents(
            TestSecurityEventListener securityEventListener
    ) {
        List<SecurityEvent> signedElements =
                securityEventListener.getTokenEvents(SecurityEventConstants.SignedElement);
        assertTrue(signedElements.size() == 2);
        SignedElementSecurityEvent signedElementEvent =
                (SignedElementSecurityEvent) signedElements.get(0);
        assertNotNull(signedElementEvent);
        assertEquals(signedElementEvent.getElementPath().size(), 2);
        assertEquals("{urn:example:po}PurchaseOrder", signedElementEvent.getElementPath().get(0).toString());
        assertEquals("{urn:example:po}ShippingAddress", signedElementEvent.getElementPath().get(1).toString());

        assertTrue(signedElementEvent.isSigned());

        signedElementEvent =
                (SignedElementSecurityEvent) signedElements.get(1);
        assertNotNull(signedElementEvent);
        assertEquals(signedElementEvent.getElementPath().size(), 2);
        assertEquals("{urn:example:po}PurchaseOrder", signedElementEvent.getElementPath().get(0).toString());
        assertEquals("{urn:example:po}PaymentInfo", signedElementEvent.getElementPath().get(1).toString());
        assertTrue(signedElementEvent.isSigned());
    }

    protected void checkSignatureToken(
            TestSecurityEventListener securityEventListener,
            X509Certificate cert,
            Key key,
            XMLSecurityConstants.XMLKeyIdentifierType keyIdentifierType
    ) throws XMLSecurityException {
        if (keyIdentifierType == XMLSecurityConstants.XMLKeyIdentifierType.KEY_VALUE) {
            KeyValueTokenSecurityEvent tokenEvent = 
                    (KeyValueTokenSecurityEvent) securityEventListener.getTokenEvent(SecurityEventConstants.KeyValueToken);
            assertNotNull(tokenEvent);
        } else if (keyIdentifierType == XMLSecurityConstants.XMLKeyIdentifierType.NO_KEY_INFO) {
            DefaultTokenSecurityEvent tokenEvent =
                    (DefaultTokenSecurityEvent) securityEventListener.getTokenEvent(SecurityEventConstants.DefaultToken);
            assertNotNull(tokenEvent);
            Key processedKey = tokenEvent.getSecurityToken().getSecretKey("", null);
            assertEquals(processedKey, key);
        } else if (keyIdentifierType == XMLSecurityConstants.XMLKeyIdentifierType.KEY_NAME) {
            KeyNameTokenSecurityEvent tokenEvent =
                    (KeyNameTokenSecurityEvent) securityEventListener.getTokenEvent(SecurityEventConstants.KeyNameToken);
            assertNotNull(tokenEvent);
            Key processedKey = tokenEvent.getSecurityToken().getSecretKey("", null);
            assertEquals(processedKey, key);
            assertNotNull(((KeyNameSecurityToken) tokenEvent.getSecurityToken()).getKeyName());
        } else {
            X509TokenSecurityEvent tokenEvent =
                    (X509TokenSecurityEvent) securityEventListener.getTokenEvent(SecurityEventConstants.X509Token);
            assertNotNull(tokenEvent);
            X509SecurityToken x509SecurityToken =
                    (X509SecurityToken) tokenEvent.getSecurityToken();
            assertNotNull(x509SecurityToken);
            if (keyIdentifierType ==
                    XMLSecurityConstants.XMLKeyIdentifierType.X509_CERTIFICATE) {
                assertEquals(cert, x509SecurityToken.getX509Certificates()[0]);
            } else if (keyIdentifierType ==
                    XMLSecurityConstants.XMLKeyIdentifierType.X509_SUBJECT_NAME) {
                Key processedKey = x509SecurityToken.getKey("", null);
                assertEquals(processedKey, cert.getPublicKey());
                assertNotNull(((X509SubjectNameSecurityToken) x509SecurityToken).getSubjectName());
            } else if (keyIdentifierType ==
                    XMLSecurityConstants.XMLKeyIdentifierType.X509_ISSUER_SERIAL) {
                Key processedKey = x509SecurityToken.getKey("", null);
                assertEquals(processedKey, cert.getPublicKey());
                assertNotNull(((X509IssuerSerialSecurityToken) x509SecurityToken).getIssuerName());
                assertNotNull(((X509IssuerSerialSecurityToken) x509SecurityToken).getSerialNumber());
            }
        }
    }

    class ReferenceInfo {
        private String resource;
        private String c14NMethod;
        private String digestMethod;
        private boolean binary;

        ReferenceInfo(String resource, String c14NMethod, String digestMethod, boolean binary) {
            this.resource = resource;
            this.c14NMethod = c14NMethod;
            this.digestMethod = digestMethod;
            this.binary = binary;
        }

        public String getResource() {
            return resource;
        }

        public void setResource(String resource) {
            this.resource = resource;
        }

        public String getC14NMethod() {
            return c14NMethod;
        }

        public void setC14NMethod(String c14NMethod) {
            this.c14NMethod = c14NMethod;
        }

        public String getDigestMethod() {
            return digestMethod;
        }

        public void setDigestMethod(String digestMethod) {
            this.digestMethod = digestMethod;
        }

        public boolean isBinary() {
            return binary;
        }

        public void setBinary(boolean binary) {
            this.binary = binary;
        }
    }
}

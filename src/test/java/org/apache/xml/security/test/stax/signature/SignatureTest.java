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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.UUID;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.stax.config.Init;
import org.apache.xml.security.stax.crypto.CryptoType;
import org.apache.xml.security.stax.ext.SecurePart;
import org.apache.xml.security.stax.ext.SecurityToken;
import org.apache.xml.security.stax.ext.SecurityTokenProvider;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityException;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.impl.DocumentContextImpl;
import org.apache.xml.security.stax.impl.OutputProcessorChainImpl;
import org.apache.xml.security.stax.impl.SecurityContextImpl;
import org.apache.xml.security.stax.impl.XMLSecurityStreamWriter;
import org.apache.xml.security.stax.impl.processor.output.FinalOutputProcessor;
import org.apache.xml.security.stax.impl.processor.output.XMLSignatureOutputProcessor;
import org.apache.xml.security.test.dom.DSNamespaceContext;
import org.apache.xml.security.test.stax.utils.XMLSecEventAllocator;
import org.apache.xml.security.test.stax.utils.XmlReaderToWriter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A set of test-cases for Signature creation.
 */
public class SignatureTest extends org.junit.Assert {

    private XMLInputFactory xmlInputFactory;
    private DocumentBuilderFactory documentBuilderFactory;
    
    static {
        try {
            Class<?> c =
                    SignatureTest.class.getClassLoader().loadClass(
                            "org.bouncycastle.jce.provider.BouncyCastleProvider"
                    );
            if (null == Security.getProvider("BC")) {
                // Security.addProvider((Provider) c.newInstance());
                Security.insertProviderAt((Provider) c.newInstance(), 1);
            }
        } catch (Throwable e) {
            throw new RuntimeException("Adding BouncyCastle provider failed", e);
        }
    }

    @Before
    public void setUp() throws Exception {
        Init.init(SignatureTest.class.getClassLoader().getResource("security-config.xml").toURI());
        org.apache.xml.security.Init.init();
        
        xmlInputFactory = XMLInputFactory.newInstance();
        xmlInputFactory.setEventAllocator(new XMLSecEventAllocator());
        
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setIgnoringComments(false);
        documentBuilderFactory.setCoalescing(false);
        documentBuilderFactory.setIgnoringElementContentWhitespace(false);
    }

    @Test
    public void testSignatureCreation() throws Exception {
        // Set up the Configuration
        XMLSecurityProperties properties = new XMLSecurityProperties();
        XMLSecurityConstants.Action[] actions = 
            new XMLSecurityConstants.Action[]{XMLSecurityConstants.SIGNATURE};
        properties.setOutAction(actions);
        properties.loadSignatureKeyStore(
            this.getClass().getClassLoader().getResource("transmitter.jks"), "default".toCharArray()
        );
        properties.setSignatureUser("transmitter");
        properties.setSignatureAlgorithm("http://www.w3.org/2000/09/xmldsig#rsa-sha1");
        properties.setSignatureCanonicalizationAlgorithm("http://www.w3.org/2001/10/xml-exc-c14n#");
        properties.setSignatureDigestAlgorithm("http://www.w3.org/2000/09/xmldsig#sha1");
        
        SecurePart securePart = 
               new SecurePart(new QName("urn:example:po", "PaymentInfo"), SecurePart.Modifier.Content);
        properties.addSignaturePart(securePart);
        
        // Set the key up
        SecurityContextImpl securityContextImpl = new SecurityContextImpl();
        Key key = properties.getSignatureCrypto().getPrivateKey("transmitter", "default");
        CryptoType cryptoType = new CryptoType(CryptoType.TYPE.ALIAS);
        cryptoType.setAlias(properties.getSignatureUser());
        X509Certificate[] x509Certificates = properties.getSignatureCrypto().getX509Certificates(cryptoType);
        
        SignatureSecurityToken securityToken = new SignatureSecurityToken(key, x509Certificates);
        String id = UUID.randomUUID().toString();
        SignatureSecurityTokenProvider securityTokenProvider = 
                new SignatureSecurityTokenProvider(securityToken, id);
        securityContextImpl.registerSecurityTokenProvider(id, securityTokenProvider);
        securityContextImpl.put(XMLSecurityConstants.PROP_USE_THIS_TOKEN_ID_FOR_SIGNATURE, id);
        securityContextImpl.putAsMap(XMLSecurityConstants.SIGNATURE_PARTS, securePart.getName(), securePart);

        final DocumentContextImpl documentContext = new DocumentContextImpl();
        documentContext.setEncoding("UTF-8");
        InputStream sourceDocument = 
                this.getClass().getClassLoader().getResourceAsStream(
                        "ie/baltimore/merlin-examples/merlin-xmlenc-five/plaintext.xml");
        
        OutputProcessorChainImpl processorChain = 
            new OutputProcessorChainImpl(securityContextImpl, documentContext);
        XMLSignatureOutputProcessor signatureOutputProcessor = new XMLSignatureOutputProcessor();
        signatureOutputProcessor.setXMLSecurityProperties(properties);
        signatureOutputProcessor.setAction(XMLSecurityConstants.SIGNATURE);
        signatureOutputProcessor.init(processorChain);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FinalOutputProcessor finalOutputProcessor = new FinalOutputProcessor(baos, "UTF-8");
        finalOutputProcessor.setXMLSecurityProperties(properties);
        finalOutputProcessor.setAction(null);
        finalOutputProcessor.init(processorChain);
        processorChain.addProcessor(finalOutputProcessor);
        
        XMLStreamWriter xmlStreamWriter = new XMLSecurityStreamWriter(processorChain);
        
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(sourceDocument);
        XmlReaderToWriter.writeAll(xmlStreamReader, xmlStreamWriter);
        xmlStreamWriter.close();
        
        // System.out.println("Got:\n" + new String(baos.toByteArray(), "UTF-8"));
        Document document = 
            documentBuilderFactory.newDocumentBuilder().parse(new ByteArrayInputStream(baos.toByteArray()));
        
        // Verify using DOM
        verifyUsingDOM(document, x509Certificates[0], securePart);
    }

    /**
     * Verify the document using DOM
     */
    private void verifyUsingDOM(
        Document document,
        X509Certificate cert,
        SecurePart securePart
    ) throws Exception {
        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        xpath.setNamespaceContext(new DSNamespaceContext());

        String expression = "//dsig:Signature[1]";
        Element sigElement = 
            (Element) xpath.evaluate(expression, document, XPathConstants.NODE);
        Assert.assertNotNull(sigElement);
        
        expression = "//*[local-name()='" + securePart.getName().getLocalPart() + "']";
        Element signedElement = 
            (Element)xpath.evaluate(expression, document, XPathConstants.NODE);
        Assert.assertNotNull(signedElement);
        signedElement.setIdAttributeNS(null, "Id", true);
        
        XMLSignature signature = new XMLSignature(sigElement, "");
        KeyInfo ki = signature.getKeyInfo();
        Assert.assertNotNull(ki);

        Assert.assertTrue(signature.checkSignatureValue(cert));
    }
    
    private static class SignatureSecurityToken implements SecurityToken {
        private Key key;
        private X509Certificate[] certs;
        
        public SignatureSecurityToken(Key key, X509Certificate[] certs) {
            this.key = key;
            this.certs = certs;
        }

        public String getId() {
            return null;
        }


        public Object getProcessor() {
            return null;
        }

        public boolean isAsymmetric() {
            return false;
        }

        public Key getSecretKey(
            String algorithmURI, XMLSecurityConstants.KeyUsage keyUsage
        ) throws XMLSecurityException {
            return key;
        }

        public PublicKey getPublicKey(
            String algorithmURI, XMLSecurityConstants.KeyUsage keyUsage
        ) throws XMLSecurityException {
            return null;
        }

        public X509Certificate[] getX509Certificates() throws XMLSecurityException {
            return certs;
        }

        public void verify() throws XMLSecurityException {
        }

        public SecurityToken getKeyWrappingToken() {
            return null;
        }

        public XMLSecurityConstants.TokenType getTokenType() {
            return null;
        }

        @Override
        public List<QName> getElementPath() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public XMLSecEvent getXMLSecEvent() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<SecurityToken> getWrappedTokens()
                throws XMLSecurityException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void addWrappedToken(SecurityToken securityToken) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void addTokenUsage(TokenUsage tokenUsage)
                throws XMLSecurityException {
            // TODO Auto-generated method stub
            
        }

        @Override
        public List<TokenUsage> getTokenUsages() {
            // TODO Auto-generated method stub
            return null;
        }
    };

    private static class SignatureSecurityTokenProvider implements SecurityTokenProvider {
        private SecurityToken token;
        private String id;
        
        public SignatureSecurityTokenProvider(SecurityToken token, String id) {
            this.token = token;
        }
        
        @Override
        public String getId() {
            return id;
        }

        @Override
        public SecurityToken getSecurityToken() throws XMLSecurityException {
            return token;
        }
    };


}
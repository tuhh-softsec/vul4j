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
package org.apache.xml.security.test.stax.performance;

import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.stax.ext.*;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;
import org.apache.xml.security.test.stax.utils.XmlReaderToWriter;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.XMLUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.crypto.KeyGenerator;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import java.io.*;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author $Author: $
 * @version $Revision: $ $Date: $
 */
public abstract class AbstractPerformanceTest {

    private static Key encryptionSymKey;
    protected XMLInputFactory xmlInputFactory;
    protected Key key;
    protected X509Certificate cert;
    private OutboundXMLSec outboundSignatureXMLSec;
    private InboundXMLSec inboundSignatureXMLSec;
    private OutboundXMLSec outboundEncryptionXMLSec;
    private InboundXMLSec inboundDecryptionXMLSec;

    @BeforeClass
    public static void genKey() throws Exception {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(256);
        encryptionSymKey = keygen.generateKey();
    }

    @Before
    public void setUp() throws Exception {
        xmlInputFactory = XMLInputFactory.newInstance();
        xmlInputFactory.setProperty(XMLInputFactory.IS_COALESCING, false);
        xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);

        org.apache.xml.security.Init.init();

        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(
                this.getClass().getClassLoader().getResource("transmitter.jks").openStream(),
                "default".toCharArray()
        );
        key = keyStore.getKey("transmitter", "default".toCharArray());
        cert = (X509Certificate) keyStore.getCertificate("transmitter");

        setUpOutboundSignatureXMLSec();
        setUpInboundSignatureXMLSec();

        setUpOutboundEncryptionXMLSec();
        setUpInboundEncryptionXMLSec();
    }

    protected File generateLargeXMLFile(int factor) throws Exception {
        File path = getTmpFilePath();
        path.mkdirs();
        File target = new File(path, "tmp.xml");
        FileWriter fileWriter = new FileWriter(target, false);
        fileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<test xmlns=\"http://www.example.com\">");
        fileWriter.close();
        FileOutputStream fileOutputStream = new FileOutputStream(target, true);
        for (int i = 0; i < factor; i++) {
            int read = 0;
            byte[] buffer = new byte[4096];
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(
                    "org/w3c/www/interop/xmlenc-core-11/plaintext.xml");
            while ((read = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, read);
            }
            inputStream.close();
        }
        fileWriter = new FileWriter(target, true);
        fileWriter.write("</test>");
        fileWriter.close();
        
        fileOutputStream.close();

        return target;
    }

    protected int countXMLStartTags(File file) throws Exception {
        int i = 0;
        FileInputStream fileInputStream = new FileInputStream(file);
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(fileInputStream);
        while (xmlStreamReader.hasNext()) {
            xmlStreamReader.next();
            switch (xmlStreamReader.getEventType()) {
                case XMLStreamConstants.START_ELEMENT:
                    i++;
                    break;
            }
        }
        xmlStreamReader.close();
        fileInputStream.close();
        return i;
    }

    protected abstract File getTmpFilePath();

    protected void setUpOutboundSignatureXMLSec() throws XMLSecurityException {
        XMLSecurityProperties xmlSecurityProperties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.SIGNATURE);
        xmlSecurityProperties.setActions(actions);
        xmlSecurityProperties.setSignatureKeyIdentifier(SecurityTokenConstants.KeyIdentifier_X509KeyIdentifier);

        xmlSecurityProperties.setSignatureKey(key);
        xmlSecurityProperties.setSignatureCerts(new X509Certificate[]{cert});
        xmlSecurityProperties.setSignatureCanonicalizationAlgorithm("http://www.w3.org/TR/2001/REC-xml-c14n-20010315");

        SecurePart securePart = new SecurePart(
                new QName("http://www.example.com", "test"),
                SecurePart.Modifier.Element,
                new String[]{
                        "http://www.w3.org/2000/09/xmldsig#enveloped-signature",
                        "http://www.w3.org/TR/2001/REC-xml-c14n-20010315"
                },
                "http://www.w3.org/2000/09/xmldsig#sha1"
        );
        xmlSecurityProperties.addSignaturePart(securePart);

        outboundSignatureXMLSec = XMLSec.getOutboundXMLSec(xmlSecurityProperties);
    }

    protected void setUpInboundSignatureXMLSec() throws XMLSecurityException {
        XMLSecurityProperties inboundProperties = new XMLSecurityProperties();
        inboundProperties.setSignatureVerificationKey(cert.getPublicKey());
        inboundSignatureXMLSec = XMLSec.getInboundWSSec(inboundProperties);
    }

    protected void setUpOutboundEncryptionXMLSec() throws XMLSecurityException {
        XMLSecurityProperties xmlSecurityProperties = new XMLSecurityProperties();
        List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();
        actions.add(XMLSecurityConstants.ENCRYPT);
        xmlSecurityProperties.setActions(actions);
        xmlSecurityProperties.setEncryptionKey(encryptionSymKey);
        xmlSecurityProperties.setEncryptionSymAlgorithm("http://www.w3.org/2001/04/xmlenc#aes256-cbc");

        SecurePart securePart = new SecurePart(
                new QName("http://www.example.com", "test"),
                SecurePart.Modifier.Element
        );
        xmlSecurityProperties.addEncryptionPart(securePart);

        outboundEncryptionXMLSec = XMLSec.getOutboundXMLSec(xmlSecurityProperties);
    }

    protected void setUpInboundEncryptionXMLSec() throws XMLSecurityException {
        XMLSecurityProperties inboundProperties = new XMLSecurityProperties();
        inboundProperties.setDecryptionKey(encryptionSymKey);
        inboundDecryptionXMLSec = XMLSec.getInboundWSSec(inboundProperties);
    }

    protected File doStreamingSignatureOutbound(File file, int tagCount) throws Exception {

        final File signedFile = new File(getTmpFilePath(), "signature-stax-" + tagCount + ".xml");
        OutputStream outputStream = new FileOutputStream(signedFile);
        XMLStreamWriter xmlStreamWriter = outboundSignatureXMLSec.processOutMessage(outputStream, "UTF-8");

        InputStream inputStream = new FileInputStream(file);
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(inputStream);

        XmlReaderToWriter.writeAll(xmlStreamReader, xmlStreamWriter);
        xmlStreamWriter.close();
        outputStream.close();
        xmlStreamReader.close();
        inputStream.close();
        return signedFile;
    }

    protected void doStreamingSignatureInbound(File file, int tagCount) throws Exception {

        InputStream inputStream = new FileInputStream(file);
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(inputStream);
        XMLStreamReader securityStreamReader = inboundSignatureXMLSec.processInMessage(xmlStreamReader);

        while (securityStreamReader.hasNext()) {
            securityStreamReader.next();
        }
        xmlStreamReader.close();
        inputStream.close();
        securityStreamReader.close();
    }

    protected void doDOMSignatureOutbound(File file, int tagCount) throws Exception {

        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(file);

        XMLSignature sig = new XMLSignature(document, "", "http://www.w3.org/2000/09/xmldsig#rsa-sha1");
        Element root = document.getDocumentElement();
        root.insertBefore(sig.getElement(), root.getFirstChild());

        Transforms transforms = new Transforms(document);
        transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
        transforms.addTransform(Transforms.TRANSFORM_C14N_OMIT_COMMENTS);
        sig.addDocument("", transforms, "http://www.w3.org/2000/09/xmldsig#sha1");

        sig.sign(key);
        sig.addKeyInfo(cert);

        XMLUtils.outputDOM(document, new BufferedOutputStream(new FileOutputStream(new File(getTmpFilePath(), "signature-dom-" + tagCount + ".xml"))));
    }

    protected void doDOMSignatureInbound(File file, int tagCount) throws Exception {

        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(file);

        Element signatureElement = (Element) document.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature").item(0);
        ((Element) signatureElement.getParentNode()).setIdAttributeNS(null, "Id", true);
        XMLSignature xmlSignature = new XMLSignature(signatureElement, "", true);
        xmlSignature.checkSignatureValue(cert);
    }

    protected File doStreamingEncryptionOutbound(File file, int tagCount) throws Exception {

        final File signedFile = new File(getTmpFilePath(), "encryption-stax-" + tagCount + ".xml");
        OutputStream outputStream = new FileOutputStream(signedFile);
        XMLStreamWriter xmlStreamWriter = outboundEncryptionXMLSec.processOutMessage(outputStream, "UTF-8");

        InputStream inputStream = new FileInputStream(file);
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(inputStream);

        XmlReaderToWriter.writeAll(xmlStreamReader, xmlStreamWriter);
        xmlStreamWriter.close();
        outputStream.close();
        xmlStreamReader.close();
        inputStream.close();
        return signedFile;
    }

    protected void doStreamingDecryptionInbound(File file, int tagCount) throws Exception {

        InputStream inputStream = new FileInputStream(file);
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(inputStream);
        XMLStreamReader securityStreamReader = inboundDecryptionXMLSec.processInMessage(xmlStreamReader);

        while (securityStreamReader.hasNext()) {
            securityStreamReader.next();
        }
        xmlStreamReader.close();
        inputStream.close();
        securityStreamReader.close();
    }

    protected void doDOMEncryptionOutbound(File file, int tagCount) throws Exception {

        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(file);

        XMLCipher cipher = XMLCipher.getInstance("http://www.w3.org/2001/04/xmlenc#aes256-cbc");
        cipher.init(XMLCipher.ENCRYPT_MODE, encryptionSymKey);
        document = cipher.doFinal(document, document.getDocumentElement());

        XMLUtils.outputDOM(document, new BufferedOutputStream(new FileOutputStream(new File(getTmpFilePath(), "encryption-dom-" + tagCount + ".xml"))));
    }

    protected void doDOMDecryptionInbound(File file, int tagCount) throws Exception {

        DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
        Document document = builder.parse(file);

        XMLCipher cipher = XMLCipher.getInstance("http://www.w3.org/2001/04/xmlenc#aes256-cbc");
        cipher.init(XMLCipher.DECRYPT_MODE, encryptionSymKey);
        cipher.doFinal(document, document.getDocumentElement());
    }
}

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
package org.apache.xml.security.test.dom.keys.content.x509;

import java.io.FileInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.xml.parsers.DocumentBuilder;

import org.apache.xml.security.Init;
import org.apache.xml.security.keys.content.x509.XMLX509Digest;
import org.apache.xml.security.utils.Base64;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLX509DigestTest extends Assert {

    private static final String BASEDIR = System.getProperty("basedir") == null ? "./": System.getProperty("basedir");
    private static final String SEP = System.getProperty("file.separator");

    private DocumentBuilder documentBuilder;

    private X509Certificate certControl;

    private final String algorithmURIControl = "http://www.w3.org/2001/04/xmlenc#sha256";

    private final String digestBase64Control = "jToLQ/K7aaLHy/aXLFnjEfCwSQd9z0MrBOH6Ru/aJyY=";
    private final byte[] digestControl;

    public XMLX509DigestTest() throws Exception {
        documentBuilder = XMLUtils.createDocumentBuilder(false);

        certControl = loadCertificate("cert-X509Digest.crt");

        digestControl = Base64.decode(digestBase64Control);

        if (!Init.isInitialized()) {
            Init.init();
        }
    }

    @org.junit.Test
    public void testSchema() throws Exception {
        XMLX509Digest x509Digest = new XMLX509Digest(documentBuilder.newDocument(), digestControl, algorithmURIControl);
        Element element = x509Digest.getElement();

        assertEquals("http://www.w3.org/2009/xmldsig11#", element.getNamespaceURI());
        assertEquals("X509Digest", element.getLocalName());
    }

    @org.junit.Test
    public void testDigestFromElement() throws Exception {
        Document doc = loadXML("X509Digest.xml");
        NodeList nl = doc.getElementsByTagNameNS(Constants.SignatureSpec11NS, Constants._TAG_X509DIGEST);
        Element element = (Element) nl.item(0);

        XMLX509Digest x509Digest = new XMLX509Digest(element, "");
        assertEquals(algorithmURIControl, x509Digest.getAlgorithm());
        assertArrayEquals(digestControl, x509Digest.getDigestBytes());
    }

    @org.junit.Test
    public void testDigestOnConstructionWithCert() throws Exception {
        XMLX509Digest x509Digest = new XMLX509Digest(documentBuilder.newDocument(), certControl, algorithmURIControl);
        assertEquals(algorithmURIControl, x509Digest.getAlgorithm());
        assertArrayEquals(digestControl, x509Digest.getDigestBytes());
    }

    @org.junit.Test
    public void testDigestOnConstructionWithBytes() throws Exception {
        XMLX509Digest x509Digest = new XMLX509Digest(documentBuilder.newDocument(), digestControl, algorithmURIControl);
        assertEquals(algorithmURIControl, x509Digest.getAlgorithm());
        assertArrayEquals(digestControl, x509Digest.getDigestBytes());
    }

    @org.junit.Test
    public void testGetDigestBytesFromCert() throws Exception {
        assertArrayEquals(digestControl, XMLX509Digest.getDigestBytesFromCert(certControl, algorithmURIControl));
    }


    // Utility methods

    private String getControlFilePath(String fileName) {
        return BASEDIR + SEP + "src" + SEP + "test" + SEP + "resources" + 
            SEP + "org" + SEP + "apache" + SEP + "xml" + SEP + "security" + 
            SEP + "keys" + SEP + "content" + SEP + "x509" +
            SEP + fileName;
    }

    private Document loadXML(String fileName) throws Exception {
        return documentBuilder.parse(new FileInputStream(getControlFilePath(fileName)));
    }

    private X509Certificate loadCertificate(String fileName) throws Exception {
        FileInputStream fis = new FileInputStream(getControlFilePath(fileName));
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) certFactory.generateCertificate(fis);
    }

}

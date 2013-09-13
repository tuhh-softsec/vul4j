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
package org.apache.xml.security.test.dom.c14n.implementations;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.xml.parsers.DocumentBuilder;

import org.apache.xml.security.Init;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.ObjectContainer;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Bug45961Test extends org.junit.Assert {

    private static final String OBJECT_ID = "Object";
    private static final String MOCK_CANONICALIZATION_METHOD = 
        MockCanonicalizationMethod.MOCK_CANONICALIZATION_METHOD;
    private static final char[] PASSWORD = "changeit".toCharArray();
    private static final String ALIAS = "mullan";
    private DocumentBuilder _builder;
    private ObjectContainer object;

    public Bug45961Test() throws Exception {
        Init.init();
        Canonicalizer.register(MOCK_CANONICALIZATION_METHOD,
                               MockCanonicalizationMethod.class.getName());
        _builder = XMLUtils.createDocumentBuilder(false);
    }

    @org.junit.Test
    public void testBug() throws Exception {
        Document document = getSignedDocument();
        NodeList list = 
            document.getElementsByTagNameNS(Constants.SignatureSpecNS, Constants._TAG_SIGNATURE);
        Element element = (Element) list.item(0);
        XMLSignature signature = new XMLSignature(element, null);
        KeyInfo keyInfo = signature.getKeyInfo();
        X509Certificate certificate = keyInfo.getX509Certificate();
        assertNotNull(certificate);
        try {
            signature.checkSignatureValue(certificate);
        } catch (XMLSignatureException e) {
            fail(e.getMessage());
        }
    }

    private Document getSignedDocument() throws Exception {
        KeyStore ks = KeyStore.getInstance("JKS");
        FileInputStream fis = 
            new FileInputStream(getAbsolutePath("src/test/resources/test.jks"));
        ks.load(fis, PASSWORD);
        fis.close();
        PrivateKey privateKey = (PrivateKey) ks.getKey(ALIAS, PASSWORD);
        X509Certificate signingCert = (X509Certificate) ks
        .getCertificate(ALIAS);

        Document document = _builder.newDocument();

        XMLSignature signature = new XMLSignature(document, null,
                                                  XMLSignature.ALGO_ID_SIGNATURE_DSA,
                                                  MOCK_CANONICALIZATION_METHOD);

        Element root = document.createElementNS("", "RootElement");
        root.appendChild(document.createTextNode("Some simple test\n"));
        root.appendChild(signature.getElement());
        document.appendChild(root);

        //		document.appendChild(signature.getElement());

        Element root2 = document.createElementNS("", "RootElement");
        root2.appendChild(document.createTextNode("Some simple test\n"));
        object = new ObjectContainer(document);
        object.appendChild(root2);
        object.setId(OBJECT_ID);
        root.appendChild(object.getElement());

        signature.addDocument("#" + OBJECT_ID);
        signature.addDocument("", getTransforms(document));

        signature.addKeyInfo(signingCert);
        signature.sign(privateKey);
        return document;
    }

    private Transforms getTransforms(Document document) throws Exception {
        Transforms transforms = new Transforms(document);
        transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
        return transforms;
    }

    private String getAbsolutePath(String path) {
        String basedir = System.getProperty("basedir");
        if (basedir != null && !"".equals(basedir)) {
            path = basedir + "/" + path;
        }
        return path;
    }
    
}

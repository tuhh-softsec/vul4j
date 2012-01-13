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
package org.apache.xml.security.samples;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author $Author$
 */
public class AxisSigner {

    /** Field AXIS_SIGNATURE_FILENAME           */
    public static final String AXIS_SIGNATURE_FILENAME = "build/axisSignature.xml";

    /**
     * Method main
     *
     * @param unused
     * @throws Exception
     */
    public static void main(String unused[]) throws Exception {

        org.apache.xml.security.Init.init();

        String keystoreType = "JKS";
        String keystoreFile = "samples/data/keystore.jks";
        String keystorePass = "xmlsecurity";
        String privateKeyAlias = "test";
        String privateKeyPass = "xmlsecurity";
        String certificateAlias = "test";
        File signatureFile = new File(AXIS_SIGNATURE_FILENAME);

        KeyStore ks = KeyStore.getInstance(keystoreType);
        FileInputStream fis = new FileInputStream(keystoreFile);

        ks.load(fis, keystorePass.toCharArray());

        PrivateKey privateKey = 
            (PrivateKey) ks.getKey(privateKeyAlias, privateKeyPass.toCharArray());
        javax.xml.parsers.DocumentBuilderFactory dbf =
            javax.xml.parsers.DocumentBuilderFactory.newInstance();

        dbf.setNamespaceAware(true);

        javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
        org.w3c.dom.Document doc = db.newDocument();

        /*
         * Start SOAP infrastructure code. This is to be made compatible with Axis.
         *
         */
        String soapNS = "http://www.w3.org/2001/12/soap-envelope";
        String SOAPSECNS = "http://schemas.xmlsoap.org/soap/security/2000-12";

        Element envelopeElement = doc.createElementNS(soapNS, "env:Envelope");

        envelopeElement.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:env", soapNS);
        doc.appendChild(envelopeElement);

        Element headerElem = doc.createElementNS(soapNS, "env:Header");
        Element bodyElem = doc.createElementNS(soapNS, "env:Body");

        envelopeElement.appendChild(doc.createTextNode("\n"));
        envelopeElement.appendChild(headerElem);
        envelopeElement.appendChild(doc.createTextNode("\n"));
        envelopeElement.appendChild(bodyElem);
        envelopeElement.appendChild(doc.createTextNode("\n"));
        bodyElem.appendChild(
            doc.createTextNode("This is signed together with it's Body ancestor")
        );

        bodyElem.setAttributeNS(SOAPSECNS, "SOAP-SEC:id", "Body");
        bodyElem.setIdAttributeNS(SOAPSECNS, "id", true);

        Element soapSignatureElem = doc.createElementNS(SOAPSECNS, "SOAP-SEC:Signature");

        envelopeElement.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:SOAP-SEC", SOAPSECNS);
        envelopeElement.setAttributeNS(null, "actor", "some-uri");
        envelopeElement.setAttributeNS(null, "mustUnderstand", "1");
        envelopeElement.appendChild(doc.createTextNode("\n"));
        headerElem.appendChild(soapSignatureElem);

        /*
         *
         * End SOAP infrastructure code. This is to be made compatible with Axis.
         */
        String BaseURI = signatureFile.toURI().toURL().toString();
        XMLSignature sig = new XMLSignature(doc, BaseURI, XMLSignature.ALGO_ID_SIGNATURE_DSA);

        soapSignatureElem.appendChild(sig.getElement());

        {
            sig.addDocument("#Body");
        }

        {
            X509Certificate cert =
                (X509Certificate) ks.getCertificate(certificateAlias);

            sig.addKeyInfo(cert);
            sig.addKeyInfo(cert.getPublicKey());
            sig.sign(privateKey);
        }

        FileOutputStream f = new FileOutputStream(signatureFile);

        XMLUtils.outputDOMc14nWithComments(doc, f);
        f.close();
        System.out.println("Wrote signature to " + BaseURI);

        for (int i = 0; i < sig.getSignedInfo().getSignedContentLength(); i++) {
            System.out.println("--- Signed Content follows ---");
            System.out.println(new String(sig.getSignedInfo().getSignedContentItem(i)));
        }
    }
}

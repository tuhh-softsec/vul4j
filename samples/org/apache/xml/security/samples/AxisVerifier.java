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

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.IdResolver;
import org.w3c.dom.Element;

/**
 * @author $Author$
 */
public class AxisVerifier {

    /**
     * Method main
     *
     * @param unused
     * @throws Exception
     */
    public static void main(String unused[]) throws Exception {

        org.apache.xml.security.Init.init();

        File signatureFile = new File(AxisSigner.AXIS_SIGNATURE_FILENAME);
        javax.xml.parsers.DocumentBuilderFactory dbf =
            javax.xml.parsers.DocumentBuilderFactory.newInstance();

        dbf.setNamespaceAware(true);

        javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
        org.w3c.dom.Document doc = db.parse(new FileInputStream(signatureFile));
        String BaseURI = signatureFile.toURI().toURL().toString();

        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        DSNamespaceContext context = new DSNamespaceContext();
        xpath.setNamespaceContext(context);

        String expression = "//ds:Signature[1]";
        Element sigElement = 
            (Element) xpath.evaluate(expression, doc, XPathConstants.NODE);
        
        expression = "//env:Body[1]";
        context.putPrefix("env", "http://www.w3.org/2001/12/soap-envelope");
        Element bodyElement = 
            (Element) xpath.evaluate(expression, doc, XPathConstants.NODE);
        IdResolver.registerElementById(bodyElement, "Body");
        
        XMLSignature sig = new XMLSignature(sigElement, BaseURI);
        boolean verify = sig.checkSignatureValue(sig.getKeyInfo().getPublicKey());

        System.out.println("The signature is" + (verify ? " " : " not ") + "valid");

        for (int i = 0; i < sig.getSignedInfo().getSignedContentLength(); i++) {
            boolean thisOneWasSigned =
                sig.getSignedInfo().getVerificationResult(i);

            if (thisOneWasSigned) {
                System.out.println("--- Signed Content follows ---");
                System.out.println(new String(sig.getSignedInfo().getSignedContentItem(i)));
            }
        }

        System.out.println("");
        System.out.println("Prior transforms");
        System.out.println(
            new String(sig.getSignedInfo().getReferencedContentBeforeTransformsItem(0).getBytes())
        );
    }
}

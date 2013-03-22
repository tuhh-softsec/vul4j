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
package org.apache.xml.security.samples.iaik;

import java.io.File;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.samples.DSNamespaceContext;
import org.apache.xml.security.samples.SampleUtils;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.apache.xml.security.utils.resolver.implementations.ResolverAnonymous;
import org.w3c.dom.Element;

/**
 * @author $Author$
 */
public class IAIKInterOp {

    /** {@link org.apache.commons.logging} logging facility */
    static org.slf4j.Logger log = 
        org.slf4j.LoggerFactory.getLogger(IAIKInterOp.class.getName());

    /** Field schemaValidate */
    static final boolean schemaValidate = false;

    /** Field signatureSchemaFile */
    static final String signatureSchemaFile = "samples/data/xmldsig-core-schema.xsd";
    
    static {
        org.apache.xml.security.Init.init();
    }

    /**
     * Method main
     *
     * @param unused
     */
    public static void main(String unused[]) {

        if (schemaValidate) {
            System.out.println("We do schema-validation");
        } else {
            System.out.println("We do not schema-validation");
        }

        javax.xml.parsers.DocumentBuilderFactory dbf =
            javax.xml.parsers.DocumentBuilderFactory.newInstance();

        if (IAIKInterOp.schemaValidate) {
            dbf.setAttribute("http://apache.org/xml/features/validation/schema", Boolean.TRUE);
            dbf.setAttribute("http://apache.org/xml/features/dom/defer-node-expansion", Boolean.TRUE);
            dbf.setValidating(true);
            dbf.setAttribute("http://xml.org/sax/features/validation", Boolean.TRUE);
            dbf.setAttribute("http://apache.org/xml/properties/schema/external-schemaLocation",
                             Constants.SignatureSpecNS + " " + IAIKInterOp.signatureSchemaFile);
        }

        dbf.setNamespaceAware(true);
        dbf.setAttribute("http://xml.org/sax/features/namespaces", Boolean.TRUE);

        String gregorsDir = "samples/data/at/iaik/ixsil/";
        String filenames[] = {
                              gregorsDir + "signatureAlgorithms/signatures/hMACSignature.xml"
                              ,gregorsDir + "signatureAlgorithms/signatures/hMACShortSignature.xml"
                              ,gregorsDir + "signatureAlgorithms/signatures/dSASignature.xml"
                              ,gregorsDir + "signatureAlgorithms/signatures/rSASignature.xml"
                              ,gregorsDir + "transforms/signatures/base64DecodeSignature.xml"
                              ,gregorsDir + "transforms/signatures/c14nSignature.xml"
                              ,gregorsDir + "coreFeatures/signatures/manifestSignature.xml"
                              ,gregorsDir + "transforms/signatures/xPathSignature.xml"
                              ,gregorsDir + "coreFeatures/signatures/signatureTypesSignature.xml"
                              ,gregorsDir + "transforms/signatures/envelopedSignatureSignature.xml"
        };
        verifyAnonymous(gregorsDir, dbf);

        for (int i = 0; i < 2; i++) {
            String signatureFileName = filenames[i];

            try {
                org.apache.xml.security.samples.signature
                .VerifyMerlinsExamplesFifteen.verifyHMAC(dbf, signatureFileName);
            } catch (Exception ex) {
                System.out.println("The XML signature in file "
                                   + signatureFileName + " crashed the application (bad)");
                ex.printStackTrace();
                System.out.println();
            }
        }

        for (int i = 2; i < filenames.length; i++) {
            String signatureFileName = filenames[i];

            try {
                org.apache.xml.security.samples.signature
                .VerifyMerlinsExamplesSixteen.verify(dbf, signatureFileName);
            } catch (Exception ex) {
                System.out.println("The XML signature in file "
                                   + signatureFileName + " crashed the application (bad)");
                ex.printStackTrace();
                System.out.println();
            }
        }

        for (int i = 2; i < filenames.length; i++) {
            String signatureFileName = filenames[i];

            try {
                org.apache.xml.security.samples.signature
                .VerifyMerlinsExamplesTwentyThree.verify(dbf, signatureFileName);
            } catch (Exception ex) {
                System.out.println("The XML signature in file "
                                   + signatureFileName + " crashed the application (bad)");
                ex.printStackTrace();
                System.out.println();
            }
        }
    }

    public static void verifyAnonymous(String gregorsDir, DocumentBuilderFactory dbf) {
        String filename =
            gregorsDir + "coreFeatures/signatures/anonymousReferenceSignature.xml";
        try {
            String anonymousRef =
                gregorsDir + "coreFeatures/samples/anonymousReferenceContent.xml";
            ResourceResolverSpi resolver = new ResolverAnonymous(anonymousRef);
            File f = new File(filename);

            System.out.println("Try to verify " + f.toURI().toURL().toString());

            javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.parse(new java.io.FileInputStream(f));

            XPathFactory xpf = XPathFactory.newInstance();
            XPath xpath = xpf.newXPath();
            xpath.setNamespaceContext(new DSNamespaceContext());

            String expression = "//ds:Signature[1]";
            Element sigElement = 
                (Element) xpath.evaluate(expression, doc, XPathConstants.NODE);
            
            XMLSignature signature = new XMLSignature(sigElement, f.toURI().toURL().toString());

            signature.setFollowNestedManifests(false);
            signature.addResourceResolver(resolver);

            KeyInfo ki = signature.getKeyInfo();

            if (ki != null) {
                X509Certificate cert = signature.getKeyInfo().getX509Certificate();

                if (cert != null) {
                    System.out.println(
                        "The XML signature in file " + f.toURI().toURL().toString() + " is "
                        + (signature.checkSignatureValue(cert) ? "valid (good)" 
                        : "invalid !!!!! (bad)")
                    );
                } else {
                    PublicKey pk = signature.getKeyInfo().getPublicKey();

                    if (pk != null) {
                        System.out.println(
                            "The XML signature in file " + f.toURI().toURL().toString() + " is "
                            + (signature.checkSignatureValue(pk) ? "valid (good)"
                            : "invalid !!!!! (bad)")
                        );
                    } else {
                        System.out.println("Did not find a public key, so I can't check the signature");
                    }
                }
            } else {
                System.out.println("Did not find a KeyInfo");
            }
        } catch (Exception ex) {
            System.out.println("The XML signature in file "
                               + filename + " crashed the application (bad)");
            ex.printStackTrace();
            System.out.println();
        }
    }

}

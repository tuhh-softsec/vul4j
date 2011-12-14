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
package org.apache.xml.security.samples.signature;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import org.w3c.dom.Element;

/**
 *
 * @author $Author$
 */
public class VerifyMerlinsExamplesSixteen {

    /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(VerifyMerlinsExamplesSixteen.class.getName());

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

        if (VerifyMerlinsExamplesSixteen.schemaValidate) {
            dbf.setAttribute("http://apache.org/xml/features/validation/schema",
                             Boolean.TRUE);
            dbf.setAttribute("http://apache.org/xml/features/dom/defer-node-expansion",
                             Boolean.TRUE);
            dbf.setValidating(true);
            dbf.setAttribute("http://xml.org/sax/features/validation",
                             Boolean.TRUE);
            dbf.setAttribute("http://apache.org/xml/properties/schema/external-schemaLocation",
                             Constants.SignatureSpecNS + " "
                             + VerifyMerlinsExamplesSixteen.signatureSchemaFile);
        }

        dbf.setNamespaceAware(true);
        dbf.setAttribute("http://xml.org/sax/features/namespaces", Boolean.TRUE);

        String filenames[] = {
            "samples/data/ie/baltimore/merlin-examples/merlin-xmldsig-sixteen/signature.xml"
        };
        int start = 0;
        int end = filenames.length;

        for (int i = start; i < end; i++) {
            String signatureFileName = filenames[i];

            try {
                verify(dbf, signatureFileName);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Method verify
     *
     * @param dbf
     * @param filename
     * @throws Exception
     */
    public static void verify(DocumentBuilderFactory dbf, String filename)
        throws Exception {

        File f = new File(filename);

        System.out.println("Try to verify " + f.toURL().toString());

        javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();

        if (VerifyMerlinsExamplesSixteen.schemaValidate) {
            db.setErrorHandler(new org.apache.xml.security.utils
                               .IgnoreAllErrorHandler());
            db.setEntityResolver(new org.xml.sax.EntityResolver() {

                public org.xml.sax.InputSource resolveEntity(String publicId, String systemId)
                    throws org.xml.sax.SAXException {

                    if (systemId.endsWith("xmldsig-core-schema.xsd")) {
                        try {
                            return new org.xml.sax.InputSource(new FileInputStream(signatureSchemaFile));
                        } catch (FileNotFoundException ex) {
                            throw new org.xml.sax.SAXException(ex);
                        }
                    } else {
                        return null;
                    }
                }
            });
        }

        org.w3c.dom.Document doc = db.parse(new java.io.FileInputStream(f));

        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        xpath.setNamespaceContext(new DSNamespaceContext());

        String expression = "//ds:Signature[1]";
        Element sigElement = 
            (Element) xpath.evaluate(expression, doc, XPathConstants.NODE);
        XMLSignature signature = 
            new XMLSignature(sigElement, f.toURI().toURL().toString());

        signature.getSignedInfo().addResourceResolver(
            new org.apache.xml.security.samples.utils.resolver.OfflineResolver()
        );

        signature.setFollowNestedManifests(false);

        KeyInfo ki = signature.getKeyInfo();

        if (ki != null) {
            X509Certificate cert = signature.getKeyInfo().getX509Certificate();

            if (cert != null) {
                System.out.println("The XML signature in file "
                                   + f.toURI().toURL().toString() + " is "
                                   + (signature.checkSignatureValue(cert)
                                       ? "valid (good)" : "invalid !!!!! (bad)"));
            } else {
                PublicKey pk = signature.getKeyInfo().getPublicKey();

                if (pk != null) {
                    System.out.println("The XML signature in file "
                                       + f.toURI().toURL().toString() + " is "
                                       + (signature.checkSignatureValue(pk)
                                           ? "valid (good)" : "invalid !!!!! (bad)"));
                } else {
                    System.out.println(
                    "Did not find a public key, so I can't check the signature");
                }
            }
        } else {
            System.out.println("Did not find a KeyInfo");
        }
    }

}

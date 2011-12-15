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
import java.io.FileOutputStream;

import org.apache.xml.security.keys.content.KeyName;
import org.apache.xml.security.signature.SignedInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.transforms.params.XPathContainer;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Element;

/**
 * These ones can be used to create Signatures which can be collected
 * using your text editors cut-and-paste feature to create a file wich
 * contains multiple signatures which remain valid after cut-and-paste.
 *
 * This program creates a Signature which can be used for cut-and-paste to be
 * put into a larger document.
 *
 * @author $Author$
 */
public class CreateCollectableSignature {

    /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(CreateCollectableSignature.class.getName());

    /** Field passphrase */
    public static final String passphrase =
        "The super-mega-secret public static passphrase";
    
    static {
        org.apache.xml.security.Init.init();

        // org.apache.xml.security.utils.Constants.setSignatureSpecNSprefix("");
    }

    /**
     * Method main
     *
     * @param unused
     * @throws Exception
     */
    public static void main(String unused[]) throws Exception {
        File signatureFile = new File("build/collectableSignature.xml");
        String BaseURI = signatureFile.toURI().toURL().toString();
        javax.xml.parsers.DocumentBuilderFactory dbf =
            javax.xml.parsers.DocumentBuilderFactory.newInstance();

        dbf.setNamespaceAware(true);

        javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
        org.w3c.dom.Document doc = db.newDocument();
        Element rootElement = doc.createElementNS(null, "root");

        doc.appendChild(rootElement);

        Element signedResourceElement = doc.createElementNS(null, "signedContent");

        signedResourceElement.appendChild(doc.createTextNode("Signed Text\n"));
        rootElement.appendChild(signedResourceElement);

        XMLSignature sig = 
            new XMLSignature(doc, BaseURI, XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

        signedResourceElement.appendChild(sig.getElement());

        {
            String rootnamespace = signedResourceElement.getNamespaceURI();
            boolean rootprefixed = (rootnamespace != null) && (rootnamespace.length() > 0);
            String rootlocalname = signedResourceElement.getNodeName();
            Transforms transforms = new Transforms(doc);
            XPathContainer xpath = new XPathContainer(doc);

            xpath.setXPathNamespaceContext("ds", Constants.SignatureSpecNS);

            if (rootprefixed) {
                xpath.setXPathNamespaceContext("root", rootnamespace);
            }

            String xpathStr = "\n"
                + "count(                                                                 " + "\n"
                + " ancestor-or-self::" + (rootprefixed ? "root:" : "") + rootlocalname + "" + "\n"
                + " |                                                                     " + "\n"
                + " here()/ancestor::" + (rootprefixed ? "root:" : "") + rootlocalname + "[1] " + "\n"
                + ") <= count(                                                             " + "\n"
                + " ancestor-or-self::" + (rootprefixed ? "root:" : "") + rootlocalname + "" + "\n"
                + ")                                                                      " + "\n"
                + " and                                                                   " + "\n"
                + "count(                                                                 " + "\n"
                + " ancestor-or-self::ds:Signature                                        " + "\n"
                + " |                                                                     " + "\n"
                + " here()/ancestor::ds:Signature[1]                                      " + "\n"
                + ") > count(                                                             " + "\n"
                + " ancestor-or-self::ds:Signature                                        " + "\n"
                + ")                                                                      " + "\n"



                ;
            xpath.setXPath(xpathStr);
            transforms.addTransform(Transforms.TRANSFORM_XPATH,
                                    xpath.getElementPlusReturns());
            sig.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);
        }

        {
            sig.getKeyInfo().add(new KeyName(doc, CreateCollectableSignature.passphrase));
            System.out.println("Start signing");
            sig.sign(sig.createSecretKey(CreateCollectableSignature.passphrase.getBytes()));
            System.out.println("Finished signing");
        }

        FileOutputStream f = new FileOutputStream(signatureFile);

        XMLUtils.outputDOMc14nWithComments(doc, f);
        f.close();
        System.out.println("Wrote signature to " + BaseURI);

        SignedInfo s = sig.getSignedInfo();

        for (int i = 0; i < s.getSignedContentLength(); i++) {
            System.out.println("################ Signed Resource " + i
                               + " ################");
            System.out.println(new String(s.getSignedContentItem(i)));
            System.out.println();
        }
    }

}

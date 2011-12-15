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
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.apache.xml.security.signature.ObjectContainer;
import org.apache.xml.security.signature.SignedInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.transforms.params.XPathContainer;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.ElementProxy;
import org.apache.xml.security.utils.HelperNodeList;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author $Author$
 */
public class HereSigner {

    /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(HereSigner.class.getName());

    static {
        org.apache.xml.security.Init.init();
    }
    
    /**
     * Method main
     *
     * @param unused
     * @throws Exception
     */
    public static void main(String unused[]) throws Exception {
        String keystoreType = "JKS";
        String keystoreFile = "samples/data/keystore.jks";
        String keystorePass = "xmlsecurity";
        String privateKeyAlias = "test";
        String privateKeyPass = "xmlsecurity";
        String certificateAlias = "test";
        File signatureFile = new File("build/hereSignature.xml");

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

        String BaseURI = signatureFile.toURI().toURL().toString();
        ElementProxy.setDefaultPrefix(Constants.SignatureSpecNS, "prof");
        XMLSignature sig = 
            new XMLSignature(doc, BaseURI, XMLSignature.ALGO_ID_SIGNATURE_DSA);

        doc.appendChild(sig.getElement());
        sig.getSignedInfo()
            .addResourceResolver(new org.apache.xml.security.samples.utils.resolver.OfflineResolver());

        {
            ObjectContainer ob1 = new ObjectContainer(doc);
            ob1.setId("object-1");
            ob1.appendChild(doc.createTextNode("\nSigned Text\n"));
            Element c = doc.createElementNS(null, "element");
            c.setAttributeNS(null, "name", "val");
            ob1.appendChild(c);
            sig.appendObject(ob1);

            Transforms transforms = new Transforms(doc);
            XPathContainer xc = new XPathContainer(doc);
            xc.setXPathNamespaceContext("prof", Constants.SignatureSpecNS);

            String xpath = "\n"
                + "count(" + "\n"
                + " ancestor-or-self::prof:Object " + "\n"
                + " | " + "\n"
                + " here()/ancestor::prof:Signature[1]/child::prof:Object[@Id='object-1']" + "\n"
                + ") <= count(" + "\n"
                + " ancestor-or-self::prof:Object" + "\n"
                + ") " + "\n";

            xc.setXPath(xpath);
            HelperNodeList nl = new HelperNodeList();
            nl.appendChild(doc.createTextNode("\n"));
            nl.appendChild(xc.getElement());
            nl.appendChild(doc.createTextNode("\n"));

            transforms.addTransform(Transforms.TRANSFORM_XPATH, nl);
            transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);
            sig.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);
        }

        {
            X509Certificate cert =
                (X509Certificate) ks.getCertificate(certificateAlias);

            sig.addKeyInfo(cert);
            sig.addKeyInfo(cert.getPublicKey());
            System.out.println("Start signing");
            sig.sign(privateKey);
            System.out.println("Finished signing");
        }

        SignedInfo s = sig.getSignedInfo();
        for (int i = 0; i < s.getSignedContentLength(); i++) {
            System.out.println(new String(s.getSignedContentItem(i)));
        }

        FileOutputStream f = new FileOutputStream(signatureFile);

        XMLUtils.outputDOMc14nWithComments(doc, f);

        f.close();
        System.out.println("Wrote signature to " + BaseURI);
    }

}

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
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author $Author$
 */
public class CreateEnvelopingSignature {

    /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(CreateSignature.class.getName());

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
        File signatureFile = new File("build/signature.xml");

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
        XMLSignature sig = 
            new XMLSignature(doc, BaseURI, XMLSignature.ALGO_ID_SIGNATURE_DSA);

        doc.appendChild(sig.getElement());

        {
            ObjectContainer obj = new ObjectContainer(doc);
            Element anElement = doc.createElementNS(null, "InsideObject");

            anElement.appendChild(doc.createTextNode("A text in a box"));
            obj.appendChild(anElement);

            String Id = "TheFirstObject";

            obj.setId(Id);
            sig.appendObject(obj);

            Transforms transforms = new Transforms(doc);

            transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);
            sig.addDocument("#" + Id, transforms, Constants.ALGO_ID_DIGEST_SHA1);
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

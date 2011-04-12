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


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.signature.ObjectContainer;
import org.apache.xml.security.signature.SignatureProperties;
import org.apache.xml.security.signature.SignatureProperty;
import org.apache.xml.security.signature.XMLSignature;
import org.w3c.dom.Document;

/**
 * Class SignaturePropertiesSample
 *
 * @author $Author$
 */
public class SignaturePropertiesSample {

    /**
     * Method main
     *
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {

        org.apache.xml.security.Init.init();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        dbf.setNamespaceAware(true);

        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();
        XMLSignature sig = new XMLSignature(doc, null, XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

        doc.appendChild(sig.getElement());

        SignatureProperty prop1 = 
            new SignatureProperty(doc, "http://www.xmlsecurity.org/#target", "prop1");

        prop1.getElement().appendChild(doc.createTextNode("\n   some data for this property\n"));

        SignatureProperties props = new SignatureProperties(doc);

        props.addSignatureProperty(prop1);

        ObjectContainer object = new ObjectContainer(doc);

        object.appendChild(doc.createTextNode("\n"));
        object.appendChild(props.getElement());
        object.appendChild(doc.createTextNode("\n"));
        sig.appendObject(object);
        sig.addDocument("#prop1");

        String secretKey = "secret";

        sig.getKeyInfo().addKeyName("The UTF-8 octets of \"" + secretKey
                                    + "\" are used for signing ("
                                    + secretKey.length() + " octets)");
        sig.sign(sig.createSecretKey(secretKey.getBytes()));

        Canonicalizer c14n =
            Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);

        System.out.println("---------------------------------------");
        System.out.println(new String(c14n.canonicalizeSubtree(doc)));
        System.out.println("---------------------------------------");
        System.out.println(new String(sig.getSignedInfo().item(0).getTransformsOutput().getBytes()));
        System.out.println("---------------------------------------");
    }
    
}

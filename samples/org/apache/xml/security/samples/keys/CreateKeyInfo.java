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
package org.apache.xml.security.samples.keys;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.content.X509Data;
import org.apache.xml.security.keys.content.keyvalues.RSAKeyValue;
import org.apache.xml.security.utils.XMLUtils;

/**
 * Class CreateKeyInfo
 *
 * @author $Author$
 * @version $Revision$
 */
public class CreateKeyInfo {

    /**
     * Method main
     *
     * @param unused
     * @throws Exception
     */
    public static void main(String unused[]) throws Exception {

        KeyStore ks = KeyStore.getInstance("JKS");
        FileInputStream fis = new FileInputStream(
            "samples/data/keystore.jks");

        ks.load(fis, "xmlsecurity".toCharArray());

        javax.xml.parsers.DocumentBuilderFactory dbf =
            javax.xml.parsers.DocumentBuilderFactory.newInstance();

        dbf.setNamespaceAware(true);

        javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
        org.w3c.dom.Document doc = db.newDocument();
        KeyInfo ki = new KeyInfo(doc);

        doc.appendChild(ki.getElement());
        ki.setId("myKI");
        ki.addKeyName("A simple key");

        X509Certificate cert = (X509Certificate) ks.getCertificate("test");

        ki.addKeyValue(cert.getPublicKey());

        X509Data x509Data = new X509Data(doc);

        ki.add(x509Data);
        x509Data.addCertificate(cert);
        x509Data.addSubjectName("Subject name");
        x509Data.addIssuerSerial("Subject nfsdfhs", 6786);
        ki.add(new RSAKeyValue(doc, new BigInteger("678"), new BigInteger("6870")));
        XMLUtils.outputDOMc14nWithComments(doc, System.out);
    }
    
}

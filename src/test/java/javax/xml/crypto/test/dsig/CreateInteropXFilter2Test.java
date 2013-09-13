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
/*
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 */
package javax.xml.crypto.test.dsig;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.util.*;

import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.*;
import javax.xml.crypto.dsig.keyinfo.*;
import javax.xml.crypto.dsig.spec.*;

import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.*;

import javax.xml.crypto.test.KeySelectors;
import javax.xml.parsers.DocumentBuilder;

/**
 * Test that recreates merlin-xpath-filter2-three test vectors
 * but with different keys and X.509 data.
 *
 * @author Sean Mullan
 */
public class CreateInteropXFilter2Test extends org.junit.Assert {

    private XMLSignatureFactory fac;
    private KeyInfoFactory kifac;
    private DocumentBuilder db;
    private KeyStore ks;
    private Key signingKey;
    private PublicKey validatingKey;
    private Certificate signingCert;

    static {
        Security.insertProviderAt
            (new org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI(), 1);
    }

    public CreateInteropXFilter2Test() throws Exception {
        fac = XMLSignatureFactory.getInstance
            ("DOM", new org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI());
        kifac = fac.getKeyInfoFactory();
        db = XMLUtils.createDocumentBuilder(false);

        // get key & self-signed certificate from keystore
        String fs = System.getProperty("file.separator");
        String base = System.getProperty("basedir") == null ? "./": System.getProperty("basedir");
        
        FileInputStream fis = new FileInputStream
            (base + fs + "src/test/resources" + fs + "test.jks");
        ks = KeyStore.getInstance("JKS");
        ks.load(fis, "changeit".toCharArray());
        signingKey = ks.getKey("mullan", "changeit".toCharArray());
        signingCert = ks.getCertificate("mullan");
        validatingKey = signingCert.getPublicKey();
    }

    @org.junit.Test
    public void test_create_sign_spec() throws Exception {
        List<Reference> refs = new ArrayList<Reference>(2);

        // create reference 1
        List<XPathType> types = new ArrayList<XPathType>(3);
        types.add(new XPathType(" //ToBeSigned ", XPathType.Filter.INTERSECT));
        types.add(new XPathType(" //NotToBeSigned ", XPathType.Filter.SUBTRACT));
        types.add(new XPathType(" //ReallyToBeSigned ", XPathType.Filter.UNION));
        XPathFilter2ParameterSpec xp1 = new XPathFilter2ParameterSpec(types);
        refs.add(fac.newReference
            ("", fac.newDigestMethod(DigestMethod.SHA1, null),
             Collections.singletonList(fac.newTransform(Transform.XPATH2, xp1)),
             null, null));

        // create reference 2
        List<Transform> trans2 = new ArrayList<Transform>(2);
        trans2.add(fac.newTransform(Transform.ENVELOPED, 
            (TransformParameterSpec) null));
        XPathFilter2ParameterSpec xp2 = new XPathFilter2ParameterSpec
            (Collections.singletonList
                (new XPathType(" / ", XPathType.Filter.UNION)));
        trans2.add(fac.newTransform(Transform.XPATH2, xp2));
        refs.add(fac.newReference("#signature-value", 
            fac.newDigestMethod(DigestMethod.SHA1, null), trans2, null, null));
                
        // create SignedInfo
        SignedInfo si = fac.newSignedInfo(
            fac.newCanonicalizationMethod
                (CanonicalizationMethod.INCLUSIVE, 
                 (C14NMethodParameterSpec) null),
            fac.newSignatureMethod(SignatureMethod.DSA_SHA1, null), refs);

        // create KeyInfo
        List<Object> kits = new ArrayList<Object>(2);
        kits.add(kifac.newKeyValue(validatingKey));
        List<Object> xds = new ArrayList<Object>(2);
        xds.add("CN=Sean Mullan, DC=sun, DC=com");
        xds.add(signingCert);
        kits.add(kifac.newX509Data(xds));
        KeyInfo ki = kifac.newKeyInfo(kits);

        // create XMLSignature
        XMLSignature sig = fac.newXMLSignature
            (si, ki, null, null, "signature-value");

        Document doc = db.newDocument();
        Element tbs1 = doc.createElementNS(null, "ToBeSigned");
        Comment tbs1Com = doc.createComment(" comment ");
        Element tbs1Data = doc.createElementNS(null, "Data");
        Element tbs1ntbs = doc.createElementNS(null, "NotToBeSigned");
        Element tbs1rtbs = doc.createElementNS(null, "ReallyToBeSigned");
        Comment tbs1rtbsCom = doc.createComment(" comment ");
        Element tbs1rtbsData = doc.createElementNS(null, "Data");
        tbs1rtbs.appendChild(tbs1rtbsCom);
        tbs1rtbs.appendChild(tbs1rtbsData);
        tbs1ntbs.appendChild(tbs1rtbs);
        tbs1.appendChild(tbs1Com);
        tbs1.appendChild(tbs1Data);
        tbs1.appendChild(tbs1ntbs);

        Element tbs2 = doc.createElementNS(null, "ToBeSigned");
        Element tbs2Data = doc.createElementNS(null, "Data");
        Element tbs2ntbs = doc.createElementNS(null, "NotToBeSigned");
        Element tbs2ntbsData = doc.createElementNS(null, "Data");
        tbs2ntbs.appendChild(tbs2ntbsData);
        tbs2.appendChild(tbs2Data);
        tbs2.appendChild(tbs2ntbs);

        Element document = doc.createElementNS(null, "Document");
        document.appendChild(tbs1);
        document.appendChild(tbs2);
        doc.appendChild(document);

        DOMSignContext dsc = new DOMSignContext(signingKey, document);

        sig.sign(dsc);
        TestUtils.validateSecurityOrEncryptionElement(document.getLastChild());

        DOMValidateContext dvc = new DOMValidateContext
            (new KeySelectors.KeyValueKeySelector(), document.getLastChild());
        XMLSignature sig2 = fac.unmarshalXMLSignature(dvc);

        assertTrue(sig.equals(sig2));

        assertTrue(sig2.validate(dvc));
    }

}

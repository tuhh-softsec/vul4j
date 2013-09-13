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

import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.*;
import javax.xml.crypto.dsig.keyinfo.*;
import javax.xml.crypto.dsig.spec.*;

import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.*;

import javax.xml.crypto.test.KeySelectors;
import javax.xml.parsers.DocumentBuilder;

/**
 * Test that recreates interop exc C14N test vectors
 * but with different keys.
 *
 * @author Sean Mullan
 */
public class CreateInteropExcC14NTest extends org.junit.Assert {

    private XMLSignatureFactory fac;
    private KeyInfoFactory kifac;
    private DocumentBuilder db;
    private KeyStore ks;
    private Key signingKey;
    private PublicKey validatingKey;

    static {
        Security.insertProviderAt
            (new org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI(), 1);
    }

    public CreateInteropExcC14NTest() throws Exception {
        fac = XMLSignatureFactory.getInstance
            ("DOM", new org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI());
        kifac = fac.getKeyInfoFactory();
        db = XMLUtils.createDocumentBuilder(false);

        // get key & self-signed certificate from keystore
        String base = System.getProperty("basedir") == null ? "./": System.getProperty("basedir");
        
        String fs = System.getProperty("file.separator");
        FileInputStream fis = new FileInputStream
            (base + fs + "src/test/resources" + fs + "test.jks");
        ks = KeyStore.getInstance("JKS");
        ks.load(fis, "changeit".toCharArray());
        Certificate signingCert = ks.getCertificate("mullan");
        signingKey = ks.getKey("mullan", "changeit".toCharArray());
        validatingKey = signingCert.getPublicKey();
    }

    @org.junit.Test
    public void test_create_Y1() throws Exception {
        List<Reference> refs = new ArrayList<Reference>(4);

        // create reference 1
        refs.add(fac.newReference
            ("#xpointer(id('to-be-signed'))", 
             fac.newDigestMethod(DigestMethod.SHA1, null),
             Collections.singletonList
                (fac.newTransform(CanonicalizationMethod.EXCLUSIVE, 
                 (TransformParameterSpec) null)),
             null, null));

        // create reference 2
        List<String> prefixList = new ArrayList<String>(2);
        prefixList.add("bar");
        prefixList.add("#default");
        ExcC14NParameterSpec params = new ExcC14NParameterSpec(prefixList);
        refs.add(fac.newReference
            ("#xpointer(id('to-be-signed'))", 
             fac.newDigestMethod(DigestMethod.SHA1, null),
             Collections.singletonList
                (fac.newTransform(CanonicalizationMethod.EXCLUSIVE, params)),
             null, null));

        // create reference 3
        refs.add(fac.newReference
            ("#xpointer(id('to-be-signed'))", 
             fac.newDigestMethod(DigestMethod.SHA1, null),
             Collections.singletonList(fac.newTransform
                (CanonicalizationMethod.EXCLUSIVE_WITH_COMMENTS, 
                 (TransformParameterSpec) null)),
             null, null));

        // create reference 4
        prefixList = new ArrayList<String>(2);
        prefixList.add("bar");
        prefixList.add("#default");
        params = new ExcC14NParameterSpec(prefixList);
        refs.add(fac.newReference
            ("#xpointer(id('to-be-signed'))", 
             fac.newDigestMethod(DigestMethod.SHA1, null),
             Collections.singletonList(fac.newTransform
                (CanonicalizationMethod.EXCLUSIVE_WITH_COMMENTS, 
                 params)),
             null, null));

        // create SignedInfo
        SignedInfo si = fac.newSignedInfo(
            fac.newCanonicalizationMethod
                (CanonicalizationMethod.EXCLUSIVE, 
                 (C14NMethodParameterSpec) null),
            fac.newSignatureMethod(SignatureMethod.DSA_SHA1, null), refs);

        // create KeyInfo
        List<KeyValue> kits = new ArrayList<KeyValue>(2);
        kits.add(kifac.newKeyValue(validatingKey));
        KeyInfo ki = kifac.newKeyInfo(kits);

        // create Objects
        Document doc = db.newDocument();
        Element baz = doc.createElementNS("urn:bar", "bar:Baz");
        Comment com = doc.createComment(" comment ");
        baz.appendChild(com);
        XMLObject obj = fac.newXMLObject(Collections.singletonList
            (new DOMStructure(baz)), "to-be-signed", null, null);

        // create XMLSignature
        XMLSignature sig = fac.newXMLSignature
            (si, ki, Collections.singletonList(obj), null, null);

        Element foo = doc.createElementNS("urn:foo", "Foo");
        foo.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "urn:foo");
        foo.setAttributeNS
            ("http://www.w3.org/2000/xmlns/", "xmlns:bar", "urn:bar");
        doc.appendChild(foo);

        DOMSignContext dsc = new DOMSignContext(signingKey, foo);
        dsc.putNamespacePrefix(XMLSignature.XMLNS, "dsig");

        sig.sign(dsc);
        TestUtils.validateSecurityOrEncryptionElement(foo.getLastChild());
        
        DOMValidateContext dvc = new DOMValidateContext
            (new KeySelectors.KeyValueKeySelector(), foo.getLastChild());
        XMLSignature sig2 = fac.unmarshalXMLSignature(dvc);

        assertTrue(sig.equals(sig2));

        assertTrue(sig2.validate(dvc));
    }

}

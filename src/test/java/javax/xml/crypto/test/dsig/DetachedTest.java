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

import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.*;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;

import java.security.*;
import java.util.*;

import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;

/**
 * This is a simple example of generating and validating a Detached XML 
 * Signature using the JSR 105 API. The resulting signature will look 
 * like (key and signature values will be different):
 *
 * <pre><code>
 * <Signature xmlns="http://www.w3.org/2000/09/xmldsig#">
 *   <SignedInfo>
 *     <CanonicalizationMethod Algorithm="http://www.w3.org/TR/2001/REC-xml-c14n-20010315"/>
 *     <SignatureMethod Algorithm="http://www.w3.org/2000/09/xmldsig#dsa-sha1"/>
 *     <Reference URI="http://www.w3.org/TR/xml-stylesheet">
 *       <DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1"/>
 *       <DigestValue>60NvZvtdTB+7UnlLp/H24p7h4bs=</DigestValue>
 *     </Reference>
 *   </SignedInfo>
 *   <SignatureValue>
 *     DpEylhQoiUKBoKWmYfajXO7LZxiDYgVtUtCNyTgwZgoChzorA2nhkQ==
 *   </SignatureValue>
 *   <KeyInfo>
 *     <KeyValue>
 *       <DSAKeyValue>
 *	   <P>
 *           rFto8uPQM6y34FLPmDh40BLJ1rVrC8VeRquuhPZ6jYNFkQuwxnu/wCvIAMhukPBL
 *           FET8bJf/b2ef+oqxZajEb+88zlZoyG8g/wMfDBHTxz+CnowLahnCCTYBp5kt7G8q
 *           UobJuvjylwj1st7V9Lsu03iXMXtbiriUjFa5gURasN8=
 *         </P>
 *         <Q>
 *           kEjAFpCe4lcUOdwphpzf+tBaUds=
 *         </Q>
 *         <G>
 *           oe14R2OtyKx+s+60O5BRNMOYpIg2TU/f15N3bsDErKOWtKXeNK9FS7dWStreDxo2
 *           SSgOonqAd4FuJ/4uva7GgNL4ULIqY7E+mW5iwJ7n/WTELh98mEocsLXkNh24HcH4
 *           BZfSCTruuzmCyjdV1KSqX/Eux04HfCWYmdxN3SQ/qqw=
 *         </G>
 *         <Y>
 *           pA5NnZvcd574WRXuOA7ZfC/7Lqt4cB0MRLWtHubtJoVOao9ib5ry4rTk0r6ddnOv
 *           AIGKktutzK3ymvKleS3DOrwZQgJ+/BDWDW8kO9R66o6rdjiSobBi/0c2V1+dkqOg
 *           jFmKz395mvCOZGhC7fqAVhHat2EjGPMfgSZyABa7+1k=
 *         </Y>
 * 	 </KeyValue>
 *     </DSAKeyValue>
 *   </KeyInfo>
 * </Signature>
 *
 * @author Sean Mullan
 */
public class DetachedTest extends org.junit.Assert {

    static {
        Security.insertProviderAt
            (new org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI(), 1);
    }

    public DetachedTest() {
        //
    }
    
    @org.junit.Test
    public void test() {
        try {
            //
            // PART 1 : Creating the detached signature
            //
    
            // Create a factory that will be used to generate the signature 
            // structures
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance
                ("DOM", new org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI());
    
            // Create a Reference to an external URI that will be digested
            Reference ref = fac.newReference
                ("http://www.w3.org/TR/xml-stylesheet", 
                fac.newDigestMethod(DigestMethod.SHA1, null));
    
            // Create a DSA KeyPair
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
            kpg.initialize(1024, 
                new SecureRandom("not so random bytes".getBytes()));
            KeyPair kp = kpg.generateKeyPair();
    
            // Create a KeyValue containing the generated DSA PublicKey
            KeyInfoFactory kif = fac.getKeyInfoFactory();
            KeyValue kv = kif.newKeyValue(kp.getPublic());
    
            // Create a KeyInfo and add the KeyValue to it
            KeyInfo ki = kif.newKeyInfo(Collections.singletonList(kv));

            // Create SignedInfo
            SignedInfo si = fac.newSignedInfo(fac.newCanonicalizationMethod(
                CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS, 
                (C14NMethodParameterSpec) null), 
                fac.newSignatureMethod(SignatureMethod.DSA_SHA1, null), 
                Collections.singletonList(ref));

            // Create XMLSignature
            XMLSignature signature = fac.newXMLSignature(si,ki,null,null,null);
    
            // Create an XMLSignContext and set the 
            // DSA PrivateKey for signing
            Document doc = XMLUtils.createDocumentBuilder(false).newDocument();
            DOMSignContext signContext = new DOMSignContext(kp.getPrivate(), doc);
            signContext.putNamespacePrefix(XMLSignature.XMLNS, "ds");

            URIDereferencer ud = new LocalHttpCacheURIDereferencer();
            signContext.setURIDereferencer(ud);

            // Generate (and sign) the XMLSignature
            signature.sign(signContext);
            TestUtils.validateSecurityOrEncryptionElement(doc.getDocumentElement());
    
            //
            // PART 2 : Validating the detached signature
            //
    
            // Create a XMLValidateContext & set the DSAPublicKey for validating
            XMLValidateContext vc = new DOMValidateContext(kp.getPublic(),
                doc.getDocumentElement());
            vc.setURIDereferencer(ud);
    
            // Validate the Signature (generated above)
            boolean coreValidity = signature.validate(vc); 
    
            // Check core validation status
            if (coreValidity == false) {
                // check the validation status of each Reference
                Iterator<?> i = signature.getSignedInfo().getReferences().iterator();
                while (i.hasNext()) {
                    Reference reference = (Reference) i.next();
                    reference.validate(vc);
                }
                fail("Signature failed core validation");
            }
    
            // You can also validate an XML Signature which is in XML format.
            // Unmarshal and validate an XMLSignature from a DOMValidateContext
            signature = fac.unmarshalXMLSignature(vc);
            coreValidity = signature.validate(vc);
            assertTrue("Core validity of unmarshalled XMLSignature is false", coreValidity);
        } catch (Exception ex) {
            fail("Exception: " + ex);
        }
    }

}

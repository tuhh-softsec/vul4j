/*
 * Copyright 2006-2009 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
/*
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 */
package javax.xml.crypto.test.dsig;

import java.security.*;
import java.util.*;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.*;
import javax.xml.crypto.dsig.spec.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

import junit.framework.*;

import javax.xml.crypto.test.KeySelectors;

/**
 * Test that recreates Phaos XMLDSig-3 test vectors
 * but with different keys. For now we are just focusing on
 * the exc-c14n vectors.
 *
 * @author Sean Mullan
 */
public class CreatePhaosXMLDSig3Test extends TestCase {

    private XMLSignatureFactory fac;
    private DocumentBuilder db;

    static {
        Security.insertProviderAt
            (new org.jcp.xml.dsig.internal.dom.XMLDSigRI(), 1);
    }

    public CreatePhaosXMLDSig3Test(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        fac = XMLSignatureFactory.getInstance
            ("DOM", new org.jcp.xml.dsig.internal.dom.XMLDSigRI());
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        db = dbf.newDocumentBuilder();
    }

    public void test_create_hmac_sha1_exclusive_c14n_comments_detached() throws Exception {
        test_create_hmac_sha1_exclusive_c14n_comments_detached(false);
    } 

    public void test_create_hmac_sha1_40_exclusive_c14n_comments_detached() 
	throws Exception {
        try {
            test_create_hmac_sha1_exclusive_c14n_comments_detached(true);
            fail("Expected HMACOutputLength Exception");
        } catch (XMLSignatureException xse) {
            System.out.println(xse.getMessage());
            // pass
        }
    } 

    private void test_create_hmac_sha1_exclusive_c14n_comments_detached(boolean fortyBit) 
	throws Exception {

	// create reference
	Reference ref = fac.newReference
	    ("http://www.ietf.org/rfc/rfc3161.txt",
	     fac.newDigestMethod(DigestMethod.SHA1, null));

        // create SignedInfo
	HMACParameterSpec spec = null;
	if (fortyBit) {
	    spec = new HMACParameterSpec(40);
        }
	    
        SignedInfo si = fac.newSignedInfo(
	    fac.newCanonicalizationMethod
	        (CanonicalizationMethod.EXCLUSIVE_WITH_COMMENTS, 
		 (C14NMethodParameterSpec) null),
	    fac.newSignatureMethod(SignatureMethod.HMAC_SHA1, spec), 
	    Collections.singletonList(ref));

	// create XMLSignature
	XMLSignature sig = fac.newXMLSignature(si, null);

	Document doc = db.newDocument();
        DOMSignContext dsc = new DOMSignContext
            (new KeySelectors.SecretKeySelector
	     ("test".getBytes("ASCII")), doc);
	dsc.putNamespacePrefix(XMLSignature.XMLNS, "dsig");

        sig.sign(dsc);

//	dumpDocument(doc, new FileWriter("/tmp/foo.xml"));

        DOMValidateContext dvc = new DOMValidateContext
            (new KeySelectors.SecretKeySelector
	     ("test".getBytes("ASCII")), doc);

        XMLSignature sig2 = fac.unmarshalXMLSignature(dvc);

        assertTrue(sig.equals(sig2));

        assertTrue(sig2.validate(dvc));
    }

    public void test_create_hmac_sha1_exclusive_c14n_enveloped() throws Exception {

	// create reference
	Reference ref = fac.newReference("",
	    fac.newDigestMethod(DigestMethod.SHA1, null),
	    Collections.singletonList(fac.newTransform(Transform.ENVELOPED, 
	    (TransformParameterSpec) null)),
	    null, null);

        // create SignedInfo
        SignedInfo si = fac.newSignedInfo(
	    fac.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE, 
		(C14NMethodParameterSpec) null),
	    fac.newSignatureMethod(SignatureMethod.HMAC_SHA1, null), 
	    Collections.singletonList(ref));

	// create XMLSignature
	XMLSignature sig = fac.newXMLSignature(si, null);

	Document doc = db.newDocument();
	Element player = doc.createElementNS(null, "player");
	player.setAttributeNS(null, "bats", "left");
	player.setAttributeNS(null, "id", "10012");
	player.setAttributeNS(null, "throws", "right");
	Element name = doc.createElementNS(null, "name");
	name.appendChild(doc.createTextNode("Alfonso Soriano"));
	Element position = doc.createElementNS(null, "position");
	position.appendChild(doc.createTextNode("2B"));
	Element team = doc.createElementNS(null, "team");
	team.appendChild(doc.createTextNode("New York Yankees"));
	player.appendChild(doc.createComment(" Here's a comment "));
	player.appendChild(name);
	player.appendChild(position);
	player.appendChild(team);
	doc.appendChild(player);

        DOMSignContext dsc = new DOMSignContext
            (new KeySelectors.SecretKeySelector
	     ("test".getBytes("ASCII")), player);
	dsc.putNamespacePrefix(XMLSignature.XMLNS, "dsig");

        sig.sign(dsc);

//	dumpDocument(doc, new FileWriter("/tmp/foo.xml"));

        DOMValidateContext dvc = new DOMValidateContext
            (new KeySelectors.SecretKeySelector
	     ("test".getBytes("ASCII")), player.getLastChild());

        XMLSignature sig2 = fac.unmarshalXMLSignature(dvc);

        assertTrue(sig.equals(sig2));

        assertTrue(sig2.validate(dvc));
    }

}

/*
 * Copyright 2006 The Apache Software Foundation.
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

import java.io.*;
import java.security.MessageDigest;
import java.security.Security;
import java.util.*;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.keyinfo.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import org.w3c.dom.Document;

import junit.framework.*;

/**
 * Unit test for javax.xml.crypto.dsig.Reference
 *
 * @version $Id$
 * @author Valerie Peng
 */
public class ReferenceTest extends TestCase {
    private XMLSignatureFactory fac;
    private KeyInfoFactory kifac;
    private DigestMethod dmSHA1;
    private String uri = "http://www.ietf.org/rfc/rfc3275.txt";

    private static final String[] CRYPTO_ALGS = { "RSA", "DSA" };
    private static final String[] SIG_ALGS = { 
	SignatureMethod.RSA_SHA1, 
	SignatureMethod.DSA_SHA1 
    };

    static {
        Security.insertProviderAt
            (new org.jcp.xml.dsig.internal.dom.XMLDSigRI(), 1);
    }

    public ReferenceTest() {
	super("ReferenceTest");
    }

    public ReferenceTest(String name) {
	super(name);
    }

    public void setUp() throws Exception {
        fac = XMLSignatureFactory.getInstance
            ("DOM", new org.jcp.xml.dsig.internal.dom.XMLDSigRI());
	kifac = KeyInfoFactory.getInstance
            ("DOM", new org.jcp.xml.dsig.internal.dom.XMLDSigRI());
	dmSHA1 = fac.newDigestMethod(DigestMethod.SHA1, null); 
    }

    public void tearDown() {}

    public void testConstructor() throws Exception {
	Reference ref;
	// test XMLSignatureFactory.newReference(String uri, 
	//     DigestMethod dm) for generating Reference objects
	ref = fac.newReference(null, dmSHA1);
	assertNotNull(ref);
	ref = fac.newReference(uri, dmSHA1);
	assertNotNull(ref);
	try {
	    ref = fac.newReference("illegal!@#$%" + uri, dmSHA1);
	    fail("Should throw a IAE for non-RFC2396-compliant uri");
	} catch (IllegalArgumentException iae) {
	} catch (Exception ex) {
	    fail("Should throw a IAE instead of " + ex + 
		 " for non-RFC2396-compliant uri");
	}
	try {
	    ref = fac.newReference(uri, null);
	    fail("Should throw a NPE for null dm");
	} catch (NullPointerException npe) {
	} catch (Exception ex) {
	    fail("Should throw a NPE instead of " + ex + " for null dm");
	}

	// test XMLSignatureFactory.newReference(String uri, 
	//    DigestMethod dm, List transforms, String type, String id)
	// for generating Reference objects
	try {   
	    ref = fac.newReference(null, dmSHA1, null, null, null);
	    assertEquals(ref.getDigestMethod(), dmSHA1);
	} catch(Exception ex) {
	    fail("Unexpected Exception: " + ex);
	}

	try {
	    ref = fac.newReference(null, null, null, null, null);
	    fail("Should throw a NPE for null dm");
	} catch (NullPointerException npe) {
	} catch(Exception ex) {
	    fail("Should throw a NPE instead of " + ex + " for null dm");
	}
	String id  = "id";
	String type = "type";
	try {
	    ref = fac.newReference(uri, dmSHA1, null, type, id);
	    assertNotNull(ref.getDigestMethod());
	    assertEquals(uri, ref.getURI());
	    assertEquals(id, ref.getId());
	    assertEquals(type, ref.getType());        
        assertEquals(ref.getTransforms(), Collections.EMPTY_LIST);
	    
	} catch(Exception ex) {
	    fail("Unexpected Exception: " + ex);
	}
	List transforms = new Vector();
	try {
	    // try empty transforms list
	    ref = fac.newReference(uri, dmSHA1, transforms, 
				   type, id);
	    assertTrue(Arrays.equals(transforms.toArray(), 
				     ref.getTransforms().toArray()));
	} catch(Exception ex) {
	    fail("Unexpected Exception: " + ex);
	}
	transforms.add(new Object());
	try {
	    // try a transforms list with an invalid object
	    ref = fac.newReference(uri, dmSHA1, transforms, 
				   type, id);
	} catch (ClassCastException cce) {
	} catch (Exception ex) {
	    fail("Should throw a ClassCastException instead of " + ex);
	}
	// Test with various composition of Transform list 
	// 1. String only 
	transforms.clear();
	transforms.add(Transform.BASE64);
	try {
	    // try a transforms list with a String object
	    ref = fac.newReference(uri, dmSHA1, transforms, 
	    			   type, id);
	    fail("Should throw a CCE for illegal transforms");
	} catch (ClassCastException cce) {
	} catch(Exception ex) {
	    fail("Should throw a CCE instead of " + ex + 
		 " for illegal transforms");
	}
	// 2. Transform only
	transforms.clear();
	Transform c14nWithComments = fac.newTransform
	    (CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS, 
	     (TransformParameterSpec) null);
	transforms.add(c14nWithComments);
	try {
	    // try a transforms list with a Transform object
	    ref = fac.newReference(uri, dmSHA1, transforms, type, id);
	    assertTrue(Arrays.equals(transforms.toArray(), 
				     ref.getTransforms().toArray()));
	} catch (Exception ex) {
	    fail("Unexpected Exception: " + ex);
	}
    }
    public void testisFeatureSupported() throws Exception {
	Reference ref = fac.newReference(null, dmSHA1, null, null, null);
	try {
	    ref.isFeatureSupported(null); 
	    fail("Should raise a NPE for null feature"); 
	} catch (NullPointerException npe) {}
	    
	assertTrue(!ref.isFeatureSupported("not supported"));
    }

    public void testvalidate() throws Exception {
        testvalidate(false);
    }

    public void testvalidateWithCaching() throws Exception {
        testvalidate(true);
    }

    private void testvalidate(boolean cache) throws Exception {
	Reference ref = null;
	String type = "http://www.w3.org/2000/09/xmldsig#Object";
	byte[] in = new byte[200];
	Random rand = new Random();
	
	// Test XMLSignContext
	XMLSignContext signContext;
	XMLValidateContext validateContext;
	for (int i = 0; i < CRYPTO_ALGS.length; i++) {
	    rand.nextBytes(in);
            URIDereferencer dereferrer =
                new TestUtils.OctetStreamURIDereferencer(in);
	    Document doc = TestUtils.newDocument();
	    signContext = new 
		DOMSignContext(TestUtils.getPrivateKey(CRYPTO_ALGS[i]), doc);
	    signContext.setURIDereferencer(dereferrer);
            if (cache) {
                signContext.setProperty
                    ("javax.xml.crypto.dsig.cacheReference", Boolean.TRUE);
            }
	    ref = fac.newReference(null, dmSHA1, null, type, null);
	    XMLSignature sig = fac.newXMLSignature(fac.newSignedInfo
		(fac.newCanonicalizationMethod
		 (CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS, 
		  (C14NMethodParameterSpec) null),
	    	fac.newSignatureMethod(SIG_ALGS[i], null),
		Collections.singletonList(ref)),
		kifac.newKeyInfo(Collections.singletonList
		(kifac.newKeyValue(TestUtils.getPublicKey(CRYPTO_ALGS[i])))));
	    try {
		sig.sign(signContext);
         	if (!cache) {
                    assertNull(ref.getDereferencedData());
                    assertNull(ref.getDigestInputStream());
                } else {
                    assertNotNull(ref.getDereferencedData());
                    assertNotNull(ref.getDigestInputStream());
                    assertTrue(digestInputEqual(ref));
                }
	        validateContext = new DOMValidateContext
		    (TestUtils.getPublicKey(CRYPTO_ALGS[i]), 
		    doc.getDocumentElement());
	        validateContext.setURIDereferencer(dereferrer);

                if (cache) {
                    validateContext.setProperty
                        ("javax.xml.crypto.dsig.cacheReference", Boolean.TRUE);
                }
		boolean result = sig.validate(validateContext);
		assertTrue(result);
     
		Iterator iter = sig.getSignedInfo().getReferences().iterator();
		for (int j=0; iter.hasNext(); j++) {
		    Reference validated_ref = (Reference) iter.next();
                    if (!cache) {
                        assertNull(validated_ref.getDereferencedData());
                        assertNull(validated_ref.getDigestInputStream());
                    } else {
                        assertNotNull(validated_ref.getDereferencedData());
                        assertNotNull(validated_ref.getDigestInputStream());
                        assertTrue(digestInputEqual(validated_ref));
                    }
		    byte[] dv =  validated_ref.getDigestValue();
                    byte[] cdv = validated_ref.getCalculatedDigestValue();
                    assertTrue(Arrays.equals(dv, cdv));
		    boolean valid = validated_ref.validate(validateContext);
		    assertTrue(valid);
		}
	    } catch (XMLSignatureException xse) {
		fail("Unexpected Exception: " + xse); 
	    }
	}
    }

    private boolean digestInputEqual(Reference ref) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        InputStream is = ref.getDigestInputStream();
        int nbytes;
        byte[] buf = new byte[256];
        while ((nbytes = is.read(buf, 0, buf.length)) != -1) {
            md.update(buf, 0, nbytes);
        }
        return Arrays.equals(md.digest(), ref.getDigestValue());
    }
}

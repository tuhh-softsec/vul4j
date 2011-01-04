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
package javax.xml.crypto.test.dsig.keyinfo;

import javax.xml.crypto.dsig.keyinfo.*;

import junit.framework.*;

/**
 * Unit test for javax.xml.crypto.dsig.keyinfo.RetrievalMethod
 *
 * @version $Id$
 * @author Sean Mullan
 */
public class RetrievalMethodTest extends TestCase {

    private KeyInfoFactory fac;

    public RetrievalMethodTest() {
	super("RetrievalMethodTest");
    }

    public RetrievalMethodTest(String name) {
	super(name);
    }

    public void setUp() throws Exception { 
	fac = KeyInfoFactory.getInstance
	    ("DOM", new org.jcp.xml.dsig.internal.dom.XMLDSigRI());
    }

    public void tearDown() { }

    public void testgetURI() {
        RetrievalMethod rm = fac.newRetrievalMethod("#X509Data");
        assertNotNull(rm.getURI());
    }

    public void testgetTransforms() {
        RetrievalMethod rm = fac.newRetrievalMethod("#X509Data");
        assertNotNull(rm.getTransforms());
    }

    public void testgetType() {
        RetrievalMethod rm = fac.newRetrievalMethod("#X509Data");
        assertNull(rm.getType());
    }

    public void testConstructors() {
        final String uri = "#X509CertChain";
        // test RetrievalMethod(String)
        RetrievalMethod rm = fac.newRetrievalMethod(uri);
        assertEquals(uri, rm.getURI());

	try {
	    rm = fac.newRetrievalMethod(null); 
	    fail("Should raise a NullPointerException"); 
        } catch (NullPointerException npe) {}

	// test RetrievalMethod(String, String, Transform[])
	try {
	    rm = fac.newRetrievalMethod(null, null, null); 
	    fail("Should raise a NullPointerException"); 
        } catch (NullPointerException npe) {}

        final String type = "http://www.w3.org/2000/09/xmldsig#X509Data";
        rm = fac.newRetrievalMethod(uri, type, null);
        assertEquals(uri, rm.getURI());
        assertEquals(type, rm.getType());
    }

    public void testisFeatureSupported() throws Exception {
	String uri = "#X509CertChain";
        String type = "http://www.w3.org/2000/09/xmldsig#X509Data";
	RetrievalMethod rm = null;
	for (int i=0; i<2; i++) {
	    switch (i) {
	    case 0:
		rm = fac.newRetrievalMethod(uri);
		break;
	    case 1:
		rm = fac.newRetrievalMethod(uri, type, null);
		break;
	    }		
	    try {
		rm.isFeatureSupported(null); 
		fail("Should raise a NPE for null feature"); 
	    } catch (NullPointerException npe) {}
	    
	    assertTrue(!rm.isFeatureSupported("not supported"));
	}
    }
}

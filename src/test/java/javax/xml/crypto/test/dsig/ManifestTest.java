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

import java.util.*;
import javax.xml.crypto.dsig.*;

import junit.framework.*;

/**
 * Unit test for javax.xml.crypto.dsig.Manifest
 *
 * @version $Id$
 * @author Valerie Peng
 */
public class ManifestTest extends TestCase {
    private XMLSignatureFactory fac;

    private static Reference VALID_REF = new 
	TestUtils.MyOwnDOMReference("ref#1", true);
    private static Reference INVALID_REF = new 
	TestUtils.MyOwnDOMReference("ref#2", false);

    public ManifestTest() {
	super("ManifestTest");
    }

    public ManifestTest(String name) {
	super(name);
    }

    public void setUp() throws Exception {
        fac = XMLSignatureFactory.getInstance
            ("DOM", new org.jcp.xml.dsig.internal.dom.XMLDSigRI());
    }

    public void tearDown() {}

    public void testConstructor() throws Exception {
	Manifest man = null;
	String id = "manifest_id";
	List refs = new Vector();
	// test XMLSignatureFactory.newManifest(List references)
	// and  XMLSignatureFactory.newManifest(List references, 
	//                                       String id)
	// for generating Manifest objects
	refs.add(VALID_REF);
	refs.add(INVALID_REF);
	for (int i = 0; i < 3; i++) {
	    String expectedId = null;
	    switch (i) {
	    case 0:
		man = fac.newManifest(refs);
		break;
	    case 1:
		man = fac.newManifest(refs, null);
		break;
	    case 2:
		man = fac.newManifest(refs, id);
		expectedId = id;
		break;
	    }		
	    assertNotNull(man);
	    assertTrue(Arrays.equals(man.getReferences().toArray(),
				     refs.toArray()));
	    assertEquals(man.getId(), expectedId);
	}
	try {
	    man = fac.newManifest(null);
	    fail("Should throw a NPE for null references");
	} catch (NullPointerException npe) {
	} catch (Exception ex) {
	    fail("Should throw a NPE instead of " + ex + 
		 " for null references");
	}
	try {
	    man = fac.newManifest(null, id);
	    fail("Should throw a NPE for null references");
	} catch (NullPointerException npe) {
	} catch (Exception ex) {
	    fail("Should throw a NPE instead of " + ex + 
		 " for null references");
	}
	// Clear the references list content
	refs.clear();
	try {
	    man = fac.newManifest(refs);
	    fail("Should throw a IAE for empty references");
	} catch (IllegalArgumentException iae) {
	} catch (Exception ex) {
	    fail("Should throw a IAE instead of " + ex + 
		 " for empty references");
	}
	try {
	    man = fac.newManifest(refs, id);
	    fail("Should throw a IAE for empty references");
	} catch (IllegalArgumentException iae) {
	} catch (Exception ex) {
	    fail("Should throw a IAE instead of " + ex + 
		 " for empty references");
	}
	refs.add("references");
	try {
	    man = fac.newManifest(refs);
	    fail("Should throw a CCE for references containing " + 
		 "non-Reference objects");
	} catch (ClassCastException cce) {
	} catch (Exception ex) {
	    fail("Should throw a CCE instead of " + ex + 
		 " for references containing non-Reference objects");
	}
	try {
	    man = fac.newManifest(refs, id);
	    fail("Should throw a CCE for references containing " + 
		 "non-Reference objects");
	} catch (ClassCastException cce) {
	} catch (Exception ex) {
	    fail("Should throw a CCE instead of " + ex + 
		 " for references containing non-Reference objects");
	}	
    }
    
    public void testisFeatureSupported() throws Exception {
	List refs = new Vector();
	refs.add(VALID_REF);

	Manifest man = fac.newManifest(refs);
	
	try {
	    man.isFeatureSupported(null); 
	    fail("Should raise a NPE for null feature"); 
	} catch (NullPointerException npe) {}
	    
	assertTrue(!man.isFeatureSupported("not supported"));
    }

    public void testgetReferences() throws Exception {
	List refs = new Vector();
	refs.add(VALID_REF);	
	Manifest man = fac.newManifest(refs);
	List stored = man.getReferences();
	try {
	    stored.add(INVALID_REF);
	    fail("Should not be able to modify the references directly");
	} catch (UnsupportedOperationException ex) {
	}
	try {
	    ListIterator li = stored.listIterator();
	    li.add(INVALID_REF);
	    fail("Should not be able to modify the references indirectly");
	} catch (UnsupportedOperationException ex) {
	}
    }
}

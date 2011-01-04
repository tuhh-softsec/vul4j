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

import java.util.*;
import javax.xml.crypto.dsig.*;

import junit.framework.*;

/**
 * Unit test for javax.xml.crypto.dsig.SignatureProperty
 *
 * @version $Id$
 * @author Valerie Peng
 */
public class SignaturePropertyTest extends TestCase {

    private XMLSignatureFactory factory;
    private String target = "target";
    private String id = "id";

    public SignaturePropertyTest() {
	super("SignaturePropertyTest");
    }

    public SignaturePropertyTest(String name) {
	super(name);
    }

    public void setUp() throws Exception {
	factory = XMLSignatureFactory.getInstance
            ("DOM", new org.jcp.xml.dsig.internal.dom.XMLDSigRI());
    }
    
    public void tearDown() {}
    
    public void testConstructor() {
	// test XMLSignatureFactory.newSignatureProperty(List, String, String) 
	SignatureProperty prop;

	try {
	    prop = factory.newSignatureProperty(null, target, id); 
	    fail("Should raise a NPE for null content"); 
	} catch (NullPointerException npe) {
	} catch (Exception ex) {
	    fail("Should raise a NPE for null content instead of " + ex);
	}
	List list = new Vector();
	try {
	    prop = factory.newSignatureProperty(list, target, id); 
	    fail("Should raise an IAE for empty content"); 
	} catch (IllegalArgumentException iae) {
	} catch (Exception ex) {
	    fail("Should raise an IAE for empty content instead of " + ex);
	}
	String strEntry = "wrong type";
	list.add(strEntry);
	try {
	    prop = factory.newSignatureProperty(list, target, id); 
	    fail("Should raise a CCE for content containing " +
		 "invalid, i.e. non-XMLStructure, entries"); 
	} catch (ClassCastException cce) {
	} catch (Exception ex) {
	    fail("Should raise a CCE for content with invalid entries " +
		 "instead of " + ex);
	}
	list.remove(strEntry);
	list.add(new TestUtils.MyOwnXMLStructure());
	try {
	    prop = factory.newSignatureProperty(list, null, id);
	    fail("Should raise a NPE for null target"); 
	} catch (NullPointerException npe) {
	} catch (Exception ex) {
	    fail("Should raise a NPE for null target instead of " + ex);
	}

	prop = factory.newSignatureProperty(list, target, id);
	assertNotNull(prop);
	List unmodifiable = prop.getContent();
	assertNotNull(unmodifiable);
	try {
	    unmodifiable.add(new TestUtils.MyOwnXMLStructure());
	    fail("Should return an unmodifiable List object");
	} catch (UnsupportedOperationException uoe) {}
	assertTrue(Arrays.equals(unmodifiable.toArray(), list.toArray()));
	assertEquals(prop.getTarget(), target);
	assertEquals(prop.getId(), id);
	assertNotNull(prop);
    }

    public void testisFeatureSupported() {
	List list = new Vector();
	list.add(new TestUtils.MyOwnXMLStructure());
	SignatureProperty prop = factory.newSignatureProperty
	    (list, target, id);
	try {
	    prop.isFeatureSupported(null); 
	    fail("Should raise a NPE for null feature"); 
	} catch (NullPointerException npe) {}

	assertTrue(!prop.isFeatureSupported("not supported"));
    }
}

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
 * Unit test for javax.xml.crypto.dsig.XMLObject
 *
 * @version $Id$
 * @author Valerie Peng
 */
public class XMLObjectTest extends TestCase {

    private XMLSignatureFactory factory;
    private String id = "id";
    private String mimeType = "mime";
    private String encoding = "encoding";

    public XMLObjectTest() {
	super("XMLObjectTest");
    }

    public XMLObjectTest(String name) {
	super(name);
    }

    public void setUp() throws Exception {
	factory = XMLSignatureFactory.getInstance
            ("DOM", new org.jcp.xml.dsig.internal.dom.XMLDSigRI());
    }
    
    public void tearDown() {}
    
    public void testConstructor() {
	// test XMLSignatureFactory.newXMLObject(List, String, String, String) 
	XMLObject obj;
	
	obj = factory.newXMLObject(null, null, null, null); 
	assertNotNull(obj);

	Vector list = new Vector();
	obj = factory.newXMLObject(list, null, null, null); 
	assertNotNull(obj);
	
	String strEntry = "wrong type";
	list.add(strEntry);
	try {
	    obj = factory.newXMLObject(list, null, null, null); 
	    fail("Should raise a CCE for content containing " +
		 "invalid, i.e. non-XMLStructure, entries"); 
	} catch (ClassCastException cce) {
	} catch (Exception ex) {
	    fail("Should raise a CCE for content with invalid entries " +
		 "instead of " + ex);
	}
	list.remove(strEntry);
	list.add(new TestUtils.MyOwnXMLStructure());
	obj = factory.newXMLObject(list, id, mimeType, encoding);
	assertNotNull(obj);
	assertNotNull(obj.getContent());
	assertTrue(Arrays.equals(obj.getContent().toArray(), list.toArray()));
	assertEquals(obj.getId(), id);
	assertEquals(obj.getMimeType(), mimeType);
	assertEquals(obj.getEncoding(), encoding);

	List unmodifiable = obj.getContent();
	try {
	    unmodifiable.add(new TestUtils.MyOwnXMLStructure());
	    fail("Should return an unmodifiable List object");
	} catch (UnsupportedOperationException uoe) {}
	assertTrue(Arrays.equals(unmodifiable.toArray(), list.toArray()));
    }

    public void testisFeatureSupported() {
	List list = new Vector();
	list.add(new TestUtils.MyOwnXMLStructure());
	XMLObject obj = factory.newXMLObject(list, id, mimeType, encoding);
	try {
	    obj.isFeatureSupported(null); 
	    fail("Should raise a NPE for null feature"); 
	} catch (NullPointerException npe) {}

	assertTrue(!obj.isFeatureSupported("not supported"));
    }
}

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
package javax.xml.crypto.test;

import java.io.*;
import java.util.*;
import javax.xml.crypto.*;

import junit.framework.*;

/**
 * Unit test for javax.xml.crypto.OctetStreamData
 *
 * @version $Id$
 * @author Valerie Peng
 */
public class OctetStreamDataTest extends TestCase {

    public OctetStreamDataTest() {
	super("OctetStreamDataTest");
    }

    public OctetStreamDataTest(String name) {
	super(name);
    }

    public void setUp() throws Exception { 
    }
    
    public void tearDown() {}
    
    public void testConstructor() {
	// test OctetStreamData(InputStream) and 
	// OctetStreamData(InputStream, String, String)
	OctetStreamData osdata;
	try {
	    osdata = new OctetStreamData(null); 
	    fail("Should raise a NPE for null input stream"); 
	} catch (NullPointerException npe) {}	
	try {
	    osdata = new OctetStreamData(null, "uri", "mimeType");
	    fail("Should raise a NPE for null input stream"); 
	} catch (NullPointerException npe) {}

	int len = 300;
	byte[] in = new byte[len];
	new Random().nextBytes(in);
	ByteArrayInputStream bais = new ByteArrayInputStream(in);
	try {
	    osdata = new OctetStreamData(bais); 
	    assertNotNull(osdata);
	    assertEquals(osdata.getOctetStream(), bais);
	    assertNull(osdata.getURI());
	    assertNull(osdata.getMimeType());
	
	    osdata = new OctetStreamData(bais, null, null);
	    assertNotNull(osdata);
	    assertEquals(osdata.getOctetStream(), bais);
	    assertNull(osdata.getURI());
	    assertNull(osdata.getMimeType());
	} catch (Exception ex) {
	    fail("Unexpected Exception: " + ex);
	}

	String uri="testUri";
	String mimeType="test";
	try {
	    osdata = new OctetStreamData(bais, uri, mimeType);
	    assertNotNull(osdata);
	    assertEquals(osdata.getOctetStream(), bais);
	    assertEquals(osdata.getURI(), uri);
	    assertEquals(osdata.getMimeType(), mimeType);
	} catch (Exception ex) {
	    fail("Unexpected Exception: " + ex);
	}
    }
}

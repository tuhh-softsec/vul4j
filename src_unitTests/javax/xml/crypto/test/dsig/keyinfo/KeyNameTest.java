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
 * Unit test for javax.xml.crypto.dsig.keyinfo.KeyName
 *
 * @version $Id$
 * @author Sean Mullan
 */
public class KeyNameTest extends TestCase {

    private KeyInfoFactory fac;

    public KeyNameTest() {
	super("KeyNameTest");
    }

    public KeyNameTest(String name) {
	super(name);
    }

    public void setUp() throws Exception { 
	fac = KeyInfoFactory.getInstance
	    ("DOM", new org.jcp.xml.dsig.internal.dom.XMLDSigRI());
    }

    public void tearDown() { }

    public void testgetName() {
	KeyName kn = fac.newKeyName("skeleton");
	assertNotNull(kn.getName());
    }

    public void testConstructor() {
	final String name = "keyName";
	KeyName kn = fac.newKeyName(name);
	assertEquals(name, kn.getName());
	try {
	    kn = fac.newKeyName(null); 
	    fail("Should raise a NullPointerException"); 
	} catch (NullPointerException npe) {}
    }

    public void testisFeatureSupported() {
	KeyName kn = fac.newKeyName("keyName");
	try {
	    kn.isFeatureSupported(null); 
	    fail("Should raise a NPE for null feature"); 
	} catch (NullPointerException npe) {}

	assertTrue(!kn.isFeatureSupported("not supported"));
    }
}

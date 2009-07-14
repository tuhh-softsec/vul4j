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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.xml.crypto.*;
import javax.xml.crypto.dsig.keyinfo.*;

import junit.framework.*;

/**
 * Unit test for javax.xml.crypto.dsig.keyinfo.PGPData
 *
 * @version $Id$
 * @author Valerie Peng
 */
public class PGPDataTest extends TestCase {

    private KeyInfoFactory fac;
    private byte[][] values = { 
	{
            0x01, 0x02, 0x03, 0x04,
            0x05, 0x06, 0x07, 0x08
	},
	{
	    (byte)0xc6, (byte)0x01, (byte)0x00
	}
    };

    public PGPDataTest() {
	super("PGPDataTest");
    }

    public PGPDataTest(String name) {
	super(name);
    }

    public void setUp() throws Exception { 
	fac = KeyInfoFactory.getInstance
	    ("DOM", new org.jcp.xml.dsig.internal.dom.XMLDSigRI());
    }

    public void tearDown() { }

    public void testgetExternalElements() {
	PGPData[] pds = {
	    fac.newPGPData(values[0]),
	    fac.newPGPData(values[0], values[1], null),
	    fac.newPGPData(values[1], null)
	};
	for (int i=0; i<pds.length; i++) {
	    List li = pds[i].getExternalElements();
	    assertNotNull(li);
	    if (!li.isEmpty()) {
		Object[] types = li.toArray();
		for (int j=0; j<types.length; j++) {
		    if (!(types[j] instanceof XMLStructure)) {
			fail("PGP element has the wrong type");
		    };
		}
	    } 
        }
	try {
	    fac.newPGPData
		(values[0], Collections.singletonList(new Object()));
	    fail("Added PGP element of wrong type");
	} catch (ClassCastException ex) {
	    // expected
	}
    }

    public void testgetKeyId() {
	PGPData pd = fac.newPGPData(values[0]);
	assertNotNull(pd.getKeyId());
	pd = fac.newPGPData(values[0], values[1], null);
	assertNotNull(pd.getKeyId());
	pd = fac.newPGPData(values[1], null);
    }

    public void testgetKeyPacket() {
	PGPData pd = fac.newPGPData(values[0]);
	pd = fac.newPGPData(values[0], values[1], null);
	assertNotNull(pd.getKeyPacket());
        pd = fac.newPGPData(values[1], null);
	assertNotNull(pd.getKeyPacket());
    }

    public void testConstructor() {
	// test newPGPKeyData(byte[])
	PGPData pd = fac.newPGPData(values[0]);
	assertTrue(Arrays.equals(values[0], pd.getKeyId()));

	// test newPGPData(byte[], byte[], List)
	pd = fac.newPGPData(values[0], values[1], null);
	assertTrue(Arrays.equals(values[0], pd.getKeyId()));
	assertTrue(Arrays.equals(values[1], pd.getKeyPacket()));
	    
	// test newPGPData(byte[], List)
	pd = fac.newPGPData(values[1], null);
	assertTrue(Arrays.equals(values[1], pd.getKeyPacket()));
    }

    public void testisFeatureSupported() {
	PGPData pd = null;
	for (int i=0; i<3; i++) {
	    switch (i) {
	    case 0:
		pd = fac.newPGPData(values[0]);
		break;
	    case 1:
		pd = fac.newPGPData(values[0], values[1], null);
		break;
	    case 2:
		pd = fac.newPGPData(values[1], null);
	    }
	    try {
		pd.isFeatureSupported(null); 
		fail("Should raise a NPE for null feature"); 
	    } catch (NullPointerException npe) {}
	    
	    assertTrue(!pd.isFeatureSupported("not supported"));
	}
    }
}

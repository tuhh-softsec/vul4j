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

import java.math.BigInteger;
import javax.xml.crypto.dsig.keyinfo.*;

import junit.framework.*;

/**
 * Unit test for javax.xml.crypto.dsig.keyinfo.X509IssuerSerial
 *
 * @version $Id: X509IssuerSerialTest.java,v 1.1 2004/04/07 21:11:36 mullan Exp $
 * @author Valerie Peng
 */
public class X509IssuerSerialTest extends TestCase {

    private KeyInfoFactory fac;
    private String name;

    public X509IssuerSerialTest() {
	super("X509IssuerSerialTest");
    }

    public X509IssuerSerialTest(String name) {
	super(name);
    }

    public void setUp() throws Exception { 
	fac = KeyInfoFactory.getInstance
	    ("DOM", new org.jcp.xml.dsig.internal.dom.XMLDSigRI());
	name = "CN = Wolfgang";
    }

    public void tearDown() { }

    public void testgetIssuerName() {
	X509IssuerSerial x509is = fac.newX509IssuerSerial(name,BigInteger.ZERO);
	assertNotNull(x509is.getIssuerName());	
    }

    public void testgetSerialNumber() {
	X509IssuerSerial x509is = fac.newX509IssuerSerial(name,BigInteger.ZERO);
	assertNotNull(x509is.getSerialNumber());	
    }

    public void testConstructor() {
	// test newX509IssuerSerial(String, BigInteger)
	X509IssuerSerial x509is = fac.newX509IssuerSerial(name, BigInteger.ONE);
	assertEquals(name, x509is.getIssuerName());
	assertEquals(BigInteger.ONE, x509is.getSerialNumber());
    }

    /*
     * Confirm that an IllegalArgumentException is thrown when an issuer
     * distinguished name does not conform to RFC 2253.
     */
    public void testConstructorBadIssuerName() {
	// test newX509IssuerSerial(String, BigInteger)
	String badName = "cn=bad,=+bad,";
	try {
	    fac.newX509IssuerSerial(badName,BigInteger.ONE);
	    fail("Should raise an IllegalArgumentException when issuer " +
		"distinguished name does not conform to RFC 2253"); 
	} catch (IllegalArgumentException e) {
	    // success
	}
    }

    public void testisFeatureSupported() {
	
	X509IssuerSerial x509is = fac.newX509IssuerSerial(name, BigInteger.ONE);
	try {
	    x509is.isFeatureSupported(null); 
	    fail("Should raise a NPE for null feature"); 
	} catch (NullPointerException npe) {}
	
	assertTrue(!x509is.isFeatureSupported("not supported"));
    }
}

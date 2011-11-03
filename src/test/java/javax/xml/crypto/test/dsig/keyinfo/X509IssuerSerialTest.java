/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/*
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 */
package javax.xml.crypto.test.dsig.keyinfo;

import java.math.BigInteger;
import javax.xml.crypto.dsig.keyinfo.*;

/**
 * Unit test for javax.xml.crypto.dsig.keyinfo.X509IssuerSerial
 *
 * @version $Id: X509IssuerSerialTest.java,v 1.1 2004/04/07 21:11:36 mullan Exp $
 * @author Valerie Peng
 */
public class X509IssuerSerialTest extends org.junit.Assert {

    private KeyInfoFactory fac;
    private String name;

    public X509IssuerSerialTest() throws Exception { 
        fac = KeyInfoFactory.getInstance
            ("DOM", new org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI());
        name = "CN = Wolfgang";
    }

    @org.junit.Test
    public void testgetIssuerName() {
        X509IssuerSerial x509is = fac.newX509IssuerSerial(name,BigInteger.ZERO);
        assertNotNull(x509is.getIssuerName());	
    }

    @org.junit.Test
    public void testgetSerialNumber() {
        X509IssuerSerial x509is = fac.newX509IssuerSerial(name,BigInteger.ZERO);
        assertNotNull(x509is.getSerialNumber());	
    }

    @org.junit.Test
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
    @org.junit.Test
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

    @org.junit.Test
    public void testisFeatureSupported() {
        
        X509IssuerSerial x509is = fac.newX509IssuerSerial(name, BigInteger.ONE);
        try {
            x509is.isFeatureSupported(null); 
            fail("Should raise a NPE for null feature"); 
        } catch (NullPointerException npe) {}
        
        assertTrue(!x509is.isFeatureSupported("not supported"));
    }
}

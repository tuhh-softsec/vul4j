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

import java.util.*;
import java.security.cert.X509Certificate;
import java.security.cert.X509CRL;
import javax.xml.crypto.*;
import javax.xml.crypto.dsig.keyinfo.*;

/**
 * Unit test for javax.xml.crypto.dsig.keyinfo.X509Data
 *
 * @version $Id$
 * @author Valerie Peng
 */
public class X509DataTest extends org.junit.Assert {

    private KeyInfoFactory fac;

    public X509DataTest() throws Exception { 
        fac = KeyInfoFactory.getInstance
            ("DOM", new org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI());
    }

    @org.junit.Test
    @SuppressWarnings("unchecked")
    public void testgetTypes() {
        X509Data x509 = fac.newX509Data(Collections.singletonList("cn=foo"));
        List<Object> li = x509.getContent();
        assertNotNull(li);
        if (!li.isEmpty()) {
            Object[] content = li.toArray();
            for (int j=0; j<content.length; j++) {
                if (!(content[j] instanceof String) &&
                    !(content[j] instanceof byte[]) &&
                    !(content[j] instanceof X509Certificate) &&
                    !(content[j] instanceof X509CRL) &&
                    !(content[j] instanceof XMLStructure)) {
                    fail("X509 element has the wrong type");
                }
            }
        } else {
            li.add("any string");
            li.add(new byte[5]);
            //@@@@@li.add(X509Certificate);
            //@@@@@li.add(X509CRL);
            //@@@@@li.add(XMLStructure);
            try {
                li.add(new Object());
                fail("Added X509 element of wrong type");
            } catch (ClassCastException ex) {
                // expected
            }
        }
    }

    @org.junit.Test
    public void testConstructor() {
        // test newX509Data()
        X509Data x509 = fac.newX509Data(Collections.singletonList("cn=foo"));
        assertNotNull(x509);
    }

    @org.junit.Test
    public void testisFeatureSupported() {

        X509Data x509 = fac.newX509Data(Collections.singletonList("cn=foo"));
        try {
            x509.isFeatureSupported(null); 
            fail("Should raise a NPE for null feature"); 
        } catch (NullPointerException npe) {}
        
        assertTrue(!x509.isFeatureSupported("not supported"));
    }
}

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

import java.security.*;
import javax.xml.crypto.dsig.keyinfo.*;

/**
 * Unit test for javax.xml.crypto.dsig.keyinfo.KeyValue
 *
 * @author Valerie Peng
 */
public class KeyValueTest extends org.junit.Assert {

    private static final String[] ALGOS = { "DSA", "RSA" };
    private KeyInfoFactory fac;
    private PublicKey keys[] = null;

    public KeyValueTest() throws Exception { 
        fac = KeyInfoFactory.getInstance
            ("DOM", new org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI());
        // generate PublicKey(s) and XMLStructure(s) for DSA and RSA
        keys = new PublicKey[ALGOS.length];

        for (int i = 0; i < ALGOS.length; i++) {
            keys[i] = genPublicKey(ALGOS[i], 512);
        }
    }

    @org.junit.Test
    public void testgetPublicKey() {
        try {
            KeyValue kv = fac.newKeyValue(keys[0]);
            assertNotNull(kv.getPublicKey());
        } catch (KeyException ke) {
            fail("Should pass instead of throwing KeyException");
        }
    }

    @org.junit.Test
    public void testConstructor() {
        // test newKeyValue(PublicKey pk)
        for (int i = 0; i < keys.length; i++) {
            try {
                KeyValue kv = fac.newKeyValue(keys[i]);
                assertEquals(keys[i], kv.getPublicKey());
            } catch (KeyException ke) {
                fail("Should pass instead of throwing KeyException");
            }
        }
    }

    @org.junit.Test
    public void testisFeatureSupported() {
        KeyValue kv = null;
        try {
            kv = fac.newKeyValue(keys[0]);
            kv.isFeatureSupported(null); 
            fail("Should raise a NPE for null feature"); 
        } catch (KeyException ke) {
            fail("Should raise a NPE for null feature"); 
        } catch (NullPointerException npe) {}
            
        assertTrue(!kv.isFeatureSupported("not supported"));
    }
    
    private PublicKey genPublicKey(String algo, int keysize) throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(algo);
        kpg.initialize(keysize, new SecureRandom(("Not so random bytes" 
            + System.currentTimeMillis() ).getBytes() ));
        KeyPair kp = kpg.generateKeyPair();
        return kp.getPublic();
    }

}

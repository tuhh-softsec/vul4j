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
package javax.xml.crypto.test;

import java.util.*;
import java.security.Key;
import java.security.cert.X509Certificate;
import javax.xml.crypto.*;
import javax.xml.crypto.dsig.keyinfo.*;
import javax.xml.crypto.dsig.*;

/**
 * Unit test for javax.xml.crypto.KeySelector
 *
 * @author Valerie Peng
 */
public class KeySelectorTest extends org.junit.Assert {
    private Key key;
    private KeySelector selector1;

    private class MyOwnKey implements Key {
        private static final long serialVersionUID = -3288147894137347920L;
        
        private String algo;
        private byte[] val;
        MyOwnKey(String algorithm, byte[] value) {
            algo = algorithm;
            val = value.clone();
        }
        
        public String getAlgorithm() {
            return algo;
        }
        public byte[] getEncoded() {
            return val;
        }
        public String getFormat() {
            return "RAW";
        }
    }

    public KeySelectorTest() throws Exception {
        // selector1: singletonKeySelector
        key = new MyOwnKey("test", new byte[16]);
        selector1 = KeySelector.singletonKeySelector(key);
    }

    @org.junit.Test
    public void testselect() throws Exception {
        KeyInfoFactory factory = KeyInfoFactory.getInstance
            ("DOM", new org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI());
        X509Data obj = factory.newX509Data(Collections.singletonList("CN=foo"));
        KeyInfo info = factory.newKeyInfo(Collections.singletonList(obj));
        //@@@@@what about other types of X509Data, i.e. subject name String,
        // X509IssuerSerial objects, etc?
        XMLSignatureFactory dsigFac = XMLSignatureFactory.getInstance
            ("DOM", new org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI());
        SignatureMethod sm1 = 
            dsigFac.newSignatureMethod(SignatureMethod.DSA_SHA1, null);
        SignatureMethod sm2 = 
            dsigFac.newSignatureMethod(SignatureMethod.RSA_SHA1, null);

        assertTrue(compareKey(key, selector1.select
            (info, KeySelector.Purpose.VERIFY, sm1, null).getKey()));
        assertTrue(compareKey(key, selector1.select
            (info, KeySelector.Purpose.VERIFY, sm2, null).getKey()));
    }
    
    private static boolean compareKey(Object answer, Key key) {
        boolean result = false;
        if (answer instanceof MyOwnKey) {
            result = (answer == key);
        } else if (answer instanceof X509Certificate) {
            result =
                ((X509Certificate)answer).getPublicKey().equals(key);
        }
        return result;
    }
}

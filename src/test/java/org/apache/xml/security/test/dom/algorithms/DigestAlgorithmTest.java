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
package org.apache.xml.security.test.dom.algorithms;

import java.lang.reflect.Constructor;
import java.security.MessageDigest;
import java.security.Provider;
import java.security.Security;
import java.util.Arrays;

import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;

/**
 * A test to make sure that the various digest algorithms are working
 */
public class DigestAlgorithmTest extends org.junit.Assert {

    static {
        org.apache.xml.security.Init.init();
    }
    
    public DigestAlgorithmTest() throws Exception {
        //
        // If the BouncyCastle provider is not installed, then try to load it 
        // via reflection.
        //
        if (Security.getProvider("BC") == null) {
            Constructor<?> cons = null;
            try {
                Class<?> c = Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider");
                cons = c.getConstructor(new Class[] {});
            } catch (Exception e) {
                //ignore
            }
            if (cons == null) {
                // BouncyCastle is not available so just return
                return;
            } else {
                Provider provider = (java.security.Provider)cons.newInstance();
                Security.insertProviderAt(provider, 2);
            }
        }
    }

    @org.junit.Test
    public void testSHA1() throws Exception {
        Document doc = XMLUtils.createDocumentBuilder(false).newDocument();
        
        MessageDigestAlgorithm digestAlgorithm = 
            MessageDigestAlgorithm.getInstance(doc, MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1);
        assertEquals(MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1, digestAlgorithm.getAlgorithmURI());
        
        byte[] digest = digestAlgorithm.digest("test-string".getBytes());
        assertNotNull(digest);
        assertTrue(digest.length > 0);
        
        // Now compare against a JDK MessageDigest Object
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digest2 = md.digest("test-string".getBytes());
        assertTrue(Arrays.equals(digest, digest2));
    }
    
    @org.junit.Test
    public void testSHA256() throws Exception {
        Document doc = XMLUtils.createDocumentBuilder(false).newDocument();
        
        MessageDigestAlgorithm digestAlgorithm = 
            MessageDigestAlgorithm.getInstance(doc, MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA256);
        assertEquals(MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA256, digestAlgorithm.getAlgorithmURI());
        
        byte[] digest = digestAlgorithm.digest("test-string".getBytes());
        assertNotNull(digest);
        assertTrue(digest.length > 0);
        
        // Now compare against a JDK MessageDigest Object
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest2 = md.digest("test-string".getBytes());
        assertTrue(Arrays.equals(digest, digest2));
    }
    
    @org.junit.Test
    public void testSHA384() throws Exception {
        Document doc = XMLUtils.createDocumentBuilder(false).newDocument();
        
        MessageDigestAlgorithm digestAlgorithm = 
            MessageDigestAlgorithm.getInstance(doc, MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA384);
        assertEquals(MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA384, digestAlgorithm.getAlgorithmURI());
        
        byte[] digest = digestAlgorithm.digest("test-string".getBytes());
        assertNotNull(digest);
        assertTrue(digest.length > 0);
        
        // Now compare against a JDK MessageDigest Object
        MessageDigest md = MessageDigest.getInstance("SHA-384");
        byte[] digest2 = md.digest("test-string".getBytes());
        assertTrue(Arrays.equals(digest, digest2));
    }
    
    @org.junit.Test
    public void testSHA512() throws Exception {
        Document doc = XMLUtils.createDocumentBuilder(false).newDocument();
        
        MessageDigestAlgorithm digestAlgorithm = 
            MessageDigestAlgorithm.getInstance(doc, MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA512);
        assertEquals(MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA512, digestAlgorithm.getAlgorithmURI());
        
        byte[] digest = digestAlgorithm.digest("test-string".getBytes());
        assertNotNull(digest);
        assertTrue(digest.length > 0);
        
        // Now compare against a JDK MessageDigest Object
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] digest2 = md.digest("test-string".getBytes());
        assertTrue(Arrays.equals(digest, digest2));
    }
    
    @org.junit.Test
    public void testMD5() throws Exception {
        Document doc = XMLUtils.createDocumentBuilder(false).newDocument();
        
        MessageDigestAlgorithm digestAlgorithm = 
            MessageDigestAlgorithm.getInstance(doc, MessageDigestAlgorithm.ALGO_ID_DIGEST_NOT_RECOMMENDED_MD5);
        assertEquals(MessageDigestAlgorithm.ALGO_ID_DIGEST_NOT_RECOMMENDED_MD5, digestAlgorithm.getAlgorithmURI());
        
        byte[] digest = digestAlgorithm.digest("test-string".getBytes());
        assertNotNull(digest);
        assertTrue(digest.length > 0);
        
        // Now compare against a JDK MessageDigest Object
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest2 = md.digest("test-string".getBytes());
        assertTrue(Arrays.equals(digest, digest2));
    }
    
    @org.junit.Test
    public void testRIPEMD160() throws Exception {
        // This only works with BouncyCastle
        if (Security.getProvider("BC") == null) {
            return;
        }
        Document doc = XMLUtils.createDocumentBuilder(false).newDocument();
        
        MessageDigestAlgorithm digestAlgorithm = 
            MessageDigestAlgorithm.getInstance(doc, MessageDigestAlgorithm.ALGO_ID_DIGEST_RIPEMD160);
        assertEquals(MessageDigestAlgorithm.ALGO_ID_DIGEST_RIPEMD160, digestAlgorithm.getAlgorithmURI());
        
        byte[] digest = digestAlgorithm.digest("test-string".getBytes());
        assertNotNull(digest);
        assertTrue(digest.length > 0);
        
        // Now compare against a JDK MessageDigest Object
        MessageDigest md = MessageDigest.getInstance("RIPEMD160");
        byte[] digest2 = md.digest("test-string".getBytes());
        assertTrue(Arrays.equals(digest, digest2));
    }
    
}

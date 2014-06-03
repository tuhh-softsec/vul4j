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
package org.apache.xml.security.algorithms;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.signature.XMLSignature;
import org.w3c.dom.Element;


/**
 * This class maps algorithm identifier URIs to JAVA JCE class names.
 */
public class JCEMapper {

    private static org.slf4j.Logger log =
        org.slf4j.LoggerFactory.getLogger(JCEMapper.class);

    private static Map<String, Algorithm> algorithmsMap = 
        new ConcurrentHashMap<String, Algorithm>();

    private static String providerName = null;
    
    /**
     * Method register
     *
     * @param id
     * @param algorithm
     */
    public static void register(String id, Algorithm algorithm) {
        algorithmsMap.put(id, algorithm);
    }
    
    /**
     * This method registers the default algorithms.
     */
    public static void registerDefaultAlgorithms() {
        // Digest algorithms
        algorithmsMap.put(
            MessageDigestAlgorithm.ALGO_ID_DIGEST_NOT_RECOMMENDED_MD5, 
            new Algorithm("MD5", "MD5", "MessageDigest")
        );
        algorithmsMap.put(
            MessageDigestAlgorithm.ALGO_ID_DIGEST_RIPEMD160, 
            new Algorithm("RIPEMD160", "RIPEMD160", "MessageDigest")
        );
        algorithmsMap.put(
            MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1, 
            new Algorithm("SHA-1", "SHA-1", "MessageDigest")
        );
        algorithmsMap.put(
            MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA224, 
            new Algorithm("SHA-224", "SHA-224", "MessageDigest")
        );
        algorithmsMap.put(
            MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA256, 
            new Algorithm("SHA-256", "SHA-256", "MessageDigest")
        );
        algorithmsMap.put(
            MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA384, 
            new Algorithm("SHA-384", "SHA-384", "MessageDigest")
        );
        algorithmsMap.put(
            MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA512, 
            new Algorithm("SHA-512", "SHA-512", "MessageDigest")
        );
        algorithmsMap.put(
            MessageDigestAlgorithm.ALGO_ID_DIGEST_WHIRLPOOL, 
            new Algorithm("WHIRLPOOL", "WHIRLPOOL", "MessageDigest")
        );
        algorithmsMap.put(
            MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA3_224, 
            new Algorithm("SHA3-224", "SHA3-224", "MessageDigest")
        );
        algorithmsMap.put(
            MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA3_256, 
            new Algorithm("SHA3-256", "SHA3-256", "MessageDigest")
        );
        algorithmsMap.put(
            MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA3_384, 
            new Algorithm("SHA3-384", "SHA3-384", "MessageDigest")
        );
        algorithmsMap.put(
            MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA3_512, 
            new Algorithm("SHA3-512", "SHA3-512", "MessageDigest")
        );
        // Signature algorithms
        algorithmsMap.put(
            XMLSignature.ALGO_ID_SIGNATURE_DSA, 
            new Algorithm("SHA1withDSA", "SHA1withDSA", "Signature")
        );
        algorithmsMap.put(
            XMLSignature.ALGO_ID_SIGNATURE_DSA_SHA256,
            new Algorithm("", "SHA256withDSA", "Signature")
        );
        algorithmsMap.put(
            XMLSignature.ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5, 
            new Algorithm("MD5withRSA", "MD5withRSA", "Signature")
        );
        algorithmsMap.put(
            XMLSignature.ALGO_ID_SIGNATURE_RSA_RIPEMD160, 
            new Algorithm("RIPEMD160withRSA", "RIPEMD160withRSA", "Signature")
        );
        algorithmsMap.put(
            XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1, 
            new Algorithm("SHA1withRSA", "SHA1withRSA", "Signature")
        );
        algorithmsMap.put(
            XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA224, 
            new Algorithm("SHA224withRSA", "SHA224withRSA", "Signature")
        );
        algorithmsMap.put(
            XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256, 
            new Algorithm("SHA256withRSA", "SHA256withRSA", "Signature")
        );
        algorithmsMap.put(
            XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA384, 
            new Algorithm("SHA384withRSA", "SHA384withRSA", "Signature")
        );
        algorithmsMap.put(
            XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA512, 
            new Algorithm("SHA512withRSA", "SHA512withRSA", "Signature")
        );
        algorithmsMap.put(
            XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1_MGF1, 
            new Algorithm("SHA1withRSAandMGF1", "SHA1withRSAandMGF1", "Signature")
        );
        algorithmsMap.put(
            XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA224_MGF1, 
            new Algorithm("SHA224withRSAandMGF1", "SHA224withRSAandMGF1", "Signature")
        );
        algorithmsMap.put(
            XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256_MGF1, 
            new Algorithm("SHA256withRSAandMGF1", "SHA256withRSAandMGF1", "Signature")
        );
        algorithmsMap.put(
            XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA384_MGF1, 
            new Algorithm("SHA384withRSAandMGF1", "SHA384withRSAandMGF1", "Signature")
        );
        algorithmsMap.put(
            XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA512_MGF1, 
            new Algorithm("SHA512withRSAandMGF1", "SHA512withRSAandMGF1", "Signature")
        );
        algorithmsMap.put(
            XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA1, 
            new Algorithm("SHA1withECDSA", "SHA1withECDSA", "Signature")
        );
        algorithmsMap.put(
            XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA224, 
            new Algorithm("SHA224withECDSA", "SHA224withECDSA", "Signature")
        );
        algorithmsMap.put(
            XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA256, 
            new Algorithm("SHA256withECDSA", "SHA256withECDSA", "Signature")
        );
        algorithmsMap.put(
            XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA384, 
            new Algorithm("SHA384withECDSA", "SHA384withECDSA", "Signature")
        );
        algorithmsMap.put(
            XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA512, 
            new Algorithm("SHA512withECDSA", "SHA512withECDSA", "Signature")
        );
        algorithmsMap.put(
            XMLSignature.ALGO_ID_SIGNATURE_ECDSA_RIPEMD160, 
            new Algorithm("RIPEMD160withECDSA", "RIPEMD160withECDSA", "Signature")
        );
        algorithmsMap.put(
            XMLSignature.ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5,
            new Algorithm("HmacMD5", "HmacMD5", "Mac", 128, 0)
        );
        algorithmsMap.put(
            XMLSignature.ALGO_ID_MAC_HMAC_RIPEMD160, 
            new Algorithm("HMACRIPEMD160", "HMACRIPEMD160", "Mac", 160, 0)
        );
        algorithmsMap.put(
            XMLSignature.ALGO_ID_MAC_HMAC_SHA1, 
            new Algorithm("HmacSHA1", "HmacSHA1", "Mac", 160, 0)
        );
        algorithmsMap.put(
            XMLSignature.ALGO_ID_MAC_HMAC_SHA224, 
            new Algorithm("HmacSHA224", "HmacSHA224", "Mac", 224, 0)
        );
        algorithmsMap.put(
            XMLSignature.ALGO_ID_MAC_HMAC_SHA256, 
            new Algorithm("HmacSHA256", "HmacSHA256", "Mac", 256, 0)
        );
        algorithmsMap.put(
            XMLSignature.ALGO_ID_MAC_HMAC_SHA384, 
            new Algorithm("HmacSHA384", "HmacSHA384", "Mac", 384, 0)
        );
        algorithmsMap.put(
            XMLSignature.ALGO_ID_MAC_HMAC_SHA512, 
            new Algorithm("HmacSHA512", "HmacSHA512", "Mac", 512, 0)
        );
        // Encryption algorithms
        algorithmsMap.put(
            XMLCipher.TRIPLEDES, 
            new Algorithm("DESede", "DESede/CBC/ISO10126Padding", "BlockEncryption", 192, 64)
        );
        algorithmsMap.put(
            XMLCipher.AES_128, 
            new Algorithm("AES", "AES/CBC/ISO10126Padding", "BlockEncryption", 128, 128)
        );
        algorithmsMap.put(
            XMLCipher.AES_192, 
            new Algorithm("AES", "AES/CBC/ISO10126Padding", "BlockEncryption", 192, 128)
        );
        algorithmsMap.put(
            XMLCipher.AES_256, 
            new Algorithm("AES", "AES/CBC/ISO10126Padding", "BlockEncryption", 256, 128)
        );
        algorithmsMap.put(
            XMLCipher.AES_128_GCM, 
            new Algorithm("AES", "AES/GCM/NoPadding", "BlockEncryption", 128, 96)
        );
        algorithmsMap.put(
            XMLCipher.AES_192_GCM, 
            new Algorithm("AES", "AES/GCM/NoPadding", "BlockEncryption", 192, 96)
        );
        algorithmsMap.put(
            XMLCipher.AES_256_GCM, 
            new Algorithm("AES", "AES/GCM/NoPadding", "BlockEncryption", 256, 96)
        );
        algorithmsMap.put(
            XMLCipher.SEED_128, 
            new Algorithm("SEED", "SEED/CBC/ISO10126Padding", "BlockEncryption", 128, 128)
        );
        algorithmsMap.put(
            XMLCipher.CAMELLIA_128, 
            new Algorithm("Camellia", "Camellia/CBC/ISO10126Padding", "BlockEncryption", 128, 128)
        );
        algorithmsMap.put(
            XMLCipher.CAMELLIA_192, 
            new Algorithm("Camellia", "Camellia/CBC/ISO10126Padding", "BlockEncryption", 192, 128)
        );
        algorithmsMap.put(
            XMLCipher.CAMELLIA_256, 
            new Algorithm("Camellia", "Camellia/CBC/ISO10126Padding", "BlockEncryption", 256, 128)
        );
        algorithmsMap.put(
            XMLCipher.RSA_v1dot5, 
            new Algorithm("RSA", "RSA/ECB/PKCS1Padding", "KeyTransport")
        );
        algorithmsMap.put(
            XMLCipher.RSA_OAEP, 
            new Algorithm("RSA", "RSA/ECB/OAEPPadding", "KeyTransport")
        );
        algorithmsMap.put(
            XMLCipher.RSA_OAEP_11, 
            new Algorithm("RSA", "RSA/ECB/OAEPPadding", "KeyTransport")
        );
        algorithmsMap.put(
            XMLCipher.DIFFIE_HELLMAN, 
            new Algorithm("", "", "KeyAgreement")
        );
        algorithmsMap.put(
            XMLCipher.TRIPLEDES_KeyWrap, 
            new Algorithm("DESede", "DESedeWrap", "SymmetricKeyWrap", 192, 0)
        );
        algorithmsMap.put(
            XMLCipher.AES_128_KeyWrap, 
            new Algorithm("AES", "AESWrap", "SymmetricKeyWrap", 128, 0)
        );
        algorithmsMap.put(
            XMLCipher.AES_192_KeyWrap, 
            new Algorithm("AES", "AESWrap", "SymmetricKeyWrap", 192, 0)
        );
        algorithmsMap.put(
            XMLCipher.AES_256_KeyWrap, 
            new Algorithm("AES", "AESWrap", "SymmetricKeyWrap", 256, 0)
        );
    }

    /**
     * Method translateURItoJCEID
     *
     * @param algorithmURI
     * @return the JCE standard name corresponding to the given URI
     */
    public static String translateURItoJCEID(String algorithmURI) {
        if (log.isDebugEnabled()) {
            log.debug("Request for URI " + algorithmURI);
        }

        Algorithm algorithm = algorithmsMap.get(algorithmURI);
        if (algorithm != null) {
            return algorithm.jceName;
        }
        return null;
    }
    
    /**
     * Method getAlgorithmClassFromURI
     * @param algorithmURI
     * @return the class name that implements this algorithm
     */
    public static String getAlgorithmClassFromURI(String algorithmURI) {
        if (log.isDebugEnabled()) {
            log.debug("Request for URI " + algorithmURI);
        }

        Algorithm algorithm = algorithmsMap.get(algorithmURI);
        if (algorithm != null) {
            return algorithm.algorithmClass;
        }
        return null;
    }

    /**
     * Returns the keylength in bits for a particular algorithm.
     *
     * @param algorithmURI
     * @return The length of the key used in the algorithm
     */
    public static int getKeyLengthFromURI(String algorithmURI) {
        if (log.isDebugEnabled()) {
            log.debug("Request for URI " + algorithmURI);
        }
        Algorithm algorithm = algorithmsMap.get(algorithmURI);
        if (algorithm != null) {
            return algorithm.keyLength;
        }
        return 0;
    }

    public static int getIVLengthFromURI(String algorithmURI) {
        Algorithm algorithm = algorithmsMap.get(algorithmURI);
        if (algorithm != null) {
            return algorithm.ivLength;
        }
        return 0;
    }
    
    /**
     * Method getJCEKeyAlgorithmFromURI
     *
     * @param algorithmURI
     * @return The KeyAlgorithm for the given URI.
     */
    public static String getJCEKeyAlgorithmFromURI(String algorithmURI) {
        if (log.isDebugEnabled()) {
            log.debug("Request for URI " + algorithmURI);
        }
        if (algorithmURI != null) {
            Algorithm algorithm = algorithmsMap.get(algorithmURI);
            if (algorithm != null) {
                return algorithm.requiredKey;
            }
        }
        return null;
    }
    
    /**
     * Method getJCEProviderFromURI
     *
     * @param algorithmURI
     * @return The JCEProvider for the given URI.
     */
    public static String getJCEProviderFromURI(String algorithmURI) {
        if (log.isDebugEnabled()) {
            log.debug("Request for URI " + algorithmURI);
        }
        Algorithm algorithm = algorithmsMap.get(algorithmURI);
        if (algorithm != null) {
            return algorithm.jceProvider;
        }
        return null;
    }

    /**
     * Gets the default Provider for obtaining the security algorithms
     * @return the default providerId.  
     */
    public static String getProviderId() {
        return providerName;
    }

    /**
     * Sets the default Provider for obtaining the security algorithms
     * @param provider the default providerId.  
     */
    public static void setProviderId(String provider) {
        providerName = provider;
    }

    /**
     * Represents the Algorithm xml element
     */   
    public static class Algorithm {
        
        final String requiredKey;
        final String jceName;
        final String algorithmClass;
        final int keyLength;
        final int ivLength;
        final String jceProvider;
        
        /**
         * Gets data from element
         * @param el
         */
        public Algorithm(Element el) {
            requiredKey = el.getAttributeNS(null, "RequiredKey");
            jceName = el.getAttributeNS(null, "JCEName");
            algorithmClass = el.getAttributeNS(null, "AlgorithmClass");
            jceProvider = el.getAttributeNS(null, "JCEProvider");
            if (el.hasAttribute("KeyLength")) {
                keyLength = Integer.parseInt(el.getAttributeNS(null, "KeyLength"));
            } else {
                keyLength = 0;
            }
            if (el.hasAttribute("IVLength")) {
                ivLength = Integer.parseInt(el.getAttributeNS(null, "IVLength"));
            } else {
                ivLength = 0;
            }
        }
        
        public Algorithm(String requiredKey, String jceName) {
            this(requiredKey, jceName, null, 0, 0);
        }
        
        public Algorithm(String requiredKey, String jceName, String algorithmClass) {
            this(requiredKey, jceName, algorithmClass, 0, 0);
        }
        
        public Algorithm(String requiredKey, String jceName, int keyLength) {
            this(requiredKey, jceName, null, keyLength, 0);
        }
        
        public Algorithm(String requiredKey, String jceName, String algorithmClass, int keyLength, int ivLength) {
            this(requiredKey, jceName, algorithmClass, keyLength, ivLength, null);
        }
        
        public Algorithm(String requiredKey, String jceName, 
                         String algorithmClass, int keyLength, int ivLength, String jceProvider) {
            this.requiredKey = requiredKey;
            this.jceName = jceName;
            this.algorithmClass = algorithmClass;
            this.keyLength = keyLength;
            this.ivLength = ivLength;
            this.jceProvider = jceProvider;
        }
    }
}

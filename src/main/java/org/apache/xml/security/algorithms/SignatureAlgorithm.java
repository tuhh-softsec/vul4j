/*
 * Copyright 1999-2009 The Apache Software Foundation.
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
package org.apache.xml.security.algorithms;

import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.xml.security.algorithms.implementations.IntegrityHmac;
import org.apache.xml.security.exceptions.AlgorithmAlreadyRegisteredException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.ClassLoaderUtils;
import org.apache.xml.security.utils.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Allows selection of digital signature's algorithm, private keys, other 
 * security parameters, and algorithm's ID.
 *
 * @author Christian Geuer-Pollmann
 */
public class SignatureAlgorithm extends Algorithm {

    /** {@link org.apache.commons.logging} logging facility */
    private static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(SignatureAlgorithm.class);

    /** All available algorithm classes are registered here */
    private static Map<String, Class<SignatureAlgorithmSpi>> algorithmHash = 
        new ConcurrentHashMap<String, Class<SignatureAlgorithmSpi>>();
   
    /** Field signatureAlgorithm */
    private final SignatureAlgorithmSpi signatureAlgorithm;

    private final String algorithmURI;

    /**
     * Constructor SignatureAlgorithm
     *
     * @param doc
     * @param algorithmURI
     * @throws XMLSecurityException
     */
    public SignatureAlgorithm(Document doc, String algorithmURI) throws XMLSecurityException {
        super(doc, algorithmURI);
        this.algorithmURI = algorithmURI;
        
        signatureAlgorithm = getSignatureAlgorithmSpi(algorithmURI);
        signatureAlgorithm.engineGetContextFromElement(this.constructionElement);
    }

    /**
     * Constructor SignatureAlgorithm
     *
     * @param doc
     * @param algorithmURI
     * @param HMACOutputLength
     * @throws XMLSecurityException
     */
    public SignatureAlgorithm(
        Document doc, String algorithmURI, int HMACOutputLength
    ) throws XMLSecurityException {
        super(doc, algorithmURI);
        this.algorithmURI = algorithmURI;
        
        signatureAlgorithm = getSignatureAlgorithmSpi(algorithmURI);
        signatureAlgorithm.engineGetContextFromElement(this.constructionElement);
        
        signatureAlgorithm.engineSetHMACOutputLength(HMACOutputLength);
        ((IntegrityHmac)signatureAlgorithm).engineAddContextToElement(constructionElement);
    }

    /**
     * Constructor SignatureAlgorithm
     *
     * @param element
     * @param BaseURI
     * @throws XMLSecurityException
     */
    public SignatureAlgorithm(Element element, String BaseURI) throws XMLSecurityException {
        super(element, BaseURI);      
        algorithmURI = this.getURI();
        
        signatureAlgorithm = getSignatureAlgorithmSpi(algorithmURI);
        signatureAlgorithm.engineGetContextFromElement(this.constructionElement);
    }

    /**
     * Get a SignatureAlgorithmSpi object corresponding to the algorithmURI argument
     */
    private static SignatureAlgorithmSpi getSignatureAlgorithmSpi(String algorithmURI) 
        throws XMLSignatureException {
        try {
            Class<SignatureAlgorithmSpi> implementingClass = algorithmHash.get(algorithmURI);
            if (log.isDebugEnabled()) {
                log.debug("Create URI \"" + algorithmURI + "\" class \""
                   + implementingClass + "\"");
            }
            return implementingClass.newInstance();   
        }  catch (IllegalAccessException ex) {
            Object exArgs[] = { algorithmURI, ex.getMessage() };
            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs, ex);
        } catch (InstantiationException ex) {
            Object exArgs[] = { algorithmURI, ex.getMessage() };
            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs, ex);
        } catch (NullPointerException ex) {
            Object exArgs[] = { algorithmURI, ex.getMessage() };
            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs, ex);
        }
    }


    /**
     * Proxy method for {@link java.security.Signature#sign()}
     * which is executed on the internal {@link java.security.Signature} object.
     *
     * @return the result of the {@link java.security.Signature#sign()} method
     * @throws XMLSignatureException
     */
    public byte[] sign() throws XMLSignatureException {
        return signatureAlgorithm.engineSign();
    }

    /**
     * Proxy method for {@link java.security.Signature#getAlgorithm}
     * which is executed on the internal {@link java.security.Signature} object.
     *
     * @return the result of the {@link java.security.Signature#getAlgorithm} method
     */
    public String getJCEAlgorithmString() {
        return signatureAlgorithm.engineGetJCEAlgorithmString();
    }

    /**
     * Method getJCEProviderName
     *
     * @return The Provider of this Signature Algorithm
     */
    public String getJCEProviderName() {
        return signatureAlgorithm.engineGetJCEProviderName();
    }

    /**
     * Proxy method for {@link java.security.Signature#update(byte[])}
     * which is executed on the internal {@link java.security.Signature} object.
     *
     * @param input
     * @throws XMLSignatureException
     */
    public void update(byte[] input) throws XMLSignatureException {
        signatureAlgorithm.engineUpdate(input);
    }

    /**
     * Proxy method for {@link java.security.Signature#update(byte)}
     * which is executed on the internal {@link java.security.Signature} object.
     *
     * @param input
     * @throws XMLSignatureException
     */
    public void update(byte input) throws XMLSignatureException {
        signatureAlgorithm.engineUpdate(input);
    }

    /**
     * Proxy method for {@link java.security.Signature#update(byte[], int, int)}
     * which is executed on the internal {@link java.security.Signature} object.
     *
     * @param buf
     * @param offset
     * @param len
     * @throws XMLSignatureException
     */
    public void update(byte buf[], int offset, int len) throws XMLSignatureException {
        signatureAlgorithm.engineUpdate(buf, offset, len);
    }

    /**
     * Proxy method for {@link java.security.Signature#initSign(java.security.PrivateKey)}
     * which is executed on the internal {@link java.security.Signature} object.
     *
     * @param signingKey
     * @throws XMLSignatureException
     */
    public void initSign(Key signingKey) throws XMLSignatureException {	   
        signatureAlgorithm.engineInitSign(signingKey);
    }

    /**
     * Proxy method for {@link java.security.Signature#initSign(java.security.PrivateKey, 
     * java.security.SecureRandom)}
     * which is executed on the internal {@link java.security.Signature} object.
     *
     * @param signingKey
     * @param secureRandom
     * @throws XMLSignatureException
     */
    public void initSign(Key signingKey, SecureRandom secureRandom) throws XMLSignatureException {
        signatureAlgorithm.engineInitSign(signingKey, secureRandom);
    }

    /**
     * Proxy method for {@link java.security.Signature#initSign(java.security.PrivateKey)}
     * which is executed on the internal {@link java.security.Signature} object.
     *
     * @param signingKey
     * @param algorithmParameterSpec
     * @throws XMLSignatureException
     */
    public void initSign(
        Key signingKey, AlgorithmParameterSpec algorithmParameterSpec
    ) throws XMLSignatureException {
        signatureAlgorithm.engineInitSign(signingKey, algorithmParameterSpec);
    }

    /**
     * Proxy method for {@link java.security.Signature#setParameter(
     * java.security.spec.AlgorithmParameterSpec)}
     * which is executed on the internal {@link java.security.Signature} object.
     *
     * @param params
     * @throws XMLSignatureException
     */
    public void setParameter(AlgorithmParameterSpec params) throws XMLSignatureException {
        signatureAlgorithm.engineSetParameter(params);
    }

    /**
     * Proxy method for {@link java.security.Signature#initVerify(java.security.PublicKey)}
     * which is executed on the internal {@link java.security.Signature} object.
     *
     * @param verificationKey
     * @throws XMLSignatureException
     */
    public void initVerify(Key verificationKey) throws XMLSignatureException {
        signatureAlgorithm.engineInitVerify(verificationKey);
    }
    
    /**
     * Proxy method for {@link java.security.Signature#verify(byte[])}
     * which is executed on the internal {@link java.security.Signature} object.
     *
     * @param signature
     * @return true if if the signature is valid.
     * 
     * @throws XMLSignatureException
     */
    public boolean verify(byte[] signature) throws XMLSignatureException {
        return signatureAlgorithm.engineVerify(signature);
    }

    /**
     * Returns the URI representation of Transformation algorithm
     *
     * @return the URI representation of Transformation algorithm
     */
    public final String getURI() {
        return constructionElement.getAttributeNS(null, Constants._ATT_ALGORITHM);
    }

    /**
     * Registers implementing class of the Transform algorithm with algorithmURI
     *
     * @param algorithmURI algorithmURI URI representation of <code>Transform algorithm</code>.
     * @param implementingClass <code>implementingClass</code> the implementing class of 
     * {@link SignatureAlgorithmSpi}
     * @throws AlgorithmAlreadyRegisteredException if specified algorithmURI is already registered
     * @throws XMLSignatureException 
     */
    @SuppressWarnings("unchecked")
    public static void register(String algorithmURI, String implementingClass)
       throws AlgorithmAlreadyRegisteredException, ClassNotFoundException, 
           XMLSignatureException {
        if (log.isDebugEnabled()) {
            log.debug("Try to register " + algorithmURI + " " + implementingClass);
        }

        // are we already registered?
        Class<SignatureAlgorithmSpi> registeredClass = algorithmHash.get(algorithmURI);
        if (registeredClass != null) {
            Object exArgs[] = { algorithmURI, registeredClass };
            throw new AlgorithmAlreadyRegisteredException(
                "algorithm.alreadyRegistered", exArgs
            );
        }
        try {
            Class<SignatureAlgorithmSpi> clazz = 
                (Class<SignatureAlgorithmSpi>)
                    ClassLoaderUtils.loadClass(implementingClass, SignatureAlgorithm.class);
            algorithmHash.put(algorithmURI, clazz);
        } catch (NullPointerException ex) {
            Object exArgs[] = { algorithmURI, ex.getMessage() };
            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs, ex);
        }
    }
    
    /**
     * Registers implementing class of the Transform algorithm with algorithmURI
     *
     * @param algorithmURI algorithmURI URI representation of <code>Transform algorithm</code>.
     * @param implementingClass <code>implementingClass</code> the implementing class of 
     * {@link SignatureAlgorithmSpi}
     * @throws AlgorithmAlreadyRegisteredException if specified algorithmURI is already registered
     * @throws XMLSignatureException 
     */
    public static void register(String algorithmURI, Class<SignatureAlgorithmSpi> implementingClass)
       throws AlgorithmAlreadyRegisteredException, ClassNotFoundException, 
           XMLSignatureException {
        if (log.isDebugEnabled()) {
            log.debug("Try to register " + algorithmURI + " " + implementingClass);
        }

        // are we already registered?
        Class<SignatureAlgorithmSpi> registeredClass = algorithmHash.get(algorithmURI);
        if (registeredClass != null) {
            Object exArgs[] = { algorithmURI, registeredClass };
            throw new AlgorithmAlreadyRegisteredException(
                "algorithm.alreadyRegistered", exArgs
            );
        }
        algorithmHash.put(algorithmURI, implementingClass);
    }

    /**
     * Method getBaseNamespace
     *
     * @return URI of this element
     */
    public String getBaseNamespace() {
        return Constants.SignatureSpecNS;
    }

    /**
     * Method getBaseLocalName
     *
     * @return Local name
     */
    public String getBaseLocalName() {
        return Constants._TAG_SIGNATUREMETHOD;
    }
}

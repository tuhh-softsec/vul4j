/*
 * Copyright  1999-2004 The Apache Software Foundation.
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
import java.util.HashMap;

import org.apache.xml.security.exceptions.AlgorithmAlreadyRegisteredException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Allows selection of digital signature's algorithm, private keys, other security parameters, and algorithm's ID.
 *
 * @author Christian Geuer-Pollmann
 */
public class SignatureAlgorithm extends Algorithm {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(SignatureAlgorithm.class.getName());

   /** Field _alreadyInitialized */
   static boolean _alreadyInitialized = false;

   /** All available algorithm classes are registered here */
   static HashMap _algorithmHash = null;

   /** Field _signatureAlgorithm */
   protected SignatureAlgorithmSpi _signatureAlgorithm = null;

   /**
    * Constructor SignatureAlgorithm
    *
    * @param doc
    * @param algorithmURI
    * @throws XMLSecurityException
    */
   public SignatureAlgorithm(Document doc, String algorithmURI)
           throws XMLSecurityException {

      super(doc, algorithmURI);

      try {
         String implementingClass =
            SignatureAlgorithm.getImplementingClass(algorithmURI);
         if (log.isDebugEnabled())
         	log.debug("Create URI \"" + algorithmURI + "\" class \""
                   + implementingClass + "\"");

         this._signatureAlgorithm =
            (SignatureAlgorithmSpi) Class.forName(implementingClass)
               .newInstance();
      } catch (ClassNotFoundException ex) {
         Object exArgs[] = { algorithmURI, ex.getMessage() };

         throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs,
                                         ex);
      } catch (IllegalAccessException ex) {
         Object exArgs[] = { algorithmURI, ex.getMessage() };

         throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs,
                                         ex);
      } catch (InstantiationException ex) {
         Object exArgs[] = { algorithmURI, ex.getMessage() };

         throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs,
                                         ex);
      } catch (NullPointerException ex) {
         Object exArgs[] = { algorithmURI, ex.getMessage() };

         throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs,
                                         ex);
      }
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
           Document doc, String algorithmURI, int HMACOutputLength)
              throws XMLSecurityException {

      this(doc, algorithmURI);

      this._signatureAlgorithm.engineSetHMACOutputLength(HMACOutputLength);
      this._signatureAlgorithm
         .engineAddContextToElement(this._constructionElement);
   }

   /**
    * Constructor SignatureAlgorithm
    *
    * @param element
    * @param BaseURI
    * @throws XMLSecurityException
    */
   public SignatureAlgorithm(Element element, String BaseURI)
           throws XMLSecurityException {

      super(element, BaseURI);

      String algorithmURI = this.getURI();

      try {
         String implementingClass =
            SignatureAlgorithm.getImplementingClass(algorithmURI);
         if (log.isDebugEnabled())
         	log.debug("Create URI \"" + algorithmURI + "\" class \""
                   + implementingClass + "\"");

         this._signatureAlgorithm =
            (SignatureAlgorithmSpi) Class.forName(implementingClass)
               .newInstance();

         this._signatureAlgorithm
            .engineGetContextFromElement(this._constructionElement);
      } catch (ClassNotFoundException ex) {
         Object exArgs[] = { algorithmURI, ex.getMessage() };

         throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs,
                                         ex);
      } catch (IllegalAccessException ex) {
         Object exArgs[] = { algorithmURI, ex.getMessage() };

         throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs,
                                         ex);
      } catch (InstantiationException ex) {
         Object exArgs[] = { algorithmURI, ex.getMessage() };

         throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs,
                                         ex);
      } catch (NullPointerException ex) {
         Object exArgs[] = { algorithmURI, ex.getMessage() };

         throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs,
                                         ex);
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
      return this._signatureAlgorithm.engineSign();
   }

   /**
    * Proxy method for {@link java.security.Signature#getAlgorithm}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @return the result of the {@link java.security.Signature#getAlgorithm} method
    */
   public String getJCEAlgorithmString() {
      return this._signatureAlgorithm.engineGetJCEAlgorithmString();
   }

   /**
    * Method getJCEProviderName
    *
    * @return
    */
   public String getJCEProviderName() {
      return this._signatureAlgorithm.engineGetJCEProviderName();
   }

   /**
    * Proxy method for {@link java.security.Signature#update(byte[])}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param input
    * @throws XMLSignatureException
    */
   public void update(byte[] input) throws XMLSignatureException {
      this._signatureAlgorithm.engineUpdate(input);
   }

   /**
    * Proxy method for {@link java.security.Signature#update(byte)}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param input
    * @throws XMLSignatureException
    */
   public void update(byte input) throws XMLSignatureException {
      this._signatureAlgorithm.engineUpdate(input);
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
   public void update(byte buf[], int offset, int len)
           throws XMLSignatureException {
      this._signatureAlgorithm.engineUpdate(buf, offset, len);
   }

   /**
    * Proxy method for {@link java.security.Signature#initSign(java.security.PrivateKey)}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param signingKey
    * @throws XMLSignatureException
    */
   public void initSign(Key signingKey) throws XMLSignatureException {
      this._signatureAlgorithm.engineInitSign(signingKey);
   }

   /**
    * Proxy method for {@link java.security.Signature#initSign(java.security.PrivateKey, java.security.SecureRandom)}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param signingKey
    * @param secureRandom
    * @throws XMLSignatureException
    */
   public void initSign(Key signingKey, SecureRandom secureRandom)
           throws XMLSignatureException {
      this._signatureAlgorithm.engineInitSign(signingKey, secureRandom);
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
           Key signingKey, AlgorithmParameterSpec algorithmParameterSpec)
              throws XMLSignatureException {
      this._signatureAlgorithm.engineInitSign(signingKey,
                                              algorithmParameterSpec);
   }

   /**
    * Proxy method for {@link java.security.Signature#setParameter(java.security.spec.AlgorithmParameterSpec)}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param params
    * @throws XMLSignatureException
    */
   public void setParameter(AlgorithmParameterSpec params)
           throws XMLSignatureException {
      this._signatureAlgorithm.engineSetParameter(params);
   }

   /**
    * Proxy method for {@link java.security.Signature#initVerify(java.security.PublicKey)}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param verificationKey
    * @throws XMLSignatureException
    */
   public void initVerify(Key verificationKey) throws XMLSignatureException {
      this._signatureAlgorithm.engineInitVerify(verificationKey);
   }

   /**
    * Proxy method for {@link java.security.Signature#verify(byte[])}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param signature
    * @return
    * 
    * @throws XMLSignatureException
    */
   public boolean verify(byte[] signature) throws XMLSignatureException {
      return this._signatureAlgorithm.engineVerify(signature);
   }

   /**
    * Returns the URI representation of Transformation algorithm
    *
    * @return the URI representation of Transformation algorithm
    */
   public final String getURI() {
      return this._constructionElement.getAttributeNS(null,
              Constants._ATT_ALGORITHM);
   }

   /**
    * Initalizes for this {@link org.apache.xml.security.transforms.Transform}
    *
    */
   public static void providerInit() {

      if (SignatureAlgorithm.log == null) {
         SignatureAlgorithm.log =
            org.apache.commons.logging.LogFactory
               .getLog(SignatureAlgorithm.class.getName());
      }

      log.debug("Init() called");

      if (!SignatureAlgorithm._alreadyInitialized) {
         SignatureAlgorithm._algorithmHash = new HashMap(10);
         SignatureAlgorithm._alreadyInitialized = true;
      }
   }

   /**
    * Registers implementing class of the Transform algorithm with algorithmURI
    *
    * @param algorithmURI algorithmURI URI representation of <code>Transform algorithm</code>.
    * @param implementingClass <code>implementingClass</code> the implementing class of {@link SignatureAlgorithmSpi}
    * @throws AlgorithmAlreadyRegisteredException if specified algorithmURI is already registered
    */
   public static void register(String algorithmURI, String implementingClass)
           throws AlgorithmAlreadyRegisteredException {

      {
         if (log.isDebugEnabled())
         	log.debug("Try to register " + algorithmURI + " " + implementingClass);

         // are we already registered?
         String registeredClass =
            SignatureAlgorithm.getImplementingClass(algorithmURI);

         if ((registeredClass != null) && (registeredClass.length() != 0)) {
            Object exArgs[] = { algorithmURI, registeredClass };

            throw new AlgorithmAlreadyRegisteredException(
               "algorithm.alreadyRegistered", exArgs);
         }

         SignatureAlgorithm._algorithmHash.put(algorithmURI, implementingClass);
      }
   }

   /**
    * Method getImplementingClass
    *
    * @param URI
    * @return
    */
   private static String getImplementingClass(String URI) {

      if (SignatureAlgorithm._algorithmHash == null) {
         return null;
      }

      return (String) SignatureAlgorithm._algorithmHash.get(URI);
   }

   /**
    * Method getBaseNamespace
    *
    * @return
    */
   public String getBaseNamespace() {
      return Constants.SignatureSpecNS;
   }

   /**
    * Method getBaseLocalName
    *
    * @return
    */
   public String getBaseLocalName() {
      return Constants._TAG_SIGNATUREMETHOD;
   }
}

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
package org.apache.xml.security.algorithms.implementations;



import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;

import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.algorithms.SignatureAlgorithmSpi;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;


/**
 *
 * @author $Author: raul $
 */
public abstract class SignatureECDSA extends SignatureAlgorithmSpi {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(SignatureECDSA.class.getName());

    /** @inheritDoc */
   public abstract String engineGetURI();

   /** Field algorithm */
   private java.security.Signature _signatureAlgorithm = null;

   /**
    * Constructor SignatureRSA
    *
    * @throws XMLSignatureException
    */
   public SignatureECDSA() throws XMLSignatureException {

      String algorithmID = JCEMapper.translateURItoJCEID(this.engineGetURI());

      if (log.isDebugEnabled())
      	log.debug("Created SignatureECDSA using " + algorithmID);
      String provider=JCEMapper.getProviderId();
      try {
      	 if (provider==null) {
      	 	this._signatureAlgorithm = Signature.getInstance(algorithmID);
      	 } else {
      	 	this._signatureAlgorithm = Signature.getInstance(algorithmID,provider);
      	 }
      } catch (java.security.NoSuchAlgorithmException ex) {
         Object[] exArgs = { algorithmID,
                             ex.getLocalizedMessage() };

         throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs);
      } catch (NoSuchProviderException ex) {
      	 Object[] exArgs = { algorithmID,
      	 					 ex.getLocalizedMessage() };

      	 throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs);
	}
   }

   /** @inheritDoc */
   protected void engineSetParameter(AlgorithmParameterSpec params)
           throws XMLSignatureException {

      try {
         this._signatureAlgorithm.setParameter(params);
      } catch (InvalidAlgorithmParameterException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /** @inheritDoc */
   protected boolean engineVerify(byte[] signature)
           throws XMLSignatureException {

      try {
         return this._signatureAlgorithm.verify(signature);
      } catch (SignatureException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /** @inheritDoc */
   protected void engineInitVerify(Key publicKey) throws XMLSignatureException {

      if (!(publicKey instanceof PublicKey)) {
         String supplied = publicKey.getClass().getName();
         String needed = PublicKey.class.getName();
         Object exArgs[] = { supplied, needed };

         throw new XMLSignatureException("algorithms.WrongKeyForThisOperation",
                                         exArgs);
      }

      try {
         this._signatureAlgorithm.initVerify((PublicKey) publicKey);
      } catch (InvalidKeyException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /** @inheritDoc */
   protected byte[] engineSign() throws XMLSignatureException {

      try {
         return this._signatureAlgorithm.sign();
      } catch (SignatureException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /** @inheritDoc */
   protected void engineInitSign(Key privateKey, SecureRandom secureRandom)
           throws XMLSignatureException {

      if (!(privateKey instanceof PrivateKey)) {
         String supplied = privateKey.getClass().getName();
         String needed = PrivateKey.class.getName();
         Object exArgs[] = { supplied, needed };

         throw new XMLSignatureException("algorithms.WrongKeyForThisOperation",
                                         exArgs);
      }

      try {
         this._signatureAlgorithm.initSign((PrivateKey) privateKey,
                                           secureRandom);
      } catch (InvalidKeyException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /** @inheritDoc */
   protected void engineInitSign(Key privateKey) throws XMLSignatureException {

      if (!(privateKey instanceof PrivateKey)) {
         String supplied = privateKey.getClass().getName();
         String needed = PrivateKey.class.getName();
         Object exArgs[] = { supplied, needed };

         throw new XMLSignatureException("algorithms.WrongKeyForThisOperation",
                                         exArgs);
      }

      try {
         this._signatureAlgorithm.initSign((PrivateKey) privateKey);
      } catch (InvalidKeyException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /** @inheritDoc */
   protected void engineUpdate(byte[] input) throws XMLSignatureException {

      try {
         this._signatureAlgorithm.update(input);
      } catch (SignatureException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /** @inheritDoc */
   protected void engineUpdate(byte input) throws XMLSignatureException {

      try {
         this._signatureAlgorithm.update(input);
      } catch (SignatureException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /** @inheritDoc */
   protected void engineUpdate(byte buf[], int offset, int len)
           throws XMLSignatureException {

      try {
         this._signatureAlgorithm.update(buf, offset, len);
      } catch (SignatureException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /** @inheritDoc */
   protected String engineGetJCEAlgorithmString() {
      return this._signatureAlgorithm.getAlgorithm();
   }

   /** @inheritDoc */
   protected String engineGetJCEProviderName() {
      return this._signatureAlgorithm.getProvider().getName();
   }

   /** @inheritDoc */
   protected void engineSetHMACOutputLength(int HMACOutputLength)
           throws XMLSignatureException {
      throw new XMLSignatureException("algorithms.HMACOutputLengthOnlyForHMAC");
   }

   /** @inheritDoc */
   protected void engineInitSign(
           Key signingKey, AlgorithmParameterSpec algorithmParameterSpec)
              throws XMLSignatureException {
      throw new XMLSignatureException(
         "algorithms.CannotUseAlgorithmParameterSpecOnRSA");
   }

   /**
    * Class SignatureRSASHA1
    *
    * @author $Author: marcx $
    * @version $Revision: 351176 $
    */
   public static class SignatureECDSASHA1 extends SignatureECDSA {

      /**
       * Constructor SignatureRSASHA1
       *
       * @throws XMLSignatureException
       */
      public SignatureECDSASHA1() throws XMLSignatureException {
         super();
      }

      /** @inheritDoc */
      public String engineGetURI() {
         return XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA1;
      }
   }

}

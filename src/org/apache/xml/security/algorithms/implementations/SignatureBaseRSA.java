/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "<WebSig>" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001, Institute for
 * Data Communications Systems, <http://www.nue.et-inf.uni-siegen.de/>.
 * The development of this software was partly funded by the European
 * Commission in the <WebSig> project in the ISIS Programme.
 * For more information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xml.security.algorithms.implementations;



import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.Signature;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import org.apache.xml.security.algorithms.*;
import org.apache.xml.security.signature.*;
import org.apache.xml.security.utils.*;
import org.w3c.dom.*;


/**
 *
 * @author $Author$
 */
public abstract class SignatureBaseRSA extends SignatureAlgorithmSpi {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(SignatureBaseRSA.class.getName());

   /**
    * Method engineGetURI
    *
    * @return
    */
   public abstract String engineGetURI();

   /** Field algorithm */
   private java.security.Signature _signatureAlgorithm = null;

   /**
    * Constructor SignatureRSA
    *
    * @throws XMLSignatureException
    */
   public SignatureBaseRSA() throws XMLSignatureException {

      JCEMapper.ProviderIdClass algorithmID =
         JCEMapper.translateURItoJCEID(this.engineGetURI());

      cat.debug("Created SignatureDSA using " + algorithmID.getAlgorithmID()
                + " " + algorithmID.getProviderId());

      try {
         this._signatureAlgorithm =
            Signature.getInstance(algorithmID.getAlgorithmID(),
                                  algorithmID.getProviderId());
      } catch (java.security.NoSuchAlgorithmException ex) {
         Object[] exArgs = { algorithmID.getAlgorithmID(),
                             ex.getLocalizedMessage() };

         throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs);
      } catch (java.security.NoSuchProviderException ex) {
         Object[] exArgs = { algorithmID.getProviderId(),
                             ex.getLocalizedMessage() };

         throw new XMLSignatureException("algorithms.NoSuchProvider", exArgs);
      }
   }

   /**
    * Proxy method for {@link java.security.Signature#setParameter}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param params
    * @throws XMLSignatureException
    */
   protected void engineSetParameter(AlgorithmParameterSpec params)
           throws XMLSignatureException {

      try {
         this._signatureAlgorithm.setParameter(params);
      } catch (InvalidAlgorithmParameterException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /**
    * Proxy method for {@link java.security.Signature#verify}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param signature
    * @return
    * @throws XMLSignatureException
    */
   protected boolean engineVerify(byte[] signature)
           throws XMLSignatureException {

      try {
         return this._signatureAlgorithm.verify(signature);
      } catch (SignatureException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /**
    * Proxy method for {@link java.security.Signature#initVerify}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param publickey
    * @throws XMLSignatureException
    */
   protected void engineInitVerify(PublicKey publickey)
           throws XMLSignatureException {

      try {
         this._signatureAlgorithm.initVerify((PublicKey) publickey);
      } catch (InvalidKeyException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /**
    * Proxy method for {@link java.security.Signature#sign}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @return the result of the {@link java.security.Signature#sign} method
    * @throws XMLSignatureException
    */
   protected byte[] engineSign() throws XMLSignatureException {

      try {
         return this._signatureAlgorithm.sign();
      } catch (SignatureException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /**
    * Proxy method for {@link java.security.Signature#initSign}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param privateKey
    * @param secureRandom
    * @throws XMLSignatureException
    */
   protected void engineInitSign(
           PrivateKey privateKey, SecureRandom secureRandom)
              throws XMLSignatureException {

      try {
         this._signatureAlgorithm.initSign((PrivateKey) privateKey,
                                           secureRandom);
      } catch (InvalidKeyException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /**
    * Proxy method for {@link java.security.Signature#initSign}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param privateKey
    * @throws XMLSignatureException
    */
   protected void engineInitSign(PrivateKey privateKey)
           throws XMLSignatureException {

      try {
         this._signatureAlgorithm.initSign((PrivateKey) privateKey);
      } catch (InvalidKeyException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /**
    * Proxy method for {@link java.security.Signature#update}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param input
    * @throws XMLSignatureException
    */
   protected void engineUpdate(byte[] input) throws XMLSignatureException {

      try {
         this._signatureAlgorithm.update(input);
      } catch (SignatureException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /**
    * Proxy method for {@link java.security.Signature#update}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param input
    * @throws XMLSignatureException
    */
   protected void engineUpdate(byte input) throws XMLSignatureException {

      try {
         this._signatureAlgorithm.update(input);
      } catch (SignatureException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /**
    * Proxy method for {@link java.security.Signature#update}
    * which is executed on the internal {@link java.security.Signature} object.
    *
    * @param buf
    * @param offset
    * @param len
    * @throws XMLSignatureException
    */
   protected void engineUpdate(byte buf[], int offset, int len)
           throws XMLSignatureException {

      try {
         this._signatureAlgorithm.update(buf, offset, len);
      } catch (SignatureException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /**
    * Method engineGetJCEAlgorithmString
    *
    * @return
    */
   protected String engineGetJCEAlgorithmString() {
      return this._signatureAlgorithm.getAlgorithm();
   }

   /**
    * Method engineGetJCEProviderName
    *
    * @return
    */
   protected String engineGetJCEProviderName() {
      return this._signatureAlgorithm.getProvider().getName();
   }

   /**
    * Method engineSetHMACOutputLength
    *
    * @param HMACOutputLength
    * @throws XMLSignatureException
    */
   protected void engineSetHMACOutputLength(int HMACOutputLength)
           throws XMLSignatureException {
      throw new XMLSignatureException("algorithms.HMACOutputLengthOnlyForHMAC");
   }

   /**
    * Method engineInitVerify
    *
    * @param secretkey
    * @throws XMLSignatureException
    */
   protected void engineInitVerify(Key secretkey) throws XMLSignatureException {
      throw new XMLSignatureException("algorithms.operationOnlyForMAC");
   }

   /**
    * Method engineInitSign
    *
    * @param secretKey
    * @param algorithmParameterSpec
    * @throws XMLSignatureException
    */
   protected void engineInitSign(
           Key secretKey, AlgorithmParameterSpec algorithmParameterSpec)
              throws XMLSignatureException {
      throw new XMLSignatureException("algorithms.operationOnlyForMAC");
   }

   /**
    * Method engineInitSign
    *
    * @param secretKey
    * @throws XMLSignatureException
    */
   protected void engineInitSign(Key secretKey) throws XMLSignatureException {
      throw new XMLSignatureException("algorithms.operationOnlyForMAC");
   }

   public static class SignatureRSASHA1 extends SignatureBaseRSA {
      public SignatureRSASHA1() throws XMLSignatureException {
         super();
      }
      public String engineGetURI() {
         return XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1;
      }
   }
   public static class SignatureRSASHA256 extends SignatureBaseRSA {
      public SignatureRSASHA256() throws XMLSignatureException {
         super();
      }
      public String engineGetURI() {
         return XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256;
      }
   }
   public static class SignatureRSASHA384 extends SignatureBaseRSA {
      public SignatureRSASHA384() throws XMLSignatureException {
         super();
      }
      public String engineGetURI() {
         return XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA384;
      }
   }
   public static class SignatureRSASHA512 extends SignatureBaseRSA {
      public SignatureRSASHA512() throws XMLSignatureException {
         super();
      }
      public String engineGetURI() {
         return XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA512;
      }
   }
   public static class SignatureRSARIPEMD160 extends SignatureBaseRSA {
      public SignatureRSARIPEMD160() throws XMLSignatureException {
         super();
      }
      public String engineGetURI() {
         return XMLSignature.ALGO_ID_SIGNATURE_RSA_RIPEMD160;
      }
   }
   public static class SignatureRSAMD5 extends SignatureBaseRSA {
      public SignatureRSAMD5() throws XMLSignatureException {
         super();
      }
      public String engineGetURI() {
         return XMLSignature.ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5;
      }
   }
}

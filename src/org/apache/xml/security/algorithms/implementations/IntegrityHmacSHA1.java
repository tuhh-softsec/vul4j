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



import org.w3c.dom.*;
import org.apache.xml.security.algorithms.*;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.signature.*;
import java.io.IOException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.InvalidKeyException;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import org.apache.xml.security.signature.XMLSignatureException;
import javax.crypto.Mac;
import javax.xml.transform.TransformerException;
import org.apache.xpath.XPathAPI;


/**
 *
 * @author $Author$
 */
public class IntegrityHmacSHA1 extends SignatureAlgorithmSpi {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(IntegrityHmacSHA1.class.getName());

   /** Field _URI */
   public static final String _URI = Constants.SignatureSpecNS + "hmac-sha1";

   /** Field _macAlgorithm */
   private Mac _macAlgorithm = null;

   /** Field _HMACOutputLength */
   int _HMACOutputLength = 0;

   /**
    * Method engineGetURI
    *
    * @return
    */
   protected String engineGetURI() {
      return this._URI;
   }

   /**
    * Method IntegrityHmacSHA1das
    *
    * @throws XMLSignatureException
    */
   public IntegrityHmacSHA1() throws XMLSignatureException {

      JCEMapper.ProviderIdClass algorithmID =
         JCEMapper.translateURItoJCEID(this._URI);

      cat.debug("Created IntegrityHmacSHA1 using "
                + algorithmID.getAlgorithmID() + " "
                + algorithmID.getProviderId());

      try {
         this._macAlgorithm = Mac.getInstance(algorithmID.getAlgorithmID(),
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
      throw new XMLSignatureException("empty");
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
         byte[] completeResult = this._macAlgorithm.doFinal();

         if ((this._HMACOutputLength == 0) || (this._HMACOutputLength >= 160)) {
            cat.debug("completeResult = " + HexDump.byteArrayToHexString(completeResult));
            cat.debug("signature      = " + HexDump.byteArrayToHexString(signature));

            return MessageDigestAlgorithm.isEqual(completeResult, signature);
         } else {
            cat.debug("completeResult = " + HexDump.byteArrayToHexString(completeResult));
            byte[] stripped = IntegrityHmacSHA1.reduceBitLength(completeResult,
                                 this._HMACOutputLength);

            cat.debug("stripped       = " + HexDump.byteArrayToHexString(stripped));
            cat.debug("signature      = " + HexDump.byteArrayToHexString(signature));

            return MessageDigestAlgorithm.isEqual(stripped, signature);
         }
      } catch (IllegalStateException ex) {
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
   protected void engineInitVerify(Key publickey) throws XMLSignatureException {

      try {
         this._macAlgorithm.init(publickey);
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
         byte[] completeResult = this._macAlgorithm.doFinal();

         if ((this._HMACOutputLength == 0) || (this._HMACOutputLength >= 160)) {
            return completeResult;
         } else {
            return IntegrityHmacSHA1.reduceBitLength(completeResult,
                                                     this._HMACOutputLength);
         }
      } catch (IllegalStateException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /**
    * Method reduceBitLength
    *
    * @param completeResult
    * @param length
    * @return
    */
   private static byte[] reduceBitLength(byte completeResult[], int length) {

      int bytes = length / 8;
      int abits = length % 8;
      byte[] strippedResult = new byte[bytes + (abits == 0 ? 0 : 1)];

      System.arraycopy(completeResult, 0, strippedResult, 0, bytes);

      if (abits > 0) {
         byte[] MASK = { (byte) 0x00, (byte) 0x80, (byte) 0xC0, (byte) 0xE0,
                         (byte) 0xF0, (byte) 0xF8, (byte) 0xFC, (byte) 0xFE };

         strippedResult[bytes] = (byte) (completeResult[bytes] & MASK[abits]);
      }

      return strippedResult;
   }

   /**
    * Method engineInitSign
    *
    * @param secretKey
    * @throws XMLSignatureException
    */
   protected void engineInitSign(Key secretKey) throws XMLSignatureException {

      try {
         this._macAlgorithm.init(secretKey);
      } catch (InvalidKeyException ex) {
         throw new XMLSignatureException("empty", ex);
      }
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

      try {
         this._macAlgorithm.init(secretKey, algorithmParameterSpec);
      } catch (InvalidKeyException ex) {
         throw new XMLSignatureException("empty", ex);
      } catch (InvalidAlgorithmParameterException ex) {
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
         this._macAlgorithm.update(input);
      } catch (IllegalStateException ex) {
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
         this._macAlgorithm.update(input);
      } catch (IllegalStateException ex) {
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
         this._macAlgorithm.update(buf, offset, len);
      } catch (IllegalStateException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /**
    * Method engineGetJCEAlgorithmString
    *
    * @return
    */
   protected String engineGetJCEAlgorithmString() {

      cat.debug("engineGetJCEAlgorithmString()");

      return this._macAlgorithm.getAlgorithm();
   }

   /**
    * Method engineGetJCEAlgorithmString
    *
    * @return
    */
   protected String engineGetJCEProviderName() {
      return this._macAlgorithm.getProvider().getName();
   }

   /**
    * Method engineSetHMACOutputLength
    *
    * @param HMACOutputLength
    */
   protected void engineSetHMACOutputLength(int HMACOutputLength) {
      this._HMACOutputLength = HMACOutputLength;
   }

   /**
    * Method engineGetContextFromElement
    *
    * @param element
    * @throws XMLSignatureException
    */
   protected void engineGetContextFromElement(Element element)
           throws XMLSignatureException {

      super.engineGetContextFromElement(element);

      if (element == null) {
         throw new XMLSignatureException("empty");
      }

      if ((element.getChildNodes() != null)
              && (element.getChildNodes().getLength() > 0)) {
         try {
            Element nscontext = XMLUtils.createDSctx(element.getOwnerDocument(),
                                                     "ds",
                                                     Constants.SignatureSpecNS);
            Text hmaclength = (Text) XPathAPI.selectSingleNode(element,
                                 "./ds:" + Constants._TAG_HMACOUTPUTLENGTH
                                 + "/text()", nscontext);

            if (hmaclength != null) {
               this._HMACOutputLength = Integer.parseInt(hmaclength.getData());
            }
         } catch (TransformerException ex) {
            throw new XMLSignatureException("empty", ex);
         }
      }
   }

   /**
    * Method engineAddContextToElement
    *
    * @param element
    * @throws XMLSignatureException
    */
   protected void engineAddContextToElement(Element element)
           throws XMLSignatureException {

      if (element == null) {
         throw new XMLSignatureException("empty");
      }

      if (this._HMACOutputLength != 0) {
         Document doc = element.getOwnerDocument();
         Element HMElem = XMLUtils.createElementInSignatureSpace(doc,
                             Constants._TAG_HMACOUTPUTLENGTH);
         Text HMText =
            doc.createTextNode(new Integer(this._HMACOutputLength).toString());

         HMElem.appendChild(HMText);
         element.appendChild(doc.createTextNode("\n"));
         element.appendChild(HMElem);
         element.appendChild(doc.createTextNode("\n"));
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
      throw new XMLSignatureException("algorithms.operationOnlyForSignature");
   }

   /**
    * Method engineInitSign
    *
    * @param privateKey
    * @throws XMLSignatureException
    */
   protected void engineInitSign(PrivateKey privateKey)
           throws XMLSignatureException {
      throw new XMLSignatureException("algorithms.operationOnlyForSignature");
   }

   /**
    * Method engineInitVerify
    *
    * @param publicKey
    * @throws XMLSignatureException
    */
   protected void engineInitVerify(PublicKey publicKey)
           throws XMLSignatureException {
      throw new XMLSignatureException("algorithms.operationOnlyForSignature");
   }

   static {
      org.apache.xml.security.Init.init();
   }
}

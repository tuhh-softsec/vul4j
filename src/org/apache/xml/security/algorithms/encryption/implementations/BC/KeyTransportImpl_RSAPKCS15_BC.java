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
package org.apache.xml.security.algorithms.encryption.implementations.BC;



import java.io.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import org.apache.xml.security.algorithms.*;
import org.apache.xml.security.algorithms.encryption.*;
import org.apache.xml.security.algorithms.encryption.helper.*;
import org.apache.xml.security.algorithms.encryption.params.*;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.EncryptionConstants;
import org.apache.xml.security.utils.PRNG;
import org.w3c.dom.*;


/**
 *
 * @author $Author$
 */
public class KeyTransportImpl_RSAPKCS15_BC extends KeyTransportImpl {

   /** Field _cipher */
   PKCS15Cipher _cipher = null;

   /**
    * Method engineGetIvLength
    *
    *
    */
   public int engineGetIvLength() {
      return -1;
   }

   /**
    * Method getImplementedAlgorithmURI
    *
    *
    */
   public String getImplementedAlgorithmURI() {
      return EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15;
   }

   /**
    * Method engineGetBlockSize
    *
    *
    */
   public int engineGetBlockSize() {
      if (this._cipher != null) {
         return this._cipher.getInputBlockSize();
      } else {
         return 0;
      }
   }

   /**
    * Method engineInit
    *
    * @param doc
    * @param params
    * @throws XMLSecurityException
    */
   public EncryptionMethodParams engineInit(Document doc, EncryptionMethodParams params)
           throws XMLSecurityException {

      this._doc = doc;

      if (params != null) {
         throw new XMLSecurityException("empty");
      }

      try {
         Cipher rsaCipher =
            Cipher.getInstance(this.getImplementedAlgorithmJCE(),
                               this.getRequiredProviderName());

         this._cipher = new PKCS15Cipher(rsaCipher);
      } catch (NoSuchAlgorithmException ex) {
         throw new XMLSecurityException("empty", ex);
      } catch (NoSuchProviderException ex) {
         throw new XMLSecurityException("empty", ex);
      } catch (NoSuchPaddingException ex) {
         throw new XMLSecurityException("empty", ex);
      }

      return params;
   }

   public EncryptionMethodParams engineInit(Element encryptionMethodElem)
              throws org.apache.xml.security.exceptions.XMLSecurityException {

      if (encryptionMethodElem.getChildNodes().getLength() != 0) {
         throw new XMLSecurityException(
            "encryption.algorithmCannotEatInitParams");
      }

      return null;
   }

   /**
    * Method engineWrap
    *
    * @param contentKey
    * @param wrapKey
    *
    * @throws XMLSecurityException
    */
   public byte[] engineWrap(Key contentKey, Key wrapKey)
           throws XMLSecurityException {

      try {
         this._cipher.init(Cipher.ENCRYPT_MODE, wrapKey,
                           PRNG.getInstance().getSecureRandom());

         byte contentKeyBytes[] = contentKey.getEncoded();

         return this._cipher.encodeBlock(contentKeyBytes, 0,
                                         contentKeyBytes.length);
      } catch (BadPaddingException ex) {
         throw new XMLSecurityException("empty", ex);
      } catch (InvalidKeyException ex) {
         throw new XMLSecurityException("empty", ex);
      } catch (IllegalBlockSizeException ex) {
         throw new XMLSecurityException("empty", ex);
      }
   }

   /**
    * Method engineUnwrap
    *
    * @param wrappedKey
    * @param wrapKey
    * @param wrappedKeyAlgoURI
    *
    * @throws XMLSecurityException
    */
   public Key engineUnwrap(
           byte[] wrappedKey, Key wrapKey, String wrappedKeyAlgoURI)
              throws XMLSecurityException {

      try {
         this._cipher.init(Cipher.DECRYPT_MODE, wrapKey, null);

         String keyAlgorithm =
            JCEMapper
               .translateURItoJCEID(wrappedKeyAlgoURI, this
                  .getRequiredProviderName()).getAlgorithmID();
         int keyType = JCEMapper.getKeyTypeFromURI(wrappedKeyAlgoURI);
         byte[] decoded = this._cipher.decodeBlock(wrappedKey, 0,
                                                   wrappedKey.length);

         if (keyType == Cipher.SECRET_KEY) {
            return new SecretKeySpec(decoded, keyAlgorithm);
         } else {
            try {
               KeyFactory kf =
                  KeyFactory.getInstance(keyAlgorithm,
                                         this.getRequiredProviderName());

               if (keyType == Cipher.PUBLIC_KEY) {
                  return kf.generatePublic(new X509EncodedKeySpec(decoded));
               } else if (keyType == Cipher.PRIVATE_KEY) {
                  return kf.generatePrivate(new PKCS8EncodedKeySpec(decoded));
               }
            } catch (NoSuchProviderException e) {
               throw new InvalidKeyException("Unknown key type "
                                             + e.getMessage());
            } catch (NoSuchAlgorithmException e) {
               throw new InvalidKeyException("Unknown key type "
                                             + e.getMessage());
            } catch (InvalidKeySpecException e2) {
               throw new InvalidKeyException("Unknown key type "
                                             + e2.getMessage());
            }

            throw new InvalidKeyException("Unknown key type " + keyType);
         }
      } catch (BadPaddingException ex) {
         throw new XMLSecurityException("empty", ex);
      } catch (InvalidKeyException ex) {
         throw new XMLSecurityException("empty", ex);
      } catch (IllegalBlockSizeException ex) {
         throw new XMLSecurityException("empty", ex);
      }
   }

}



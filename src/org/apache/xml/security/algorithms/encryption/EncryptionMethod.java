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
package org.apache.xml.security.algorithms.encryption;



import java.io.IOException;
import java.security.*;
import java.security.spec.*;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import javax.xml.transform.TransformerException;
import org.apache.xml.security.algorithms.Algorithm;
import org.apache.xml.security.algorithms.encryption.params
   .EncryptionMethodParams;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.exceptions.AlgorithmAlreadyRegisteredException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.*;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.*;


/**
 * This is the representation of an <CODE>xenc:EncryptionMethod</CODE>
 * element. It's additionally the factory for encryption/wrap/transport
 * algorithms.
 *
 * @author $Author$
 */
public class EncryptionMethod extends Algorithm {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(EncryptionMethod.class.getName());

   /** Field _emSpi */
   EncryptionMethodSpi _emSpi = null;

   /** Field _encMethodParams */
   EncryptionMethodParams _encMethodParams = null;

   /**
    * Method getBaseNamespace
    *
    * @return
    */
   public String getBaseNamespace() {
      return EncryptionConstants.EncryptionSpecNS;
   }

   /**
    * Method getBaseLocalName
    *
    * @return
    */
   public String getBaseLocalName() {
      return EncryptionConstants._TAG_ENCRYPTIONMETHOD;
   }

   /**
    * Constructor EncryptionMethod
    *
    * @param doc
    * @param algorithmURI
    * @throws XMLSecurityException
    */
   public EncryptionMethod(Document doc, String algorithmURI)
           throws XMLSecurityException {
      this(doc, algorithmURI, null);
   }

   /**
    * Method getUsableInEncryptedData
    *
    * @return
    */
   public boolean getUsableInEncryptedData() {

      int type = this._emSpi.getImplementedAlgorithmType();

      switch (type) {

      case EncryptionMethodSpi.ALGOTYPE_BLOCK_ENCRYPTION :
         return true;

      case EncryptionMethodSpi.ALGOTYPE_STREAM_ENCRYPTION :
         return true;

      case EncryptionMethodSpi.ALGOTYPE_SYMMETRIC_KEY_WRAP :
         return false;

      case EncryptionMethodSpi.ALGOTYPE_KEY_TRANSPORT :
         return false;

      case EncryptionMethodSpi.ALGOTYPE_KEY_AGREEMENT :
         return false;

      default :
         return false;
      }
   }

   /**
    * Method getUsableInEncryptedKey
    *
    * @return
    */
   public boolean getUsableInEncryptedKey() {

      int type = this._emSpi.getImplementedAlgorithmType();

      switch (type) {

      case EncryptionMethodSpi.ALGOTYPE_BLOCK_ENCRYPTION :
         return false;

      case EncryptionMethodSpi.ALGOTYPE_STREAM_ENCRYPTION :
         return false;

      case EncryptionMethodSpi.ALGOTYPE_SYMMETRIC_KEY_WRAP :
         return true;

      case EncryptionMethodSpi.ALGOTYPE_KEY_TRANSPORT :
         return true;

      case EncryptionMethodSpi.ALGOTYPE_KEY_AGREEMENT :
         return true;

      default :
         return false;
      }
   }

   /**
    * Constructor EncryptionMethod
    *
    * @param doc
    * @param algorithmURI
    * @param params
    * @throws XMLSecurityException
    */
   public EncryptionMethod(
           Document doc, String algorithmURI, EncryptionMethodParams params)
              throws XMLSecurityException {

      super(doc, algorithmURI);

      Vector v = (Vector) this._algorithmHash.get(algorithmURI);

      if (v == null) {
         Object exArgs[] = { "Could not find a registered Provider" };

         throw new XMLSecurityException("empty");
      }

      searchForWorkingClass: for (int i = 0; i < v.size(); i++) {
         try {
            String implementingClass = (String) v.elementAt(i);

            this._emSpi =
               (EncryptionMethodSpi) Class.forName(implementingClass)
                  .newInstance();

            if ((this._emSpi != null)
                    && this._emSpi.getRequiredProviderAvailable()) {
               cat.debug("Create URI \"" + algorithmURI + "\" class \""
                         + implementingClass + "\"");

               break searchForWorkingClass;
            }
         } catch (ClassNotFoundException ex) {
            throw new XMLSecurityException("empty", ex);
         } catch (IllegalAccessException ex) {
            throw new XMLSecurityException("empty", ex);
         } catch (InstantiationException ex) {
            throw new XMLSecurityException("empty", ex);
         }
      }

      if (this._emSpi == null) {
         Object exArgs[] = { "Could not find a registered Provider" };

         throw new XMLSecurityException("empty");
      }

      this._encMethodParams = this._emSpi.engineInit(doc, params);

      if (this._encMethodParams != null) {
         this._constructionElement
            .appendChild(this._encMethodParams.createChildNodes(this._doc));
      }
   }

   /**
    * Constructor EncryptionMethod
    *
    * @param element
    * @param BaseURI
    * @throws XMLSecurityException
    */
   public EncryptionMethod(Element element, String BaseURI)
           throws XMLSecurityException {

      super(element, BaseURI);

      String algorithmURI = this.getAlgorithmURI();
      Vector v = (Vector) this._algorithmHash.get(algorithmURI);

      if (v == null) {
         Object exArgs[] = { "Could not find a registered Provider" };

         throw new XMLSecurityException("empty");
      }

      searchForWorkingClass: for (int i = 0; i < v.size(); i++) {
         try {
            String implementingClass = (String) v.elementAt(i);

            this._emSpi =
               (EncryptionMethodSpi) Class.forName(implementingClass)
                  .newInstance();

            if ((this._emSpi != null)
                    && this._emSpi.getRequiredProviderAvailable()) {
               cat.debug("Create URI \"" + algorithmURI + "\" class \""
                         + implementingClass + "\"");

               break searchForWorkingClass;
            }
         } catch (ClassNotFoundException ex) {
            throw new XMLSecurityException("empty", ex);
         } catch (IllegalAccessException ex) {
            throw new XMLSecurityException("empty", ex);
         } catch (InstantiationException ex) {
            throw new XMLSecurityException("empty", ex);
         }
      }

      if (this._emSpi == null) {
         Object exArgs[] = { "Could not find a registered Provider" };

         throw new XMLSecurityException("empty");
      }

      this._encMethodParams = this._emSpi.engineInit(this._constructionElement);
   }

   /**
    * Method wrap
    *
    * @param contentKey
    * @param wrapKey
    * @return
    * @throws XMLSecurityException
    */
   public byte[] wrap(Key contentKey, Key wrapKey) throws XMLSecurityException {
      return this._emSpi.engineWrap(contentKey, wrapKey);
   }

   /**
    * Method wrap
    *
    * @param contentKey
    * @param wrapKey
    * @param IV
    * @return
    * @throws XMLSecurityException
    */
   public byte[] wrap(Key contentKey, Key wrapKey, byte[] IV)
           throws XMLSecurityException {
      return this._emSpi.engineWrap(contentKey, wrapKey, IV);
   }

   /**
    * Method unwrap
    *
    * @param wrappedKey
    * @param wrapKey
    * @param wrappedKeyAlgoURI
    * @return
    * @throws XMLSecurityException
    */
   public Key unwrap(byte[] wrappedKey, Key wrapKey, String wrappedKeyAlgoURI)
           throws XMLSecurityException {
      return this._emSpi.engineUnwrap(wrappedKey, wrapKey, wrappedKeyAlgoURI);
   }

   /**
    * Method encrypt
    *
    * @param plaintextBytes
    * @param contentKey
    * @return
    * @throws XMLSecurityException
    */
   public byte[] encrypt(byte[] plaintextBytes, Key contentKey)
           throws XMLSecurityException {
      return this._emSpi.engineEncrypt(plaintextBytes, contentKey);
   }

   /**
    * Method encrypt
    *
    * @param plaintextBytes
    * @param contentKey
    * @param IV
    * @return
    * @throws XMLSecurityException
    */
   public byte[] encrypt(byte[] plaintextBytes, Key contentKey, byte[] IV)
           throws XMLSecurityException {
      return this._emSpi.engineEncrypt(plaintextBytes, contentKey, IV);
   }

   /**
    * Method decrypt
    *
    * @param ciphertextBytes
    * @param contentKey
    * @return
    * @throws XMLSecurityException
    */
   public byte[] decrypt(byte[] ciphertextBytes, Key contentKey)
           throws XMLSecurityException {
      return this._emSpi.engineDecrypt(ciphertextBytes, contentKey);
   }

   /**
    * Method getParams
    *
    * @return
    */
   public EncryptionMethodParams getParams() {
      return this._encMethodParams;
   }

   /**
    * Method getIvLength
    *
    * @return
    */
   public int getIvLength() {
      return this._emSpi.engineGetIvLength();
   }

   /**
    * Method getBlockSize
    *
    * @return
    */
   public int getBlockSize() {
      return this._emSpi.engineGetBlockSize();
   }

   /**
    * Method createSecretKeyFromBytes
    *
    * @param encodedKey
    * @return
    * @throws XMLSecurityException
    */
   public Key createSecretKeyFromBytes(byte encodedKey[])
           throws XMLSecurityException {

      String JceAlgo =
         JCEMapper
            .getJCEKeyAlgorithmFromURI(this._emSpi
               .getImplementedAlgorithmURI(), this._emSpi
               .getRequiredProviderName());

      return new SecretKeySpec(encodedKey, JceAlgo);
   }

   /** Field _alreadyInitialized */
   static boolean _alreadyInitialized = false;

   /** All available algorithm classes are registered here */
   static HashMap _algorithmHash = null;

   /**
    * Method providerInit
    *
    */
   public static void providerInit() {

      if (EncryptionMethod.cat == null) {
         EncryptionMethod.cat =
            org.apache.log4j.Category
               .getInstance(EncryptionMethod.class.getName());
      }

      cat.debug("Init() called");

      if (!EncryptionMethod._alreadyInitialized) {
         EncryptionMethod._algorithmHash = new HashMap(10);
         EncryptionMethod._alreadyInitialized = true;
      }
   }

   /**
    * Method register
    *
    * @param algorithmURI
    * @param implementingClass
    * @return
    */
   public static boolean register(String algorithmURI,
                                  String implementingClass) {

      Vector v = (Vector) EncryptionMethod._algorithmHash.get(algorithmURI);

      if (v == null) {
         v = new Vector();

         EncryptionMethod._algorithmHash.put(algorithmURI, v);
      }

      try {
         Class c = Class.forName(implementingClass);

         // System.out.println(c.getSuperclass().getSuperclass().getName());
         if (c != null) {
            EncryptionMethodSpi emSpi = (EncryptionMethodSpi) c.newInstance();

            if (emSpi.getRequiredProviderAvailable()) {
               v.add(implementingClass);

               return true;
            } else {
               cat.debug("Try to register class " + implementingClass
                         + " but Provider " + emSpi.getRequiredProviderName()
                         + " not available");
            }
         } else {
            cat.debug("Try to register class " + implementingClass
                      + " but Class not available");
         }
      } catch (ClassNotFoundException ex) {
         cat.debug("Try to register class " + implementingClass
                   + " but Class not found: ", ex);
      } catch (IllegalAccessException ex) {
         cat.debug("Try to register class " + implementingClass
                   + " but Class not found: ", ex);
      } catch (InstantiationException ex) {
         cat.debug("Try to register class " + implementingClass
                   + " but Class not found: ", ex);
      }

      return false;
   }

   //J-
   public String encryptB64(byte[] plaintextBytes, Key contentKey, byte[] IV) throws XMLSecurityException {
       byte ciphertextBytes[] = this.encrypt(plaintextBytes, contentKey, IV);
       return Base64.encode(ciphertextBytes);
   }
   public String encryptB64(byte[] plaintextBytes, Key contentKey) throws XMLSecurityException {
       byte ciphertextBytes[] = this.encrypt(plaintextBytes, contentKey);
       return Base64.encode(ciphertextBytes);
   }
   public byte[] decryptB64(String ciphertext, Key contentKey) throws XMLSecurityException {
       byte ciphertextBytes[] = Base64.decode(ciphertext);
       return this.decrypt(ciphertextBytes, contentKey);
   }
   public String wrapB64(Key contentKey, Key wrapKey) throws XMLSecurityException {
       byte wrappedKeyBytes[] = this.wrap(contentKey, wrapKey);
       return Base64.encode(wrappedKeyBytes);
   }
   public String wrapB64(Key contentKey, Key wrapKey, byte[] IV) throws XMLSecurityException {
       byte wrappedKeyBytes[] = this.wrap(contentKey, wrapKey, IV);
       return Base64.encode(wrappedKeyBytes);
   }
   public Key unwrapB64(String wrappedKey, Key wrapKey, String wrappedKeyAlgoURI) throws XMLSecurityException {
       byte wrappedKeyBytes[] = Base64.decode(wrappedKey);
       return this.unwrap(wrappedKeyBytes, wrapKey, wrappedKeyAlgoURI);
   }

   //J+
   static {
      org.apache.xml.security.Init.init();
   }
}

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
package org.apache.xml.security.keys.provider;



import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.algorithms.encryption.EncryptionMethod;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.X509Data;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.apache.xml.security.utils.*;
import org.w3c.dom.*;


/**
 *
 * @author $Author$
 */
public class KeyElement extends KeyBaseType {

   /**
    * Constructor KeyElement
    *
    * @param doc
    * @param alias
    * @param k
    * @param password
    * @param chain
    */
   public KeyElement(Document doc, String alias, Key k, char[] password,
                     Certificate[] chain) {

      super(doc, alias);

      this.wrap(k, password);
      this.setCertificateChain(chain);
   }

   /**
    * Constructor KeyElement
    *
    * @param doc
    * @param alias
    * @param key
    * @param chain
    */
   public KeyElement(Document doc, String alias, byte[] key,
                     Certificate[] chain) {

      super(doc, alias);

      this.wrap(key);
      this.setCertificateChain(chain);
   }

   /** Field salt */
   public static final byte[] salt = { (byte) 0xc9, (byte) 0x36, (byte) 0x78,
                                       (byte) 0x99, (byte) 0x52, (byte) 0x3e,
                                       (byte) 0xea, (byte) 0xf2 };

   /**
    * Method wrap
    *
    * @param key
    */
   private void wrap(byte[] key) {}

   /**
    * Method wrap
    *
    * @param k
    * @param password
    */
   private void wrap(Key k, char[] password) {

      try {
         Key wrapKey = createWrapKey(EncryptionConstants.ALGO_ID_KEYWRAP_AES256,
                                     password);
         EncryptedKey ek =
            new EncryptedKey(this._doc,
                             EncryptionConstants.ALGO_ID_KEYWRAP_AES256, null,
                             null, k, wrapKey, null, null, null, null, null,
                             null);

         /*
         String JCAalgo = k.getAlgorithm();
         String JCAformat = k.getFormat();
         String keyType = null;
         if (JavaUtils.implementsInterface(k, "java.security.PrivateKey")) {
            keyType = "PrivateKey";
         } else if (JavaUtils
                 .implementsInterface(k, "javax.crypto.SecretKey")) {
            keyType = "SecretKey";
         }
         */
         this._constructionElement.appendChild(ek.getElement());
         XMLUtils.addReturnToElement(this._constructionElement);
      } catch (XMLSecurityException ex) {
         throw new RuntimeException(ex.getMessage());
      }
   }

   /**
    * Method unwrap
    *
    * @param password
    * @return
    * @throws NoSuchAlgorithmException
    * @throws UnrecoverableKeyException
    */
   public Key unwrap(char[] password)
           throws NoSuchAlgorithmException, UnrecoverableKeyException {

      try {
         Key wrapKey = createWrapKey(EncryptionConstants.ALGO_ID_KEYWRAP_AES256,
                                     password);
         EncryptedKey ek =
            new EncryptedKey(this
               .getChildElementLocalName(0, EncryptionConstants
               .EncryptionSpecNS, EncryptionConstants._TAG_ENCRYPTEDKEY), this
                  ._baseURI);

         return ek.unwrap(wrapKey, EncryptionConstants.ALGO_ID_KEYWRAP_AES128);
      } catch (XMLSecurityException ex) {
         throw new RuntimeException(ex.getMessage());
      }
   }

   /**
    * Constructor KeyElement
    *
    * @param element
    * @param BaseURI
    * @throws XMLSecurityException
    */
   public KeyElement(Element element, String BaseURI)
           throws XMLSecurityException {
      super(element, BaseURI);
   }

   /**
    * Method getBaseLocalName
    *
    * @return
    */
   public String getBaseLocalName() {
      return ApacheKeyStoreConstants._TAG_KEY;
   }

   /**
    * Method setCertificateChain
    *
    * @param chain
    * @throws XMLSecurityException
    */
   public void setCertificateChain(Certificate[] chain)
           throws XMLSecurityException {

      if ((this._state == MODE_CREATE) && (chain != null)
              && (chain.length > 0)) {
         Element chainElement =
            ElementProxy.createElementForFamily(this._doc,
                                                this.getBaseNamespace(),
                                                ApacheKeyStoreConstants
                                                   ._TAG_CERTIFICATE_CHAIN);

         XMLUtils.addReturnToElement(chainElement);

         for (int i = 0; i < chain.length; i++) {
            Certificate currentCert = chain[i];

            if (currentCert != null) {
               if (currentCert.getType().equals("X.509")) {
                  X509Data xd = new X509Data(this._doc);

                  xd.add(new XMLX509Certificate(this._doc,
                                                (X509Certificate) currentCert));
                  chainElement.appendChild(xd.getElement());
                  XMLUtils.addReturnToElement(chainElement);
               } else {
                  throw new IllegalArgumentException(
                     "The certificate " + i + " is of type "
                     + currentCert.getType()
                     + ", but I can only handle X.509 certificates");
               }
            }
         }

         this._constructionElement.appendChild(chainElement);
      }
   }

   /**
    * Method getCertificateChain
    *
    * @param alias
    * @return
    */
   public Certificate[] getCertificateChain(String alias) {

      try {
         NodeList certificates =
            this._constructionElement
               .getElementsByTagNameNS(Constants.SignatureSpecNS,
                                       Constants._TAG_X509CERTIFICATE);
         Certificate result[] = new Certificate[certificates.getLength()];

         for (int i = 0; i < certificates.getLength(); i++) {
            Element currentCert = (Element) certificates.item(i);
            XMLX509Certificate cert = new XMLX509Certificate(currentCert,
                                         this._baseURI);

            result[i] = cert.getX509Certificate();
         }

         return result;
      } catch (XMLSecurityException ex) {}

      return null;
   }

   /**
    * converts a password to a byte array according to the scheme in
    * PKCS12 (unicode, big endian, 2 zero pad bytes at the end).
    *
    * @param password a character array reqpresenting the password.
    * @return a byte array representing the password.
    */
   public static byte[] PKCS12PasswordToBytes(char[] password) {

      // +1 for extra 2 pad bytes.
      byte[] bytes = new byte[(password.length + 1) * 2];

      for (int i = 0; i != password.length; i++) {
         bytes[i * 2] = (byte) (password[i] >>> 8);
         bytes[i * 2 + 1] = (byte) password[i];
      }

      return bytes;
   }

   /**
    * Method createWrapKey
    *
    * @param algorithmURI
    * @param password
    * @return
    * @throws XMLSecurityException
    */
   private static Key createWrapKey(String algorithmURI, char[] password)
           throws XMLSecurityException {

      int requiredKeyLength = JCEMapper.getKeyLengthFromURI(algorithmURI) / 8;
      String JCEAlgoID = JCEMapper.getJCEKeyAlgorithmFromURI(algorithmURI,
                            "BC");

      try {
         MessageDigest md = MessageDigest.getInstance("SHA-1", "BC");

         md.update(PKCS12PasswordToBytes(password));

         byte[] keyBytes = new byte[requiredKeyLength];

         md.digest(keyBytes, 0, keyBytes.length);

         return new SecretKeySpec(keyBytes, JCEAlgoID);
      } catch (NoSuchProviderException ex) {}
      catch (NoSuchAlgorithmException ex) {}
      catch (java.security.DigestException ex) {}

      return null;
   }
}

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



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Vector;
import java.util.Enumeration;
import javax.crypto.*;
import javax.xml.parsers.*;
import javax.xml.transform.TransformerException;
import org.apache.xml.security.c14n.*;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.*;
import org.apache.xml.security.transforms.*;
import org.apache.xml.security.utils.*;
import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.*;
import org.xml.sax.*;


/**
 *
 * @author $Author$
 */
public class ApacheKeyStore extends KeyStoreSpi {

   /** Field PERMIT_EMPTY_STORE_PASSWORDS */
   private static final boolean PERMIT_EMPTY_STORE_PASSWORDS = true;

   /** Field PERMIT_STORE_WITHOUT_SIGNATURE */
   private static final boolean PERMIT_STORE_WITHOUT_SIGNATURE = true;

   /**
    * Constructor ApacheKeyStore
    *
    */
   public ApacheKeyStore() {

      try {
         org.apache.xml.security.utils.ElementProxy
            .setDefaultPrefix(ApacheKeyStoreConstants
               .ApacheKeyStore_NAMESPACE, ApacheKeyStoreConstants
               .ApacheKeyStore_PREFIX);
      } catch (XMLSecurityException ex) {}
   }

   /** Field _keyStoreElement */
   KeyStoreElement _keyStoreElement;

   /**
    *
    * @param is
    * @param integrityPassPhrase
    * @throws CertificateException
    * @throws IOException
    * @throws NoSuchAlgorithmException
    */
   public void engineLoad(InputStream is, char[] integrityPassPhrase)
           throws IOException, NoSuchAlgorithmException, CertificateException {

      try {
         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

         dbf.setNamespaceAware(true);

         DocumentBuilder db = dbf.newDocumentBuilder();

         if (is == null) {
            Document doc = db.newDocument();

            this._keyStoreElement = new KeyStoreElement(doc);

            doc.appendChild(this._keyStoreElement.getElement());
         } else {
            Document doc = db.parse(is);

            // unfortunaltely, we have to use that stoopid memory:// URI
            // because the JCA only gives us an InputStream and no way to
            // determine what URI the InputStream has
            this._keyStoreElement =
               new KeyStoreElement(doc.getDocumentElement(), "memory://");

            if (integrityPassPhrase != null) {
               boolean verified =
                  this._keyStoreElement.verify(integrityPassPhrase);

               if (!verified) {

                  /*
                  java.io.FileOutputStream fos = new java.io.FileOutputStream("signed");
                  fos.write(signature.getSignedInfo().getSignedContentItem(0));
                  fos.close();
                  */
                  throw new IOException(
                     "The integrity of the KeyStore is broken; maybe someone messed around in the KeyStore");
               }
            }
         }
      } catch (ParserConfigurationException ex) {
         throw new IOException(ex.getMessage());
      } catch (SAXException ex) {
         throw new IOException(ex.getMessage());
      } catch (XMLSecurityException ex) {
         throw new IOException(ex.getMessage());
      }
   }

   /**
    *
    * @param os
    * @param integrityPassPhrase
    * @throws CertificateException
    * @throws IOException
    * @throws NoSuchAlgorithmException
    */
   public void engineStore(OutputStream os, char[] integrityPassPhrase)
           throws IOException, NoSuchAlgorithmException, CertificateException {

      try {
         if (integrityPassPhrase != null) {
            this._keyStoreElement.sign(integrityPassPhrase);
         } else {
            if (PERMIT_EMPTY_STORE_PASSWORDS) {
               if (PERMIT_STORE_WITHOUT_SIGNATURE) {
                  this._keyStoreElement.removeOldSignatures();
               } else {

                  /* If the user supplies no integrityPassPhrase, a passphrase is
                   * generated at random. This prevents an attacker from knowing that
                   * a particular keystore is unprotected.
                   */
                  byte bytes[] = PRNG.createBytes(100);
                  char chars[] = new char[bytes.length];

                  for (int i = 0; i < chars.length; i++) {
                     chars[i] = (char) bytes[i];
                  }

                  this._keyStoreElement.sign(chars);
               }
            } else {
               throw new IllegalArgumentException(
                  "integrityPassPhrase can't be null");
            }
         }

         Canonicalizer c14nizer =
            Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);

         os.write(c14nizer.canonicalizeSubtree(this._keyStoreElement.getDocument()));
      } catch (InvalidCanonicalizerException ex) {
         throw new IOException(ex.getMessage());
      } catch (CanonicalizationException ex) {
         throw new IOException(ex.getMessage());
      }
   }

   /**
    *
    * @param alias
    *
    */
   public boolean engineContainsAlias(String alias) {

      if (alias == null) {
         return false;
      }

      Enumeration aliases = this.engineAliases();

      if (aliases == null) {
         return false;
      }

      while (aliases.hasMoreElements()) {
         String current = (String) aliases.nextElement();

         if (current.equals(alias)) {
            return true;
         }
      }

      return false;
   }

   /**
    *
    * @param cert
    *
    */
   public String engineGetCertificateAlias(Certificate cert) {
      Enumeration aliases = this.engineAliases();
      while (aliases.hasMoreElements()) {
         String alias = (String) aliases.nextElement();

         if (this.engineIsCertificateEntry(alias)) {
            Certificate currentCert = this.engineGetCertificate(alias);
            if (cert.equals(currentCert)) {
               return alias;
            }
         }
      }

      return null;
   }

   /**
    *
    *
    */
   public int engineSize() {
      return this._keyStoreElement.getNumberOfKeys()
             + this._keyStoreElement.getNumberOfCertificates();
   }

   /**
    *
    *
    */
   public Enumeration engineAliases() {
      return this._keyStoreElement.aliases();
   }
   public Date engineGetCreationDate(String alias) {
      return this._keyStoreElement.getCreationDate(alias);
   }
   public void engineDeleteEntry(String alias) throws KeyStoreException {
      this._keyStoreElement.deleteEntry(alias);
   }
   public boolean engineIsCertificateEntry(String alias) {
      return this._keyStoreElement.isCertificateEntry(alias);
   }
   public void engineSetCertificateEntry(String alias, Certificate cert)
           throws KeyStoreException {
      this._keyStoreElement.setCertificateEntry(alias, cert);
   }
   public Certificate engineGetCertificate(String alias) {
      return this._keyStoreElement.getCertificate(alias);
   }
   public Certificate[] engineGetCertificateChain(String alias) {
      return this._keyStoreElement.getCertificateChain(alias);
   }
   public boolean engineIsKeyEntry(String alias) {
      return this._keyStoreElement.isCertificateEntry(alias);
   }
   public void engineSetKeyEntry(String alias, byte[] key, Certificate[] chain)
           throws KeyStoreException {
      this._keyStoreElement.setKeyEntry(alias, key, chain);
   }
   public void engineSetKeyEntry(
           String alias, Key k, char[] password, Certificate[] chain)
              throws KeyStoreException {
      this._keyStoreElement.setKeyEntry(alias, k, password, chain);
   }
   public Key engineGetKey(String alias, char[] password)
           throws NoSuchAlgorithmException, UnrecoverableKeyException {
      return this._keyStoreElement.getKey(alias, password);
   }
}

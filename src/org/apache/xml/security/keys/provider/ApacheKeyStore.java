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

   /** Field APACHEKEYSTORE_NAMESPACE */
   public static final String APACHEKEYSTORE_NAMESPACE =
      "http://xml.apache.org/security/#KeyStore";

   /** Field APACHEKEYSTORE_PREFIX */
   public static final String APACHEKEYSTORE_PREFIX = "";

   /** Field _keyStoreElement */
   KeyStoreElement _keyStoreElement;

   /**
    * Constructor ApacheKeyStore
    *
    */
   public ApacheKeyStore() {

      try {
         org.apache.xml.security.utils.ElementProxy
            .setDefaultPrefix(APACHEKEYSTORE_NAMESPACE, APACHEKEYSTORE_PREFIX);
      } catch (XMLSecurityException ex) {}
   }

   /**
    *
    * @param is
    * @param password
    * @throws CertificateException
    * @throws IOException
    * @throws NoSuchAlgorithmException
    */
   public void engineLoad(InputStream is, char[] password)
           throws IOException, NoSuchAlgorithmException, CertificateException {

      try {
         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

         dbf.setNamespaceAware(true);

         DocumentBuilder db = dbf.newDocumentBuilder();

         if ((is == null) && (password == null)) {
            Document doc = db.newDocument();

            this._keyStoreElement = new KeyStoreElement(doc);

            doc.appendChild(this._keyStoreElement.getElement());
         } else {
            Document doc = db.parse(is);

            this._keyStoreElement =
               new KeyStoreElement(doc.getDocumentElement(), "memory://");

            boolean verified = this._keyStoreElement.verify(password);

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
    * @param password
    * @throws CertificateException
    * @throws IOException
    * @throws NoSuchAlgorithmException
    */
   public void engineStore(OutputStream os, char[] password)
           throws IOException, NoSuchAlgorithmException, CertificateException {

      try {
         this._keyStoreElement.sign(password);

         // System.out.println(new String(signature.getSignedInfo().getSignedContentItem(0)));
         Canonicalizer c14nizer =
            Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);

         os.write(c14nizer.canonicalize(this._keyStoreElement.getDocument()));
      } catch (InvalidCanonicalizerException ex) {
         throw new IOException(ex.getMessage());
      } catch (CanonicalizationException ex) {
         throw new IOException(ex.getMessage());
      }
   }

   /**
    *
    * @return
    */
   public Enumeration engineAliases() {
      return this._keyStoreElement.getAliases();
   }

   /**
    *
    * @param alias
    * @return
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
    * @return
    */
   public int engineSize() {
      return this._keyStoreElement.getNumberOfKeys()
             + this._keyStoreElement.getNumberOfCertificates();
   }

   /**
    *
    * @param alias
    * @return
    */
   public Date engineGetCreationDate(String alias) {
      return null;
   }

   /**
    *
    * @param alias
    * @throws KeyStoreException
    */
   public void engineDeleteEntry(String alias) throws KeyStoreException {}

   /**
    *
    * @param alias
    * @return
    */
   public boolean engineIsCertificateEntry(String alias) {
      return this._keyStoreElement.isCertificateEntry(alias);
   }

   /**
    *
    * @param alias
    * @param cert
    * @throws KeyStoreException
    */
   public void engineSetCertificateEntry(String alias, Certificate cert)
           throws KeyStoreException {

      try {
         CertificateElement certificateElement =
            new CertificateElement(this._keyStoreElement.getDocument(), alias,
                                   cert);

         this._keyStoreElement.add(certificateElement);
      } catch (XMLSecurityException ex) {
         throw new KeyStoreException(ex.getMessage());
      }
   }

   /**
    *
    * @param alias
    * @return
    */
   public Certificate engineGetCertificate(String alias) {
      return this._keyStoreElement.getCertificate(alias);
   }

   /**
    *
    * @param alias
    * @return
    */
   public Certificate[] engineGetCertificateChain(String alias) {
      return null;
   }

   /**
    *
    * @param cert
    * @return
    */
   public String engineGetCertificateAlias(Certificate cert) {
      return null;
   }

   /**
    *
    * @param alias
    * @return
    */
   public boolean engineIsKeyEntry(String alias) {
      return false;
   }

   /**
    *
    * @param alias
    * @param key
    * @param chain
    * @throws KeyStoreException
    */
   public void engineSetKeyEntry(String alias, byte[] key, Certificate[] chain)
           throws KeyStoreException {

      KeyElement keyElement =
         new KeyElement(this._keyStoreElement.getDocument(), alias, key, chain);

      this._keyStoreElement.add(keyElement);
   }

   /**
    *
    * @param alias
    * @param k
    * @param password
    * @param chain
    * @throws KeyStoreException
    */
   public void engineSetKeyEntry(
           String alias, Key k, char[] password, Certificate[] chain)
              throws KeyStoreException {

      KeyElement keyElement =
         new KeyElement(this._keyStoreElement.getDocument(), alias, k,
                        password, chain);

      this._keyStoreElement.add(keyElement);
   }

   /**
    *
    * @param alias
    * @param password
    * @return
    * @throws NoSuchAlgorithmException
    * @throws UnrecoverableKeyException
    */
   public Key engineGetKey(String alias, char[] password)
           throws NoSuchAlgorithmException, UnrecoverableKeyException {
      return null;
   }
}

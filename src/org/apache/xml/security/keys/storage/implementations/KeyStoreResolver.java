
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
package org.apache.xml.security.keys.storage.implementations;



import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Enumeration;
import org.apache.xml.security.keys.storage.*;


/**
 * Makes the Certificates from a JAVA {@link KeyStore} object available to the
 * {@link StorageResolver}.
 *
 * @author $Author$
 */
public class KeyStoreResolver extends StorageResolverSpi {

   /** Field _keyStore */
   KeyStore _keyStore = null;

   /** Field _iterator */
   Iterator _iterator = null;

   /**
    * Constructor KeyStoreResolver
    *
    * @param keyStore is the keystore which contains the Certificates
    * @throws StorageResolverException
    */
   public KeyStoreResolver(KeyStore keyStore) throws StorageResolverException {
      this._keyStore = keyStore;
      this._iterator = new KeyStoreIterator(this, this._keyStore);
   }

   /**
    * Method getIterator
    *
    * @return
    */
   public Iterator getIterator() {
      return this._iterator;
   }

   /**
    * Class KeyStoreIterator
    *
    * @author $Author$
    * @version $Revision$
    */
   class KeyStoreIterator implements Iterator {

      /** Field _keyStore */
      KeyStore _keyStore = null;

      /** Field _aliases */
      Enumeration _aliases = null;

      /**
       * Constructor KeyStoreIterator
       *
       * @param ksresolver
       * @param keyStore
       * @throws StorageResolverException
       */
      public KeyStoreIterator(KeyStoreResolver ksresolver, KeyStore keyStore)
              throws StorageResolverException {

         try {
            this._keyStore = keyStore;
            this._aliases = this._keyStore.aliases();
         } catch (KeyStoreException ex) {
            throw new StorageResolverException("generic.EmptyMessage", ex);
         }
      }

      /**
       * Method hasNext
       *
       * @return
       */
      public boolean hasNext() {
         return this._aliases.hasMoreElements();
      }

      /**
       * Method next
       *
       * @return
       */
      public Object next() {

         String alias = (String) this._aliases.nextElement();

         try {
            return this._keyStore.getCertificate(alias);
         } catch (KeyStoreException ex) {
            return null;
         }
      }

      /**
       * Method remove
       *
       */
      public void remove() {
         throw new UnsupportedOperationException(
            "Can't remove keys from KeyStore");
      }
   }

   /**
    * Method main
    *
    * @param unused
    * @throws Exception
    */
   public static void main(String unused[]) throws Exception {

      KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

      ks.load(
         new java.io.FileInputStream(
         "data/org/apache/xml/security/samples/input/keystore.jks"),
            "xmlsecurity".toCharArray());

      KeyStoreResolver krs = new KeyStoreResolver(ks);

      for (Iterator i = krs.getIterator(); i.hasNext(); ) {
         X509Certificate cert = (X509Certificate) i.next();
         byte[] ski =
            org.apache.xml.security.keys.content.x509.XMLX509SKI
               .getSKIBytesFromCert(cert);

         System.out.println(org.apache.xml.security.utils.Base64.encode(ski));
      }
   }
}

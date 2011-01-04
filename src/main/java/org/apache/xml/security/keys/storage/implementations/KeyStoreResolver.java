
/*
 * Copyright  1999-2010 The Apache Software Foundation.
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
package org.apache.xml.security.keys.storage.implementations;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.xml.security.keys.storage.StorageResolverException;
import org.apache.xml.security.keys.storage.StorageResolverSpi;

/**
 * Makes the Certificates from a JAVA {@link KeyStore} object available to the
 * {@link org.apache.xml.security.keys.storage.StorageResolver}.
 *
 * @author $Author$
 */
public class KeyStoreResolver extends StorageResolverSpi {

   /** Field _keyStore */
   KeyStore _keyStore = null;

   /**
    * Constructor KeyStoreResolver
    *
    * @param keyStore is the keystore which contains the Certificates
    * @throws StorageResolverException
    */
   public KeyStoreResolver(KeyStore keyStore) throws StorageResolverException {
      this._keyStore = keyStore;
      // Do a quick check on the keystore
      try {
         _keyStore.aliases();
      } catch (KeyStoreException ex) {
         throw new StorageResolverException("generic.EmptyMessage", ex);
      }
   }

   /** @inheritDoc */
   public Iterator getIterator() {
      return new KeyStoreIterator(this._keyStore);
   }

   /**
    * Class KeyStoreIterator
    *
    * @author $Author$
    * @version $Revision$
    */
   static class KeyStoreIterator implements Iterator {

      /** Field _keyStore */
      KeyStore _keyStore = null;

      /** Field _aliases */
      Enumeration _aliases = null;
      
      /** Field _nextCert */
      Certificate _nextCert = null;

      /**
       * Constructor KeyStoreIterator
       *
       * @param keyStore
       */
      public KeyStoreIterator(KeyStore keyStore) {
         try {
            this._keyStore = keyStore;
            this._aliases = this._keyStore.aliases();
         } catch (KeyStoreException ex) {
            // empty Enumeration
            this._aliases = new Enumeration() {
               public boolean hasMoreElements() {
                  return false;
               }
               public Object nextElement() {
                  return null;
               }
            };
         }
      }

      /** @inheritDoc */
      public boolean hasNext() {
         if (_nextCert == null)
            _nextCert = findNextCert();

         return (_nextCert != null);
      }

      /** @inheritDoc */
      public Object next() {
         if (_nextCert == null) {
            // maybe caller did not call hasNext()
            _nextCert = findNextCert();
            
            if (_nextCert == null) {
                throw new NoSuchElementException();
            }
         }
         
         Certificate ret = _nextCert;
         _nextCert = null;
         return ret;
      }

      /**
       * Method remove
       *
       */
      public void remove() {
         throw new UnsupportedOperationException(
            "Can't remove keys from KeyStore");
      }
      
      // Find the next entry that contains a certificate and return it.
      // In particular, this skips over entries containing symmetric keys.
      private Certificate findNextCert() {
         while (this._aliases.hasMoreElements()) {
             String alias = (String) this._aliases.nextElement();
             try {
                Certificate cert = this._keyStore.getCertificate(alias);
                if (cert != null)
                   return cert;
             } catch (KeyStoreException ex) {
                return null;
             }
         }

         return null;
      }
      
   }
   
}

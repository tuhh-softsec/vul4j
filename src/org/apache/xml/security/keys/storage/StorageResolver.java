
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
package org.apache.xml.security.keys.storage;



import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Vector;

import org.apache.xml.security.keys.storage.implementations.KeyStoreResolver;
import org.apache.xml.security.keys.storage.implementations.SingleCertificateResolver;


/**
 * This class collects customized resolvers for Certificates.
 *
 * @author $Author$
 */
public class StorageResolver {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(StorageResolver.class.getName());

   /** Field _storageResolvers */
   Vector _storageResolvers = new Vector();

   /** Field _iterator */
   Iterator _iterator = null;

   /**
    * Constructor StorageResolver
    *
    */
   public StorageResolver() {}

   /**
    * Constructor StorageResolver
    *
    * @param resolver
    */
   public StorageResolver(StorageResolverSpi resolver) {
      this.add(resolver);
   }

   /**
    * Method addResolver
    *
    * @param resolver
    */
   public void add(StorageResolverSpi resolver) {

      this._storageResolvers.add(resolver);

      this._iterator = null;
   }

   /**
    * Constructor StorageResolver
    *
    * @param keyStore
    */
   public StorageResolver(KeyStore keyStore) {
      this.add(keyStore);
   }

   /**
    * Method addKeyStore
    *
    * @param keyStore
    */
   public void add(KeyStore keyStore) {

      try {
         this.add(new KeyStoreResolver(keyStore));
      } catch (StorageResolverException ex) {
         log.error("Could not add KeyStore because of: ", ex);
      }
   }

   /**
    * Constructor StorageResolver
    *
    * @param x509certificate
    */
   public StorageResolver(X509Certificate x509certificate) {
      this.add(x509certificate);
   }

   /**
    * Method addCertificate
    *
    * @param x509certificate
    */
   public void add(X509Certificate x509certificate) {
      this.add(new SingleCertificateResolver(x509certificate));
   }

   /**
    * Method getIterator
    * @return
    *
    */
   public Iterator getIterator() {

      if (this._iterator == null) {
         this._iterator = new StorageResolverIterator(this._storageResolvers);
      }

      return this._iterator;
   }

   /**
    * Method hasNext
    *
    * @return
    */
   public boolean hasNext() {

      if (this._iterator == null) {
         this._iterator = new StorageResolverIterator(this._storageResolvers);
      }

      return this._iterator.hasNext();
   }

   /**
    * Method next
    *
    * @return
    */
   public X509Certificate next() {
      return (X509Certificate) this._iterator.next();
   }

   /**
    * Class StorageResolverIterator
    *
    * @author $Author$
    * @version $Revision$
    */
   class StorageResolverIterator implements Iterator {

      /** Field _resolvers */
      Vector _resolvers = null;

      /** Field _currentResolver */
      int _currentResolver = 0;

      /**
       * Constructor FilesystemIterator
       *
       * @param resolvers
       */
      public StorageResolverIterator(Vector resolvers) {
         this._resolvers = resolvers;
         this._currentResolver = 0;
      }

      /** @inheritDoc */
      public boolean hasNext() {

         if (this._resolvers == null) {
            return false;
         }

         while (this._currentResolver < this._resolvers.size()) {
            StorageResolverSpi current =
               (StorageResolverSpi) this._resolvers
                  .elementAt(this._currentResolver);

            if (current == null) {
               continue;
            }

            if (current.getIterator().hasNext()) {
               return true;
            } 
            this._currentResolver++;            
         }

         return false;
      }

      /** @inheritDoc */
      public Object next() {

         if (this._resolvers == null) {
            return null;
         }

         while (this._currentResolver < this._resolvers.size()) {
            StorageResolverSpi current =
               (StorageResolverSpi) this._resolvers
                  .elementAt(this._currentResolver);

            if (current == null) {
               continue;
            }

            if (current.getIterator().hasNext()) {
               return current.getIterator().next();
            }
            this._currentResolver++;           
         }

         return null;
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
}

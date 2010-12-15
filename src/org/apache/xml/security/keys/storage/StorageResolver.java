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
package org.apache.xml.security.keys.storage;

import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

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
   List _storageResolvers = null;

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
	   if (_storageResolvers == null)
		   _storageResolvers = new ArrayList();
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
    * @return the iterator for the resolvers.
    */
   public Iterator getIterator() {
      return new StorageResolverIterator(this._storageResolvers.iterator());
   }

   /**
    * Method hasNext
    *
    * @return true if there are more elements.
    * @deprecated no way to restart the iteration, use {@link #getIterator() getIterator()} instead
    */
   public boolean hasNext() {

      if (this._iterator == null) {
    	  if (_storageResolvers == null)
   		   _storageResolvers = new ArrayList();
         this._iterator = new StorageResolverIterator(this._storageResolvers.iterator());
      }

      return this._iterator.hasNext();
   }

   /**
    * Method next
    *
    * @return the next element
    * @deprecated no way to restart the iteration, use {@link #getIterator() getIterator()} instead
    */
   public X509Certificate next() {
      
      if (this._iterator == null) {
         if (_storageResolvers == null)
            _storageResolvers = new ArrayList();
         this._iterator = new StorageResolverIterator(this._storageResolvers.iterator());
      }
      
      return (X509Certificate) this._iterator.next();
   }

   /**
    * Class StorageResolverIterator
    * This iterates over all the Certificates found in all the resolvers.
    *
    * @author $Author$
    * @version $Revision$
    */
   static class StorageResolverIterator implements Iterator {

      /** Field _resolvers */
      Iterator _resolvers = null;

      /** Field _currentResolver */
      Iterator _currentResolver = null;

      /**
       * Constructor StorageResolverIterator
       *
       * @param resolvers
       */
      public StorageResolverIterator(Iterator resolvers) {
         this._resolvers = resolvers;
         _currentResolver = findNextResolver();
      }

      /** @inheritDoc */
      public boolean hasNext() {
         if (_currentResolver == null) {
            return false;
         }
            
         if (_currentResolver.hasNext()) {
            return true;
         }

         _currentResolver = findNextResolver();
         return (_currentResolver != null);
      }

      /** @inheritDoc */
      public Object next() {
         if (hasNext()) {
            return _currentResolver.next();
         }
         
         throw new NoSuchElementException();
      }

      /**
       * Method remove
       */
      public void remove() {
         throw new UnsupportedOperationException(
            "Can't remove keys from KeyStore");
      }

      // Find the next storage with at least one element and return its Iterator
      private Iterator findNextResolver() {
         
         while (_resolvers.hasNext()) {
            StorageResolverSpi resolverSpi = (StorageResolverSpi)_resolvers.next();
            Iterator iter = resolverSpi.getIterator();
            if (iter.hasNext()) {
               return iter;
            }
         }
         
         return null;
      }
   }
}

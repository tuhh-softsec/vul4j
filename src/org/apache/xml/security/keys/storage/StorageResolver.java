
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
package org.apache.xml.security.keys.storage;



import java.util.Vector;
import java.util.Iterator;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import org.apache.xml.security.keys.storage.implementations.*;
import org.apache.xml.security.utils.*;


/**
 * This class collects customized resolvers for Certificates.
 *
 * @author $Author$
 */
public class StorageResolver {

   /** Field cat */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(StorageResolver.class.getName());

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
         cat.error("Could not add KeyStore because of: ", ex);
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
    *
    * @return
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

      /**
       * Method hasNext
       *
       * @return
       */
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
            } else {
               this._currentResolver++;
            }
         }

         return false;
      }

      /**
       * Method next
       *
       * @return
       */
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
            } else {
               this._currentResolver++;
            }
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

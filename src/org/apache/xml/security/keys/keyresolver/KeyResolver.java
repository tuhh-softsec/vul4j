
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
package org.apache.xml.security.keys.keyresolver;



import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Vector;

import javax.crypto.SecretKey;

import org.apache.xml.security.keys.storage.StorageResolver;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * KeyResolver is factory class for subclass of KeyResolverSpi that
 * represent child element of KeyInfo.
 *
 * @author $Author$
 * @version %I%, %G%
 */
public class KeyResolver {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(KeyResolver.class.getName());

   /** Field _alreadyInitialized */
   static boolean _alreadyInitialized = false;

   /** Field _resolverVector */
   static Vector _resolverVector = null;

   /** Field _resolverSpi */
   protected KeyResolverSpi _resolverSpi = null;

   /** Field _storage */
   protected StorageResolver _storage = null;

   /**
    * Constructor ResourceResolver
    *
    * @param className
    * @throws ClassNotFoundException
    * @throws IllegalAccessException
    * @throws InstantiationException
    */
   private KeyResolver(String className)
           throws ClassNotFoundException, IllegalAccessException,
                  InstantiationException {
      this._resolverSpi =
         (KeyResolverSpi) Class.forName(className).newInstance();
   }

   /**
    * Method length
    *
    *
    */
   public static int length() {
      return KeyResolver._resolverVector.size();
   }

   /**
    * Method item
    *
    * @param i
    *
    * @throws KeyResolverException
    */
   public static KeyResolver item(int i) throws KeyResolverException {

      String currentClass = (String) KeyResolver._resolverVector.elementAt(i);
      KeyResolver resolver = null;

      try {
         resolver = new KeyResolver(currentClass);
      } catch (Exception e) {
         throw new KeyResolverException("utils.resolver.noClass", e);
      }

      return resolver;
   }

   /**
    * Method getInstance
    *
    * @param element
    * @param BaseURI
    * @param storage
    *
    * @throws KeyResolverException
    */
   public static final KeyResolver getInstance(
           Element element, String BaseURI, StorageResolver storage)
              throws KeyResolverException {

      for (int i = 0; i < KeyResolver._resolverVector.size(); i++) {
         String currentClass =
            (String) KeyResolver._resolverVector.elementAt(i);
         KeyResolver resolver = null;

         try {
            resolver = new KeyResolver(currentClass);
         } catch (Exception e) {
            Object exArgs[] = {
               (((element != null)
                 && (element.getNodeType() == Node.ELEMENT_NODE))
                ? element.getTagName()
                : "null") };

            throw new KeyResolverException("utils.resolver.noClass", exArgs, e);
         }

         log.debug("check resolvability by class " + currentClass);

         if ((resolver != null)
                 && resolver.canResolve(element, BaseURI, storage)) {
            return resolver;
         }
      }

      Object exArgs[] = {
         (((element != null) && (element.getNodeType() == Node.ELEMENT_NODE))
          ? element.getTagName()
          : "null") };

      throw new KeyResolverException("utils.resolver.noClass", exArgs);
   }

   /**
    * The init() function is called by org.apache.xml.security.Init.init()
    */
   public static void init() {

      if (!KeyResolver._alreadyInitialized) {
         KeyResolver._resolverVector = new Vector(10);
         _alreadyInitialized = true;
      }
   }

   /**
    * This method is used for registering {@link KeyResolverSpi}s which are
    * available to <I>all</I> {@link KeyInfo} objects. This means that
    * personalized {@link KeyResolverSpi}s should only be registered directly
    * to the {@link KeyInfo} using {@link KeyInfo#registerInternalKeyResolver}.
    *
    * @param className
    */
   public static void register(String className) {
      KeyResolver._resolverVector.add(className);
   }

   /**
    * This method is used for registering {@link KeyResolverSpi}s which are
    * available to <I>all</I> {@link KeyInfo} objects. This means that
    * personalized {@link KeyResolverSpi}s should only be registered directly
    * to the {@link KeyInfo} using {@link KeyInfo#registerInternalKeyResolver}.
    *
    * @param className
    */
   public static void registerAtStart(String className) {
      KeyResolver._resolverVector.add(0, className);
   }

   /*
    * Method resolve
    *
    * @param element
    *
    * @throws KeyResolverException
    */

   /**
    * Method resolveStatic
    *
    * @param element
    * @param BaseURI
    * @param storage
    *
    * @throws KeyResolverException
    */
   public static PublicKey resolveStatic(
           Element element, String BaseURI, StorageResolver storage)
              throws KeyResolverException {

      KeyResolver myResolver = KeyResolver.getInstance(element, BaseURI,
                                  storage);

      return myResolver.resolvePublicKey(element, BaseURI, storage);
   }

   /**
    * Method resolve
    *
    * @param element
    * @param BaseURI
    * @param storage
    *
    * @throws KeyResolverException
    */
   public PublicKey resolvePublicKey(
           Element element, String BaseURI, StorageResolver storage)
              throws KeyResolverException {
      return this._resolverSpi.engineResolvePublicKey(element, BaseURI, storage);
   }

   /**
    * Method resolveX509Certificate
    *
    * @param element
    * @param BaseURI
    * @param storage
    *
    * @throws KeyResolverException
    */
   public X509Certificate resolveX509Certificate(
           Element element, String BaseURI, StorageResolver storage)
              throws KeyResolverException {
      return this._resolverSpi.engineResolveX509Certificate(element, BaseURI,
              storage);
   }

   public SecretKey resolveSecretKey(
           Element element, String BaseURI, StorageResolver storage)
              throws KeyResolverException {
      return this._resolverSpi.engineResolveSecretKey(element, BaseURI,
              storage);
   }

   /**
    * Method setProperty
    *
    * @param key
    * @param value
    */
   public void setProperty(String key, String value) {
      this._resolverSpi.engineSetProperty(key, value);
   }

   /**
    * Method getProperty
    *
    * @param key
    *
    */
   public String getProperty(String key) {
      return this._resolverSpi.engineGetProperty(key);
   }

   /**
    * Method getPropertyKeys
    *
    *
    */
   public String[] getPropertyKeys() {
      return this._resolverSpi.engineGetPropertyKeys();
   }

   /**
    * Method understandsProperty
    *
    * @param propertyToTest
    *
    */
   public boolean understandsProperty(String propertyToTest) {
      return this._resolverSpi.understandsProperty(propertyToTest);
   }

   /**
    * Method canResolve
    *
    * @param element
    * @param BaseURI
    * @param storage
    *
    */
   public boolean canResolve(Element element, String BaseURI,
                             StorageResolver storage) {
      return this._resolverSpi.engineCanResolve(element, BaseURI, storage);
   }

   /**
    * Method resolverClassName
    *
    *
    */
   public String resolverClassName() {
      return this._resolverSpi.getClass().getName();
   }
}


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
package org.apache.xml.security.keys.keyresolver;



import java.security.cert.*;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import java.util.*;
import org.w3c.dom.*;
import org.apache.xml.security.exceptions.*;
import org.apache.xml.security.keys.storage.*;


/**
 * KeyResovler is factory class for subclass of KeyResolverSpi that
 * represent child element of KeyInfo.
 *
 * @author $Author$
 * @version %I%, %G%
 */
public class KeyResolver {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(KeyResolver.class.getName());

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

         cat.debug("check resolvability by class " + currentClass);

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
    * to the {@link KeyInfo} using {@link KeyInfo#register}.
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
    * to the {@link KeyInfo} using {@link KeyInfo#register}.
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

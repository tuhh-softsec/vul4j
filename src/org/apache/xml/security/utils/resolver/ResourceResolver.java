
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
package org.apache.xml.security.utils.resolver;



import java.util.*;
import org.w3c.dom.*;
import org.apache.xml.utils.URI;
import org.apache.xml.security.signature.XMLSignatureInput;


/**
 * During reference validation, we have to retrieve resources from somewhere.
 * This is done by retrieving a Resolver. The resolver needs two arguments: The
 * URI in which the link to the new resource is defined and the BaseURI of the
 * file/entity in which the URI occurs (the BaseURI is the same as the SystemId
 * for {@link jaxax.xml.transform.stream.StreamSource#setSystemId(String)}.
 *
 * <UL xml:lang="DE" LANG="DE">
 * <LI> Verschiedene Implementierungen können sich als Resolver registrieren.
 * <LI> Standardmäßig werden erste Implementierungen auf dem XML config file registrirt.
 * <LI> Der Benutzer kann bei Bedarf Implementierungen voranstellen oder anfügen.
 * <LI> Implementierungen können mittels Features customized werden ä
 *      (z.B. um Proxy-Passworter übergeben zu können).
 * <LI> Jede Implementierung bekommt das URI Attribut und den Base URI
 *      übergeben und muss antworten, ob sie auflösen kann.
 * <LI> Die erste Implementierung, die die Aufgabe erfüllt, führt die Auflösung durch.
 * </UL>
 *
 * @author $Author$
 */
public class ResourceResolver {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(ResourceResolver.class.getName());

   /** Field _alreadyInitialized */
   static boolean _alreadyInitialized = false;

   /** these are the system-wide resolvers */
   static Vector _resolverVector = null;

   /** Field _individualResolverVector */
   Vector _individualResolverVector = null;

   /** Field transformSpi */
   protected ResourceResolverSpi _resolverSpi = null;

   /**
    * Constructor ResourceResolver
    *
    * @param className
    * @throws ClassNotFoundException
    * @throws IllegalAccessException
    * @throws InstantiationException
    */
   private ResourceResolver(String className)
           throws ClassNotFoundException, IllegalAccessException,
                  InstantiationException {
      this._resolverSpi =
         (ResourceResolverSpi) Class.forName(className).newInstance();
   }

   /**
    * Constructor ResourceResolver
    *
    * @param resourceResolver
    */
   public ResourceResolver(ResourceResolverSpi resourceResolver) {
      this._resolverSpi = resourceResolver;
   }

   /**
    * Method getInstance
    *
    * @param uri
    * @param BaseURI
    * @return
    * @throws ResourceResolverException
    */
   public static final ResourceResolver getInstance(Attr uri, String BaseURI)
           throws ResourceResolverException {

      for (int i = 0; i < ResourceResolver._resolverVector.size(); i++) {
         String currentClass =
            (String) ResourceResolver._resolverVector.elementAt(i);
         ResourceResolver resolver = null;

         try {
            resolver = new ResourceResolver(currentClass);
         } catch (Exception e) {
            Object exArgs[] = { ((uri != null)
                                 ? uri.getNodeValue()
                                 : "null"), BaseURI };

            throw new ResourceResolverException("utils.resolver.noClass",
                                                exArgs, e, uri, BaseURI);
         }

         cat.debug("check resolvability by class " + currentClass);

         if ((resolver != null) && resolver.canResolve(uri, BaseURI)) {
            return resolver;
         }
      }

      Object exArgs[] = { ((uri != null)
                           ? uri.getNodeValue()
                           : "null"), BaseURI };

      throw new ResourceResolverException("utils.resolver.noClass", exArgs,
                                          uri, BaseURI);
   }

   /**
    * Method getInstance
    *
    * @param uri
    * @param BaseURI
    * @param individualResolvers
    * @return
    * @throws ResourceResolverException
    */
   public static final ResourceResolver getInstance(
           Attr uri, String BaseURI, Vector individualResolvers)
              throws ResourceResolverException {

      cat.debug("I was asked to create a ResourceResolver and got " + individualResolvers.size());
      cat.debug(" extra resolvers to my existing " + ResourceResolver._resolverVector.size() + " system-wide resolvers");

      // first check the individual Resolvers
      if ((individualResolvers != null) && (individualResolvers.size() > 0)) {
         for (int i = 0; i < individualResolvers.size(); i++) {
            ResourceResolver resolver =
               (ResourceResolver) individualResolvers.elementAt(i);

            if (resolver != null) {
               String currentClass = resolver._resolverSpi.getClass().getName();

               cat.debug("check resolvability by class " + currentClass);

               if (resolver.canResolve(uri, BaseURI)) {
                  return resolver;
               }
            }
         }
      }

      for (int i = 0; i < ResourceResolver._resolverVector.size(); i++) {
         String currentClass =
            (String) ResourceResolver._resolverVector.elementAt(i);
         ResourceResolver resolver = null;

         try {
            resolver = new ResourceResolver(currentClass);
         } catch (Exception e) {
            Object exArgs[] = { ((uri != null)
                                 ? uri.getNodeValue()
                                 : "null"), BaseURI };

            throw new ResourceResolverException("utils.resolver.noClass",
                                                exArgs, e, uri, BaseURI);
         }

         cat.debug("check resolvability by class " + currentClass);

         if ((resolver != null) && resolver.canResolve(uri, BaseURI)) {
            return resolver;
         }
      }

      Object exArgs[] = { ((uri != null)
                           ? uri.getNodeValue()
                           : "null"), BaseURI };

      throw new ResourceResolverException("utils.resolver.noClass", exArgs,
                                          uri, BaseURI);
   }

   /**
    * The init() function is called by org.apache.xml.security.Init.init()
    */
   public static void init() {

      if (!ResourceResolver._alreadyInitialized) {
         ResourceResolver._resolverVector = new Vector(10);
         _alreadyInitialized = true;
      }
   }

   /**
    * Method register
    *
    * @param className
    */
   public static void register(String className) {
      ResourceResolver._resolverVector.add(className);
   }

   /**
    * Method registerAtStart
    *
    * @param className
    */
   public static void registerAtStart(String className) {
      ResourceResolver._resolverVector.add(0, className);
   }

   /**
    * Method resolve
    *
    * @param uri
    * @param BaseURI
    * @return
    * @throws ResourceResolverException
    */
   public static XMLSignatureInput resolveStatic(Attr uri, String BaseURI)
           throws ResourceResolverException {

      ResourceResolver myResolver = ResourceResolver.getInstance(uri, BaseURI);

      return myResolver.resolve(uri, BaseURI);
   }

   /**
    * Method resolve
    *
    * @param uri
    * @param BaseURI
    * @return
    * @throws ResourceResolverException
    */
   public XMLSignatureInput resolve(Attr uri, String BaseURI)
           throws ResourceResolverException {
      return this._resolverSpi.engineResolve(uri, BaseURI);
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
    * @return
    */
   public String getProperty(String key) {
      return this._resolverSpi.engineGetProperty(key);
   }

   /**
    * Method addProperties
    *
    * @param properties
    */
   public void addProperties(Map properties) {
      this._resolverSpi.engineAddProperies(properties);
   }

   /**
    * Method getPropertyKeys
    *
    * @return
    */
   public String[] getPropertyKeys() {
      return this._resolverSpi.engineGetPropertyKeys();
   }

   /**
    * Method understandsProperty
    *
    * @param propertyToTest
    * @return
    */
   public boolean understandsProperty(String propertyToTest) {
      return this._resolverSpi.understandsProperty(propertyToTest);
   }

   /**
    * Method canResolve
    *
    * @param uri
    * @param BaseURI
    * @return
    */
   private boolean canResolve(Attr uri, String BaseURI) {
      return this._resolverSpi.engineCanResolve(uri, BaseURI);
   }
}

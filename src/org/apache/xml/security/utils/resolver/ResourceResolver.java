
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
package org.apache.xml.security.utils.resolver;



import java.util.Map;
import java.util.Vector;

import org.apache.xml.security.signature.XMLSignatureInput;
import org.w3c.dom.Attr;


/**
 * During reference validation, we have to retrieve resources from somewhere.
 * This is done by retrieving a Resolver. The resolver needs two arguments: The
 * URI in which the link to the new resource is defined and the BaseURI of the
 * file/entity in which the URI occurs (the BaseURI is the same as the SystemId
 * for {@link javax.xml.transform.stream.StreamSource#getSystemId}.
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

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(ResourceResolver.class.getName());

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
    *
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

         log.debug("check resolvability by class " + currentClass);

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
    *
    * @throws ResourceResolverException
    */
   public static final ResourceResolver getInstance(
           Attr uri, String BaseURI, Vector individualResolvers)
              throws ResourceResolverException {

      log.debug("I was asked to create a ResourceResolver and got " + individualResolvers.size());
      log.debug(" extra resolvers to my existing " + ResourceResolver._resolverVector.size() + " system-wide resolvers");

      // first check the individual Resolvers
      if ((individualResolvers != null) && (individualResolvers.size() > 0)) {
         for (int i = 0; i < individualResolvers.size(); i++) {
            ResourceResolver resolver =
               (ResourceResolver) individualResolvers.elementAt(i);

            if (resolver != null) {
               String currentClass = resolver._resolverSpi.getClass().getName();

               log.debug("check resolvability by class " + currentClass);

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

         log.debug("check resolvability by class " + currentClass);

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
    *
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
    *
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
    *
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
    * @param uri
    * @param BaseURI
    *
    */
   private boolean canResolve(Attr uri, String BaseURI) {
      return this._resolverSpi.engineCanResolve(uri, BaseURI);
   }
}

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
package org.apache.xml.security;



import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import javax.xml.parsers.*;
import org.apache.xpath.XPathAPI;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.compiler.FuncLoader;
import org.apache.xpath.functions.Function;
import org.w3c.dom.*;
import org.apache.xml.security.algorithms.encryption.EncryptionMethod;
import org.apache.xml.security.algorithms.SignatureAlgorithm;
import org.apache.xml.security.algorithms.encryption.EncryptionMethod;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.transforms.params.XPathContainer;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.transforms.implementations.FuncHere;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.utils.resolver.ResourceResolver;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.ContentHandlerAlreadyRegisteredException;
import org.apache.xml.security.keys.keyresolver.KeyResolver;


/**
 * This class does the configuration of the library. This includes creating
 * the mapping of Canonicalization and Transform algorithms. Initialization is
 * done by calling {@link Init#init} which should be done in any static block
 * of the files of this library. We ensure that this call is only executed once.
 *
 * @author $Author$
 */
public class Init {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(Init.class.getName());

   /** Field _initialized */
   private static boolean _alreadyInitialized = false;

   public static final boolean isInitialized() {
     return Init._alreadyInitialized;
    }

   /**
    * Method init
    *
    */
   public synchronized static void init() {

      if (!_alreadyInitialized) {
         _alreadyInitialized = true;

         PRNG.init(new java.security.SecureRandom());

         try {

            /* read library configuration file */
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            dbf.setNamespaceAware(true);
            dbf.setValidating(false);

            DocumentBuilder db = dbf.newDocumentBuilder();
            InputStream is =
               Class.forName("org.apache.xml.security.Init")
                  .getResourceAsStream("resource/config.xml");
            Document doc = db.parse(is);
            Element context = doc.createElement("nscontext");

            context.setAttribute(
               "xmlns:x", "http://www.xmlsecurity.org/NS/#configuration");
            context.setAttribute("xmlns:log4j",
                                 "http://jakarta.apache.org/log4j/");

            {

               /* configure logging */
               Element log4jElem = (Element) XPathAPI.selectSingleNode(doc,
                                      "//log4j:configuration[1]", context);

               try {
                  Attr logfile = (Attr) XPathAPI.selectSingleNode(
                     log4jElem,
                     "./x:appender[@name='STDOUT']/x:param[@name='File']/@value",
                     context);
                  String logFileName = logfile.getNodeValue();
                  File f = new File(logFileName);

                  f.delete();
               } catch (Exception ex) {}

               org.apache.log4j.xml.DOMConfigurator.configure(log4jElem);
               cat.info("Logging is working");
            }

            {

               /* configure internationalization */
               Attr langAttr = (Attr) XPathAPI.selectSingleNode(
                  doc,
                  "/x:Configuration/x:ResourceBundles/@defaultLanguageCode",
                  context);
               Attr countryAttr = (Attr) XPathAPI.selectSingleNode(
                  doc,
                  "/x:Configuration/x:ResourceBundles/@defaultCountryCode",
                  context);
               String languageCode = (langAttr == null)
                                     ? null
                                     : langAttr.getNodeValue();
               String countryCode = (countryAttr == null)
                                    ? null
                                    : countryAttr.getNodeValue();

               I18n.init(languageCode, countryCode);
            }

            /**
             * Try to register our here() implementation as internal function.
             */
            {
               FunctionTable.installFunction("here", new FuncHere());
               cat.debug(
                  "Registered class " + FuncHere.class.getName()
                  + " for XPath function 'here()' function in internal table");

               /* The following tweak by "Eric Olson" <ego@alum.mit.edu>
                * is to enable xml-security to play with JDK 1.4 which
                * unfortunately bundles an old version of Xalan
                */
               FuncLoader funcHereLoader = new FuncHereLoader();

               for (int i = 0; i < FunctionTable.m_functions.length; i++) {
                  FuncLoader loader = FunctionTable.m_functions[i];

                  if (loader != null) {
                     cat.debug("Func " + i + " " + loader.getName());

                     if (loader.getName().equals(funcHereLoader.getName())) {
                        FunctionTable.m_functions[i] = funcHereLoader;
                     }
                  }
               }
            }

            {
               Canonicalizer.init();

               NodeList c14nElem = XPathAPI.selectNodeList(
                  doc,
                  "/x:Configuration/x:CanonicalizationMethods/x:CanonicalizationMethod",
                  context);

               for (int i = 0; i < c14nElem.getLength(); i++) {
                  String URI = ((Element) c14nElem.item(i)).getAttribute("URI");
                  String JAVACLASS =
                     ((Element) c14nElem.item(i)).getAttribute("JAVACLASS");
                  boolean registerClass = true;

                  try {
                     Class c = Class.forName(JAVACLASS);
                     Method methods[] = c.getMethods();

                     for (int j = 0; j < methods.length; j++) {
                        Method currMeth = methods[j];

                        if (currMeth.getDeclaringClass().getName()
                                .equals(JAVACLASS)) {
                           cat.debug(currMeth.getDeclaringClass());
                        }
                     }
                  } catch (ClassNotFoundException e) {
                     Object exArgs[] = { URI, JAVACLASS };

                     cat.fatal(I18n.translate("algorithm.classDoesNotExist",
                                              exArgs));

                     registerClass = false;
                  }

                  if (registerClass) {
                     cat.debug("Canonicalizer.register(" + URI + ", "
                               + JAVACLASS + ")");
                     Canonicalizer.register(URI, JAVACLASS);
                  }
               }
            }

            {
               Transform.init();

               NodeList tranElem = XPathAPI.selectNodeList(
                  doc,
                  "/x:Configuration/x:TransformAlgorithms/x:TransformAlgorithm",
                  context);

               for (int i = 0; i < tranElem.getLength(); i++) {
                  String URI = ((Element) tranElem.item(i)).getAttribute("URI");
                  String JAVACLASS =
                     ((Element) tranElem.item(i)).getAttribute("JAVACLASS");
                  boolean registerClass = true;

                  try {
                     Class c = Class.forName(JAVACLASS);
                     Method methods[] = c.getMethods();

                     for (int j = 0; j < methods.length; j++) {
                        Method currMeth = methods[j];

                        if (currMeth.getDeclaringClass().getName()
                                .equals(JAVACLASS)) {
                           cat.debug(currMeth.getDeclaringClass());
                        }
                     }
                  } catch (ClassNotFoundException e) {
                     Object exArgs[] = { URI, JAVACLASS };

                     cat.fatal(I18n.translate("algorithm.classDoesNotExist",
                                              exArgs));

                     registerClass = false;
                  }

                  if (registerClass) {
                     cat.debug("Transform.register(" + URI + ", " + JAVACLASS
                               + ")");
                     Transform.register(URI, JAVACLASS);
                  }
               }
            }

            {
               Element jcemapperElem = (Element) XPathAPI.selectSingleNode(
                  doc, "/x:Configuration/x:JCEAlgorithmMappings", context);

               JCEMapper.init(jcemapperElem);
            }

            {
               SignatureAlgorithm.providerInit();

               NodeList sigElems = XPathAPI.selectNodeList(
                  doc,
                  "/x:Configuration/x:SignatureAlgorithms/x:SignatureAlgorithm",
                  context);

               for (int i = 0; i < sigElems.getLength(); i++) {
                  String URI = ((Element) sigElems.item(i)).getAttribute("URI");
                  String JAVACLASS =
                     ((Element) sigElems.item(i)).getAttribute("JAVACLASS");

                  /** @todo handle registering */
                  boolean registerClass = true;

                  try {
                     Class c = Class.forName(JAVACLASS);
                     Method methods[] = c.getMethods();

                     for (int j = 0; j < methods.length; j++) {
                        Method currMeth = methods[j];

                        if (currMeth.getDeclaringClass().getName()
                                .equals(JAVACLASS)) {
                           cat.debug(currMeth.getDeclaringClass());
                        }
                     }
                  } catch (ClassNotFoundException e) {
                     Object exArgs[] = { URI, JAVACLASS };

                     cat.fatal(I18n.translate("algorithm.classDoesNotExist",
                                              exArgs));

                     registerClass = false;
                  }

                  if (registerClass) {
                     cat.debug("SignatureAlgorithm.register(" + URI + ", "
                               + JAVACLASS + ")");
                     SignatureAlgorithm.register(URI, JAVACLASS);
                  }
               }
            }

            /*
            {
               Element cipherAlgos = (Element) XPathAPI.selectSingleNode(doc,
                                        "/x:Configuration/x:EncryptionMethods",
                                        context);

               EncryptionMethod.init(cipherAlgos);
            }
            */
            {
               ResourceResolver.init();

               NodeList resolverElem = XPathAPI.selectNodeList(
                  doc, "/x:Configuration/x:ResourceResolvers/x:Resolver",
                  context);

               for (int i = 0; i < resolverElem.getLength(); i++) {
                  String JAVACLASS =
                     ((Element) resolverElem.item(i)).getAttribute("JAVACLASS");
                  String Description =
                     ((Element) resolverElem.item(i))
                        .getAttribute("DESCRIPTION");

                  if ((Description != null) && (Description.length() > 0)) {
                     cat.debug("Register Resolver: " + JAVACLASS + ": "
                               + Description);
                  } else {
                     cat.debug("Register Resolver: " + JAVACLASS
                               + ": For unknown purposes");
                  }

                  ResourceResolver.register(JAVACLASS);
               }
            }

            {
               try {
                  KeyInfo.init();

                  Init._contentHandlerHash = new HashMap(10);

                  {
                     NodeList keyElem = XPathAPI.selectNodeList(
                        doc, "/x:Configuration/x:KeyInfo/x:ContentHandler",
                        context);

                     for (int i = 0; i < keyElem.getLength(); i++) {
                        String namespace =
                           ((Element) keyElem.item(i))
                              .getAttribute("NAMESPACE");
                        String localname =
                           ((Element) keyElem.item(i))
                              .getAttribute("LOCALNAME");
                        String JAVACLASS =
                           ((Element) keyElem.item(i))
                              .getAttribute("JAVACLASS");

                        cat.debug("KeyInfoContent: " + namespace + " "
                                  + localname + " " + JAVACLASS);
                        Init.registerKeyInfoContentHandler(namespace,
                                                           localname,
                                                           JAVACLASS);
                     }
                  }
               } catch (Exception e) {
                  e.printStackTrace();

                  throw e;
               }
            }

            {
               KeyResolver.init();

               NodeList resolverElem = XPathAPI.selectNodeList(
                  doc, "/x:Configuration/x:KeyResolver/x:Resolver", context);

               for (int i = 0; i < resolverElem.getLength(); i++) {
                  String JAVACLASS =
                     ((Element) resolverElem.item(i)).getAttribute("JAVACLASS");
                  String Description =
                     ((Element) resolverElem.item(i))
                        .getAttribute("DESCRIPTION");

                  if ((Description != null) && (Description.length() > 0)) {
                     cat.debug("Register Resolver: " + JAVACLASS + ": "
                               + Description);
                  } else {
                     cat.debug("Register Resolver: " + JAVACLASS
                               + ": For unknown purposes");
                  }

                  KeyResolver.register(JAVACLASS);
               }
            }

            {
               cat.debug("Now I try to bind prefixes:");

               NodeList nl = XPathAPI.selectNodeList(
                  doc, "/x:Configuration/x:PrefixMappings/x:PrefixMapping",
                  context);

               for (int i = 0; i < nl.getLength(); i++) {
                  String namespace =
                     ((Element) nl.item(i)).getAttribute("namespace");
                  String prefix = ((Element) nl.item(i)).getAttribute("prefix");

                  cat.debug("Now I try to bind " + prefix + " to " + namespace);
                  org.apache.xml.security.utils.ElementProxy
                     .setDefaultPrefix(namespace, prefix);
               }
            }

            //J-
         EncryptionMethod.providerInit();
         EncryptionMethod.register(EncryptionConstants.ALGO_ID_KEYWRAP_TRIPLEDES,     "org.apache.xml.security.algorithms.encryption.implementations.BC.KeyWrapImpl_TRIPLEDES_BC");
         EncryptionMethod.register(EncryptionConstants.ALGO_ID_KEYWRAP_AES128,        "org.apache.xml.security.algorithms.encryption.implementations.BC.KeyWrapImpl_AES128_BC");
         EncryptionMethod.register(EncryptionConstants.ALGO_ID_KEYWRAP_AES192,        "org.apache.xml.security.algorithms.encryption.implementations.BC.KeyWrapImpl_AES192_BC");
         EncryptionMethod.register(EncryptionConstants.ALGO_ID_KEYWRAP_AES256,        "org.apache.xml.security.algorithms.encryption.implementations.BC.KeyWrapImpl_AES256_BC");
         EncryptionMethod.register(EncryptionConstants.ALGO_ID_BLOCKCIPHER_TRIPLEDES, "org.apache.xml.security.algorithms.encryption.implementations.BC.BlockEncryptionImpl_TRIPLEDES_BC");
         EncryptionMethod.register(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128,    "org.apache.xml.security.algorithms.encryption.implementations.BC.BlockEncryptionImpl_AES128_BC");
         EncryptionMethod.register(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192,    "org.apache.xml.security.algorithms.encryption.implementations.BC.BlockEncryptionImpl_AES192_BC");
         EncryptionMethod.register(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256,    "org.apache.xml.security.algorithms.encryption.implementations.BC.BlockEncryptionImpl_AES256_BC");
         EncryptionMethod.register(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP,  "org.apache.xml.security.algorithms.encryption.implementations.BC.KeyTransportImpl_RSAOAEP_BC");
         EncryptionMethod.register(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15,    "org.apache.xml.security.algorithms.encryption.implementations.BC.KeyTransportImpl_RSAPKCS15_BC");
         //J+
         } catch (Exception e) {
            cat.fatal("Bad: ", e);
            e.printStackTrace();
         }
      }
   }

   /**
    * This method customizes the library with user supplied configuration.
    * This includes access to keystores etc.
    * By default, this method tries to find the configurationfile in
    * the System.getProperty("user.home") directory.
    *
    * @throws XMLSecurityException
    */
   public static void readUserConfiguration() throws XMLSecurityException {

      try {
         String filename = System.getProperty("user.home") + "/"
                           + Constants.configurationFileNew;
         InputStream is = new FileInputStream(filename);

         Init.readUserConfiguration(is);
      } catch (IOException ex) {
         throw new XMLSecurityException("generic.EmptyMessage", ex);
      }
   }

   /**
    * This method customizes the library with user supplied configuration.
    * This includes access to keystores etc.
    *
    * @param fileURI
    * @throws XMLSecurityException
    */
   public static void readUserConfiguration(String fileURI)
           throws XMLSecurityException {

      try {
         InputStream is = null;

         // first try to interpret fileURI as filename in the local file system
         File f = new File(fileURI);

         if (f.exists()) {
            is = new FileInputStream(f);
         } else {

            // then treat it as USI
            is = new java.net.URL(fileURI).openStream();
         }

         Init.readUserConfiguration(is);
      } catch (IOException ex) {
         throw new XMLSecurityException("generic.EmptyMessage", ex);
      }
   }

   /**
    * Method readUserConfiguration
    *
    * @param is
    * @throws XMLSecurityException
    */
   public static void readUserConfiguration(InputStream is)
           throws XMLSecurityException {

      try {

         /* read library configuration file */
         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

         dbf.setNamespaceAware(true);
         dbf.setValidating(false);

         DocumentBuilder db = dbf.newDocumentBuilder();
         Document doc = db.parse(is);
         Element context = XMLUtils.createDSctx(
            doc, "x", "http://www.xmlsecurity.org/NS/#configuration");

         {
            NodeList nl =
               XPathAPI.selectNodeList(doc, "/x:AppConfiguration/x:KeyStore",
                                       context);

            for (int i = 0; i < nl.getLength(); i++) {
               Element e = (Element) nl.item(i);
               String URI = e.getAttribute("URI");
               String keyStoreType = e.getAttribute("Type");
               String defaultKeyAlias = e.getAttribute("DefaultKeyAlias");
               String storePass = e.getAttribute("StorePass");
               String KeyPass = e.getAttribute("KeyPass");

               // org.apache.xml.security.keys.keyStorage.KeyStorage.registerStore(URI, JAVACLASS, LOCATION, DEFAULTKEYOBJECT, CONTEXT);
            }
         }
      } catch (Exception ex) {
         throw new XMLSecurityException("generic.EmptyMessage", ex);
      }
   }

   /** Field _contentHandlerHash */
   public static HashMap _contentHandlerHash;

   /**
    * Method registerKeyinfoContentHandler
    *
    * @param namespace
    * @param localname
    * @param implementingClass
    * @throws ContentHandlerAlreadyRegisteredException
    */
   public static void registerKeyInfoContentHandler(
           String namespace, String localname, String implementingClass)
              throws ContentHandlerAlreadyRegisteredException {

      String namespacequali = Init.qualifyNamespace(namespace, localname);

      // are we already registered?
      if (Init._contentHandlerHash.containsKey(namespacequali)) {
         cat.error("Already registered");

         Object exArgs[] = { namespacequali,
                             ((String) Init._contentHandlerHash
                                .get(namespacequali)) };

         throw new ContentHandlerAlreadyRegisteredException(
            "algorithm.alreadyRegistered", exArgs);
      }

      synchronized (Init._contentHandlerHash) {
         Init._contentHandlerHash.put(namespacequali, implementingClass);
         cat.debug("Init._contentHandlerHash.put(\"" + namespacequali
                   + "\", \"" + implementingClass + "\")");
         cat.debug("Init._contentHandlerHash.size()="
                   + Init._contentHandlerHash.size());
      }
   }

   /**
    * Method qualifyNamespace
    *
    * @param namespace
    * @param localname
    * @return
    */
   private static String qualifyNamespace(String namespace, String localname) {
      return "{" + namespace + "}" + localname;
   }

   /**
    * Method getContentHandlerClass
    *
    * @param namespace
    * @param localname
    * @return
    */
   public static String getKeyInfoContentHandler(String namespace,
           String localname) {

      /*
      Iterator i = KeyInfo._contentHandlerHash.keySet().iterator();
      while (i.hasNext()) {
         String key = (String) i.next();
         if (key.equals(URI)) {
            return (String) KeyInfo._contentHandlerHash.get(key);
         }
      }
      return null;
      */
      String namespacequali = Init.qualifyNamespace(namespace, localname);

      cat.debug("Asked for handler for " + namespacequali);

      if (Init._contentHandlerHash == null) {
         cat.debug("But I can't help (hash==null) ");

         return null;
      }

      if (Init._contentHandlerHash.size() == 0) {
         cat.debug("But I can't help (size()==0)");

         return null;
      }

      Set keyset = Init._contentHandlerHash.keySet();
      Iterator i = keyset.iterator();

      while (i.hasNext()) {
         String key = (String) i.next();

         if (key.equals(namespacequali)) {
            return (String) Init._contentHandlerHash.get(key);
         }
      }

      return null;
   }

   /**
    * Class FuncHereLoader
    *
    * @author $Author$
    * @version $Revision$
    */
   public static class FuncHereLoader extends FuncLoader {

      /**
       * Constructor FuncHereLoader
       *
       */
      public FuncHereLoader() {
         super(FuncHere.class.getName(), 0);
      }

      /**
       * Method getFunction
       *
       * @return
       * @throws javax.xml.transform.TransformerException
       */
      public Function getFunction()
              throws javax.xml.transform.TransformerException {
         return new FuncHere();
      }

      /**
       * Method getName
       *
       * @return
       */
      public String getName() {
         return FuncHere.class.getName();
      }
   }
}

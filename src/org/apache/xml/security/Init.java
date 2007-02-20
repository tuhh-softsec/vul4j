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
package org.apache.xml.security;

import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.algorithms.SignatureAlgorithm;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.keyresolver.KeyResolver;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.utils.I18n;
//import org.apache.xml.security.utils.PRNG;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.resolver.ResourceResolver;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * This class does the configuration of the library. This includes creating
 * the mapping of Canonicalization and Transform algorithms. Initialization is
 * done by calling {@link Init#init} which should be done in any static block
 * of the files of this library. We ensure that this call is only executed once.
 *
 * @author $Author$
 */
public class Init {

  /** {@link org.apache.commons.logging} logging facility */
  static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(Init.class.getName());

   /** Field _initialized */
   private static boolean _alreadyInitialized = false;
   
   /** The namespace for CONF file **/
   public static final String CONF_NS="http://www.xmlsecurity.org/NS/#configuration";

   /**
    * Method isInitialized
    * @return true if the librairy is already initialized.     
    *
    */
   public static final boolean isInitialized() {
      return Init._alreadyInitialized;
   }

   /**
    * Method init
    *
    */
   public synchronized static void init() {

      if (_alreadyInitialized) {
        return;
      }
      long XX_configure_i18n_end=0;
      long XX_configure_reg_c14n_start=0;
      long XX_configure_reg_c14n_end=0;
      long XX_configure_reg_jcemapper_end=0;
      long XX_configure_reg_keyInfo_start=0;
      long XX_configure_reg_keyResolver_end=0;
      long XX_configure_reg_prefixes_start=0;
      long XX_configure_reg_resourceresolver_start=0;
      long XX_configure_reg_sigalgos_end=0;
      long XX_configure_reg_transforms_end=0;
      long XX_configure_reg_keyInfo_end=0;
      long XX_configure_reg_keyResolver_start=0;
         _alreadyInitialized = true;

         try {
            long XX_init_start = System.currentTimeMillis();
            long XX_prng_start = System.currentTimeMillis();

            //PRNG.init(new java.security.SecureRandom());

            long XX_prng_end = System.currentTimeMillis();

            /* read library configuration file */
            long XX_parsing_start = System.currentTimeMillis();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            dbf.setNamespaceAware(true);
            dbf.setValidating(false);

            DocumentBuilder db = dbf.newDocumentBuilder();
            // InputStream is = Class.forName("org.apache.xml.security.Init").getResourceAsStream("resource/config.xml");
            InputStream is = (InputStream) AccessController.doPrivileged(
		new PrivilegedAction() {
		    public Object run() {
            		String cfile = System.getProperty
			    ("org.apache.xml.security.resource.config");
               		return getClass().getResourceAsStream
			    (cfile != null ? cfile : "resource/config.xml");
		    }
		});

            Document doc = db.parse(is);
            long XX_parsing_end = System.currentTimeMillis();                       
            long XX_configure_i18n_start = 0;            
            
            {
                XX_configure_reg_keyInfo_start = System.currentTimeMillis();
               try {
                  KeyInfo.init();
               } catch (Exception e) {
                  e.printStackTrace();

                  throw e;
               }
               XX_configure_reg_keyInfo_end = System.currentTimeMillis();
            }
            
			long XX_configure_reg_transforms_start=0;
			long XX_configure_reg_jcemapper_start=0;
			long XX_configure_reg_sigalgos_start=0;
			long XX_configure_reg_resourceresolver_end=0;
			long XX_configure_reg_prefixes_end=0;
            Node config=doc.getFirstChild();
            for (;config!=null;config=config.getNextSibling()) {
            	if ("Configuration".equals(config.getLocalName())) {
            		break;
                }
            }
			for (Node el=config.getFirstChild();el!=null;el=el.getNextSibling()) {
                if (!(el instanceof Element)) {
                	continue;
                }
                String tag=el.getLocalName();
            if (tag.equals("ResourceBundles")){
                XX_configure_i18n_start = System.currentTimeMillis();
            	Element resource=(Element)el;
               /* configure internationalization */
               Attr langAttr = resource.getAttributeNode("defaultLanguageCode");
               Attr countryAttr = resource.getAttributeNode("defaultCountryCode");
               String languageCode = (langAttr == null)
                                     ? null
                                     : langAttr.getNodeValue();
               String countryCode = (countryAttr == null)
                                    ? null
                                    : countryAttr.getNodeValue();

               I18n.init(languageCode, countryCode);
               XX_configure_i18n_end = System.currentTimeMillis();
            }
           
            if (tag.equals("CanonicalizationMethods")){
                XX_configure_reg_c14n_start = System.currentTimeMillis();
               Canonicalizer.init();
               Element[] list=XMLUtils.selectNodes(el.getFirstChild(),CONF_NS,"CanonicalizationMethod");               

               for (int i = 0; i < list.length; i++) {
                  String URI = list[i].getAttributeNS(null,
                                  "URI");
                  String JAVACLASS =
                     list[i].getAttributeNS(null,
                        "JAVACLASS");
                  try {
                      Class.forName(JAVACLASS);
/*                     Method methods[] = c.getMethods();

                     for (int j = 0; j < methods.length; j++) {
                        Method currMeth = methods[j];

                        if (currMeth.getDeclaringClass().getName()
                                .equals(JAVACLASS)) {
                           log.debug(currMeth.getDeclaringClass());
                        }
                     }*/
                      if (log.isDebugEnabled())
                      	log.debug("Canonicalizer.register(" + URI + ", "
                            + JAVACLASS + ")");
                     Canonicalizer.register(URI, JAVACLASS);
                  } catch (ClassNotFoundException e) {
                     Object exArgs[] = { URI, JAVACLASS };

                     log.fatal(I18n.translate("algorithm.classDoesNotExist",
                                              exArgs));
                  }
               }
               XX_configure_reg_c14n_end = System.currentTimeMillis();
            }
                        
            if (tag.equals("TransformAlgorithms")){
               XX_configure_reg_transforms_start = System.currentTimeMillis();
               Transform.init();

               Element[] tranElem = XMLUtils.selectNodes(el.getFirstChild(),CONF_NS,"TransformAlgorithm");

               for (int i = 0; i < tranElem.length; i++) {
                  String URI = tranElem[i].getAttributeNS(null,
                                  "URI");
                  String JAVACLASS =
                     tranElem[i].getAttributeNS(null,
                        "JAVACLASS");
                  try {
                     Class.forName(JAVACLASS);
                     if (log.isDebugEnabled())
                     	log.debug("Transform.register(" + URI + ", " + JAVACLASS
                            + ")");
                     Transform.register(URI, JAVACLASS);
                  } catch (ClassNotFoundException e) {
                     Object exArgs[] = { URI, JAVACLASS };

                     log.fatal(I18n.translate("algorithm.classDoesNotExist",
                                              exArgs));

                  } catch (NoClassDefFoundError ex) {
					  log.warn("Not able to found dependecies for algorithm, I'm keep working.");
                  }
               }
               XX_configure_reg_transforms_end = System.currentTimeMillis();
            }
                        

            if ("JCEAlgorithmMappings".equals(tag)){
               XX_configure_reg_jcemapper_start = System.currentTimeMillis();
               JCEMapper.init((Element)el);
               XX_configure_reg_jcemapper_end = System.currentTimeMillis();
            }

                        

            if (tag.equals("SignatureAlgorithms")){
               XX_configure_reg_sigalgos_start = System.currentTimeMillis();
               SignatureAlgorithm.providerInit();

               Element[] sigElems = XMLUtils.selectNodes(el.getFirstChild(), CONF_NS,                  
                  "SignatureAlgorithm");

               for (int i = 0; i < sigElems.length; i++) {
                  String URI = sigElems[i].getAttributeNS(null,
                                  "URI");
                  String JAVACLASS =
                    sigElems[i].getAttributeNS(null,
                        "JAVACLASS");

                  /** $todo$ handle registering */

                  try {
                      Class.forName(JAVACLASS);
 //                    Method methods[] = c.getMethods();

//                     for (int j = 0; j < methods.length; j++) {
//                        Method currMeth = methods[j];
//
//                        if (currMeth.getDeclaringClass().getName()
//                                .equals(JAVACLASS)) {
//                           log.debug(currMeth.getDeclaringClass());
//                        }
//                     }
                      if (log.isDebugEnabled())
                      	log.debug("SignatureAlgorithm.register(" + URI + ", "
                            + JAVACLASS + ")");
                     SignatureAlgorithm.register(URI, JAVACLASS);
                  } catch (ClassNotFoundException e) {
                     Object exArgs[] = { URI, JAVACLASS };

                     log.fatal(I18n.translate("algorithm.classDoesNotExist",
                                              exArgs));

                  }
               }
               XX_configure_reg_sigalgos_end = System.currentTimeMillis();
            }

            
            
            if (tag.equals("ResourceResolvers")){
               XX_configure_reg_resourceresolver_start = System.currentTimeMillis();
               ResourceResolver.init();

               Element[]resolverElem = XMLUtils.selectNodes(el.getFirstChild(),CONF_NS,
                  "Resolver");

               for (int i = 0; i < resolverElem.length; i++) {
                  String JAVACLASS =
                      resolverElem[i].getAttributeNS(null,
                        "JAVACLASS");
                  String Description =
                     resolverElem[i].getAttributeNS(null,
                        "DESCRIPTION");

                  if ((Description != null) && (Description.length() > 0)) {
                    if (log.isDebugEnabled())
                    	log.debug("Register Resolver: " + JAVACLASS + ": "
                               + Description);
                  } else {
                    if (log.isDebugEnabled())
                    	log.debug("Register Resolver: " + JAVACLASS
                               + ": For unknown purposes");
                  }
				  try {
					  ResourceResolver.register(JAVACLASS);
				  } catch (Throwable e) {
					  log.warn("Cannot register:"+JAVACLASS+" perhaps some needed jars are not installed",e);
				  }
                  XX_configure_reg_resourceresolver_end =
                    System.currentTimeMillis();
               }               

            }

            

            

                        
            if (tag.equals("KeyResolver")){
               XX_configure_reg_keyResolver_start =System.currentTimeMillis();
               KeyResolver.init();

               Element[] resolverElem = XMLUtils.selectNodes(el.getFirstChild(), CONF_NS,"Resolver");

               for (int i = 0; i < resolverElem.length; i++) {
                  String JAVACLASS =
                     resolverElem[i].getAttributeNS(null,
                        "JAVACLASS");
                  String Description =
                     resolverElem[i].getAttributeNS(null,
                        "DESCRIPTION");

                  if ((Description != null) && (Description.length() > 0)) {
                    if (log.isDebugEnabled())
                    	log.debug("Register Resolver: " + JAVACLASS + ": "
                               + Description);
                  } else {
                    if (log.isDebugEnabled())
                    	log.debug("Register Resolver: " + JAVACLASS
                               + ": For unknown purposes");
                  }

                  KeyResolver.register(JAVACLASS);
               }
               XX_configure_reg_keyResolver_end = System.currentTimeMillis();
            }

                        
            if (tag.equals("PrefixMappings")){
                XX_configure_reg_prefixes_start = System.currentTimeMillis();
                if (log.isDebugEnabled())
                	log.debug("Now I try to bind prefixes:");

               Element[] nl = XMLUtils.selectNodes(el.getFirstChild(), CONF_NS,"PrefixMapping");

               for (int i = 0; i < nl.length; i++) {
                  String namespace = nl[i].getAttributeNS(null,
                                        "namespace");
                  String prefix = nl[i].getAttributeNS(null,
                                     "prefix");
                  if (log.isDebugEnabled())
                  	log.debug("Now I try to bind " + prefix + " to " + namespace);
                  org.apache.xml.security.utils.ElementProxy
                     .setDefaultPrefix(namespace, prefix);
               }
               XX_configure_reg_prefixes_end = System.currentTimeMillis();
            }
            }
            
            long XX_init_end = System.currentTimeMillis();

            //J-
            if (log.isDebugEnabled()) {
            	log.debug("XX_init                             " + ((int)(XX_init_end - XX_init_start)) + " ms");
                log.debug("  XX_prng                           " + ((int)(XX_prng_end - XX_prng_start)) + " ms");
                log.debug("  XX_parsing                        " + ((int)(XX_parsing_end - XX_parsing_start)) + " ms");            
                log.debug("  XX_configure_i18n                 " + ((int)(XX_configure_i18n_end- XX_configure_i18n_start)) + " ms");
                log.debug("  XX_configure_reg_c14n             " + ((int)(XX_configure_reg_c14n_end- XX_configure_reg_c14n_start)) + " ms");                       
                log.debug("  XX_configure_reg_jcemapper        " + ((int)(XX_configure_reg_jcemapper_end- XX_configure_reg_jcemapper_start)) + " ms");
                log.debug("  XX_configure_reg_keyInfo          " + ((int)(XX_configure_reg_keyInfo_end- XX_configure_reg_keyInfo_start)) + " ms");
                log.debug("  XX_configure_reg_keyResolver      " + ((int)(XX_configure_reg_keyResolver_end- XX_configure_reg_keyResolver_start)) + " ms");
                log.debug("  XX_configure_reg_prefixes         " + ((int)(XX_configure_reg_prefixes_end- XX_configure_reg_prefixes_start)) + " ms");            
                log.debug("  XX_configure_reg_resourceresolver " + ((int)(XX_configure_reg_resourceresolver_end- XX_configure_reg_resourceresolver_start)) + " ms");
                log.debug("  XX_configure_reg_sigalgos         " + ((int)(XX_configure_reg_sigalgos_end- XX_configure_reg_sigalgos_start)) + " ms");
                log.debug("  XX_configure_reg_transforms       " + ((int)(XX_configure_reg_transforms_end- XX_configure_reg_transforms_start)) + " ms");
            }
         } catch (Exception e) {
            log.fatal("Bad: ", e);
            e.printStackTrace();
         }
      
   }

  
}


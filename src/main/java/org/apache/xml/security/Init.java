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
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.algorithms.SignatureAlgorithm;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.keys.keyresolver.KeyResolver;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.utils.ElementProxy;
import org.apache.xml.security.utils.I18n;
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
 */
public class Init {
    
    /** The namespace for CONF file **/
    public static final String CONF_NS = "http://www.xmlsecurity.org/NS/#configuration";

    /** {@link org.apache.commons.logging} logging facility */
    private static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(Init.class);

    /** Field _initialized */
    private static boolean alreadyInitialized = false;
    
    private static Map<String, String> defaultNamespacePrefixes = new HashMap<String, String>();
    
    static {
        defaultNamespacePrefixes.put("http://www.w3.org/2000/09/xmldsig#", "ds");
        defaultNamespacePrefixes.put("http://www.w3.org/2001/04/xmlenc#", "xenc");
        defaultNamespacePrefixes.put("http://www.xmlsecurity.org/experimental#", "experimental");
        defaultNamespacePrefixes.put("http://www.w3.org/2002/04/xmldsig-filter2", "dsig-xpath-old");
        defaultNamespacePrefixes.put("http://www.w3.org/2002/06/xmldsig-filter2", "dsig-xpath");
        defaultNamespacePrefixes.put("http://www.w3.org/2001/10/xml-exc-c14n#", "ec");
        defaultNamespacePrefixes.put(
            "http://www.nue.et-inf.uni-siegen.de/~geuer-pollmann/#xpathFilter", "xx"
        );
    }
    
    /**
     * Method isInitialized
     * @return true if the library is already initialized.     
     */
    public synchronized static final boolean isInitialized() {
        return Init.alreadyInitialized;
    }

    /**
     * Method init
     *
     */
    public synchronized static void init() {
        if (alreadyInitialized) {
            return;
        }

        try {
            /* read library configuration file */
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            dbf.setNamespaceAware(true);
            dbf.setValidating(false);

            DocumentBuilder db = dbf.newDocumentBuilder();
            InputStream is = 
                AccessController.doPrivileged(
                    new PrivilegedAction<InputStream>() {
                        public InputStream run() {
                            String cfile = 
                                System.getProperty("org.apache.xml.security.resource.config");
                            return getClass().getResourceAsStream
                                (cfile != null ? cfile : "resource/config.xml");
                        }
                    });

            Document doc = db.parse(is);
            Node config = doc.getFirstChild();
            for (; config != null; config = config.getNextSibling()) {
                if ("Configuration".equals(config.getLocalName())) {
                    break;
                }
            }
            for (Node el = config.getFirstChild(); el != null; el = el.getNextSibling()) {
                if (el == null || Node.ELEMENT_NODE != el.getNodeType()) {
                    continue;
                }
                String tag = el.getLocalName();
                if (tag.equals("ResourceBundles")) {
                    Element resource = (Element)el;
                    /* configure internationalization */
                    Attr langAttr = resource.getAttributeNode("defaultLanguageCode");
                    Attr countryAttr = resource.getAttributeNode("defaultCountryCode");
                    String languageCode = 
                        (langAttr == null) ? null : langAttr.getNodeValue();
                    String countryCode = 
                        (countryAttr == null) ? null : countryAttr.getNodeValue();
                    I18n.init(languageCode, countryCode);
                }

                if (tag.equals("CanonicalizationMethods")) {
                    Element[] list =
                        XMLUtils.selectNodes(el.getFirstChild(), CONF_NS, "CanonicalizationMethod");               

                    for (int i = 0; i < list.length; i++) {
                        String URI = list[i].getAttributeNS(null, "URI");
                        String JAVACLASS =
                            list[i].getAttributeNS(null, "JAVACLASS");
                        try {
                            Canonicalizer.register(URI, JAVACLASS);
                            if (log.isDebugEnabled()) {
                                log.debug("Canonicalizer.register(" + URI + ", " + JAVACLASS + ")");
                            }
                        } catch (ClassNotFoundException e) {
                            Object exArgs[] = { URI, JAVACLASS };
                            log.error(I18n.translate("algorithm.classDoesNotExist", exArgs));
                        }
                    }
                }

                if (tag.equals("TransformAlgorithms")) {
                    Element[] tranElem = 
                        XMLUtils.selectNodes(el.getFirstChild(), CONF_NS, "TransformAlgorithm");

                    for (int i = 0; i < tranElem.length; i++) {
                        String URI = tranElem[i].getAttributeNS(null, "URI");
                        String JAVACLASS =
                            tranElem[i].getAttributeNS(null, "JAVACLASS");
                        try {
                            Transform.register(URI, JAVACLASS);
                            if (log.isDebugEnabled()) {
                                log.debug("Transform.register(" + URI + ", " + JAVACLASS + ")");
                            }
                        } catch (ClassNotFoundException e) {
                            Object exArgs[] = { URI, JAVACLASS };

                            log.error(I18n.translate("algorithm.classDoesNotExist", exArgs));
                        } catch (NoClassDefFoundError ex) {
                            log.warn("Not able to found dependencies for algorithm, I'll keep working.");
                        }
                    }
                }

                if ("JCEAlgorithmMappings".equals(tag)) {
                    JCEMapper.init((Element)el);
                }

                if (tag.equals("SignatureAlgorithms")) {
                    Element[] sigElems = 
                        XMLUtils.selectNodes(el.getFirstChild(), CONF_NS, "SignatureAlgorithm");

                    for (int i = 0; i < sigElems.length; i++) {
                        String URI = sigElems[i].getAttributeNS(null, "URI");
                        String JAVACLASS =
                            sigElems[i].getAttributeNS(null, "JAVACLASS");

                        /** $todo$ handle registering */

                        try {
                            SignatureAlgorithm.register(URI, JAVACLASS);
                            if (log.isDebugEnabled()) {
                                log.debug("SignatureAlgorithm.register(" + URI + ", "
                                          + JAVACLASS + ")");
                            }
                        } catch (ClassNotFoundException e) {
                            Object exArgs[] = { URI, JAVACLASS };

                            log.error(I18n.translate("algorithm.classDoesNotExist", exArgs));
                        }
                    }
                }

                if (tag.equals("ResourceResolvers")) {
                    Element[]resolverElem = 
                        XMLUtils.selectNodes(el.getFirstChild(), CONF_NS, "Resolver");

                    for (int i = 0; i < resolverElem.length; i++) {
                        String JAVACLASS =
                            resolverElem[i].getAttributeNS(null, "JAVACLASS");
                        String Description =
                            resolverElem[i].getAttributeNS(null, "DESCRIPTION");

                        if ((Description != null) && (Description.length() > 0)) {
                            if (log.isDebugEnabled()) {
                                log.debug("Register Resolver: " + JAVACLASS + ": "
                                          + Description);
                            }
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug("Register Resolver: " + JAVACLASS
                                          + ": For unknown purposes");
                            }
                        }
                        try {
                            ResourceResolver.register(JAVACLASS);
                        } catch (Throwable e) {
                            log.warn(
                                 "Cannot register:" + JAVACLASS 
                                 + " perhaps some needed jars are not installed", 
                                 e
                             );
                        }
                    }               
                }

                if (tag.equals("KeyResolver")){
                    Element[] resolverElem = 
                        XMLUtils.selectNodes(el.getFirstChild(), CONF_NS, "Resolver");

                    for (int i = 0; i < resolverElem.length; i++) {
                        String JAVACLASS =
                            resolverElem[i].getAttributeNS(null, "JAVACLASS");
                        String Description =
                            resolverElem[i].getAttributeNS(null, "DESCRIPTION");

                        if ((Description != null) && (Description.length() > 0)) {
                            if (log.isDebugEnabled()) {
                                log.debug("Register Resolver: " + JAVACLASS + ": "
                                          + Description);
                            }
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug("Register Resolver: " + JAVACLASS
                                          + ": For unknown purposes");
                            }
                        }
                        KeyResolver.register(JAVACLASS);
                    }
                }


                if (tag.equals("PrefixMappings")){
                    if (log.isDebugEnabled()) {
                        log.debug("Now I try to bind prefixes:");
                    }

                    Element[] nl = 
                        XMLUtils.selectNodes(el.getFirstChild(), CONF_NS, "PrefixMapping");

                    for (int i = 0; i < nl.length; i++) {
                        String namespace = nl[i].getAttributeNS(null, "namespace");
                        String prefix = nl[i].getAttributeNS(null, "prefix");
                        if (log.isDebugEnabled()) {
                            log.debug("Now I try to bind " + prefix + " to " + namespace);
                        }
                        ElementProxy.setDefaultPrefix(namespace, prefix);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Bad: ", e);
            e.printStackTrace();
        }
        alreadyInitialized = true;
    }
    
    /**
     * TODO
     */
    public synchronized static void dynamicInit() {
        if (alreadyInitialized) {
            return;
        }
        
        //
        // Load the Resource Bundle - the default is the English resource bundle.
        // To load another resource bundle, call I18n.init(...) before calling this
        // method.
        //
        I18n.init("en", "US");
        
        //
        // Bind the default prefixes
        // TODO possibly move the default Map into ElementProxy?
        //
        try {
            for (String key : defaultNamespacePrefixes.keySet()) {
                ElementProxy.setDefaultPrefix(key, defaultNamespacePrefixes.get(key));
            }
        } catch (Exception ex) {
            log.error(ex);
            ex.printStackTrace();
        }
    }

}


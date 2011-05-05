/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.xml.security;

import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.algorithms.SignatureAlgorithm;
import org.apache.xml.security.algorithms.SignatureAlgorithmSpi;
import org.apache.xml.security.algorithms.implementations.IntegrityHmac;
import org.apache.xml.security.algorithms.implementations.SignatureBaseRSA;
import org.apache.xml.security.algorithms.implementations.SignatureDSA;
import org.apache.xml.security.algorithms.implementations.SignatureECDSA;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.CanonicalizerSpi;
import org.apache.xml.security.c14n.implementations.Canonicalizer11_OmitComments;
import org.apache.xml.security.c14n.implementations.Canonicalizer11_WithComments;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315ExclOmitComments;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315ExclWithComments;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315OmitComments;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315WithComments;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.keys.keyresolver.KeyResolver;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.keyresolver.implementations.DSAKeyValueResolver;
import org.apache.xml.security.keys.keyresolver.implementations.RSAKeyValueResolver;
import org.apache.xml.security.keys.keyresolver.implementations.RetrievalMethodResolver;
import org.apache.xml.security.keys.keyresolver.implementations.X509CertificateResolver;
import org.apache.xml.security.keys.keyresolver.implementations.X509IssuerSerialResolver;
import org.apache.xml.security.keys.keyresolver.implementations.X509SKIResolver;
import org.apache.xml.security.keys.keyresolver.implementations.X509SubjectNameResolver;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.transforms.TransformSpi;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.transforms.implementations.TransformBase64Decode;
import org.apache.xml.security.transforms.implementations.TransformC14N;
import org.apache.xml.security.transforms.implementations.TransformC14N11;
import org.apache.xml.security.transforms.implementations.TransformC14N11_WithComments;
import org.apache.xml.security.transforms.implementations.TransformC14NExclusive;
import org.apache.xml.security.transforms.implementations.TransformC14NExclusiveWithComments;
import org.apache.xml.security.transforms.implementations.TransformC14NWithComments;
import org.apache.xml.security.transforms.implementations.TransformEnvelopedSignature;
import org.apache.xml.security.transforms.implementations.TransformXPath;
import org.apache.xml.security.transforms.implementations.TransformXPath2Filter;
import org.apache.xml.security.transforms.implementations.TransformXSLT;
import org.apache.xml.security.utils.ElementProxy;
import org.apache.xml.security.utils.I18n;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.resolver.ResourceResolver;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.apache.xml.security.utils.resolver.implementations.ResolverDirectHTTP;
import org.apache.xml.security.utils.resolver.implementations.ResolverFragment;
import org.apache.xml.security.utils.resolver.implementations.ResolverLocalFilesystem;
import org.apache.xml.security.utils.resolver.implementations.ResolverXPointer;
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
    private static Map<String, Class<? extends TransformSpi>> defaultTransforms = 
        new HashMap<String, Class<? extends TransformSpi>>();
    private static Map<String, Class<? extends SignatureAlgorithmSpi>> defaultSignatures = 
        new HashMap<String, Class<? extends SignatureAlgorithmSpi>>();
    private static Map<String, JCEMapper.Algorithm> defaultAlgorithms = 
        new HashMap<String, JCEMapper.Algorithm>();
    private static Map<String, Class<? extends CanonicalizerSpi>> defaultC14nAlgorithms = 
        new HashMap<String, Class<? extends CanonicalizerSpi>>();
    private static List<ResourceResolverSpi> defaultResolverList = 
        new ArrayList<ResourceResolverSpi>();
    private static List<KeyResolverSpi> defaultKeyResolverList = 
        new ArrayList<KeyResolverSpi>();
    
    static {
        //
        // Default Namespace-prefix pairs
        //
        defaultNamespacePrefixes.put("http://www.w3.org/2000/09/xmldsig#", "ds");
        defaultNamespacePrefixes.put("http://www.w3.org/2001/04/xmlenc#", "xenc");
        defaultNamespacePrefixes.put("http://www.xmlsecurity.org/experimental#", "experimental");
        defaultNamespacePrefixes.put("http://www.w3.org/2002/04/xmldsig-filter2", "dsig-xpath-old");
        defaultNamespacePrefixes.put("http://www.w3.org/2002/06/xmldsig-filter2", "dsig-xpath");
        defaultNamespacePrefixes.put("http://www.w3.org/2001/10/xml-exc-c14n#", "ec");
        defaultNamespacePrefixes.put(
            "http://www.nue.et-inf.uni-siegen.de/~geuer-pollmann/#xpathFilter", "xx"
        );
        
        //
        // Default URI-TransformSpi class pairs
        //
        defaultTransforms.put(
            Transforms.TRANSFORM_BASE64_DECODE, TransformBase64Decode.class
        );
        defaultTransforms.put(
            Transforms.TRANSFORM_C14N_OMIT_COMMENTS, TransformC14N.class
        );
        defaultTransforms.put(
            Transforms.TRANSFORM_C14N_WITH_COMMENTS, TransformC14NWithComments.class
        );
        defaultTransforms.put(
            Transforms.TRANSFORM_C14N11_OMIT_COMMENTS, TransformC14N11.class
        );
        defaultTransforms.put(
            Transforms.TRANSFORM_C14N11_WITH_COMMENTS, TransformC14N11_WithComments.class
        );
        defaultTransforms.put(
            Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS, TransformC14NExclusive.class
        );
        defaultTransforms.put(
            Transforms.TRANSFORM_C14N_EXCL_WITH_COMMENTS, TransformC14NExclusiveWithComments.class
        );
        defaultTransforms.put(
            Transforms.TRANSFORM_XPATH, TransformXPath.class
        );
        defaultTransforms.put(
            Transforms.TRANSFORM_ENVELOPED_SIGNATURE, TransformEnvelopedSignature.class
        );
        defaultTransforms.put(
            Transforms.TRANSFORM_XSLT, TransformXSLT.class
        );
        defaultTransforms.put(
            Transforms.TRANSFORM_XPATH2FILTER, TransformXPath2Filter.class
        );
        defaultTransforms.put(
            Transforms.TRANSFORM_XPATH2FILTER04, TransformXPath2Filter.class
        );
        
        //
        // default URI-SignatureAlgorithmSpi class pairs
        //
        defaultSignatures.put(SignatureDSA.URI, SignatureDSA.class);
        defaultSignatures.put(
            XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1, SignatureBaseRSA.SignatureRSASHA1.class
        );
        defaultSignatures.put(
            XMLSignature.ALGO_ID_MAC_HMAC_SHA1, IntegrityHmac.IntegrityHmacSHA1.class
        );
        defaultSignatures.put(
            XMLSignature.ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5, 
            SignatureBaseRSA.SignatureRSAMD5.class
        );
        defaultSignatures.put(
            XMLSignature.ALGO_ID_SIGNATURE_RSA_RIPEMD160, 
            SignatureBaseRSA.SignatureRSARIPEMD160.class
        );
        defaultSignatures.put(
            XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256, SignatureBaseRSA.SignatureRSASHA256.class
        );
        defaultSignatures.put(
            XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA384, SignatureBaseRSA.SignatureRSASHA384.class
        );
        defaultSignatures.put(
            XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA512, SignatureBaseRSA.SignatureRSASHA512.class
        );
        defaultSignatures.put(
            XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA1, SignatureECDSA.SignatureECDSASHA1.class
        );
        defaultSignatures.put(
            XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA256, SignatureECDSA.SignatureECDSASHA256.class
        );
        defaultSignatures.put(
            XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA384, SignatureECDSA.SignatureECDSASHA384.class
        );
        defaultSignatures.put(
            XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA512, SignatureECDSA.SignatureECDSASHA512.class
        );
        defaultSignatures.put(
            XMLSignature.ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5, IntegrityHmac.IntegrityHmacMD5.class
        );
        defaultSignatures.put(
            XMLSignature.ALGO_ID_MAC_HMAC_RIPEMD160, IntegrityHmac.IntegrityHmacRIPEMD160.class
        );
        defaultSignatures.put(
            XMLSignature.ALGO_ID_MAC_HMAC_SHA256, IntegrityHmac.IntegrityHmacSHA256.class
        );
        defaultSignatures.put(
            XMLSignature.ALGO_ID_MAC_HMAC_SHA384, IntegrityHmac.IntegrityHmacSHA384.class
        );
        defaultSignatures.put(
            XMLSignature.ALGO_ID_MAC_HMAC_SHA512, IntegrityHmac.IntegrityHmacSHA512.class
        );
        
        //
        // Default URI-Algorithm object pairs
        //
        defaultAlgorithms.put(
            MessageDigestAlgorithm.ALGO_ID_DIGEST_NOT_RECOMMENDED_MD5, 
            new JCEMapper.Algorithm(null, "MD5")
        );
        defaultAlgorithms.put(
            MessageDigestAlgorithm.ALGO_ID_DIGEST_RIPEMD160, 
            new JCEMapper.Algorithm(null, "RIPEMD160")
        );
        defaultAlgorithms.put(
            MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1, 
            new JCEMapper.Algorithm(null, "SHA-1")
        );
        defaultAlgorithms.put(
            MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA256, 
            new JCEMapper.Algorithm(null, "SHA-256")
        );
        defaultAlgorithms.put(
            MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA384, 
            new JCEMapper.Algorithm(null, "SHA-384")
        );
        defaultAlgorithms.put(
            MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA512, 
            new JCEMapper.Algorithm(null, "SHA-512")
        );
        defaultAlgorithms.put(
            XMLSignature.ALGO_ID_SIGNATURE_DSA, 
            new JCEMapper.Algorithm(null, "SHA1withDSA")
        );
        defaultAlgorithms.put(
            XMLSignature.ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5, 
            new JCEMapper.Algorithm(null, "MD5withRSA")
        );
        defaultAlgorithms.put(
            XMLSignature.ALGO_ID_SIGNATURE_RSA_RIPEMD160, 
            new JCEMapper.Algorithm(null, "RIPEMD160withRSA")
        );
        defaultAlgorithms.put(
            XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1, 
            new JCEMapper.Algorithm(null, "SHA1withRSA")
        );
        defaultAlgorithms.put(
            XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256, 
            new JCEMapper.Algorithm(null, "SHA256withRSA")
        );
        defaultAlgorithms.put(
            XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA384, 
            new JCEMapper.Algorithm(null, "SHA384withRSA")
        );
        defaultAlgorithms.put(
            XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA512, 
            new JCEMapper.Algorithm(null, "SHA512withRSA")
        );
        defaultAlgorithms.put(
            XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA1, 
            new JCEMapper.Algorithm(null, "SHA1withECDSA")
        );
        defaultAlgorithms.put(
            XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA256, 
            new JCEMapper.Algorithm(null, "SHA256withECDSA")
        );
        defaultAlgorithms.put(
            XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA384, 
            new JCEMapper.Algorithm(null, "SHA384withECDSA")
        );
        defaultAlgorithms.put(
            XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA512, 
            new JCEMapper.Algorithm(null, "SHA512withECDSA")
        );
        defaultAlgorithms.put(
            XMLSignature.ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5, 
            new JCEMapper.Algorithm(null, "HmacMD5")
        );
        defaultAlgorithms.put(
            XMLSignature.ALGO_ID_MAC_HMAC_RIPEMD160, 
            new JCEMapper.Algorithm(null, "HMACRIPEMD160")
        );
        defaultAlgorithms.put(
            XMLSignature.ALGO_ID_MAC_HMAC_SHA1, 
            new JCEMapper.Algorithm(null, "HmacSHA1")
        );
        defaultAlgorithms.put(
            XMLSignature.ALGO_ID_MAC_HMAC_SHA256, 
            new JCEMapper.Algorithm(null, "HmacSHA256")
        );
        defaultAlgorithms.put(
            XMLSignature.ALGO_ID_MAC_HMAC_SHA384, 
            new JCEMapper.Algorithm(null, "HmacSHA384")
        );
        defaultAlgorithms.put(
            XMLSignature.ALGO_ID_MAC_HMAC_SHA512, 
            new JCEMapper.Algorithm(null, "HmacSHA512")
        );
        defaultAlgorithms.put(
            XMLCipher.TRIPLEDES, 
            new JCEMapper.Algorithm("DESede", "DESede/CBC/ISO10126Padding")
        );
        defaultAlgorithms.put(
            XMLCipher.AES_128, 
            new JCEMapper.Algorithm("AES", "AES/CBC/ISO10126Padding")
        );
        defaultAlgorithms.put(
            XMLCipher.AES_192, 
            new JCEMapper.Algorithm("AES", "AES/CBC/ISO10126Padding")
        );
        defaultAlgorithms.put(
            XMLCipher.AES_256, 
            new JCEMapper.Algorithm("AES", "AES/CBC/ISO10126Padding")
        );
        defaultAlgorithms.put(
            XMLCipher.RSA_v1dot5, 
            new JCEMapper.Algorithm("RSA", "RSA/ECB/PKCS1Padding")
        );
        defaultAlgorithms.put(
            XMLCipher.RSA_OAEP, 
            new JCEMapper.Algorithm("RSA", "RSA/ECB/OAEPWithSHA1AndMGF1Padding")
        );
        defaultAlgorithms.put(
            XMLCipher.DIFFIE_HELLMAN, 
            new JCEMapper.Algorithm(null, null)
        );
        defaultAlgorithms.put(
            XMLCipher.TRIPLEDES_KeyWrap, 
            new JCEMapper.Algorithm("DESede", "DESedeWrap")
        );
        defaultAlgorithms.put(
            XMLCipher.AES_128_KeyWrap, 
            new JCEMapper.Algorithm("AES", "AESWrap")
        );
        defaultAlgorithms.put(
            XMLCipher.AES_192_KeyWrap, 
            new JCEMapper.Algorithm("AES", "AESWrap")
        );
        defaultAlgorithms.put(
            XMLCipher.AES_256_KeyWrap, 
            new JCEMapper.Algorithm("AES", "AESWrap")
        );
        
        //
        // Default URI-Canonicalizer class pairs
        //
        defaultC14nAlgorithms.put(
            Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS, Canonicalizer20010315OmitComments.class
        );
        defaultC14nAlgorithms.put(
            Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS, Canonicalizer20010315WithComments.class
        );
        defaultC14nAlgorithms.put(
            Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS, Canonicalizer20010315ExclOmitComments.class
        );
        defaultC14nAlgorithms.put(
            Canonicalizer.ALGO_ID_C14N_EXCL_WITH_COMMENTS, Canonicalizer20010315ExclWithComments.class
        );
        defaultC14nAlgorithms.put(
            Canonicalizer.ALGO_ID_C14N11_OMIT_COMMENTS, Canonicalizer11_OmitComments.class
        );
        defaultC14nAlgorithms.put(
            Canonicalizer.ALGO_ID_C14N11_WITH_COMMENTS, Canonicalizer11_WithComments.class
        );
        
        //
        // Default Resolvers
        //
        defaultResolverList.add(new ResolverFragment());
        defaultResolverList.add(new ResolverLocalFilesystem());
        defaultResolverList.add(new ResolverXPointer());
        defaultResolverList.add(new ResolverDirectHTTP());
        
        //
        // Default KeyResolvers
        //
        defaultKeyResolverList.add(new RSAKeyValueResolver());
        defaultKeyResolverList.add(new DSAKeyValueResolver());
        defaultKeyResolverList.add(new X509CertificateResolver());
        defaultKeyResolverList.add(new X509SKIResolver());
        defaultKeyResolverList.add(new RetrievalMethodResolver());
        defaultKeyResolverList.add(new X509SubjectNameResolver());
        defaultKeyResolverList.add(new X509IssuerSerialResolver());
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
            if (config == null) {
                log.error("Error in reading configuration file - Configuration element not found");
                return;
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
                    Node algorithmsNode = ((Element)el).getElementsByTagName("Algorithms").item(0);
                    if (algorithmsNode != null) {
                        Element[] algorithms = 
                            XMLUtils.selectNodes(algorithmsNode.getFirstChild(), CONF_NS, "Algorithm");
                        for (int i = 0; i < algorithms.length; i++) {
                            Element element = algorithms[i];
                            String id = element.getAttribute("URI");
                            JCEMapper.register(id, new JCEMapper.Algorithm(element));
                        }
                    }
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
                    List<String> classNames = new ArrayList<String>(resolverElem.length);
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
                        classNames.add(JAVACLASS);
                    }
                    KeyResolver.registerClassNames(classNames);
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
    @SuppressWarnings("unchecked")
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
        
        try {
            //
            // Bind the default prefixes
            // TODO possibly move the default Map into ElementProxy?
            //
            for (String key : defaultNamespacePrefixes.keySet()) {
                ElementProxy.setDefaultPrefix(key, defaultNamespacePrefixes.get(key));
            }
            
            //
            // Set the default Transforms
            //
            for (String key : defaultTransforms.keySet()) {
                Transform.register(key, (Class<TransformSpi>)defaultTransforms.get(key));
            }
            
            //
            // Set the default signature algorithms
            //
            for (String key : defaultSignatures.keySet()) {
                SignatureAlgorithm.register(
                    key, (Class<SignatureAlgorithmSpi>)defaultSignatures.get(key)
                );
            }
            
            //
            // Set the default JCE algorithms
            //
            for (String key : defaultAlgorithms.keySet()) {
                JCEMapper.register(key, defaultAlgorithms.get(key));
            }
            
            //
            // Set the default c14n algorithms
            //
            for (String key : defaultC14nAlgorithms.keySet()) {
                Canonicalizer.register(
                    key, (Class<CanonicalizerSpi>)defaultC14nAlgorithms.get(key)
                );
            }
            
            //
            // Register the default resolvers
            //
            for (ResourceResolverSpi resourceResolverSpi : defaultResolverList) {
                ResourceResolver.register(resourceResolverSpi, false);
            }
            
            //
            // Register the default key resolvers
            //
            KeyResolver.register(defaultKeyResolverList);
        } catch (Exception ex) {
            log.error(ex);
            ex.printStackTrace();
        }
        alreadyInitialized = true;
    }

}


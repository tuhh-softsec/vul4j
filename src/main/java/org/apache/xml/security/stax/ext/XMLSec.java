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
package org.apache.xml.security.stax.ext;

import java.net.URISyntaxException;
import java.net.URL;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashSet;

import javax.crypto.SecretKey;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.validation.Schema;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.config.Init;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;
import org.apache.xml.security.utils.ClassLoaderUtils;
import org.xml.sax.SAXException;

/**
 * This is the central class of the streaming XML-security framework.<br/>
 * Instances of the inbound and outbound security streams can be retrieved
 * with this class.
 *
 * @author $Author: coheigea $
 * @version $Revision: 1354898 $ $Date: 2012-06-28 11:19:02 +0100 (Thu, 28 Jun 2012) $
 */
public class XMLSec {
    
    static {
        try {
            URL resource = ClassLoaderUtils.getResource("security-config.xml", XMLSec.class);
            if (resource == null) {
                throw new RuntimeException("security-config.xml not found in classpath");
            }
            Init.init(resource.toURI(), XMLSec.class);
            
            try {
                XMLSecurityConstants.setJaxbContext(
                        JAXBContext.newInstance(
                            org.apache.xml.security.binding.xmlenc.ObjectFactory.class,
                            org.apache.xml.security.binding.xmlenc11.ObjectFactory.class,
                            org.apache.xml.security.binding.xmldsig.ObjectFactory.class,
                            org.apache.xml.security.binding.xmldsig11.ObjectFactory.class,
                            org.apache.xml.security.binding.excc14n.ObjectFactory.class 
                        )
                );
                
                Schema schema = XMLSecurityUtils.loadXMLSecuritySchemas();
                XMLSecurityConstants.setJaxbSchemas(schema);
            } catch (JAXBException e) {
                throw new RuntimeException(e);
            } catch (SAXException e) {
                throw new RuntimeException(e);
            }
        } catch (XMLSecurityException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    public static void init() {
        // Do nothing
    }

    /**
     * Creates and configures an outbound streaming security engine
     *
     * @param securityProperties The user-defined security configuration
     * @return A new OutboundXMLSec
     * @throws XMLSecurityException
     *          if the initialisation failed
     * @throws org.apache.xml.security.stax.ext.XMLSecurityConfigurationException
     *          if the configuration is invalid
     */
    public static OutboundXMLSec getOutboundXMLSec(XMLSecurityProperties securityProperties) throws XMLSecurityException {
        if (securityProperties == null) {
            throw new XMLSecurityConfigurationException("stax.missingSecurityProperties");
        }

        securityProperties = validateAndApplyDefaultsToOutboundSecurityProperties(securityProperties);
        return new OutboundXMLSec(securityProperties);
    }

    /**
     * Creates and configures an inbound streaming security engine
     *
     * @param securityProperties The user-defined security configuration
     * @return A new InboundWSSec
     * @throws XMLSecurityException
     *          if the initialisation failed
     * @throws org.apache.xml.security.stax.ext.XMLSecurityConfigurationException
     *          if the configuration is invalid
     */
    public static InboundXMLSec getInboundWSSec(XMLSecurityProperties securityProperties) throws XMLSecurityException {
        if (securityProperties == null) {
            throw new XMLSecurityConfigurationException("stax.missingSecurityProperties");
        }

        securityProperties = validateAndApplyDefaultsToInboundSecurityProperties(securityProperties);
        return new InboundXMLSec(securityProperties);
    }

    /**
     * Validates the user supplied configuration and applies default values as appropriate for the outbound security engine
     *
     * @param securityProperties The configuration to validate
     * @return The validated configuration
     * @throws org.apache.xml.security.stax.ext.XMLSecurityConfigurationException
     *          if the configuration is invalid
     */
    public static XMLSecurityProperties validateAndApplyDefaultsToOutboundSecurityProperties(XMLSecurityProperties securityProperties) throws XMLSecurityConfigurationException {
        if (securityProperties.getActions() == null || securityProperties.getActions().isEmpty()) {
            throw new XMLSecurityConfigurationException("stax.noOutputAction");
        }
        
        // Check for duplicate actions
        if (new HashSet<XMLSecurityConstants.Action>(securityProperties.getActions()).size() 
            != securityProperties.getActions().size()) {
            throw new XMLSecurityConfigurationException("stax.duplicateActions");
        }

        for (XMLSecurityConstants.Action action : securityProperties.getActions()) {
            if (XMLSecurityConstants.SIGNATURE.equals(action)) {
                if (securityProperties.getSignatureAlgorithm() == null) {
                    if (securityProperties.getSignatureKey() instanceof RSAPrivateKey) {
                        securityProperties.setSignatureAlgorithm("http://www.w3.org/2000/09/xmldsig#rsa-sha1");
                    } else if (securityProperties.getSignatureKey() instanceof DSAPrivateKey) {
                        securityProperties.setSignatureAlgorithm("http://www.w3.org/2000/09/xmldsig#dsa-sha1");
                    } else if (securityProperties.getSignatureKey() instanceof SecretKey) {
                        securityProperties.setSignatureAlgorithm("http://www.w3.org/2000/09/xmldsig#hmac-sha1");
                    }
                }
                if (securityProperties.getSignatureDigestAlgorithm() == null) {
                    securityProperties.setSignatureDigestAlgorithm("http://www.w3.org/2000/09/xmldsig#sha1");
                }
                if (securityProperties.getSignatureCanonicalizationAlgorithm() == null) {
                    securityProperties.setSignatureCanonicalizationAlgorithm(XMLSecurityConstants.NS_C14N_EXCL_OMIT_COMMENTS);
                }
                if (securityProperties.getSignatureKeyIdentifier() == null) {
                    securityProperties.setSignatureKeyIdentifier(SecurityTokenConstants.KeyIdentifier_IssuerSerial);
                }
            } else if (XMLSecurityConstants.ENCRYPT.equals(action)) {
                if (securityProperties.getEncryptionKeyTransportAlgorithm() == null) {
                    //@see http://www.w3.org/TR/2002/REC-xmlenc-core-20021210/Overview.html#rsa-1_5 :
                    //"RSA-OAEP is RECOMMENDED for the transport of AES keys"
                    //@see http://www.w3.org/TR/2002/REC-xmlenc-core-20021210/Overview.html#rsa-oaep-mgf1p
                    securityProperties.setEncryptionKeyTransportAlgorithm("http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p");
                }
                if (securityProperties.getEncryptionSymAlgorithm() == null) {
                    securityProperties.setEncryptionSymAlgorithm("http://www.w3.org/2001/04/xmlenc#aes256-cbc");
                }
                if (securityProperties.getEncryptionKeyIdentifier() == null) {
                    securityProperties.setEncryptionKeyIdentifier(SecurityTokenConstants.KeyIdentifier_IssuerSerial);
                }
            }
        }
        return new XMLSecurityProperties(securityProperties);
    }

    /**
     * Validates the user supplied configuration and applies default values as appropriate for the inbound security engine
     *
     * @param securityProperties The configuration to validate
     * @return The validated configuration
     * @throws org.apache.xml.security.stax.ext.XMLSecurityConfigurationException
     *          if the configuration is invalid
     */
    public static XMLSecurityProperties validateAndApplyDefaultsToInboundSecurityProperties(XMLSecurityProperties securityProperties) throws XMLSecurityConfigurationException {
        return new XMLSecurityProperties(securityProperties);
    }
}

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
package org.apache.xml.security.stax.impl.securityToken;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.security.auth.callback.CallbackHandler;

import org.apache.xml.security.binding.xmldsig.DSAKeyValueType;
import org.apache.xml.security.binding.xmldsig.KeyInfoType;
import org.apache.xml.security.binding.xmldsig.KeyValueType;
import org.apache.xml.security.binding.xmldsig.RSAKeyValueType;
import org.apache.xml.security.binding.xmldsig.X509DataType;
import org.apache.xml.security.binding.xmldsig.X509IssuerSerialType;
import org.apache.xml.security.binding.xmldsig11.ECKeyValueType;
import org.apache.xml.security.exceptions.Base64DecodingException;
import org.apache.xml.security.stax.ext.SecurityContext;
import org.apache.xml.security.stax.ext.SecurityToken;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityConstants.KeyIdentifierType;
import org.apache.xml.security.stax.ext.XMLSecurityConstants.KeyUsage;
import org.apache.xml.security.stax.ext.XMLSecurityConstants.TokenType;
import org.apache.xml.security.stax.ext.XMLSecurityException;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.XMLSecurityUtils;
import org.apache.xml.security.utils.RFC2253Parser;

/**
 * Factory to create SecurityToken Objects from keys in XML
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class SecurityTokenFactoryImpl extends SecurityTokenFactory {

    protected SecurityTokenFactoryImpl() {
    }

    @Override
    public SecurityToken getSecurityToken(KeyInfoType keyInfoType,
                                          SecurityToken.KeyInfoUsage keyInfoUsage,
                                          XMLSecurityProperties securityProperties,
                                          SecurityContext securityContext) throws XMLSecurityException {
        if (keyInfoType != null) {
            // KeyValue
            final KeyValueType keyValueType
                    = XMLSecurityUtils.getQNameType(keyInfoType.getContent(), XMLSecurityConstants.TAG_dsig_KeyValue);
            if (keyValueType != null) {
                return getSecurityToken(keyValueType, securityProperties.getCallbackHandler(), securityContext);
            }
            
            // KeyName
            final String keyName = 
                XMLSecurityUtils.getQNameType(keyInfoType.getContent(), XMLSecurityConstants.TAG_dsig_KeyName);
            if (keyName != null) {
                KeyNameSecurityToken token = 
                    new KeyNameSecurityToken(keyName, securityContext, securityProperties.getCallbackHandler(), 
                            XMLSecurityConstants.XMLKeyIdentifierType.KEY_NAME);
                token.setKey(securityProperties.getSignatureVerificationKey());
                return token;
            }
            
            // X509Data
            final X509DataType x509DataType = 
                XMLSecurityUtils.getQNameType(keyInfoType.getContent(), XMLSecurityConstants.TAG_dsig_X509Data);
            if (x509DataType != null) {
                try {
                    return getSecurityToken(x509DataType, securityProperties, securityContext);
                } catch (Base64DecodingException e) {
                    throw new XMLSecurityException(XMLSecurityException.ErrorCode.INVALID_SECURITY, "noKeyinfo", e);
                }
            }
        }
        
        // Use a default key if it exists
        if (securityProperties.getSignatureVerificationKey() != null) {
            DefaultSecurityToken token = 
                    new DefaultSecurityToken(securityContext, securityProperties.getCallbackHandler(), "", 
                            XMLSecurityConstants.XMLKeyIdentifierType.NO_KEY_INFO);
            token.setKey(securityProperties.getSignatureVerificationKey());
            return token;
        }
        
        throw new XMLSecurityException(XMLSecurityException.ErrorCode.INVALID_SECURITY, "noKeyinfo");
    }
    
    private static SecurityToken getSecurityToken(KeyValueType keyValueType,
            final CallbackHandler callbackHandler, SecurityContext securityContext)
        throws XMLSecurityException {

        final RSAKeyValueType rsaKeyValueType = 
                XMLSecurityUtils.getQNameType(keyValueType.getContent(), XMLSecurityConstants.TAG_dsig_RSAKeyValue);
        if (rsaKeyValueType != null) {
            return new RsaKeyValueSecurityToken(rsaKeyValueType, securityContext,
                    callbackHandler, XMLSecurityConstants.XMLKeyIdentifierType.KEY_VALUE);
        }
        final DSAKeyValueType dsaKeyValueType = 
                XMLSecurityUtils.getQNameType(keyValueType.getContent(), XMLSecurityConstants.TAG_dsig_DSAKeyValue);
        if (dsaKeyValueType != null) {
            return new DsaKeyValueSecurityToken(dsaKeyValueType, securityContext,
                    callbackHandler, XMLSecurityConstants.XMLKeyIdentifierType.KEY_VALUE);
        }
        final ECKeyValueType ecKeyValueType = 
                XMLSecurityUtils.getQNameType(keyValueType.getContent(), XMLSecurityConstants.TAG_dsig11_ECKeyValue);
        if (ecKeyValueType != null) {
            return new ECKeyValueSecurityToken(ecKeyValueType, securityContext,
                    callbackHandler, XMLSecurityConstants.XMLKeyIdentifierType.KEY_VALUE);
        }
        throw new XMLSecurityException(XMLSecurityException.ErrorCode.INVALID_SECURITY, "unsupportedKeyInfo");
    }
    
    private static SecurityToken getSecurityToken(X509DataType x509DataType,
                                                  XMLSecurityProperties securityProperties, 
                                                  SecurityContext securityContext)
                                              throws XMLSecurityException, Base64DecodingException {
        // X509Certificate
        byte[] certBytes = 
            XMLSecurityUtils.getQNameType(
                x509DataType.getX509IssuerSerialOrX509SKIOrX509SubjectName(), 
                XMLSecurityConstants.TAG_dsig_X509Certificate
            );
        if (certBytes != null) {
            X509Certificate cert = getCertificateFromBytes(certBytes);
            TokenType tokenType = XMLSecurityConstants.X509V3Token;
            if (cert.getVersion() == 1) {
                tokenType = XMLSecurityConstants.X509V1Token;
            }
            X509SecurityToken token = 
                new X509SecurityToken(tokenType, securityContext,
                        securityProperties.getCallbackHandler(), "", 
                        XMLSecurityConstants.XMLKeyIdentifierType.X509_CERTIFICATE);
            token.setX509Certificates(new X509Certificate[]{cert});
            return token;
        }
        
        // Issuer Serial
        final X509IssuerSerialType issuerSerialType = 
            XMLSecurityUtils.getQNameType(
                x509DataType.getX509IssuerSerialOrX509SKIOrX509SubjectName(), 
                XMLSecurityConstants.TAG_dsig_X509IssuerSerial
            );
        if (issuerSerialType != null) {
            if (issuerSerialType.getX509IssuerName() == null
                || issuerSerialType.getX509SerialNumber() == null) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_CHECK);
            }
            X509IssuerSerialSecurityToken token = 
                new X509IssuerSerialSecurityToken(XMLSecurityConstants.X509V3Token, securityContext,
                     securityProperties.getCallbackHandler(), "", XMLSecurityConstants.XMLKeyIdentifierType.X509_ISSUER_SERIAL);
            token.setIssuerName(issuerSerialType.getX509IssuerName());
            token.setSerialNumber(issuerSerialType.getX509SerialNumber());
            token.setKey(securityProperties.getSignatureVerificationKey());
            return token;
        }
        
        // Subject Key Identifier
        byte[] skiBytes = 
            XMLSecurityUtils.getQNameType(
                x509DataType.getX509IssuerSerialOrX509SKIOrX509SubjectName(), 
                XMLSecurityConstants.TAG_dsig_X509SKI
            );
        if (skiBytes != null) {
            X509SKISecurityToken token = 
                new X509SKISecurityToken(XMLSecurityConstants.X509V3Token, securityContext,
                     securityProperties.getCallbackHandler(), "", XMLSecurityConstants.XMLKeyIdentifierType.X509_SKI);
            token.setSkiBytes(skiBytes);
            token.setKey(securityProperties.getSignatureVerificationKey());
            return token;
        }
        
        // Subject Name
        String subjectName = 
            XMLSecurityUtils.getQNameType(
                x509DataType.getX509IssuerSerialOrX509SKIOrX509SubjectName(), 
                XMLSecurityConstants.TAG_dsig_X509SubjectName
            );
        if (subjectName != null) {
            String normalizedSubjectName = 
                RFC2253Parser.normalize(subjectName);
            X509SubjectNameSecurityToken token = 
                new X509SubjectNameSecurityToken(XMLSecurityConstants.X509V3Token, securityContext,
                        securityProperties.getCallbackHandler(), "", 
                        XMLSecurityConstants.XMLKeyIdentifierType.X509_SUBJECT_NAME);
            token.setSubjectName(normalizedSubjectName);
            token.setKey(securityProperties.getSignatureVerificationKey());
            return token;
        }
        
        throw new XMLSecurityException(XMLSecurityException.ErrorCode.INVALID_SECURITY, "noKeyinfo");
    }
    
    private static class DefaultSecurityToken extends AbstractSecurityToken {

        private Key key;
        
        public DefaultSecurityToken(SecurityContext securityContext,
                CallbackHandler callbackHandler, String id,
                KeyIdentifierType keyIdentifierType) {
            super(securityContext, callbackHandler, id, keyIdentifierType);
        }

        @Override
        public boolean isAsymmetric() {
            if (key instanceof PublicKey) {
                return true;
            }
            return false;
        }

        @Override
        public SecurityToken getKeyWrappingToken() throws XMLSecurityException {
            return null;
        }

        @Override
        public TokenType getTokenType() {
            return XMLSecurityConstants.DefaultToken;
        }

        @Override
        protected Key getKey(String algorithmURI, KeyUsage keyUsage)
                throws XMLSecurityException {
            return key;
        }
        
        public void setKey(Key key) {
            this.key = key;
        }

        @Override
        protected PublicKey getPubKey(String algorithmURI, KeyUsage keyUsage)
                throws XMLSecurityException {
            if (key instanceof PublicKey) {
                return (PublicKey)key;
            }
            return null;
        }
        
    }
    
    /**
     * Construct an X509Certificate'from the byte array.
     * <p/>
     *
     * @param data The <code>byte</code> array containing the X509 data
     * @return An X509 certificate
     * @throws XMLSecurityException
     */
    private static X509Certificate getCertificateFromBytes(byte[] data)
            throws XMLSecurityException {
        InputStream in = new ByteArrayInputStream(data);
        try {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) factory.generateCertificate(in);
        } catch (CertificateException e) {
            throw new XMLSecurityException(
                    XMLSecurityException.ErrorCode.SECURITY_TOKEN_UNAVAILABLE, "parseError",
                    null, e
            );
        }
    }
}

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

import org.apache.xml.security.binding.xmldsig.*;
import org.apache.xml.security.binding.xmldsig11.ECKeyValueType;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.*;
import org.apache.xml.security.stax.ext.XMLSecurityConstants.TokenType;
import org.apache.xml.security.stax.impl.util.UnsynchronizedByteArrayInputStream;
import org.apache.xml.security.utils.RFC2253Parser;

import javax.security.auth.callback.CallbackHandler;
import java.io.InputStream;
import java.security.Key;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

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
                setTokenKey(securityProperties, keyInfoUsage, token);
                return token;
            }

            // X509Data
            final X509DataType x509DataType =
                    XMLSecurityUtils.getQNameType(keyInfoType.getContent(), XMLSecurityConstants.TAG_dsig_X509Data);
            if (x509DataType != null) {
                return getSecurityToken(x509DataType, securityProperties, securityContext, keyInfoUsage);
            }
        }

        // Use a default key if it exists
        if (keyInfoUsage == SecurityToken.KeyInfoUsage.SIGNATURE_VERIFICATION
                && securityProperties.getSignatureVerificationKey() != null) {
            AbstractInboundSecurityToken token =
                    new AbstractInboundSecurityToken(securityContext, securityProperties.getCallbackHandler(), null,
                            XMLSecurityConstants.XMLKeyIdentifierType.NO_KEY_INFO) {
                        @Override
                        public TokenType getTokenType() {
                            return XMLSecurityConstants.DefaultToken;
                        }
                    };
            setTokenKey(securityProperties, keyInfoUsage, token);
            return token;
        } else if (keyInfoUsage == SecurityToken.KeyInfoUsage.DECRYPTION
                && securityProperties.getDecryptionKey() != null) {
            AbstractInboundSecurityToken token =
                    new AbstractInboundSecurityToken(securityContext, securityProperties.getCallbackHandler(), null,
                            XMLSecurityConstants.XMLKeyIdentifierType.NO_KEY_INFO) {
                        @Override
                        public TokenType getTokenType() {
                            return XMLSecurityConstants.DefaultToken;
                        }
                    };
            setTokenKey(securityProperties, keyInfoUsage, token);
            return token;
        }

        throw new XMLSecurityException("stax.noKey", keyInfoUsage);
    }

    //todo can the callbackhandler be eliminated?
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
        throw new XMLSecurityException("stax.unsupportedKeyValue");
    }

    private static SecurityToken getSecurityToken(X509DataType x509DataType,
                                                  XMLSecurityProperties securityProperties,
                                                  SecurityContext securityContext,
                                                  SecurityToken.KeyInfoUsage keyInfoUsage)
            throws XMLSecurityException {
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

            setTokenKey(securityProperties, keyInfoUsage, token);
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
                    || issuerSerialType.getX509SerialNumber() == null
                    || securityProperties.getSignatureVerificationKey() == null) {
                throw new XMLSecurityException("stax.noKey", keyInfoUsage);
            }
            X509IssuerSerialSecurityToken token =
                    new X509IssuerSerialSecurityToken(XMLSecurityConstants.X509V3Token, securityContext,
                            securityProperties.getCallbackHandler(), "", XMLSecurityConstants.XMLKeyIdentifierType.X509_ISSUER_SERIAL);
            token.setIssuerName(issuerSerialType.getX509IssuerName());
            token.setSerialNumber(issuerSerialType.getX509SerialNumber());

            setTokenKey(securityProperties, keyInfoUsage, token);
            return token;
        }

        // Subject Key Identifier
        byte[] skiBytes =
                XMLSecurityUtils.getQNameType(
                        x509DataType.getX509IssuerSerialOrX509SKIOrX509SubjectName(),
                        XMLSecurityConstants.TAG_dsig_X509SKI
                );
        if (skiBytes != null) {
            if (securityProperties.getSignatureVerificationKey() == null) {
                throw new XMLSecurityException("stax.noKey", keyInfoUsage);
            }
            X509SKISecurityToken token =
                    new X509SKISecurityToken(XMLSecurityConstants.X509V3Token, securityContext,
                            securityProperties.getCallbackHandler(), "", XMLSecurityConstants.XMLKeyIdentifierType.X509_SKI);
            token.setSkiBytes(skiBytes);

            setTokenKey(securityProperties, keyInfoUsage, token);
            return token;
        }

        // Subject Name
        String subjectName =
                XMLSecurityUtils.getQNameType(
                        x509DataType.getX509IssuerSerialOrX509SKIOrX509SubjectName(),
                        XMLSecurityConstants.TAG_dsig_X509SubjectName
                );
        if (subjectName != null) {
            if (securityProperties.getSignatureVerificationKey() == null) {
                throw new XMLSecurityException("stax.noKey", keyInfoUsage);
            }
            String normalizedSubjectName =
                    RFC2253Parser.normalize(subjectName);
            X509SubjectNameSecurityToken token =
                    new X509SubjectNameSecurityToken(XMLSecurityConstants.X509V3Token, securityContext,
                            securityProperties.getCallbackHandler(), "",
                            XMLSecurityConstants.XMLKeyIdentifierType.X509_SUBJECT_NAME);
            token.setSubjectName(normalizedSubjectName);

            setTokenKey(securityProperties, keyInfoUsage, token);
            return token;
        }

        throw new XMLSecurityException("stax.noKey", keyInfoUsage);
    }

    private static void setTokenKey(XMLSecurityProperties securityProperties, SecurityToken.KeyInfoUsage keyInfoUsage,
                                    AbstractInboundSecurityToken token) {
        Key key = null;
        if (keyInfoUsage == SecurityToken.KeyInfoUsage.SIGNATURE_VERIFICATION) {
            key = securityProperties.getSignatureVerificationKey();
        } else if (keyInfoUsage == SecurityToken.KeyInfoUsage.DECRYPTION) {
            key = securityProperties.getDecryptionKey();
        }
        if (key instanceof PublicKey) {
            token.setPublicKey((PublicKey) key);
        } else {
            token.setSecretKey("", key);
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
        InputStream in = new UnsynchronizedByteArrayInputStream(data);
        try {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) factory.generateCertificate(in);
        } catch (CertificateException e) {
            throw new XMLSecurityException(e);
        }
    }
}

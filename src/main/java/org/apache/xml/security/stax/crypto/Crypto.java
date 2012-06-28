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
package org.apache.xml.security.stax.crypto;

import org.apache.xml.security.stax.ext.XMLSecurityException;

import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public interface Crypto {

    //
    // Accessor methods
    //

    /**
     * Get the crypto provider associated with this implementation
     *
     * @return the crypto provider
     */
    public String getCryptoProvider();

    /**
     * Set the crypto provider associated with this implementation
     *
     * @param provider the crypto provider to set
     */
    public void setCryptoProvider(String provider);

    /**
     * Retrieves the identifier name of the default certificate. This should be the certificate
     * that is used for signature and encryption. This identifier corresponds to the certificate
     * that should be used whenever KeyInfo is not present in a signed or an encrypted
     * message. May return null. The identifier is implementation specific, e.g. it could be the
     * KeyStore alias.
     *
     * @return name of the default X509 certificate.
     */
    public String getDefaultX509Identifier() throws XMLSecurityException;

    /**
     * Sets the identifier name of the default certificate. This should be the certificate
     * that is used for signature and encryption. This identifier corresponds to the certificate
     * that should be used whenever KeyInfo is not present in a signed or an encrypted
     * message. The identifier is implementation specific, e.g. it could be the KeyStore alias.
     *
     * @param identifier name of the default X509 certificate.
     */
    public void setDefaultX509Identifier(String identifier);

    /**
     * Sets the CertificateFactory instance on this Crypto instance
     *
     * @param provider    the CertificateFactory provider name
     * @param certFactory the CertificateFactory the CertificateFactory instance to set
     */
    public void setCertificateFactory(String provider, CertificateFactory certFactory);

    /**
     * Get the CertificateFactory instance on this Crypto instance
     *
     * @return Returns a <code>CertificateFactory</code> to construct
     *         X509 certificates
     * @throws org.apache.ws.security.XMLSecurityException
     *
     */
    public CertificateFactory getCertificateFactory() throws XMLSecurityException;

    //
    // Base Crypto functionality methods
    //

    /**
     * Load a X509Certificate from the input stream.
     *
     * @param in The <code>InputStream</code> containing the X509 data
     * @return An X509 certificate
     * @throws XMLSecurityException
     */
    public X509Certificate loadCertificate(InputStream in) throws XMLSecurityException;

    /**
     * Reads the SubjectKeyIdentifier information from the certificate.
     * <p/>
     * If the the certificate does not contain a SKI extension then
     * try to compute the SKI according to RFC3280 using the
     * SHA-1 hash value of the public key. The second method described
     * in RFC3280 is not support. Also only RSA public keys are supported.
     * If we cannot compute the SKI throw a XMLSecurityException.
     *
     * @param cert The certificate to read SKI
     * @return The byte array containing the binary SKI data
     */
    public byte[] getSKIBytesFromCert(X509Certificate cert) throws XMLSecurityException;

    /**
     * Get a byte array given an array of X509 certificates.
     * <p/>
     *
     * @param certs The certificates to convert
     * @return The byte array for the certificates
     * @throws XMLSecurityException
     */
    public byte[] getBytesFromCertificates(X509Certificate[] certs) throws XMLSecurityException;

    /**
     * Construct an array of X509Certificate's from the byte array.
     *
     * @param data The <code>byte</code> array containing the X509 data
     * @return An array of X509 certificates
     * @throws XMLSecurityException
     */
    public X509Certificate[] getCertificatesFromBytes(byte[] data) throws XMLSecurityException;

    //
    // Implementation-specific Crypto functionality methods
    //

    /**
     * Get an X509Certificate (chain) corresponding to the CryptoType argument. The supported
     * types are as follows:
     * <p/>
     * TYPE.ISSUER_SERIAL - A certificate (chain) is located by the issuer name and serial number
     * TYPE.THUMBPRINT_SHA1 - A certificate (chain) is located by the SHA1 of the (root) cert
     * TYPE.SKI_BYTES - A certificate (chain) is located by the SKI bytes of the (root) cert
     * TYPE.SUBJECT_DN - A certificate (chain) is located by the Subject DN of the (root) cert
     * TYPE.ALIAS - A certificate (chain) is located by an alias. This alias is implementation
     * specific, for example - it could be a java KeyStore alias.
     */
    public X509Certificate[] getX509Certificates(CryptoType cryptoType) throws XMLSecurityException;

    /**
     * Get the implementation-specific identifier corresponding to the cert parameter, e.g. the
     * identifier could be a KeyStore alias.
     *
     * @param cert The X509Certificate for which to search for an identifier
     * @return the identifier corresponding to the cert parameter
     * @throws XMLSecurityException
     */
    public String getX509Identifier(X509Certificate cert) throws XMLSecurityException;

    /**
     * Gets the private key corresponding to the identifier.
     *
     * @param identifier The implementation-specific identifier corresponding to the key
     * @param password   The password needed to get the key
     * @return The private key
     */
    public PrivateKey getPrivateKey(
            String identifier, String password
    ) throws XMLSecurityException;

    /**
     * Evaluate whether a given certificate chain should be trusted.
     *
     * @param certs Certificate chain to validate
     * @return true if the certificate chain is valid, false otherwise
     * @throws XMLSecurityException
     */
    @Deprecated
    public boolean verifyTrust(X509Certificate[] certs) throws XMLSecurityException;

    /**
     * Evaluate whether a given certificate chain should be trusted.
     *
     * @param certs            Certificate chain to validate
     * @param enableRevocation whether to enable CRL verification or not
     * @return true if the certificate chain is valid, false otherwise
     * @throws XMLSecurityException
     */
    public boolean verifyTrust(
            X509Certificate[] certs, boolean enableRevocation
    ) throws XMLSecurityException;

    /**
     * Evaluate whether a given public key should be trusted.
     *
     * @param publicKey The PublicKey to be evaluated
     * @return whether the PublicKey parameter is trusted or not
     */
    public boolean verifyTrust(PublicKey publicKey) throws XMLSecurityException;

}

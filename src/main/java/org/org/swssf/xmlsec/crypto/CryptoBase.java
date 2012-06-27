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

package org.swssf.xmlsec.crypto;

import org.swssf.xmlsec.ext.XMLSecurityException;

import javax.security.auth.x500.X500Principal;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.security.MessageDigest;
import java.security.NoSuchProviderException;
import java.security.cert.*;
import java.util.*;

/**
 * This Abstract Base Class implements the accessor and keystore-independent methods and
 * functionality of the Crypto interface.
 */
public abstract class CryptoBase implements Crypto {
    public static final String SKI_OID = "2.5.29.14";
    /**
     * OID For the NameConstraints Extension to X.509
     * <p/>
     * http://java.sun.com/j2se/1.4.2/docs/api/
     * http://www.ietf.org/rfc/rfc3280.txt (s. 4.2.1.11)
     */
    public static final String NAME_CONSTRAINTS_OID = "2.5.29.30";

    private static final Constructor<?> BC_509CLASS_CONS;

    protected Map<String, CertificateFactory> certFactMap =
            new HashMap<String, CertificateFactory>();
    protected String defaultAlias = null;
    protected String cryptoProvider = null;

    static {
        Constructor<?> cons = null;
        try {
            Class<?> c = Class.forName("org.bouncycastle.asn1.x509.X509Name");
            cons = c.getConstructor(new Class[]{String.class});
        } catch (Exception e) {
            //ignore
        }
        BC_509CLASS_CONS = cons;
    }

    /**
     * Constructor
     */
    protected CryptoBase() {
    }

    /**
     * Get the crypto provider associated with this implementation
     *
     * @return the crypto provider
     */
    public String getCryptoProvider() {
        return cryptoProvider;
    }

    /**
     * Set the crypto provider associated with this implementation
     *
     * @param provider the crypto provider to set
     */
    public void setCryptoProvider(String provider) {
        cryptoProvider = provider;
    }

    /**
     * Retrieves the identifier name of the default certificate. This should be the certificate
     * that is used for signature and encryption. This identifier corresponds to the certificate
     * that should be used whenever KeyInfo is not present in a signed or an encrypted
     * message. May return null. The identifier is implementation specific, e.g. it could be the
     * KeyStore alias.
     *
     * @return name of the default X509 certificate.
     */
    public String getDefaultX509Identifier() throws XMLSecurityException {
        return defaultAlias;
    }

    /**
     * Sets the identifier name of the default certificate. This should be the certificate
     * that is used for signature and encryption. This identifier corresponds to the certificate
     * that should be used whenever KeyInfo is not present in a signed or an encrypted
     * message. The identifier is implementation specific, e.g. it could be the KeyStore alias.
     *
     * @param identifier name of the default X509 certificate.
     */
    public void setDefaultX509Identifier(String identifier) {
        defaultAlias = identifier;
    }

    /**
     * Sets the CertificateFactory instance on this Crypto instance
     *
     * @param provider    the CertificateFactory provider name
     * @param certFactory the CertificateFactory the CertificateFactory instance to set
     */
    public void setCertificateFactory(String provider, CertificateFactory certFactory) {
        if (provider == null || provider.length() == 0) {
            certFactMap.put(certFactory.getProvider().getName(), certFactory);
        } else {
            certFactMap.put(provider, certFactory);
        }
    }

    /**
     * Get the CertificateFactory instance on this Crypto instance
     *
     * @return Returns a <code>CertificateFactory</code> to construct
     *         X509 certificates
     * @throws XMLSecurityException
     */
    public CertificateFactory getCertificateFactory() throws XMLSecurityException {
        String provider = getCryptoProvider();

        //Try to find a CertificateFactory that generates certs that are fully
        //compatible with the certs in the KeyStore  (Sun -> Sun, BC -> BC, etc...)
        CertificateFactory factory = null;
        if (provider != null && provider.length() != 0) {
            factory = certFactMap.get(provider);
        } else {
            factory = certFactMap.get("DEFAULT");
        }
        if (factory == null) {
            try {
                if (provider == null || provider.length() == 0) {
                    factory = CertificateFactory.getInstance("X.509");
                    certFactMap.put("DEFAULT", factory);
                } else {
                    factory = CertificateFactory.getInstance("X.509", provider);
                    certFactMap.put(provider, factory);
                }
                certFactMap.put(factory.getProvider().getName(), factory);
            } catch (CertificateException e) {
                throw new XMLSecurityException(
                        XMLSecurityException.ErrorCode.SECURITY_TOKEN_UNAVAILABLE, "unsupportedCertType",
                        null, e
                );
            } catch (NoSuchProviderException e) {
                throw new XMLSecurityException(
                        XMLSecurityException.ErrorCode.SECURITY_TOKEN_UNAVAILABLE, "noSecProvider",
                        null, e
                );
            }
        }
        return factory;
    }

    /**
     * Load a X509Certificate from the input stream.
     *
     * @param in The <code>InputStream</code> containing the X509Certificate
     * @return An X509 certificate
     * @throws XMLSecurityException
     */
    public X509Certificate loadCertificate(InputStream in) throws XMLSecurityException {
        try {
            CertificateFactory certFactory = getCertificateFactory();
            return (X509Certificate) certFactory.generateCertificate(in);
        } catch (CertificateException e) {
            throw new XMLSecurityException(
                    XMLSecurityException.ErrorCode.SECURITY_TOKEN_UNAVAILABLE, "parseError",
                    null, e
            );
        }
    }

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
    public byte[] getSKIBytesFromCert(X509Certificate cert) throws XMLSecurityException {
        //
        // Gets the DER-encoded OCTET string for the extension value (extnValue)
        // identified by the passed-in oid String. The oid string is represented
        // by a set of positive whole numbers separated by periods.
        //
        byte[] derEncodedValue = cert.getExtensionValue(SKI_OID);

        if (cert.getVersion() < 3 || derEncodedValue == null) {
            X509SubjectPublicKeyInfo spki = new X509SubjectPublicKeyInfo(cert.getPublicKey());
            byte[] value = spki.getSubjectPublicKey();
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-1");
                return digest.digest(value);
            } catch (Exception ex) {
                throw new XMLSecurityException(
                        XMLSecurityException.ErrorCode.UNSUPPORTED_SECURITY_TOKEN, "noSKIHandling",
                        new Object[]{"No SKI certificate extension and no SHA1 message digest available"},
                        ex
                );
            }
        }

        //
        // Strip away first (four) bytes from the DerValue (tag and length of
        // ExtensionValue OCTET STRING and KeyIdentifier OCTET STRING)
        //
        DERDecoder extVal = new DERDecoder(derEncodedValue);
        extVal.expect(DERDecoder.TYPE_OCTET_STRING);  // ExtensionValue OCTET STRING
        extVal.getLength();
        extVal.expect(DERDecoder.TYPE_OCTET_STRING);  // KeyIdentifier OCTET STRING
        int keyIDLen = extVal.getLength();
        return extVal.getBytes(keyIDLen);
    }

    /**
     * Get a byte array given an array of X509 certificates.
     * <p/>
     *
     * @param certs The certificates to convert
     * @return The byte array for the certificates
     * @throws XMLSecurityException
     */
    public byte[] getBytesFromCertificates(X509Certificate[] certs)
            throws XMLSecurityException {
        try {
            CertPath path = getCertificateFactory().generateCertPath(Arrays.asList(certs));
            return path.getEncoded();
        } catch (CertificateEncodingException e) {
            throw new XMLSecurityException(
                    XMLSecurityException.ErrorCode.SECURITY_TOKEN_UNAVAILABLE, "encodeError",
                    null, e
            );
        } catch (CertificateException e) {
            throw new XMLSecurityException(
                    XMLSecurityException.ErrorCode.SECURITY_TOKEN_UNAVAILABLE, "parseError",
                    null, e
            );
        }
    }

    /**
     * Construct an array of X509Certificate's from the byte array.
     * <p/>
     *
     * @param data The <code>byte</code> array containing the X509 data
     * @return An array of X509 certificates
     * @throws XMLSecurityException
     */
    public X509Certificate[] getCertificatesFromBytes(byte[] data)
            throws XMLSecurityException {
        InputStream in = new ByteArrayInputStream(data);
        CertPath path = null;
        try {
            path = getCertificateFactory().generateCertPath(in);
        } catch (CertificateException e) {
            throw new XMLSecurityException(
                    XMLSecurityException.ErrorCode.SECURITY_TOKEN_UNAVAILABLE, "parseError",
                    null, e
            );
        }
        List<?> l = path.getCertificates();
        X509Certificate[] certs = new X509Certificate[l.size()];
        int i = 0;
        for (Iterator<?> iterator = l.iterator(); iterator.hasNext(); ) {
            certs[i++] = (X509Certificate) iterator.next();
        }
        return certs;
    }

    protected Object createBCX509Name(String s) {
        if (BC_509CLASS_CONS != null) {
            try {
                return BC_509CLASS_CONS.newInstance(s);
            } catch (Exception e) {
                //ignore
            }
        }
        return new X500Principal(s);
    }

}

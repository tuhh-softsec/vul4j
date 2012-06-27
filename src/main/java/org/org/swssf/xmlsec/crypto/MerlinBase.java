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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.util.*;

/**
 * A base Crypto implementation based on two Java KeyStore objects, one being the keystore, and one
 * being the truststore.
 */
public class MerlinBase extends CryptoBase {

    private static final org.apache.commons.logging.Log log =
            org.apache.commons.logging.LogFactory.getLog(MerlinBase.class);
    private static final boolean doDebug = log.isDebugEnabled();

    protected static CertificateFactory certFact;
    protected KeyStore keystore = null;
    protected KeyStore truststore = null;
    protected CertStore crlCertStore = null;
    protected boolean loadCACerts = false;

    public MerlinBase() {
        // Default constructor
    }

    /**
     * Load a KeyStore object as an InputStream, using the ClassLoader and location arguments
     */
    public static InputStream loadInputStream(ClassLoader loader, String location)
            throws XMLSecurityException, IOException {
        InputStream is = null;
        if (location != null) {
            java.net.URL url = Loader.getResource(loader, location);
            if (url != null) {
                is = url.openStream();
            }

            //
            // If we don't find it, then look on the file system.
            //
            if (is == null) {
                try {
                    is = new FileInputStream(location);
                } catch (Exception e) {
                    if (doDebug) {
                        log.debug(e.getMessage(), e);
                    }
                    throw new XMLSecurityException(
                            XMLSecurityException.ErrorCode.FAILURE, "proxyNotFound", new Object[]{location}, e
                    );
                }
            }
        }
        return is;
    }


    /**
     * Loads the keystore from an <code>InputStream </code>.
     * <p/>
     *
     * @param input <code>InputStream</code> to read from
     * @throws XMLSecurityException
     */
    public KeyStore load(InputStream input, String storepass, String provider, String type)
            throws XMLSecurityException {
        KeyStore ks = null;

        try {
            if (provider == null || provider.length() == 0) {
                ks = KeyStore.getInstance(type);
            } else {
                ks = KeyStore.getInstance(type, provider);
            }

            ks.load(input, (storepass == null || storepass.length() == 0)
                    ? new char[0] : storepass.toCharArray());
        } catch (IOException e) {
            if (doDebug) {
                log.debug(e.getMessage(), e);
            }
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILURE, "ioError00", e);
        } catch (GeneralSecurityException e) {
            if (doDebug) {
                log.debug(e.getMessage(), e);
            }
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILURE, "secError00", e);
        } catch (Exception e) {
            if (doDebug) {
                log.debug(e.getMessage(), e);
            }
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILURE, "error00", e);
        }
        return ks;
    }

    //
    // Accessor methods
    //

    /**
     * Gets the Keystore that was loaded
     *
     * @return the Keystore
     */
    public KeyStore getKeyStore() {
        return keystore;
    }

    /**
     * Set the Keystore on this Crypto instance
     *
     * @param keyStore the Keystore to set
     */
    public void setKeyStore(KeyStore keyStore) {
        keystore = keyStore;
    }

    /**
     * Gets the trust store that was loaded by the underlying implementation
     *
     * @return the trust store
     */
    public KeyStore getTrustStore() {
        return truststore;
    }

    /**
     * Set the trust store on this Crypto instance
     *
     * @param trustStore the trust store to set
     */
    public void setTrustStore(KeyStore trustStore) {
        truststore = trustStore;
    }

    /**
     * Set the CertStore from which to obtain a list of CRLs for Certificate Revocation
     * checking.
     *
     * @param crlCertStore the CertStore from which to obtain a list of CRLs for Certificate
     *                     Revocation checking.
     */
    public void setCRLCertStore(CertStore crlCertStore) {
        this.crlCertStore = crlCertStore;
    }

    /**
     * Get the CertStore from which to obtain a list of CRLs for Certificate Revocation
     * checking.
     *
     * @return the CertStore from which to obtain a list of CRLs for Certificate
     *         Revocation checking.
     */
    public CertStore getCRLCertStore() {
        return crlCertStore;
    }

    /**
     * Singleton certificate factory for this Crypto instance.
     * <p/>
     *
     * @return Returns a <code>CertificateFactory</code> to construct
     *         X509 certificates
     * @throws org.apache.ws.security.XMLSecurityException
     *
     */
    @Override
    public CertificateFactory getCertificateFactory() throws XMLSecurityException {
        String provider = getCryptoProvider();
        String keyStoreProvider = null;
        if (keystore != null) {
            keyStoreProvider = keystore.getProvider().getName();
        }

        //Try to find a CertificateFactory that generates certs that are fully
        //compatible with the certs in the KeyStore  (Sun -> Sun, BC -> BC, etc...)
        CertificateFactory factory = null;
        if (provider != null) {
            factory = certFactMap.get(provider);
        } else if (keyStoreProvider != null) {
            factory =
                    certFactMap.get(mapKeystoreProviderToCertProvider(keyStoreProvider));
            if (factory == null) {
                factory = certFactMap.get(keyStoreProvider);
            }
        } else {
            factory = certFactMap.get("DEFAULT");
        }
        if (factory == null) {
            try {
                if (provider == null || provider.length() == 0) {
                    if (keyStoreProvider != null && keyStoreProvider.length() != 0) {
                        try {
                            factory =
                                    CertificateFactory.getInstance(
                                            "X.509", mapKeystoreProviderToCertProvider(keyStoreProvider)
                                    );
                            certFactMap.put(keyStoreProvider, factory);
                            certFactMap.put(
                                    mapKeystoreProviderToCertProvider(keyStoreProvider), factory
                            );
                        } catch (Exception ex) {
                            log.debug(ex);
                            //Ignore, we'll just use the default since they didn't specify one.
                            //Hopefully that will work for them.
                        }
                    }
                    if (factory == null) {
                        factory = CertificateFactory.getInstance("X.509");
                        certFactMap.put("DEFAULT", factory);
                    }
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

    private String mapKeystoreProviderToCertProvider(String s) {
        if ("SunJSSE".equals(s)) {
            return "SUN";
        }
        return s;
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
    @Override
    public String getDefaultX509Identifier() throws XMLSecurityException {
        if (defaultAlias != null) {
            return defaultAlias;
        }

        if (keystore != null) {
            try {
                Enumeration<String> as = keystore.aliases();
                if (as.hasMoreElements()) {
                    String alias = as.nextElement();
                    if (!as.hasMoreElements()) {
                        defaultAlias = alias;
                        return alias;
                    }
                }
            } catch (KeyStoreException ex) {
                throw new XMLSecurityException(
                        XMLSecurityException.ErrorCode.FAILURE, "keystore", null, ex
                );
            }
        }
        return null;
    }

    //
    // Keystore-specific Crypto functionality methods
    //

    /**
     * Get an X509Certificate (chain) corresponding to the CryptoType argument. The supported
     * types are as follows:
     * <p/>
     * TYPE.ISSUER_SERIAL - A certificate (chain) is located by the issuer name and serial number
     * TYPE.THUMBPRINT_SHA1 - A certificate (chain) is located by the SHA1 of the (root) cert
     * TYPE.SKI_BYTES - A certificate (chain) is located by the SKI bytes of the (root) cert
     * TYPE.SUBJECT_DN - A certificate (chain) is located by the Subject DN of the (root) cert
     * TYPE.ALIAS - A certificate (chain) is located by an alias, which for this implementation
     * means an alias of the keystore or truststore.
     */
    public X509Certificate[] getX509Certificates(CryptoType cryptoType) throws XMLSecurityException {
        if (cryptoType == null) {
            return null;
        }
        CryptoType.TYPE type = cryptoType.getType();
        X509Certificate[] certs = null;
        switch (type) {
            case ISSUER_SERIAL: {
                certs = getX509Certificates(cryptoType.getIssuer(), cryptoType.getSerial());
                break;
            }
            case THUMBPRINT_SHA1: {
                certs = getX509Certificates(cryptoType.getBytes());
                break;
            }
            case SKI_BYTES: {
                certs = getX509CertificatesSKI(cryptoType.getBytes());
                break;
            }
            case SUBJECT_DN: {
                certs = getX509CertificatesSubjectDN(cryptoType.getSubjectDN());
                break;
            }
            case ALIAS: {
                certs = getX509Certificates(cryptoType.getAlias());
                break;
            }
        }
        return certs;
    }

    /**
     * Get the implementation-specific identifier corresponding to the cert parameter. In this
     * case, the identifier corresponds to a KeyStore alias.
     *
     * @param cert The X509Certificate for which to search for an identifier
     * @return the identifier corresponding to the cert parameter
     * @throws XMLSecurityException
     */
    public String getX509Identifier(X509Certificate cert) throws XMLSecurityException {
        String identifier = null;

        if (keystore != null) {
            identifier = getIdentifier(cert, keystore);
        }

        if (identifier == null && truststore != null) {
            identifier = getIdentifier(cert, truststore);
        }

        return identifier;
    }

    /**
     * Gets the private key corresponding to the identifier.
     *
     * @param identifier The implementation-specific identifier corresponding to the key
     * @param password   The password needed to get the key
     * @return The private key
     */
    public PrivateKey getPrivateKey(
            String identifier,
            String password
    ) throws XMLSecurityException {
        if (keystore == null) {
            throw new XMLSecurityException("The keystore is null");
        }
        try {
            if (identifier == null || !keystore.isKeyEntry(identifier)) {
                String msg = "Cannot find key for alias: [" + identifier + "]";
                String logMsg = createKeyStoreErrorMessage(keystore);
                log.error(msg + logMsg);
                throw new XMLSecurityException(msg);
            }
            /*
             * todo
            if (password == null && privatePasswordSet) {
                password = properties.getProperty(KEYSTORE_PRIVATE_PASSWORD);
                if (password != null) {
                    password = password.trim();
                }
            }
            */
            Key keyTmp = keystore.getKey(identifier, password == null
                    ? new char[]{} : password.toCharArray());
            if (!(keyTmp instanceof PrivateKey)) {
                String msg = "Key is not a private key, alias: [" + identifier + "]";
                String logMsg = createKeyStoreErrorMessage(keystore);
                log.error(msg + logMsg);
                throw new XMLSecurityException(msg);
            }
            return (PrivateKey) keyTmp;
        } catch (KeyStoreException ex) {
            throw new XMLSecurityException(
                    XMLSecurityException.ErrorCode.FAILURE, "noPrivateKey", new Object[]{ex.getMessage()}, ex
            );
        } catch (UnrecoverableKeyException ex) {
            throw new XMLSecurityException(
                    XMLSecurityException.ErrorCode.FAILURE, "noPrivateKey", new Object[]{ex.getMessage()}, ex
            );
        } catch (NoSuchAlgorithmException ex) {
            throw new XMLSecurityException(
                    XMLSecurityException.ErrorCode.FAILURE, "noPrivateKey", new Object[]{ex.getMessage()}, ex
            );
        }
    }

    /**
     * Evaluate whether a given certificate chain should be trusted.
     * Uses the CertPath API to validate a given certificate chain.
     *
     * @param certs Certificate chain to validate
     * @return true if the certificate chain is valid, false otherwise
     * @throws XMLSecurityException
     */
    @Deprecated
    public boolean verifyTrust(X509Certificate[] certs) throws XMLSecurityException {
        return verifyTrust(certs, false);
    }

    /**
     * Evaluate whether a given certificate chain should be trusted.
     * Uses the CertPath API to validate a given certificate chain.
     *
     * @param certs            Certificate chain to validate
     * @param enableRevocation whether to enable CRL verification or not
     * @return true if the certificate chain is valid, false otherwise
     * @throws XMLSecurityException
     */
    public boolean verifyTrust(
            X509Certificate[] certs,
            boolean enableRevocation
    ) throws XMLSecurityException {
        try {
            // Generate cert path
            List<X509Certificate> certList = Arrays.asList(certs);
            CertPath path = getCertificateFactory().generateCertPath(certList);

            Set<TrustAnchor> set = new HashSet<TrustAnchor>();
            if (truststore != null) {
                Enumeration<String> truststoreAliases = truststore.aliases();
                while (truststoreAliases.hasMoreElements()) {
                    String alias = truststoreAliases.nextElement();
                    X509Certificate cert =
                            (X509Certificate) truststore.getCertificate(alias);
                    if (cert != null) {
                        TrustAnchor anchor =
                                new TrustAnchor(cert, cert.getExtensionValue(NAME_CONSTRAINTS_OID));
                        set.add(anchor);
                    }
                }
            }

            //
            // Add certificates from the keystore - only if there is no TrustStore, apart from
            // the case that the truststore is the JDK CA certs. This behaviour is preserved
            // for backwards compatibility reasons
            //
            if (keystore != null && (truststore == null || loadCACerts)) {
                Enumeration<String> aliases = keystore.aliases();
                while (aliases.hasMoreElements()) {
                    String alias = aliases.nextElement();
                    X509Certificate cert =
                            (X509Certificate) keystore.getCertificate(alias);
                    if (cert != null) {
                        TrustAnchor anchor =
                                new TrustAnchor(cert, cert.getExtensionValue(NAME_CONSTRAINTS_OID));
                        set.add(anchor);
                    }
                }
            }

            PKIXParameters param = new PKIXParameters(set);
            param.setRevocationEnabled(enableRevocation);
            if (enableRevocation && crlCertStore != null) {
                param.addCertStore(crlCertStore);
            }

            // Verify the trust path using the above settings
            String provider = getCryptoProvider();
            CertPathValidator validator = null;
            if (provider == null || provider.length() == 0) {
                validator = CertPathValidator.getInstance("PKIX");
            } else {
                validator = CertPathValidator.getInstance("PKIX", provider);
            }
            validator.validate(path, param);
            return true;
        } catch (java.security.NoSuchProviderException e) {
            throw new XMLSecurityException(
                    XMLSecurityException.ErrorCode.FAILURE, "certpath",
                    new Object[]{e.getMessage()}, e
            );
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new XMLSecurityException(
                    XMLSecurityException.ErrorCode.FAILURE,
                    "certpath", new Object[]{e.getMessage()},
                    e
            );
        } catch (java.security.cert.CertificateException e) {
            throw new XMLSecurityException(
                    XMLSecurityException.ErrorCode.FAILURE, "certpath",
                    new Object[]{e.getMessage()}, e
            );
        } catch (java.security.InvalidAlgorithmParameterException e) {
            throw new XMLSecurityException(
                    XMLSecurityException.ErrorCode.FAILURE, "certpath",
                    new Object[]{e.getMessage()}, e
            );
        } catch (java.security.cert.CertPathValidatorException e) {
            throw new XMLSecurityException(
                    XMLSecurityException.ErrorCode.FAILURE, "certpath",
                    new Object[]{e.getMessage()}, e
            );
        } catch (java.security.KeyStoreException e) {
            throw new XMLSecurityException(
                    XMLSecurityException.ErrorCode.FAILURE, "certpath",
                    new Object[]{e.getMessage()}, e
            );
        } catch (NullPointerException e) {
            // NPE thrown by JDK 1.7 for one of the test cases
            throw new XMLSecurityException(
                    XMLSecurityException.ErrorCode.FAILURE, "certpath",
                    new Object[]{e.getMessage()}, e
            );
        }
    }

    /**
     * Evaluate whether a given public key should be trusted.
     *
     * @param publicKey The PublicKey to be evaluated
     * @return whether the PublicKey parameter is trusted or not
     */
    public boolean verifyTrust(PublicKey publicKey) throws XMLSecurityException {
        //
        // If the public key is null, do not trust the signature
        //
        if (publicKey == null) {
            return false;
        }

        //
        // Search the keystore for the transmitted public key (direct trust)
        //
        boolean trust = findPublicKeyInKeyStore(publicKey, keystore);
        if (trust) {
            return true;
        } else {
            //
            // Now search the truststore for the transmitted public key (direct trust)
            //
            trust = findPublicKeyInKeyStore(publicKey, truststore);
            if (trust) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get an X509 Certificate (chain) according to a given serial number and issuer string.
     *
     * @param issuer       The Issuer String
     * @param serialNumber The serial number of the certificate
     * @return an X509 Certificate (chain) corresponding to the found certificate(s)
     * @throws XMLSecurityException
     */
    private X509Certificate[] getX509Certificates(
            String issuer,
            BigInteger serialNumber
    ) throws XMLSecurityException {
        //
        // Convert the subject DN to a java X500Principal object first. This is to ensure
        // interop with a DN constructed from .NET, where e.g. it uses "S" instead of "ST".
        // Then convert it to a BouncyCastle X509Name, which will order the attributes of
        // the DN in a particular way (see WSS-168). If the conversion to an X500Principal
        // object fails (e.g. if the DN contains "E" instead of "EMAILADDRESS"), then fall
        // back on a direct conversion to a BC X509Name
        //
        Object issuerName = null;
        try {
            X500Principal issuerRDN = new X500Principal(issuer);
            issuerName = createBCX509Name(issuerRDN.getName());
        } catch (java.lang.IllegalArgumentException ex) {
            issuerName = createBCX509Name(issuer);
        }
        Certificate[] certs = null;
        if (keystore != null) {
            certs = getCertificates(issuerName, serialNumber, keystore);
        }

        //If we can't find the issuer in the keystore then look at the truststore
        if ((certs == null || certs.length == 0) && truststore != null) {
            certs = getCertificates(issuerName, serialNumber, truststore);
        }

        if ((certs == null || certs.length == 0)) {
            return null;
        }

        X509Certificate[] x509certs = new X509Certificate[certs.length];
        for (int i = 0; i < certs.length; i++) {
            x509certs[i] = (X509Certificate) certs[i];
        }
        return x509certs;
    }

    /**
     * Get an X509 Certificate (chain) of the X500Principal argument in the supplied KeyStore
     *
     * @param subjectRDN either an X500Principal or a BouncyCastle X509Name instance.
     * @param store      The KeyStore
     * @return an X509 Certificate (chain)
     * @throws XMLSecurityException
     */
    private Certificate[] getCertificates(
            Object issuerRDN,
            BigInteger serialNumber,
            KeyStore store
    ) throws XMLSecurityException {
        try {
            for (Enumeration<String> e = store.aliases(); e.hasMoreElements(); ) {
                String alias = e.nextElement();
                Certificate cert = null;
                Certificate[] certs = store.getCertificateChain(alias);
                if (certs == null || certs.length == 0) {
                    // no cert chain, so lets check if getCertificate gives us a result.
                    cert = store.getCertificate(alias);
                    if (cert == null) {
                        continue;
                    }
                    certs = new Certificate[]{cert};
                } else {
                    cert = certs[0];
                }
                if (cert instanceof X509Certificate) {
                    X509Certificate x509cert = (X509Certificate) cert;
                    if (x509cert.getSerialNumber().compareTo(serialNumber) == 0) {
                        Object certName =
                                createBCX509Name(x509cert.getIssuerX500Principal().getName());
                        if (certName.equals(issuerRDN)) {
                            return certs;
                        }
                    }
                }
            }
        } catch (KeyStoreException e) {
            throw new XMLSecurityException(
                    XMLSecurityException.ErrorCode.FAILURE, "keystore", null, e
            );
        }
        return new Certificate[]{};
    }

    /**
     * Get an X509 Certificate (chain) according to a given Thumbprint.
     *
     * @param thumbprint The SHA1 thumbprint info bytes
     * @return the X509 Certificate (chain) that was found (can be null)
     * @throws XMLSecurityException if problems during keystore handling or wrong certificate
     */
    private X509Certificate[] getX509Certificates(byte[] thumbprint) throws XMLSecurityException {
        MessageDigest sha = null;

        try {
            sha = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            throw new XMLSecurityException(
                    XMLSecurityException.ErrorCode.FAILURE, "noSHA1availabe", null, e
            );
        }
        Certificate[] certs = null;
        if (keystore != null) {
            certs = getCertificates(thumbprint, keystore, sha);
        }

        //If we can't find the issuer in the keystore then look at the truststore
        if ((certs == null || certs.length == 0) && truststore != null) {
            certs = getCertificates(thumbprint, truststore, sha);
        }

        if ((certs == null || certs.length == 0)) {
            return null;
        }

        X509Certificate[] x509certs = new X509Certificate[certs.length];
        for (int i = 0; i < certs.length; i++) {
            x509certs[i] = (X509Certificate) certs[i];
        }
        return x509certs;
    }

    /**
     * Get an X509 Certificate (chain) of the X500Principal argument in the supplied KeyStore
     *
     * @param subjectRDN either an X500Principal or a BouncyCastle X509Name instance.
     * @param store      The KeyStore
     * @return an X509 Certificate (chain)
     * @throws XMLSecurityException
     */
    private Certificate[] getCertificates(
            byte[] thumbprint,
            KeyStore store,
            MessageDigest sha
    ) throws XMLSecurityException {
        try {
            for (Enumeration<String> e = store.aliases(); e.hasMoreElements(); ) {
                String alias = e.nextElement();
                Certificate cert = null;
                Certificate[] certs = store.getCertificateChain(alias);
                if (certs == null || certs.length == 0) {
                    // no cert chain, so lets check if getCertificate gives us a result.
                    cert = store.getCertificate(alias);
                    if (cert == null) {
                        continue;
                    }
                    certs = new Certificate[]{cert};
                } else {
                    cert = certs[0];
                }
                if (cert instanceof X509Certificate) {
                    X509Certificate x509cert = (X509Certificate) cert;
                    try {
                        sha.update(x509cert.getEncoded());
                    } catch (CertificateEncodingException ex) {
                        throw new XMLSecurityException(
                                XMLSecurityException.ErrorCode.SECURITY_TOKEN_UNAVAILABLE, "encodeError",
                                null, ex
                        );
                    }
                    byte[] data = sha.digest();

                    if (Arrays.equals(data, thumbprint)) {
                        return certs;
                    }
                }
            }
        } catch (KeyStoreException e) {
            throw new XMLSecurityException(
                    XMLSecurityException.ErrorCode.FAILURE, "keystore", null, e
            );
        }
        return new Certificate[]{};
    }

    /**
     * Get an X509 Certificate (chain) according to a given SubjectKeyIdentifier.
     *
     * @param skiBytes The SKI bytes
     * @return the X509 certificate (chain) that was found (can be null)
     */
    private X509Certificate[] getX509CertificatesSKI(byte[] skiBytes) throws XMLSecurityException {
        Certificate[] certs = null;
        if (keystore != null) {
            certs = getCertificates(skiBytes, keystore);
        }

        //If we can't find the issuer in the keystore then look at the truststore
        if ((certs == null || certs.length == 0) && truststore != null) {
            certs = getCertificates(skiBytes, truststore);
        }

        if ((certs == null || certs.length == 0)) {
            return null;
        }

        X509Certificate[] x509certs = new X509Certificate[certs.length];
        for (int i = 0; i < certs.length; i++) {
            x509certs[i] = (X509Certificate) certs[i];
        }
        return x509certs;
    }

    /**
     * Get an X509 Certificate (chain) of the X500Principal argument in the supplied KeyStore
     *
     * @param subjectRDN either an X500Principal or a BouncyCastle X509Name instance.
     * @param store      The KeyStore
     * @return an X509 Certificate (chain)
     * @throws XMLSecurityException
     */
    private Certificate[] getCertificates(
            byte[] skiBytes,
            KeyStore store
    ) throws XMLSecurityException {
        try {
            for (Enumeration<String> e = store.aliases(); e.hasMoreElements(); ) {
                String alias = e.nextElement();
                Certificate cert = null;
                Certificate[] certs = store.getCertificateChain(alias);
                if (certs == null || certs.length == 0) {
                    // no cert chain, so lets check if getCertificate gives us a result.
                    cert = store.getCertificate(alias);
                    if (cert == null) {
                        continue;
                    }
                    certs = new Certificate[]{cert};
                } else {
                    cert = certs[0];
                }
                if (cert instanceof X509Certificate) {
                    X509Certificate x509cert = (X509Certificate) cert;
                    byte[] data = getSKIBytesFromCert(x509cert);
                    if (data.length == skiBytes.length && Arrays.equals(data, skiBytes)) {
                        return certs;
                    }
                }
            }
        } catch (KeyStoreException e) {
            throw new XMLSecurityException(
                    XMLSecurityException.ErrorCode.FAILURE, "keystore", null, e
            );
        }
        return new Certificate[]{};
    }

    /**
     * Get an X509 Certificate (chain) according to a given DN of the subject of the certificate
     *
     * @param subjectDN The DN of subject to look for
     * @return An X509 Certificate (chain) with the same DN as given in the parameters
     * @throws XMLSecurityException
     */
    private X509Certificate[] getX509CertificatesSubjectDN(String subjectDN) throws XMLSecurityException {
        //
        // Convert the subject DN to a java X500Principal object first. This is to ensure
        // interop with a DN constructed from .NET, where e.g. it uses "S" instead of "ST".
        // Then convert it to a BouncyCastle X509Name, which will order the attributes of
        // the DN in a particular way (see WSS-168). If the conversion to an X500Principal
        // object fails (e.g. if the DN contains "E" instead of "EMAILADDRESS"), then fall
        // back on a direct conversion to a BC X509Name
        //
        Object subject;
        try {
            X500Principal subjectRDN = new X500Principal(subjectDN);
            subject = createBCX509Name(subjectRDN.getName());
        } catch (java.lang.IllegalArgumentException ex) {
            subject = createBCX509Name(subjectDN);
        }

        Certificate[] certs = null;
        if (keystore != null) {
            certs = getCertificates(subject, keystore);
        }

        //If we can't find the issuer in the keystore then look at the truststore
        if ((certs == null || certs.length == 0) && truststore != null) {
            certs = getCertificates(subject, truststore);
        }

        if ((certs == null || certs.length == 0)) {
            return null;
        }

        X509Certificate[] x509certs = new X509Certificate[certs.length];
        for (int i = 0; i < certs.length; i++) {
            x509certs[i] = (X509Certificate) certs[i];
        }
        return x509certs;
    }

    /**
     * Get an X509 Certificate (chain) that correspond to the identifier. For this implementation,
     * the identifier corresponds to the KeyStore alias.
     *
     * @param identifier The identifier that corresponds to the returned certs
     * @return an X509 Certificate (chain) that corresponds to the identifier
     */
    private X509Certificate[] getX509Certificates(String identifier) throws XMLSecurityException {
        Certificate[] certs = null;
        try {
            if (keystore != null) {
                // There's a chance that there can only be a set of trust stores
                certs = keystore.getCertificateChain(identifier);
                if (certs == null || certs.length == 0) {
                    // no cert chain, so lets check if getCertificate gives us a result.
                    Certificate cert = keystore.getCertificate(identifier);
                    if (cert != null) {
                        certs = new Certificate[]{cert};
                    }
                }
            }

            if (certs == null && truststore != null) {
                // Now look into the trust stores
                certs = truststore.getCertificateChain(identifier);
                if (certs == null) {
                    Certificate cert = truststore.getCertificate(identifier);
                    if (cert != null) {
                        certs = new Certificate[]{cert};
                    }
                }
            }

            if (certs == null) {
                return null;
            }
        } catch (KeyStoreException e) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILURE, "keystore", null, e);
        }

        X509Certificate[] x509certs = new X509Certificate[certs.length];
        for (int i = 0; i < certs.length; i++) {
            x509certs[i] = (X509Certificate) certs[i];
        }
        return x509certs;
    }

    /**
     * Find the Public Key in a keystore.
     */
    private boolean findPublicKeyInKeyStore(PublicKey publicKey, KeyStore keyStoreToSearch) {
        if (keyStoreToSearch == null) {
            return false;
        }
        try {
            for (Enumeration<String> e = keyStoreToSearch.aliases(); e.hasMoreElements(); ) {
                String alias = e.nextElement();
                Certificate[] certs = keyStoreToSearch.getCertificateChain(alias);
                Certificate cert;
                if (certs == null || certs.length == 0) {
                    // no cert chain, so lets check if getCertificate gives us a result.
                    cert = keyStoreToSearch.getCertificate(alias);
                    if (cert == null) {
                        continue;
                    }
                } else {
                    cert = certs[0];
                }
                if (!(cert instanceof X509Certificate)) {
                    continue;
                }
                X509Certificate x509cert = (X509Certificate) cert;
                if (publicKey.equals(x509cert.getPublicKey())) {
                    return true;
                }
            }
        } catch (KeyStoreException e) {
            return false;
        }
        return false;
    }

    /**
     * Get an X509 Certificate (chain) of the X500Principal argument in the supplied KeyStore
     *
     * @param subjectRDN either an X500Principal or a BouncyCastle X509Name instance.
     * @param store      The KeyStore
     * @return an X509 Certificate (chain)
     * @throws XMLSecurityException
     */
    private Certificate[] getCertificates(Object subjectRDN, KeyStore store)
            throws XMLSecurityException {
        try {
            for (Enumeration<String> e = store.aliases(); e.hasMoreElements(); ) {
                String alias = e.nextElement();
                Certificate cert = null;
                Certificate[] certs = store.getCertificateChain(alias);
                if (certs == null || certs.length == 0) {
                    // no cert chain, so lets check if getCertificate gives us a result.
                    cert = store.getCertificate(alias);
                    if (cert == null) {
                        continue;
                    }
                    certs = new Certificate[]{cert};
                } else {
                    cert = certs[0];
                }
                if (cert instanceof X509Certificate) {
                    X500Principal foundRDN = ((X509Certificate) cert).getSubjectX500Principal();
                    Object certName = createBCX509Name(foundRDN.getName());

                    if (subjectRDN.equals(certName)) {
                        return certs;
                    }
                }
            }
        } catch (KeyStoreException e) {
            throw new XMLSecurityException(
                    XMLSecurityException.ErrorCode.FAILURE, "keystore", null, e
            );
        }
        return new Certificate[]{};
    }

    private static String createKeyStoreErrorMessage(KeyStore keystore) throws KeyStoreException {
        Enumeration<String> aliases = keystore.aliases();
        StringBuilder sb = new StringBuilder(keystore.size() * 7);
        boolean firstAlias = true;
        while (aliases.hasMoreElements()) {
            if (!firstAlias) {
                sb.append(", ");
            }
            sb.append(aliases.nextElement());
            firstAlias = false;
        }
        String msg = " in keystore of type [" + keystore.getType()
                + "] from provider [" + keystore.getProvider()
                + "] with size [" + keystore.size() + "] and aliases: {"
                + sb.toString() + "}";
        return msg;
    }

    /**
     * Get an implementation-specific identifier that corresponds to the X509Certificate. In
     * this case, the identifier is the KeyStore alias.
     *
     * @param cert  The X509Certificate corresponding to the returned identifier
     * @param store The KeyStore to search
     * @return An implementation-specific identifier that corresponds to the X509Certificate
     */
    private String getIdentifier(X509Certificate cert, KeyStore store)
            throws XMLSecurityException {
        try {
            for (Enumeration<String> e = store.aliases(); e.hasMoreElements(); ) {
                String alias = e.nextElement();

                Certificate[] certs = store.getCertificateChain(alias);
                Certificate retrievedCert = null;
                if (certs == null || certs.length == 0) {
                    // no cert chain, so lets check if getCertificate gives us a  result.
                    retrievedCert = store.getCertificate(alias);
                    if (retrievedCert == null) {
                        continue;
                    }
                } else {
                    retrievedCert = certs[0];
                }
                if (!(retrievedCert instanceof X509Certificate)) {
                    continue;
                }
                if (retrievedCert != null && retrievedCert.equals(cert)) {
                    return alias;
                }
            }
        } catch (KeyStoreException e) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILURE, "keystore", null, e);
        }
        return null;
    }

}

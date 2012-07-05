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

import org.apache.xml.security.stax.crypto.Crypto;
import org.apache.xml.security.stax.crypto.MerlinBase;

import javax.security.auth.callback.CallbackHandler;
import java.net.URL;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Main configuration class to supply keys etc.
 * This class is subject to change in the future.
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class XMLSecurityProperties {

    private final List<InputProcessor> inputProcessorList = new ArrayList<InputProcessor>();

    /**
     * Add an additional, non standard, InputProcessor to the chain
     *
     * @param inputProcessor The InputProcessor to add
     */
    public void addInputProcessor(InputProcessor inputProcessor) {
        this.inputProcessorList.add(inputProcessor);
    }

    /**
     * Returns the currently registered additional InputProcessors
     *
     * @return the List with the InputProcessors
     */
    public List<InputProcessor> getInputProcessorList() {
        return inputProcessorList;
    }

    private Class<? extends MerlinBase> decryptionCryptoClass;
    private KeyStore decryptionKeyStore;
    private CallbackHandler callbackHandler;

    /**
     * Returns the decryption keystore
     *
     * @return A keystore for decryption operation
     */
    public KeyStore getDecryptionKeyStore() {
        return decryptionKeyStore;
    }

    /**
     * loads a java keystore from the given url for decrypt operations
     *
     * @param url              The URL to the keystore
     * @param keyStorePassword The keyStorePassword
     * @throws Exception thrown if something goes wrong while loading the keystore
     */
    public void loadDecryptionKeystore(URL url, char[] keyStorePassword) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(url.openStream(), keyStorePassword);
        this.decryptionKeyStore = keyStore;
    }

    /**
     * Returns the decryption crypto class
     *
     * @return
     */
    public Class<? extends MerlinBase> getDecryptionCryptoClass() {
        if (decryptionCryptoClass != null) {
            return decryptionCryptoClass;
        }
        decryptionCryptoClass = org.apache.xml.security.stax.crypto.Merlin.class;
        return decryptionCryptoClass;
    }

    /**
     * Sets a custom decryption class
     *
     * @param decryptionCryptoClass
     */
    public void setDecryptionCryptoClass(Class<? extends MerlinBase> decryptionCryptoClass) {
        this.decryptionCryptoClass = decryptionCryptoClass;
    }

    private Crypto cachedDecryptionCrypto;
    private KeyStore cachedDecryptionKeyStore;

    /**
     * returns the decryptionCrypto for the key-management
     *
     * @return A Crypto instance
     * @throws XMLSecurityException thrown if something goes wrong
     */
    public Crypto getDecryptionCrypto() throws XMLSecurityException {

        if (this.getDecryptionKeyStore() == null) {
            throw new XMLSecurityConfigurationException(XMLSecurityException.ErrorCode.FAILURE, "decryptionKeyStoreNotSet");
        }

        if (this.getDecryptionKeyStore() == cachedDecryptionKeyStore) {
            return cachedDecryptionCrypto;
        }

        Class<? extends MerlinBase> decryptionCryptoClass = this.getDecryptionCryptoClass();

        try {
            MerlinBase decryptionCrypto = decryptionCryptoClass.newInstance();
            decryptionCrypto.setKeyStore(this.getDecryptionKeyStore());
            cachedDecryptionCrypto = decryptionCrypto;
            cachedDecryptionKeyStore = this.getDecryptionKeyStore();
            return decryptionCrypto;
        } catch (Exception e) {
            throw new XMLSecurityConfigurationException(XMLSecurityException.ErrorCode.FAILURE, "decryptionCryptoFailure", e);
        }
    }

    /**
     * returns the password callback handler
     *
     * @return
     */
    public CallbackHandler getCallbackHandler() {
        return callbackHandler;
    }

    /**
     * sets the password callback handler
     *
     * @param callbackHandler
     */
    public void setCallbackHandler(CallbackHandler callbackHandler) {
        this.callbackHandler = callbackHandler;
    }

    private XMLSecurityConstants.Action[] outAction;

    private Class<? extends MerlinBase> encryptionCryptoClass;
    private KeyStore encryptionKeyStore;
    private String encryptionUser;
    private X509Certificate encryptionUseThisCertificate;
    private String encryptionSymAlgorithm;
    private String encryptionCompressionAlgorithm;
    private String encryptionKeyTransportAlgorithm;
    private final List<SecurePart> encryptionParts = new LinkedList<SecurePart>();

    /**
     * Returns the encryption keystore
     *
     * @return A keystore for encryption operation
     */
    public KeyStore getEncryptionKeyStore() {
        return encryptionKeyStore;
    }

    /**
     * loads a java keystore from the given url for encrypt operations
     *
     * @param url              The URL to the keystore
     * @param keyStorePassword The keyStorePassword
     * @throws Exception thrown if something goes wrong while loading the keystore
     */
    public void loadEncryptionKeystore(URL url, char[] keyStorePassword) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(url.openStream(), keyStorePassword);
        this.encryptionKeyStore = keyStore;
    }

    /**
     * Returns the encryption crypto class
     *
     * @return
     */
    public Class<? extends MerlinBase> getEncryptionCryptoClass() {
        if (encryptionCryptoClass != null) {
            return encryptionCryptoClass;
        }
        encryptionCryptoClass = org.apache.xml.security.stax.crypto.Merlin.class;
        return encryptionCryptoClass;
    }

    /**
     * Sets a custom encryption class
     *
     * @param encryptionCryptoClass
     */
    public void setEncryptionCryptoClass(Class<? extends MerlinBase> encryptionCryptoClass) {
        this.encryptionCryptoClass = encryptionCryptoClass;
    }

    private Crypto cachedEncryptionCrypto;
    private KeyStore cachedEncryptionKeyStore;

    /**
     * returns the encryptionCrypto for the key-management
     *
     * @return A Crypto instance
     * @throws XMLSecurityException thrown if something goes wrong
     */
    public Crypto getEncryptionCrypto() throws XMLSecurityException {

        if (this.getEncryptionKeyStore() == null) {
            throw new XMLSecurityConfigurationException(XMLSecurityException.ErrorCode.FAILURE, "encryptionKeyStoreNotSet");
        }

        if (this.getEncryptionKeyStore() == cachedEncryptionKeyStore) {
            return cachedEncryptionCrypto;
        }

        Class<? extends MerlinBase> encryptionCryptoClass = this.getEncryptionCryptoClass();

        try {
            MerlinBase encryptionCrypto = encryptionCryptoClass.newInstance();
            encryptionCrypto.setKeyStore(this.getEncryptionKeyStore());
            cachedEncryptionCrypto = encryptionCrypto;
            cachedEncryptionKeyStore = this.getEncryptionKeyStore();
            return encryptionCrypto;
        } catch (Exception e) {
            throw new XMLSecurityConfigurationException(XMLSecurityException.ErrorCode.FAILURE, "encryptionCryptoFailure", e);
        }
    }

    /**
     * Adds a part which must be encrypted by the framework
     *
     * @param securePart
     */
    public void addEncryptionPart(SecurePart securePart) {
        encryptionParts.add(securePart);
    }

    /**
     * Returns the encryption parts which are actually set
     *
     * @return A List of SecurePart's
     */
    public List<SecurePart> getEncryptionSecureParts() {
        return encryptionParts;
    }

    /**
     * Returns the Encryption-Algo
     *
     * @return the Encryption-Algo as String
     */
    public String getEncryptionSymAlgorithm() {
        return encryptionSymAlgorithm;
    }

    /**
     * Specifies the encryption algorithm
     *
     * @param encryptionSymAlgorithm The algo to use for encryption
     */
    public void setEncryptionSymAlgorithm(String encryptionSymAlgorithm) {
        this.encryptionSymAlgorithm = encryptionSymAlgorithm;
    }

    /**
     * Returns the encryption key transport algorithm
     *
     * @return the key transport algorithm as string
     */
    public String getEncryptionKeyTransportAlgorithm() {
        return encryptionKeyTransportAlgorithm;
    }

    /**
     * Specifies the encryption key transport algorithm
     *
     * @param encryptionKeyTransportAlgorithm
     *         the encryption key transport algorithm as string
     */
    public void setEncryptionKeyTransportAlgorithm(String encryptionKeyTransportAlgorithm) {
        this.encryptionKeyTransportAlgorithm = encryptionKeyTransportAlgorithm;
    }

    public X509Certificate getEncryptionUseThisCertificate() {
        return encryptionUseThisCertificate;
    }

    public void setEncryptionUseThisCertificate(X509Certificate encryptionUseThisCertificate) {
        this.encryptionUseThisCertificate = encryptionUseThisCertificate;
    }

    /**
     * Returns the alias for the encryption key in the keystore
     *
     * @return the alias for the encryption key in the keystore as string
     */
    public String getEncryptionUser() {
        return encryptionUser;
    }

    /**
     * Specifies the the alias for the encryption key in the keystore
     *
     * @param encryptionUser the the alias for the encryption key in the keystore as string
     */
    public void setEncryptionUser(String encryptionUser) {
        this.encryptionUser = encryptionUser;
    }

    public String getEncryptionCompressionAlgorithm() {
        return encryptionCompressionAlgorithm;
    }

    public void setEncryptionCompressionAlgorithm(String encryptionCompressionAlgorithm) {
        this.encryptionCompressionAlgorithm = encryptionCompressionAlgorithm;
    }

    private final List<SecurePart> signatureParts = new LinkedList<SecurePart>();
    private String signatureAlgorithm;
    private String signatureDigestAlgorithm;
    private String signatureCanonicalizationAlgorithm;
    private boolean useSingleCert = true;
    private Key signatureKey;
    private X509Certificate[] signatureCerts;

    public X509Certificate[] getSignatureCerts() {
        return signatureCerts;
    }

    public void setSignatureCerts(X509Certificate[] signatureCerts) {
        this.signatureCerts = signatureCerts;
    }


    public void addSignaturePart(SecurePart securePart) {
        signatureParts.add(securePart);
    }

    public List<SecurePart> getSignatureSecureParts() {
        return signatureParts;
    }

    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public void setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }

    public String getSignatureDigestAlgorithm() {
        return signatureDigestAlgorithm;
    }

    public void setSignatureDigestAlgorithm(String signatureDigestAlgorithm) {
        this.signatureDigestAlgorithm = signatureDigestAlgorithm;
    }

    public void setSignatureKey(Key signatureKey) {
        this.signatureKey = signatureKey;
    }
    
    public Key getSignatureKey() {
        return signatureKey;
    }

    public boolean isUseSingleCert() {
        return useSingleCert;
    }

    public void setUseSingleCert(boolean useSingleCert) {
        this.useSingleCert = useSingleCert;
    }

    /**
     * Returns the actual set actions
     *
     * @return The Actions in applied order
     */
    public XMLSecurityConstants.Action[] getOutAction() {
        return outAction;
    }

    /**
     * Specifies how to secure the document eg. Timestamp, Signature, Encrypt
     *
     * @param outAction
     */
    public void setOutAction(XMLSecurityConstants.Action[] outAction) {
        this.outAction = outAction;
    }

    public String getSignatureCanonicalizationAlgorithm() {
        return signatureCanonicalizationAlgorithm;
    }

    public void setSignatureCanonicalizationAlgorithm(String signatureCanonicalizationAlgorithm) {
        this.signatureCanonicalizationAlgorithm = signatureCanonicalizationAlgorithm;
    }

    private Key signatureVerificationKey;
    
    public Key getSignatureVerificationKey() {
        return signatureVerificationKey;
    }

    public void setSignatureVerificationKey(Key signatureVerificationKey) {
        this.signatureVerificationKey = signatureVerificationKey;
    }

    private boolean skipDocumentEvents = false;

    /**
     * Returns if the framework is skipping document-events
     *
     * @return true if document-events will be skipped, false otherwise
     */
    public boolean isSkipDocumentEvents() {
        return skipDocumentEvents;
    }

    /**
     * specifies if the framework should forward Document-Events or not
     *
     * @param skipDocumentEvents set to true when document events should be discarded, false otherwise
     */
    public void setSkipDocumentEvents(boolean skipDocumentEvents) {
        this.skipDocumentEvents = skipDocumentEvents;
    }

    private boolean disableSchemaValidation = false;

    public boolean isDisableSchemaValidation() {
        return disableSchemaValidation;
    }

    public void setDisableSchemaValidation(boolean disableSchemaValidation) {
        this.disableSchemaValidation = disableSchemaValidation;
    }

}

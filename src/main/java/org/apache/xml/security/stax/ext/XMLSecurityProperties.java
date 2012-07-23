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

import java.security.Key;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.security.auth.callback.CallbackHandler;

/**
 * Main configuration class to supply keys etc.
 * This class is subject to change in the future.
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class XMLSecurityProperties {

    private XMLSecurityConstants.KeyIdentifierType signatureKeyIdentifierType;

    public XMLSecurityConstants.KeyIdentifierType getSignatureKeyIdentifierType() {
        return signatureKeyIdentifierType;
    }

    public void setSignatureKeyIdentifierType(XMLSecurityConstants.KeyIdentifierType signatureKeyIdentifierType) {
        this.signatureKeyIdentifierType = signatureKeyIdentifierType;
    }
    
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

    private CallbackHandler callbackHandler;

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

    private X509Certificate encryptionUseThisCertificate;
    private String encryptionSymAlgorithm;
    private String encryptionCompressionAlgorithm;
    private String encryptionKeyTransportAlgorithm;
    private final List<SecurePart> encryptionParts = new LinkedList<SecurePart>();
    private Key encryptionKey;
    private Key encryptionTransportKey;
    private Key decryptionKey;
    
    public void setDecryptionKey(Key decryptionKey) {
        this.decryptionKey = decryptionKey;
    }
    
    public Key getDecryptionKey() {
        return decryptionKey;
    }
    
    public void setEncryptionTransportKey(Key encryptionTransportKey) {
        this.encryptionTransportKey = encryptionTransportKey;
    }
    
    public Key getEncryptionTransportKey() {
        return encryptionTransportKey;
    }

    public void setEncryptionKey(Key encryptionKey) {
        this.encryptionKey = encryptionKey;
    }
    
    public Key getEncryptionKey() {
        return encryptionKey;
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

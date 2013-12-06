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

import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;

import java.security.Key;
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
    private boolean skipDocumentEvents = false;
    private boolean disableSchemaValidation = false;

    private List<XMLSecurityConstants.Action> actions = new ArrayList<XMLSecurityConstants.Action>();

    private X509Certificate encryptionUseThisCertificate;
    private String encryptionSymAlgorithm;
    private String encryptionKeyTransportAlgorithm;
    private String encryptionKeyTransportDigestAlgorithm;
    private String encryptionKeyTransportMGFAlgorithm;
    private byte[] encryptionKeyTransportOAEPParams;
    private final List<SecurePart> encryptionParts = new LinkedList<SecurePart>();
    private Key encryptionKey;
    private Key encryptionTransportKey;

    private Key decryptionKey;

    private final List<SecurePart> signatureParts = new LinkedList<SecurePart>();
    private String signatureAlgorithm;
    private String signatureDigestAlgorithm;
    private String signatureCanonicalizationAlgorithm;
    private Key signatureKey;
    private X509Certificate[] signatureCerts;
    private boolean addExcC14NInclusivePrefixes = false;
    private SecurityTokenConstants.KeyIdentifier signatureKeyIdentifier;
    private boolean useSingleCert = true;

    private Key signatureVerificationKey;

    public XMLSecurityProperties() {
    }

    protected XMLSecurityProperties(XMLSecurityProperties xmlSecurityProperties) {
        this.inputProcessorList.addAll(xmlSecurityProperties.inputProcessorList);
        this.skipDocumentEvents = xmlSecurityProperties.skipDocumentEvents;
        this.disableSchemaValidation = xmlSecurityProperties.disableSchemaValidation;
        this.actions = xmlSecurityProperties.actions;
        this.encryptionUseThisCertificate = xmlSecurityProperties.encryptionUseThisCertificate;
        this.encryptionSymAlgorithm = xmlSecurityProperties.encryptionSymAlgorithm;
        this.encryptionKeyTransportAlgorithm = xmlSecurityProperties.encryptionKeyTransportAlgorithm;
        this.encryptionKeyTransportDigestAlgorithm = xmlSecurityProperties.encryptionKeyTransportDigestAlgorithm;
        this.encryptionKeyTransportMGFAlgorithm = xmlSecurityProperties.encryptionKeyTransportMGFAlgorithm;
        this.encryptionKeyTransportOAEPParams = xmlSecurityProperties.encryptionKeyTransportOAEPParams;
        this.encryptionParts.addAll(xmlSecurityProperties.encryptionParts);
        this.encryptionKey = xmlSecurityProperties.encryptionKey;
        this.encryptionTransportKey = xmlSecurityProperties.encryptionTransportKey;
        this.decryptionKey = xmlSecurityProperties.decryptionKey;
        this.signatureParts.addAll(xmlSecurityProperties.signatureParts);
        this.signatureAlgorithm = xmlSecurityProperties.signatureAlgorithm;
        this.signatureDigestAlgorithm = xmlSecurityProperties.signatureDigestAlgorithm;
        this.signatureCanonicalizationAlgorithm = xmlSecurityProperties.signatureCanonicalizationAlgorithm;
        this.signatureKey = xmlSecurityProperties.signatureKey;
        this.signatureCerts = xmlSecurityProperties.signatureCerts;
        this.addExcC14NInclusivePrefixes = xmlSecurityProperties.addExcC14NInclusivePrefixes;
        this.signatureKeyIdentifier = xmlSecurityProperties.signatureKeyIdentifier;
        this.useSingleCert = xmlSecurityProperties.useSingleCert;
        this.signatureVerificationKey = xmlSecurityProperties.signatureVerificationKey;
    }

    public SecurityTokenConstants.KeyIdentifier getSignatureKeyIdentifier() {
        return signatureKeyIdentifier;
    }

    public void setSignatureKeyIdentifier(SecurityTokenConstants.KeyIdentifier signatureKeyIdentifier) {
        this.signatureKeyIdentifier = signatureKeyIdentifier;
    }

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

    public String getEncryptionKeyTransportDigestAlgorithm() {
        return encryptionKeyTransportDigestAlgorithm;
    }

    public void setEncryptionKeyTransportDigestAlgorithm(String encryptionKeyTransportDigestAlgorithm) {
        this.encryptionKeyTransportDigestAlgorithm = encryptionKeyTransportDigestAlgorithm;
    }

    public String getEncryptionKeyTransportMGFAlgorithm() {
        return encryptionKeyTransportMGFAlgorithm;
    }

    public void setEncryptionKeyTransportMGFAlgorithm(String encryptionKeyTransportMGFAlgorithm) {
        this.encryptionKeyTransportMGFAlgorithm = encryptionKeyTransportMGFAlgorithm;
    }

    public byte[] getEncryptionKeyTransportOAEPParams() {
        return encryptionKeyTransportOAEPParams;
    }

    public void setEncryptionKeyTransportOAEPParams(byte[] encryptionKeyTransportOAEPParams) {
        this.encryptionKeyTransportOAEPParams = encryptionKeyTransportOAEPParams;
    }

    public X509Certificate getEncryptionUseThisCertificate() {
        return encryptionUseThisCertificate;
    }

    public void setEncryptionUseThisCertificate(X509Certificate encryptionUseThisCertificate) {
        this.encryptionUseThisCertificate = encryptionUseThisCertificate;
    }

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

    public boolean isAddExcC14NInclusivePrefixes() {
        return addExcC14NInclusivePrefixes;
    }

    public void setAddExcC14NInclusivePrefixes(boolean addExcC14NInclusivePrefixes) {
        this.addExcC14NInclusivePrefixes = addExcC14NInclusivePrefixes;
    }

    /**
     * Returns the actual set actions
     *
     * @return The Actions in applied order
     */
    public List<XMLSecurityConstants.Action> getActions() {
        return actions;
    }

    /**
     * Specifies how to secure the document eg. Timestamp, Signature, Encrypt
     *
     * @param actions
     */
    public void setActions(List<XMLSecurityConstants.Action> actions) {
        this.actions = actions;
    }
    
    public void addAction(XMLSecurityConstants.Action action) {
        if (actions == null) {
            actions = new ArrayList<XMLSecurityConstants.Action>();
        }
        actions.add(action);
    }

    public String getSignatureCanonicalizationAlgorithm() {
        return signatureCanonicalizationAlgorithm;
    }

    public void setSignatureCanonicalizationAlgorithm(String signatureCanonicalizationAlgorithm) {
        this.signatureCanonicalizationAlgorithm = signatureCanonicalizationAlgorithm;
    }

    public Key getSignatureVerificationKey() {
        return signatureVerificationKey;
    }

    public void setSignatureVerificationKey(Key signatureVerificationKey) {
        this.signatureVerificationKey = signatureVerificationKey;
    }

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

    public boolean isDisableSchemaValidation() {
        return disableSchemaValidation;
    }

    public void setDisableSchemaValidation(boolean disableSchemaValidation) {
        this.disableSchemaValidation = disableSchemaValidation;
    }
}

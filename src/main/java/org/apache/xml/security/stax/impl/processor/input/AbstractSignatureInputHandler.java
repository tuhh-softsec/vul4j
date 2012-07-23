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
package org.apache.xml.security.stax.impl.processor.input;

import org.apache.xml.security.binding.excc14n.InclusiveNamespaces;
import org.apache.xml.security.binding.xmldsig.CanonicalizationMethodType;
import org.apache.xml.security.binding.xmldsig.KeyInfoType;
import org.apache.xml.security.binding.xmldsig.SignatureType;
import org.apache.xml.security.stax.ext.*;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.impl.algorithms.SignatureAlgorithm;
import org.apache.xml.security.stax.impl.algorithms.SignatureAlgorithmFactory;
import org.apache.xml.security.stax.impl.util.SignerOutputStream;

import javax.xml.bind.JAXBElement;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public abstract class AbstractSignatureInputHandler extends AbstractInputSecurityHeaderHandler {

    @Override
    public void handle(final InputProcessorChain inputProcessorChain, final XMLSecurityProperties securityProperties,
                       Deque<XMLSecEvent> eventQueue, Integer index) throws XMLSecurityException {

        @SuppressWarnings("unchecked")
        final SignatureType signatureType = ((JAXBElement<SignatureType>) parseStructure(eventQueue, index, securityProperties)).getValue();
        SecurityToken securityToken = verifySignedInfo(inputProcessorChain, securityProperties, signatureType, eventQueue, index);
        addSignatureReferenceInputProcessorToChain(inputProcessorChain, securityProperties, signatureType, securityToken);
    }

    protected abstract void addSignatureReferenceInputProcessorToChain(
            InputProcessorChain inputProcessorChain, XMLSecurityProperties securityProperties,
            SignatureType signatureType, SecurityToken securityToken) throws XMLSecurityException;

    protected SecurityToken verifySignedInfo(InputProcessorChain inputProcessorChain, XMLSecurityProperties securityProperties,
                                             SignatureType signatureType, Deque<XMLSecEvent> eventDeque, int index)
            throws XMLSecurityException {
        //todo reparse SignedInfo when custom canonicalization method is used
        //verify SignedInfo
        SignatureVerifier signatureVerifier = newSignatureVerifier(inputProcessorChain, securityProperties, signatureType);

        Iterator<XMLSecEvent> iterator = eventDeque.descendingIterator();
        //skip to <Signature> Element
        int i = 0;
        while (i < index) {
            iterator.next();
            i++;
        }

        try {
            loop:
            while (iterator.hasNext()) {
                XMLSecEvent xmlSecEvent = iterator.next();
                switch (xmlSecEvent.getEventType()) {
                    case XMLStreamConstants.START_ELEMENT:
                        if (xmlSecEvent.asStartElement().getName().equals(XMLSecurityConstants.TAG_dsig_SignedInfo)) {
                            signatureVerifier.processEvent(xmlSecEvent);
                            break loop;
                        }
                        break;
                }
            }
            loop:
            while (iterator.hasNext()) {
                XMLSecEvent xmlSecEvent = iterator.next();
                signatureVerifier.processEvent(xmlSecEvent);
                switch (xmlSecEvent.getEventType()) {
                    case XMLStreamConstants.END_ELEMENT:
                        if (xmlSecEvent.asEndElement().getName().equals(XMLSecurityConstants.TAG_dsig_SignedInfo)) {
                            break loop;
                        }
                        break;
                }
            }
        } catch (XMLStreamException e) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_CHECK, e);
        }
        signatureVerifier.doFinal();
        return signatureVerifier.getSecurityToken();
    }

    protected abstract SignatureVerifier newSignatureVerifier(InputProcessorChain inputProcessorChain,
                                                              XMLSecurityProperties securityProperties,
                                                              final SignatureType signatureType) throws XMLSecurityException;

/*
    <ds:Signature xmlns:ds="http://www.w3.org/2000/09/xmldsig#" Id="Signature-1022834285">
        <ds:SignedInfo>
            <ds:CanonicalizationMethod Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#" />
            <ds:SignatureMethod Algorithm="http://www.w3.org/2000/09/xmldsig#rsa-sha1" />
            <ds:Reference URI="#id-1612925417">
                <ds:Transforms>
                    <ds:Transform Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#" />
                </ds:Transforms>
                <ds:DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1" />
                <ds:DigestValue>cy/khx5N6UobCJ1EbX+qnrGID2U=</ds:DigestValue>
            </ds:Reference>
            <ds:Reference URI="#Timestamp-1106985890">
                <ds:Transforms>
                    <ds:Transform Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#" />
                </ds:Transforms>
                <ds:DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1" />
                <ds:DigestValue>+p5YRII6uvUdsJ7XLKkWx1CBewE=</ds:DigestValue>
            </ds:Reference>
        </ds:SignedInfo>
        <ds:SignatureValue>
            Izg1FlI9oa4gOon2vTXi7V0EpiyCUazECVGYflbXq7/3GF8ThKGDMpush/fo1I2NVjEFTfmT2WP/
            +ZG5N2jASFptrcGbsqmuLE5JbxUP1TVKb9SigKYcOQJJ8klzmVfPXnSiRZmIU+DUT2UXopWnGNFL
            TwY0Uxja4ZuI6U8m8Tg=
        </ds:SignatureValue>
        <ds:KeyInfo Id="KeyId-1043455692">
            <wsse:SecurityTokenReference xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"
            xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" wsu:Id="STRId-1008354042">
                <wsse:Reference xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"
                URI="#CertId-3458500" ValueType="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3" />
            </wsse:SecurityTokenReference>
        </ds:KeyInfo>
    </ds:Signature>
     */

    public abstract class SignatureVerifier {

        private final SignatureType signatureType;
        private final SecurityToken securityToken;

        private SignerOutputStream signerOutputStream;
        private OutputStream bufferedSignerOutputStream;
        private Transformer transformer;

        public SignatureVerifier(SignatureType signatureType, SecurityContext securityContext,
                                 XMLSecurityProperties securityProperties) throws XMLSecurityException {
            this.signatureType = signatureType;

            KeyInfoType keyInfoType = signatureType.getKeyInfo();
            SecurityToken securityToken = 
                retrieveSecurityToken(keyInfoType, securityProperties, securityContext);
            securityToken.verify();

            handleSecurityToken(securityToken);
            try {
                createSignatureAlgorithm(securityToken, signatureType);
            } catch (InvalidKeyException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_CHECK, e);
            } catch (NoSuchAlgorithmException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_CHECK, e);
            } catch (NoSuchProviderException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_CHECK, e);
            } catch (CertificateException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_CHECK, e);
            }
            this.securityToken = securityToken;
        }
        
        protected abstract SecurityToken retrieveSecurityToken(KeyInfoType keyInfoType,
                                                 XMLSecurityProperties securityProperties,
                                                 SecurityContext securityContext) throws XMLSecurityException;

        public SecurityToken getSecurityToken() {
            return securityToken;
        }

        protected void handleSecurityToken(SecurityToken securityToken) throws XMLSecurityException {
        }

        protected void createSignatureAlgorithm(SecurityToken securityToken, SignatureType signatureType)
                throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException,
                CertificateException, XMLSecurityException {

            Key verifyKey;
            final String algorithmURI = signatureType.getSignedInfo().getSignatureMethod().getAlgorithm();
            if (securityToken.isAsymmetric()) {
                verifyKey = securityToken.getPublicKey(algorithmURI, XMLSecurityConstants.Asym_Sig);
            } else {
                verifyKey = securityToken.getSecretKey(
                        algorithmURI, XMLSecurityConstants.Sym_Sig);
            }

            SignatureAlgorithm signatureAlgorithm =
                    SignatureAlgorithmFactory.getInstance().getSignatureAlgorithm(
                            algorithmURI);
            signatureAlgorithm.engineInitVerify(verifyKey);
            signerOutputStream = new SignerOutputStream(signatureAlgorithm);
            bufferedSignerOutputStream = new BufferedOutputStream(signerOutputStream);

            try {
                final CanonicalizationMethodType canonicalizationMethodType =
                        signatureType.getSignedInfo().getCanonicalizationMethod();
                InclusiveNamespaces inclusiveNamespacesType =
                        XMLSecurityUtils.getQNameType(
                                canonicalizationMethodType.getContent(),
                                XMLSecurityConstants.TAG_c14nExcl_InclusiveNamespaces
                        );
                List<String> inclusiveNamespaces = inclusiveNamespacesType != null
                        ? inclusiveNamespacesType.getPrefixList()
                        : null;
                transformer = XMLSecurityUtils.getTransformer(
                        inclusiveNamespaces,
                        this.bufferedSignerOutputStream,
                        canonicalizationMethodType.getAlgorithm(),
                        XMLSecurityConstants.DIRECTION.IN);
            } catch (NoSuchMethodException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_CHECK, e);
            } catch (InstantiationException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_CHECK, e);
            } catch (IllegalAccessException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_CHECK, e);
            } catch (InvocationTargetException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_CHECK, e);
            }
        }

        protected void processEvent(XMLSecEvent xmlSecEvent) throws XMLStreamException {
            transformer.transform(xmlSecEvent);
        }

        protected void doFinal() throws XMLSecurityException {
            try {
                transformer.doFinal();
                bufferedSignerOutputStream.close();
            } catch (IOException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_CHECK, e);
            } catch (XMLStreamException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_CHECK, e);
            }
            if (!signerOutputStream.verify(signatureType.getSignatureValue().getValue())) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_CHECK);
            }
        }
    }
}

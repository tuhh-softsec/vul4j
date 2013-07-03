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
import org.apache.xml.security.binding.xmldsig.SignatureType;
import org.apache.xml.security.binding.xmldsig.SignedInfoType;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.*;
import org.apache.xml.security.stax.securityToken.InboundSecurityToken;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecEventFactory;
import org.apache.xml.security.stax.impl.algorithms.SignatureAlgorithm;
import org.apache.xml.security.stax.impl.algorithms.SignatureAlgorithmFactory;
import org.apache.xml.security.stax.impl.util.*;

import javax.xml.bind.JAXBElement;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayDeque;
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
        if (signatureType.getSignedInfo() == null) {
            throw new XMLSecurityException("stax.signature.signedInfoMissing");
        }
        if (signatureType.getSignedInfo().getSignatureMethod() == null) {
            throw new XMLSecurityException("stax.signature.signatureMethodMissing");
        }
        if (signatureType.getSignedInfo().getCanonicalizationMethod() == null) {
            throw new XMLSecurityException("stax.signature.canonicalizationMethodMissing");
        }
        if (signatureType.getSignatureValue() == null) {
            throw new XMLSecurityException("stax.signature.signatureValueMissing");
        }
        if (signatureType.getId() == null) {
            signatureType.setId(IDGenerator.generateID(null));
        }
        InboundSecurityToken inboundSecurityToken = verifySignedInfo(inputProcessorChain, securityProperties, signatureType, eventQueue, index);
        addSignatureReferenceInputProcessorToChain(inputProcessorChain, securityProperties, signatureType, inboundSecurityToken);
    }

    protected abstract void addSignatureReferenceInputProcessorToChain(
            InputProcessorChain inputProcessorChain, XMLSecurityProperties securityProperties,
            SignatureType signatureType, InboundSecurityToken inboundSecurityToken) throws XMLSecurityException;

    protected InboundSecurityToken verifySignedInfo(InputProcessorChain inputProcessorChain, XMLSecurityProperties securityProperties,
                                             SignatureType signatureType, Deque<XMLSecEvent> eventDeque, int index)
            throws XMLSecurityException {

        Iterator<XMLSecEvent> iterator;

        String c14NMethod = signatureType.getSignedInfo().getCanonicalizationMethod().getAlgorithm();
        if (XMLSecurityConstants.NS_C14N_OMIT_COMMENTS.equals(c14NMethod) ||
                XMLSecurityConstants.NS_C14N_WITH_COMMENTS.equals(c14NMethod) ||
                XMLSecurityConstants.NS_C14N_EXCL_OMIT_COMMENTS.equals(c14NMethod) ||
                XMLSecurityConstants.NS_C14N_EXCL_WITH_COMMENTS.equals(c14NMethod) ||
                XMLSecurityConstants.NS_C14N11_OMIT_COMMENTS.equals(c14NMethod) ||
                XMLSecurityConstants.NS_C14N11_WITH_COMMENTS.equals(c14NMethod)) {

            iterator = eventDeque.descendingIterator();
            //forward to <Signature> Element
            int i = 0;
            while (i < index) {
                iterator.next();
                i++;
            }

        } else {
            iterator = reparseSignedInfo(inputProcessorChain, securityProperties, signatureType, eventDeque, index).descendingIterator();
            index = 0;
        }

        SignatureVerifier signatureVerifier = newSignatureVerifier(inputProcessorChain, securityProperties, signatureType);

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
            throw new XMLSecurityException(e);
        }
        signatureVerifier.doFinal();
        return signatureVerifier.getInboundSecurityToken();
    }

    protected Deque<XMLSecEvent> reparseSignedInfo(InputProcessorChain inputProcessorChain, XMLSecurityProperties securityProperties,
                                                   SignatureType signatureType, Deque<XMLSecEvent> eventDeque, int index
    ) throws XMLSecurityException {

        Deque<XMLSecEvent> signedInfoDeque = new ArrayDeque<XMLSecEvent>();

        UnsynchronizedByteArrayOutputStream unsynchronizedByteArrayOutputStream = new UnsynchronizedByteArrayOutputStream();
        Transformer transformer = XMLSecurityUtils.getTransformer(
                null,
                unsynchronizedByteArrayOutputStream,
                signatureType.getSignedInfo().getCanonicalizationMethod().getAlgorithm(),
                XMLSecurityConstants.DIRECTION.IN);

        Iterator<XMLSecEvent> iterator = eventDeque.descendingIterator();
        //forward to <Signature> Element
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
                            transformer.transform(xmlSecEvent);
                            break loop;
                        }
                        break;
                }
            }

            loop:
            while (iterator.hasNext()) {
                XMLSecEvent xmlSecEvent = iterator.next();
                transformer.transform(xmlSecEvent);
                switch (xmlSecEvent.getEventType()) {
                    case XMLStreamConstants.END_ELEMENT:
                        if (xmlSecEvent.asEndElement().getName().equals(XMLSecurityConstants.TAG_dsig_SignedInfo)) {
                            break loop;
                        }
                        break;
                }
            }

            transformer.doFinal();

            XMLStreamReader xmlStreamReader = inputProcessorChain.getSecurityContext().
                    <XMLInputFactory>get(XMLSecurityConstants.XMLINPUTFACTORY).
                    createXMLStreamReader(new UnsynchronizedByteArrayInputStream(unsynchronizedByteArrayOutputStream.toByteArray()));

            while (xmlStreamReader.hasNext()) {
                XMLSecEvent xmlSecEvent = XMLSecEventFactory.allocate(xmlStreamReader, null);
                signedInfoDeque.push(xmlSecEvent);
                xmlStreamReader.next();
            }

            @SuppressWarnings("unchecked")
            final SignedInfoType signedInfoType =
                    ((JAXBElement<SignedInfoType>) parseStructure(signedInfoDeque, 0, securityProperties)).getValue();
            signatureType.setSignedInfo(signedInfoType);

            return signedInfoDeque;

        } catch (XMLStreamException e) {
            throw new XMLSecurityException(e);
        }
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
        private final InboundSecurityToken inboundSecurityToken;

        private SignerOutputStream signerOutputStream;
        private OutputStream bufferedSignerOutputStream;
        private Transformer transformer;

        public SignatureVerifier(SignatureType signatureType, InboundSecurityContext inboundSecurityContext,
                                 XMLSecurityProperties securityProperties) throws XMLSecurityException {
            this.signatureType = signatureType;

            InboundSecurityToken inboundSecurityToken =
                retrieveSecurityToken(signatureType, securityProperties, inboundSecurityContext);
            this.inboundSecurityToken = inboundSecurityToken;

            createSignatureAlgorithm(inboundSecurityToken, signatureType);
        }
        
        protected abstract InboundSecurityToken retrieveSecurityToken(SignatureType signatureType,
                                                 XMLSecurityProperties securityProperties,
                                                 InboundSecurityContext inboundSecurityContext) throws XMLSecurityException;

        public InboundSecurityToken getInboundSecurityToken() {
            return inboundSecurityToken;
        }

        protected void createSignatureAlgorithm(InboundSecurityToken inboundSecurityToken, SignatureType signatureType)
                throws XMLSecurityException {

            Key verifyKey;
            final String algorithmURI = signatureType.getSignedInfo().getSignatureMethod().getAlgorithm();
            if (inboundSecurityToken.isAsymmetric()) {
                verifyKey = inboundSecurityToken.getPublicKey(algorithmURI, XMLSecurityConstants.Asym_Sig, signatureType.getId());
            } else {
                verifyKey = inboundSecurityToken.getSecretKey(
                        algorithmURI, XMLSecurityConstants.Sym_Sig, signatureType.getId());
                verifyKey = XMLSecurityUtils.prepareSecretKey(algorithmURI, verifyKey.getEncoded());
            }
            
            try {
                SignatureAlgorithm signatureAlgorithm =
                        SignatureAlgorithmFactory.getInstance().getSignatureAlgorithm(
                                algorithmURI);
                signatureAlgorithm.engineInitVerify(verifyKey);
                signerOutputStream = new SignerOutputStream(signatureAlgorithm);
                bufferedSignerOutputStream = new UnsynchronizedBufferedOutputStream(signerOutputStream);

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
            } catch (NoSuchAlgorithmException e) {
                throw new XMLSecurityException(e);
            } catch (NoSuchProviderException e) {
                throw new XMLSecurityException(e);
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
                throw new XMLSecurityException(e);
            } catch (XMLStreamException e) {
                throw new XMLSecurityException(e);
            }
            if (!signerOutputStream.verify(signatureType.getSignatureValue().getValue())) {
                throw new XMLSecurityException("errorMessages.InvalidSignatureValueException");
            }
        }
    }
}

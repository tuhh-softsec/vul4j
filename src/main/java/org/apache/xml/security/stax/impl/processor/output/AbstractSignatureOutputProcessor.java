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
package org.apache.xml.security.stax.impl.processor.output;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.config.JCEAlgorithmMapper;
import org.apache.xml.security.stax.config.ResourceResolverMapper;
import org.apache.xml.security.stax.ext.*;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.impl.SignaturePartDef;
import org.apache.xml.security.stax.impl.transformer.TransformIdentity;
import org.apache.xml.security.stax.impl.util.DigestOutputStream;
import org.apache.xml.security.stax.impl.util.UnsynchronizedBufferedOutputStream;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.*;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public abstract class AbstractSignatureOutputProcessor extends AbstractOutputProcessor {

    private static final transient Logger logger = LoggerFactory.getLogger(AbstractSignatureOutputProcessor.class);

    private final List<SignaturePartDef> signaturePartDefList = new ArrayList<SignaturePartDef>();
    private InternalSignatureOutputProcessor activeInternalSignatureOutputProcessor = null;

    public AbstractSignatureOutputProcessor() throws XMLSecurityException {
        super();
    }

    public List<SignaturePartDef> getSignaturePartDefList() {
        return signaturePartDefList;
    }

    @Override
    public abstract void processEvent(XMLSecEvent xmlSecEvent, OutputProcessorChain outputProcessorChain)
            throws XMLStreamException, XMLSecurityException;

    @Override
    public void doFinal(OutputProcessorChain outputProcessorChain) throws XMLStreamException, XMLSecurityException {
        doFinalInternal(outputProcessorChain);
        super.doFinal(outputProcessorChain);
    }

    protected void doFinalInternal(OutputProcessorChain outputProcessorChain) throws XMLSecurityException, XMLStreamException {
        List<SignaturePartDef> signaturePartDefs = getSignaturePartDefList();

        Map<Object, SecurePart> dynamicSecureParts = outputProcessorChain.getSecurityContext().getAsMap(XMLSecurityConstants.SIGNATURE_PARTS);
        Iterator<Map.Entry<Object, SecurePart>> securePartsMapIterator = dynamicSecureParts.entrySet().iterator();
        while (securePartsMapIterator.hasNext()) {
            Map.Entry<Object, SecurePart> securePartEntry = securePartsMapIterator.next();
            final SecurePart securePart = securePartEntry.getValue();
            final String externalReference = securePart.getExternalReference();
            if (externalReference != null) {
                ResourceResolver resourceResolver = ResourceResolverMapper.getResourceResolver(externalReference, outputProcessorChain.getDocumentContext().getBaseURI());

                DigestOutputStream digestOutputStream = createMessageDigestOutputStream(securePart.getDigestMethod());
                InputStream inputStream = resourceResolver.getInputStreamFromExternalReference();

                SignaturePartDef signaturePartDef = new SignaturePartDef();
                signaturePartDef.setSecurePart(securePart);
                signaturePartDef.setSigRefId(externalReference);
                signaturePartDef.setExternalResource(true);
                signaturePartDef.setTransforms(securePart.getTransforms());
                String digestMethod = securePart.getDigestMethod();
                if (digestMethod == null) {
                    digestMethod = getSecurityProperties().getSignatureDigestAlgorithm();
                }
                signaturePartDef.setDigestAlgo(digestMethod);
                signaturePartDefs.add(signaturePartDef);

                try {
                    if (securePart.getTransforms() != null) {
                        signaturePartDef.setExcludeVisibleC14Nprefixes(true);
                        Transformer transformer = buildTransformerChain(digestOutputStream, signaturePartDef, null);
                        transformer.transform(inputStream);
                        transformer.doFinal();
                    } else {
                        XMLSecurityUtils.copy(inputStream, digestOutputStream);
                    }
                    digestOutputStream.close();
                } catch (IOException e) {
                    throw new XMLSecurityException(e);
                }

                String calculatedDigest = new String(Base64.encodeBase64(digestOutputStream.getDigestValue()));
                if (logger.isDebugEnabled()) {
                    logger.debug("Calculated Digest: " + calculatedDigest);
                }

                signaturePartDef.setDigestValue(calculatedDigest);
            }
        }

        verifySignatureParts(outputProcessorChain);
    }

    protected void verifySignatureParts(OutputProcessorChain outputProcessorChain) throws XMLSecurityException {
        List<SignaturePartDef> signaturePartDefs = getSignaturePartDefList();
        Map<Object, SecurePart> dynamicSecureParts = outputProcessorChain.getSecurityContext().getAsMap(XMLSecurityConstants.SIGNATURE_PARTS);
        Iterator<Map.Entry<Object, SecurePart>> securePartsMapIterator = dynamicSecureParts.entrySet().iterator();
        loop:
        while (securePartsMapIterator.hasNext()) {
            Map.Entry<Object, SecurePart> securePartEntry = securePartsMapIterator.next();
            final SecurePart securePart = securePartEntry.getValue();

            if (securePart.isRequired()) {
                for (int i = 0; i < signaturePartDefs.size(); i++) {
                    SignaturePartDef signaturePartDef = signaturePartDefs.get(i);
    
                    if (signaturePartDef.getSecurePart() == securePart) {
                        continue loop;
                    }
                }
                throw new XMLSecurityException("stax.signature.securePartNotFound", securePart.getName());
            }
        }
    }

    protected InternalSignatureOutputProcessor getActiveInternalSignatureOutputProcessor() {
        return activeInternalSignatureOutputProcessor;
    }

    protected void setActiveInternalSignatureOutputProcessor(
            InternalSignatureOutputProcessor activeInternalSignatureOutputProcessor) {
        this.activeInternalSignatureOutputProcessor = activeInternalSignatureOutputProcessor;
    }

    private DigestOutputStream createMessageDigestOutputStream(String digestAlgorithm)
            throws XMLSecurityException {

        String jceName = JCEAlgorithmMapper.translateURItoJCEID(digestAlgorithm);
        String jceProvider = JCEAlgorithmMapper.getJCEProviderFromURI(digestAlgorithm);
        if (jceName == null) {
            throw new XMLSecurityException("algorithms.NoSuchMap", digestAlgorithm);
        }
        MessageDigest messageDigest;
        try {
            if (jceProvider != null) {
                messageDigest = MessageDigest.getInstance(jceName, jceProvider);
            } else {
                messageDigest = MessageDigest.getInstance(jceName);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new XMLSecurityException(e);
        } catch (NoSuchProviderException e) {
            throw new XMLSecurityException(e);
        }
        return new DigestOutputStream(messageDigest);
    }

    protected Transformer buildTransformerChain(OutputStream outputStream,
                                                SignaturePartDef signaturePartDef,
                                                XMLSecStartElement xmlSecStartElement)
            throws XMLSecurityException {

        String[] transforms = signaturePartDef.getTransforms();

        if (transforms == null || transforms.length == 0) {
            Transformer transformer = new TransformIdentity();
            transformer.setOutputStream(outputStream);
            return transformer;
        }

        Transformer parentTransformer = null;
        for (int i = transforms.length - 1; i >= 0; i--) {
            String transform = transforms[i];

            List<String> inclusiveNamespacePrefixes = null;
            if (getSecurityProperties().isAddExcC14NInclusivePrefixes() &&
                    XMLSecurityConstants.NS_C14N_EXCL.equals(transform)) {

                Set<String> prefixSet = XMLSecurityUtils.getExcC14NInclusiveNamespacePrefixes(
                        xmlSecStartElement, signaturePartDef.isExcludeVisibleC14Nprefixes()
                );
                StringBuilder prefixes = new StringBuilder();
                for (Iterator<String> iterator = prefixSet.iterator(); iterator.hasNext(); ) {
                    String prefix = iterator.next();
                    if (prefixes.length() != 0) {
                        prefixes.append(" ");
                    }
                    prefixes.append(prefix);
                }
                inclusiveNamespacePrefixes = new ArrayList<String>(prefixSet);
                signaturePartDef.setInclusiveNamespacesPrefixes(prefixes.toString());
            }

            if (parentTransformer != null) {
                parentTransformer = XMLSecurityUtils.getTransformer(
                        parentTransformer, null, transform, XMLSecurityConstants.DIRECTION.OUT);
            } else {
                parentTransformer = XMLSecurityUtils.getTransformer(
                        inclusiveNamespacePrefixes, outputStream, transform, XMLSecurityConstants.DIRECTION.OUT);
            }
        }
        return parentTransformer;
    }

    public class InternalSignatureOutputProcessor extends AbstractOutputProcessor {

        private SignaturePartDef signaturePartDef;
        private XMLSecStartElement xmlSecStartElement;
        private int elementCounter = 0;

        private OutputStream bufferedDigestOutputStream;
        private DigestOutputStream digestOutputStream;
        private Transformer transformer;

        public InternalSignatureOutputProcessor(SignaturePartDef signaturePartDef, XMLSecStartElement xmlSecStartElement)
                throws XMLSecurityException {
            super();
            this.addBeforeProcessor(InternalSignatureOutputProcessor.class.getName());
            this.signaturePartDef = signaturePartDef;
            this.xmlSecStartElement = xmlSecStartElement;
        }

        @Override
        public void init(OutputProcessorChain outputProcessorChain) throws XMLSecurityException {
            this.digestOutputStream = createMessageDigestOutputStream(signaturePartDef.getDigestAlgo());
            this.bufferedDigestOutputStream = new UnsynchronizedBufferedOutputStream(digestOutputStream);
            this.transformer = buildTransformerChain(this.bufferedDigestOutputStream, signaturePartDef, xmlSecStartElement);
            super.init(outputProcessorChain);
        }

        @Override
        public void processEvent(XMLSecEvent xmlSecEvent, OutputProcessorChain outputProcessorChain)
                throws XMLStreamException, XMLSecurityException {

            transformer.transform(xmlSecEvent);

            switch (xmlSecEvent.getEventType()) {
                case XMLStreamConstants.START_ELEMENT:
                    elementCounter++;
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    elementCounter--;

                    if (elementCounter == 0 &&
                            xmlSecEvent.asEndElement().getName().equals(this.xmlSecStartElement.getName())) {

                        transformer.doFinal();
                        try {
                            bufferedDigestOutputStream.close();
                        } catch (IOException e) {
                            throw new XMLSecurityException(e);
                        }
                        String calculatedDigest = new String(Base64.encodeBase64(this.digestOutputStream.getDigestValue()));
                        if (logger.isDebugEnabled()) {
                            logger.debug("Calculated Digest: " + calculatedDigest);
                        }
                        signaturePartDef.setDigestValue(calculatedDigest);

                        outputProcessorChain.removeProcessor(this);
                        //from now on signature is possible again
                        setActiveInternalSignatureOutputProcessor(null);
                    }
                    break;
            }
            outputProcessorChain.processEvent(xmlSecEvent);
        }
    }
}

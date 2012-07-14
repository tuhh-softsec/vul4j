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
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.security.stax.config.JCEAlgorithmMapper;
import org.apache.xml.security.stax.config.ResourceResolverMapper;
import org.apache.xml.security.stax.ext.*;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.impl.SignaturePartDef;
import org.apache.xml.security.stax.impl.util.DigestOutputStream;
import org.xmlsecurity.ns.configuration.AlgorithmType;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public abstract class AbstractSignatureOutputProcessor extends AbstractOutputProcessor {

    private static final transient Log logger = LogFactory.getLog(AbstractSignatureOutputProcessor.class);

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
        Map<Object, SecurePart> dynamicSecureParts = outputProcessorChain.getSecurityContext().getAsMap(XMLSecurityConstants.SIGNATURE_PARTS);
        Iterator<Map.Entry<Object, SecurePart>> securePartsMapIterator = dynamicSecureParts.entrySet().iterator();
        while (securePartsMapIterator.hasNext()) {
            Map.Entry<Object, SecurePart> securePartEntry = securePartsMapIterator.next();
            final SecurePart securePart = securePartEntry.getValue();
            final String externalReference = securePart.getExternalReference();
            if (externalReference != null) {
                ResourceResolver resourceResolver = ResourceResolverMapper.getResourceResolver(externalReference);

                DigestOutputStream digestOutputStream = null;
                try {
                    digestOutputStream = createMessageDigestOutputStream(securePart.getDigestMethod());
                } catch (NoSuchAlgorithmException e) {
                    throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_SIGNATURE, e);
                } catch (NoSuchProviderException e) {
                    throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_SIGNATURE, e);
                }

                InputStream inputStream = resourceResolver.getInputStreamFromExternalReference();

                //todo transformers

                try {
                    IOUtils.copy(inputStream, digestOutputStream);
                } catch (IOException e) {
                    throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_SIGNATURE, e);
                }
                String calculatedDigest = new String(Base64.encodeBase64(digestOutputStream.getDigestValue()));
                if (logger.isDebugEnabled()) {
                    logger.debug("Calculated Digest: " + calculatedDigest);
                }

                SignaturePartDef signaturePartDef = new SignaturePartDef();
                signaturePartDef.setSigRefId(externalReference);
                signaturePartDef.setDigestValue(calculatedDigest);
                signaturePartDef.setExternalResource(true);
                signaturePartDef.setC14nAlgo(securePart.getC14nMethod());
                String digestMethod = securePart.getDigestMethod();
                if (digestMethod == null) {
                    digestMethod = getSecurityProperties().getSignatureDigestAlgorithm();
                }
                signaturePartDef.setDigestAlgo(digestMethod);
                getSignaturePartDefList().add(signaturePartDef);
            }
        }

        super.doFinal(outputProcessorChain);
    }

    protected InternalSignatureOutputProcessor getActiveInternalSignatureOutputProcessor() {
        return activeInternalSignatureOutputProcessor;
    }

    protected void setActiveInternalSignatureOutputProcessor(
            InternalSignatureOutputProcessor activeInternalSignatureOutputProcessor) {
        this.activeInternalSignatureOutputProcessor = activeInternalSignatureOutputProcessor;
    }

    private DigestOutputStream createMessageDigestOutputStream(String digestAlgorithm) throws XMLSecurityException, NoSuchAlgorithmException, NoSuchProviderException {
        AlgorithmType algorithmType = JCEAlgorithmMapper.getAlgorithmMapping(digestAlgorithm);
        if (algorithmType == null) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.UNSUPPORTED_ALGORITHM, "unknownSignatureAlgorithm", digestAlgorithm);
        }
        MessageDigest messageDigest;
        if (algorithmType.getJCEProvider() != null) {
            messageDigest = MessageDigest.getInstance(algorithmType.getJCEName(), algorithmType.getJCEProvider());
        } else {
            messageDigest = MessageDigest.getInstance(algorithmType.getJCEName());
        }
        return new DigestOutputStream(messageDigest);
    }

    public class InternalSignatureOutputProcessor extends AbstractOutputProcessor {

        private SignaturePartDef signaturePartDef;
        private QName startElement;
        private int elementCounter = 0;

        private OutputStream bufferedDigestOutputStream;
        private DigestOutputStream digestOutputStream;
        private Transformer transformer;

        public InternalSignatureOutputProcessor(SignaturePartDef signaturePartDef, QName startElement)
                throws XMLSecurityException, NoSuchProviderException, NoSuchAlgorithmException {
            super();
            this.addBeforeProcessor(InternalSignatureOutputProcessor.class.getName());
            this.signaturePartDef = signaturePartDef;
            this.startElement = startElement;
        }

        @Override
        public void init(OutputProcessorChain outputProcessorChain) throws XMLSecurityException {
            try {
                this.digestOutputStream = createMessageDigestOutputStream(signaturePartDef.getDigestAlgo());
                this.bufferedDigestOutputStream = new BufferedOutputStream(digestOutputStream);

                if (signaturePartDef.getTransformAlgo() != null) {
                    List<String> inclusiveNamespaces = new ArrayList<String>(1);
                    inclusiveNamespaces.add("#default");
                    Transformer transformer = XMLSecurityUtils.getTransformer(inclusiveNamespaces,
                            this.bufferedDigestOutputStream, signaturePartDef.getC14nAlgo());
                    this.transformer = XMLSecurityUtils.getTransformer(transformer, null, signaturePartDef.getTransformAlgo());
                } else {
                    transformer = XMLSecurityUtils.getTransformer(null, this.bufferedDigestOutputStream, signaturePartDef.getC14nAlgo());
                }
            } catch (NoSuchMethodException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_SIGNATURE, e);
            } catch (InstantiationException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_SIGNATURE, e);
            } catch (IllegalAccessException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_SIGNATURE, e);
            } catch (InvocationTargetException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_SIGNATURE, e);
            } catch (NoSuchAlgorithmException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_SIGNATURE, e);
            } catch (NoSuchProviderException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_SIGNATURE, e);
            }

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

                    if (elementCounter == 0 && xmlSecEvent.asEndElement().getName().equals(this.startElement)) {
                        try {
                            bufferedDigestOutputStream.close();
                        } catch (IOException e) {
                            throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_SIGNATURE, e);
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

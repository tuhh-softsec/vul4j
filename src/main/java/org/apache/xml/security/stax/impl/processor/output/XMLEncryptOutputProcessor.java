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
import org.apache.xml.security.stax.securityToken.SecurityTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.config.JCEAlgorithmMapper;
import org.apache.xml.security.stax.ext.*;
import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.impl.EncryptionPartDef;
import org.apache.xml.security.stax.securityToken.OutboundSecurityToken;
import org.apache.xml.security.stax.impl.util.IDGenerator;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.util.ArrayList;
import java.util.List;

/**
 * Processor to encrypt XML structures
 *
 * @author $Author: coheigea $
 * @version $Revision: 1236690 $ $Date: 2012-01-27 14:07:10 +0000 (Fri, 27 Jan 2012) $
 */
public class XMLEncryptOutputProcessor extends AbstractEncryptOutputProcessor {

    private static final transient Logger logger = LoggerFactory.getLogger(XMLEncryptOutputProcessor.class);

    public XMLEncryptOutputProcessor() throws XMLSecurityException {
        super();
    }

    @Override
    public void processEvent(XMLSecEvent xmlSecEvent, OutputProcessorChain outputProcessorChain) throws XMLStreamException, XMLSecurityException {
        if (xmlSecEvent.getEventType() == XMLStreamConstants.START_ELEMENT) {
            XMLSecStartElement xmlSecStartElement = xmlSecEvent.asStartElement();

            //avoid double encryption when child elements matches too
            if (getActiveInternalEncryptionOutputProcessor() == null) {
                SecurePart securePart = securePartMatches(xmlSecStartElement, outputProcessorChain, XMLSecurityConstants.ENCRYPTION_PARTS);
                if (securePart != null) {
                    logger.debug("Matched encryptionPart for encryption");
                    String tokenId = outputProcessorChain.getSecurityContext().get(
                            XMLSecurityConstants.PROP_USE_THIS_TOKEN_ID_FOR_ENCRYPTION);
                    SecurityTokenProvider<OutboundSecurityToken> securityTokenProvider =
                            outputProcessorChain.getSecurityContext().getSecurityTokenProvider(tokenId);
                    final OutboundSecurityToken securityToken = securityTokenProvider.getSecurityToken();

                    EncryptionPartDef encryptionPartDef = new EncryptionPartDef();
                    encryptionPartDef.setSecurePart(securePart);
                    encryptionPartDef.setModifier(securePart.getModifier());
                    encryptionPartDef.setEncRefId(IDGenerator.generateID(null));
                    encryptionPartDef.setKeyId(securityTokenProvider.getId());
                    encryptionPartDef.setSymmetricKey(securityToken.getSecretKey(getSecurityProperties().getEncryptionSymAlgorithm()));
                    outputProcessorChain.getSecurityContext().putAsList(EncryptionPartDef.class, encryptionPartDef);

                    AbstractInternalEncryptionOutputProcessor internalEncryptionOutputProcessor =
                            createInternalEncryptionOutputProcessor(
                                    encryptionPartDef, xmlSecStartElement,
                                    outputProcessorChain.getDocumentContext().getEncoding(),
                                    (OutboundSecurityToken)securityToken.getKeyWrappingToken()
                            );
                    internalEncryptionOutputProcessor.setXMLSecurityProperties(getSecurityProperties());
                    internalEncryptionOutputProcessor.setAction(getAction());
                    internalEncryptionOutputProcessor.init(outputProcessorChain);

                    setActiveInternalEncryptionOutputProcessor(internalEncryptionOutputProcessor);
                }
            }
        }

        outputProcessorChain.processEvent(xmlSecEvent);
    }

    /**
     * Override this method to return a different AbstractInternalEncryptionOutputProcessor instance
     * which will write out the KeyInfo contents in the EncryptedData.
     */
    protected AbstractInternalEncryptionOutputProcessor createInternalEncryptionOutputProcessor(
            EncryptionPartDef encryptionPartDef,
            XMLSecStartElement startElement,
            String encoding,
            final OutboundSecurityToken keyWrappingToken
    ) throws XMLStreamException, XMLSecurityException {

        final AbstractInternalEncryptionOutputProcessor processor =
                new AbstractInternalEncryptionOutputProcessor(encryptionPartDef,
                        startElement,
                        encoding) {

                    @Override
                    protected void createKeyInfoStructure(OutputProcessorChain outputProcessorChain)
                            throws XMLStreamException, XMLSecurityException {
                        if (keyWrappingToken == null) {
                            // Do not write out a KeyInfo element
                            return;
                        }

                        final String encryptionKeyTransportAlgorithm = getSecurityProperties().getEncryptionKeyTransportAlgorithm();

                        PublicKey pubKey = keyWrappingToken.getPublicKey();
                        Key secretKey = keyWrappingToken.getSecretKey(encryptionKeyTransportAlgorithm);
                        if (pubKey == null && secretKey == null) {
                            // Do not write out a KeyInfo element
                            return;
                        }

                        createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_KeyInfo, true, null);

                        List<XMLSecAttribute> attributes = new ArrayList<XMLSecAttribute>(1);
                        String keyId = getEncryptionPartDef().getKeyId();
                        if (keyId == null) {
                            keyId = IDGenerator.generateID("EK");
                        }
                        attributes.add(createAttribute(XMLSecurityConstants.ATT_NULL_Id, keyId));
                        createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_EncryptedKey, true, attributes);

                        attributes = new ArrayList<XMLSecAttribute>(1);
                        attributes.add(createAttribute(XMLSecurityConstants.ATT_NULL_Algorithm, encryptionKeyTransportAlgorithm));
                        createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_EncryptionMethod, false, attributes);

                        final String encryptionKeyTransportDigestAlgorithm = getSecurityProperties().getEncryptionKeyTransportDigestAlgorithm();
                        final String encryptionKeyTransportMGFAlgorithm = getSecurityProperties().getEncryptionKeyTransportMGFAlgorithm();

                        if (XMLSecurityConstants.NS_XENC11_RSAOAEP.equals(encryptionKeyTransportAlgorithm) ||
                                XMLSecurityConstants.NS_XENC_RSAOAEPMGF1P.equals(encryptionKeyTransportAlgorithm)) {

                            byte[] oaepParams = getSecurityProperties().getEncryptionKeyTransportOAEPParams();
                            if (oaepParams != null) {
                                createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_OAEPparams, false, null);
                                createCharactersAndOutputAsEvent(outputProcessorChain, Base64.encodeBase64String(oaepParams));
                                createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_OAEPparams);
                            }

                            if (encryptionKeyTransportDigestAlgorithm != null) {
                                attributes = new ArrayList<XMLSecAttribute>(1);
                                attributes.add(createAttribute(XMLSecurityConstants.ATT_NULL_Algorithm, encryptionKeyTransportDigestAlgorithm));
                                createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_DigestMethod, true, attributes);
                                createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_DigestMethod);
                            }

                            if (encryptionKeyTransportMGFAlgorithm != null) {
                                attributes = new ArrayList<XMLSecAttribute>(1);
                                attributes.add(createAttribute(XMLSecurityConstants.ATT_NULL_Algorithm, encryptionKeyTransportMGFAlgorithm));
                                createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc11_MGF, true, attributes);
                                createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc11_MGF);
                            }
                        }

                        createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_EncryptionMethod);
                        createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_CipherData, false, null);
                        createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_CipherValue, false, null);

                        //encrypt the symmetric session key with the public key from the receiver:
                        String jceid = JCEAlgorithmMapper.translateURItoJCEID(encryptionKeyTransportAlgorithm);
                        if (jceid == null) {
                            throw new XMLSecurityException("algorithms.NoSuchMap", encryptionKeyTransportAlgorithm);
                        }

                        try {
                            Cipher cipher = Cipher.getInstance(jceid);

                            AlgorithmParameterSpec algorithmParameterSpec = null;
                            if (XMLSecurityConstants.NS_XENC11_RSAOAEP.equals(encryptionKeyTransportAlgorithm) ||
                                    XMLSecurityConstants.NS_XENC_RSAOAEPMGF1P.equals(encryptionKeyTransportAlgorithm)) {

                                String jceDigestAlgorithm = "SHA-1";
                                if (encryptionKeyTransportDigestAlgorithm != null) {
                                    jceDigestAlgorithm = JCEAlgorithmMapper.translateURItoJCEID(encryptionKeyTransportDigestAlgorithm);
                                }

                                PSource.PSpecified pSource = PSource.PSpecified.DEFAULT;
                                byte[] oaepParams = getSecurityProperties().getEncryptionKeyTransportOAEPParams();
                                if (oaepParams != null) {
                                    pSource = new PSource.PSpecified(oaepParams);
                                }

                                MGF1ParameterSpec mgfParameterSpec = new MGF1ParameterSpec("SHA-1");
                                if (encryptionKeyTransportMGFAlgorithm != null) {
                                    String jceMGFAlgorithm = JCEAlgorithmMapper.translateURItoJCEID(encryptionKeyTransportMGFAlgorithm);
                                    mgfParameterSpec = new MGF1ParameterSpec(jceMGFAlgorithm);
                                }
                                algorithmParameterSpec = new OAEPParameterSpec(jceDigestAlgorithm, "MGF1", mgfParameterSpec, pSource);
                            }

                            if (pubKey != null) {
                                cipher.init(Cipher.WRAP_MODE, pubKey, algorithmParameterSpec);
                            } else {
                                cipher.init(Cipher.WRAP_MODE, secretKey, algorithmParameterSpec);
                            }

                            String tokenId = outputProcessorChain.getSecurityContext().get(
                                    XMLSecurityConstants.PROP_USE_THIS_TOKEN_ID_FOR_ENCRYPTION);
                            SecurityTokenProvider<OutboundSecurityToken> securityTokenProvider =
                                    outputProcessorChain.getSecurityContext().getSecurityTokenProvider(tokenId);

                            final OutboundSecurityToken securityToken = securityTokenProvider.getSecurityToken();
                            Key sessionKey =
                                    securityToken.getSecretKey(getSecurityProperties().getEncryptionSymAlgorithm());
                            if (pubKey != null) {
                                int blockSize = cipher.getBlockSize();
                                if (blockSize > 0 && blockSize < sessionKey.getEncoded().length) {
                                    throw new XMLSecurityException(
                                            "stax.unsupportedKeyTransp"
                                    );
                                }
                            }
                            byte[] encryptedEphemeralKey = cipher.wrap(sessionKey);

                            createCharactersAndOutputAsEvent(outputProcessorChain, new Base64(76, new byte[]{'\n'}).encodeToString(encryptedEphemeralKey));

                        } catch (NoSuchPaddingException e) {
                            throw new XMLSecurityException(e);
                        } catch (NoSuchAlgorithmException e) {
                            throw new XMLSecurityException(e);
                        } catch (InvalidKeyException e) {
                            throw new XMLSecurityException(e);
                        } catch (IllegalBlockSizeException e) {
                            throw new XMLSecurityException(e);
                        } catch (InvalidAlgorithmParameterException e) {
                            throw new XMLSecurityException(e);
                        }

                        createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_CipherValue);
                        createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_CipherData);

                        createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_EncryptedKey);

                        createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_KeyInfo);
                    }
                };
        return processor;
    }
}

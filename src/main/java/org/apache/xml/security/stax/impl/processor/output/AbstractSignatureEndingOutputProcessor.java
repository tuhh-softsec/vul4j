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
import org.apache.xml.security.stax.ext.*;
import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.impl.SignaturePartDef;
import org.apache.xml.security.stax.impl.algorithms.SignatureAlgorithm;
import org.apache.xml.security.stax.impl.algorithms.SignatureAlgorithmFactory;
import org.apache.xml.security.stax.impl.util.IDGenerator;
import org.apache.xml.security.stax.impl.util.SignerOutputStream;

import javax.xml.stream.XMLStreamException;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public abstract class AbstractSignatureEndingOutputProcessor extends AbstractBufferingOutputProcessor {

    private List<SignaturePartDef> signaturePartDefList;

    public AbstractSignatureEndingOutputProcessor(AbstractSignatureOutputProcessor signatureOutputProcessor)
            throws XMLSecurityException {
        super();
        signaturePartDefList = signatureOutputProcessor.getSignaturePartDefList();
    }

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
                <wsse:SecurityTokenReference xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" wsu:Id="STRId-1008354042">
                    <wsse:Reference xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd" URI="#CertId-3458500" ValueType="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3" />
                </wsse:SecurityTokenReference>
            </ds:KeyInfo>
        </ds:Signature>
    */

    @Override
    public void doFinal(OutputProcessorChain outputProcessorChain) throws XMLStreamException, XMLSecurityException {
        setAppendAfterThisTokenId(outputProcessorChain.getSecurityContext().<String>get(XMLSecurityConstants.PROP_APPEND_SIGNATURE_ON_THIS_ID));
        super.doFinal(outputProcessorChain);
    }

    public void processHeaderEvent(OutputProcessorChain outputProcessorChain) throws XMLStreamException, XMLSecurityException {

        OutputProcessorChain subOutputProcessorChain = outputProcessorChain.createSubChain(this);

        List<XMLSecAttribute> attributes = new ArrayList<XMLSecAttribute>(1);
        attributes.add(createAttribute(XMLSecurityConstants.ATT_NULL_Id, IDGenerator.generateID(null)));
        createStartElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_Signature, true, attributes);

        SignatureAlgorithm signatureAlgorithm;

        try {
            signatureAlgorithm = SignatureAlgorithmFactory.getInstance().getSignatureAlgorithm(getSecurityProperties().getSignatureAlgorithm());
        } catch (NoSuchAlgorithmException e) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_SIGNATURE, e);
        } catch (NoSuchProviderException e) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILURE, "noSecProvider", e);
        }

        String tokenId = outputProcessorChain.getSecurityContext().get(XMLSecurityConstants.PROP_USE_THIS_TOKEN_ID_FOR_SIGNATURE);
        if (tokenId == null) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_SIGNATURE);
        }
        SecurityTokenProvider wrappingSecurityTokenProvider = outputProcessorChain.getSecurityContext().getSecurityTokenProvider(tokenId);
        if (wrappingSecurityTokenProvider == null) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_SIGNATURE);
        }
        final SecurityToken wrappingSecurityToken = wrappingSecurityTokenProvider.getSecurityToken();
        if (wrappingSecurityToken == null) {
            throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_SIGNATURE);
        }

        signatureAlgorithm.engineInitSign(wrappingSecurityToken.getSecretKey(getSecurityProperties().getSignatureAlgorithm(), null));

        SignedInfoProcessor signedInfoProcessor = newSignedInfoProcessor(signatureAlgorithm, subOutputProcessorChain);

        createStartElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_SignedInfo, false, null);

        attributes = new ArrayList<XMLSecAttribute>(1);
        attributes.add(createAttribute(XMLSecurityConstants.ATT_NULL_Algorithm, getSecurityProperties().getSignatureCanonicalizationAlgorithm()));
        createStartElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_CanonicalizationMethod, false, attributes);
        createEndElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_CanonicalizationMethod);

        attributes = new ArrayList<XMLSecAttribute>(1);
        attributes.add(createAttribute(XMLSecurityConstants.ATT_NULL_Algorithm, getSecurityProperties().getSignatureAlgorithm()));
        createStartElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_SignatureMethod, false, attributes);
        createEndElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_SignatureMethod);

        Iterator<SignaturePartDef> signaturePartDefIterator = signaturePartDefList.iterator();
        while (signaturePartDefIterator.hasNext()) {
            SignaturePartDef signaturePartDef = signaturePartDefIterator.next();
            attributes = new ArrayList<XMLSecAttribute>(1);
            attributes.add(createAttribute(XMLSecurityConstants.ATT_NULL_URI, "#" + signaturePartDef.getSigRefId()));
            createStartElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_Reference, false, attributes);
            createStartElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_Transforms, false, null);
            createTransformsStructureForSignature(subOutputProcessorChain, signaturePartDef);
            createEndElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_Transforms);

            attributes = new ArrayList<XMLSecAttribute>(1);
            attributes.add(createAttribute(XMLSecurityConstants.ATT_NULL_Algorithm, getSecurityProperties().getSignatureDigestAlgorithm()));
            createStartElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_DigestMethod, false, attributes);
            createEndElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_DigestMethod);
            createStartElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_DigestValue, false, null);
            createCharactersAndOutputAsEvent(subOutputProcessorChain, signaturePartDef.getDigestValue());
            createEndElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_DigestValue);
            createEndElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_Reference);
        }

        createEndElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_SignedInfo);
        subOutputProcessorChain.removeProcessor(signedInfoProcessor);

        createStartElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_SignatureValue, false, null);
        final byte[] signatureValue = signedInfoProcessor.getSignatureValue();
        createCharactersAndOutputAsEvent(subOutputProcessorChain, new Base64(76, new byte[]{'\n'}).encodeToString(signatureValue));
        createEndElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_SignatureValue);

        attributes = new ArrayList<XMLSecAttribute>(1);
        attributes.add(createAttribute(XMLSecurityConstants.ATT_NULL_Id, IDGenerator.generateID(null)));
        createStartElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_KeyInfo, false, attributes);
        createKeyInfoStructureForSignature(subOutputProcessorChain, wrappingSecurityToken, getSecurityProperties().isUseSingleCert());
        createEndElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_KeyInfo);
        createEndElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_Signature);
    }

    protected SignedInfoProcessor newSignedInfoProcessor(SignatureAlgorithm signatureAlgorithm, OutputProcessorChain outputProcessorChain)
            throws XMLSecurityException {
        SignedInfoProcessor signedInfoProcessor = new SignedInfoProcessor(signatureAlgorithm);
        signedInfoProcessor.setXMLSecurityProperties(getSecurityProperties());
        signedInfoProcessor.setAction(getAction());
        signedInfoProcessor.addAfterProcessor(AbstractSignatureEndingOutputProcessor.class.getName());
        signedInfoProcessor.init(outputProcessorChain);
        return signedInfoProcessor;
    }

    protected abstract void createTransformsStructureForSignature(
            OutputProcessorChain subOutputProcessorChain,
            SignaturePartDef signaturePartDef) throws XMLStreamException, XMLSecurityException;

    protected abstract void createKeyInfoStructureForSignature(
            OutputProcessorChain outputProcessorChain,
            SecurityToken securityToken,
            boolean useSingleCertificate) throws XMLStreamException, XMLSecurityException;


    public class SignedInfoProcessor extends AbstractOutputProcessor {

        private SignerOutputStream signerOutputStream;
        private OutputStream bufferedSignerOutputStream;
        private Transformer transformer;
        private byte[] signatureValue = null;
        private SignatureAlgorithm signatureAlgorithm;

        public SignedInfoProcessor(SignatureAlgorithm signatureAlgorithm) throws XMLSecurityException {
            super();
            this.signatureAlgorithm = signatureAlgorithm;
        }

        @Override
        public void init(OutputProcessorChain outputProcessorChain) throws XMLSecurityException {

            this.signerOutputStream = new SignerOutputStream(this.signatureAlgorithm);
            this.bufferedSignerOutputStream = new BufferedOutputStream(this.signerOutputStream);

            try {
                this.transformer = XMLSecurityUtils.getTransformer(null, this.bufferedSignerOutputStream,
                        getSecurityProperties().getSignatureCanonicalizationAlgorithm());
            } catch (NoSuchMethodException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_SIGNATURE, e);
            } catch (InstantiationException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_SIGNATURE, e);
            } catch (IllegalAccessException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_SIGNATURE, e);
            } catch (InvocationTargetException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_SIGNATURE, e);
            }

            super.init(outputProcessorChain);
        }

        public byte[] getSignatureValue() throws XMLSecurityException {
            if (signatureValue != null) {
                return signatureValue;
            }
            try {
                bufferedSignerOutputStream.close();
                signatureValue = signerOutputStream.sign();
                return signatureValue;
            } catch (IOException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_SIGNATURE, e);
            }
        }

        @Override
        public void processEvent(XMLSecEvent xmlSecEvent, OutputProcessorChain outputProcessorChain)
                throws XMLStreamException, XMLSecurityException {
            transformer.transform(xmlSecEvent);
            outputProcessorChain.processEvent(xmlSecEvent);
        }
    }
}

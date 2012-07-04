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

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.xml.security.stax.ext.OutputProcessorChain;
import org.apache.xml.security.stax.ext.SecurityToken;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityException;
import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.impl.SignaturePartDef;
import org.apache.xml.security.stax.impl.algorithms.SignatureAlgorithm;

/**
 * An EndingOutputProcessor for XML Signature.
 */
public class XMLSignatureEndingOutputProcessor extends AbstractSignatureEndingOutputProcessor {

    private SignedInfoProcessor signedInfoProcessor = null;

    public XMLSignatureEndingOutputProcessor(XMLSignatureOutputProcessor signatureOutputProcessor) throws XMLSecurityException {
        super(signatureOutputProcessor);
        this.addAfterProcessor(XMLSignatureOutputProcessor.class.getName());
    }

    @Override
    protected SignedInfoProcessor newSignedInfoProcessor(SignatureAlgorithm signatureAlgorithm, OutputProcessorChain outputProcessorChain) throws XMLSecurityException {
        this.signedInfoProcessor = new SignedInfoProcessor(signatureAlgorithm);
        this.signedInfoProcessor.setXMLSecurityProperties(getSecurityProperties());
        this.signedInfoProcessor.setAction(getAction());
        this.signedInfoProcessor.addAfterProcessor(XMLSignatureEndingOutputProcessor.class.getName());
        this.signedInfoProcessor.init(outputProcessorChain);
        return this.signedInfoProcessor;
    }

    @Override
    protected void createKeyInfoStructureForSignature(
            OutputProcessorChain outputProcessorChain,
            SecurityToken securityToken,
            boolean useSingleCertificate)
            throws XMLStreamException, XMLSecurityException {
        // Issuer-Serial by default
        X509Certificate[] x509Certificates = securityToken.getX509Certificates();
        if (x509Certificates != null) {
            createX509IssuerSerialStructure(outputProcessorChain, x509Certificates);
        }
    }

    @Override
    protected void createTransformsStructureForSignature(OutputProcessorChain subOutputProcessorChain, SignaturePartDef signaturePartDef) throws XMLStreamException, XMLSecurityException {
        if (signaturePartDef.getTransformAlgo() != null) {
            List<XMLSecAttribute> attributes = new ArrayList<XMLSecAttribute>(1);
            attributes.add(createAttribute(XMLSecurityConstants.ATT_NULL_Algorithm, signaturePartDef.getTransformAlgo()));
            createStartElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_Transform, false, attributes);
            createEndElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_Transform);
        } else {
            List<XMLSecAttribute> attributes = new ArrayList<XMLSecAttribute>(1);
            attributes.add(createAttribute(XMLSecurityConstants.ATT_NULL_Algorithm, signaturePartDef.getC14nAlgo()));
            createStartElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_Transform, false, attributes);
            createEndElementAndOutputAsEvent(subOutputProcessorChain, XMLSecurityConstants.TAG_dsig_Transform);
        }
    }
}

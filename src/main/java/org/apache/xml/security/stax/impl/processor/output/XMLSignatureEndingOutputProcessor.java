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
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import org.apache.xml.security.stax.ext.AbstractBufferingOutputProcessor;
import org.apache.xml.security.stax.ext.OutputProcessorChain;
import org.apache.xml.security.stax.ext.SecurityToken;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityException;
import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.ext.stax.XMLSecEndElement;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
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
    public void doFinal(OutputProcessorChain outputProcessorChain) throws XMLStreamException, XMLSecurityException {
        setAppendAfterThisTokenId(outputProcessorChain.getSecurityContext().<String>get(XMLSecurityConstants.PROP_APPEND_SIGNATURE_ON_THIS_ID));
        OutputProcessorChain subOutputProcessorChain = outputProcessorChain.createSubChain(this);
        flushBufferAndCallbackAfterTokenID(subOutputProcessorChain, this, getXmlSecEventBuffer());
        //call final on the rest of the chain
        subOutputProcessorChain.doFinal();
        //this processor is now finished and we can remove it now
        subOutputProcessorChain.removeProcessor(this);
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
    
    private static void flushBufferAndCallbackAfterTokenID(OutputProcessorChain outputProcessorChain,
            AbstractBufferingOutputProcessor abstractBufferingOutputProcessor,
            Deque<XMLSecEvent> xmlSecEventDeque
    ) throws XMLStreamException, XMLSecurityException {
        final Iterator<XMLSecEvent> xmlSecEventIterator = xmlSecEventDeque.descendingIterator();

        String appendAfterThisTokenId = abstractBufferingOutputProcessor.getAppendAfterThisTokenId();

        //append current header
        if (appendAfterThisTokenId == null) {
            abstractBufferingOutputProcessor.processHeaderEvent(outputProcessorChain);
        } else {
            //we have a dependent token. so we have to append the current header after the token
            QName matchingElementName = null;

            loop:
            while (xmlSecEventIterator.hasNext()) {
                XMLSecEvent xmlSecEvent = xmlSecEventIterator.next();

                outputProcessorChain.reset();
                outputProcessorChain.processEvent(xmlSecEvent);
                switch (xmlSecEvent.getEventType()) {
                    //search for an element with a matching wsu:Id. this is our token
                    case XMLStreamConstants.START_ELEMENT:
                        XMLSecStartElement xmlSecStartElement = xmlSecEvent.asStartElement();
                        List<XMLSecAttribute> xmlSecAttributes = xmlSecStartElement.getOnElementDeclaredAttributes();
                        for (int i = 0; i < xmlSecAttributes.size(); i++) {
                            XMLSecAttribute xmlSecAttribute = xmlSecAttributes.get(i);
                            final QName attributeName = xmlSecAttribute.getName();
                            final String attributeValue = xmlSecAttribute.getValue();
                            if (XMLSecurityConstants.ATT_NULL_Id.equals(attributeName)
                                    && appendAfterThisTokenId.equals(attributeValue)) {
                                matchingElementName = xmlSecStartElement.getName();
                                break loop;
                            }
                        }
                        break;
                }
            }

            //we found the token and...
            int level = 0;
            loop:
            while (xmlSecEventIterator.hasNext()) {
                XMLSecEvent xmlSecEvent = xmlSecEventIterator.next();

                outputProcessorChain.reset();
                outputProcessorChain.processEvent(xmlSecEvent);
                //...loop until we reach the token end element
                switch (xmlSecEvent.getEventType()) {
                    case XMLStreamConstants.START_ELEMENT:
                        level++;
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        XMLSecEndElement xmlSecEndElement = xmlSecEvent.asEndElement();
                        if (level == 0 && xmlSecEndElement.getName().equals(matchingElementName)) {
                            //output now the current header
                            abstractBufferingOutputProcessor.processHeaderEvent(outputProcessorChain);
                            break loop;
                        }
                        level--;
                        break;
                }
            }
        }
        
        //loop through the rest of the document
        while (xmlSecEventIterator.hasNext()) {
            XMLSecEvent xmlSecEvent = xmlSecEventIterator.next();
            outputProcessorChain.reset();
            outputProcessorChain.processEvent(xmlSecEvent);
        }
        outputProcessorChain.reset();
    }
}

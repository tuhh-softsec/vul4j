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

import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.config.JCEAlgorithmMapper;
import org.apache.xml.security.stax.ext.AbstractOutputProcessor;
import org.apache.xml.security.stax.ext.OutputProcessorChain;
import org.apache.xml.security.stax.ext.SecurePart;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.stax.*;
import org.apache.xml.security.stax.impl.EncryptionPartDef;
import org.apache.xml.security.stax.impl.XMLSecurityEventWriter;
import org.apache.xml.security.stax.impl.util.TrimmerOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Processor to encrypt XML structures
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public abstract class AbstractEncryptOutputProcessor extends AbstractOutputProcessor {

    private static final XMLSecStartElement wrapperStartElement;
    private static final XMLSecEndElement wrapperEndElement;

    static {
        wrapperStartElement = XMLSecEventFactory.createXmlSecStartElement(new QName("a"), null, null);
        wrapperEndElement = XMLSecEventFactory.createXmlSecEndElement(new QName("a"));
    }

    private AbstractInternalEncryptionOutputProcessor activeInternalEncryptionOutputProcessor = null;

    public AbstractEncryptOutputProcessor() throws XMLSecurityException {
        super();
    }

    @Override
    public abstract void processEvent(XMLSecEvent xmlSecEvent, OutputProcessorChain outputProcessorChain)
            throws XMLStreamException, XMLSecurityException;

    @Override
    public void doFinal(OutputProcessorChain outputProcessorChain) throws XMLStreamException, XMLSecurityException {
        doFinalInternal(outputProcessorChain);
        super.doFinal(outputProcessorChain);
    }

    protected void doFinalInternal(OutputProcessorChain outputProcessorChain) throws XMLSecurityException {
        verifyEncryptionParts(outputProcessorChain);
    }

    protected void verifyEncryptionParts(OutputProcessorChain outputProcessorChain) throws XMLSecurityException {
        List<EncryptionPartDef> encryptionPartDefs =
                outputProcessorChain.getSecurityContext().getAsList(EncryptionPartDef.class);

        Map<Object, SecurePart> dynamicSecureParts = outputProcessorChain.getSecurityContext().getAsMap(XMLSecurityConstants.ENCRYPTION_PARTS);
        Iterator<Map.Entry<Object, SecurePart>> securePartsMapIterator = dynamicSecureParts.entrySet().iterator();
        loop:
        while (securePartsMapIterator.hasNext()) {
            Map.Entry<Object, SecurePart> securePartEntry = securePartsMapIterator.next();
            final SecurePart securePart = securePartEntry.getValue();

            if (securePart.isRequired()) {
                for (int i = 0; encryptionPartDefs != null && i < encryptionPartDefs.size(); i++) {
                    EncryptionPartDef encryptionPartDef = encryptionPartDefs.get(i);
    
                    if (encryptionPartDef.getSecurePart() == securePart) {
                        continue loop;
                    }
                }
                throw new XMLSecurityException("stax.encryption.securePartNotFound", securePart.getName());
            }
        }
    }

    protected AbstractInternalEncryptionOutputProcessor getActiveInternalEncryptionOutputProcessor() {
        return activeInternalEncryptionOutputProcessor;
    }

    protected void setActiveInternalEncryptionOutputProcessor(
            AbstractInternalEncryptionOutputProcessor activeInternalEncryptionOutputProcessor) {
        this.activeInternalEncryptionOutputProcessor = activeInternalEncryptionOutputProcessor;
    }

    /**
     * Processor which handles the effective encryption of the data
     */
    public abstract class AbstractInternalEncryptionOutputProcessor extends AbstractOutputProcessor {

        private EncryptionPartDef encryptionPartDef;
        private CharacterEventGeneratorOutputStream characterEventGeneratorOutputStream;
        private XMLEventWriter xmlEventWriter;
        private OutputStream cipherOutputStream;
        private String encoding;

        private XMLSecStartElement xmlSecStartElement;
        private int elementCounter = 0;

        public AbstractInternalEncryptionOutputProcessor(EncryptionPartDef encryptionPartDef,
                                                         XMLSecStartElement xmlSecStartElement, String encoding)
                throws XMLSecurityException {

            super();
            this.addBeforeProcessor(AbstractEncryptEndingOutputProcessor.class.getName());
            this.addBeforeProcessor(AbstractInternalEncryptionOutputProcessor.class.getName());
            this.addAfterProcessor(AbstractEncryptOutputProcessor.class.getName());
            this.setEncryptionPartDef(encryptionPartDef);
            this.setXmlSecStartElement(xmlSecStartElement);
            this.setEncoding(encoding);
        }

        @Override
        public void init(OutputProcessorChain outputProcessorChain) throws XMLSecurityException {

            String encryptionSymAlgorithm = securityProperties.getEncryptionSymAlgorithm();
            try {
                //initialize the cipher
                String jceAlgorithm = JCEAlgorithmMapper.translateURItoJCEID(encryptionSymAlgorithm);
                if (jceAlgorithm == null) {
                    throw new XMLSecurityException("algorithms.NoSuchMap", encryptionSymAlgorithm);
                }
                Cipher symmetricCipher = Cipher.getInstance(jceAlgorithm);

                int ivLen = JCEMapper.getIVLengthFromURI(encryptionSymAlgorithm) / 8;
                byte[] iv = new byte[ivLen];
                XMLSecurityConstants.secureRandom.nextBytes(iv);
                IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
                symmetricCipher.init(Cipher.ENCRYPT_MODE, encryptionPartDef.getSymmetricKey(), ivParameterSpec);

                characterEventGeneratorOutputStream = new CharacterEventGeneratorOutputStream();
                Base64OutputStream base64EncoderStream =
                        new Base64OutputStream(characterEventGeneratorOutputStream, true, 0, null);
                base64EncoderStream.write(iv);

                OutputStream outputStream = new CipherOutputStream(base64EncoderStream, symmetricCipher);
                outputStream = applyTransforms(outputStream);
                //the trimmer output stream is needed to strip away the dummy wrapping element which must be added
                cipherOutputStream = new TrimmerOutputStream(outputStream, 8192 * 10, 3, 4);

                //we create a new StAX writer for optimized namespace writing.
                //spec says (4.2): "The cleartext octet sequence obtained in step 3 is interpreted as UTF-8 encoded character data."
                xmlEventWriter = new XMLSecurityEventWriter(
                        XMLSecurityConstants.xmlOutputFactoryNonRepairingNs.createXMLStreamWriter(
                                cipherOutputStream, "UTF-8"));
                //we have to output a fake element to workaround text-only encryption:
                xmlEventWriter.add(wrapperStartElement);
            } catch (NoSuchPaddingException e) {
                throw new XMLSecurityException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new XMLSecurityException(e);
            } catch (IOException e) {
                throw new XMLSecurityException(e);
            } catch (XMLStreamException e) {
                throw new XMLSecurityException(e);
            } catch (InvalidKeyException e) {
                throw new XMLSecurityException(e);
            } catch (InvalidAlgorithmParameterException e) {
                throw new XMLSecurityException(e);
            }
            super.init(outputProcessorChain);
        }

        protected OutputStream applyTransforms(OutputStream outputStream) throws XMLSecurityException {
            return outputStream;
        }

        @Override
        public void processEvent(final XMLSecEvent xmlSecEvent, OutputProcessorChain outputProcessorChain)
                throws XMLStreamException, XMLSecurityException {

            switch (xmlSecEvent.getEventType()) {
                case XMLStreamConstants.START_ELEMENT:
                    XMLSecStartElement xmlSecStartElement = xmlSecEvent.asStartElement();

                    if (this.elementCounter == 0 && xmlSecStartElement.getName().equals(this.getXmlSecStartElement().getName())) {
                        //if the user selected element encryption we have to encrypt the current element-event...
                        switch (getEncryptionPartDef().getModifier()) {
                            case Element:
                                OutputProcessorChain subOutputProcessorChain = outputProcessorChain.createSubChain(this);
                                processEventInternal(xmlSecStartElement, subOutputProcessorChain);
                                //encrypt the current element event
                                encryptEvent(xmlSecEvent);
                                break;
                            case Content:
                                outputProcessorChain.processEvent(xmlSecEvent);
                                subOutputProcessorChain = outputProcessorChain.createSubChain(this);
                                processEventInternal(xmlSecStartElement, subOutputProcessorChain);
                                break;
                        }
                    } else {
                        encryptEvent(xmlSecEvent);
                    }

                    this.elementCounter++;
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    this.elementCounter--;

                    if (this.elementCounter == 0 && xmlSecEvent.asEndElement().getName().equals(this.getXmlSecStartElement().getName())) {
                        OutputProcessorChain subOutputProcessorChain = outputProcessorChain.createSubChain(this);
                        switch (getEncryptionPartDef().getModifier()) {
                            case Element:
                                encryptEvent(xmlSecEvent);
                                doFinalInternal(subOutputProcessorChain);
                                break;
                            case Content:
                                doFinalInternal(subOutputProcessorChain);
                                outputAsEvent(subOutputProcessorChain, xmlSecEvent);
                                break;
                        }
                        subOutputProcessorChain.removeProcessor(this);
                        //from now on encryption is possible again
                        setActiveInternalEncryptionOutputProcessor(null);

                    } else {
                        encryptEvent(xmlSecEvent);
                    }
                    break;
                default:
                    //not an interesting start nor an interesting end element
                    //so encrypt this
                    encryptEvent(xmlSecEvent);

                    //push all buffered encrypted character events through the chain
                    final Deque<XMLSecCharacters> charactersBuffer = characterEventGeneratorOutputStream.getCharactersBuffer();
                    if (charactersBuffer.size() > 5) {
                        OutputProcessorChain subOutputProcessorChain = outputProcessorChain.createSubChain(this);
                        Iterator<XMLSecCharacters> charactersIterator = charactersBuffer.iterator();
                        while (charactersIterator.hasNext()) {
                            XMLSecCharacters characters = charactersIterator.next();
                            outputAsEvent(subOutputProcessorChain, characters);
                            charactersIterator.remove();
                        }
                    }
                    break;
            }
        }

        private void encryptEvent(XMLSecEvent xmlSecEvent) throws XMLStreamException {
            xmlEventWriter.add(xmlSecEvent);
        }

        /**
         * Creates the Data structure around the cipher data
         */
        protected void processEventInternal(XMLSecStartElement xmlSecStartElement, OutputProcessorChain outputProcessorChain)
                throws XMLStreamException, XMLSecurityException {
            List<XMLSecAttribute> attributes = new ArrayList<XMLSecAttribute>(2);
            attributes.add(createAttribute(XMLSecurityConstants.ATT_NULL_Id, getEncryptionPartDef().getEncRefId()));
            attributes.add(createAttribute(XMLSecurityConstants.ATT_NULL_Type, getEncryptionPartDef().getModifier().getModifier()));
            createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_EncryptedData, true, attributes);

            attributes = new ArrayList<XMLSecAttribute>(1);
            attributes.add(createAttribute(XMLSecurityConstants.ATT_NULL_Algorithm, securityProperties.getEncryptionSymAlgorithm()));
            createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_EncryptionMethod, false, attributes);

            createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_EncryptionMethod);
            createKeyInfoStructure(outputProcessorChain);
            createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_CipherData, false, null);
            createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_CipherValue, false, null);

            /*
            <xenc:EncryptedData xmlns:xenc="http://www.w3.org/2001/04/xmlenc#" Id="EncDataId-1612925417"
                Type="http://www.w3.org/2001/04/xmlenc#Content">
                <xenc:EncryptionMethod xmlns:xenc="http://www.w3.org/2001/04/xmlenc#"
                    Algorithm="http://www.w3.org/2001/04/xmlenc#aes256-cbc" />
                <ds:KeyInfo xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
                    <wsse:SecurityTokenReference xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
                    <wsse:Reference xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"
                        URI="#EncKeyId-1483925398" />
                    </wsse:SecurityTokenReference>
                </ds:KeyInfo>
                <xenc:CipherData xmlns:xenc="http://www.w3.org/2001/04/xmlenc#">
                    <xenc:CipherValue xmlns:xenc="http://www.w3.org/2001/04/xmlenc#">
                    ...
                    </xenc:CipherValue>
                </xenc:CipherData>
            </xenc:EncryptedData>
             */
        }

        protected abstract void createKeyInfoStructure(OutputProcessorChain outputProcessorChain)
                throws XMLStreamException, XMLSecurityException;

        protected void doFinalInternal(OutputProcessorChain outputProcessorChain)
                throws XMLStreamException, XMLSecurityException {

            try {
                xmlEventWriter.add(wrapperEndElement);
                //close the event writer to flush all outstanding events to the encrypt stream
                xmlEventWriter.close();
                //call close to force a cipher.doFinal()
                cipherOutputStream.close();
            } catch (IOException e) {
                throw new XMLStreamException(e);
            }

            //push all buffered encrypted character events through the chain
            final Deque<XMLSecCharacters> charactersBuffer = characterEventGeneratorOutputStream.getCharactersBuffer();
            if (!charactersBuffer.isEmpty()) {
                Iterator<XMLSecCharacters> charactersIterator = charactersBuffer.iterator();
                while (charactersIterator.hasNext()) {
                    XMLSecCharacters characters = charactersIterator.next();
                    outputAsEvent(outputProcessorChain, characters);
                    charactersIterator.remove();
                }
            }

            createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_CipherValue);
            createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_CipherData);
            createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_xenc_EncryptedData);
        }

        protected EncryptionPartDef getEncryptionPartDef() {
            return encryptionPartDef;
        }

        protected void setEncryptionPartDef(EncryptionPartDef encryptionPartDef) {
            this.encryptionPartDef = encryptionPartDef;
        }

        protected XMLSecStartElement getXmlSecStartElement() {
            return xmlSecStartElement;
        }

        protected void setXmlSecStartElement(XMLSecStartElement xmlSecStartElement) {
            this.xmlSecStartElement = xmlSecStartElement;
        }

        public String getEncoding() {
            return encoding;
        }

        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }
    }

    /**
     * Creates Character-XMLEvents from the byte stream
     */
    public class CharacterEventGeneratorOutputStream extends OutputStream {

        private final Deque<XMLSecCharacters> charactersBuffer = new ArrayDeque<XMLSecCharacters>();

        public Deque<XMLSecCharacters> getCharactersBuffer() {
            return charactersBuffer;
        }

        @Override
        public void write(int b) throws IOException {
            charactersBuffer.offer(createCharacters(new char[]{(char)b}));
        }

        @Override
        public void write(byte[] b) throws IOException {
            charactersBuffer.offer(createCharacters(byteToCharArray(b, 0, b.length)));
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            charactersBuffer.offer(createCharacters(byteToCharArray(b, off, len)));
        }
    }

    private char[] byteToCharArray(byte[]  bytes, int off, int len) {
        char[] chars = new char[len];
        for (int i = off; i < len; i++) {
            chars[i] = (char)bytes[i];
        }
        return chars;
    }
}

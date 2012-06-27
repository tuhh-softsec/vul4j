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
package org.swssf.xmlsec.impl.processor.output;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.swssf.xmlsec.config.JCEAlgorithmMapper;
import org.swssf.xmlsec.config.TransformerAlgorithmMapper;
import org.swssf.xmlsec.ext.AbstractOutputProcessor;
import org.swssf.xmlsec.ext.OutputProcessorChain;
import org.swssf.xmlsec.ext.XMLSecurityConstants;
import org.swssf.xmlsec.ext.XMLSecurityException;
import org.swssf.xmlsec.ext.stax.*;
import org.swssf.xmlsec.impl.EncryptionPartDef;
import org.swssf.xmlsec.impl.util.TrimmerOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Processor to encrypt XML structures
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public abstract class AbstractEncryptOutputProcessor extends AbstractOutputProcessor {

    private static final XMLOutputFactory xmlOutputFactory;
    private static final XMLSecStartElement wrapperStartElement;
    private static final XMLSecEndElement wrapperEndElement;

    static {
        xmlOutputFactory = XMLOutputFactory.newInstance();
        xmlOutputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, false);
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
                throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, XMLStreamException {

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
            try {
                //initialize the cipher
                String jceAlgorithm = JCEAlgorithmMapper.translateURItoJCEID(securityProperties.getEncryptionSymAlgorithm());
                Cipher symmetricCipher = Cipher.getInstance(jceAlgorithm);

                //Should internally generate an IV
                symmetricCipher.init(Cipher.ENCRYPT_MODE, encryptionPartDef.getSymmetricKey());
                byte[] iv = symmetricCipher.getIV();

                characterEventGeneratorOutputStream = new CharacterEventGeneratorOutputStream(getEncoding());
                Base64OutputStream base64EncoderStream =
                        new Base64OutputStream(characterEventGeneratorOutputStream, true, 0, null);
                base64EncoderStream.write(iv);

                OutputStream outputStream = new CipherOutputStream(base64EncoderStream, symmetricCipher);

                String compressionAlgorithm = getSecurityProperties().getEncryptionCompressionAlgorithm();
                if (compressionAlgorithm != null) {
                    @SuppressWarnings("unchecked")
                    Class<OutputStream> transformerClass =
                            (Class<OutputStream>) TransformerAlgorithmMapper.getTransformerClass(compressionAlgorithm, "OUT");
                    Constructor<OutputStream> constructor = transformerClass.getConstructor(OutputStream.class);
                    outputStream = constructor.newInstance(outputStream);
                }
                //the trimmer output stream is needed to strip away the dummy wrapping element which must be added
                cipherOutputStream = new TrimmerOutputStream(outputStream, 1024, 3, 4);

                //we create a new StAX writer for optimized namespace writing.
                //spec says (4.2): "The cleartext octet sequence obtained in step 3 is interpreted as UTF-8 encoded character data."
                xmlEventWriter = xmlOutputFactory.createXMLEventWriter(new BufferedOutputStream(cipherOutputStream, 8192 * 5), "UTF-8");
                //we have to output a fake element to workaround text-only encryption:
                xmlEventWriter.add(wrapperStartElement);
            } catch (NoSuchPaddingException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_ENCRYPTION, e);
            } catch (NoSuchAlgorithmException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_ENCRYPTION, e);
            } catch (IOException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_ENCRYPTION, e);
            } catch (XMLStreamException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_ENCRYPTION, e);
            } catch (InvalidKeyException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILED_ENCRYPTION, e);
            } catch (InvocationTargetException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILURE, e);
            } catch (NoSuchMethodException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILURE, e);
            } catch (InstantiationException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILURE, e);
            } catch (IllegalAccessException e) {
                throw new XMLSecurityException(XMLSecurityException.ErrorCode.FAILURE, e);
            }

            super.init(outputProcessorChain);
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
                    final List<XMLSecCharacters> charactersBuffer = characterEventGeneratorOutputStream.getCharactersBuffer();
                    if (!charactersBuffer.isEmpty()) {
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
            createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_KeyInfo, true, null);
            createKeyInfoStructure(outputProcessorChain);
            createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_KeyInfo);
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
            final List<XMLSecCharacters> charactersBuffer = characterEventGeneratorOutputStream.getCharactersBuffer();
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

        private final List<XMLSecCharacters> charactersBuffer = new ArrayList<XMLSecCharacters>();
        private final String encoding;

        public CharacterEventGeneratorOutputStream(String encoding) {
            this.encoding = encoding;
        }

        public List<XMLSecCharacters> getCharactersBuffer() {
            return charactersBuffer;
        }

        @Override
        public void write(int b) throws IOException {
            charactersBuffer.add(createCharacters(new String(new byte[]{((byte) b)}, encoding)));
        }

        @Override
        public void write(byte[] b) throws IOException {
            charactersBuffer.add(createCharacters(new String(b, encoding)));
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            charactersBuffer.add(createCharacters(new String(b, off, len, encoding)));
        }
    }
}

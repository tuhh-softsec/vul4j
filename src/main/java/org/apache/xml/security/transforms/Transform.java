/*
 * Copyright 1999-2009 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.xml.security.transforms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.exceptions.AlgorithmAlreadyRegisteredException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.ClassLoaderUtils;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.HelperNodeList;
import org.apache.xml.security.utils.SignatureElementProxy;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Implements the behaviour of the <code>ds:Transform</code> element.
 *
 * This <code>Transform</code>(Factory) class acts as the Factory and Proxy of
 * the implementing class that supports the functionality of <a
 * href=http://www.w3.org/TR/xmldsig-core/#sec-TransformAlg>a Transform
 * algorithm</a>.
 * Implements the Factory and Proxy pattern for ds:Transform algorithms.
 *
 * @author Christian Geuer-Pollmann
 * @see Transforms
 * @see TransformSpi
 */
public final class Transform extends SignatureElementProxy {

    /** {@link org.apache.commons.logging} logging facility */
    private static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(Transform.class.getName());

    /** All available Transform classes are registered here */
    private static Map<String, TransformSpi> transformSpiHash = 
        new ConcurrentHashMap<String, TransformSpi>();
    
    private TransformSpi transformSpi;

    /**
     * Constructs {@link Transform}
     *
     * @param doc the {@link Document} in which <code>Transform</code> will be 
     * placed
     * @param algorithmURI URI representation of 
     * <code>Transform algorithm</code> which will be specified as parameter of 
     * {@link #getInstance(Document, String)}, when generated. </br>
     * @param contextNodes the child node list of <code>Transform</code> element
     * @throws InvalidTransformException
     */
    public Transform(Document doc, String algorithmURI, NodeList contextNodes)
        throws InvalidTransformException {
        super(doc);

        this.constructionElement.setAttributeNS(null, Constants._ATT_ALGORITHM, algorithmURI);

        transformSpi = transformSpiHash.get(algorithmURI);
        if (transformSpi == null) {
            Object exArgs[] = { algorithmURI };
            throw new InvalidTransformException("signature.Transform.UnknownTransform", exArgs);
        }

        if (log.isDebugEnabled()) {
            log.debug("Create URI \"" + algorithmURI + "\" class \""
                   + transformSpi.getClass() + "\"");
            log.debug("The NodeList is " + contextNodes);
        }

        // give it to the current document
        if (contextNodes != null) {
            for (int i = 0; i < contextNodes.getLength(); i++) {
                this.constructionElement.appendChild(contextNodes.item(i).cloneNode(true));
            }
         }
    }

    /**
     * @param element <code>ds:Transform</code> element
     * @param BaseURI the URI of the resource where the XML instance was stored
     * @throws InvalidTransformException
     * @throws TransformationException
     * @throws XMLSecurityException
     */
    public Transform(Element element, String BaseURI)
        throws InvalidTransformException, TransformationException, XMLSecurityException {
        super(element, BaseURI);

        // retrieve Algorithm Attribute from ds:Transform
        String algorithmURI = element.getAttributeNS(null, Constants._ATT_ALGORITHM);

        if (algorithmURI == null || algorithmURI.length() == 0) {
            Object exArgs[] = { Constants._ATT_ALGORITHM, Constants._TAG_TRANSFORM };
            throw new TransformationException("xml.WrongContent", exArgs);
        }

     
        transformSpi = transformSpiHash.get(algorithmURI);
        if (transformSpi == null) {
            Object exArgs[] = { algorithmURI };
            throw new InvalidTransformException(
                "signature.Transform.UnknownTransform", exArgs);
        }
    }

    /**
     * Generates a Transform object that implements the specified 
     * <code>Transform algorithm</code> URI.
     *
     * @param algorithmURI <code>Transform algorithm</code> URI representation, 
     * such as specified in 
     * <a href=http://www.w3.org/TR/xmldsig-core/#sec-TransformAlg>Transform algorithm </a>
     * @param doc the proxy {@link Document}
     * @return <code>{@link Transform}</code> object
     * @throws InvalidTransformException
     */
    public static Transform getInstance(
        Document doc, String algorithmURI
    ) throws InvalidTransformException {
        return getInstance(doc, algorithmURI, (NodeList) null);
    }

    /**
     * Generates a Transform object that implements the specified 
     * <code>Transform algorithm</code> URI.
     *
     * @param algorithmURI <code>Transform algorithm</code> URI representation, 
     * such as specified in 
     * <a href=http://www.w3.org/TR/xmldsig-core/#sec-TransformAlg>Transform algorithm </a>
     * @param contextChild the child element of <code>Transform</code> element
     * @param doc the proxy {@link Document}
     * @return <code>{@link Transform}</code> object
     * @throws InvalidTransformException
     */
    public static Transform getInstance(
        Document doc, String algorithmURI, Element contextChild
    ) throws InvalidTransformException {

        HelperNodeList contextNodes = null;
        
        if (contextChild != null) {
            contextNodes = new HelperNodeList();
    
            XMLUtils.addReturnToElement(doc, contextNodes);
            contextNodes.appendChild(contextChild);
            XMLUtils.addReturnToElement(doc, contextNodes);
        }

        return getInstance(doc, algorithmURI, contextNodes);
    }

    /**
     * Generates a Transform object that implements the specified 
     * <code>Transform algorithm</code> URI.
     *
     * @param algorithmURI <code>Transform algorithm</code> URI form, such as 
     * specified in <a href=http://www.w3.org/TR/xmldsig-core/#sec-TransformAlg>
     * Transform algorithm </a>
     * @param contextNodes the child node list of <code>Transform</code> element
     * @param doc the proxy {@link Document}
     * @return <code>{@link Transform}</code> object
     * @throws InvalidTransformException
     */
    public static Transform getInstance(
        Document doc, String algorithmURI, NodeList contextNodes
    ) throws InvalidTransformException {
        return new Transform(doc, algorithmURI, contextNodes);
    }

    /**
     * Registers implementing class of the Transform algorithm with algorithmURI
     *
     * @param algorithmURI algorithmURI URI representation of 
     * <code>Transform algorithm</code> will be specified as parameter of 
     * {@link #getInstance(Document, String)}, when generate. </br>
     * @param implementingClass <code>implementingClass</code> the implementing 
     * class of {@link TransformSpi}
     * @throws AlgorithmAlreadyRegisteredException if specified algorithmURI 
     * is already registered
     */
    @SuppressWarnings("unchecked")
    public static void register(String algorithmURI, String implementingClass)
        throws AlgorithmAlreadyRegisteredException, InvalidTransformException {
        // are we already registered?
        TransformSpi transformSpi = transformSpiHash.get(algorithmURI);
        if ((transformSpi != null) ) {
            Object exArgs[] = { algorithmURI, transformSpi.getClass() };
            throw new AlgorithmAlreadyRegisteredException(
               "algorithm.alreadyRegistered", exArgs);
        }
        try {
            Class<TransformSpi> transformSpiClass = 
                (Class<TransformSpi>)ClassLoaderUtils.loadClass(implementingClass, Transform.class);
            transformSpiHash.put(algorithmURI, transformSpiClass.newInstance());
        } catch (InstantiationException ex) {
            Object exArgs[] = { algorithmURI };
            throw new InvalidTransformException(
                "signature.Transform.UnknownTransform", exArgs, ex
            );
        } catch (IllegalAccessException ex) {
            Object exArgs[] = { algorithmURI };
            throw new InvalidTransformException(
                "signature.Transform.UnknownTransform", exArgs, ex
            );
        } catch (ClassNotFoundException ex) {
            Object exArgs[] = { algorithmURI };
            throw new InvalidTransformException(
                "signature.Transform.UnknownTransform", exArgs, ex
            );
        } 
    }

    /**
     * Returns the URI representation of Transformation algorithm
     *
     * @return the URI representation of Transformation algorithm
     */
    public String getURI() {
        return this.constructionElement.getAttributeNS(null, Constants._ATT_ALGORITHM);
    }

    /**
     * Transforms the input, and generates {@link XMLSignatureInput} as output.
     *
     * @param input input {@link XMLSignatureInput} which can supplied Octet 
     * Stream and NodeSet as Input of Transformation
     * @return the {@link XMLSignatureInput} class as the result of 
     * transformation
     * @throws CanonicalizationException
     * @throws IOException
     * @throws InvalidCanonicalizerException
     * @throws TransformationException
     */
    public XMLSignatureInput performTransform(XMLSignatureInput input)
        throws IOException, CanonicalizationException,
               InvalidCanonicalizerException, TransformationException {

        XMLSignatureInput result = null;

        try {
            result = transformSpi.enginePerformTransform(input, this);
        } catch (ParserConfigurationException ex) {
            Object exArgs[] = { this.getURI(), "ParserConfigurationException" };
            throw new CanonicalizationException(
                "signature.Transform.ErrorDuringTransform", exArgs, ex);
        } catch (SAXException ex) {
            Object exArgs[] = { this.getURI(), "SAXException" };
            throw new CanonicalizationException(
                "signature.Transform.ErrorDuringTransform", exArgs, ex);
        }

        return result;
    }
   
    /**
     * Transforms the input, and generates {@link XMLSignatureInput} as output.
     *
     * @param input input {@link XMLSignatureInput} which can supplied Octect 
     * Stream and NodeSet as Input of Transformation
     * @param os where to output the result of the last transformation
     * @return the {@link XMLSignatureInput} class as the result of 
     * transformation
     * @throws CanonicalizationException
     * @throws IOException
     * @throws InvalidCanonicalizerException
     * @throws TransformationException
     */
    public XMLSignatureInput performTransform(XMLSignatureInput input, 
        OutputStream os
    ) throws IOException, CanonicalizationException,
        InvalidCanonicalizerException, TransformationException {
        XMLSignatureInput result = null;

        try {
            result = transformSpi.enginePerformTransform(input, os, this);
        } catch (ParserConfigurationException ex) {
            Object exArgs[] = { this.getURI(), "ParserConfigurationException" };
            throw new CanonicalizationException(
                "signature.Transform.ErrorDuringTransform", exArgs, ex);
        } catch (SAXException ex) {
            Object exArgs[] = { this.getURI(), "SAXException" };
            throw new CanonicalizationException(
                "signature.Transform.ErrorDuringTransform", exArgs, ex);
        }

        return result;
    }

    /** @inheritDoc */
    public String getBaseLocalName() {
        return Constants._TAG_TRANSFORM;
    }
}

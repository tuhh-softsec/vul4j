/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "<WebSig>" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001, Institute for
 * Data Communications Systems, <http://www.nue.et-inf.uni-siegen.de/>.
 * The development of this software was partly funded by the European
 * Commission in the <WebSig> project in the ISIS Programme.
 * For more information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xml.security.transforms;



import java.util.Iterator;
import java.util.HashMap;
import java.io.InputStream;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import org.apache.xml.security.c14n.*;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.exceptions.*;
import org.apache.xml.security.utils.*;


/**
 * Implements the behaviour of the <code>ds:Transform</code> element.
 *
 * This <code>Transform</code>(Factory) class role as the Factory and Proxy of
 * implemanting class that have the functionality of <a
 * href=http://www.w3.org/TR/xmldsig-core/#sec-TransformAlg>a Transform
 * algorithm</a>.
 * Implements the Factory and Proxy pattern for ds:Transform algorithms.
 *
 * @author Christian Geuer-Pollmann
 * @see Transforms
 * @see TransformSpi
 * @see c14.Canonicalizer
 *
 */
public final class Transform extends SignatureElementProxy {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(Transform.class.getName());

   /** Field _alreadyInitialized */
   static boolean _alreadyInitialized = false;

   /** All available Transform classes are registered here */
   static HashMap _transformHash = null;

   /** Field transformSpi */
   protected TransformSpi transformSpi = null;

   /**
    * Constructs {@link Transform}
    *
    * @param doc the {@link Document} in which <code>Transform</code> will be placed
    * @param algorithmURI URI representation of <code>transfrom algorithm</code> will be specified as parameter of {@link #getInstance}, when generate. </br>
    * @param contextNodes the child node list of <code>Transform</code> element
    * @throws InvalidTransformException
    */
   public Transform(Document doc, String algorithmURI, NodeList contextNodes)
           throws InvalidTransformException {

      super(doc);

      try {
         this._constructionElement.setAttributeNS(null, Constants._ATT_ALGORITHM,
                                                algorithmURI);

         String implementingClass =
            Transform.getImplementingClass(algorithmURI);

         cat.debug("Create URI \"" + algorithmURI + "\" class \""
                   + implementingClass + "\"");
         cat.debug("The NodeList is " + contextNodes);

         // create the custom Transform object
         this.transformSpi =
            (TransformSpi) Class.forName(implementingClass).newInstance();

         this.transformSpi.setTransform(this);

         // give it to the current document
         if (contextNodes != null) {
            /*
            while (contextNodes.getLength() > 0) {
               this._constructionElement.appendChild(contextNodes.item(0));
            }
            */

            for (int i = 0; i < contextNodes.getLength(); i++) {
               this._constructionElement.appendChild(contextNodes.item(i).cloneNode(true));
            }

         }
      } catch (ClassNotFoundException ex) {
         Object exArgs[] = { algorithmURI };

         throw new InvalidTransformException(
            "signature.Transform.UnknownTransform", exArgs, ex);
      } catch (IllegalAccessException ex) {
         Object exArgs[] = { algorithmURI };

         throw new InvalidTransformException(
            "signature.Transform.UnknownTransform", exArgs, ex);
      } catch (InstantiationException ex) {
         Object exArgs[] = { algorithmURI };

         throw new InvalidTransformException(
            "signature.Transform.UnknownTransform", exArgs, ex);
      }
   }

   /**
    * This constructor can only be called from the {@link Transforms} object, so
    * it's protected.
    *
    * @param element <code>ds:Transform</code> element
    * @param BaseURI the URI of the resource where the XML instance was stored
    * @throws InvalidTransformException
    * @throws TransformationException
    * @throws XMLSecurityException
    */
   public Transform(Element element, String BaseURI)
           throws InvalidTransformException, TransformationException,
                  XMLSecurityException {

      super(element, BaseURI);

      // retrieve Algorithm Attribute from ds:Transform
      String AlgorithmURI = element.getAttributeNS(null, Constants._ATT_ALGORITHM);

      if ((AlgorithmURI == null) || (AlgorithmURI.length() == 0)) {
         Object exArgs[] = { Constants._ATT_ALGORITHM,
                             Constants._TAG_TRANSFORM };

         throw new TransformationException("xml.WrongContent", exArgs);
      }

      try {
         String implementingClass = (String) _transformHash.get(AlgorithmURI);

         this.transformSpi =
            (TransformSpi) Class.forName(implementingClass).newInstance();

         this.transformSpi.setTransform(this);
      } catch (ClassNotFoundException e) {
         Object exArgs[] = { AlgorithmURI };

         throw new InvalidTransformException(
            "signature.Transform.UnknownTransform", exArgs);
      } catch (IllegalAccessException e) {
         Object exArgs[] = { AlgorithmURI };

         throw new InvalidTransformException(
            "signature.Transform.UnknownTransform", exArgs);
      } catch (InstantiationException e) {
         Object exArgs[] = { AlgorithmURI };

         throw new InvalidTransformException(
            "signature.Transform.UnknownTransform", exArgs);
      }
   }

   /**
    * Generates a Transform object that implements the specified <code>Transform algorithm</code> URI.
    *
    * @param algorithmURI <code>Transform algorithm</code> URI representation, such as specified in <a href=http://www.w3.org/TR/xmldsig-core/#sec-TransformAlg>Transform algorithm </a>
    * @param doc the proxy {@link documenet}
    * @return <code>{@link Transfrom}</code> object
    * @throws InvalidTransformException
    */
   public static final Transform getInstance(
           Document doc, String algorithmURI) throws InvalidTransformException {
      return Transform.getInstance(doc, algorithmURI, (NodeList) null);
   }

   /**
    * Generates a Transform object that implements the specified <code>Transform algorithm</code> URI.
    *
    * @param algorithmURI <code>Transform algorithm</code> URI representation, such as specified in <a href=http://www.w3.org/TR/xmldsig-core/#sec-TransformAlg>Transform algorithm </a>
    * @param contextChild the child element of <code>Transform</code> element
    * @param doc the proxy {@link documenet}
    * @return <code>{@link Transfrom}</code> object
    * @throws InvalidTransformException
    */
   public static final Transform getInstance(
           Document doc, String algorithmURI, Element contextChild)
              throws InvalidTransformException {

      HelperNodeList contextNodes = new HelperNodeList();

      contextNodes.appendChild(doc.createTextNode("\n"));
      contextNodes.appendChild(contextChild);
      contextNodes.appendChild(doc.createTextNode("\n"));

      return Transform.getInstance(doc, algorithmURI, contextNodes);
   }

   /**
    * Generates a Transform object that implements the specified <code>Transform algorithm</code> URI.
    *
    * @param algorithmURI <code>Transform algorithm</code> URI form, such as specified in <a href=http://www.w3.org/TR/xmldsig-core/#sec-TransformAlg>Transform algorithm </a>
    * @param contextNodes the child node list of <code>Transform</code> element
    * @param doc the proxy {@link documenet}
    * @return <code>{@link Transfrom}</code> object
    * @throws InvalidTransformException
    */
   public static final Transform getInstance(
           Document doc, String algorithmURI, NodeList contextNodes)
              throws InvalidTransformException {
      return new Transform(doc, algorithmURI, contextNodes);
   }

   /**
    * Initalizes for this {@link Transform}
    *
    */
   public static void init() {

      if (!_alreadyInitialized) {
         _transformHash = new HashMap(10);
         _alreadyInitialized = true;
      }
   }

   /**
    * Registers implementing class of the transfrom algorithm with algorithmURI
    *
    * @param algorithmURI algorithmURI URI representation of <code>transfrom algorithm</code> will be specified as parameter of {@link #getInstance}, when generate. </br>
    * @param implementingClass <code>implementingClass</code> the implementing class of {@link TransformSpi}
    * @throws AlgorithmAlreadyRegisteredException if specified algorithmURI is already registered
    */
   public static void register(String algorithmURI, String implementingClass)
           throws AlgorithmAlreadyRegisteredException {

      {

         // are we already registered?
         String registeredClass = Transform.getImplementingClass(algorithmURI);

         if ((registeredClass != null) && (registeredClass.length() != 0)) {
            Object exArgs[] = { algorithmURI, registeredClass };

            throw new AlgorithmAlreadyRegisteredException(
               "algorithm.alreadyRegistered", exArgs);
         }

         Transform._transformHash.put(algorithmURI, implementingClass);
      }
   }

   /**
    * Returns the URI representation of Transformation algorithm
    *
    * @return the URI representation of Transformation algorithm
    */
   public final String getURI() {
      return this._constructionElement.getAttributeNS(null, Constants._ATT_ALGORITHM);
   }

   /**
    * Transforms the input, and generats {@link XMLSignatureInput} as output.
    *
    * @param input input {@link XMLSignatureInput} which can supplied Octect Stream and NodeSet as Input of Transformation
    * @return the {@link XMLSignatureInput} class as the result of transformation
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
         result = transformSpi.enginePerformTransform(input);
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
    * Method getImplementingClass
    *
    * @param URI
    * @return
    */
   private static String getImplementingClass(String URI) {

      try {
         Iterator i = Transform._transformHash.keySet().iterator();

         while (i.hasNext()) {
            String key = (String) i.next();

            if (key.equals(URI)) {
               return (String) Transform._transformHash.get(key);
            }
         }
      } catch (NullPointerException ex) {}

      return null;
   }

   public String getBaseLocalName() {
      return Constants._TAG_TRANSFORM;
   }
}

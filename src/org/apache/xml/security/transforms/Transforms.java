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



import java.io.*;
import java.lang.IllegalArgumentException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Vector;
import org.apache.xpath.XPathAPI;
import javax.xml.transform.TransformerException;
import org.w3c.dom.*;
import org.apache.xml.security.c14n.*;
import org.apache.xml.security.exceptions.*;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.*;
import org.apache.xml.security.utils.*;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;


/**
 * Holder of the {@link org.apache.xml.security.transforms.Transform} steps to * Holder of the {@link org.apache.xml.security.transforms.Transform} steps to be performed on the data.
 * The input to the first Transform is the result of dereferencing the <code>URI</code> attribute of the <code>Reference</code> element.
 * The output from the last Transform is the input for the <code>DigestMethod algorithm</code>
 *
 * @author: Christian Geuer-Pollmann
 * @see Transform
 * @see signature.Reference
 */
public class Transforms extends SignatureElementProxy {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(Transforms.class.getName());
   //J-
   /** Canonicalization - Required Canonical XML (omits comments) */
   public static final String TRANSFORM_C14N_OMIT_COMMENTS = Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS;
   /** Canonicalization - Recommended Canonical XML with Comments */
   public static final String TRANSFORM_C14N_WITH_COMMENTS = Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS;
   /** Canonicalization - Required Exclusive Canonicalization (omits comments) */
   public static final String TRANSFORM_C14N_EXCL_OMIT_COMMENTS = Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS;
   /** Canonicalization - Recommended Exclusive Canonicalization with Comments */
   public static final String TRANSFORM_C14N_EXCL_WITH_COMMENTS = Canonicalizer.ALGO_ID_C14N_EXCL_WITH_COMMENTS;
   /** Transform - Optional XSLT */
   public static final String TRANSFORM_XSLT = "http://www.w3.org/TR/1999/REC-xslt-19991116";
   /** Transform - Required base64 decoding */
   public static final String TRANSFORM_BASE64_DECODE = Constants.SignatureSpecNS + "base64";
   /** Transform - Recommended XPath */
   public static final String TRANSFORM_XPATH = "http://www.w3.org/TR/1999/REC-xpath-19991116";
   /** Transform - Required Enveloped Signature */
   public static final String TRANSFORM_ENVELOPED_SIGNATURE = Constants.SignatureSpecNS + "enveloped-signature";
   /** Transform - XPointer */
   public static final String TRANSFORM_XPOINTER = "http://www.w3.org/TR/2001/WD-xptr-20010108";
   /** Transform - XPath Filter v2.0 */
   public static final String TRANSFORM_XPATH2FILTER04 = "http://www.w3.org/2002/04/xmldsig-filter2";
   public static final String TRANSFORM_XPATH2FILTER = "http://www.w3.org/2002/06/xmldsig-filter2";
   public static final String TRANSFORM_XPATHFILTERCHGP = "http://www.nue.et-inf.uni-siegen.de/~geuer-pollmann/#xpathFilter";
   //J+

   /**
    * Consturcts {@link Transforms}
    *
    * @param doc the {@link Document} in which <code>XMLsignature</code> will be placed
    */
   public Transforms(Document doc) {

      super(doc);

      XMLUtils.addReturnToElement(this._constructionElement);
   }

   /**
    * Consturcts {@link Transforms} from {@link Element} which is <code>Transforms</code> Element
    *
    * @param element  is <code>Transforms</code> element
    * @param BaseURI the URI where the XML instance was stored
    * @throws DOMException
    * @throws InvalidTransformException
    * @throws TransformationException
    * @throws XMLSecurityException
    * @throws XMLSignatureException
    */
   public Transforms(Element element, String BaseURI)
           throws DOMException, XMLSignatureException,
                  InvalidTransformException, TransformationException,
                  XMLSecurityException {

      super(element, BaseURI);

      int numberOfTransformElems = this.getLength();

      if (numberOfTransformElems == 0) {

         // At least ont Transform element must be present. Bad.
         Object exArgs[] = { Constants._TAG_TRANSFORM,
                             Constants._TAG_TRANSFORMS };

         throw new TransformationException("xml.WrongContent", exArgs);
      }
   }

   /**
    * Adds the <code>Transform</code> with the specified <code>Transform algorithm URI</code>
    *
    * @param transformURI the URI form of transform that indicates which transformation is applied to data
    * @throws TransformationException
    */
   public void addTransform(String transformURI)
           throws TransformationException {

      try {
         cat.debug("Transforms.addTransform(" + transformURI + ")");

         Transform transform = Transform.getInstance(this._doc, transformURI);

         this.addTransform(transform);
      } catch (InvalidTransformException ex) {
         throw new TransformationException("empty", ex);
      }
   }

   /**
    * Adds the <code>Transform</code> with the specified <code>Transform algorithm URI</code>
    *
    * @param transformURI the URI form of transform that indicates which transformation is applied to data
    * @param contextElement
    * @throws TransformationException
    * @see Transform#getInstance(Document doc, String algorithmURI, Element childElement)
    */
   public void addTransform(String transformURI, Element contextElement)
           throws TransformationException {

      try {
         cat.debug("Transforms.addTransform(" + transformURI + ")");

         Transform transform = Transform.getInstance(this._doc, transformURI,
                                                     contextElement);

         this.addTransform(transform);
      } catch (InvalidTransformException ex) {
         throw new TransformationException("empty", ex);
      }
   }

   /**
    * Adds the <code>Transform</code> with the specified <code>Transform algorithm URI</code>
    *
    * @param transformURI the URI form of transform that indicates which transformation is applied to data
    * @param contextNodes
    * @throws TransformationException
    * @see Transform#getInstance(Document doc, String algorithmURI, NodeList contextNodes)
    */
   public void addTransform(String transformURI, NodeList contextNodes)
           throws TransformationException {

      try {
         Transform transform = Transform.getInstance(this._doc, transformURI,
                                                     contextNodes);

         this.addTransform(transform);
      } catch (InvalidTransformException ex) {
         throw new TransformationException("empty", ex);
      }
   }

   /**
    * Adds a user-provided Transform step.
    *
    * @param transform {@link Transform} object
    */
   private void addTransform(Transform transform) {

      cat.debug("Transforms.addTransform(" + transform.getURI() + ")");

      Element transformElement = transform.getElement();

      this._constructionElement.appendChild(transformElement);
      XMLUtils.addReturnToElement(this._constructionElement);
   }

   /**
    * Applies all included <code>Transform</code>s to xmlSignatureInput and returns the result of these transformations.
    *
    * @param xmlSignatureInput the input for the <code>Transform</code>s
    * @return the result of the <code>Transforms</code>
    * @throws TransformationException
    */
   public XMLSignatureInput performTransforms(
           XMLSignatureInput xmlSignatureInput) throws TransformationException {

      try {
         for (int i = 0; i < this.getLength(); i++) {
            Transform t = this.item(i);

            cat.debug("Preform the (" + i + ")th " + t.getURI() + " transform");

            xmlSignatureInput = t.performTransform(xmlSignatureInput);
         }

         /*
         // if the final result is a node set, we must c14nize
         if (xmlSignatureInput.isNodeSet()) {
            Canonicalizer c14n = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS);
            NodeList nodes = xmlSignatureInput.getNodeSet();
            byte[] bytes = c14n.canonicalizeXPathNodeSet(nodes);
            xmlSignatureInput = new XMLSignatureInput(bytes);
         }
         */

         return xmlSignatureInput;
      } catch (IOException ex) {
         throw new TransformationException("empty", ex);
      // } catch (ParserConfigurationException ex) { throw new TransformationException("empty", ex);
      // } catch (SAXException ex) { throw new TransformationException("empty", ex);
      } catch (CanonicalizationException ex) {
         throw new TransformationException("empty", ex);
      } catch (InvalidCanonicalizerException ex) {
         throw new TransformationException("empty", ex);
      }
   }

   /**
    * Return the nonnegative number of transformations.
    *
    * @return the number of transformations
    * @throws TransformationException
    */
   public int getLength() throws TransformationException {

      try {
         Element nscontext = XMLUtils.createDSctx(this._doc, "ds",
                                                  Constants.SignatureSpecNS);
         NodeList transformElems =
            XPathAPI.selectNodeList(this._constructionElement,
                                    "./ds:Transform", nscontext);

         return transformElems.getLength();
      } catch (TransformerException ex) {
         throw new TransformationException("empty", ex);
      }
   }

   /**
    * Return the <it>i</it><sup>th</sup> <code>{@link Transform}</code>.
    * Valid <code>i</code> values are 0 to <code>{@link #getLength}-1</code>.
    *
    * @param i index of {@link Transform} to return
    * @return the <it>i</it><sup>th</sup> transforms
    * @throws TransformationException
    */
   public Transform item(int i) throws TransformationException {

      try {
         Element nscontext = XMLUtils.createDSctx(this._doc, "ds",
                                                  Constants.SignatureSpecNS);
         Element transformElem =
            (Element) XPathAPI.selectSingleNode(this._constructionElement,
                                                "./ds:"
                                                + Constants._TAG_TRANSFORM
                                                + "[" + (i + 1) + "]",
                                                nscontext);

         if (transformElem == null) {
            return null;
         } else {
            return new Transform(transformElem, this._baseURI);
         }
      } catch (TransformerException ex) {
         throw new TransformationException("empty", ex);
      } catch (XMLSecurityException ex) {
         throw new TransformationException("empty", ex);
      }
   }

   public String getBaseLocalName() {
      return Constants._TAG_TRANSFORMS;
   }
}

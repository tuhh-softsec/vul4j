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
package org.apache.xml.security.encryption;



import java.io.IOException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.*;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.transforms.InvalidTransformException;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.EncryptionElementProxy;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.EncryptionConstants;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xpath.CachedXPathAPI;


/**
 * This class maps to the <CODE><B>xenc</B>:ReferenceList</CODE> element. NOTE:
 * this is physically the same as a {@link org.apache.xml.security.transforms.Transforms},
 * but has different semantics. Using <CODE>ds:Transforms</CODE>, signer and
 * verifier perform the same operations on the data. Using <CODE>xenc:Transforms</CODE>,
 * encryptor and decryptor perform opposite operations.
 *
 * @author $Author$
 */
public class Transforms extends EncryptionElementProxy {

   /**
    * Constructor Transforms
    *
    * @param doc
    */
   public Transforms(Document doc) {

      super(doc, EncryptionConstants._TAG_TRANSFORMS);

      this._constructionElement.appendChild(this._doc.createTextNode("\n"));
   }

   /**
    * Consturcts {@link Transforms} from {@link Element} which is <code>Transforms</code> Element
    *
    * @param element  is <code>Transforms</code> element
    * @param BaseURI the URI where the XML instance was stored
    * @throws XMLSecurityException
    */
   public Transforms(Element element, String BaseURI)
           throws XMLSecurityException {

      super(element, BaseURI, EncryptionConstants._TAG_TRANSFORMS);

      int numberOfTransformElems = this.getLength();

      if (numberOfTransformElems == 0) {

         // At least ont Transform element must be present. Bad.
         Object exArgs[] = { "ds:" + Constants._TAG_TRANSFORM,
                             "xenc:" + EncryptionConstants._TAG_TRANSFORMS };

         throw new XMLSecurityException("xml.WrongContent", exArgs);
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

      Element transformElement = transform.getElement();

      this._constructionElement.appendChild(transformElement);
      this._constructionElement.appendChild(this._doc.createTextNode("\n"));
   }

   /**
    * Applies all included <code>Transform</code>s to xmlSignatureInput and returns the result of these transformations.
    *
    * @param xmlSignatureInput the input for the <code>Transform</code>s
    * @return the result of the <code>Transforms</code>
    * @throws TransformationException
    */
   public XMLSignatureInput performDecryptionTransforms(
           XMLSignatureInput xmlSignatureInput) throws TransformationException {

      try {
         for (int i = 0; i < this.getLength(); i++) {
            Transform t = this.item(i);

            xmlSignatureInput = t.performTransform(xmlSignatureInput);
         }

         return xmlSignatureInput;
      } catch (IOException ex) {
         throw new TransformationException("empty", ex);
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
         CachedXPathAPI xpathAPI = new CachedXPathAPI();
         NodeList transformElems =
            xpathAPI.selectNodeList(this._constructionElement,
                                    "./ds:" + Constants._TAG_TRANSFORM + "",
                                    nscontext);

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
   private Transform item(int i) throws TransformationException {

      try {
         Element nscontext = XMLUtils.createDSctx(this._doc, "ds",
                                                  Constants.SignatureSpecNS);
         CachedXPathAPI xpathAPI = new CachedXPathAPI();
         Element transformElem =
            (Element) xpathAPI.selectSingleNode(this._constructionElement,
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
}

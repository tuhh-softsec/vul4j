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
package org.apache.xml.security.signature;



import java.io.*;
import java.math.BigInteger;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.SerializerFactory;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xml.utils.PrefixResolverDefault;
import org.apache.xpath.NodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathAPI;
import org.apache.xpath.XPathContext;
import org.w3c.dom.*;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.c14n.*;
import org.apache.xml.security.c14n.helper.XPathContainer;
import org.apache.xml.security.exceptions.*;
import org.apache.xml.security.signature.*;
import org.apache.xml.security.transforms.*;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.utils.resolver.*;
import javax.xml.transform.TransformerException;


/**
 * Handles <code>&lt;ds:Reference&gt;</code> elements.
 *
 * This includes:
 *
 * Constuct a <CODE>ds:Reference</CODE> from an {@link org.w3c.dom.Element}.
 *
 * <p>Create a new reference</p>
 * <pre>
 * Document _doc;
 * MessageDigestAlgorithm sha1 = MessageDigestAlgorithm.getInstance("http://#sha1");
 * Reference ref = new Reference(new XMLSignatureInput(new FileInputStream("1.gif"),
 *                               "http://localhost/1.gif",
 *                               (Transforms) null, sha1);
 * Element refElem = ref.toElement(_doc);
 * </pre>
 *
 * <p>Verify a reference</p>
 * <pre>
 * Element refElem = _doc.getElement("Reference"); // PSEUDO
 * Reference ref = new Reference(refElem);
 * String url = ref.getURI();
 * ref.setData(new XMLSignatureInput(new FileInputStream(url)));
 * if (ref.verify()) {
 *    System.out.println("verified");
 * }
 * </pre>
 *
 * <pre>
 * &lt;element name="Reference" type="ds:ReferenceType"/&gt;
 *  &lt;complexType name="ReferenceType"&gt;
 *    &lt;sequence&gt;
 *      &lt;element ref="ds:Transforms" minOccurs="0"/&gt;
 *      &lt;element ref="ds:DigestMethod"/&gt;
 *      &lt;element ref="ds:DigestValue"/&gt;
 *    &lt;/sequence&gt;
 *    &lt;attribute name="Id" type="ID" use="optional"/&gt;
 *    &lt;attribute name="URI" type="anyURI" use="optional"/&gt;
 *    &lt;attribute name="Type" type="anyURI" use="optional"/&gt;
 *  &lt;/complexType&gt;
 * </pre>
 *
 * @author Christian Geuer-Pollmann
 * @see ObjectContainer
 * @see Manifest
 */
public class Reference extends SignatureElementProxy {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(Reference.class.getName());

   /** Field OBJECT_URI */
   public static final String OBJECT_URI = Constants.SignatureSpecNS
                                           + Constants._TAG_OBJECT;

   /** Field MANIFEST_URI */
   public static final String MANIFEST_URI = Constants.SignatureSpecNS
                                             + Constants._TAG_MANIFEST;
   //J-
   Manifest _manifest = null;
   XMLSignatureInput _transformsInput;
   XMLSignatureInput _transformsOutput;
   //J+

   /**
    * Constructor Reference
    *
    * @param doc the {@link Document} in which <code>XMLsignature</code> is placed
    * @param BaseURI the URI of the resource where the XML instance will be stored
    * @param ReferenceURI URI indicate where is data which will digested
    * @param manifest
    * @param transforms {@link transforms} applied to data
    * @param messageDigestAlgorithm {@link algorithms.MessageDigestAlgorithm Digest algorithm} which is applied to the data
    * @todo should we throw XMLSignatureException if MessageDigestAlgoURI is wrong?
    * @throws XMLSignatureException
    */
   protected Reference(
           Document doc, String BaseURI, String ReferenceURI, Manifest manifest, Transforms transforms, String messageDigestAlgorithm)
              throws XMLSignatureException {

      super(doc);

      XMLUtils.addReturnToElement(this._constructionElement);

      this._baseURI = BaseURI;
      this._manifest = manifest;

      this.setURI(ReferenceURI);

      // important: The ds:Reference must be added to the associated ds:Manifest
      //            or ds:SignedInfo _before_ the this.resolverResult() is called.
      // this._manifest.appendChild(this._constructionElement);
      // this._manifest.appendChild(this._doc.createTextNode("\n"));
      Element nscontext = XMLUtils.createDSctx(this._doc, "ds");

      if (transforms != null) {
         this._constructionElement.appendChild(transforms.getElement());
         XMLUtils.addReturnToElement(this._constructionElement);
      }
      {
         MessageDigestAlgorithm mda =
            MessageDigestAlgorithm.getInstance(this._doc,
                                               messageDigestAlgorithm);

         this._constructionElement.appendChild(mda.getElement());
         XMLUtils.addReturnToElement(this._constructionElement);
      }
      {
         Element digestValueElement =
            XMLUtils.createElementInSignatureSpace(this._doc,
                                                   Constants._TAG_DIGESTVALUE);

         this._constructionElement.appendChild(digestValueElement);
         XMLUtils.addReturnToElement(this._constructionElement);
      }
   }

   /**
    * Constructor Reference
    *
    * @param doc this {@link Document} in which <code>XMLsignature</code> is placed
    * @param BaseURI the URI of the resource where the XML instance will be stored
    * @param ReferenceURI This referenceURI indicate where the data will for signature validation
    * @param manifest
    * @param messageDigestAlgorithm  {@link algorithms.MessageDigestAlgorithm Digest algorithm} which is applied to the data
    * @throws XMLSignatureException
    */
   protected Reference(
           Document doc, String BaseURI, String ReferenceURI, Manifest manifest, String messageDigestAlgorithm)
              throws XMLSignatureException {
      this(doc, BaseURI, ReferenceURI, manifest, (Transforms) null,
           messageDigestAlgorithm);
   }

   /**
    * Constructor Reference
    *
    * @param doc this {@link Document} in which <code>XMLsignature</code> is placed
    * @param BaseURI the URI of the resource where the XML instance will be stored
    * @param ReferenceURI This referenceURI indicate where the data is for signature validation
    * @param manifest
    * @param transforms {@link transforms} applied to data
    * @throws XMLSignatureException
    */
   protected Reference(
           Document doc, String BaseURI, String ReferenceURI, Manifest manifest, Transforms transforms)
              throws XMLSignatureException {
      this(doc, BaseURI, ReferenceURI, manifest, transforms,
           Constants.ALGO_ID_DIGEST_SHA1);
   }

   /**
    * Constructor Reference
    *
    * @param doc this {@link Document} in which <code>XMLsignature</code> is placed
    * @param BaseURI the URI of the resource where the XML instance will be stored
    * @param ReferenceURI This referenceURI indicate where the data is for signature validation
    * @param manifest
    * @throws XMLSignatureException
    */
   protected Reference(
           Document doc, String BaseURI, String ReferenceURI, Manifest manifest)
              throws XMLSignatureException {
      this(doc, BaseURI, ReferenceURI, manifest, (Transforms) null,
           Constants.ALGO_ID_DIGEST_SHA1);
   }

   /**
    * Build a {@link Reference} from an {@link Element}
    *
    * @param element <code>Reference</code> element
    * @param BaseURI the URI of the resource where the XML instance was stored
    * @param manifest is the {@link Manifest} of {@link SignedInfo} in which the Reference occurs. We need this because the Manifest has the individual {@link ResourceResolver}s whcih have been set by the user
    * @throws XMLSecurityException
    */
   protected Reference(Element element, String BaseURI, Manifest manifest)
           throws XMLSecurityException {

      super(element, BaseURI);

      this._manifest = manifest;
   }

   /**
    * Returns {@link MessageDigestAlgorithm}
    *
    *
    * @return {@link MessageDigestAlgorithm}
    *
    * @throws XMLSignatureException
    */
   public MessageDigestAlgorithm getMessageDigestAlgorithm()
           throws XMLSignatureException {

      Element digestMethodElem = this.getChildElementLocalName(0,
                                    Constants.SignatureSpecNS,
                                    Constants._TAG_DIGESTMETHOD);

      if (digestMethodElem == null) {
         return null;
      }

      String uri = digestMethodElem.getAttribute(Constants._ATT_ALGORITHM);

      return MessageDigestAlgorithm.getInstance(this._doc, uri);
   }

   /**
    * Sets the <code>URI</code> of this <code>Reference</code> element
    *
    * @param URI the <code>URI</code> of this <code>Reference</code> element
    */
   public void setURI(String URI) {

      if ((this._state == MODE_SIGN) && (URI != null)) {
         this._constructionElement.setAttribute(Constants._ATT_URI, URI);
      }
   }

   /**
    * Returns the <code>URI</code> of this <code>Reference</code> element
    *
    * @return URI the <code>URI</code> of this <code>Reference</code> element
    */
   public String getURI() {
      return this._constructionElement.getAttribute(Constants._ATT_URI);
   }

   /**
    * Sets the <code>Id</code> attribute of this <code>Reference</code> element
    *
    * @param Id the <code>Id</code> attribute of this <code>Reference</code> element
    */
   public void setId(String Id) {

      if ((this._state == MODE_SIGN) && (Id != null)) {
         this._constructionElement.setAttribute(Constants._ATT_ID, Id);
         IdResolver.registerElementById(this._constructionElement, Id);
      }
   }

   /**
    * Returns the <code>Id</code> attribute of this <code>Reference</code> element
    *
    * @return Id the <code>Id</code> attribute of this <code>Reference</code> element
    */
   public String getId() {
      return this._constructionElement.getAttribute(Constants._ATT_ID);
   }

   /**
    * Sets the <code>type</code> atttibute of the Reference indicate whether an <code>ds:Object</code>, <code>ds:SignatureProperty</code>, or <code>ds:Manifest</code> element
    *
    * @param Type the <code>type</code> attribute of the Reference
    */
   public void setType(String Type) {

      if ((this._state == MODE_SIGN) && (Type != null)) {
         this._constructionElement.setAttribute(Constants._ATT_TYPE, Type);
      }
   }

   /**
    * Return the <code>type</code> atttibute of the Reference indicate whether an <code>ds:Object</code>, <code>ds:SignatureProperty</code>, or <code>ds:Manifest</code> element
    *
    * @return the <code>type</code> attribute of the Reference
    */
   public String getType() {
      return this._constructionElement.getAttribute(Constants._ATT_TYPE);
   }

   /**
    * Method isReferenceToObject
    *
    * This returns true if the <CODE>Type</CODE> attribute of the
    * <CODE>Refernce</CODE> element points to a <CODE>#Object</CODE> element
    *
    * @return true if the Reference type indicates that this Reference points to an <code>Object</code>
    */
   public boolean typeIsReferenceToObject() {

      if ((this.getType() != null)
              && this.getType().equals(Reference.OBJECT_URI)) {
         return true;
      }

      return false;
   }

   /**
    * Method isReferenceToManifest
    *
    * This returns true if the <CODE>Type</CODE> attribute of the
    * <CODE>Refernce</CODE> element points to a <CODE>#Manifest</CODE> element
    *
    * @return true if the Reference type indicates that this Reference points to a {@link Manifest}
    */
   public boolean typeIsReferenceToManifest() {

      if ((this.getType() != null)
              && this.getType().equals(Reference.MANIFEST_URI)) {
         return true;
      }

      return false;
   }

   /**
    * Method setDigestValueElement
    *
    * @param digestValue
    * @throws XMLSignatureException
    */
   private void setDigestValueElement(byte[] digestValue)
           throws XMLSignatureException {

      if (this._state == MODE_SIGN) {
         Element digestValueElement = this.getChildElementLocalName(0,
                                         Constants.SignatureSpecNS,
                                         Constants._TAG_DIGESTVALUE);
         NodeList children = digestValueElement.getChildNodes();

         for (int i = 0; i < children.getLength(); i++) {
            digestValueElement.removeChild(children.item(i));
         }

         String base64codedValue = Base64.encode(digestValue);
         Text t = this._doc.createTextNode(base64codedValue);

         digestValueElement.appendChild(t);
      }
   }

   /**
    * Method generateDigestValue
    *
    * @throws ReferenceNotInitializedException
    * @throws XMLSignatureException
    */
   public void generateDigestValue()
           throws XMLSignatureException, ReferenceNotInitializedException {

      if (this._state == MODE_SIGN) {
         byte calculatedBytes[] = this.calculateDigest();

         this.setDigestValueElement(calculatedBytes);
      }
   }

   /**
    * This method returns the {@link XMLSignatureInput} which is referenced by the
    * <CODE>URI</CODE> Attribute.
    *
    * @throws ReferenceNotInitializedException
    * @throws XMLSignatureException
    * @ see Manifest#verifyReferences
    */
   protected void dereferenceURIandPerformTransforms()
           throws ReferenceNotInitializedException, XMLSignatureException {

      try {
         Attr URIAttr =
            this._constructionElement.getAttributeNode(Constants._ATT_URI);
         String URI;

         if (URIAttr == null) {
            URI = null;
         } else {
            URI = URIAttr.getNodeValue();
         }

         ResourceResolver resolver = ResourceResolver.getInstance(URIAttr,
                                        this._baseURI,
                                        this._manifest._perManifestResolvers);

         if (resolver == null) {
            Object exArgs[] = { URI };

            throw new ReferenceNotInitializedException(
               "signature.Verification.Reference.NoInput", exArgs);
         }

         resolver.addProperties(this._manifest._resolverProperties);

         this._transformsInput = resolver.resolve(URIAttr, this._baseURI);

         Transforms transforms = this.getTransforms();

         if (transforms != null) {
            this._transformsOutput =
               transforms.performTransforms(this._transformsInput);
         } else {
            this._transformsOutput = this._transformsInput;
         }
      } catch (ResourceResolverException ex) {
         throw new ReferenceNotInitializedException("empty", ex);
      } catch (CanonicalizationException ex) {
         throw new ReferenceNotInitializedException("empty", ex);
      } catch (InvalidCanonicalizerException ex) {
         throw new ReferenceNotInitializedException("empty", ex);
      } catch (TransformationException ex) {
         throw new ReferenceNotInitializedException("empty", ex);
      } catch (XMLSecurityException ex) {
         throw new ReferenceNotInitializedException("empty", ex);
      }
   }

   /**
    * Method getTransforms
    *
    * @return
    * @throws InvalidTransformException
    * @throws TransformationException
    * @throws XMLSecurityException
    * @throws XMLSignatureException
    */
   public Transforms getTransforms()
           throws XMLSignatureException, InvalidTransformException,
                  TransformationException, XMLSecurityException {

      Element transformsElement = this.getChildElementLocalName(0,
                                     Constants.SignatureSpecNS,
                                     Constants._TAG_TRANSFORMS);

      if (transformsElement != null) {
         Transforms transforms = new Transforms(transformsElement,
                                                this._baseURI);

         return transforms;
      } else {
         return null;
      }
   }

   /**
    * Method getReferencedBytes
    *
    * @return
    * @throws ReferenceNotInitializedException
    * @throws XMLSignatureException
    */
   public byte[] getReferencedBytes()
           throws ReferenceNotInitializedException, XMLSignatureException {

      try {
         this.dereferenceURIandPerformTransforms();

         byte[] signedBytes = this.getTransformsOutput().getBytes();

         return signedBytes;
      } catch (IOException ex) {
         throw new ReferenceNotInitializedException("empty", ex);
      } catch (CanonicalizationException ex) {
         throw new ReferenceNotInitializedException("empty", ex);
      } catch (InvalidCanonicalizerException ex) {
         throw new ReferenceNotInitializedException("empty", ex);
      }
   }

   /**
    * Method resolverResult
    *
    * @return
    * @throws ReferenceNotInitializedException
    * @throws XMLSignatureException
    */
   private byte[] calculateDigest()
           throws ReferenceNotInitializedException, XMLSignatureException {

      try {
         byte[] data = this.getReferencedBytes();
         MessageDigestAlgorithm mda = this.getMessageDigestAlgorithm();

         mda.reset();
         mda.update(data);

         byte calculatedDigestValue[] = mda.digest();

         //J-
         if (data.length < 20) {
            cat.debug(new String(data));
         } else {
            cat.debug(new String(data).substring(0, 20) + " ...");
         }
         //J+
         return calculatedDigestValue;
      } catch (XMLSecurityException ex) {
         throw new ReferenceNotInitializedException("empty", ex);
      }
   }

   /**
    * Tests reference valdiation is success or false
    *
    * @return true if reference valdiation is success, otherwise false
    * @throws ReferenceNotInitializedException
    * @throws XMLSecurityException
    */
   public boolean verify()
           throws ReferenceNotInitializedException, XMLSecurityException {

      Element digestValueElem = this.getChildElementLocalName(0,
                                   Constants.SignatureSpecNS,
                                   Constants._TAG_DIGESTVALUE);
      byte[] elemDig = Base64.decode(digestValueElem);
      byte[] calcDig = this.calculateDigest();
      boolean equal = MessageDigestAlgorithm.isEqual(elemDig, calcDig);

      if (!equal) {
         cat.warn("Verification failed for URI \"" + this.getURI() + "\"");

         if (cat.isDebugEnabled()) {
            cat.debug("unverifiedDigestValue= " + Base64.encode(elemDig));
            cat.debug("calculatedDigestValue= " + Base64.encode(calcDig));

            try {
               String tmp = new Long(System.currentTimeMillis()).toString()
                            + ".txt";

               cat.warn("Wrote \"" + this.getURI() + "\" to file " + tmp);
               JavaUtils.writeBytesToFilename(tmp, this.getReferencedBytes());
            } catch (Exception ex) {}
         }
      } else {
         cat.info("Verification successful for URI \"" + this.getURI() + "\"");
      }

      return equal;
   }

   /**
    * Method getTransformsInput
    *
    * @return
    */
   public XMLSignatureInput getTransformsInput() {
      return this._transformsInput;
   }

   /**
    * Method getTransformsOutput
    *
    * @return
    */
   public XMLSignatureInput getTransformsOutput() {
      return this._transformsOutput;
   }

   /**
    * Method getBaseLocalName
    *
    * @return
    */
   public String getBaseLocalName() {
      return Constants._TAG_REFERENCE;
   }

   static {
      org.apache.xml.security.Init.init();
   }
}

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



import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.c14n.*;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.utils.resolver.*;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.*;
import org.xml.sax.SAXException;


/**
 * Handles <code>&lt;ds:Manifest&gt;</code> elements
 * <p> This element holds the <code>Reference</code> elements</p>
 * @author $author: $
 */
public class Manifest extends SignatureElementProxy {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(Manifest.class.getName());

   /** Field _references */
   Vector _references;

   /** Field verificationResults[] */
   private boolean verificationResults[] = null;

   /** Field _signedContents */
   Vector _signedContents = new Vector();

   /** Field _resolverProperties */
   HashMap _resolverProperties = new HashMap(10);

   /** Field _perManifestResolvers */
   Vector _perManifestResolvers = new Vector();

   /**
    * Consturts {@link Manifest}
    *
    * @param doc the {@link Document} in which <code>XMLsignature</code> is placed
    */
   public Manifest(Document doc) {

      super(doc);

      XMLUtils.addReturnToElement(this._constructionElement);

      this._references = new Vector();
   }

   /**
    * Constructor Manifest
    *
    * @param element
    * @param BaseURI
    * @throws XMLSecurityException
    */
   public Manifest(Element element, String BaseURI)
           throws XMLSecurityException {

      super(element, BaseURI);

      // check out Reference children
      int le = this.length(Constants.SignatureSpecNS, Constants._TAG_REFERENCE);
      {
         if (le == 0) {

            // At least one Reference must be present. Bad.
            Object exArgs[] = { Constants._TAG_REFERENCE,
                                Constants._TAG_MANIFEST };

            throw new DOMException(DOMException.WRONG_DOCUMENT_ERR,
                                   I18n.translate("xml.WrongContent", exArgs));
         }
      }

      // create Vector
      this._references = new Vector(le);

      for (int i = 0; i < le; i++) {
         this._references.add(null);
      }
   }

   /**
    * This <code>addDocument</code> method is used to add a new resource to the
    * signed info. A {@link org.apache.xml.security.signature.Reference} is built
    * from the supplied values.
    *
    * @param BaseURI the URI of the resource where the XML instance was stored
    * @param referenceURI <code>URI</code> attribute in <code>Reference</code> for specifing where data is
    * @param transforms org.apache.xml.security.signature.Transforms object with an ordered list of transformations to be performed.
    * @param digestURI The digest algorthim URI to be used.
    * @param ReferenceId
    * @param ReferenceType
    * @throws XMLSignatureException
    */
   public void addDocument(
           String BaseURI, String referenceURI, Transforms transforms, String digestURI, String ReferenceId, String ReferenceType)
              throws XMLSignatureException {

      if (this._state == MODE_SIGN) {

         // the this._doc is handed implicitly by the this.getOwnerDocument()
         Reference ref = new Reference(this._doc, BaseURI, referenceURI, this,
                                       transforms, digestURI);

         if (ReferenceId != null) {
            ref.setId(ReferenceId);
         }

         if (ReferenceType != null) {
            ref.setType(ReferenceType);
         }

         // add Reference object to our cache vector
         this._references.add(ref);

         // add the Element of the Reference object to the Manifest/SignedInfo
         this._constructionElement.appendChild(ref.getElement());
         XMLUtils.addReturnToElement(this._constructionElement);
      }
   }

   /**
    * The calculation of the DigestValues in the References must be after the
    * References are already added to the document and during the signing
    * process. This ensures that all neccesary data is in place.
    *
    * @throws ReferenceNotInitializedException
    * @throws XMLSignatureException
    */
   public void generateDigestValues()
           throws XMLSignatureException, ReferenceNotInitializedException {

      if (this._state == MODE_SIGN) {
         for (int i = 0; i < this.getLength(); i++) {

            // update the cached Reference object, the Element content is automatically updated
            Reference currentRef = (Reference) this._references.elementAt(i);

            currentRef.generateDigestValue();
         }
      }
   }

   /**
    * Return the nonnegative number of added references.
    *
    * @return the number of references
    */
   public int getLength() {
      return this._references.size();
   }

   /**
    * Return the <it>i</it><sup>th</sup> reference.  Valid <code>i</code>
    * values are 0 to <code>{link@ getSize}-1</code>.
    *
    * @param i Index of the requested {@link Reference}
    * @return the <it>i</it><sup>th</sup> reference
    * @throws XMLSecurityException
    */
   public Reference item(int i) throws XMLSecurityException {

      if (this._state == MODE_SIGN) {

         // we already have real objects
         return (Reference) this._references.elementAt(i);
      } else {
         if (this._references.elementAt(i) == null) {

            // not yet constructed, so _we_ have to
            Element refElem = super.getChildElementLocalName(i,
                                 Constants.SignatureSpecNS,
                                 Constants._TAG_REFERENCE);
            Reference ref = new Reference(refElem, this._baseURI, this);

            this._references.set(i, ref);
         }

         return (Reference) this._references.elementAt(i);
      }
   }

   /**
    * Sets the <code>Id</code> attribute
    *
    * @param Id the <code>Id</code> attribute in <code>ds:Manifest</code>
    */
   public void setId(String Id) {

      if ((this._state == MODE_SIGN) && (Id != null)) {
         this._constructionElement.setAttributeNS(null, Constants._ATT_ID, Id);
         IdResolver.registerElementById(this._constructionElement, Id);
      }
   }

   /**
    * Returns the <code>Id</code> attribute
    *
    * @return the <code>Id</code> attribute in <code>ds:Manifest</code>
    */
   public String getId() {
      return this._constructionElement.getAttributeNS(null, Constants._ATT_ID);
   }

   /**
    * Used to do a <A HREF="http://www.w3.org/TR/xmldsig-core/#def-ValidationReference">reference
    * validation</A> of all enclosed references using the {@link Reference#verify} method.
    *
    * <p>This step loops through all {@link Reference}s and does verify the hash
    * values. If one or more verifications fail, the method returns
    * <code>false</code>. If <i>all</i> verifications are successful,
    * it returns <code>true</code>. The results of the individual reference
    * validations are available by using the {@link #getVerificationResult(int)} method
    *
    * @return true if all References verify, false if one or more do not verify.
    * @throws MissingResourceFailureException if a {@link Reference} does not verify (throws a {@link org.apache.xml.security.signature.ReferenceNotInitializedException} because of an uninitialized {@link XMLSignatureInput}
    * @see org.apache.xml.security.signature.Reference#verify
    * @see org.apache.xml.security.signature.SignedInfo#verify
    * @see org.apache.xml.security.signature.MissingResourceFailureException
    * @throws XMLSecurityException
    */
   public boolean verifyReferences()
           throws MissingResourceFailureException, XMLSecurityException {
      return this.verifyReferences(false);
   }

   /**
    * Used to do a <A HREF="http://www.w3.org/TR/xmldsig-core/#def-ValidationReference">reference
    * validation</A> of all enclosed references using the {@link Reference#verify} method.
    *
    * <p>This step loops through all {@link Reference}s and does verify the hash
    * values. If one or more verifications fail, the method returns
    * <code>false</code>. If <i>all</i> verifications are successful,
    * it returns <code>true</code>. The results of the individual reference
    * validations are available by using the {@link #getVerificationResult(int)} method
    *
    * @param followManifests
    * @return true if all References verify, false if one or more do not verify.
    * @throws MissingResourceFailureException if a {@link Reference} does not verify (throws a {@link org.apache.xml.security.signature.ReferenceNotInitializedException} because of an uninitialized {@link XMLSignatureInput}
    * @see org.apache.xml.security.signature.Reference#verify
    * @see org.apache.xml.security.signature.SignedInfo#verify
    * @see org.apache.xml.security.signature.MissingResourceFailureException
    * @throws XMLSecurityException
    */
   public boolean verifyReferences(boolean followManifests)
           throws MissingResourceFailureException, XMLSecurityException {

      cat.debug(
         "verify "
         + this.length(Constants.SignatureSpecNS, Constants._TAG_REFERENCE)
         + " References");
      cat.debug("I am " + (followManifests
                           ? ""
                           : "not") + " requested to follow nested Manifests");

      boolean verify = true;

      if (this.length(Constants.SignatureSpecNS, Constants._TAG_REFERENCE)
              == 0) {
         throw new XMLSecurityException("empty");
      }

      this.verificationResults =
         new boolean[this.length(Constants.SignatureSpecNS, Constants._TAG_REFERENCE)];

      for (int i =
              0; i < this
                 .length(Constants.SignatureSpecNS, Constants
                    ._TAG_REFERENCE); i++) {
         Reference currentRef =
            new Reference(this
               .getChildElementLocalName(i, Constants.SignatureSpecNS, Constants
               ._TAG_REFERENCE), this._baseURI, this);

         this._references.set(i, currentRef);

         /* if only one item does not verify, the whole verification fails */
         try {
            boolean currentRefVerified = currentRef.verify();

            this.setVerificationResult(i, currentRefVerified);

            if (!currentRefVerified) {
               verify = false;
            }

            cat.debug("The Reference has Type " + currentRef.getType());

            // was verification successful till now and do we want to verify the Manifest?
            if (verify && followManifests
                    && currentRef.typeIsReferenceToManifest()) {
               cat.debug("We have to follow a nested Manifest");

               try {
                  currentRef.dereferenceURIandPerformTransforms();

                  XMLSignatureInput signedManifestNodes =
                     currentRef.getTransformsOutput();
                  Set nl = signedManifestNodes.getNodeSet();
                  Manifest referencedManifest = null;
                  Iterator nlIterator = nl.iterator();

                  findManifest: while (nlIterator.hasNext()) {
                     Node n = (Node) nlIterator.next();

                     if ((n.getNodeType() == Node.ELEMENT_NODE) && ((Element) n)
                             .getNamespaceURI()
                             .equals(Constants.SignatureSpecNS) && ((Element) n)
                             .getLocalName().equals(Constants._TAG_MANIFEST)) {
                        try {
                           referencedManifest =
                              new Manifest((Element) n,
                                           signedManifestNodes.getSourceURI());

                           break findManifest;
                        } catch (XMLSecurityException ex) {

                           // Hm, seems not to be a ds:Manifest
                        }
                     }
                  }

                  if (referencedManifest == null) {

                     // The Reference stated that it points to a ds:Manifest
                     // but we did not find a ds:Manifest in the signed area
                     throw new MissingResourceFailureException("empty",
                                                               currentRef);
                  }

                  referencedManifest._perManifestResolvers =
                     this._perManifestResolvers;
                  referencedManifest._resolverProperties =
                     this._resolverProperties;

                  boolean referencedManifestValid =
                     referencedManifest.verifyReferences(followManifests);

                  if (!referencedManifestValid) {
                     verify = false;

                     cat.warn("The nested Manifest was invalid (bad)");
                  } else {
                     cat.debug("The nested Manifest was valid (good)");
                  }
               } catch (IOException ex) {
                  throw new ReferenceNotInitializedException("empty", ex);
               } catch (ParserConfigurationException ex) {
                  throw new ReferenceNotInitializedException("empty", ex);
               } catch (SAXException ex) {
                  throw new ReferenceNotInitializedException("empty", ex);
               }
            }
         } catch (ReferenceNotInitializedException ex) {
            Object exArgs[] = { currentRef.getURI() };

            throw new MissingResourceFailureException(
               "signature.Verification.Reference.NoInput", exArgs, ex,
               currentRef);
         }
      }

      return verify;
   }

   /**
    * Method setVerificationResult
    *
    * @param index
    * @param verify
    * @throws XMLSecurityException
    */
   private void setVerificationResult(int index, boolean verify)
           throws XMLSecurityException {

      if (this.verificationResults == null) {
         this.verificationResults = new boolean[this.getLength()];
      }

      this.verificationResults[index] = verify;
   }

   /**
    * After verifying a {@link Manifest} or a {@link SignedInfo} using the
    * {@link Manifest#verifyReferences} or {@link SignedInfo#verify} methods,
    * the individual results can be retrieved with this method.
    *
    * @param index an index of into a {@link Manifest} or a {@link SignedInfo}
    * @return the results of reference validation at the specified index
    * @throws XMLSecurityException
    */
   public boolean getVerificationResult(int index) throws XMLSecurityException {

      if ((index < 0) || (index > this.getLength() - 1)) {
         Object exArgs[] = { Integer.toString(index),
                             Integer.toString(this.getLength()) };
         Exception e =
            new IndexOutOfBoundsException(I18n
               .translate("signature.Verification.IndexOutOfBounds", exArgs));

         throw new XMLSecurityException("generic.EmptyMessage", e);
      }

      if (this.verificationResults == null) {
         try {
            boolean discard = this.verifyReferences();
         } catch (Exception ex) {
            throw new XMLSecurityException("generic.EmptyMessage", ex);
         }
      }

      return this.verificationResults[index];
   }

   /**
    * Adds Resource Resolver for retrieving resources at specified <code>URI</code> attribute in <code>reference</code> element
    *
    * @param resolver {@link ResourceResolver} can provide the implemenatin subclass of {@link ResourceResolverSpi} for retrieving resource.
    */
   public void addResourceResolver(ResourceResolver resolver) {

      if (resolver != null) {
         this._perManifestResolvers.add(resolver);
      }
   }

   /**
    * Adds Resource Resolver for retrieving resources at specified <code>URI</code> attribute in <code>reference</code> element
    *
    * @param resolverSpi the implemenatin subclass of {@link ResourceResolverSpi} for retrieving resource.
    */
   public void addResourceResolver(ResourceResolverSpi resolverSpi) {

      if (resolverSpi != null) {
         this._perManifestResolvers.add(new ResourceResolver(resolverSpi));
      }
   }

   /**
    * Used to pass parameters like proxy servers etc. to the ResourceResolver
    * implementation.
    *
    * @param key the key
    * @param value the value
    */
   public void setResolverProperty(String key, String value) {

      java.util.Iterator i = this._resolverProperties.keySet().iterator();

      while (i.hasNext()) {
         String c = (String) i.next();

         if (c.equals(key)) {
            key = c;

            break;
         }
      }

      this._resolverProperties.put(key, value);
   }

   /**
    * Returns the value at specified key
    *
    * @param key the key
    * @return the value
    */
   public String getResolverProperty(String key) {

      java.util.Iterator i = this._resolverProperties.keySet().iterator();

      while (i.hasNext()) {
         String c = (String) i.next();

         if (c.equals(key)) {
            key = c;

            break;
         }
      }

      return (String) this._resolverProperties.get(key);
   }

   /**
    * Method getSignedContentItem
    *
    * @param i
    * @return
    * @throws XMLSignatureException
    */
   public byte[] getSignedContentItem(int i) throws XMLSignatureException {

      try {
         return this.getReferencedContentAfterTransformsItem(i).getBytes();
      } catch (IOException ex) {
         throw new XMLSignatureException("empty", ex);
      } catch (CanonicalizationException ex) {
         throw new XMLSignatureException("empty", ex);
      } catch (InvalidCanonicalizerException ex) {
         throw new XMLSignatureException("empty", ex);
      } catch (XMLSecurityException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /**
    * Method getReferencedContentPriorTransformsItem
    *
    * @param i
    * @return
    * @throws XMLSecurityException
    */
   public XMLSignatureInput getReferencedContentBeforeTransformsItem(int i)
           throws XMLSecurityException {
      return this.item(i).getTransformsInput();
   }

   /**
    * Method getReferencedContentAfterTransformsItem
    *
    * @param i
    * @return
    * @throws XMLSecurityException
    */
   public XMLSignatureInput getReferencedContentAfterTransformsItem(int i)
           throws XMLSecurityException {
      return this.item(i).getTransformsOutput();
   }

   /**
    * Method getSignedContentLength
    *
    * @return
    */
   public int getSignedContentLength() {
      return this.getLength();
   }

   /**
    * Method getBaseLocalName
    *
    * @return
    */
   public String getBaseLocalName() {
      return Constants._TAG_MANIFEST;
   }
}

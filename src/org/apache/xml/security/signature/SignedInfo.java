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



import java.io.IOException;
import java.util.Collection;
import java.util.Vector;
import org.w3c.dom.*;
import org.apache.xpath.XPathAPI;
import javax.xml.transform.TransformerException;
import org.apache.xml.security.algorithms.*;
import org.apache.xml.security.c14n.*;
import org.apache.xml.security.c14n.helper.XPathContainer;
import org.apache.xml.security.exceptions.*;
import org.apache.xml.security.transforms.*;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.utils.resolver.*;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


/**
 * Handles <code>&lt;ds:SignedInfo&gt;</code> elements
 * This <code>SignedInfo<code> element includes the canonicalization algorithm,
 * a signature algorithm, and one or more references
 * @author Christian Geuer-Pollmann
 */
public class SignedInfo extends Manifest {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(SignedInfo.class.getName());

   /** Field _signatureAlgorithm */
   private SignatureAlgorithm _signatureAlgorithm = null;

   /**
    * Constructor SignedInfo
    *
    */
   private SignedInfo() {}

   /**
    * Overwrites {@link Manifest(org.w3c.dom.Document)} because it creates another Element.
    *
    * @param doc the {@link Document} in which <code>XMLsignature</code> will be placed
    */
   public SignedInfo(Document doc) {
      this(doc, Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS,
           XMLSignature.ALGO_ID_SIGNATURE_DSA);
   }

   /**
    * Constructs {@link SignedInfo} using given Canoicaliztion algorithm and Signature algorithm
    *
    * @param doc <code>SignedInfo</code> is placed in this document
    * @param CanonicalizationMethodURI URI representation of the Canonicalization method
    * @param SignatureMethodURI URI representation of the Digest and Signature algorithm
    */
   public SignedInfo(Document doc, String CanonicalizationMethodURI,
                     String SignatureMethodURI) {
      this(doc, CanonicalizationMethodURI, SignatureMethodURI, 0);
   }

   /**
    * Constructor SignedInfo
    *
    * @param doc
    * @param CanonicalizationMethodURI
    * @param SignatureMethodURI
    * @param HMACOutputLength
    */
   public SignedInfo(Document doc, String CanonicalizationMethodURI,
                     String SignatureMethodURI, int HMACOutputLength) {

      super(doc, Constants._TAG_SIGNEDINFO);

      this._constructionElement.appendChild(this._doc.createTextNode("\n"));
      {
         Element canonElem = XMLUtils.createElementInSignatureSpace(this._doc,
                                Constants._TAG_CANONICALIZATIONMETHOD);

         canonElem.setAttribute(Constants._ATT_ALGORITHM,
                                CanonicalizationMethodURI);
         this._constructionElement.appendChild(canonElem);
         this._constructionElement.appendChild(this._doc.createTextNode("\n"));
      }
      {
         if (HMACOutputLength > 0) {
            this._signatureAlgorithm = new SignatureAlgorithm(this._doc,
                    SignatureMethodURI, HMACOutputLength);
         } else {
            this._signatureAlgorithm = new SignatureAlgorithm(this._doc,
                    SignatureMethodURI);
         }

         this._constructionElement
            .appendChild(this._signatureAlgorithm.getElement());
         this._constructionElement.appendChild(this._doc.createTextNode("\n"));
      }
   }

   /**
    * Build a {@link SignedInfo} from an {@link Element}
    *
    * @param element <code>SignedInfo</code>
    * @param BaseURI the URI of the resource where the XML instance was stored
    * @throws XMLSecurityException
    */
   public SignedInfo(Element element, String BaseURI)
           throws XMLSecurityException {

      // Parse the Reference childern and Id attribute in the Manifest
      super(element, BaseURI);

      // element must be of type ds:Reference
      XMLUtils.guaranteeThatElementInSignatureSpace(element,
              Constants._TAG_SIGNEDINFO);

      this._signatureAlgorithm =
         new SignatureAlgorithm(this.getSignatureMethodElement(),
                                this.getBaseURI());
   }

   /**
    * Tests core validation process
    *
    * @return true if verification was successful
    * @throws MissingResourceFailureException
    * @throws XMLSecurityException
    */
   public boolean verify()
           throws MissingResourceFailureException, XMLSecurityException {
      return super.verifyReferences();
   }

   /**
    * Returns getCanonicalizedOctetStream
    *
    * @return the canonicalization result octedt stream of <code>SignedInfo</code> element
    * @throws CanonicalizationException
    * @throws IOException
    * @throws InvalidCanonicalizerException
    * @throws XMLSecurityException
    */
   public byte[] getCanonicalizedOctetStream()
           throws CanonicalizationException, InvalidCanonicalizerException,
                  IOException, XMLSecurityException {

      Canonicalizer c14nizer =
         Canonicalizer.getInstance(this.getCanonicalizationMethodURI());
      byte[] output = c14nizer.canonicalize(this._constructionElement);

      return output;
   }

   /**
    * Returns the Canonicalization method URI
    *
    * @return the Canonicalization method URI
    */
   public String getCanonicalizationMethodURI() {

      NodeList children = this._constructionElement.getChildNodes();

      for (int i = 0; i < children.getLength(); i++) {
         Node n = children.item(i);

         if (n.getNodeType() == Node.ELEMENT_NODE) {
            boolean found = true;

            try {
               XMLUtils.guaranteeThatElementInSignatureSpace((Element) n,
                       Constants._TAG_CANONICALIZATIONMETHOD);
            } catch (XMLSecurityException ex) {
               found = false;
            }

            if (found) {
               return ((Element) n).getAttribute(Constants._ATT_ALGORITHM);
            }
         }
      }

      return null;
   }

   /**
    * Returns the Signature method URI
    *
    * @return the Signature method URI
    */
   public String getSignatureMethodURI() {

      Element signatureElement = this.getSignatureMethodElement();

      if (signatureElement != null) {
         return signatureElement.getAttribute(Constants._ATT_ALGORITHM);
      }

      return null;
   }

   /**
    * Method getSignatureMethodElement
    *
    * @return
    */
   public Element getSignatureMethodElement() {

      NodeList children = this._constructionElement.getChildNodes();

      for (int i = 0; i < children.getLength(); i++) {
         Node n = children.item(i);

         cat.debug("Looking for SignatureMethodURI in " + n);

         if (n.getNodeType() == Node.ELEMENT_NODE) {
            boolean found = true;

            try {
               XMLUtils.guaranteeThatElementInSignatureSpace((Element) n,
                       Constants._TAG_SIGNATUREMETHOD);
            } catch (XMLSecurityException ex) {
               found = false;
            }

            if (found) {
               return (Element) n;
            }
         }
      }

      return null;
   }

   /**
    * Creates a SecretKey for the appropriate Mac algorithm based on a
    * byte[] array password.
    *
    * @param secretKeyBytes
    * @return
    * @throws XMLSecurityException
    */
   public SecretKey createSecretKey(byte[] secretKeyBytes)
           throws XMLSecurityException {

      return new SecretKeySpec(secretKeyBytes,
                               this._signatureAlgorithm
                                  .getJCEAlgorithmString());
   }

   static {
      org.apache.xml.security.Init.init();
   }
}

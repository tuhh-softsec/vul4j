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
import java.security.*;
import java.security.cert.*;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.xml.security.algorithms.*;
import org.apache.xml.security.c14n.*;
import org.apache.xml.security.c14n.helper.XPathContainer;
import org.apache.xml.security.exceptions.*;
import org.apache.xml.security.keys.*;
import org.apache.xml.security.keys.content.*;
import org.apache.xml.security.keys.content.keyvalues.*;
import org.apache.xml.security.keys.content.x509.*;
import org.apache.xml.security.keys.keyresolver.*;
import org.apache.xml.security.keys.storage.*;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.utils.resolver.*;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.*;


/**
 * Handles <code>&lt;ds:Signature&gt;</code> elements
 *
 * @author $Author$
 */
public class XMLSignature extends ElementProxy {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(XMLSignature.class.getName());
   //J-
   /** MAC - Required HMAC-SHA1 */
   public static final String ALGO_ID_MAC_HMAC_SHA1 =
      Constants.SignatureSpecNS + "hmac-sha1";

   /** Signature - Required DSAwithSHA1 (DSS) */
   public static final String ALGO_ID_SIGNATURE_DSA =
      Constants.SignatureSpecNS + "dsa-sha1";

   /** Signature - Recommended RSAwithSHA1 */
   public static final String ALGO_ID_SIGNATURE_RSA =
      Constants.SignatureSpecNS + "rsa-sha1";
   //J+

   /** ds:Signature.ds:SignedInfo element */
   SignedInfo _signedInfo = null;

   /** Field _signatureValueElement */
   Element _signatureValueElement = null;

   /** ds:Signature.ds:KeyInfo */
   KeyInfo _keyInfo = null;

   /**
    * This creates a new <CODE>ds:Signature</CODE> Element and adds an empty
    * <CODE>ds:SignedInfo</CODE> to it.
    *
    * @param doc
    * @param BaseURI
    * @param signatureAlgorithmURI
    * @throws XMLSignatureException
    */
   public XMLSignature(
           Document doc, String BaseURI, String signatureAlgorithmURI)
              throws XMLSignatureException {

      super(doc, Constants._TAG_SIGNATURE);

      this._constructionElement.appendChild(this._doc.createTextNode("\n"));

      this._baseURI = BaseURI;
      this._signedInfo =
         new SignedInfo(this._doc, Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS,
                        signatureAlgorithmURI);

      this._constructionElement.appendChild(this._signedInfo.getElement());
      this._constructionElement.appendChild(this._doc.createTextNode("\n"));

      // create an empty SignatureValue; this is filled by setSignatureValueElement
      this._signatureValueElement =
         XMLUtils.createElementInSignatureSpace(this._doc,
                                                Constants._TAG_SIGNATUREVALUE);

      this._constructionElement.appendChild(this._signatureValueElement);
      this._constructionElement.appendChild(this._doc.createTextNode("\n"));

      // this._constructionElement.appendChild(this._keyInfo.getElement());
      // this._constructionElement.appendChild(this._doc.createTextNode("\n"));
   }

   /**
    * Constructor XMLSignature
    *
    * @param element
    * @param BaseURI
    * @throws IOException
    * @throws XMLSecurityException
    * @throws XMLSignatureException
    */
   public XMLSignature(Element element, String BaseURI)
           throws XMLSignatureException, XMLSecurityException, IOException {

      super(element, BaseURI);

      // element must be of type ds:Signature
      XMLUtils.guaranteeThatElementInSignatureSpace(element,
              Constants._TAG_SIGNATURE);

      Element nscontext = XMLUtils.createDSctx(this._doc, "ds",
                                               Constants.SignatureSpecNS);

      // check out SignedInfo child
      {

         // at this stage, nl can contain more than one SignedInfo, possibly
         // in Object Elements. Make shure that nl contains exactly one
         // SignedInfo which is child of element
         try {
            Element signedInfoElem =
               (Element) XPathAPI
                  .selectSingleNode(element, "./ds:"
                                    + Constants._TAG_SIGNEDINFO, nscontext);

            if (signedInfoElem == null) {
               Object exArgs[] = { Constants._TAG_SIGNEDINFO,
                                   Constants._TAG_SIGNATURE };

               throw new XMLSignatureException("xml.WrongContent", exArgs);
            }

            this._signedInfo = new SignedInfo(signedInfoElem, BaseURI);
         } catch (javax.xml.transform.TransformerException ex) {
            Object exArgs[] = { Constants._TAG_SIGNEDINFO,
                                Constants._TAG_SIGNATURE };

            throw new XMLSignatureException("xml.WrongContent", exArgs, ex);
         }
      }

      // check out SignatureValue child
      {
         try {
            this._signatureValueElement =
               (Element) XPathAPI
                  .selectSingleNode(element, "./ds:"
                                    + Constants._TAG_SIGNATUREVALUE, nscontext);

            if (this._signatureValueElement == null) {
               Object exArgs[] = { Constants._TAG_SIGNATUREVALUE,
                                   Constants._TAG_SIGNATURE };

               throw new XMLSignatureException("xml.WrongContent", exArgs);
            }
         } catch (javax.xml.transform.TransformerException ex) {
            Object exArgs[] = { Constants._TAG_SIGNATUREVALUE,
                                Constants._TAG_SIGNATURE };

            throw new XMLSignatureException("xml.WrongContent", exArgs, ex);
         }
      }

      // <element ref="ds:KeyInfo" minOccurs="0"/>
      {
         try {
            Element keyInfoElem = (Element) XPathAPI.selectSingleNode(element,
                                     "./ds:" + Constants._TAG_KEYINFO,
                                     nscontext);

            if (keyInfoElem != null) {

               /** @todo check out KeyInfo here */
               this._keyInfo = new KeyInfo(keyInfoElem, BaseURI);

               cat.debug("Found a KeyInfo in the Signature: " + this._keyInfo);
            } else {
               cat.debug("I didn't find a KeyInfo in the Signature");
            }
         } catch (javax.xml.transform.TransformerException ex) {
            Object exArgs[] = { Constants._TAG_KEYINFO,
                                Constants._TAG_SIGNATURE };

            throw new XMLSignatureException("xml.WrongContent", exArgs, ex);
         }
      }

      if (cat.isDebugEnabled()) {
         cat.debug("Signature: Id = \"" + this.getId() + "\"");
      }
   }

   /**
    * Method flushInternalObjects
    *
    * @throws XMLSecurityException
    */
   private void flushInternalObjects() throws XMLSecurityException {

      // now we fill our SignatureValue Element with life
      // this.setSignatureValueElement();
      // The Signature contains a KeyInfo; we delete it if it's empty
      Element keyInfoElement = this._keyInfo.getElement();

      if (this._keyInfo.isEmpty()) {
         cat.debug("KeyInfo is empty, try to remove it");
         this._constructionElement.removeChild(keyInfoElement);
      } else {
         cat.debug("KeyInfo is not empty, try to add a return");

         Node nodeAfterKeyInfo = keyInfoElement.getNextSibling();

         if (nodeAfterKeyInfo == null) {
            this._constructionElement
               .appendChild(this._doc.createTextNode("\n"));
         } else {
            this._constructionElement
               .insertBefore(this._doc.createTextNode("\n"), nodeAfterKeyInfo);
         }
      }

      {
         for (int i = 0; i < this.getObjectLength(); i++) {
            ObjectContainer oc = this.getObjectItem(i);
            Element ocElem = oc.getElement();

            this._constructionElement.appendChild(ocElem);
            this._constructionElement
               .appendChild(this._doc.createTextNode("\n"));
         }
      }
   }

   /**
    * Serializes the XMLSignature object to an Element
    *
    * @param Id
    *
    *  if (this._state == MODE_VERIFY) {
    *     return this._constructionElement;
    *  }
    *
    *  try {
    *     this.flushInternalObjects();
    *
    *     return this._constructionElement;
    *  } catch (XMLSecurityException ex) {
    *     ex.printStackTrace();
    *
    *     throw new RuntimeException(
    *        "XMLSignature.flushInternalObjects() failed");
    *  } catch (DOMException ex) {
    *     ex.printStackTrace();
    *
    *     throw new RuntimeException(
    *        "XMLSignature.flushInternalObjects() failed");
    *  }
    * }
    */

   /**
    * Sets the <code>Id</code> attribute
    *
    * @param Id ID
    */
   public void setId(String Id) {

      if ((this._state == MODE_SIGN) && (Id != null)) {
         this._constructionElement.setAttribute(Constants._ATT_ID, Id);
         IdResolver.registerElementById(this._constructionElement, Id);
      }
   }

   /**
    * Returns the <code>Id</code> attribute
    *
    * @return the <code>Id</code> attribute
    */
   public String getId() {
      return this._constructionElement.getAttribute(Constants._ATT_ID);
   }

   /**
    * Method getSignedInfo
    *
    * @return
    */
   public SignedInfo getSignedInfo() {
      return this._signedInfo;
   }

   /**
    * Method getSignatureValue
    *
    * @return
    */
   public byte[] getSignatureValue() {

      cat.debug("getSignatureValue " + this._signatureValueElement);
      cat.debug("getSignatureValue base64 = "
                + ((Text) this._signatureValueElement.getChildNodes().item(0))
                   .getData());

      return Base64.decode(this._signatureValueElement);
   }

   /**
    * Method setSignatureValueElement
    *
    * @param signatureValue
    * @throws XMLSignatureException
    */
   private void setSignatureValueElement(byte[] signatureValue)
           throws XMLSignatureException {

      if (this._state == MODE_SIGN) {
         NodeList children = this._signatureValueElement.getChildNodes();

         for (int i = 0; i < children.getLength(); i++) {
            this._signatureValueElement.removeChild(children.item(i));
         }

         String base64codedValue = Base64.encode(signatureValue);
         Text t = this._doc.createTextNode(base64codedValue);

         this._signatureValueElement.appendChild(t);
      } else {
         ;
      }
   }

   /**
    * Returns the KeyInfo child. If we are in signing mode and the KeyInfo
    * does not exist yet, we create it and add it to the Signature.
    *
    * @return the KeyInfo object
    */
   public KeyInfo getKeyInfo() {

      if ((this._state == MODE_SIGN) && (this._keyInfo == null)) {
         this._keyInfo = new KeyInfo(this._doc);

         try {
            Element nscontext = XMLUtils.createDSctx(this._doc, "ds",
                                                     Constants.SignatureSpecNS);
            Element firstObject =
               (Element) XPathAPI.selectSingleNode(this._constructionElement,
                                                   "./ds:"
                                                   + Constants._TAG_OBJECT
                                                   + "[1]", nscontext);

            if (firstObject != null) {
               cat.debug("Found a ds:Object");
               this._constructionElement
                  .insertBefore(this._keyInfo.getElement(), firstObject);
               this._constructionElement
                  .insertBefore(this._doc.createTextNode("\n"), firstObject);
            } else {
               cat.debug("Found no ds:Object");
               this._constructionElement
                  .appendChild(this._keyInfo.getElement());
               this._constructionElement
                  .appendChild(this._doc.createTextNode("\n"));
            }
         } catch (TransformerException ex) {
            ex.printStackTrace();
         }

         this._constructionElement.appendChild(this._keyInfo.getElement());
      }

      return this._keyInfo;
   }

   /**
    * Method setKeyInfo
    *
    * @param keyInfo
    */
   private void setKeyInfo(KeyInfo keyInfo) {
      this._keyInfo = keyInfo;
   }

   /**
    * Method appendObject
    *
    * @param object
    * @throws XMLSignatureException
    */
   public void appendObject(ObjectContainer object)
           throws XMLSignatureException {

      try {
         if (this._state != MODE_SIGN) {
            throw new XMLSignatureException(
               "signature.operationOnlyBeforeSign");
         }

         cat.debug("Added ds:Object with Id " + object.getId());
         this._constructionElement.appendChild(object.getElement());
         this._constructionElement.appendChild(this._doc.createTextNode("\n"));
      } catch (XMLSecurityException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /**
    * Method objectItem
    *
    * @param index
    * @return
    */
   public ObjectContainer getObjectItem(int index) {

      try {
         Element nscontext = XMLUtils.createDSctx(this._doc, "ds",
                                                  Constants.SignatureSpecNS);
         NodeList nl = XPathAPI.selectNodeList(this._doc,
                                               "./ds:" + Constants._TAG_OBJECT,
                                               nscontext);

         if (index >= nl.getLength()) {
            return null;
         }

         return new ObjectContainer((Element) nl.item(index), this._baseURI);
      } catch (TransformerException ex) {
         return null;
      } catch (XMLSecurityException ex) {
         return null;
      }
   }

   /**
    * Method getObjectLength
    *
    * @return
    */
   public int getObjectLength() {

      try {
         Element nscontext = XMLUtils.createDSctx(this._doc, "ds",
                                                  Constants.SignatureSpecNS);
         NodeList nl = XPathAPI.selectNodeList(this._doc,
                                               "./ds:" + Constants._TAG_OBJECT,
                                               nscontext);

         return nl.getLength();
      } catch (TransformerException ex) {
         return 0;
      }
   }

   /**
    * Method sign
    *
    * @param privateKey
    * @throws XMLSignatureException
    */
   public void sign(PrivateKey privateKey) throws XMLSignatureException {

      cat.debug("sign() called");

      try {
         if (this._state == MODE_SIGN) {
            String signatureMethodURI =
               this._signedInfo.getSignatureMethodURI();
            SignatureAlgorithm sa = SignatureAlgorithm.getInstance(this._doc,
                                       signatureMethodURI);

            sa.initSign(privateKey);

            SignedInfo si = this.getSignedInfo();

            si.generateDigestValues();

            byte signedInfoOctets[] = si.getCanonicalizedOctetStream();

            sa.update(signedInfoOctets);

            byte signatureValue[] = sa.sign();

            this.setSignatureValueElement(signatureValue);
            cat.debug("sa.sign() finished");
         }
      } catch (IOException ex) {
         throw new XMLSignatureException("empty", ex);
      } catch (SignatureException ex) {
         throw new XMLSignatureException("empty", ex);
      } catch (InvalidKeyException ex) {
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
    * Method sign
    *
    * @return
    * @throws XMLSignatureException
    */
   public boolean verify() throws XMLSignatureException {

      if (this._state == MODE_VERIFY) {
         String signatureMethodURI = this._signedInfo.getSignatureMethodURI();
         SignatureAlgorithm sa = SignatureAlgorithm.getInstance(this._doc,
                                    signatureMethodURI);

         cat.debug("I would use " + sa.getAlgorithm().getAlgorithm()
                   + " to verify");

         /** @todo do real work here */
         return false;
      } else {

         /** @todo fill in error message */
         Object exArgs[] = {
         };

         throw new XMLSignatureException("", exArgs);
      }
   }

   /**
    * Method addResourceResolver
    *
    * @param resolver
    */
   public void addResourceResolver(ResourceResolver resolver) {
      this.getSignedInfo().addResourceResolver(resolver);
   }

   /**
    * Method addResourceResolver
    *
    * @param resolver
    */
   public void addResourceResolver(ResourceResolverSpi resolver) {
      this.getSignedInfo().addResourceResolver(resolver);
   }

   /**
    * Method checkSignatureValue
    *
    * @param cert
    * @return
    * @throws Exception
    */
   public boolean checkSignatureValue(X509Certificate cert) throws Exception {

      if (cert != null) {
         String SignatureMethodURI =
            this.getSignedInfo().getSignatureMethodURI();
         String jceSigID = JCEMapper.translateURItoJCEID(SignatureMethodURI);
         java.security.Signature signature =
            java.security.Signature.getInstance(jceSigID);

         signature.initVerify(cert);

         byte inputBytes[] = this.getSignedInfo().getCanonicalizedOctetStream();

         JavaUtils.writeBytesToFilename("signedInfo", inputBytes);
         signature.update(inputBytes);

         byte sigBytes[] = this.getSignatureValue();
         boolean verify = signature.verify(sigBytes);

         if (!this.getSignedInfo().verify()) {
            return false;
         }

         return verify;
      } else {
         throw new Exception("Didn't get a certificate");
      }
   }

   /**
    * Method checkSignatureValue
    *
    * @param pk
    * @return
    * @throws Exception
    */
   public boolean checkSignatureValue(java.security.PublicKey pk)
           throws Exception {

      if (!this.getSignedInfo().verify()) {
         return false;
      }

      String SignatureMethodURI = this.getSignedInfo().getSignatureMethodURI();
      String jceSigID = JCEMapper.translateURItoJCEID(SignatureMethodURI);
      java.security.Signature signature =
         java.security.Signature.getInstance(jceSigID);

      if (pk != null) {
         signature.initVerify(pk);
         cat.debug("PublicKey = " + pk);

         byte inputBytes[] = this._signedInfo.getCanonicalizedOctetStream();

         signature.update(inputBytes);

         byte sigBytes[] = this.getSignatureValue();

         cat.debug("SignatureValue = "
                   + HexDump.byteArrayToHexString(sigBytes));

         boolean verify = signature.verify(sigBytes);

         return verify;
      } else {
         throw new Exception("Didn't get a key");
      }
   }

   /**
    * This method is a proxy method for the {@link Manifest#addDocument} method
    *
    * @param referenceURI
    * @param trans
    * @param digestURI
    * @see Manifest#addDocument(org.apache.xml.security.signature.XMLSignatureInput, java.lang.String, org.apache.xml.security.transforms.Transforms, java.lang.String)
    * @throws XMLSignatureException
    */
   public void addDocument(
           String referenceURI, Transforms trans, String digestURI)
              throws XMLSignatureException {
      this._signedInfo.addDocument(this._baseURI, referenceURI, trans,
                                   digestURI);
   }

   /**
    * Method addDocument
    *
    * @param referenceURI
    * @param trans
    * @throws XMLSignatureException
    */
   public void addDocument(String referenceURI, Transforms trans)
           throws XMLSignatureException {
      this._signedInfo.addDocument(this._baseURI, referenceURI, trans,
                                   Constants.ALGO_ID_DIGEST_SHA1);
   }

   /**
    * Method addDocument
    *
    * @param referenceURI
    * @throws XMLSignatureException
    */
   public void addDocument(String referenceURI) throws XMLSignatureException {

      cat.debug("The baseURI is " + this._baseURI);
      this._signedInfo.addDocument(this._baseURI, referenceURI, null,
                                   Constants.ALGO_ID_DIGEST_SHA1);
   }

   /**
    * Method addToKeyInfoCompleteCertificate
    *
    * @param cert
    * @throws XMLSecurityException
    */
   public void addKeyInfo(X509Certificate cert) throws XMLSecurityException {

      X509Data x509data = new X509Data(this._doc);

      x509data.addCertificate(cert);
      this.getKeyInfo().add(x509data);
   }

   /**
    * Method addToKeyInfo
    *
    * @param pk
    */
   public void addKeyInfo(PublicKey pk) {
      this.getKeyInfo().add(pk);
   }

   static {
      org.apache.xml.security.Init.init();
   }
}

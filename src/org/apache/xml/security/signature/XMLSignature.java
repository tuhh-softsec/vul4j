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
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.xml.security.algorithms.*;
import org.apache.xml.security.algorithms.implementations.*;
import org.apache.xml.security.c14n.*;
import org.apache.xml.security.transforms.params.XPathContainer;
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
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


/**
 * Handles <code>&lt;ds:Signature&gt;</code> elements
 *
 * @author $Author$
 */
public class XMLSignature extends SignatureElementProxy {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(XMLSignature.class.getName());
   //J-
   /** MAC - Required HMAC-SHA1 */
   public static final String ALGO_ID_MAC_HMAC_SHA1 = Constants.SignatureSpecNS + "hmac-sha1";

   /** Signature - Required DSAwithSHA1 (DSS) */
   public static final String ALGO_ID_SIGNATURE_DSA = Constants.SignatureSpecNS + "dsa-sha1";

   /** Signature - Recommended RSAwithSHA1 */
   public static final String ALGO_ID_SIGNATURE_RSA = Constants.SignatureSpecNS + "rsa-sha1";
   public static final String ALGO_ID_SIGNATURE_RSA_SHA1 = Constants.SignatureSpecNS + "rsa-sha1";

   public static final String ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5 = Constants.MoreAlgorithmsSpecNS + "rsa-md5";
   public static final String ALGO_ID_SIGNATURE_RSA_RIPEMD160 = Constants.MoreAlgorithmsSpecNS + "rsa-ripemd160";
   public static final String ALGO_ID_SIGNATURE_RSA_SHA256 = Constants.MoreAlgorithmsSpecNS + "rsa-sha256";
   public static final String ALGO_ID_SIGNATURE_RSA_SHA384 = Constants.MoreAlgorithmsSpecNS + "rsa-sha384";
   public static final String ALGO_ID_SIGNATURE_RSA_SHA512 = Constants.MoreAlgorithmsSpecNS + "rsa-sha512";

   public static final String ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5 = Constants.MoreAlgorithmsSpecNS + "hmac-md5";
   public static final String ALGO_ID_MAC_HMAC_RIPEMD160 = Constants.MoreAlgorithmsSpecNS + "hmac-ripemd160";
   public static final String ALGO_ID_MAC_HMAC_SHA256 = Constants.MoreAlgorithmsSpecNS + "hmac-sha256";
   public static final String ALGO_ID_MAC_HMAC_SHA384 = Constants.MoreAlgorithmsSpecNS + "hmac-sha384";
   public static final String ALGO_ID_MAC_HMAC_SHA512 = Constants.MoreAlgorithmsSpecNS + "hmac-sha512";
   //J+

   /** ds:Signature.ds:SignedInfo element */
   SignedInfo _signedInfo = null;

   /** ds:Signature.ds:KeyInfo */
   KeyInfo _keyInfo = null;

   /** Field _followManifestsDuringValidation */
   boolean _followManifestsDuringValidation = false;

   /**
    * This creates a new <CODE>ds:Signature</CODE> Element and adds an empty
    * <CODE>ds:SignedInfo</CODE> to it.
    *
    * @param doc
    * @param BaseURI
    * @param signatureAlgorithmURI
    * @throws XMLSecurityException
    */
   public XMLSignature(
           Document doc, String BaseURI, String signatureAlgorithmURI)
              throws XMLSecurityException {

      super(doc);

      XMLUtils.addReturnToElement(this._constructionElement);

      this._baseURI = BaseURI;
      this._signedInfo =
         new SignedInfo(this._doc, Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS,
                        signatureAlgorithmURI);

      this._constructionElement.appendChild(this._signedInfo.getElement());
      XMLUtils.addReturnToElement(this._constructionElement);

      // create an empty SignatureValue; this is filled by setSignatureValueElement
      Element signatureValueElement =
         XMLUtils.createElementInSignatureSpace(this._doc,
                                                Constants._TAG_SIGNATUREVALUE);

      this._constructionElement.appendChild(signatureValueElement);
      XMLUtils.addReturnToElement(this._constructionElement);
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

      // check out SignedInfo child
      Element signedInfoElem = this.getChildElementLocalName(0,
                                  Constants.SignatureSpecNS,
                                  Constants._TAG_SIGNEDINFO);

      if (signedInfoElem == null) {
         Object exArgs[] = { Constants._TAG_SIGNEDINFO,
                             Constants._TAG_SIGNATURE };

         throw new XMLSignatureException("xml.WrongContent", exArgs);
      }

      this._signedInfo = new SignedInfo(signedInfoElem, BaseURI);

      // check out SignatureValue child
      Element signatureValueElement = this.getChildElementLocalName(0,
                                         Constants.SignatureSpecNS,
                                         Constants._TAG_SIGNATUREVALUE);

      if (signatureValueElement == null) {
         Object exArgs[] = { Constants._TAG_SIGNATUREVALUE,
                             Constants._TAG_SIGNATURE };

         throw new XMLSignatureException("xml.WrongContent", exArgs);
      }

      // <element ref="ds:KeyInfo" minOccurs="0"/>
      Element keyInfoElem = this.getChildElementLocalName(0,
                               Constants.SignatureSpecNS,
                               Constants._TAG_KEYINFO);

      if (keyInfoElem != null) {
         this._keyInfo = new KeyInfo(keyInfoElem, BaseURI);
      }
   }

   /**
    * Sets the <code>Id</code> attribute
    *
    * @param Id ID
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
    * @return the <code>Id</code> attribute
    */
   public String getId() {
      return this._constructionElement.getAttributeNS(null, Constants._ATT_ID);
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
    * @throws XMLSignatureException
    */
   public byte[] getSignatureValue() throws XMLSignatureException {

      try {
         Element signatureValueElem = this.getChildElementLocalName(0,
                                         Constants.SignatureSpecNS,
                                         Constants._TAG_SIGNATUREVALUE);
         byte[] signatureValue = Base64.decode(signatureValueElem);

         return signatureValue;
      } catch (Base64DecodingException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /**
    * Method setSignatureValueElement
    *
    * @param bytes
    * @throws XMLSignatureException
    */
   private void setSignatureValueElement(byte[] bytes)
           throws XMLSignatureException {

      if (this._state == MODE_SIGN) {
         Element signatureValueElem = this.getChildElementLocalName(0,
                                         Constants.SignatureSpecNS,
                                         Constants._TAG_SIGNATUREVALUE);
         NodeList children = signatureValueElem.getChildNodes();

         while (signatureValueElem.hasChildNodes()) {
            signatureValueElem.removeChild(signatureValueElem.getFirstChild());
         }

         String base64codedValue = Base64.encode(bytes);

         if (base64codedValue.length() > 76) {
            base64codedValue = "\n" + base64codedValue + "\n";
         }

         Text t = this._doc.createTextNode(base64codedValue);

         signatureValueElem.appendChild(t);
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

         Element keyInfoElement = this._keyInfo.getElement();

         try {
            Element nscontext = XMLUtils.createDSctx(this._doc, "ds",
                                                     Constants.SignatureSpecNS);
            Element firstObject =
               (Element) XPathAPI.selectSingleNode(this._constructionElement,
                                                   "./ds:"
                                                   + Constants._TAG_OBJECT
                                                   + "[1]", nscontext);

            if (firstObject != null) {
               this._constructionElement.insertBefore(keyInfoElement,
                                                      firstObject);
               this._constructionElement
                  .insertBefore(this._doc.createTextNode("\n"), firstObject);
            } else {
               this._constructionElement.appendChild(keyInfoElement);
               XMLUtils.addReturnToElement(this._constructionElement);
            }
         } catch (TransformerException ex) {
            ex.printStackTrace();
         }
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

         this._constructionElement.appendChild(object.getElement());
         XMLUtils.addReturnToElement(this._constructionElement);
      } catch (XMLSecurityException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /**
    * Method objectItem
    *
    * @param i
    * @return
    */
   public ObjectContainer getObjectItem(int i) {

      Element objElem = this.getChildElementLocalName(i,
                           Constants.SignatureSpecNS, Constants._TAG_OBJECT);

      try {
         return new ObjectContainer(objElem, this._baseURI);
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
      return this.length(Constants.SignatureSpecNS, Constants._TAG_OBJECT);
   }

   /**
    * Method sign
    *
    * @param privateKey
    * @throws XMLSignatureException
    */
   public void sign(PrivateKey privateKey) throws XMLSignatureException {

      try {
         if (this._state == MODE_SIGN) {

            // XMLUtils.indentSignature(this._constructionElement, "   ", 0);
            Element signatureMethodElement =
               this._signedInfo.getSignatureMethodElement();
            SignatureAlgorithm sa =
               new SignatureAlgorithm(signatureMethodElement,
                                      this.getBaseURI());

            sa.initSign(privateKey);

            SignedInfo si = this.getSignedInfo();

            si.generateDigestValues();

            byte signedInfoOctets[] = si.getCanonicalizedOctetStream();

            sa.update(signedInfoOctets);

            byte jcebytes[] = sa.sign();

            this.setSignatureValueElement(jcebytes);
         }
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
    * Method sign
    *
    * @param secretKey
    * @throws XMLSignatureException
    */
   public void sign(SecretKey secretKey) throws XMLSignatureException {

      try {
         if (this._state == MODE_SIGN) {

            // XMLUtils.indentSignature(this._constructionElement, "   ", 0);
            Element signatureMethodElement =
               this._signedInfo.getSignatureMethodElement();
            SignatureAlgorithm sa =
               new SignatureAlgorithm(signatureMethodElement,
                                      this.getBaseURI());

            sa.initSign(secretKey);

            SignedInfo si = this.getSignedInfo();

            si.generateDigestValues();

            byte signedInfoOctets[] = si.getCanonicalizedOctetStream();

            sa.update(signedInfoOctets);

            byte jcebytes[] = sa.sign();

            this.setSignatureValueElement(jcebytes);
         }
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
    * Method sign
    *
    * @return
    * @throws XMLSignatureException
    */
   public boolean verify() throws XMLSignatureException {

      if (this._state == MODE_VERIFY) {
         try {
            Element signatureMethodElement =
               this._signedInfo.getSignatureMethodElement();
            SignatureAlgorithm sa =
               new SignatureAlgorithm(signatureMethodElement,
                                      this.getBaseURI());

            /** @todo do real work here */
            return false;
         } catch (XMLSecurityException ex) {
            throw new XMLSignatureException("empty", ex);
         }
      }

      /** @todo fill in error message */
      throw new XMLSignatureException("empty");
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
    * @throws XMLSignatureException
    */
   public boolean checkSignatureValue(X509Certificate cert)
           throws XMLSignatureException {

      if (cert != null) {
         return this.checkSignatureValue(cert.getPublicKey());
      } else {
         Object exArgs[] = { "Didn't get a certificate" };

         throw new XMLSignatureException("empty", exArgs);
      }
   }

   /**
    * Method checkSignatureValue
    *
    * @param pk
    * @return
    * @throws XMLSignatureException
    */
   public boolean checkSignatureValue(Key pk) throws XMLSignatureException {

      if (pk == null) {
         Object exArgs[] = { "Didn't get a key" };

         throw new XMLSignatureException("empty", exArgs);
      }

      try {
         if (!this.getSignedInfo()
                 .verify(this._followManifestsDuringValidation)) {
            return false;
         }

         SignatureAlgorithm sa =
            new SignatureAlgorithm(this.getSignedInfo()
               .getSignatureMethodElement(), this.getBaseURI());

         cat.debug("SignatureMethodURI = " + sa.getAlgorithmURI());
         cat.debug("jceSigAlgorithm    = " + sa.getJCEAlgorithmString());
         cat.debug("jceSigProvider     = " + sa.getJCEProviderName());
         cat.debug("PublicKey = " + pk);
         sa.initVerify(pk);

         byte inputBytes[] = this._signedInfo.getCanonicalizedOctetStream();

         sa.update(inputBytes);

         byte sigBytes[] = this.getSignatureValue();

         cat.debug("SignatureValue = "
                   + HexDump.byteArrayToHexString(sigBytes));

         boolean verify = sa.verify(sigBytes);

         return verify;
      } catch (XMLSecurityException ex) {
         throw new XMLSignatureException("empty", ex);
      } catch (IOException ex) {
         throw new XMLSignatureException("empty", ex);
      }
   }

   /**
    * Method addDocument
    *
    * @param referenceURI
    * @param trans
    * @param digestURI
    * @param ReferenceId
    * @param ReferenceType
    * @throws XMLSignatureException
    */
   public void addDocument(
           String referenceURI, Transforms trans, String digestURI, String ReferenceId, String ReferenceType)
              throws XMLSignatureException {
      this._signedInfo.addDocument(this._baseURI, referenceURI, trans,
                                   digestURI, ReferenceId, ReferenceType);
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
                                   digestURI, null, null);
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
                                   Constants.ALGO_ID_DIGEST_SHA1, null, null);
   }

   /**
    * Method addDocument
    *
    * @param referenceURI
    * @throws XMLSignatureException
    */
   public void addDocument(String referenceURI) throws XMLSignatureException {

      this._signedInfo.addDocument(this._baseURI, referenceURI, null,
                                   Constants.ALGO_ID_DIGEST_SHA1, null, null);
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

   /**
    * Proxy method for {@link SignedInfo#createSecretKey(byte[])}
    *
    * @param secretKeyBytes
    * @return
    * @throws XMLSecurityException
    * @see SignedInfo#createSecretKey(byte[])
    */
   public SecretKey createSecretKey(byte[] secretKeyBytes)
           throws XMLSecurityException {
      return this.getSignedInfo().createSecretKey(secretKeyBytes);
   }

   /**
    * Method setFollowNestedManifests
    *
    * @param followManifests
    */
   public void setFollowNestedManifests(boolean followManifests) {
      this._followManifestsDuringValidation = followManifests;
   }

   /**
    * Method getBaseLocalName
    *
    * @return
    */
   public String getBaseLocalName() {
      return Constants._TAG_SIGNATURE;
   }
}

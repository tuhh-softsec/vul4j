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
package org.apache.xml.security.keys.provider;



import java.util.Date;
import java.io.*;
import java.security.KeyStoreException;
import java.security.cert.*;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.signature.Reference;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.transforms.*;
import org.w3c.dom.*;
import javax.crypto.SecretKey;
import javax.xml.transform.TransformerException;
import org.apache.xpath.CachedXPathAPI;
import java.util.Enumeration;
import java.util.Vector;


/**
 *
 * @author $Author$
 */
public class KeyStoreElement extends ElementProxy {

   /**
    * Constructor KeyStoreElement
    *
    * @param doc
    */
   public KeyStoreElement(Document doc) {

      super(doc);

      XMLUtils.addReturnToElement(this);
   }

   /**
    * Constructor KeyStoreElement
    *
    * @param element
    * @param BaseURI
    * @throws XMLSecurityException
    */
   public KeyStoreElement(Element element, String BaseURI)
           throws XMLSecurityException {
      super(element, BaseURI);
   }

   /**
    * Method getBaseLocalName
    *
    * @return
    */
   public String getBaseLocalName() {
      return ApacheKeyStoreConstants._TAG_KEYSTORE;
   }

   /**
    * Method getBaseNamespace
    *
    * @return
    */
   public String getBaseNamespace() {
      return ApacheKeyStoreConstants.ApacheKeyStore_NAMESPACE;
   }

   /**
    * This method removes all <CODE>ds:Signature</CODE> children from the
    * KeyStore.
    *
    */
   protected void removeOldSignatures() {

      Element oldSignatureElement = null;

      while ((oldSignatureElement =
              this.getChildElementLocalName(0, Constants
                 .SignatureSpecNS, Constants._TAG_SIGNATURE)) != null) {
         if (oldSignatureElement != null) {
            Node parent = oldSignatureElement.getParentNode();

            {

               // just beautifying; remove a possibly following return text node
               Node nextSibl = oldSignatureElement.getNextSibling();

               if ((nextSibl != null)
                       && (nextSibl.getNodeType() == Node.TEXT_NODE)) {
                  if (((Text) nextSibl).getData().equals("\n")) {
                     parent.removeChild(nextSibl);
                  }
               }
            }

            parent.removeChild(oldSignatureElement);
         }
      }
   }

   /**
    * Method sign
    *
    * @param password
    * @throws IOException
    */
   public void sign(char[] password) throws IOException {

      try {
         this.removeOldSignatures();

         XMLSignature signature =
            new XMLSignature(this._doc, "", XMLSignature.ALGO_ID_MAC_HMAC_SHA1);

         this._constructionElement.appendChild(signature.getElement());
         XMLUtils.addReturnToElement(this);

         Transforms enveloped = new Transforms(this._doc);

         enveloped.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
         signature.addDocument("", enveloped);

         SecretKey secretKey = this.generateKeyFromPass(signature, password);

         signature.sign(secretKey);
      } catch (XMLSignatureException ex) {
         throw new IOException(ex.getMessage());
      } catch (XMLSecurityException ex) {
         throw new IOException(ex.getMessage());
      }
   }

   /**
    * Method getSignatureElement
    *
    * @return
    * @throws XMLSecurityException
    */
   public Element getSignatureElement() throws XMLSecurityException {

      NodeList signatureElems =
         this._doc.getElementsByTagNameNS(Constants.SignatureSpecNS,
                                          Constants._TAG_SIGNATURE);

      if (signatureElems.getLength() == 0) {
         return null;
      } else if (signatureElems.getLength() == 1) {
         return (Element) signatureElems.item(0);
      } else {
         throw new XMLSecurityException("empty");
      }
   }

   /**
    * Method verify
    *
    * @param password
    * @return
    * @throws IOException
    */
   public boolean verify(char[] password) throws IOException {

      try {
         Element signatureElement = this.getSignatureElement();

         if (signatureElement == null) {
            throw new IOException(
               "There must be exactly one ds:Signature in the KeyStore");
         }

         XMLSignature signature = new XMLSignature(signatureElement,
                                                   "memory://");

         if (signature.getSignedInfo().getLength() != 1) {
            throw new IOException(
               "ds:Signature/ds:getSignedInfo must contain exactly one ds:Reference but it was "
               + signature.getSignedInfo().getLength());
         }

         Reference reference = signature.getSignedInfo().item(0);

         if (!reference.getURI().equals("")) {
            throw new IOException("ds:Reference/@URI!=\"\"");
         }

         Transforms transforms = reference.getTransforms();

         if ((transforms == null) || (transforms.getLength() != 1)) {
            throw new IOException(
               "There must be exactly one EnvelopedSignature Transform");
         }

         Transform transform = transforms.item(0);

         if (!transform.getURI()
                 .equals(Transforms.TRANSFORM_ENVELOPED_SIGNATURE)) {
            throw new IOException(
               "There must be exactly one EnvelopedSignature Transform");
         }

         SecretKey secretKey = this.generateKeyFromPass(signature, password);

         return signature.checkSignatureValue(secretKey);
      } catch (XMLSignatureException ex) {
         throw new IOException(ex.getMessage());
      } catch (XMLSecurityException ex) {
         throw new IOException(ex.getMessage());
      }
   }

   /**
    * Method generateKeyFromPass
    *
    * @param signature
    * @param password
    * @return
    * @throws XMLSecurityException
    */
   private static SecretKey generateKeyFromPass(
           XMLSignature signature, char[] password)
              throws XMLSecurityException {

      StringBuffer sb = new StringBuffer();

      sb.append(password);

      return signature.createSecretKey(sb.toString().getBytes());
   }

   /**
    * Method getNumberOfKeys
    *
    * @return
    */
   public int getNumberOfKeys() {
      return this.length(ApacheKeyStoreConstants.ApacheKeyStore_NAMESPACE,
                         ApacheKeyStoreConstants._TAG_KEY);
   }

   /**
    * Method getNumberOfCertificates
    *
    * @return
    */
   public int getNumberOfCertificates() {
      return this.length(ApacheKeyStoreConstants.ApacheKeyStore_NAMESPACE,
                         ApacheKeyStoreConstants._TAG_CERTIFICATE);
   }

   /**
    * Method getAliases
    *
    * @return
    */
   public Enumeration aliases() {

      try {
         CachedXPathAPI xpath = new CachedXPathAPI();
         Element nsctx = this._doc.createElement("nsctx");

         nsctx.setAttribute("xmlns:x",
                            ApacheKeyStoreConstants.ApacheKeyStore_NAMESPACE);

         NodeList aliasNodes =
            xpath.selectNodeList(this._doc,
                                 "/x:" + ApacheKeyStoreConstants._TAG_KEYSTORE
                                 + "/x:*/x:"
                                 + ApacheKeyStoreConstants._TAG_ALIAS
                                 + "/text()", nsctx);
         Vector result = new Vector(aliasNodes.getLength());

         for (int i = 0; i < aliasNodes.getLength(); i++) {
            Text aliasText = (Text) aliasNodes.item(i);

            result.add(aliasText.getNodeValue());
         }

         return new MyEnumeration(result);
      } catch (TransformerException ex) {}

      return new MyEnumeration(new Vector());
   }

   /**
    * Method isCertificateEntry
    *
    * @param alias
    * @return
    */
   public boolean isCertificateEntry(String alias) {

      Element certElem = this.getCertificateEntryElement(alias);

      return (certElem != null);
   }

   /**
    * Method isKeyEntry
    *
    * @param alias
    * @return
    */
   public boolean isKeyEntry(String alias) {

      Element certElem = this.getKeyEntryElement(alias);

      return (certElem != null);
   }

   /**
    * Method getCertificateEntryElement
    *
    * @param alias
    * @return
    */
   public Element getCertificateEntryElement(String alias) {

      try {
         CachedXPathAPI xpath = new CachedXPathAPI();
         Element nsctx = this._doc.createElement("nsctx");

         nsctx.setAttribute("xmlns:x",
                            ApacheKeyStoreConstants.ApacheKeyStore_NAMESPACE);

         String searchExpr = "/x:" + ApacheKeyStoreConstants._TAG_KEYSTORE
                             + "/x:" + ApacheKeyStoreConstants._TAG_CERTIFICATE
                             + "[./x:" + ApacheKeyStoreConstants._TAG_ALIAS
                             + "/text()=\"" + alias + "\"]";
         NodeList aliasNodes = xpath.selectNodeList(this._doc, searchExpr,
                                                    nsctx);

         if (aliasNodes.getLength() == 1) {
            return (Element) aliasNodes.item(0);
         }
      } catch (TransformerException ex) {
         ex.printStackTrace();
      }

      return null;
   }

   /**
    * Method getKeyEntryElement
    *
    * @param alias
    * @return
    */
   public Element getKeyEntryElement(String alias) {

      try {
         CachedXPathAPI xpath = new CachedXPathAPI();
         Element nsctx = this._doc.createElement("nsctx");

         nsctx.setAttribute("xmlns:x",
                            ApacheKeyStoreConstants.ApacheKeyStore_NAMESPACE);

         String searchExpr = "/x:" + ApacheKeyStoreConstants._TAG_KEYSTORE
                             + "/x:" + ApacheKeyStoreConstants._TAG_KEY
                             + "[./x:" + ApacheKeyStoreConstants._TAG_ALIAS
                             + "/text()=\"" + alias + "\"]";
         NodeList aliasNodes = xpath.selectNodeList(this._doc, searchExpr,
                                                    nsctx);

         if (aliasNodes.getLength() == 1) {
            return (Element) aliasNodes.item(0);
         }
      } catch (TransformerException ex) {
         ex.printStackTrace();
      }

      return null;
   }

   /**
    * Method getCertificate
    *
    * @param alias
    * @return
    */
   public Certificate getCertificate(String alias) {

      try {
         Element certElement = this.getCertificateEntryElement(alias);

         if (certElement == null) {
            return null;
         }

         CertificateElement certElemObject = new CertificateElement(certElement,
                                                this._baseURI);

         return certElemObject.getCertificate();
      } catch (CertificateException ex) {
         return null;
      } catch (XMLSecurityException ex) {
         return null;
      }
   }

   /**
    * Method getCreationDate
    *
    * @param alias
    * @return
    */
   public Date getCreationDate(String alias) {

      try {
         Element certElem = this.getCertificateEntryElement(alias);

         if (certElem != null) {
            CertificateElement kbt = new CertificateElement(certElem,
                                        this._baseURI);

            return kbt.getDate();
         }

         Element keyElem = this.getKeyEntryElement(alias);

         if (keyElem != null) {
            KeyElement kbt = new KeyElement(keyElem, this._baseURI);

            return kbt.getDate();
         }
      } catch (XMLSecurityException ex) {
         ;
      }

      return null;
   }

   /**
    * Method deleteEntry
    *
    * @param alias
    * @throws KeyStoreException
    */
   public void deleteEntry(String alias) throws KeyStoreException {

      Element element = this.getKeyEntryElement(alias);

      if (element != null) {
         Node parent = element.getParentNode();
         Node following = element.getNextSibling();

         if ((following != null) && (following.getNodeType() == Node.TEXT_NODE)
                 && ((Text) following).getData().equals("\n")) {
            parent.removeChild(following);
         }

         parent.removeChild(element);
      }

      element = this.getCertificateEntryElement(alias);

      if (element != null) {
         Node parent = element.getParentNode();
         Node following = element.getNextSibling();

         if ((following != null) && (following.getNodeType() == Node.TEXT_NODE)
                 && ((Text) following).getData().equals("\n")) {
            parent.removeChild(following);
         }

         parent.removeChild(element);
      }
   }

   /**
    * Method engineGetCertificateChain
    *
    * @param alias
    * @return
    */
   public Certificate[] getCertificateChain(String alias) {

      try {
         Element keyElement = this.getKeyEntryElement(alias);

         if (keyElement != null) {
            KeyElement ke = new KeyElement(keyElement, this._baseURI);

            return ke.getCertificateChain(alias);
         }
      } catch (XMLSecurityException ex) {
         ex.printStackTrace();
      }

      return new Certificate[0];
   }

   /**
    * Method setCertificateEntry
    *
    * @param alias
    * @param cert
    * @throws KeyStoreException
    */
   public void setCertificateEntry(String alias, Certificate cert)
           throws KeyStoreException {

      try {
         CertificateElement certificateElement =
            new CertificateElement(this._doc, alias, cert);

         this._constructionElement.appendChild(certificateElement.getElement());
         XMLUtils.addReturnToElement(this);
      } catch (XMLSecurityException ex) {
         throw new KeyStoreException(ex.getMessage());
      }
   }

   /**
    * Method setKeyEntry
    *
    * @param alias
    * @param key
    * @param chain
    * @throws KeyStoreException
    */
   public void setKeyEntry(String alias, byte[] key, Certificate[] chain)
           throws KeyStoreException {

      try {
         KeyElement keyElement = new KeyElement(this._doc, alias, key, chain);

         this._constructionElement.appendChild(keyElement.getElement());
         XMLUtils.addReturnToElement(this);
      } catch (XMLSecurityException ex) {
         throw new KeyStoreException(ex.getMessage());
      }
   }

   /**
    * Method setKeyEntry
    *
    * @param alias
    * @param k
    * @param password
    * @param chain
    * @throws KeyStoreException
    */
   public void setKeyEntry(
           String alias, Key k, char[] password, Certificate[] chain)
              throws KeyStoreException {

      try {
         KeyElement keyElement = new KeyElement(this._doc, alias, k, password,
                                                chain);

         this._constructionElement.appendChild(keyElement.getElement());
         XMLUtils.addReturnToElement(this);
      } catch (XMLSecurityException ex) {
         throw new KeyStoreException(ex.getMessage());
      }
   }

   /**
    * Method getKey
    *
    * @param alias
    * @param password
    * @return
    * @throws NoSuchAlgorithmException
    * @throws UnrecoverableKeyException
    */
   public Key getKey(String alias, char[] password)
           throws NoSuchAlgorithmException, UnrecoverableKeyException {

      try {
         KeyElement keyElement = new KeyElement(this.getKeyEntryElement(alias),
                                                this._baseURI);

         return keyElement.unwrap(password);
      } catch (XMLSecurityException ex) {
         throw new UnrecoverableKeyException(ex.getMessage());
      }
   }

   /**
    * Class MyEnumeration
    *
    * @author $Author$
    * @version $Revision$
    */
   public class MyEnumeration implements Enumeration {

      /** Field _v */
      Vector _v;

      /** Field _i */
      int _i;

      /**
       * Constructor MyEnumeration
       *
       * @param v
       */
      protected MyEnumeration(Vector v) {
         this._v = v;
         this._i = 0;
      }

      /**
       * Method hasMoreElements
       *
       * @return
       */
      public boolean hasMoreElements() {
         return this._i < this._v.size();
      }

      /**
       * Method nextElement
       *
       * @return
       */
      public Object nextElement() {

         if (this.hasMoreElements()) {
            return this._v.elementAt(this._i++);
         } else {
            return null;
         }
      }
   }
}

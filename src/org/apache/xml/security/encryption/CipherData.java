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



import org.w3c.dom.*;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.EncryptionElementProxy;
import org.apache.xml.security.utils.EncryptionConstants;
import org.apache.xml.security.utils.XMLUtils;


/**
 * This class maps to the <CODE>xenc:CipherData</CODE> element.
 *
 * @author $Author$
 */
public class CipherData extends EncryptionElementProxy {

   /*
   public CipherData(Document doc, CipherValue cipherValue) {
      this(doc, cipherValue, 0);
   }
   public CipherData(Document doc, byte ciphertext[], int Nonce) {
      this(doc, new CipherValue(doc, ciphertext), Nonce);
   }
   public CipherData(Document doc, CipherValue cipherValue, int Nonce) {
      super(doc, EncryptionConstants._TAG_CIPHERDATA);
      this._constructionElement.appendChild(this._doc.createTextNode("\n"));
      this._constructionElement.appendChild(cipherValue.getElement());
      this._constructionElement.appendChild(this._doc.createTextNode("\n"));
      this.setNonce(Nonce);
   }
   private void setNonce(int Nonce) {
      if (Nonce > 0) {
         this._constructionElement.setAttributeNS(null, EncryptionConstants._ATT_NONCE, (new Integer(Nonce)).toString());
      }
   }

   public int getNonce() {

      String nonceStr =
         this._constructionElement.getAttributeNS(null, EncryptionConstants._ATT_NONCE);

      if ((nonceStr != null) && (nonceStr.length() > 0)) {
         return (new Integer(nonceStr)).intValue();
      } else {
         return 0;
      }
   }
   */

   /**
    * Constructor CipherData
    *
    * @param doc
    */
   public CipherData(Document doc) {

      super(doc);

      XMLUtils.addReturnToElement(this._constructionElement);
   }

   /**
    * Constructor CipherData
    *
    * @param doc
    * @param cipherValue
    */
   public CipherData(Document doc, CipherValue cipherValue) {

      super(doc);

      XMLUtils.addReturnToElement(this._constructionElement);
      this._constructionElement.appendChild(cipherValue.getElement());
      XMLUtils.addReturnToElement(this._constructionElement);
   }

   /**
    * Constructor CipherData
    *
    * @param doc
    * @param cipherReference
    */
   public CipherData(Document doc, CipherReference cipherReference) {

      super(doc);

      XMLUtils.addReturnToElement(this._constructionElement);
      this._constructionElement.appendChild(cipherReference.getElement());
      XMLUtils.addReturnToElement(this._constructionElement);
   }

   /**
    * Constructor CipherData
    *
    * @param doc
    * @param ciphertext
    * @throws XMLSecurityException
    */
   public CipherData(Document doc, byte ciphertext[])
           throws XMLSecurityException {
      this(doc, new CipherValue(doc, ciphertext));
   }

   /**
    * Constructor CipherData
    *
    * @param element
    * @param BaseURI
    * @throws XMLSecurityException
    */
   public CipherData(Element element, String BaseURI)
           throws XMLSecurityException {
      super(element, BaseURI);
   }

   /**
    * Method getCipherValue
    *
    *
    * @throws XMLSecurityException
    */
   public CipherValue getCipherValue() throws XMLSecurityException {

      Element cipherValue = XMLUtils.getDirectChild(this._constructionElement,
                               EncryptionConstants._TAG_CIPHERVALUE,
                               EncryptionConstants.EncryptionSpecNS);

      if (cipherValue == null) {
         return null;
      } else {
         return new CipherValue(cipherValue, this._baseURI);
      }
   }

   /**
    * Method getCipherReference
    *
    *
    * @throws XMLSecurityException
    */
   public CipherReference getCipherReference() throws XMLSecurityException {

      Element cipherReference =
         XMLUtils.getDirectChild(this._constructionElement,
                                 EncryptionConstants._TAG_CIPHERREFERENCE,
                                 EncryptionConstants.EncryptionSpecNS);

      if (cipherReference == null) {
         return null;
      } else {
         return new CipherReference(cipherReference, this._baseURI);
      }
   }

   /**
    * Method isCipherValue
    *
    *
    */
   public boolean isCipherValue() {

      Element cipherValue = XMLUtils.getDirectChild(this._constructionElement,
                               EncryptionConstants._TAG_CIPHERVALUE,
                               EncryptionConstants.EncryptionSpecNS);

      if (cipherValue == null) {
         return false;
      } else {
         return true;
      }
   }

   /**
    * Method isCipherReference
    *
    *
    */
   public boolean isCipherReference() {

      Element cipherValue = XMLUtils.getDirectChild(this._constructionElement,
                               EncryptionConstants._TAG_CIPHERREFERENCE,
                               EncryptionConstants.EncryptionSpecNS);

      if (cipherValue == null) {
         return false;
      } else {
         return true;
      }
   }

   /**
    * Method isNotYetFilled
    *
    *
    */
   public boolean isNotYetFilled() {
      return (!this.isCipherReference() &&!this.isCipherValue());
   }

   /**
    * Method setCipherValue
    *
    * @param cipherValue
    * @throws XMLSecurityException
    */
   public void setCipherValue(CipherValue cipherValue)
           throws XMLSecurityException {

      if (this.isNotYetFilled()) {
         this._constructionElement.appendChild(cipherValue.getElement());
         XMLUtils.addReturnToElement(this._constructionElement);
      }
   }

   /**
    * Method setCipherReference
    *
    * @param cipherReference
    * @throws XMLSecurityException
    */
   public void setCipherReference(CipherReference cipherReference)
           throws XMLSecurityException {

      if (this.isNotYetFilled()) {
         this._constructionElement.appendChild(cipherReference.getElement());
         XMLUtils.addReturnToElement(this._constructionElement);
      }
   }

   public String getBaseLocalName() {
      return EncryptionConstants._TAG_CIPHERDATA;
   }
}

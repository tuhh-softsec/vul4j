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



import java.security.Key;
import org.w3c.dom.*;
import org.apache.xml.security.algorithms.encryption.EncryptionMethod;
import org.apache.xml.security.algorithms.encryption.params
   .EncryptionMethodParams;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.encryption.type.EncryptedType;


/**
 * This class maps to the <CODE>xenc:EncryptedKey</CODE> element.
 *
 * @author $Author$
 */
public class EncryptedKey extends EncryptionElementProxy
        implements EncryptedType {

   /**
    * Constructor EncryptedKey
    *
    * @param doc
    * @param encryptionMethod
    * @param keyInfo
    * @param cipherData
    * @param encryptionProperties
    * @param referenceList
    * @param CarriedKeyName
    * @param Id
    * @param Type
    * @param Recipient
    * @throws XMLSecurityException
    */
   public EncryptedKey(
           Document doc, EncryptionMethod encryptionMethod, KeyInfo keyInfo,
           CipherData cipherData, EncryptionProperties encryptionProperties, ReferenceList referenceList, String CarriedKeyName, String Id, String Type, String Recipient)
              throws XMLSecurityException {

      super(doc);

      XMLUtils.addReturnToElement(this._constructionElement);

      if (encryptionMethod != null) {
         if (!encryptionMethod.getUsableInEncryptedKey()) {
            Object exArgs[] = { encryptionMethod.getAlgorithmURI() };

            throw new XMLSecurityException(
               "encryption.algorithmCannotBeUsedForEncryptedKey", exArgs);
         }

         this._constructionElement.appendChild(encryptionMethod.getElement());
         XMLUtils.addReturnToElement(this._constructionElement);
      }

      if (keyInfo != null) {
         this._constructionElement.appendChild(keyInfo.getElement());
         XMLUtils.addReturnToElement(this._constructionElement);
      }
      {
         this._constructionElement.appendChild(cipherData.getElement());
         XMLUtils.addReturnToElement(this._constructionElement);
      }

      if (encryptionProperties != null) {
         this._constructionElement
            .appendChild(encryptionProperties.getElement());
         XMLUtils.addReturnToElement(this._constructionElement);
      }

      if (referenceList != null) {
         this._constructionElement.appendChild(referenceList.getElement());
         XMLUtils.addReturnToElement(this._constructionElement);
      }

      this.setCarriedKeyName(CarriedKeyName);
      this.setId(Id);
      this.setType(Type);
      this.setRecipient(Recipient);
   }

   /**
    * Constructor EncryptedKey
    *
    * @param doc
    * @param encryptionMethodURI
    * @param encryptionMethodParams
    * @param keyInfo
    * @param contentKey
    * @param wrapKey
    * @param encryptionProperties
    * @param referenceList
    * @param CarriedKeyName
    * @param Id
    * @param Type
    * @param Recipient
    * @throws XMLSecurityException
    */
   public EncryptedKey(
           Document doc, String encryptionMethodURI,
           EncryptionMethodParams encryptionMethodParams, KeyInfo keyInfo,
           Key contentKey, Key wrapKey, EncryptionProperties encryptionProperties, ReferenceList referenceList, String CarriedKeyName, String Id, String Type, String Recipient)
              throws XMLSecurityException {

      super(doc);

      XMLUtils.addReturnToElement(this._constructionElement);

      EncryptionMethod encryptionMethod = new EncryptionMethod(doc,
                                             encryptionMethodURI,
                                             encryptionMethodParams);

      if (!encryptionMethod.getUsableInEncryptedKey()) {
         Object exArgs[] = { encryptionMethod.getAlgorithmURI() };

         throw new XMLSecurityException(
            "encryption.algorithmCannotBeUsedForEncryptedKey", exArgs);
      }

      this._constructionElement.appendChild(encryptionMethod.getElement());
      XMLUtils.addReturnToElement(this._constructionElement);

      if (keyInfo != null) {
         this._constructionElement.appendChild(keyInfo.getElement());
         XMLUtils.addReturnToElement(this._constructionElement);
      }
      {
         byte wrappedKey[] = encryptionMethod.wrap(contentKey, wrapKey);
         CipherData cipherData = new CipherData(doc, wrappedKey);
         this._constructionElement.appendChild(cipherData.getElement());
         XMLUtils.addReturnToElement(this._constructionElement);
      }

      if (encryptionProperties != null) {
         this._constructionElement
            .appendChild(encryptionProperties.getElement());
         XMLUtils.addReturnToElement(this._constructionElement);
      }

      if (referenceList != null) {
         this._constructionElement.appendChild(referenceList.getElement());
         XMLUtils.addReturnToElement(this._constructionElement);
      }

      this.setCarriedKeyName(CarriedKeyName);
      this.setId(Id);
      this.setType(Type);
      this.setRecipient(Recipient);
   }

   /**
    * Constructor EncryptedKey
    *
    * @param element
    * @param BaseURI
    * @throws XMLSecurityException
    */
   public EncryptedKey(Element element, String BaseURI)
           throws XMLSecurityException {
      super(element, BaseURI);
   }

   /**
    * Method setReferenceList
    *
    * @param referenceList
    */
   private void setReferenceList(ReferenceList referenceList) {

      // If xenc:ReferenceList already exists do it
      // otherwise appendChild;
   }

   /**
    * Method getReferenceList
    *
    * @return
    * @throws XMLSecurityException
    */
   public ReferenceList getReferenceList() throws XMLSecurityException {

      int noOfReferences = this.length(EncryptionConstants.EncryptionSpecNS,
                                       EncryptionConstants._TAG_REFERENCELIST);

      if (noOfReferences > 1) {
         Object exArgs[] = { "More then one xenc:ReferenceList found" };

         throw new XMLSecurityException("empty", exArgs);
      } else if (noOfReferences == 1) {
         Element referenceListElem = this.getChildElementLocalName(0,
                                        EncryptionConstants.EncryptionSpecNS,
                                        EncryptionConstants._TAG_REFERENCELIST);

         return new ReferenceList(referenceListElem, this._baseURI);
      } else {
         return null;
      }
   }

   /**
    * Method addDataReference
    *
    * @param dataReference
    * @throws XMLSecurityException
    */
   public void addDataReference(DataReference dataReference)
           throws XMLSecurityException {

      if (this.getReferenceList() == null) {
         ReferenceList referenceList = new ReferenceList(this._doc);

         this.setReferenceList(referenceList);
      }

      this.getReferenceList().add(dataReference);
   }

   /**
    * Method addKeyReference
    *
    * @param keyReference
    * @throws XMLSecurityException
    */
   public void addKeyReference(KeyReference keyReference)
           throws XMLSecurityException {

      if (this.getReferenceList() == null) {
         ReferenceList referenceList = new ReferenceList(this._doc);

         this.setReferenceList(referenceList);
      }

      this.getReferenceList().add(keyReference);
   }

   /**
    * Method getLengthDataReference
    *
    * @return
    * @throws XMLSecurityException
    */
   public int getLengthDataReference() throws XMLSecurityException {

      ReferenceList referenceList = this.getReferenceList();

      if (referenceList == null) {
         return 0;
      } else {
         return referenceList.getLengthDataReference();
      }
   }

   /**
    * Method getLengthKeyReference
    *
    * @return
    * @throws XMLSecurityException
    */
   public int getLengthKeyReference() throws XMLSecurityException {

      ReferenceList referenceList = this.getReferenceList();

      if (referenceList == null) {
         return 0;
      } else {
         return referenceList.getLengthKeyReference();
      }
   }

   /**
    * Method itemDataReference
    *
    * @param i
    * @return
    * @throws XMLSecurityException
    */
   public DataReference itemDataReference(int i) throws XMLSecurityException {

      ReferenceList referenceList = this.getReferenceList();

      return referenceList.itemDataReference(i);
   }

   /**
    * Method itemKeyReference
    *
    * @param i
    * @return
    * @throws XMLSecurityException
    */
   public KeyReference itemKeyReference(int i) throws XMLSecurityException {

      ReferenceList referenceList = this.getReferenceList();

      return referenceList.itemKeyReference(i);
   }

   /**
    * Method setCarriedKeyName
    *
    * @param carriedKeyName
    * @throws XMLSecurityException
    */
   public void setCarriedKeyName(String carriedKeyName)
           throws XMLSecurityException {

      if ((carriedKeyName != null) && (carriedKeyName.length() > 0)
              && (this._state == MODE_CREATE)) {
         CarriedKeyName cn = this.getCarriedKeyName();

         if (cn != null) {
            cn.setCarriedKeyName(carriedKeyName);
         } else {
            cn = new CarriedKeyName(this._doc, carriedKeyName);

            this._constructionElement.appendChild(cn.getElement());
            XMLUtils.addReturnToElement(this._constructionElement);
         }
      }
   }

   /**
    * Method getCarriedKeyName
    *
    * @return
    * @throws XMLSecurityException
    */
   public CarriedKeyName getCarriedKeyName() throws XMLSecurityException {

      Element e =
         XMLUtils.getDirectChild(this._constructionElement,
                                 EncryptionConstants._TAG_CARRIEDKEYNAME,
                                 EncryptionConstants.EncryptionSpecNS);

      if (e != null) {
         return new CarriedKeyName(e, this._baseURI);
      } else {
         return null;
      }
   }

   /**
    * Method getRecipient
    *
    * @return
    */
   public String getRecipient() {
      return this._constructionElement
         .getAttributeNS(null, EncryptionConstants._ATT_RECIPIENT);
   }

   /**
    * Method setRecipient
    *
    * @param recipient
    */
   public void setRecipient(String recipient) {
      if (this._state == MODE_CREATE && recipient != null && recipient.length() > 0) {
      this._constructionElement.setAttributeNS(null, EncryptionConstants._ATT_RECIPIENT,
                                             recipient);
      }
   }

   /**
    * Method getEncryptionMethod
    *
    * @return
    * @throws XMLSecurityException
    */
   public EncryptionMethod getEncryptionMethod() throws XMLSecurityException {

      Element e =
         XMLUtils.getDirectChild(this._constructionElement,
                                 EncryptionConstants._TAG_ENCRYPTIONMETHOD,
                                 EncryptionConstants.EncryptionSpecNS);

      if (e != null) {
         return new EncryptionMethod(e, this._baseURI);
      } else {
         return null;
      }
   }

   /**
    * Method getKeyInfo
    *
    * @return
    * @throws XMLSecurityException
    */
   public KeyInfo getKeyInfo() throws XMLSecurityException {

      Element e = XMLUtils.getDirectChild(this._constructionElement,
                                          Constants._TAG_KEYINFO,
                                          Constants.SignatureSpecNS);

      if (e != null) {
         return new KeyInfo(e, this._baseURI);
      } else {
         return null;
      }
   }

   /**
    * Method getCipherData
    *
    * @return
    * @throws XMLSecurityException
    */
   public CipherData getCipherData() throws XMLSecurityException {

      Element e = XMLUtils.getDirectChild(this._constructionElement,
                                          EncryptionConstants._TAG_CIPHERDATA,
                                          EncryptionConstants.EncryptionSpecNS);

      if (e != null) {
         return new CipherData(e, this._baseURI);
      } else {
         return null;
      }
   }

   /**
    * Method getEncryptionProperties
    *
    * @return
    * @throws XMLSecurityException
    */
   public EncryptionProperties getEncryptionProperties()
           throws XMLSecurityException {

      Element e =
         XMLUtils.getDirectChild(this._constructionElement,
                                 EncryptionConstants._TAG_ENCRYPTIONPROPERTIES,
                                 EncryptionConstants.EncryptionSpecNS);

      if (e != null) {
         return new EncryptionProperties(e, this._baseURI);
      } else {
         return null;
      }
   }

   /**
    * Sets the <code>Id</code> attribute
    *
    * @param Id ID
    */
   public void setId(String Id) {

      if ((this._state == MODE_CREATE) && (Id != null) && (Id.length() != 0)) {
         this._constructionElement.setAttributeNS(null, EncryptionConstants._ATT_ID,
                                                Id);
         IdResolver.registerElementById(this._constructionElement, Id);
      }
   }

   /**
    * Returns the <code>Id</code> attribute
    *
    * @return the <code>Id</code> attribute
    */
   public String getId() {
      return this._constructionElement
         .getAttributeNS(null, EncryptionConstants._ATT_ID);
   }

   /**
    * Sets the <code>Type</code> attribute
    *
    * @param Type
    */
   public void setType(String Type) {

      if ((this._state == MODE_CREATE) && (Type != null)) {
         this._constructionElement.setAttributeNS(null, EncryptionConstants._ATT_TYPE,
                                                Type);
      }
   }

   /**
    * Method getType
    *
    * @return
    */
   public String getType() {
      return this._constructionElement
         .getAttributeNS(null, EncryptionConstants._ATT_TYPE);
   }

   /**
    * Method getTypeIsElement
    *
    * @return
    */
   public boolean getTypeIsElement() {

      String type = this.getType();

      if ((type == null) || (type.length() == 0)) {
         return false;
      }

      return type.equals(EncryptionConstants.TYPE_ELEMENT);
   }

   /**
    * Method getTypeIsContent
    *
    * @return
    */
   public boolean getTypeIsContent() {

      String type = this.getType();

      if ((type == null) || (type.length() == 0)) {
         return false;
      }

      return type.equals(EncryptionConstants.TYPE_CONTENT);
   }

   /**
    * Method getTypeIsMediaType
    *
    * @return
    */
   public boolean getTypeIsMediaType() {

      String type = this.getType();

      if ((type == null) || (type.length() == 0)) {
         return false;
      }

      return type.startsWith(EncryptionConstants.TYPE_MEDIATYPE);
   }

   /**
    * Method getMediaTypeOfType
    *
    * @return
    */
   public String getMediaTypeOfType() {

      if (this.getTypeIsMediaType()) {
         return this.getType()
            .substring(EncryptionConstants.TYPE_MEDIATYPE.length());
      }

      return null;
   }

   /**
    * Method getBaseLocalName
    *
    * @return
    */
   public String getBaseLocalName() {
      return EncryptionConstants._TAG_ENCRYPTEDKEY;
   }

   public Key unwrap(Key wrapKey, String wrappedKeyAlgoURI) throws XMLSecurityException {
      byte[] wrappedKey = this.getCipherData().getCipherValue().getCipherText();
      return this.getEncryptionMethod().unwrap(wrappedKey, wrapKey, wrappedKeyAlgoURI);
   }

   /**
    * Method main
    *
    * @param unused
    * @throws Exception
    */
   public static void main(String unused[]) throws Exception {

      org.apache.xml.security.Init.init();

      javax.xml.parsers.DocumentBuilderFactory dbf =
         javax.xml.parsers.DocumentBuilderFactory.newInstance();

      dbf.setNamespaceAware(true);

      javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.newDocument();
      EncryptionMethod em =
         new EncryptionMethod(doc, EncryptionConstants.ALGO_ID_KEYWRAP_AES128);
      Key wrapKey = em.createSecretKeyFromBytes(
         org.apache.xml.security.utils.HexDump.hexStringToByteArray(
            "00 01 02 03 04 05 06 07 08 09 0a 0b 0c 0d 0e 0f"));
      Key contentKey = em.createSecretKeyFromBytes(
         org.apache.xml.security.utils.HexDump.hexStringToByteArray(
            "00 01 02 03 04 05 06 07 08 09 0a 0b 0c 0d 0e 0f"));
      byte ciphertext[] = em.wrap(contentKey, wrapKey);
      KeyInfo ki = new KeyInfo(doc);

      ki.add(new org.apache.xml.security.keys.content.KeyName(doc,
              "Christian Geuer-Pollmann"));

      EncryptedKey ed = new EncryptedKey(doc, em, ki,
                                         new CipherData(doc, ciphertext), null,
                                         null, "Christian Geuer-Pollmann", "",
                                         EncryptionConstants.TYPE_CONTENT,
                                         "Ed Simon");

      doc.appendChild(ed.getElement());
      org.apache.xml.security.utils.XMLUtils.outputDOMc14nWithComments(doc,
              System.out);

      EncryptionMethod em2 = ed.getEncryptionMethod();
      byte[] ciphertext2 = ed.getCipherData().getCipherValue().getCipherText();
      Key decrypt = em2.createSecretKeyFromBytes(
         org.apache.xml.security.utils.HexDump.hexStringToByteArray(
            "00 01 02 03 04 05 06 07 08 09 0a 0b 0c 0d 0e 0f"));
      Key unwrapped =
         em2.unwrap(ciphertext2, wrapKey,
                    EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128);

      System.out.println();
      System.out.println();
      System.out.println();
      System.out.println("getTypeIsContent   " + ed.getTypeIsContent());
      System.out.println("getTypeIsElement   " + ed.getTypeIsElement());
      System.out.println("getMediaTypeOfType " + ed.getMediaTypeOfType());
      System.out.println("Decrypted: '"
                         + HexDump.byteArrayToHexString(unwrapped.getEncoded())
                         + "'");
      System.out
         .println("Match: "
                  + ed.getCarriedKeyName()
                     .matchesAgainstKeyInfo(ed.getKeyInfo()));
   }
}

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



import java.io.*;
import java.security.Key;
import org.apache.xml.security.algorithms.encryption.EncryptionMethod;
import org.apache.xml.security.algorithms.encryption.params
   .EncryptionMethodParams;
import org.apache.xml.security.c14n.*;
import org.apache.xml.security.encryption.type.EncryptedType;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.utils.*;
import org.w3c.dom.*;


/**
 * This class maps to the <CODE>xenc:EncryptedData</CODE> element.
 *
 * @author $Author$
 */
public class EncryptedData extends EncryptionElementProxy
        implements EncryptedType {

   /**
    * Constructor EncryptedData
    *
    * @param doc
    * @param encryptionMethod
    * @param keyInfo
    * @param cipherData
    * @param encryptionProperties
    * @param Id
    * @param Type
    * @param Nonce
    * @throws XMLSecurityException
    */
   public EncryptedData(
           Document doc, EncryptionMethod encryptionMethod, KeyInfo keyInfo, CipherData cipherData, EncryptionProperties encryptionProperties, String Id, String Type, int Nonce)
              throws XMLSecurityException {

      super(doc);

      XMLUtils.addReturnToElement(this._constructionElement);

      if (encryptionMethod != null) {
         if (!encryptionMethod.getUsableInEncryptedData()) {
            Object exArgs[] = { encryptionMethod.getAlgorithmURI() };

            throw new XMLSecurityException(
               "encryption.algorithmCannotBeUsedForEncryptedData", exArgs);
         }

         this._constructionElement.appendChild(encryptionMethod.getElement());
         XMLUtils.addReturnToElement(this._constructionElement);

         this._cachedEncryptionMethod = encryptionMethod;
      }

      if (keyInfo != null) {
         this._constructionElement.appendChild(keyInfo.getElement());
         XMLUtils.addReturnToElement(this._constructionElement);
      }

      if (cipherData == null) {

         //
         // the CipherData child will be filled by this object, so we only
         // create a place holder
         //
         cipherData = new CipherData(doc);
      }

      this._constructionElement.appendChild(cipherData.getElement());
      XMLUtils.addReturnToElement(this._constructionElement);

      if (encryptionProperties != null) {
         this._constructionElement
            .appendChild(encryptionProperties.getElement());
         XMLUtils.addReturnToElement(this._constructionElement);
      }

      this.setId(Id);
      this.setType(Type);
      this.setNonce(Nonce);
   }

   /**
    * Constructor EncryptedData
    *
    * @param doc
    * @param encryptionMethod
    * @param encryptionMethodParams
    * @param keyInfo
    * @param encryptionProperties
    * @param Id
    * @param Nonce
    * @throws XMLSecurityException
    */
   public EncryptedData(
           Document doc, String encryptionMethod, EncryptionMethodParams encryptionMethodParams, KeyInfo keyInfo, EncryptionProperties encryptionProperties, String Id, int Nonce)
              throws XMLSecurityException {

      this(doc,
           new EncryptionMethod(doc, encryptionMethod, encryptionMethodParams),
           keyInfo, (CipherData) null, encryptionProperties, Id, (String) null,
           Nonce);
   }

   /**
    * Constructor EncryptedData
    *
    * @param doc
    * @param encryptionMethod
    * @param encryptionMethodParams
    * @param keyInfo
    * @param cipherData
    * @param encryptionProperties
    * @param Id
    * @param Type
    * @param Nonce
    * @throws XMLSecurityException
    */
   public EncryptedData(
           Document doc, String encryptionMethod,
           EncryptionMethodParams encryptionMethodParams, KeyInfo keyInfo, CipherData cipherData, EncryptionProperties encryptionProperties, String Id, String Type, int Nonce)
              throws XMLSecurityException {

      this(doc,
           new EncryptionMethod(doc, encryptionMethod, encryptionMethodParams),
           keyInfo, cipherData, encryptionProperties, Id, Type, Nonce);
   }

   /**
    * Constructor EncryptedData
    *
    * @param element
    * @param BaseURI
    * @throws XMLSecurityException
    */
   public EncryptedData(Element element, String BaseURI)
           throws XMLSecurityException {
      super(element, BaseURI);
   }

   /**
    * Method getEncryptionMethod
    *
    * @return
    * @throws XMLSecurityException
    */
   public EncryptionMethod getEncryptionMethod() throws XMLSecurityException {

      if (this._cachedEncryptionMethod == null) {
         Element e =
            XMLUtils.getDirectChild(this._constructionElement,
                                    EncryptionConstants._TAG_ENCRYPTIONMETHOD,
                                    EncryptionConstants.EncryptionSpecNS);

         if (e != null) {
            this._cachedEncryptionMethod = new EncryptionMethod(e,
                    this._baseURI);
         }
      }

      return this._cachedEncryptionMethod;
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
    * Method setNonce
    *
    * @param Nonce
    */
   private void setNonce(int Nonce) {

      if (Nonce > 0) {
         this._constructionElement
            .setAttribute(EncryptionConstants._ATT_NONCE,
                          (new Integer(Nonce)).toString());
      }
   }

   /**
    * Method getNonce
    *
    * @return
    */
   public int getNonce() {

      String nonceStr =
         this._constructionElement.getAttribute(EncryptionConstants._ATT_NONCE);

      if ((nonceStr != null) && (nonceStr.length() > 0)) {
         return (new Integer(nonceStr)).intValue();
      } else {
         return 0;
      }
   }

   /**
    * Sets the <code>Id</code> attribute
    *
    * @param Id ID
    */
   public void setId(String Id) {

      if ((this._state == MODE_CREATE) && (Id != null) && (Id.length() != 0)) {
         this._constructionElement.setAttribute(EncryptionConstants._ATT_ID,
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
         .getAttribute(EncryptionConstants._ATT_ID);
   }

   /**
    * Method setType
    *
    * @param Type
    */
   public void setType(String Type) {

      if ((this._state == MODE_CREATE) && (Type != null)) {
         this._constructionElement.setAttribute(EncryptionConstants._ATT_TYPE,
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
         .getAttribute(EncryptionConstants._ATT_TYPE);
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
    * Method replace
    *
    * @param oldElement
    * @param newContent
    * @return
    */
   public static Element replace(Element oldElement, NodeList newContent) {

      /*
      if (oldElement == null) {
        throw new IllegalArgumentException("oldElement is null");
      }
      if (newContent == null) {
        throw new IllegalArgumentException("newContent is null");
      }
      */
      Document oldDocument = oldElement.getOwnerDocument();

      {
         HelperNodeList nl2 = new HelperNodeList();

         for (int i = 0; i < newContent.getLength(); i++) {
            if (oldDocument != newContent.item(i).getOwnerDocument()) {

               // both elements are in different documents so we have to import.
               nl2.appendChild(oldDocument.importNode(newContent.item(i),
                                                      true));
            } else {
               nl2.appendChild(newContent.item(i));
            }
         }

         newContent = nl2;
      }

      Node parent = oldElement.getParentNode();

      if (parent == oldDocument) {

         //
         // we cannot use replaceChild because this throws DOMException
         //
         NodeList topLevelNodes = oldDocument.getChildNodes();

         if (topLevelNodes.getLength() == 1) {
            Node returnValue = oldDocument.removeChild(oldElement);

            for (int i = 0; i < newContent.getLength(); i++) {
               if (newContent.item(i).getNodeType() != Node.TEXT_NODE) {
                  oldDocument.appendChild(newContent.item(i));
               }
            }

            return (Element) returnValue;
         } else {
            int i = 0;

            searchForRootElem: for (i = 0; i < topLevelNodes.getLength(); i++) {
               if (topLevelNodes.item(i) == oldElement) {
                  break searchForRootElem;
               }
            }

            if (i == topLevelNodes.getLength() - 1) {
               Node returnValue = oldDocument.removeChild(oldElement);

               for (int j = 0; j < newContent.getLength(); j++) {
                  oldDocument.appendChild(newContent.item(j));
               }

               return (Element) returnValue;
            } else {
               Node returnValue = oldDocument.removeChild(oldElement);
               Node insertBefore = topLevelNodes.item(i);

               for (int j = 0; j < newContent.getLength(); j++) {
                  oldDocument.insertBefore(newContent.item(j), insertBefore);
               }

               return (Element) returnValue;
            }
         }
      } else {
         for (int j = 0; j < newContent.getLength(); j++) {
            parent.insertBefore(newContent.item(j), oldElement);
         }

         return (Element) parent.removeChild(oldElement);
      }
   }

   /**
    * Replaces an old Element by a new one
    *
    * @param oldElement the old Element which has to be removed from the Document
    * @param newElement the new Element which has to be place in the position of <CODE>oldElement</CODE>
    * @return the removed element
    */
   public static Element replace(Element oldElement, Element newElement) {

      HelperNodeList newContent = new HelperNodeList();

      newContent.appendChild(newElement);

      return replace(oldElement, newContent);
   }

   /**
    * Method replace
    *
    * @param oldElement
    * @param plaintextBytes
    * @return
    * @throws XMLSecurityException
    */
   public static Element replace(Element oldElement, byte[] plaintextBytes)
           throws XMLSecurityException {

      try {
         javax.xml.parsers.DocumentBuilderFactory dbf =
            javax.xml.parsers.DocumentBuilderFactory.newInstance();

         dbf.setNamespaceAware(true);

         javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
         Document doc =
            db.parse(new java.io.ByteArrayInputStream(plaintextBytes));
         HelperNodeList newContent = new HelperNodeList();

         for (int i = 0; i < doc.getChildNodes().getLength(); i++) {
            newContent.appendChild(doc.getChildNodes().item(i));
         }

         return replace(oldElement, newContent);
      } catch (javax.xml.parsers.ParserConfigurationException ex) {
         throw new XMLSecurityException("empty", ex);
      } catch (java.io.IOException ex) {
         throw new XMLSecurityException("empty", ex);
      } catch (org.xml.sax.SAXException ex) {
         throw new XMLSecurityException("empty", ex);
      }
   }

   /** Field _cachedEncryptionMethod */
   EncryptionMethod _cachedEncryptionMethod = null;

   /**
    * Method createSecretKeyFromBytes
    *
    * @param encodedKey
    * @return
    * @throws XMLSecurityException
    */
   public Key createSecretKeyFromBytes(byte encodedKey[])
           throws XMLSecurityException {
      return this.getEncryptionMethod().createSecretKeyFromBytes(encodedKey);
   }

   /**
    * Method encryptAndReplace
    *
    * @param plaintextElement
    * @param secretKey
    * @throws XMLSecurityException
    */
   public void encryptElementAndReplace(Element plaintextElement, Key secretKey)
           throws XMLSecurityException {

      EncryptionMethod em = this.getEncryptionMethod();
      Canonicalizer c14n =
         Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
      byte plaintext[] = c14n.canonicalize(plaintextElement);
      byte ciphertext[] = em.encrypt(plaintext, secretKey, this.getNonce());

      this.getCipherData().setCipherValue(new CipherValue(this._doc,
              ciphertext));
      this.setType(EncryptionConstants.TYPE_ELEMENT);
      EncryptedData.replace(plaintextElement, this._constructionElement);
   }

   /**
    * This method is the old implementation of {@link #encryptContentAndReplace()}.
    *
    * @param parentOfPlaintext the parent of the Nodes which are to be encrypted. All child nodes will be encrypted but not the parent itself.
    * @param contentEncryptionKey the {@link Key} which is used to encrypt the data
    * @throws XMLSecurityException
    */
   private void encryptContentAndReplace_old(
           Node parentOfPlaintext, Key contentEncryptionKey)
              throws XMLSecurityException {

      EncryptionMethod em = this.getEncryptionMethod();
      byte plaintext[] = null;

      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();

         for (int i = 0; i < parentOfPlaintext.getChildNodes().getLength();
                 i++) {
            Node plaintextItem = parentOfPlaintext.getChildNodes().item(i);

            // we cannot c14nize Comments, and PIs because the c14nizer appends CRs to the String
            if (plaintextItem.getNodeType() == Node.COMMENT_NODE) {
               baos.write(("<!--" + ((Comment) plaintextItem).getData()
                           + "-->").getBytes());
            } else if (plaintextItem.getNodeType()
                       == Node.PROCESSING_INSTRUCTION_NODE) {
               baos.write(("<?"
                           + ((ProcessingInstruction) plaintextItem).getTarget()
                           + " "
                           + ((ProcessingInstruction) plaintextItem).getData()
                           + "?>").getBytes());
            } else if (plaintextItem.getNodeType() == Node.TEXT_NODE) {
               baos.write((((Text) plaintextItem).getData()).getBytes());
            } else {

               // we have to create a new Canonicalizer for each Node because it stores state ;-(
               Canonicalizer c14n =
                  Canonicalizer
                     .getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);

               baos.write(c14n.canonicalize(plaintextItem));
            }
         }

         plaintext = baos.toByteArray();
      } catch (Exception ex) {
         throw new XMLSecurityException("empty", ex);
      }

      byte ciphertext[] = em.encrypt(plaintext, contentEncryptionKey,
                                     this.getNonce());

      this.getCipherData().setCipherValue(new CipherValue(this._doc,
              ciphertext));
      this.setType(EncryptionConstants.TYPE_CONTENT);

      while (parentOfPlaintext.hasChildNodes()) {
         parentOfPlaintext.removeChild(parentOfPlaintext.getLastChild());
      }

      parentOfPlaintext.appendChild(this._constructionElement);
   }

   /**
    * Encrypts all child {@link Node}s of a given {@link Element}.
    *
    * @param parentOfPlaintext the parent of the Nodes which are to be encrypted. All child nodes will be encrypted but not the parent itself.
    * @param contentEncryptionKey the {@link Key} which is used to encrypt the data
    * @throws XMLSecurityException
    */
   public void encryptContentAndReplace(
           Node parentOfPlaintext, Key contentEncryptionKey)
              throws XMLSecurityException {

      encryptContentAndReplace(parentOfPlaintext.getFirstChild(),
                               parentOfPlaintext.getChildNodes().getLength(),
                               contentEncryptionKey);
   }

   /**
    * Encrypts <B>some</B> child {@link Node}s of a given {@link Element}.
    *
    * @param firstPlaintextNode the first Node to be encrypted
    * @param length the total number of Nodes to be encrypted (the firstPlaintextNode and (length-1) next siblings)
    * @param contentEncryptionKey the {@link Key} which is used to encrypt the data
    * @throws XMLSecurityException
    */
   public void encryptContentAndReplace(
           Node firstPlaintextNode, int length, Key contentEncryptionKey)
              throws XMLSecurityException {

      try {
         EncryptionMethod em = this.getEncryptionMethod();
         byte plaintext[] = null;
         Node parent = firstPlaintextNode.getParentNode();
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         Node currentNode = firstPlaintextNode;
         int i = 0;

         while (i < length) {
            if (currentNode == null) {
               throw new IndexOutOfBoundsException(
                  "The index " + length + " is out of bounds: maximum is "
                  + (i - 1));
            }

            // we cannot c14nize Comments, and PIs because the c14nizer appends CRs to the String
            if (currentNode.getNodeType() == Node.COMMENT_NODE) {
               baos.write(("<!--" + ((Comment) currentNode).getData()
                           + "-->").getBytes());
            } else if (currentNode.getNodeType()
                       == Node.PROCESSING_INSTRUCTION_NODE) {
               baos.write(("<?"
                           + ((ProcessingInstruction) currentNode).getTarget()
                           + " "
                           + ((ProcessingInstruction) currentNode).getData()
                           + "?>").getBytes());
            } else if (currentNode.getNodeType() == Node.TEXT_NODE) {
               baos.write((((Text) currentNode).getData()).getBytes());
            } else {

               // we have to create a new Canonicalizer for each Node because it stores state ;-(
               Canonicalizer c14n =
                  Canonicalizer
                     .getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);

               baos.write(c14n.canonicalize(currentNode));
            }

            currentNode = currentNode.getNextSibling();
            i = i + 1;
         }

         Node insertBeforeNode = currentNode;

         plaintext = baos.toByteArray();

         byte ciphertext[] = em.encrypt(plaintext, contentEncryptionKey,
                                        this.getNonce());

         this.getCipherData().setCipherValue(new CipherValue(this._doc,
                 ciphertext));
         this.setType(EncryptionConstants.TYPE_CONTENT);

         int start = 0;

         for (currentNode = parent.getFirstChild();
                 currentNode != firstPlaintextNode;
                 currentNode = currentNode.getNextSibling()) {
            start++;
         }

         for (i = 0; i < length; i++) {
            parent.removeChild(parent.getChildNodes().item(start));
         }

         parent.insertBefore(this._constructionElement, insertBeforeNode);
      } catch (XMLSecurityException ex) {
         throw ex;
      } catch (Exception ex) {
         throw new XMLSecurityException("empty", ex);
      }
   }

   /**
    * Method decryptAndReplace
    *
    * @param contentDecryptionKey
    * @throws XMLSecurityException
    */
   public void decryptAndReplace(Key contentDecryptionKey)
           throws XMLSecurityException {

      EncryptionMethod em = this.getEncryptionMethod();
      byte ciphertext[] = this.getCipherData().getCipherValue().getCipherText();
      byte plaintext[] = em.decrypt(ciphertext, contentDecryptionKey,
                                    this.getNonce());

      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         String container = "container";

         baos.write((new String("<" + container + ">")).getBytes());
         baos.write(plaintext);
         baos.write((new String("</" + container + ">")).getBytes());

         javax.xml.parsers.DocumentBuilderFactory dbf =
            javax.xml.parsers.DocumentBuilderFactory.newInstance();

         dbf.setNamespaceAware(true);

         javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
         Document doc2 =
            db.parse(new java.io.ByteArrayInputStream(baos.toByteArray()));
         Element doc2Elem = doc2.getDocumentElement();
         HelperNodeList newContent = new HelperNodeList();

         for (int i = 0; i < doc2Elem.getChildNodes().getLength(); i++) {
            newContent.appendChild(doc2Elem.getChildNodes().item(i));
         }

         replace(this._constructionElement, newContent);
      } catch (javax.xml.parsers.ParserConfigurationException ex) {
         throw new XMLSecurityException("empty", ex);
      } catch (java.io.IOException ex) {
         throw new XMLSecurityException("empty", ex);
      } catch (org.xml.sax.SAXException ex) {
         throw new XMLSecurityException("empty", ex);
      }
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
      Element root = doc.createElement("root");
      int NonceLength = 0;
      String realContent = "1 USD           ";
      String desired = "999.999.999 EUR ";
      String estimated = realContent;

      {
         root.appendChild(doc.createTextNode(realContent));

         /*
         root.appendChild(doc.createComment("afasd"));
         root.appendChild(doc.createProcessingInstruction("sfd",
                 "d sdf kjghkds "));
         */
         doc.appendChild(doc.createComment(" 0 "));
         doc.appendChild(doc.createComment(" 1 "));
         doc.appendChild(root);
         doc.appendChild(doc.createComment(" 2 "));
         doc.appendChild(doc.createComment(" 3 "));
         System.out.println(
            "------------------------------------------------------------");
         XMLUtils.outputDOMc14nWithComments(doc, System.out);
         System.out.println();
      }

      Key cek;

      {
         KeyInfo ki = new KeyInfo(doc);

         ki.add(new org.apache.xml.security.keys.content.KeyName(doc,
                 "Christian Geuer-Pollmann"));

         EncryptedData ed =
            new EncryptedData(doc,
                              EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128,
                              null, ki, null, "myFirstEncryptedElement",
                              NonceLength);

         cek = ed.createSecretKeyFromBytes(
            org.apache.xml.security.utils.HexDump.hexStringToByteArray(
               "00 01 02 03 04 05 06 07 08 09 0a 0b 0c 0d 0e 0f"));

         ed.encryptContentAndReplace(root, cek);

         // ed.encryptElementAndReplace(root, cek);
         // ed.encryptContentAndReplace(doc, cek);
         // ed.encryptContentAndReplace(doc.getChildNodes().item(2), 10, cek);
         System.out.println(
            "------------------------------------------------------------");
         XMLUtils.outputDOMc14nWithComments(doc, System.out);
         System.out.println();
      }


      {
         org.apache.xpath.CachedXPathAPI xpath =
            new org.apache.xpath.CachedXPathAPI();
         Element nsctx = doc.createElement("nsctx");

         nsctx.setAttribute("xmlns:xenc", EncryptionConstants.EncryptionSpecNS);

         Element encryptedDataElem = (Element) xpath.selectSingleNode(doc,
                                        "//xenc:EncryptedData", nsctx);
         EncryptedData ed2 = new EncryptedData(encryptedDataElem, "memory://");
/*
         byte[] ciphertext =
            ed2.getCipherData().getCipherValue().getCipherText();

         System.out.println(
            "------------------------------------------------------------");

         {
            for (int i = 0; i < ed2.getEncryptionMethod().getIvLength(); i++) {
               System.out.print("XXX");
            }

            System.out.println();
         }

         System.out.println(HexDump.byteArrayToHexString(ciphertext));

         // byte[] newRandom = PRNG.createBytes(overWriteLength);
         int blockSize = ed2.getEncryptionMethod().getBlockSize();
         int ivSize = ed2.getEncryptionMethod().getIvLength();
         int modifyableBytes = ed2.getEncryptionMethod().getBlockSize()
                               - (ed2.getNonce()
                                  % ed2.getEncryptionMethod().getBlockSize());
         byte estimatedBytes[] = estimated.getBytes("UTF-8");
         byte desiredBytes[] = desired.getBytes("UTF-8");
         int differenceSize = min(modifyableBytes, estimatedBytes.length,
                                  desiredBytes.length);
         byte difference[] = new byte[differenceSize];

         for (int i = 0; i < difference.length; i++) {
            difference[i] = (byte) (estimatedBytes[i] ^ desiredBytes[i]);
         }

         {
            for (int i = 0; i < ed2.getNonce(); i++) {
               System.out.print("   ");
            }

            System.out.println(HexDump.byteArrayToHexString(difference));
         }

         for (int i = 0; i < difference.length; i++) {
            ciphertext[ed2.getNonce() + i] ^= difference[i];
         }

         System.out.println(HexDump.byteArrayToHexString(ciphertext));
         ed2.getCipherData().getCipherValue().setCipherText(ciphertext);
         XMLUtils.outputDOMc14nWithComments(doc, System.out);
         System.out.println();
         System.out.println(
            "------------------------------------------------------------");
*/


         ed2.decryptAndReplace(cek);
         System.out.println(
            "------------------------------------------------------------");
         XMLUtils.outputDOMc14nWithComments(doc, System.out);
         System.out.println();
         System.out.println(
            "------------------------------------------------------------");
      }
   }

   /**
    * Method min
    *
    * @param a
    * @param b
    * @param c
    * @return
    */
   public static int min(int a, int b, int c) {
      return min(min(a, b), c);
   }

   /**
    * Method min
    *
    * @param a
    * @param b
    * @return
    */
   public static int min(int a, int b) {

      if (a < b) {
         return a;
      }

      return b;
   }

   public String getBaseLocalName() {
      return EncryptionConstants._TAG_ENCRYPTEDDATA;
   }
}

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
package org.apache.xml.security.utils;



import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import org.apache.xml.security.c14n.*;
import org.apache.xml.security.exceptions.*;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.*;
import java.math.BigInteger;
import javax.xml.transform.TransformerException;
import org.apache.xpath.XPathAPI;


/**
 *
 * @author $Author$
 */
public abstract class ElementProxy {

   /** Field cat */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(ElementProxy.class.getName());
   //J-
   public static final int MODE_SIGN = 0;
   public static final int MODE_VERIFY = 1;

   public static final int MODE_ENCRYPT = 0;
   public static final int MODE_DECRYPT = 1;

   public static final int MODE_CREATE = 0;
   public static final int MODE_PROCESS = 1;

   public static final int MODE_UNKNOWN = 2;
   protected int _state = MODE_UNKNOWN;
   //J+

   /**
    * Method getBaseNamespace
    *
    * @return
    */
   public abstract String getBaseNamespace();

   /** Field _constructionElement */
   protected Element _constructionElement = null;

   /** Field _baseURI */
   protected String _baseURI = null;

   /** Field _doc */
   protected Document _doc = null;

   /**
    * Constructor ElementProxy
    *
    */
   public ElementProxy() {

      this._doc = null;
      this._state = ElementProxy.MODE_UNKNOWN;
      this._baseURI = null;
      this._constructionElement = null;
   }

   /**
    * Constructor ElementProxy
    *
    * @param doc
    * @param localname
    * @param namespace
    */
   public ElementProxy(Document doc, String localname, String namespace) {

      if (doc == null) {
         throw new RuntimeException("Document is null");
      }

      this._doc = doc;
      this._state = ElementProxy.MODE_CREATE;

      String prefix = ElementProxy.getDefaultPrefix(namespace);

      if (namespace == null) {
         this._constructionElement = doc.createElement(localname);
      } else {
         if ((prefix == null) || (prefix.length() == 0)) {
            this._constructionElement = doc.createElementNS(namespace,
                    localname);

            this._constructionElement.setAttribute("xmlns", namespace);
         } else {
            this._constructionElement = doc.createElementNS(namespace,
                    prefix + ":" + localname);

            this._constructionElement.setAttribute("xmlns:" + prefix,
                                                   namespace);
         }
      }
   }

   /**
    * Method setElement
    *
    * @param element
    * @param BaseURI
    * @throws XMLSecurityException
    */
   public void setElement(Element element, String BaseURI)
           throws XMLSecurityException {

      if (element == null) {
         throw new XMLSecurityException("ElementProxy.nullElement");
      }

      cat.debug("setElement(" + element.getTagName() + ", \"" + BaseURI + "\"");

      this._doc = element.getOwnerDocument();
      this._state = ElementProxy.MODE_PROCESS;
      this._constructionElement = element;
      this._baseURI = BaseURI;
   }

   /**
    * Constructor ElementProxy
    *
    * @param element
    * @param BaseURI
    * @param localname
    * @throws XMLSecurityException
    */
   public ElementProxy(Element element, String BaseURI, String localname)
           throws XMLSecurityException {

      if (element == null) {
         throw new XMLSecurityException("ElementProxy.nullElement");
      }

      cat.debug("setElement(" + element.getTagName() + ", \"" + BaseURI + "\"");

      this._doc = element.getOwnerDocument();
      this._state = ElementProxy.MODE_PROCESS;
      this._constructionElement = element;
      this._baseURI = BaseURI;

      this.guaranteeThatElementInCorrectSpace(localname);

      // this.setElement(element, BaseURI);
   }

   /**
    * Returns the Element which was constructed by the Object.
    *
    * @return the Element which was constructed by the Object.
    */
   public final Element getElement() {
      return this._constructionElement;
   }

   /**
    * Returns the Element plus a leading and a trailing CarriageReturn Text node.
    *
    * @return the Element which was constructed by the Object.
    */
   public final NodeList getElementPlusReturns() {

      HelperNodeList nl = new HelperNodeList();

      nl.appendChild(this._doc.createTextNode("\n"));
      nl.appendChild(this.getElement());
      nl.appendChild(this._doc.createTextNode("\n"));

      return nl;
   }

   /**
    * Method getBaseURI
    *
    * @return
    */
   public String getBaseURI() {
      return this._baseURI;
   }

   /**
    * Method guaranteeThatElementInCorrectSpace
    *
    * @param localname
    * @throws XMLSecurityException
    */
   public void guaranteeThatElementInCorrectSpace(String localname)
           throws XMLSecurityException {

      if ((localname == null) || (localname.equals(""))
              || (this._constructionElement == null)
              || (this._constructionElement.getNamespaceURI() == null)
              || (!this._constructionElement.getLocalName().equals(localname))
              || (!this._constructionElement.getNamespaceURI()
                 .equals(this.getBaseNamespace()))) {
         Object exArgs[] = { localname,
                             this._constructionElement.getLocalName() };

         throw new XMLSecurityException("xml.WrongElement", exArgs);
      }
   }

   /**
    * Method setVal
    *
    * @param bi
    * @param localname
    */
   public void addBigIntegerElement(BigInteger bi, String localname) {

      if (bi != null) {
         Element e = XMLUtils.createElementInSignatureSpace(this._doc,
                        localname);

         Base64.fillElementWithBigInteger(e, bi);
         this._constructionElement.appendChild(e);
         this._constructionElement.appendChild(this._doc.createTextNode("\n"));
      }
   }

   /**
    * Method addBase64Element
    *
    * @param bytes
    * @param localname
    */
   public void addBase64Element(byte[] bytes, String localname) {

      if (bytes != null) {
         Element e = Base64.encodeToElement(this._doc, localname, bytes);

         this._constructionElement.appendChild(e);
         this._constructionElement.appendChild(this._doc.createTextNode("\n"));
      }
   }

   /**
    * Method addTextElement
    *
    * @param text
    * @param localname
    */
   public void addTextElement(String text, String localname) {

      Element e = XMLUtils.createElementInSignatureSpace(this._doc, localname);
      Text t = this._doc.createTextNode(text);

      e.appendChild(t);
      this._constructionElement.appendChild(e);
      this._constructionElement.appendChild(this._doc.createTextNode("\n"));
   }

   /**
    * Method addBase64Text
    *
    * @param bytes
    */
   public void addBase64Text(byte[] bytes) {

      if (bytes != null) {
         Text t = this._doc.createTextNode(Base64.encode(bytes));

         this._constructionElement.appendChild(t);
      }
   }

   /**
    * Method addText
    *
    * @param text
    */
   public void addText(String text) {

      if (text != null) {
         Text t = this._doc.createTextNode(text);

         this._constructionElement.appendChild(t);
      }
   }

   /**
    * Method getVal
    *
    * @param localname
    * @param namespace
    * @return
    * @throws XMLSecurityException
    */
   public BigInteger getBigIntegerFromChildElement(
           String localname, String namespace) throws XMLSecurityException {

      try {
         Element nscontext = XMLUtils.createDSctx(this._doc, "x", namespace);
         Text t = (Text) XPathAPI.selectSingleNode(this._constructionElement,
                                                   "./x:" + localname
                                                   + "/text()", nscontext);

         return Base64.decodeBigIntegerFromText(t);
      } catch (TransformerException ex) {
         throw new XMLSecurityException("empty", ex);
      }
   }

   /**
    * Method getBytesFromChildElement
    *
    * @param localname
    * @param namespace
    * @return
    * @throws XMLSecurityException
    */
   public byte[] getBytesFromChildElement(String localname, String namespace)
           throws XMLSecurityException {

      try {
         Element nscontext = XMLUtils.createDSctx(this._doc, "x", namespace);
         Element e =
            (Element) XPathAPI.selectSingleNode(this._constructionElement,
                                                "./x:" + localname, nscontext);

         return Base64.decode(e);
      } catch (TransformerException ex) {
         throw new XMLSecurityException("empty", ex);
      }
   }

   /**
    * Method getTextFromChildElement
    *
    * @param localname
    * @param namespace
    * @return
    * @throws XMLSecurityException
    */
   public String getTextFromChildElement(String localname, String namespace)
           throws XMLSecurityException {

      try {
         Element nscontext = XMLUtils.createDSctx(this._doc, "x", namespace);
         Text t = (Text) XPathAPI.selectSingleNode(this._constructionElement,
                                                   "./x:" + localname
                                                   + "/text()", nscontext);

         return t.getData();
      } catch (TransformerException ex) {
         throw new XMLSecurityException("empty", ex);
      }
   }

   /**
    * Method getBytesFromTextChild
    *
    * @return
    * @throws XMLSecurityException
    */
   public byte[] getBytesFromTextChild() throws XMLSecurityException {

      try {
         Text t = (Text) XPathAPI.selectSingleNode(this._constructionElement,
                                                   "./text()");

         return Base64.decode(t.getData());
      } catch (TransformerException ex) {
         throw new XMLSecurityException("empty", ex);
      }
   }

   /**
    * Method getTextFromTextChild
    *
    * @return
    * @throws XMLSecurityException
    */
   public String getTextFromTextChild() throws XMLSecurityException {

      try {
         Text t = (Text) XPathAPI.selectSingleNode(this._constructionElement,
                                                   "./text()");

         return t.getData();
      } catch (TransformerException ex) {
         throw new XMLSecurityException("empty", ex);
      }
   }

   /**
    * Method getChildElementLocalName
    *
    * @param index
    * @param namespace
    * @param localname
    * @return
    */
   protected Element getChildElementLocalName(int index, String namespace,
                                              String localname) {

      try {
         Element nscontext = XMLUtils.createDSctx(this._doc, "ds", namespace);
         Element e =
            (Element) XPathAPI.selectSingleNode(this._constructionElement,
                                                "./ds:" + localname + "["
                                                + (index + 1) + "]", nscontext);

         return e;
      } catch (TransformerException ex) {}

      return null;
   }

   /**
    * Method length
    *
    * @param namespace
    * @param localname
    * @return
    */
   protected int length(String namespace, String localname) {

      try {
         Element nscontext = XMLUtils.createDSctx(this._doc, "ds", namespace);
         NodeList nl = XPathAPI.selectNodeList(this._constructionElement,
                                               "./ds:" + localname, nscontext);

         return nl.getLength();
      } catch (TransformerException ex) {}

      return 0;
   }

   /** Field _prefixMappings */
   static HashMap _prefixMappings = new HashMap();

   /**
    * Method setDefaultPrefix
    *
    * @param namespace
    * @param prefix
    */
   public static void setDefaultPrefix(String namespace, String prefix) throws XMLSecurityException {
      Iterator keys = ElementProxy._prefixMappings.keySet().iterator();
      while (keys.hasNext()) {
         String storedNamespace = (String) keys.next();
         String storedPrefix = (String) ElementProxy._prefixMappings.get(storedNamespace);
         if (storedPrefix.equals(prefix) && !storedNamespace.equals(namespace)) {
            Object exArgs[] = { prefix, namespace, storedNamespace };
            throw new XMLSecurityException("prefix.AlreadyAssigned", exArgs);
         }
      }
      ElementProxy._prefixMappings.put(namespace, prefix);
   }

   /**
    * Method getDefaultPrefix
    *
    * @param namespace
    * @return
    */
   public static String getDefaultPrefix(String namespace) {

      String prefix = (String) ElementProxy._prefixMappings.get(namespace);

      return prefix;
   }

   static {
      org.apache.xml.security.Init.init();
   }
}

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

// public abstract class ElementProxy implements org.w3c.dom.Element {
public class ElementProxy {

   /** Field cat */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(ElementProxy.class.getName());
   //J-
   public static final int MODE_SIGN = 0;
   public static final int MODE_VERIFY = 1;
   public static final int MODE_UNKNOWN = 2;
   protected int _state = MODE_UNKNOWN;
   //J+

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
      this._state = MODE_SIGN;
      this._baseURI = null;
      this._constructionElement = null;
   }

   /**
    * Constructor ElementProxy
    *
    * @param doc
    * @param localname
    */
   public ElementProxy(Document doc, String localname) {

      this._doc = doc;
      this._state = MODE_SIGN;
      this._constructionElement =
         XMLUtils.createElementInSignatureSpace(this._doc, localname);
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
      this._state = MODE_VERIFY;
      this._constructionElement = element;
      this._baseURI = BaseURI;
   }

   /**
    * Constructor ElementProxy
    *
    * @param element
    * @param BaseURI
    * @throws XMLSecurityException
    */
   public ElementProxy(Element element, String BaseURI)
           throws XMLSecurityException {

      if (element == null) {
         throw new XMLSecurityException("ElementProxy.nullElement");
      }

      cat.debug("Constructed from " + element.getTagName());
      cat.debug("Now I have children: " + element.getChildNodes().getLength());

      this._state = MODE_VERIFY;
      this._constructionElement = element;
      this._doc = this._constructionElement.getOwnerDocument();
      this._baseURI = BaseURI;
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
    * Method getBaseURI
    *
    * @return
    */
   public String getBaseURI() {
      return this._baseURI;
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
    * @return
    * @throws XMLSecurityException
    */
   public BigInteger getBigIntegerFromChildElement(String localname)
           throws XMLSecurityException {

      try {
         Element nscontext = XMLUtils.createDSctx(this._doc, "ds");
         Text t = (Text) XPathAPI.selectSingleNode(this._constructionElement,
                                                   "./ds:" + localname
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
    * @return
    * @throws XMLSecurityException
    */
   public byte[] getBytesFromChildElement(String localname)
           throws XMLSecurityException {

      try {
         Element nscontext = XMLUtils.createDSctx(this._doc, "ds");
         Element e =
            (Element) XPathAPI.selectSingleNode(this._constructionElement,
                                                "./ds:" + localname, nscontext);

         return Base64.decode(e);
      } catch (TransformerException ex) {
         throw new XMLSecurityException("empty", ex);
      }
   }

   public String getTextFromChildElement(String localname)
           throws XMLSecurityException {

      try {
         Element nscontext = XMLUtils.createDSctx(this._doc, "ds");
         Text t =
            (Text) XPathAPI.selectSingleNode(this._constructionElement,
                                                "./ds:" + localname + "/text()", nscontext);

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

   /*
   //J-
   // Interface Node
   public String getNodeName(){return _constructionElement.getNodeName();}
   public String getNodeValue() throws DOMException{return _constructionElement.getNodeValue();}
   public void setNodeValue(String nodeValue) throws DOMException{_constructionElement.setNodeValue(nodeValue);}
   public short getNodeType(){return _constructionElement.getNodeType();}
   public Node getParentNode(){return _constructionElement.getParentNode();}
   public NodeList getChildNodes(){return _constructionElement.getChildNodes();}
   public Node getFirstChild(){return _constructionElement.getFirstChild();}
   public Node getLastChild(){return _constructionElement.getLastChild();}
   public Node getPreviousSibling(){return _constructionElement.getPreviousSibling();}
   public Node getNextSibling(){return _constructionElement.getNextSibling();}
   public NamedNodeMap getAttributes(){return _constructionElement.getAttributes();}
   public Document getOwnerDocument(){return _constructionElement.getOwnerDocument();}
   public Node insertBefore(Node newChild, Node refChild) throws DOMException{return _constructionElement.insertBefore( newChild , refChild);}
   public Node replaceChild(Node newChild, Node oldChild) throws DOMException{return _constructionElement.replaceChild( newChild , oldChild);}
   public Node removeChild(Node oldChild) throws DOMException{return _constructionElement.removeChild(oldChild);}
   public Node appendChild(Node newChild) throws DOMException{return _constructionElement.appendChild(newChild);}
   public boolean hasChildNodes(){return _constructionElement.hasChildNodes();}
   public Node cloneNode(boolean deep){return _constructionElement.cloneNode(deep);}
   public void normalize(){_constructionElement.normalize();}
   public boolean isSupported(String feature, String version){return _constructionElement.isSupported(feature, version);}
   public String getNamespaceURI(){return _constructionElement.getNamespaceURI();}
   public String getPrefix(){return _constructionElement.getPrefix();}
   public void setPrefix(String prefix) throws DOMException{_constructionElement.setPrefix(prefix);}
   public String getLocalName(){return _constructionElement.getLocalName();}
   public boolean hasAttributes(){return _constructionElement.hasAttributes();}

   // Interface Element
   public String getTagName(){return _constructionElement.getTagName();}
   public String getAttribute(String name){return _constructionElement.getAttribute(name);}
   public void setAttribute(String name, String value) throws DOMException{_constructionElement.setAttribute(name, value);}
   public void removeAttribute(String name) throws DOMException{_constructionElement.removeAttribute(name);}
   public Attr getAttributeNode(String name){return _constructionElement.getAttributeNode(name);}
   public Attr setAttributeNode(Attr newAttr) throws DOMException{return _constructionElement.setAttributeNode(newAttr);}
   public Attr removeAttributeNode(Attr oldAttr) throws DOMException{return _constructionElement.removeAttributeNode(oldAttr);}
   public NodeList getElementsByTagName(String name){return _constructionElement.getElementsByTagName(name);}
   public String getAttributeNS(String namespaceURI, String localName){return _constructionElement.getAttributeNS(namespaceURI, localName);}
   public void setAttributeNS(String namespaceURI, String qualifiedName, String value)throws DOMException{_constructionElement.setAttributeNS(namespaceURI, qualifiedName, value);}
   public void removeAttributeNS(String namespaceURI, String localName) throws DOMException{removeAttributeNS(namespaceURI, localName);}
   public Attr getAttributeNodeNS(String namespaceURI, String localName){return _constructionElement.getAttributeNodeNS(namespaceURI, localName);}
   public Attr setAttributeNodeNS(Attr newAttr) throws DOMException{return _constructionElement.setAttributeNodeNS(newAttr);}
   public NodeList getElementsByTagNameNS(String namespaceURI, String localName){return _constructionElement.getElementsByTagNameNS(namespaceURI, localName);}
   public boolean hasAttribute(String name){return _constructionElement.hasAttribute(name);}
   public boolean hasAttributeNS(String namespaceURI, String localName){return _constructionElement.hasAttributeNS(namespaceURI, localName);}
   //J+
   */
   static {
      org.apache.xml.security.Init.init();
   }
}

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
import java.util.Vector;
import java.math.BigInteger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.*;
import org.apache.xml.security.c14n.*;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.Base64;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.HelperNodeList;


/**
 * DOM and XML accessibility and comfort functions.
 *
 * @author Christian Geuer-Pollmann
 */
public class XMLUtils {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(XMLUtils.class.getName());

   /**
    * Method spitOutVersions
    *
    * @param cat
    */
   public static void spitOutVersions(org.apache.log4j.Category cat) {

      cat.debug("Apache Xerces "
                + org.apache.xerces.framework.Version.fVersion);
      cat.debug("Apache Xalan  "
                + org.apache.xalan.processor.XSLProcessorVersion.S_VERSION);
   }

   /** Field nodeTypeString */
   private static String[] nodeTypeString = new String[]{ "", "ELEMENT",
                                                          "ATTRIBUTE",
                                                          "TEXT_NODE",
                                                          "CDATA_SECTION",
                                                          "ENTITY_REFERENCE",
                                                          "ENTITY",
                                                          "PROCESSING_INSTRUCTION",
                                                          "COMMENT", "DOCUMENT",
                                                          "DOCUMENT_TYPE",
                                                          "DOCUMENT_FRAGMENT",
                                                          "NOTATION" };

   /**
    * Transforms <code>org.w3c.dom.Node.XXX_NODE</code> NodeType values into
    * Strings.
    *
    * @param nodeType as taken from the {@link org.w3c.dom.Node#getNodeType} function
    * @return the String value.
    * @see org.w3c.dom.Node#getNodeType
    */
   public static String getNodeTypeString(short nodeType) {

      if ((nodeType > 0) && (nodeType < 13)) {
         return nodeTypeString[nodeType];
      } else {
         return "";
      }
   }

   /**
    * Method getNodeTypeString
    *
    * @param n
    * @return
    */
   public static String getNodeTypeString(Node n) {
      return getNodeTypeString(n.getNodeType());
   }

   /**
    * Transforms <code>org.w3c.dom.Node.XXX_NODE</code> NodeType values into
    * Strings.
    *
    * This is the old and un-elegant version of {@link #getNodeTypeString}.
    *
    * @param nodeType as taken from the {@link org.w3c.dom.Node#getNodeType} function
    * @return the String value.
    * @see org.w3c.dom.Node#getNodeType
    * @see #getNodeTypeString
    */
   public static String getNodeTypeStringOld(short nodeType) {

      switch (nodeType) {

      case Node.ELEMENT_NODE :
         return "ELEMENT";

      case Node.ATTRIBUTE_NODE :
         return "ATTRIBUTE";

      case Node.TEXT_NODE :
         return "TEXT";

      case Node.CDATA_SECTION_NODE :
         return "CDATA_SECTION";

      case Node.ENTITY_REFERENCE_NODE :
         return "ENTITY_REFERENCE";

      case Node.ENTITY_NODE :
         return "ENTITY";

      case Node.PROCESSING_INSTRUCTION_NODE :
         return "PROCESSING_INSTRUCTION";

      case Node.COMMENT_NODE :
         return "COMMENT";

      case Node.DOCUMENT_NODE :
         return "DOCUMENT";

      case Node.DOCUMENT_TYPE_NODE :
         return "DOCUMENT_TYPE";

      case Node.DOCUMENT_FRAGMENT_NODE :
         return "DOCUMENT_FRAGMENT";

      case Node.NOTATION_NODE :
         return "NOTATION";

      default :
         return "UNKNOWN_NODE_TYPE";
      }
   }

   /**
    * appends a return plus <code>indent</code> spaces to afterThisElement
    *
    * @param parentElement
    * @param childLocalName
    * @return
    */

   /*
   public static void appendIndentationToElement(Element afterThisElement,
                                        int indent) {

      if (afterThisElement != null) {
         Document doc = afterThisElement.getOwnerDocument();
         Node parentElem = afterThisElement.getParentNode();

         if (parentElem.getNodeType() == Node.ELEMENT_NODE) {
            String indentStr = "";

            for (int i = 0; i < indent; i++) {
               indentStr += " ";
            }

            Text indentText = doc.createTextNode(indentStr);
            Node nextSibling = parentElem.getNextSibling();

            if (nextSibling == null) {
               parentElem.appendChild(indentText);
            } else {
               parentElem.insertBefore(indentText, nextSibling);
            }
         }
      }
   }

   public static void indentElement(Element thisElement) {

      if (thisElement != null) {
         Document doc = thisElement.getOwnerDocument();
         Node parentElem = thisElement.getParentNode();

         if (parentElem.getNodeType() == Node.ELEMENT_NODE) {

            String indentStr = "";

            for (int i = 0; i < Constants.xmlOutputProperties.getXMLIndentLevel(); i++) {
               indentStr += Constants.xmlOutputProperties.getXMLIndentPattern();
            }

            Text indentText = doc.createTextNode(indentStr);
            parentElem.insertBefore(indentText, thisElement);


            Node nextSibling = parentElem.getNextSibling();

            if (nextSibling != null) {
               parentElem.insertBefore(doc.createTextNode("\n"), nextSibling);
            } else {
               parentElem.appendChild(doc.createTextNode("\n"));
            }
         }
      }
   }
   */

   /**
    * Convenience methods to catch the <B>first</B> child of an
    * <CODE>Element</CODE> with a given <CODE>NodeName</CODE> which
    * is in XML Signature namespace.
    *
    * @param parentNode the parent node
    * @param nodeName the name of the child
    * @return the child Element
    */
   public static Element getFirstChildElementInSignatureNS(
           Element parentElement, String childLocalName) {

      NodeList nl = getDirectChildrenElementsNS(parentElement, childLocalName,
                                                Constants.SignatureSpecNS);

      if (nl.getLength() == 0) {
         return null;
      } else {
         return (Element) nl.item(0);
      }
   }

   /**
    * Convenience methods to catch the <B>first</B> child of an
    * <CODE>Element</CODE> with a given <CODE>NodeName</CODE> and
    * <CODE>NamespaceURI</CODE>
    *
    * @param parentElement
    * @param childLocalName
    * @param namespace
    * @return the child Element
    */
   public static Element getFirstChildElementNS(Element parentElement,
           String childLocalName, String namespace) {

      NodeList nl = getDirectChildrenElementsNS(parentElement, childLocalName,
                                                namespace);

      if (nl.getLength() == 0) {
         return null;
      } else {
         return (Element) nl.item(0);
      }
   }

   /**
    * Retrieves the direct Element children of a particular namespace with a
    * specific local name from the element.
    *
    * This is very often used instead of {@link org.w3c.dom.Element#getElementsByTagNameNS}
    * due to the fact that {@link org.w3c.dom.Element#getElementsByTagNameNS}
    * searches recursive through all descendants. If e.g. the one and only
    * ds:SignatureValue of a ds:Signature should be matched,
    * {@link org.w3c.dom.Element#getElementsByTagNameNS} can return more than
    * one Node because it finds ds:SignedInfo elements in Signatures, which are
    * in Objects of the original ds:Signature.
    *
    * @param parentElement
    * @param namespace
    * @param childLocalName
    * @return the NodeList which contains the selected children
    */
   public static NodeList getDirectChildrenElementsNS(Element parentElement,
           String childLocalName, String namespace) {

      NodeList allNodes = parentElement.getChildNodes();
      HelperNodeList selectedNodes = new HelperNodeList();

      for (int i = 0; i < allNodes.getLength(); i++) {
         Node currentNode = allNodes.item(i);

         //J-
         if ((currentNode.getNodeType() == Node.ELEMENT_NODE) &&
             ((Element) currentNode).getLocalName().equals(childLocalName) &&
             (((Element) currentNode).getNamespaceURI() != null) &&
             ((Element) currentNode).getNamespaceURI().equals(namespace)
             ) {
            selectedNodes.appendChild(currentNode);
         }
         //J+
      }

      return selectedNodes;
   }

   /**
    * Method getSingleExistingChildElementSignatureNS
    *
    * @param element
    * @param childLocalName
    * @return
    * @throws DOMException
    */
   public static Element getSingleExistingChildElementSignatureNS(
           Element element, String childLocalName) throws DOMException {
      return getSingleExistingChildElementNS(element, childLocalName,
                                             Constants.SignatureSpecNS);
   }

   /**
    * Retrieves the one and only direct Element child of a particular namespace
    * with a specific local name from the element.
    *
    * @param element is the parent element which contains the searches one.
    * @param namespace the namespace in which the searched element has to be
    * @param childLocalName
    * @return
    * @throws DOMException
    */
   public static Element getSingleExistingChildElementNS(
           Element element, String childLocalName, String namespace)
              throws DOMException {

      NodeList correctOnes = getDirectChildrenElementsNS(element,
                                childLocalName, namespace);

      if (correctOnes.getLength() == 1) {
         return (Element) correctOnes.item(0);
      } else {

         /*
         Object exArgs[] = { childLocalName, element.getLocalName() };

         throw new DOMException(DOMException.WRONG_DOCUMENT_ERR,
                                Constants.translate("xml.WrongContent",
                                                    exArgs));
         */
         return null;
      }
   }

   /**
    * Prints a sub-tree to standard out.
    *
    * @param ctxNode
    *
    *  try {
    *     Document doc = contextNode.getOwnerDocument();
    *     OutputFormat format = new OutputFormat(doc);
    *     StringWriter stringOut = new StringWriter();
    *     XMLSerializer serial = new XMLSerializer(stringOut, format);
    *
    *     serial.asDOMSerializer();
    *     serial.serialize(doc.getDocumentElement());
    *     os.write(stringOut.toString());
    *  } catch (Exception ex) {
    *     ex.printStackTrace();
    *  }
    * }
    * @return
    */
   public static Vector getAncestorElements(Node ctxNode) {

      if (ctxNode.getNodeType() != Node.ELEMENT_NODE) {
         return null;
      }

      Vector ancestorVector = new Vector();
      Node parent = ctxNode;

      while ((parent = parent.getParentNode()) != null
             && (parent.getNodeType() == Node.ELEMENT_NODE)) {
         ancestorVector.add(parent);
      }

      ancestorVector.trimToSize();

      return ancestorVector;
   }

   /**
    * Method getDirectChildrenElements
    *
    * @param parentElement
    * @return
    */
   public static NodeList getDirectChildrenElements(Element parentElement) {

      NodeList allNodes = parentElement.getChildNodes();
      HelperNodeList selectedNodes = new HelperNodeList();

      for (int i = 0; i < allNodes.getLength(); i++) {
         Node currentNode = allNodes.item(i);

         if ((currentNode.getNodeType() == Node.ELEMENT_NODE)) {
            selectedNodes.appendChild(currentNode);
         }
      }

      return selectedNodes;
   }    //getDirectChildrenElements:NodeList

   /**
    * Outputs a DOM tree to a file.
    *
    * @param contextNode root node of the DOM tree
    * @param filename the file name
    * @throws java.io.FileNotFoundException
    */
   public static void outputDOM(Node contextNode, String filename)
           throws java.io.FileNotFoundException {

      OutputStream os = new FileOutputStream(filename);

      outputDOM(contextNode, os);
   }

   /**
    * Outputs a DOM tree to an {@link OutputStream}.
    *
    * @param contextNode root node of the DOM tree
    * @param os the {@link OutputStream}
    */
   public static void outputDOM(Node contextNode, OutputStream os) {

      try {
         TransformerFactory tFactory = TransformerFactory.newInstance();
         Transformer transformer = tFactory.newTransformer();

         transformer
            .setOutputProperty(javax.xml.transform.OutputKeys
               .OMIT_XML_DECLARATION, "yes");

         DOMSource source = new DOMSource(contextNode);
         StreamResult result = new StreamResult(os);

         transformer.transform(source, result);
      } catch (TransformerConfigurationException e) {
         e.printStackTrace();
      } catch (TransformerException e) {
         e.printStackTrace();
      }
   }

   /**
    * Method outputDOMc14n
    *
    * @param contextNode
    * @param os
    */
   public static void outputDOMc14nWithComments(Node contextNode,
           OutputStream os) {

      try {
         os.write(Canonicalizer
            .getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS)
               .canonicalize(contextNode));
      } catch (IOException ex) {}
      catch (InvalidCanonicalizerException ex) {}
      catch (CanonicalizationException ex) {}
   }

   /**
    * Converts a single {@link Node} into a {@link NodeList} which contains only that {@link Node}
    *
    * @param node the Node
    * @return the NodeList
    */
   public static NodeList elementToNodeList(Node node) {

      HelperNodeList nl = new HelperNodeList();

      nl.appendChild(node);

      return (NodeList) nl;
   }

   /**
    * Creates Attributes {@link org.w3c.dom.Attr} in the given namespace
    * (if possible). If the namespace is empty, only the QName is used.
    *
    * @param doc the generator (factory) Document
    * @param QName the QName of the Attr
    * @param Value the String value of the Attr
    * @param NamespaceURI the namespace for the Attr
    * @return the Attr
    */
   public static Attr createAttr(Document doc, String QName, String Value,
                                 String NamespaceURI) {

      Attr attr = null;

      if ((NamespaceURI != null) && (NamespaceURI.length() > 0)) {
         attr = doc.createAttributeNS(NamespaceURI, QName);
      } else {
         attr = doc.createAttribute(QName);
      }

      attr.appendChild(doc.createTextNode(Value));

      return attr;
   }

   /**
    * Sets the Attribute QName with Value in Element elem.
    *
    * @param elem the Element which has to contain the Attribute
    * @param QName the QName of the Attribute
    * @param Value the value of the Attribute
    */
   public static void setAttr(Element elem, String QName, String Value) {

      Document doc = elem.getOwnerDocument();
      Attr attr = doc.createAttributeNS(Constants.SignatureSpecNS, QName);

      attr.appendChild(doc.createTextNode(Value));
      elem.setAttributeNode(attr);
   }

   /**
    * Creates an Element from a BigInteger. The BigInteger is base64-encoded
    * and put into the Element with a given name.
    *
    * See
    * <A HREF="http://www.w3.org/TR/2001/CR-xmldsig-core-20010419/#sec-CryptoBinary">Section
    * 4.0.1 The ds:CryptoBinary Simple Type</A>:
    *
    * This specification defines the ds:CryptoBinary simple type for
    * representing arbitrary-length integers (e.g. "bignums") in XML as
    * octet strings. The integer value is first converted to a "big
    * endian" bitstring. The bitstring is then padded with leading zero
    * bits so that the total number of bits == 0 mod 8 (so that there are
    * an integral number of octets). If the bitstring contains entire
    * leading octets that are zero, these are removed (so the high-order
    * octet is always non-zero). This octet string is then base64 [MIME]
    * encoded. (The conversion from integer to octet string is equivalent
    * to IEEE 1363's I2OSP [1363] with minimal length).
    *
    *
    * @param doc the factory Document
    * @param elementName the name of the Element
    * @param bigInteger the BigInteger wo be inserted
    * @return the Element
    * @throws XMLSignatureException if bigInteger is not positive
    */
   public static Element createElementFromBigint(
           Document doc, String elementName, BigInteger bigInteger)
              throws XMLSignatureException {

      Element element = doc.createElementNS(
         Constants.SignatureSpecNS,
         Constants.xmlOutputProperties.getSignatureSpecNSprefix() + ":"
         + elementName);

      /* bigInteger must be positive */
      if (bigInteger.signum() != 1) {
         throw new XMLSignatureException("signature.Util.BignumNonPositive");
      }

      byte byteRepresentation[] = bigInteger.toByteArray();

      while (byteRepresentation[0] == 0) {
         byte oldByteRepresentation[] = byteRepresentation;

         byteRepresentation = new byte[oldByteRepresentation.length - 1];

         System.arraycopy(oldByteRepresentation, 1, byteRepresentation, 0,
                          oldByteRepresentation.length - 1);
      }

      Text text =
         doc.createTextNode(org.apache.xml.security.utils.Base64
            .encode(byteRepresentation));

      element.appendChild(text);

      return element;
   }

   /**
    * Fetches a base64-encoded BigInteger from an Element.
    *
    * @param element the Element
    * @return the BigInteger
    * @throws XMLSignatureException if Element has not exactly one Text child
    */
   public static BigInteger getBigintFromElement(Element element)
           throws XMLSignatureException {

      if (element.getChildNodes().getLength() != 1) {
         throw new XMLSignatureException("signature.Util.TooManyChilds");
      }

      Node child = element.getFirstChild();

      if ((child == null) || (child.getNodeType() != Node.TEXT_NODE)) {
         throw new XMLSignatureException("signature.Util.NonTextNode");
      }

      Text text = (Text) child;
      String textData = text.getData();
      byte magnitude[] = org.apache.xml.security.utils.Base64.decode(textData);
      int signum = 1;
      BigInteger bigInteger = new BigInteger(signum, magnitude);

      return bigInteger;
   }

   /**
    * Fetches base64-encoded byte[] data from an Element.
    *
    * @param element
    * @return the byte[] data
    * @throws XMLSignatureException if Element has not exactly one Text child
    */
   public static byte[] getBytesFromElement(Element element)
           throws XMLSignatureException {

      if (element.getChildNodes().getLength() != 1) {
         throw new XMLSignatureException("signature.Util.TooManyChilds");
      }

      Node child = element.getFirstChild();

      if ((child == null) || (child.getNodeType() != Node.TEXT_NODE)) {
         throw new XMLSignatureException("signature.Util.NonTextNode");
      }

      Text text = (Text) child;
      String textData = text.getData();
      byte bytes[] = org.apache.xml.security.utils.Base64.decode(textData);

      return bytes;
   }

   /**
    * Creates an Element in the XML Signature specification namespace.
    *
    * @param doc the factory Document
    * @param elementName the local name of the Element
    * @return the Element
    */
   public static Element createElementInSignatureSpace(Document doc,
           String elementName) {

      if (doc == null) {
         throw new RuntimeException("Document is null");
      }

      String ds = Constants.getSignatureSpecNSprefix();

      if ((ds == null) || (ds.length() == 0)) {
         Element element = doc.createElementNS(Constants.SignatureSpecNS,
                                               elementName);

         element.setAttribute("xmlns", Constants.SignatureSpecNS);

         return element;
      } else {
         Element element = doc.createElementNS(Constants.SignatureSpecNS,
                                               ds + ":" + elementName);

         element.setAttribute("xmlns:" + ds, Constants.SignatureSpecNS);

         return element;
      }
   }

   /**
    * Returns true if the element is in XML Signature namespace and the local name equals the supplied one.
    *
    * @param element
    * @param localName
    * @return true if the element is in XML Signature namespace and the local name equals the supplied one
    */
   public static boolean elementIsInSignatureSpace(Element element,
           String localName) {

      if (element == null) {
         return false;
      }

      if (element.getNamespaceURI() == null) {
         return false;
      }

      if (!element.getNamespaceURI().equals(Constants.SignatureSpecNS)) {
         return false;
      }

      if (!element.getLocalName().equals(localName)) {
         return false;
      }

      return true;
   }

   /**
    * Verifies that the given Element is in the XML Signature namespace
    * {@link org.apache.xml.security.utils.Constants#SignatureSpecNS} and that the
    * local name of the Element matches the supplied on.
    *
    * @param element Element to be checked
    * @param localName
    * @throws XMLSignatureException if element is not in Signature namespace or if the local name does not match
    * @see org.apache.xml.security.utils.Constants#SignatureSpecNS
    */
   public static void guaranteeThatElementInSignatureSpace(
           Element element, String localName) throws XMLSignatureException {

      /*
      cat.debug("guaranteeThatElementInSignatureSpace(" + element + ", "
                + localName + ")");
      */
      if (element == null) {
         Object exArgs[] = { localName, null };

         throw new XMLSignatureException("xml.WrongElement", exArgs);
      }

      if ((localName == null) || localName.equals("")
              ||!elementIsInSignatureSpace(element, localName)) {
         Object exArgs[] = { localName, element.getLocalName() };

         throw new XMLSignatureException("xml.WrongElement", exArgs);
      }
   }

   /**
    * This method returns the owner document of a particular node.
    * This method is necessary because it <I>always</I> returns a
    * {@link Document}. {@link Node#getOwnerDocument} returns <CODE>null</CODE>
    * if the {@link Node} is a {@link Document}.
    *
    * @param node
    * @return the owner document of the node
    */
   public static Document getOwnerDocument(Node node) {

      if (node.getNodeType() == Node.DOCUMENT_NODE) {
         return (Document) node;
      } else {
         return node.getOwnerDocument();
      }
   }

   /** Field randomNS */
   private static String randomNS = null;

   /**
    * Prefix for random namespaces.
    *
    * @see #getRandomNamespace
    */
   public static final String randomNSprefix =
      "http://www.xmlsecurity.org/NS#randomval";

   /**
    * This method creates a random String like
    * <CODE>http://www.xmlsecurity.org/NS#randomval8dcc/C2qwxFukXjJhS7W1xvHHq4Z</CODE>
    * that will be used for registering the <CODE>here()</CODE> function in a
    * specific namespace. The random string is the Base64 encoded version of a
    * 168 bit {@link java.security.SecureRandom} value.
    * <BR/>
    * This random namespace prefix prevents attackers from inserting malicious
    * here() functions in our namespace. The method caches the valued for
    * subsequent calls during the application run.
    *
    * @return the random namespace prefix String.
    */
   public static String getRandomNamespacePrefix() {

      if (XMLUtils.randomNS == null) {
         byte[] randomData = new byte[21];
         java.security.SecureRandom sr = new java.security.SecureRandom();

         sr.nextBytes(randomData);

         String prefix =
            "xmlsecurityOrgPref"
            + org.apache.xml.security.utils.Base64.encode(randomData);

         XMLUtils.randomNS = "";

         for (int i = 0; i < prefix.length(); i++) {
            if ((prefix.charAt(i) != '+') && (prefix.charAt(i) != '/')
                    && (prefix.charAt(i) != '=')) {
               XMLUtils.randomNS += prefix.charAt(i);
            }
         }
      }

      return XMLUtils.randomNS;
   }

   /**
    * Method createDSctx
    *
    * @param doc
    * @param prefix
    * @param namespace
    * @return
    */
   public static Element createDSctx(Document doc, String prefix,
                                     String namespace) {

      Element ctx = doc.createElement("namespaceContext");

      ctx.setAttribute("xmlns:" + prefix.trim(), namespace);

      return ctx;
   }

   /**
    * Method createDSctx
    *
    * @param doc
    * @param prefix
    * @return
    */
   public static Element createDSctx(Document doc, String prefix) {
      return XMLUtils.createDSctx(doc, prefix, Constants.SignatureSpecNS);
   }

   /**
    * Method createDSctx
    *
    * @param doc
    * @return
    */
   public static Element createDSctx(Document doc) {
      return XMLUtils.createDSctx(doc, "ds", Constants.SignatureSpecNS);
   }

   static {
      org.apache.xml.security.Init.init();
   }
}

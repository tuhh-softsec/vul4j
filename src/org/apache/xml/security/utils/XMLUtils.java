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
import org.apache.xpath.objects.XObject;
import org.w3c.dom.*;
import org.apache.xml.security.c14n.*;
import org.apache.xml.security.exceptions.*;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.Base64;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.HelperNodeList;
import org.apache.xpath.XPathAPI;
import javax.xml.transform.TransformerException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


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
    * Constructor XMLUtils
    *
    */
   private XMLUtils() {

      // we don't allow instantiation
   }

   /**
    * Method getXalanVersion
    *
    * @return
    */
   public static String getXalanVersion() {

      String version = XMLUtils.getXalan1Version();

      if (version != null) {
         return version;
      }

      version = XMLUtils.getXalan20Version();

      if (version != null) {
         return version;
      }

      version = XMLUtils.getXalan2Version();

      if (version != null) {
         return version;
      }

      return "Apache Xalan not installed";

      // return "Apache " + org.apache.xalan.processor.XSLProcessorVersion.S_VERSION;
      // return "Apache " + org.apache.xalan.Version.getVersion();
   }

   /**
    * Method getXercesVersion
    *
    * @return
    */
   public static String getXercesVersion() {

      String version = XMLUtils.getXerces1Version();

      if (version != null) {
         return version;
      }

      version = XMLUtils.getXerces2Version();

      if (version != null) {
         return version;
      }

      return "Apache Xerces not installed";

      // return "Apache " + org.apache.xerces.impl.Version.fVersion;
      // return "Apache " + org.apache.xerces.framework.Version.fVersion;
   }

   /**
    * Method getXalan1Version
    *
    * @return
    */
   private static String getXalan1Version() {

      try {
         final String XALAN1_VERSION_CLASS =
            "org.apache.xalan.xslt.XSLProcessorVersion";
         Class clazz = classForName(XALAN1_VERSION_CLASS);

         // Found Xalan-J 1.x, grab it's version fields
         StringBuffer buf = new StringBuffer();
         Field f = clazz.getField("PRODUCT");

         buf.append(f.get(null));
         buf.append(';');

         f = clazz.getField("LANGUAGE");

         buf.append(f.get(null));
         buf.append(';');

         f = clazz.getField("S_VERSION");

         buf.append(f.get(null));
         buf.append(';');

         return buf.toString();
      } catch (Exception e1) {
         return null;
      }
   }

   /**
    * Method getXalan20Version
    *
    * @return
    */
   private static String getXalan20Version() {

      try {

         // NOTE: This is the new Xalan 2.2+ version class
         final String XALAN2_2_VERSION_CLASS = "org.apache.xalan.Version";
         final String XALAN2_2_VERSION_METHOD = "getVersion";
         final Class noArgs[] = new Class[0];
         Class clazz = classForName(XALAN2_2_VERSION_CLASS);
         Method method = clazz.getMethod(XALAN2_2_VERSION_METHOD, noArgs);
         Object returnValue = method.invoke(null, new Object[0]);

         return (String) returnValue;
      } catch (Exception e2) {
         return null;
      }
   }

   /**
    * Method getXalan2Version
    *
    * @return
    */
   private static String getXalan2Version() {

      try {

         // NOTE: This is the old Xalan 2.0, 2.1, 2.2 version class,
         //    is being replaced by class below
         final String XALAN2_VERSION_CLASS =
            "org.apache.xalan.processor.XSLProcessorVersion";
         Class clazz = classForName(XALAN2_VERSION_CLASS);

         // Found Xalan-J 2.x, grab it's version fields
         StringBuffer buf = new StringBuffer();
         Field f = clazz.getField("S_VERSION");

         buf.append(f.get(null));

         return buf.toString();
      } catch (Exception e2) {
         return null;
      }
   }

   /**
    * Method getXerces1Version
    *
    * @return
    */
   private static String getXerces1Version() {

      try {
         final String XERCES1_VERSION_CLASS =
            "org.apache.xerces.framework.Version";
         Class clazz = classForName(XERCES1_VERSION_CLASS);

         // Found Xerces-J 1.x, grab it's version fields
         Field f = clazz.getField("fVersion");
         String parserVersion = (String) f.get(null);

         return parserVersion;
      } catch (Exception e) {
         return null;
      }
   }

   /**
    * Method getXerces2Version
    *
    * @return
    */
   private static String getXerces2Version() {

      try {
         final String XERCES2_VERSION_CLASS = "org.apache.xerces.impl.Version";
         Class clazz = classForName(XERCES2_VERSION_CLASS);

         // Found Xerces-J 2.x, grab it's version fields
         Field f = clazz.getField("fVersion");
         String parserVersion = (String) f.get(null);

         return parserVersion;
      } catch (Exception e) {
         return null;
      }
   }

   /**
    * Worker method to load a class.
    * Factor out loading classes for future use and JDK differences.
    * Copied from javax.xml.*.FactoryFinder
    * @param className name of class to load from
    * an appropriate classLoader
    * @return the class asked for
    * @throws ClassNotFoundException
    */
   protected static Class classForName(String className)
           throws ClassNotFoundException {

      ClassLoader classLoader = findClassLoader();

      if (classLoader == null) {
         return Class.forName(className);
      } else {
         return classLoader.loadClass(className);
      }
   }

   /**
    * Worker method to figure out which ClassLoader to use.
    * For JDK 1.2 and later use the context ClassLoader.
    * Copied from javax.xml.*.FactoryFinder
    * @return the appropriate ClassLoader
    * @throws ClassNotFoundException
    */
   protected static ClassLoader findClassLoader()
           throws ClassNotFoundException {

      ClassLoader classLoader = null;
      Method m = null;

      try {
         m = Thread.class.getMethod("getContextClassLoader", null);
      } catch (NoSuchMethodException e) {

         // Assume that we are running JDK 1.1, use the current ClassLoader
         return XMLUtils.class.getClassLoader();
      }

      try {
         return (ClassLoader) m.invoke(Thread.currentThread(), null);
      } catch (Exception e) {
         throw new RuntimeException(e.toString());
      }
   }

   /**
    * Method spitOutVersions
    *
    * @param cat
    */
   public static void spitOutVersions(org.apache.log4j.Category cat) {
      cat.debug(XMLUtils.getXercesVersion());
      cat.debug(XMLUtils.getXalanVersion());
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
    * Returns all ancestor elements of a given node up to the document element
    *
    * @param ctxNode
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
    * Returns all ancestor elements of a given node up to the given root element
    *
    * @param ctxNode
    * @param rootElement
    * @return
    */
   public static Vector getAncestorElements(Node ctxNode, Node rootElement) {

      Vector ancestorVector = new Vector();

      if (ctxNode.getNodeType() != Node.ELEMENT_NODE) {
         return ancestorVector;
      }

      Node parent = ctxNode;
      Node parentOfRoot = rootElement.getParentNode();

      while ((parent = parent.getParentNode()) != null
             && (parent.getNodeType() == Node.ELEMENT_NODE)
             && (parent != parentOfRoot)) {
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
   }

   /**
    * Method getDirectChild
    *
    * @param parentElement
    * @param childLocalName
    * @param childNamespaceURI
    * @return
    */
   public static Element getDirectChild(Element parentElement,
                                        String childLocalName,
                                        String childNamespaceURI) {

      NodeList nl = parentElement.getChildNodes();
      Vector results = new Vector();

      for (int i = 0; i < nl.getLength(); i++) {
         Node n = nl.item(i);

         if (n.getNodeType() == Node.ELEMENT_NODE) {
            if (((Element) n).getLocalName().equals(childLocalName)
                    && ((Element) n).getNamespaceURI()
                       .equals(childNamespaceURI)) {
               results.add(n);
            }
         }
      }

      if (results.size() != 1) {
         return null;
      }

      return (Element) results.elementAt(0);
   }

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

      XMLUtils.outputDOM(contextNode, os);
   }

   /**
    * Outputs a DOM tree to an {@link OutputStream}.
    *
    * @param contextNode root node of the DOM tree
    * @param os the {@link OutputStream}
    */
   public static void outputDOM(Node contextNode, OutputStream os) {
      XMLUtils.outputDOM(contextNode, os, false);
   }

   /**
    * Outputs a DOM tree to an {@link OutputStream}. <I>If an Exception is
    * thrown during execution, it's StackTrace is output to System.out, but the
    * Exception is not re-thrown.</I>
    *
    * @param contextNode root node of the DOM tree
    * @param os the {@link OutputStream}
    * @param addPreamble
    */
   public static void outputDOM(Node contextNode, OutputStream os,
                                boolean addPreamble) {

      try {
         if (addPreamble) {
            os.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes());
         }

         os.write(Canonicalizer
            .getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS)
               .canonicalizeSubtree(contextNode));
      } catch (IOException ex) {}
      catch (InvalidCanonicalizerException ex) {
         ex.printStackTrace();
      } catch (CanonicalizationException ex) {
         ex.printStackTrace();
      }
   }

   /**
    * Serializes the <CODE>contextNode</CODE> into the OutputStream, <I>but
    * supresses all Exceptions</I>.
    * <BR />
    * NOTE: <I>This should only be used for debugging purposes,
    * NOT in a production environment; this method ignores all exceptions,
    * so you won't notice if something goes wrong. If you're asking what is to
    * be used in a production environment, simply use the code inside the
    * <code>try{}</code> statement, but handle the Exceptions appropriately.</I>
    *
    * @param contextNode
    * @param os
    */
   public static void outputDOMc14nWithComments(Node contextNode,
           OutputStream os) {

      try {
         os.write(Canonicalizer
            .getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS)
               .canonicalizeSubtree(contextNode));
      } catch (IOException ex) {

         // throw new RuntimeException(ex.getMessage());
      } catch (InvalidCanonicalizerException ex) {

         // throw new RuntimeException(ex.getMessage());
      } catch (CanonicalizationException ex) {

         // throw new RuntimeException(ex.getMessage());
      }
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

      Attr attr = doc.createAttributeNS(NamespaceURI, QName);

      attr.setNodeValue(Value);

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

      attr.setNodeValue(Value);
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

      Element element = doc.createElementNS(Constants.SignatureSpecNS,
                                            Constants.getSignatureSpecNSprefix()
                                            + ":" + elementName);

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
    * Method getFullTextChildrenFromElement
    *
    * @param element
    * @return
    */
   public static String getFullTextChildrenFromElement(Element element) {

      StringBuffer sb = new StringBuffer();
      NodeList children = element.getChildNodes();
      int iMax = children.getLength();

      for (int i = 0; i < iMax; i++) {
         Node curr = children.item(i);

         if (curr.getNodeType() == Node.TEXT_NODE) {
            sb.append(((Text) curr).getData());
         }
      }

      return sb.toString();
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

      try {
         if (element.getChildNodes().getLength() != 1) {
            throw new XMLSignatureException("signature.Util.TooManyChilds");
         }

         Node child = element.getFirstChild();

         if ((child == null) || (child.getNodeType() != Node.TEXT_NODE)) {
            throw new XMLSignatureException("signature.Util.NonTextNode");
         }

         Text text = (Text) child;
         String textData = text.getData();
         byte magnitude[] =
            org.apache.xml.security.utils.Base64.decode(textData);
         int signum = 1;
         BigInteger bigInteger = new BigInteger(signum, magnitude);

         return bigInteger;
      } catch (Base64DecodingException ex) {
         throw new XMLSignatureException("empty", ex);
      }
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

      try {
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
      } catch (Base64DecodingException ex) {
         throw new XMLSignatureException("empty", ex);
      }
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

         element.setAttributeNS(Constants.NamespaceSpecNS, "xmlns",
                                Constants.SignatureSpecNS);

         return element;
      } else {
         Element element = doc.createElementNS(Constants.SignatureSpecNS,
                                               ds + ":" + elementName);

         element.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:" + ds,
                                Constants.SignatureSpecNS);

         return element;
      }
   }

   /**
    * Creates an Element in the XML Encryption specification namespace.
    *
    * @param doc the factory Document
    * @param elementName the local name of the Element
    * @return the Element
    */
   public static Element createElementInEncryptionSpace(Document doc,
           String elementName) {

      if (doc == null) {
         throw new RuntimeException("Document is null");
      }

      String xenc = EncryptionConstants.getEncryptionSpecNSprefix();

      if ((xenc == null) || (xenc.length() == 0)) {
         Element element =
            doc.createElementNS(EncryptionConstants.EncryptionSpecNS,
                                elementName);

         element.setAttributeNS(Constants.NamespaceSpecNS, "xmlns",
                                Constants.SignatureSpecNS);

         return element;
      } else {
         Element element =
            doc.createElementNS(EncryptionConstants.EncryptionSpecNS,
                                xenc + ":" + elementName);

         element.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:" + xenc,
                                EncryptionConstants.EncryptionSpecNS);

         return element;
      }
   }

   /**
    * Returns true if the element is in XML Signature namespace and the local
    * name equals the supplied one.
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
    * Returns true if the element is in XML Encryption namespace and the local
    * name equals the supplied one.
    *
    * @param element
    * @param localName
    * @return true if the element is in XML Encryption namespace and the local name equals the supplied one
    */
   public static boolean elementIsInEncryptionSpace(Element element,
           String localName) {

      if (element == null) {
         return false;
      }

      if (element.getNamespaceURI() == null) {
         return false;
      }

      if (!element.getNamespaceURI()
              .equals(EncryptionConstants.EncryptionSpecNS)) {
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
    * Verifies that the given Element is in the XML Encryption namespace
    * {@link org.apache.xml.security.utils.Constants#EncryptionSpecNS} and that the
    * local name of the Element matches the supplied on.
    *
    * @param element Element to be checked
    * @param localName
    * @throws XMLSecurityException if element is not in Encryption namespace or if the local name does not match
    * @see org.apache.xml.security.utils.Constants#EncryptionSpecNS
    */
   public static void guaranteeThatElementInEncryptionSpace(
           Element element, String localName) throws XMLSecurityException {

      if (element == null) {
         Object exArgs[] = { localName, null };

         throw new XMLSecurityException("xml.WrongElement", exArgs);
      }

      if ((localName == null) || localName.equals("")
              ||!elementIsInEncryptionSpace(element, localName)) {
         Object exArgs[] = { localName, element.getLocalName() };

         throw new XMLSecurityException("xml.WrongElement", exArgs);
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
         try {
            return node.getOwnerDocument();
         } catch (NullPointerException npe) {
            throw new NullPointerException(I18n.translate("endorsed.jdk1.4.0")
                                           + " Original message was \""
                                           + npe.getMessage() + "\"");
         }
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

      Element ctx = doc.createElementNS(null, "namespaceContext");

      ctx.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:" + prefix.trim(),
                         namespace);

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
    * Method indentSignature
    *
    * @param element
    * @param indentString
    * @param initialDepth
    */
   public static void indentSignature(Element element, String indentString,
                                      int initialDepth) {

      try {
         NodeList returns = XPathAPI.selectNodeList(element, ".//text()");

         for (int i = 0; i < returns.getLength(); i++) {
            Text returnText = (Text) returns.item(i);
            Element parent = (Element) returnText.getParentNode();
            Document doc = returnText.getOwnerDocument();
            int j = 0;

            while (parent != element) {
               j++;
            }

            String newReturn = "";

            for (int k = 0; k < j; k++) {
               newReturn += indentString;
            }

            Text newReturnText = doc.createTextNode(newReturn);

            parent.replaceChild(newReturnText, returnText);
         }
      } catch (TransformerException ex) {}
   }

   /**
    * Method addReturnToElement
    *
    * @param elementProxy
    */
   public static void addReturnToElement(ElementProxy elementProxy) {

      Document doc = elementProxy._doc;

      elementProxy.getElement().appendChild(doc.createTextNode("\n"));
   }

   /**
    * Method addReturnToElement
    *
    * @param e
    */
   public static void addReturnToElement(Element e) {

      Document doc = e.getOwnerDocument();

      e.appendChild(doc.createTextNode("\n"));
   }

   /**
    * Method addReturnToNode
    *
    * @param n
    */
   public static void addReturnToNode(Node n) {

      Document doc = n.getOwnerDocument();

      n.appendChild(doc.createTextNode("\n"));
   }

   /**
    * Method convertNodelistToSet
    *
    * @param xpathNodeSet
    * @return
    */
   public static Set convertNodelistToSet(NodeList xpathNodeSet) {

      if (xpathNodeSet == null) {
         return new HashSet();
      }

      int length = xpathNodeSet.getLength();
      Set set = new HashSet(length);

      for (int i = 0; i < length; i++) {
         set.add(xpathNodeSet.item(i));
      }

      return set;
   }

   /**
    * Method convertSetToNodelist
    *
    * @param set
    * @return
    */
   public static NodeList convertSetToNodelist(Set set) {

      HelperNodeList result = new HelperNodeList();
      Iterator it = set.iterator();

      while (it.hasNext()) {
         result.appendChild((Node) it.next());
      }

      return result;
   }

   /**
    * This method spreads all namespace attributes in a DOM document to their
    * children. This is needed because the XML Signature XPath transform
    * must evaluate the XPath against all nodes in the input, even against
    * XPath namespace nodes. Through a bug in XalanJ2, the namespace nodes are
    * not fully visible in the Xalan XPath model, so we have to do this by
    * hand in DOM spaces so that the nodes become visible in XPath space.
    *
    * @param doc
    * @see <A HREF="http://nagoya.apache.org/bugzilla/show_bug.cgi?id=2650">Namespace axis resolution is not XPath compliant </A>
    */
   public static void circumventBug2650(Document doc) {
      XMLUtils.circumventBug2650recurse(doc);
   }

   /**
    * This is the work horse for {@link #circumventBug2650}.
    *
    * @param node
    * @see <A HREF="http://nagoya.apache.org/bugzilla/show_bug.cgi?id=2650">Namespace axis resolution is not XPath compliant </A>
    */
   private static void circumventBug2650recurse(Node node) {

      if (node.getNodeType() == Node.ELEMENT_NODE) {
         Element element = (Element) node;
         NamedNodeMap attributes = element.getAttributes();
         int attributesLength = attributes.getLength();
         NodeList children = element.getChildNodes();
         int childrenLength = children.getLength();

         for (int j = 0; j < childrenLength; j++) {
            Node child = children.item(j);

            if (child.getNodeType() == Node.ELEMENT_NODE) {
               Element childElement = (Element) child;

               for (int i = 0; i < attributesLength; i++) {
                  Attr currentAttr = (Attr) attributes.item(i);
                  String name = currentAttr.getNodeName();

                  if (name.startsWith("xmlns")) {
                     String value = currentAttr.getNodeValue();
                     boolean mustBeDefinedInChild =
                        !childElement.hasAttribute(name);

                     if (mustBeDefinedInChild) {
                        childElement.setAttributeNS(Constants.NamespaceSpecNS,
                                                    name, value);
                     }
                  }
               }
            }
         }
      }

      for (Node child = node.getFirstChild(); child != null;
              child = child.getNextSibling()) {
         switch (child.getNodeType()) {

         case Node.ELEMENT_NODE :
         case Node.ENTITY_REFERENCE_NODE :
         case Node.DOCUMENT_NODE :
            circumventBug2650recurse(child);
         }
      }
   }

   /**
    * Method getXPath
    *
    * @param n
    * @param result
    * @return
    */
   private static String getXPath(Node n, String result) {

      if (n == null) {
         return result;
      }

      switch (n.getNodeType()) {

      case Node.ATTRIBUTE_NODE :
         return getXPath(((Attr) n).getOwnerElement(),
                         "/@" + ((Attr) n).getNodeName() + "=\""
                         + ((Attr) n).getNodeValue() + "\"");

      case Node.ELEMENT_NODE :
         return getXPath(n.getParentNode(),
                         "/" + ((Element) n).getTagName() + result);

      case Node.TEXT_NODE :
         return getXPath(n.getParentNode(), "/#text");

      case Node.DOCUMENT_NODE :
         if (result.length() > 0) {
            return result;
         } else {
            return "/";
         }
      }

      return result;
   }

   /**
    * Simple tool to return the position of a particular node in an XPath like String.
    *
    * @param n
    * @return
    */
   public static String getXPath(Node n) {
      return getXPath(n, "");
   }
}

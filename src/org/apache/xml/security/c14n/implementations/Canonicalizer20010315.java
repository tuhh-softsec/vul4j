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
package org.apache.xml.security.c14n.implementations;



import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.TransformerException;
import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xml.security.c14n.*;
import org.apache.xml.security.c14n.helper.*;
import org.apache.xml.security.utils.*;


/**
 * Class Canonicalizer20010315
 *
 * @author $Author$
 * @version $Revision$
 */
public abstract class Canonicalizer20010315 extends CanonicalizerSpi {
   //J-
   boolean _includeComments = false;

   Set _xpathNodeSet = null;

   Document _doc = null;
   Element _documentElement = null;
   Node _rootNodeOfC14n = null;

   Writer _writer = null;
   //J+

   /**
    * Constructor Canonicalizer20010315
    *
    * @param includeComments
    */
   public Canonicalizer20010315(boolean includeComments) {
      this._includeComments = includeComments;
   }

   /**
    * Method engineCanonicalizeSubTree
    *
    * @param rootNode
    * @return
    * @throws CanonicalizationException
    */
   public byte[] engineCanonicalizeSubTree(Node rootNode)
           throws CanonicalizationException {

      this._rootNodeOfC14n = rootNode;
      this._doc = XMLUtils.getOwnerDocument(this._rootNodeOfC14n);
      this._documentElement = this._doc.getDocumentElement();

      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();

         this._writer = new OutputStreamWriter(baos, Canonicalizer.ENCODING);

         Map inscopeNamespaces;

         if (rootNode.getNodeType() == Node.ELEMENT_NODE) {
            inscopeNamespaces = this.getInscopeNamespaces((Element) rootNode);
         } else {
            inscopeNamespaces = new HashMap();
         }

         Map alreadyVisible = new HashMap();

         this.canonicalizeSubTree(rootNode, inscopeNamespaces, alreadyVisible);
         this._writer.close();

         return baos.toByteArray();
      } catch (UnsupportedEncodingException ex) {
         throw new CanonicalizationException("empty", ex);
      } catch (IOException ex) {
         throw new CanonicalizationException("empty", ex);
      } finally {

         // mark contents for garbage collector
         this._rootNodeOfC14n = null;
         this._doc = null;
         this._documentElement = null;
         this._writer = null;
      }
   }

   /**
    * Method canonicalizeSubTree
    *
    * @param currentNode
    * @param inscopeNamespaces
    * @param alreadyVisible
    * @throws CanonicalizationException
    * @throws IOException
    */
   void canonicalizeSubTree(
           Node currentNode, Map inscopeNamespaces, Map alreadyVisible)
              throws CanonicalizationException, IOException {

      int currentNodeType = currentNode.getNodeType();

      switch (currentNodeType) {

      case Node.DOCUMENT_TYPE_NODE :
      default :
         break;

      case Node.ENTITY_NODE :
      case Node.NOTATION_NODE :
      case Node.DOCUMENT_FRAGMENT_NODE :
      case Node.ATTRIBUTE_NODE :

         // illegal node type during traversal
         throw new CanonicalizationException("empty");
      case Node.DOCUMENT_NODE :
         for (Node currentChild = currentNode.getFirstChild();
                 currentChild != null;
                 currentChild = currentChild.getNextSibling()) {
            canonicalizeSubTree(currentChild, inscopeNamespaces,
                                alreadyVisible);
         }
         break;

      case Node.COMMENT_NODE :
         if (this._includeComments) {
            int position = getPositionRelativeToDocumentElement(currentNode);

            if (position == NODE_AFTER_DOCUMENT_ELEMENT) {
               this._writer.write("\n");
            }

            outputCommentToWriter((Comment) currentNode);

            if (position == NODE_BEFORE_DOCUMENT_ELEMENT) {
               this._writer.write("\n");
            }
         }
         break;

      case Node.PROCESSING_INSTRUCTION_NODE :
         int position = getPositionRelativeToDocumentElement(currentNode);

         if (position == NODE_AFTER_DOCUMENT_ELEMENT) {
            this._writer.write("\n");
         }

         outputPItoWriter((ProcessingInstruction) currentNode);

         if (position == NODE_BEFORE_DOCUMENT_ELEMENT) {
            this._writer.write("\n");
         }
         break;

      case Node.TEXT_NODE :
      case Node.CDATA_SECTION_NODE :
         outputTextToWriter(currentNode.getNodeValue());
         break;

      case Node.ELEMENT_NODE :
         Element currentElement = (Element) currentNode;

         this._writer.write("<");
         this._writer.write(currentElement.getTagName());

         Object[] attrs =
            updateInscopeNamespacesAndReturnVisibleAttrs(currentElement,
               inscopeNamespaces, alreadyVisible);

         attrs = C14nHelper.sortAttributes(attrs);

         // we output all Attrs which are available
         for (int i = 0; i < attrs.length; i++) {
            outputAttrToWriter(((Attr) attrs[i]).getNodeName(),
                               ((Attr) attrs[i]).getNodeValue());
         }

         this._writer.write(">");

         // traversal
         for (Node currentChild = currentNode.getFirstChild();
                 currentChild != null;
                 currentChild = currentChild.getNextSibling()) {
            if (currentChild.getNodeType() == Node.ELEMENT_NODE) {

               /*
                * We must 'clone' the inscopeNamespaces to allow the descendants
                * to mess around in their own map
                */
               canonicalizeSubTree(currentChild,
                                   new HashMap(inscopeNamespaces),
                                   new HashMap(alreadyVisible));
            } else {
               canonicalizeSubTree(currentChild, inscopeNamespaces,
                                   alreadyVisible);
            }
         }

         this._writer.write("</");
         this._writer.write(currentElement.getTagName());
         this._writer.write(">");
         break;
      }
   }

   /**
    * This method updates the inscopeNamespaces based on the currentElement and
    * returns the Attr[]s to be outputted.
    *
    * @param inscopeNamespaces is changed by this method !!!
    * @param currentElement
    * @param alreadyVisible
    * @return the Attr[]s to be outputted
    * @throws CanonicalizationException
    */
   Object[] updateInscopeNamespacesAndReturnVisibleAttrs(
           Element currentElement, Map inscopeNamespaces, Map alreadyVisible)
              throws CanonicalizationException {

      List result = new Vector();
      NamedNodeMap attributes = currentElement.getAttributes();
      int attributesLength = attributes.getLength();

      for (int i = 0; i < attributesLength; i++) {
         Attr currentAttr = (Attr) attributes.item(i);
         String name = currentAttr.getNodeName();
         String value = currentAttr.getValue();

         if (name.equals("xmlns") && value.equals("")) {

            // undeclare default namespace
            inscopeNamespaces.remove("xmlns");
         } else if (name.startsWith("xmlns") &&!value.equals("")) {

            // update inscope namespaces
            inscopeNamespaces.put(name, value);
         } else if (name.startsWith("xml:")) {

            // output xml:blah features
            inscopeNamespaces.put(name, value);
         } else {

            // output regular attributes
            result.add(currentAttr);
         }
      }

      {

         // check whether default namespace must be deleted
         if (alreadyVisible.containsKey("xmlns")
                 &&!inscopeNamespaces.containsKey("xmlns")) {

            // undeclare default namespace
            alreadyVisible.remove("xmlns");

            Attr a = this._doc.createAttributeNS(Constants.NamespaceSpecNS,
                                                 "xmlns");

            a.setValue("");
            result.add(a);
         }
      }

      boolean isOrphanNode = currentElement == this._rootNodeOfC14n;
      Iterator it = inscopeNamespaces.keySet().iterator();

      while (it.hasNext()) {
         String name = (String) it.next();
         String inscopeValue = (String) inscopeNamespaces.get(name);

         if (name.startsWith("xml:")
                 && (isOrphanNode
                     ||!(alreadyVisible.containsKey(name)
                         && alreadyVisible.get(name).equals(inscopeValue)))) {
            alreadyVisible.put(name, inscopeValue);

            Attr a =
               this._doc.createAttributeNS(Constants.XML_LANG_SPACE_SpecNS,
                                           name);

            a.setValue(inscopeValue);
            result.add(a);
         } else if (!alreadyVisible.containsKey(name)
                    || (alreadyVisible.containsKey(name)
                        &&!alreadyVisible.get(name).equals(inscopeValue))) {
            if (C14nHelper.namespaceIsRelative(inscopeValue)) {
               Object exArgs[] = { currentElement.getTagName(), name,
                                   inscopeValue };

               throw new CanonicalizationException(
                  "c14n.Canonicalizer.RelativeNamespace", exArgs);
            }

            alreadyVisible.put(name, inscopeValue);

            Attr a = this._doc.createAttributeNS(Constants.NamespaceSpecNS,
                                                 name);

            a.setValue(inscopeValue);
            result.add(a);
         }
      }

      return result.toArray();
   }

   /**
    * Collects all relevant xml:* and xmlns:* attributes from all ancestor
    * Elements from rootNode and creates a Map containg the attribute
    * names/values.
    *
    * @param apexElement
    * @return
    * @throws CanonicalizationException
    */
   Map getInscopeNamespaces(Element apexElement)
           throws CanonicalizationException {

      Map result = new HashMap();

      for (Node parent = apexElement.getParentNode();
              ((parent != null) && (parent.getNodeType() == Node.ELEMENT_NODE));
              parent = parent.getParentNode()) {
         NamedNodeMap attributes = parent.getAttributes();
         int nrOfAttrs = attributes.getLength();

         for (int i = 0; i < nrOfAttrs; i++) {
            Attr currentAttr = (Attr) attributes.item(i);
            String name = currentAttr.getNodeName();
            String value = currentAttr.getValue();

            if (name.equals("xmlns") && value.equals("")) {
               ;    // result.remove(name);
            } else if (name.startsWith("xml:")
                       || (name.startsWith("xmlns") &&!value.equals(""))) {
               if (!result.containsKey(name)) {
                  result.put(name, value);
               }
            }
         }
      }

      return result;
   }

   //J-
   private static final int NODE_BEFORE_DOCUMENT_ELEMENT = -1;
   private static final int NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT = 0;
   private static final int NODE_AFTER_DOCUMENT_ELEMENT = 1;
   //J+

   /**
    * Checks whether a Comment or ProcessingInstruction is before or after the
    * document element. This is needed for prepending or appending "\n"s.
    *
    * @param currentNode comment or pi to check
    * @return NODE_BEFORE_DOCUMENT_ELEMENT, NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT or NODE_AFTER_DOCUMENT_ELEMENT
    * @see NODE_BEFORE_DOCUMENT_ELEMENT
    * @see NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT
    * @see NODE_AFTER_DOCUMENT_ELEMENT
    */
   static int getPositionRelativeToDocumentElement(Node currentNode) {

      if (currentNode == null) {
         return NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT;
      }

      Document doc = currentNode.getOwnerDocument();

      if (currentNode.getParentNode() != doc) {
         return NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT;
      }

      Element documentElement = doc.getDocumentElement();

      if (documentElement == null) {
         return NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT;
      }

      if (documentElement == currentNode) {
         return NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT;
      }

      for (Node x = currentNode; x != null; x = x.getNextSibling()) {
         if (x == documentElement) {
            return NODE_BEFORE_DOCUMENT_ELEMENT;
         }
      }

      return NODE_AFTER_DOCUMENT_ELEMENT;
   }

   /**
    * Method engineCanonicalizeXPathNodeSet
    *
    * @param xpathNodeSet
    * @return
    * @throws CanonicalizationException
    */
   public byte[] engineCanonicalizeXPathNodeSet(Set xpathNodeSet)
           throws CanonicalizationException {

      this._xpathNodeSet = xpathNodeSet;

      if (this._xpathNodeSet.size() == 0) {
         return new byte[0];
      }

      if (this._doc == null) {
         Node n = (Node) this._xpathNodeSet.iterator().next();

         this._doc = XMLUtils.getOwnerDocument(n);
         this._documentElement = this._doc.getDocumentElement();
         this._rootNodeOfC14n = this._doc;
      }

      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();

         this._writer = new OutputStreamWriter(baos, Canonicalizer.ENCODING);

         Map inscopeNamespaces = new HashMap();
         Map lostNamespaces = new HashMap();

         this.canonicalizeXPathNodeSet(this._rootNodeOfC14n, inscopeNamespaces,
                                       lostNamespaces, true);
         this._writer.close();

         return baos.toByteArray();
      } catch (UnsupportedEncodingException ex) {
         throw new CanonicalizationException("empty", ex);
      } catch (IOException ex) {
         throw new CanonicalizationException("empty", ex);
      } finally {
         this._xpathNodeSet = null;
         this._rootNodeOfC14n = null;
         this._doc = null;
         this._documentElement = null;
         this._writer = null;
      }
   }

   /**
    * Method canonicalizeXPathNodeSet
    *
    * @param currentNode
    * @param inscopeNamespaces
    * @param alreadyVisible
    * @param parentIsVisible
    * @throws CanonicalizationException
    * @throws IOException
    */
   void canonicalizeXPathNodeSet(
           Node currentNode, Map inscopeNamespaces, Map alreadyVisible, boolean parentIsVisible)
              throws CanonicalizationException, IOException {

      int currentNodeType = currentNode.getNodeType();
      boolean currentNodeIsVisible = this._xpathNodeSet.contains(currentNode);

      switch (currentNodeType) {

      case Node.DOCUMENT_TYPE_NODE :
      default :
         break;

      case Node.ENTITY_NODE :
      case Node.NOTATION_NODE :
      case Node.DOCUMENT_FRAGMENT_NODE :
      case Node.ATTRIBUTE_NODE :
         throw new CanonicalizationException("empty");
      case Node.DOCUMENT_NODE :
         for (Node currentChild = currentNode.getFirstChild();
                 currentChild != null;
                 currentChild = currentChild.getNextSibling()) {
            canonicalizeXPathNodeSet(currentChild, inscopeNamespaces,
                                     alreadyVisible, currentNodeIsVisible);
         }
         break;

      case Node.COMMENT_NODE :
         if (this._includeComments
                 && this._xpathNodeSet.contains(currentNode)) {
            int position = getPositionRelativeToDocumentElement(currentNode);

            if (position == NODE_AFTER_DOCUMENT_ELEMENT) {
               this._writer.write("\n");
            }

            outputCommentToWriter((Comment) currentNode);

            if (position == NODE_BEFORE_DOCUMENT_ELEMENT) {
               this._writer.write("\n");
            }
         }
         break;

      case Node.PROCESSING_INSTRUCTION_NODE :
         if (this._xpathNodeSet.contains(currentNode)) {
            int position = getPositionRelativeToDocumentElement(currentNode);

            if (position == NODE_AFTER_DOCUMENT_ELEMENT) {
               this._writer.write("\n");
            }

            outputPItoWriter((ProcessingInstruction) currentNode);

            if (position == NODE_BEFORE_DOCUMENT_ELEMENT) {
               this._writer.write("\n");
            }
         }
         break;

      case Node.TEXT_NODE :
      case Node.CDATA_SECTION_NODE :
         if (this._xpathNodeSet.contains(currentNode)) {
            outputTextToWriter(currentNode.getNodeValue());

            for (Node nextSibling =
                    currentNode
                       .getNextSibling(); (nextSibling != null) && ((nextSibling
                          .getNodeType() == Node.TEXT_NODE) || (nextSibling
                             .getNodeType() == Node
                                .CDATA_SECTION_NODE)); nextSibling =
                                   nextSibling.getNextSibling()) {

               /* The XPath data model allows to select only the first of a
                * sequence of mixed text and CDATA nodes. But we must output
                * them all, so we must search:
                *
                * @see http://nagoya.apache.org/bugzilla/show_bug.cgi?id=6329
                */
               outputTextToWriter(nextSibling.getNodeValue());
            }
         }
         break;

      case Node.ELEMENT_NODE :
         Element currentElement = (Element) currentNode;

         if (currentNodeIsVisible) {
            Object[] attrs =
               updateInscopeNamespacesAndReturnVisibleAttrsXPathImplVisibleElement(
                  currentElement, inscopeNamespaces, alreadyVisible);

            this._writer.write("<");
            this._writer.write(currentElement.getTagName());

            attrs = C14nHelper.sortAttributes(attrs);

            // we output all Attrs which are available
            for (int i = 0; i < attrs.length; i++) {
               outputAttrToWriter(((Attr) attrs[i]).getNodeName(),
                                  ((Attr) attrs[i]).getNodeValue());
            }

            this._writer.write(">");
         } else {
            this.updateInscopeNamespacesXPathImplInvisibleElement(
               currentElement, inscopeNamespaces);
         }

         // traversal
         for (Node currentChild = currentNode.getFirstChild();
                 currentChild != null;
                 currentChild = currentChild.getNextSibling()) {
            if (currentChild.getNodeType() == Node.ELEMENT_NODE) {

               /*
                * We must 'clone' the inscopeNamespaces to allow the descendants
                * to mess around in their own map
                */
               canonicalizeXPathNodeSet(currentChild,
                                        new HashMap(inscopeNamespaces),
                                        new HashMap(alreadyVisible),
                                        currentNodeIsVisible);
            } else {
               canonicalizeXPathNodeSet(currentChild, inscopeNamespaces,
                                        alreadyVisible, currentNodeIsVisible);
            }
         }

         if (currentNodeIsVisible) {
            this._writer.write("</");
            this._writer.write(currentElement.getTagName());
            this._writer.write(">");
         }
         break;
      }
   }

   /**
    * This method updates the inscopeNamespaces based on the currentElement and
    * returns the Attr[]s to be outputted.
    *
    * @param inscopeNamespaces is changed by this method !!!
    * @param currentElement
    * @param alreadyVisible
    * @return the Attr[]s to be outputted
    * @throws CanonicalizationException
    */
   Object[] updateInscopeNamespacesAndReturnVisibleAttrsXPathImplVisibleElement(
           Element currentElement, Map inscopeNamespaces, Map alreadyVisible)
              throws CanonicalizationException {

      List result = new Vector();
      NamedNodeMap attributes = currentElement.getAttributes();
      int attributesLength = attributes.getLength();

      for (int i = 0; i < attributesLength; i++) {
         Attr currentAttr = (Attr) attributes.item(i);
         String name = currentAttr.getNodeName();
         String value = currentAttr.getValue();

         if (name.equals("xmlns") && value.equals("")) {

            // undeclare default namespace
            if (this._xpathNodeSet.contains(currentAttr)) {
               inscopeNamespaces.remove("xmlns");
            }
         } else if (name.startsWith("xmlns") &&!value.equals("")) {

            // update inscope namespaces
            if (this._xpathNodeSet.contains(currentAttr)) {
               inscopeNamespaces.put(name, value);
            }
         } else if (name.startsWith("xml:")) {

            // update xml:blah features
            if (this._xpathNodeSet.contains(currentAttr)) {
               inscopeNamespaces.put(name, value);
            }
         } else {

            // output regular attributes
            if (this._xpathNodeSet.contains(currentAttr)) {
               result.add(currentAttr);
            }
         }
      }

      {

         // check whether default namespace must be deleted
         if (alreadyVisible.containsKey("xmlns")
                 &&!inscopeNamespaces.containsKey("xmlns")) {

            // undeclare default namespace
            alreadyVisible.remove("xmlns");

            Attr a = this._doc.createAttributeNS(Constants.NamespaceSpecNS,
                                                 "xmlns");

            a.setValue("");
            result.add(a);
         }
      }

      Node parentNode = currentElement.getParentNode();
      boolean isOrphanNode = !this._xpathNodeSet.contains(parentNode);
      Iterator it = inscopeNamespaces.keySet().iterator();

      while (it.hasNext()) {
         String name = (String) it.next();
         String inscopeValue = (String) inscopeNamespaces.get(name);

         if (name.startsWith("xml:")
                 && (isOrphanNode
                     ||!(alreadyVisible.containsKey(name)
                         && alreadyVisible.get(name).equals(inscopeValue)))) {
            alreadyVisible.put(name, inscopeValue);

            Attr a =
               this._doc.createAttributeNS(Constants.XML_LANG_SPACE_SpecNS,
                                           name);

            a.setValue(inscopeValue);
            result.add(a);
         } else if (!alreadyVisible.containsKey(name)
                    || (alreadyVisible.containsKey(name)
                        &&!alreadyVisible.get(name).equals(inscopeValue))) {
            if (C14nHelper.namespaceIsRelative(inscopeValue)) {
               Object exArgs[] = { currentElement.getTagName(), name,
                                   inscopeValue };

               throw new CanonicalizationException(
                  "c14n.Canonicalizer.RelativeNamespace", exArgs);
            }

            alreadyVisible.put(name, inscopeValue);

            Attr a = this._doc.createAttributeNS(Constants.NamespaceSpecNS,
                                                 name);

            a.setValue(inscopeValue);
            result.add(a);
         }
      }

      return result.toArray();
   }

   /**
    * This method is called in elements which are not in the document subset.
    *
    * @param inscopeNamespaces
    * @param currentElement
    * @throws CanonicalizationException
    */
   void updateInscopeNamespacesXPathImplInvisibleElement(
           Element currentElement, Map inscopeNamespaces)
              throws CanonicalizationException {

      NamedNodeMap attributes = currentElement.getAttributes();
      int attributesLength = attributes.getLength();

      for (int i = 0; i < attributesLength; i++) {
         Attr currentAttr = (Attr) attributes.item(i);
         String name = currentAttr.getNodeName();
         String value = currentAttr.getValue();

         if (name.equals("xmlns") && value.equals("")) {

            // undeclare default namespace
            inscopeNamespaces.remove("xmlns");
         } else if (name.startsWith("xmlns") &&!value.equals("")) {
            if (C14nHelper.namespaceIsRelative(value)) {
               Object exArgs[] = { currentElement.getTagName(), name, value };

               throw new CanonicalizationException(
                  "c14n.Canonicalizer.RelativeNamespace", exArgs);
            }

            // update inscope namespaces
            inscopeNamespaces.put(name, value);
         } else if (name.startsWith("xml:")) {
            inscopeNamespaces.put(name, value);
         }
      }
   }

   /**
    * Normalizes an {@link Attr}ibute value
    *
    * The string value of the node is modified by replacing
    * <UL>
    * <LI>all ampersands (&) with <CODE>&amp;amp;</CODE></LI>
    * <LI>all open angle brackets (<) with <CODE>&amp;lt;</CODE></LI>
    * <LI>all quotation mark characters with <CODE>&amp;quot;</CODE></LI>
    * <LI>and the whitespace characters <CODE>#x9</CODE>, #xA, and #xD, with character
    * references. The character references are written in uppercase
    * hexadecimal with no leading zeroes (for example, <CODE>#xD</CODE> is represented
    * by the character reference <CODE>&amp;#xD;</CODE>)</LI>
    * </UL>
    *
    * @param name
    * @param value
    * @throws IOException
    */
   void outputAttrToWriter(String name, String value) throws IOException {

      this._writer.write(" ");
      this._writer.write(name);
      this._writer.write("=\"");

      int length = value.length();

      for (int i = 0; i < length; i++) {
         char c = value.charAt(i);

         switch (c) {

         case '&' :
            this._writer.write("&amp;");
            break;

         case '<' :
            this._writer.write("&lt;");
            break;

         case '"' :
            this._writer.write("&quot;");
            break;

         case 0x09 :    // '\t'
            this._writer.write("&#x9;");
            break;

         case 0x0A :    // '\n'
            this._writer.write("&#xA;");
            break;

         case 0x0D :    // '\r'
            this._writer.write("&#xD;");
            break;

         default :
            this._writer.write(c);
            break;
         }
      }

      this._writer.write("\"");
   }

   /**
    * Normalizes a {@link org.w3c.dom.Comment} value
    *
    * @param currentPI
    * @throws IOException
    */
   void outputPItoWriter(ProcessingInstruction currentPI) throws IOException {

      this._writer.write("<?");

      String target = currentPI.getTarget();
      int length = target.length();

      for (int i = 0; i < length; i++) {
         char c = target.charAt(i);

         switch (c) {

         case 0x0D :
            this._writer.write("&#xD;");
            break;

         default :
            this._writer.write(c);
            break;
         }
      }

      String data = currentPI.getData();

      length = data.length();

      if ((data != null) && (length > 0)) {
         this._writer.write(" ");

         for (int i = 0; i < length; i++) {
            char c = data.charAt(i);

            switch (c) {

            case 0x0D :
               this._writer.write("&#xD;");
               break;

            default :
               this._writer.write(c);
               break;
            }
         }
      }

      this._writer.write("?>");
   }

   /**
    * Method outputCommentToWriter
    *
    * @param currentComment
    * @throws IOException
    */
   void outputCommentToWriter(Comment currentComment) throws IOException {

      this._writer.write("<!--");

      String data = currentComment.getData();
      int length = data.length();

      for (int i = 0; i < length; i++) {
         char c = data.charAt(i);

         switch (c) {

         case 0x0D :
            this._writer.write("&#xD;");
            break;

         default :
            this._writer.write(c);
            break;
         }
      }

      this._writer.write("-->");
   }

   /**
    * Method outputTextToWriter
    *
    * @param text
    * @throws IOException
    */
   void outputTextToWriter(String text) throws IOException {

      int length = text.length();

      for (int i = 0; i < length; i++) {
         char c = text.charAt(i);

         switch (c) {

         case '&' :
            this._writer.write("&amp;");
            break;

         case '<' :
            this._writer.write("&lt;");
            break;

         case '>' :
            this._writer.write("&gt;");
            break;

         case 0xD :
            this._writer.write("&#xD;");
            break;

         default :
            this._writer.write(c);
            break;
         }
      }
   }

   /**
    * Method engineCanonicalizeXPathNodeSet
    *
    * @param xpathNodeSet
    * @param inclusiveNamespaces
    * @return
    * @throws CanonicalizationException
    */
   public byte[] engineCanonicalizeXPathNodeSet(
           Set xpathNodeSet, String inclusiveNamespaces)
              throws CanonicalizationException {

      /** @todo well, should we throw UnsupportedOperationException ? */
      throw new CanonicalizationException(
         "c14n.Canonicalizer.UnsupportedOperation");
   }

   /**
    * Method engineCanonicalizeSubTree
    *
    * @param rootNode
    * @param inclusiveNamespaces
    * @return
    * @throws CanonicalizationException
    */
   public byte[] engineCanonicalizeSubTree(
           Node rootNode, String inclusiveNamespaces)
              throws CanonicalizationException {

      /** @todo well, should we throw UnsupportedOperationException ? */
      throw new CanonicalizationException(
         "c14n.Canonicalizer.UnsupportedOperation");
   }
}

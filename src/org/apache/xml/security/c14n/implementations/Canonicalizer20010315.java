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
    *
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
            inscopeNamespaces = this.getinscopeNamespaces((Element) rootNode);
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

         List attrs =
            updateinscopeNamespacesAndReturnVisibleAttrs(currentElement,
               inscopeNamespaces, alreadyVisible);

         // we output all Attrs which are available
         for (int i = 0; i < attrs.size(); i++) {
            outputAttrToWriter(((Attr) attrs.get(i)).getNodeName(),
                               ((Attr) attrs.get(i)).getNodeValue());
         }

         this._writer.write(">");

         // traversal
         for (Node currentChild = currentNode.getFirstChild();
                 currentChild != null;
                 currentChild = currentChild.getNextSibling()) {
            if (currentChild.getNodeType() == Node.ELEMENT_NODE) {

               /*
                * We must 'clone' the inscopeXMLAttrs to allow the descendants
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
    * This method updates the inscopeXMLAttrs based on the currentElement and
    * returns the Attr[]s to be outputted.
    *
    * @param inscopeXMLAttrs is changed by this method !!!
    * @param currentElement
    * @param alreadyVisible
    * @return the Attr[]s to be outputted
    * @throws CanonicalizationException
    */
   List updateinscopeNamespacesAndReturnVisibleAttrs(
           Element currentElement, Map inscopeXMLAttrs, Map alreadyVisible)
              throws CanonicalizationException {

      Vector ns = new Vector();
      Vector at = new Vector();
      NamedNodeMap attributes = currentElement.getAttributes();
      int attributesLength = attributes.getLength();

      for (int i = 0; i < attributesLength; i++) {
         Attr currentAttr = (Attr) attributes.item(i);
         String name = currentAttr.getNodeName();
         String value = currentAttr.getValue();

         if (name.equals("xmlns") && value.equals("")) {

            // undeclare default namespace
            inscopeXMLAttrs.remove("xmlns");
         } else if (name.startsWith("xmlns")) {

            // update inscope namespaces
            if (!value.equals("")) {
               inscopeXMLAttrs.put(name, value);
            }
         } else if (name.startsWith("xml:")) {

            // output xml:blah features
            inscopeXMLAttrs.put(name, value);
         } else {

            // output regular attributes
            at.add(currentAttr);
         }
      }

      {

         // check whether default namespace must be deleted
         if (alreadyVisible.containsKey("xmlns")
                 &&!inscopeXMLAttrs.containsKey("xmlns")) {

            // undeclare default namespace
            alreadyVisible.remove("xmlns");

            Attr a = this._doc.createAttributeNS(Constants.NamespaceSpecNS,
                                                 "xmlns");

            a.setValue("");
            ns.add(a);
         }
      }

      boolean isOrphanNode = currentElement == this._rootNodeOfC14n;
      Iterator it = inscopeXMLAttrs.keySet().iterator();

      while (it.hasNext()) {
         String name = (String) it.next();
         String inscopeValue = (String) inscopeXMLAttrs.get(name);

         if (name.startsWith("xml:")
                 && (isOrphanNode
                     ||!(alreadyVisible.containsKey(name)
                         && alreadyVisible.get(name).equals(inscopeValue)))) {
            alreadyVisible.put(name, inscopeValue);

            Attr a =
               this._doc.createAttributeNS(Constants.XML_LANG_SPACE_SpecNS,
                                           name);

            a.setValue(inscopeValue);
            at.add(a);
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
            ns.add(a);
         }
      }

      Collections.sort(ns,
                       new org.apache.xml.security.c14n.helper.NSAttrCompare());
      Collections.sort(at,
                       new org.apache.xml.security.c14n.helper
                          .NonNSAttrCompare());
      ns.addAll(at);

      return ns;
   }

   /**
    * Collects all relevant xml:* and xmlns:* attributes from all ancestor
    * Elements from rootNode and creates a Map containg the attribute
    * names/values.
    *
    * @param apexElement
    *
    */
   public static Map getinscopeNamespaces(Element apexElement) {

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
    *
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
      }

      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();

         this._writer = new OutputStreamWriter(baos, Canonicalizer.ENCODING);

         this.canonicalizeXPathNodeSet(this._doc, true,
                                       new C14nCtx());
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
    * @param parentIsVisible
    * @param ctx
    * @throws CanonicalizationException
    * @throws IOException
    */
   void canonicalizeXPathNodeSet(
           Node currentNode, boolean parentIsVisible, C14nCtx ctx)
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
            canonicalizeXPathNodeSet(currentChild, currentNodeIsVisible, ctx);
         }
         break;

      case Node.COMMENT_NODE :
         if (this._includeComments && currentNodeIsVisible) {
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
         if (currentNodeIsVisible) {
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
         if (currentNodeIsVisible) {
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
            this._writer.write("<");
            this._writer.write(currentElement.getTagName());
         }

         // we output all Attrs which are available
         List attrs = this.getAttrs(currentElement, parentIsVisible, ctx);
         int attrsLength = attrs.size();

         for (int i = 0; i < attrsLength; i++) {
            Attr a = (Attr) attrs.get(i);

            outputAttrToWriter(a.getNodeName(), a.getNodeValue());
         }

         if (currentNodeIsVisible) {
            this._writer.write(">");
         }

         // traversal
         for (Node currentChild = currentNode.getFirstChild();
                 currentChild != null;
                 currentChild = currentChild.getNextSibling()) {
            if (currentChild.getNodeType() == Node.ELEMENT_NODE) {

               /*
                * We must 'clone' the inscopeXMLAttrs to allow the descendants
                * to mess around in their own map
                */
               canonicalizeXPathNodeSet(currentChild, currentNodeIsVisible,
                                        ctx.copy());
            } else {
               canonicalizeXPathNodeSet(currentChild, currentNodeIsVisible,
                                        ctx);
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
    * Method getAttrs
    *
    * @param currentElement
    * @param parentIsVisible
    * @param ctx
    *
    * @throws CanonicalizationException
    */
   List getAttrs(Element currentElement, boolean parentIsVisible, C14nCtx ctx)
           throws CanonicalizationException {

      boolean currentElementIsInNodeset =
         this._xpathNodeSet.contains(currentElement);
      Vector namespacesInSubset = new Vector();
      Vector attributesInSubset = new Vector();
      Vector xmlAttributesInSubset = new Vector();
      NamedNodeMap attributes = currentElement.getAttributes();
      int attributesLength = attributes.getLength();

      for (int i = 0; i < attributesLength; i++) {
         Attr currentAttr = (Attr) attributes.item(i);
         String URI = currentAttr.getNamespaceURI();

         if (this._xpathNodeSet.contains(currentAttr)) {
            if (URI != null) {
               if (Constants.NamespaceSpecNS.equals(URI)) {
                  String value = currentAttr.getValue();

                  if (C14nHelper.namespaceIsRelative(value)) {
                     Object exArgs[] = { currentElement.getTagName(),
                                         currentAttr.getNodeName(), value };

                     throw new CanonicalizationException(
                        "c14n.Canonicalizer.RelativeNamespace", exArgs);
                  }

                  namespacesInSubset.add(currentAttr);
               } else if (Constants.XML_LANG_SPACE_SpecNS.equals(URI)) {
                  xmlAttributesInSubset.add(currentAttr);
                  ctx.a.put(currentAttr.getNodeName(), currentAttr);
               } else {
                  attributesInSubset.add(currentAttr);
               }
            } else {
               attributesInSubset.add(currentAttr);
            }
         } else {
            if (URI != null && Constants.XML_LANG_SPACE_SpecNS.equals(URI)) {
               ctx.a.put(currentAttr.getNodeName(), currentAttr);
            }
         }
      }

      Collections.sort(namespacesInSubset,
                       new org.apache.xml.security.c14n.helper.NSAttrCompare());

      // update the ctx.a with the xml:* values
      for (int i = 0; i < xmlAttributesInSubset.size(); i++) {
         Attr currentAttr = (Attr) xmlAttributesInSubset.get(i);
         String name = currentAttr.getNodeName();

         ctx.a.put(name, currentAttr);
      }

      if (currentElementIsInNodeset &&!parentIsVisible) {
         // it's an orphan node, so we must include all xml:* attrs all the
         // ancestor axis along
         Iterator it = ctx.a.keySet().iterator();

         while (it.hasNext()) {
            String name = (String) it.next();

            attributesInSubset.add(ctx.a.get(name));
         }
      }
      Collections.sort(attributesInSubset,
                       new org.apache.xml.security.c14n.helper
                          .NonNSAttrCompare());


      Vector nsResult = new Vector();
      Map outputNamespaces = new HashMap();

      if (namespacesInSubset.size() > 0) {
         int firstNonDefaultNS = -1;
         Attr firstNode = (Attr) namespacesInSubset.get(0);

         if (!firstNode.getNodeName().equals("xmlns")) {

            // there is no default namespace in L
            firstNonDefaultNS = 0;

            // if the output ancestor defines a default namespace
            if (currentElementIsInNodeset && ctx.n.containsKey("xmlns") &&!ctx.n.get("xmlns").equals("")) {
               Attr xmlns = this._doc.createAttributeNS(Constants.NamespaceSpecNS, "xmlns");
               xmlns.setValue("");
               nsResult.add(xmlns);
            }
         } else if (firstNode.getNodeName().equals("xmlns")
                    && firstNode.getValue().equals("")) {

            // there is an empty default namespace in L
            // skip
            firstNonDefaultNS = 1;

            // if the output ancestor defines a default namespace
            if (currentElementIsInNodeset && ctx.n.containsKey("xmlns") &&!ctx.n.get("xmlns").equals("")) {
               nsResult.add(firstNode);
            }
         } else {
            firstNonDefaultNS = 0;
         }

         // handle non-empty namespaces
         for (int i = firstNonDefaultNS; i < namespacesInSubset.size(); i++) {
            Attr currentAttr = (Attr) namespacesInSubset.get(i);
            String name = currentAttr.getNodeName();

            outputNamespaces.put(name, currentAttr);

            if (!ctx.n.containsKey(name) || !((Attr) ctx.n.get(name)).getValue().equals(currentAttr.getValue())) {
               nsResult.add(currentAttr);
            }
         }
      }

      if (currentElementIsInNodeset) {

         // if the element E is in the node set, remember the namespaces for the next one
         ctx.n = outputNamespaces;
      }

      // and append them to the result
      nsResult.addAll(attributesInSubset);

      return nsResult;
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
      if (currentPI == null) {
        return;
      }

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
      if (currentComment == null) {
        return;
      }

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
      if (text == null) {
        return;
      }

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
    *
    * @throws CanonicalizationException
    */
   public byte[] engineCanonicalizeXPathNodeSet(
           Set xpathNodeSet, String inclusiveNamespaces)
              throws CanonicalizationException {

      /** $todo$ well, should we throw UnsupportedOperationException ? */
      throw new CanonicalizationException(
         "c14n.Canonicalizer.UnsupportedOperation");
   }

   /**
    * Method engineCanonicalizeSubTree
    *
    * @param rootNode
    * @param inclusiveNamespaces
    *
    * @throws CanonicalizationException
    */
   public byte[] engineCanonicalizeSubTree(
           Node rootNode, String inclusiveNamespaces)
              throws CanonicalizationException {

      /** $todo$ well, should we throw UnsupportedOperationException ? */
      throw new CanonicalizationException(
         "c14n.Canonicalizer.UnsupportedOperation");
   }

   /**
    * Class C14nCtx
    *
    * @author $Author$
    * @version $Revision$
    */
   class C14nCtx {

      /** Field a */
      Map a;

      /** Field n */
      Map n;

      /**
       * Constructor C14nCtx
       *
       */
      public C14nCtx() {
         this.a = new HashMap();
         this.n = new HashMap();
      }

      /**
       * Constructor C14nCtx
       *
       * @param a
       * @param n
       */
      public C14nCtx(Map a, Map n) {
         this.a = a;
         this.n = n;
      }

      /**
       * Method copy
       *
       *
       */
      public C14nCtx copy() {

         C14nCtx c = new C14nCtx();

         c.a = new HashMap(this.a);
         c.n = new HashMap(this.n);

         return c;
      }
   }
}

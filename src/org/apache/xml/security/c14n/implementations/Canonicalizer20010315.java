
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
import org.apache.xml.security.c14n.*;
import org.apache.xml.security.c14n.helper.*;
import org.apache.xml.security.utils.*;
import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.*;
import org.xml.sax.*;


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
    * @throws CanonicalizationException
    */
   public byte[] engineCanonicalizeSubTree(Node rootNode)
           throws CanonicalizationException {

      this._rootNodeOfC14n = rootNode;
      this._doc = XMLUtils.getOwnerDocument(this._rootNodeOfC14n);
      this._documentElement = this._doc.getDocumentElement();

      XMLUtils.circumventBug2650(this._doc);

      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();

         this._writer = new OutputStreamWriter(baos, Canonicalizer.ENCODING);

         this.canonicalizeSubTree(this._rootNodeOfC14n, true);
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
    * @throws CanonicalizationException
    * @throws IOException
    */
   void canonicalizeSubTree(Node currentNode, boolean isRootNode)
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
            canonicalizeSubTree(currentChild, false);
         }
         break;

      case Node.COMMENT_NODE :
         if (this._includeComments) {
            int position =
               Canonicalizer20010315.getPositionRelativeToDocumentElement(
                  currentNode);

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
         int position =
            Canonicalizer20010315.getPositionRelativeToDocumentElement(
               currentNode);

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

         Object[] attrs = this.handleAttributesSubtree(currentElement);

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
            canonicalizeSubTree(currentChild, false);
         }

         this._writer.write("</");
         this._writer.write(currentElement.getTagName());
         this._writer.write(">");
         break;
      }
   }

   /**
    * Returns the Attr[]s to be outputted for the given element.
    * <br>
    * IMPORTANT: This method expects to work on a modified DOM tree, i.e. a DOM which has
    * been prepared using {@link XMLUtils#circumventBug2650(Document)}.
    * <br>
    * The code of this method is a copy of {@link #handleAttributes(Element)},
    * whereas it takes into account that subtree-c14n is -- well -- subtree-based.
    * So if the element in question isRoot of c14n, it's parent is not in the
    * node set, as well as all other ancestors.
    *
    * @param E
    * @return the Attr[]s to be outputted
    * @throws CanonicalizationException
    */
   Object[] handleAttributesSubtree(Element E)
           throws CanonicalizationException {

      boolean isRoot = E == this._rootNodeOfC14n;

      // result will contain the attrs which have to be outputted
      List result = new Vector();
      NamedNodeMap attrs = E.getAttributes();
      int attrsLength = attrs.getLength();

      /* ***********************************************************************
       * Handle xmlns=""
       * ***********************************************************************/

      // first check whether we have to output xmlns=""
      Attr xmlns = E.getAttributeNodeNS(Constants.NamespaceSpecNS, "xmlns");

      if (xmlns == null) {
         throw new CanonicalizationException(
            "c14n.XMLUtils.circumventBug2650forgotten");
      }

      /* To begin processing L, if the first node is not the default namespace
       * node (a node with no namespace URI and no local name), then generate
       * a space followed by xmlns="" if and only if the following conditions
       * are met:
       */
      boolean firstNodeIsNotDefaultNamespaceNode =
         xmlns.getNodeValue().equals("");

      /* the element E that owns the axis is in the node-set
       */
      if (firstNodeIsNotDefaultNamespaceNode) {

         /* The nearest ancestor element of E in the node-set has a default
          * namespace node in the node-set (default namespace nodes always
          * have non-empty values in XPath)
          */
         Node ancestor = E.getParentNode();

         if (!isRoot && (ancestor.getNodeType() == Node.ELEMENT_NODE)) {
            Attr xmlnsAncestor = ((Element) ancestor).getAttributeNodeNS(
               Constants.NamespaceSpecNS, "xmlns");

            if (xmlnsAncestor == null) {
               throw new CanonicalizationException(
                  "c14n.XMLUtils.circumventBug2650forgotten");
            }

            if (!xmlnsAncestor.getNodeValue().equals("")) {

               // OK, we must output xmlns=""
               result.add(xmlns);
            }
         }
      }

      /* ***********************************************************************
       * Handle namespace axis
       * ***********************************************************************/
      handleNamespaces: for (int i = 0; i < attrsLength; i++) {
         Attr N = (Attr) attrs.item(i);

         if (!Constants.NamespaceSpecNS.equals(N.getNamespaceURI())) {

            // only handle namespaces here
            continue handleNamespaces;
         }

         if (C14nHelper.namespaceIsRelative(N)) {
            Object exArgs[] = { E.getTagName(), N.getName(), N.getNodeValue() };

            throw new CanonicalizationException(
               "c14n.Canonicalizer.RelativeNamespace", exArgs);
         }

         if (N.getName().equals("xmlns") && N.getNodeValue().equals("")) {

            // xmlns="" already handled
            continue handleNamespaces;
         }

         if ("xml".equals(N.getLocalName())
                 && Constants.XML_LANG_SPACE_SpecNS.equals(N.getNodeValue())) {

            /* except omit namespace node with local name xml, which defines
             * the xml prefix, if its string value is http://www.w3.org/XML/1998/namespace.
             */
            continue handleNamespaces;
         }

         /* OK, now we have a 'real' namespace in N, no attrs, no xmlns=""
          * and no xmlns:xml="http://www.w3.org/XML/1998/namespace"
          */

         /* A namespace node N is ignored if the nearest ancestor element of E
          * that is in the node-set has a namespace node in the node-set with
          * the same local name and value as N.
          *
          * Otherwise, process the namespace node N in the same way as an
          * attribute node, except assign the local name xmlns to the default
          * namespace node if it exists (in XPath, the default namespace node
          * has an empty URI and local name).
          */
         boolean ignoreN = false;
         Node ancestor = E.getParentNode();

         if (!isRoot && (ancestor.getNodeType() == Node.ELEMENT_NODE)) {
            Attr NA = ((Element) ancestor).getAttributeNodeNS(
               Constants.NamespaceSpecNS, N.getLocalName());

            if ((NA != null) && NA.getNodeValue().equals(N.getNodeValue())) {
               ignoreN = true;
            }
         }

         if (!ignoreN) {
            result.add(N);
         }
      }

      /* ***********************************************************************
       * Handle attribute axis
       * ***********************************************************************/
      handleAttributes: for (int i = 0; i < attrsLength; i++) {
         Attr a = (Attr) attrs.item(i);

         if (Constants.NamespaceSpecNS.equals(a.getNamespaceURI())) {

            // only handle attributes here
            continue handleAttributes;
         }

         result.add(a);
      }

      /* The processing of an element node E MUST be modified slightly when an
       * XPath node-set is given as input and the element's parent is omitted
       * from the node-set. The method for processing the attribute axis of an
       * element E in the node-set is enhanced. All element nodes along E's
       * ancestor axis are examined for nearest occurrences of attributes in
       * the xml namespace, such as xml:lang and xml:space (whether or not they
       * are in the node-set). From this list of attributes, remove any that are
       * in E's attribute axis (whether or not they are in the node-set). Then,
       * lexicographically merge this attribute list with the nodes of E's
       * attribute axis that are in the node-set. The result of visiting the
       * attribute axis is computed by processing the attribute nodes in this
       * merged attribute list.
       */
      if (isRoot) {

         // E is in the node-set
         Node parent = E.getParentNode();
         Map loa = new HashMap();

         if ((parent != null) && (parent.getNodeType() == Node.ELEMENT_NODE)) {

            // parent element is not in node set
            for (Node ancestor = parent;
                    (ancestor != null)
                    && (ancestor.getNodeType() == Node.ELEMENT_NODE);
                    ancestor = ancestor.getParentNode()) {

               // for all ancestor elements
               NamedNodeMap ancestorAttrs =
                  ((Element) ancestor).getAttributes();

               for (int i = 0; i < ancestorAttrs.getLength(); i++) {

                  // for all attributes in the ancestor element
                  Attr currentAncestorAttr = (Attr) ancestorAttrs.item(i);

                  if (Constants.XML_LANG_SPACE_SpecNS.equals(
                          currentAncestorAttr.getNamespaceURI())) {

                     // do we have an xml:* ?
                     if (!E.hasAttributeNS(
                             Constants.XML_LANG_SPACE_SpecNS,
                             currentAncestorAttr.getLocalName())) {

                        // the xml:* attr is not in E
                        if (!loa.containsKey(currentAncestorAttr.getName())) {
                           loa.put(currentAncestorAttr.getName(),
                                   currentAncestorAttr);
                        }
                     }
                  }
               }
            }
         }

         Iterator it = loa.values().iterator();

         while (it.hasNext()) {
            result.add(it.next());
         }
      }

      return result.toArray();
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

         this.canonicalizeXPathNodeSet(this._rootNodeOfC14n);
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
    * @throws CanonicalizationException
    * @throws IOException
    */
   void canonicalizeXPathNodeSet(Node currentNode)
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
            canonicalizeXPathNodeSet(currentChild);
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

            for (Node nextSibling = currentNode.getNextSibling();
                    (nextSibling != null)
                    && ((nextSibling.getNodeType() == Node.TEXT_NODE)
                        || (nextSibling.getNodeType()
                            == Node.CDATA_SECTION_NODE));
                    nextSibling = nextSibling.getNextSibling()) {

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
         Object[] attrs = handleAttributes(currentElement);

         attrs = C14nHelper.sortAttributes(attrs);

         for (int i = 0; i < attrs.length; i++) {
            Attr attr = (Attr) attrs[i];

            outputAttrToWriter(attr.getNodeName(), attr.getNodeValue());
         }

         if (currentNodeIsVisible) {
            this._writer.write(">");
         }

         // traversal
         for (Node currentChild = currentNode.getFirstChild();
                 currentChild != null;
                 currentChild = currentChild.getNextSibling()) {
            canonicalizeXPathNodeSet(currentChild);
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
    * Returns the Attr[]s to be outputted for the given element.
    * <br>
    * IMPORTANT: This method expects to work on a modified DOM tree, i.e. a DOM which has
    * been prepared using {@link XMLUtils#circumventBug2650(Document)}.
    *
    * @param E
    * @return the Attr[]s to be outputted
    * @throws CanonicalizationException
    */
   Object[] handleAttributes(Element E) throws CanonicalizationException {

      // result will contain the attrs which have to be outputted
      List result = new Vector();
      NamedNodeMap attrs = E.getAttributes();
      int attrsLength = attrs.getLength();

      /* ***********************************************************************
       * Handle xmlns=""
       * ***********************************************************************/

      // first check whether we have to output xmlns=""
      Attr xmlns = E.getAttributeNodeNS(Constants.NamespaceSpecNS, "xmlns");

      if (xmlns == null) {
         throw new CanonicalizationException(
            "c14n.XMLUtils.circumventBug2650forgotten");
      }

      /* To begin processing L, if the first node is not the default namespace
       * node (a node with no namespace URI and no local name), then generate
       * a space followed by xmlns="" if and only if the following conditions
       * are met:
       */
      boolean firstNodeIsNotDefaultNamespaceNode =
         xmlns.getNodeValue().equals("");

      /* the element E that owns the axis is in the node-set
       */
      if (firstNodeIsNotDefaultNamespaceNode
              && this._xpathNodeSet.contains(E)) {

         /* The nearest ancestor element of E in the node-set has a default
          * namespace node in the node-set (default namespace nodes always
          * have non-empty values in XPath)
          */
         for (Node ancestor = E.getParentNode();
                 (ancestor != null)
                 && (ancestor.getNodeType() == Node.ELEMENT_NODE);
                 ancestor = ancestor.getParentNode()) {
            if (this._xpathNodeSet.contains(ancestor)) {
               Attr xmlnsAncestor = ((Element) ancestor).getAttributeNodeNS(
                  Constants.NamespaceSpecNS, "xmlns");

               if (xmlnsAncestor == null) {
                  throw new CanonicalizationException(
                     "c14n.XMLUtils.circumventBug2650forgotten");
               }

               if (!xmlnsAncestor.getNodeValue().equals("")) {

                  // OK, we must output xmlns=""
                  result.add(xmlns);
               }

               break;
            }
         }
      }

      /* ***********************************************************************
       * Handle namespace axis
       * ***********************************************************************/
      handleNamespaces: for (int i = 0; i < attrsLength; i++) {
         Attr N = (Attr) attrs.item(i);

         if (!Constants.NamespaceSpecNS.equals(N.getNamespaceURI())) {

            // only handle namespaces here
            continue handleNamespaces;
         }

         if (C14nHelper.namespaceIsRelative(N)) {
            Object exArgs[] = { E.getTagName(), N.getName(), N.getNodeValue() };

            throw new CanonicalizationException(
               "c14n.Canonicalizer.RelativeNamespace", exArgs);
         }

         if (N.getName().equals("xmlns") && N.getNodeValue().equals("")) {

            // xmlns="" already handled
            continue handleNamespaces;
         }

         if (!this._xpathNodeSet.contains(N)) {

            // Consider a list L containing only namespace nodes in the axis and in the node-set
            //
            // only if N in the node set
            continue handleNamespaces;
         }

         if ("xml".equals(N.getLocalName())
                 && Constants.XML_LANG_SPACE_SpecNS.equals(N.getNodeValue())) {

            /* except omit namespace node with local name xml, which defines
             * the xml prefix, if its string value is http://www.w3.org/XML/1998/namespace.
             */
            continue handleNamespaces;
         }

         /* OK, now we have a 'real' namespace in N, no attrs, no xmlns=""
          * and no xmlns:xml="http://www.w3.org/XML/1998/namespace"
          */

         /* A namespace node N is ignored if the nearest ancestor element of E
          * that is in the node-set has a namespace node in the node-set with
          * the same local name and value as N.
          *
          * Otherwise, process the namespace node N in the same way as an
          * attribute node, except assign the local name xmlns to the default
          * namespace node if it exists (in XPath, the default namespace node
          * has an empty URI and local name).
          */
         boolean ignoreN = false;

         lookForAncestorsInNodeset: for (Node ancestor = E.getParentNode();
                                            (ancestor != null)
                                            && (ancestor.getNodeType()
                                                == Node.ELEMENT_NODE);
                                            ancestor =
                                               ancestor.getParentNode()) {
            if (this._xpathNodeSet.contains(ancestor)) {
               Attr NA = ((Element) ancestor).getAttributeNodeNS(
                  Constants.NamespaceSpecNS, N.getLocalName());

               if ((NA != null) && NA.getNodeValue().equals(N.getNodeValue())
                       && this._xpathNodeSet.contains(NA)) {
                  ignoreN = true;
               }

               break lookForAncestorsInNodeset;
            }
         }

         if (!ignoreN) {
            result.add(N);
         }
      }

      /* ***********************************************************************
       * Handle attribute axis
       * ***********************************************************************/
      handleAttributes: for (int i = 0; i < attrsLength; i++) {
         Attr a = (Attr) attrs.item(i);

         if (Constants.NamespaceSpecNS.equals(a.getNamespaceURI())) {

            // only handle attributes here
            continue handleAttributes;
         }

         if (!this._xpathNodeSet.contains(a)) {

            // only if a in the node set
            continue handleAttributes;
         }

         result.add(a);
      }

      /* The processing of an element node E MUST be modified slightly when an
       * XPath node-set is given as input and the element's parent is omitted
       * from the node-set. The method for processing the attribute axis of an
       * element E in the node-set is enhanced. All element nodes along E's
       * ancestor axis are examined for nearest occurrences of attributes in
       * the xml namespace, such as xml:lang and xml:space (whether or not they
       * are in the node-set). From this list of attributes, remove any that are
       * in E's attribute axis (whether or not they are in the node-set). Then,
       * lexicographically merge this attribute list with the nodes of E's
       * attribute axis that are in the node-set. The result of visiting the
       * attribute axis is computed by processing the attribute nodes in this
       * merged attribute list.
       */
      if (this._xpathNodeSet.contains(E)) {

         // E is in the node-set
         Node parent = E.getParentNode();
         Map loa = new HashMap();

         if ((parent != null) && (parent.getNodeType() == Node.ELEMENT_NODE)
                 &&!this._xpathNodeSet.contains(parent)) {

            // parent element is not in node set
            for (Node ancestor = parent;
                    (ancestor != null)
                    && (ancestor.getNodeType() == Node.ELEMENT_NODE);
                    ancestor = ancestor.getParentNode()) {

               // for all ancestor elements
               NamedNodeMap ancestorAttrs =
                  ((Element) ancestor).getAttributes();

               for (int i = 0; i < ancestorAttrs.getLength(); i++) {

                  // for all attributes in the ancestor element
                  Attr currentAncestorAttr = (Attr) ancestorAttrs.item(i);

                  if (Constants.XML_LANG_SPACE_SpecNS.equals(
                          currentAncestorAttr.getNamespaceURI())) {

                     // do we have an xml:* ?
                     if (!E.hasAttributeNS(
                             Constants.XML_LANG_SPACE_SpecNS,
                             currentAncestorAttr.getLocalName())) {

                        // the xml:* attr is not in E
                        if (!loa.containsKey(currentAncestorAttr.getName())) {
                           loa.put(currentAncestorAttr.getName(),
                                   currentAncestorAttr);
                        }
                     }
                  }
               }
            }
         }

         Iterator it = loa.values().iterator();

         while (it.hasNext()) {
            result.add(it.next());
         }
      }

      return result.toArray();
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
    * Always throws a CanonicalizationException because this is inclusive c14n.
    *
    * @param xpathNodeSet
    * @param inclusiveNamespaces
    * @throws CanonicalizationException
    */
   public byte[] engineCanonicalizeXPathNodeSet(Set xpathNodeSet, String inclusiveNamespaces)
           throws CanonicalizationException {

      /** $todo$ well, should we throw UnsupportedOperationException ? */
      throw new CanonicalizationException(
         "c14n.Canonicalizer.UnsupportedOperation");
   }

   /**
    * Always throws a CanonicalizationException because this is inclusive c14n.
    *
    * @param rootNode
    * @param inclusiveNamespaces
    * @throws CanonicalizationException
    */
   public byte[] engineCanonicalizeSubTree(Node rootNode, String inclusiveNamespaces)
           throws CanonicalizationException {

      /** $todo$ well, should we throw UnsupportedOperationException ? */
      throw new CanonicalizationException(
         "c14n.Canonicalizer.UnsupportedOperation");
   }
}

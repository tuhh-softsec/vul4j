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
import java.util.Vector;
import org.w3c.dom.*;
import org.w3c.dom.traversal.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import org.apache.log4j.*;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.I18n;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.CanonicalizerSpi;
import org.apache.xml.security.c14n.helper.C14nNodeFilter;
import org.apache.xml.security.c14n.helper.C14nHelper;


/**
 * This class implements the <A
 * HREF="http://www.w3.org/TR/2001/REC-xml-c14n-20010315">Canonical XML
 * Version 1.0</A> specification.
 *
 * @author Christian Geuer-Pollmann
 * @since  REC-xml-c14n-20010315
 */
public abstract class Canonicalizer20010315WithoutXPathSupport
        extends CanonicalizerSpi {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category
         .getInstance(Canonicalizer20010315WithoutXPathSupport.class.getName());

   /**
    * indicates the current state of the document traversal
    *
    * @see CanonicalizerSpi#BEFORE_DOCUMENT_ELEM
    * @see CanonicalizerSpi#INSIDE_DOCUMENT_ELEM
    * @see CanonicalizerSpi#AFTER_DOCUMENT_ELEM
    */
   private short processingPos = CanonicalizerSpi.BEFORE_DOCUMENT_ELEM;

   /** Field _xpathChecked */
   private boolean _xpathChecked = false;

   /**
    * This method always returns true because this implementation is not XPath
    * based and all Nodes are visible
    *
    * @todo If we only canonicalize a sub Tree, we eventually have to change this
    * @param node
    * @return
    */
   public boolean engineVisible(Node node) {
      throw new UnsupportedOperationException(I18n
         .translate("c14n.Canonicalizer.UnsupportedOperation"));
   }

   /**
    * This method is only needed for XPath based implememtations
    *
    * @todo shall we throw Exceptions ?
    * @param node
    */
   public void engineMakeVisible(Node node) {
      throw new UnsupportedOperationException(I18n
         .translate("c14n.Canonicalizer.UnsupportedOperation"));
   }

   /**
    * This method is only needed for XPath based implememtations
    *
    * @todo shall we throw Exceptions ?
    * @param node
    */
   public void engineMakeInVisible(Node node) {
      throw new UnsupportedOperationException(I18n
         .translate("c14n.Canonicalizer.UnsupportedOperation"));
   }

   /**
    * This method is only needed for XPath based implememtations
    *
    * @todo shall we throw Exceptions ?
    * @param nodeList
    */
   public void engineSetXPathNodeSet(NodeList nodeList) {
      throw new UnsupportedOperationException(I18n
         .translate("c14n.Canonicalizer.UnsupportedOperation"));
   }

   /**
    * Constructor Canonicalizer20010315
    *
    * @param includeComments
    */
   public Canonicalizer20010315WithoutXPathSupport(boolean includeComments) {

      this.engineSetIncludeComments(includeComments);

      if (includeComments) {
         this.engineSetURI(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
         this.engineSetXPath(Canonicalizer.XPATH_C14N_WITH_COMMENTS);
      } else {
         this.engineSetURI(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS);
         this.engineSetXPath(Canonicalizer.XPATH_C14N_OMIT_COMMENTS);
      }
   }

   /**
    * Method engineCanonicalize
    *
    * @param selectedNodes
    * @return
    * @throws CanonicalizationException
    */
   public byte[] engineCanonicalize(NodeList selectedNodes)
           throws CanonicalizationException {
      throw new CanonicalizationException(
         "c14n.Canonicalizer.UnsupportedOperation");
   }

   /**
    * This method is the work horse of the canonicalizer which canonicalizes
    * a Node and returns a byte[] array.
    *
    * @param node
    * @return
    * @throws CanonicalizationException
    */
   public byte[] engineCanonicalize(Node node)
           throws CanonicalizationException {

      if (node == null) {
         Object exArgs[] = { "null", "null" };

         throw new CanonicalizationException("c14n.Canonicalizer.IllegalNode",
                                             exArgs);
      }

      /*
       * check whether the correct XPath is set. This version can only handle
       * the core types from the spec.
       */
      if (!_xpathChecked) {
         if (this.engineGetIncludeComments()) {

            /**
             * @todo these xpath Strings are not compared to be semantically equal,
             *  only char-by-char. Can we compare compiled XPathes?
             */
            if (!this.engineGetXPathString()
                    .equals(Canonicalizer.XPATH_C14N_WITH_COMMENTS)) {
               cat.fatal("initialized with wrong xpath \"" + engineGetXPath()
                         + "\"");

               throw new RuntimeException("initialized with wrong xpath "
                                          + engineGetXPath());
            }
         } else {
            if (!this.engineGetXPathString()
                    .equals(Canonicalizer.XPATH_C14N_OMIT_COMMENTS)) {
               cat.fatal("initialized with wrong xpath \"" + engineGetXPath()
                         + "\"");

               throw new RuntimeException("initialized with wrong xpath "
                                          + engineGetXPath());
            }
         }

         this._xpathChecked = true;
      }

      NodeFilter nodefilter =
         new C14nNodeFilter(this.engineGetIncludeComments());
      Document document;

      if (node.getNodeType() == Node.DOCUMENT_NODE) {
         document = (Document) node;
      } else {
         document = node.getOwnerDocument();
      }

      boolean entityReferenceExpansion = true;
      TreeWalker treewalker =
         ((DocumentTraversal) document).createTreeWalker(node,
            NodeFilter.SHOW_ALL, nodefilter, entityReferenceExpansion);
      ByteArrayOutputStream bytearrayoutputstream;
      PrintWriter printwriter;

      try {
         bytearrayoutputstream = new ByteArrayOutputStream();
         printwriter =
            new PrintWriter(new OutputStreamWriter(bytearrayoutputstream,
                                                   Canonicalizer.ENCODING));
      } catch (UnsupportedEncodingException ex) {
         Object exArgs[] = { Canonicalizer.ENCODING };

         throw new CanonicalizationException(
            "c14n.Canonicalizer.UnsupportedEncoding", exArgs, ex);

         // return new byte[0];
      }

      process(treewalker, printwriter);
      printwriter.flush();

      return bytearrayoutputstream.toByteArray();
   }

   /**
    * Method process
    *
    * @param treewalker
    * @param printwriter
    * @throws CanonicalizationException
    */
   private void process(TreeWalker treewalker, PrintWriter printwriter)
           throws CanonicalizationException {

      Node currentNode = treewalker.getCurrentNode();

      switch (currentNode.getNodeType()) {

      case Node.ENTITY_REFERENCE_NODE :
         for (Node node1 = treewalker.firstChild(); node1 != null;
                 node1 = treewalker.nextSibling()) {
            process(treewalker, printwriter);
         }

         treewalker.setCurrentNode(currentNode);
         break;

      case Node.ENTITY_NODE :
         cat.warn("Node.ENTITY_NODE called");
         break;

      case Node.ATTRIBUTE_NODE :
         Object[] exArgs = {
            XMLUtils.getNodeTypeString(currentNode.getNodeType()),
            currentNode.getNodeName() };

         throw new CanonicalizationException(
            "c14n.Canonicalizer20010315.IllegalNode", exArgs);
      case Node.TEXT_NODE :
      case Node.CDATA_SECTION_NODE :
         printwriter
            .print(C14nHelper.normalizeText(currentNode.getNodeValue()));
         break;

      case Node.COMMENT_NODE :

         /*
          * Comment Nodes- Nothing if generating canonical XML without comments.
          * For canonical XML with comments, generate the opening comment
          * symbol (<!--), the string value of the node, and the closing
          * comment symbol (-->). Also, a trailing #xA is rendered after the
          * closing comment symbol for comment children of the root node with
          * a lesser document order than the document element, and a leading
          * #xA is rendered before the opening comment symbol of comment
          * children of the root node with a greater document order than the
          * document element. (Comment children of the root node represent
          * comments outside of the top-level document element and outside
          * of the document type declaration).
          *
          */
         if (this.engineGetIncludeComments()) {
            if (processingPos == CanonicalizerSpi.AFTER_DOCUMENT_ELEM) {
               printwriter.print("\n");
            }

            printwriter.print("<!--");

            /** @todo Do we need really have to normalize Comments */
            printwriter
               .print(C14nHelper.normalizeComment(currentNode.getNodeValue()));
            printwriter.print("-->");

            if (processingPos == CanonicalizerSpi.BEFORE_DOCUMENT_ELEM) {
               printwriter.print("\n");
            }
         }
         break;

      case Node.PROCESSING_INSTRUCTION_NODE :

         /*
          * Processing Instruction (PI) Nodes- The opening PI symbol (<?),
          * the PI target name of the node, a leading space and the string
          * value if it is not empty, and the closing PI symbol (?>). If the
          * string value is empty, then the leading space is not added.
          * Also, a trailing #xA is rendered after the closing PI symbol for PI
          * children of the root node with a lesser document order than the
          * document element, and a leading #xA is rendered before the opening
          * PI symbol of PI children of the root node with a greater document
          * order than the document element.
          *
          */
         if (processingPos == CanonicalizerSpi.AFTER_DOCUMENT_ELEM) {
            printwriter.print("\n");
         }

         printwriter.print("<?");
         printwriter.print(currentNode.getNodeName());

         String s = currentNode.getNodeValue();

         if ((s != null) && (s.length() > 0)) {
            printwriter.print(" ");

            /** @todo Do we need really have to normalize PIs */
            printwriter.print(C14nHelper.normalizeProcessingInstruction(s));
         }

         printwriter.print("?>");

         if (processingPos == CanonicalizerSpi.BEFORE_DOCUMENT_ELEM) {
            printwriter.print("\n");
         }
         break;

      case Node.ELEMENT_NODE :
         // if (currentNode == currentNode.getOwnerDocument().getDocumentElement()) {
         processingPos = CanonicalizerSpi.INSIDE_DOCUMENT_ELEM;
         // }
         Canonicalizer20010315.checkForRelativeNamespace(currentNode);

         printwriter.print('<');
         printwriter.print(currentNode.getNodeName());
         removeExtraNamespaces(currentNode);

         NamedNodeMap namednodemap = currentNode.getAttributes();
         Attr aattr[] = C14nHelper.sortAttributes(namednodemap);

         for (int i = 0; i < aattr.length; i++) {
            Attr attr = aattr[i];

            printwriter.print(' ');
            printwriter.print(attr.getNodeName());
            printwriter.print("=\"");
            printwriter.print(C14nHelper.normalizeAttr(attr.getNodeValue()));
            printwriter.print('"');
         }

         printwriter.print(">");

         for (Node node1 = treewalker.firstChild(); node1 != null;
                 node1 = treewalker.nextSibling()) {
            process(treewalker, printwriter);
         }

         treewalker.setCurrentNode(currentNode);
         printwriter.print("</");
         printwriter.print(currentNode.getNodeName());
         printwriter.print('>');

         if (currentNode
                 == currentNode.getOwnerDocument().getDocumentElement()) {
            processingPos = CanonicalizerSpi.AFTER_DOCUMENT_ELEM;
         }
         break;

      case Node.DOCUMENT_NODE :

         /**
          * Root Node- The root node is the parent of the top-level document
          * element. The result of processing each of its child nodes that
          * is in the node-set in document order. The root node does not
          * generate a byte order mark, XML declaration, nor anything from
          * within the document type declaration.
          *
          */
         for (Node node1 = treewalker.firstChild(); node1 != null;
                 node1 = treewalker.nextSibling()) {
            process(treewalker, printwriter);
         }

         treewalker.setCurrentNode(currentNode);
         break;

      default :
         for (Node node1 = treewalker.firstChild(); node1 != null;
                 node1 = treewalker.nextSibling()) {
            process(treewalker, printwriter);
         }

         treewalker.setCurrentNode(currentNode);
         break;
      }
   }

   /**
    * Namespace Nodes: A namespace node N is ignored if the nearest
    * ancestor element of the node's parent element that is in the
    * node-set has a namespace node in the node-set with the same local
    * name and value as N. Otherwise, process the namespace node N in
    * the same way as an attribute node, except assign the local name
    * xmlns to the default namespace node if it exists (in XPath, the
    * default namespace node has an empty URI and local name).
    *
    * @param node element to be cleaned up
    */
   private void removeExtraNamespaces(Node node) {

      if (node.getNodeType() != Node.ELEMENT_NODE) {
         cat.fatal("removeExtraNamespaces with "
                   + XMLUtils.getNodeTypeString(node.getNodeType())
                   + " called. Has to get an ELEMENT");
      }

      Vector ancestorVector = XMLUtils.getAncestorElements(node);
      Vector redundantAttrsVector = new Vector();
      NamedNodeMap nodeAttributes = node.getAttributes();

      // loop through all Attributes
      nextNodeAttribute: for (int i = 0; i < nodeAttributes.getLength(); i++) {
         Attr nodeAttr = (Attr) nodeAttributes.item(i);
         String nodeAttrName = nodeAttr.getNodeName();
         String nodeAttrValue = nodeAttr.getValue();

         // handle only namespace declarations
         boolean definesDefaultNS = nodeAttrName.equals("xmlns");
         boolean definesArbitraryNS = nodeAttrName.startsWith("xmlns:");

         if (definesDefaultNS) {
            boolean found = false;

            /**
             * loop through all ancestors (parent elements) and
             * check for redundancy in ancestors
             */
            nextAncestor: for (int j = 0; j < ancestorVector.size(); j++) {
               Node ancestorNode = (Node) ancestorVector.get(j);
               NamedNodeMap ancestorAttributes = ancestorNode.getAttributes();

               /**
                * if this element has no attributes (and no namespace defs),
                * we don't have to check it
                */
               if (ancestorAttributes == null) {
                  continue nextAncestor;
               }

               // loop through the attributes of the current ancestor
               nextAncestorAttribute: for (int k = 0;
                                           k < ancestorAttributes.getLength();
                                           k++) {
                  Attr ancestorAttr = (Attr) ancestorAttributes.item(k);

                  // processNextAncestorAttribute if ancestorAttr does not define the defaultNS
                  if (!ancestorAttr.getNodeName().equals("xmlns")) {
                     continue nextAncestorAttribute;
                  }

                  String ancestorAttrValue = ancestorAttr.getValue();

                  // same namespace URI? then add to redundant attrs
                  if (nodeAttrValue.equals(ancestorAttrValue)) {

                     // cat.debug("<" + ((Element)node).getNodeName() + " " + nodeAttrName + "=\"" + nodeAttrValue + "\"> will be deleted in default NS handler");
                     redundantAttrsVector.add(nodeAttr);

                     found = true;
                  }

                  // handle next node attribute
                  continue nextNodeAttribute;
               }    // processNextAncestorAttribute
            }    // nextAncestor

            /*
             * Did we run up to the document element but did not find an
             * empty default namespace definition ?
             */
            if (nodeAttrValue.equals("") &&!found) {

               // cat.debug("<" + ((Element)node).getNodeName() + " " + nodeAttrName + "=\"" + nodeAttrValue + "\"> will be deleted in notFound section");
               redundantAttrsVector.add(nodeAttr);
            }
         } else if (definesArbitraryNS) {

            /**
             * loop through all ancestors (parent elements) and
             * check for redundancy in ancestors
             */
            nextAncestor: for (int j = 0; j < ancestorVector.size(); j++) {
               Node ancestorNode = (Node) ancestorVector.get(j);
               NamedNodeMap ancestorAttributes = ancestorNode.getAttributes();

               /**
                * if this element has no attributes (and no namespace defs),
                * we don't have to check it
                */
               if (ancestorAttributes == null) {
                  continue nextAncestor;
               }

               // loop through the attributes of the current ancestor
               nextAncestorAttribute: for (int k = 0;
                                           k < ancestorAttributes.getLength();
                                           k++) {
                  Attr ancestorAttr = (Attr) ancestorAttributes.item(k);

                  // wrong attribute name? then check the next ancestor attr
                  if (!nodeAttrName.equals(ancestorAttr.getNodeName())) {
                     continue nextAncestorAttribute;
                  }

                  String ancestorAttrValue = ancestorAttr.getValue();

                  /**
                   * same attribute value? then add to redundant
                   * attrs and stop searching ancestors
                   */
                  if (nodeAttrValue.equals(ancestorAttrValue)) {
                     redundantAttrsVector.add(nodeAttr);
                  }

                  break nextAncestor;
               }    // processNextAncestorAttribute
            }    // nextAncestor
         }    // isNamespace
      }    // nodeAttribute

      /**
       * after collecting deletable namespace definitions, erase them
       */
      for (int i = 0; i < redundantAttrsVector.size(); i++) {
         Attr attrToDelete = (Attr) redundantAttrsVector.get(i);

         nodeAttributes.removeNamedItem(attrToDelete.getNodeName());
      }
   }

   /**
    * Method engineSetRemoveNSAttrs
    *
    * @param remove
    */
   public void engineSetRemoveNSAttrs(boolean remove) {
      throw new UnsupportedOperationException(I18n
         .translate("c14n.Canonicalizer.UnsupportedOperation"));
   }

   /**
    * Method engineGetRemoveNSAttrs
    *
    * @return
    */
   public boolean engineGetRemoveNSAttrs() {
      return false;
   }

   static {
      org.apache.xml.security.Init.init();
   }
}

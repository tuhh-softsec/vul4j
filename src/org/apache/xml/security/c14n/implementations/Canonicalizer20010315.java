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
import java.util.Enumeration;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.HashSet;
import java.util.Iterator;
import javax.xml.parsers.*;
import javax.xml.transform.TransformerException;
import org.apache.xml.utils.PrefixResolverDefault;
import org.apache.xpath.NodeSet;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathAPI;
import org.apache.xpath.compiler.XPathParser;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.*;
import org.w3c.dom.traversal.*;
import org.xml.sax.*;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.CanonicalizerSpi;
import org.apache.xml.security.c14n.helper.AttrCompare;
import org.apache.xml.security.c14n.helper.NamespaceSearcher;
import org.apache.xml.security.c14n.helper.C14nHelper;
import org.apache.xml.security.c14n.helper.C14nNodeFilter;
import org.apache.xml.security.utils.HelperNodeList;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;


/**
 * This class implements the <A
 * HREF="http://www.w3.org/TR/2001/REC-xml-c14n-20010315">Canonical XML
 * Version 1.0</A> specification.
 * <BR>
 * The calling hierarchie is relativly easy:
 *
 * <OL>
 * <LI><code>c14nFiles</code> is called with the filenames of the input and
 * output file, the includeComments boolean and the XPath string</LI>
 * <LI><code>c14nFiles</code> calls <code>canonicalize</code> with the root
 * node and the NodeList with the selected <code>Node</code>s</LI>
 * <LI><code>canonicalize</code> calls <code>process</code></LI>
 * <LI><code>process</code> recursively calls itself and uses the
 * <code>normalizeXXX</code> functions for normalizing the different
 * NodeTypes</LI>
 * </OL>
 *
 * @author Christian Geuer-Pollmann
 * @since  REC-xml-c14n-20010315 .
 */
public abstract class Canonicalizer20010315 extends CanonicalizerSpi {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category
         .getInstance(Canonicalizer20010315.class.getName());

   /** Field processingPos */
   private short processingPos = CanonicalizerSpi.BEFORE_DOCUMENT_ELEM;

   /** Field hmVisibleNodes */
   public Map hmVisibleNodes = null;

   /**
    * During c14n of a document with only a document subset visible,
    * Attributes for namespace declarations are created in 'visible' Elements.
    * This means that after c14n, the infoset of the document is modified because
    * this process added namespace attrs. If this is a problem, the added
    * attributes have to be removed from the DOM after c14n.
    */
   private Vector _attrsToBeRemovedAfterC14n = new Vector();

   /** Field _removeNSattrsAfterC14n */
   private boolean _removeNSattrsAfterC14n = true;

   /**
    * Method engineVisible
    *
    * @param node
    * @return
    */
   public boolean engineVisible(Node node) {

      if (this.hmVisibleNodes == null) {
         return false;
      }

      return this.hmVisibleNodes.containsKey(node);
   }

   /**
    * Method engineMakeVisible
    *
    * @param node
    */
   public void engineMakeVisible(Node node) {

      if (this.hmVisibleNodes == null) {
         this.hmVisibleNodes = (Map) new HashMap();
      }

      if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
         Attr a = (Attr) node;
         Element ownerElement = a.getOwnerElement();

         if (ownerElement == null) {
            cat.warn("makeVisible(" + a + ") in NULL !?! Element");
         } else {
            cat.debug("makeVisible(" + a + ") in Element "
                      + ownerElement.getTagName());
         }
      } else {
         cat.debug("makeVisible(" + ((node.getNamespaceURI() != null)
                                     ? "{" + node.getNamespaceURI() + "} "
                                     : "") + node.getNodeName() + ")");
      }

      this.hmVisibleNodes.put(node, Boolean.TRUE);
   }

   /**
    * Method engineMakeInVisible
    *
    * @param node
    */
   public void engineMakeInVisible(Node node) {

      if (this.hmVisibleNodes != null) {
         if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
            Attr a = (Attr) node;

            cat.debug("makeInVisible(" + a + ") in Element "
                      + a.getOwnerElement().getTagName());
         } else {
            cat.debug("makeInVisible(" + node + ")");
         }

         if (!engineVisible(node)) {
            cat.fatal("Try to hide " + node + " but already is not visible");
         }

         this.hmVisibleNodes.remove(node);
      }
   }

   /**
    * Method engineSetXPathNodeSet
    *
    * @param nodeList
    */
   public void engineSetXPathNodeSet(NodeList nodeList) {

      cat.debug("Canonicalizer20010315.engineSetXPathNodeSet("
                + nodeList.getLength() + " nodes)");

      this.hmVisibleNodes = (Map) new HashMap();

      for (int i = 0; i < nodeList.getLength(); i++) {
         engineMakeVisible(nodeList.item(i));
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

      this.engineSetXPathNodeSet(selectedNodes);

      if (selectedNodes.getLength() == 0) {
         return new byte[0];
      }

      Document document = XMLUtils.getOwnerDocument(selectedNodes.item(0));

      return this.engineDoCanonicalization(document);
   }

   /**
    * Method engineCanonicalize
    *
    * @param node
    * @return
    * @throws CanonicalizationException
    */
   public byte[] engineCanonicalize(Node node)
           throws CanonicalizationException {

      /**
       * If the node set has not been set by anyone else before, we apply
       * our own XPath to it.
       */
      if (this.hmVisibleNodes == null) {
         try {
            NodeList selected = null;

            if (this.engineGetXPath() instanceof Element) {
               selected = XPathAPI.selectNodeList(node,
                                                  this.engineGetXPathString(),
                                                  (Node) this.engineGetXPath());
            } else {
               selected = XPathAPI.selectNodeList(node,
                                                  this.engineGetXPathString());
            }

            cat.debug("xpath is" + this.engineGetXPathString());
            this.engineSetXPathNodeSet(selected);
         } catch (TransformerException e) {
            Object exArgs[] = { "TransformerException: " + e.getMessage() };

            throw new CanonicalizationException("generic.EmptyMessage", exArgs,
                                                e);
         }
      }

      return this.engineDoCanonicalization(node);
   }

   /**
    * Method engineDoCanonicalization
    *
    * @param node
    * @return
    * @throws CanonicalizationException
    */
   private byte[] engineDoCanonicalization(Node node)
           throws CanonicalizationException {

      Document document = XMLUtils.getOwnerDocument(node);

      this.checkTraversability(document);

      DocumentTraversal dt = ((DocumentTraversal) document);
      Node rootNode = (Node) node;
      NodeFilter nodefilter =
         new C14nNodeFilter(this.engineGetIncludeComments());
      TreeWalker treewalker = dt.createTreeWalker(rootNode,
                                                  NodeFilter.SHOW_ALL,
                                                  nodefilter, true);
      ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
      PrintWriter printwriter = null;

      try {
         printwriter =
            new PrintWriter(new OutputStreamWriter(bytearrayoutputstream,
                                                   Canonicalizer.ENCODING));
      } catch (UnsupportedEncodingException ex) {
         throw new CanonicalizationException("generic.EmptyMessage", ex);
      }

      process(treewalker, printwriter, this.engineGetIncludeComments());
      printwriter.flush();

      if (this.engineGetRemoveNSAttrs()) {
         this.removeNSAttrs();
      }

      return bytearrayoutputstream.toByteArray();
   }

   /**
    * Method process
    *
    * @param treewalker
    * @param printwriter
    * @param includeComments
    * @throws CanonicalizationException
    */
   private void process(
           TreeWalker treewalker, PrintWriter printwriter, boolean includeComments)
              throws CanonicalizationException {

      Node currentNode = treewalker.getCurrentNode();

      switch (currentNode.getNodeType()) {

      case Node.ENTITY_REFERENCE_NODE :
         for (Node node1 = treewalker.firstChild(); node1 != null;
                 node1 = treewalker.nextSibling()) {
            process(treewalker, printwriter, includeComments);
         }

         treewalker.setCurrentNode(currentNode);
         break;

      case Node.ENTITY_NODE :
         cat.warn("Node.ENTITY_NODE called");
         break;

      case Node.ATTRIBUTE_NODE : {
         Object[] exArgs = {
            XMLUtils.getNodeTypeString(currentNode.getNodeType()),
            currentNode.getNodeName() };

         throw new CanonicalizationException(
            "c14n.Canonicalizer20010315.IllegalNode", exArgs);
      }
      case Node.TEXT_NODE :
      case Node.CDATA_SECTION_NODE :
         if (engineVisible(currentNode)) {
            printwriter
               .print(C14nHelper.normalizeText(currentNode.getNodeValue()));
         } else {
            cat.error(currentNode + " not visible");
         }
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
          */
         if ( /* includeComments && */engineVisible(currentNode)) {
            if (processingPos == CanonicalizerSpi.AFTER_DOCUMENT_ELEM) {
               printwriter.print("\n");
            }

            /** do we need have to normalize Comments ? */
            printwriter.print("<!--");
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
         if (engineVisible(currentNode)) {
            if (processingPos == CanonicalizerSpi.AFTER_DOCUMENT_ELEM) {
               printwriter.print("\n");
            }

            printwriter.print("<?");
            printwriter.print(currentNode.getNodeName());

            String s = currentNode.getNodeValue();

            if ((s != null) && (s.length() > 0)) {
               printwriter.print(" ");

               /** @todo do we need PI normalization ? */
               printwriter.print(C14nHelper.normalizeProcessingInstruction(s));
            }

            printwriter.print("?>");

            if (processingPos == CanonicalizerSpi.BEFORE_DOCUMENT_ELEM) {
               printwriter.print("\n");
            }
         }
         break;

      case Node.ELEMENT_NODE :

         // If we enter an Element, we are inside the Document Element
         processingPos = CanonicalizerSpi.INSIDE_DOCUMENT_ELEM;

         /* We check relative Namespaces in _all_ nodes;
          *
          * Implementations MUST report an operation failure on documents containing
          * relative namespace URIs. (This reads to me that the _complete_ document
          * must not contain relative namespaces, even if we only canonicalize a
          * subtree that does not contain relative namespace URIs).
          */
         checkForRelativeNamespace(currentNode);

         if (engineVisible(currentNode)) {
            cat.debug(currentNode.getNodeName() + " included");
            printwriter.print('<');
            printwriter.print(currentNode.getNodeName());

            {    // make all namespaces visible !!!
               NamedNodeMap namednodemap = currentNode.getAttributes();
               Attr aattr[] = C14nHelper.sortAttributes(namednodemap);

               for (int i = 0; i < aattr.length; i++) {
                  Attr attr = aattr[i];

                  if (attr.getNodeName().startsWith("xmlns:")
                          || attr.getNodeName().equals("xmlns")) {
                     this.engineMakeVisible(attr);
                  }
               }
            }

            processXmlAttributes(currentNode);
            processNamespaces(currentNode);

            NamedNodeMap namednodemap = currentNode.getAttributes();
            Attr aattr[] = C14nHelper.sortAttributes(namednodemap);

            processingAttrs: for (int i = 0; i < aattr.length; i++) {
               Attr attr = aattr[i];

               // To finish processing L, simply process every namespace node
               // in L, except omit namespace node with local name xml, which
               // defines the xml prefix, if its string value is
               // "http://www.w3.org/XML/1998/namespace".
               if (attr.getNodeName().equals("xmlns:xml") &&

               // attr.getLocalName().equals("xml") &&
               attr.getNodeValue()
                       .equals("http://www.w3.org/XML/1998/namespace")) {
                  continue processingAttrs;
               }

               if (engineVisible((Node) attr)) {
                  printwriter.print(' ');
                  printwriter.print(attr.getNodeName());
                  printwriter.print("=\"");
                  printwriter
                     .print(C14nHelper.normalizeAttr(attr.getNodeValue()));
                  printwriter.print('"');
               } else {
                  cat.debug("Suppressed Attr: " + attr + " from Element "
                            + attr.getOwnerElement().getTagName());
               }
            }

            printwriter.print(">");
         } else {
            cat.debug(currentNode.getNodeName() + " excluded !!!");
         }

         for (Node node1 = treewalker.firstChild(); node1 != null;
                 node1 = treewalker.nextSibling()) {
            process(treewalker, printwriter, includeComments);
         }

         treewalker.setCurrentNode(currentNode);

         if (engineVisible(currentNode)) {
            printwriter.print("</");
            printwriter.print(currentNode.getNodeName());
            printwriter.print('>');
         }

         // If we leave the Document Element, we are outside the Document Element
         if (currentNode
                 == currentNode.getOwnerDocument().getDocumentElement()) {
            processingPos = CanonicalizerSpi.AFTER_DOCUMENT_ELEM;
         }
         break;

      case Node.DOCUMENT_NODE :

         /*
          * Root Node- The root node is the parent of the top-level document
          * element. The result of processing each of its child nodes that
          * is in the node-set in document order. The root node does not
          * generate a byte order mark, XML declaration, nor anything from
          * within the document type declaration.
          *
          */
         for (Node node1 = treewalker.firstChild(); node1 != null;
                 node1 = treewalker.nextSibling()) {
            process(treewalker, printwriter, includeComments);
         }

         treewalker.setCurrentNode(currentNode);
         break;

      default :
         for (Node node1 = treewalker.firstChild(); node1 != null;
                 node1 = treewalker.nextSibling()) {
            process(treewalker, printwriter, includeComments);
         }

         treewalker.setCurrentNode(currentNode);
         break;
      }
   }

   /**
    * Method collectUsedXmlAttributes
    *
    * @param ctxNode
    * @return
    */
   private static HashSet collectUsedXmlAttributes(Node ctxNode) {

      HashSet attrs = new HashSet();

      if ((ctxNode != null) && (ctxNode.getNodeType() == Node.ELEMENT_NODE)) {
         Node parent = ctxNode;

         searchParents: while ((parent = parent.getParentNode()) != null
                               && (parent.getNodeType() == Node.ELEMENT_NODE)) {
            NamedNodeMap attributes = parent.getAttributes();

            for (int i = 0; i < attributes.getLength(); i++) {
               Attr attr = (Attr) attributes.item(i);

               if (attr.getName().startsWith("xml:")) {
                  attrs.add(new String(attr.getName()));
               }
            }
         }
      }

      return attrs;
   }

   /**
    * Method processXmlAttributes
    *
    * @param ctxNode
    */
   private void processXmlAttributes(Node ctxNode) {

      if (!engineVisible(ctxNode)) {
         return;
      }

      HashSet usedXMLAttributes = collectUsedXmlAttributes(ctxNode);

      // Attr attributes[] = new Attr[usedXMLAttributes.size()];
      // cat.debug("attributes[" + usedXMLAttributes.size() + "]");
      Iterator iterator = usedXMLAttributes.iterator();

      // int i = 0;
      while (iterator.hasNext()) {
         String currentXMLAttribute = (String) iterator.next();
         String newAttrValue = null;
         boolean deleteOriginalAttribute = false;

         {
            Object result[] = processXmlAttributesAlgo(ctxNode,
                                                       currentXMLAttribute);

            newAttrValue = (String) result[0];
            deleteOriginalAttribute = ((Boolean) result[1]).booleanValue();
         }

         if (newAttrValue == null) {
            if (deleteOriginalAttribute) {
               engineMakeInVisible(((Element) ctxNode)
                  .getAttributeNode(currentXMLAttribute));
               ((Element) ctxNode).getAttributes()
                  .removeNamedItem(currentXMLAttribute);
            }
         } else {
            ((Element) ctxNode).setAttribute(currentXMLAttribute, newAttrValue);
            engineMakeVisible(((Element) ctxNode)
               .getAttributeNode(currentXMLAttribute));
         }
      }
   }

   /**
    * Namespace Nodes- A namespace node N is ignored if the nearest
    * ancestor element of the node's parent element that is in the
    * node-set has a namespace node in the node-set with the same local
    * name and value as N. Otherwise, process the namespace node N in
    * the same way as an attribute node, except assign the local name
    * xmlns to the default namespace node if it exists (in XPath, the
    * default namespace node has an empty URI and local name).
    *
    * <PRE LANG="DE">
    * case 1 - definiert selbst einen nicht-leeren default NS - muss evtl. gelöscht werden
    * case 2 - definiert selbst einen       leeren default NS - muss evtl. gelöscht werden
    * case 3 - definiert selbst einen nicht-leeren         NS - muss evtl. gelöscht werden
    * case 4 - definiert selbst einen       leeren         NS - muss evtl. gelöscht werden
    * case 5 - in einem invisible wird ein nicht-leerer default NS definiert - muss evtl. hinzugefügt werden
    * case 6 - in einem invisible wird ein       leerer default NS definiert - muss evtl. hinzugefügt werden
    * case 7 - in einem invisible wird ein nicht-leerer         NS definiert - muss evtl. hinzugefügt werden
    * case 8 - in einem invisible wird ein       leerer         NS definiert - muss evtl. hinzugefügt werden
    * </PRE>
    *
    * <PRE LANG="EN">
    * case 1 - defines itself a non-empty default NS - must eventually be deleted
    * case 2 - defines itself an    empty default NS - must eventually be deleted
    * case 3 - defines itself a non-empty         NS - must eventually be deleted
    * case 4 - defines itself an    empty         NS - must eventually be deleted
    * case 5 - an invivible elem defines a non-empty default NS - must eventually be added
    * case 6 - an invivible elem defines an    empty default NS - must eventually be added
    * case 7 - an invivible elem defines a non-empty         NS - must eventually be added
    * case 8 - an invivible elem defines an    empty         NS - must eventually be added
    * </PRE>
    *
    * @param ctxNode
    * @throws CanonicalizationException
    */
   private void processNamespaces(Node ctxNode)
           throws CanonicalizationException {

      if (!engineVisible(ctxNode)) {
         return;
      }

      if (ctxNode.getNodeType() != Node.ELEMENT_NODE) {
         cat.fatal("removeExtraNamespaces with "
                   + XMLUtils.getNodeTypeString(ctxNode.getNodeType())
                   + " called. Has to get an ELEMENT");

         return;
      }

      NamedNodeMap ctxAttributes = ctxNode.getAttributes();
      NamespaceSearcher nss = new NamespaceSearcher(ctxNode,
                                 this.hmVisibleNodes);

      /*
       * case 1 - definiert selbst einen nicht-leeren default NS - muss evtl. gelöscht werden
       * case 2 - definiert selbst einen       leeren default NS - muss evtl. gelöscht werden
       * case 3 - definiert selbst einen nicht-leeren         NS - muss evtl. gelöscht werden
       * case 4 - definiert selbst einen       leeren         NS - muss evtl. gelöscht werden
       * case 5 - in einem invisible wird ein nicht-leerer default NS definiert - muss evtl. hinzugefügt werden
       * case 6 - in einem invisible wird ein       leerer default NS definiert - muss evtl. hinzugefügt werden
       * case 7 - in einem invisible wird ein nicht-leerer         NS definiert - muss evtl. hinzugefügt werden
       * case 8 - in einem invisible wird ein       leerer         NS definiert - muss evtl. hinzugefügt werden
       */

      // loop through all Attributes and check whether we have to delete some
      nextNodeAttribute: for (int i = 0; i < ctxAttributes.getLength(); i++) {
         Attr nodeAttr = (Attr) ctxAttributes.item(i);
         String nodeAttrName = nodeAttr.getNodeName();
         String nodeAttrValue = nodeAttr.getValue();
         boolean definesDefaultNS = nodeAttrName.equals("xmlns");
         boolean definesArbitraryNS = nodeAttrName.startsWith("xmlns:");

         if (definesDefaultNS) {
            boolean attrValueEmpty = (nodeAttrValue.length() == 0);

            if (!attrValueEmpty) {
               cat.debug("case 1");

               /* case 1
                * definiert selbst einen nicht-leeren default NS - muss evtl. gelöscht werden
                * Suche in allen sichtbaren ancestors nach default NS.
                * Löschung kommt nur in Frage, falls überhaupt visible ancestors existieren
                * Falls der erste Treffer identisch ist, muss der default NS gelöscht werden
                * Falls der erste Treffer ungleich ist, Suche abbrechen.
                * Falls kein Treffer gefunden wird, tue nichts
                */
               Attr a = nss.findFirstVisibleDefaultNSAttr();

               if (a != null) {
                  if (nodeAttrValue.equals(a.getValue())) {
                     cat.debug("Here I call it");
                     engineMakeInVisible(nodeAttr);
                  } else {
                     continue nextNodeAttribute;
                  }
               } else {
                  cat.debug("Didn't find a visibleNS for " + nodeAttr);
               }
            } else {
               cat.debug("case 2");

               /* case 2
                * definiert selbst einen leeren default NS - muss evtl. gelöscht werden
                * Suche in allen sichtbaren ancestors nach default NS.
                * Falls der erste Treffer nicht-leer ist, Suche abbrechen.
                * Falls der erste Treffer leer ist, muss der default NS gelöscht werden
                * Falls kein Treffer gefunden wird, muss der default NS gelöscht werden
                */
               Attr a = nss.findFirstVisibleDefaultNSAttr();

               if (a != null) {
                  if (a.getValue().length() != 0) {
                     continue nextNodeAttribute;
                  } else {
                     engineMakeInVisible(nodeAttr);
                  }
               } else {
                  engineMakeInVisible(nodeAttr);
               }
            }
         } else if (definesArbitraryNS) {
            boolean attrValueEmpty = (nodeAttrValue.length() == 0);

            if (!attrValueEmpty) {
               cat.debug("case 3");

               /* case 3
                * definiert selbst einen NS - muss evtl. gelöscht werden
                * Suche in allen sichtbaren ancestors nach dem gleichen NS.
                * Falls der erste Treffer identisch ist, muss der NS gelöscht werden
                * Falls der erste Treffer ungleich ist, Suche abbrechen.
                * Falls kein Treffer gefunden wird, tue nichts
                */
               Map ns = nss.findVisibleNonDefaultNSAttrs();
               Attr a = (Attr) ns.get(nodeAttrName);

               if (a != null) {
                  if (nodeAttrValue.equals(a.getValue())) {
                     engineMakeInVisible(nodeAttr);
                  } else {
                     continue nextNodeAttribute;
                  }
               }
            } else {
               cat.debug("case 4");

               /* case 4
                * definiert selbst einen leeren NS - muss evtl. gelöscht werden
                */

               /** @todo check what we have to do here */
            }
         }
      }

      // loop through all ancestors and check whether we have to add some NS defs
      if (nss.invisibleAncestorsContainDefaultNS()) {
         Attr invisDefNS = nss.findFirstInvisibleDefaultNSAttr();

         cat.debug("nss.findFirstInvisibleDefaultNSAttr() == " + invisDefNS);

         if ((invisDefNS != null) && (invisDefNS.getValue().length() != 0)) {
            cat.debug("case 5");

            /* case 5
             * in einem invisible zwischen ihm und dem nächsten visible wird ein nicht-leerer default NS definiert - muss evtl. hinzugefügt werden
             * Suche alle visible ancestors über dem invisible definierenden nach default NS ab.
             * Falls der erste Treffer identisch ist, tue nichts
             * Falls der erste Treffer ungleich ist, füge die invisble default NS def. in den Knoten ein
             * Falls kein Treffer gefunden wird, füge die invisble default NS def. in den Knoten ein
             */
            Attr visDefNS = nss.findFirstVisibleDefaultNSAttr();

            if (visDefNS != null) {
               if (invisDefNS.getValue().equals(visDefNS.getValue())) {
                  ;
               } else {
                  Document doc = ctxNode.getOwnerDocument();
                  Attr newAttr = doc.createAttribute("xmlns");

                  newAttr.setValue(invisDefNS.getValue());
                  ((Element) ctxNode).setAttributeNode(newAttr);
                  engineMakeVisible(newAttr);
                  this._attrsToBeRemovedAfterC14n.add(newAttr);
               }
            } else {
               if (((Element) ctxNode).getAttributeNode("xmlns") == null) {
                  Document doc = ctxNode.getOwnerDocument();
                  Attr newAttr = doc.createAttribute("xmlns");

                  newAttr.setValue(invisDefNS.getValue());
                  ((Element) ctxNode).setAttributeNode(newAttr);
                  engineMakeVisible(newAttr);
                  this._attrsToBeRemovedAfterC14n.add(newAttr);
               }
            }
         } else {
            cat.debug("case 6");

            /* case 6
             * in einem invisible zwischen ihm und dem nächsten visible wird ein leerer default NS definiert - muss evtl. hinzugefügt werden
             * Suche alle visible ancestors über dem invisible definierenden nach default NS ab.
             * Falls keine visible ancestors existieren, tue nichts
             * Falls der erste Treffer auch leer ist, tue nichts
             * Falls der erste Treffer nicht leer ist, füge die invisble default NS def. in den Knoten ein
             * Falls kein Treffer gefunden wird, ftue nichts
             */
            Attr visDefNS = nss.findFirstVisibleDefaultNSAttr();

            if (visDefNS != null) {
               if (invisDefNS.getValue().equals(visDefNS.getValue())) {
                  ;
               } else {
                  Document doc = ctxNode.getOwnerDocument();
                  Attr newAttr = doc.createAttribute("xmlns");

                  newAttr.setValue(invisDefNS.getValue());
                  ((Element) ctxNode).setAttributeNode(newAttr);
                  engineMakeVisible(newAttr);
                  this._attrsToBeRemovedAfterC14n.add(newAttr);
               }
            } else {
               ;
            }
         }
      }

      if (nss.invisibleAncestorsContainNonDefaultNS()) {
         Map invisNS = nss.findInvisibleNonDefaultNSAttrs();
         Iterator invisIterator = invisNS.keySet().iterator();

         while (invisIterator.hasNext()) {
            String invisAttrName = (String) invisIterator.next();
            Attr invisAttr = (Attr) invisNS.get(invisAttrName);

            cat.debug("7 " + invisAttrName + "='" + invisAttr.getValue() + "'");

            if (invisAttr.getValue().length() != 0) {
               cat.debug("case 7");

               /* case 7
                * in einem invisible zwischen ihm und dem nächsten visible wird ein nicht-leerer NS definiert - muss evtl. hinzugefügt werden
                * Suche alle visible ancestors über dem invisible definierenden nach dem NS ab.
                * Falls der erste Treffer identisch ist, tue nichts
                * Falls der erste Treffer ungleich ist, füge die invisble NS def. in den Knoten ein
                * Falls kein Treffer gefunden wird, füge die invisble NS def. in den Knoten ein
                */
               Map visNS = nss.findVisibleNonDefaultNSAttrs();
               Attr visAttr = (Attr) visNS.get(invisAttrName);

               if (visAttr != null) {
                  if (invisAttr.getValue().equals(visAttr.getValue())) {
                     ;
                  } else {
                     Document doc = ctxNode.getOwnerDocument();
                     Attr newAttr = doc.createAttribute(invisAttrName);

                     newAttr.setValue(invisAttr.getValue());
                     ((Element) ctxNode).setAttributeNode(newAttr);
                     engineMakeVisible(newAttr);
                     this._attrsToBeRemovedAfterC14n.add(newAttr);
                  }
               } else {
                  Document doc = ctxNode.getOwnerDocument();
                  Attr newAttr = doc.createAttribute(invisAttrName);

                  newAttr.setValue(invisAttr.getValue());
                  ((Element) ctxNode).setAttributeNode(newAttr);
                  engineMakeVisible(newAttr);
                  this._attrsToBeRemovedAfterC14n.add(newAttr);
               }
            } else {
               cat.debug("case 8");

               /* case 8
                * in einem invisible zwischen ihm und dem nächsten visible wird ein leerer NS definiert - muss evtl. hinzugefügt werden
                */

               /** @todo check what we have to do here */
            }
         }
      }
   }

   /**
    * Method algo
    *
    * @param ctxNode
    * @param attributeName
    * @return
    */
   private Object[] processXmlAttributesAlgo(Node ctxNode,
                                             String attributeName) {

      // cat.debug(((Element) ctxNode).getTagName() + " (" + id(ctxNode) + ") hat \"" + ((Element) ctxNode).getAttribute(attributeName) + "\"");
      Object result[] = new Object[2];
      boolean deleteOriginalAttribute = false;
      String ctxAttrValue = null;

      if (((Element) ctxNode).getAttribute(attributeName) == null
              || ((Element) ctxNode).getAttribute(attributeName).length()
                 == 0) {
         Vector parents = XMLUtils.getAncestorElements(ctxNode);

         ctxAttrValue = null;

         // bottom up
         parent: for (int i = 0; i < parents.size(); i++) {
            Element currentParent = (Element) parents.elementAt(i);

            if (!engineVisible(currentParent) && (ctxAttrValue == null)) {
               ctxAttrValue = currentParent.getAttribute(attributeName);

               // cat.debug("set to " + ctxAttrValue);
            } else if (engineVisible(currentParent) && (ctxAttrValue != null)) {
               if (ctxAttrValue
                       .equals(currentParent.getAttribute(attributeName))) {
                  ctxAttrValue = null;

                  // cat.debug("set to " + ctxAttrValue);
               }

               break parent;
            }
         }
      } else {
         Vector parents = XMLUtils.getAncestorElements(ctxNode);

         ctxAttrValue = ((Element) ctxNode).getAttribute(attributeName);

         parent: for (int i = 0; i < parents.size(); i++) {
            Element currentParent = (Element) parents.elementAt(i);

            if (engineVisible(currentParent)
                    && (currentParent.getAttribute(attributeName) != null)
                    && (ctxAttrValue != null)) {
               if (ctxAttrValue
                       .equals(currentParent.getAttribute(attributeName))) {
                  ctxAttrValue = null;
                  deleteOriginalAttribute = true;

                  cat.debug("set to " + ctxAttrValue);
               }

               break parent;
            }
         }
      }

      result[0] = ctxAttrValue;
      result[1] = new Boolean(deleteOriginalAttribute);

      return result;
   }

   /**
    * This method uses the {@link Document#isSupported} method to check whether
    * the <CODE>Traversal</CODE> feature is available.
    *
    * @param document
    * @throws CanonicalizationException
    */
   private void checkTraversability(Document document)
           throws CanonicalizationException {

      if (!document.isSupported("Traversal", "2.0")) {
         cat.fatal("This DOM Document does not support Traversal");

         Object exArgs[] = {
            document.getImplementation().getClass().getName() };

         throw new CanonicalizationException(
            "c14n.Canonicalizer.TraversalNotSupported", exArgs);
      }
   }

   /**
    * Method checkForRelativeNamespace
    *
    * @param ctxNode
    * @throws CanonicalizationException
    */
   private void checkForRelativeNamespace(Node ctxNode)
           throws CanonicalizationException {

      if ((ctxNode != null) && (ctxNode.getNodeType() == Node.ELEMENT_NODE)) {
         NamedNodeMap attributes = ctxNode.getAttributes();

         cat.debug("checkForRelativeNamespace(" + ctxNode + ")");

         for (int i = 0; i < attributes.getLength(); i++) {
            cat.debug("checkForRelativeNamespace " + (Attr) attributes.item(i));
            C14nHelper.assertNotRelativeNS((Attr) attributes.item(i));
         }
      } else {
         cat.error("Called checkForRelativeNamespace() on a " + ctxNode);
      }
   }

   /**
    * Method engineSetRemoveNSAttrs
    *
    * @param remove
    */
   public void engineSetRemoveNSAttrs(boolean remove) {
      this._removeNSattrsAfterC14n = remove;
   }

   /**
    * Method engineGetRemoveNSAttrs
    *
    * @return
    */
   public boolean engineGetRemoveNSAttrs() {
      return this._removeNSattrsAfterC14n;
   }

   /**
    * Iterates over all Attributes which have been added during c14n and
    * removes them.
    */
   private void removeNSAttrs() {

      for (int i = 0; i < this._attrsToBeRemovedAfterC14n.size(); i++) {
         Attr currentNSdecl =
            (Attr) this._attrsToBeRemovedAfterC14n.elementAt(i);
         Element ownerElem = currentNSdecl.getOwnerElement();

         ownerElem.removeAttributeNode(currentNSdecl);
      }
   }

   static {
      org.apache.xml.security.Init.init();
   }
}

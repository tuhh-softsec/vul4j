
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
package org.apache.xml.security.c14n.helper;



import java.util.Map;
import java.util.Vector;
import org.w3c.dom.*;
import org.apache.xml.security.utils.HelperNodeList;
import org.apache.xml.security.utils.XMLUtils;
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
import org.apache.xpath.XPath;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.*;
import org.w3c.dom.traversal.*;
import org.xml.sax.*;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.utils.HelperNodeList;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;


/**
 * This is a utility class which is being used by
 * {@link org.apache.xml.security.c14n.implementations.Canonicalizer20010315WithXPath}
 * which offers different methods for searching for namespaces in a document.
 *
 * <UL>
 * <LI><B>v</B>: visible node</LI>
 * <LI><B>I</B>: invisible node</LI>
 * <LI><B>C</B>: context node</LI>
 * <LI><B>x</B>: result of selection</LI>
 * </UL>
 *
 * <PRE>
 * vvvvIIIIIvvvvvIIIIIIvvvvvvvIIIIIIIvvvvvvIIIIIC - input
 * xxxx     xxxxx      xxxxxxx       xxxxxx     C - visible selection
 *                                         xxxxxC - invisible selection
 * </PRE>
 *
 *
 * @author Christian Geuer-Pollmann
 */
public class NamespaceSearcher {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(NamespaceSearcher.class.getName());

   /** Field _ctxNode */
   Node _ctxNode = null;

   /** Field _currentNode */
   Node _currentNode = null;

   /** Field _hmSelectedNodes */
   Map _hmSelectedNodes = null;

   /** Field _searchVisible */
   boolean _searchVisible;

   /**
    * Constructor NamespaceSearcher
    *
    * @param ctxNode
    * @param hmSelectedNodes
    */
   public NamespaceSearcher(Node ctxNode, Map hmSelectedNodes) {
      this(ctxNode, hmSelectedNodes, true);
   }

   /**
    * Constructor NamespaceSearcher
    *
    * @param ctxNode
    * @param hmSelectedNodes
    * @param searchVisible
    */
   public NamespaceSearcher(Node ctxNode, Map hmSelectedNodes,
                            boolean searchVisible) {

      this._ctxNode = ctxNode;
      this._currentNode = ctxNode;
      this._hmSelectedNodes = hmSelectedNodes;
      this._searchVisible = searchVisible;
   }

   /**
    * Method visible
    *
    * @param node
    * @return
    */
   private boolean visible(Node node) {
      return this._hmSelectedNodes.containsKey(node);
   }

   /**
    * Method makeVisible
    *
    * @param node
    */
   private void makeVisible(Node node) {
      this._hmSelectedNodes.put(node, Boolean.TRUE);
   }

   /**
    * Method makeInVisible
    *
    * @param node
    */
   private void makeInVisible(Node node) {

      if (!visible(node)) {
         cat.fatal("Try to hide " + node + " but already is not visible");
      }

      this._hmSelectedNodes.remove(node);
   }

   public boolean visibleAncestorsExist() {
      // reset _currentNode;
      this._currentNode = this._ctxNode;
      this._searchVisible = true;

      Node n;

      while ((n = this.getNextNode()) != null) {
         cat.debug(n);
         return true;
      }
      return false;
   }

   /**
    * Method invisibleAncestorsContainDefaultNS
    *
    * @return
    * @throws CanonicalizationException
    */
   public boolean invisibleAncestorsContainDefaultNS()
           throws CanonicalizationException {

      boolean searchForDefault = true;
      boolean searchForNonDefault = false;

      return this.invisibleAncestorsContainAnyNS(searchForDefault,
                                                 searchForNonDefault);
   }

   /**
    * Method invisibleAncestorsContainNonDefaultNS
    *
    * @return
    * @throws CanonicalizationException
    */
   public boolean invisibleAncestorsContainNonDefaultNS()
           throws CanonicalizationException {

      boolean searchForDefault = false;
      boolean searchForNonDefault = true;

      return this.invisibleAncestorsContainAnyNS(searchForDefault,
                                                 searchForNonDefault);
   }

   /**
    * Method invisibleAncestorsContainNS
    *
    * @return
    * @throws CanonicalizationException
    */
   public boolean invisibleAncestorsContainNS()
           throws CanonicalizationException {

      boolean searchForDefault = true;
      boolean searchForNonDefault = true;

      return this.invisibleAncestorsContainAnyNS(searchForDefault,
                                                 searchForNonDefault);
   }

   /**
    * Method visibleAncestorsContainDefaultNS
    *
    * @return
    * @throws CanonicalizationException
    */
   public boolean visibleAncestorsContainDefaultNS()
           throws CanonicalizationException {

      boolean searchForDefault = true;
      boolean searchForNonDefault = false;

      return this.visibleAncestorsContainAnyNS(searchForDefault,
                                               searchForNonDefault);
   }

   /**
    * Method visibleAncestorsContainNonDefaultNS
    *
    * @return
    * @throws CanonicalizationException
    */
   public boolean visibleAncestorsContainNonDefaultNS()
           throws CanonicalizationException {

      boolean searchForDefault = false;
      boolean searchForNonDefault = true;

      return this.visibleAncestorsContainAnyNS(searchForDefault,
                                               searchForNonDefault);
   }

   /**
    * Method visibleAncestorsContainNS
    *
    * @return
    * @throws CanonicalizationException
    */
   public boolean visibleAncestorsContainNS() throws CanonicalizationException {

      boolean searchForDefault = true;
      boolean searchForNonDefault = true;

      return this.visibleAncestorsContainAnyNS(searchForDefault,
                                               searchForNonDefault);
   }

   /**
    * Searches all direct invisible ancestors of the context node and returns
    * true if it finds a namespace definition.
    * @param searchForDefault
    * @param searchForNonDefault
    * @return
    * @throws CanonicalizationException
    */
   private boolean invisibleAncestorsContainAnyNS(
           boolean searchForDefault, boolean searchForNonDefault)
              throws CanonicalizationException {

      Vector parents = XMLUtils.getAncestorElements(this._ctxNode);

      cat.debug("parents = " + parents.size());

      for (int i = 0; i < parents.size(); i++) {
         Node currentAncestor = (Node) parents.get(i);

         // we only search the ancestors until we hit a visible one
         if (visible(currentAncestor)) {
            return false;
         }

         NamedNodeMap attributes = ((Element) currentAncestor).getAttributes();

         for (int j = 0; j < attributes.getLength(); j++) {
            Attr currentAttr = (Attr) attributes.item(j);
            String nodeAttrName = currentAttr.getNodeName();

            // handle only namespace declarations
            boolean definesDefaultNS = nodeAttrName.equals("xmlns");
            boolean definesNonDefaultNS = nodeAttrName.startsWith("xmlns:");

            if ((definesDefaultNS && searchForDefault)
                    || (definesNonDefaultNS && searchForNonDefault)) {
               return true;
            }
         }
      }

      return false;
   }

   /**
    * Method visibleAncestorsContainAnyNS
    *
    * @param searchForDefault
    * @param searchForNonDefault
    * @return
    * @throws CanonicalizationException
    */
   private boolean visibleAncestorsContainAnyNS(
           boolean searchForDefault, boolean searchForNonDefault)
              throws CanonicalizationException {

      Vector parents = XMLUtils.getAncestorElements(this._ctxNode);

      for (int i = 0; i < parents.size(); i++) {
         Node currentAncestor = (Node) parents.get(i);

         // we only inspect visible ancestors
         if (visible(currentAncestor)) {
            NamedNodeMap attributes =
               ((Element) currentAncestor).getAttributes();

            for (int j = 0; j < attributes.getLength(); j++) {
               Attr currentAttr = (Attr) attributes.item(j);
               String nodeAttrName = currentAttr.getNodeName();

               // handle only namespace declarations
               boolean definesDefaultNS = nodeAttrName.equals("xmlns");
               boolean definesNonDefaultNS = nodeAttrName.startsWith("xmlns:");

               if ((definesDefaultNS && searchForDefault)
                       || (definesNonDefaultNS && searchForNonDefault)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   /**
    * Method nodeContainsNSdefs
    *
    * @param ctxNode
    * @return
    * @throws CanonicalizationException
    */
   public boolean nodeContainsNSdefs(Node ctxNode)
           throws CanonicalizationException {

      NamedNodeMap attributes = ((Element) ctxNode).getAttributes();

      for (int j = 0; j < attributes.getLength(); j++) {
         Attr currentAttr = (Attr) attributes.item(j);
         String nodeAttrName = currentAttr.getNodeName();

         // handle only namespace declarations
         boolean definesDefaultNS = nodeAttrName.equals("xmlns");
         boolean definesArbitraryNS = nodeAttrName.startsWith("xmlns:");

         if (definesDefaultNS || definesArbitraryNS) {
            return true;
         }
      }

      return false;
   }

   /**
    * Method currentNodeContainsNSdefs
    *
    * @return
    * @throws CanonicalizationException
    */
   public boolean currentNodeContainsNSdefs() throws CanonicalizationException {
      return this.nodeContainsNSdefs(this.getCurrentNode());
   }

   /**
    * Method getCurrentNode
    *
    * @return
    */
   public Node getCurrentNode() {
      return this._currentNode;
   }

   /**
    * Method getNextNode
    *
    * @return
    */
   public Node getNextNode() {

      this.goToNextNode();

      return this.getCurrentNode();
   }

   /**
    * Method goToNextNode
    *
    */
   private void goToNextNode() {

      if (this._currentNode == null) {
         return;
      }

      if (this._searchVisible) {
         Node parent = this._currentNode;

         // step over invisibe Nodes;
         while (true) {
            parent = parent.getParentNode();

            if (parent.getNodeType() != Node.ELEMENT_NODE) {
               this._currentNode = null;

               return;
            }

            if (this.visible(parent)) {
               this._currentNode = parent;

               return;
            }
         }
      } else {
         Node parent = this._currentNode.getParentNode();

         if (parent.getNodeType() != Node.ELEMENT_NODE) {
            this._currentNode = null;

            return;
         }

         if (this.visible(parent)) {
            this._currentNode = null;

            return;
         } else {
            this._currentNode = parent;

            return;
         }
      }
   }

   /**
    * This method searches the direct invisible ancestors of the context node
    * and returns the first attribute which defines a default NS. It returns
    * <CODE>null</CODE> otherwise.
    * @return
    * @throws CanonicalizationException
    */
   public Attr findFirstInvisibleDefaultNSAttr() {

      // reset _currentNode;
      this._currentNode = this._ctxNode;
      this._searchVisible = false;

      Node n;

      while ((n = this.getNextNode()) != null) {
         cat.debug(n);
         Attr attr = ((Element) n).getAttributeNode("xmlns");

         if (attr != null) {
            return attr;
         }
      }

      return null;
   }

   /**
    * This method searches all visible ancestors of the context node
    * and returns the first attribute which defines a default NS. It returns
    * <CODE>null</CODE> otherwise.
    *
    * @return
    * @throws CanonicalizationException
    */
   public Attr findFirstVisibleDefaultNSAttr()
           throws CanonicalizationException {

      // reset _currentNode;
      this._currentNode = this._ctxNode;
      this._searchVisible = true;

      Node n;

      while ((n = this.getNextNode()) != null) {
         Attr attr = ((Element) n).getAttributeNode("xmlns");

         if (attr != null) {
            return attr;
         }
      }

      return null;
   }

   /**
    * Collects all non-default namespace declarations that are being defined
    * in the invisible direct ancestors.
    *
    * @return a {@link Map} which contains the {@link Attr} nodes as values and uses the Attribute names as keys.
    * @throws CanonicalizationException
    */
   public Map findInvisibleNonDefaultNSAttrs()
           throws CanonicalizationException {

      // reset _currentNode;
      this._currentNode = this._ctxNode;
      this._searchVisible = false;

      HashMap result = new HashMap();
      Node n;

      while ((n = this.getNextNode()) != null) {
         NamedNodeMap attributes = ((Element) n).getAttributes();

         for (int i = 0; i < attributes.getLength(); i++) {
            Attr currentAttr = (Attr) attributes.item(i);
            String nodeAttrName = currentAttr.getNodeName();

            if (nodeAttrName.startsWith("xmlns:")) {
               if (!result.containsKey(nodeAttrName)) {
                  result.put(nodeAttrName, currentAttr);
               }
            }
         }
      }

      return result;
   }

   /**
    * Method logInvisibleNonDefaultNSAttrs
    *
    * @return
    * @throws CanonicalizationException
    */
   public String logInvisibleNonDefaultNSAttrs()
           throws CanonicalizationException {

      Map invisNS = this.findInvisibleNonDefaultNSAttrs();
      String result = "";
      Iterator invisIterator = invisNS.keySet().iterator();

      while (invisIterator.hasNext()) {
         String invisAttrName = (String) invisIterator.next();
         Attr invisAttr = (Attr) invisNS.get(invisAttrName);

         result += invisAttrName + "=\"" + invisAttr.getValue() + "\"";
      }

      return result;
   }

   /**
    * Method logVisibleNonDefaultNSAttrs
    *
    * @return
    * @throws CanonicalizationException
    */
   public String logVisibleNonDefaultNSAttrs()
           throws CanonicalizationException {

      Map invisNS = this.findVisibleNonDefaultNSAttrs();
      String result = "";
      Iterator invisIterator = invisNS.keySet().iterator();

      while (invisIterator.hasNext()) {
         String invisAttrName = (String) invisIterator.next();
         Attr invisAttr = (Attr) invisNS.get(invisAttrName);

         result += invisAttrName + "=\"" + invisAttr.getValue() + "\"";
      }

      return result;
   }

   /**
    * Collects all non-default namespace declarations that are being defined
    * in the visible ancestors.
    *
    * @return a {@link Map} which contains the {@link Attr} nodes as values and uses the Attribute names as keys.
    * @throws CanonicalizationException
    */
   public Map findVisibleNonDefaultNSAttrs() throws CanonicalizationException {

      // reset _currentNode;
      this._currentNode = this._ctxNode;
      this._searchVisible = true;

      HashMap result = new HashMap();
      Node n;

      while ((n = this.getNextNode()) != null) {
         NamedNodeMap attributes = ((Element) n).getAttributes();

         for (int i = 0; i < attributes.getLength(); i++) {
            Attr currentAttr = (Attr) attributes.item(i);
            String nodeAttrName = currentAttr.getNodeName();

            if (nodeAttrName.startsWith("xmlns:")) {
               if (!result.containsKey(nodeAttrName)) {
                  result.put(nodeAttrName, currentAttr);
               }
            }
         }
      }

      return result;
   }

   static {
      org.apache.xml.security.Init.init();
   }
}

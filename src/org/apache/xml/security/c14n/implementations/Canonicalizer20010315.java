
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
 * Implements <A HREF="http://www.w3.org/TR/2001/REC-xml-c14n-20010315">Canonical
 * XML Version 1.0</A>, a W3C Recommendation from 15 March 2001.
 *
 * @author Christian Geuer-Pollmann <geuerp@apache.org>
 * @version $Revision$
 */
public abstract class Canonicalizer20010315 extends CanonicalizerBase {

   /**
    * Constructor Canonicalizer20010315
    *
    * @param includeComments
    */
   public Canonicalizer20010315(boolean includeComments) {
      super(includeComments);
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

      return C14nHelper.sortAttributes(result.toArray());
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

      // System.out.println("During the traversal, I encountered " + XMLUtils.getXPath(E));
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
      boolean firstNodeIsDefaultNamespaceNode =
         !xmlns.getNodeValue().equals("") && this._xpathNodeSet.contains(xmlns);

      /* the element E that owns the axis is in the node-set
       */
      if (this._xpathNodeSet.contains(E) &&!firstNodeIsDefaultNamespaceNode) {

         /* The nearest ancestor element of E in the node-set has a default
          * namespace node in the node-set (default namespace nodes always
          * have non-empty values in XPath)
          */
         for (Node ancestor = E.getParentNode();
                 (ancestor != null)
                 && (ancestor.getNodeType() == Node.ELEMENT_NODE);
                 ancestor = ancestor.getParentNode()) {
            if (this._xpathNodeSet.contains(ancestor)) {
               Attr xmlnsA = ((Element) ancestor).getAttributeNodeNS(
                  Constants.NamespaceSpecNS, "xmlns");

               if (xmlnsA == null) {
                  throw new CanonicalizationException(
                     "c14n.XMLUtils.circumventBug2650forgotten");
               }

               if (!xmlnsA.getNodeValue().equals("")
                       && this._xpathNodeSet.contains(xmlnsA)) {

                  // OK, we must output xmlns=""
                  xmlns = this._doc.createAttributeNS(Constants.NamespaceSpecNS,
                                                      "xmlns");

                  xmlns.setValue("");
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

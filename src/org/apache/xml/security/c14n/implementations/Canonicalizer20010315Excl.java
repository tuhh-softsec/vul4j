
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
import org.apache.xml.utils.PrefixResolverDefault;
import org.apache.xpath.NodeSet;
import org.apache.xpath.XPath;
import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xml.security.c14n.*;
import org.apache.xml.security.c14n.helper.*;
import org.apache.xml.security.utils.*;
import org.apache.xml.security.transforms.params.InclusiveNamespaces;


/**
 * Implements &quot;<A HREF="http://www.w3.org/TR/2002/REC-xml-exc-c14n-20020718/">Exclusive XML Canonicalization, Version 1.0</A>&quot;
 * <BR />
 * Credits: During restructuring of the Canonicalizer framework, René Kollmorgen from
 * Software AG submitted an implementation of ExclC14n which fitted into the old
 * architecture and which based heavily on my old (and slow) implementation of
 * "Canonical XML". A big "thank you" to René for this.
 * <BR />
 * <i>THIS</i> implementation is a complete rewrite of the algorithm.
 *
 * @author Christian Geuer-Pollmann <geuerp@apache.org>
 * @version $Revision$
 * @see <A HREF="http://www.w3.org/TR/2002/REC-xml-exc-c14n-20020718/">"Exclusive XML Canonicalization, Version 1.0"</A>
 */
public abstract class Canonicalizer20010315Excl extends CanonicalizerBase {

   /**
    * Constructor Canonicalizer20010315Excl
    *
    * @param includeComments
    */
   public Canonicalizer20010315Excl(boolean includeComments) {
      super(includeComments);
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
      return this.engineCanonicalizeSubTree(rootNode, "");
   }

   /**
    * Method engineCanonicalizeSubTree
    *
    * @param rootNode
    * @param inclusiveNamespaces
    *
    * @throws CanonicalizationException
    */
   public byte[] engineCanonicalizeSubTree(Node rootNode, String inclusiveNamespaces)
           throws CanonicalizationException {

      this._rootNodeOfC14n = rootNode;
      this._doc = XMLUtils.getOwnerDocument(this._rootNodeOfC14n);
      this._documentElement = this._doc.getDocumentElement();

      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();

         this._writer = new OutputStreamWriter(baos, Canonicalizer.ENCODING);
         this._inclusiveNSSet =
            InclusiveNamespaces.prefixStr2Set(inclusiveNamespaces);

         Map inscopeNamespaces = this.getInscopeNamespaces(rootNode);
         Map alreadyVisible = new HashMap();

         canonicalizeSubTree(rootNode, inscopeNamespaces, alreadyVisible);
         this._writer.flush();
         this._writer.close();

         return baos.toByteArray();
      } catch (UnsupportedEncodingException ex) {
         throw new CanonicalizationException("empty", ex);
      } catch (IOException ex) {
         throw new CanonicalizationException("empty", ex);
      } finally {
         this._xpathNodeSet = null;
         this._inclusiveNSSet = null;
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
   void canonicalizeSubTree(Node currentNode, Map inscopeNamespaces, Map alreadyVisible)
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
            updateInscopeNamespacesAndReturnVisibleAttrs(currentElement,
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
                * We must 'clone' the inscopeNamespaces to allow the child elements
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
   List updateInscopeNamespacesAndReturnVisibleAttrs(Element currentElement, Map inscopeNamespaces, Map alreadyVisible)
           throws CanonicalizationException {

      Vector ns = new Vector();
      Vector at = new Vector();
      NamedNodeMap attributes = currentElement.getAttributes();
      int attributesLength = attributes.getLength();

      for (int i = 0; i < attributesLength; i++) {
         Attr currentAttr = (Attr) attributes.item(i);
         String name = currentAttr.getNodeName();
         String value = currentAttr.getValue();

         if (name.equals("xmlns") && value.equals("")

         /* && inscopeNamespaces.containsKey(name) */
         ) {

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
            at.add(currentAttr);
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
            ns.add(a);
         }
      }

      Iterator it = inscopeNamespaces.keySet().iterator();

      while (it.hasNext()) {
         String name = (String) it.next();
         String inscopeValue = (String) inscopeNamespaces.get(name);

         if (name.startsWith("xml:")
                 &&!(alreadyVisible.containsKey(name)
                     && alreadyVisible.get(name).equals(inscopeValue))) {
            alreadyVisible.put(name, inscopeValue);

            Attr a =
               this._doc.createAttributeNS(Constants.XML_LANG_SPACE_SpecNS,
                                           name);

            a.setValue(inscopeValue);
            at.add(a);
         } else if (this.utilizedOrIncluded(currentElement, name)
                    && (!alreadyVisible.containsKey(name)
                        || (alreadyVisible.containsKey(name)
                            &&!alreadyVisible.get(name).equals(
                               inscopeValue)))) {
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
      Collections.sort(
         at, new org.apache.xml.security.c14n.helper.NonNSAttrCompare());
      ns.addAll(at);

      return ns;
   }

   /**
    * Method handleAttributesSubtree
    *
    * @param E
    * @throws CanonicalizationException
    */
   Object[] handleAttributesSubtree(Element E)
           throws CanonicalizationException {
      throw new RuntimeException("Not yet implemented");
   }

   /**
    * Method engineCanonicalizeXPathNodeSet
    *
    * @param xpathNodeSet
    * @throws CanonicalizationException
    */
   public byte[] engineCanonicalizeXPathNodeSet(Set xpathNodeSet)
           throws CanonicalizationException {
      return this.engineCanonicalizeXPathNodeSet(xpathNodeSet, "");
   }

   /**
    * Method engineCanonicalizeXPathNodeSet
    *
    * @param xpathNodeSet
    * @param inclusiveNamespaces
    * @throws CanonicalizationException
    */
   public byte[] engineCanonicalizeXPathNodeSet(Set xpathNodeSet, String inclusiveNamespaces)
           throws CanonicalizationException {

      try {
         this._inclusiveNSSet =
            InclusiveNamespaces.prefixStr2Set(inclusiveNamespaces);
         this._renderedPrefixesForElement = new HashMap();

         return super.engineCanonicalizeXPathNodeSet(xpathNodeSet);
      } finally {
         this._inclusiveNSSet = null;
         this._renderedPrefixesForElement = null;
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
            this.canonicalizeXPathNodeSet(currentChild);
         }
         break;

      case Node.COMMENT_NODE :
         if (this._includeComments
                 && this._xpathNodeSet.contains(currentNode)) {
            int position = getPositionRelativeToDocumentElement(currentNode);

            if (position == NODE_AFTER_DOCUMENT_ELEMENT) {
               this._writer.write("\n");
            }

            this.outputCommentToWriter((Comment) currentNode);

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

            this.outputPItoWriter((ProcessingInstruction) currentNode);

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
               this.outputTextToWriter(nextSibling.getNodeValue());
            }
         }
         break;

      case Node.ELEMENT_NODE :
         Element currentElement = (Element) currentNode;

         this._renderedPrefixesForElement.put(currentElement, new HashSet());

         if (currentNodeIsVisible) {
            this._writer.write("<");
            this._writer.write(currentElement.getTagName());
         }

         // we output all Attrs which are available
         Object attrs[] = this.handleAttributes(currentElement);

         for (int i = 0; i < attrs.length; i++) {
            Attr a = (Attr) attrs[i];

            this.outputAttrToWriter(a.getNodeName(), a.getNodeValue());
         }

         if (currentNodeIsVisible) {
            this._writer.write(">");
         }

         // traversal
         for (Node currentChild = currentNode.getFirstChild();
                 currentChild != null;
                 currentChild = currentChild.getNextSibling()) {
            this.canonicalizeXPathNodeSet(currentChild);
         }

         if (currentNodeIsVisible) {
            this._writer.write("</");
            this._writer.write(currentElement.getTagName());
            this._writer.write(">");
         }

         this._renderedPrefixesForElement.remove(currentElement);
         break;
      }
   }

   /**
    *
    * @param E
    * @throws CanonicalizationException
    */
   Object[] handleAttributes(Element E) throws CanonicalizationException {

      // System.out.println("During the traversal, I encountered " + XMLUtils.getXPath(E));
      // result will contain the attrs which have to be outputted
      List result = new Vector();
      HashSet namespacePrefixesOutputForCurrentElement =
         (HashSet) this._renderedPrefixesForElement.get(E);
      NamedNodeMap attrs = E.getAttributes();
      int attrsLength = attrs.getLength();

      if (this._inclusiveNSSet.contains("xmlns")) {

         // handle the default namespace if the #default token is in the prefix list

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
            !xmlns.getNodeValue().equals("")
            && this._xpathNodeSet.contains(xmlns);

         /* the element E that owns the axis is in the node-set
          */
         if (!firstNodeIsDefaultNamespaceNode
                 && this._xpathNodeSet.contains(E)) {

            /* The nearest output ancestor element of E has a default
             * namespace node in the node-set
             */
            for (Node ancestor = E.getParentNode();
                    (ancestor != null)
                    && (ancestor.getNodeType() == Node.ELEMENT_NODE);
                    ancestor = ancestor.getParentNode()) {
               if (this._xpathNodeSet.contains(ancestor)) {

                  // we're on an output ancestor
                  Attr xmlnsA = ((Element) ancestor).getAttributeNodeNS(
                     Constants.NamespaceSpecNS, "xmlns");

                  if (xmlnsA == null) {
                     throw new CanonicalizationException(
                        "c14n.XMLUtils.circumventBug2650forgotten");
                  }

                  if (!xmlnsA.getNodeValue().equals("")
                          && this._xpathNodeSet.contains(xmlnsA)) {

                     // OK, we must output xmlns=""
                     xmlns =
                        this._doc.createAttributeNS(Constants.NamespaceSpecNS,
                                                    "xmlns");

                     xmlns.setValue("");
                     result.add(xmlns);
                  }

                  break;
               }
            }
         }
      }

      /*
       * [xmlns not present in the prefix list]
       * If the token representing the default namespace is not present in
       * InclusiveNamespaces PrefixList, then the rules for rendering xmlns=""
       * are changed as follows. When canonicalizing the namespace axis of an
       * element E that is in the node-set, output xmlns="" if and only if all
       * of the conditions are met:
       *
       * (1) E is in the node set, and
       * (2) E visibly utilizes the default namespace (i.e., it has no namespace prefix), and
       * (3) E has no default namespace node in the node-set, and
       * (4) the nearest output ancestor of E that visibly utilizes the
       *     default namespace has a default namespace node in the node-set.
       */
      if (!this._inclusiveNSSet.contains("xmlns")) {

         // #default is not present in InclusiveNamespaces PrefixList
         if (this._xpathNodeSet.contains(E)) {

            // E is in the node set
            if (E.getPrefix() == null) {

               // it has no namespace prefix, i.e. E visibly utilizes the default namespace
               Attr xmlns = E.getAttributeNodeNS(Constants.NamespaceSpecNS,
                                                 "xmlns");

               if (xmlns == null) {
                  throw new CanonicalizationException(
                     "c14n.XMLUtils.circumventBug2650forgotten");
               }

               if (xmlns.getValue().equals("")) {

                  // it has no default namespace in the node set
                  searchForNearestOutputAncestorWhichUtilizesDefault: for (
                          Node ancestor = E.getParentNode();
                                                                              (ancestor != null)
                                                                              && (ancestor.getNodeType()
                                                                                 == Node.ELEMENT_NODE);
                                                                              ancestor = ancestor
                                                                              .getParentNode()) {
                     if (!this._xpathNodeSet.contains(ancestor)) {
                        continue searchForNearestOutputAncestorWhichUtilizesDefault;
                     }

                     Element ancestorElement = (Element) ancestor;

                     // now we are on an output ancestor
                     if (ancestorElement.getPrefix() == null) {

                        // it utilizes the default, so our search found an end, so we must break
                        Attr xmlnsA = ancestorElement.getAttributeNodeNS(
                           Constants.NamespaceSpecNS, "xmlns");

                        if (xmlnsA == null) {
                           throw new CanonicalizationException(
                              "c14n.XMLUtils.circumventBug2650forgotten");
                        }

                        if (!xmlnsA.getValue().equals("")) {

                           // has a default namespace
                           if (this._xpathNodeSet.contains(xmlnsA)) {

                              // in the node set
                              result.add(xmlns);
                           }
                        }

                        break searchForNearestOutputAncestorWhichUtilizesDefault;
                     }
                  }
               } else {

                  // !xmlns.getValue().equals("")
                  //
                  // the inclusivePrefixList does not contain the #default token,
                  // but E *HAS* a non-empty default namespace in the node set.
                  // now we must see what to do
                  boolean foundAnOutputAncestor = false;

                  searchForNearestOutputAncestorWhichUtilizesDefault: for (
                          Node ancestor = E.getParentNode();
                                                                              (ancestor != null)
                                                                              && (ancestor.getNodeType()
                                                                                 == Node.ELEMENT_NODE);
                                                                              ancestor = ancestor
                                                                              .getParentNode()) {
                     if (!this._xpathNodeSet.contains(ancestor)) {
                        continue searchForNearestOutputAncestorWhichUtilizesDefault;
                     }

                     Element ancestorElement = (Element) ancestor;
                     Attr xmlnsA = ancestorElement.getAttributeNodeNS(
                        Constants.NamespaceSpecNS, "xmlns");

                     if (xmlnsA == null) {
                        throw new CanonicalizationException(
                           "c14n.XMLUtils.circumventBug2650forgotten");
                     }

                     // now we are on an output ancestor
                     if (ancestorElement.getPrefix() == null) {

                        // it utilizes the default, so our search found an end, so we must break
                        foundAnOutputAncestor = true;

                        boolean valueEquals =
                           xmlnsA.getValue().equals(xmlns.getValue());
                        boolean inNodeset = this._xpathNodeSet.contains(xmlnsA);

                        // boolean alreadyOutputted = ((Set)this._renderedPrefixesForElement.get(ancestorElement)).contains(xmlns.getName());
                        if (valueEquals) {
                           if (inNodeset) {
                              ;    // do nothing
                           } else {
                              ;
                           }
                        } else {
                           if (inNodeset) {

                              // has a different namespace value in the node set
                              result.add(xmlns);

                              if (xmlns.getPrefix() != null) {
                                 namespacePrefixesOutputForCurrentElement.add(
                                    xmlns.getName());
                              }
                           } else {
                              ;
                           }
                        }

                        break searchForNearestOutputAncestorWhichUtilizesDefault;
                     }
                  }

                  if (!foundAnOutputAncestor) {

                     // we did not find an output ancestor of E which utilizes
                     // the default namespace, so we must output the non-empty
                     // default namespace
                     result.add(xmlns);

                     if (xmlns.getPrefix() != null) {
                        namespacePrefixesOutputForCurrentElement.add(
                           xmlns.getName());
                     }
                  }
               }
            }
         }
      }

      /* ***********************************************************************
       * Handle namespace axis
       * ***********************************************************************/
      handleNamespacesWhichAreIncludedInInclusiveNamespaces: for (int i = 0;
                                                                     i < attrsLength;
                                                                     i++) {
         Attr N = (Attr) attrs.item(i);

         if (!Constants.NamespaceSpecNS.equals(N.getNamespaceURI())) {

            // only handle namespaces here
            continue handleNamespacesWhichAreIncludedInInclusiveNamespaces;
         }

         if (C14nHelper.namespaceIsRelative(N)) {
            Object exArgs[] = { E.getTagName(), N.getName(), N.getNodeValue() };

            throw new CanonicalizationException(
               "c14n.Canonicalizer.RelativeNamespace", exArgs);
         }

         if ("xml".equals(N.getLocalName())
                 && Constants.XML_LANG_SPACE_SpecNS.equals(N.getNodeValue())) {

            /* except omit namespace node with local name xml, which defines
             * the xml prefix, if its string value is http://www.w3.org/XML/1998/namespace.
             */
            continue handleNamespacesWhichAreIncludedInInclusiveNamespaces;
         }

         if (N.getName().equals("xmlns") && N.getNodeValue().equals("")) {

            // xmlns="" already handled
            continue handleNamespacesWhichAreIncludedInInclusiveNamespaces;
         }

         if (!this._inclusiveNSSet.contains(N.getName())) {

            /*
             * The Exclusive XML Canonicalization method may receive an
             * additional, possibly null, parameter InclusiveNamespaces PrefixList
             * containing a list of namespace prefixes and/or a token indicating
             * the presence of the default namespace.
             * All namespace nodes appearing on this list are handled as provided in Canonical XML.
             */
            continue handleNamespacesWhichAreIncludedInInclusiveNamespaces;
         }

         if (!this._xpathNodeSet.contains(N)) {

            // Consider a list L containing only namespace nodes in the axis and in the node-set
            //
            // only if N in the node set
            continue handleNamespacesWhichAreIncludedInInclusiveNamespaces;
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

            if (N.getPrefix() != null) {
               namespacePrefixesOutputForCurrentElement.add(N.getName());
            }
         }
      }

      /* **********************************************************************
       * [xmlns:* not present in the prefix list]                             *
       * **********************************************************************/
      Set visiblyUtilized = this.visiblyUtilized(E);

      handleNamespacesWhichAreNotIncludedInInclusiveNamespaces: for (int i = 0;
                                                                        i < attrsLength;
                                                                        i++) {
         Attr N = (Attr) attrs.item(i);

         if (!Constants.NamespaceSpecNS.equals(N.getNamespaceURI())) {

            // only handle namespaces here
            continue handleNamespacesWhichAreNotIncludedInInclusiveNamespaces;
         }

         if (C14nHelper.namespaceIsRelative(N)) {
            Object exArgs[] = { E.getTagName(), N.getName(), N.getNodeValue() };

            throw new CanonicalizationException(
               "c14n.Canonicalizer.RelativeNamespace", exArgs);
         }

         if (N.getName().equals("xmlns")) {

            // xmlns already handled
            continue handleNamespacesWhichAreNotIncludedInInclusiveNamespaces;
         }

         if ("xml".equals(N.getLocalName())
                 && Constants.XML_LANG_SPACE_SpecNS.equals(N.getNodeValue())) {

            /* except omit namespace node with local name xml, which defines
             * the xml prefix, if its string value is http://www.w3.org/XML/1998/namespace.
             */
            continue handleNamespacesWhichAreNotIncludedInInclusiveNamespaces;
         }

         if (this._inclusiveNSSet.contains(N.getName())) {

            // A namespace node N with a prefix that does not appear
            continue handleNamespacesWhichAreNotIncludedInInclusiveNamespaces;
         }

         if (!this._xpathNodeSet.contains(N)) {

            // c14n: Consider a list L containing only  namespace nodes in the axis and *IN THE NODE-SET*
            continue handleNamespacesWhichAreNotIncludedInInclusiveNamespaces;
         }

         /*
          * [xmlns:* not present in the prefix list]
          * A namespace node N with a prefix that does not appear in the
          * InclusiveNamespaces PrefixList is rendered if all of the conditions
          * are met:
          *
          * (1)  Its parent element E is in the node-set, and
          * (2)  N is visibly utilized by its parent element E, and
          * (3a) the prefix has not yet been rendered by any output ancestor, or
          * (3b) the nearest output ancestor of E that visibly utilizes the
          *      namespace prefix does not have a namespace node in the node-set
          *      with the same namespace prefix and value as N.
          */
         if (this._xpathNodeSet.contains(E)) {

            // (1) Its parent element E is in the node-set, and
            if (visiblyUtilized.contains(N.getName())) {

               // (2) N is visibly utilized by its parent element E
               boolean renderedByAnOutputAncestor = false;

               if (E != this._documentElement) {

                  // this ensure that we have ancestors where we have to look
                  // whether the prefix has been rendered
                  for (Node ancestor = E.getParentNode(); ancestor != this._doc;
                          ancestor = ancestor.getParentNode()) {
                     if (this._xpathNodeSet.contains(ancestor)) {

                        // which prefixes have been rendered by ancestor
                        HashSet prefixesRenderedInAncestor =
                           (HashSet) this._renderedPrefixesForElement.get(
                              ancestor);

                        if (prefixesRenderedInAncestor.contains(N.getName())) {

                           // the prefix HAS been rendered by an output ancestor
                           renderedByAnOutputAncestor = true;

                           break;
                        }
                     }
                  }
               }

               if (!renderedByAnOutputAncestor) {
                  result.add(N);

                  if (N.getPrefix() != null) {
                     namespacePrefixesOutputForCurrentElement.add(N.getName());
                  }

                  continue handleNamespacesWhichAreNotIncludedInInclusiveNamespaces;
               }

               // now handle (3b)
               searchForNearestOutputAncestorWhichUtilizesPrefix: for (
                       Node ancestor = E.getParentNode();
                                                                          (ancestor != null)
                                                                          && (ancestor.getNodeType()
                                                                             == Node.ELEMENT_NODE);
                                                                          ancestor = ancestor
                                                                          .getParentNode()) {
                  if (!this._xpathNodeSet.contains(ancestor)) {
                     continue searchForNearestOutputAncestorWhichUtilizesPrefix;
                  }

                  Element ancestorElement = (Element) ancestor;

                  // now we are on an output ancestor
                  Set utilizedByAncestor =
                     this.visiblyUtilized(ancestorElement);

                  if (utilizedByAncestor.contains(N.getName())) {

                     // it utilizes the prefix, so our search found an end, so we must break
                     Attr xmlnsA = ancestorElement.getAttributeNodeNS(
                        Constants.NamespaceSpecNS, N.getLocalName());

                     if (xmlnsA == null) {

                        // the nearest output ancestor does not have a namespace node
                        result.add(N);

                        if (N.getPrefix() != null) {
                           namespacePrefixesOutputForCurrentElement.add(
                              N.getName());
                        }

                        continue handleNamespacesWhichAreNotIncludedInInclusiveNamespaces;
                     }

                     boolean inNodeSet = this._xpathNodeSet.contains(xmlnsA);
                     final boolean prefixEquals = true;
                     boolean valueEquals =
                        N.getValue().equals(xmlnsA.getValue());

                     if (!inNodeSet && prefixEquals && valueEquals) {
                        result.add(N);

                        if (N.getPrefix() != null) {
                           namespacePrefixesOutputForCurrentElement.add(
                              N.getName());
                        }

                        continue handleNamespacesWhichAreNotIncludedInInclusiveNamespaces;
                     }

                     break searchForNearestOutputAncestorWhichUtilizesPrefix;
                  }
               }    // searchForNearestOutputAncestorWhichUtilizesPrefix
            }
         }
      }

      /*
       * Handle the regular attributes
       */
      for (int i = 0; i < attrsLength; i++) {
         Attr attr = (Attr) attrs.item(i);

         if (!Constants.NamespaceSpecNS.equals(attr.getNamespaceURI())) {

            // we have an attribute, not a namespace
            if (this._xpathNodeSet.contains(attr)) {

               // output attributes which are in the node set
               result.add(attr);
            }
         }
      }

      Object resultAsArray[] = result.toArray();
      Object sortedResultAsArray[] = C14nHelper.sortAttributes(resultAsArray);

      return sortedResultAsArray;
   }

   /**
    * Collects all relevant xml:* and attributes from all ancestor
    * Elements from rootNode and creates a Map containg the attribute
    * names/values.
    *
    * @param apexNode
    *
    * @throws CanonicalizationException
    */
   Map getInscopeNamespaces(Node apexNode) throws CanonicalizationException {

      Map result = new HashMap();

      if (apexNode.getNodeType() != Node.ELEMENT_NODE) {
         return result;
      }

      Element apexElement = (Element) apexNode;

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
               result.remove(name);
            } else if (name.startsWith("xmlns") &&!value.equals("")) {
               if (!result.containsKey(name)) {
                  result.put(name, value);
               }
            }
         }
      }

      return result;
   }

   /**
    * Returns <code>true</code> is the namespace is either utilized by the
    * given element or included by the includedNamespaces parameter.
    *
    * @param element
    * @param namespace
    *
    */
   public boolean utilizedOrIncluded(Element element, String namespace) {

      if (this._inclusiveNSSet.contains(namespace)) {

         // included;
         return true;
      }

      boolean utilized = this.visiblyUtilized(element).contains(namespace);

      return utilized;
   }

   /**
    * Method visiblyUtilized
    *
    * @param element
    * @return a Set of namespace names.
    */
   public Set visiblyUtilized(Element element) {

      Set result = new HashSet();

      if (this._xpathNodeSet == null) {

         // we are in the canonicalizeSubtree part
         if (element.getNamespaceURI() != null) {
            String prefix = element.getPrefix();

            if (prefix == null) {
               result.add("xmlns");
            } else {
               result.add("xmlns:" + prefix);
            }
         }

         NamedNodeMap attributes = element.getAttributes();
         int attributesLength = attributes.getLength();

         // if the attribute is not xmlns:... and not xml:... but
         // a:..., add xmlns:a to the list
         for (int i = 0; i < attributesLength; i++) {
            Attr currentAttr = (Attr) attributes.item(i);
            String prefix = currentAttr.getPrefix();

            if (prefix != null) {
               if (!prefix.equals("xml") &&!prefix.equals("xmlns")) {
                  result.add("xmlns:" + prefix);
               }
            }
         }
      } else {
         if (this._xpathNodeSet.contains(element)) {

            // we are in the canonicalizeXPathNodeSet part
            if (element.getNamespaceURI() != null) {
               String prefix = element.getPrefix();

               if ((prefix == null) || (prefix.length() == 0)) {
                  result.add("xmlns");
               } else {
                  result.add("xmlns:" + prefix);
               }
            }

            NamedNodeMap attributes = element.getAttributes();
            int attributesLength = attributes.getLength();

            // if the attribute is not xmlns:... and not xml:... but
            // a:..., add xmlns:a to the list
            for (int i = 0; i < attributesLength; i++) {
               Attr currentAttr = (Attr) attributes.item(i);

               if (this._xpathNodeSet.contains(currentAttr)) {
                  String prefix = currentAttr.getPrefix();

                  if (prefix != null) {
                     if (!prefix.equals("xml") &&!prefix.equals("xmlns")) {
                        result.add("xmlns:" + prefix);
                     }
                  }
               }
            }
         }
      }

      return result;
   }
}

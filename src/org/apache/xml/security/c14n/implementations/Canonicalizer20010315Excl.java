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
 * Implements <A HREF="http://www.w3.org/Signature/Drafts/xml-exc-c14n">"Exclusive
 * XML Canonicalization, Version 1.0"</A>, Rev 1.58.
 * <BR />
 * Credits: During restructuring of the Canonicalizer framework, René Kollmorgen from
 * Software AG submitted an implementation of ExclC14n which fitted into the old
 * architecture and which based heavily on my old (and slow) implementation of
 * "Canonical XML". A big "thank you" to René for this.
 * <BR />
 * THIS implementation is a complete rewrite of the algorithm.
 *
 * @author $Author$
 * @version $Revision$
 * @see <A HREF="http://www.w3.org/Signature/Drafts/xml-exc-c14n">"Exclusive XML Canonicalization, Version 1.0"</A>, Rev 1.58.
 */
public abstract class Canonicalizer20010315Excl extends CanonicalizerSpi {
   //J-
   boolean _includeComments = false;

   Set _xpathNodeSet = null;
   Set _inclusiveNSSet = null;

   Document _doc = null;
   Element _documentElement = null;
   Node _rootNodeOfC14n = null;

   Writer _writer = null;
   //J+

   /**
    * Constructor Canonicalizer20010315Excl
    *
    * @param includeComments
    */
   public Canonicalizer20010315Excl(boolean includeComments) {
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
   public byte[] engineCanonicalizeSubTree(
           Node rootNode, String inclusiveNamespaces)
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
   List updateInscopeNamespacesAndReturnVisibleAttrs(
           Element currentElement, Map inscopeNamespaces, Map alreadyVisible)
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
                            &&!alreadyVisible.get(name)
                               .equals(inscopeValue)))) {
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

      Collections.sort(ns, new org.apache.xml.security.c14n.helper.NSAttrCompare());
      Collections.sort(at, new org.apache.xml.security.c14n.helper.NonNSAttrCompare());

      ns.addAll(at);

      return ns;
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
      return this.engineCanonicalizeXPathNodeSet(xpathNodeSet, "");
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

      this._xpathNodeSet = xpathNodeSet;

      if (this._xpathNodeSet.size() == 0) {
         return new byte[0];
      }

      {

         // get only a single node as anchor to fetch the owner document
         Node n = (Node) this._xpathNodeSet.iterator().next();

         this._doc = XMLUtils.getOwnerDocument(n);
         this._documentElement = this._doc.getDocumentElement();
      }

      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();

         this._writer = new OutputStreamWriter(baos, Canonicalizer.ENCODING);
         this._inclusiveNSSet =
            InclusiveNamespaces.prefixStr2Set(inclusiveNamespaces);

         this.canonicalizeXPathNodeSet(this._doc, true, new EC14nCtx());

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
    * Method canonicalizeXPathNodeSet
    *
    * @param currentNode
    * @param parentIsVisible
    * @param ctx
    * @throws CanonicalizationException
    * @throws IOException
    */
   void canonicalizeXPathNodeSet(
           Node currentNode, boolean parentIsVisible, EC14nCtx ctx)
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
            canonicalizeXPathNodeSet(currentChild, true, ctx);
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
   List getAttrs(Element currentElement, boolean parentIsVisible, EC14nCtx ctx)
           throws CanonicalizationException {

      Set visiblyUtilized = new HashSet();

      boolean currentElementIsInNodeset = this._xpathNodeSet.contains(currentElement);
      if (currentElementIsInNodeset) {
         if (currentElement.getNamespaceURI() != null) {
            String prefix = currentElement.getPrefix();

            if (prefix == null) {
               visiblyUtilized.add("xmlns");
            } else {
               visiblyUtilized.add("xmlns:" + prefix);
            }
         }
      }

      Vector namespacesInSubset = new Vector();
      Vector attributesInSubset = new Vector();
      NamedNodeMap attributes = currentElement.getAttributes();
      int attributesLength = attributes.getLength();
      for (int i = 0; i < attributesLength; i++) {
         Attr currentAttr = (Attr) attributes.item(i);
         if (this._xpathNodeSet.contains(currentAttr)) {
            String URI = currentAttr.getNamespaceURI();
            if (URI != null && Constants.NamespaceSpecNS.equals(URI)) {
               String value = currentAttr.getValue();
               if (C14nHelper.namespaceIsRelative(value)) {
                  Object exArgs[] = { currentElement.getTagName(), currentAttr.getNodeName(), value };

                  throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", exArgs);
               }
               namespacesInSubset.add(currentAttr);
            } else {
               attributesInSubset.add(currentAttr);

               String prefix = currentAttr.getPrefix();
               if (prefix != null) {
                  visiblyUtilized.add("xmlns:" + prefix);
               }
            }
         }
      }
      Collections.sort(namespacesInSubset,
                       new org.apache.xml.security.c14n.helper.NSAttrCompare());
      Collections.sort(attributesInSubset,
                       new org.apache.xml.security.c14n.helper.NonNSAttrCompare());

      Vector nsResult = new Vector();
      for (int i = 0; i < namespacesInSubset.size(); i++) {
         Attr currentAttr = (Attr) namespacesInSubset.get(i);
         String name = currentAttr.getNodeName();
         String value = currentAttr.getValue();

         if (name.equals("xmlns") && value.equals("")) {
            // undeclare default namespace
            boolean nameAlreadyVisible = ctx.n.containsKey(name);

            if (nameAlreadyVisible) {
               ctx.n.remove(name);
               nsResult.add(currentAttr);
            }
         } else {
            //J-
            // boolean utilizedOrIncluded = this.utilizedOrIncluded(currentElement, name);
            boolean utilizedOrIncluded = visiblyUtilized.contains(name) || this._inclusiveNSSet.contains(name);
            boolean nameAlreadyVisible = ctx.n.containsKey(name);
            boolean visibleButNotEqual = nameAlreadyVisible && !ctx.n.get(name).equals(value);
            //J+
            if (currentElementIsInNodeset && utilizedOrIncluded && (!nameAlreadyVisible || visibleButNotEqual)) {
               // ns_rendered_new.put(name, value);
               ctx.n.put(name, value);
               nsResult.add(currentAttr);
            }
         }
      }
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
    *
    */
   public Set visiblyUtilized(Element element) {

      Set result = new HashSet();

      if (this._xpathNodeSet == null) {

         // we are in the canonicalizeSubtree part
         if (element.getNamespaceURI() != null) {
            String elementPrefix = element.getPrefix();

            if (elementPrefix == null) {
               result.add("xmlns");
            } else {
               result.add("xmlns:" + elementPrefix);
            }
         }

         NamedNodeMap attributes = element.getAttributes();
         int attributesLength = attributes.getLength();

         // if the attribute is not xmlns:... and not xml:... but
         // a:..., add xmlns:a to the list
         for (int i = 0; i < attributesLength; i++) {
            Attr currentAttr = (Attr) attributes.item(i);

            if (currentAttr.getNamespaceURI() != null) {
               String attrPrefix = currentAttr.getPrefix();

               if ((attrPrefix != null) &&!attrPrefix.equals("xml")
                       &&!attrPrefix.equals("xmlns")) {
                  result.add("xmlns:" + attrPrefix);
               }
            }
         }
      } else if ((this._xpathNodeSet != null)
                 && this._xpathNodeSet.contains(element)) {

         // we are in the canonicalizeXPathNodeSet part
         if (element.getNamespaceURI() != null) {
            String elementPrefix = element.getPrefix();

            if ((elementPrefix == null) || (elementPrefix.length() == 0)) {
               result.add("xmlns");
            } else {
               result.add("xmlns:" + elementPrefix);
            }
         }

         NamedNodeMap attributes = element.getAttributes();
         int attributesLength = attributes.getLength();

         // if the attribute is not xmlns:... and not xml:... but
         // a:..., add xmlns:a to the list
         for (int i = 0; i < attributesLength; i++) {
            Attr currentAttr = (Attr) attributes.item(i);

            if (this._xpathNodeSet.contains(currentAttr)
                    && (currentAttr.getNamespaceURI() != null)) {
               String attrPrefix = currentAttr.getPrefix();

               if ((attrPrefix != null) &&!attrPrefix.equals("xml")
                       &&!attrPrefix.equals("xmlns")) {
                  result.add("xmlns:" + attrPrefix);
               }
            }
         }
      }

      return result;
   }

   /**
    * Class EC14nCtx
    *
    * @author $Author$
    * @version $Revision$
    */
   class EC14nCtx {

      /** Field n */
      Map n;

      /**
       * Constructor EC14nCtx
       *
       */
      public EC14nCtx() {
         this.n = new HashMap();
      }

      /**
       * Constructor EC14nCtx
       *
       * @param n
       */
      public EC14nCtx(Map n) {
         this.n = n;
      }

      /**
       * Method copy
       *
       *
       */
      public EC14nCtx copy() {

         EC14nCtx c = new EC14nCtx();

         c.n = new HashMap(this.n);

         return c;
      }
   }
}

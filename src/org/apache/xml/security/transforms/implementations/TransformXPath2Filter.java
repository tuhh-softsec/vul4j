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
package org.apache.xml.security.transforms.implementations;



import java.util.*;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
import org.w3c.dom.*;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.c14n.*;
import org.apache.xml.security.transforms.params.XPath2FilterContainer;
import org.apache.xml.security.transforms.*;
import org.apache.xml.security.exceptions.*;
import org.apache.xml.security.utils.*;
import org.apache.xpath.CachedXPathAPI;
import org.apache.xpath.objects.XObject;
import org.apache.xml.utils.PrefixResolverDefault;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.dtm.DTMManager;


/**
 * Implements the <I>XML Signature XPath Filter v2.0</I>
 *
 * @author $Author$
 * @see <A HREF="http://www.w3.org/TR/xmldsig-filter2/">XPath Filter v2.0 (TR)</A>
 * @see <A HREF=http://www.w3.org/Signature/Drafts/xmldsig-xfilter2/">XPath Filter v2.0 (editors copy)</A>
 */
public class TransformXPath2Filter extends TransformSpi {

   /** {@link org.apache.log4j} logging facility */
   // static org.apache.log4j.Category cat = org.apache.log4j.Category.getInstance(TransformXPath2Filter.class.getName());

   /** Field implementedTransformURI */
   public static final String implementedTransformURI =
      Transforms.TRANSFORM_XPATH2FILTER;
   //J-
   // contains the type of the filter
   Vector _filterTypes = new Vector();

   // contains the node set
   Vector _filterNodes = new Vector();

   Set _F = null;
   Vector _ancestors = null;

   private static final String FUnion = "union";
   private static final String FSubtract = "subtract";
   private static final String FIntersect = "intersect";

   public boolean wantsOctetStream ()   { return false; }
   public boolean wantsNodeSet ()       { return true; }
   public boolean returnsOctetStream () { return false; }
   public boolean returnsNodeSet ()     { return true; }

   //J+

   /**
    * Method engineGetURI
    *
    * @return
    */
   protected String engineGetURI() {
      return this.implementedTransformURI;
   }

   Set _inputSet = null;

   /**
    * Method enginePerformTransform
    *
    * @param input
    * @return
    * @throws TransformationException
    */
   protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input)
           throws TransformationException {

      try {
         long start = System.currentTimeMillis();

         this._inputSet = input.getNodeSet();

         if (this._inputSet.size() == 0) {

            // input node set contains no nodes
            return input;
         }

         CachedXPathFuncHereAPI xPathFuncHereAPI =
            new CachedXPathFuncHereAPI(input.getCachedXPathAPI());
         CachedXPathAPI myXPathAPI =
            new CachedXPathAPI(input.getCachedXPathAPI());
         Document inputDoc = null;

         {
            Iterator it = this._inputSet.iterator();

            inputDoc = XMLUtils.getOwnerDocument((Node) it.next());
         }

         int noOfSteps =
            this._transformObject.length(Transforms.TRANSFORM_XPATH2FILTER,
                                         "XPath");

         if (noOfSteps == 0) {
            Object exArgs[] = { Transforms.TRANSFORM_XPATH2FILTER, "XPath" };

            throw new TransformationException("xml.WrongContent", exArgs);
         }

         if (true) {
            XPath2FilterContainer unionDocFilter =
               XPath2FilterContainer
                  .newInstanceUnion(this._transformObject.getDocument(), "/");

            _filterTypes.add(FUnion);

            // Set root = new HashSet(); root.add(inputDoc);
            HelperNodeList root = new HelperNodeList();

            root.appendChild(inputDoc);
            _filterNodes.add(root);
         }

         for (int i = 0; i < noOfSteps; i++) {
            Element xpathElement =
               this._transformObject.getChildElementLocalName(i,
                  XPath2FilterContainer.XPathFilter2NS,
                  XPath2FilterContainer._TAG_XPATH2);
            XPath2FilterContainer xpathContainer =
               XPath2FilterContainer.newInstance(xpathElement,
                                                   input.getSourceURI());

            if (xpathContainer.isIntersect()) {
               _filterTypes.add(FIntersect);
            } else if (xpathContainer.isSubtract()) {
               _filterTypes.add(FSubtract);
            } else if (xpathContainer.isUnion()) {
               _filterTypes.add(FUnion);
            } else {
               _filterTypes.add(null);
            }

            NodeList subtreeRoots = xPathFuncHereAPI.selectNodeList(inputDoc,
                                       xpathContainer.getXPathFilterTextNode(),
                                       xpathContainer.getElement());

            // _filterNodes.add(XMLUtils.convertNodelistToSet(subtreeRoots));
            _filterNodes.add(subtreeRoots);
         }

         this._F = new HashSet();
         this._ancestors = new Vector();

         this.traversal(inputDoc);

         Set resultSet = new HashSet();
         Iterator it = this._inputSet.iterator();
         while (it.hasNext()) {
            Node n = (Node) it.next();
            if (this._F.contains(n)) {
               resultSet.add(n);
            }
         }

         XMLSignatureInput result = new XMLSignatureInput(resultSet,
                                       input.getCachedXPathAPI());


         result.setSourceURI(input.getSourceURI());

         long end = System.currentTimeMillis();

         return result;
      } catch (TransformerException ex) {
         throw new TransformationException("empty", ex);
      } catch (DOMException ex) {
         throw new TransformationException("empty", ex);
      } catch (IOException ex) {
         throw new TransformationException("empty", ex);
      } catch (CanonicalizationException ex) {
         throw new TransformationException("empty", ex);
      } catch (InvalidCanonicalizerException ex) {
         throw new TransformationException("empty", ex);
      } catch (ParserConfigurationException ex) {
         throw new TransformationException("empty", ex);
      } catch (XMLSecurityException ex) {
         throw new TransformationException("empty", ex);
      } catch (SAXException ex) {
         throw new TransformationException("empty", ex);
      }
   }

   /**
    * Method traversal
    *
    * @param currentNode
    * @param Z
    */
   private void traversal(Node currentNode) {

      this._ancestors.add(currentNode);

      if (this._inputSet.contains(currentNode)) {

      int iMax = this._filterTypes.size();
      int i = 0;

      searchFirstUnionWhichContainsNode: for (i = iMax - 1; i >= 0; i--) {
         NodeList rootNodes = (NodeList) this._filterNodes.elementAt(i);
         String type = (String) this._filterTypes.elementAt(i);

         if ((type == FUnion)
                 && rooted(currentNode, this._ancestors, rootNodes)) {

            /*
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
               cat.debug(i + " " + ((Element) currentNode).getTagName()
                         + " is " + type + " of " + rootNode.getNodeName());
            }
            */
            break searchFirstUnionWhichContainsNode;
         }
      }

      int IStart = i;

      if (IStart == -1) {
         IStart = 0;
      }

      boolean include = true;

      // search in the subsequent steps for
      for (int I = IStart; I < iMax; I++) {
         NodeList rootNodes = (NodeList) this._filterNodes.elementAt(I);
         String type = (String) this._filterTypes.elementAt(I);
         boolean rooted = rooted(currentNode, this._ancestors, rootNodes);

         if ((type == FIntersect) &&!rooted) {

            /*
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
               cat.debug("The intersect operation from step " + I
                         + " does not include " + currentNode.getNodeName());
            }
            */
            include = false;

            break;
         } else if ((type == FSubtract) && rooted) {

            /*
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
               cat.debug("The subtract operation from step " + I
                         + " does subtract " + currentNode.getNodeName());
            }
            */
            include = false;

            break;
         } else {
            ;
         }
      }

      if (include) {
         this._F.add(currentNode);
      }
      }

      {

         // here we do the traversal
         if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
            NamedNodeMap attributes = ((Element) currentNode).getAttributes();
            int attributesLength = attributes.getLength();

            for (int x = 0; x < attributesLength; x++) {
               Node attr = attributes.item(x);

               traversal(attr);
            }
         }

         for (Node currentChild = currentNode.getFirstChild();
                 currentChild != null;
                 currentChild = currentChild.getNextSibling()) {
            traversal(currentChild);
         }
      }

      this._ancestors.remove(currentNode);
   }

   /**
    * Method rooted
    *
    * @param currentNode
    * @param ancestors
    * @param rootNodes
    * @return
    */
   boolean rooted(Node currentNode, Vector ancestors, NodeList rootNodes) {

      int length = rootNodes.getLength();

      for (int i = 0; i < length; i++) {
         Node rootNode = rootNodes.item(i);

         if (ancestors.contains(rootNode)) {
            return true;
         }
      }

      return false;
   }

   /**
    * Method __isRootedBy
    *
    * @param ctx
    * @param rootInQuestion
    * @return
    */
   private static boolean __isRootedBy(Node ctx, Node rootInQuestion) {

      if ((ctx == null) || (rootInQuestion == null)) {
         return false;
      }

      if (rootInQuestion.getNodeType() == Node.DOCUMENT_NODE) {
         return true;
      }

      Node n = ctx;

      while (n != null) {
         if (n == rootInQuestion) {
            return true;
         }

         if (n.getNodeType() == Node.ATTRIBUTE_NODE) {
            n = ((Attr) n).getOwnerElement();
         } else {
            n = n.getParentNode();
         }
      }

      return false;
   }
}

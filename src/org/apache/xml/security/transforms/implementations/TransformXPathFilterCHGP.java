/*
 * Copyright  1999-2004 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.xml.security.transforms.implementations;



import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.transforms.params.XPathFilterCHGPContainer;
import org.apache.xml.security.utils.CachedXPathFuncHereAPI;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;
import org.xml.sax.SAXException;


/**
 * @author $Author$
 */
public class TransformXPathFilterCHGP extends TransformSpi {

   /** Field implementedTransformURI */
   public static final String implementedTransformURI =
      Transforms.TRANSFORM_XPATHFILTERCHGP;

   //J-
   public boolean wantsOctetStream ()   { return false; }
   public boolean wantsNodeSet ()       { return true; }
   public boolean returnsOctetStream () { return false; }
   public boolean returnsNodeSet ()     { return true; }
   //J+
   //J-

   // values for state and stateStack
   static final Integer STATE_INCLUDE =            new Integer(0);
   static final Integer STATE_EXCLUDE_BUT_SEARCH = new Integer(1);
   static final Integer STATE_EXCLUDE =            new Integer(2); // this is never assigned. Just for clarity

   // the current state during the traversal
   Integer state = TransformXPathFilterCHGP.STATE_EXCLUDE_BUT_SEARCH;

   // the state of the ancestors during the traversal
   Stack stateStack = new Stack();
   //J+

   /** all nodes which are in the input XPath node set */
   Set inputSet;

   /** all nodes which are tagged include-but-search */
   Set includeSearchSet;

   /** all nodes which are tagged exclude-but-search */
   Set excludeSearchSet;

   /** all nodes which are tagged exclude */
   Set excludeSet;

   /** the result XPath node set */
   Set resultSet;

   /**
    * Method engineGetURI
    *
    *
    */
   protected String engineGetURI() {
      return implementedTransformURI;
   }

   /**
    * Method enginePerformTransform
    *
    * @param input
    *
    * @throws TransformationException
    */
   protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input)
           throws TransformationException {

      try {
         this.inputSet = input.getNodeSet();

         CachedXPathFuncHereAPI xPathFuncHereAPI =
            new CachedXPathFuncHereAPI(input.getCachedXPathAPI());
         CachedXPathAPI myXPathAPI =
            new CachedXPathAPI(input.getCachedXPathAPI());

         if (this.inputSet.size() == 0) {
            Object exArgs[] = { "input node set contains no nodes" };

            throw new TransformationException("empty", exArgs);
         }

         Element transformElement = this._transformObject.getElement();
         Document doc = transformElement.getOwnerDocument();

         // create the XPathFilterCHGPContainer so that we easily can read it out
         Element nscontext =
            XMLUtils.createDSctx(doc, "dsig-xpathalt",
                                 Transforms.TRANSFORM_XPATHFILTERCHGP);
         Element xpathElement =
            (Element) myXPathAPI
               .selectSingleNode(transformElement, "./dsig-xpathalt:"
                                 + XPathFilterCHGPContainer
                                    ._TAG_XPATHCHGP, nscontext);

         if (xpathElement == null) {
            Object exArgs[] = { "{" + TransformXPathFilterCHGP.implementedTransformURI + "}XPath",
                                "Transform" };

            throw new TransformationException("xml.WrongContent", exArgs);
         }

         XPathFilterCHGPContainer xpathContainer =
            XPathFilterCHGPContainer.getInstance(xpathElement,
                                                 input.getSourceURI());

         // get the document (root node) for the traversal
         Document inputDoc = null;

         {
            Iterator it = this.inputSet.iterator();

            if (it.hasNext()) {
               inputDoc = XMLUtils.getOwnerDocument((Node) it.next());
            }
         }

         {

            // 'tag' the include-but-search nodes
            Node includeButSearchCtxNode =
               xpathContainer.getHereContextNodeIncludeButSearch();
            NodeList includeButSearchNodes = null;

            if (includeButSearchCtxNode != null) {
               includeButSearchNodes = xPathFuncHereAPI.selectNodeList(doc,
                       includeButSearchCtxNode, xpathContainer.getElement());
            }

            this.includeSearchSet = nodeListToSet(includeButSearchNodes);
         }

         {

            // 'tag' the exclude-but-search nodes
            Node excludeButSearchCtxNode =
               xpathContainer.getHereContextNodeExcludeButSearch();
            NodeList excludeButSearchNodes = null;

            if (excludeButSearchCtxNode != null) {
               excludeButSearchNodes = xPathFuncHereAPI.selectNodeList(doc,
                       excludeButSearchCtxNode, xpathContainer.getElement());
            }

            this.excludeSearchSet = nodeListToSet(excludeButSearchNodes);
         }

         {

            // 'tag' the exclude nodes
            Node excludeCtxNode = xpathContainer.getHereContextNodeExclude();
            NodeList excludeNodes = null;

            if (excludeCtxNode != null) {
               excludeNodes = xPathFuncHereAPI.selectNodeList(doc,
                       excludeCtxNode, xpathContainer.getElement());
            }

            this.excludeSet = nodeListToSet(excludeNodes);
         }

         if (xpathContainer.getIncludeSlashPolicy()
                 == XPathFilterCHGPContainer.IncludeSlash) {
            this.includeSearchSet.add(inputDoc);
         } else {
            this.excludeSearchSet.add(inputDoc);
         }

         // create empty set for results
         this.resultSet = new HashSet();

         {
            DocumentTraversal dt = ((DocumentTraversal) inputDoc);
            Node rootNode = (Node) inputDoc;

            // we accept all nodes
            NodeFilter nodefilter = new AlwaysAcceptNodeFilter();
            TreeWalker treewalker = dt.createTreeWalker(rootNode,
                                                        NodeFilter.SHOW_ALL,
                                                        nodefilter, true);

            /* do the magic here
             *
             * We make a traversal of the full DOM tree and check all nodes
             * which are in the inputSet whether we put them into the resultSet.
             *
             */
            process(treewalker);
         }

         XMLSignatureInput result = new XMLSignatureInput(resultSet,
                                       input.getCachedXPathAPI());

         result.setSourceURI(input.getSourceURI());

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
    * Method process
    *
    * @param treewalker
    */
   private void process(TreeWalker treewalker) {

      Node currentNode = treewalker.getCurrentNode();

      if (this.excludeSet.contains(currentNode)) {

         /* THIS is what allows optimization: if the subtree cannot contain any
          * nodes which are to be included, we do not descend.
          *
          * It would also work to move all nodes from the exclude set to the
          * excludeButSearch set. But it would be waste of time.
          */
         treewalker.setCurrentNode(currentNode);

         return;
      } else if (this.includeSearchSet.contains(currentNode)) {
         this.state = TransformXPathFilterCHGP.STATE_INCLUDE;
      } else if (this.excludeSearchSet.contains(currentNode)) {
         this.state = TransformXPathFilterCHGP.STATE_EXCLUDE_BUT_SEARCH;
      }

      /* This works actually as a filter: We can only decide to not include
       * nodes in the result; We do not include nodes which haven't been
       * in the inputSet (no union operation).
       */
      if (this.inputSet.contains(currentNode)
              && (this.state == TransformXPathFilterCHGP.STATE_INCLUDE)) {
         this.resultSet.add(currentNode);

         // the treewalker does not descend into the attributes, so we must check them by hand
         if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
            NamedNodeMap nnm = ((Element) currentNode).getAttributes();

            for (int i = 0; i < nnm.getLength(); i++) {
               Node attr = nnm.item(i);

               // if the node was in the input XPath node set AND
               // is NOT deselected by this transform
               if (this.inputSet.contains(attr)
                       &&!this.excludeSearchSet.contains(attr)
                       &&!this.excludeSet.contains(attr)) {
                  this.resultSet.add(attr);
               }
            }
         }
      }

      for (Node node1 = treewalker.firstChild(); node1 != null;
              node1 = treewalker.nextSibling()) {

         // store state before descend
         this.stateStack.push(this.state);

         // descend
         process(treewalker);

         // restore state after descend
         this.state = (Integer) this.stateStack.pop();
      }

      treewalker.setCurrentNode(currentNode);
   }

   /**
    * Copies all nodes from a given {@link NodeList} into a {@link Set}
    *
    * @param nl
    *
    */
   private static Set nodeListToSet(NodeList nl) {

      Set set = new HashSet();
      int iMax = ((nl == null)
                  ? 0
                  : nl.getLength());

      for (int i = 0; i < iMax; i++) {
         set.add(nl.item(i));
      }

      return set;
   }

   /**
    * This {@link NodeFilter} always returns <code>true</code>
    *
    * @author Christian Geuer-Pollmann
    */
   public class AlwaysAcceptNodeFilter implements NodeFilter {

      /**
       * Method acceptNode
       *
       * @param n
       *
       */
      public short acceptNode(Node n) {
         return NodeFilter.FILTER_ACCEPT;
      }
   }
}

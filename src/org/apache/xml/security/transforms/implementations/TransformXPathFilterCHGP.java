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
import org.w3c.dom.traversal.*;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.c14n.*;
import org.apache.xml.security.transforms.params.XPathFilterCHGPContainer;
import org.apache.xml.security.transforms.*;
import org.apache.xml.security.exceptions.*;
import org.apache.xml.security.utils.*;
import org.apache.xpath.CachedXPathAPI;
import org.apache.xpath.objects.XObject;
import org.apache.xml.utils.PrefixResolverDefault;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.dtm.DTMManager;


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

   /** Field STATE_INCLUDE */
   static final Integer STATE_INCLUDE = new Integer(0);

   /** Field STATE_EXCLUDE_BUT_SEARCH */
   static final Integer STATE_EXCLUDE_BUT_SEARCH = new Integer(1);

   /** Field STATE_EXCLUDE */
   static final Integer STATE_EXCLUDE = new Integer(2);

   /** Field state */
   Integer state = TransformXPathFilterCHGP.STATE_EXCLUDE_BUT_SEARCH;

   /** Field stateStack */
   List stateStack = new Vector();

   /** Field inputSet */
   Set inputSet;

   /** Field includeSearchSet */
   Set includeSearchSet;

   /** Field excludeSearchSet */
   Set excludeSearchSet;

   /** Field excludeSet */
   Set excludeSet;

   /** Field resultSet */
   Set resultSet;

   /**
    * Method engineGetURI
    *
    * @return
    */
   protected String engineGetURI() {
      return this.implementedTransformURI;
   }

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
         if (input.isOctetStream()) {
            input.setNodesetXPath(Canonicalizer.XPATH_C14N_WITH_COMMENTS);
         }

         NodeList inputNodes = input.getNodeSet();
         CachedXPathFuncHereAPI xPathFuncHereAPI =
            new CachedXPathFuncHereAPI(input.getCachedXPathAPI());
         CachedXPathAPI myXPathAPI =
            new CachedXPathAPI(input.getCachedXPathAPI());

         if (inputNodes.getLength() == 0) {
            Object exArgs[] = { "input node set contains no nodes" };

            throw new TransformationException("empty", exArgs);
         }

         Element transformElement = this._transformObject.getElement();
         Document doc = transformElement.getOwnerDocument();
         Element nscontext =
            XMLUtils.createDSctx(doc, "dsig-xpathalt",
                                 Transforms.TRANSFORM_XPATHFILTERCHGP);
         Element xpathElement =
            (Element) myXPathAPI
               .selectSingleNode(transformElement, "./dsig-xpathalt:"
                                 + XPathFilterCHGPContainer
                                    ._TAG_XPATHCHGP, nscontext);

         if (xpathElement == null) {
            Object exArgs[] = { "{" + this.implementedTransformURI + "}XPath",
                                "Transform" };

            throw new TransformationException("xml.WrongContent", exArgs);
         }

         Document inputDoc = XMLUtils.getOwnerDocument(inputNodes.item(0));
         XPathFilterCHGPContainer xpathContainer =
            XPathFilterCHGPContainer.getInstance(xpathElement,
                                                 input.getSourceURI());

         {
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
            Node excludeCtxNode = xpathContainer.getHereContextNodeExclude();
            NodeList excludeNodes = null;

            if (excludeCtxNode != null) {
               excludeNodes = xPathFuncHereAPI.selectNodeList(doc,
                       excludeCtxNode, xpathContainer.getElement());
            }

            this.excludeSet = nodeListToSet(excludeNodes);
         }

         this.inputSet = nodeListToSet(inputNodes);
         this.resultSet = new HashSet();

         {
            DocumentTraversal dt = ((DocumentTraversal) inputDoc);
            Node rootNode = (Node) inputDoc;
            NodeFilter nodefilter =
               new org.apache.xml.security.c14n.helper.AlwaysAcceptNodeFilter();
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

         HelperNodeList resultNodes = new HelperNodeList();
         Iterator it = this.resultSet.iterator();

         while (it.hasNext()) {
            resultNodes.appendChild((Node) it.next());
         }

         XMLSignatureInput result = new XMLSignatureInput(resultNodes,
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

      if (this.includeSearchSet.contains(currentNode)) {
         this.state = TransformXPathFilterCHGP.STATE_INCLUDE;

         // we must search so we cannot return;
      } else if (this.excludeSearchSet.contains(currentNode)) {
         this.state = TransformXPathFilterCHGP.STATE_EXCLUDE_BUT_SEARCH;

         // we must search so we cannot return;
      } else if (this.excludeSet.contains(currentNode)) {

         /* this is what allows optimization: if the subtree cannot contain any
          * nodes which are to be included, we do not descend.
          *
          * It would also work to put all nodes from the exclude set to the
          * excludeButSearch set. But it would be waste of time.
          */

         // assignment only for clarity; never needed because never checked:
         // this.state = TransformXPathFilterCHGP.STATE_EXCLUDE;
         //
         treewalker.setCurrentNode(currentNode);

         return;
      }

      /* This works actually as a filter: We can only decide to not include
       * nodes in the result; We do not include nodes which haven't been
       * in the inputSet.
       */
      if (this.inputSet.contains(currentNode)
              && (this.state == TransformXPathFilterCHGP.STATE_INCLUDE)) {
         this.resultSet.add(currentNode);

         if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
            NamedNodeMap nnm = ((Element) currentNode).getAttributes();

            for (int i = 0; i < nnm.getLength(); i++) {
               Node attr = nnm.item(i);

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
         this.stateStack.add(this.state);

         // descend
         process(treewalker);

         // restore state after descend
         this.state = (Integer) this.stateStack.remove(this.stateStack.size()
                 - 1);
      }

      treewalker.setCurrentNode(currentNode);
   }

   /**
    * Method nodeListToSet
    *
    * @param nl
    * @return
    */
   static Set nodeListToSet(NodeList nl) {

      Set set = new HashSet();
      int iMax = ((nl == null)
                  ? 0
                  : nl.getLength());

      for (int i = 0; i < iMax; i++) {
         set.add(nl.item(i));
      }

      return set;
   }
}

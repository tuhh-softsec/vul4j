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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.transforms.params.XPath2FilterContainer04;
import org.apache.xml.security.utils.CachedXPathFuncHereAPI;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * Implements the <I>XML Signature XPath Filter v2.0</I>
 *
 * @author $Author$
 * @see <A HREF="http://www.w3.org/TR/xmldsig-filter2/">XPath Filter v2.0 (TR)</A>
 * @see <A HREF="http://www.w3.org/Signature/Drafts/xmldsig-xfilter2/">XPath Filter v2.0 (editors copy)</A>
 */
public class TransformXPath2Filter04 extends TransformSpi {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(
                        TransformXPath2Filter04.class.getName());

   /** Field implementedTransformURI */
   public static final String implementedTransformURI =
      Transforms.TRANSFORM_XPATH2FILTER04;

   //J-
   /** @inheritDoc */
   public boolean wantsOctetStream ()   { return false; }
   /** @inheritDoc */
   public boolean wantsNodeSet ()       { return true; }
   /** @inheritDoc */
   public boolean returnsOctetStream () { return false; }
   /** @inheritDoc */
   public boolean returnsNodeSet ()     { return true; }
   //J+

   /**
    * Method engineGetURI
    *
    * @inheritDoc
    */
   protected String engineGetURI() {
      return implementedTransformURI;
   }

   /**
    * Method enginePerformTransform
    *
    * @param input
    * @inheritDoc
    * @throws TransformationException
    */
   protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input)
           throws TransformationException {

      try {
         Set inputSet = input.getNodeSet();

         log.debug("perform xfilter2 on " + inputSet.size() + " nodes");

         CachedXPathFuncHereAPI xPathFuncHereAPI =
            new CachedXPathFuncHereAPI(input.getCachedXPathAPI().getCachedXPathAPI());
         CachedXPathAPI myXPathAPI =
            new CachedXPathAPI(input.getCachedXPathAPI().getCachedXPathAPI());

         if (inputSet.size() == 0) {
            Object exArgs[] = { "input node set contains no nodes" };

            throw new TransformationException("empty", exArgs);
         }

         Element xpathElement =XMLUtils.selectNode(
            this._transformObject.getElement().getFirstChild(),
               XPath2FilterContainer04.XPathFilter2NS,
               XPath2FilterContainer04._TAG_XPATH2,0);

         if (xpathElement == null) {
            Object exArgs[] = { "dsig-xpath:XPath", "Transform" };

            throw new TransformationException("xml.WrongContent", exArgs);
         }

         XPath2FilterContainer04 xpathContainer =
            XPath2FilterContainer04.newInstance(xpathElement,
                                              input.getSourceURI());
         Document doc = this._transformObject.getElement().getOwnerDocument();
         NodeList subtreeRoots = xPathFuncHereAPI.selectNodeList(doc,
                                    xpathContainer.getXPathFilterTextNode(),
                                    CachedXPathFuncHereAPI.getStrFromNode(xpathContainer.getXPathFilterTextNode()),
                                    xpathContainer.getElement());

         log.debug("subtreeRoots contains " + subtreeRoots.getLength()
                   + " nodes");

         /*
          * foreach subtree
          */
         Set selectedNodes = new HashSet();

         for (int i = 0; i < subtreeRoots.getLength(); i++) {
            Node currentRootNode = subtreeRoots.item(i);
            int currentRootNodeType = currentRootNode.getNodeType();

            if ((currentRootNodeType == Node.ELEMENT_NODE)
                    || (currentRootNodeType == Node.DOCUMENT_NODE)) {
               NodeList nodesInSubtree =
                  myXPathAPI
                     .selectNodeList(currentRootNode, Canonicalizer
                        .XPATH_C14N_WITH_COMMENTS_SINGLE_NODE);
               int jMax = nodesInSubtree.getLength();

               for (int j = 0; j < jMax; j++) {
                  selectedNodes.add(nodesInSubtree.item(j));
               }
            } else if ((currentRootNodeType == Node.ATTRIBUTE_NODE)
                       || (currentRootNodeType == Node.TEXT_NODE)
                       || (currentRootNodeType == Node.CDATA_SECTION_NODE)
                       || (currentRootNodeType
                           == Node.PROCESSING_INSTRUCTION_NODE)) {
               selectedNodes.add(currentRootNode);
            } else {
               throw new RuntimeException("unknown node type: " + currentRootNodeType + " " + currentRootNode);
            }
         }

         log.debug("selection process identified " + selectedNodes.size()
                   + " nodes");

         Set resultNodes = new HashSet();

         if (xpathContainer.isIntersect()) {
            Iterator inputSetIterator = inputSet.iterator();

            while (inputSetIterator.hasNext()) {
               Node currentInputNode = (Node) inputSetIterator.next();

               // if the input node is selected, include it in the output
               if (selectedNodes.contains(currentInputNode)) {
                  resultNodes.add(currentInputNode);
               }
            }
         } else if (xpathContainer.isSubtract()) {
            Iterator inputSetIterator = inputSet.iterator();

            while (inputSetIterator.hasNext()) {
               Node currentInputNode = (Node) inputSetIterator.next();

               // if the input node is selected, do not include it
               // otherwise, include it
               if (!selectedNodes.contains(currentInputNode)) {
                  resultNodes.add(currentInputNode);
               }
            }
         } else if (xpathContainer.isUnion()) {
            Iterator inputSetIterator = inputSet.iterator();

            while (inputSetIterator.hasNext()) {
               Node currentInputNode = (Node) inputSetIterator.next();

               // add all input nodes to the result
               resultNodes.add(currentInputNode);
            }

            Iterator selectedSetIterator = selectedNodes.iterator();

            while (selectedSetIterator.hasNext()) {
               Node currentSelectedNode = (Node) selectedSetIterator.next();

               // add all selected nodes to the result
               resultNodes.add(currentSelectedNode);
            }
         } else {
            throw new TransformationException("empty");
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
}

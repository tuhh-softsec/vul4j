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
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.transforms.params.XPath2FilterContainer;
import org.apache.xml.security.utils.CachedXPathFuncHereAPI;
import org.apache.xml.security.utils.HelperNodeList;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * Implements the <I>XML Signature XPath Filter v2.0</I>
 *
 * @author $Author$
 * @see <A HREF="http://www.w3.org/TR/xmldsig-filter2/">XPath Filter v2.0 (TR)</A>
 * @see <A HREF=http://www.w3.org/Signature/Drafts/xmldsig-xfilter2/">XPath Filter v2.0 (editors copy)</A>
 */
public class TransformXPath2Filter extends TransformSpi {

   /** {@link org.apache.commons.logging} logging facility */
//    static org.apache.commons.logging.Log log = 
//        org.apache.commons.logging.LogFactory.getLog(
//                            TransformXPath2Filter.class.getName());

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
    *
    */
   protected String engineGetURI() {
      return implementedTransformURI;
   }

   Set _inputSet = null;

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

         this._inputSet = input.getNodeSet();

         if (this._inputSet.size() == 0) {

            // input node set contains no nodes
            return input;
         }

         CachedXPathFuncHereAPI xPathFuncHereAPI =
            new CachedXPathFuncHereAPI(input.getCachedXPathAPI());
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
               log.debug(i + " " + ((Element) currentNode).getTagName()
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
               log.debug("The intersect operation from step " + I
                         + " does not include " + currentNode.getNodeName());
            }
            */
            include = false;

            break;
         } else if ((type == FSubtract) && rooted) {

            /*
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
               log.debug("The subtract operation from step " + I
                         + " does subtract " + currentNode.getNodeName());
            }
            */
            include = false;

            break;
         } else {
			//do nothing
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
    *
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
}

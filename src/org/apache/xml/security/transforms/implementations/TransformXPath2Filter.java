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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.NodeFilter;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.transforms.TransformSpi;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.transforms.params.XPath2FilterContainer;
import org.apache.xml.security.utils.CachedXPathAPIHolder;
import org.apache.xml.security.utils.CachedXPathFuncHereAPI;
import org.apache.xml.security.utils.XMLUtils;
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
 * @see <a HREF="http://www.w3.org/Signature/Drafts/xmldsig-xfilter2/">XPath Filter v2.0 (editors copy)</a>
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

   // contains the node set
  
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
    * @inheritDoc
    * @param input
    *
    * @throws TransformationException
    */
   protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input, Transform _transformObject)
           throws TransformationException {
	  CachedXPathAPIHolder.setDoc(_transformObject.getElement().getOwnerDocument());
      try {
    	  List unionNodes=new ArrayList();
    	   List substractNodes=new ArrayList();
    	   List intersectNodes=new ArrayList();

         CachedXPathFuncHereAPI xPathFuncHereAPI =
            new CachedXPathFuncHereAPI(CachedXPathAPIHolder.getCachedXPathAPI());

         
         Element []xpathElements =XMLUtils.selectNodes(
                _transformObject.getElement().getFirstChild(),
                   XPath2FilterContainer.XPathFilter2NS,
                   XPath2FilterContainer._TAG_XPATH2);
         int noOfSteps = xpathElements.length;


         if (noOfSteps == 0) {
            Object exArgs[] = { Transforms.TRANSFORM_XPATH2FILTER, "XPath" };

            throw new TransformationException("xml.WrongContent", exArgs);
         }

         Document inputDoc = null;
	 if (input.getSubNode() != null) {   
            inputDoc = XMLUtils.getOwnerDocument(input.getSubNode());
	 } else {
            inputDoc = XMLUtils.getOwnerDocument(input.getNodeSet());
	 }

         for (int i = 0; i < noOfSteps; i++) {
            Element xpathElement =XMLUtils.selectNode(
               _transformObject.getElement().getFirstChild(),
                  XPath2FilterContainer.XPathFilter2NS,
                  XPath2FilterContainer._TAG_XPATH2,i);
            XPath2FilterContainer xpathContainer =
               XPath2FilterContainer.newInstance(xpathElement,
                                                   input.getSourceURI());
           

            NodeList subtreeRoots = xPathFuncHereAPI.selectNodeList(inputDoc,
                                       xpathContainer.getXPathFilterTextNode(),
                                       CachedXPathFuncHereAPI.getStrFromNode(xpathContainer.getXPathFilterTextNode()),
                                       xpathContainer.getElement());
            if (xpathContainer.isIntersect()) {
                intersectNodes.add(subtreeRoots);
             } else if (xpathContainer.isSubtract()) {
            	 substractNodes.add(subtreeRoots);
             } else if (xpathContainer.isUnion()) {
                unionNodes.add(subtreeRoots);
             } 
         }

         
         input.addNodeFilter(new XPath2NodeFilter(convertNodeListToSet(unionNodes),
        		 convertNodeListToSet(substractNodes),convertNodeListToSet(intersectNodes)));
         input.setNodeSet(true);
         return input;
      } catch (TransformerException ex) {
         throw new TransformationException("empty", ex);
      } catch (DOMException ex) {
         throw new TransformationException("empty", ex);
      } catch (CanonicalizationException ex) {
         throw new TransformationException("empty", ex);
      } catch (InvalidCanonicalizerException ex) {
         throw new TransformationException("empty", ex);
      } catch (XMLSecurityException ex) {
         throw new TransformationException("empty", ex);
      } catch (SAXException ex) {
         throw new TransformationException("empty", ex);
      } catch (IOException ex) {
         throw new TransformationException("empty", ex);
      } catch (ParserConfigurationException ex) {
         throw new TransformationException("empty", ex);
      } 
   }
   static Set convertNodeListToSet(List l){
	   Set result=new HashSet();
	   for (int j=0;j<l.size();j++) {
		   NodeList rootNodes=(NodeList) l.get(j);	   
	       int length = rootNodes.getLength();

	       for (int i = 0; i < length; i++) {
	            Node rootNode = rootNodes.item(i);
	            result.add(rootNode);
	            
	         }
	         
	   }
	   return result;
   }
}

class XPath2NodeFilter implements NodeFilter {
	boolean hasUnionNodes;
	boolean hasSubstractNodes;
	boolean hasIntersectNodes;
	XPath2NodeFilter(Set unionNodes, Set substractNodes,
			Set intersectNodes) {
		this.unionNodes=unionNodes;
		hasUnionNodes=!unionNodes.isEmpty();
		this.substractNodes=substractNodes;
		hasSubstractNodes=!substractNodes.isEmpty();
		this.intersectNodes=intersectNodes;
		hasIntersectNodes=!intersectNodes.isEmpty();
	}
	Set unionNodes;
	Set substractNodes;
	Set intersectNodes;


   /**
    * @see org.apache.xml.security.signature.NodeFilter#isNodeInclude(org.w3c.dom.Node)
    */
   public int isNodeInclude(Node currentNode) {	 
	   boolean notIncluded=false;
	   if (hasSubstractNodes && rooted(currentNode, substractNodes)) {
		      notIncluded = true;
	   } else if (hasIntersectNodes && !rooted(currentNode, intersectNodes)) {
		   notIncluded = true;
	   }
	   	   
	  //TODO OPTIMIZE
      if (!notIncluded)     	        
    	  return 1;
      if (hasUnionNodes && rooted(currentNode, unionNodes)) {
		   return 1;
	   }
      if (!hasUnionNodes && !hasIntersectNodes) {
    	  return -1; //Not union nodes to safe a node that has been exclude.
      }
      return 0;

   }
   int inSubstract=-1;
   int inIntersect=-1;
   int inUnion=-1;
   public int isNodeIncludeDO(Node n, int level) {
	   boolean notIncluded=false;
	   if (hasSubstractNodes) {
		   if ((inSubstract==-1) || (level<=inSubstract)) {
			   if (inList(n,  substractNodes)) {
				   inSubstract=level;
			   } else {
				   inSubstract=-1;   			   
			   }		   
		   } 
		   if (inSubstract!=-1){
			   notIncluded=true;
		   }
	   } 
	   if (!notIncluded){ 
		   if (hasIntersectNodes) {
		   if ((inIntersect==-1) || (level<=inIntersect)) {
			   if (!inList(n,  intersectNodes)) {
				   inIntersect=-1;
				   notIncluded = true;
			   } else {
				   notIncluded=false;
				   inIntersect=level;   			   
			   }		   
		   }
		   }
	   }
	   	   
	  if (level<=inUnion)
		   inUnion=-1;
      if (!notIncluded)     	        
    	  return 1;
      if (hasUnionNodes) {
    	  if ((inUnion==-1) && inList(n,  unionNodes)) {
    		  inUnion=level;
    	  }
    	  if (inUnion!=-1)
    		  return 1;
      }
		
      if (!hasUnionNodes && !hasIntersectNodes) {
    	  return -1; //Not union nodes to safe a node that has been exclude.
      }
      return 0;
   }

   /**
    * Method rooted
    * @param currentNode 
    * @param nodeList 
    *
    * @return if rooted bye the rootnodes
    */
   static boolean  rooted(Node currentNode, Set nodeList ) {
	   if (nodeList.contains(currentNode)) {
		   return true;
	   }
	   Iterator it=nodeList.iterator();
	   while (it.hasNext()) {
	   		Node rootNode = (Node) it.next();
			if (XMLUtils.isDescendantOrSelf(rootNode,currentNode)) {
				   return true;
			}
	   }
	   return false;
   }
   
      /**
       * Method rooted
       * @param currentNode 
       * @param nodeList 
       *
       * @return if rooted bye the rootnodes
       */
      static boolean  inList(Node currentNode, Set nodeList ) {
   	      return nodeList.contains(currentNode);
      }
}

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
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.CachedXPathFuncHereAPI;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.utils.PrefixResolverDefault;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


/**
 * Class TransformXPath
 *
 * Implements the <CODE>http://www.w3.org/TR/1999/REC-xpath-19991116</CODE>
 * transform.
 *
 * @author Christian Geuer-Pollmann
 * @see <a href="http://www.w3.org/TR/1999/REC-xpath-19991116">XPath</a>
 *
 */
public class TransformXPath extends TransformSpi {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(TransformXPath.class.getName());

   /** Field implementedTransformURI */
   public static final String implementedTransformURI =
      Transforms.TRANSFORM_XPATH;

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
    * @inheritDoc
    * @param input
    *
    * @throws TransformationException
    */
   protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input)
           throws TransformationException {

      try {

         /**
          * If the actual input is an octet stream, then the application MUST
          * convert the octet stream to an XPath node-set suitable for use by
          * Canonical XML with Comments. (A subsequent application of the
          * REQUIRED Canonical XML algorithm would strip away these comments.)
          *
          * ...
          *
          * The evaluation of this expression includes all of the document's nodes
          * (including comments) in the node-set representing the octet stream.
          */

         
         CachedXPathFuncHereAPI xPathFuncHereAPI =
            new CachedXPathFuncHereAPI(input.getCachedXPathAPI().getCachedXPathAPI());
         

         Element xpathElement =XMLUtils.selectDsNode(
            this._transformObject.getElement().getFirstChild(),
               Constants._TAG_XPATH,0);

         if (xpathElement == null) {
            Object exArgs[] = { "ds:XPath", "Transform" };

            throw new TransformationException("xml.WrongContent", exArgs);
         }
         Node xpathnode = xpathElement.getChildNodes().item(0);
         String str=CachedXPathFuncHereAPI.getStrFromNode(xpathnode);
         boolean circumvent=needsCircunvent(str);
         Set inputSet = input.getNodeSet(circumvent);
         if (inputSet.size() == 0) {
            Object exArgs[] = { "input node set contains no nodes" };

            throw new TransformationException("empty", exArgs);
         }
         
         /**
          * The transform output is also an XPath node-set. The XPath expression
          * appearing in the XPath parameter is evaluated once for each node in
          * the input node-set. The result is converted to a boolean. If the
          * boolean is true, then the node is included in the output node-set.
          * If the boolean is false, then the node is omitted from the output
          * node-set.
          */
         Set resultNodes = new HashSet();

         /**
          * precompile XPath for evaluation; this is taken from {@link XPathAPI#eval}
          */
         PrefixResolverDefault prefixResolver =
            new PrefixResolverDefault(xpathElement);
         
         
         if (xpathnode == null) {
            throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR,
                                   "Text must be in ds:Xpath");
         }

         Iterator iterator = inputSet.iterator();
         
         while (iterator.hasNext()) {
            Node currentNode = (Node) iterator.next();

            /* Same solution as in TransformBase64 ?
            if (currentNode.getClass().getName().equals(
               "org.apache.xml.dtm.ref.dom2dtm.DOM2DTM$defaultNamespaceDeclarationNode")) {
               continue;
            }
            */            
            XObject includeInResult = xPathFuncHereAPI.eval(currentNode,
                                         xpathnode, str,prefixResolver);
           
            if (includeInResult.bool()) {
               resultNodes.add(currentNode);
               // log.debug("    Added " + org.apache.xml.security.c14n.implementations.Canonicalizer20010315.getXPath(currentNode));
             } else {
               // log.debug("Not added " + org.apache.xml.security.c14n.implementations.Canonicalizer20010315.getXPath(currentNode));
            }
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
      } catch (ParserConfigurationException ex) {
         throw new TransformationException("empty", ex);
      } catch (XMLSecurityException ex) {
         throw new TransformationException("empty", ex);
      } catch (SAXException ex) {
         throw new TransformationException("empty", ex);
      }
   }
   /**
    *  @param str
    * @return
    */
    private boolean needsCircunvent(String str) {
    	return true; //str.indexOf("namespace")>0;
    	
    }
}
